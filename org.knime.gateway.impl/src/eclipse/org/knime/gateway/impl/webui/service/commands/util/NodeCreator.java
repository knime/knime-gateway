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
 *   Apr 12, 2023 (hornm): created
 */
package org.knime.gateway.impl.webui.service.commands.util;

import java.io.IOException;
import java.net.URL;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

import org.knime.core.node.ConfigurableNodeFactory;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.NodeTimer;
import org.knime.core.node.workflow.NodeTimer.GlobalNodeStats.NodeCreationType;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.api.webui.entity.NodeFactoryKeyEnt;
import org.knime.gateway.api.webui.entity.XYEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.OperationNotAllowedException;

/**
 * Helper to create a node and optionally connect it to upstream and/or downstream nodes.
 *
 * Mandatory parameters to create the node are passed via the constructor, optional ones via individual methods. The
 * node is finally created with the {@link #create()}-method.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public final class NodeCreator {

    private final WorkflowManager m_wfm;

    private final NodeFactoryKeyEnt m_factoryKeyEnt;

    private final XYEnt m_position;

    private URL m_url;

    private boolean m_track;

    private boolean m_isNodeAddedViaQuickNodeInsertion;

    private int m_nodeYPosCorrection = 0;

    private boolean m_centerNode;

    private boolean m_failOnConnectionAttempt;

    private NodeConnector m_nodeConnector;

    /**
     * @param wfm the workflow to add the node to
     * @param factoryKeyEnt determines the type of node to be created
     * @param position
     */
    public NodeCreator(final WorkflowManager wfm, final NodeFactoryKeyEnt factoryKeyEnt, final XYEnt position) {
        m_wfm = wfm;
        m_factoryKeyEnt = factoryKeyEnt;
        m_position = position;
    }

    /**
     * @param url a url to initialized the node with (if its a {@link ConfigurableNodeFactory}
     * @return this creator
     */
    public NodeCreator withUrl(final URL url) {
        m_url = url;
        return this;
    }

    /**
     * @param nodeYPosCorrection a y-position correction added to the actual y-position
     * @return this creator
     */
    public NodeCreator withNodeYPosCorrection(final int nodeYPosCorrection) {
        m_nodeYPosCorrection = nodeYPosCorrection;
        return this;
    }

    /**
     * The node creation will be tracked via the {@link NodeTimer}.
     *
     * @return this creator
     */
    public NodeCreator trackCreation() {
        m_track = true;
        return this;
    }

    /**
     * Whether the node has been added via the quick node insertion feature. Only relevant, if the creation is being
     * tracked ({@link #trackCreation()}).
     *
     * @param isNodeAddedViaQuickNodeInsertion
     * @return this creator
     */
    public NodeCreator isNodeAddedViaQuickNodeInsertion(final boolean isNodeAddedViaQuickNodeInsertion) {
        m_isNodeAddedViaQuickNodeInsertion = isNodeAddedViaQuickNodeInsertion;
        return this;
    }

    /**
     * If the node is to be centered at the given coordinates. By default the position refers to the upper left corner
     * of the node 'body'.
     *
     * @return this creator
     */
    public NodeCreator centerNode() {
        m_centerNode = true;
        return this;
    }

    /**
     * Allows one to additionally connect the node.
     *
     * @param nodeConnectorConsumer provides a {@link NodeConnector} to define to what nodes to connect
     * @return this creator
     */
    public NodeCreator connect(final Consumer<NodeConnector> nodeConnectorConsumer) {
        m_nodeConnector = new NodeConnector(m_wfm);
        nodeConnectorConsumer.accept(m_nodeConnector);
        return this;
    }

    /**
     * When enabled, the {@link #create()}-method will fail with a {@link OperationNotAllowedException} if a connecction
     * couldn't be created. The node won't be created.
     *
     * @return this creator
     */
    public NodeCreator failOnConnectionAttempt() {
        m_failOnConnectionAttempt = true;
        return this;
    }

    /**
     * Carries out the actual node creation operation.
     *
     * @return the id of the new node
     * @throws OperationNotAllowedException if the node couldn't be created (e.g. because no node could be found for the
     *             given factory name) or a connection couldn't be created (in case {@link #failOnConnectionAttempt()}
     *             is enabled)
     */
    public NodeID create() throws OperationNotAllowedException {
        NodeID nodeId;
        try {
            nodeId = CoreUtil.createAndAddNode(m_factoryKeyEnt.getClassName(), m_factoryKeyEnt.getSettings(), m_url,
                m_position.getX(), m_position.getY() + m_nodeYPosCorrection, m_wfm, m_centerNode);
        } catch (IOException | NoSuchElementException e) {
            throw new OperationNotAllowedException(e.getMessage(), e);
        }

        if (m_track) {
            var nc = m_wfm.getNodeContainer(nodeId);
            trackNodeCreation(nc, m_isNodeAddedViaQuickNodeInsertion);
        }

        if (m_nodeConnector != null) {
            if (!m_nodeConnector.connect(nodeId) && m_failOnConnectionAttempt) {
                m_wfm.removeNode(nodeId);
                throw new OperationNotAllowedException(
                    "Node couldn't be created because a connection couldn't be added.");
            } else {
                // TODO log warning?
                // LOGGER.warn("Could not find a suitable destination port for incomming connection.");
                // LOGGER.warn("Could not find a suitable destination port for outgoing connection.");
            }
        }

        return nodeId;
    }

    /**
     * @param nc the node that has been added
     * @param isNodeAddedViaQuickNodeInsertion
     */
    @SuppressWarnings("java:S2301")
    public static void trackNodeCreation(final NodeContainer nc, final boolean isNodeAddedViaQuickNodeInsertion) {
        NodeTimer.GLOBAL_TIMER.addNodeCreation(nc);
        NodeTimer.GLOBAL_TIMER.incNodeCreatedVia(NodeCreationType.WEB_UI);
        if (isNodeAddedViaQuickNodeInsertion) {
            NodeTimer.GLOBAL_TIMER.incNodeCreatedVia(NodeCreationType.WEB_UI_QUICK_INSERTION_RECOMMENDED);
        }
    }

}
