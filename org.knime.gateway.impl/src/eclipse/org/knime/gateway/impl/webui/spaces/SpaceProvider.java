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
 *   Dec 9, 2022 (hornm): created
 */
package org.knime.gateway.impl.webui.spaces;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.knime.core.util.Version;
import org.knime.core.util.auth.CouldNotAuthorizeException;
import org.knime.gateway.api.webui.entity.SpaceGroupEnt;
import org.knime.gateway.api.webui.entity.SpaceProviderEnt.ResetOnUploadEnum;
import org.knime.gateway.api.webui.entity.SpaceProviderEnt.TypeEnum;

/**
 * Represents an entity that holds spaces. E.g. a Hub instance.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public interface SpaceProvider {

    /**
     * The ID of the local space provider
     */
    String LOCAL_SPACE_PROVIDER_ID = "local";

    /**
     * Initializes the space provider after creation.
     *
     * @param loginErrorHandler error handler for login errors
     */
    void init(final Consumer<String> loginErrorHandler);

    /**
     * @return the space provider type
     */
    default TypeEnum getType() {
        return TypeEnum.LOCAL;
    }

    /**
     * @return a globally unique id
     */
    String getId();

    /**
     * @return a human readable name for the space provider
     */
    String getName();

    /**
     * Retrieves the space with the given ID from this provider.
     *
     * @param spaceId space ID
     * @return space with the given ID
     * @throws NoSuchElementException if no space with the given ID exists
     */
    Space getSpace(String spaceId);

    /**
     * Returns the space groups of this provider.
     *
     * @param spaceGroupName The name of the space group to obtain
     * @return spaceGroup The associated space group
     * @throws NoSuchElementException if no group with the given name exists
     */
    @SuppressWarnings("java:S1452") // wildcard is needed so `HubSpaceGroup implements SpaceGroup<HubSpace>` works here
    SpaceGroup<? extends Space> getSpaceGroup(String spaceGroupName);

    /**
     * Returns the server address of the current space provider
     *
     * @return the server address or an empty optional if this provider is not connected
     */
    default Optional<String> getServerAddress() {
        return Optional.empty();
    }

    /**
     * Creates an entity representing the available spaces.
     *
     * @return entity representing this space provider
     */
    List<SpaceGroupEnt> toEntity();

    /**
     * Uploads a workflow to the location represented by the given KNIME URL.
     *
     * @param localWorkflow workflow directory
     * @param targetUri target KNIME URL
     * @param deleteSource flag indicating that the operation is a move instead of a copy operation
     * @param excludeDataInWorkflows data exclusion flag
     * @param progressMonitor monitor for aborting or receiving progress updates
     * @throws CoreException if errors occur during upload
     * @throws IOException if I/O errors occur during upload
     * @throws UnsupportedOperationException for local space providers
     */
    default void syncUploadWorkflow(final Path localWorkflow, final URI targetUri, final boolean deleteSource,
        final boolean excludeDataInWorkflows, final IProgressMonitor progressMonitor)
        throws CoreException, IOException {
        throw new UnsupportedOperationException();
    }

    /**
     * Downloads a workflow from the location represented by the given KNIME URL.
     *
     * @param sourceUri source KNIME URL
     * @param targetUri target KNIME URL
     * @param deleteSource flag indicating that the operation is a move instead of a copy operation
     * @param progressMonitor monitor for aborting or receiving progress updates
     * @throws CoreException if errors occur during download
     * @throws IOException if I/O errors occur during download
     * @throws UnsupportedOperationException for local space providers
     */
    default void syncDownloadWorkflow(final URI sourceUri, final URI targetUri, final boolean deleteSource,
        final IProgressMonitor progressMonitor) throws CoreException, IOException {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the connection if this provider is connected to its remote location.
     *
     * @param doConnect whether to connect if there isn't a connection, yet
     * @return the connection or an empty optional if this provider is not connected
     */
    default Optional<SpaceProviderConnection> getConnection(final boolean doConnect) {
        return Optional.empty();
    }

    /**
     * Gets the path to the server's REST interface.
     *
     * @return the REST path if known, or an empty optional if not available
     */
    default Optional<String> getRESTPath() {
        return Optional.empty();
    }

    /**
     * @return a change notifier for items in this space. Empty if not available.
     */
    default Optional<SpaceItemChangeNotifier> getChangeNotifier() {
        return Optional.empty();
    }

    /**
     * Notifies callbacks if the specified space item has changed.
     */
    interface SpaceItemChangeNotifier {
        /**
         * If an item already has a subscription, it is removed.
         */
        void subscribeToItem(final String spaceId, final String itemId, Runnable callback);

        void unsubscribe(String spaceId, String itemId);

        void unsubscribeAll();
    }

    /**
     * Gets the server version.
     *
     * @return The {@link Version} running on this server
     * @throws UnsupportedOperationException for local space providers
     */
    default Version getServerVersion() {
        throw new UnsupportedOperationException();
    }

    /**
     * @return if this provider is the community Hub
     */
    default boolean isCommunityHub() {
        return false;
    }

    /**
     * Represents a connection of a space provider to its remote location (e.g. a Hub).
     */
    interface SpaceProviderConnection {

        /**
         * @return the user that is connected
         */
        String getUsername();

        /**
         * @return the authorization string that can be set for the authorization header
         * @throws CouldNotAuthorizeException if the authorization cannot be created
         */
        default String getAuthorization() throws CouldNotAuthorizeException {
            throw new CouldNotAuthorizeException("No authorization token available");
        }

        /**
         * Cuts the connection.
         */
        void disconnect();

    }

    /**
     * Resolves the space item and its containing space given an absolute KNIME URI.
     *
     * @param uri uri of item to resolve
     * @return resolved item or {@link Optional#empty()} if item could not be resolved by this space provider
     */
    default Optional<SpaceAndItemId> resolveSpaceAndItemId(final URI uri) {
        return Optional.empty();
    }

    /**
     * The (remote) space provider's setting regarding resetting workflows before upload.
     *
     * @return the setting
     * @since 5.5
     */
    default ResetOnUploadEnum getResetOnUploadMode() {
        return ResetOnUploadEnum.NO_PREFERENCE;
    }

    /**
     * Location of a space item in the context of this space provider.
     *
     * @param spaceId ID of the space containing the item
     * @param itemId ID of the item itself
     */
    record SpaceAndItemId(String spaceId, String itemId) {
    }

}
