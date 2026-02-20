package org.knime.gateway.impl.util;

import org.knime.core.node.NodeLogger;
import org.knime.core.node.workflow.NodeProgressEvent;

/**
 * Utilities
 * @since 5.11
 */
public final class NonThrowing {
    private NonThrowing() {

    }

    /**
     * A consumer wrapped in try-catch-log
     * @param consumer The callback to run in try
     * @param <V> The value type
     */
    public record Consumer<V>(java.util.function.Consumer<V> consumer) implements java.util.function.Consumer<V> {

        @Override
        public void accept(V value) {
            try {
                consumer().accept(value);
            } catch (Throwable throwable) {
                NodeLogger.getLogger(Consumer.class).error(throwable);
            }
        }
    }

    /**
     * A progress listener wrapped in try-catch-log
     * @param listener The listener to notify, wrapped in try
     */
    public record NodeProgressListener(
            org.knime.core.node.workflow.NodeProgressListener listener) implements org.knime.core.node.workflow.NodeProgressListener {

        @Override
        public void progressChanged(NodeProgressEvent nodeProgressEvent) {
            try {
                listener().progressChanged(nodeProgressEvent);
            } catch (Throwable throwable) {
                NodeLogger.getLogger(NodeProgressListener.class).error(throwable);
            }
        }
    }
}
