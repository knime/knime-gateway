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
 *   Jul 30, 2020 (hornm): created
 */
package org.knime.gateway.impl.webui.service;

import static org.knime.gateway.api.entity.EntityBuilderManager.builder;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.WorkflowEnt;
import org.knime.gateway.api.webui.entity.WorkflowOperationEnt;
import org.knime.gateway.api.webui.entity.WorkflowSnapshotEnt;
import org.knime.gateway.api.webui.entity.WorkflowSnapshotEnt.WorkflowSnapshotEntBuilder;
import org.knime.gateway.api.webui.service.WorkflowService;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.NodeNotFoundException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.NotASubWorkflowException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.OperationNotAllowedException;
import org.knime.gateway.api.webui.util.EntityBuilderUtil;
import org.knime.gateway.impl.project.WorkflowProjectManager;
import org.knime.gateway.impl.service.util.DefaultServiceUtil;
import org.knime.gateway.impl.service.util.EntityRepository;
import org.knime.gateway.impl.service.util.SimpleRepository;
import org.knime.gateway.impl.webui.service.operations.WorkflowOperations;

/**
 * The default workflow service implementation for the web-ui.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public final class DefaultWorkflowService implements WorkflowService {

    /**
     * Determines the number of operations per workflow kept in the undo and redo stacks.
     */
    private static final int UNDO_AND_REDO_STACK_SIZE_PER_WORKFLOW = 50;

    private static final DefaultWorkflowService INSTANCE = new DefaultWorkflowService();

    private final EntityRepository<WorkflowKey, WorkflowEnt> m_entityRepo;

    private final WorkflowOperations m_operations;

    /**
     * Returns the singleton instance for this service.
     *
     * @return the singleton instance
     */
    public static DefaultWorkflowService getInstance() {
        return INSTANCE;
    }

    private DefaultWorkflowService() {
        m_entityRepo = new SimpleRepository<>(1, new SnapshotIdGenerator());
        m_operations = new WorkflowOperations(UNDO_AND_REDO_STACK_SIZE_PER_WORKFLOW);
        WorkflowProjectManager.addWorkflowProjectRemovedListener(id -> {
            m_entityRepo.disposeHistory(k -> k.getProjectId().equals(id));
            m_operations.disposeUndoAndRedoStacks(id);
        });
    }

    EntityRepository<WorkflowKey, WorkflowEnt> getEntityRepository() {
        return m_entityRepo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorkflowSnapshotEnt getWorkflow(final String projectId, final NodeIDEnt workflowId,
        final Boolean includeInfoOnAllowedActions) throws NotASubWorkflowException, NodeNotFoundException {
        WorkflowKey wfKey = new WorkflowKey(projectId, workflowId);
        WorkflowManager wfm = getWorkflowManager(wfKey);
        if (Boolean.TRUE.equals(includeInfoOnAllowedActions)) {
            return buildWorkflowSnapshotEnt(EntityBuilderUtil.buildWorkflowEntWithInteractionInfo(wfm,
                m_operations.canUndo(wfKey), m_operations.canRedo(wfKey)), wfKey);
        } else {
            return buildWorkflowSnapshotEnt(EntityBuilderUtil.buildWorkflowEnt(wfm), wfKey);
        }
    }

    /**
     * Helper method to get the workflow manager from a project-id and workflow-id (referencing a sub-workflow or
     * 'root').
     *
     * @param wfKey
     * @return the workflow manager
     * @throws NodeNotFoundException if there is no metanode or component for the given workflow-id
     * @throws NotASubWorkflowException if the workflow-id doesn't reference a metanode or a component
     */
    public static WorkflowManager getWorkflowManager(final WorkflowKey wfKey)
        throws NodeNotFoundException, NotASubWorkflowException {
        WorkflowManager wfm;
        try {
            wfm = DefaultServiceUtil.getWorkflowManager(wfKey.getProjectId(), wfKey.getWorkflowId());
        } catch (IllegalArgumentException ex) {
            throw new NodeNotFoundException(ex.getMessage(), ex);
        } catch (IllegalStateException ex) {
            throw new NotASubWorkflowException(ex.getMessage(), ex);
        }
        return wfm;
    }

    WorkflowSnapshotEnt buildWorkflowSnapshotEnt(final WorkflowEnt workflow, final WorkflowKey wfKey) {
        String snapshotId = m_entityRepo.commit(wfKey, workflow);
        return builder(WorkflowSnapshotEntBuilder.class).setSnapshotId(snapshotId).setWorkflow(workflow).build();
    }

    private static class SnapshotIdGenerator implements Supplier<String> {

        private AtomicLong m_count = new AtomicLong();

        @Override
        public String get() {
            return Long.toString(m_count.getAndIncrement());
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void applyWorkflowOperation(final String projectId, final NodeIDEnt workflowId,
        final WorkflowOperationEnt workflowOperationEnt)
        throws NotASubWorkflowException, NodeNotFoundException, OperationNotAllowedException {
        m_operations.apply(new WorkflowKey(projectId, workflowId), workflowOperationEnt);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void undoWorkflowOperation(final String projectId, final NodeIDEnt workflowId)
        throws OperationNotAllowedException {
        m_operations.undo(new WorkflowKey(projectId, workflowId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void redoWorkflowOperation(final String projectId, final NodeIDEnt workflowId)
        throws OperationNotAllowedException {
        m_operations.redo(new WorkflowKey(projectId, workflowId));
    }

    /**
     * Gives access to the workflow operations instance.
     *
     * @return the workflow operations instance
     */
    WorkflowOperations getWorkflowOperations() {
        return m_operations;
    }

}
