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
package org.knime.gateway.impl.webui.repo;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertThrows;
import static org.knime.gateway.impl.util.ListFunctions.enumerate;
import static org.mockito.Mockito.mock;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.extension.CategoryExtension;
import org.knime.core.node.extension.NodeSpec;
import org.knime.gateway.api.webui.entity.NodeCategoryEnt;
import org.knime.shared.workflow.def.impl.VendorDefBuilder;

@SuppressWarnings({"javadoc", "java:S5960"})
public class NodeCategoriesTest {

    private static final String SOME_FEATURE_VENDOR = "org.foo.someFeatureVendor";

    private static final String SOME_BUNDLE_VENDOR = "org.foo.someBundleVendor";

    private static NodeRepository nodeRepositoryWith(final NodeSpec... specs) {
        var nodes = enumerate(specs).collect(Collectors.toMap( //
            e -> String.valueOf(e.index()), //
            e -> e.element() //
        ));
        var nodeSpecProvider = new NodeSpecProvider() {
            @Override
            public Map<String, NodeSpec> getNodes() {
                return nodes;
            }

            @Override
            public Map<String, NodeSpec> getActiveNodes() {
                return nodes;
            }
        };
        return new NodeRepository(null, nodeSpecProvider);
    }

    private static NodeSpec nodeSpec(final String categoryPath) {
        return nodeSpec(categoryPath, someVendor());
    }

    private static NodeSpec nodeSpec(final String categoryPath, final NodeSpec.Metadata.Vendor vendor) {
        var metadata = new NodeSpec.Metadata( //
            vendor, "node name", //
            NodeFactory.NodeType.Unknown, //
            categoryPath, //
            "afterId", //
            List.of(), //
            List.of() //
        );
        return new NodeSpec( //
            new NodeSpec.Factory("id", "className", null), //
            NodeFactory.NodeType.Unknown, //
            new NodeSpec.Ports(List.of(), List.of(), List.of(), List.of()), //
            metadata, //
            mock(URL.class), //
            false, //
            false //
        );
    }

    private static NodeSpec.Metadata.Vendor someVendor() {
        return new NodeSpec.Metadata.Vendor( //
            new VendorDefBuilder().setSymbolicName(SOME_FEATURE_VENDOR).build(), //
            new VendorDefBuilder().setSymbolicName(SOME_BUNDLE_VENDOR).build() //
        );
    }

    private static NodeCategoryExtensions categories(final CategoryExtension... catExts) {
        return () -> Arrays.stream(catExts).collect(Collectors.toMap( //
            cat -> cat.getCompletePath(), //
            cat -> cat) //
        );
    }

    private static void assertHasNodes(final NodeCategoryEnt categoryEnt) {
        assertThat("The returned category should contain some nodes", !categoryEnt.getNodes().isEmpty());
    }

    private static NodeSpec.Metadata.Vendor knimeVendor() {
        return new NodeSpec.Metadata.Vendor( //
            new VendorDefBuilder().setSymbolicName("org.knime.featureVendor").build(), //
            new VendorDefBuilder().setSymbolicName("org.knime.bundleVendor").build() //
        );
    }

    private static void assertUncategorizedNotEmpty(final NodeCategories nodeCategories) {
        assertThat( //
            "Node should be placed in uncategorized", //
            !nodeCategories.getCategoryEnt(List.of("uncategorized")).getNodes().isEmpty() //
        );
    }

    @Test
    public void testGetRootCategory() {
        var repo = nodeRepositoryWith(nodeSpec("/parent/cat/"));
        var categories = categories(CategoryExtension.builder("cat", "cat").withPath("parent").build());
        var nodeCategories = new NodeCategories(repo, categories);
        var rootCategory = nodeCategories.getCategoryEnt(List.of());
        assertThat("The root category should contain some child categories",
            !rootCategory.getChildCategories().isEmpty());
    }

    @Test
    public void testNodeIsInsertedIntoChildCategory() {
        var repo = nodeRepositoryWith(nodeSpec("/parent/child/"));
        var categories = categories( //
            CategoryExtension.builder("parent", "parent").build(), //
            CategoryExtension.builder("child", "child") //
                .withPath("parent").build() //
        );
        assertHasNodes(new NodeCategories(repo, categories).getCategoryEnt(List.of("parent", "child")));

    }

    @Test
    public void testNodeCanInduceCategory() {
        var categories = categories(CategoryExtension.builder("cat", "cat").withPath("parent").build());
        var repo = nodeRepositoryWith(nodeSpec("/parent/cat"));
        var categoryEnt = new NodeCategories(repo, categories).getCategoryEnt(List.of("parent", "cat"));
        assertHasNodes(categoryEnt);
    }

    @Test
    public void testNonExistingCategoryThrows() {
        assertThrows(NoSuchElementException.class, () -> //
        new NodeCategories(nodeRepositoryWith(), Map::of) //
            .getCategoryEnt(List.of("foo", "bar", "baz")) //
        );
    }

    /**
     * A category can be added as child to a locked parent category if the child category is contributed by KNIME.
     */
    @Test
    public void testCanInsertIntoLockedIfContributedByKNIME() {
        var lockedParent =
            CategoryExtension.builder("parent", "parent").withPluginId(someVendor().bundle().getSymbolicName()) //
                .withLocked(true) //
                .build();
        var childContributedByKNIME = CategoryExtension.builder("cat", "cat") //
            .withPath("parent") //
            .withPluginId(knimeVendor().bundle().getSymbolicName()) //
            .build();
        var categories = categories(lockedParent, childContributedByKNIME);
        // important to make the node compatible, else it would not be allowed to be added -- but this is not what
        // we want to test here. Instead, we want to test that the child category can be added.
        var repo = nodeRepositoryWith(nodeSpec("/parent/cat/", someVendor()));
        var categoryEnt = new NodeCategories(repo, categories) //
            .getCategoryEnt(List.of("parent"));
        assertThat("The locked parent category should contain some child categories",
            !categoryEnt.getChildCategories().isEmpty());
    }

    /**
     * Similar to {@link this#testCanInsertIntoLockedIfContributedByKNIME()}, except for that the category to be created
     * is not explicitly given but inferred ad-hoc from the node target path.
     */
    @Test
    public void testNodeCanInduceChildOfLockedIfCompatible() {
        var categories = categories(CategoryExtension.builder("parent", "parent")
            .withPluginId(someVendor().bundle().getSymbolicName()).withLocked(true).build());
        var repo = nodeRepositoryWith(nodeSpec("/parent/cat/", someVendor()));
        var categoryEnt = new NodeCategories(repo, categories) //
            .getCategoryEnt(List.of("parent", "cat")); // should not throw
        assertHasNodes(categoryEnt);
    }

    /**
     * Negation of {@link this#testNodeCanInduceChildOfLockedIfCompatible()} Analog to
     * {@link this#testCanNotInsertIntoLockedElse()} ()}
     */
    @Test
    public void testNodeCanNotInduceChildofLockedIfNotCompatible() {
        var lockedParent = CategoryExtension.builder("parent", "parent") //
            .withLocked(true) //
            .withPluginId("some other vendor") //
            .build();
        // the child is not explicitly defined, the node would "induce" it with its category path
        // but since it is from an incompatible vendor, this should not be allowed
        var repo = nodeRepositoryWith(nodeSpec("/parent/child", someVendor()));
        var nodeCategories = new NodeCategories(repo, categories(lockedParent));
        // no categories should have been added to the tree since that one node insertion failed
        assertThrows(NoSuchElementException.class, () -> nodeCategories.getCategoryEnt(List.of("parent")));
        assertThrows(NoSuchElementException.class, () -> nodeCategories.getCategoryEnt(List.of("parent", "child")));
        // node should have been put into uncategorized instead
        assertUncategorizedNotEmpty(nodeCategories);
    }

    /**
     * Similar to {@link this#testCanInsertIntoLockedIfContributedByKNIME()}, except for that the category to be created
     * is not explicitly given but inferred ad-hoc from the node target path.
     * <p>
     * This is the special case for if the node is contributed by KNIME. A more general case is if the node is of a
     * compatible vendor, see {@link this#testNodeCanInduceChildOfLockedIfCompatible()}
     */
    @Test
    public void testNodeCanInduceCategoryInLockedIfContributedByKNIME() {
        var categories = categories(CategoryExtension.builder("parent", "parent").withLocked(true).build());
        // adding this node would imply adding a category `cat` ad hoc
        var nodeByKNIME = nodeSpec("/parent/cat/", knimeVendor());
        var categoryEnt =
            new NodeCategories(nodeRepositoryWith(nodeByKNIME), categories).getCategoryEnt(List.of("parent", "cat"));
        assertHasNodes(categoryEnt);
    }

    /**
     * Can not insert into locked in the general case.
     * 
     * Analog to {@link this#testNodeCanNotInduceChildofLockedIfNotCompatible()}
     */
    @Test
    public void testCanNotInsertIntoLockedElse() {
        var lockedParent = CategoryExtension.builder("parent", "parent") //
            .withPluginId("some other vendor") //
            .withLocked(true) //
            .build();
        var childContributedBySomeVendor = CategoryExtension.builder("child", "child") //
            .withPath("parent") //
            .withPluginId(SOME_BUNDLE_VENDOR) //
            .build();
        var categories = categories(lockedParent, childContributedBySomeVendor);
        var repo = nodeRepositoryWith(nodeSpec("/parent/child/"));
        var nodeCategories = new NodeCategories(repo, categories);
        assertThrows( //
            NoSuchElementException.class, //
            () -> nodeCategories.getCategoryEnt(List.of("parent", "child")) //
        );
        assertUncategorizedNotEmpty(nodeCategories);
    }

    /**
     * Some nodes are not allowed to imply categories ad-hoc.
     */
    @Test
    public void testBlackListedNodeMayNotCreateCategories() {
        var repo = nodeRepositoryWith(nodeSpec("/parent/", someVendor()));
        var categoryCreationBlacklist = Set.of(someVendor().bundle().getSymbolicName());
        var nodeCategories = new NodeCategories( //
            repo, //
            categories(), // no explicit category metadata -- any category would have to be implied ad-hoc by a node
            categoryCreationBlacklist //
        );
        assertThrows( //
            NoSuchElementException.class, //
            () -> nodeCategories.getCategoryEnt(List.of("parent")) //
        );
        assertUncategorizedNotEmpty(nodeCategories);
    }

    /**
     * See NXT-2840 WEKA nodes not categorized correctly
     */
    @Test
    public void testCanInsertCategoriesWithNoExplicitMetadata() {
        var repo = nodeRepositoryWith(nodeSpec("/cat", knimeVendor()));
        var categoryEnt = new NodeCategories(repo, categories()) //
            .getCategoryEnt(List.of("cat"));
        assertHasNodes(categoryEnt);
    }

    /**
     * See NXT-3229 Some community nodes are not categorized
     */
    @Test
    public void testCanInsertNodeWithIncompatibleVendorIntoUnlocked() {
        var categories = categories(CategoryExtension.builder("cat", "cat") //
            .withPluginId("some other vendor") //
            .withLocked(false) //
            .build() //
        );
        var repo = nodeRepositoryWith(nodeSpec("/cat", someVendor()));
        var categoryEnt = new NodeCategories(repo, categories) //
            .getCategoryEnt(List.of("cat"));
        assertHasNodes(categoryEnt);
    }

    @Test
    public void testCanNotInsertNodeWithIncompatibleVendorIntoLocked() {
        var categories = categories(CategoryExtension.builder("cat", "cat") //
            .withPluginId("some other vendor") //
            .withLocked(true) //
            .build() //
        );
        var repo = nodeRepositoryWith(nodeSpec("/cat", someVendor()));
        var nodeCategories = new NodeCategories(repo, categories);
        assertThrows( //
            NoSuchElementException.class, //
            () -> nodeCategories.getCategoryEnt(List.of("cat")) //
        );
        assertUncategorizedNotEmpty(nodeCategories);
    }

    @Test
    public void testCanInsertNodeIntoLockedIfContributedByKNIME() {
        var categories = categories(CategoryExtension.builder("cat", "cat") //
            .withPluginId("some other vendor") //
            .withLocked(true) //
            .build() //
        );
        var repo = nodeRepositoryWith(nodeSpec("/cat", knimeVendor()));
        var categoryEnt = new NodeCategories(repo, categories) //
            .getCategoryEnt(List.of("cat"));
        assertHasNodes(categoryEnt);
    }

}
