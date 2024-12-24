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

import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.knime.gateway.api.webui.entity.SpaceProviderEnt;
import org.knime.gateway.impl.webui.service.ServiceDependencies;

/**
 * Summarizes all available space providers. Mainly used as a service dependency (see, e.g.,
 * {@link ServiceDependencies}).
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public interface SpaceProviders {

    /**
     * @param spaceProviders
     * @param spaceProviderId
     * @return The space provider for the ID given if available
     * @throws NoSuchElementException if there is no space-provider for the given ID
     */
    static SpaceProvider getSpaceProvider(final SpaceProviders spaceProviders, final String spaceProviderId) {
        final var spaceProvider = spaceProviders.getProvidersMap().get(spaceProviderId);
        if (spaceProvider == null) {
            throw new NoSuchElementException("No space provider found for id '" + spaceProviderId + "'");
        }
        return spaceProvider;
    }

    default SpaceProvider getSpaceProvider(final String spaceProviderId) {
        return this.getProvidersMap().get(spaceProviderId);
    }

    /**
     * @param spaceProviders
     * @param spaceId
     * @param spaceProviderId
     * @return the space for the given id if available
     * @throws NoSuchElementException if there is no space or space-provider for the given ids
     */
    static Space getSpace(final SpaceProviders spaceProviders, final String spaceProviderId, final String spaceId)
        throws NoSuchElementException {
        final var spaceProvider = getSpaceProvider(spaceProviders, spaceProviderId);
        return spaceProvider.getSpace(spaceId);
    }

    /**
     * @param spaceProviders
     * @param spaceProviderId
     * @param spaceId
     * @return The optional space requested
     */
    static Optional<Space> getSpaceOptional(final SpaceProviders spaceProviders, final String spaceProviderId,
        final String spaceId) {
        try {
            return Optional.of(getSpace(spaceProviders, spaceProviderId, spaceId));
        } catch (NoSuchElementException e) { // NOSONAR
            return Optional.empty();
        }
    }

    /**
     * @return {@code true} this space provider only returns local spaces, i.e. spaces that don't require a remote
     *         connection.
     */
    default boolean isLocal() {
        return false;
    }

    /**
     * @return map of available {@link SpaceProvider}s; maps the space-provider-id to the space-provider.
     */
    Map<String, SpaceProvider> getProvidersMap();

    /**
     * Types of available {@link SpaceProvider}s, may be overridden for performance.
     *
     * @return types of available {@link SpaceProvider}s
     */
    default Map<String, SpaceProviderEnt.TypeEnum> getProviderTypes() {
        return getProvidersMap().entrySet().stream() //
                .collect(Collectors.toMap(Entry::getKey, e -> e.getValue().getType()));
    }
}
