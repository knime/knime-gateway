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
 *   Mar 22, 2021 (hornm): created
 */
package org.knime.gateway.impl.webui;

import static org.knime.gateway.api.entity.EntityBuilderManager.builder;
import static org.knime.gateway.impl.webui.NodeRepository.Node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.knime.core.node.extension.CategoryExtension;
import org.knime.core.node.extension.NodeAndCategorySorter;
import org.knime.core.node.extension.NodeSpecCollectionProvider;
import org.knime.core.util.Pair;
import org.knime.gateway.api.webui.entity.NodeGroupEnt;
import org.knime.gateway.api.webui.entity.NodeGroupEnt.NodeGroupEntBuilder;
import org.knime.gateway.api.webui.entity.NodeGroupsEnt;
import org.knime.gateway.api.webui.entity.NodeGroupsEnt.NodeGroupsEntBuilder;

/**
 * Logic and state (e.g. caching) required to filter and group nodes from the {@link NodeRepository}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public final class NodeGroups {

    private final NodeRepository m_nodeRepo;

    private final NodeCategoryExtensions m_nodeCategoryExtensions;

    private Map<String, List<Node>> m_nodesPerCategory;

    private List<Pair<String, String>> m_topLevelCats;

    /**
     * Creates a new instance.
     *
     * @param nodeRepo the node repository to select the nodes from
     */
    public NodeGroups(final NodeRepository nodeRepo) {
        this(nodeRepo, () -> NodeSpecCollectionProvider.getInstance().getCategoryExtensions());
    }

    public NodeGroups(final NodeRepository nodeRepo, final NodeCategoryExtensions nodeCategoryExtensions) {
        m_nodeRepo = nodeRepo;
        m_nodeCategoryExtensions = nodeCategoryExtensions;
    }

    /**
     * Filters and groups nodes from the node repository.
     *
     * @param numNodesPerTag the number of nodes per selected tag
     * @param tagsOffset the number of tags to be skipped (the tags have a fixed order)
     * @param tagsLimit the maximum number of tags to select
     * @param fullTemplateInfo see {@link NodeRepository#getNodeTemplate(String, boolean)}
     * @return the node groups entity
     */
    public NodeGroupsEnt getNodesGroupedByTags(final Integer numNodesPerTag, final Integer tagsOffset,
        final Integer tagsLimit, final Boolean fullTemplateInfo) {
        initCategories();
        var nodesPerCategory = getNodesPerCategory();

        var groups = m_topLevelCats.stream()//
            .filter(category -> nodesPerCategory.containsKey(category.getFirst()))//
            .skip(tagsOffset == null ? 0 : tagsOffset)//
            .limit(tagsLimit == null ? Integer.MAX_VALUE : tagsLimit)//
            .map(p -> buildNodeGroupEnt(nodesPerCategory.get(p.getFirst()), p.getSecond(), numNodesPerTag,
                fullTemplateInfo, m_nodeRepo))//
            .filter(Objects::nonNull)//
            .toList();
        return builder(NodeGroupsEntBuilder.class).setGroups(groups).setTotalNumGroups(nodesPerCategory.size()).build();
    }

    private synchronized void initCategories() {
        if (m_topLevelCats == null) {
            var topLevelCategories = getSortedCategoriesAtLevel("/", m_nodeCategoryExtensions.get().values());
            var uncategorizedCategory =
                Pair.create(NodeCategories.UNCATEGORIZED_KEY, NodeCategories.UNCATEGORIZED_NAME);
            if (!topLevelCategories.contains(uncategorizedCategory)) {
                topLevelCategories.add(uncategorizedCategory);
            }
            m_topLevelCats = Collections.synchronizedList(topLevelCategories);
        }
    }

    private synchronized Map<String, List<Node>> getNodesPerCategory() {
        if (m_nodesPerCategory == null) {
            m_nodesPerCategory = Collections.synchronizedMap(categorizeNodes(m_nodeRepo.getNodes(), m_topLevelCats));
        }
        return m_nodesPerCategory;
    }

    private static List<Pair<String, String>> getSortedCategoriesAtLevel(final String targetPath,
        final Collection<CategoryExtension> categories) {
        return NodeAndCategorySorter.sortCategoryExtensions(categories.stream()//
            .filter(category -> {
                var categoryPath = category.getPath();
                return categoryPath == null || StringUtils.isBlank(categoryPath) || categoryPath.equals(targetPath);
            }))//
            .map(cat -> Pair.create(cat.getCompletePath(), cat.getName()))//
            .toList();
    }

    /**
     * @return Map of category path to list of nodes in that category
     */
    private static Map<String, List<Node>> categorizeNodes(final Collection<Node> nodes,
        final List<Pair<String, String>> targetCategories) {
        Map<String, List<Node>> categorizedNodes = new HashMap<>();
        Set<String> alreadyCategorized = new HashSet<>();
        for (final var targetCategory : targetCategories) {
            var catPath = targetCategory.getFirst();
            if (catPath.equals(NodeCategories.UNCATEGORIZED_KEY)) {
                // 'uncategorized' nodes are handled below
                continue;
            }
            List<Node> nodesMatchingTargetCategory = new ArrayList<>();
            nodes.stream()//
                .filter(n -> n.nodeSpec().metadata().categoryPath().equals(catPath)
                    || n.nodeSpec().metadata().categoryPath().startsWith(catPath + "/"))//
                .sorted(Comparator.<Node> comparingInt(n -> n.weight()).reversed())//
                .forEach(n -> { // No `collect(...)` here, nodes are collected in two different ways at the same time
                    alreadyCategorized.add(n.templateId());
                    nodesMatchingTargetCategory.add(n);
                });
            if (!nodesMatchingTargetCategory.isEmpty()) {
                categorizedNodes.put(catPath, nodesMatchingTargetCategory);
            }
        }

        // collect all nodes that didn't end up in any of the given categories
        // (e.g. because they are at root level '/' or don't have a category at all)
        var uncategorizedNodes = nodes.stream() //
            .filter(n -> !alreadyCategorized.contains(n.templateId())) //
            .toList();
        if (!uncategorizedNodes.isEmpty()) {
            categorizedNodes.put(NodeCategories.UNCATEGORIZED_KEY, uncategorizedNodes);
        }
        return categorizedNodes;
    }

    private static NodeGroupEnt buildNodeGroupEnt(final List<Node> nodesPerCategory, final String name,
        final Integer numNodesPerTag, final Boolean fullTemplateInfo, final NodeRepository nodeRepo) {
        if (nodesPerCategory == null || nodesPerCategory.isEmpty()) {
            return null;
        }
        var res = nodesPerCategory.stream()//
            .limit(numNodesPerTag == null ? Integer.MAX_VALUE : numNodesPerTag)//
            .map(n -> nodeRepo.getNodeTemplate(n.templateId(), Boolean.TRUE.equals(fullTemplateInfo)))//
            .filter(Objects::nonNull)//
            .toList();
        return builder(NodeGroupEntBuilder.class).setNodes(res).setTag(name).build();
    }
}
