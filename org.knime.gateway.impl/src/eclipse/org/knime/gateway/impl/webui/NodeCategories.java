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
 */
package org.knime.gateway.impl.webui;

import static org.knime.gateway.api.entity.EntityBuilderManager.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.knime.core.node.NodeLogger;
import org.knime.core.node.extension.CategoryExtension;
import org.knime.core.node.extension.NodeAndCategorySorter;
import org.knime.core.node.extension.NodeSpecCollectionProvider;
import org.knime.core.node.util.CheckUtils;
import org.knime.gateway.api.webui.entity.CategoryMetadataEnt;
import org.knime.gateway.api.webui.entity.NodeCategoryEnt;
import org.knime.gateway.impl.service.util.Lazy;
import org.knime.gateway.impl.webui.NodeRepository.Node;

/**
 * The hierarchy of node categories
 */
public final class NodeCategories {

    static final String UNCATEGORIZED_KEY = "/uncategorized";

    /*
     * ('top-level') tag for nodes that are at root-level, that are without a category, or
     * that reference a (first-level) category that is not registered (via the category-extension point).
     * Note that this is a real category like any other.
     */
    static final String UNCATEGORIZED_NAME = "Uncategorized";

    /**
     * tree with null-ish root
     */
    private final Lazy.Init<CategoryTree> m_tree;

    private final NodeRepository m_nodeRepo;

    /**
     * Build the node category hierarchy based on the given nodes
     * 
     * @param nodeRepository The nodes that will span this category hierarchy
     */
    public NodeCategories(final NodeRepository nodeRepository) {
        CheckUtils.checkArgumentNotNull(nodeRepository);
        m_nodeRepo = nodeRepository;
        m_tree = new Lazy.Init<>(() -> new CategoryTree(nodeRepository.getNodes()));
        nodeRepository.onContentChange(m_tree::clear);
    }

    @SuppressWarnings("java:S3398")
    private static Optional<CategoryMetadata> getCategoryMetadata(final List<CategoryId> path) {
        var categoryExtensions = NodeSpecCollectionProvider.getInstance().getCategoryExtensions();
        var extensionAtPath = categoryExtensions.get(CategoryId.categoryPathToString(path));
        return Optional.ofNullable(extensionAtPath).map(CategoryMetadata::new);
    }

    /**
     * Type to distinguish category IDs from other Strings.
     *
     * @param value Also known as the "level ID"
     */
    private record CategoryId(String value) implements Comparable<CategoryId> {

        static CategoryId of(final String string) {
            return toCategoryPath(string).get(0);
        }

        static List<CategoryId> toCategoryPath(final String pathString) {
            return Arrays.stream(pathString.split("/")) //
                .filter(segment -> !segment.isEmpty()) //
                .map(CategoryId::new).toList();
        }

        static String categoryPathToString(final List<CategoryId> path) {
            return "/" + String.join("/", path.stream().map(CategoryId::value).toList());
        }

        @Override
        public String toString() {
            return value;
        }

        @Override
        public int compareTo(final CategoryId other) {
            return this.value().compareTo(other.value());
        }
    }

    /**
     * Get the category at the given path.
     *
     * @param path
     * @throws NoSuchElementException if no category is found at this path
     * @return the category at this path
     */
    public NodeCategoryEnt getCategory(final List<String> path) throws NoSuchElementException {
        var treeNode = m_tree //
            .initialised() // lazily build category hierarchy tree on first access
            .get(path.stream().map(CategoryId::new).toList()) //
            .orElseThrow();
        var children = treeNode.categories().transformed().values().stream() //
            .map(child -> child.metadata().toEntity()).toList();
        var nodes = m_nodeRepo.mapNodeTemplateEnts(treeNode.nodes().transformed(), true);
        var categoryMetadata = treeNode.metadata();
        return builder(NodeCategoryEnt.NodeCategoryEntBuilder.class) //
            .setMetadata(categoryMetadata != null ? categoryMetadata.toEntity() : null) //
            .setChildCategories(children) //
            .setNodes(nodes) //
            .build();
    }

    private record CategoryMetadata(CategoryExtension extension) implements Comparable<CategoryMetadata> {

        CategoryId id() {
            return CategoryId.of(extension().getLevelId());
        }

        String displayName() {
            return extension().getName();
        }

        CategoryMetadataEnt toEntity() {
            var path = CategoryId.toCategoryPath(extension().getCompletePath()) //
                .stream().map(CategoryId::toString).toList();
            return builder(CategoryMetadataEnt.CategoryMetadataEntBuilder.class) //
                .setPath(path) //
                .setDisplayName(this.displayName()) //
                .build();
        }

        @Override
        public int compareTo(final CategoryMetadata other) {
            return Comparator.comparing(CategoryMetadata::displayName) //
                .thenComparing(CategoryMetadata::id) //
                .compare(this, other);
        }
    }

    /**
     * A node category hierarchy.
     * <p>
     * The tree hierarchically organises values of the type {@link CategoryTreeNode}. Such a tree node corresponds to
     * one node category, constituted of nodes contained in this hierarchy and child categories.
     * <p>
     * 
     * @implNote An instance of this class is intended to be used as if it was fully immutable. It is in fact not
     *           because {@link CategoryTreeNode#m_nodes} and {@link CategoryTreeNode#m_children} have to be mutable
     *           collections for the way the tree is constructed -- see {@link Tree#getOrInsert(Iterable)}.
     */
    private static final class CategoryTree extends Tree<CategoryId, CategoryTreeNode> {

        private static CategoryTreeNode createNode(final List<CategoryId> path) throws NodeCreationException {
            var metadata = getCategoryMetadata(path).orElseThrow(NodeCreationException::new);
            return new CategoryTreeNode(metadata);
        }

        /**
         * Create a category tree that organises the given nodes.
         *
         * @param nodes The nodes that will be leaves of the category tree, inner nodes are categories.
         */
        public CategoryTree(final Collection<Node> nodes) {
            super(new CategoryTreeNode(null), CategoryTree::createNode);
            // Since the tree should contain only the given nodes and the given collection is unordered,
            // insert nodes one-by-one and upsert categories as required.
            nodes.forEach(this::insertNode);
        }

        /**
         * Special category for nodes that could not be assigned a category.
         * 
         * @implNote This is expected to correspond to an actually defined, installed category, see
         *           {@link NodeCategories#UNCATEGORIZED_KEY}.
         */
        Lazy.Init<Optional<CategoryTreeNode>> m_uncategorized = new Lazy.Init<>(() -> {
            CategoryTreeNode uncategorized = null;
            try {
                uncategorized = createNode(List.of(CategoryId.of(UNCATEGORIZED_KEY)));
            } catch (NodeCreationException e) { // NOSONAR
                return Optional.empty();
            }
            root().children().put(uncategorized.metadata().id(), uncategorized);
            return Optional.of(uncategorized);
        });

        /**
         * Find (optionally create) the category tree node this node should go into and insert the given node there.
         * 
         * @param node to insert
         */
        private void insertNode(final Node node) {
            CategoryTreeNode categoryToInsertInto = null;
            try {
                categoryToInsertInto =
                    this.getOrInsert(CategoryId.toCategoryPath(node.nodeSpec.metadata().categoryPath()));
            } catch (NodeCreationException e) { // NOSONAR
                if (m_uncategorized.initialised().isPresent()) {
                    categoryToInsertInto = m_uncategorized.initialised().get();
                } else {
                    NodeLogger.getLogger(this.getClass()).error("Could not find metadata for 'uncategorized' category");
                    return;
                }
            }
            categoryToInsertInto.nodes().original().add(node);
        }

    }

    /**
     * Wrapper to provide accessors for {@link NodeAndCategorySorter}
     */
    private record SortableCategory(
            CategoryTreeNode category) implements NodeAndCategorySorter.NodeOrCategory<SortableCategory> {

        private static LinkedHashMap<CategoryId, CategoryTreeNode>
            sortCategories(final Map<CategoryId, CategoryTreeNode> categories) {
            var sortableCategories = categories.values().stream().map(SortableCategory::new).toList();
            var sortedCategories = NodeAndCategorySorter.sortNodesAndCategories(sortableCategories).stream()
                .map(SortableCategory::category);
            var result = new LinkedHashMap<CategoryId, CategoryTreeNode>();
            sortedCategories.forEach(cat -> result.put(cat.metadata().id(), cat));
            return result;
        }

        @Override
        public String getID() {
            return category().metadata().id().toString();
        }

        @Override
        public String getName() {
            return category().metadata().displayName();
        }

        @Override
        public String getContributingPlugin() {
            return category().metadata().extension().getContributingPlugin();
        }

        @Override
        public boolean isNode() {
            return false;
        }

        @Override
        public String getAfterID() {
            return category().metadata().extension().getAfterID();
        }

        @Override
        public int compareTo(final SortableCategory other) {
            return Comparator.comparing((final SortableCategory o) -> o.category().metadata()).compare(this, other);
        }
    }

    /**
     * Wrapper to provide accessors for {@link NodeAndCategorySorter}.
     */
    private record SortableNode(Node node) implements NodeAndCategorySorter.NodeOrCategory<SortableNode> {

        private static List<Node> sort(final List<Node> nodes) {
            var sortableNodes = nodes.stream().map(SortableNode::new).toList();
            return NodeAndCategorySorter.sortNodesAndCategories(sortableNodes).stream().map(SortableNode::node)
                .toList();
        }

        @Override
        public String getID() {
            return node().nodeSpec.factory().id();
        }

        @Override
        public String getName() {
            return node().name;
        }

        @Override
        public String getContributingPlugin() {
            return node().nodeSpec.metadata().vendor().bundle().getSymbolicName();
        }

        @Override
        public boolean isNode() {
            return true;
        }

        @Override
        public String getAfterID() {
            return node().nodeSpec.metadata().afterID();
        }

        @Override
        public int compareTo(final SortableNode other) {
            return Comparator.comparing(SortableNode::getName) //
                .thenComparing(SortableNode::getID) //
                .compare(this, other);
        }
    }

    private static final class CategoryTreeNode implements Tree.TreeNode<CategoryId, CategoryTreeNode> {
        private final CategoryMetadata m_metadata;

        private final Lazy.Transform<List<Node>> m_nodes;

        private final Lazy.Transform<Map<CategoryId, CategoryTreeNode>> m_children;

        /**
         * @param metadata Metadata for this category
         * @param nodes Nodes directly in this category (i.e., not including nodes in child categories)
         * @param children child categories of this category.
         */
        private CategoryTreeNode( //
            final CategoryMetadata metadata, //
            final List<Node> nodes, //
            final Map<CategoryId, CategoryTreeNode> children //
        ) {
            this.m_metadata = metadata;
            this.m_nodes = new Lazy.Transform<>(nodes, SortableNode::sort);
            this.m_children = new Lazy.Transform<>(children, SortableCategory::sortCategories);
        }

        CategoryTreeNode(final CategoryMetadata metadata) {
            this( //
                metadata, //
                new ArrayList<>(), // needs to be mutable
                new HashMap<>() // needs to be mutable
            );
        }

        public CategoryMetadata metadata() {
            return m_metadata;
        }

        public Lazy.Transform<List<Node>> nodes() {
            return m_nodes;
        }

        @Override
        public Map<CategoryId, CategoryTreeNode> children() {
            return m_children.original();
        }

        public Lazy.Transform<Map<CategoryId, CategoryTreeNode>> categories() {
            return m_children;
        }

    }
}
