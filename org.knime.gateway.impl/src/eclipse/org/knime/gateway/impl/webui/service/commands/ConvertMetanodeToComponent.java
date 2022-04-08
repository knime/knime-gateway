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
package org.knime.gateway.impl.webui.service.commands;

import static org.knime.gateway.api.entity.EntityBuilderManager.builder;

import java.util.Optional;

import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.node.workflow.action.MetaNodeToSubNodeResult;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.CommandResultEnt;
import org.knime.gateway.api.webui.entity.ConvertContainerResultEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.OperationNotAllowedException;
import org.knime.gateway.impl.service.util.EventTracker;
import org.knime.gateway.impl.webui.WorkflowKey;

/**
 * Convert the queried metanode to a component.
 *
 * @author Benjamin Moser, KNIME GmbH, Konstanz, Germany
 */
public class ConvertMetanodeToComponent extends AbstractWorkflowCommand {

    private final NodeID m_nodeToConvert;

    private MetaNodeToSubNodeResult m_metaNodeToSubNodeResult;

    static final String DEFAULT_NODE_NAME = "Component";

    /**
     * Initialise the command.
     * @param wfKey The workflow to operate in
     * @param nodeToConvert The ID of the metanode to convert to a component.
     * @throws ServiceExceptions.NodeNotFoundException If the workflow to operate in could not be found
     * @throws ServiceExceptions.NotASubWorkflowException If the specified node id is not a sub-workflow
     * @throws ServiceExceptions.OperationNotAllowedException If the command could not be initalized
     */
    public ConvertMetanodeToComponent(final WorkflowKey wfKey, final NodeID nodeToConvert)
            throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.NotASubWorkflowException, OperationNotAllowedException {
        super(wfKey);

        m_nodeToConvert = nodeToConvert;
    }

    @Override
    protected boolean executeImpl() throws ServiceExceptions.OperationNotAllowedException {
        try {
            m_metaNodeToSubNodeResult = getWorkflowManager().convertMetaNodeToSubNode(m_nodeToConvert);
            var snc = getWorkflowManager().getNodeContainer(
                    m_metaNodeToSubNodeResult.getConvertedNodeID(), SubNodeContainer.class, true
            );
            if (snc.getName().equals(CollapseToMetanode.DEFAULT_NODE_NAME)) {
                snc.setName(ConvertMetanodeToComponent.DEFAULT_NODE_NAME);
            }
            return true;
        } catch (IllegalArgumentException e) {  // NOSONAR: Exception is re-thrown as different type
            throw new ServiceExceptions.OperationNotAllowedException(e.getMessage());
        }
    }

    @Override
    public void undo() throws ServiceExceptions.OperationNotAllowedException {
        if (!m_metaNodeToSubNodeResult.canUndo()) {
            throw new ServiceExceptions.OperationNotAllowedException("Can not undo component creation");
        }
        m_metaNodeToSubNodeResult.undo();
    }

    @Override
    public boolean providesResult() {
        return true;
    }

    @Override
    public Optional<CommandResult> getResult() {
        return Optional.of(new ConvertResult(getConvertedNode().orElseThrow()));
    }

    @Override
    public Optional<EventTracker.Event> getTrackedEvent() {
        return Optional.of(EventTracker.Event.NODE_OR_CONNECTION_ADDED_OR_REMOVED);
    }

    private Optional<NodeID> getConvertedNode() {
        return Optional.ofNullable(m_metaNodeToSubNodeResult).map(MetaNodeToSubNodeResult::getConvertedNodeID);
    }

    /**
     * The result of the conversion.
     */
    public static class ConvertResult implements CommandResult {

        private final NodeID m_convertedNode;

        /**
         * Create a new result
         * @param convertedNode The ID of the newly introduced node that is the result of the conversion.
         */
        public ConvertResult(final NodeID convertedNode) {
            m_convertedNode = convertedNode;
        }

        @Override public CommandResultEnt buildEntity(final String snapshotId) {
            return builder(ConvertContainerResultEnt.ConvertContainerResultEntBuilder.class)
                    .setKind(CommandResultEnt.KindEnum.CONVERTRESULT)
                    .setSnapshotId(snapshotId)
                    .setConvertedNodeId(new NodeIDEnt(m_convertedNode))
                    .build();
        }
    }
}
