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

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.WorkflowAnnotationID;
import org.knime.core.node.workflow.WorkflowCopyContent;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.node.workflow.WorkflowPersistor;
import org.knime.core.node.workflow.action.ExpandSubnodeResult;
import org.knime.gateway.api.entity.AnnotationIDEnt;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.CommandResultEnt;
import org.knime.gateway.api.webui.entity.ExpandCommandEnt;
import org.knime.gateway.api.webui.entity.ExpandResultEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.OperationNotAllowedException;
import org.knime.gateway.impl.service.util.DefaultServiceUtil;
import org.knime.gateway.impl.service.util.WorkflowChangesTracker;
import org.knime.gateway.impl.webui.WorkflowKey;
import org.knime.gateway.impl.webui.WorkflowStatefulUtil;

/**
 * Base methods for expanding a component or a metanode.
 *
 * @author Benjamin Moser, KNIME GmbH, Konstanz, Germany
 */
class AbstractExpand extends AbstractWorkflowCommand {

    private ExpandSubnodeResult m_subNodeExpandResult;

    protected NodeID m_nodeToExpand;

    private WorkflowPersistor m_expandedNodePersistor;

    private boolean m_allowReset;

    AbstractExpand configure(final WorkflowKey wfKey, final WorkflowManager wfm, final ExpandCommandEnt commandEnt) {
        super.configure(wfKey, wfm);
        m_nodeToExpand = DefaultServiceUtil.entityToNodeID(getWorkflowKey().getProjectId(), commandEnt.getNodeId());
        m_allowReset = Optional.ofNullable(commandEnt.isAllowReset()).orElse(false);
        return this;
    }

    @Override
    protected boolean execute() throws ServiceExceptions.OperationNotAllowedException {
        WorkflowCommandUtils.resetNodesOrThrow(this, Set.of(m_nodeToExpand), m_allowReset);
        checkCanExecuteOrThrow();

        WorkflowCopyContent copyContent = WorkflowCopyContent.builder()
                .setNodeIDs(m_nodeToExpand)
                .setIncludeInOutConnections(true)
                .build();
        m_expandedNodePersistor = getWorkflowManager().copy(true, copyContent);

        m_subNodeExpandResult = getWorkflowManager().expandSubWorkflow(m_nodeToExpand);

        WorkflowStatefulUtil.getInstance()
            .clearWorkflowState(new WorkflowKey(getWorkflowKey().getProjectId(), new NodeIDEnt(m_nodeToExpand)));

        return true;
    }

    protected void checkCanExecuteOrThrow() throws ServiceExceptions.OperationNotAllowedException {
        var containingWfmLocked = getWorkflowManager().isEncrypted() && !getWorkflowManager().isUnlocked();
        if (containingWfmLocked) {
            throw new ServiceExceptions.OperationNotAllowedException("Cannot expand in locked workflow manager");
        }

        var containerWfm = getWorkflowManager().getNodeContainer(
                m_nodeToExpand,
                WorkflowManager.class,
                false
        );
        if (containerWfm != null && !containerWfm.isUnlocked()) {
            throw new ServiceExceptions.OperationNotAllowedException("Password-protected component needs to" +
                    "be unlocked first");
        }

        if (containerWfm != null && containerWfm.isWriteProtected()) {
            throw new ServiceExceptions.OperationNotAllowedException("Workflow to be expanded is write-protected" +
                    " (may be a linked metanode or component)");
        }
    }

    @Override
    public void undo() throws ServiceExceptions.OperationNotAllowedException {
        var wfm = getWorkflowManager();
        Set<NodeID> expandedNodes = getExpandedNodes().orElseThrow();
        Set<WorkflowAnnotationID> expandedAnnots = getExpandedAnnotations().orElseThrow();
        expandedNodes.forEach(wfm::removeNode);
        Arrays.stream(wfm.getWorkflowAnnotations(expandedAnnots.toArray(WorkflowAnnotationID[]::new)))
                .forEach(wfm::removeAnnotation);
        wfm.paste(m_expandedNodePersistor);
    }

    @Override
    public boolean canUndo() {
        var wfm = getWorkflowManager();
        return getExpandedNodes().map(nodes -> nodes.stream().allMatch(wfm::canRemoveNode)).orElse(false);
    }

    @Override
    public boolean canRedo() {
        try {
            checkCanExecuteOrThrow();
            return true;
        } catch (OperationNotAllowedException e) {  // NOSONAR
            return false;
        }
    }

    /**
     * @return The set of node IDs previously contained in the freshly expanded container. Only set if command has
     * already been executed successfully.
     */
    public Optional<Set<NodeID>> getExpandedNodes() {
        return Optional.ofNullable(m_subNodeExpandResult).map(r -> Set.of(r.getExpandedCopyContent().getNodeIDs()));
    }

    /**
     * @return The set of annotation IDs previously contained in the contained expanded by this command. Onyl set if command
     * has already been executed successfully.
     */
    public Optional<Set<WorkflowAnnotationID>> getExpandedAnnotations() {
            return Optional.ofNullable(m_subNodeExpandResult).map(r -> Set.of(r.getExpandedCopyContent().getAnnotationIDs()));
    }

    @Override
    public Optional<CommandResultBuilder> getResultBuilder() {
        return Optional.of(new ExpandResultBuilder());
    }

    /**
     * The result of the expand command.
     */
    private class ExpandResultBuilder implements CommandResultBuilder {

        @Override
        public CommandResultEnt buildEntity(final String snapshotId) {
            return builder(ExpandResultEnt.ExpandResultEntBuilder.class)
                    .setKind(CommandResultEnt.KindEnum.EXPANDRESULT)
                    .setSnapshotId(snapshotId)
                    .setExpandedNodeIds(getExpandedNodes().orElseThrow().stream().map(NodeIDEnt::new).collect(Collectors.toList()))
                    .setExpandedAnnotationIds(getExpandedAnnotations().orElseThrow().stream().map(AnnotationIDEnt::new).collect(Collectors.toList()))
                    .build();
        }

        @Override
        public WorkflowChangesTracker.WorkflowChange getChangeToWaitFor() {
            return WorkflowChangesTracker.WorkflowChange.NODE_EXPANDED;
        }

    }
}
