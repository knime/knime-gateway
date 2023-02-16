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
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.ToDoubleBiFunction;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import org.apache.commons.collections4.map.LRUMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.knime.core.data.sort.AlphanumericComparator;
import org.knime.core.node.util.CheckUtils;
import org.knime.core.ui.util.FuzzySearchable;
import org.knime.gateway.api.webui.entity.NodeSearchResultEnt;
import org.knime.gateway.api.webui.entity.NodeSearchResultEnt.NodeSearchResultEntBuilder;
import org.knime.gateway.api.webui.entity.NodeTemplateEnt;
import org.knime.gateway.api.webui.util.EntityFactory;
import org.knime.gateway.api.webui.util.WorkflowEntityFactory;
import org.knime.gateway.impl.webui.NodeRepository.Node;

/**
 * Logic and state (e.g. caching) required to search for nodes in the {@link NodeRepository}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class NodeSearch {

    private static final Comparator<String> ALPHANUMERIC_COMPARATOR = new AlphanumericComparator(Comparator.naturalOrder());

    /*
     * Lower (including) bound of similarity to a query a node may have to be labeled as a match.
     */
    private static final double SIMILARITY_THRESHOLD = 0.15;

    /**
     * Weight for the similarity score derived from keywords.
     */
    private static final double KEYWORD_SCORE_WEIGHT = 0.8;

    private static final ToDoubleBiFunction<FuzzySearchable, String> SCORING_FN =
            // keywords contribute slightly lower similarity values since their influence on the search result is
            // less obvious to the user
            (n, q) -> Math.max(n.computeNameSimilarity(q), KEYWORD_SCORE_WEIGHT * n.computeKeywordSimilarity(q));

    /*
     * Maps a search query to the list of found nodes.
     */
    private final Map<SearchQuery, List<Node>> m_foundNodesCache = Collections.synchronizedMap(new LRUMap<>(100));

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
     *            {@link WorkflowEntityFactory#buildMinimalNodeTemplateEnt(org.knime.core.node.NodeFactory)}
     * @param includeAll If true, all nodes/components will be included in the search result. Otherwise, only the
     *            nodes/components that are part of the current collection will be included.
     * @return the search result entity
     */
    public NodeSearchResultEnt searchNodes(final String q, final List<String> tags, final Boolean allTagsMatch,
        final Integer nodesOffset, final Integer nodesLimit, final Boolean fullTemplateInfo, final Boolean includeAll) {
        Collection<Node> allNodes;
        final boolean allNodesFromRepo = Boolean.TRUE.equals(includeAll);
        // this function gives us the chance to remove our "easter egg" before the search query normalizes the input
        Normalizer fn;
        if (q != null && q.endsWith("//hidden")) {
            allNodes = m_nodeRepo.getHiddenNodes();
            fn = t -> t.replace("//hidden", "");
        } else if (q != null && q.endsWith("//deprecated")) {
            allNodes = m_nodeRepo.getDeprecatedNodes();
            fn = t -> t.replace("//deprecated", "");
        } else {
            allNodes = m_nodeRepo.getNodes(allNodesFromRepo);
            fn = Normalizer.identity();
        }

        final var foundNodes = m_foundNodesCache.computeIfAbsent(
            new SearchQuery(q, SearchQuery.DEFAULT_NORMALIZATION.compose(fn), tags, Boolean.TRUE.equals(allTagsMatch),
                allNodesFromRepo), searchQuery -> searchNodes(allNodes, searchQuery));

        // map templates
        List<NodeTemplateEnt> templates = foundNodes.stream()
            .map(n -> Boolean.TRUE.equals(fullTemplateInfo) ? EntityFactory.NodeTemplateAndDescription.buildNodeTemplateEnt(n.factory)
                : EntityFactory.NodeTemplateAndDescription.buildMinimalNodeTemplateEnt(n.factory))//
            .filter(Objects::nonNull)
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

    private static List<Node> searchNodes(final Collection<Node> nodes, final SearchQuery searchQuery) {
        final var searchTerm = searchQuery.getSearchTerm();
        final var tags = searchQuery.m_tags;
        final var allTagsMatch = searchQuery.m_allTagsMatch;
        final Predicate<Node> tagFilter = n -> filterByTags(n, tags, allTagsMatch);
        if (searchTerm.isEmpty()) {
            // Case 1: no filter, no ranking
            if (tags == null || tags.isEmpty()) {
                return Collections.unmodifiableList(new ArrayList<>(nodes));
            }
            // Case 2: filter only by tags, rank nodes
            return nodes.stream().filter(tagFilter)//
                    .sorted(//
                        Comparator.<Node> comparingInt(n -> n.isIncluded ? 0 : 1)//
                        .thenComparingInt(n -> -n.weight)//
                        .thenComparing(n -> n.name, ALPHANUMERIC_COMPARATOR))//
                    .collect(Collectors.toList());
        }
        final var term = searchTerm.get();
        // Case 3: filter by tags, rank by similarity to search term
        return nodes.stream().filter(tagFilter)//
            .map(n -> new FoundNode(n,//
                StringUtils.containsIgnoreCase(n.name, term),//
                SCORING_FN.applyAsDouble(n.getFuzzySearchable(), term)))//
            .filter(n -> n.m_substringMatch || n.m_score >= SIMILARITY_THRESHOLD)//
            .sorted(//
                // 1) included nodes
                Comparator.<FoundNode> comparingInt(n -> n.m_node.isIncluded ? 0 : 1)//
                // 2) then exact substring matches (only based on names)
                .thenComparingInt(n -> n.m_substringMatch ? 0 : 1)//
                // 3) then fuzzy matches (also based on "hidden" keywords)
                .thenComparingDouble(n -> -n.m_score)//
                // 4) then on manually defined weight
                .thenComparingInt(n -> -n.m_node.weight)//
                // 5) tie-breaks
                .thenComparing(n -> n.m_node.name, ALPHANUMERIC_COMPARATOR))//
            .map(wn -> wn.m_node)//
            .collect(Collectors.toList());
    }

    private static boolean filterByTags(final Node n, final List<String> tags, final boolean allTagsMatch) {
        if (tags == null || tags.isEmpty()) {
            return true;
        }
        if (allTagsMatch) {
            return tags.stream().allMatch(n.tags::contains);
        }
        return tags.stream().anyMatch(n.tags::contains);
    }

    private static class FoundNode {

        final Node m_node;

        final boolean m_substringMatch;

        final double m_score;

        FoundNode(final Node node, final boolean substringMatch, final double score) {
            m_node = node;
            m_substringMatch = substringMatch;
            m_score = score;
        }
    }

    /**
     * This interface exists because UnaryOperator has no covariant overrides of {@code andThen} and {@code compose}.
     * So either we use {@code Function<String, String>} throughout and silence sonar everywhere, or we use this
     * interface.
     */
    interface Normalizer extends UnaryOperator<String> {
        static Normalizer identity() {
            return s -> s;
        }

        default Normalizer andThen(final Normalizer after) {
            CheckUtils.checkNotNull(after);
            return s -> after.apply(apply(s));
        }

        default Normalizer compose(final Normalizer before) {
            CheckUtils.checkNotNull(before);
            return s -> apply(before.apply(s));
        }
    }

    private static class SearchQuery {

        static final Normalizer DEFAULT_NORMALIZATION = t -> t.strip().toUpperCase();

        /**
         * The search term exactly as given by the user.
         */
        private final String m_surfaceForm;

        /**
         * The search term after our normalization (e.g. stripping whitespace).
         */
        private final String m_normalizedForm;

        private final List<String> m_tags;

        private final boolean m_allTagsMatch;

        private final boolean m_includeAll;

        /**
         * Creates a new search query, normalizing/analyzing the given search term.
         * @param searchTerm search term
         * @param normalization a function applied to the non-null term
         * @param tags
         * @param allTagsMatch
         * @param includeAll
         */
        SearchQuery(final String searchTerm, final Normalizer normalization, final List<String> tags,
                final boolean allTagsMatch, final boolean includeAll) {
            m_surfaceForm = searchTerm;
            m_normalizedForm = normalize(searchTerm, normalization);
            m_tags = tags;
            m_allTagsMatch = allTagsMatch;
            m_includeAll = includeAll;
        }

        private static String normalize(final String searchTerm, final Normalizer normalization) {
            if (searchTerm == null || normalization == null) {
                return searchTerm;
            }
            return normalization.apply(searchTerm);
        }

        /**
         * @return non-null and non-blank search term, or {@link Optional#empty()}
         */
        Optional<String> getSearchTerm() {
            if (StringUtils.isEmpty(m_normalizedForm)) {
                return Optional.empty();
            }
            return Optional.of(m_normalizedForm);
        }

        /**
         * Gets the raw search term exactly as it was given in the constructor.
         * @return raw search term
         */
        String getRawSearchTerm() {
            return m_surfaceForm;
        }

        @Override
        public int hashCode() {
            // explicitly don't include normalized form in hashCode/equals
            return new HashCodeBuilder().append(m_surfaceForm).append(m_tags).append(m_allTagsMatch)
                    .append(m_includeAll).build();
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
                return new EqualsBuilder().append(m_surfaceForm, sq.m_surfaceForm).append(m_tags, sq.m_tags)
                    .append(m_allTagsMatch, sq.m_allTagsMatch).append(m_includeAll, sq.m_includeAll).build();
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
