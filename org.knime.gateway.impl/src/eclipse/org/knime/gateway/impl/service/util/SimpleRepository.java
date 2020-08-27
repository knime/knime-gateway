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
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Diff;
import org.knime.core.util.LRUCache;
import org.knime.core.util.Pair;
import org.knime.gateway.api.entity.AnnotationIDEnt;
import org.knime.gateway.api.entity.ConnectionIDEnt;
import org.knime.gateway.api.entity.GatewayEntity;
import org.knime.gateway.api.entity.NodeIDEnt;

/**
 * Straightforward repository implementation that just keeps every single snapshot as is and sacrifices memory (and a
 * limited history) for speed.
 *
 * For workflow entity comparison (i.e. to create the diff), this implementation uses the javers-library.
 *
 * NOTE: not a thread-safe implementation
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @param <K> the entity key
 * @param <E> the entity type
 */
public class SimpleRepository<K, E extends GatewayEntity> implements EntityRepository<K, E> {

    /* The default value of the maximum number of snapshots in memory */
    private static final int DEFAULT_MAX_NUM_SNAPSHOTS_IN_MEM = 500;

    /* maps snapshotID to workflow */
    private final LRUCache<UUID, E> m_snapshots;

    /* maps snapshotID to key */
    private final LRUCache<UUID, K> m_snapshotsKeyMap;

    /* maps key to <snapshotID, entity> */
    private final Map<K, Pair<UUID, E>> m_latestSnapshotPerEntity = new HashMap<>();

    private final Javers m_javers = JaversBuilder.javers().registerValue(NodeIDEnt.class)
        .registerValue(ConnectionIDEnt.class).registerValue(AnnotationIDEnt.class).build();

    /**
     * Creates a new instance. The maximum number of snapshots kept in memory is initialized with the default value.
     */
    public SimpleRepository() {
        this(DEFAULT_MAX_NUM_SNAPSHOTS_IN_MEM);
    }

    /**
     * Creates a new instance.
     *
     * @param maxNumSnapshotsInMem the maximum number of snapshots (i.e. history) kept in memory. If full, the least
     *            recently used will be removed.
     */
    public SimpleRepository(final int maxNumSnapshotsInMem) {
        m_snapshots = new LRUCache<>(maxNumSnapshotsInMem);
        m_snapshotsKeyMap = new LRUCache<>(maxNumSnapshotsInMem);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UUID commit(final K key, final E entity) {
        return commitInternal(key, entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <P> Optional<P> getChangesAndCommit(final UUID snapshotID, final E entity,
        final Function<UUID, PatchCreator<P>> patchCreator) {
        K key = m_snapshotsKeyMap.get(snapshotID);
        E snapshot = m_snapshots.get(snapshotID);
        if (key == null || snapshot == null) {
            throw new IllegalArgumentException("No workflow found for snapshot with ID '" + snapshotID + "'");
        }

        Diff diff = m_javers.compare(snapshot, entity);
        if (diff.hasChanges()) {
            //try committing the current vision since there might be changes
            //compared to the latest version in the repository (not necessarily)
            UUID newSnapshotID = commitInternal(key, entity);
            return Optional.of(m_javers.processChangeList(diff.getChanges(),
                new PatchChangeProcessor<P>(patchCreator.apply(newSnapshotID))));
        } else {
            return Optional.empty();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disposeHistory(final Predicate<K> keyFilter) {
        //remove all snapshots (and other map entries) for the given entity id
        List<UUID> snapshotIDs = m_snapshotsKeyMap.entrySet().stream().filter(e -> keyFilter.test(e.getValue()))
            .map(Entry::getKey).collect(Collectors.toList());
        snapshotIDs.forEach(s -> {
            m_snapshotsKeyMap.remove(s);
            m_snapshots.remove(s);
        });
        m_latestSnapshotPerEntity.entrySet().removeIf(e -> keyFilter.test(e.getKey()));
    }

    private UUID commitInternal(final K key, final E entity) {
        //look for the most recent commit for the given key
        Pair<UUID, E> latestSnapshot = m_latestSnapshotPerEntity.get(key); //NOSONAR
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
            m_latestSnapshotPerEntity.put(key, Pair.create(snapshotID, entity));
            m_snapshotsKeyMap.put(snapshotID, key);
        }
        return snapshotID;
    }
}
