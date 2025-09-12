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
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipInputStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.workflow.WorkflowExporter;
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
import org.knime.gateway.api.util.VersionId;
import org.knime.gateway.api.webui.entity.SpaceEnt;
import org.knime.gateway.api.webui.entity.SpaceItemEnt;
import org.knime.gateway.api.webui.entity.SpaceItemEnt.TypeEnum;
import org.knime.gateway.api.webui.entity.SpaceItemReferenceEnt.ProjectTypeEnum;
import org.knime.gateway.api.webui.entity.WorkflowGroupContentEnt;
import org.knime.gateway.api.webui.service.util.MutableServiceCallException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.OperationNotAllowedException;
import org.knime.gateway.api.webui.util.EntityFactory;
import org.knime.gateway.impl.project.ProjectManager;
import org.knime.gateway.impl.project.WorkflowServiceProjects;
import org.knime.gateway.impl.webui.spaces.Collision;
import org.knime.gateway.impl.webui.spaces.Space;
import org.knime.gateway.impl.webui.spaces.SpaceProvider;

/**
 * {@link Space}-implementation that represents the local Eclipse RCP workspace.
 * <p>
 * This state may become stale if the workspace directory contents are modified by other means, for example if
 * projects/workflows are deleted via the OS file explorer.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @author Kai Franze, KNIME GmbH
 * @author Benjamin Moser, KNIME GmbH
 */
public final class LocalSpace implements Space {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(LocalSpace.class);

    private static final Pattern KNWF_KNAR_FILE_EXTENSION =
        Pattern.compile("\\.(knwf|knar)$", Pattern.CASE_INSENSITIVE);

    /**
     * ID of the single {@link Space} provider by the {@link LocalSpaceProvider}
     */
    public static final String LOCAL_SPACE_ID = "local";

    /**
     * Holds both, the item ID to path map and the path to type map
     */
    @SuppressWarnings("WeakerAccess") // Package scope for testing
    final LocalSpaceItemPathAndTypeCache m_spaceItemPathAndTypeCache;

    private final Path m_rootPath;

    private final Set<Consumer<String>> m_itemRemovedListeners = new HashSet<>();

    /**
     * -
     *
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
    public TypeEnum getItemType(final String itemId) throws MutableServiceCallException {
        final var itemPath = resolveItemPath(itemId);
        return m_spaceItemPathAndTypeCache.determineTypeOrGetFromCache(itemPath);
    }

    @Override
    public WorkflowGroupContentEnt listWorkflowGroup(final String workflowGroupItemId)
        throws MutableServiceCallException {

        final var absolutePath = getAbsolutePath(workflowGroupItemId);
        try {
            return EntityFactory.Space.buildLocalWorkflowGroupContentEnt(absolutePath, m_rootPath, this::getItemId,
                m_spaceItemPathAndTypeCache::determineTypeOrGetFromCache, LocalSpace::isValidItem, ITEM_COMPARATOR);
        } catch (final IOException ex) {
            throw new MutableServiceCallException(ex.getMessage(), true, ex)
                .addDetails("Failed to list folder '%s'.".formatted(absolutePath));
        }
    }

    @Override
    public SpaceItemEnt createWorkflow(final String workflowGroupItemId, final String workflowName)
        throws MutableServiceCallException {

        final Path parentWorkflowGroupPath;
        try {
            parentWorkflowGroupPath = getAbsolutePath(workflowGroupItemId);
        } catch (MutableServiceCallException ex) {
            ex.addDetails("Failed to determine parent folder for new workflow '%s'.".formatted(workflowName));
            throw ex;
        }

        final var pathToCreate =
            parentWorkflowGroupPath.resolve(generateUniqueSpaceItemName(parentWorkflowGroupPath, workflowName, true));
        try {
            var workflowDir = Files.createDirectory(pathToCreate);
            Files.createFile(workflowDir.resolve(WorkflowPersistor.WORKFLOW_FILE));
            return getSpaceItemEntFromPathAndUpdateCache(workflowDir);
        } catch (final IOException e) {
            throw new MutableServiceCallException(e.getMessage(), true, e)
                .addDetails("Failed to create workflow '%s'.".formatted(pathToCreate));
        }
    }

    @Override
    public SpaceItemEnt createWorkflowGroup(final String workflowGroupItemId) throws MutableServiceCallException {
        final Path parentWorkflowGroupPath;
        try {
            parentWorkflowGroupPath = getAbsolutePath(workflowGroupItemId);
        } catch (MutableServiceCallException ex) {
            ex.addDetails("Failed to determine parent folder.");
            throw ex;
        }

        final var workflowGroupName =
            generateUniqueSpaceItemName(parentWorkflowGroupPath, DEFAULT_WORKFLOW_GROUP_NAME, false);
        final var pathToCreate = parentWorkflowGroupPath.resolve(workflowGroupName);
        try {
            var directoryPath = Files.createDirectory(pathToCreate);
            return getSpaceItemEntFromPathAndUpdateCache(directoryPath);
        } catch (final IOException e) {
            throw new MutableServiceCallException(e.getMessage(), true, e)
                .addDetails("Failed to create folder '%s'.".formatted(pathToCreate));
        }
    }

    @Override
    public Optional<Path> toLocalAbsolutePath(final ExecutionMonitor monitor, final String itemId,
        final VersionId version) {
        if (!version.isCurrentState()) {
            return Optional.empty(); // currently not supported by this implementation
        }
        return toLocalAbsolutePath(itemId);
    }

    /**
     * Resolves the item with the given ID into a local file.
     *
     * @param itemId ID if the item to resolve
     * @return the local path of the item and if available, empty if not available
     */
    @Override
    public Optional<Path> toLocalAbsolutePath(final String itemId) {
        var path = m_spaceItemPathAndTypeCache.getPath(itemId);
        return path != null && Files.exists(path) ? Optional.of(path) : Optional.empty();
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
            throw new IllegalStateException("Space item is at path '" + toLocalAbsolutePath(itemId)
                + "' and thus not inside root '" + getRootPath() + "'");
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

    /**
     * Root path of the workspace.
     *
     * @return absolute path of the workspace's root folder
     */
    public Path getRootPath() {
        return m_rootPath;
    }

    @Override
    public LocationInfo getLocationInfo(final String itemId, final VersionId version) {
        return LocalLocationInfo.getInstance(null);
    }

    @Override
    public void deleteItems(final List<String> itemIds, final boolean softDelete)
        throws MutableServiceCallException, OperationNotAllowedException {

        // Check if the root is part of the IDs
        if (itemIds.contains(Space.ROOT_ITEM_ID)) {
            throw OperationNotAllowedException.builder().withTitle("The root of the space cannot be deleted.")
                .withDetails().canCopy(false).build();
        }

        assertAllItemIdsExistOrElseThrow(itemIds);

        var deletedItems = new ArrayList<DeletedItem>(itemIds.size());
        try {
            for (var itemId : itemIds) {
                var path = m_spaceItemPathAndTypeCache.getPath(itemId);
                if (path == null) {
                    continue;
                }

                // `WorkflowServiceProjects` is a view on a subset of Projects in ProjectManager
                // used as e.g. callees of "Call Workflow" nodes. Additionally, it is backed by
                // an external cache of WorkflowManagers, from which items are removed only after
                // an expiry timeout. This might prevent deletion of these files.
                WorkflowServiceProjects.clearCached(path);

                PathUtils.deleteDirectoryIfExists(path);
                deletedItems.add(new DeletedItem(itemId, path));
            }
        } catch (final IOException e) {
            throw new MutableServiceCallException(e.getMessage(), true, e).addDetails("Failed to delete item.");
        } finally {
            deletedItems.forEach(deletedItem -> {
                m_spaceItemPathAndTypeCache.prunePath(deletedItem.path());
                m_itemRemovedListeners.forEach(listener -> listener.accept(deletedItem.itemId()));
            });
        }
    }

    @Override
    public SpaceItemEnt renameItem(final String itemId, final String queriedName)
        throws OperationNotAllowedException, MutableServiceCallException {

        if (itemId.equals(Space.ROOT_ITEM_ID)) {
            throw OperationNotAllowedException.builder() //
                .withTitle("Cannot rename root item") //
                .withDetails("The root item of the local space cannot be renamed.") //
                .canCopy(false) //
                .build();
        }

        final var sourcePath = toLocalAbsolutePath(itemId)
            .orElseThrow(() -> new MutableServiceCallException("Unknown item ID: '%s'.".formatted(itemId), true, null));
        final var originalName = sourcePath.getFileName().toString();
        final var newName = queriedName.trim();
        assertValidItemNameOrThrow(newName);
        final var itemType = m_spaceItemPathAndTypeCache.determineTypeOrGetFromCache((sourcePath));
        final var destinationPath = sourcePath.resolveSibling(newName); // with potentially different casing

        // If only case changes, we still want the rename operation to be carried out: it is possible to change case via
        // rename e.g. on macOS, even though the file system is case-insensitive w.r.t. existence or equality checks.
        if (originalName.equals(newName)) { // String comparison here is case-sensitive
            return EntityFactory.Space.buildSpaceItemEnt(originalName, itemId, itemType);
        }

        // If these are the same file on the file system (potentially case-insensitive, as indicated by
        // Files#isSameFile), then do not throw here and still carry out the rename: as per the above if-return we may
        // be currently changing case of an existing file. If it is a different file but existing, then we indeed have a
        // collision.
        final boolean isExistingDifferentFile;
        try {
            isExistingDifferentFile = Files.exists(destinationPath) && !Files.isSameFile(sourcePath, destinationPath);
        } catch (final IOException ex) {
            throw new MutableServiceCallException(ex.getMessage(), true, ex)
                .addDetails("Failed to check for name collision (%s).".formatted(ex.getClass().getSimpleName()));
        }
        if (isExistingDifferentFile) {
            throw new MutableServiceCallException(
                "There already exists a %s with that name. Pick a different name or rename the other item first."
                    .formatted(getReadableFileType(destinationPath)),
                false, null);
        }

        // Otherwise, we are either changing case or have no collision.
        try {
            if (!sourcePath.toFile().renameTo(destinationPath.toFile())) {
                throw new MutableServiceCallException(
                    "Check if the workflow folder or a contained folder is opened by another application and if "
                        + "there are sufficient permissions.",
                    false, null);
            }
        } catch (final SecurityException e) {
            throw new MutableServiceCallException(e.getMessage(), true, e)
                .addDetails("Failed to rename item '%s'.".formatted(itemId));
        }

        m_spaceItemPathAndTypeCache.update(itemId, sourcePath, destinationPath);
        return EntityFactory.Space.buildSpaceItemEnt(newName, itemId, itemType);
    }

    private String getReadableFileType(final Path destinationPath) {
        return switch (m_spaceItemPathAndTypeCache.getSpaceItemType(destinationPath)) {
            case COMPONENT -> "component";
            case DATA -> "data file";
            case WORKFLOW -> "workflow";
            case WORKFLOWGROUP -> "workflow group";
            case WORKFLOWTEMPLATE -> "workflow template";
            default -> {
                final var symLinkOrFile = Files.isSymbolicLink(destinationPath) ? "symbolic link" : "file";
                yield Files.isDirectory(destinationPath) ? "directory" : symLinkOrFile;
            }
        };
    }

    @Override
    public void moveOrCopyItems(final List<String> itemIds, final String destItemId,
        final Space.NameCollisionHandling collisionHandling, final boolean copy)
        throws OperationNotAllowedException, MutableServiceCallException {

        if (itemIds.contains(Space.ROOT_ITEM_ID)) {
            throw OperationNotAllowedException.builder() //
                .withTitle("Cannot move root item") //
                .withDetails("The root item of the local space cannot be moved.") //
                .canCopy(false) //
                .build();
        }
        if (itemIds.contains(destItemId)) {
            throw OperationNotAllowedException.builder() //
                .withTitle("Cannot move items to themselves") //
                .withDetails("Cannot move items to themselves or their descendants.") //
                .canCopy(false) //
                .build();
        }
        assertAllItemIdsExistOrElseThrow(Stream.concat(itemIds.stream(), Stream.of(destItemId)).toList());
        var destPathParent = getAbsolutePath(destItemId);
        if (m_spaceItemPathAndTypeCache.determineTypeOrGetFromCache(destPathParent) != TypeEnum.WORKFLOWGROUP) {
            throw new MutableServiceCallException(
                "Cannot move space item to a location that is not a workflow group. (Item ID: %s, destination: %s)."
                    .formatted(destItemId, destPathParent),
                false, null);
        }

        Map<String, Pair<Path, Path>> newItemIdToPathMap = new HashMap<>();
        try {
            for (var itemId : itemIds) {
                var srcPath = m_spaceItemPathAndTypeCache.getPath(itemId);
                var destPath = moveItem(srcPath, destPathParent, collisionHandling, copy);
                newItemIdToPathMap.put(itemId, Pair.create(srcPath, destPath)); // Keep the ones that actually moved
            }
        } finally { // Update map for all the items that were actually moved
            newItemIdToPathMap.forEach((itemId, value) -> {
                var oldPath = value.getFirst();
                var newPath = value.getSecond();
                m_spaceItemPathAndTypeCache.update(itemId, oldPath, newPath);
            });
        }
    }

    /**
     * @throws MutableServiceCallException
     * @see this#resolveWithNameCollisions(Path, String, NameCollisionHandling, Supplier)
     */
    private Path resolveWithNameCollisions(final String parentId, final String fileName,
        final NameCollisionHandling requestedStrategy, final Supplier<String> uniqueName)
        throws MutableServiceCallException {
        var parentWorkflowGroupPath = getAbsolutePath(parentId);
        return resolveWithNameCollisions(parentWorkflowGroupPath, fileName, requestedStrategy, uniqueName);
    }

    /**
     * Resolve the given {@code fileName} against the given {@code parentPath}, resolving name collisions as according
     * to {@code requestedStrategy}
     *
     * @throws MutableServiceCallException
     */
    @SuppressWarnings("java:S1151")
    private Path resolveWithNameCollisions(final Path parentPath, final String fileName,
        final NameCollisionHandling requestedStrategy, final Supplier<String> uniqueName)
        throws MutableServiceCallException {

        return switch (requestedStrategy) {
            case NOOP -> parentPath.resolve(fileName);
            case AUTORENAME -> parentPath.resolve(uniqueName.get());
            case OVERWRITE -> {
                var destination = parentPath.resolve(fileName);
                try {
                    deleteItems(List.of(getItemId(destination)), false);
                } catch (MutableServiceCallException ex) {
                    ex.addDetails(
                        "Failed to overwrite \"%s\". Check that it is not currently open.".formatted(fileName));
                    throw ex;
                } catch (Exception ex) { // NOSONAR
                    LOGGER.error(ex);
                    throw new MutableServiceCallException(ex.getMessage(), true, ex).addDetails(
                        "Failed to overwrite \"%s\". Check that it is not currently open.".formatted(fileName));
                }
                yield destination;
            }
        };
    }

    @Override
    public SpaceItemEnt importFile(final Path srcPath, final String workflowGroupItemId,
        final NameCollisionHandling collisionHandling, final IProgressMonitor progress)
        throws MutableServiceCallException {

        var parentWorkflowGroupPath = getAbsolutePath(workflowGroupItemId);
        var fileName = srcPath.getFileName().toString();
        Supplier<String> uniqueName = () -> generateUniqueSpaceItemName(parentWorkflowGroupPath, fileName, false);

        final var destPath = resolveWithNameCollisions(workflowGroupItemId, fileName, collisionHandling, uniqueName);
        try {
            FileUtil.copy(srcPath.toFile(), destPath.toFile());
        } catch (final IOException ex) {
            throw new MutableServiceCallException(ex.getMessage(), true, ex)
                .addDetails("Failed to copy item into workspace.");
        }

        return getSpaceItemEntFromPathAndUpdateCache(destPath);
    }

    @Override
    public SpaceItemEnt importWorkflowOrWorkflowGroup(final Path srcPath, final String workflowGroupItemId,
        final Consumer<Path> createMetaInfoFileFor, final Space.NameCollisionHandling collisionHandling,
        final IProgressMonitor progressMonitor) throws MutableServiceCallException {

        final var parentWorkflowGroupPath = getAbsolutePath(workflowGroupItemId);
        final var fileName = KNWF_KNAR_FILE_EXTENSION.matcher(srcPath.getFileName().toString()).replaceAll("").trim();
        final var destPath = resolveWithNameCollisions(workflowGroupItemId, fileName, collisionHandling,
            () -> generateUniqueSpaceItemName(parentWorkflowGroupPath, fileName, true));
        try {
            final var singleRootFolder = WorkflowExporter.hasZipSingleRootFolder(srcPath);
            try (final var zipInput = new ZipInputStream(Files.newInputStream(srcPath))) {
                FileUtil.unzip(zipInput, destPath.toFile(), singleRootFolder ? 1 : 0);
            }
        } catch (final IOException ex) {
            throw new MutableServiceCallException("Failed to move item(s) to workspace.", true, ex);
        }
        createMetaInfoFileFor.accept(destPath);
        return getSpaceItemEntFromPathAndUpdateCache(destPath);
    }

    @Override
    public List<String> getAncestorItemIds(final String itemId) throws MutableServiceCallException {
        if (ROOT_ITEM_ID.equals(itemId)) {
            return List.of();
        }

        final var path = resolveItemPath(itemId);
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
    public Optional<String> getItemIdForName(final String workflowGroupItemId, final String itemName)
        throws MutableServiceCallException {

        final var workflowGroup = getAbsolutePath(workflowGroupItemId);
        final var itemPath = workflowGroup.resolve(itemName);
        if (Files.exists(itemPath)) {
            return Optional.of(m_spaceItemPathAndTypeCache.determineItemIdOrGetFromCache(itemPath));
        } else {
            return Optional.empty();
        }
    }

    /**
     * @return The item's path after it was moved.
     * @throws MutableServiceCallException
     */
    private Path moveItem(final Path srcPath, final Path destPathParent,
        final Space.NameCollisionHandling collisionHandling, final boolean copy) throws MutableServiceCallException {
        final var type = m_spaceItemPathAndTypeCache.determineTypeOrGetFromCache(srcPath);
        final var fileName = srcPath.getFileName().toString();

        final Supplier<String> uniqueName = () -> {
            var isWorkflowOrWorkflowGroup = type == TypeEnum.WORKFLOW || type == TypeEnum.WORKFLOWGROUP;
            return generateUniqueSpaceItemName(destPathParent, fileName, isWorkflowOrWorkflowGroup);
        };

        final var destPath =
            resolveWithNameCollisions(destPathParent, srcPath.getFileName().toString(), collisionHandling, uniqueName);

        if (Files.exists(destPath)) {
            throw new MutableServiceCallException("There already exists an item at the target location", true, null);
        }

        if (copy) {
            try {
                FileUtil.copyDir(srcPath.toFile(), destPath.toFile());
                return destPath;
            } catch (final IOException e) {
                throw new MutableServiceCallException(e.getMessage(), true, e)
                    .addDetails("Failed to copy '%s' to '%s'.".formatted(srcPath, destPath));
            }
        }

        try {
            try { // NOSONAR
                  // Moving within the same file system, simple move can be applied
                return Files.move(srcPath, destPath, StandardCopyOption.ATOMIC_MOVE);
            } catch (final AtomicMoveNotSupportedException e) { // NOSONAR no need to log or rethrow
                // Moving across different file systems, simple move isn't possible
                FileUtil.copyDir(srcPath.toFile(), destPath.toFile());
                FileUtil.deleteRecursively(srcPath.toFile()); // Delete the remaining space item
                return destPath;
            }
        } catch (final IOException e) {
            throw new MutableServiceCallException(e.getMessage(), true, e)
                .addDetails("Failed to move '%s' to '%s'.".formatted(srcPath, destPath));
        }
    }

    /**
     * Creates a directory into which a currently opened workflow can be saved.
     *
     * @param workflowGroupItemId enclosing workflow group
     * @param workflowName name of the workflow to be saved
     * @param collisionHandling collision handling if the workflow's name is already taken
     * @return path to an empty, newly created directory
     * @throws MutableServiceCallException
     */
    public Path createWorkflowDir(final String workflowGroupItemId, final String workflowName,
        final Space.NameCollisionHandling collisionHandling) throws MutableServiceCallException {
        var destPathParent = getAbsolutePath(workflowGroupItemId);

        Supplier<String> uniqueName = () -> generateUniqueSpaceItemName(destPathParent, workflowName, true);
        var destPath = resolveWithNameCollisions(getAbsolutePath(workflowGroupItemId), workflowName, collisionHandling,
            uniqueName);

        if (Files.exists(destPath)) {
            throw new MutableServiceCallException(
                "Attempting to overwrite '%s', name collision handling went wrong.".formatted(destPath), false, null);
        }

        try {
            Files.createDirectory(destPath);
            return destPath;
        } catch (final IOException ex) {
            throw new MutableServiceCallException(ex.getMessage(), true, ex)
                .addDetails("Failed to create workflow directory.");
        }
    }

    private SpaceItemEnt getSpaceItemEntFromPathAndUpdateCache(final Path absolutePath) {
        var id = m_spaceItemPathAndTypeCache.determineItemIdOrGetFromCache(absolutePath);
        var type = m_spaceItemPathAndTypeCache.determineTypeOrGetFromCache(absolutePath);
        return EntityFactory.Space.buildLocalSpaceItemEnt(absolutePath, m_rootPath, id, type);
    }

    private Path resolveItemPath(final String itemId) throws MutableServiceCallException {
        final var path = m_spaceItemPathAndTypeCache.getPath(itemId);
        if (path == null) {
            throw new MutableServiceCallException("Item ID '%s' is invalid for the local workspace.".formatted(itemId),
                false, null);
        }
        return path;
    }

    Path getAbsolutePath(final String workflowGroupItemId) throws MutableServiceCallException {
        final var absolutePath = resolveItemPath(workflowGroupItemId);
        final var type = m_spaceItemPathAndTypeCache.determineTypeOrGetFromCache(absolutePath);
        if (type != TypeEnum.WORKFLOWGROUP) {
            throw new MutableServiceCallException(
                "Expected item '%s' to be a folder, found '%s'.".formatted(workflowGroupItemId, type), false, null);
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
    public String getItemName(final String itemId) throws MutableServiceCallException {
        final var itemPath = resolveItemPath(itemId);
        return itemPath.getFileName().toString();
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

    private void assertAllItemIdsExistOrElseThrow(final List<String> itemIds) throws MutableServiceCallException {
        var unknownItemIds = itemIds.stream()//
            .filter(id -> !m_spaceItemPathAndTypeCache.containsKey(id))//
            .collect(Collectors.joining(", "));
        if (!unknownItemIds.isEmpty()) {
            throw new MutableServiceCallException("Unknown item ids: %s.".formatted(unknownItemIds), false, null);
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
        String detail = null;
        if (StringUtils.isBlank(name)) {
            detail = "Name cannot be empty or blank: '%s'.".formatted(name);
        } else if (Path.of(name).getParent() != null) {
            detail = "Name cannot be a path: '%s'.".formatted(name);
        } else if (name.startsWith(".")) {
            detail = "Name cannot start with dot: '%s'.".formatted(name);
        } else if (name.endsWith(".")) {
            detail = "Name cannot end with dot: '%s'.".formatted(name);
        } else if (FileUtil.ILLEGAL_FILENAME_CHARS_PATTERN.matcher(name).find()) {
            detail = "Name contains invalid characters (%s): '%s'.".formatted(FileUtil.ILLEGAL_FILENAME_CHARS, name);
        }

        if (detail != null) {
            throw OperationNotAllowedException.builder() //
                .withTitle("Invalid name") //
                .withDetails(detail) //
                .canCopy(false) //
                .build();
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
        var path = toLocalAbsolutePath(itemId).orElse(null);
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
     * @return location and type of the collision if one exists, {@link Optional#empty()} otherwise
     * @throws MutableServiceCallException
     */
    public Optional<Pair<IPath, Collision>> checkForCollision(final String workflowGroupId, final IPath path,
        final TypeEnum newItemType) throws MutableServiceCallException {
        var currentId = workflowGroupId; // NOSONAR: assignment is useful
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

    /**
     * Add a listener that is notified when an item has been successfully removed from the space
     *
     * @param listener Notified with the item ID of the removed item
     */
    public void addItemRemovedListener(final Consumer<String> listener) {
        m_itemRemovedListeners.add(listener);
    }

    private record DeletedItem(String itemId, Path path) {
        //
    }

}
