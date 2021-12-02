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
import java.util.NoSuchElementException;

import org.apache.commons.collections4.map.LRUMap;
import org.apache.commons.lang3.StringUtils;
import org.knime.core.node.Node;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.workflow.LoopEndNode;
import org.knime.core.node.workflow.NativeNodeContainer;
import org.knime.core.node.workflow.NativeNodeContainer.LoopStatus;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.webui.data.rpc.RpcServerManager;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.NativeNodeDescriptionEnt;
import org.knime.gateway.api.webui.entity.NodeFactoryKeyEnt;
import org.knime.gateway.api.webui.service.NodeService;
import org.knime.gateway.api.webui.service.util.ServiceExceptions;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.InvalidRequestException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.NodeNotFoundException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.OperationNotAllowedException;
import org.knime.gateway.api.webui.util.EntityBuilderUtil;
import org.knime.gateway.impl.service.util.DefaultServiceUtil;

/**
 * The default implementation of the {@link NodeService}-interface.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public final class DefaultNodeService implements NodeService {
    private static final DefaultNodeService INSTANCE = new DefaultNodeService();

    static final LRUMap<NodeFactoryKeyEnt, NativeNodeDescriptionEnt> m_nodeDescriptionCache = new LRUMap<>(100);

    /**
     * Returns the singleton instance for this service.
     *
     * @return the singleton instance
     */
    public static DefaultNodeService getInstance() {
        return INSTANCE;
    }

    private DefaultNodeService() {
        // singleton
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void changeNodeStates(final String projectId, final NodeIDEnt workflowId, final List<NodeIDEnt> nodeIds,
        final String action) throws NodeNotFoundException, OperationNotAllowedException {
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
        try {
            NodeContainer nc = DefaultServiceUtil.getNodeContainer(projectId, workflowId, nodeId);
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
    public String doPortRpc(final String projectId, final NodeIDEnt workflowId, final NodeIDEnt nodeId, final Integer portIdx,
        final String body) throws NodeNotFoundException, InvalidRequestException {
        NodeContainer nc;
        try {
            nc = DefaultServiceUtil.getNodeContainer(projectId, workflowId, nodeId);
        } catch (IllegalArgumentException e) {
            throw new NodeNotFoundException(e.getMessage(), e);
        }

        try {
            return RpcServerManager.getInstance().doRpc(nc.getOutPort(portIdx), body);
        } catch (IOException | IllegalStateException ex) {
            throw new InvalidRequestException(ex.getMessage(), ex);
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
                fac = DefaultServiceUtil.getNodeFactory(
                        factoryKey.getClassName(),
                        factoryKey.getSettings()
                );
            } catch (NoSuchElementException | IOException e) { // NOSONAR: exceptions are handled
                NodeLogger.getLogger(this.getClass()).error(e.getStackTrace());
                throw new NodeNotFoundException("Could not read node description", e);
            }

            final Node coreNode = new Node(fac);  // needed to init information on ports
            NativeNodeDescriptionEnt description = EntityBuilderUtil.buildNativeNodeDescriptionEnt(coreNode);
            m_nodeDescriptionCache.put(factoryKey, description);
            return description;
        }
        return m_nodeDescriptionCache.get(factoryKey);
    }

}
