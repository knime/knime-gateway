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
 *   Mar 27, 2023 (Juan Baquero, KNIME GmbH): created
 */
package org.knime.gateway.impl.webui.service.commands;

import java.util.Set;
import java.util.function.Function;

import org.knime.core.node.workflow.ConnectionContainer;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.WorkflowCopyContent;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.node.workflow.WorkflowPersistor;
import org.knime.gateway.api.entity.ConnectionIDEnt;
import org.knime.gateway.api.webui.entity.InsertNodeCommandEnt;
import org.knime.gateway.api.webui.entity.InsertionOptionsEnt;
import org.knime.gateway.api.webui.entity.NodeFactoryKeyEnt;
import org.knime.gateway.api.webui.entity.XYEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.ServiceCallException;
import org.knime.gateway.impl.service.util.DefaultServiceUtil;
import org.knime.gateway.impl.webui.service.commands.util.Geometry;
import org.knime.gateway.impl.webui.service.commands.util.NodeConnector;
import org.knime.gateway.impl.webui.service.commands.util.NodeCreator;

/**
 * Workflow command to insert a node on top of an active connection
 *
 * @author Juan Baquero, KNIME GmbH
 */
final class InsertNode extends AbstractWorkflowCommand {

    private NodeID m_insertedNode;

    private WorkflowPersistor m_copyOfExistingInsertedNode;

    private Parameters m_parameters;

    private Port m_source;

    private Port m_destination;

    private ConnectionContainer m_originalConnection;

    public InsertNode(final NodeID nodeToInsert, final InsertionOptionsEnt insertionOptions, final XYEnt position) {
        m_parameters = new Parameters( //
            insertionOptions.getConnectionId(), //
            Geometry.Point.of(position), //
            wfm -> nodeToInsert, //
            null //
        );
    }

    private record Parameters( //
        ConnectionIDEnt connection, //
        Geometry.Point position, //
        Function<WorkflowManager, NodeID> nodeToInsertGetter, //
        NodeFactoryKeyEnt nodeToCreateAndInsert) {
    }

    private record Port(NodeID node, int port) {

    }

    InsertNode(final InsertNodeCommandEnt commandEnt) {
        m_parameters = new Parameters( //
            commandEnt.getConnectionId(), //
            Geometry.Point.of(commandEnt.getPosition()), //
            wfm -> commandEnt.getNodeId().toNodeID(wfm), //
            commandEnt.getNodeFactory() //
        );
    }

    @Override
    protected boolean executeWithWorkflowLockAndContext() throws ServiceCallException {
        var wfm = getWorkflowManager();
        var nodeToInsert =
            m_parameters.nodeToInsertGetter == null ? null : m_parameters.nodeToInsertGetter().apply(wfm);
        var connectionId =
            DefaultServiceUtil.entityToConnectionID(getWorkflowKey().getProjectId(), m_parameters.connection());
        m_originalConnection = wfm.getConnection(connectionId);
        m_source = new Port(this.m_originalConnection.getSource(), this.m_originalConnection.getSourcePort());
        m_destination = new Port(this.m_originalConnection.getDest(), this.m_originalConnection.getDestPort());

        // Remove Connection
        wfm.removeConnection(this.m_originalConnection);

        // Move previous node / Create new node
        if (nodeToInsert != null) { // Move node
            m_insertedNode = nodeToInsert;
            m_copyOfExistingInsertedNode = wfm.copy( //
                true, //
                WorkflowCopyContent.builder().setNodeIDs(nodeToInsert).build() //
            );
            var nc = wfm.getNodeContainer(nodeToInsert);
            Translate.translateNodes( //
                wfm, //
                Set.of(nc), //
                Geometry.Delta.of( //
                    m_parameters.position(), //
                    Geometry.Point.of(nc.getUIInformation().getBounds()) //
                ) //
            );
            new NodeConnector(wfm, nodeToInsert) //
                .connectFrom(m_source.node(), m_source.port()) //
                .connectTo(m_destination.node(), m_destination.port()) //
                .trackCreation() //
                .connect();
        } else if (m_parameters.nodeToCreateAndInsert() != null) { // New node
            m_insertedNode = new NodeCreator(wfm, m_parameters.nodeToCreateAndInsert(), m_parameters.position().toEnt()) //
                .centerNode() //
                .trackCreation() //
                .connect(connector -> connector //
                    .connectFrom(m_source.node(), m_source.port()) //
                    .connectTo(m_destination.node(), m_destination.port()) //
                    .trackCreation()) //
                .create();
        } else {
            throw ServiceCallException.builder() //
                .withTitle("Failed to insert node") //
                .withDetails("Both nodeId and nodeFactoryId are not defined. Provide one of them.") //
                .canCopy(false) //
                .build();
        }

        return true;
    }

    @Override
    public boolean canUndo() {
        var wfm = getWorkflowManager();
        return wfm.canRemoveNode(m_insertedNode) //
            && wfm.canAddConnection(m_source.node(), m_source.port(), m_destination.node(), m_destination.port());
    }

    @Override
    public boolean canRedo() {
        var wfm = getWorkflowManager();
        return wfm.canRemoveConnection(m_originalConnection);
    }

    @Override
    public void undo() throws ServiceCallException {
        var wfm = getWorkflowManager();
        wfm.removeNode(m_insertedNode);
        if (m_copyOfExistingInsertedNode != null) {
            wfm.paste(m_copyOfExistingInsertedNode);
        }
        wfm.addConnection(m_source.node(), m_source.port(), m_destination.node(), m_destination.port());
        m_insertedNode = null;
        m_source = null;
        m_destination = null;
    }

}
