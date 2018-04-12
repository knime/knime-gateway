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
package com.knime.gateway.remote.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.changelog.ChangeProcessor;
import org.javers.core.commit.Commit;
import org.javers.core.commit.CommitId;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.diff.Change;
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
import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.object.UnboundedValueObjectId;
import org.javers.core.metamodel.object.ValueObjectId;
import org.javers.repository.jql.JqlQuery;
import org.javers.repository.jql.QueryBuilder;
import org.javers.shadow.Shadow;
import org.knime.core.node.NodeLogger;
import org.knime.core.util.Pair;

import com.knime.gateway.entity.EntityBuilderManager;
import com.knime.gateway.v0.entity.PatchEnt;
import com.knime.gateway.v0.entity.PatchEnt.PatchEntBuilder;
import com.knime.gateway.v0.entity.PatchOpEnt;
import com.knime.gateway.v0.entity.PatchOpEnt.OpEnum;
import com.knime.gateway.v0.entity.WorkflowEnt;
import com.knime.gateway.v0.entity.WorkflowSnapshotEnt;
import com.knime.gateway.v0.entity.WorkflowSnapshotEnt.WorkflowSnapshotEntBuilder;
import com.knime.gateway.v0.entity.impl.DefaultPatchEnt.DefaultPatchEntBuilder;
import com.knime.gateway.v0.entity.impl.DefaultPatchOpEnt.DefaultPatchOpEntBuilder;
import com.knime.gateway.v0.entity.impl.DefaultWorkflowEnt;

/**
 * Implementation of the {@link WorkflowEntRepository} using {@link Javers}. Current implementation uses a in-memory
 * javers repository. However, javers also provides a repository backed by a DB.
 *
 * NOTE: for simplicity the javers repository for now assumes the default implementations of the entities (i.e.
 * DefaultWorkflowEnt) and will fail otherwise.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class JaversRepository implements WorkflowEntRepository {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(JaversRepository.class);

    private static final PatchEnt EMPTY_PATCH = EntityBuilderManager.builder(PatchEntBuilder.class).build();

    // one javers repository per workflow id
    private final Map<UUID, Javers> m_repos = new HashMap<UUID, Javers>();

    private final Map<UUID, SnapshotMetadata> m_snapshotMetadataMap = new HashMap<>();

    private final Map<Pair<UUID, String>, UUID> m_latestSnapshotIDs = new HashMap<>();

    @Override
    public WorkflowSnapshotEnt commit(final UUID workflowID, final String nodeID, final WorkflowEnt entity) {
        if (!(entity instanceof DefaultWorkflowEnt)) {
            throw new IllegalArgumentException(
                "Javers repository only supports default entity implementations so far!");
        }
        UUID snapshotID = commitInternal(workflowID, nodeID, entity);
        logStats();
        return EntityBuilderManager.builder(WorkflowSnapshotEntBuilder.class).setWorkflow(entity)
            .setSnapshotID(snapshotID).build();
    }

    @Override
    public PatchEnt getChangesAndCommit(final UUID snapshotID, final WorkflowEnt entity) {
        if (!m_snapshotMetadataMap.containsKey(snapshotID)) {
            throw new IllegalArgumentException("No workflow found for snapshot with ID '" + snapshotID + "'");
        }

        WorkflowEnt snapshot = getSnapshot(snapshotID);
        SnapshotMetadata snapshotMeta = m_snapshotMetadataMap.get(snapshotID);
        Javers repo = m_repos.get(snapshotMeta.getRootWorkflowID());
        assert repo != null;

        //TODO is there a way to get (all) changes directly from the repo?
        List<Change> changes = repo.compare(snapshot, entity).getChanges();
        logStats();

        if (changes.size() > 0) {
            //try committing the current version of the workflow entity if there are changes
            //(doesn't mean that there are changes relative to the last commit)
            UUID newSnapshotID = commitInternal(snapshotMeta.getRootWorkflowID(), snapshotMeta.getNodeID(), entity);

            // create patch ent
            return repo.processChangeList(changes, new PatchEntChangeProcessor(newSnapshotID, entity.getTypeID()));
        } else {
            return EMPTY_PATCH;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disposeHistory(final UUID workflowID) {
        m_repos.remove(workflowID);

        // clear all snapshotIDs entries for this particular workflow id
        m_snapshotMetadataMap.entrySet().removeIf(e -> e.getValue().getRootWorkflowID().equals(workflowID));
        m_latestSnapshotIDs.entrySet().removeIf(e -> e.getKey().getFirst().equals(workflowID));
    }

    private UUID commitInternal(final UUID workflowID, final String nodeID, final WorkflowEnt entity) {
        Commit commit = getOrCreateRepo(workflowID).commit("knime",
            new WorkflowEntWrapper(getNonNullNodeID(nodeID), (DefaultWorkflowEnt)entity));
        UUID snapshotID;
        if (commit.getSnapshots().isEmpty()) {
            //there aren't any changes -> get the latest snapshot id
            snapshotID = m_latestSnapshotIDs.get(Pair.create(workflowID, getNonNullNodeID(nodeID)));
        } else {
            //create a new snapshot id and keep track of it
            snapshotID = UUID.randomUUID();
            m_snapshotMetadataMap.put(snapshotID,
                new SnapshotMetadata(workflowID, getNonNullNodeID(nodeID), commit.getId()));
            m_latestSnapshotIDs.put(Pair.create(workflowID, getNonNullNodeID(nodeID)), snapshotID);
        }
        return snapshotID;
    }

    private WorkflowEnt getSnapshot(final UUID snapshotID) {
        SnapshotMetadata snapshotMeta = m_snapshotMetadataMap.get(snapshotID);
        JqlQuery query = QueryBuilder.byInstanceId(snapshotMeta.getNodeID(), WorkflowEntWrapper.class)
            .withCommitId(snapshotMeta.getCommitID()).build();
        List<Shadow<Object>> shadows = m_repos.get(snapshotMeta.getRootWorkflowID()).findShadows(query);
        //for some unknown reason there are always two shadows (the first and the requested one)
        assert shadows.size() < 3;
        return ((WorkflowEntWrapper)shadows.get(0).get()).get();
    }

    private Javers getOrCreateRepo(final UUID workflowID) {
        return m_repos.computeIfAbsent(workflowID, id -> {
            return JaversBuilder.javers().build();
        });
    }

    private String getNonNullNodeID(final String nodeID) {
        return nodeID == null ? "" : nodeID;
    }

    private void logStats() {
        LOGGER.info("WorkflowEnt Repo Stats: " + m_repos.size() + "  repositories");
        LOGGER.info("WorkflowEnt Repo Stats: " + m_snapshotMetadataMap.size() + "  snapshots/shadows");
    }

    private class WorkflowEntWrapper {

        private final DefaultWorkflowEnt m_ent;

        @Id
        private final String m_workflowID;

        public WorkflowEntWrapper(final String workflowID, final DefaultWorkflowEnt ent) {
            m_workflowID = workflowID;
            m_ent = ent;
        }

        public DefaultWorkflowEnt get() {
            return m_ent;
        }
    }

    private class SnapshotMetadata {

        private UUID m_rootWorkflowID;

        private String m_nodeID;

        private CommitId m_commitID;

        public SnapshotMetadata(final UUID rootWorkflowID, final String nodeID, final CommitId commitID) {
            m_rootWorkflowID = rootWorkflowID;
            m_nodeID = nodeID;
            m_commitID = commitID;
        }

        public UUID getRootWorkflowID() {
            return m_rootWorkflowID;
        }

        public String getNodeID() {
            return m_nodeID;
        }

        public CommitId getCommitID() {
            return m_commitID;
        }
    }

    private class PatchEntChangeProcessor implements ChangeProcessor<PatchEnt> {

        private List<PatchOpEnt> m_ops = new ArrayList<PatchOpEnt>();

        private UUID m_newSnapshotID;

        private String m_targetTypeID;

        public PatchEntChangeProcessor(final UUID newSnapshotID, final String targetTypeID) {
            m_newSnapshotID = newSnapshotID;
            m_targetTypeID = targetTypeID;
        }

        @Override
        public void onCommit(final CommitMetadata commitMetadata) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onAffectedObject(final GlobalId globalId) {
            // TODO Auto-generated method stub
        }

        @Override
        public void beforeChangeList() {
            // TODO Auto-generated method stub

        }

        @Override
        public void afterChangeList() {
            // TODO Auto-generated method stub

        }

        @Override
        public void beforeChange(final Change change) {
            // TODO Auto-generated method stub

        }

        @Override
        public void afterChange(final Change change) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPropertyChange(final PropertyChange propertyChange) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onValueChange(final ValueChange valueChange) {
            GlobalId globalId = valueChange.getAffectedGlobalId();
            String path = "";
            if (globalId instanceof ValueObjectId) {
                path = "/" + ((ValueObjectId)globalId).getFragment().replaceAll("m_", "");
                path += "/" + valueChange.getPropertyName().replace("m_", "");
            }
            if (globalId instanceof UnboundedValueObjectId) {
                path = "/" + valueChange.getPropertyName().replace("m_", "");
            }
            m_ops.add(new DefaultPatchOpEntBuilder().setOp(OpEnum.REPLACE).setPath(path)
                .setValue(valueChange.getRight()).build());
        }

        @Override
        public void onReferenceChange(final ReferenceChange referenceChange) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onNewObject(final NewObject newObject) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onObjectRemoved(final ObjectRemoved objectRemoved) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onContainerChange(final ContainerChange containerChange) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onSetChange(final SetChange setChange) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onArrayChange(final ArrayChange arrayChange) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onListChange(final ListChange listChange) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onMapChange(final MapChange mapChange) {
            // TODO Auto-generated method stub

        }

        @Override
        public PatchEnt result() {
            return new DefaultPatchEntBuilder().setOps(m_ops).setSnapshotID(m_newSnapshotID)
                .setTargetTypeID(m_targetTypeID).build();
        }
    }
}
