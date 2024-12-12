package org.knime.gateway.impl.webui.service.subscriptions;


import java.net.URI;
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
public class EventDispatcherClient implements AutoCloseable {

    private final WebsocketClient websocketClient;

    private static final AdaptiveHeartbeat.Duration defaultHeartbeatDelay = AdaptiveHeartbeat.Duration.ofSeconds(30);

    private final ActiveSubscriptions activeSubscriptions = new ActiveSubscriptions();

    public EventDispatcherClient() throws WebsocketClient.ConnectException {
        var endpoint = "foo"; // TODO
        var authToken = "bar"; // TODO

        // TODO this will already connect & upgrade to ws and all, and throw -- wanted?
        // TODO should rather just connect when first subscription is done -- lazyInit
        websocketClient = WebsocketClient.createDefault(URI.create(endpoint), authToken);

        var heartbeat = new AdaptiveHeartbeat(() -> {
            websocketClient.send(new RequestPayload.Ping().serialise());
        });
        heartbeat.setDelay(defaultHeartbeatDelay);

        websocketClient.addListener(new WebsocketClient.Listener() {
            @Override
            void onMessage(String message) {
                // TODO deserialise `message`
                var isEvent = true;
                boolean isHeartbeatPong = true;
                if (isEvent) {
                    var mockEventResponse = new ResponsePayload.Event();
                    mockEventResponse.notifications().forEach(activeSubscriptions::notify);
                } else  if (isHeartbeatPong) {
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
        });


    }

    static class ActiveSubscriptions {

        Map<UUID, ActiveSubscription> activeSubscriptions = new HashMap<>();

        public void notify(ResponsePayload.Event.SubscriptionNotification notification) {
            this.get(notification.id()).ifPresent(
                    subscription -> subscription.callback().accept(
                            notification.timestamp(),
                            notification.count()
                    ));
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
    public void subscribeToSpace(final String spaceId, SubscriptionCallback callback) {
        var entity = new RequestPayload.CreateSubscription.Subscription.RepositoryItems(new Filter("spaceId", spaceId));
        activeSubscriptions.put(new ActiveSubscription(entity, callback));
        updateSubscriptions();
    }

    private void updateSubscriptions() {
        websocketClient.send(new RequestPayload.CreateSubscription(activeSubscriptions.getPayloads()).serialise());
    }

    @Override
    public void close() throws Exception {
        websocketClient.close();
    }

    public interface SubscriptionCallback {
        void accept(String timestamp, int count);
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
