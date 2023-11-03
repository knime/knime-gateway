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

import java.util.Set;
import java.util.function.Supplier;

import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.node.workflow.action.MetaNodeToSubNodeResult;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.api.webui.entity.CommandResultEnt;
import org.knime.gateway.api.webui.entity.ConvertContainerResultEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions;
import org.knime.gateway.impl.service.util.WorkflowChangesTracker.WorkflowChange;

/**
 * Convert the queried metanode to a component.
 *
 * @author Benjamin Moser, KNIME GmbH, Konstanz, Germany
 */
class ConvertMetanodeToComponent extends AbstractWorkflowCommand implements WithResult {

    private final Supplier<NodeIDEnt> m_nodeToConvert;

    private MetaNodeToSubNodeResult m_metaNodeToSubNodeResult;

    private static final String DEFAULT_COMPONENT_NAME = "Component";

    ConvertMetanodeToComponent(final Supplier<NodeIDEnt> nodeToConvert) {
        m_nodeToConvert = nodeToConvert;
    }

    @Override
    protected boolean executeWithLockedWorkflow() throws ServiceExceptions.OperationNotAllowedException {
        try {
            var nodeID = m_nodeToConvert.get().toNodeID(CoreUtil.getProjectWorkflowNodeID(getWorkflowManager()));
            m_metaNodeToSubNodeResult = getWorkflowManager().convertMetaNodeToSubNode(nodeID);
            var snc = getWorkflowManager().getNodeContainer(m_metaNodeToSubNodeResult.getConvertedNodeID(),
                SubNodeContainer.class, true);
            if (snc.getName().equals(CollapseToMetanode.DEFAULT_METANODE_NAME)) {
                snc.setName(ConvertMetanodeToComponent.DEFAULT_COMPONENT_NAME);
            }
            return true;
        } catch (IllegalArgumentException e) { // NOSONAR: Exception is re-thrown as different type
            throw new ServiceExceptions.OperationNotAllowedException(e.getMessage());
        }
    }

    @Override
    public void undo() throws ServiceExceptions.OperationNotAllowedException {
        m_metaNodeToSubNodeResult.undo();
    }

    @Override
    public boolean canUndo() {
        return m_metaNodeToSubNodeResult.canUndo();
    }

    @Override
    public CommandResultEnt buildEntity(final String snapshotId) {
        return builder(ConvertContainerResultEnt.ConvertContainerResultEntBuilder.class) //
            .setKind(CommandResultEnt.KindEnum.CONVERTCONTAINERRESULT) //
            .setSnapshotId(snapshotId) //
            .setConvertedNodeId(new NodeIDEnt(m_metaNodeToSubNodeResult.getConvertedNodeID())) //
            .build();
    }

    @Override
    public Set<WorkflowChange> getChangesToWaitFor() {
        return Set.of(WorkflowChange.NODE_ADDED, WorkflowChange.NODE_REMOVED, WorkflowChange.CONNECTION_ADDED,
            WorkflowChange.CONNECTION_REMOVED);
    }
}
