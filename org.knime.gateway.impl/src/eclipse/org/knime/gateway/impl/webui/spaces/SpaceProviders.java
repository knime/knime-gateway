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

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.knime.gateway.api.webui.entity.SpaceProviderEnt;
import org.knime.gateway.impl.webui.service.ServiceDependencies;

/**
 * Summarizes all available space providers. Mainly used as a service dependency (see, e.g.,
 * {@link ServiceDependencies}).
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public final class SpaceProviders {

    private final Map<String, SpaceProvider> m_spaceProviders;

    /**
     * @param spaceProviders the spaceProvidersMap
     * @param localSpaceProvider the local space provider or {@code null} if none
     */
    SpaceProviders(final Map<String, SpaceProvider> spaceProviders) {
        m_spaceProviders = spaceProviders;
    }

    /**
     * Returns the space for the given space-provider-id and space-id.
     *
     * @param spaceProviderId
     * @param spaceId
     * @throws NoSuchElementException if there is no space provider or space for the given ids
     * @return the space
     */
    public Space getSpace(final String spaceProviderId, final String spaceId) {
        return getSpaceProvider(spaceProviderId).getSpace(spaceId);
    }

    /**
     * Returns the space provider for the given project-id and space-provider-id.
     *
     * @param spaceProviderId
     * @return the space provider
     * @throws NoSuchElementException if there is no space provider for the given id
     */
    public SpaceProvider getSpaceProvider(final String spaceProviderId) {
        var res = m_spaceProviders.get(spaceProviderId);
        if (res == null) {
            throw new NoSuchElementException("No space provider found for id '" + spaceProviderId + "'");
        }
        return res;
    }

    /**
     * @return all space providers
     */
    public Collection<SpaceProvider> getAllSpaceProviders() {
        return m_spaceProviders.values();
    }

    /**
     * Types of available {@link SpaceProvider}s, may be overridden for performance.
     *
     * @return types of available {@link SpaceProvider}s
     */
    public synchronized Map<String, SpaceProviderEnt.TypeEnum> getProviderTypes() {
        return m_spaceProviders.entrySet().stream() //
            .collect(Collectors.toMap(Entry::getKey, e -> e.getValue().getType()));
    }

}
