/* ------------------------------------------------------------------
 * This source code, its documentation and all appendant files
 * are protected by copyright law. All rights reserved.
 *
 * Copyright by KNIME AG, Zurich, Switzerland
 *
 * You may not modify, publish, transmit, transfer or sell, reproduce,
 * create derivative works from, distribute, perform, display, or in
 * any way exploit any of the content, in whole or in part, except as
 * otherwise expressly permitted in writing by the copyright owner or
 * as specified in the license file distributed with this product.
 *
 * If you have any questions please contact the copyright holder:
 * website: www.knime.com
 * email: contact@knime.com
 * ---------------------------------------------------------------------
 *
 */
package com.knime.gateway.remote.service.util;

import java.util.UUID;

import org.knime.core.node.NodeLogger;
import org.knime.core.node.workflow.WorkflowPersistor;
import org.knime.core.util.LRUCache;

/**
 * Keeps track of copies of parts of workflows, represented by {@link WorkflowPersistor}-instances.
 *
 * The number of copies kept in the repository is limited and least recently used ones will be removed if exceeded.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class WorkflowCopyRepository {
    private static final NodeLogger LOGGER = NodeLogger.getLogger(WorkflowCopyRepository.class);

    /* The default value of the maximum number of copies of workflow-parts in memory - can be set via a system property */
    private static final int DEFAULT_MAX_NUM_COPIES_IN_MEM = 500;

    private final int maxNumCopiesInMem = getMaxNumCopiesInMem();

    private static int getMaxNumCopiesInMem() {
        String prop = System.getProperty("com.knime.enterprise.executor.jobview.max_num_workflow_copies_in_mem");
        if (prop != null) {
            try {
                return Integer.parseInt(prop);
            } catch (NumberFormatException e) {
                LOGGER.warn("Couldn't parse value for system property"
                    + " 'com.knime.enterprise.executor.jobview.max_num_workflow_copies_in_mem'");
            }
        }
        return DEFAULT_MAX_NUM_COPIES_IN_MEM;
    }

    private final LRUCache<UUID, WorkflowPersistor> m_workflowCopyMap =
        new LRUCache<UUID, WorkflowPersistor>(maxNumCopiesInMem);

    /**
     * Adds a new copy to the repo.
     *
     * @param copyId identifier of the copy
     * @param copy the copy represented by a {@link WorkflowPersistor}-instance
     */
    public void put(final UUID copyId, final WorkflowPersistor copy) {
        m_workflowCopyMap.put(copyId, copy);
    }

    /**
     * Let one retrieve a copy from the repo (if available).
     *
     * @param copyId the identifier
     * @return the copy represented by a {@link WorkflowPersistor}-instance or <code>null</code> if there is none (e.g.
     *         because it has been removed due to an exceeded number of copies)
     */
    public WorkflowPersistor get(final UUID copyId) {
        return m_workflowCopyMap.get(copyId);
    }
}
