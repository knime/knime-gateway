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
 *   Feb 6, 2023 (kai): created
 */
package org.knime.gateway.impl.webui.spaces;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.knime.core.node.util.CheckUtils;
import org.knime.gateway.api.webui.entity.SpaceItemEnt;

/**
 * Space item path and item type cache
 *
 * @param <T> concrete type of the paths
 *
 * @author Benjamin Moser
 * @author Kai Franze, KNIME GmbH
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public abstract class SpaceItemPathAndTypeCache<T> {
    /** Mapping between IDs and paths. */
    protected final BidiMap<String, T> m_itemIdAndPathMapping = new DualHashBidiMap<>();
    /** Mapping from path to type. */
    protected final Map<T, SpaceItemEnt.TypeEnum> m_pathToTypeMap = new HashMap<>();

    /**
     * @param rootItemId root item ID
     * @param rootPath root path
     */
    protected SpaceItemPathAndTypeCache(final String rootItemId, final T rootPath) {
        m_itemIdAndPathMapping.put(rootItemId, rootPath);
    }

    /**
     * Determines the type of the item at the given path.
     *
     * @param item path
     * @return item type
     */
    protected abstract SpaceItemEnt.TypeEnum getSpaceItemType(T item);

    /**
     *
     * @param prefix
     * @param pathToCheck
     * @return
     */
    protected abstract boolean isPrefixOf(T prefix, T pathToCheck);

    protected abstract boolean isAbsolute(T path);

    /**
     * @param itemId
     * @return The cached path of the given item ID
     */
    public T getPath(final String itemId) {
        return m_itemIdAndPathMapping.get(itemId);
    }

    /**
     * @param itemId
     * @return {@code true} if the item id is known
     */
    public boolean containsKey(final String itemId) {
        return m_itemIdAndPathMapping.containsKey(itemId);
    }

    /**
     * @param path
     * @return {@code true} if the path is known
     */
    public boolean containsKey(final T path) {
        return m_pathToTypeMap.containsKey(path);
    }

    /**
     * @param path
     * @return {@code true} if the path is known
     */
    public boolean containsValue(final T path) {
        return m_itemIdAndPathMapping.inverseBidiMap().containsKey(path);
    }

    /**
     * Remove all entries corresponding to a given path. This also removes items that are path-wise children of the
     * given path.
     *
     * @param path The path to prune from the map.
     */
    public void prunePath(final T path) {
        m_itemIdAndPathMapping.entrySet().removeIf(e -> isPrefixOf(path, e.getValue()));
        m_pathToTypeMap.keySet().removeIf(k -> isPrefixOf(path, k));
    }

    /**
     * Returns a space item type if the path is already in the cache. Otherwise it will be added to the cache.
     *
     * @param item The path of the space item
     * @return The type of the space item
     */
    public SpaceItemEnt.TypeEnum determineTypeOrGetFromCache(final T item) {
        return m_pathToTypeMap.computeIfAbsent(item, this::getSpaceItemType);
    }

    /**
     * Determine an item ID for a given absolute path. Persist the mapping and handle collisions.
     *
     * @param absolutePath the absolute(!) path to get the id for
     * @throws IllegalArgumentException if the provided path is not absolute
     * @return the item id
     */
    public String determineItemIdOrGetFromCache(final T absolutePath) {
        var idString = m_itemIdAndPathMapping.inverseBidiMap().get(absolutePath);
        if (idString != null) {
            return idString;
        }

        CheckUtils.checkArgument(isAbsolute(absolutePath), "Provided path is not absolute");
        var id = absolutePath.hashCode();
        T existingPath;
        while ((existingPath = m_itemIdAndPathMapping.get(Integer.toString(id))) != null
            && !absolutePath.equals(existingPath)) {
            // handle hash collision
            id = 31 * id;
        }
        idString = Integer.toString(id);
        m_itemIdAndPathMapping.put(idString, absolutePath);
        return idString;
    }

    private void updateItemPathCache(final String itemId, final T absolutePath) {
        CheckUtils.checkArgument(m_itemIdAndPathMapping.containsKey(itemId), "Item id not yet in map");
        CheckUtils.checkArgument(isAbsolute(absolutePath), "Provided path is not absolute");
        m_itemIdAndPathMapping.put(itemId, absolutePath);
    }

    private SpaceItemEnt.TypeEnum updateItemTypeCache(final T oldKey, final T newKey) {
        if (!m_pathToTypeMap.containsKey(oldKey)) {
            throw new IllegalArgumentException("Item not yet in cache");
        }
        var value = m_pathToTypeMap.get(oldKey);
        m_pathToTypeMap.remove(oldKey);
        m_pathToTypeMap.put(newKey, value);
        return value;
    }

    /**
     * Updates the space item path and the space item type cache.
     *
     * @param itemId The space item ID
     * @param oldPath The old path of the space item
     * @param newPath The new path of the space item
     */
    public void update(final String itemId, final T oldPath, final T newPath) {
        updateItemPathCache(itemId, newPath);
        updateItemTypeCache(oldPath, newPath);
    }
}
