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
import java.util.stream.StreamSupport;

import org.knime.gateway.api.webui.entity.SpaceItemEnt;
import org.knime.gateway.api.webui.entity.SpaceItemEnt.SpaceItemEntBuilder;
import org.knime.gateway.api.webui.entity.SpaceItemsEnt;
import org.knime.gateway.api.webui.entity.SpaceItemsEnt.SpaceItemsEntBuilder;

/**
 * See {@link EntityBuilderUtil}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@SuppressWarnings("static-method")
public final class SpaceEntityBuilder {

    SpaceEntityBuilder() {
        //
    }

    /**
     * Builds a {@link SpaceItemsEnt}-entity from an absolute local directory-path by listening all contained items.
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
    public SpaceItemsEnt buildSpaceItemsEnt(final Path absolutePath, final Path rootWorkspacePath,
        final Function<Path, String> getItemId, final Function<Path, SpaceItemEnt.TypeEnum> getItemType,
        final Predicate<Path> itemFilter, final Comparator<SpaceItemEnt> comparator) throws IOException {
        var isRoot = absolutePath.equals(rootWorkspacePath);
        var relativePath = rootWorkspacePath.relativize(absolutePath);
        var pathIds = isRoot ? Collections.<String> emptyList()
            : getPathIds(absolutePath, relativePath.getNameCount(), getItemId);
        var pathNames = isRoot ? Collections.<String> emptyList() : getPathNames(relativePath);
        var items = buildSpaceItemEnts(absolutePath, rootWorkspacePath, getItemId, getItemType, itemFilter, comparator);
        return builder(SpaceItemsEntBuilder.class) //
            .setPathIds(pathIds) //
            .setPathNames(pathNames) //
            .setItems(items) //
            .build();
    }

    private static List<SpaceItemEnt> buildSpaceItemEnts(final Path absolutePath, final Path rootWorkspacePath,
        final Function<Path, String> getItemId, final Function<Path, SpaceItemEnt.TypeEnum> getItemType,
        final Predicate<Path> itemFilter, final Comparator<SpaceItemEnt> comparator) throws IOException {
        try (var pathsStream = Files.list(absolutePath)) {
            return pathsStream.filter(itemFilter) //
                .map(p -> buildSpaceItemEnt(rootWorkspacePath.relativize(p), getItemId.apply(p), getItemType.apply(p))) //
                .sorted(comparator) //
                .collect(Collectors.toList());
        }
    }

    private static List<String> getPathNames(final Path relativePath) {
        return StreamSupport.stream(relativePath.spliterator(), false).map(Path::toString).collect(Collectors.toList());
    }

    private static List<String> getPathIds(final Path absolutePath, final int relativePathNameCount,
        final Function<Path, String> getItemId) {
        var res = new String[relativePathNameCount];
        var parent = absolutePath;
        for (int i = res.length - 1; i >= 0; i--) {
            res[i] = getItemId.apply(parent);
            parent = parent.getParent();
        }
        return Arrays.asList(res);
    }

    private static SpaceItemEnt buildSpaceItemEnt(final Path relativePath, final String id,
        final SpaceItemEnt.TypeEnum type) {
        return builder(SpaceItemEntBuilder.class) //
            .setId(id) //
            .setName(relativePath.getName(relativePath.getNameCount() - 1).toString()) //
            .setType(type) //
            .build();
    }

}