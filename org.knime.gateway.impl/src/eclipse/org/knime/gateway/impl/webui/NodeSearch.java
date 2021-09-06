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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.knime.core.util.LRUCache;
import org.knime.gateway.api.webui.entity.NodeSearchResultEnt;
import org.knime.gateway.api.webui.entity.NodeSearchResultEnt.NodeSearchResultEntBuilder;
import org.knime.gateway.api.webui.entity.NodeTemplateEnt;
import org.knime.gateway.api.webui.util.EntityBuilderUtil;
import org.knime.gateway.impl.webui.NodeRepository.Node;

/**
 * Logic and state (e.g. caching) required to search for nodes in the {@link NodeRepository}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class NodeSearch {

    /*
     * Upper excluding bound of distances to a query a node may have to be labeled as a match.
     */
    private static final double DISTANCE_THRESHOLD = 0.85;

    /*
     * Maps a search query to the list of found nodes.
     */
    private final Map<SearchQuery, List<Node>> m_foundNodesCache = Collections.synchronizedMap(new LRUCache<>(100));

    private final NodeRepository m_nodeRepo;

    /**
     * Creates a new instance.
     *
     * @param nodeRepo the node repository to search in
     */
    public NodeSearch(final NodeRepository nodeRepo) {
        m_nodeRepo = nodeRepo;
    }

    /**
     * Performs a node search and compiles the result accordingly.
     *
     * @param q the search query or a blank string
     * @param tags only the nodes which have at least one of the tags are considered in the search result
     * @param allTagsMatch if <code>true</code>, only the nodes/components that have all of the given tags are included
     *            in the search result. Otherwise nodes/components that have at least one of the given tags are
     *            included.
     * @param nodesOffset the number of nodes to skip (in the list of found nodes, which have a fixed order) - for
     *            pagination
     * @param nodesLimit the maximum number of nodes to include in the search result (mainly for pagination)
     * @param fullTemplateInfo see
     *            {@link EntityBuilderUtil#buildMinimalNodeTemplateEnt(org.knime.core.node.NodeFactory)}
     * @return the search result entity
     */
    public NodeSearchResultEnt searchNodes(final String q, final List<String> tags, final Boolean allTagsMatch,
        final Integer nodesOffset, final Integer nodesLimit, final Boolean fullTemplateInfo) {
        Collection<Node> allNodes;
        String query;
        if (q != null && q.endsWith("//hidden")) {
            allNodes = m_nodeRepo.getHiddenNodes();
            query = q.replace("//hidden", "");
        } else if (q != null && q.endsWith("//deprecated")) {
            allNodes = m_nodeRepo.getDeprecatedNodes();
            query = q.replace("//deprecated", "");
        } else {
            allNodes = m_nodeRepo.getNodes();
            query = q;
        }

        List<Node> foundNodes = m_foundNodesCache.computeIfAbsent(new SearchQuery(q, tags, allTagsMatch),
            key -> searchNodes(allNodes, query, tags, allTagsMatch));

        // map templates
        List<NodeTemplateEnt> templates = foundNodes.stream()
            .map(n -> Boolean.TRUE.equals(fullTemplateInfo) ? EntityBuilderUtil.buildNodeTemplateEnt(n.factory)
                : EntityBuilderUtil.buildMinimalNodeTemplateEnt(n.factory))//
            .skip(nodesOffset == null ? 0 : nodesOffset).limit(nodesLimit == null ? Long.MAX_VALUE : nodesLimit)//
            .collect(Collectors.toList());

        // collect all tags from the templates and sort according to their frequency
        Map<String, Long> tagFrequencies = foundNodes.stream().flatMap(n -> n.tags.stream())
            .collect(Collectors.groupingBy(t -> t, HashMap::new, Collectors.counting()));
        List<String> resTags = tagFrequencies.entrySet().stream()//
            .sorted(Comparator.<Entry<String, Long>, Long> comparing(Entry::getValue).reversed())//
            .map(Entry::getKey)//
            .collect(Collectors.toList());

        return builder(NodeSearchResultEntBuilder.class)//
            .setNodes(templates)//
            .setTags(resTags)//
            .setTotalNumNodes(foundNodes.size())//
            .build();
    }

    @SuppressWarnings("java:S3776")
    private static List<Node> searchNodes(final Collection<Node> nodes, final String q, final List<String> tags,
        final Boolean allTagsMatch) {
        List<Node> foundNodes;
        boolean isSearchQueryGiven = !StringUtils.isBlank(q);
        if (!isSearchQueryGiven && (tags == null || tags.isEmpty())) {
            foundNodes = new ArrayList<>(nodes);
        } else {
            Stream<Node> tagFiltered = nodes.stream()//
                .filter(n -> filterByTags(n, tags, allTagsMatch));
            if (isSearchQueryGiven) {
                final String upperCaseQuery = q.toUpperCase();
                foundNodes = tagFiltered.map(n -> {
                    String upperCaseName = n.name.toUpperCase();
                    double score = upperCaseName.contains(upperCaseQuery) ? 0.0
                        : TanimotoBiGramDistance.computeTanimotoBiGramDistance(upperCaseName, upperCaseQuery);
                    return new FoundNode(n, score);
                })//
                    .filter(n -> n.score < DISTANCE_THRESHOLD)//
                    .sorted(Comparator.<FoundNode> comparingDouble(n -> n.score).thenComparingInt(n -> -n.node.weight))//
                    .map(wn -> wn.node)//
                    .collect(Collectors.toList());
            } else {
                foundNodes =
                    tagFiltered.sorted(Comparator.<Node> comparingInt(n -> -n.weight)).collect(Collectors.toList());
            }
        }
        return foundNodes;
    }

    private static boolean filterByTags(final Node n, final List<String> tags, final Boolean allTagsMatch) {
        if (tags == null || tags.isEmpty()) {
            return true;
        } else {
            if (Boolean.TRUE.equals(allTagsMatch)) {
                return tags.stream().allMatch(n.tags::contains);
            } else {
                return tags.stream().anyMatch(n.tags::contains);
            }
        }
    }

    @SuppressWarnings("java:S116")
    private static class FoundNode {

        Node node;

        double score;

        @SuppressWarnings("hiding")
        FoundNode(final Node node, final double score) {
            this.node = node;
            this.score = score;
        }
    }

    private static class SearchQuery {

        private String m_q;

        private List<String> m_tags;

        private Boolean m_allTagsMatch;

        @SuppressWarnings("hiding")
        SearchQuery(final String q, final List<String> tags, final Boolean allTagsMatch) {
            m_q = q;
            m_tags = tags;
            m_allTagsMatch = allTagsMatch;

        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder().append(m_q).append(m_tags).append(m_allTagsMatch).build();
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() == obj.getClass()) {
                SearchQuery sq = (SearchQuery)obj;
                return new EqualsBuilder().append(m_q, sq.m_q).append(m_tags, sq.m_tags)
                    .append(m_allTagsMatch, sq.m_allTagsMatch).build();
            }
            return false;
        }

    }

    /**
     * For testing purposes only!
     *
     * @return the size of the 'found nodes' cache
     */
    int cacheSize() {
        return m_foundNodesCache.size();
    }

}
