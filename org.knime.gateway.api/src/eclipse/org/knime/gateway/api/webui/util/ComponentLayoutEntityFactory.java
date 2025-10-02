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
 *   Oct 1, 2025 (motacilla): created
 */
package org.knime.gateway.api.webui.util;

import static org.knime.gateway.api.entity.EntityBuilderManager.builder;

import java.util.List;

import org.knime.gateway.api.webui.entity.ComponentConfigurationLayoutEnt;
import org.knime.gateway.api.webui.entity.ComponentConfigurationLayoutEnt.ComponentConfigurationLayoutEntBuilder;
import org.knime.gateway.api.webui.entity.ComponentConfigurationLayoutNodeEnt;
import org.knime.gateway.api.webui.entity.ComponentConfigurationLayoutNodeEnt.ComponentConfigurationLayoutNodeEntBuilder;
import org.knime.gateway.api.webui.entity.ComponentConfigurationLayoutTableEnt;
import org.knime.gateway.api.webui.entity.ComponentConfigurationLayoutTableEnt.ComponentConfigurationLayoutTableEntBuilder;
import org.knime.gateway.api.webui.entity.ComponentLayoutEnt;
import org.knime.gateway.api.webui.entity.ComponentLayoutEnt.ComponentLayoutEntBuilder;
import org.knime.gateway.api.webui.entity.ComponentViewLayoutEnt;
import org.knime.gateway.api.webui.entity.ComponentViewLayoutEnt.ComponentViewLayoutEntBuilder;
import org.knime.gateway.api.webui.entity.ComponentViewLayoutNodeEnt;
import org.knime.gateway.api.webui.entity.ComponentViewLayoutNodeEnt.ComponentViewLayoutNodeEntBuilder;
import org.knime.gateway.api.webui.entity.ComponentViewLayoutTableEnt;
import org.knime.gateway.api.webui.entity.ComponentViewLayoutTableEnt.ComponentViewLayoutTableEntBuilder;

/**
 * TODO: ...
 *
 * @author Kai Franze, KNIME GmbH, Germany
 * @since 5.8
 */
@SuppressWarnings("static-method")
public final class ComponentLayoutEntityFactory {

    ComponentLayoutEntityFactory() {
        // Package-scoped on purpose
    }

    /**
     * TODO: ...
     *
     * @param viewLayout
     * @param viewNodes
     * @param configurationLayout
     * @param configurationNodes
     * @return ...
     */
    public ComponentLayoutEnt buildComponentLayoutEnt(final String viewLayout, final List<String> viewNodes,
        final String configurationLayout, final List<String> configurationNodes) {
        final var viewLayoutTableEnt = buildComponentViewTableEnt(viewLayout);
        final var viewLayoutNodeEnts = viewNodes.stream() //
            .map(this::buildComponentViewLayoutNodeEnt) //
            .toList();
        final var viewLayoutEnt = buildComponentViewLayoutEnt(viewLayoutTableEnt, viewLayoutNodeEnts);

        final var configureationTableEnt = buildComponentConfigurationLayoutTableEnt(configurationLayout);
        final var configurationNodeEnts = configurationNodes.stream() //
            .map(this::buildComponentConfigurationLayoutNodeEnt) //
            .toList();
        final var configurationLayoutEnt =
            buildComponentConfigurationLayoutEnt(configureationTableEnt, configurationNodeEnts);

        return builder(ComponentLayoutEntBuilder.class) //
            .setViewLayout(viewLayoutEnt) //
            .setConfigurationLayout(configurationLayoutEnt) //
            .build();
    }

    private ComponentViewLayoutTableEnt buildComponentViewTableEnt(final String viewLayout) {
        return builder(ComponentViewLayoutTableEntBuilder.class) //
            .setData(viewLayout) //
            .build();
    }

    private ComponentViewLayoutNodeEnt buildComponentViewLayoutNodeEnt(final String viewNode) {
        return builder(ComponentViewLayoutNodeEntBuilder.class) //
            .setData(viewNode) //
            .build();
    }

    private ComponentViewLayoutEnt buildComponentViewLayoutEnt(final ComponentViewLayoutTableEnt table,
        final List<ComponentViewLayoutNodeEnt> nodes) {
        return builder(ComponentViewLayoutEntBuilder.class) //
            .setTable(table) //
            .setNodes(nodes) //
            .build();
    }

    private ComponentConfigurationLayoutTableEnt
        buildComponentConfigurationLayoutTableEnt(final String configurationLayout) {
        return builder(ComponentConfigurationLayoutTableEntBuilder.class) //
            .setData(configurationLayout) //
            .build();
    }

    private ComponentConfigurationLayoutNodeEnt
        buildComponentConfigurationLayoutNodeEnt(final String configurationNode) {
        return builder(ComponentConfigurationLayoutNodeEntBuilder.class) //
            .setData(configurationNode) //
            .build();
    }

    private ComponentConfigurationLayoutEnt buildComponentConfigurationLayoutEnt(
        final ComponentConfigurationLayoutTableEnt table, final List<ComponentConfigurationLayoutNodeEnt> nodes) {
        return builder(ComponentConfigurationLayoutEntBuilder.class) //
            .setTable(table) //
            .setNodes(nodes) //
            .build();
    }
}
