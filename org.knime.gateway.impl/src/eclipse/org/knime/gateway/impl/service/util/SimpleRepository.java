/*
 * ------------------------------------------------------------------------
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
 */
package org.knime.gateway.impl.service.util;

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
import org.knime.gateway.api.entity.EntityBuilderManager;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.entity.PatchEnt;
import org.knime.gateway.api.entity.WorkflowEnt;
import org.knime.gateway.api.entity.WorkflowSnapshotEnt;
import org.knime.gateway.api.entity.WorkflowSnapshotEnt.WorkflowSnapshotEntBuilder;
import org.knime.gateway.impl.entity.DefaultWorkflowEnt;

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
    private final LRUCache<UUID, Pair<UUID, NodeIDEnt>> m_snapshotsWorkflowMap = new LRUCache<>(maxNumSnapshotsInMem);

    /* maps <workflowID, nodeID> to <snapshotID, workflow> */
    private final Map<Pair<UUID, NodeIDEnt>, Pair<UUID, WorkflowEnt>> m_latestSnapshotPerWorkflow = new HashMap<>();

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
    public WorkflowSnapshotEnt commit(final UUID workflowID, final NodeIDEnt nodeID, final WorkflowEnt entity) {
        if (!(entity instanceof DefaultWorkflowEnt)) {
            throw new IllegalArgumentException("Repository only supports default entity implementations so far!");
        }
        Pair<UUID, NodeIDEnt> wfKey = Pair.create(workflowID, nodeID);
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
        Pair<UUID, NodeIDEnt> wfKey = m_snapshotsWorkflowMap.get(snapshotID);
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

    private UUID commitInternal(final Pair<UUID, NodeIDEnt> wfKey, final WorkflowEnt entity) {
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
