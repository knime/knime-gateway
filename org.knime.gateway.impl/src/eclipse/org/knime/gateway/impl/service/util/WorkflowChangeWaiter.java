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
 */
package org.knime.gateway.impl.service.util;

import java.util.concurrent.Semaphore;

/**
 * Semaphore that is released once a specific workflow change event is tracked.
 * @author Benjamin Moser, KNIME GmbH, Konstanz, Germany
 *
 */
public class WorkflowChangeWaiter {

    private final WorkflowChangesListener m_wfChangesListener;

    private final Semaphore m_semaphore;

    private final Runnable m_postProcessCallback;

    private final WorkflowChangesTracker m_tracker;

    /**
     * Create and configure a workflow change waiter instance for the given event on the given workflow.
     *
     * @param workflowChanges The workflow change to wait for
     */
    WorkflowChangeWaiter(final WorkflowChangesTracker.WorkflowChange[] workflowChanges,
        final WorkflowChangesListener wfChangesListener) {
        m_wfChangesListener = wfChangesListener;

        m_semaphore = new Semaphore(0, true);

        m_tracker = m_wfChangesListener.createWorkflowChangeTracker();

        m_postProcessCallback = () -> {
            if (Boolean.FALSE.equals(m_tracker.invoke(t -> t.hasOccurredAtLeastOne(workflowChanges)))) {
                return;
            }
            m_semaphore.release();
        };
        m_wfChangesListener.addPostProcessCallback(m_postProcessCallback);

    }

    /**
     * Block until the workflow change given during initialisation has occurred.
     * @throws InterruptedException If the waiting thread is interrupted.
     */
    public void blockUntilOccurred() throws InterruptedException {
        try {
            m_semaphore.acquire();
        }  finally {
            m_wfChangesListener.removeWorkflowChangesTracker(m_tracker);
            m_wfChangesListener.removePostProcessCallback(m_postProcessCallback);
        }
    }

}
