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
 *   Feb 10, 2025 (hornm): created
 */
package org.knime.gateway.impl.webui.spaces;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import org.knime.core.node.workflow.contextv2.WorkflowContextV2;
import org.knime.gateway.impl.webui.service.ServiceDependencies;
import org.knime.gateway.impl.webui.spaces.local.LocalSpaceProvider;

/**
 * Manages instances of {@link SpaceProviders}, accessible by a {@link Key}. Mainly used as a service dependency (see,
 * e.g., {@link ServiceDependencies}).
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public final class SpaceProvidersManager {

    private final Map<Key, SpaceProviders> m_spaceProviders = new LinkedHashMap<>();

    private final Consumer<String> m_loginErrorHandler;

    private final LocalSpaceProvider m_localSpaceProvider;

    private final List<SpaceProviderFactory> m_spaceProvidersFactories;

    /**
     * @param loginErrorHandler error handler for login errors
     * @param localSpaceProvider the local space provider or {@code null} if none
     */
    public SpaceProvidersManager(final Consumer<String> loginErrorHandler,
        final LocalSpaceProvider localSpaceProvider) {
        this(loginErrorHandler, localSpaceProvider, SpaceProviderFactory.collectSpaceProviderFactories());
    }

    /**
     * @param loginErrorHandler error handler for login errors
     * @param localSpaceProvider the local space provider or {@code null} if none
     * @param spaceProviderFactories the factories to create the space providers from
     */
    public SpaceProvidersManager(final Consumer<String> loginErrorHandler, final LocalSpaceProvider localSpaceProvider,
        final List<SpaceProviderFactory> spaceProviderFactories) {
        m_loginErrorHandler = loginErrorHandler;
        m_localSpaceProvider = localSpaceProvider;
        m_spaceProvidersFactories = spaceProviderFactories;
    }

    /**
     * @param key
     * @return the {@link SpaceProviders} instance for the given key or the default key if none is found
     * @throws NoSuchElementException if no {@link SpaceProviders} instance is found for the given key
     */
    public SpaceProviders getSpaceProviders(final Key key) {
        var res = m_spaceProviders.get(key);
        if (res == null) {
            res = m_spaceProviders.get(Key.defaultKey());
        }
        if (res == null) {
            throw new NoSuchElementException("No space providers found for key '" + key + "'");
        }
        return res;
    }

    /**
     * @return all available keys
     */
    public Set<Key> getKeys() {
        return m_spaceProviders.keySet();
    }

    /**
     * Updates the space providers by adding or removing space providers for a key. A space provider is specifically
     * created for the projects workflow context.
     *
     * @param key the key to create or the space providers for
     * @param context
     */
    public void update(final Key key, final WorkflowContextV2 context) {
        m_spaceProvidersFactories.forEach(factory -> factory.createSpaceProvider(context).ifPresent(provider -> {
            provider.init(m_loginErrorHandler);
            m_spaceProviders.put(key, new SpaceProviders(Map.of(provider.getId(), provider)));
        }));
    }

    /**
     * Removes the {@link SpaceProviders} for the given key.
     *
     * @param key
     */
    public void remove(final Key key) {
        m_spaceProviders.remove(key);
    }

    /**
     * Updates the space providers by creating the space providers anew from the {@link SpaceProviderFactory}-extension
     * point.
     */
    public synchronized void update() {
        m_spaceProviders.clear();
        var spaceProvidersMap = new LinkedHashMap<String, SpaceProvider>();
        if (m_localSpaceProvider != null) {
            spaceProvidersMap.put(m_localSpaceProvider.getId(), m_localSpaceProvider);
        }
        m_spaceProvidersFactories.forEach(factory -> factory.createSpaceProviders().forEach(provider -> {
            provider.init(m_loginErrorHandler);
            spaceProvidersMap.put(provider.getId(), provider);
        }));
        m_spaceProviders.put(Key.defaultKey(), new SpaceProviders(spaceProvidersMap));
    }

    /**
     * Key to identify a {@link SpaceProviders} instance.
     */
    public static final class Key {

        private final String m_key;

        private Key(final String key) {
            m_key = key;
        }

        /**
         * @param key
         * @return a key for the given string
         */
        public static Key of(final String key) {
            return new Key(key);
        }

        /**
         * @return the default key
         */
        public static Key defaultKey() {
            return new Key(null);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof Key k) {
                return Objects.equals(k.m_key, m_key);
            } else {
                return false;
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return m_key == null ? 0 : m_key.hashCode();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return m_key;
        }

    }

}
