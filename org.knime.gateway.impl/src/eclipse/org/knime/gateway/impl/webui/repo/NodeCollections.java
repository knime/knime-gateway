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
 */
package org.knime.gateway.impl.webui.repo;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import org.knime.gateway.impl.webui.PreferencesProvider;
import org.knime.gateway.impl.webui.modes.WebUIMode;

/**
 * Provide information about node collections to services. A node collection is a subset of all installed nodes.
 * This can be used to limit the set of nodes displayed to the user.
 *
 * @author Benjamin Moser, KNIME GmbH
 */
public final class NodeCollections {

    private final PreferencesProvider m_preferencesProvider;

    private final WebUIMode m_mode;

    public NodeCollections(final PreferencesProvider preferencesProvider, WebUIMode mode) {
        this.m_preferencesProvider = preferencesProvider;
        this.m_mode = mode;
    }

    /**
     * @param displayName Must be compatible for an input placeholder "Search in {displayName} nodes"
     * @param nodeFilter Decides whether a given node factory class name is in this collection
     */
    public record NodeCollection(String displayName, Predicate<String> nodeFilter) {
        public NodeCollection(final String displayName, final Predicate<String> nodeFilter) {
            this.displayName = Objects.requireNonNull(displayName);
            this.nodeFilter = Objects.requireNonNull(nodeFilter);
        }
    }

    /**
     * @return The currently active collection
     */
    public Optional<NodeCollection> getActiveCollection() {
        if (m_mode == WebUIMode.PLAYGROUND) {
            return Optional.of(new NodeCollection("preview", id -> true));
        } else {
            return getCollectionFromPreferences();
        }
    }

    private Optional<NodeCollection> getCollectionFromPreferences() {
        var configuredPredicate = m_preferencesProvider.activeNodeCollection();
        if (configuredPredicate == null) {
            return Optional.empty();
        }
        return Optional.of(new NodeCollection("starter", configuredPredicate));
    }

}
