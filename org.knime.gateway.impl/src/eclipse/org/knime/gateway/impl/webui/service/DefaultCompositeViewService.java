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
 *   jun 12, 2025 (kampmann): created
 */
package org.knime.gateway.impl.webui.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.knime.core.node.NodeLogger;
import org.knime.core.node.workflow.NativeNodeContainer;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.entity.NodeViewEnt;
import org.knime.gateway.api.util.ExtPointUtil;
import org.knime.gateway.api.webui.service.CompositeViewService;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.InvalidRequestException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.ServiceCallException;
import org.knime.gateway.impl.webui.service.events.SelectionEventBus;

/**
 * Service implementation for the composite view service. Practically a wrapper that delegates all calls to a
 * {@link CompositeViewService} implementation created in js-core
 *
 * @since 5.5
 */
public class DefaultCompositeViewService implements CompositeViewService {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(DefaultCompositeViewService.class);

    private final SelectionEventBus m_selectionEventBus =
        ServiceDependencies.getServiceDependency(SelectionEventBus.class, false);

    private CompositeViewService m_compositeViewServiceDelegate;

    /**
     * Returns the singleton instance for this service.
     *
     * @return the singleton instance
     */
    public static CompositeViewService getInstance() {
        return ServiceInstances.getDefaultServiceInstance(DefaultCompositeViewService.class);
    }

    DefaultCompositeViewService() {
        // singleton
    }

    @Override
    public Object getCompositeViewPage(final String projectId, final NodeIDEnt workflowId, final String versionId,
        final NodeIDEnt nodeId) throws ServiceCallException {

        assertProjectId(projectId);
        return getCompositeViewServiceDelegate().getCompositeViewPage(projectId, workflowId, versionId, nodeId);
    }

    @Override
    public Object triggerComponentReexecution(final String projectId, final NodeIDEnt workflowId,
        final NodeIDEnt nodeId, final String resetNodeIdSuffix, final Map<String, String> viewValues)
        throws ServiceCallException {

        assertProjectId(projectId);
        return getCompositeViewServiceDelegate().triggerComponentReexecution(projectId, workflowId, nodeId,
            resetNodeIdSuffix, viewValues);
    }

    @Override
    public Object triggerCompleteComponentReexecution(final String projectId, final NodeIDEnt workflowId,
        final NodeIDEnt nodeId, final Map<String, String> viewValues) throws ServiceCallException {

        assertProjectId(projectId);
        return getCompositeViewServiceDelegate().triggerCompleteComponentReexecution(projectId, workflowId, nodeId,
            viewValues);
    }

    @Override
    public Object pollComponentReexecutionStatus(final String projectId, final NodeIDEnt workflowId,
        final NodeIDEnt nodeId, final String nodeIdThatTriggered) throws ServiceCallException {

        assertProjectId(projectId);
        return getCompositeViewServiceDelegate().pollComponentReexecutionStatus(projectId, workflowId, nodeId,
            nodeIdThatTriggered);
    }

    @Override
    public Object pollCompleteComponentReexecutionStatus(final String projectId, final NodeIDEnt workflowId,
        final NodeIDEnt nodeId) throws ServiceCallException {

        assertProjectId(projectId);
        return getCompositeViewServiceDelegate().pollCompleteComponentReexecutionStatus(projectId, workflowId, nodeId);
    }

    @Override
    public void setViewValuesAsNewDefault(final String projectId, final NodeIDEnt workflowId, final NodeIDEnt nodeId,
        final Map<String, String> viewValues) throws ServiceCallException {

        assertProjectId(projectId);
        getCompositeViewServiceDelegate().setViewValuesAsNewDefault(projectId, workflowId, nodeId, viewValues);
    }

    @Override
    public void deactivateAllCompositeViewDataServices(final String projectId, final NodeIDEnt workflowId,
        final NodeIDEnt nodeId) throws ServiceCallException {

        assertProjectId(projectId);
        getCompositeViewServiceDelegate().deactivateAllCompositeViewDataServices(projectId, workflowId, nodeId);
    }

    /*
     * Convenience function to map exception when the projectId is not valid.
     */
    private static void assertProjectId(final String projectId) throws ServiceCallException {
        try {
            DefaultServiceContext.assertWorkflowProjectId(projectId);
        } catch (IllegalStateException ex) {
            throw ServiceCallException.builder() //
                .withTitle("Invalid project ID") //
                .withDetails(ex.getMessage()) //
                .canCopy(false) //
                .withCause(ex) //
                .build();
        }
    }

    private CompositeViewService getCompositeViewServiceDelegate() {
        if (m_compositeViewServiceDelegate == null) {
            List<GatewayServiceFactory> componentServiceFactories =
                ExtPointUtil.collectExecutableExtensions("org.knime.gateway.impl.GatewayServiceFactory", "impl");
            if (componentServiceFactories.size() != 1) {
                throw new IllegalStateException(
                    "Expected only a single gateway service factory. Got " + componentServiceFactories.size() + ".");
            }
            m_compositeViewServiceDelegate =
                componentServiceFactories.get(0).createCompositeViewService(this::getNodeViewCreator);
        }
        return m_compositeViewServiceDelegate;
    }

    private Function<NativeNodeContainer, NodeViewEnt> getNodeViewCreator(final String projectId) {
        return nnc -> {
            try {
                var nodeView = DefaultNodeService.getNodeView(nnc, projectId, m_selectionEventBus);
                if (!(nodeView instanceof NodeViewEnt)) {
                    throw InvalidRequestException.builder() //
                        .withTitle("Invalid node view type") //
                        .withDetails("NodeView is not of type " + NodeViewEnt.class.getSimpleName() + ", but "
                            + nodeView.getClass().getName() + ".") //
                        .canCopy(false) //
                        .build();
                }
                return nodeView;
            } catch (InvalidRequestException ex) {
                LOGGER.error("Could not create a node view for " + nnc.getNameWithID() + ": " + ex.getMessage());
                return NodeViewEnt.create(nnc);
            }
        };
    }
}
