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
package org.knime.gateway.impl.webui.spaces.local;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.http.client.utils.URIBuilder;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.workflow.WorkflowPersistor;
import org.knime.core.node.workflow.contextv2.LocalLocationInfo;
import org.knime.core.node.workflow.contextv2.LocationInfo;
import org.knime.core.util.FileUtil;
import org.knime.core.util.KnimeUrlType;
import org.knime.core.util.Pair;
import org.knime.core.util.PathUtils;
import org.knime.gateway.api.webui.entity.SpaceItemEnt;
import org.knime.gateway.api.webui.entity.SpaceItemEnt.TypeEnum;
import org.knime.gateway.api.webui.entity.WorkflowGroupContentEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.OperationNotAllowedException;
import org.knime.gateway.api.webui.util.EntityFactory;
import org.knime.gateway.api.webui.util.WorkflowEntityFactory;
import org.knime.gateway.impl.webui.spaces.Space;

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

    /**
     * Default name for a newly created workflow groups.
     */
    public static final String DEFAULT_WORKFLOW_GROUP_NAME = "Folder";

    /**
     * Holds both, the item ID to path map and the path to type map
     */
    final SpaceItemPathAndTypeCache m_spaceItemPathAndTypeCache = new SpaceItemPathAndTypeCache(); // Package scope for testing

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
            this::getItemId, m_spaceItemPathAndTypeCache::determineTypeOrGetFromCache,
            LocalWorkspace::isValidWorkspaceItem, ITEM_COMPARATOR);
    }

    @Override
    public SpaceItemEnt createWorkflow(final String workflowGroupItemId) throws IOException {
        var parentWorkflowGroupPath = getAbsolutePath(workflowGroupItemId);
        var workflowName = generateUniqueSpaceItemName(parentWorkflowGroupPath, DEFAULT_WORKFLOW_NAME, true);
        var directoryPath = Files.createDirectory(parentWorkflowGroupPath.resolve(workflowName));
        Files.createFile(directoryPath.resolve(WorkflowPersistor.WORKFLOW_FILE));
        return getSpaceItemEntFromPathAndUpdateCache(directoryPath);
    }

    @Override
    public SpaceItemEnt createWorkflowGroup(final String workflowGroupItemId) throws IOException {
        var parentWorkflowGroupPath = getAbsolutePath(workflowGroupItemId);
        var workflowGroupName = generateUniqueSpaceItemName(parentWorkflowGroupPath, DEFAULT_WORKFLOW_GROUP_NAME, false);
        var directoryPath = Files.createDirectory(parentWorkflowGroupPath.resolve(workflowGroupName));
        // TODO(NXT-1484) create a meta info file for the folder
        return getSpaceItemEntFromPathAndUpdateCache(directoryPath);
    }

    @Override
    public Path toLocalAbsolutePath(final ExecutionMonitor monitor, final String itemId) {
        return m_spaceItemPathAndTypeCache.getPath(itemId);
    }

    @Override
    public URI toKnimeUrl(final String itemId) {
        var absolutePath = toLocalAbsolutePath(null, itemId);
        var type = m_spaceItemPathAndTypeCache.determineTypeOrGetFromCache(absolutePath);
        if (type == TypeEnum.WORKFLOW) {
            absolutePath = absolutePath.resolve("workflow.knime");
        }
        final var rootUri = m_localWorkspaceRootPath.toUri();
        final var itemRelUri = rootUri.relativize(absolutePath.toUri());
        if (itemRelUri.isAbsolute()) {
            throw new IllegalStateException(
                "Space item '" + absolutePath + "' is not inside root '" + m_localWorkspaceRootPath + "'");
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
        // Check if the root is part of the IDs
        if (itemIds.contains(Space.ROOT_ITEM_ID)) {
            throw new UnsupportedOperationException("The root of the space cannot be deleted.");
        }

        // Check if there are any item IDs that do not exist
        assertAllItemIdsExistOrElseThrow(itemIds);

        var deletedPaths = new ArrayList<Path>(itemIds.size());
        try {
            for (var stringItemId : itemIds) {
                // NB: Should not be null because we checked this before
                var itemId = stringItemId;
                var path = m_spaceItemPathAndTypeCache.getPath(itemId);
                // NB: This also works for files
                PathUtils.deleteDirectoryIfExists(path);
                deletedPaths.add(path);
            }
        } finally {
            // NB: We only remove the paths that were deleted successfully
            deletedPaths.forEach(m_spaceItemPathAndTypeCache::prunePath);
        }
    }

    @Override
    public SpaceItemEnt renameItem(final String itemId, final String queriedName)
        throws IOException, ServiceExceptions.OperationNotAllowedException {

        if (itemId.equals(Space.ROOT_ITEM_ID)) {
            throw new ServiceExceptions.OperationNotAllowedException("Can not rename root item");
        }

        var newName = queriedName.trim();
        assertValidItemNameOrThrow(newName);

        var sourcePath = toLocalAbsolutePath(null, itemId);
        if (sourcePath == null) {
            throw new IOException("Unknown item ID");
        }
        var itemType = m_spaceItemPathAndTypeCache.determineTypeOrGetFromCache((sourcePath));
        var destinationPath = sourcePath.resolveSibling(Path.of(newName));
        if (sourcePath.equals(destinationPath)) {
            var oldName = sourcePath.getFileName().toString();
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

        m_spaceItemPathAndTypeCache.update(itemId, sourcePath, destinationPath);

        return EntityFactory.Space.buildSpaceItemEnt(newName, itemId, itemType);
    }

    @Override
    public void moveItems(final List<String> itemIds, final String destItemId,
        final Space.NameCollisionHandling collisionHandling) throws IOException {
        if (itemIds.contains(Space.ROOT_ITEM_ID)) {
            throw new IllegalArgumentException("The root of the space cannot be moved.");
        }
        if (itemIds.contains(destItemId)) {
            throw new IllegalArgumentException("Cannot move a space item to itself");
        }
        assertAllItemIdsExistOrElseThrow(
            Stream.concat(itemIds.stream(), Stream.of(destItemId)).collect(Collectors.toList()));
        var destPathParent = getAbsolutePath(destItemId);
        if (m_spaceItemPathAndTypeCache.determineTypeOrGetFromCache(destPathParent) != TypeEnum.WORKFLOWGROUP) {
            throw new IllegalArgumentException(
                "Cannot move space items to a location that is not a workflow group");
        }

        Map<String, Pair<Path, Path>> newItemIdToPathMap = new HashMap<>();
        try {
            for (var itemId : itemIds) {
                var srcPath = m_spaceItemPathAndTypeCache.getPath(itemId);
                var destPath = moveItem(srcPath, destPathParent, collisionHandling);
                newItemIdToPathMap.put(itemId, Pair.create(srcPath, destPath)); // Keep the ones that actually moved
            }
        } finally { // Update map for all the items that were actually moved
            newItemIdToPathMap.entrySet().forEach(e -> {
                var itemId = e.getKey();
                var oldPath = e.getValue().getFirst();
                var newPath = e.getValue().getSecond();
                m_spaceItemPathAndTypeCache.update(itemId, oldPath, newPath);
            });
        }
    }

    @Override
    public SpaceItemEnt importFile(final Path srcPath, final String workflowGroupItemId,
        final NameCollisionHandling collisionHandling) throws IOException {
        var parentWorkflowGroupPath = getAbsolutePath(workflowGroupItemId);
        var fileName = srcPath.getFileName().toString();

        Path destPath;
        if (collisionHandling == Space.NameCollisionHandling.NOOP) {
            destPath = parentWorkflowGroupPath.resolve(fileName);
        } else if (collisionHandling == Space.NameCollisionHandling.AUTORENAME) {
            var uniqueName = generateUniqueSpaceItemName(parentWorkflowGroupPath, fileName, false);
            destPath = parentWorkflowGroupPath.resolve(uniqueName);
        } else {
            destPath = parentWorkflowGroupPath.resolve(fileName);
            FileUtil.deleteRecursively(destPath.toFile()); // Delete the existing space item first
        }
        FileUtil.copy(srcPath.toFile(), destPath.toFile());

        return getSpaceItemEntFromPathAndUpdateCache(destPath);
    }

    @Override
    public SpaceItemEnt importWorkflowOrWorkflowGroup(final Path srcPath, final String workflowGroupItemId,
        final Consumer<Path> createMetaInfoFileFor, final Space.NameCollisionHandling collisionHandling)
        throws IOException {
        var parentWorkflowGroupPath = getAbsolutePath(workflowGroupItemId);

        var tmpDir = FileUtil.createTempDir(srcPath.getFileName().toString());
        FileUtil.unzip(srcPath.toFile(), tmpDir);
        var tmpSrcPath = tmpDir.listFiles()[0].toPath();
        var fileName = tmpSrcPath.getFileName().toString();

        Path destPath;
        if (collisionHandling == Space.NameCollisionHandling.NOOP) {
            destPath = parentWorkflowGroupPath.resolve(fileName);
        } else if (collisionHandling == Space.NameCollisionHandling.AUTORENAME) {
            var uniqueName = generateUniqueSpaceItemName(parentWorkflowGroupPath, fileName, true);
            destPath = parentWorkflowGroupPath.resolve(uniqueName);
        } else {
            destPath = parentWorkflowGroupPath.resolve(fileName);
            FileUtil.deleteRecursively(destPath.toFile()); // Delete the existing space item first
        }
        FileUtil.copyDir(tmpSrcPath.toFile(), destPath.toFile());
        createMetaInfoFileFor.accept(destPath);

        return getSpaceItemEntFromPathAndUpdateCache(destPath);
    }

    @Override
    public List<String> getAncestorItemIds(final String itemId) {
        if (ROOT_ITEM_ID.equals(itemId)) {
            return List.of();
        }
        var path = m_spaceItemPathAndTypeCache.getPath(itemId);
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

    @Override
    public boolean containsItemWithName(final String workflowGroupItemId, final String itemName)
        throws NoSuchElementException {
        var workflowGroup = getAbsolutePath(workflowGroupItemId);
        return Files.exists(workflowGroup.resolve(itemName));
    }

    /**
     * @return The items path after it was moved.
     */
    private Path moveItem(final Path srcPath, final Path destPathParent,
        final Space.NameCollisionHandling collisionHandling) throws IOException {
        var type = m_spaceItemPathAndTypeCache.determineTypeOrGetFromCache(srcPath);
        var file = srcPath.getFileName();
        var name = file.toString();
        Path destPath;
        if (collisionHandling == Space.NameCollisionHandling.NOOP) { // Assume no name collisions
            destPath = destPathParent.resolve(name);
        } else if (collisionHandling == Space.NameCollisionHandling.AUTORENAME) { // Auto-rename in case of name collisions
            var isWorkflowOrWorkflowGroup = type == TypeEnum.WORKFLOW || type == TypeEnum.WORKFLOWGROUP;
            var uniqueName = generateUniqueSpaceItemName(destPathParent, name, isWorkflowOrWorkflowGroup);
            destPath = destPathParent.resolve(uniqueName);
        } else { // Overwrite in case of name collision
            destPath = destPathParent.resolve(name);
            FileUtil.deleteRecursively(destPath.toFile()); // Delete the existing space item first
        }
        return Files.move(srcPath, destPath); // Moves the file/directory and returns its destination path
    }

    private SpaceItemEnt getSpaceItemEntFromPathAndUpdateCache(final Path absolutePath) {
        var id = m_spaceItemPathAndTypeCache.determineItemIdOrGetFromCache(absolutePath);
        var type = m_spaceItemPathAndTypeCache.determineTypeOrGetFromCache(absolutePath);
        return EntityFactory.Space.buildLocalSpaceItemEnt(absolutePath, m_localWorkspaceRootPath, id, type);
    }

    private Path getAbsolutePath(final String workflowGroupItemId) throws NoSuchElementException {
        Path absolutePath = null;
        var isRoot = Space.ROOT_ITEM_ID.equals(workflowGroupItemId);
        if (isRoot) {
            absolutePath = m_localWorkspaceRootPath;
        } else {
            absolutePath = m_spaceItemPathAndTypeCache.getPath(workflowGroupItemId);
            if (absolutePath == null) {
                throw new NoSuchElementException("Unknown item id '" + workflowGroupItemId + "'");
            }
        }
        if (m_spaceItemPathAndTypeCache.determineTypeOrGetFromCache(absolutePath) != TypeEnum.WORKFLOWGROUP) {
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
        return m_spaceItemPathAndTypeCache.determineItemIdOrGetFromCache(absolutePath);
    }

    /**
     * Returns the name of a space item
     *
     * @param itemId The space item ID
     * @return The space items name
     * @throws NoSuchElementException If no such item is present cache
     */
    public String getItemName(final String itemId) throws NoSuchElementException {
        if (itemId.equals(ROOT_ITEM_ID)) {
            return ROOT_ITEM_ID;
        } else {
            return Optional.ofNullable(m_spaceItemPathAndTypeCache.getPath(itemId))//
                .map(Path::getFileName)//
                .map(Path::toString)//
                .orElseThrow(
                    () -> new NoSuchElementException(String.format("No item with <%s> present in cache", itemId)));
        }
    }

    /**
     * Generates unique space item names, preserves file extensions
     *
     * @return The initial name if that doesn't exist, the unique one otherwise.
     */
    private static String generateUniqueSpaceItemName(final Path workflowGroup, final String name,
        final boolean isWorkflowOrWorkflowGroup) {
        if (!Files.exists(workflowGroup.resolve(name))) {
            return name;
        } else {
            var lastIndexOfDot = isWorkflowOrWorkflowGroup ? -1 : name.lastIndexOf("."); // Ignore dots in workflow names
            var fileExtension = lastIndexOfDot > -1 ? name.substring(lastIndexOfDot) : "";
            var oldName = lastIndexOfDot > -1 ? name.substring(0, lastIndexOfDot) : name;
            var counter = 0;
            String newName;
            do {
                counter++;
                newName = isWorkflowOrWorkflowGroup ? (oldName + counter) : (oldName + "(" + counter + ")"); // No brackets in workflow names
            } while (Files.exists(workflowGroup.resolve(newName + fileExtension)));
            return newName + fileExtension;
        }
    }

    private void assertAllItemIdsExistOrElseThrow(final List<String> itemIds) throws NoSuchElementException {
        var unknownItemIds = itemIds.stream()//
            .filter(id -> !m_spaceItemPathAndTypeCache.containsKey(id))//
            .collect(Collectors.joining(", "));
        if (unknownItemIds != null && !unknownItemIds.isEmpty()) {
            throw new NoSuchElementException(String.format("Unknown item ids: <%s>", unknownItemIds));
        }
    }

    /**
     * Verify that the given name is a valid name for an item in a {@link LocalWorkspace}.
     *
     * @see FileStoreNameValidator#isValid
     * @see ExplorerFileSystem#validateFilename
     * @param name The candidate new name.
     */
    private static void assertValidItemNameOrThrow(final String name) throws OperationNotAllowedException {
        if (name == null || name.trim().isEmpty()) {
            throw new OperationNotAllowedException("Please choose a name");
        }
        if (Path.of(name).getParent() != null) {
            throw new OperationNotAllowedException("Name cannot be a path");
        }
        if (name.startsWith(".")) {
            throw new OperationNotAllowedException("Name cannot start with dot.");
        }
        if (name.endsWith(".")) {
            throw new OperationNotAllowedException("Name cannot end with dot.");
        }
        var matcher = FileUtil.ILLEGAL_FILENAME_CHARS_PATTERN.matcher(name);
        if (matcher.find()) {
            throw new OperationNotAllowedException(
                    "Name contains invalid characters (" + FileUtil.ILLEGAL_FILENAME_CHARS + ").");
        }
    }

}
