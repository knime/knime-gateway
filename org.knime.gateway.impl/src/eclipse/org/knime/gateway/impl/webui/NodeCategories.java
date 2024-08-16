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
import java.util.Objects;
import java.util.Optional;

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

    private static Optional<CategoryExtension> getCategoryExtension(List<CategoryId> path) {
        var categoryExtensions = NodeSpecCollectionProvider.getInstance().getCategoryExtensions();
        return Optional.ofNullable(categoryExtensions.get(CategoryId.categoryPathToString(path)));
    }

    private static boolean allowsChild(String parentPluginId, String childPluginId) {
        return childPluginId.equals(parentPluginId) //
            || childPluginId.equals("org.knime.") // child is contributed by KNIME
            || childPluginId.equals("com.knime.") // child is contributed by KNIME
            || vendorEqual(parentPluginId, childPluginId);
    }

    private static boolean vendorEqual(String plugInId, String otherPlugInId) {
        if (plugInId == null || otherPlugInId == null) {
            return false;
        }
        int secondDotIndex = plugInId.indexOf('.', plugInId.indexOf('.') + 1);
        if (secondDotIndex == -1) {
            secondDotIndex = 0;
        }
        return plugInId.regionMatches(0, otherPlugInId, 0, secondDotIndex);
    }

    /**
     * Type to distinguish category IDs from other Strings.
     *
     * @param value Also known as the "level ID"
     */
    private record CategoryId(String value) implements Comparable<CategoryId> {

        /**
         * Parse a single category ID out of the given string.
         * 
         * @param string Assumed to be a valid id, method throws otherwise.
         * @return The category ID
         * @throws NoSuchElementException if not parseable.
         */
        static CategoryId of(final String string) throws NoSuchElementException {
            return toCategoryPath(string).orElseThrow().get(0);
        }

        /**
         * Parse a single category ID out of the given string.
         * 
         * @param string
         * @return The category ID, or an empty Optional if not parseable.
         */
        static Optional<CategoryId> optionalOf(final String string) {
            return toCategoryPath(string).map(path -> path.get(0));
        }

        /**
         * Parse a category path from the given string.
         * 
         * @param string
         * @return Empty optional if {@code pathString} is null, empty or blank. Optional of empty list if the assigned
         *         category is the root.
         */
        static Optional<List<CategoryId>> toCategoryPath(final String string) {
            if (string == null || string.isBlank()) {
                return Optional.empty();
            }
            return Optional.of(Arrays.stream(string.split("/")) //
                .filter(segment -> !segment.isEmpty()) //
                .map(CategoryId::new).toList());
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
    @SuppressWarnings("java:S3242")
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

    private record CategoryMetadata(CategoryId id, String displayName, List<CategoryId> path,
            Optional<CategoryId> afterId, Optional<String> contributingPlugin) implements Comparable<CategoryMetadata> {

        /**
         * Infer category metadata from the path and a node in the subtree rooted at the end of this path.
         */
        static Optional<CategoryMetadata> fromInsertionContext(CategoryTree.CategoryInsertionContext context) {
            return Optional.of(new CategoryMetadata( //
                context.categoryIdentifier(), //
                context.categoryIdentifier().toString(), //
                context.path(), //
                Optional.empty(), //
                Optional.ofNullable(context.contributingPlugin()) //
            ));
        }

        /**
         * Fetch metadata from pre-defined category extensions
         */
        static CategoryMetadata fromCategoryExtension(CategoryExtension extension) {
            return new CategoryMetadata( //
                CategoryId.of(extension.getLevelId()), //
                extension.getName(), //
                CategoryId.toCategoryPath(extension.getCompletePath()).orElseThrow(),
                CategoryId.optionalOf(extension.getAfterID()), //
                Optional.ofNullable(extension.getContributingPlugin()) //
            );
        }

        static CategoryMetadata uncategorized() {
            return getCategoryExtension(List.of(CategoryId.of(UNCATEGORIZED_KEY))) //
                .map(CategoryMetadata::fromCategoryExtension) //
                .orElse(new CategoryMetadata( //
                    CategoryId.of(UNCATEGORIZED_KEY), //
                    UNCATEGORIZED_NAME, //
                    List.of(), //
                    Optional.empty(), //
                    Optional.empty() //
                ));
        }

        CategoryMetadataEnt toEntity() {
            return builder(CategoryMetadataEnt.CategoryMetadataEntBuilder.class) //
                .setPath(path().stream().map(CategoryId::toString).toList()) //
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
     *           collections for the way the tree is constructed -- see
     *           {@link Tree#getOrGrowBranchAlong(List, TreeNodeCreator)}.
     */
    private static final class CategoryTree extends Tree<CategoryId, CategoryTreeNode> {

        /**
         * Create a category tree that organises the given nodes.
         *
         * @param nodes The nodes that will be leaves of the category tree, inner nodes are categories.
         */
        public CategoryTree(final Collection<Node> nodes) {
            super(new CategoryTreeNode(null));
            // Since the tree should contain only the given nodes and the given collection is unordered,
            // iterate over the given nodes and create category tree nodes as needed.
            nodes.forEach(this::insertNode);
        }

        /**
         * Special category for nodes that could not be assigned a category.
         *
         * @implNote This is expected to correspond to an actually defined, installed category, see
         *           {@link NodeCategories#UNCATEGORIZED_KEY}.
         */

        Lazy.Init<CategoryTreeNode> m_uncategorized = new Lazy.Init<>(() -> {
            var treeNode = new CategoryTreeNode(CategoryMetadata.uncategorized());
            root().children().put(treeNode.metadata().id(), treeNode);
            return treeNode;
        });

        /**
         * Context under which the current insertion operation is performed. This is required in some cases for
         * computing a value to insert.
         */
        record CategoryInsertionContext(TreeInsertionContext<CategoryId, CategoryTreeNode> treeInsertionContext,
                Node node) {

            List<CategoryId> path() {
                return treeInsertionContext().path();
            }

            Optional<CategoryTreeNode> parent() {
                return treeInsertionContext().parent();
            }

            boolean parentLocked() {
                return parent().map(CategoryTreeNode::locked).orElse(false);
            }

            boolean allowsChild() {
                return parent().flatMap(p -> p.metadata().contributingPlugin()) //
                    .map(parentPlugin -> NodeCategories.allowsChild(parentPlugin, contributingPlugin())) //
                    .orElse(true);
            }

            CategoryId categoryIdentifier() {
                var path = treeInsertionContext().path();
                return path.get(path.size() - 1);
            }

            String contributingPlugin() {
                return this.node().nodeSpec.metadata().vendor().bundle().getSymbolicName();
            }
        }

        private static CategoryTreeNode createTreeNode(CategoryInsertionContext context)
            throws TreeNodeCreationException {
            var canInsert = context.allowsChild() // cheaper criteria first
                // in legacy code the below criterion is only applied to node sets
                || !Objects.equals(context.contributingPlugin(), "org.knime.python3.nodes") || !context.parentLocked();
            if (!canInsert) {
                throw new TreeNodeCreationException();
            }
            var metadata = getCategoryExtension(context.path()).map(CategoryMetadata::fromCategoryExtension) //
                .or(() -> CategoryMetadata.fromInsertionContext(context)) //
                .orElseThrow(TreeNodeCreationException::new);
            return new CategoryTreeNode(metadata);
        }

        /**
         * Find (optionally create) the category tree node this node should go into and insert the given node there.
         *
         * @param node to insert
         */
        private void insertNode(final Node node) {
            var nodeCategoryPath = CategoryId.toCategoryPath(node.nodeSpec.metadata().categoryPath());
            findCategoryToInsertInto(nodeCategoryPath, node) //
                .nodes().original() //
                .add(node); //
        }

        @SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "java:S3553"})
        private CategoryTreeNode findCategoryToInsertInto(Optional<List<CategoryId>> nodeCategoryPath, Node node) {
            // Providing an empty string or omitting the category-path attribute in the plugin.xml
            // file defaults to the root category path. Consequently, an empty Optional is not expected here.
            if (nodeCategoryPath.isEmpty() || nodeCategoryPath.get().isEmpty()) {
                return m_uncategorized.initialised();
            }
            try {
                return getOrGrowBranchAlong( //
                    nodeCategoryPath.get(), //
                    treeInsertionContext -> createTreeNode( //
                        new CategoryInsertionContext(treeInsertionContext, node) //
                    ) //
                );
            } catch (TreeNodeCreationException e) { // NOSONAR
                return m_uncategorized.initialised();
            }

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
            return category().metadata().contributingPlugin().orElse("");
        }

        @Override
        public boolean isNode() {
            return false;
        }

        @Override
        public String getAfterID() {
            return category().metadata().afterId().map(CategoryId::toString).orElse("");
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
            // May default to "/". This value does not apply for nodes (factory ID expected). `NodeAndCategorySorter`
            // uses `null` as absence value.
            var metadata = node().nodeSpec.metadata().afterID();
            return Objects.equals(metadata, "/") ? null : metadata;
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

        private final Lazy.Init<Boolean> m_locked;

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
            this.m_locked = new Lazy.Init<>(
                () -> getCategoryExtension(metadata.path()).map(CategoryExtension::isLocked).orElse(false));
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

        public boolean locked() {
            return m_locked.initialised();
        }

    }
}
