package org.knime.gateway.impl.webui.service.subscriptions;


import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.knime.gateway.impl.util.Lazy;

/**
 * High-level interface to interact with the event dispatcher service
 // TODO name tbd
 // TODO number of websockets per user limited (can't do anything about that here?)
 // TODO number of subscriptions per websocket limited (need to open new ws if we have more subs?)
 // ~> <a href="https://knime-com.atlassian.net/wiki/spaces/PROD/pages/4250533889/Event+Dispatcher">confl docs</a>
 */
// TODO could implement/adapt an interface HubResourceChangeProvider
public class EventDispatcherClient {

    // moved to field to enable calling this from testing method
    private final WebsocketClient.Listener websocketListener = new WebsocketClient.Listener() {
        @Override
        void onMessage(String message) {

            activeSubscriptions.notifyAllForTesting();  // TODO

            // TODO deserialise `message`
            var isEvent = false;
            boolean isHeartbeatPong = false;
            if (isEvent) {
                var mockEventResponse = new ResponsePayload.Event();
                mockEventResponse.notifications().forEach(activeSubscriptions::notify);
            } else if (isHeartbeatPong) {
                var mockResponse = new ResponsePayload.Pong("timestamp");
                heartbeat.setDelay(AdaptiveHeartbeat.Duration.until(mockResponse.expiresAt()));
            }
        }

        @Override
        void onClose(int statusCode, String reason) {
            // TODO?
        }

        @Override
        void onError(int statusCode, String reason) {
            // TODO?
        }
    };

    private AdaptiveHeartbeat heartbeat;

    private final Lazy.Init<WebsocketClient> websocketClient = new Lazy.Init<>(() -> {
        URI endpoint = null;
        try {
            endpoint = new URI("ws://0.0.0.0:8765");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        var authToken = "bar"; // TODO
        var client = WebsocketClient.createDefault(endpoint, authToken);
        heartbeat = new AdaptiveHeartbeat(() -> client.send(new RequestPayload.Ping().serialise()));
        heartbeat.setDelay(defaultHeartbeatDelay);
        client.addListener(websocketListener);
        return client;
        // TODO close client?
    });

    private static final AdaptiveHeartbeat.Duration defaultHeartbeatDelay = AdaptiveHeartbeat.Duration.ofSeconds(30);

    private final ActiveSubscriptions activeSubscriptions = new ActiveSubscriptions();

    public void callWebsocketListenerForTesting(String message) {
        websocketListener.onMessage(message);
    }

    public void unsubscribe(String spaceId) {
        activeSubscriptions.removeAllFor(spaceId);
        sendSubscriptionUpdate();
        // TODO close connection when last subscription is removed? with timeout?
    }

    public void unsubscribeAll() {
        activeSubscriptions.clear();
        sendSubscriptionUpdate();
    }

    static class ActiveSubscriptions {

        Map<UUID, ActiveSubscription> activeSubscriptions = new HashMap<>();

        public void notify(ResponsePayload.Event.SubscriptionNotification notification) {
            var activeSubscription = this.get(notification.id());
            activeSubscription.ifPresent(
                    activeSub -> activeSub.callback().accept(
                            SubscriptionCallback.SubscriptionNotification.of(notification)
                    ));
        }

        public void notifyAllForTesting() {
            activeSubscriptions.values().forEach(activeSubscription -> {
                activeSubscription.callback().accept(new SubscriptionCallback.SubscriptionNotification("0", 1));
            });
        }

        public void put(ActiveSubscription subscription) {
            if (activeSubscriptions.size() > 10) {
                // TODO error
                // TODO number of subscriptions per websocket limited (need to open new ws if we have more subs?)
                // ~> https://knime-com.atlassian.net/wiki/spaces/PROD/pages/4250533889/Event+Dispatcher
                // TODO log
                return;
            }
            activeSubscriptions.put(subscription.payload().id(), subscription);
        }

        public void removeAllFor(String spaceId) {
            // TODO not only spaceId
            // TODO not consider payload but particular type here after all?
            // i.e. this is way too indirect for our usecases, via filters and whatnot...
            activeSubscriptions.entrySet().stream()
                    .filter(entry -> entry.getValue().payload().filters.stream()
                    .anyMatch(filter -> filter.value.equals(spaceId))).map(Map.Entry::getValue).findFirst()
                    .ifPresent(keyToRemove -> activeSubscriptions.remove(keyToRemove));
        }

        public void clear() {
            activeSubscriptions.clear();
        }

        public Optional<ActiveSubscription> get(UUID id) {
            return Optional.ofNullable(activeSubscriptions.get(id));
        }

        public RequestPayload.CreateSubscription.Subscription[] getPayloads() {
            return activeSubscriptions.values().stream().map(ActiveSubscription::payload)
                    .toList()
                    .toArray(new RequestPayload.CreateSubscription.Subscription[0]);
        }
    }

    record ActiveSubscription(RequestPayload.CreateSubscription.Subscription payload, SubscriptionCallback callback) {

    }

    /**
     * public interface
     */
    public void subscribeToSpace(final String spaceId, SubscriptionCallback subscriptionCallback) {
        // TODO "payload" vs "entity"
        var payload = new RequestPayload.CreateSubscription.Subscription.RepositoryItems(new Filter("spaceId", spaceId));
        activeSubscriptions.put(new ActiveSubscription(payload, subscriptionCallback));
        sendSubscriptionUpdate();
    }

    private void sendSubscriptionUpdate() {
        websocketClient.initialised().send(new RequestPayload.CreateSubscription(activeSubscriptions.getPayloads()).serialise());
    }

    public interface SubscriptionCallback {
        void accept(SubscriptionNotification notification);

        public record SubscriptionNotification(String timestamp, int count) {

            static SubscriptionNotification of(ResponsePayload.Event.SubscriptionNotification notificationPayload) {
                return new SubscriptionNotification(notificationPayload.timestamp(), notificationPayload.count());
            }

        }
    }


    private static class AdaptiveHeartbeat {
        private final Runnable onBeat;

        private final Lazy.Init<ScheduledThreadPoolExecutor> executor = new Lazy.Init<>(() -> {
            return new ScheduledThreadPoolExecutor(1);
        });

        private ScheduledFuture<?> scheduled;

        public AdaptiveHeartbeat(Runnable onBeat) {
            this.onBeat = onBeat;
        }

        public void setDelay(Duration delay) {
            if (scheduled != null) {
                scheduled.cancel(false);
            }
            scheduled = executor.initialised().scheduleAtFixedRate(onBeat, delay.value(), delay.value(), delay.unit());
        }

        record Duration(long value, TimeUnit unit) {
            static Duration ofSeconds(long value) {
                return new Duration(value, TimeUnit.SECONDS);
            }

            static Duration until(String timestamp) {
                // TODO parse string
                return new Duration(30, TimeUnit.SECONDS);
            }
        }
    }

    private static class ResponsePayload {

        String status;

        String expiresAt;

        /**
         *
         * Not to be confused with the PONG control frame of the WebSocket spec
         * @see RequestPayload.Ping
         */
        private static class Pong extends ResponsePayload {
            public Pong(String expiresAt) {
                this.status = "OK";
                this.expiresAt = expiresAt;
            }
        }

        String expiresAt() {
            return expiresAt;
        }

        private static class Event extends ResponsePayload {

            // TODO de/serialise to/from `data`
            private List<SubscriptionNotification> notifications;

            public Event() {
                this.status = "event";
            }

            List<SubscriptionNotification> notifications() {
                return this.notifications;
            }

            record SubscriptionNotification(UUID id, String timestamp, int count) {

            }
        }
    }

    private static class RequestPayload {

        Op op;

        String serialise() {
            // TODO objectmapper and whatnot
            return null;
        }

        private enum Op {
                CREATE_SUBSCRIPTION("createSubscription"), PING("ping");

            // TODO use in serialisation
            @SuppressWarnings("FieldCanBeLocal")
            private final String m_value;

            Op(String value) {
                m_value = value;
            }
        }

        /**
         * Not to be confused with the PING control frame of the WebSocket spec
         * @see ResponsePayload.Pong
         */
        private static class Ping extends RequestPayload {
            public Ping() {
                this.op = Op.PING;
            }
        }

        static class CreateSubscription extends RequestPayload {

            List<Subscription> data;

            public CreateSubscription(Subscription... subscriptions) {
                this.op = Op.CREATE_SUBSCRIPTION;
                this.data = Arrays.asList(subscriptions);
            }

            abstract static class Subscription {
                String type;

                String scope; // TODO

                List<Filter> filters;

                // TODO but de/serialise to/from String
                UUID id = UUID.randomUUID();

                UUID id() {
                    return this.id;
                }

                private static class RepositoryItems extends Subscription {
                    public RepositoryItems(Filter... filters) {
                        this.type = "repositoryItems";
                        this.filters = Arrays.asList(filters);
                        // TODO set scope, whatever that is
                    }
                }

            }

        }
    }

    // TODO move
    record Filter(String name, String value) {

    }

}
