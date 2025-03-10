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
import java.util.Collections;
import java.util.Set;

import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.WorkflowAnnotationID;
import org.knime.core.node.workflow.WorkflowCopyContent;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.node.workflow.WorkflowPersistor;
import org.knime.core.node.workflow.action.ExpandSubnodeResult;
import org.knime.gateway.api.entity.AnnotationIDEnt;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.api.webui.entity.CommandResultEnt;
import org.knime.gateway.api.webui.entity.ExpandCommandEnt;
import org.knime.gateway.api.webui.entity.ExpandResultEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.ServiceCallException;
import org.knime.gateway.impl.service.util.WorkflowChangesTracker;

/**
 * Base methods for expanding a component or a metanode.
 *
 * @author Benjamin Moser, KNIME GmbH, Konstanz, Germany
 */
class AbstractExpand extends AbstractWorkflowCommand implements WithResult {

    private final ExpandCommandEnt m_commandEnt;

    private ExpandSubnodeResult m_subNodeExpandResult;

    private WorkflowPersistor m_expandedNodePersistor;

    AbstractExpand(final ExpandCommandEnt commandEnt) {
        m_commandEnt = commandEnt;
    }

    @Override
    protected boolean executeWithLockedWorkflow() throws ServiceExceptions.ServiceCallException {
        var wfm = getWorkflowManager();
        var nodeToExpand = m_commandEnt.getNodeId().toNodeID(wfm);
        if (wfm.canResetNode(nodeToExpand)) {
            wfm.resetAndConfigureNode(nodeToExpand);
        }

        checkCanExpandOrThrow(wfm, nodeToExpand);

        WorkflowCopyContent copyContent = WorkflowCopyContent.builder() //
            .setNodeIDs(nodeToExpand) //
            .setIncludeInOutConnections(true) //
            .build();
        m_expandedNodePersistor = wfm.copy(true, copyContent);

        m_subNodeExpandResult = wfm.expandSubWorkflow(nodeToExpand);
        return true;
    }

    protected void checkCanExpandOrThrow(final WorkflowManager wfm, final NodeID nodeToExpand)
        throws ServiceExceptions.ServiceCallException {
        var containedWfm = CoreUtil.getContainedWfm(nodeToExpand, wfm)
            .orElseThrow(() -> new ServiceExceptions.ServiceCallException(
                "No container node with the supplied ID can be found in workflow"));
        if (!containedWfm.isUnlocked()) {
            throw new ServiceExceptions.ServiceCallException(
                "Password-protected metanode/container needs to be unlocked first");
        }
        if (containedWfm.isWriteProtected()) {
            throw new ServiceExceptions.ServiceCallException(
                "Workflow to be expanded is write-protected (may be a linked metanode or component)");
        }
    }

    @Override
    public void undo() throws ServiceExceptions.ServiceCallException {
        var wfm = getWorkflowManager();
        var expandedNodes = getExpandedNodes();
        var expandedAnnots = getExpandedAnnotations();
        expandedNodes.forEach(wfm::removeNode);
        expandedAnnots.forEach(wfm::removeAnnotation);
        wfm.paste(m_expandedNodePersistor);
    }

    @Override
    public boolean canUndo() {
        var wfm = getWorkflowManager();
        return getExpandedNodes().stream().allMatch(wfm::canRemoveNode);
    }

    @Override
    public boolean canRedo() {
        try {
            var wfm = getWorkflowManager();
            checkCanExpandOrThrow(getWorkflowManager(),
                m_commandEnt.getNodeId().toNodeID(wfm));
            return true;
        } catch (ServiceCallException e) { // NOSONAR
            return false;
        }
    }

    private Set<NodeID> getExpandedNodes() {
        // using `Set.of(T...)` here in combination with external null annotations triggers an Eclipse 2024-03 bug
        return Set.copyOf(Arrays.asList(m_subNodeExpandResult.getExpandedCopyContent().getNodeIDs()));
    }

    private Set<WorkflowAnnotationID> getExpandedAnnotations() {
        // using `Set.of(T...)` here in combination with external null annotations triggers an Eclipse 2024-03 bug
        return Set.copyOf(Arrays.asList(m_subNodeExpandResult.getExpandedCopyContent().getAnnotationIDs()));
    }

    @Override
    public CommandResultEnt buildEntity(final String snapshotId) {
        return builder(ExpandResultEnt.ExpandResultEntBuilder.class) //
            .setKind(CommandResultEnt.KindEnum.EXPAND_RESULT) //
            .setSnapshotId(snapshotId) //
            .setExpandedNodeIds(getExpandedNodes().stream().map(NodeIDEnt::new).toList()) //
            .setExpandedAnnotationIds(
                getExpandedAnnotations().stream().map(AnnotationIDEnt::new).toList()) //
            .build();
    }

    @Override
    public Set<WorkflowChangesTracker.WorkflowChange> getChangesToWaitFor() {
        return Collections.singleton(WorkflowChangesTracker.WorkflowChange.NODE_EXPANDED);
    }

}
