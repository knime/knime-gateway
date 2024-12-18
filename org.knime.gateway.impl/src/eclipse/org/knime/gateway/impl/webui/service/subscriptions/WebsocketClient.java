package org.knime.gateway.impl.webui.service.subscriptions;

import java.io.Serial;
import java.net.URI;
import java.util.Optional;
import java.util.function.Function;

import org.knime.core.util.proxy.GlobalProxyConfig;
import org.knime.core.util.proxy.search.GlobalProxySearch;

/**
 * very similar to {@link org.knime.ai.assistant.java.Backend} -- the ws capabilities should be unified while still
 * having the generic `Backend` interface for the AI service (i.e. it will be implemented by this)
 *
 * I currently think this is a very reasonable interface for a websocket conn and nice that it matches with the concept
 * of the "AI backend"
 *
 * Implementing classes should make use of the proxy config supplied to the constructor
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public abstract class WebsocketClient {

    public static class ConnectException extends Exception {

        @Serial
        private static final long serialVersionUID = 1L;

        public ConnectException(final String message) {
            super(message);
        }

        public ConnectException(final String message, final Throwable cause) {
            super(message, cause);
        }
    }

    WebsocketClient(Function<URI, Optional<GlobalProxyConfig>> proxyConfig) {

    }

    public static WebsocketClient createDefault(final URI endpoint, final String authToken) {
        try {
            return new JettyWebsocketClient(endpoint, authToken, GlobalProxySearch::getCurrentFor);
        } catch (ConnectException e) {
            // TODO report, raise w/ this method, ...
        }
        return null;
    }

    abstract void send(final String message);

    abstract void addListener(final Listener listener);

    abstract static class Listener {
        abstract void onMessage(String message);

        abstract void onClose(final int statusCode, String reason);

        abstract void onError(final int statusCode, final String reason);

    }

    static abstract class MessageListener extends Listener {
        @Override
        void onClose(int statusCode, String reason) {
            // do nothing
        }

        @Override
        void onError(int statusCode, String reason) {
            // do nothing
        }
    }

}
