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
 *   Jan 15, 2025 (kai): created
 */
package org.knime.gateway.impl.webui.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.knime.core.util.exception.ResourceAccessException;
import org.knime.gateway.api.webui.entity.SpaceItemVersionEnt;
import org.knime.gateway.api.webui.service.VersionService;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.NetworkException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.ServiceCallException;
import org.knime.gateway.impl.util.NetworkExceptions;
import org.knime.gateway.impl.webui.spaces.SpaceProviders;

/**
 * The default implementation of the {@link VersionService}.
 *
 * @author Kai Franze, KNIME GmbH, Germany
 * @since 5.5
 */
public final class DefaultVersionService implements VersionService {

    /**
     * Returns the singleton instance for this service.
     *
     * @return the singleton instance
     */
    public static DefaultVersionService getInstance() {
        return ServiceInstances.getDefaultServiceInstance(DefaultVersionService.class);
    }

    private final SpaceProviders m_spaceProviders =
            ServiceDependencies.getServiceDependency(SpaceProviders.class, true);

    DefaultVersionService() {
        // Singleton
    }

    @Override
    public List<SpaceItemVersionEnt> listVersionsForItem(final String spaceProviderId, final String spaceId,
        final String itemId, final Integer limit) throws ServiceCallException, NetworkException {
        try {
            final var spaceProvider = SpaceProviders.getSpaceProvider(m_spaceProviders, spaceProviderId);
            final var space = spaceProvider.getSpace(spaceId);
            final var message = "Could not fetch versions of '%s' from '%s'".formatted(itemId, space.getName());

            // TODO: How would we know if there is a draft or not?
            // TODO: And how would we return that? New endpoint or magic version number?
            // Note: Empty list means no versions.

            return NetworkExceptions.callWithCatch(() -> space.listVersionsForItem(itemId, limit), message);
        } catch (ResourceAccessException | NoSuchElementException e) {
            throw new ServiceCallException(e.getMessage(), e);
        }
    }

}
