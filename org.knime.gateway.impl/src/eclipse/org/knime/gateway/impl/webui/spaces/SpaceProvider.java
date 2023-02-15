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

import java.net.URI;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.knime.gateway.api.webui.entity.SpaceProviderEnt;
import org.knime.gateway.impl.webui.spaces.local.LocalWorkspace;

/**
 * Represents an entity that holds spaces. E.g. a Hub instance.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public interface SpaceProvider {

    /**
     * @return a globally unique id
     */
    String getId();

    /**
     * @return a human readable name for the space provider
     */
    String getName();

    /**
     * @param spaceId space ID
     * @return space with the given ID
     * @throws NoSuchElementException if no space with the given ID exists
     */
    Space getSpace(String spaceId);

    /**
     * @return {@code true} if this provider only returns {@link LocalWorkspace LocalWorkspace(s)}
     */
    default boolean isLocal() {
        return false;
    }

    /**
     * Creates an entity representing this space provider and its available spaces.
     *
     * @return entity representing this space provider
     */
    SpaceProviderEnt toEntity();

    /**
     * Uploads a workflow to the location represented by the given KNIME URL.
     *
     * @param localWorkflow workflow directory
     * @param targetUri target KNIME URL
     * @param excludeDataInWorkflows data exclusion flag
     * @param monitor progress monitor
     * @param callback after-upload callback which is invoked from the UI thread
     * @throws CoreException if something goes wrong
     */
    default void syncUploadWorkflow(final java.nio.file.Path localWorkflow, final URI targetUri,
            final boolean excludeDataInWorkflows, final IProgressMonitor monitor,
            final Consumer<Throwable> callback) throws CoreException {
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
     * Represents a connection of a space provider to its remote location (e.g. a Hub).
     */
    public interface SpaceProviderConnection {

        /**
         * @return the user that is connected
         */
        String getUsername();

        /**
         * Cuts the connection.
         */
        void disconnect();

    }

}
