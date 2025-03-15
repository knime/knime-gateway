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
 *   Apr 30, 2024 (hornm): created
 */
package org.knime.gateway.impl.service.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.knime.core.node.util.CheckUtils;

/**
 * Helps to throttle calls, e.g., to an event consumer in order to not overwhelm it. It makes sure there is a minimum
 * duration between consecutive calls but also guarantees that 'latest' invocation is carried out.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public final class CallThrottle {

    /**
     * The minimum time interval between two consecutive calls in order to throttle the number of calls to not overwhelm
     * the consumer. If a calls takes longer than this amount, it won't be throttled any further.
     */
    private static final int MINIMUM_DURATION_BETWEEN_CONSECUTIVE_CALLBACKS_IN_MS = 100;

    /**
     * The time to (optionally) delay a call in case no other call is in progress. This is an optimization to prevent
     * calls from being executed less often than necessary in case of 'call storms' (i.e. many invocations within this
     * time interval).
     */
    private static final int CALL_DELAY_WHEN_IDLE_IN_MS = 30;

    private CallState m_callState = CallState.IDLE;

    private final ExecutorService m_executorService;

    private Runnable m_call;

    private final boolean m_delayWhenIdle;

    /**
     * @param call the logic to be run on {@link #invoke()}
     * @param threadName the name of the thread being used
     */
    public CallThrottle(final Runnable call, final String threadName) {
        this(call, threadName, false);
    }

    /**
     * @param call the logic to be run on {@link #invoke()}
     * @param threadName the name of the thread being used
     * @param delayWhenIdle whether to briefly delay a call while no other call is in progress - optimization to avoid
     *            too many calls in case of calls in very rapid succession
     */
    public CallThrottle(final Runnable call, final String threadName, final boolean delayWhenIdle) {
        CheckUtils.checkNotNull(call);
        m_call = call;
        m_executorService = Executors.newSingleThreadExecutor(r -> {
            var t = new Thread(r, threadName);
            t.setDaemon(true);
            return t;
        });
        m_delayWhenIdle = delayWhenIdle;
    }

    /**
     * Tries to execute the given call. It's either
     * <ul>
     * <li>queued to be the next call (if there is already a call in progress),</li>
     * <li>superseded by another follow-up call of this method,</li>
     * <li>executed immediately (if there is no other call in progress and {@code delayWhenIdle = false}),</li>
     * <li>or briefly delayed (if {@code delayWhenIdle = true}) and then executed, in case there is currently no other
     * call in progress. It helps to prevent the call from being executed more often than necessary in case of a 'call
     * storm' (i.e. many invocations within a split second).</li>
     * </ul>
     */
    public synchronized void invoke() {
        if (m_callState == CallState.IN_PROGRESS) {
            m_callState = CallState.IN_PROGRESS_AND_AWAITING;
        }

        if (m_delayWhenIdle) {
            if (m_callState == CallState.DELAYING) {
                return;
            }
            if (m_callState == CallState.IDLE) {
                m_callState = CallState.DELAYING;
                m_executorService.execute(() -> {
                    sleep(CALL_DELAY_WHEN_IDLE_IN_MS);
                    m_callState = CallState.IN_PROGRESS;
                    throttleAndExecuteInLoop();
                });
                return;
            }
        } else {
            if (m_callState == CallState.IDLE) {
                m_callState = CallState.IN_PROGRESS;
                m_executorService.execute(() -> {
                    throttleAndExecuteInLoop();
                });
                return;
            }
        }
    }

    private void throttleAndExecuteInLoop() {
        do {
            throttleAndExecute(m_call);
        } while (checkIsCallAwaitingAndChangeState());
    }

    private synchronized boolean checkIsCallAwaitingAndChangeState() {
        if (m_callState == CallState.IN_PROGRESS_AND_AWAITING) {
            m_callState = CallState.IN_PROGRESS;
            return true;
        } else {
            m_callState = CallState.IDLE;
            return false;
        }
    }

    private static void throttleAndExecute(final Runnable run) {
        var start = System.currentTimeMillis();
        run.run();
        var duration = System.currentTimeMillis() - start;
        var waitTimeToThrottle = MINIMUM_DURATION_BETWEEN_CONSECUTIVE_CALLBACKS_IN_MS - duration;
        if (waitTimeToThrottle > 0) {
            sleep(waitTimeToThrottle);
        }
    }

    private static void sleep(final long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) { // NOSONAR
            // ignore
        }
    }

    /**
     * Gives the current call state. Mainly for testing purposes.
     *
     * @return the current call state
     */
    public CallState getCallState() {
        return m_callState;
    }

    /**
     * Disposes the throttle.
     */
    public void dispose() {
        m_executorService.shutdown();
        m_call = null;
    }

    /**
     * The state the 'call process' is in.
     */
    public enum CallState {
            /**
             * No call in progress nor is one awaiting
             */
            IDLE,

            /**
             * Call in progress, none is awaiting
             */
            IN_PROGRESS,

            /**
             * Call in progress, and another one awaiting
             */
            IN_PROGRESS_AND_AWAITING,

            /**
             * Call is currently being delayed (briefly) in order for to allow other calls to arrive before the actual
             * action is carried out.
             */
            DELAYING;
    }

}
