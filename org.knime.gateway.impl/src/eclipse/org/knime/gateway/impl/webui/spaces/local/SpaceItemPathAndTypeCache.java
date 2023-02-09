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
package org.knime.gateway.impl.webui.spaces.local;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.util.CheckUtils;
import org.knime.core.node.workflow.MetaNodeTemplateInformation;
import org.knime.core.node.workflow.WorkflowPersistor;
import org.knime.core.util.workflowalizer.MetadataConfig;
import org.knime.gateway.api.webui.entity.SpaceItemEnt;

/**
 * Space item path and item type cache
 *
 * @author Benjamin Moser
 * @author Kai Franze, KNIME GmbH
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
class SpaceItemPathAndTypeCache {

    private final BidiMap<String, Path> m_itemIdAndPathMapping = new DualHashBidiMap<>();

    private final Map<Path, SpaceItemEnt.TypeEnum> m_pathToTypeMap = new HashMap<>();

    SpaceItemPathAndTypeCache(final String rootItemId, final Path localWorkspaceRootPath) {
        m_itemIdAndPathMapping.put(rootItemId, localWorkspaceRootPath);
    }

    /**
     * @param itemId
     * @return The cached path of the given item ID
     */
    Path getPath(final String itemId) {
        return m_itemIdAndPathMapping.get(itemId);
    }

    /**
     * @param itemId
     * @return {@code true} if the item id is known
     */
    boolean containsKey(final String itemId) {
        return m_itemIdAndPathMapping.containsKey(itemId);
    }

    /**
     * @param path
     * @return {@code true} if the path is known
     */
    boolean containsKey(final Path path) {
        return m_pathToTypeMap.containsKey(path);
    }

    /**
     * @param path
     * @return {@code true} if the path is known
     */
    boolean containsValue(final Path path) {
        return m_itemIdAndPathMapping.inverseBidiMap().containsKey(path);
    }

    /**
     * Remove all entries corresponding to a given path. This also removes items that are path-wise children of the
     * given path.
     *
     * @param path The path to prune from the map.
     */
    void prunePath(final Path path) {
        m_itemIdAndPathMapping.entrySet().removeIf(e -> e.getValue().startsWith(path));
        m_pathToTypeMap.keySet().removeIf(k -> k.startsWith(path));
    }

    Set<Map.Entry<String, Path>> entrySet() {
        return m_itemIdAndPathMapping.entrySet();
    }

    int sizeOfItemIdToPathMap() {
        return m_itemIdAndPathMapping.size();
    }

    int sizeOfPathToTypeMap() {
        return m_pathToTypeMap.size();
    }

    /**
     * Returns a space item type if the path is already in the cache. Otherwise it will be added to the cache.
     *
     * @param item The path of the space item
     * @return The type of the space item
     */
    SpaceItemEnt.TypeEnum determineTypeOrGetFromCache(final Path item) {
        return m_pathToTypeMap.computeIfAbsent(item, SpaceItemPathAndTypeCache::getSpaceItemType);
    }

    /**
     * Determine an item ID for a given absolute path. Persist the mapping and handle collisions.
     *
     * @param absolutePath the absolute(!) path to get the id for
     * @throws IllegalArgumentException if the provided path is not absolute
     * @return the item id
     */
    String determineItemIdOrGetFromCache(final Path absolutePath) {
        var idString = m_itemIdAndPathMapping.inverseBidiMap().get(absolutePath);
        if (idString != null) {
            return idString;
        }

        CheckUtils.checkArgument(absolutePath.isAbsolute(), "Provided path is not absolute");
        var id = absolutePath.hashCode();
        Path existingPath;
        while ((existingPath = m_itemIdAndPathMapping.get(Integer.toString(id))) != null
            && !absolutePath.equals(existingPath)) {
            // handle hash collision
            id = 31 * id;
        }
        idString = Integer.toString(id);
        m_itemIdAndPathMapping.put(idString, absolutePath);
        return idString;
    }

    private void updateItemPathCache(final String itemId, final Path absolutePath) {
        CheckUtils.checkArgument(m_itemIdAndPathMapping.containsKey(itemId), "Item id not yet in map");
        CheckUtils.checkArgument(absolutePath.isAbsolute(), "Provided path is not absolute");
        m_itemIdAndPathMapping.put(itemId, absolutePath);
    }

    private SpaceItemEnt.TypeEnum updateItemTypeCache(final Path oldKey, final Path newKey) {
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
    void update(final String itemId, final Path oldPath, final Path newPath) {
        updateItemPathCache(itemId, newPath);
        updateItemTypeCache(oldPath, newPath);
    }

    private static SpaceItemEnt.TypeEnum getSpaceItemType(final Path item) {
        if (!Files.exists(item)) {
            return null;
        }
        if (Files.isDirectory(item)) {
            SpaceItemEnt.TypeEnum type;
            // the order of checking is important because, e.g., a component also contains a workflow.knime file
            if (containsFile(item, WorkflowPersistor.TEMPLATE_FILE)) {
                try (final var s = Files.newInputStream(item.resolve(WorkflowPersistor.TEMPLATE_FILE))) {
                    final var c = new MetadataConfig("ignored");
                    c.load(s);
                    var isComponent = c.getConfigBase("workflow_template_information").getString("templateType")
                        .equals(MetaNodeTemplateInformation.TemplateType.SubNode.toString());
                    type = isComponent ? SpaceItemEnt.TypeEnum.COMPONENT : SpaceItemEnt.TypeEnum.WORKFLOWTEMPLATE;
                } catch (InvalidSettingsException | IOException ex) {
                    NodeLogger.getLogger(LocalWorkspace.class)
                        .warnWithFormat("Space item type couldn't be determined for %s", item, ex);
                    type = SpaceItemEnt.TypeEnum.DATA;
                }
            } else if (containsFile(item, WorkflowPersistor.WORKFLOW_FILE)) {
                type = SpaceItemEnt.TypeEnum.WORKFLOW;
            } else {
                type = SpaceItemEnt.TypeEnum.WORKFLOWGROUP;
            }
            return type;
        } else {
            return SpaceItemEnt.TypeEnum.DATA;
        }
    }

    private static boolean containsFile(final Path directory, final String filename) {
        return Files.exists(directory.resolve(filename));
    }

}
