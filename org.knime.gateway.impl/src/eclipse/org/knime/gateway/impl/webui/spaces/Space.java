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
import java.util.function.Consumer;

import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.workflow.contextv2.LocationInfo;
import org.knime.gateway.api.webui.entity.SpaceItemEnt;
import org.knime.gateway.api.webui.entity.WorkflowGroupContentEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions;

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
    static final Comparator<SpaceItemEnt> ITEM_COMPARATOR = Comparator.comparing(SpaceItemEnt::getType) //
            .thenComparing(SpaceItemEnt::getName, SpaceItemNameComparator.INSTANCE);

    /**
     * Id of the root 'workflow group'.
     */
    String ROOT_ITEM_ID = "root";

    /**
     * Name collision handling options.
     */
    enum NameCollisionHandling {

            /** Nothing needs to be done */
            NOOP,

            /** Automatically rename items with identical names */
            AUTORENAME,

            /** Overwrite items with identical names */
            OVERWRITE;

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
     * @return space owner
     */
    String getOwner();

    /**
     * @return space description
     */
    default String getDescription() {
        return "";
    }

    /**
     * @return whether it's a private or a public space
     */
    default boolean isPrivate() {
        return false;
    }

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
     * Creates a new workflow.
     *
     * @param workflowGroupItemId The ID of the workflow group where to create the new workflow
     * @return The newly created space item
     * @throws IOException If there was a problem creating the files for the new workflow
     * @throws NoSuchElementException If the given workflow group item id doesn't refer to a workflow group
     */
    SpaceItemEnt createWorkflow(String workflowGroupItemId) throws IOException;

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
     * Resolves the item with the given ID into a local file, potentially downloading it.
     *
     * @param monitor to report progress, progress messages and for cancellation
     * @param itemId ID if the item to resolve
     * @return the local path of the item and (if available, otherwise {@code null}) its location info
     */
    Path toLocalAbsolutePath(ExecutionMonitor monitor, String itemId);

    /**
     * @return the local root path of the space
     */
    Path getLocalRootPath();

    /**
     * @param itemId
     * @return the location info for the given item
     */
    LocationInfo getLocationInfo(String itemId);

    /**
     * Creates a mountpoint-absolute KNIME URL for the given space item.
     *
     * @param itemId item ID
     * @return KNIME URL
     */
    URI toKnimeUrl(String itemId);

    /**
     * Deletes the items from the space.
     *
     * @param itemIds item IDs
     * @throws IOException If there was a problem deleting the items
     * @throws NoSuchElementException If one of the given item IDs does not exist
     */
    void deleteItems(List<String> itemIds) throws IOException;

    /**
     * Moves the items to the new location within the space.
     *
     * @param itemIds The item IDs
     * @param destWorkflowGroupItemId The ID of the new parent item
     * @param collisionHandling How to handle name collisions
     * @throws IOException If there was a problem moving the items
     * @throws NoSuchElementException If one of the given item IDs does not exist
     */
    void moveItems(List<String> itemIds, String destWorkflowGroupItemId, Space.NameCollisionHandling collisionHandling)
        throws IOException;

    /**
     * Imports a data file to a workflow group.
     *
     * @param srcPath The source path of the data file to import
     * @param workflowGroupItemId The workflow group item ID
     * @param collisionHandling How to handle name collisions
     * @return The imported space item entity
     * @throws IOException If the import failed
     */
    SpaceItemEnt importFile(final Path srcPath, final String workflowGroupItemId,
        Space.NameCollisionHandling collisionHandling) throws IOException;

    /**
     * Imports a workflow (group) to the specified workflow group.
     *
     * @param srcPath The source path of the *.knwf or *.knar file
     * @param workflowGroupItemId The workflow group item ID
     * @param createMetaInfoFileFor Consumer to create the necessary `MetaInfoFile` for an imported workflow (group)
     * @param collisionHandling How to handle name collisions
     * @return The imported space item entity
     * @throws IOException If the import failed
     */
    SpaceItemEnt importWorkflowOrWorkflowGroup(final Path srcPath, final String workflowGroupItemId,
        final Consumer<Path> createMetaInfoFileFor, Space.NameCollisionHandling collisionHandling) throws IOException;

    /**
     * @param itemId the id of the item to get the ancestors for
     * @return the list of ids of the ancestor items; with the first element being the direct parent, the second the
     *         parent of the parent etc. Returns an empty list if the item is at root-level.
     */
    List<String> getAncestorItemIds(final String itemId);

    /**
     * Checks whether a certain workflow group (or the workspace root) already contains an item with the given name.
     *
     * @param workflowGroupItemId The workflow group item ID
     * @param itemName The item name to check
     * @return Returns {@code true} if an item with that name already exists, {@code false} otherwise.
     * @throws NoSuchElementException If there is no workflow group for the given id.
     */
    boolean containsItemWithName(final String workflowGroupItemId, final String itemName) throws NoSuchElementException;
}
