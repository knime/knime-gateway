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
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.http.client.utils.URIBuilder;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.workflow.WorkflowPersistor;
import org.knime.core.node.workflow.contextv2.LocalLocationInfo;
import org.knime.core.node.workflow.contextv2.LocationInfo;
import org.knime.core.util.FileUtil;
import org.knime.core.util.KnimeUrlType;
import org.knime.core.util.Pair;
import org.knime.core.util.PathUtils;
import org.knime.core.util.exception.ResourceAccessException;
import org.knime.core.util.pathresolve.ResolverUtil;
import org.knime.gateway.api.util.EntityUtil;
import org.knime.gateway.api.webui.entity.SpaceEnt;
import org.knime.gateway.api.webui.entity.SpaceItemEnt;
import org.knime.gateway.api.webui.entity.SpaceItemEnt.TypeEnum;
import org.knime.gateway.api.webui.entity.SpaceItemReferenceEnt.ProjectTypeEnum;
import org.knime.gateway.api.webui.entity.WorkflowGroupContentEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.OperationNotAllowedException;
import org.knime.gateway.api.webui.util.EntityFactory;
import org.knime.gateway.impl.project.ProjectManager;
import org.knime.gateway.api.util.VersionId;
import org.knime.gateway.impl.webui.spaces.Collision;
import org.knime.gateway.impl.webui.spaces.Space;
import org.knime.gateway.impl.webui.spaces.SpaceProvider;

/**
 * {@link Space}-implementation that represents the local workspace.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @author Kai Franze, KNIME GmbH
 * @author Benjamin Moser, KNIME GmbH
 */
public final class LocalSpace implements Space {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(LocalSpace.class);

    /**
     * ID of the single {@link Space} provider by the {@link }
     */
    public static final String LOCAL_SPACE_ID = "local";

    /**
     * Holds both, the item ID to path map and the path to type map
     */
    @SuppressWarnings("WeakerAccess") // Package scope for testing
    final LocalSpaceItemPathAndTypeCache m_spaceItemPathAndTypeCache;

    private final Path m_rootPath;


    /**
     * @param rootPath the path to the root of the local workspace
     */
    public LocalSpace(final Path rootPath) {
        m_rootPath = rootPath;
        // To make sure the path of the root item ID can also be retrieved from the cache
        m_spaceItemPathAndTypeCache = new LocalSpaceItemPathAndTypeCache(Space.ROOT_ITEM_ID, rootPath);
    }

    @Override
    public String getId() {
        return LOCAL_SPACE_ID;
    }

    @Override
    public String getName() {
        return "Local space";
    }

    @Override
    public SpaceEnt toEntity() {
        return EntityFactory.Space.buildSpaceEnt(getId(), getName(), "local user", "", false);
    }

    @Override
    public TypeEnum getItemType(final String itemId) {
        if (!m_spaceItemPathAndTypeCache.containsKey(itemId)) {
            throw new NoSuchElementException("Unknown item ID for local workspace: " + itemId);
        }
        final var path = m_spaceItemPathAndTypeCache.getPath(itemId);
        return m_spaceItemPathAndTypeCache.determineTypeOrGetFromCache(path);
    }

    @Override
    public WorkflowGroupContentEnt listWorkflowGroup(final String workflowGroupItemId) throws IOException {
        var absolutePath = getAbsolutePath(workflowGroupItemId);
        return EntityFactory.Space.buildLocalWorkflowGroupContentEnt(absolutePath, m_rootPath,
            this::getItemId, m_spaceItemPathAndTypeCache::determineTypeOrGetFromCache,
            LocalSpace::isValidItem, ITEM_COMPARATOR);
    }

    @Override
    public SpaceItemEnt createWorkflow(final String workflowGroupItemId, final String workflowName) throws IOException {
        var parentWorkflowGroupPath = getAbsolutePath(workflowGroupItemId);
        var realWorkflowName = generateUniqueSpaceItemName(parentWorkflowGroupPath, workflowName, true);
        var directoryPath = Files.createDirectory(parentWorkflowGroupPath.resolve(realWorkflowName));
        Files.createFile(directoryPath.resolve(WorkflowPersistor.WORKFLOW_FILE));
        return getSpaceItemEntFromPathAndUpdateCache(directoryPath);
    }

    @Override
    public SpaceItemEnt createWorkflowGroup(final String workflowGroupItemId) throws IOException {
        var parentWorkflowGroupPath = getAbsolutePath(workflowGroupItemId);
        var workflowGroupName =
            generateUniqueSpaceItemName(parentWorkflowGroupPath, DEFAULT_WORKFLOW_GROUP_NAME, false);
        var pathToCreate = parentWorkflowGroupPath.resolve(workflowGroupName);
        try {
            var directoryPath = Files.createDirectory(pathToCreate);
            return getSpaceItemEntFromPathAndUpdateCache(directoryPath);
        } catch (IOException e) { // NOSONAR
            throw new IOException("Cannot create folders in '%s'.".formatted(parentWorkflowGroupPath.toString()),
                e.getCause());
        }
    }

    @Override
    public Optional<Path> toLocalAbsolutePath(final ExecutionMonitor monitor, final String itemId, final VersionId version) {
        return toLocalAbsolutePath(itemId);
    }

    /**
     * Resolves the item with the given ID into a local file.
     *
     * @param itemId ID if the item to resolve
     * @return the local path of the item and if available, empty if not available
     */
    public Optional<Path> toLocalAbsolutePath(final String itemId) {
        var path = m_spaceItemPathAndTypeCache.getPath(itemId);
        if (path == null || !Files.exists(path)) {
            return Optional.empty();
        }
        return Optional.of(path);
    }

    /**
     * Return the file system path of the requested item, relative to the root directory of this space.
     *
     * @param itemId The query item
     * @return The path, or empty if item ID is not in space.
     */
    public Optional<Path> toLocalRelativePath(final String itemId) {
        return toLocalAbsolutePath(itemId).map(absolutePath -> this.getRootPath().relativize(absolutePath));
    }

    private Optional<URI> toLocalRelativeURI(final String itemId) {
        return toLocalAbsolutePath(itemId)
            .map(absolutePath -> this.getRootPath().toUri().relativize(absolutePath.toUri()));
    }

    @Override
    public URI toKnimeUrl(final String itemId) {
        if (Space.ROOT_ITEM_ID.equals(itemId)) {
            // for historical reasons, the local space root gets mapped to "knime://LOCAL/" (note the trailing slash!)
            return URI.create(KnimeUrlType.SCHEME + "://" + LOCAL_SPACE_ID.toUpperCase(Locale.ROOT) + "/");
        }
        final var relativeUri =
            toLocalRelativeURI(itemId).orElseThrow(() -> new IllegalStateException("No item found for id " + itemId));
        if (relativeUri.isAbsolute()) {
            throw new IllegalStateException(
                "Space item is at path '" + toLocalAbsolutePath(itemId) + "' and thus not inside root '" + getRootPath()
                    + "'");
        }
        try {
            return new URIBuilder(relativeUri) //
                .setScheme(KnimeUrlType.SCHEME) //
                .setHost(LOCAL_SPACE_ID.toUpperCase(Locale.ROOT)) //
                .build();
        } catch (URISyntaxException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public Path getRootPath() {
        return m_rootPath;
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
            for (var itemId : itemIds) {
                var path = m_spaceItemPathAndTypeCache.getPath(itemId);
                if (path == null) {
                    continue;
                }
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

        var sourcePath = toLocalAbsolutePath(null, itemId) //
            .orElseThrow(() -> new IOException("Unknown item ID: '%s'".formatted(itemId)));
        var itemType = m_spaceItemPathAndTypeCache.determineTypeOrGetFromCache((sourcePath));
        var destinationPath = sourcePath.resolveSibling(newName);
        var oldName = sourcePath.getFileName().toString();
        // Path#equals does not distinguish character case on some file systems...
        if (sourcePath.equals(destinationPath)) {
            // ...allow changing the case (upper/lower) in the name anyway
            if (oldName.equals(newName)) {
                return EntityFactory.Space.buildSpaceItemEnt(oldName, itemId, itemType);
            }
        } else if (Files.exists(destinationPath)) {
            throw new ServiceExceptions.OperationNotAllowedException("There already exists a file of that name");
        }

        try {
            if (!sourcePath.toFile().renameTo(destinationPath.toFile())) {
                throw new IOException(
                    "Check if the workflow folder or a contained folder is open by another application and "
                        + "if there are sufficient permissions.");
            }
        } catch (SecurityException e) {
            throw new IOException(e);
        }

        m_spaceItemPathAndTypeCache.update(itemId, sourcePath, destinationPath);

        return EntityFactory.Space.buildSpaceItemEnt(newName, itemId, itemType);
    }

    @Override
    public void moveOrCopyItems(final List<String> itemIds, final String destItemId,
        final Space.NameCollisionHandling collisionHandling, final boolean copy) throws IOException {
        if (itemIds.contains(Space.ROOT_ITEM_ID)) {
            throw new IllegalArgumentException("The root of the space cannot be moved.");
        }
        if (itemIds.contains(destItemId)) {
            throw new IllegalArgumentException("Cannot move a space item to itself");
        }
        assertAllItemIdsExistOrElseThrow(Stream.concat(itemIds.stream(), Stream.of(destItemId)).toList());
        var destPathParent = getAbsolutePath(destItemId);
        if (m_spaceItemPathAndTypeCache.determineTypeOrGetFromCache(destPathParent) != TypeEnum.WORKFLOWGROUP) {
            throw new IllegalArgumentException("Cannot move space items to a location that is not a workflow group");
        }

        Map<String, Pair<Path, Path>> newItemIdToPathMap = new HashMap<>();
        try {
            for (var itemId : itemIds) {
                var srcPath = m_spaceItemPathAndTypeCache.getPath(itemId);
                var destPath = moveItem(srcPath, destPathParent, collisionHandling, copy);
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

    /**
     * @see this#resolveWithNameCollisions(Path, String, NameCollisionHandling, Supplier)
     */
    private Path resolveWithNameCollisions(final String parentId, final Path filePath,
        final NameCollisionHandling requestedStrategy, final Supplier<String> uniqueName) throws IOException {
        var parentWorkflowGroupPath = getAbsolutePath(parentId);
        var fileName = filePath.getFileName().toString();
        return resolveWithNameCollisions(parentWorkflowGroupPath, fileName, requestedStrategy, uniqueName);
    }

    /**
     * Resolve the given {@code fileName} against the given {@code parentPath}, resolving name collisions as according
     * to {@code requestedStrategy}
     */
    @SuppressWarnings("java:S1151")
    private Path resolveWithNameCollisions(final Path parentPath, final String fileName,
        final NameCollisionHandling requestedStrategy, final Supplier<String> uniqueName) throws IOException {
        return switch (requestedStrategy) {
            case NOOP -> parentPath.resolve(fileName);
            case AUTORENAME -> parentPath.resolve(uniqueName.get());
            case OVERWRITE -> {
                var destination = parentPath.resolve(fileName);
                try {
                    deleteItems(List.of(getItemId(destination)));
                } catch (Exception ex) { // NOSONAR
                    LOGGER.error(ex);
                    throw new IOException(String.format(
                        "There was an error overwriting \"%s\". Check that it is not currently open.", fileName), ex);
                }
                yield destination;
            }
        };
    }

    @Override
    public SpaceItemEnt importFile(final Path srcPath, final String workflowGroupItemId,
        final NameCollisionHandling collisionHandling, final IProgressMonitor progress) throws IOException {
        var parentWorkflowGroupPath = getAbsolutePath(workflowGroupItemId);
        var fileName = srcPath.getFileName().toString();
        Supplier<String> uniqueName = () -> generateUniqueSpaceItemName(parentWorkflowGroupPath, fileName, false);
        var destPath = resolveWithNameCollisions(workflowGroupItemId, srcPath, collisionHandling, uniqueName);

        FileUtil.copy(srcPath.toFile(), destPath.toFile());

        return getSpaceItemEntFromPathAndUpdateCache(destPath);
    }

    @Override
    public SpaceItemEnt importWorkflowOrWorkflowGroup(final Path srcPath, final String workflowGroupItemId,
        final Consumer<Path> createMetaInfoFileFor, final Space.NameCollisionHandling collisionHandling,
        final IProgressMonitor progressMonitor) throws IOException {
        var parentWorkflowGroupPath = getAbsolutePath(workflowGroupItemId);
        var tmpDir = FileUtil.createTempDir(srcPath.getFileName().toString());
        FileUtil.unzip(srcPath.toFile(), tmpDir);
        var tmpSrcPath = tmpDir.listFiles()[0].toPath();
        var fileName = tmpSrcPath.getFileName().toString();

        Supplier<String> uniqueName = () -> generateUniqueSpaceItemName(parentWorkflowGroupPath, fileName, true);
        var destPath = resolveWithNameCollisions(workflowGroupItemId, tmpSrcPath, collisionHandling, uniqueName);

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
        if (!path.startsWith(m_rootPath)) {
            // item not below the root (happens with Team Spaces opened in Classic AP, startup crash reported by Bernd)
            return List.of();
        }
        final var res = new ArrayList<String>();
        var parent = path;
        while (!(parent = parent.getParent()).equals(m_rootPath)) {
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
     * @return The item's path after it was moved.
     */
    private Path moveItem(final Path srcPath, final Path destPathParent,
        final Space.NameCollisionHandling collisionHandling, final boolean copy) throws IOException {
        final var type = m_spaceItemPathAndTypeCache.determineTypeOrGetFromCache(srcPath);
        final var fileName = srcPath.getFileName().toString();

        final Supplier<String> uniqueName = () -> {
            var isWorkflowOrWorkflowGroup = type == TypeEnum.WORKFLOW || type == TypeEnum.WORKFLOWGROUP;
            return generateUniqueSpaceItemName(destPathParent, fileName, isWorkflowOrWorkflowGroup);
        };
        final var destPath =
            resolveWithNameCollisions(destPathParent, srcPath.getFileName().toString(), collisionHandling, uniqueName);

        if (Files.exists(destPath)) {
            throw new IOException(
                String.format("Attempting to overwrite <%s>, name collision handling went wrong.", destPath));
        }

        if (copy) {
            FileUtil.copyDir(srcPath.toFile(), destPath.toFile());
            return destPath;
        }

        try {
            // Moving within the same file system, simple move can be applied
            return Files.move(srcPath, destPath, StandardCopyOption.ATOMIC_MOVE);
        } catch (final AtomicMoveNotSupportedException e) { // NOSONAR no need to log or rethrow
            // Moving across different file systems, simple move isn't possible
            FileUtil.copyDir(srcPath.toFile(), destPath.toFile());
            FileUtil.deleteRecursively(srcPath.toFile()); // Delete the remaining space item
            return destPath;
        }
    }

    /**
     * Creates a directory into which a currently opened workflow can be saved.
     *
     * @param workflowGroupItemId enclosing workflow group
     * @param workflowName name of the workflow to be saved
     * @param collisionHandling collision handling if the workflow's name is already taken
     * @return path to an empty, newly created directory
     * @throws IOException if directory creation failed
     */
    public Path createWorkflowDir(final String workflowGroupItemId, final String workflowName,
        final Space.NameCollisionHandling collisionHandling) throws IOException {
        var destPathParent = getAbsolutePath(workflowGroupItemId);

        Supplier<String> uniqueName = () -> generateUniqueSpaceItemName(destPathParent, workflowName, true);
        var destPath = resolveWithNameCollisions(getAbsolutePath(workflowGroupItemId), workflowName, collisionHandling,
            uniqueName);

        if (Files.exists(destPath)) {
            throw new IOException(
                String.format("Attempting to overwrite <%s>, name collision handling went wrong.", destPath));
        }
        Files.createDirectory(destPath);
        return destPath;
    }

    private SpaceItemEnt getSpaceItemEntFromPathAndUpdateCache(final Path absolutePath) {
        var id = m_spaceItemPathAndTypeCache.determineItemIdOrGetFromCache(absolutePath);
        var type = m_spaceItemPathAndTypeCache.determineTypeOrGetFromCache(absolutePath);
        return EntityFactory.Space.buildLocalSpaceItemEnt(absolutePath, m_rootPath, id, type);
    }

    Path getAbsolutePath(final String workflowGroupItemId) throws NoSuchElementException {
        var absolutePath = m_spaceItemPathAndTypeCache.getPath(workflowGroupItemId);
        if (absolutePath == null) {
            throw new NoSuchElementException("Unknown item id '" + workflowGroupItemId + "'");
        }
        if (m_spaceItemPathAndTypeCache.determineTypeOrGetFromCache(absolutePath) != TypeEnum.WORKFLOWGROUP) {
            throw new NoSuchElementException("The item with id '" + workflowGroupItemId + "' is not a workflow group");
        }
        return absolutePath;
    }

    /**
     * Checks whether the given path is a valid item or a hidden, internal path.
     *
     * @param path file or directory in the workspace
     * @return {@code true} if the item is a workspace item, {@code false} otherwise
     */
    public static boolean isValidItem(final Path path) {
        try {
            var filename = path.getFileName().toString();
            return !Files.isHidden(path) //
                && !WorkflowPersistor.METAINFO_FILE.equals(filename) //
                && !filename.equals(".metadata");
        } catch (IOException ex) {
            LOGGER.warnWithFormat("Failed to evaluate 'isHidden' on path %s. Path ignored.", ex);
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

    @Override
    public String getItemName(final String itemId) throws NoSuchElementException {
        return Optional.ofNullable(m_spaceItemPathAndTypeCache.getPath(itemId))//
            .map(Path::getFileName)//
            .map(Path::toString)//
            .orElseThrow(() -> new NoSuchElementException(String.format("No item with <%s> present in cache", itemId)));
    }

    /**
     * Generates unique space item names, preserves file extensions
     *
     * @return The initial name if that doesn't exist, the unique one otherwise.
     */
    private static String generateUniqueSpaceItemName(final Path workflowGroup, final String name,
        final boolean isWorkflowOrWorkflowGroup) {
        return Space.generateUniqueSpaceItemName(newName -> Files.exists(workflowGroup.resolve(newName)), name,
            isWorkflowOrWorkflowGroup);
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
     * Verify that the given name is a valid name for an item in a {@link LocalSpace}. See
     * <ul>
     * <li>FileStoreNameValidator#isValid</li>
     * <li>ExplorerFileSystem#validateFilename</li>
     * </ul>
     * 
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

    @Override
    public Optional<String> getItemIdByURI(final URI uri) {
        if (KnimeUrlType.getType(uri).orElse(null) != KnimeUrlType.MOUNTPOINT_ABSOLUTE
            || !"LOCAL".equals(uri.getAuthority())) {
            return Optional.empty();
        }
        try {
            final var path = ResolverUtil.resolveURItoLocalFile(uri).toPath().toAbsolutePath();
            return Optional.of(m_spaceItemPathAndTypeCache.determineItemIdOrGetFromCache(path));
        } catch (final ResourceAccessException e) {
            throw new IllegalArgumentException("URI could not be resolved to local file", e);
        }
    }

    @Override
    public Optional<ProjectTypeEnum> getProjectType(final String itemId) {
        var path = toLocalAbsolutePath(null, itemId).orElse(null);
        if (path == null) {
            return Optional.empty();
        }
        var itemType = m_spaceItemPathAndTypeCache.determineTypeOrGetFromCache(path);
        return EntityUtil.toProjectType(itemType);
    }

    private static final EnumSet<TypeEnum> WORKFLOW_LIKE =
        EnumSet.of(TypeEnum.WORKFLOW, TypeEnum.COMPONENT, TypeEnum.WORKFLOWTEMPLATE);

    /**
     * Checks whether an item exists at the given path below a workflow group and determines how such a collision can be
     * resolved.
     *
     * @param workflowGroupId root workflow group's ID
     * @param path path below the root group
     * @param newItemType type of the new item to be added
     * @return location and type of the colision if one exists, {@link Optional#empty()} otherwise
     */
    public Optional<Pair<IPath, Collision>> checkForCollision(final String workflowGroupId, final IPath path,
        final TypeEnum newItemType) {

        var currentId = workflowGroupId;  // NOSONAR: assignment is useful
        var current = getAbsolutePath(workflowGroupId);
        var currentType = getItemType(workflowGroupId);

        // descend the existing tree and check all levels for conflicts
        for (var level = 0; level < path.segmentCount(); level++) {
            if (currentType == TypeEnum.WORKFLOWGROUP) {
                // old and new are folders, descend
                current = current.resolve(path.segment(level));
                if (!Files.exists(current)) {
                    // ancestor item doesn't exist, no conflict
                    return Optional.empty();
                }
                currentId = getItemId(current);
                currentType = getItemType(currentId);
            } else {
                // conflict between a new ancestor folder and an existing non-folder item
                return Optional.of(Pair.create(path.uptoSegment(level), new Collision(false, false, true)));
            }
        }

        // reached the end, and an item at the path already exists
        final boolean existingIsFolder = currentType == TypeEnum.WORKFLOWGROUP;
        final boolean newIsFolder = newItemType == TypeEnum.WORKFLOWGROUP;
        if (existingIsFolder && newIsFolder) {
            // copying an empty folder over an existing one is not a conflict
            return Optional.empty();
        } else if (existingIsFolder || newIsFolder) {
            // conflict between leaf item and folder
            return Optional.of(Pair.create(path, new Collision(false, false, true)));
        } else {
            // collision between two leaf items
            final boolean typesCompatible = (currentType == newItemType)
                || (WORKFLOW_LIKE.contains(currentType) && WORKFLOW_LIKE.contains(newItemType));
            var localProjectWithId = ProjectManager.getInstance().getProject( //
                SpaceProvider.LOCAL_SPACE_PROVIDER_ID, //
                LocalSpace.LOCAL_SPACE_ID, //
                currentId //
            );
            final var isOpenedAsProject = localProjectWithId.isPresent();
            return Optional.of(Pair.create(path, new Collision(typesCompatible, !isOpenedAsProject, true)));
        }
    }

}
