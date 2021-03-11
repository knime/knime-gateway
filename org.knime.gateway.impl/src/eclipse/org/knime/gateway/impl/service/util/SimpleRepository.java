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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Diff;
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
    private static final int DEFAULT_MAX_NUM_SNAPSHOTS_PER_ENTITY_IN_MEM = 500;

    /* maps snapshotID to key */
    private final Map<String, K> m_snapshotsKeyMap;

    /* maps key to <snapshotID, entity> */
    private final Map<K, Pair<String, E>> m_latestSnapshotPerEntity = new HashMap<>();

    /* maps key to entity history (lru-cache of <snapshotID, entity>) */
    private final Map<K, LRUCache> m_historyPerEntity = new HashMap<>();

    private final Javers m_javers =
        JaversBuilder.javers().registerValue(NodeIDEnt.class).registerValue(ConnectionIDEnt.class)
            .registerValue(AnnotationIDEnt.class).withNewObjectsSnapshot(false).build();

    private final Supplier<String> m_snapshotIdGenerator;

    private final int m_maxNumSnapshotsPerEntity;

    /**
     * Creates a new instance. The maximum number of snapshots kept in memory is initialized with the default value.
     */
    public SimpleRepository() {
        this(DEFAULT_MAX_NUM_SNAPSHOTS_PER_ENTITY_IN_MEM);
    }

    /**
     * Creates a new instance.
     *
     * @param maxNumSnapshotsPerEntity the maximum number of snapshots (i.e. history) kept in memory. If full, the
     *            least recently used will be removed.
     */
    public SimpleRepository(final int maxNumSnapshotsPerEntity) {
        this(maxNumSnapshotsPerEntity, () -> UUID.randomUUID().toString());
    }

    /**
     * Creates a new instance.
     *
     * @param maxNumSnapshotsPerEntity the maximum number of snapshots (i.e. history) per entity kept in memory. If
     *            full, the least recently used will be removed.
     * @param snapshotIdGenerator supplier that generates unique ids
     */
    public SimpleRepository(final int maxNumSnapshotsPerEntity, final Supplier<String> snapshotIdGenerator) {
        m_maxNumSnapshotsPerEntity = maxNumSnapshotsPerEntity;
        m_snapshotsKeyMap = new HashMap<>();
        m_snapshotIdGenerator = snapshotIdGenerator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String commit(final K key, final E entity) {
        return commitInternal(key, entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <P> Optional<P> getChangesAndCommit(final String snapshotID, final E entity,
        final PatchCreator<P> patchCreator) {
        K key = m_snapshotsKeyMap.get(snapshotID);
        LRUCache entityHistory = m_historyPerEntity.get(key);
        E snapshot = null;
        if (entityHistory != null) {
            snapshot = entityHistory.get(snapshotID);
        }
        if (key == null || snapshot == null) {
            throw new IllegalArgumentException("No workflow found for snapshot with ID '" + snapshotID + "'");
        }

        Diff diff = m_javers.compare(snapshot, entity);
        if (diff.hasChanges()) {
            //try committing the current vision since there might be changes
            //compared to the latest version in the repository (not necessarily)
            String newSnapshotID = commitInternal(key, entity);
            return Optional.of(m_javers.processChangeList(diff.getChanges(),
                new PatchChangeProcessor<P>(patchCreator, newSnapshotID)));
        } else {
            return Optional.empty();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Pair<String, E>> getLastCommit(final K key) {
        return Optional.ofNullable(m_latestSnapshotPerEntity.get(key));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disposeHistory(final Predicate<K> keyFilter) {
        //remove all snapshots (and other map entries) for the given entity id
        List<String> snapshotIDs = m_snapshotsKeyMap.entrySet().stream().filter(e -> keyFilter.test(e.getValue()))
            .map(Entry::getKey).collect(Collectors.toList());
        snapshotIDs.forEach(m_snapshotsKeyMap::remove);
        m_latestSnapshotPerEntity.entrySet().removeIf(e -> keyFilter.test(e.getKey()));
        m_historyPerEntity.entrySet().removeIf(e -> keyFilter.test(e.getKey()));
    }

    private String commitInternal(final K key, final E entity) {
        //look for the most recent commit for the given key
        Pair<String, E> latestSnapshot = m_latestSnapshotPerEntity.get(key); //NOSONAR
        String snapshotID = null;
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
            snapshotID = m_snapshotIdGenerator.get();
            m_latestSnapshotPerEntity.put(key, Pair.create(snapshotID, entity));
            m_snapshotsKeyMap.put(snapshotID, key);
            LRUCache entityHistory = m_historyPerEntity.computeIfAbsent(key,
                k -> new LRUCache(m_maxNumSnapshotsPerEntity, m_maxNumSnapshotsPerEntity));
            entityHistory.put(snapshotID, entity);
        }
        return snapshotID;
    }

    @SuppressWarnings("java:S2160")
    private class LRUCache extends LinkedHashMap<String, E> {

        private static final long serialVersionUID = 107343581425956367L;

        private final int m_maxHistory;

        /**
         * @param initialCapacity the initial capacity of the cache
         * @param maxHistory the maximum size of the cache
         * @since 4.0
         */
        public LRUCache(final int initialCapacity, final int maxHistory) {
            super(initialCapacity, 0.75f, true);
            if (maxHistory < 1) {
                throw new IllegalArgumentException("max history must be larger 0: " + maxHistory);
            }
            m_maxHistory = maxHistory;

        }

        @Override
        protected boolean removeEldestEntry(final Map.Entry<String, E> e) {
            if (size() > m_maxHistory) {
                m_snapshotsKeyMap.remove(e.getKey());
                return true;
            } else {
                return false;
            }
        }
    }
}
