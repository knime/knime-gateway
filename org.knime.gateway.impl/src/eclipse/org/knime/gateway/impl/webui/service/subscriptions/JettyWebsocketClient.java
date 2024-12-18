package org.knime.gateway.impl.webui.service.subscriptions;
import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.websocket.api.Callback;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.exceptions.CloseException;
import org.eclipse.jetty.websocket.api.exceptions.UpgradeException;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.knime.core.util.proxy.GlobalProxyConfig;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class JettyWebsocketClient extends WebsocketClient {

    private final org.eclipse.jetty.websocket.client.WebSocketClient m_client;

    private final JettyWebsocketListener m_websocketListener;

    private final Session m_session;

    public JettyWebsocketClient(
            final URI endpoint,
            final String authToken,
            final Function<URI, Optional<GlobalProxyConfig>> proxyConfig
    ) throws ConnectException {
        super(proxyConfig);
        // TODO use `ProxyConfigurator`
        m_client = createClient(endpoint);
        m_websocketListener = new JettyWebsocketListener();
        m_session = startSession(endpoint, authToken, m_client, m_websocketListener);
    }


    private WebSocketClient createClient(final URI endpoint) {
        HttpClient httpClient = new HttpClient();
        // A timeout can lead to proxy authentication failure if the back and forth takes too long
        httpClient.setConnectTimeout(10000);
        // TODO -- will modify httpClient
//        m_proxyConfigurator.configureProxy(createHttpUri(endpoint), httpClient);
        return new WebSocketClient(httpClient);
    }


    private Session startSession(final URI endpoint, final String authToken, final WebSocketClient webSocketClient,
            final JettyWebsocketListener webSocketListener) throws ConnectException {


        // Connect the client EndPoint to the server.
        try {
            webSocketClient.start();
            //ClientEndPoint clientEndPoint = new ClientEndPoint();
            // The server URI to connect to.
            URI serverURI = endpoint;
            CompletableFuture<Session> clientSessionPromise = webSocketClient.connect(this, serverURI);
            return clientSessionPromise.get();
        } catch (IOException | InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //        try {
//            var sessionFuture = webSocketClient.connect(webSocketListener, endpoint, createUpgradeRequest(authToken));
//            return sessionFuture.get(10, TimeUnit.SECONDS);
//        } catch (Exception exception) {
//            stopClient(webSocketClient);
//            throw translateException(exception);
//        }
    }


    private static ConnectException translateException(final Throwable throwable) {
        if (throwable instanceof ExecutionException executionException) {
            return translateException(executionException.getCause());
        } else if (throwable instanceof UpgradeException upgradeException) {
            int statusCode = upgradeException.getResponseStatusCode();
            var errorMessage = statusCode == 403 ? "Failed to authenticate."
                    : "Failed to establish connection. This might happen due to a proxy.";
            return new ConnectException(errorMessage, upgradeException);
        } else {
            return new ConnectException(throwable.getMessage(), throwable);
        }
    }

    static void stopClient(final WebSocketClient client) {
        new Thread(() -> LifeCycle.stop(client)).start();
    }

    private static ClientUpgradeRequest createUpgradeRequest(final String jwt) {
        var request = new ClientUpgradeRequest();
        request.setHeader("Authorization", jwt);
        return request;
    }

    @Override
    void send(final String message) {
        m_session.sendText(message, Callback.NOOP);
    }

    @Override
    void addListener(final Listener listener) {
        m_websocketListener.addListener(listener);
    }


    /**
     * Jetty-specific listener that is attached to the jetty websocket client implementation.
     * Wraps an implementation-agnostic, general {@link WebsocketClient.Listener}
     *
     * TODO currently duplicates JettyBackendFactory#WebsocketListener
     */
    static final class JettyWebsocketListener implements Session.Listener.AutoDemanding {

        private final Set<Listener> m_listeners = new HashSet<>();

        public JettyWebsocketListener() {
        }

        public void addListener(final Listener listener) {
            m_listeners.add(listener);
        }

        @Override
        public void onWebSocketText(final String message) {
            m_listeners.forEach(l -> l.onMessage(message));
        }

        @Override
        public void onWebSocketClose(final int statusCode, final String reason) {
            m_listeners.forEach(l -> l.onClose(statusCode, reason));
        }

        @Override
        public void onWebSocketError(final Throwable cause) {
            m_listeners.forEach(l -> l.onError(extractStatusCode(cause), cause.getMessage()));
        }

        private static int extractStatusCode(final Throwable cause) {
            if (cause instanceof UpgradeException upgradeException) {
                return upgradeException.getResponseStatusCode();
            } else if (cause instanceof CloseException closeException) {
                return closeException.getStatusCode();
            }
            return -1;
        }



    }
}
