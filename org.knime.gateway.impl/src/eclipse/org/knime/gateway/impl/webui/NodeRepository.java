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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSetFactory;
import org.knime.core.node.extension.CategoryExtension;
import org.knime.core.node.extension.CategoryExtensionManager;
import org.knime.core.node.extension.InvalidNodeFactoryExtensionException;
import org.knime.core.node.extension.NodeFactoryExtension;
import org.knime.core.node.extension.NodeFactoryExtensionManager;
import org.knime.core.node.extension.NodeSetFactoryExtension;
import org.knime.gateway.api.webui.entity.NodeTemplateEnt;
import org.knime.gateway.api.webui.util.EntityBuilderUtil;
import org.knime.gateway.impl.webui.service.DefaultNodeRepositoryService;

/**
 * Node repository logic and state.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public final class NodeRepository {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(NodeRepository.class);

    private static final String NODE_USAGE_FILE = "/files/node_usage/node_usage.csv";

    private static final Pattern MULTIPLE_SLASHES = Pattern.compile("/{2,}");

    /*
     * All available nodes. Loaded/filled once with the call of any of the non-private methods.
     */
    private Map<String, Node> m_nodes;

    private Map<String, Node> m_hiddenNodes;

    private Map<String, Node> m_deprecatedNodes;

    /**
     * Compiles the map of {@link NodeTemplateEnt} with all properties set (including the node/component icon etc.) for
     * the given template ids. Template ids that aren't found are omitted.
     *
     * @param templateIds the ids to create the entities for
     * @return a new map instance containing newly created entity instances
     */
    public Map<String, NodeTemplateEnt> getNodeTemplates(final List<String> templateIds) {
        loadAllNodesAndNodeSets();
        // note: we could cache the already created node template entities here (which also contain, e.g., the icon)
        // but we expect the frontend to do it already
        return templateIds.stream().map(this::getNode)//
            .filter(Objects::nonNull)//
            .map(n -> EntityBuilderUtil.buildNodeTemplateEnt(n.factory))//
            .collect(Collectors.toMap(NodeTemplateEnt::getId, t -> t));
    }

    private Node getNode(final String templateId) {
        Node n = m_nodes.get(templateId);
        if (n != null) {
            return n;
        }
        n = m_hiddenNodes != null ? m_hiddenNodes.get(templateId) : null;
        if (n != null) {
            return n;
        }
        return m_deprecatedNodes != null ? m_deprecatedNodes.get(templateId) : null;
    }

    /**
     * @return all nodes available in the node repository
     */
    Collection<Node> getNodes() {
        loadAllNodesAndNodeSets();
        return m_nodes.values();
    }

    /**
     * @return all hidden nodes available in the node repository
     */
    synchronized Collection<Node> getHiddenNodes() {
        if (m_hiddenNodes == null) {
            Map<String, CategoryExtension> categories = CategoryExtensionManager.getInstance().getCategoryExtensions();
            m_hiddenNodes = new HashMap<>();
            loadNodes(categories, m_hiddenNodes, ext -> !ext.isHidden());
            loadNodeSets(categories, m_hiddenNodes, ext -> false, set -> !set.isHidden(), fac -> false);
        }
        return m_hiddenNodes.values();
    }

    /**
     * @return all deprecated nodes available in the node repository
     */
    synchronized Collection<Node> getDeprecatedNodes() {
        if (m_deprecatedNodes == null) {
            Map<String, CategoryExtension> categories = CategoryExtensionManager.getInstance().getCategoryExtensions();
            m_deprecatedNodes = new HashMap<>();
            loadNodes(categories, m_deprecatedNodes, ext -> !ext.isDeprecated());
            loadNodeSets(categories, m_deprecatedNodes, ext -> !ext.isDeprecated(), set -> false,
                fac -> !fac.isDeprecated());
            addNodeWeights(m_deprecatedNodes);
        }
        return m_deprecatedNodes.values();
    }

    private synchronized void loadAllNodesAndNodeSets() {
        if (m_nodes == null) {
            Map<String, CategoryExtension> categories = CategoryExtensionManager.getInstance().getCategoryExtensions();
            m_nodes = new HashMap<>();
            loadNodes(categories, m_nodes, ext -> ext.isDeprecated() || ext.isHidden());
            loadNodeSets(categories, m_nodes, NodeSetFactoryExtension::isDeprecated, NodeSetFactory::isHidden,
                NodeFactory::isDeprecated);
            addNodeWeights(m_nodes);
        }
    }

    private static void loadNodes(final Map<String, CategoryExtension> categories, final Map<String, Node> nodes,
        final Predicate<NodeFactoryExtension> exclude) {
        for (NodeFactoryExtension ext : NodeFactoryExtensionManager.getInstance().getNodeFactoryExtensions()) {
            if (!exclude.test(ext)) {
                NodeFactory<? extends NodeModel> factory;
                try {
                    factory = ext.getFactory();
                } catch (InvalidNodeFactoryExtensionException ex) {
                    LOGGER.error(ex.getMessage(), ex);
                    continue;
                }
                Node n = new Node();
                n.templateId = EntityBuilderUtil.createTemplateId(factory);
                n.factory = factory;
                n.name = factory.getNodeName();
                n.path = normalizeCategoryPath(ext.getCategoryPath());
                n.tags = getTagsFromCategoryPath(n.path, categories, n.name);
                nodes.put(n.templateId, n);
            }
        }
    }

    private static void loadNodeSets(final Map<String, CategoryExtension> categories, final Map<String, Node> nodes,
        final Predicate<NodeSetFactoryExtension> excludeExt, final Predicate<NodeSetFactory> excludeSetFactory,
        final Predicate<NodeFactory<? extends NodeModel>> excludeFactory) {
        for (NodeSetFactoryExtension ext : NodeFactoryExtensionManager.getInstance().getNodeSetFactoryExtensions()) {
            NodeSetFactory set = ext.getNodeSetFactory();
            if (excludeExt.test(ext) || excludeSetFactory.test(set)) {
                continue;
            }
            for (String factoryId : set.getNodeFactoryIds()) {
                NodeFactory<? extends NodeModel> factory = ext.getNodeFactory(factoryId).orElse(null);
                if (factory == null || excludeFactory.test(factory)) {
                    continue;
                }
                Node n = new Node();
                n.templateId = EntityBuilderUtil.createTemplateId(factory);
                n.factory = factory;
                n.name = factory.getNodeName();
                n.path = normalizeCategoryPath(ext.getCategoryPath(factoryId));
                n.tags = getTagsFromCategoryPath(n.path, categories, n.name);
                nodes.put(n.templateId, n);
            }
        }
    }

    private static Set<String> getTagsFromCategoryPath(final String catPath,
        final Map<String, CategoryExtension> cats, final String nodeName) {
        String path = catPath;
        Set<String> tags = new HashSet<>();
        while (!path.isEmpty() && !path.equals("/")) {
            CategoryExtension cat = cats.get(path);
            if (cat != null) {
                tags.add(cat.getName());
            } else {
                LOGGER.warn(
                    "No category registered for path '" + path + "'. Ignored as tag for node '" + nodeName + "'.");
            }
            path = path.substring(0, path.lastIndexOf('/'));
        }
        return tags;
    }

    private static void addNodeWeights(final Map<String, Node> nodes) {
        Map<Integer, Node> tmpMap = nodes.values().stream()
            .collect(Collectors.toMap(n -> (n.factory.getClass().getName() + "#" + n.name).hashCode(), n -> n));
        try (Stream<int[]> lines = readNodeUsageFile()) {
            lines.forEach(l -> {
                Node n = tmpMap.get(l[0]);
                if (n != null) {
                    n.weight = l[1];
                }
            });
        }
    }

    @SuppressWarnings("java:S1943")
    private static Stream<int[]> readNodeUsageFile() {
        BufferedReader br = new BufferedReader(
            new InputStreamReader(DefaultNodeRepositoryService.class.getResourceAsStream(NODE_USAGE_FILE)));
        return br.lines().map(s -> s.split(",")).map(ar -> {
            int[] res = new int[ar.length];
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

    /**
     * Makes sure a category path is always '/this/is/a/path' (i.e. with a leading '/', without a trailing '/' and
     * without double slashes).
     *
     * @param path the path to normalize
     */
    private static String normalizeCategoryPath(final String path) {
        String newPath = MULTIPLE_SLASHES.matcher(path).replaceAll("/");
        if (!path.startsWith("/")) {
            newPath = "/" + path; // NOSONAR
        }
        if (newPath.endsWith("/")) {
            newPath = newPath.substring(0, newPath.length() - 1);
        }
        return newPath;
    }

    /**
     * Helper data structure which represents a node (or component) in the node repository.
     */
    @SuppressWarnings("java:S116")
    static class Node {

        /**
         * The node name.
         */
        String name;

        /**
         * The tags associated with this node (for nodes these are all the categories, i.e. their names, along the
         * hierarchy).
         */
        Set<String> tags;

        /**
         * The node's id.
         */
        String templateId;

        /**
         * The actual category path of the node (as defined by the node extension point).
         */
        String path;

        /**
         * The node's factory instance.
         */
        NodeFactory<? extends NodeModel> factory;

        /**
         * A weight used for sorting nodes if no other sort criteria is available (such as the search score). The weight
         * is, e.g., the node's popularity among the users.
         */
        int weight;

    }

}