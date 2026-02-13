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
import static org.knime.gateway.api.entity.NodeIDEnt.getRootID;
import static org.knime.gateway.impl.webui.service.ServiceUtilities.getSpaceProvidersKey;

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.ui.component.CheckForComponentUpdatesUtil;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.api.util.VersionId;
import org.knime.gateway.api.webui.entity.CommandResultEnt;
import org.knime.gateway.api.webui.entity.NodeIdAndIsExecutedEnt;
import org.knime.gateway.api.webui.entity.SpaceProviderEnt;
import org.knime.gateway.api.webui.entity.WorkflowCommandEnt;
import org.knime.gateway.api.webui.entity.WorkflowMonitorStateSnapshotEnt;
import org.knime.gateway.api.webui.entity.WorkflowSnapshotEnt;
import org.knime.gateway.api.webui.service.WorkflowService;
import org.knime.gateway.api.webui.service.util.MutableServiceCallException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.InvalidRequestException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.LoggedOutException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.NetworkException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.NodeNotFoundException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.NotASubWorkflowException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.ServiceCallException;
import org.knime.gateway.api.webui.util.EntityFactory;
import org.knime.gateway.api.webui.util.WorkflowBuildContext;
import org.knime.gateway.impl.project.ProjectManager;
import org.knime.gateway.impl.service.util.DefaultServiceUtil;
import org.knime.gateway.impl.webui.NodeFactoryProvider;
import org.knime.gateway.impl.webui.WorkflowKey;
import org.knime.gateway.impl.webui.WorkflowMiddleware;
import org.knime.gateway.impl.webui.WorkflowUtil;
import org.knime.gateway.impl.webui.spaces.LinkVariants;
import org.knime.gateway.impl.webui.spaces.SpaceProvidersManager;
import org.knime.gateway.impl.webui.spaces.SpaceProvidersManager.Key;

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

    private final SpaceProvidersManager m_spaceProvidersManager =
        ServiceDependencies.getServiceDependency(SpaceProvidersManager.class, false);

    private final ProjectManager m_projectManager =
        ServiceDependencies.getServiceDependency(ProjectManager.class, true);

    private final LinkVariants m_linkVariants = ServiceDependencies.getServiceDependency(LinkVariants.class, false);

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

    @Override
    public WorkflowSnapshotEnt getWorkflow(final String projectId, final NodeIDEnt workflowId, final String versionId,
        final Boolean includeInteractionInfo) throws NodeNotFoundException {
        final var version = VersionId.parse(versionId);
        final var wfKey = new WorkflowKey(projectId, workflowId, version);
        final var wfm = ServiceUtilities.assertProjectIdAndGetWorkflowManager(wfKey);
        final var buildContext = WorkflowBuildContext.builder();
        buildContext.setVersion(version);
        // TODO NXT-3605 remove `includeInteractionInfo` (NOSONAR)
        if (Boolean.TRUE.equals(includeInteractionInfo) && version.isCurrentState()) {
            Map<String, SpaceProviderEnt.TypeEnum> providerTypes = m_spaceProvidersManager == null //
                ? Map.of() //
                : m_spaceProvidersManager.getSpaceProviders(Key.of(wfKey.getProjectId())).getProviderTypes();
            var workflowSyncer = m_workflowMiddleware.getWorkflowSyncer(wfKey);
            buildContext.includeInteractionInfo(true)//
                .canUndo(m_workflowMiddleware.getCommands().canUndo(wfKey))//
                .canRedo(m_workflowMiddleware.getCommands().canRedo(wfKey))//
                .setSpaceProviderTypes(providerTypes) //
                .setComponentPlaceholders(
                    m_workflowMiddleware.getComponentLoadJobManager(wfKey).getComponentPlaceholdersAndCleanUp()) //
                .setSyncStateSupplier(workflowSyncer::getSyncState);
        } else {
            buildContext.includeInteractionInfo(false);
        }
        if (version.isCurrentState()) {
            return m_workflowMiddleware.buildWorkflowSnapshotEnt(wfKey, () -> buildContext);
        } else {
            // fixed versions are not editable,
            // we do not need to cache state, execute commands, provide change events etc. for these
            var workflowEntity = EntityFactory.Workflow.buildWorkflowEnt(wfm, buildContext);
            return builder(WorkflowSnapshotEnt.WorkflowSnapshotEntBuilder.class) //
                .setSnapshotId(null) //
                .setWorkflow(workflowEntity) //
                .build();
        }
    }

    @Override
    public List<NodeIdAndIsExecutedEnt> getUpdatableLinkedComponents(final String projectId, final NodeIDEnt workflowId)
        throws NodeNotFoundException, NotASubWorkflowException, InvalidRequestException {
        DefaultServiceContext.assertWorkflowProjectId(projectId);
        final var wfKey = new WorkflowKey(projectId, workflowId);
        final var wfm = WorkflowUtil.getWorkflowManager(wfKey);
        try {
            final var linkedComponentsToStateMap = CoreUtil.getLinkedComponentToStateMap(wfm);
            final var candidateList = linkedComponentsToStateMap.keySet().stream().toList();
            final var componentUpdateResult = CheckForComponentUpdatesUtil.checkForComponentUpdatesAndSetUpdateStatus(
                wfm, "org.knime.gateway.impl", candidateList, new NullProgressMonitor());
            return componentUpdateResult.updateList().stream()//
                .map(wfm::findNodeContainer)//
                .map(NodeContainer::getID)//
                .map(nodeId -> {
                    final var ncState = linkedComponentsToStateMap.get(nodeId);
                    return EntityFactory.Workflow.buildNodeIdAndIsExecutedEnt(nodeId, ncState, wfm);
                })//
                .toList();
        } catch (IllegalStateException | InterruptedException e) { // NOSONAR
            throw InvalidRequestException.builder().withTitle("Failed to retrieve linked components") //
                .withDetails(e.getClass().getSimpleName() + ": " + e.getMessage()) //
                .canCopy(true) //
                .withCause(e) //
                .build();
        }
    }

    @Override
    public void saveProject(final String projectId) throws ServiceCallException, LoggedOutException, NetworkException {
        // Note that the current implementation can only work in the browser context.
        if (DefaultServiceContext.getProjectId().isEmpty()) {
            // only to be called from browser environment and this value is only set in browser environment
            LOGGER.warn("Called 'saveProject' without project id, indicating usage from Desktop environment.");
            return;
        }
        DefaultServiceContext.assertWorkflowProjectId(projectId);
        m_workflowMiddleware.getWorkflowSyncer(new WorkflowKey(projectId, getRootID())).syncProjectNow();
    }

    @Override
    public void disposeVersion(final String projectId, final String versionParameter) throws ServiceCallException {
        DefaultServiceContext.assertWorkflowProjectId(projectId);
        disposeVersion(projectId, VersionId.parse(versionParameter));
    }

    @Override
    public void restoreVersion(final String projectId, final String versionId)
        throws ServiceCallException, NetworkException, LoggedOutException {
        DefaultServiceContext.assertWorkflowProjectId(projectId);
        if (m_spaceProvidersManager == null) {
            throw ServiceCallException.builder() //
                .withTitle("Failed to restore item version") //
                .withDetails("Surrounding space could not be identified (missing space providers manager)") //
                .canCopy(false) //
                .build();
        }
        final var project = m_projectManager.getProject(projectId).orElseThrow(); // No specific version needed here
        final var origin = project.getOrigin().orElseThrow();
        try {
            m_spaceProvidersManager.getSpaceProviders(getSpaceProvidersKey()) //
                .getSpace(origin.providerId(), origin.spaceId()) //
                .restoreItemVersion(origin.itemId(), VersionId.parse(versionId));
        } catch (final MutableServiceCallException ex) { // NOSONAR
            throw ex.toGatewayException("Failed to restore item version");
        }
        disposeVersion(projectId, VersionId.currentState());
    }

    @Override
    public CommandResultEnt executeWorkflowCommand(final String projectId, final NodeIDEnt workflowId,
        final WorkflowCommandEnt workflowCommandEnt) throws ServiceCallException {
        DefaultServiceContext.assertWorkflowProjectId(projectId);
        DefaultServiceUtil.assertProjectVersion(projectId, VersionId.currentState());
        var key = DefaultServiceContext.getProjectId().map(Key::of).orElse(Key.defaultKey());
        var spaceProviders = m_spaceProvidersManager == null ? null : m_spaceProvidersManager.getSpaceProviders(key);
        return m_workflowMiddleware.getCommands().execute( //
                new WorkflowKey(projectId, workflowId), //
                workflowCommandEnt, //
                m_workflowMiddleware, //
                m_nodeFactoryProvider, //
                spaceProviders, //
                m_linkVariants
        );
    }

    @Override
    public void undoWorkflowCommand(final String projectId, final NodeIDEnt workflowId) throws ServiceCallException {
        DefaultServiceContext.assertWorkflowProjectId(projectId);
        DefaultServiceUtil.assertProjectVersion(projectId, VersionId.currentState());
        m_workflowMiddleware.getCommands().undo(new WorkflowKey(projectId, workflowId));
    }

    @Override
    public void redoWorkflowCommand(final String projectId, final NodeIDEnt workflowId) throws ServiceCallException {
        DefaultServiceContext.assertWorkflowProjectId(projectId);
        DefaultServiceUtil.assertProjectVersion(projectId, VersionId.currentState());
        m_workflowMiddleware.getCommands().redo(new WorkflowKey(projectId, workflowId));
    }

    @Override
    public WorkflowMonitorStateSnapshotEnt getWorkflowMonitorState(final String projectId) {
        DefaultServiceContext.assertWorkflowProjectId(projectId);
        return m_workflowMiddleware
            .buildWorkflowMonitorStateSnapshotEnt(new WorkflowKey(projectId, NodeIDEnt.getRootID()));
    }

    private void disposeVersion(final String projectId, final VersionId versionId) {
        m_projectManager.getProject(projectId).ifPresent(project -> project.disposeCachedWfm(versionId));
        m_workflowMiddleware.clearWorkflowState(
            wfKey -> wfKey.getProjectId().equals(projectId) && wfKey.getVersionId().equals(versionId));
    }
}
