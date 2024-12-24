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

import org.knime.core.node.NodeLogger;

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

    private final AtomicCallState m_callState = new AtomicCallState();

    private final ExecutorService m_executorService;

    private Runnable m_call;

    /**
     * @param call the logic to be run on {@link #invoke()}
     * @param threadName the name of the thread being used
     */
    public CallThrottle(final Runnable call, final String threadName) {
        m_call = call;
        m_executorService = Executors.newSingleThreadExecutor(r -> {
            var t = new Thread(r, threadName);
            t.setDaemon(true);
            return t;
        });
    }

    /**
     * Tries to execute the given call. It's either executed directly, queued to be the next call or superseded by
     * another follow-up call of this method.
     */
    public void invoke() {
        if (!m_callState.checkIsCallInProgressAndChangeState()) {
            m_executorService.execute(() -> {
                do {
                    throttle(m_call);
                } while (m_callState.checkIsCallAwaitingAndChangeState());
            });
        }
    }

    private static void throttle(final Runnable run) {
        if (run == null) {
            NodeLogger.getLogger(CallThrottle.class).coding("No runnable given.");
            return;
        }
        var start = System.currentTimeMillis();
        run.run();
        var duration = System.currentTimeMillis() - start;
        var waitTimeToThrottle = MINIMUM_DURATION_BETWEEN_CONSECUTIVE_CALLBACKS_IN_MS - duration;
        if (waitTimeToThrottle > 0) {
            try {
                Thread.sleep(waitTimeToThrottle);
            } catch (InterruptedException ex) { // NOSONAR
                // ignore
            }
        }
    }

    /**
     * Gives the current call state. Mainly for testing purposes.
     *
     * @return the current call state
     */
    public CallState getCallState() {
        return m_callState.m_state;
    }

    /**
     * Disposes the throttle.
     */
    void dispose() {
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
            IN_PROGRESS_AND_AWAITING;
    }

    private static final class AtomicCallState {

        private CallState m_state = CallState.IDLE;

        /**
         * If the call is in progress, state will change to 'in progress and one call awaiting' (2); else to 'in
         * progress, no call awaiting' (1).
         *
         * @return <code>true</code> if the call is in progress, otherwise <code>false</code>
         */
        synchronized boolean checkIsCallInProgressAndChangeState() {
            if (m_state == CallState.IDLE) {
                m_state = CallState.IN_PROGRESS;
                return false;
            } else {
                m_state = CallState.IN_PROGRESS_AND_AWAITING;
                return true;
            }
        }

        /**
         * If a call is awaiting (and another already in progress), the state will change to 'in progress' (1);
         * otherwise it will change to 'not in progress, none awaiting' (0)
         *
         * @return <code>true</code> if a call is awaiting, otherwise <code>false</code>
         */
        synchronized boolean checkIsCallAwaitingAndChangeState() {
            if (m_state == CallState.IN_PROGRESS_AND_AWAITING) {
                m_state = CallState.IN_PROGRESS;
                return true;
            } else {
                m_state = CallState.IDLE;
                return false;
            }
        }
    }

}
