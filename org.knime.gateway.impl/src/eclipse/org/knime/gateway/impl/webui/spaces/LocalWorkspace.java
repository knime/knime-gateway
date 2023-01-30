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
 *   Dec 8, 2022 (hornm): created
 */
package org.knime.gateway.impl.webui.spaces;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import org.apache.http.client.utils.URIBuilder;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.util.CheckUtils;
import org.knime.core.node.workflow.MetaNodeTemplateInformation;
import org.knime.core.node.workflow.WorkflowPersistor;
import org.knime.core.node.workflow.contextv2.LocalLocationInfo;
import org.knime.core.node.workflow.contextv2.LocationInfo;
import org.knime.core.util.FileUtil;
import org.knime.core.util.KnimeUrlType;
import org.knime.core.util.PathUtils;
import org.knime.core.util.workflowalizer.MetadataConfig;
import org.knime.gateway.api.webui.entity.SpaceItemEnt;
import org.knime.gateway.api.webui.entity.SpaceItemEnt.TypeEnum;
import org.knime.gateway.api.webui.entity.WorkflowGroupContentEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions;
import org.knime.gateway.api.webui.util.EntityFactory;
import org.knime.gateway.api.webui.util.WorkflowEntityFactory;
import org.knime.gateway.impl.project.WorkflowProjectManager;

/**
 * {@link Space}-implementation that represents the local workspace.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @author Kai Franze, KNIME GmbH
 * @author Benjamin Moser, KNIME GmbH
 */
public final class LocalWorkspace implements Space {

    /**
     * ID of the local workspace.
     */
    public static final String LOCAL_WORKSPACE_ID = "local";

    /**
     * Default name for a newly created workflow.
     */
    public static final String DEFAULT_WORKFLOW_NAME = "KNIME_project";

    // Map from hash code representing the item-id to the absolute path.
    // assumption is that there is exactly one user of the local workspace at a time
    final ItemIdToPathMap m_itemIdToPathMap = new ItemIdToPathMap(); // package-private for testing

    // just for optimization purposes: avoids the repeated determination of the item type
    // which sometimes involves reading and parsing files (in order to determine the component type)
    final Map<Path, SpaceItemEnt.TypeEnum> m_pathToTypeMap = new HashMap<>(); // package private for tests

    private final Path m_localWorkspaceRootPath;

    /**
     * @param localWorkspaceRootPath the path to the root of the local workspace
     */
    public LocalWorkspace(final Path localWorkspaceRootPath) {
        m_localWorkspaceRootPath = localWorkspaceRootPath;
    }

    @Override
    public String getId() {
        return LOCAL_WORKSPACE_ID;
    }

    @Override
    public String getName() {
        return "Local space";
    }

    @Override
    public String getOwner() {
        return "local user";
    }

    @Override
    public WorkflowGroupContentEnt listWorkflowGroup(final String workflowGroupItemId) throws IOException {
        var absolutePath = getAbsolutePath(workflowGroupItemId);
        return EntityFactory.Space.buildLocalWorkflowGroupContentEnt(absolutePath, m_localWorkspaceRootPath,
            this::getItemId, this::cacheOrGetSpaceItemTypeFromCache, LocalWorkspace::isValidWorkspaceItem,
            ITEM_COMPARATOR);
    }

    @Override
    public SpaceItemEnt createWorkflow(final String workflowGroupItemId) throws IOException {
        var parentWorkflowGroupPath = getAbsolutePath(workflowGroupItemId);
        var workflowName = generateUniqueSpaceItemName(parentWorkflowGroupPath, DEFAULT_WORKFLOW_NAME, true);
        var directoryPath = Files.createDirectory(parentWorkflowGroupPath.resolve(workflowName));
        Files.createFile(directoryPath.resolve(WorkflowPersistor.WORKFLOW_FILE));
        return getSpaceItemEntFromPath(directoryPath);
    }

    @Override
    public Path toLocalAbsolutePath(final ExecutionMonitor monitor, final String itemId) {
        return m_itemIdToPathMap.get(Integer.parseInt(itemId));
    }

    @Override
    public URI toKnimeUrl(final String itemId) {
        final var rootUri = m_localWorkspaceRootPath.toUri();
        final var workflowFilePath = toLocalAbsolutePath(null, itemId).resolve("workflow.knime");
        final var itemRelUri = rootUri.relativize(workflowFilePath.toUri());
        if (itemRelUri.isAbsolute()) {
            throw new IllegalStateException("Workflow '" + workflowFilePath + "' is not inside root '"
                + m_localWorkspaceRootPath + "'");
        }
        try {
            return new URIBuilder(itemRelUri) //
                    .setScheme(KnimeUrlType.SCHEME) //
                    .setHost(LOCAL_WORKSPACE_ID.toUpperCase()) //
                    .build();
        } catch (URISyntaxException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public Path getLocalRootPath() {
        return m_localWorkspaceRootPath;
    }

    @Override
    public LocationInfo getLocationInfo(final String itemId) {
        return LocalLocationInfo.getInstance(null);
    }

    @Override
    public void deleteItems(final List<String> itemIds) throws IOException {
        // Check if the root is part of the
        if (itemIds.contains(Space.ROOT_ITEM_ID)) {
            throw new UnsupportedOperationException("The root of the space cannot be deleted.");
        }

        // Check if there are any item ids that do not exist
        var unknownItemIds = itemIds.stream().filter(id -> !m_itemIdToPathMap.containsKey(id))
            .collect(Collectors.joining(", "));
        if (unknownItemIds != null && !unknownItemIds.isEmpty()) {
            throw new NoSuchElementException("Unknown item ids: '" + unknownItemIds + "'");
        }

        var deletedPaths = new ArrayList<Path>(itemIds.size());
        try {
            for (var stringItemId : itemIds) {
                // NB: Should not be null because we checked this before
                var itemId = Integer.parseInt(stringItemId);
                var path = m_itemIdToPathMap.get(itemId);
                // NB: This also works for files
                PathUtils.deleteDirectoryIfExists(path);
                deletedPaths.add(path);
            }
        } finally {
            // NB: We only remove the paths that were deleted successfully
            deletedPaths.forEach(m_itemIdToPathMap::prunePath);
            m_pathToTypeMap.keySet().removeIf(k -> deletedPaths.stream().anyMatch(k::startsWith));
        }
    }

    @Override
    public SpaceItemEnt importFile(final Path srcPath, final String workflowGroupItemId) throws IOException {
        var parentWorkflowGroupPath = getAbsolutePath(workflowGroupItemId);
        var uniqueName = generateUniqueSpaceItemName(parentWorkflowGroupPath, srcPath.getFileName().toString(), false);
        var destPath = parentWorkflowGroupPath.resolve(uniqueName);
        FileUtil.copy(srcPath.toFile(), destPath.toFile());
        return getSpaceItemEntFromPath(destPath);
    }

    @Override
    public SpaceItemEnt importWorkflows(final Path srcPath, final String workflowGroupItemId,
        final Consumer<Path> createMetaInfoFileFor) throws IOException {
        var parentWorkflowGroupPath = getAbsolutePath(workflowGroupItemId);

        // Extract archive to temporary location
        var tmpDir = FileUtil.createTempDir(srcPath.getFileName().toString());
        FileUtil.unzip(srcPath.toFile(), tmpDir);
        var tmpSrcPath = tmpDir.listFiles()[0].toPath();
        // Import the workflow (group)
        var uniqueName =
            generateUniqueSpaceItemName(parentWorkflowGroupPath, tmpSrcPath.getFileName().toString(), true);
        var destPath = parentWorkflowGroupPath.resolve(uniqueName);
        FileUtil.copyDir(tmpSrcPath.toFile(), destPath.toFile());
        // Add `MetaInfoFile` to workflow (group)
        createMetaInfoFileFor.accept(destPath);

        return getSpaceItemEntFromPath(destPath);
    }

    @Override
    public List<String> getAncestorItemIds(final String itemId) {
        if (ROOT_ITEM_ID.equals(itemId)) {
            return List.of();
        }
        var path = m_itemIdToPathMap.get(Integer.parseInt(itemId));
        if (path == null) {
            throw new NoSuchElementException("No item for id '" + itemId + "'");
        }
        var parent = path;
        var res = new ArrayList<String>();
        while (!(parent = parent.getParent()).equals(m_localWorkspaceRootPath)) {
            res.add(getItemId(parent));
        }
        return res;
    }

    /**
     * @param workflowGroupItemId The workflow group item ID
     * @param name the name to check
     * @return A predicate checking for name collision within the corresponding workflow group
     */
    public boolean containsItemWithName(final String workflowGroupItemId, final String name) {
        var workflowGroup = getAbsolutePath(workflowGroupItemId);
        return Files.exists(workflowGroup.resolve(name));
    }

    private SpaceItemEnt getSpaceItemEntFromPath(final Path spaceItemPath) {
        var id = getItemId(spaceItemPath);
        var type = getSpaceItemType(spaceItemPath);
        return EntityFactory.Space.buildLocalSpaceItemEnt(spaceItemPath, m_localWorkspaceRootPath, id, type);
    }

    private Path getAbsolutePath(final String workflowGroupItemId) throws NoSuchElementException {
        Path absolutePath = null;
        var isRoot = Space.ROOT_ITEM_ID.equals(workflowGroupItemId);
        if (isRoot) {
            absolutePath = m_localWorkspaceRootPath;
        } else {
            absolutePath = m_itemIdToPathMap.get(Integer.valueOf(workflowGroupItemId));
            if (absolutePath == null) {
                throw new NoSuchElementException("Unknown item id '" + workflowGroupItemId + "'");
            }
        }
        if (cacheOrGetSpaceItemTypeFromCache(absolutePath) != TypeEnum.WORKFLOWGROUP) {
            throw new NoSuchElementException("The item with id '" + workflowGroupItemId + "' is not a workflow group");
        }
        return absolutePath;
    }

    private static boolean isValidWorkspaceItem(final Path p) {
        try {
            return !Files.isHidden(p)
                && !WorkflowPersistor.METAINFO_FILE.equals(p.getName(p.getNameCount() - 1).toString());
        } catch (IOException ex) {
            NodeLogger.getLogger(WorkflowEntityFactory.class)
                .warnWithFormat("Failed to evaluate 'isHidden' on path %s. Path ignored.", ex);
            return false;
        }
    }

    /**
     * Determine an item ID for a given absolute path. Persist the mapping and handle collisions.
     *
     * @param absolutePath the absolute(!) path to get the id for
     * @throws IllegalArgumentException if the provided path is not absolute
     * @return the item id
     */
    public String getItemId(final Path absolutePath) {
        return m_itemIdToPathMap.getItemId(absolutePath);
    }

    private SpaceItemEnt.TypeEnum cacheOrGetSpaceItemTypeFromCache(final Path item) {
        return m_pathToTypeMap.computeIfAbsent(item, LocalWorkspace::getSpaceItemType);
    }

    private SpaceItemEnt.TypeEnum updateSpaceItemTypeCache(final Path oldKey, final Path newKey) {
        if (!m_pathToTypeMap.containsKey(oldKey)) {
            throw new IllegalArgumentException("Item not yet in cache");
        }
        var value = m_pathToTypeMap.get(oldKey);
        m_pathToTypeMap.remove(oldKey);
        m_pathToTypeMap.put(newKey, value);
        return value;
    }

    private static SpaceItemEnt.TypeEnum getSpaceItemType(final Path item) {
        if (!Files.exists(item)) {
            return null;
        }
        if (Files.isDirectory(item)) {
            // the order of checking is important because, e.g., a component also contains a workflow.knime file
            if (containsFile(item, WorkflowPersistor.TEMPLATE_FILE)) {
                try (final var s = Files.newInputStream(item.resolve(WorkflowPersistor.TEMPLATE_FILE))) {
                    final var c = new MetadataConfig("ignored");
                    c.load(s);
                    var isComponent = c.getConfigBase("workflow_template_information").getString("templateType")
                        .equals(MetaNodeTemplateInformation.TemplateType.SubNode.toString());
                    return isComponent ? SpaceItemEnt.TypeEnum.COMPONENT : SpaceItemEnt.TypeEnum.WORKFLOWTEMPLATE;
                } catch (InvalidSettingsException | IOException ex) {
                    NodeLogger.getLogger(LocalWorkspace.class)
                        .warnWithFormat("Space item type couldn't be determined for %s", item, ex);
                    return SpaceItemEnt.TypeEnum.DATA;
                }
            } else if (containsFile(item, WorkflowPersistor.WORKFLOW_FILE)) {
                return SpaceItemEnt.TypeEnum.WORKFLOW;
            } else {
                return SpaceItemEnt.TypeEnum.WORKFLOWGROUP;
            }
        } else {
            return SpaceItemEnt.TypeEnum.DATA;
        }
    }

    private static boolean containsFile(final Path directory, final String filename) {
        return Files.exists(directory.resolve(filename));
    }

    /**
     * Generates unique space item names, preserves file extensions
     *
     * @return The initial name if that doesn't exist, the unique one otherwise.
     */
    private static String generateUniqueSpaceItemName(final Path workflowGroup, final String name,
        final boolean isWorkflow) {
        if (!Files.exists(workflowGroup.resolve(name))) {
            return name;
        } else {
            var lastIndexOfDot = isWorkflow ? -1 : name.lastIndexOf("."); // Ignore dots in workflow names
            var fileExtension = lastIndexOfDot > -1 ? name.substring(lastIndexOfDot) : "";
            var oldName = lastIndexOfDot > -1 ? name.substring(0, lastIndexOfDot) : name;
            var counter = 0;
            String newName;
            do {
                counter++;
                newName = isWorkflow ? (oldName + counter) : (oldName + "(" + counter + ")"); // No brackets in workflow names
            } while (Files.exists(workflowGroup.resolve(newName + fileExtension)));
            return newName + fileExtension;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SpaceItemEnt renameItem(final String itemId, final String queriedName)
            throws IOException, ServiceExceptions.OperationNotAllowedException {

        // TODO: not checked whether renamed item is ancestor of an open workflow
        //  this can be verified once we determine itemIDs of ancestors, cf. NXT-1432

        if (itemId.equals(Space.ROOT_ITEM_ID)) {
            throw new ServiceExceptions.OperationNotAllowedException("Can not rename root item");
        }

        var newName = queriedName.trim();
        assertValidItemNameOrThrow(newName);

        var sourcePath = toLocalAbsolutePath(null, itemId);
        if (sourcePath == null) {
            throw new IOException("Unknown item ID");
        }
        var itemType = getSpaceItemType(sourcePath);
        var destinationPath = sourcePath.resolveSibling(Path.of(newName));
        if (sourcePath.equals(destinationPath)) {
            var oldName = sourcePath.getName(sourcePath.getNameCount() - 1).toString();
            return EntityFactory.Space.buildSpaceItemEnt(oldName, itemId, itemType);
        }
        var sourceFile = sourcePath.toFile();
        var destinationFile = destinationPath.toFile();

        if (destinationFile.exists()) {
            throw new ServiceExceptions.OperationNotAllowedException("There already exists a file of that name");
        }


        try {
            var renamingSucceeded = sourceFile.renameTo(destinationFile);
            if (!renamingSucceeded) {
                throw new IOException("Could not rename item");
            }
        } catch (SecurityException e) {
            throw new IOException(e);
        }

        m_itemIdToPathMap.update(itemId, destinationPath);
        updateSpaceItemTypeCache(sourcePath, destinationPath);

        return EntityFactory.Space.buildSpaceItemEnt(newName, itemId, itemType);
    }

    /**
     * Map Item IDs to local paths.
     *
     * Package-private for testing.
     *
     * @author Benjamin Moser
     */
    static final class ItemIdToPathMap {

        private final Map<Integer, Path> m_itemIdToPathMap = new HashMap<>();

        /**
         * @param itemId
         * @return The cached path of the given item ID
         */
        public Path get(final String itemId) {
            return get(Integer.parseInt(itemId));
        }

        /**
         * @param itemId
         * @return The cached path of the given item ID
         */
        public Path get(final int itemId) {
            return m_itemIdToPathMap.get(itemId);
        }

        /**
         * Add or update the mapping for given item ID
         *
         * @param itemId The key
         * @param path The new value, expected to be an absolute path
         * @return The previous value associated with the key or <code>null</code> if there was no previous value.
         */
        public Path put(final String itemId, final Path path) {
            return put(Integer.parseInt(itemId), path);
        }

        /**
         * Add or update the mapping for given item ID
         *
         * @param itemId The key
         * @param path The new value, expected to be an absolute path
         * @return The previous value associated with the key or <code>null</code> if there was no previous value.
         */
        public Path put(final int itemId, final Path path) {
            CheckUtils.checkArgument(path.isAbsolute(), "Provided path is not absolute");
            return m_itemIdToPathMap.put(itemId, path);
        }

        public boolean containsKey(final String itemId) {
            return m_itemIdToPathMap.containsKey(Integer.parseInt(itemId));
        }

        /**
         * Update the mapping for a given item ID
         *
         * @param itemId The key, expected to be already present in the mapping
         * @param path The new value, expected to be an absolute path
         * @return The previous value associated with the key or <code>null</code> if there was no previous value.
         */
        public Path update(final String itemId, final Path path) {
            return update(Integer.parseInt(itemId), path);
        }

        /**
         * Update the mapping for a given item ID
         *
         * @param itemId The key, expected to be already present in the mapping
         * @param path The new value, expected to be an absolute path
         * @return The previous value associated with the key or <code>null</code> if there was no previous value.
         */
        public Path update(final int itemId, final Path path) {
            CheckUtils.checkArgument(m_itemIdToPathMap.containsKey(itemId), "Key not yet in map");
            return put(itemId, path);
        }

        /**
         * Remove all entries corresponding to a given path. This also removes items that are path-wise children of the
         * given path.
         * @param path The path to prune from the map.
         */
        public void prunePath(final Path path) {
            m_itemIdToPathMap.entrySet().removeIf(e -> e.getValue().startsWith(path));
        }

        public Set<Map.Entry<Integer, Path>> entrySet() {
            return m_itemIdToPathMap.entrySet();
        }

        public int size() {
            return m_itemIdToPathMap.size();
        }

        public Collection<Path> values() {
            return m_itemIdToPathMap.values();
        }

        /**
         * Determine an item ID for a given absolute path. Persist the mapping and handle collisions.
         *
         * @param absolutePath the absolute(!) path to get the id for
         * @throws IllegalArgumentException if the provided path is not absolute
         * @return the item id
         */
        public String getItemId(final Path absolutePath) {
            CheckUtils.checkArgument(absolutePath.isAbsolute(), "Provided path is not absolute");
            var id = absolutePath.hashCode();
            Path existingPath;
            while ((existingPath = m_itemIdToPathMap.get(id)) != null && !absolutePath.equals(existingPath)) {
                // handle hash collision
                id = 31 * id;
            }
            m_itemIdToPathMap.put(id, absolutePath);
            return Integer.toString(id);
        }

    }

    /**
     * Verify that the given name is a valid name for an item in a {@link LocalWorkspace}.
     *
     * @see FileStoreNameValidator#isValid
     * @see ExplorerFileSystem#validateFilename
     * @param name The candidate new name.
     */
    public static void assertValidItemNameOrThrow(final String name) throws IllegalArgumentException {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Please choose a name");
        }
        if (Path.of(name).getParent() != null) {
            throw new IllegalArgumentException("Name cannot be a path");
        }
        if (name.startsWith(".")) {
            throw new IllegalArgumentException("Name cannot start with dot.");
        }
        if (name.endsWith(".")) {
            throw new IllegalArgumentException("Name cannot end with dot.");
        }
        Matcher matcher = FileUtil.ILLEGAL_FILENAME_CHARS_PATTERN.matcher(name);
        if (matcher.find()) {
            throw new IllegalArgumentException(
                    "Name contains invalid characters (" + FileUtil.ILLEGAL_FILENAME_CHARS + ").");
        }
    }

}
