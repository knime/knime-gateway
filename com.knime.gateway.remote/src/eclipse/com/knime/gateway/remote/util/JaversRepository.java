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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.commit.Commit;
import org.javers.core.commit.CommitId;
import org.javers.core.diff.Change;
import org.javers.core.metamodel.annotation.Id;
import org.javers.repository.jql.JqlQuery;
import org.javers.repository.jql.QueryBuilder;
import org.javers.shadow.Shadow;
import org.knime.core.node.NodeLogger;
import org.knime.core.util.Pair;

import com.knime.gateway.entity.EntityBuilderManager;
import com.knime.gateway.v0.entity.PatchEnt;
import com.knime.gateway.v0.entity.WorkflowEnt;
import com.knime.gateway.v0.entity.WorkflowSnapshotEnt;
import com.knime.gateway.v0.entity.WorkflowSnapshotEnt.WorkflowSnapshotEntBuilder;
import com.knime.gateway.v0.entity.impl.DefaultWorkflowEnt;

/**
 * Implementation of the {@link WorkflowEntRepository} using {@link Javers}. Current implementation uses a in-memory
 * javers repository. However, javers also provides a repository backed by a DB.
 *
 * NOTE: for simplicity the javers repository for now assumes the default implementations of the entities (i.e.
 * DefaultWorkflowEnt) and will fail otherwise.
 *
 * NOTE: it turned out that querying for snapshots ({@link #getSnapshot(UUID)} is slowed down considerably the more
 * commits are there. Thus the simpler {@link SimpleRepository}-implementation.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class JaversRepository implements WorkflowEntRepository {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(JaversRepository.class);


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

        if (!changes.isEmpty()) {
            //try committing the current version of the workflow entity if there are changes
            //(doesn't mean that there are changes relative to the last commit)
            UUID newSnapshotID = commitInternal(snapshotMeta.getRootWorkflowID(), snapshotMeta.getNodeID(), entity);

            // create patch ent
            return repo.processChangeList(changes, new PatchEntChangeProcessor(newSnapshotID, entity.getTypeID()));
        } else {
            return PatchEntChangeProcessor.EMPTY_PATCH;
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
        return m_repos.computeIfAbsent(workflowID, id -> JaversBuilder.javers().build());
    }

    private static String getNonNullNodeID(final String nodeID) {
        return nodeID == null ? "" : nodeID;
    }

    private void logStats() {
        LOGGER.info("WorkflowEnt Repo Stats: " + m_repos.size() + "  repositories");
        LOGGER.info("WorkflowEnt Repo Stats: " + m_snapshotMetadataMap.size() + "  snapshots/shadows");
    }

    private static class WorkflowEntWrapper {
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

    private static class SnapshotMetadata {
        private final UUID m_rootWorkflowID;

        private final String m_nodeID;

        private final CommitId m_commitID;

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
}
