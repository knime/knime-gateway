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
package org.knime.gateway.impl.webui;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.NoSuchElementException;

import org.knime.core.node.workflow.contextv2.LocationInfo;
import org.knime.core.util.Pair;
import org.knime.gateway.api.webui.entity.SpaceItemEnt;
import org.knime.gateway.api.webui.entity.WorkflowGroupContentEnt;

/**
 * Represents a space in order to abstract from different space implementations (e.g. the local workspace or a hub
 * space).
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @author Kai Franze, KNIME GmbH
 */
public interface Space {

    /**
     * Id of the root 'workflow group'.
     */
    String ROOT_ITEM_ID = "root";

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
     * Resolves the item with the given ID into a local file, potentially downloading it.
     *
     * @param itemId ID if the item to resolve
     * @return the local path of the item and (if available, otherwise {@code null}) its location info
     */
    Pair<Path, LocationInfo> toLocalAbsolutePath(String itemId);

    /**
     * Creates a mountpoint-absolute KNIME URL for the given space item.
     *
     * @param itemId item ID
     * @return KNIME URL
     */
    URI toKnimeUrl(String itemId);
}
