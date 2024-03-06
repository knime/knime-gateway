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
package org.knime.gateway.impl.webui.service;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

import org.apache.commons.collections4.map.LRUMap;
import org.apache.commons.lang3.StringUtils;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.workflow.LoopEndNode;
import org.knime.core.node.workflow.NativeNodeContainer;
import org.knime.core.node.workflow.NativeNodeContainer.LoopStatus;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.SingleNodeContainer;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.webui.node.DataServiceManager;
import org.knime.core.webui.node.NodeWrapper;
import org.knime.core.webui.node.dialog.NodeDialogManager;
import org.knime.core.webui.node.view.NodeViewManager;
import org.knime.gateway.api.entity.NodeDialogEnt;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.entity.NodeViewEnt;
import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.api.webui.entity.ComponentNodeDescriptionEnt;
import org.knime.gateway.api.webui.entity.NativeNodeDescriptionEnt;
import org.knime.gateway.api.webui.entity.NodeFactoryKeyEnt;
import org.knime.gateway.api.webui.service.NodeService;
import org.knime.gateway.api.webui.service.util.ServiceExceptions;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.InvalidRequestException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.NodeNotFoundException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.OperationNotAllowedException;
import org.knime.gateway.api.webui.util.EntityFactory;
import org.knime.gateway.impl.service.util.DefaultServiceUtil;
import org.knime.gateway.impl.webui.entity.UIExtensionEntityFactory;
import org.knime.gateway.impl.webui.service.events.EventConsumer;
import org.knime.gateway.impl.webui.service.events.SelectionEventSource;
import org.knime.gateway.impl.webui.service.events.SelectionEventSource.SelectionEventMode;

/**
 * The default implementation of the {@link NodeService}-interface.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @author Kai Franze, KNIME GmbH, Germany
 */
public final class DefaultNodeService implements NodeService {

    private final EventConsumer m_eventConsumer = ServiceDependencies.getServiceDependency(EventConsumer.class, false);

    static final LRUMap<NodeFactoryKeyEnt, NativeNodeDescriptionEnt> m_nodeDescriptionCache = new LRUMap<>(100);

    /**
     * Returns the singleton instance for this service.
     *
     * @return the singleton instance
     */
    public static DefaultNodeService getInstance() {
        return ServiceInstances.getDefaultServiceInstance(DefaultNodeService.class);
    }

    DefaultNodeService() {
        // singleton
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void changeNodeStates(final String projectId, final NodeIDEnt workflowId, final List<NodeIDEnt> nodeIds,
        final String action) throws NodeNotFoundException, OperationNotAllowedException {
        DefaultServiceContext.assertWorkflowProjectId(projectId);
        try {
            DefaultServiceUtil.changeNodeStates(projectId, workflowId, action,
                nodeIds.toArray(new NodeIDEnt[nodeIds.size()]));
        } catch (IllegalArgumentException e) {
            throw new NodeNotFoundException(e.getMessage(), e);
        } catch (IllegalStateException e) {
            throw new OperationNotAllowedException(e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void changeLoopState(final String projectId, final NodeIDEnt workflowId, final NodeIDEnt nodeId,
        final String action) throws NodeNotFoundException, OperationNotAllowedException {
        DefaultServiceContext.assertWorkflowProjectId(projectId);
        try {
            var nc = DefaultServiceUtil.getNodeContainer(projectId, workflowId, nodeId);
            if (nc instanceof NativeNodeContainer) {
                NativeNodeContainer nnc = (NativeNodeContainer)nc;
                if (nnc.isModelCompatibleTo(LoopEndNode.class)) {
                    changeLoopState(action, nnc);
                    return;
                }
            }
            throw new OperationNotAllowedException("The action to change the loop state is not applicable for "
                + nc.getNameWithID() + ". Not a loop end node.");
        } catch (IllegalArgumentException e) {
            throw new NodeNotFoundException(e.getMessage(), e);
        }
    }

    private static void changeLoopState(final String action, final NativeNodeContainer nnc)
        throws OperationNotAllowedException {
        WorkflowManager wfm = nnc.getParent();
        if (StringUtils.isBlank(action)) {
            // if there is no action (null or empty)
        } else if (action.equals("pause")) {
            wfm.pauseLoopExecution(nnc);
        } else if (action.equals("resume")) {
            if (nnc.getLoopStatus() == LoopStatus.PAUSED) {
                wfm.resumeLoopExecution(nnc, false);
            }
        } else if (action.equals("step")) {
            if (nnc.getLoopStatus() == LoopStatus.PAUSED) {
                wfm.resumeLoopExecution(nnc, true);
            } else if (wfm.canExecuteNodeDirectly(nnc.getID())) {
                wfm.executeUpToHere(nnc.getID());
                assert nnc.getLoopStatus() == LoopStatus.RUNNING;
                wfm.pauseLoopExecution(nnc);
            } else {
                //
            }
        } else {
            throw new OperationNotAllowedException("Unknown action '" + action + "'");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getNodeDialog(final String projectId, final NodeIDEnt workflowId, final NodeIDEnt nodeId)
        throws NodeNotFoundException, InvalidRequestException {
        DefaultServiceContext.assertWorkflowProjectId(projectId);
        var snc = getNC(projectId, workflowId, nodeId, SingleNodeContainer.class);
        if (!NodeDialogManager.hasNodeDialog(snc)) {
            throw new InvalidRequestException("The node " + snc.getNameWithID() + " doesn't have a dialog");
        }
        return new NodeDialogEnt(snc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getNodeView(final String projectId, final NodeIDEnt workflowId, final NodeIDEnt nodeId)
        throws NodeNotFoundException, InvalidRequestException {
        DefaultServiceContext.assertWorkflowProjectId(projectId);
        var nnc = getNC(projectId, workflowId, nodeId, NativeNodeContainer.class);
        if (!NodeViewManager.hasNodeView(nnc)) {
            throw new InvalidRequestException("The node " + nnc.getNameWithID() + " doesn't have a view");
        }
        if (!nnc.getNodeContainerState().isExecuted()) {
            throw new InvalidRequestException(
                "Node view can't be requested. The node " + nnc.getNameWithID() + " is not executed.");
        }
        if (m_eventConsumer == null) {
            return NodeViewEnt.create(nnc);
        } else {
            return UIExtensionEntityFactory.createNodeViewEntAndEventSources(nnc, m_eventConsumer, false).getFirst();
        }
    }

    private static <T> T getNC(final String projectId, final NodeIDEnt workflowId,
        final NodeIDEnt nodeId, final Class<T> ncClass) throws NodeNotFoundException, InvalidRequestException {
        NodeContainer nc;
        try {
            nc = DefaultServiceUtil.getNodeContainer(projectId, workflowId, nodeId);
        } catch (IllegalArgumentException e) {
            throw new NodeNotFoundException(e.getMessage(), e);
        }

        if (!ncClass.isAssignableFrom(nc.getClass())) {
            throw new InvalidRequestException(
                "The requested node " + nc.getNameWithID() + " is not a " + ncClass.getName());
        }

        return ncClass.cast(nc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String callNodeDataService(final String projectId, final NodeIDEnt workflowId, final NodeIDEnt nodeId,
        final String extensionType, final String serviceType, final String request)
        throws NodeNotFoundException, InvalidRequestException {
        DefaultServiceContext.assertWorkflowProjectId(projectId);

        var nnc = getNC(projectId, workflowId, nodeId, NativeNodeContainer.class);

        final var dataServiceManager = getDataServiceManager(extensionType);
        var nncWrapper = NodeWrapper.of(nnc);
        if ("initial_data".equals(serviceType)) {
            return dataServiceManager.callInitialDataService(nncWrapper);
        } else if ("data".equals(serviceType)) {
            return dataServiceManager.callRpcDataService(nncWrapper, request);
        } else if ("apply_data".equals(serviceType)) {
            try {
                dataServiceManager.callApplyDataService(nncWrapper, request);
            } catch (IOException e) {
                NodeLogger.getLogger(getClass()).error(e);
                return e.getMessage();
            }
            return "";
        } else {
            throw new InvalidRequestException("Unknown service type '" + serviceType + "'");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deactivateNodeDataServices(final String projectId, final NodeIDEnt workflowId, final NodeIDEnt nodeId,
        final String extensionType) throws NodeNotFoundException, InvalidRequestException {
        DefaultServiceContext.assertWorkflowProjectId(projectId);
        var nc = getNC(projectId, workflowId, nodeId, NodeContainer.class);
        var dataServiceManager = getDataServiceManager(extensionType);
        dataServiceManager.deactivateDataServices(NodeWrapper.of(nc));
    }

    private static DataServiceManager<NodeWrapper> getDataServiceManager(final String extensionType)
        throws InvalidRequestException {
        final DataServiceManager<NodeWrapper> dataServiceManager;
        if ("view".equals(extensionType)) {
            dataServiceManager = NodeViewManager.getInstance().getDataServiceManager();
        } else if ("dialog".equals(extensionType)) {
            dataServiceManager = NodeDialogManager.getInstance().getDataServiceManager();
        } else {
            throw new InvalidRequestException("Unknown target for node data service: " + extensionType);
        }
        return dataServiceManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateDataPointSelection(final String projectId, final NodeIDEnt workflowId,
        final NodeIDEnt nodeId, final String mode, final List<String> selection) {
        DefaultServiceContext.assertWorkflowProjectId(projectId);

        final var selectionEventMode = SelectionEventMode.valueOf(mode.toUpperCase(Locale.ROOT));
        var nc = DefaultServiceUtil.getNodeContainer(projectId, workflowId, nodeId);
        try {
            var nodeWrapper = NodeWrapper.of(nc);
            var tableViewManager = NodeViewManager.getInstance().getTableViewManager();
            var rowKeys = tableViewManager.callSelectionTranslationService(nodeWrapper, selection);
            var hiLiteHandler = tableViewManager.getHiLiteHandler(nodeWrapper).orElseThrow();
            SelectionEventSource.processSelectionEvent(hiLiteHandler, nc.getID(), selectionEventMode, true, rowKeys);
        } catch (IOException ex) {
            throw new IllegalStateException("Problem translating selection to row keys", ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NativeNodeDescriptionEnt getNodeDescription(final NodeFactoryKeyEnt factoryKey) throws NodeNotFoundException,
            ServiceExceptions.NodeDescriptionNotAvailableException {
        if (!m_nodeDescriptionCache.containsKey(factoryKey)) {
            NodeFactory<NodeModel> fac;
            try {
                fac = CoreUtil.getNodeFactory(factoryKey.getClassName(), factoryKey.getSettings());
            } catch (NoSuchElementException | IOException e) { // NOSONAR: exceptions are handled
                var message = "Could not read node description";
                NodeLogger.getLogger(this.getClass()).error(message + ": " + e.getMessage());
                throw new NodeNotFoundException(message, e);
            }

            final var coreNode = CoreUtil.createNode(fac) // needed to init information on ports
                .orElseThrow(() -> new ServiceExceptions.NodeDescriptionNotAvailableException(
                    "Could not create instance of node"));
            var description = EntityFactory.NodeTemplateAndDescription.buildNativeNodeDescriptionEnt(coreNode);
            m_nodeDescriptionCache.put(factoryKey, description);
            return description;
        }
        return m_nodeDescriptionCache.get(factoryKey);
    }

    @Override
    public void dispose() {
        m_nodeDescriptionCache.clear();
    }

    @Override
    public ComponentNodeDescriptionEnt getComponentDescription(final String projectId, final NodeIDEnt workflowId,
        final NodeIDEnt nodeId) throws NodeNotFoundException, InvalidRequestException {
        final var snc = getNC(projectId, workflowId, nodeId, SubNodeContainer.class);
        return EntityFactory.Workflow.buildComponentNodeDescriptionEnt(snc);
    }

}
