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
import static org.knime.gateway.impl.util.ListFunctions.foldAppend;
import static org.knime.gateway.impl.util.ListFunctions.mapWithPrevious;

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
import java.util.regex.Pattern;

import org.knime.core.node.NodeLogger;
import org.knime.core.node.extension.CategoryExtension;
import org.knime.core.node.extension.NodeAndCategorySorter;
import org.knime.core.node.extension.NodeSpecCollectionProvider;
import org.knime.gateway.api.webui.entity.CategoryMetadataEnt;
import org.knime.gateway.api.webui.entity.NodeCategoryEnt;
import org.knime.gateway.impl.util.Lazy;

/**
 * The hierarchy of node categories
 *
 * @author Benjamin Moser, KNIME GmbH, Konstanz, Germany
 */
public final class NodeCategories {

    static final String UNCATEGORIZED_KEY = "/uncategorized";

    /**
     * ('top-level') tag for nodes that are at root-level, that are without a category, or that reference a
     * (first-level) category that is not registered (via the category-extension point). Note that this is a real
     * category like any other.
     */
    static final String UNCATEGORIZED_NAME = "Uncategorized";

    /**
     * These Plug-Ins are not allowed to dynamically create categories.
     *
     */
    static final Set<String> DEFAULT_CATEGORY_CREATION_BLACKLIST = Set.of("org.knime.python3.nodes");

    /**
     * The category hierarchy tree
     */
    private final Lazy.Init<CategoryTree> m_tree;

    /**
     * Provides access to available nodes.
     */
    private final NodeRepository m_nodeRepository;

    /**
     * Build the node category hierarchy based on the nodes in the given repository.
     *
     * @param nodeRepository The nodes that will span this category hierarchy
     * @param categoryExtensions Provides access to category metadata
     */
    NodeCategories(final NodeRepository nodeRepository,
        final Set<String> categoryCreationBlacklist) {
        var categoryExtensions = NodeSpecCollectionProvider.getInstance().getCategoryExtensions();
        m_nodeRepository = nodeRepository;
        m_tree = new Lazy.Init<>(() -> new CategoryTree( //
            nodeRepository.getNodes(), //
            new RepositoryContext( //
                CategoryMetadataAccess.adapt(categoryExtensions), //
                categoryCreationBlacklist //
            ) //
        ));
        nodeRepository.onContentChange(m_tree::clear);
    }

    public NodeCategories(final NodeRepository nodeRepository) {
        this(nodeRepository, DEFAULT_CATEGORY_CREATION_BLACKLIST);
    }

    /**
     * Get the category at the given path.
     *
     * @throws NoSuchElementException if no category is found at this path
     * @return the category at this path
     */
    @SuppressWarnings("java:S3242") // more general type for method parameter possible
    public NodeCategoryEnt getCategoryEnt(final List<String> path) throws NoSuchElementException {
        var treeNode = m_tree //
            .initialised()
            .get(path.stream().map(CategoryId::new).toList()) //
            .orElseThrow();
        var children = treeNode.categories().transformed().values().stream() //
            .map(child -> child.metadata().toEntity()).toList();
        var nodes = m_nodeRepository.mapNodeTemplateEnts(treeNode.nodes().transformed(), true);
        var categoryMetadata = treeNode.metadata();
        return builder(NodeCategoryEnt.NodeCategoryEntBuilder.class) //
            .setMetadata(categoryMetadata != null ? categoryMetadata.toEntity() : null) //
            .setChildCategories(children) //
            .setNodes(nodes) //
            .build();
    }

    /**
     * Adapt {@link NodeCategoryExtensions} with keys of type {@code String} to keys of type {@code List<CategoryId>}
     */
    @FunctionalInterface
    private interface CategoryMetadataAccess {
        static CategoryMetadataAccess adapt(final Map<String, CategoryExtension> categories) {
            return path -> {
                return Optional.ofNullable(categories.get(CategoryId.categoryPathToString(path)));
            };
        }

        Optional<CategoryExtension> get(List<CategoryId> path);
    }

    private record RepositoryContext(CategoryMetadataAccess categoryMetadataAccess,
            Set<String> categoryCreationBlacklist) {

    }

    /**
     * Type to distinguish category IDs from other Strings.
     *
     * @param value Also known as the "level ID"
     */
    private record CategoryId(String value) implements Comparable<CategoryId> {

        @Override
        public String toString() {
            return value;
        }

        @Override
        public int compareTo(final CategoryId other) {
            return this.value().compareTo(other.value());
        }

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
    }

    record CategoryMetadata(CategoryId id, String displayName, List<CategoryId> path, Optional<CategoryId> afterId,
            Optional<String> contributingPlugin, boolean isLocked) implements Comparable<CategoryMetadata> {

        @Override
        public int compareTo(final CategoryMetadata other) {
            return Comparator.comparing(CategoryMetadata::displayName) //
                .thenComparing(CategoryMetadata::id) //
                .compare(this, other);
        }

        /**
         * Infer category metadata from the path and a node in the subtree rooted at the end of this path.
         */
        static CategoryMetadata fromInsertionContext(final CategoryTree.CategoryInsertionContext context) {
            return new CategoryMetadata( //
                context.categoryIdentifier(), //
                context.categoryIdentifier().toString(), //
                context.path(), //
                Optional.empty(), //
                Optional.ofNullable(context.nodeContributingPlugin()), //
                false
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
                Optional.ofNullable(extension.getContributingPlugin()), //
                extension.isLocked()
            );
        }

        /**
         * @return The (hardcoded) category that is intended to contain uncategorized nodes.
         */
        static CategoryMetadata getUncategorizedCategory(
            final CategoryMetadataAccess categoryMetadataAccess) {
            return categoryMetadataAccess.get(List.of(CategoryId.of(UNCATEGORIZED_KEY))) //
                .map(CategoryMetadata::fromCategoryExtension) //
                .orElse(new CategoryMetadata( //
                    CategoryId.of(UNCATEGORIZED_KEY), //
                    UNCATEGORIZED_NAME, //
                    List.of(), //
                    Optional.empty(), //
                    Optional.empty(), //
                    false
                ));
        }

        CategoryMetadataEnt toEntity() {
            return builder(CategoryMetadataEnt.CategoryMetadataEntBuilder.class) //
                .setPath(path().stream().map(CategoryId::toString).toList()) //
                .setDisplayName(this.displayName()) //
                .build();
        }
    }

    /**
     * A node category hierarchy.
     * <p>
     * The tree hierarchically organizes values of the type {@link CategoryTreeNode}. Such a tree node corresponds to
     * one node category, constituted of nodes contained in this hierarchy and child categories.
     * <p>
     * The returned category hierarchy is the one induced by the given nodes. In other words, there were explicitly
     * defined metadata of a category but no node to go into it, this category will appear here.
     * <p>
     * If a category is <i>locked</i>, only compatible categories can be added as children. See
     * {@link CategoryInsertionContext#canInsertOrThrow()}.
     * <p>
     * If a node is <i>blacklisted</i>, it can not cause dynamic creation of categories, i.e. insertion of categories
     * whose metadata is not given by a category extension but inferred from the node metadata, see
     * {@link CategoryInsertionContext#nodeMayCreateCategory()}
     *
     * @implNote An instance of this class is intended to be used as if it was fully immutable. It is in fact not
     *           because {@link CategoryTreeNode#m_nodes} and {@link CategoryTreeNode#m_children} have to be mutable
     *           collections for the way the tree is constructed.
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private static final class CategoryTree extends Tree<CategoryId, CategoryTreeNode> {

        private final RepositoryContext m_repositoryContext;

        /**
         * Special category for nodes that could not be assigned a category.
         *
         * @implNote This is expected to correspond to an actually defined, installed category, see
         *           {@link NodeCategories#UNCATEGORIZED_KEY}.
         */
        Lazy.Init<CategoryTreeNode> m_uncategorized;

        /**
         * Create a category tree that organizes the given nodes.
         */
        CategoryTree(final Collection<NodeRepository.Node> nodes, final RepositoryContext repositoryContext) {
            super(new CategoryTreeNode(null));

            m_repositoryContext = repositoryContext;

            m_uncategorized = new Lazy.Init<>(() -> {
                var treeNode = new CategoryTreeNode( //
                    CategoryMetadata.getUncategorizedCategory(repositoryContext.categoryMetadataAccess()) //
                );
                root().children().put(treeNode.metadata().id(), treeNode);
                return treeNode;
            });

            // Since the tree should contain only the given nodes and the given collection is unordered,
            // iterate over the given nodes and create category tree nodes as needed.
            nodes.forEach(node -> this.insertNode(node));
        }

        private static boolean isContributedByKNIME(final Optional<String> plugInId) {
            return plugInId.flatMap(Vendor::parse) //
                .filter(vendor -> vendor.equals(Vendor.ORG_KNIME) || vendor.equals(Vendor.COM_KNIME)) //
                .isPresent();
        }

        /**
         * @return Whether the vendor part (substring up to second dot) of the given plugin IDs is equal.
         */
        private static boolean vendorEqual(final Optional<String> plugInId, final Optional<String> otherPlugInId) {
            if (plugInId.equals(otherPlugInId)) {
                return true;
            }
            var plugInVendor = plugInId.flatMap(Vendor::parse);
            var otherPlugInVendor = otherPlugInId.flatMap(Vendor::parse);
            if (plugInVendor.isEmpty() || otherPlugInVendor.isEmpty()) {
                return false;
            }
            return plugInVendor.equals(otherPlugInVendor);
        }

        /**
         * Creates a the new {@link CategoryTreeNode} if possible, else throws.
         *
         * @return The new {@link CategoryTreeNode}
         * @throws CategoryCreationException If the new node could not be inserted into its context
         */
        private static CategoryTreeNode createTreeNode(final CategoryInsertionContext context)
            throws CategoryCreationException {
            context.canInsertOrThrow(); // throws
            return new CategoryTreeNode(context.categoryMetadata());
        }

        /**
         * Find (optionally create) the category tree node this node should go into and insert the given node there.
         *
         * @param node to insert
         */
        private void insertNode(final NodeRepository.Node node) {
            var nodeCategoryPath = CategoryId.toCategoryPath(node.nodeSpec.metadata().categoryPath());
            var categoryToInsertInto = findCategoryToInsertInto(nodeCategoryPath, node);
            categoryToInsertInto //
                .nodes().original() //
                .add(node); //
        }

        @SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "java:S3553"})
        private CategoryTreeNode findCategoryToInsertInto(final Optional<List<CategoryId>> nodeCategoryPath,
            final NodeRepository.Node node) {
            // Providing an empty string or omitting the category-path attribute in the plugin.xml
            // file defaults to the root category path. Consequently, an empty Optional is not expected here.
            if (nodeCategoryPath.isEmpty() || nodeCategoryPath.get().isEmpty()) {
                return m_uncategorized.initialised();
            }
            try {
                var categoryToInsertInto = insertParentCategoriesFor(nodeCategoryPath.get(), node);
                // The category might already be part of the tree via some earlier valid insertion. Still need to check
                //  whether the node is allowed to be in that category. The same check is done during insertion of
                //  categories: A node cannot cause insertion of a category it is not allowed to be in.
                // Note that we do not need to check parent categories because in the incompatible case such a child
                //  could not have been added.
                var nodeCompatible = CategoryInsertionContext.isVendorCompatible( //
                    Optional.ofNullable(node.nodeSpec().metadata().vendor().bundle().getSymbolicName()), //
                    categoryToInsertInto.metadata().contributingPlugin() //
                );
                if (categoryToInsertInto.locked() && !nodeCompatible) {
                    NodeLogger.getLogger(NodeCategories.class)
                        .warn(("Could not insert node into category because category is locked."
                            + " Assigning to uncategorized instead. \n %s \n %s")
                            .formatted(node, categoryToInsertInto));
                    return m_uncategorized.initialised();
                }
                return categoryToInsertInto;
            } catch (CategoryCreationException e) { // NOSONAR
                NodeLogger.getLogger(NodeCategories.class).info(e);
                return m_uncategorized.initialised();
            }
        }

        /**
         * Search and, if needed, extend the category tree
         */
        private CategoryTreeNode insertParentCategoriesFor(final List<CategoryId> nodeCategoryPath,
            final NodeRepository.Node node)
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
            var pathsToInsert = foldAppend(difference.contained(), difference.notContained());
            List<CategoryTreeNode> values = mapWithPrevious(pathsToInsert, (previouslyCreated, path) -> {
                var parent = previouslyCreated.or(() -> Optional.of(treeLeaf));
                var insertionContext = new CategoryInsertionContext(parent, path, node, m_repositoryContext);
                return createTreeNode(insertionContext); // throws
            });
            var keys = difference.notContained();
            var newBranch = new Branch<>(keys, values);
            treeLeaf.attach(newBranch);
            return newBranch.lastValue();
        }

        private record Vendor(String domain, String group) {

            private static final Pattern PATTERN = Pattern.compile("^([^.]+)\\.([^.]+).*");

            private static final Vendor ORG_KNIME = new Vendor("org", "knime");

            private static final Vendor COM_KNIME = new Vendor("com", "knime");

            static Optional<Vendor> parse(final String plugInId) {
                var matcher = PATTERN.matcher(plugInId);
                if (!matcher.matches() || matcher.group(1) == null || matcher.group(2) == null) {
                    return Optional.empty();
                }
                return Optional.of(new Vendor(matcher.group(1), matcher.group(2)));
            }
        }

        /**
         * Context under which the current insertion operation is performed.
         */
        @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
        private record CategoryInsertionContext(Optional<CategoryTreeNode> parent, List<CategoryId> path,
                NodeRepository.Node node, RepositoryContext repositoryContext) {

            /**
             * @param contributingPlugin The plugin that aims to contribute to something provided by the
             *            {@code targetPlugin}
             * @param targetPlugin The plugin to be contributed to.
             */
            private static boolean isVendorCompatible(final Optional<String> contributingPlugin,
                final Optional<String> targetPlugin) {
                return isContributedByKNIME(contributingPlugin) || vendorEqual(contributingPlugin, targetPlugin);
            }

            private boolean isParentLocked() {
                return parent().map(CategoryTreeNode::locked).orElse(false);
            }

            /**
             * This check also applies to nodes supplied by KNIME.
             *
             * @throws CategoryCreationException
             */
            private void nodeMayCreateCategory() throws CategoryCreationException {
                var allowed = !repositoryContext().categoryCreationBlacklist().contains(this.nodeContributingPlugin());
                if (!allowed) {
                    throw new CategoryCreationException("Contributing Plug-In %s may not dynamically create categories"
                        .formatted(this.nodeContributingPlugin()));
                }
            }

            CategoryId categoryIdentifier() {
                return path().get(path().size() - 1);
            }

            /**
             * @return The plug-in of the node which implies creation of this category in the hierarchy.
             */
            String nodeContributingPlugin() {
                return this.node().nodeSpec().metadata().vendor().bundle().getSymbolicName();
            }

            Optional<String> categoryContributingPlugin() {
                return categoryMetadata().contributingPlugin();
            }

            /**
             * Check whether this category insertion context is valid, i.e. whether the described category can be
             * inserted.
             */
            void canInsertOrThrow() throws CategoryCreationException {
                nodeMayCreateCategory();
                // A node may not be allowed to be inserted into a category. This case is relevant if the category
                //   metadata is explicitly given -- if it is inferred from the node, it is trivially compatible.
                // For example, metadata of category `/mycategory` would be given by vendor org.foo.bar and marked as
                //  locked, and another extension of vendor org.baz.qux could provide a node with
                //  category path `/mycategory`. This check prevents the insertion of `/mycategory` due to the
                //  org.baz.qux node.
                var isNodeCompatibleToCategory = isVendorCompatible( //
                    Optional.of(nodeContributingPlugin()), //
                    categoryContributingPlugin() //
                );
                if (categoryMetadata().isLocked() && !isNodeCompatibleToCategory) {
                    throw new CategoryCreationException("Node is not allowed to be a child of category");
                }
                // A category may be marked as "locked", in which case only compatible child categories may be inserted.
                var parentPluginId = parent() //
                    .map(CategoryTreeNode::metadata) //
                    .flatMap(CategoryMetadata::contributingPlugin);
                var isCategoryCompatibleToParent = isVendorCompatible( //
                    categoryContributingPlugin(), //
                    parentPluginId //
                );
                if (isParentLocked() && !isCategoryCompatibleToParent) {
                    throw new CategoryCreationException(
                        "Parent category is locked and child category is neither from same vendor"
                            + "nor contributed by KNIME.");
                }
            }

            /**
             * @return The metadata of the candidate category to be inserted.
             */
            CategoryMetadata categoryMetadata() {
                return repositoryContext().categoryMetadataAccess().get(this.path()) //
                    .map(CategoryMetadata::fromCategoryExtension) //
                    .orElse(CategoryMetadata.fromInsertionContext(this));
            }
        }

        static class CategoryCreationException extends Exception {
            CategoryCreationException(final String message) {
                super(message);
            }
        }
    }

    /**
     * Wrapper to provide accessors for {@link NodeAndCategorySorter}
     */
    private record SortableCategory(
            CategoryTreeNode category) implements NodeAndCategorySorter.NodeOrCategory<SortableCategory> {

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

        private static LinkedHashMap<CategoryId, CategoryTreeNode>
            sortCategories(final Map<CategoryId, CategoryTreeNode> categories) {
            var sortableCategories = categories.values().stream().map(SortableCategory::new).toList();
            var sortedCategories = NodeAndCategorySorter.sortNodesAndCategories(sortableCategories).stream()
                .map(SortableCategory::category);
            var result = new LinkedHashMap<CategoryId, CategoryTreeNode>();
            sortedCategories.forEach(cat -> result.put(cat.metadata().id(), cat));
            return result;
        }
    }

    /**
     * Wrapper to provide accessors for {@link NodeAndCategorySorter}.
     */
    private record SortableNode(
            NodeRepository.Node node) implements NodeAndCategorySorter.NodeOrCategory<SortableNode> {

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

        private static List<NodeRepository.Node> sort(final List<NodeRepository.Node> nodes) {
            var sortableNodes = nodes.stream().map(SortableNode::new).toList();
            return NodeAndCategorySorter.sortNodesAndCategories(sortableNodes).stream().map(SortableNode::node)
                .toList();
        }
    }

    private static final class CategoryTreeNode implements Tree.TreeNode<CategoryId, CategoryTreeNode> {
        private final CategoryMetadata m_metadata;

        private final Lazy.Transform<List<NodeRepository.Node>> m_nodes;

        private final Lazy.Transform<Map<CategoryId, CategoryTreeNode>> m_children;

        /**
         * @param metadata Metadata for this category
         * @param nodes Nodes directly in this category (i.e., not including nodes in child categories)
         * @param children child categories of this category.
         */
        private CategoryTreeNode( //
            final CategoryMetadata metadata, //
            final List<NodeRepository.Node> nodes, //
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

        @Override
        public Map<CategoryId, CategoryTreeNode> children() {
            return m_children.original();
        }

        @Override
        public String toString() {
            return "CategoryTreeNode{" + "m_metadata=" + m_metadata + ", m_nodes=" + m_nodes + ", m_children="
                + m_children + '}';
        }

        CategoryMetadata metadata() {
            return m_metadata;
        }

        Lazy.Transform<List<NodeRepository.Node>> nodes() {
            return m_nodes;
        }

        Lazy.Transform<Map<CategoryId, CategoryTreeNode>> categories() {
            return m_children;
        }

        /**
         * Root of tree has null metadata, see {@link CategoryTree#CategoryTree}
         */
        boolean locked() {
            if (m_metadata == null) {
                return false;
            }
            return m_metadata.isLocked();
        }
    }
}
