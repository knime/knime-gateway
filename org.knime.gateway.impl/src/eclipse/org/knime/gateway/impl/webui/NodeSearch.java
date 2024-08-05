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
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.collections4.map.LRUMap;
import org.apache.commons.lang3.StringUtils;
import org.knime.core.data.sort.AlphanumericComparator;
import org.knime.core.ui.util.FuzzySearchable;
import org.knime.gateway.api.webui.entity.DirectionEnt;
import org.knime.gateway.api.webui.entity.NodeSearchResultEnt;
import org.knime.gateway.api.webui.entity.NodeSearchResultEnt.NodeSearchResultEntBuilder;
import org.knime.gateway.api.webui.entity.NodeTemplateEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.InvalidRequestException;
import org.knime.gateway.impl.webui.NodeRepository.Node;

/**
 * Logic and state (e.g. caching) required to search for nodes in the {@link NodeRepository}.
 * <p>
 * Each node repository search will search in two sets ({@link NodePartition}). This can be used to indicate that
 * although there were no matches found for some current filtering, matches have been found in some complementary set.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @author Kai Franze, KNIME GmbH, Konstanz, Germany
 * @author Benjamin Moser, KNIME GmbH, konstanz, Germany
 */
public class NodeSearch {

    private static final Comparator<String> ALPHANUMERIC_COMPARATOR =
        new AlphanumericComparator(Comparator.naturalOrder());

    /*
     * Lower (including) bound of similarity to a query a node may have to be labeled as a match.
     */
    private static final double SIMILARITY_THRESHOLD = 0.15;

    /**
     * Weight for the similarity score derived from keywords.
     */
    private static final double KEYWORD_SCORE_WEIGHT = 0.8;

    /**
     * Score a candidate result. Keywords contribute slightly lower similarity values since their influence on the
     * search result is less obvious to the user
     */
    private static double score(final FuzzySearchable candidate, final String query) {
        return Math.max( //
            candidate.computeNameSimilarity(query), //
            candidate.computeKeywordSimilarity(query) * KEYWORD_SCORE_WEIGHT //
        );
    }

    /*
     * Maps a search query to the list of found nodes. Note that this cache depends on the state of the backing
     * node repository.
     */
    private final Map<SearchQuery, SearchResult> m_nodeSearchResultCache =
        Collections.synchronizedMap(new LRUMap<>(100));

    private final NodeRepository m_nodeRepo;

    /**
     * Creates a new instance.
     *
     * @param nodeRepo the node repository to search in
     */
    public NodeSearch(final NodeRepository nodeRepo) {
        m_nodeRepo = nodeRepo;
        m_nodeRepo.onContentChange(m_nodeSearchResultCache::clear);
    }

    /**
     * Performs a node search and compiles the result accordingly.
     *
     * @param queryString the search query or a blank string
     * @param tags only the nodes which have at least one of the tags are included in the search result
     * @param allTagsMatch if <code>true</code>, only the nodes/components that have all the given tags are included in
     *            the search result. Otherwise, nodes/components that have at least one of the given tags are included.
     * @param offset the number of nodes to skip (in the list of found nodes, which have a fixed order) - for pagination
     * @param limit the maximum number of nodes to include in the search result (mainly for pagination)
     * @param includeFullTemplateInfo Whether to include the full node template information or not.
     * @param portTypeId The port type all returned nodes (and components) have to be compatible with.
     * @param searchDirection if the search should be for successors of predecessors of the given portTypeId, if null it
     *            will allow any node
     *
     * @return the search result entity
     * @throws InvalidRequestException
     */
    @SuppressWarnings("java:S107")
    public NodeSearchResultEnt searchNodes(final String queryString, final List<String> tags,
        final Boolean allTagsMatch, final Integer offset, final Integer limit, final Boolean includeFullTemplateInfo,
        final String portTypeId, final DirectionEnt searchDirection) throws InvalidRequestException {

        if (portTypeId == null ^ searchDirection == null) {
            throw new InvalidRequestException(
                "Both <portTypeId> and <searchDirection> must either be both null or both not null");
        }
        final var searchForSuccesors =
            searchDirection == null || searchDirection.getDirection() == DirectionEnt.DirectionEnum.SUCCESSORS;
        final var query = new SearchQuery(queryString, tags, allTagsMatch, portTypeId, searchForSuccesors);
        // the partition is kept separate from the query to allow equals-checks for queries, which makes it simple
        // to cache them in a map.
        final var partition = partitionNodesOf(m_nodeRepo, query);
        final var searchResult = m_nodeSearchResultCache.computeIfAbsent(query, q -> searchNodes(partition, q));

        // map templates
        List<NodeTemplateEnt> foundTemplates = searchResult.foundNodes().stream()
            .map(n -> m_nodeRepo.getNodeTemplate(n.templateId, Boolean.TRUE.equals(includeFullTemplateInfo)))//
            .filter(Objects::nonNull)//
            .skip(offset == null ? 0 : offset)//
            .limit(limit == null ? Long.MAX_VALUE : limit)//
            .toList();

        // collect all tags from the templates and sort according to their frequency
        var tagFrequencies = searchResult.foundNodes().stream() //
            .flatMap(n -> n.nodeSpec.metadata().tags().stream()) //
            .collect(Collectors.groupingBy(t -> t, HashMap::new, Collectors.counting())); //
        var tagsSortedByFrequencyDescending = tagFrequencies.entrySet().stream() //
            .sorted(Entry.<String, Long> comparingByValue().reversed()) //
            .map(Entry::getKey) //
            .toList();

        return builder(NodeSearchResultEntBuilder.class)//
            .setNodes(foundTemplates)//
            .setTags(tagsSortedByFrequencyDescending)//
            .setTotalNumNodesFound(searchResult.foundNodes().size())//
            .setTotalNumFilteredNodesFound(searchResult.numSecondaryNodesFound)//
            .build();
    }

    private static SearchResult searchNodes(final NodePartition partition, final SearchQuery query) {
        final var foundNodes = searchNodes(partition.primary(), query);
        Integer numSecondaryNodesFound = partition.secondary() != null ? //
            searchNodes(partition.secondary(), query).size() //
            : null;
        return new SearchResult(foundNodes, numSecondaryNodesFound);
    }

    private static List<Node> searchNodes(final Collection<Node> nodes, final SearchQuery query) {
        final Predicate<Node> tagFilter = node -> filterByTags(node, query.tags(), query.allTagsMustMatch());
        if (query.searchTerm() == null) {
            assert query.portType() == null;
            // Case 1: no filter, no ranking
            if (query.tags() == null || query.tags().isEmpty()) {
                return List.copyOf(nodes);
            }
            // Case 2: filter only by tags, rank nodes
            return nodes.stream() //
                .filter(tagFilter)//
                .sorted(//
                    Comparator.<Node> comparingInt(n -> -n.weight)//
                        .thenComparing(n -> n.name, ALPHANUMERIC_COMPARATOR))//
                .toList();
        }
        // Case 3: filter by tags, rank by similarity to search term
        return nodes.stream() //
            .filter(tagFilter)//
            .map(n -> new FoundNode(n, //
                StringUtils.containsIgnoreCase(n.name, query.searchTerm()), //
                score(n.getFuzzySearchable(), query.searchTerm()))) //
            .filter(n -> n.isSubstringMatch || n.score >= SIMILARITY_THRESHOLD)//
            .filter(n -> filterByCompatiblePort(n, query))
            .sorted(//
                // 1) exact substring matches (only based on names)
                Comparator.<FoundNode> comparingInt(n -> n.isSubstringMatch ? 0 : 1)//
                    // 2) then fuzzy matches (also based on "hidden" keywords)
                    .thenComparingDouble(n -> -n.score)//
                    // 3) then on manually defined weight
                    .thenComparingInt(n -> -n.node.weight)//
                    // 4) tie-breaks
                    .thenComparing(n -> n.node.name, ALPHANUMERIC_COMPARATOR))//
            .map(wn -> wn.node)//
            .toList();
    }

    private static boolean filterByTags(final Node node, final List<String> tags, final boolean allTagsMatch) {
        if (tags == null || tags.isEmpty()) {
            return true;
        }
        if (allTagsMatch) {
            return new HashSet<>(node.nodeSpec.metadata().tags()).containsAll(tags);
        }
        return tags.stream().anyMatch(node.nodeSpec.metadata().tags()::contains);
    }

    private static boolean filterByCompatiblePort(final FoundNode n, final SearchQuery query) {
        if (query.portType() == null) {
            return true;
        } else if (query.isSearchForSuccesors()) {
            return n.node.isInputCompatibleWith(query.portType());
        } else {
            return n.node.isOutputCompatibleWith(query.portType());
        }
    }


    /**
     * Partition the nodes available in this node repository into a primary and a secondary set to search in. The
     * partitioning is implied by the search query.
     *
     * @param nodeRepo The node repository providing the available nodes.
     * @return A partition according to the search query.
     */
    private static NodeSearch.NodePartition partitionNodesOf(final NodeRepository nodeRepo, final SearchQuery query) {
        return switch (query.nodeFilter()) {
            case HIDDEN -> new NodeSearch.NodePartition(nodeRepo.getHiddenNodes(), null);
            case DEPRECATED -> new NodeSearch.NodePartition(nodeRepo.getDeprecatedNodes(), null);
            case NONE ->
                    new NodeSearch.NodePartition(nodeRepo.getNodes(), nodeRepo.getFilteredNodes());
        };
    }

    private record FoundNode(Node node, boolean isSubstringMatch, double score) {
    }

    /**
     *
     * @param primary
     * @param secondary
     */
    static record NodePartition(Collection<Node> primary, Collection<Node> secondary) {

    }

    /**
     * @param foundNodes Nodes found in the primary set of a {@link NodePartition}
     * @param numSecondaryNodesFound Number of nodes found in the secondary set of a {@link NodePartition}
     */
    private record SearchResult(List<Node> foundNodes, Integer numSecondaryNodesFound) {
    }

    /**
     * For testing purposes only!
     *
     * @return the size of the 'found nodes' cache
     */
    int cacheSize() {
        return m_nodeSearchResultCache.size();
    }

}
