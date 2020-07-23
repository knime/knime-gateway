/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.com; Email: contact@knime.com
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
