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
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.workflow.contextv2.LocationInfo;
import org.knime.core.util.Pair;
import org.knime.core.util.exception.ResourceAccessException;
import org.knime.gateway.api.util.VersionId;
import org.knime.gateway.api.webui.entity.SpaceEnt;
import org.knime.gateway.api.webui.entity.SpaceItemEnt;
import org.knime.gateway.api.webui.entity.SpaceItemReferenceEnt.ProjectTypeEnum;
import org.knime.gateway.api.webui.entity.WorkflowGroupContentEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.OperationNotAllowedException;
import org.knime.gateway.impl.webui.spaces.local.LocalSpace;

/**
 * Represents a space in order to abstract from different space implementations (e.g. the local workspace or a hub
 * space).
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @author Kai Franze, KNIME GmbH
 * @author Benjamin Moser, KNIME GmbH
 */
public interface Space {

    /**
     * Comparator used for ordering items inside a space. While names are compared ignoring upper/lower case, ties are
     * broken by comparing the original strings. This guarantees that the order is consistent even if items are only
     * distinguished by case.
     */
    Comparator<SpaceItemEnt> ITEM_COMPARATOR = Comparator.comparing(SpaceItemEnt::getType) //
        .thenComparing(SpaceItemEnt::getName, SpaceItemNameComparator.INSTANCE);

    /**
     * Id of the root 'workflow group'.
     */
    String ROOT_ITEM_ID = "root";

    /**
     * Default name for a newly created workflow.
     */
    String DEFAULT_WORKFLOW_NAME = "KNIME_project";

    /**
     * Default name for a newly created workflow groups.
     */
    String DEFAULT_WORKFLOW_GROUP_NAME = "Folder";

    /**
     * Describe the operation that should be applied to space items to resolve a potential name collision.
     */
    enum NameCollisionHandling {

            /** Do nothing */
            NOOP,

            /** Rename the items */
            AUTORENAME,

            /** Overwrite any existing items with the same identifier */
            OVERWRITE;

        public static Optional<NameCollisionHandling> of(final String string) {
            if (string == null) {
                return Optional.empty();
            }
            try {
                return Optional.of(NameCollisionHandling.valueOf(string));
            } catch (IllegalArgumentException e) { // NOSONAR
                NodeLogger.getLogger(NameCollisionHandling.class).info("Could not parse given NameCollisionHandling",
                    e);
                return Optional.empty();
            }
        }

    }

    /**
     * @return a space id unique within a {@link SpaceProvider}
     */
    String getId();

    /**
     * @return space name
     */
    String getName();

    /**
     * Gets the items.
     *
     * @param workflowGroupItemId the id to list item for
     *
     * @return the items and some metadata
     * @throws NoSuchElementException if the given workflow group item id doesn't refer to a workflow group
     * @throws IOException if the there was a problem with read or fetching the items
     */
    WorkflowGroupContentEnt listWorkflowGroup(String workflowGroupItemId) throws IOException;

    /**
     * Lists the jobs for a workflow for the given id.
     *
     * @param workflowId
     *
     * @return list of jobs where each job is in the format of
     *         com.knime.enterprise.server.rest.api.v4.jobs.ent.WorkflowJob
     */
    default List<Object> listJobsForWorkflow(final String workflowId) {
        return List.of();
    }

    /**
     * Saves the provided job as a workflow on the same space under the given absolute workflow group path.
     *
     * @param workflowGroupPath absolute workflow group path
     * @param workflowName name of the workflow to save under
     * @param jobId id of the job to save
     * @return the newly created space item
     * @throws ResourceAccessException
     */
    default SpaceItemEnt saveJobAsWorkflow(final IPath workflowGroupPath, final String workflowName, final String jobId)
            throws ResourceAccessException {
        throw new UnsupportedOperationException("Saving job as workflow is not supported.");
    }

    /**
     * Delets the given jobs.
     *
     * @param workflowId id of the workflow from which the jobs originated
     * @param jobIds jobs to delete
     * @throws ResourceAccessException if any job could not be deleted
     */
    default void deleteJobsForWorkflow(final String workflowId, final List<String> jobIds)
            throws ResourceAccessException {
        throw new UnsupportedOperationException("Deletion of workflow jobs is not supported.");
    }

    /**
     * Lists the schedules for a workflow for the given id.
     *
     * @param workflowId
     *
     * @return list of schedules where each schedule is in the format of
     *         com.knime.enterprise.server.rest.api.v4.schedule.ent.ScheduledJobInfo
     */
    default List<Object> listSchedulesForWorkflow(final String workflowId) {
        return List.of();
    }

    /**
     * Edit the info of a scheduled job.
     *
     * @param workflowId ID of workflow from which the schedule originated
     * @param scheduleId ID of the scheduled job to edit
     * @return the ID of the scheduled job if it was changed or {@code null} if nothing was changed
     * @throws ResourceAccessException if an exception occured while saving the edited job
     */
    default String editScheduleInfo(final String workflowId, final String scheduleId) throws ResourceAccessException {
        throw new UnsupportedOperationException("Editing schedules is not supported.");
    }

    /**
     * Deletes the given schedules.
     * @param workflowId id of the workflow from which the schedule originated
     * @param scheduleIds schedules to delete
     * @throws ResourceAccessException if any schedule could not be deleted
     */
    default void deleteSchedulesForWorkflow(final String workflowId, final List<String> scheduleIds)
            throws ResourceAccessException {
        throw new UnsupportedOperationException("Deletion of schedules is not supported.");
    }

    /**
     * Creates a new workflow.
     *
     * @param workflowGroupItemId The ID of the workflow group where to create the new workflow
     * @param workflowName name of the new workflow
     * @return The newly created space item
     * @throws IOException If there was a problem creating the files for the new workflow
     * @throws NoSuchElementException If the given workflow group item id doesn't refer to a workflow group
     */
    SpaceItemEnt createWorkflow(String workflowGroupItemId, String workflowName) throws IOException;

    /**
     * Creates a new workflow group within a given workflow group.
     *
     * @param workflowGroupItemId The ID of the workflow group where to create the new workflow group
     * @return the newly created space item
     * @throws IOException If there was a problem creating the folder
     * @throws NoSuchElementException If the given workflow group item id doesn't refer to a workflow group
     */
    SpaceItemEnt createWorkflowGroup(String workflowGroupItemId) throws IOException;

    /**
     * Rename a space item
     *
     * @param itemId The space item ID of the item to rename
     * @param newName The new name
     * @return {@link SpaceItemEnt} describing the item after renaming
     * @throws IOException
     * @throws ServiceExceptions.OperationNotAllowedException
     */
    SpaceItemEnt renameItem(String itemId, String newName) throws IOException,
            ServiceExceptions.OperationNotAllowedException;

    /**
     * Rename this space
     *
     * @param newName The new name
     * @return {@link SpaceEnt} describing the space after renaming
     * @throws IOException
     * @throws ServiceExceptions.OperationNotAllowedException
     */
    default SpaceEnt renameSpace(final String newName)
        throws IOException, ServiceExceptions.OperationNotAllowedException {
        throw new ServiceExceptions.OperationNotAllowedException(
            "Renaming of spaces is not supported in this provider");
    }

    /**
     * Resolves the item with the given ID into a local file, potentially downloading it.
     *
     * @param monitor to report progress, progress messages and for cancellation
     * @param itemId ID if the item to resolve
     * @param version The version of the item
     * @return the local path of the item and if available, empty if not available or the path resolution (e.g.
     *         download) has been cancelled
     * @throws CanceledExecutionException if the operation was cancelled
     */
    Optional<Path> toLocalAbsolutePath(ExecutionMonitor monitor, String itemId, final VersionId version)
        throws CanceledExecutionException;

    /**
     * @see this#toLocalAbsolutePath(ExecutionMonitor, String, VersionId)
     */
    default Optional<Path> toLocalAbsolutePath(final ExecutionMonitor monitor, final String itemId)
        throws CanceledExecutionException {
        return toLocalAbsolutePath(monitor, itemId, VersionId.currentState());
    }

    /**
     * @see this#toLocalAbsolutePath(ExecutionMonitor, String, VersionId)
     */
    default Optional<Path> toLocalAbsolutePath(final String itemId) {
        try {
            return toLocalAbsolutePath(new ExecutionMonitor(), itemId, VersionId.currentState());
        } catch (CanceledExecutionException ex) {
            // can never happen since no execution-monitor is passed
            throw new IllegalStateException("local path resolution cancelled", ex);
        }
    }

    /**
     * @param itemId
     * @return the location info for the given item
     */
    default LocationInfo getLocationInfo(final String itemId) {
        return getLocationInfo(itemId, VersionId.currentState());
    }

    /**
     * @param itemId
     * @param version
     * @return the location info for the given item
     * @since 5.5
     */
    LocationInfo getLocationInfo(String itemId, final VersionId version);

    /**
     * Gets the space item ID given a URI
     *
     * @param uri uri to retrieve space item ID for
     * @return item ID for the given URI or {@link Optional#empty()} if URI could not be resolved to an item ID
     */
    Optional<String> getItemIdByURI(final URI uri);

    /**
     * Creates a mountpoint-absolute KNIME URL for the given space item.
     * The URL may be either path- or ID-based. ID-based KNIME URLs can only be used to reference the item itself
     * and carry no information about the position of the item in the Space's folder hierarchy.
     *
     * @see this#toPathBasedKnimeUrl(String)
     * @param itemId item ID
     * @return KNIME URL
     * @throws IllegalStateException if there were problems determining the URI
     */
    URI toKnimeUrl(String itemId);

    /**
     * @param itemId
     * @return A browser-viewable URL corresponding to the item
     * @throws ResourceAccessException
     */
    default Optional<URI> getItemUrl(final String itemId) throws ResourceAccessException  {
        return Optional.empty();
    }

    /**
     * @param itemId
     * @return A browser-viewable URL corresponding to the API definition of the item
     * @throws ResourceAccessException
     */
    default Optional<URI> getAPIDefinitionUrl(final String itemId) throws ResourceAccessException {
        return Optional.empty();
    }

    /**
     * Creates a mountpoint-absolute KNIME URL for the given space item. The resulting KNIME-URL is guaranteed to be
     * path-based. May come with additional cost such as additional requests.
     *
     * @param itemId item ID
     * @return path-based KNIME URL
     * @throws ResourceAccessException if there were problems resolving the item's path
     */
    default URI toPathBasedKnimeUrl(final String itemId) throws ResourceAccessException {
        return toKnimeUrl(itemId);
    }

    /**
     * Deletes the items from the space.
     *
     * @param itemIds item IDs
     * @param softDelete If true the specified items will be moved to the bin.
     *                   Otherwise they will be permanently deleted.
     * @throws IOException If there was a problem deleting the items
     * @throws NoSuchElementException If one of the given item IDs does not exist
     */
    void deleteItems(List<String> itemIds, boolean softDelete) throws IOException;

    /**
     * Moves or copies the items to the new location within the space.
     *
     * @param itemIds The item IDs
     * @param destWorkflowGroupItemId The ID of the new parent item
     * @param collisionHandling How to handle name collisions
     * @param copy copy items instead of move
     * @throws IOException If there was a problem moving the items
     * @throws NoSuchElementException If one of the given item IDs does not exist
     * @throws IllegalArgumentException
     */
    void moveOrCopyItems(List<String> itemIds, String destWorkflowGroupItemId,
        Space.NameCollisionHandling collisionHandling, boolean copy) throws IOException;

    /**
     * Imports a data file to a workflow group.
     *
     * @param srcPath The source path of the data file to import
     * @param workflowGroupItemId The workflow group item ID
     * @param collisionHandling How to handle name collisions
     * @param progressMonitor progress monitor
     * @return The imported space item entity
     * @throws IOException If the import failed
     */
    SpaceItemEnt importFile(Path srcPath, String workflowGroupItemId, Space.NameCollisionHandling collisionHandling,
        IProgressMonitor progressMonitor) throws IOException;

    /**
     * Imports a workflow (group) to the specified workflow group.
     *
     * @param srcPath The source path of the *.knwf or *.knar file
     * @param workflowGroupItemId The workflow group item ID
     * @param createMetaInfoFileFor Consumer to create the necessary `MetaInfoFile` for an imported workflow (group)
     * @param collisionHandling How to handle name collisions
     * @param progressMonitor progress monitor
     * @return The imported space item entity
     * @throws IOException If the import failed
     */
    SpaceItemEnt importWorkflowOrWorkflowGroup(Path srcPath, String workflowGroupItemId,
        Consumer<Path> createMetaInfoFileFor, Space.NameCollisionHandling collisionHandling,
        IProgressMonitor progressMonitor) throws IOException;

    /**
     * @param itemId the id of the item to get the ancestors for
     * @return the list of ids of the ancestor items; with the first element being the direct parent, the second the
     *         parent of the parent etc. Returns an empty list if the item is at root-level.
     * @throws ResourceAccessException If the ancestors could not be fetched, e.g. if the remote item was deleted
     *             meanwhile. The exception cannot be thrown by the local space.
     */
    List<String> getAncestorItemIds(String itemId) throws ResourceAccessException;

    /**
     * Determines the item id for the given name for a item within a certain workflow group (or the space root).
     *
     * @param workflowGroupItemId The workflow group item ID
     * @param itemName The item name to check
     * @return the item id if there is an item for the given name, empty otherwise
     * @throws NoSuchElementException If there is no workflow group for the given id.
     * @since 5.5
     */
    Optional<String> getItemIdForName(String workflowGroupItemId, String itemName) throws NoSuchElementException;

    /**
     * Checks whether a certain workflow group (or the workspace root) already contains an item with the given name.
     *
     * @param workflowGroupItemId The workflow group item ID
     * @param itemName The item name to check
     * @return Returns {@code true} if an item with that name already exists, {@code false} otherwise.
     * @throws NoSuchElementException If there is no workflow group for the given id.
     */
    default boolean containsItemWithName(final String workflowGroupItemId, final String itemName) {
        return getItemIdForName(workflowGroupItemId, itemName).isPresent();
    }

    /**
     * Returns the name of a space item.
     *
     * @param itemId The space item ID
     * @return The space item's name
     * @throws NoSuchElementException If no such item is present
     */
    String getItemName(String itemId);

    /**
     * Returns the type of a space item.
     *
     * @param itemId The space item ID
     * @return The space item's type
     * @throws NoSuchElementException If no such item is present
     */
    SpaceItemEnt.TypeEnum getItemType(String itemId);

    /**
     * Returns the project type of a space item if it is a project.
     *
     * @param itemId The space item ID
     * @return The optional project type of the space item, if it is a project.
     * @throws NoSuchElementException If no such item is present
     */
    Optional<ProjectTypeEnum> getProjectType(final String itemId);

    /**
     * Creates a {@link SpaceEnt} for this space.
     *
     * @return space entity for this space
     */
    SpaceEnt toEntity();

    /**
     * Opens the permission dialog for Server items only.
     *
     * @param itemId The item to get the dialog for
     */
    default void openPermissionsDialogForItem(final String itemId) {
        throw new UnsupportedOperationException("Cannot call this method on 'Spaces' other than 'ServerSpaces'.");
    }

    /**
     * Opens the Remote Execution dialog for Server items only.
     *
     * @param itemId The item to get the dialog for
     */
    default void openRemoteExecution(final String itemId) {
        throw new UnsupportedOperationException("Cannot call this method on 'Spaces' other than 'ServerSpaces'.");
    }

    /**
     * Result of an item transfer from/to a Hub.
     *
     * @param successful whether the operation was (completely) successful or unsuccessful
     * @param errorTitleAndDescription description of the error (so it can be presented to the user), {@code null} if
     *            {@link #successful()} is {@code true} or if the user aborted the operation
     * @param itemPaths the paths of the items that were successfully transferred to a remote hub, always {@code null}
     *            if the operation was unsuccessful
     */
    public record TransferResult(boolean successful, Pair<String, String> errorTitleAndDescription,
        Set<IPath> itemPaths) {

        public static final TransferResult SUCCESS = new TransferResult(true, null, null);

        /** A cancelled transfer. */
        public static final TransferResult CANCELLED = new TransferResult(false, null, null);

        /**
         * Creates a failure with the given title and description.
         *
         * @param title title for the user notification
         * @param description description for the user notification
         * @return failure result
         */
        public static TransferResult failureWithError(final String title, final String description) {
            return new TransferResult(false, Pair.create(title, description), null);
        }
    }

    /**
     * Start an asynchronous upload flow on a Hub space.
     *
     * @param sourceSpace source space
     * @param itemIds IDs of the items to upload
     * @param targetItemId ID of the target folder to upload to
     * @param excludeData whether or not to exclude data from uploaded workflows
     * @return result indicating success or failure of the upload
     * @throws OperationNotAllowedException if the method is called for anything but a space on a Hub that supports
     *     asynchronous uploads
     */
    default TransferResult uploadFrom(final LocalSpace sourceSpace, final List<String> itemIds,
            final String targetItemId, final boolean excludeData) throws OperationNotAllowedException {
        throw new OperationNotAllowedException("Cannot call this method on spaces other than Hub spaces.");
    }

    /**
     * Start an asynchronous download flow on a Hub space.
     *
     * @param itemIds IDs of the items to download
     * @param targetSpace local space to download to
     * @param targetItemId item ID of the target folder
     * @return result indicating success or failure of the download
     * @throws OperationNotAllowedException
     */
    default TransferResult downloadInto(final List<String> itemIds, final LocalSpace targetSpace,
            final String targetItemId) throws OperationNotAllowedException {
        throw new OperationNotAllowedException("Cannot call this method on spaces other than Hub spaces.");
    }

    /**
     * Saves a workflow in the yellow bar editor back to the associated Hub/Server space.
     *
     * @param localWorkflow the path of the local workflow
     * @param targetURI the URI to the target
     * @param excludeDataInWorkflows <code>true</code> if internal workflow data should be excluded
     * @param progressMonitor progress monitor
     * @return {@code true} if the upload succeeded, {@code false} if it was canceled
     * @throws IOException if the upload failed
     * @throws UnsupportedOperationException if invoked on a something other than a Hub or Server space
     * @since 5.5
     */
    default boolean saveBackTo(final Path localWorkflow, final URI targetURI, final boolean excludeDataInWorkflows,
        final IProgressMonitor progressMonitor)
        throws IOException, UnsupportedOperationException {
        throw new UnsupportedOperationException("Cannot call this method on spaces other than Hub spaces.");
    }

    /**
     * Generates unique space item names, preserves file extensions.
     *
     * @param taken predicate for determining whether a name is already taken in a space
     * @param name initial recommendation for the item name
     * @param isWorkflowOrWorkflowGroup {@code true} if the name is for a workflow(group), {@code false} otherwise
     * @return the initial name if that doesn't exist, the unique one otherwise
     */
    static String generateUniqueSpaceItemName(final Predicate<String> taken, final String name,
            final boolean isWorkflowOrWorkflowGroup) {
        if (!taken.test(name)) {
            return name;
        } else {
            // Ignore dots in workflow names
            var lastIndexOfDot = isWorkflowOrWorkflowGroup ? -1 : name.lastIndexOf(".");
            var fileExtension = lastIndexOfDot > -1 ? name.substring(lastIndexOfDot) : "";
            var oldName = lastIndexOfDot > -1 ? name.substring(0, lastIndexOfDot) : name;
            var counter = 0;
            String newName;
            do {
                counter++;
                // No brackets in workflow names
                newName = isWorkflowOrWorkflowGroup ? (oldName + counter) : (oldName + "(" + counter + ")");
            } while (taken.test(newName + fileExtension));
            return newName + fileExtension;
        }
    }

}
