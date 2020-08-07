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
import static com.knime.gateway.remote.service.util.DefaultServiceUtil.getWizardExecutionState;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.knime.core.node.workflow.FlowLoopContext;
import org.knime.core.node.workflow.FlowObjectStack;
import org.knime.core.node.workflow.NativeNodeContainer;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.NodeStateChangeListener;
import org.knime.core.node.workflow.NodeStateEvent;
import org.knime.core.node.workflow.NodeTimer;
import org.knime.core.node.workflow.SingleNodeContainer;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.node.workflow.WebResourceController;
import org.knime.core.node.workflow.WizardExecutionController;
import org.knime.core.node.workflow.WorkflowLock;
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

    /*
     * Set of node ids being watched (for execution statistics) between two different wizard page nodes
     * (or between source nodes and the first wizard page(s))
     */
    private final Set<NodeID> m_watchedNodeSuccessors = new HashSet<>();

    /*
     * Set of node ids being watches (for execution statistics) that are part of the (outermost) loop scope
     * the current wizard page is part of.
     */
    private final Set<NodeID> m_watchedNodesInLoop = new HashSet<>();

    /*
     * Adds a constant value to the node executions count.
     * Only role is to add the number of executions of the current wizard page in a loop twice.
     */
    private int m_extraNodeExecutionsCountInLoop = 0;

    /*
     * The number of executions for each node when the statistics have been 'reset' to a new wizard page.
     */
    private Map<NodeID, Integer> m_originalNodeExecutionCounts = new HashMap<>();

    private long m_executionStartTime;

    private Long m_executionEndTime = null;

    /**
     * Helper method to check whether the workflow is not executing anymore (either finished completely or halted at a
     * wizard page).
     *
     * @param wfm the workflow manager to check
     * @return <code>true</code> if the workflow execution is finished, otherwise <code>false</code>
     */
    public static boolean isWfmDone(final WorkflowManager wfm) {
        try (WorkflowLock lock = wfm.lock()) {
            return !wfm.getNodeContainerState().isExecutionInProgress() || isHaltedAtWizardPage(wfm);
        }
    }

    private static boolean isHaltedAtWizardPage(final WorkflowManager wfm) {
        if (wfm.isInWizardExecution()) {
            WizardExecutionController wec = wfm.getWizardExecutionController();
            return wec.hasCurrentWizardPage() && !wfm.getNodeContainer(wec.getCurrentWizardPageNodeID())
                .getNodeContainerState().isExecutionInProgress();
        } else {
            return false;
        }
    }

    /**
     * Resets the execution statistics to the given wizard page (represented by the node id). Only nodes between the
     * given wizard page and successor wizard pages are considered in the statistics.
     *
     * @param wizardPageNodeID the node id of the wizard page to reset the statistics to, must be a node representing a
     *            wizard page ({@link WebResourceController#isWizardPage(NodeID, WorkflowManager)})
     * @param wfm the workflow
     * @throws IllegalArgumentException if the node id doesn't represent a wizard page
     */
    public void resetStatisticsToWizardPage(final NodeID wizardPageNodeID, final WorkflowManager wfm) {
        if (wizardPageNodeID != null && !isWizardPage(wfm.getNodeContainer(wizardPageNodeID))) {
            throw new IllegalArgumentException("Not a wizard page: " + wizardPageNodeID);
        }
        timeWorkflowExecution(wfm);
        m_watchedNodeSuccessors.clear();
        m_originalNodeExecutionCounts.clear();
        m_watchedNodesInLoop.clear();
        m_extraNodeExecutionsCountInLoop = 0;

        try (WorkflowLock lock = wfm.lock()) {
            if (wizardPageNodeID == null) {
                // only include source nodes, 'source' wizard pages and the nodes in between
                wfm.getNodeContainers(getSourceNodes(wfm), getWizardPageStopCondition(wizardPageNodeID), true, false)
                    .forEach(nc -> addContainedNativeNodes(nc, m_watchedNodeSuccessors, m_originalNodeExecutionCounts));
            } else {
                // handle a wizard page in a loop
                // TODO multiple nested loops are not handled
                getLoopBodyOfOutermostLoop(wizardPageNodeID, wfm, m_watchedNodesInLoop, m_originalNodeExecutionCounts);
                Set<NodeID> tmp = new HashSet<>();
                addContainedNativeNodes(wfm.getNodeContainer(wizardPageNodeID), tmp, null);
                m_extraNodeExecutionsCountInLoop = tmp.size();

                // handle 'wizard page successor'
                wfm.getNodeContainers(Collections.singleton(wizardPageNodeID),
                    getWizardPageStopCondition(wizardPageNodeID), false, false)
                    .forEach(nc -> addContainedNativeNodes(nc, m_watchedNodeSuccessors, m_originalNodeExecutionCounts));
            }
        }
    }

    private Predicate<NodeContainer> getWizardPageStopCondition(final NodeID wizardPageNodeID) {
        return nc -> !nc.getID().equals(wizardPageNodeID) && isWizardPage(nc);
    }

    private static void getLoopBodyOfOutermostLoop(final NodeID wizardPageNodeID, final WorkflowManager wfm,
        final Set<NodeID> watchedNodesInLoop, final Map<NodeID, Integer> nodesExecutionCount) {
        NodeContainer wizardPage = wfm.getNodeContainer(wizardPageNodeID);
        FlowObjectStack fos = wizardPage.getFlowObjectStack();
        if (fos != null) {
            FlowLoopContext flc = null;
            for (Object o : fos) {
                if (o instanceof FlowLoopContext) {
                    flc = (FlowLoopContext)o;
                }
            }
            if (flc != null) {
                wfm.getNodesInScope((SingleNodeContainer)wfm.getNodeContainer(flc.getHeadNode())).forEach(nc -> {
                    addContainedNativeNodes(nc, watchedNodesInLoop, nodesExecutionCount);
                });
            }
        }
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
     * Check whether a node represents a wizard page.
     */
    private static boolean isWizardPage(final NodeContainer nc) {
        return WebResourceController.isWizardPage(nc.getID(), nc.getParent());
    }

    /*
     * Recursively adds all (deeply) contained native nodes to the provided list.
     */
    private static void addContainedNativeNodes(final NodeContainer nc, final Set<NodeID> nativeNodes,
        final Map<NodeID, Integer> executionsCount) {
        if (nc instanceof SubNodeContainer) {
            addContainedNativeNodes(((SubNodeContainer)nc).getWorkflowManager(), nativeNodes, executionsCount);
        } else if (nc instanceof WorkflowManager) {
            addContainedNativeNodes((WorkflowManager)nc, nativeNodes, executionsCount);
        } else {
            nativeNodes.add(nc.getID());
            if (executionsCount != null) {
                executionsCount.put(nc.getID(), nc.getNodeTimer().getNrExecsSinceStart());
            }
        }
    }

    /*
     * Recursively adds all (deeply) contained native nodes to the provided list.
     */
    private static void addContainedNativeNodes(final WorkflowManager wfm, final Set<NodeID> nativeNodes,
        final Map<NodeID, Integer> executionsCount) {
        for (NodeContainer nc : wfm.getNodeContainers()) {
            addContainedNativeNodes(nc, nativeNodes, executionsCount);
        }
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
        Pair<Set<NodeID>, Integer> watchedNodes;
        try (WorkflowLock lock = wfm.lock()) {
            watchedNodes = selectWatchedNodes(wfm);
            for (NodeID node : watchedNodes.getFirst()) {
                NodeContainer nc = wfm.findNodeContainer(node);
                if (((NativeNodeContainer)nc).getNode().isInactive()) {
                    inactiveNodeCount++;
                } else if (nc.getNodeTimer().getNrExecsSinceStart() > m_originalNodeExecutionCounts.get(node)) {
                    // node's 'executed' state is determined by comparing the original number of executions vs.
                    // the current number of executions -> if it has increased, the node has been executed at
                    // least once
                    executedNodes.add(getExecutedNodeStatistics(nc, nc.getNodeTimer().getNrExecsSinceStart()));
                } else if (nc.getNodeContainerState().isExecutionInProgress()) {
                    executingNodes.add(getExecutingNodeStatistics(nc));
                }
            }
        }

        int totalExpectedNodeExecutionsCount =
            watchedNodes.getFirst().size() - inactiveNodeCount + watchedNodes.getSecond();
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
            .setTotalNodeExecutionsCount(totalExpectedNodeExecutionsCount).build();
    }

    /*
     * Selects the node set to be watched atm. There are two choices:
     * 1. the nodes in the scope of the outermost loop of the current wizard page (if there is any)
     * 2. the nodes between this wizard page and the next one
     *
     * If wizard page is part of a loop which is still in execution, the nodes in the scope are returned (1).
     * Otherwise the node 'successors' (2) .
     */
    private Pair<Set<NodeID>, Integer> selectWatchedNodes(final WorkflowManager wfm) {
        if (!m_watchedNodesInLoop.isEmpty() && isLoopInProgress(m_watchedNodesInLoop, wfm)) {
            return Pair.create(m_watchedNodesInLoop, m_extraNodeExecutionsCountInLoop);
        }
        m_watchedNodesInLoop.clear();
        return Pair.create(m_watchedNodeSuccessors, 0);
    }

    /*
     * Helper to determine whether a loop is still in progress (i.e. still looping).
     */
    private static boolean isLoopInProgress(final Set<NodeID> nodesInScope, final WorkflowManager wfm) {
        // loop executing is finished if all nodes are executed
        for (NodeID node : nodesInScope) {
            if (!wfm.findNodeContainer(node).getNodeContainerState().isExecuted()) {
                return true;
            }
        }
        return false;
    }

    /*
     * Helper to get the details of an executing node.
     */
    private static Pair<Long, NodeExecutingStatisticsEnt> getExecutingNodeStatistics(final NodeContainer nc) {
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
    private static Pair<Long, NodeExecutedStatisticsEnt> getExecutedNodeStatistics(final NodeContainer nc,
        final int runs) {
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

}
