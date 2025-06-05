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
 *   Feb 4, 2025 (hornm): created
 */
package org.knime.gateway.impl.webui.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.knime.core.node.NodeLogger;
import org.knime.core.node.workflow.NativeNodeContainer;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.entity.NodeViewEnt;
import org.knime.gateway.api.util.ExtPointUtil;
import org.knime.gateway.api.util.VersionId;
import org.knime.gateway.api.webui.entity.ComponentNodeDescriptionEnt;
import org.knime.gateway.api.webui.service.ComponentService;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.InvalidRequestException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.NodeNotFoundException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.ServiceCallException;
import org.knime.gateway.api.webui.util.EntityFactory;
import org.knime.gateway.impl.webui.WorkflowKey;
import org.knime.gateway.impl.webui.WorkflowMiddleware;
import org.knime.gateway.impl.webui.service.events.SelectionEventBus;

/**
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @author Tobias Kampmann, TNG Technology Consulting GmbH
 * @since 5.5
 */
public class DefaultComponentService implements ComponentService {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(DefaultComponentService.class);

    private final SelectionEventBus m_selectionEventBus =
        ServiceDependencies.getServiceDependency(SelectionEventBus.class, false);

    private final WorkflowMiddleware m_workflowMiddleware =
        ServiceDependencies.getServiceDependency(WorkflowMiddleware.class, true);

    private static CompositeViewDataProvider m_cachedCompositeViewDataProvider;

    /**
     * Returns the singleton instance for this service.
     *
     * @return the singleton instance
     */
    public static DefaultComponentService getInstance() {
        return ServiceInstances.getDefaultServiceInstance(DefaultComponentService.class);
    }

    DefaultComponentService() {
        // singleton
    }

    @Override
    public Object getCompositeViewPage(final String projectId, final NodeIDEnt workflowId, final String versionId,
        final NodeIDEnt nodeId) throws ServiceCallException {
        var version = VersionId.parse(versionId);
        try {
            var snc = getSubnodeContainer(projectId, workflowId, version, nodeId);
            return getViewDataProvider().getCompositeViewData(snc, getNodeViewCreator(projectId));
        } catch (IOException | NodeNotFoundException ex) {
            throw new ServiceCallException("Could not create component view data. " + ex.getMessage(), ex);
        }
    }

    @Override
    public Object triggerComponentReexecution(final String projectId, final NodeIDEnt workflowId,
        final NodeIDEnt nodeId, final String resetNodeIdSuffix, final Map<String, String> viewValues)
        throws ServiceCallException {
        try {
            var snc = getSubnodeContainer(projectId, workflowId, nodeId);
            return getViewDataProvider().triggerComponentReexecution(snc, resetNodeIdSuffix, viewValues,
                getNodeViewCreator(projectId));
        } catch (IOException | NodeNotFoundException ex) {
            throw new ServiceCallException("Could not reexecute component. " + ex.getMessage(), ex);
        }
    }

    @Override
    public Object triggerCompleteComponentReexecution(final String projectId, final NodeIDEnt workflowId,
        final NodeIDEnt nodeId, final Map<String, String> viewValues) throws ServiceCallException {
        LOGGER.info("reexecuteCompleteCompositeViewPage: " + projectId + " " + workflowId + " " + nodeId);

        try {
            var snc = getSubnodeContainer(projectId, workflowId, nodeId);
            return getViewDataProvider().triggerCompleteComponentReexecution(snc, viewValues,
                getNodeViewCreator(projectId));
        } catch (IOException | NodeNotFoundException ex) {
            throw new ServiceCallException("Could not reexecute complete component. " + ex.getMessage(), ex);
        }

    }

    @Override
    public ComponentNodeDescriptionEnt getComponentDescription(final String projectId, final NodeIDEnt workflowId,
        final String versionId, final NodeIDEnt nodeId) throws ServiceCallException {
        var version = VersionId.parse(versionId);
        try {
            SubNodeContainer snc = getSubnodeContainer(projectId, workflowId, version, nodeId);
            return EntityFactory.Workflow.buildComponentNodeDescriptionEnt(snc);
        } catch (NodeNotFoundException ex) {
            throw new ServiceCallException("Could not get component description. " + ex.getMessage(), ex);
        }
    }

    @Override
    public Object pollComponentReexecutionStatus(final String projectId, final NodeIDEnt workflowId,
        final NodeIDEnt nodeId, final String nodeIdThatTriggered) throws ServiceCallException {
        try {
            SubNodeContainer snc = getSubnodeContainer(projectId, workflowId, nodeId);
            return getViewDataProvider().pollComponentReexecutionStatus(snc, nodeIdThatTriggered,
                getNodeViewCreator(projectId));
        } catch (NodeNotFoundException | IOException ex) {
            throw new ServiceCallException("Could not get reexecuting page. " + ex.getMessage(), ex);
        }
    }

    @Override
    public Object pollCompleteComponentReexecutionStatus(final String projectId, final NodeIDEnt workflowId,
        final NodeIDEnt nodeId) throws ServiceCallException {

        LOGGER.info("getCompleteReexecutingPage: " + projectId + " " + workflowId + " " + nodeId);

        try {
            SubNodeContainer snc = getSubnodeContainer(projectId, workflowId, nodeId);
            return getViewDataProvider().pollCompleteComponentReexecutionStatus(snc, getNodeViewCreator(projectId));
        } catch (NodeNotFoundException | IOException ex) {
            throw new ServiceCallException("Could not get reexecuting page. " + ex.getMessage(), ex);
        }
    }

    @Override
    public void setViewValuesAsNewDefault(final String projectId, final NodeIDEnt workflowId, final NodeIDEnt nodeId,
        final Map<String, String> viewValues) throws ServiceCallException {
        try {
            SubNodeContainer snc = getSubnodeContainer(projectId, workflowId, nodeId);
            getViewDataProvider().setViewValuesAsNewDefault(snc, viewValues);
        } catch (IOException | NodeNotFoundException ex) {
            throw new ServiceCallException("Could not set view values as new default. " + ex.getMessage(), ex);
        }
    }

    @Override
    public void deactivateAllComponentDataServices(final String projectId, final NodeIDEnt workflowId,
        final NodeIDEnt nodeId) throws ServiceCallException {
        try {
            SubNodeContainer snc = getSubnodeContainer(projectId, workflowId, nodeId);
            getViewDataProvider().deactivateAllComponentDataServices(snc);
        } catch (IOException | NodeNotFoundException ex) {
            throw new ServiceCallException("Could not deactivate all component data services. " + ex.getMessage(), ex);
        }
    }

    private static CompositeViewDataProvider getViewDataProvider() {
        if (m_cachedCompositeViewDataProvider == null) {
            List<CompositeViewDataProvider> dataProviders =
                ExtPointUtil.collectExecutableExtensions("org.knime.gateway.impl.CompositeViewDataProvider", "impl");
            if (dataProviders.size() != 1) {
                throw new IllegalStateException(
                    "Expected only a single data provider for component views. Got " + dataProviders.size() + ".");
            }
            m_cachedCompositeViewDataProvider = dataProviders.get(0);
        }
        return m_cachedCompositeViewDataProvider;
    }

    private static SubNodeContainer getSubnodeContainer(final String projectId, final NodeIDEnt workflowId,
        final NodeIDEnt nodeId) throws ServiceCallException, NodeNotFoundException {
        return getSubnodeContainer(projectId, workflowId, VersionId.currentState(), nodeId);
    }

    private static SubNodeContainer getSubnodeContainer(final String projectId, final NodeIDEnt workflowId,
        final VersionId versionId, final NodeIDEnt nodeId) throws ServiceCallException, NodeNotFoundException {
        var nc = ServiceUtilities.assertProjectIdAndGetNodeContainer(projectId, workflowId, versionId, nodeId);
        if (nc instanceof SubNodeContainer snc) {
            return snc;
        }
        throw new ServiceCallException("No Component for " + projectId + ", " + workflowId + ", " + nodeId + " found.");
    }

    private Function<NativeNodeContainer, NodeViewEnt> getNodeViewCreator(final String projectId) {
        return nnc -> {
            try {
                var nodeView = DefaultNodeService.getNodeView(nnc, projectId, m_selectionEventBus);
                if (!(nodeView instanceof NodeViewEnt)) {
                    throw new InvalidRequestException("NodeView is not of type " + NodeViewEnt.class.getSimpleName()
                        + ", but " + nodeView.getClass().getName() + ".");
                }
                return (NodeViewEnt)nodeView;
            } catch (InvalidRequestException ex) {
                LOGGER.error("Could not create a node view for " + nnc.getNameWithID() + ": " + ex.getMessage());
                // Note: This function will be used in a context where no exception is expected.
                // This handling here might be superfluous.
                return NodeViewEnt.create(nnc);
            }
        };
    }

    @Override
    public void cancelOrRetryComponentLoadJob(final String projectId, final NodeIDEnt workflowId,
        final String placeholderId, final String action) throws ServiceCallException {
        DefaultServiceContext.assertWorkflowProjectId(projectId);
        var componentLoader = m_workflowMiddleware.getComponentLoadJobManager(new WorkflowKey(projectId, workflowId));
        if ("cancel".equals(action)) {
            componentLoader.cancelLoadJob(placeholderId);
        } else if ("retry".equals(action)) {
            componentLoader.rerunLoadJob(placeholderId);
        } else {
            throw new ServiceCallException("Unknown action: " + action);
        }
    }
}
