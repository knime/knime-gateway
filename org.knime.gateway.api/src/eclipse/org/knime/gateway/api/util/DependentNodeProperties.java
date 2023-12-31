/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.com; Email: contact@knime.com
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
 *   Sep 21, 2020 (hornm): created
 */
package org.knime.gateway.api.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import org.knime.core.node.workflow.LoopEndNode;
import org.knime.core.node.workflow.NativeNodeContainer;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeContainerState;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.node.workflow.WorkflowLock;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.node.workflow.virtual.subnode.VirtualSubNodeOutputNodeModel;

/**
 * Represents node properties that depend on other nodes in the workflow graph (i.e. successors or predecessors). This
 * class helps to be able to calculate those properties in one go and caches them for subsequent usage.
 *
 * Dependent node properties are {@link WorkflowManager#hasExecutablePredecessor(NodeID)} and
 * {@link WorkflowManager#hasSuccessorInProgress(NodeID)}.
 *
 * NOTE: this class is meant to be a TEMPORARY workaround. A long-term solution to this problem (the efficient
 * calculation of those dependent node properties) should be covered by and tightly integrated into the workflow-manager
 * API/framework.
 *
 * Another note: every time the {@link #calc()}-method is called the entire workflow (of the associated workflow manager
 * and partly of the parent workflow manager) graph is traversed. That maybe could be avoided somehow if we knew what
 * parts of the graph have been changed (a changed node state itself is not enough).
 *
 * @noreference This class is not intended to be referenced by clients.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @since 4.3
 */
public final class DependentNodeProperties {

    /**
     * This method serves to speed up the calls of the {@link #canExecuteNode(NodeID)} and {@link #canResetNode(NodeID)}
     * methods, if intended to be called for many (usually all) nodes that are contained in this workflow.
     *
     * Explanation: The mentioned methods require to determine the 'executable predecessors' or 'executing successors'
     * which is normally done every time they are called. However, if this method is used beforehand, the 'executable
     * predecessors' and 'executing successors' are determined in one go for all nodes.
     *
     * @param wfm the workflow to calculate the dependent node properties for
     *
     * @return the {@link DependentNodeProperties} object which contains the pre-calculated can-reset and can-execute
     *         predicates. NOT automatically kept in sync with the workflow. This method needs to be called again to
     *         update the predicates.
     */
    public static DependentNodeProperties determineDependentNodeProperties(final WorkflowManager wfm) {
        try (WorkflowLock lock = wfm.lock()) {
            return new DependentNodeProperties(wfm);
        }
    }

    // all nodes including the nc parent (metanode or component; unless this is the project workflow)
    private final Map<NodeID, Properties> m_props = new HashMap<>();

    private final WorkflowManager m_wfm;

    /**
     * @param wfm the 'dependent properties' of the nodes contained in the given workflow manager will be updated
     */
    private DependentNodeProperties(final WorkflowManager wfm) {
        m_wfm = wfm;
        calc();
    }

    /**
     * Whether a node can be executed. Equivalent to the {@link WorkflowManager#canExecuteNode(NodeID)}-method, but
     * faster if called for many (or usually all) nodes.
     *
     * @param id
     * @return <code>true</code> if the node can be executed, otherwise <code>false</code>
     */
    public boolean canExecuteNode(final NodeID id) {
        if (!m_props.containsKey(id)) {
            return false;
        }
        return m_wfm.canExecuteNode(id, i -> m_props.get(i).hasExecutablePredecessors());
    }

    /**
     * Whether a node can be reset. Equivalent to the {@link WorkflowManager#canResetNode(NodeID)}-method, but faster if
     * called for many (or usually all) nodes.
     *
     * @param id
     * @return <code>true</code> if the node can be reset, otherwise <code>false</code>
     */
    public boolean canResetNode(final NodeID id) {
        if (!m_props.containsKey(id)) {
            return false;
        }
        boolean wfmCanReset = m_wfm.canResetNode(id, i -> m_props.get(i).hasExecutingSuccessors());
        boolean hasPausedSuccessor = m_props.get(id).hasPausedSuccessor();
        // Applies e.g. to incoming branches of loops or nodes upstream of the loop head.
        // Will not be true if there is a paused successor in a parent workflow. However, then, the workflow manager
        //  will not allow to reset the node by means of canResetContainedNodes -> canResetSuccessors,
        //  which checks for isExecutionInProgress, which also applies to paused tail nodes.
        return wfmCanReset && !hasPausedSuccessor;
    }

    /**
     * Determine whether a connection incident to the given node can be removed.
     *
     * @param id The node to consider.
     * @return Whether a connection on that node can be removed.
     */
    public boolean canRemoveIncomingConnections(final NodeID id) {
        var nc = m_wfm.getNodeContainer(id);
        var isExecutionInProgress = isExecutionInProgress(nc);
        var isExecuted = nc.getNodeContainerState().isExecuted();
        var canReset = canResetNode(id);
        return !(isExecutionInProgress || (isExecuted && !canReset));
    }

    /**
     * Determine whether nodes in the "loop body" are currently executing, based on the current dependent node
     * properties. Here, the loop body is the set of nodes forward-reachable from the loop head, up to the loop tail.
     * Consequently, this includes outgoing branches and does not include incoming branches.
     *
     * @param tail The tail node of the loop, expected to be an instance of {@link LoopEndNode}.
     * @return A boolean indicating whether nodes in the loop body (as defined above) are currently executing. If there
     *         is no loop body (i.e. no loop head), this method returns {@code false}.
     */
    public boolean hasExecutingLoopBody(final NativeNodeContainer tail) {
        if (!tail.isModelCompatibleTo(LoopEndNode.class)) {
            throw new IllegalArgumentException("Given node must be a loop end node");
        }

        return CoreUtil.getLoopContext(tail).map(loopContext -> {
            var nHead = loopContext.getHeadNode();
            var props = m_props.get(nHead);
            if (props == null) {
                // When a loop head node is removed, the loop context may point to a node ID that is no longer
                //  contained in the workflow and consquentely the dependent node properties.
                return false;
            }
            boolean sHead = props.hasExecutingSuccessors();
            boolean sTail = CoreUtil.successors(tail.getID(), m_wfm).stream()
                .anyMatch(id -> m_props.get(id).hasExecutingSuccessors());
            return sHead && !sTail;
        }).orElse(false);
    }

    /**
     * @param id
     *
     * @return Whether the node has a successor that is currently executing. Also considers successors across
     *         component/metanode borders. The property is boundary-inclusive: A currently executing node will also have
     *         this property.
     */
    public boolean hasExecutingSuccessor(final NodeID id) {
        if (!m_props.containsKey(id)) {
            return false;
        }
        return m_props.get(id).hasExecutingSuccessors();
    }

    /**
     * @return True iff at least one node in the workflow can be reset. Here, reset-ability also considers the context
     *         of the node, e.g. whether it has executing successors.
     */
    public boolean canResetAny() {
        return m_props.keySet().stream().anyMatch(this::canResetNode);
    }

    /**
     * Calculates the node dependent properties. The respective workflow need to be locked when the calculation is
     * performed.
     */
    private void calc() {
        assert m_wfm.isLockedByCurrentThread();
        LinkedList<NodeID> hasExecutingSuccessors = new LinkedList<>();
        LinkedList<NodeID> hasExecutablePredecessors = new LinkedList<>();
        LinkedList<NodeID> hasPausedSuccessors = new LinkedList<>();
        findAndInitStartNodes(m_wfm, m_props, hasExecutingSuccessors, hasExecutablePredecessors, hasPausedSuccessors);
        if (m_props.size() > m_wfm.getNodeContainers().size()) {
            removeSurplusNodes(m_wfm, m_props);
        }
        updateHasExecutablePredecessorsProperties(hasExecutablePredecessors);
        updateHasExecutingSuccessorsProperties(hasExecutingSuccessors);
        updateHasPausedSuccessorsProperties(hasPausedSuccessors);
    }

    private void updateHasExecutablePredecessorsProperties(final Queue<NodeID> startNodes) {
        iterateNodes(startNodes, this::hasExecutablePredecessorVisitor, id -> CoreUtil.successors(id, m_wfm));
    }

    private void updateHasExecutingSuccessorsProperties(final Queue<NodeID> startNodes) {
        iterateNodes(startNodes, this::hasExecutingSuccessorVisitor, id -> CoreUtil.predecessors(id, m_wfm));
    }

    private void updateHasPausedSuccessorsProperties(final Queue<NodeID> startNodes) {
        iterateNodes(startNodes, this::hasPausedSuccessorVisitor, id -> CoreUtil.predecessors(id, m_wfm));
    }

    private static void removeSurplusNodes(final WorkflowManager wfm, final Map<NodeID, Properties> props) {
        props.entrySet().removeIf(e -> !wfm.containsNodeContainer(e.getKey()) && !e.getKey().equals(wfm.getID()));
    }

    private static void iterateNodes(final Queue<NodeID> queue, final Predicate<NodeID> visitor,
        final Function<NodeID, Set<NodeID>> nextNodes) {
        while (!queue.isEmpty()) {
            NodeID current = queue.poll();
            for (NodeID next : nextNodes.apply(current)) {
                if (visitor.test(next)) {
                    queue.add(next);
                }
            }
        }
    }

    private static void findAndInitStartNodes(final WorkflowManager wfm, final Map<NodeID, Properties> propsMap,
        final Queue<NodeID> hasExecutingSuccessors, final Queue<NodeID> hasExecutablePredecessors,
        final LinkedList<NodeID> hasPausedSuccessors) {
        for (NodeContainer nc : wfm.getNodeContainers()) {
            var nodeId = nc.getID();
            var props = propsMap.computeIfAbsent(nodeId, i -> new Properties());
            var nodeState = nc.getNodeContainerState();
            var added = false;

            // Optimisation: Set the property for any waiting node as well, but add only actually executing nodes as seeds.
            //  (Per definition, waiting nodes must have an upstream node that is executing).
            props.setHasExecutingSuccessors(isExecutionInProgress(nc));
            if (isExecuting(nodeState)) {
                hasExecutingSuccessors.add(nodeId);
                added = true;
            }

            props.setHasExecutablePredecessors(nodeState.isConfigured());
            if (nodeState.isConfigured() || isIdleButContainsConfiguredNodes(nodeState, nc)) {
                hasExecutablePredecessors.add(nodeId);
                added = true;
            }

            props.setHasPausedSuccessor(isPausedLoopEndNode(nc));
            if (isPausedLoopEndNode(nc)) {
                hasPausedSuccessors.add(nodeId);
            }

            if (!added) {
                handleNodesAtComponentAndMetanodeBorders(wfm, hasExecutingSuccessors, hasExecutablePredecessors, nc,
                    nodeId, props);
            }
        }
    }

    private static boolean isIdleButContainsConfiguredNodes(final NodeContainerState nodeState,
        final NodeContainer nc) {
        if (nodeState.isIdle() && (nc instanceof SubNodeContainer || nc instanceof WorkflowManager)) {
            return nc.getParent().canExecuteNodeDirectly(nc.getID());
        }
        return false;
    }

    /*
     * Special handling of component output nodes: The logical successor of a component output node is the parent
     * component itself -> that's why it's checked for successors in progress. If there are any, the component output
     * node is added to the list of executing successors. Component input nodes (and predecessors of the component
     * itself) don't need to be checked because all direct predecessor of a component need to be executed anyway for a
     * component to be executable in its entirety.
     *
     * Special handling of nodes connected to metanode-incoming or -outgoing ports (i.e. from within the metanode): If
     * they happen to have executing successors or executable predecessors in the parent workflow (requires an extra
     * traversal of the parent workflow), they are added to the respective lists.
     */
    private static void handleNodesAtComponentAndMetanodeBorders(final WorkflowManager wfm,
        final Queue<NodeID> hasExecutingSuccessors, final Queue<NodeID> hasExecutablePredecessors,
        final NodeContainer nc, final NodeID id, final Properties p) {
        if (nc instanceof NativeNodeContainer
            && ((NativeNodeContainer)nc).getNodeModel() instanceof VirtualSubNodeOutputNodeModel) {
            // special handling of component output nodes
            getParentComponent(wfm) //
                .filter(snc -> snc.getParent().hasSuccessorInProgress(snc.getID())) //
                .ifPresent(snc -> {
                    p.setHasExecutingSuccessors(true);
                    hasExecutingSuccessors.add(snc.getID());
                });
        } else {
            // special handling of nodes connected to metanode incoming or outgoing ports (i.e. from within the metanode)
            if (wfm.getIncomingConnectionsFor(id).stream().anyMatch(cc -> cc.getSource().equals(wfm.getID())) && //
                wfm.hasExecutablePredecessor(id)) {
                p.setHasExecutablePredecessors(true);
                hasExecutablePredecessors.add(id);
            }
            if (wfm.getOutgoingConnectionsFor(id).stream().anyMatch(cc -> cc.getDest().equals(wfm.getID())) && //
                wfm.hasSuccessorInProgress(id)) {
                p.setHasExecutingSuccessors(true);
                hasExecutingSuccessors.add(id);
            }
        }
    }

    private static boolean isExecutionInProgress(final NodeContainer nc) {
        // Paused loop tails have state CONFIGURED_MARKEDFOREXEC but are not considered as executing for the purpose
        //  of this method since they could in fact also be paused (this paused state is not explicitly reflected in
        //  the node state but determined via NativeNodeContainer#getLoopStatus).
        var s = nc.getNodeContainerState();
        return !isPausedLoopEndNode(nc) && (s.isExecutionInProgress() || s.isExecutingRemotely());
    }

    private static boolean isExecuting(final NodeContainerState s) {
        return (s.isExecutionInProgress() && !s.isWaitingToBeExecuted()) || s.isExecutingRemotely();
    }

    private static boolean isPausedLoopEndNode(final NodeContainer nc) {
        if (!(nc instanceof NativeNodeContainer)) {
            return false;
        }
        var nnc = (NativeNodeContainer)nc;
        return nnc.isModelCompatibleTo(LoopEndNode.class)
            && nnc.getLoopStatus() == NativeNodeContainer.LoopStatus.PAUSED;
    }

    private boolean hasExecutablePredecessorVisitor(final NodeID id) {
        Properties p = m_props.get(id);
        if (p.hasExecutablePredecessors()) {
            return false;
        } else {
            p.setHasExecutablePredecessors(true);
            return true;
        }
    }

    private boolean hasExecutingSuccessorVisitor(final NodeID id) {
        Properties p = m_props.get(id);
        if (p.hasExecutingSuccessors()) {
            return false;
        } else {
            p.setHasExecutingSuccessors(true);
            return true;
        }
    }

    private boolean hasPausedSuccessorVisitor(final NodeID id) {
        Properties p = m_props.get(id);
        if (p.hasPausedSuccessor()) {
            return false;
        } else {
            p.setHasPausedSuccessor(true);
            return true;
        }
    }

    /**
     * @param wfm The child workflow manager
     * @return The parent component of a child wfm, or empty if there is no parent component (i.e. the parent is the
     *         root wfm, which is not a component).
     */
    private static Optional<SubNodeContainer> getParentComponent(final WorkflowManager wfm) {
        return Optional.of(wfm.getDirectNCParent()) //
            .filter(p -> p != WorkflowManager.ROOT) //
            .map(p -> (SubNodeContainer)p);
    }

    private static class Properties {

        private boolean m_hasExecutablePredecessors;

        private boolean m_hasExecutingSuccessors;

        private boolean m_hasPausedSuccessor;

        Properties() {
            this(false, false);
        }

        Properties(final boolean hasExecutablePredecessors, final boolean hasExecutingSuccessors) {
            m_hasExecutablePredecessors = hasExecutablePredecessors;
            m_hasExecutingSuccessors = hasExecutingSuccessors;
        }

        /**
         * @return Whether the node has a predecessor that is executable. Also considers predecessors across
         *         component/metanode borders. The property is boundary-inclusive: An executable node will also have
         *         this property.
         */
        boolean hasExecutablePredecessors() {
            return m_hasExecutablePredecessors;
        }

        /**
         * @return Whether the node has a successor that is currently executing. Also considers successors across
         *         component/metanode borders. The property is boundary-inclusive: A currently executing node will also
         *         have this property.
         */
        boolean hasExecutingSuccessors() {
            return m_hasExecutingSuccessors;
        }

        /**
         * @return Whether the node has a successor that is a loop end node ("tail") in a paused state. Note that any
         *         such nodes outside the current workflow (i.e. in a parent workflow) are not considered, see
         *         {@link #canResetNode(NodeID)}}.
         */
        boolean hasPausedSuccessor() {
            return m_hasPausedSuccessor;
        }

        void setHasExecutablePredecessors(final boolean b) {
            m_hasExecutablePredecessors = b;
        }

        void setHasExecutingSuccessors(final boolean b) {
            m_hasExecutingSuccessors = b;
        }

        void setHasPausedSuccessor(final boolean b) {
            m_hasPausedSuccessor = b;
        }
    }

}
