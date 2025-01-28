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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.knime.core.node.workflow.contextv2.WorkflowContextV2;
import org.knime.gateway.api.webui.entity.SpaceProviderEnt;
import org.knime.gateway.impl.webui.service.ServiceDependencies;
import org.knime.gateway.impl.webui.spaces.local.LocalSpaceProvider;

/**
 * Summarizes all available space providers. Mainly used as a service dependency (see, e.g.,
 * {@link ServiceDependencies}).
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public final class SpaceProviders {

    // project-id -> (space-provider-id -> space-provider)
    private final MultiKeyMap<String, SpaceProvider> m_spaceProviders = new MultiKeyMap<>();

    private final MultiKeyMap<String, SpaceProviderEnt.TypeEnum> m_spaceProviderTypes = new MultiKeyMap<>();

    private final List<SpaceProvidersFactory> m_spaceProvidersFactories;

    private final Consumer<String> m_loginErrorHandler;

    private final LocalSpaceProvider m_localSpaceProvider;

    /**
     * @param loginErrorHandler error handler for login errors
     * @param localSpaceProvider the local space provider or {@code null} if none
     */
    public SpaceProviders(final Consumer<String> loginErrorHandler, final LocalSpaceProvider localSpaceProvider) {
        this(loginErrorHandler, localSpaceProvider, SpaceProvidersFactory.collectSpaceProviderFactories());
    }

    /**
     * @param loginErrorHandler error handler for login errors
     * @param localSpaceProvider the local space provider or {@code null} if none
     * @param spaceProviderFactories the factories to create the space providers from
     */
    public SpaceProviders(final Consumer<String> loginErrorHandler, final LocalSpaceProvider localSpaceProvider,
        final List<SpaceProvidersFactory> spaceProviderFactories) {
        m_loginErrorHandler = loginErrorHandler;
        m_localSpaceProvider = localSpaceProvider;
        m_spaceProvidersFactories = spaceProviderFactories;
    }

    /**
     * Updates the space providers by adding or removing space providers for the project-id. A space provider is
     * specifically created for the projects workflow context.
     *
     * @param projectId the id of the project to create or remove the space providers for
     * @param context will remove the space providers for the project-id if the context is {@code null}
     */
    public void update(final String projectId, final WorkflowContextV2 context) {
        if (context == null) {
            m_spaceProviders.clear(projectId);
            m_spaceProviderTypes.clear(projectId);
        } else {
            m_spaceProvidersFactories.forEach(factory -> {
                factory.createSpaceProvider(context).ifPresent(provider -> {
                    provider.init(m_loginErrorHandler);
                    m_spaceProviders.put(projectId, provider.getId(), provider);
                    m_spaceProviderTypes.put(projectId, provider.getId(), provider.getType());
                });
            });
        }
    }

    /**
     * Updates the space providers by creating the space providers anew from the {@link SpaceProvidersFactory}-extension
     * point.
     */
    public synchronized void update() {
        m_spaceProviders.clear(null);
        m_spaceProviderTypes.clear(null);
        if (m_localSpaceProvider != null) {
            m_spaceProviders.put(null, m_localSpaceProvider.getId(), m_localSpaceProvider);
        }
        m_spaceProvidersFactories.forEach(factory -> {
            factory.createSpaceProviders().forEach(provider -> {
                provider.init(m_loginErrorHandler);
                m_spaceProviders.put(null, provider.getId(), provider);
                m_spaceProviderTypes.put(null, provider.getId(), provider.getType());
            });
        });
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
        return getSpace(null, spaceProviderId, spaceId);
    }

    /**
     * Returns the space for the given space-provider-id and space-id.
     *
     * @param projectId id of the project to get the space providers for; can be {@code null} in case of the desktop
     *            environment where space providers aren't associated with specific workflows
     * @param spaceProviderId
     * @param spaceId
     * @throws NoSuchElementException if there is no space provider or space for the given ids
     * @return the space
     */
    public Space getSpace(final String projectId, final String spaceProviderId, final String spaceId) {
        return getSpaceProvider(projectId, spaceProviderId).getSpace(spaceId);
    }

    /**
     * Returns the space provider for the given project-id and space-provider-id.
     *
     * @param spaceProviderId
     * @return the space provider
     * @throws NoSuchElementException if there is no space provider for the given id
     */
    public SpaceProvider getSpaceProvider(final String spaceProviderId) {
        return getSpaceProvider(null, spaceProviderId);
    }

    /**
     * Returns the space provider for the given project-id and space-provider-id.
     *
     * @param projectId id of the project to get the space providers for; can be {@code null} in case of the desktop
     *            environment where space providers aren't associated with specific workflows
     * @param spaceProviderId
     * @return the space provider
     * @throws NoSuchElementException if there is no space provider for the given id
     */
    public SpaceProvider getSpaceProvider(final String projectId, final String spaceProviderId) {
        var res = this.getProvidersMap(projectId).get(spaceProviderId);
        if (res == null) {
            throw new NoSuchElementException("No space provider found for id '" + spaceProviderId + "'");
        }
        return res;
    }

    /**
     * @return map of available {@link SpaceProvider}s; maps the space-provider-id to the space-provider.
     */
    public synchronized Map<String, SpaceProvider> getProvidersMap() {
        return getProvidersMap(null);
    }

    /**
     * @param projectId id of the project to get the space providers for; can be {@code null} in case of the desktop
     *            environment where space providers aren't associated with specific workflows
     *
     * @return map of available {@link SpaceProvider}s; maps the space-provider-id to the space-provider.
     */
    public synchronized Map<String, SpaceProvider> getProvidersMap(final String projectId) {
        var providersMap = m_spaceProviders.get(projectId);
        if (providersMap == null) {
            providersMap = m_spaceProviders.get(null);
        }
        if (providersMap == null) {
            return Map.of();
        }
        return providersMap;
    }

    /**
     * Types of available {@link SpaceProvider}s, may be overridden for performance.
     *
     * @return types of available {@link SpaceProvider}s
     */
    public synchronized Map<String, SpaceProviderEnt.TypeEnum> getProviderTypes() {
        return getProviderTypes(null);
    }

    /**
     * Types of available {@link SpaceProvider}s, may be overridden for performance.
     *
     * @param projectId id of the project to get the space providers for; can be {@code null} in case of the desktop
     *            environment where space providers aren't associated with specific workflows
     *
     * @return types of available {@link SpaceProvider}s
     */
    public synchronized Map<String, SpaceProviderEnt.TypeEnum> getProviderTypes(final String projectId) {
        return getProvidersMap(projectId).entrySet().stream() //
            .collect(Collectors.toMap(Entry::getKey, e -> e.getValue().getType()));
    }

    private static final class MultiKeyMap<K, V> {

        private final Map<K, Map<K, V>> m_map = new LinkedHashMap<>();

        V put(final K key1, final K key2, final V value) {
            return m_map.computeIfAbsent(key1, k -> new LinkedHashMap<>()).put(key2, value);
        }

        void clear(final K key1) {
            var nestedMap = m_map.remove(key1);
            if (nestedMap != null) {
                nestedMap.clear();
            }
        }

        Map<K, V> get(final K key1) {
            return m_map.get(key1);
        }

    }
}
