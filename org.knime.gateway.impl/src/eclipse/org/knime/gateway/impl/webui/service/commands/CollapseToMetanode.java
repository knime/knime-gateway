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

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.WorkflowAnnotation;
import org.knime.core.node.workflow.WorkflowAnnotationID;
import org.knime.core.node.workflow.WorkflowCopyContent;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.node.workflow.action.CollapseIntoMetaNodeResult;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.CollapseCommandEnt;
import org.knime.gateway.api.webui.entity.CollapseResultEnt;
import org.knime.gateway.api.webui.entity.CommandResultEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions;
import org.knime.gateway.impl.service.util.WorkflowChangesTracker;
import org.knime.gateway.impl.webui.WorkflowKey;
import org.knime.gateway.impl.webui.WorkflowStatefulUtil;

/**
 * @author Benjamin Moser, KNIME GmbH, Konstanz, Germany
 */
class CollapseToMetanode extends AbstractPartBasedWorkflowCommand {

    private CollapseIntoMetaNodeResult m_metaNodeCollapseResult;

    static final String DEFAULT_NODE_NAME = "Metanode";

    private Set<NodeID> m_newNodeIdsAfterUndo = new HashSet<>();

    private Set<WorkflowAnnotationID> m_newAnnotIdsAfterUndo = new HashSet<>();

    CollapseToMetanode configure(final WorkflowKey wfKey, final WorkflowManager wfm, final CollapseCommandEnt commandEntity) {
        super.configure(wfKey, wfm, commandEntity);
        return this;
    }

    @Override
    public void undo() throws ServiceExceptions.OperationNotAllowedException {
        Objects.requireNonNull(m_metaNodeCollapseResult);
        if (!m_metaNodeCollapseResult.canUndo()) {
            throw new ServiceExceptions.OperationNotAllowedException("Can not undo metanode creation");
        }
        WorkflowCopyContent reintroducedParts = m_metaNodeCollapseResult.undoWithResult();
        m_newNodeIdsAfterUndo = Set.of(reintroducedParts.getNodeIDs());
        m_newAnnotIdsAfterUndo = Set.of(reintroducedParts.getAnnotationIDs());

        WorkflowStatefulUtil.getInstance().clearWorkflowState(
            new WorkflowKey(getWorkflowKey().getProjectId(), new NodeIDEnt(getNewNode().orElseThrow())));
    }

    @Override
    public boolean canRedo() {
        return getNodeIDs().stream().anyMatch(getWorkflowManager()::canResetNode);
    }

    @Override
    public Optional<CommandResultBuilder> getResultBuilder() {
        return Optional.of(new CollapseResultBuilder());
    }

    @Override
    protected boolean execute() throws ServiceExceptions.OperationNotAllowedException {
        checkPartsPresentElseThrow();

        var wfm = getWorkflowManager();
        getNodeIDs().stream().filter(wfm::canResetNode).forEach(wfm::resetAndConfigureNode);

        var nodeIds = getNodeIDs().toArray(NodeID[]::new);

        var cannotCollapseReason = getWorkflowManager().canCollapseNodesIntoMetaNode(nodeIds);
        if (cannotCollapseReason != null) {
            throw new ServiceExceptions.OperationNotAllowedException(cannotCollapseReason);
        }

        try {
            m_metaNodeCollapseResult = getWorkflowManager().collapseIntoMetaNode(
                    nodeIds,
                    getAnnotations().toArray(WorkflowAnnotation[]::new),
                    DEFAULT_NODE_NAME
            );
            return true;
        } catch (IllegalArgumentException e) {  // NOSONAR: Exception is re-thrown as different type
            throw new ServiceExceptions.OperationNotAllowedException(e.getMessage());
        }
    }

    @Override
    protected Set<NodeID> getNodeIDs() {
        return !m_newNodeIdsAfterUndo.isEmpty() ? m_newNodeIdsAfterUndo : super.getNodeIDs();
    }

    @Override
    protected Set<WorkflowAnnotationID> getAnnotationIDs() {
        return !m_newAnnotIdsAfterUndo.isEmpty() ? m_newAnnotIdsAfterUndo : super.getAnnotationIDs();
    }

    @Override
    public boolean canUndo() {
        return Optional.ofNullable(m_metaNodeCollapseResult).map(CollapseIntoMetaNodeResult::canUndo).orElse(false);
    }

    Optional<NodeID> getNewNode() {
        return Optional.ofNullable(m_metaNodeCollapseResult).map(CollapseIntoMetaNodeResult::getCollapsedMetanodeID);
    }

    /**
     * The result of the collapse command.
     */
    private class CollapseResultBuilder implements CommandResultBuilder {

        /**
         * {@inheritDoc}
         */
        @Override
        public CommandResultEnt buildEntity(final String snapshotId) {
            return builder(CollapseResultEnt.CollapseResultEntBuilder.class)
                    .setKind(CommandResultEnt.KindEnum.COLLAPSERESULT)
                    .setSnapshotId(snapshotId)
                    .setNewNodeId(new NodeIDEnt(getNewNode().orElseThrow()))
                    .build();
        }

        @Override
        public WorkflowChangesTracker.WorkflowChange getChangeToWaitFor() {
            return WorkflowChangesTracker.WorkflowChange.NODES_COLLAPSED;
        }
    }
}
