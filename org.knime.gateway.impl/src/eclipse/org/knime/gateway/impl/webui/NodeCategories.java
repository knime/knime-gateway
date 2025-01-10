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
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import org.knime.core.node.NodeLogger;
import org.knime.core.node.extension.CategoryExtension;
import org.knime.core.node.extension.NodeAndCategorySorter;
import org.knime.gateway.api.webui.entity.CategoryMetadataEnt;
import org.knime.gateway.api.webui.entity.NodeCategoryEnt;
import org.knime.gateway.api.webui.entity.NodeTemplateEnt;
import org.knime.gateway.impl.util.Lazy;
import org.knime.gateway.impl.util.ListFunctions;
import org.knime.gateway.impl.webui.NodeRepository.Node;

/**
 * The hierarchy of node categories
 *
 * @author Benjamin Moser, KNIME GmbH, Konstanz, Germany
 */
@SuppressWarnings("java:S1602")
public final class NodeCategories {

    static final String UNCATEGORIZED_KEY = "/uncategorized";

    /*
     * ('top-level') tag for nodes that are at root-level, that are without a category, or
     * that reference a (first-level) category that is not registered (via the category-extension point).
     * Note that this is a real category like any other.
     */
    static final String UNCATEGORIZED_NAME = "Uncategorized";

    /**
     * These Plug-Ins are not allowed to dynamically create categories.
     */
    private static final Set<String> CATEGORY_CREATION_BLACKLIST = Set.of("org.knime.python3.nodes");

    /**
     * tree with null-ish root
     */
    private final Lazy.Init<CategoryTree> m_tree;

    /**
     * Build the node category hierarchy based on the given nodes
     *
     * @param nodeRepository The nodes that will span this category hierarchy
     * @param getCategoryExtensions
     */
    public NodeCategories(final NodeRepository nodeRepository,
        final Supplier<Map<String, CategoryExtension>> getCategoryExtensions) {
        this(nodeRepository.getNodes(), getCategoryExtensions);
        nodeRepository.onContentChange(m_tree::clear);
    }

    public NodeCategories(final Collection<Node> nodes,
        final Supplier<Map<String, CategoryExtension>> categoryExtensions) {
        Function<List<CategoryId>, Optional<CategoryExtension>> getCategoryExtension = path -> {
            return Optional.ofNullable(categoryExtensions.get().get(CategoryId.categoryPathToString(path)));
        };
        m_tree = new Lazy.Init<>(() -> new CategoryTree(nodes, getCategoryExtension));
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
         * @return Empty optional if {@code string} is null, empty or blank. Optional of empty list if the assigned
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
    @SuppressWarnings("java:S3242") // more general type for method parameter possible
    public NodeCategoryEnt getCategoryEnt(final List<String> path,
        final BiFunction<Collection<NodeRepository.Node>, Boolean, List<NodeTemplateEnt>> mapNodeTemplateEnts)
        throws NoSuchElementException {
        var treeNode = m_tree //
            .get() // lazily build category hierarchy tree on first access
            .get(path.stream().map(CategoryId::new).toList()) //
            .orElseThrow();
        var children = treeNode.categories().transformed().values().stream() //
            .map(child -> child.metadata().toEntity()).toList();
        var nodes = mapNodeTemplateEnts.apply(treeNode.nodes().transformed(), true);
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
        static CategoryMetadata fromInsertionContext(final CategoryTree.CategoryInsertionContext context) {
            return new CategoryMetadata( //
                context.categoryIdentifier(), //
                context.categoryIdentifier().toString(), //
                context.path(), //
                Optional.empty(), //
                Optional.ofNullable(context.contributingPlugin()) //
            );
        }

        /**
         * Fetch metadata from pre-defined category extensions
         */
        static CategoryMetadata fromCategoryExtension(final CategoryExtension extension) {
            return new CategoryMetadata( //
                CategoryId.of(extension.getLevelId()), //
                extension.getName(), //
                CategoryId.toCategoryPath(extension.getCompletePath()).orElseThrow(),
                CategoryId.optionalOf(extension.getAfterID()), //
                Optional.ofNullable(extension.getContributingPlugin()) //
            );
        }

        /**
         * @return The (hardcoded) category that is supposed to contain uncategorized nodes.
         */
        static CategoryMetadata getUncategorizedCategory(
            final Function<List<CategoryId>, Optional<CategoryExtension>> getCategoryExtension) {
            return getCategoryExtension.apply(List.of(CategoryId.of(UNCATEGORIZED_KEY))) //
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
     * The tree hierarchically organizes values of the type {@link CategoryTreeNode}. Such a tree node corresponds to
     * one node category, constituted of nodes contained in this hierarchy and child categories.
     * <p>
     *
     * @implNote An instance of this class is intended to be used as if it was fully immutable. It is in fact not
     *           because {@link CategoryTreeNode#m_nodes} and {@link CategoryTreeNode#m_children} have to be mutable
     *           collections for the way the tree is constructed.
     */
    private static final class CategoryTree extends Tree<CategoryId, CategoryTreeNode> {

        private final Function<List<CategoryId>, Optional<CategoryExtension>> m_getCategoryExtension;

        /**
         * Create a category tree that organizes the given nodes.
         *
         * @param nodes The nodes that will be leaves of the category tree, inner nodes are categories.
         * @param getCategoryExtension Function that provides category metadata
         */
        CategoryTree(final Collection<Node> nodes,
            final Function<List<CategoryId>, Optional<CategoryExtension>> getCategoryExtension) {
            super(new CategoryTreeNode(null, () -> false));

            m_getCategoryExtension = getCategoryExtension;

            m_uncategorized = new Lazy.Init<>(() -> {
                var treeNode =
                    new CategoryTreeNode(CategoryMetadata.getUncategorizedCategory(getCategoryExtension), () -> false);
                root().children().put(treeNode.metadata().id(), treeNode);
                return treeNode;
            });

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
        Lazy.Init<CategoryTreeNode> m_uncategorized;

        /**
         * Context under which the current insertion operation is performed.
         */
        record CategoryInsertionContext(Optional<CategoryTreeNode> parent, List<CategoryId> path, Node node) {

            private static boolean isContributedByKNIME(final String plugInId) {
                return vendorEqual(plugInId, "org.knime.") //
                    || vendorEqual(plugInId, "com.knime.");
            }

            /**
             * @return Whether the vendor part (substring up to second dot) of the given plugin IDs is equal.
             */
            private static boolean vendorEqual(final String plugInId, final String otherPlugInId) {
                if (plugInId == null || otherPlugInId == null) {
                    return false;
                }
                int secondDotIndex = plugInId.indexOf('.', plugInId.indexOf('.') + 1);
                if (secondDotIndex == -1) {
                    secondDotIndex = 0;
                }
                return plugInId.regionMatches(0, otherPlugInId, 0, secondDotIndex);
            }

            private boolean isParentLocked() {
                return parent().map(CategoryTreeNode::locked).orElse(false);
            }

            CategoryId categoryIdentifier() {
                return path().get(path().size() - 1);
            }

            String contributingPlugin() {
                return this.node().nodeSpec().metadata().vendor().bundle().getSymbolicName();
            }

            private void mayCreateCategory() throws CategoryCreationException {
                var allowed = !NodeCategories.CATEGORY_CREATION_BLACKLIST.contains(this.contributingPlugin());
                if (!allowed) {
                    throw new CategoryCreationException("Contributing Plug-In %s may not dynamically create categories"
                        .formatted(this.contributingPlugin()));
                }
            }

            private boolean isCompatibleToParentVendor() {
                var parentPluginId = parent() //
                    .map(CategoryTreeNode::metadata) //
                    .flatMap(CategoryMetadata::contributingPlugin);
                if (parentPluginId.isPresent()) {
                    var childPluginId = this.contributingPlugin();
                    return childPluginId.equals(parentPluginId.get()) //
                        || vendorEqual(childPluginId, parentPluginId.get());
                }
                return true;
            }

            /**
             * Check whether this category insertion context is valid, i.e. the operation it describes is allowed. If
             * not, throw.
             */
            void canInsertOrThrow() throws CategoryCreationException {
                mayCreateCategory();
                if (isContributedByKNIME(this.contributingPlugin())) {
                    return;
                }
                if (isParentLocked() && !isCompatibleToParentVendor()) {
                    throw new CategoryCreationException(
                        "Parent category is locked and child is neither from same vendor"
                            + "nor contributed by KNIME.");
                }
            }
        }

        /**
         * Creates a the new {@link CategoryTreeNode} if possible, else throws.
         *
         * @param context
         * @return The new {@link CategoryTreeNode}
         * @throws CategoryCreationException If the new node could not be inserted into its context
         */
        private CategoryTreeNode createTreeNode(final CategoryInsertionContext context)
            throws CategoryCreationException {
            context.canInsertOrThrow(); // throws
            var metadata = m_getCategoryExtension.apply(context.path()) //
                .map(CategoryMetadata::fromCategoryExtension) //
                .orElse(CategoryMetadata.fromInsertionContext(context));
            Supplier<Boolean> checkIsLocked = () -> Optional.of(metadata) //
                .map(CategoryMetadata::path) //
                .flatMap(m_getCategoryExtension) //
                .map(CategoryExtension::isLocked).orElse(false);
            return new CategoryTreeNode(metadata, checkIsLocked);
        }

        @SuppressWarnings("serial")
        static class CategoryCreationException extends Exception {

            CategoryCreationException(final String message) {
                super(message);
            }
        }

        /**
         * Find (optionally create) the category tree node this node should go into and insert the given node there.
         *
         * @param node to insert
         */
        private void insertNode(final Node node) {
            var nodeCategoryPath = CategoryId.toCategoryPath(node.nodeSpec().metadata().categoryPath());
            findCategoryToInsertInto(nodeCategoryPath, node) //
                .nodes().original() //
                .add(node); //
        }

        @SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "java:S3553"})
        private CategoryTreeNode findCategoryToInsertInto(final Optional<List<CategoryId>> nodeCategoryPath,
            final Node node) {
            // Providing an empty string or omitting the category-path attribute in the plugin.xml
            // file defaults to the root category path. Consequently, an empty Optional is not expected here.
            if (nodeCategoryPath.isEmpty() || nodeCategoryPath.get().isEmpty()) {
                return m_uncategorized.get();
            }
            try {
                return insertParentCategoriesFor(nodeCategoryPath.get(), node);
            } catch (CategoryCreationException e) { // NOSONAR
                NodeLogger.getLogger(NodeCategories.class).info(e);
                return m_uncategorized.get();
            }
        }

        private CategoryTreeNode insertParentCategoriesFor(final List<CategoryId> nodeCategoryPath, final Node node)
            throws CategoryCreationException {
            // Suppose tree contains path [x1, x2]
            // Let path = [x1, x2, x3, x4, x5]
            var difference = this.difference(nodeCategoryPath);
            // difference.contained() = [x1, x2]
            // difference.notContained() = [x3, x4, x5]
            CategoryTreeNode treeLeaf = this.get(difference.contained())
                // contained in the tree by definition of `difference`, else this is an implementation error
                .orElseThrow();
            if (difference.notContained().isEmpty()) {
                // all required categories are already in the tree
                return treeLeaf;
            }
            // Otherwise, insert parent categories as needed. Try creating all categories first. If any creation fails,
            //  the tree is not modified.
            // For creating tree nodes, we need their *full* path. This means for each
            //   path segment not currently contained in the tree (i.e. in difference.notContained()), we need to
            //   construct a full path.
            // pathsToInsert = [ [x1, x2, x3], [x1, x2, x3, x4], [x1, x2, x3, x4, x5] ]
            var pathsToInsert = ListFunctions.foldAppend(difference.contained(), difference.notContained());
            List<CategoryTreeNode> values = ListFunctions.mapWithPrevious(pathsToInsert, (previouslyCreated, path) -> {
                var parent = previouslyCreated.or(() -> Optional.of(treeLeaf));
                var insertionContext = new CategoryInsertionContext(parent, path, node);
                return createTreeNode(insertionContext); // throws
            });
            var keys = difference.notContained();
            var newBranch = new Branch<>(keys, values);
            treeLeaf.attach(newBranch);
            return newBranch.lastValue();
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
            return node().nodeSpec().factory().id();
        }

        @Override
        public String getName() {
            return node().name();
        }

        @Override
        public String getContributingPlugin() {
            return node().nodeSpec().metadata().vendor().bundle().getSymbolicName();
        }

        @Override
        public boolean isNode() {
            return true;
        }

        @Override
        public String getAfterID() {
            // May default to "/". This value does not apply for nodes (factory ID expected). `NodeAndCategorySorter`
            // uses `null` as absence value.
            var metadata = node().nodeSpec().metadata().afterID();
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
            final Map<CategoryId, CategoryTreeNode> children, //
            final Supplier<Boolean> checkIsLocked) {
            this.m_metadata = metadata;
            this.m_nodes = new Lazy.Transform<>(nodes, SortableNode::sort);
            this.m_children = new Lazy.Transform<>(children, SortableCategory::sortCategories);
            this.m_locked = new Lazy.Init<>(checkIsLocked);
        }

        CategoryTreeNode(final CategoryMetadata metadata, final Supplier<Boolean> checkIsLocked) {
            this( //
                metadata, //
                new ArrayList<>(), // needs to be mutable
                new HashMap<>(), // needs to be mutable
                checkIsLocked
            );
        }

        CategoryMetadata metadata() {
            return m_metadata;
        }

        Lazy.Transform<List<Node>> nodes() {
            return m_nodes;
        }

        @Override
        public Map<CategoryId, CategoryTreeNode> children() {
            return m_children.original();
        }

        Lazy.Transform<Map<CategoryId, CategoryTreeNode>> categories() {
            return m_children;
        }

        boolean locked() {
            return m_locked.get();
        }

    }
}
