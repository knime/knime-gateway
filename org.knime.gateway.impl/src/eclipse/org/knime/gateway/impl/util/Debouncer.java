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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Can be used to wrap actions that should not be executed too frequently.
 *
 * @author Kai Franze, KNIME GmbH, Germany
 * @since 5.10
 */
public final class Debouncer {

    private final ScheduledExecutorService m_scheduler = Executors.newSingleThreadScheduledExecutor();

    private final Duration m_delay;

    private ScheduledFuture<?> m_pendingTask;

    private final Runnable m_task;

    /**
     * Creates a new Debouncer.
     *
     * @param delay -
     * @param task -
     */
    public Debouncer(final Duration delay, final Runnable task) {
        m_delay = delay;
        m_task = task;
    }

    /**
     * Will schedule the task to be executed after the specified delay. If called again before the delay has passed, the
     * previous call is cancelled and the delay restarts.
     */
    public void call() {
        if (m_pendingTask != null && !m_pendingTask.isDone()) {
            m_pendingTask.cancel(false); // Set to 'false' to not cancel if the actual sync is already running
        }
        m_pendingTask = m_scheduler.schedule(m_task::run, m_delay.toSeconds(), TimeUnit.SECONDS);
    }

    /**
     * TODO: Do we need to call this, and if so, when?
     */
    public void shutdown() {
        m_scheduler.shutdown();
    }
}
