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
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.ToDoubleBiFunction;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.LRUMap;
import org.apache.commons.lang3.StringUtils;
import org.knime.core.data.sort.AlphanumericComparator;
import org.knime.core.node.port.PortType;
import org.knime.core.node.util.CheckUtils;
import org.knime.core.ui.util.FuzzySearchable;
import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.api.webui.entity.NodeSearchResultEnt;
import org.knime.gateway.api.webui.entity.NodeSearchResultEnt.NodeSearchResultEntBuilder;
import org.knime.gateway.api.webui.entity.NodeTemplateEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.InvalidRequestException;
import org.knime.gateway.impl.webui.NodeRepository.Node;

/**
 * Logic and state (e.g. caching) required to search for nodes in the {@link NodeRepository}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @author Kai Franze, KNIME GmbH
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

    private static final ToDoubleBiFunction<FuzzySearchable, String> SCORING_FN =
            // keywords contribute slightly lower similarity values since their influence on the search result is
            // less obvious to the user
            (n, q) -> Math.max(n.computeNameSimilarity(q), KEYWORD_SCORE_WEIGHT * n.computeKeywordSimilarity(q));

    /**
     * Which subset of nodes to search in
     */
    enum NodePartition { // Package scope for testing
            IN_COLLECTION, NOT_IN_COLLECTION, ALL
    }

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
     * @param portTypeId The port type all returned nodes (and components) have to be compatible with.
     * @param q the search query or a blank string
     * @param tags only the nodes which have at least one of the tags are considered in the search result
     * @param allTagsMatch if <code>true</code>, only the nodes/components that have all of the given tags are included
     *            in the search result. Otherwise nodes/components that have at least one of the given tags are
     *            included.
     * @param offset the number of nodes to skip (in the list of found nodes, which have a fixed order) - for
     *            pagination
     * @param limit the maximum number of nodes to include in the search result (mainly for pagination)
     * @param fullTemplateInfo Whether to include the full node template information or not.
     * @param nodesPartition If 'IN_COLLECTION' then only nodes that are part of the collection are returned. If
     *            'NOT_IN_COLLECTION' then only nodes that are not part of the active collection are returned. If 'ALL'
     *            then all nodes (ignoring collections) are returned. Defaults to 'ALL'.
     *
     * @return the search result entity
     * @throws InvalidRequestException
     */
    public NodeSearchResultEnt searchNodes(final String q, final List<String> tags, final Boolean allTagsMatch,
        final Integer offset, final Integer limit, final Boolean fullTemplateInfo, final String nodesPartition,
        final String portTypeId) throws InvalidRequestException {
        var partition = verifyNodePartition(nodesPartition);
        var portType = verifyPortTypeId(portTypeId);
        List<String> tagList = tags == null ? Collections.emptyList() : tags;

        var fn = Normalizer.identity();
        final Collection<Node> allNodes;
        boolean hasMoreNodes = false;
        if (q != null && q.endsWith("//hidden")) {
            fn = t -> t.replace("//hidden", "");
            allNodes = m_nodeRepo.getHiddenNodes();
        } else if (q != null && q.endsWith("//deprecated")) {
            fn = t -> t.replace("//deprecated", "");
            allNodes = m_nodeRepo.getDeprecatedNodes();
        } else if (partition == NodePartition.IN_COLLECTION) {
            // Only consider the nodes that are part of the collection
            allNodes = m_nodeRepo.getNodes();
            hasMoreNodes = true;
        } else if (partition == NodePartition.NOT_IN_COLLECTION) {
            // Only consider the nodes that are NOT part of the collection
            allNodes = m_nodeRepo.getAdditionalNodes();
            hasMoreNodes = true;
        } else {
            // Consider all nodes regardless their collection membership
            allNodes = CollectionUtils.union(m_nodeRepo.getNodes(), m_nodeRepo.getAdditionalNodes());
        }

        final var searchQuery = new SearchQuery(q, tagList, Boolean.TRUE.equals(allTagsMatch), partition, portType);


        final var normalizer = Normalizer.DEFAULT_NORMALIZATION.compose(fn);
        final var foundNodes =
            m_foundNodesCache.computeIfAbsent(searchQuery, query -> searchNodes(allNodes, query, normalizer));

        // map templates
        List<NodeTemplateEnt> templates = foundNodes.stream()
            .map(n -> m_nodeRepo.getNodeTemplate(n.templateId, Boolean.TRUE.equals(fullTemplateInfo)))//
            .filter(Objects::nonNull)//
            .skip(offset == null ? 0 : offset)//
            .limit(limit == null ? Long.MAX_VALUE : limit)//
            .collect(Collectors.toList());

        // collect all tags from the templates and sort according to their frequency
        Map<String, Long> tagFrequencies = foundNodes.stream().flatMap(n -> n.nodeSpec.metadata().tags().stream())
            .collect(Collectors.groupingBy(t -> t, HashMap::new, Collectors.counting()));
        List<String> resTags = tagFrequencies.entrySet().stream()//
            .sorted(Comparator.<Entry<String, Long>, Long> comparing(Entry::getValue).reversed())//
            .map(Entry::getKey)//
            .collect(Collectors.toList());

        Integer numFilteredNodesFound = null;
        if (foundNodes.isEmpty() && hasMoreNodes) {
            var complementSetNodes =
                partition == NodePartition.IN_COLLECTION ? m_nodeRepo.getAdditionalNodes() : m_nodeRepo.getNodes();
            numFilteredNodesFound = searchNodes(complementSetNodes, searchQuery, normalizer).size();
        }

        return builder(NodeSearchResultEntBuilder.class)//
            .setNodes(templates)//
            .setTags(resTags)//
            .setTotalNumNodesFound(foundNodes.size())//
            .setTotalNumFilteredNodesFound(numFilteredNodesFound)//
            .build();
    }

    private static List<Node> searchNodes(final Collection<Node> nodes, final SearchQuery searchQuery,
        final Normalizer normalizer) {
        final var tags = searchQuery.tags();
        final var allTagsMatch = searchQuery.allTagsMatch();
        final Predicate<Node> tagFilter = n -> filterByTags(n, tags, allTagsMatch);
        final var normalizedSearchTerm = normalizer.normalizeSearchTerm(searchQuery.searchTerm());
        if (normalizedSearchTerm == null) {
            assert searchQuery.portType() == null;

            // Case 1: no filter, no ranking
            if (tags == null || tags.isEmpty()) {
                return Collections.unmodifiableList(new ArrayList<>(nodes));
            }
            // Case 2: filter only by tags, rank nodes
            return nodes.stream().filter(tagFilter)//
                .sorted(//
                    Comparator.<Node> comparingInt(n -> -n.weight)//
                        .thenComparing(n -> n.name, ALPHANUMERIC_COMPARATOR))//
                .collect(Collectors.toList());
        }
        // Case 3: filter by tags, rank by similarity to search term
        var portType = searchQuery.portType();
        return nodes.stream().filter(tagFilter)//
            .map(n -> new FoundNode(n, //
                StringUtils.containsIgnoreCase(n.name, normalizedSearchTerm), //
                SCORING_FN.applyAsDouble(n.getFuzzySearchable(), normalizedSearchTerm)))//
            .filter(n -> n.m_substringMatch || n.m_score >= SIMILARITY_THRESHOLD)//
            .filter(n -> portType == null || n.m_node.isCompatibleWith(portType))//
            .sorted(//
                // 1) exact substring matches (only based on names)
                Comparator.<FoundNode> comparingInt(n -> n.m_substringMatch ? 0 : 1)//
                    // 2) then fuzzy matches (also based on "hidden" keywords)
                    .thenComparingDouble(n -> -n.m_score)//
                    // 3) then on manually defined weight
                    .thenComparingInt(n -> -n.m_node.weight)//
                    // 4) tie-breaks
                    .thenComparing(n -> n.m_node.name, ALPHANUMERIC_COMPARATOR))//
            .map(wn -> wn.m_node)//
            .collect(Collectors.toList());
    }

    private static boolean filterByTags(final Node n, final List<String> tags, final boolean allTagsMatch) {
        if (tags == null || tags.isEmpty()) {
            return true;
        }
        if (allTagsMatch) {
            return tags.stream().allMatch(n.nodeSpec.metadata().tags()::contains);
        }
        return tags.stream().anyMatch(n.nodeSpec.metadata().tags()::contains);
    }

    private static NodePartition verifyNodePartition(final String nodePartition) throws InvalidRequestException {
        if (StringUtils.isBlank(nodePartition)) {
            return NodePartition.ALL;
        }
        try {
            return NodePartition.valueOf(nodePartition);
        } catch (IllegalArgumentException e) {
            throw new InvalidRequestException(String.format("<%s> is not a valid node partition", nodePartition), e);
        }
    }

    private static PortType verifyPortTypeId(final String portTypeId) throws InvalidRequestException {
        if (StringUtils.isBlank(portTypeId)) {
            return null;
        }
        return CoreUtil.getPortType(portTypeId)
            .orElseThrow(() -> new InvalidRequestException(String.format("Not port type found for <%s>", portTypeId)));
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

        static final Normalizer DEFAULT_NORMALIZATION = t -> t.strip().toUpperCase(Locale.ROOT);

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

        /**
         * @param searchTerm The original search term
         *
         * @return The normalized search term, can be {@code null}.
         */
        default String normalizeSearchTerm(final String searchTerm) {
            final String normalizedForm = searchTerm == null ? searchTerm : apply(searchTerm);
            if (StringUtils.isEmpty(normalizedForm)) {
                return null;
            }
            return normalizedForm;
        }
    }

    /**
     * Simple search query record for bookkeeping
     */
    private static record SearchQuery(String searchTerm, List<String> tags, boolean allTagsMatch, // NOSONAR: Parameters not used is fine
        NodePartition nodePartition, PortType portType) {
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
