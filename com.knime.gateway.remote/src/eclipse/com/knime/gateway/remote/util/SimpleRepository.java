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
import java.util.stream.Collectors;

import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Diff;
import org.knime.core.node.NodeLogger;
import org.knime.core.util.LRUCache;
import org.knime.core.util.Pair;

import com.knime.gateway.entity.EntityBuilderManager;
import com.knime.gateway.v0.entity.PatchEnt;
import com.knime.gateway.v0.entity.WorkflowEnt;
import com.knime.gateway.v0.entity.WorkflowSnapshotEnt;
import com.knime.gateway.v0.entity.WorkflowSnapshotEnt.WorkflowSnapshotEntBuilder;
import com.knime.gateway.v0.entity.impl.DefaultWorkflowEnt;

/**
 * Straightforward repository implementation that just keeps every single snapshot as is and sacrifices memory (and a
 * limited history) for speed.
 *
 * For workflow entity comparison (i.e. to create the diff), this implementation uses the javers-library.
 *
 * NOTE: not a thread-safe implementation
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class SimpleRepository implements WorkflowEntRepository {
    private static final NodeLogger LOGGER = NodeLogger.getLogger(SimpleRepository.class);

    /* The default value of the maximum number of snapshots in memory - can be set via a system property */
    private static final int DEFAULT_MAX_NUM_SNAPSHOTS_IN_MEM = 500;

    private final int maxNumSnapshotsInMem = getMaxNumSnapShotsInMem();

    /* maps snapshotID to workflow */
    private final LRUCache<UUID, WorkflowEnt> m_snapshots = new LRUCache<>(maxNumSnapshotsInMem);

    /* maps snapshotID to <workflowID, nodeID> */
    private final LRUCache<UUID, Pair<UUID, String>> m_snapshotsWorkflowMap = new LRUCache<>(maxNumSnapshotsInMem);

    /* maps <workflowID, nodeID> to <snapshotID, workflow> */
    private final Map<Pair<UUID, String>, Pair<UUID, WorkflowEnt>> m_latestSnapshotPerWorkflow = new HashMap<>();

    private final Javers m_javers = JaversBuilder.javers().build();

    private static int getMaxNumSnapShotsInMem() {
        String prop = System.getProperty("com.knime.enterprise.executor.jobview.max_num_snapshots_in_mem");
        if (prop != null) {
            try {
                return Integer.parseInt(prop);
            } catch (NumberFormatException e) {
                LOGGER.warn("Couldn't parse value for system property"
                    + " 'com.knime.enterprise.executor.jobview.max_num_snapshots_in_mem'");
            }
        }
        return DEFAULT_MAX_NUM_SNAPSHOTS_IN_MEM;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorkflowSnapshotEnt commit(final UUID workflowID, final String nodeID, final WorkflowEnt entity) {
        if (!(entity instanceof DefaultWorkflowEnt)) {
            throw new IllegalArgumentException("Repository only supports default entity implementations so far!");
        }
        Pair<UUID, String> wfKey = Pair.create(workflowID, nodeID);
        UUID snapshotID = commitInternal(wfKey, entity);
        return EntityBuilderManager.builder(WorkflowSnapshotEntBuilder.class).setWorkflow(entity)
            .setSnapshotID(snapshotID).build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PatchEnt getChangesAndCommit(final UUID snapshotID, final WorkflowEnt entity)
        throws IllegalArgumentException {
        Pair<UUID, String> wfKey = m_snapshotsWorkflowMap.get(snapshotID);
        WorkflowEnt snapshot = m_snapshots.get(snapshotID);
        if (wfKey == null || snapshot == null) {
            throw new IllegalArgumentException("No workflow found for snapshot with ID '" + snapshotID + "'");
        }

        Diff diff = m_javers.compare(snapshot, entity);
        if (diff.hasChanges()) {
            //try committing the current vision since there might be changes
            //compared to the latest version in the repository (not necessarily)
            UUID newSnapshotID = commitInternal(wfKey, entity);
            return m_javers.processChangeList(diff.getChanges(),
                new PatchEntChangeProcessor(newSnapshotID, entity.getTypeID()));
        } else {
            return PatchEntChangeProcessor.EMPTY_PATCH;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disposeHistory(final UUID workflowID) {
        //remove all snapshots (and other map entries) for the given workflow id
        List<UUID> snapshotIDs = m_snapshotsWorkflowMap.entrySet().stream()
            .filter(e -> e.getValue().getFirst().equals(workflowID)).map(e -> e.getKey()).collect(Collectors.toList());
        snapshotIDs.forEach(s -> {
            m_snapshotsWorkflowMap.remove(s);
            m_snapshots.remove(s);
        });
        m_latestSnapshotPerWorkflow.entrySet().removeIf(e -> e.getKey().getFirst().equals(workflowID));
    }

    private UUID commitInternal(final Pair<UUID, String> wfKey, final WorkflowEnt entity) {
        //look for the most recent commit for the given workflow- and nodeID combination
        Pair<UUID, WorkflowEnt> latestSnapshot = m_latestSnapshotPerWorkflow.get(wfKey);
        UUID snapshotID = null;
        if (latestSnapshot != null) {
            //only commit if there is a difference to the latest commit
            Diff diff = m_javers.compare(latestSnapshot.getSecond(), entity);
            if (!diff.hasChanges()) {
                //if there are no changes, use the last snapshot id and don't commit
                snapshotID = latestSnapshot.getFirst();
            }
        }

        //commit if necessary
        if (snapshotID == null) {
            snapshotID = UUID.randomUUID();
            m_snapshots.put(snapshotID, entity);
            m_latestSnapshotPerWorkflow.put(wfKey, Pair.create(snapshotID, entity));
            m_snapshotsWorkflowMap.put(snapshotID, wfKey);
        }
        return snapshotID;
    }
}
