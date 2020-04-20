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
 *   Apr 18, 2020 (hornm): created
 */
package com.knime.gateway.remote.service.util;

import static com.knime.gateway.entity.EntityBuilderManager.builder;
import static java.util.stream.Collectors.toCollection;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.knime.core.node.workflow.ConnectionContainer;
import org.knime.core.node.workflow.NativeNodeContainer;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.NodeStateChangeListener;
import org.knime.core.node.workflow.NodeStateEvent;
import org.knime.core.node.workflow.NodeTimer;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.node.workflow.WebResourceController;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.util.Pair;

import com.knime.gateway.entity.ExecutionStatisticsEnt;
import com.knime.gateway.entity.ExecutionStatisticsEnt.ExecutionStatisticsEntBuilder;
import com.knime.gateway.entity.NodeExecutedStatisticsEnt;
import com.knime.gateway.entity.NodeExecutedStatisticsEnt.NodeExecutedStatisticsEntBuilder;
import com.knime.gateway.entity.NodeExecutingStatisticsEnt;
import com.knime.gateway.entity.NodeExecutingStatisticsEnt.NodeExecutingStatisticsEntBuilder;

/**
 * Provides execution statistics for a workflow in wizard execution.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class WizardExecutionStatistics {

    private final Map<NodeID, Integer> m_watchedNodes = new HashMap<>();

    private long m_executionStartTime;

    private Long m_executionEndTime = null;

    /**
     * Helper method to determine the wizard execution state from a workflow.
     *
     * @param wfm the workflow manager to check
     * @return the workflows wizard execution state
     */
    public static String getWizardExecutionState(final WorkflowManager wfm) {
        if (wfm.isInWizardExecution() && wfm.getWizardExecutionController().hasCurrentWizardPage()) {
            return "INTERACTION_REQUIRED";
        } else if (wfm.getNodeContainerState().isExecutionInProgress()) {
            return "EXECUTING";
        } else if (wfm.getNodeContainerState().isExecuted()) {
            return "EXECUTION_FINISHED";
        } else if (!hasWorkflowExecutionStarted(wfm)) {
            return "UNDEFINED";
        } else {
            return "EXECUTION_FAILED";
        }
    }

    /**
     * Helper method to check whether the workflow execution is finished.
     *
     * @param wfm the workflow manager to check
     * @return <code>true</code> if the workflow execution is finished, otherwise <code>false</code>
     */
    public static boolean isWfmDone(final WorkflowManager wfm) {
        return wfm.getNodeContainerState().isConfigured() || wfm.getNodeContainerState().isWaitingToBeExecuted()
            || wfm.getNodeContainerState().isExecuted() || wfm.getNodeContainerState().isIdle();
    }

    /**
     * Resets the execution statistics to the given wizard page (represented by the node id). Only nodes between the
     * given wizard page and successor wizard pages are considered in the statistics.
     *
     * @param wizardPageNodeID
     * @param wfm the workflow
     */
    public void resetStatisticsToWizardPage(final NodeID wizardPageNodeID, final WorkflowManager wfm) {
        assert isWizardPage(wfm.getNodeContainer(wizardPageNodeID));
        timeWorkflowExecution(wfm);

        //TODO loop contexts
        //notes:
        //* get the loop context stack of the start node
        //* the loop body are the affected nodes in each context
        //* store them in a map with loop start node as key
        //* context selection if start node is re-executed (i.e. nr-exec increased)

        m_watchedNodes.clear();
        if (wizardPageNodeID == null) {
            // only include source nodes, 'source' wizard pages and the nodes in between
            final Set<NodeID> dependentWizardPages = getDependentWizardPages(wfm);
            for (NodeID sourceNode : getSourceNodes(wfm)) {
                addContainedAndConnectedNativeNodes(sourceNode, sourceNode, wfm,
                    nc -> dependentWizardPages.contains(nc.getID()), m_watchedNodes);
            }
        } else {
            addContainedAndConnectedNativeNodes(wizardPageNodeID, wizardPageNodeID, wfm, nc -> false, m_watchedNodes);
        }
    }

    /*
     * Dependent wizard pages (i.e. components) are those that have another wizard page as (indirect) predecessor.
     */
    private static Set<NodeID> getDependentWizardPages(final WorkflowManager wfm) {
        Set<NodeID> wizardPages = wfm.getNodeContainers().stream().filter(WizardExecutionStatistics::isWizardPage)
            .map(NodeContainer::getID).collect(toCollection(HashSet::new));
        Set<NodeID> reachableNodes = new HashSet<>();
        checkReachability(wizardPages, wizardPages, reachableNodes, wfm);
        return reachableNodes;
    }

    /*
     * Checks whether to nodes in the list are reachable by each other (along a chain of successors).
     * The reachable nodes are added to the reachableNodes list recursively.
     */
    private static void checkReachability(final Set<NodeID> nodesToCheck, final Set<NodeID> nodesToCheckThisIteration,
        final Set<NodeID> reachableNodes, final WorkflowManager wfm) {
        if (nodesToCheckThisIteration.isEmpty()) {
            return;
        }
        Set<NodeID> successors = nodesToCheckThisIteration.stream().flatMap(id -> getSuccessors(id, wfm).stream())
            .collect(toCollection(HashSet::new));
        for (NodeID id : successors) {
            if (!reachableNodes.contains(id) && nodesToCheck.contains(id)) {
                reachableNodes.add(id);
            }
        }
        successors.removeAll(reachableNodes);
        checkReachability(nodesToCheck, successors, reachableNodes, wfm);
    }

    /*
     * Collects all nodes without any connected input.
     */
    private static Set<NodeID> getSourceNodes(final WorkflowManager wfm) {
        return wfm.getNodeContainers().stream()
            .filter(nc -> nc.getParent().getIncomingConnectionsFor(nc.getID()).isEmpty()).map(NodeContainer::getID)
            .collect(Collectors.toSet());
    }

    /*
     * Recursively adds all native nodes (this node, nodes contained in this node and all successors) and stops
     * as soon as another wizard page is encountered.
     */
    private static void addContainedAndConnectedNativeNodes(final NodeID startNodeID, final NodeID currentNodeID,
        final WorkflowManager wfm, final Predicate<NodeContainer> exclude, final Map<NodeID, Integer> nodes) {
        if (nodes.containsKey(currentNodeID)) {
            return;
        }
        NodeContainer nc = wfm.getNodeContainer(currentNodeID);
        if (!exclude.test(nc)) {
            addContainedNativeNodes(nc, nodes);
        }
        if (!currentNodeID.equals(startNodeID) && isWizardPage(nc)) {
            return;
        }
        Set<NodeID> nextNodes = getSuccessors(currentNodeID, wfm);
        for (NodeID nextID : nextNodes) {
            addContainedAndConnectedNativeNodes(startNodeID, nextID, wfm, exclude, nodes);
        }
    }

    /*
     * Check whether a node represents a wizard page.
     */
    private static boolean isWizardPage(final NodeContainer nc) {
        return WebResourceController.isWizardPage(nc.getID(), nc.getParent());
    }

    /*
     * Recursively adds all (deeply) contained native nodes to the provided list.
     */
    private static void addContainedNativeNodes(final NodeContainer nc, final Map<NodeID, Integer> nativeNodes) {
        if (nc instanceof SubNodeContainer) {
            addContainedNativeNodes(((SubNodeContainer)nc).getWorkflowManager(), nativeNodes);
        } else if (nc instanceof WorkflowManager) {
            addContainedNativeNodes(nc, nativeNodes);
        } else if (nc instanceof NativeNodeContainer) {
            nativeNodes.put(nc.getID(), nc.getNodeTimer().getNrExecsSinceStart());
        }
    }

    /*
     * Recursively adds all (deeply) contained native nodes to the provided list.
     */
    private static void addContainedNativeNodes(final WorkflowManager wfm, final Map<NodeID, Integer> nativeNodes) {
        for (NodeContainer nc : wfm.getNodeContainers()) {
            addContainedNativeNodes(nc, nativeNodes);
        }
    }

    /*
     * Returns all direct node successors on the same level.
     */
    private static Set<NodeID> getSuccessors(final NodeID id, final WorkflowManager wfm) {
        return wfm.getOutgoingConnectionsFor(id).stream().map(ConnectionContainer::getDest).collect(Collectors.toSet());
    }

    /**
     * Gets up-to-date statistics on the provided workflow possibly only considering a subset of nodes (i.e. all nodes
     * between wizard pages as set by {@link #resetStatisticsToWizardPage(NodeID, WorkflowManager)}).
     *
     * @param wfm the workflow to get the updated statistics for
     * @return the statistics
     */
    public ExecutionStatisticsEnt getUpdatedStatistics(final WorkflowManager wfm) {
        List<Pair<Long, NodeExecutingStatisticsEnt>> executingNodes = new ArrayList<>();
        List<Pair<Long, NodeExecutedStatisticsEnt>> executedNodes = new ArrayList<>();
        int inactiveNodeCount = 0;
        for (Entry<NodeID, Integer> node : m_watchedNodes.entrySet()) {
            NodeContainer nc = wfm.findNodeContainer(node.getKey());
            if (((NativeNodeContainer)nc).getNode().isInactive()) {
                inactiveNodeCount++;
            } else if (nc.getNodeTimer().getNrExecsSinceStart() > node.getValue()) {
                executedNodes.add(getExecutedNodeStatistics(nc, nc.getNodeTimer().getNrExecsSinceStart()));
            } else if (nc.getNodeContainerState().isExecutionInProgress()) {
                executingNodes.add(getExecutingNodeStatistics(nc));
            }
        }
        int executionsCount = m_watchedNodes.size() - inactiveNodeCount;
        long totalExecutionTime = m_executionEndTime == null ? System.currentTimeMillis() - m_executionStartTime
            : m_executionEndTime - m_executionStartTime;
        //sort with respect to the node's end (executed nodes) and start (executing nodes) time
        executedNodes.sort((p1, p2) -> Long.compare(p1.getFirst(), p2.getFirst()));
        executingNodes.sort((p1, p2) -> Long.compare(p1.getFirst(), p2.getFirst()));
        return builder(ExecutionStatisticsEntBuilder.class)
            .setNodesExecuted(executedNodes.stream().map(Pair::getSecond).collect(Collectors.toList()))
            .setNodesExecuting(executingNodes.stream().map(Pair::getSecond).collect(Collectors.toList()))
            .setTotalExecutionDuration(BigDecimal.valueOf(totalExecutionTime))
            .setWizardExecutionState(com.knime.gateway.entity.ExecutionStatisticsEnt.WizardExecutionStateEnum
                .valueOf(getWizardExecutionState(wfm)))
            .setTotalNodeExecutionsCount(executionsCount).build();
    }

    /*
     * Helper to get the details of an executing node.
     */
    private Pair<Long, NodeExecutingStatisticsEnt> getExecutingNodeStatistics(final NodeContainer nc) {
        long startTime = nc.getNodeTimer().getStartTime();
        long executionDuration = System.currentTimeMillis() - startTime;
        NodeExecutingStatisticsEntBuilder nodeStats =
            builder(NodeExecutingStatisticsEntBuilder.class).setName(nc.getName()).setNodeID(nc.getID().toString())
                .setExecutionDuration(BigDecimal.valueOf(executionDuration));
        if (nc.getProgressMonitor() != null && nc.getProgressMonitor().getProgress() != null) {
            nodeStats.setProgress(BigDecimal.valueOf(nc.getProgressMonitor().getProgress()));
        }
        if (!nc.getNodeAnnotation().getText().isEmpty()) {
            nodeStats.setAnnotation(nc.getNodeAnnotation().getText());
        }
        return Pair.create(startTime, nodeStats.build());
    }

    /*
     * Helper to get the details of an executed node.
     */
    private Pair<Long, NodeExecutedStatisticsEnt> getExecutedNodeStatistics(final NodeContainer nc, final int runs) {
        NodeTimer nodeTimer = nc.getNodeTimer();
        NodeExecutedStatisticsEntBuilder nodeStats =
            builder(NodeExecutedStatisticsEntBuilder.class).setName(nc.getName()).setNodeID(nc.getID().toString())
                .setExecutionDuration(BigDecimal.valueOf(nodeTimer.getLastExecutionDuration()))
                .setRuns(BigDecimal.valueOf(runs));
        if (!nc.getNodeAnnotation().getText().isEmpty()) {
            nodeStats.setAnnotation(nc.getNodeAnnotation().getText());
        }
        return Pair.create(nodeTimer.getStartTime() + nodeTimer.getLastExecutionDuration(), nodeStats.build());
    }

    /*
     * Keeps track of start and end time of workflow execution to be able to return the total execution duration in
     * between wizard pages (returned at {@link #getExecutionStatistics(UUID)}).
     */
    private void timeWorkflowExecution(final WorkflowManager wfm) {
        m_executionStartTime = System.currentTimeMillis();
        NodeStateChangeListener listener = new NodeStateChangeListener() {

            @Override
            public void stateChanged(final NodeStateEvent state) {
                if (isWfmDone(wfm)) {
                    m_executionEndTime = System.currentTimeMillis();
                    wfm.removeNodeStateChangeListener(this);
                }
            }
        };
        wfm.addNodeStateChangeListener(listener);
    }

    /*
     * Helper to determine whether the workflow execution has already been started.
     */
    private static boolean hasWorkflowExecutionStarted(final WorkflowManager wfm) {
        //is there a better way?
        return wfm.getNodeContainers().stream().anyMatch(n -> n.getNodeTimer().getNrExecsSinceStart() > 0);
    }

}
