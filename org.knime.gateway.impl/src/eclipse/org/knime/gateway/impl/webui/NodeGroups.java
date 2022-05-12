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

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.knime.core.node.extension.CategoryExtension;
import org.knime.core.node.extension.CategoryExtensionManager;
import org.knime.core.node.extension.NodeAndCategorySorter;
import org.knime.core.util.Pair;
import org.knime.gateway.api.webui.entity.NodeGroupEnt;
import org.knime.gateway.api.webui.entity.NodeGroupEnt.NodeGroupEntBuilder;
import org.knime.gateway.api.webui.entity.NodeGroupsEnt;
import org.knime.gateway.api.webui.entity.NodeGroupsEnt.NodeGroupsEntBuilder;
import org.knime.gateway.api.webui.entity.NodeTemplateEnt;
import org.knime.gateway.api.webui.util.EntityBuilderUtil;
import org.knime.gateway.impl.webui.NodeRepository.Node;

/**
 * Logic and state (e.g. caching) required to filter and group nodes from the {@link NodeRepository}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public final class NodeGroups {

    private static final String UNCATEGORIZED_KEY = "/uncategorized";

    /*
     * ('top-level') tag for nodes that are at root-level, that are without a category or
     * that reference a (first-level) category that is not registered (via the category-extension point).
     */
    private static final String UNCATEGORIZED_NAME = "Uncategorized";

    private final NodeRepository m_nodeRepo;

    private Map<String, List<Node>> m_nodesPerCategory;

    private List<Pair<String, String>> m_topLevelCats;

    /**
     * Creates a new instance.
     *
     * @param nodeRepo the node repository to select the nodes from
     */
    public NodeGroups(final NodeRepository nodeRepo) {
        m_nodeRepo = nodeRepo;
    }

    /**
     * Filters and groups nodes nodes from the node repository.
     *
     * @param numNodesPerTag the number of nodes per selected tag
     * @param tagsOffset the number of tags to be skipped (the tags have a fixed order)
     * @param tagsLimit the maximum number of tags to select
     * @param fullTemplateInfo see
     *            {@link EntityBuilderUtil#buildMinimalNodeTemplateEnt(org.knime.core.node.NodeFactory)}
     * @return the node groups entity
     */
    public NodeGroupsEnt getNodesGroupedByTags(final Integer numNodesPerTag, final Integer tagsOffset,
        final Integer tagsLimit, final Boolean fullTemplateInfo) {
        initNodesAndCategories();
        List<NodeGroupEnt> groups = m_topLevelCats.stream()//
            .skip(tagsOffset == null ? 0 : tagsOffset)//
            .limit(tagsLimit == null ? Integer.MAX_VALUE : tagsLimit)//
            .map(p -> buildNodeGroupEnt(p.getFirst(), p.getSecond(), numNodesPerTag, fullTemplateInfo))//
            .filter(Objects::nonNull)//
            .collect(Collectors.toList());
        return builder(NodeGroupsEntBuilder.class).setGroups(groups)
            .setTotalNumGroups(m_nodesPerCategory.size()).build();
    }

    private synchronized void initNodesAndCategories() {
        if (m_nodesPerCategory == null) {
            Map<String, CategoryExtension> cats = CategoryExtensionManager.getInstance().getCategoryExtensions();
            List<Pair<String, String>> topLevelCats = getSortedCategoriesAtLevel("/", cats.values());
            Pair<String, String> uncat = Pair.create(UNCATEGORIZED_KEY, UNCATEGORIZED_NAME);
            if (!topLevelCats.contains(uncat)) {
                topLevelCats.add(uncat);
            }
            Map<String, List<Node>> nodesPerCategory = categorizeNodes(m_nodeRepo.getNodes(), topLevelCats);

            m_topLevelCats = Collections.synchronizedList(topLevelCats);
            m_nodesPerCategory = Collections.synchronizedMap(nodesPerCategory);
        }
    }

    private static List<Pair<String, String>> getSortedCategoriesAtLevel(final String levelId,
        final Collection<CategoryExtension> categories) {
        return NodeAndCategorySorter.sortCategoryExtensions(categories.stream()//
            .filter(c -> {
                String p = c.getPath();
                return p == null || StringUtils.isBlank(p) || p.equals(levelId);
            }))//
            .map(c -> Pair.create(c.getCompletePath(), c.getName()))//
            .collect(Collectors.toList());
    }

    private static Map<String, List<Node>> categorizeNodes(final Collection<Node> allNodes,
        final List<Pair<String, String>> categories) {
        Map<String, List<Node>> res = new HashMap<>();
        Set<String> categorized = new HashSet<>();
        for (Pair<String, String> c : categories) {
            String catPath = c.getFirst();
            if (catPath.equals(UNCATEGORIZED_KEY)) {
                // 'uncategorized' nodes are handled below
                continue;
            }
            List<Node> nodes = allNodes.stream()//
                .filter(n -> n.path.equals(catPath) || n.path.startsWith(catPath + "/"))//
                .sorted(Comparator.<Node> comparingInt(n -> n.weight).reversed())//
                .map(n -> {
                    categorized.add(n.templateId);
                    return n;
                })//
                .collect(Collectors.toList());
            if (!nodes.isEmpty()) {
                res.put(catPath, nodes);
            }
        }

        // collect all nodes that didn't end up in any of the given categories
        // (e.g. because they are at root level '/' or don't have a category at all)
        List<Node> uncategorizedNodes =
            allNodes.stream().filter(n -> !categorized.contains(n.templateId)).collect(Collectors.toList());
        if (!uncategorizedNodes.isEmpty()) {
            res.put(UNCATEGORIZED_KEY, uncategorizedNodes);
        }
        return res;
    }

    private NodeGroupEnt buildNodeGroupEnt(final String completePath, final String name,
        final Integer numNodesPerTag, final Boolean fullTemplateInfo) {
        List<Node> nodesPerCategory = m_nodesPerCategory.get(completePath);
        if (nodesPerCategory == null || nodesPerCategory.isEmpty()) {
            return null;
        }
        List<NodeTemplateEnt> res = nodesPerCategory.stream()//
            .limit(numNodesPerTag == null ? Integer.MAX_VALUE : numNodesPerTag)//
            .map(n -> Boolean.TRUE.equals(fullTemplateInfo) ? EntityBuilderUtil.buildNodeTemplateEnt(n.factory)
                : EntityBuilderUtil.buildMinimalNodeTemplateEnt(n.factory))//
            .filter(Objects::nonNull)//
            .collect(Collectors.toList());
        return builder(NodeGroupEntBuilder.class).setNodes(res).setTag(name).build();
    }
}
