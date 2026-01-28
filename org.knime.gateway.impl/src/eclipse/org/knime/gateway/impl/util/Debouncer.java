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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Can be used to wrap actions that should not be executed too frequently.
 *
 * @author Kai Franze, KNIME GmbH, Germany
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @since 5.10
 */
public final class Debouncer {

    /**
     * It is vital for the debounce implementation that this is a single-thread executor.
     */
    private final ExecutorService m_executor = Executors.newSingleThreadExecutor(new DaemonThreadFactory());

    private final Runnable m_runnable;

    private final AtomicReference<State> m_state = new AtomicReference<>(State.IDLE);

    private final Semaphore m_semaphore = new Semaphore(0);

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
     * @param userTask -
     */
    public Debouncer(final Duration delay, final Runnable userTask) {
        m_runnable = () -> {
            // each call during the delay restarts the delay, multiple calls collapse to a single follow-up run
            do {
                try {
                    // block until delay has elapsed or permit becomes available
                    m_semaphore.tryAcquire(delay.toMillis(), TimeUnit.MILLISECONDS);
                } catch (InterruptedException ex) { // formality
                    Thread.currentThread().interrupt();
                    return;
                }
            } while (m_state.updateAndGet( //
                state -> state == State.QUEUED //
                    ? State.SCHEDULED //
                    : State.IDLE //
            ) == State.SCHEDULED);
            // go again if we've had an incoming call during tryAcquire
            // the do-while "drains" the QUEUED state by delaying again and invoking a follow-up userTask run
            userTask.run();
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
        if (m_executor.isShutdown()) {
            return;
        }

        if (m_state.compareAndSet(State.IDLE, State.SCHEDULED)) {
            // if IDLE: transition to SCHEDULED and enqueue runnable on thread executor
            m_executor.execute(m_runnable);
        } else if (m_state.compareAndSet(State.SCHEDULED, State.QUEUED)) {
            // if SCHEDULED: transition to QUEUED and unblock any runnable currently at tryAcquire,
            // drop this #call
            m_semaphore.release();
        }
    }

    /**
     * Shuts down the internal scheduler.
     */
    public void shutdown() {
        m_executor.shutdown();
    }

    private enum State {
            /**
             * No worker queued
             */
            IDLE,
            /**
             * Worker waiting a delay
             */
            SCHEDULED,
            /**
             * "Delay has been reset"-signal is pending
             */
            QUEUED,
    }

}
