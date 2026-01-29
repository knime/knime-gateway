/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.org; Email: contact@knime.org
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME AG herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ---------------------------------------------------------------------
 *
 * History
 *   Nov 14, 2025 (motacilla): created
 */
package org.knime.gateway.impl.util;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.function.UnaryOperator;

/**
 * Can be used to wrap actions that should not be executed too frequently.
 *
 * @author Kai Franze, KNIME GmbH, Germany
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @since 5.10
 */
public final class Debouncer {

    private final ScheduledExecutorService m_scheduler =
        Executors.newSingleThreadScheduledExecutor(new DaemonThreadFactory());

    private final Duration m_delay;

    private ScheduledFuture<?> m_scheduledTask;

    private final Runnable m_runnable;

    private State m_state = State.IDLE;

    /**
     * Daemon threads do not block JVM shutdown even if call to {@link #shutdown()} is missed.
     */
    private static final class DaemonThreadFactory implements ThreadFactory {

        @Override
        public Thread newThread(final Runnable r) {
            final var t = new Thread(r);
            t.setDaemon(true);
            Optional.ofNullable(Debouncer.class.getCanonicalName()) //
                .ifPresent(t::setName);
            return t;
        }
    }

    /**
     * Creates a new Debouncer.
     *
     * @param delay -
     * @param runnable -
     */
    public Debouncer(final Duration delay, final Runnable runnable) {
        m_delay = delay;
        m_runnable = () -> {
            updateState(state -> State.EXECUTING);

            runnable.run();

            var previousState = updateState(state -> State.IDLE);
            if (previousState == State.EXECUTING_AND_SCHEDULED) {
                call();
            }
        };
    }

    /**
     * Will schedule the task to be executed after the specified delay. If called again before the delay has passed, the
     * previous call is cancelled and the delay restarts. If called while the task is already running, another execution
     * will be scheduled once the task is done.
     *
     * Won't do anything if {@link #shutdown()} has been called.
     */
    public void call() {
        if (m_scheduler.isShutdown()) {
            return;
        }

        updateState(state -> switch (state) {
            case State.IDLE -> {
                scheduleTask();
                yield State.SCHEDULED;
            }
            case State.SCHEDULED -> { // task not executed, yet -> re-schedule
                if (m_scheduledTask.getDelay(TimeUnit.MILLISECONDS) > 0) {
                    m_scheduledTask.cancel(false);
                    scheduleTask();
                }
                yield State.SCHEDULED;
            }
            case State.EXECUTING -> State.EXECUTING_AND_SCHEDULED; // task not completed, yet -> schedule another call once completed
            case State.EXECUTING_AND_SCHEDULED -> State.EXECUTING_AND_SCHEDULED;
        });
    }

    private synchronized State updateState(final UnaryOperator<State> updater) {
        var previousState = m_state;
        m_state = updater.apply(m_state);
        return previousState;
    }

    private void scheduleTask() {
        m_scheduledTask = m_scheduler.schedule(m_runnable, m_delay.toSeconds(), TimeUnit.SECONDS);
    }

    /**
     * For testing purposes only.
     *
     * @return the current state
     */
    synchronized State getState() {
        return m_state;
    }

    /**
     * Shuts down the internal scheduler.
     */
    public void shutdown() {
        m_scheduler.shutdown();
    }

    enum State {
            IDLE, SCHEDULED, EXECUTING, EXECUTING_AND_SCHEDULED;
    }

}
