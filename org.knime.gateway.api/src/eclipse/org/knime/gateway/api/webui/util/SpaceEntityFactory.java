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
 *   Dec 12, 2022 (hornm): created
 */
package org.knime.gateway.api.webui.util;

import static org.knime.gateway.api.entity.EntityBuilderManager.builder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.knime.gateway.api.webui.entity.SpaceEnt;
import org.knime.gateway.api.webui.entity.SpaceEnt.SpaceEntBuilder;
import org.knime.gateway.api.webui.entity.SpaceItemEnt;
import org.knime.gateway.api.webui.entity.SpaceItemEnt.SpaceItemEntBuilder;
import org.knime.gateway.api.webui.entity.SpacePathSegmentEnt;
import org.knime.gateway.api.webui.entity.SpacePathSegmentEnt.SpacePathSegmentEntBuilder;
import org.knime.gateway.api.webui.entity.SpaceProviderEnt;
import org.knime.gateway.api.webui.entity.SpaceProviderEnt.SpaceProviderEntBuilder;
import org.knime.gateway.api.webui.entity.WorkflowGroupContentEnt;
import org.knime.gateway.api.webui.entity.WorkflowGroupContentEnt.WorkflowGroupContentEntBuilder;

/**
 * See {@link EntityFactory}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @author Kai Franze, KNIME GmbH
 */
@SuppressWarnings("static-method")
public final class SpaceEntityFactory {

    SpaceEntityFactory() {
        //
    }

    /**
     * @param spaces
     *
     * @return a new {@link SpaceProviderEnt}-instance
     */
    public SpaceProviderEnt buildSpaceProviderEnt(final List<SpaceEnt> spaces) {
        return builder(SpaceProviderEntBuilder.class) //
            .setSpaces(spaces) //
            .build();
    }

    /**
     * @param id
     * @param name
     * @param owner
     * @param description
     * @param isPrivate
     *
     * @return a new {@link SpaceEnt}-instance
     */
    public SpaceEnt buildSpaceEnt(final String id, final String name, final String owner, final String description,
        final Boolean isPrivate) {
        return builder(SpaceEntBuilder.class) //
            .setId(id) //
            .setName(name) //
            .setOwner(owner) //
            .setDescription(description) //
            .setPrivate(isPrivate) //
            .build();
    }

    /**
     * Builds a {@link WorkflowGroupContentEnt}-entity from an absolute local directory-path by listening all contained
     * items.
     *
     * @param absolutePath the absolute path to list the items for
     * @param rootWorkspacePath workspace root path
     * @param getItemId function which determines the id per item/path
     * @param getItemType function which determines the type per item/path
     * @param itemFilter determines the items to be excluded (e.g. hidden files)
     * @param comparator determines the order of the items
     * @return a new entity instance
     * @throws IOException
     */
    public WorkflowGroupContentEnt buildLocalWorkflowGroupContentEnt(final Path absolutePath,
            final Path rootWorkspacePath, final Function<Path, String> getItemId,
            final Function<Path, SpaceItemEnt.TypeEnum> getItemType, final Predicate<Path> itemFilter,
            final Comparator<SpaceItemEnt> comparator) throws IOException {
        final var isRoot = absolutePath.equals(rootWorkspacePath);
        final var relativePath = rootWorkspacePath.relativize(absolutePath);
        final var path = isRoot ? Collections.<SpacePathSegmentEnt> emptyList()
            : buildSpacePathSegmentEnts(absolutePath, relativePath.getNameCount(), getItemId);
        final var items = buildLocalSpaceItemEnts(absolutePath, rootWorkspacePath, getItemId, getItemType, itemFilter,
            comparator);
        return builder(WorkflowGroupContentEntBuilder.class) //
            .setPath(path) //
            .setItems(items) //
            .build();
    }

    /**
     * Builds a {@link WorkflowGroupContentEnt}-entity for a workflow group on Hub.
     *
     * @param path path of the workflow group
     * @param items items in the workflow group
     * @return group contents entity
     */
    public WorkflowGroupContentEnt buildHubWorkflowGroupContentEnt(final List<SpacePathSegmentEnt> path,
            final List<SpaceItemEnt> items) {
        return builder(WorkflowGroupContentEntBuilder.class) //
            .setPath(path) //
            .setItems(items) //
            .build();
    }

    private List<SpaceItemEnt> buildLocalSpaceItemEnts(final Path absolutePath, final Path rootWorkspacePath,
        final Function<Path, String> getItemId, final Function<Path, SpaceItemEnt.TypeEnum> getItemType,
        final Predicate<Path> itemFilter, final Comparator<SpaceItemEnt> comparator) throws IOException {
        try (var pathsStream = Files.list(absolutePath)) {
            return pathsStream.filter(itemFilter) //
                .map(p -> {
                        final var relativePath = rootWorkspacePath.relativize(p);
                        final var name = relativePath.getName(relativePath.getNameCount() - 1).toString();
                        return buildSpaceItemEnt(name, getItemId.apply(p), getItemType.apply(p));
                    }) //
                .sorted(comparator) //
                .collect(Collectors.toList());
        }
    }

    private List<SpacePathSegmentEnt> buildSpacePathSegmentEnts(final Path absolutePath,
        final int relativePathNameCount, final Function<Path, String> getItemId) {
        var res = new SpacePathSegmentEnt[relativePathNameCount];
        var parent = absolutePath;
        for (int i = relativePathNameCount - 1; i >= 0; i--) {
            res[i] = buildSpacePathSegmentEnt(getItemId.apply(parent), parent.getFileName().toString());
            parent = parent.getParent();
        }
        return Arrays.asList(res);
    }

    /**
     * Builds a space path segment entity.
     *
     * @param itemId item ID of the entity the segment represents
     * @param name name of the entity the segment represents
     * @return path segment entity
     */
    public SpacePathSegmentEnt buildSpacePathSegmentEnt(final String itemId, final String name) {
        return builder(SpacePathSegmentEntBuilder.class).setId(itemId).setName(name).build();
    }

    /**
     * Creates a space item entity.
     *
     * @param name item name
     * @param id item ID
     * @param type item type
     * @return resulting entity
     */
    public SpaceItemEnt buildSpaceItemEnt(final String name, final String id,
        final SpaceItemEnt.TypeEnum type) {
        return builder(SpaceItemEntBuilder.class) //
            .setId(id) //
            .setName(name) //
            .setType(type) //
            .build();
    }

    /**
     * Builds a {@link SpaceItemEnt} from an absolute local directory path pointing to a workflow.
     *
     * @param absolutePath The absolute path of the newly created workflow
     * @param rootWorkspacePath Workspace root path
     * @param id The ID of the newly created workflow
     * @return The space item entity
     */
    public SpaceItemEnt buildSpaceItemEnt(final Path absolutePath, final Path rootWorkspacePath,
        final String id) {
        var relativePath = rootWorkspacePath.relativize(absolutePath);
        return buildSpaceItemEnt(relativePath, id, TypeEnum.WORKFLOW);
    }

}
