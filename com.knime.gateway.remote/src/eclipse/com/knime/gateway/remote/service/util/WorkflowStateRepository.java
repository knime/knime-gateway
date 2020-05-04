/* ------------------------------------------------------------------
 * This source code, its documentation and all appendant files
 * are protected by copyright law. All rights reserved.
 *
 * Copyright by KNIME AG, Zurich, Switzerland
 *
 * You may not modify, publish, transmit, transfer or sell, reproduce,
 * create derivative works from, distribute, perform, display, or in
 * any way exploit any of the content, in whole or in part, except as
 * otherwise expressly permitted in writing by the copyright owner or
 * as specified in the license file distributed with this product.
 *
 * If you have any questions please contact the copyright holder:
 * website: www.knime.com
 * email: contact@knime.com
 * ---------------------------------------------------------------------
 *
 */
package com.knime.gateway.remote.service.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.changelog.ChangeProcessor;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.diff.Change;
import org.javers.core.diff.Diff;
import org.javers.core.diff.changetype.NewObject;
import org.javers.core.diff.changetype.ObjectRemoved;
import org.javers.core.diff.changetype.PropertyChange;
import org.javers.core.diff.changetype.ReferenceChange;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.diff.changetype.container.ArrayChange;
import org.javers.core.diff.changetype.container.ContainerChange;
import org.javers.core.diff.changetype.container.ListChange;
import org.javers.core.diff.changetype.container.SetChange;
import org.javers.core.diff.changetype.map.MapChange;
import org.javers.core.metamodel.object.GlobalId;
import org.knime.core.node.workflow.NativeNodeContainer;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.node.workflow.WorkflowManager;

/**
 * Utility class to save and compare workflow states (including nested sub-workflows).
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @since 4.11
 */
public class WorkflowStateRepository {

    private final Map<UUID, List<WorkflowStateSnapshot>> m_executionSnapshots = new HashMap<>();

    private Javers m_javers = JaversBuilder.javers().build();

    /**
     * Creates and keeps a snapshot of the workflow state (i.e. essentially the states of the contained nodes) including
     * all sub-workflows.
     *
     * @param workflowId
     * @param wfm
     */
    public void addDeepWorkflowStateSnapshot(final UUID workflowId, final WorkflowManager wfm) {
        m_executionSnapshots.put(workflowId, createDeepWorkflowStateSnapshot(wfm));
    }

    private List<WorkflowStateSnapshot> createDeepWorkflowStateSnapshot(final WorkflowManager rootWfm) {
        List<WorkflowStateSnapshot> snapshots = new ArrayList<>();
        snapshots.add(createWorkflowStateSnapshot(rootWfm));
        List<WorkflowManager> subWorkflows = new ArrayList<>();
        getAllSubWorkflows(rootWfm, subWorkflows);
        for (WorkflowManager wfm : subWorkflows) {
            snapshots.add(createWorkflowStateSnapshot(wfm));
        }
        return snapshots;
    }

    /**
     * @param workflowId the workflow id
     * @return <code>true</code> if there is a snapshot for the workflow with the given id
     */
    public boolean containsWorkflowStateSnapshotFor(final UUID workflowId) {
        return m_executionSnapshots.containsKey(workflowId);
    }

    /**
     * Removes all snapshots of the workflow with the given id.
     *
     * @param workflowId the workflow id
     */
    public void removeWorkflowStateSnapshot(final UUID workflowId) {
        m_executionSnapshots.remove(workflowId);
    }

    private WorkflowStateSnapshot createWorkflowStateSnapshot(final WorkflowManager wfm) {
        List<NodeStateSnapshot> nodes = new ArrayList<>();
        for (NodeContainer nc : wfm.getNodeContainers()) {
            if (nc instanceof NativeNodeContainer) {
                nodes.add(new NodeStateSnapshot(nc.getID(), nc.getNodeContainerState().toString(),
                    nc.getNodeTimer().getNrExecsSinceStart()));
            }
        }
        return new WorkflowStateSnapshot(nodes);
    }

    private void getAllSubWorkflows(final WorkflowManager wfm, final List<WorkflowManager> subWorkflowIds) {
        for (NodeContainer nc : wfm.getNodeContainers()) {
            if (nc instanceof WorkflowManager) {
                WorkflowManager subwfm = (WorkflowManager)nc;
                subWorkflowIds.add(subwfm);
                getAllSubWorkflows(subwfm, subWorkflowIds);
            } else if (nc instanceof SubNodeContainer) {
                WorkflowManager subwfm = ((SubNodeContainer)nc).getWorkflowManager();
                subWorkflowIds.add(subwfm);
                getAllSubWorkflows(subwfm, subWorkflowIds);
            }
        }
    }

    /**
     * Compares the snapshot state kept for the given workflow id with the state of the passed workflow.
     *
     * @param workflowId reference to the kept workflow state
     * @param wfm the new workflow used to compare
     * @param changes callback to process the state differences
     */
    public void compareStates(final UUID workflowId, final WorkflowManager wfm,
        final NodeStateChangeProcessor changes) {
        if (!containsWorkflowStateSnapshotFor(workflowId)) {
            return;
        }

        List<WorkflowStateSnapshot> initSnapshots = m_executionSnapshots.get(workflowId);
        List<WorkflowStateSnapshot> newSnapshots = createDeepWorkflowStateSnapshot(wfm);
        for (int i = 0; i < initSnapshots.size(); i++) {
            Diff diff = m_javers.compare(initSnapshots.get(i), newSnapshots.get(i));
            m_javers.processChangeList(diff.getChanges(), new ChangeProcessor<Object>() {

                @Override
                public void onCommit(final CommitMetadata commitMetadata) {
                }

                @Override
                public void onAffectedObject(final GlobalId globalId) {
                }

                @Override
                public void beforeChangeList() {
                }

                @Override
                public void afterChangeList() {
                }

                @Override
                public void beforeChange(final Change change) {
                }

                @Override
                public void afterChange(final Change change) {
                }

                @Override
                public void onPropertyChange(final PropertyChange propertyChange) {
                }

                @Override
                public void onValueChange(final ValueChange valueChange) {
                    NodeStateSnapshot nss = (NodeStateSnapshot)valueChange.getAffectedObject().get();
                    if ("state".equals(valueChange.getPropertyName()) && "EXECUTING".equals(nss.state)) {
                        NodeContainer nc = wfm.findNodeContainer(nss.nodeID);
                        changes.nodeExecuting(nc);
                    } else if ("numExec".equals(valueChange.getPropertyName())) {
                        NodeContainer nc = wfm.findNodeContainer(nss.nodeID);
                        changes.nodeExecuted(nc, nss.numExec);
                    }
                }

                @Override
                public void onReferenceChange(final ReferenceChange referenceChange) {
                }

                @Override
                public void onNewObject(final NewObject newObject) {
                }

                @Override
                public void onObjectRemoved(final ObjectRemoved objectRemoved) {
                }

                @Override
                public void onContainerChange(final ContainerChange containerChange) {
                }

                @Override
                public void onSetChange(final SetChange setChange) {
                }

                @Override
                public void onArrayChange(final ArrayChange arrayChange) {
                }

                @Override
                public void onListChange(final ListChange listChange) {
                }

                @Override
                public void onMapChange(final MapChange mapChange) {
                }

                @Override
                public Object result() {
                    return null;
                }
            });
        }
    }

    private class WorkflowStateSnapshot {
        @SuppressWarnings("unused")
        private List<NodeStateSnapshot> nodes;

        @SuppressWarnings("hiding")
        private WorkflowStateSnapshot(final List<NodeStateSnapshot> nodes) {
            this.nodes = nodes;
        }
    }

    private class NodeStateSnapshot {
        private String state;

        private Integer numExec;

        private NodeID nodeID;

        @SuppressWarnings("hiding")
        private NodeStateSnapshot(final NodeID nodeID, final String state, final Integer numExec) {
            this.state = state;
            this.numExec = numExec;
            this.nodeID = nodeID;
        }
    }

    /**
     * Callbacks for state difference processing in
     * {@link WorkflowStateRepository#compareStates(UUID, WorkflowManager, NodeStateChangeProcessor)}.
     */
    public interface NodeStateChangeProcessor {
        /**
         * If node state changed to 'executing'.
         *
         * @param nc the affected node container
         */
        void nodeExecuting(NodeContainer nc);

        /**
         * If the number of how often the node has been executed changed
         *
         * @param nc the affected node container
         * @param runs the number of executions since the workflow start
         */
        void nodeExecuted(NodeContainer nc, long runs);
    }
}
