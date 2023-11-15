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

import java.io.IOException;
import java.util.List;

import org.knime.core.node.NodeLogger;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.api.webui.entity.CommandResultEnt;
import org.knime.gateway.api.webui.entity.WorkflowCommandEnt;
import org.knime.gateway.api.webui.entity.WorkflowSnapshotEnt;
import org.knime.gateway.api.webui.service.WorkflowService;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.InvalidRequestException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.NodeNotFoundException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.NotASubWorkflowException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.OperationNotAllowedException;
import org.knime.gateway.api.webui.util.WorkflowBuildContext;
import org.knime.gateway.impl.webui.NodeFactoryProvider;
import org.knime.gateway.impl.webui.WorkflowKey;
import org.knime.gateway.impl.webui.WorkflowMiddleware;
import org.knime.gateway.impl.webui.WorkflowUtil;
import org.knime.gateway.impl.webui.spaces.SpaceProviders;

/**
 * The default workflow service implementation for the web-ui.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public final class DefaultWorkflowService implements WorkflowService {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(DefaultWorkflowService.class);

    private final WorkflowMiddleware m_workflowMiddleware =
        ServiceDependencies.getServiceDependency(WorkflowMiddleware.class, true);

    private final NodeFactoryProvider m_nodeFactoryProvider =
        ServiceDependencies.getServiceDependency(NodeFactoryProvider.class, false);

    private final SpaceProviders m_spaceProvides =
        ServiceDependencies.getServiceDependency(SpaceProviders.class, false);

    /**
     * Returns the singleton instance for this service.
     *
     * @return the singleton instance
     */
    public static DefaultWorkflowService getInstance() {
        return ServiceInstances.getDefaultServiceInstance(DefaultWorkflowService.class);
    }

    DefaultWorkflowService() {
        //
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorkflowSnapshotEnt getWorkflow(final String projectId, final NodeIDEnt workflowId,
        final Boolean includeInfoOnAllowedActions) throws NotASubWorkflowException, NodeNotFoundException {
        DefaultServiceContext.assertWorkflowProjectId(projectId);
        var wfKey = new WorkflowKey(projectId, workflowId);
        LOGGER.debug("'getWorkflow()' was called for: " + wfKey); // Quickly get project ID and workflow ID while debugging
        if (Boolean.TRUE.equals(includeInfoOnAllowedActions)) {
            var buildContext = WorkflowBuildContext.builder()//
                .includeInteractionInfo(true)//
                .canUndo(m_workflowMiddleware.getCommands().canUndo(wfKey))//
                .canRedo(m_workflowMiddleware.getCommands().canRedo(wfKey));
            return m_workflowMiddleware.buildWorkflowSnapshotEnt(wfKey, () -> buildContext);
        } else {
            var buildConext = WorkflowBuildContext.builder().includeInteractionInfo(false);
            return m_workflowMiddleware.buildWorkflowSnapshotEnt(wfKey, () -> buildConext);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<NodeIDEnt> getLinkUpdates(final String projectId, final NodeIDEnt workflowId)
        throws NotASubWorkflowException, NodeNotFoundException, InvalidRequestException {
        DefaultServiceContext.assertWorkflowProjectId(projectId);
        final var wfKey = new WorkflowKey(projectId, workflowId);
        final var wfm = WorkflowUtil.getWorkflowManager(wfKey);
        try {
            final var nodesToUpdate = CoreUtil.getUpdatableLinkedComponentsAndSetUpdateStatus(wfm);
            return nodesToUpdate.stream()//
                .map(NodeIDEnt::new)//
                .toList();
        } catch (IOException e) {
            throw new InvalidRequestException("Could not determine updatable node IDs", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommandResultEnt executeWorkflowCommand(final String projectId, final NodeIDEnt workflowId,
        final WorkflowCommandEnt workflowCommandEnt)
        throws NotASubWorkflowException, NodeNotFoundException, OperationNotAllowedException {
        DefaultServiceContext.assertWorkflowProjectId(projectId);
        return m_workflowMiddleware.getCommands().execute(new WorkflowKey(projectId, workflowId), workflowCommandEnt,
            m_workflowMiddleware, m_nodeFactoryProvider, m_spaceProvides);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void undoWorkflowCommand(final String projectId, final NodeIDEnt workflowId)
        throws OperationNotAllowedException {
        DefaultServiceContext.assertWorkflowProjectId(projectId);
        m_workflowMiddleware.getCommands().undo(new WorkflowKey(projectId, workflowId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void redoWorkflowCommand(final String projectId, final NodeIDEnt workflowId)
        throws OperationNotAllowedException {
        DefaultServiceContext.assertWorkflowProjectId(projectId);
        m_workflowMiddleware.getCommands().redo(new WorkflowKey(projectId, workflowId));
    }

}
