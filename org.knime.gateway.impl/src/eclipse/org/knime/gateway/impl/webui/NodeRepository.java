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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.apache.commons.lang3.concurrent.LazyInitializer;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.extension.NodeSpec;
import org.knime.core.node.extension.NodeSpecCollectionProvider;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.flowvariable.FlowVariablePortObject;
import org.knime.core.ui.util.FuzzySearchable;
import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.api.webui.entity.NodeTemplateEnt;
import org.knime.gateway.api.webui.util.EntityFactory;
import org.knime.gateway.impl.GatewayImplPlugin;
import org.knime.gateway.impl.webui.service.DefaultNodeRepositoryService;

/**
 * Node repository logic and state.
 * <p>
 * An instance is stateful in that it caches various sets of nodes.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public final class NodeRepository {

    private static final String NODE_USAGE_FILE = "/files/node_usage/node_usage.csv";

    /**
     * Determines whether a given {@link NodeFactory#getFactoryId() FactoryId} is available through this node
     * repository.
     */
    private Predicate<String> m_filter;

    /**
     * Nodes included by the predicate, or all nodes available in the installation if no predicate is given.
     *
     * @see NodeRepository#m_filter
     */
    private Map<String, Node> m_nodes;

    /**
     * Nodes that are not included by the predicate
     *
     * @see NodeRepository#m_filter
     */
    private Map<String, Node> m_filteredNodes;

    /**
     * Nodes available in the installation that are marked as hidden in their definition.
     */
    private Map<String, Node> m_hiddenNodes;

    /**
     * Nodes available in the installation that are marked as deprecated in their definition.
     */
    private Map<String, Node> m_deprecatedNodes;

    private final Map<String, NodeTemplateEnt> m_fullInfoNodeTemplateEntCache = new ConcurrentHashMap<>(2000);

    /**
     * Does not apply to {@link this#m_hiddenNodes} or {@link this#m_deprecatedNodes}, these are assumed to be static
     * during runtime and not affected by filtering.
     */
    private final Set<Runnable> m_contentChangedListeners = new HashSet<>();

    /**
     * Determine whether a node is forbidden to be used as per {@link org.knime.core.customization.APCustomization}.
     *
     * @param templateId node in question
     * @return true if the node usage is forbidden (regardless of whether it's installed or not)
     * @since 5.3
     */
    public static boolean isNodeUsageForbidden(final String templateId) {
        return !GatewayImplPlugin.getInstance().getCustomization().nodes().isUsageAllowed(templateId);
    }

    /**
     * Create a new node repository. All available nodes are included.
     */
    public NodeRepository() {
        this(null);
    }

    /**
     * Create a new node repository.
     * <p>
     * An instance is optionally based on a predicate on a node. This predicate partitions the set of nodes
     * <i>available</i> in this AP installation into <i>included</i> and <i>not included</i> nodes. The intuition behind
     * this partition is to be able to indicate during node searching that additional nodes may be available in a
     * different usage context.
     * <p>
     *
     * @param filter defines which nodes will be included in this instance. If {@code null}, all nodes are included.
     */
    public NodeRepository(final Predicate<String> filter) {
        m_filter = filter;
    }

    /**
     * Compiles the map of {@link NodeTemplateEnt} with all properties set (including the node/component icon etc.) for
     * the given template ids. Template ids that aren't found are omitted.
     *
     * @param templateIds the ids to create the entities for
     * @param fullTemplateInfo whether to include the full node template information or not
     * @return a new map instance containing newly created entity instances
     */
    public Map<String, NodeTemplateEnt> getNodeTemplates(final Collection<String> templateIds,
        final boolean fullTemplateInfo) {
        loadAllNodesAndNodeSets();
        return templateIds.stream().map(this::getNodeIncludeFilteredNodes)//
            .filter(Objects::nonNull)//
            .map(n -> getNodeTemplate(n, fullTemplateInfo))//
            .filter(Objects::nonNull)//
            .collect(Collectors.toMap(NodeTemplateEnt::getId, t -> t));
    }

    /**
     * Builds the {@link NodeTemplateEnt} with all properties set (including the node/component icon etc.) for the given
     * template id.
     *
     * @param templateId the id to create the entity for
     * @param fullTemplateInfo whether to include the full node template information or not
     * @return the template entity or {@code null} if there is none for the given id
     */
    public NodeTemplateEnt getNodeTemplate(final String templateId, final boolean fullTemplateInfo) {
        return getNodeTemplate(getNodeIncludeFilteredNodes(templateId), fullTemplateInfo);
    }

    /**
     * Resets the 'filter' predicate.
     *
     * @param filter defines which nodes should be included in the node repository by matching the templateId of the
     *            node. Can be <code>null</code>, which means all nodes are included.
     */
    public void resetFilter(final Predicate<String> filter) {
        m_filter = filter;
        m_nodes = null;
        m_filteredNodes = null;
        notifyContentChanged();
    }

    void onContentChange(final Runnable onContentChanged) {
        m_contentChangedListeners.add(onContentChanged);
    }

    private void notifyContentChanged() {
        m_contentChangedListeners.forEach(Runnable::run);
    }

    private NodeTemplateEnt getNodeTemplate(final Node n, final boolean fullTemplateInfo) {
        if (fullTemplateInfo) {
            return m_fullInfoNodeTemplateEntCache.computeIfAbsent(n.templateId,
                k -> EntityFactory.NodeTemplateAndDescription.buildNodeTemplateEnt(n.nodeSpec()));
        } else {
            return EntityFactory.NodeTemplateAndDescription.buildMinimalNodeTemplateEnt(n.nodeSpec());
        }
    }

    /**
     * Find a node by template id if it is part of the active repository.
     *
     * @param templateId
     * @return the node or <code>null<code> if it is not included in this node repository.
     */
    Node getNode(final String templateId) {
        loadAllNodesAndNodeSets();
        return m_nodes.get(templateId);
    }

    /**
     * Find a node by template id
     *
     * @param templateId
     * @return The node
     */
    Node getNodeIncludeFilteredNodes(final String templateId) {
        loadAllNodesAndNodeSets();
        var node = m_nodes.get(templateId);
        if (node != null) {
            return node;
        }
        node = m_filteredNodes != null ? m_filteredNodes.get(templateId) : null;
        if (node != null) {
            return node;
        }
        node = m_hiddenNodes != null ? m_hiddenNodes.get(templateId) : null;
        if (node != null) {
            return node;
        }
        return m_deprecatedNodes != null ? m_deprecatedNodes.get(templateId) : null;
    }

    /**
     * @return all nodes included in this node repository.
     */
    Collection<Node> getNodes() {
        loadAllNodesAndNodeSets();
        return m_nodes.values();
    }

    /**
     * @return all nodes not included in this node repository.
     */
    Collection<Node> getFilteredNodes() {
        loadAllNodesAndNodeSets();
        return m_filteredNodes.values();
    }

    /**
     * @return all hidden nodes included in the node repository
     */
    synchronized Collection<Node> getHiddenNodes() {
        if (m_hiddenNodes == null) {
            m_hiddenNodes = mapFromNodeSpecToNodeAndFilter(NodeSpecCollectionProvider.getInstance().getHiddenNodes());
        }
        return m_hiddenNodes.values();
    }

    /**
     * @return all deprecated nodes included in the node repository
     */
    synchronized Collection<Node> getDeprecatedNodes() {
        if (m_deprecatedNodes == null) {
            m_deprecatedNodes =
                mapFromNodeSpecToNodeAndFilter(NodeSpecCollectionProvider.getInstance().getDeprecatedNodes());
            addNodeWeights(m_deprecatedNodes);
        }
        return m_deprecatedNodes.values();
    }

    private synchronized void loadAllNodesAndNodeSets() {
        if (m_nodes == null) { // Do not run this if nodes have already been fetched
            // Read in all node templates available
            var activeNodes = mapFromNodeSpecToNodeAndFilter(NodeSpecCollectionProvider.getInstance().getActiveNodes());
            addNodeWeights(activeNodes);

            if (m_filter == null) {
                m_nodes = activeNodes;
                m_filteredNodes = Collections.emptyMap();
            } else {
                m_nodes = filterNodes(activeNodes, m_filter);
                m_filteredNodes = filterNodes(activeNodes, m_filter.negate());
            }
        }
    }

    private static Map<String, Node> mapFromNodeSpecToNodeAndFilter(final Map<String, NodeSpec> nodes) {
        final var nodesCustomization = GatewayImplPlugin.getInstance().getCustomization().nodes();
        return nodes.values().stream() //
            .filter(ns -> nodesCustomization.isViewAllowed(ns.factory().id())) //
            .collect(Collectors.toMap(nodeSpec -> nodeSpec.factory().id(), Node::new));
    }

    private static Map<String, Node> filterNodes(final Map<String, Node> nodes, final Predicate<String> filter) {
        return nodes.entrySet().stream() //
            .filter(e -> filter.test(e.getValue().templateId)) //
            .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }

    private static void addNodeWeights(final Map<String, Node> nodes) {
        Map<Integer, Node> templateIdsToNodes = nodes.values().stream() //
            .collect(Collectors.toMap(n -> (n.templateId).hashCode(), n -> n));
        try (Stream<int[]> lines = readNodeUsageFile()) {
            lines.forEach(line -> {
                var node = templateIdsToNodes.get(line[0]);
                if (node != null) {
                    node.weight = line[1];
                }
            });
        }
    }

    @SuppressWarnings("java:S1943")
    private static Stream<int[]> readNodeUsageFile() {
        var br = new BufferedReader(
            new InputStreamReader(DefaultNodeRepositoryService.class.getResourceAsStream(NODE_USAGE_FILE)));
        return br.lines().map(s -> s.split(",")).map(ar -> {
            var res = new int[ar.length];
            res[0] = Integer.valueOf(ar[0]);
            res[1] = Integer.valueOf(ar[1]);
            return res;
        }).onClose(() -> {
            try {
                br.close();
            } catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
        });
    }

    public List<NodeTemplateEnt> mapNodeTemplateEnts(final Collection<Node> nodes,
        final Boolean includeFullTemplateInfo) {
        return nodes.stream().map(n -> getNodeTemplate(n.templateId, Boolean.TRUE.equals(includeFullTemplateInfo)))//
            .filter(Objects::nonNull)//
            .toList();
    }

    /**
     * Helper data structure which represents a node (or component) in the node repository.
     */
    @SuppressWarnings("java:S116")
    public static final class Node {

        private final LazyInitializer<FuzzySearchable> m_fuzzySearchableInitializer = new LazyInitializer<>() {
            @Override
            protected FuzzySearchable initialize() throws ConcurrentException {
                return new FuzzySearchable(name, m_nodeSpec.metadata().keywords().toArray(String[]::new));
            }
        };

        final String templateId;

        final String name;

        // TODO adjust other fields too
        private final NodeSpec m_nodeSpec;

        /**
         * A weight used for sorting nodes if no other sort criteria is available (such as the search score). The weight
         * is, e.g., the node's popularity among the users.
         */
        int weight;

        Node(final NodeSpec spec) {
            templateId = spec.factory().id();
            name = spec.metadata().nodeName();
            m_nodeSpec = spec;
        }

        NodeSpec nodeSpec() {
            return m_nodeSpec;
        }

        FuzzySearchable getFuzzySearchable() {
            try {
                return m_fuzzySearchableInitializer.get();
            } catch (ConcurrentException ex) {
                throw new IllegalStateException(ex);
            }
        }

        /**
         * Checks for compatible port types in the input side, considering existing ports and ports that can be added
         * on.
         *
         * @return True if there exists a compatible port type, false otherwise.
         */
        boolean isInputCompatibleWith(final PortType portType) {
            return FlowVariablePortObject.TYPE.equals(portType) || m_nodeSpec.ports().getSupportedInputPortTypes() //
                .anyMatch(pt -> CoreUtil.arePortTypesCompatible(portType, pt));
        }

        /**
         * Checks for compatible port types in the output side, considering existing ports and ports that can be added
         * on.
         *
         * @return True if there exists a compatible port type, false otherwise.
         */
        boolean isOutputCompatibleWith(final PortType portType) {
            return FlowVariablePortObject.TYPE.equals(portType) || m_nodeSpec.ports().getSupportedOutputPortTypes() //
                .anyMatch(pt -> CoreUtil.arePortTypesCompatible(portType, pt));
        }
    }
}
