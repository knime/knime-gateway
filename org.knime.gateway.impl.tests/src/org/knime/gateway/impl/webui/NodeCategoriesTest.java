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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertThrows;
import static org.knime.gateway.api.entity.EntityBuilderManager.builder;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import org.junit.Test;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.extension.CategoryExtension;
import org.knime.core.node.extension.NodeSpec;
import org.knime.gateway.api.webui.entity.NativeNodeInvariantsEnt;
import org.knime.gateway.api.webui.entity.NodeCategoryEnt;
import org.knime.gateway.api.webui.entity.NodeTemplateEnt;
import org.knime.shared.workflow.def.impl.VendorDefBuilder;

@SuppressWarnings({"javadoc", "java:S5960"})
public class NodeCategoriesTest {

    private static final String SOME_FEATURE_VENDOR = "org.foo.someFeatureVendor";

    private static final String SOME_BUNDLE_VENDOR = "org.foo.someBundleVendor";

    private final BiFunction<Collection<NodeRepository.Node>, Boolean, List<NodeTemplateEnt>> m_mapNodesToEnts =
        (nodes, ignored) -> nodes.stream().map(node -> buildMinimalNodeTemplateEnt(node.nodeSpec())).toList();

    private static NodeTemplateEnt buildMinimalNodeTemplateEnt(final NodeSpec nodeSpec) {
        return builder(NodeTemplateEnt.NodeTemplateEntBuilder.class)//
            .setId(nodeSpec.factory().id())//
            .setName(nodeSpec.metadata().nodeName())//
            .setType(NativeNodeInvariantsEnt.TypeEnum.CONFIGURATION)//
            .build();
    }

    private static NodeRepository.Node mockNodeWithMetadata(final String categoryPath) {
        return mockNodeWithMetadata(categoryPath, someVendor());
    }

    private static NodeRepository.Node mockNodeWithMetadata(final String categoryPath,
        final NodeSpec.Metadata.Vendor vendor) {
        var nameAndFactoryId = "some node";
        var node = mock(NodeRepository.Node.class, RETURNS_DEEP_STUBS);
        var nodeSpec = mock(NodeSpec.class);
        when(node.nodeSpec()).thenReturn(nodeSpec);
        var factory = mock(NodeSpec.Factory.class);
        when(factory.id()).thenReturn(nameAndFactoryId);
        when(nodeSpec.factory()).thenReturn(factory);
        when(nodeSpec.metadata()).thenReturn( //
            new NodeSpec.Metadata( //
                vendor, //
                nameAndFactoryId, //
                NodeFactory.NodeType.Configuration, //
                categoryPath, //
                null, // afterId
                List.of(), // keywords
                List.of() // tags
            ) //
        );
        return node;
    }

    private static NodeSpec.Metadata.Vendor knimeVendor() {
        return new NodeSpec.Metadata.Vendor( //
            new VendorDefBuilder().setSymbolicName("org.knime.featureVendor").build(), //
            new VendorDefBuilder().setSymbolicName("org.knime.bundleVendor").build() //
        );
    }

    private static NodeSpec.Metadata.Vendor someVendor() {
        return new NodeSpec.Metadata.Vendor( //
            new VendorDefBuilder().setSymbolicName(SOME_FEATURE_VENDOR).build(), //
            new VendorDefBuilder().setSymbolicName(SOME_BUNDLE_VENDOR).build() //
        );
    }

    @Test
    public void testGetRootCategory() {
        Supplier<Map<String, CategoryExtension>> categoryExtensions = () -> Map.of( //
            "/parent/", CategoryExtension.builder("cat0", "cat1").withPath("parent").build() //
        );
        // category will only be present in hierarchy if a node has been put into it
        List<NodeRepository.Node> nodes = List.of(mockNodeWithMetadata("/parent/cat0"));
        var returnedCategory =
            new NodeCategories(nodes, categoryExtensions).getCategoryEnt(List.of(), m_mapNodesToEnts);
        assertThat("The returned category should contain some child categories",
            !returnedCategory.getChildCategories().isEmpty());
    }

    @Test
    public void testExistingCategoryIsReturned() {
        Supplier<Map<String, CategoryExtension>> categoryExtensions = () -> Map.of( //
            "/parent/cat1", CategoryExtension.builder("cat1", "cat1").withPath("parent").build() //
        );
        // category will only be present in hierarchy if a node has been put into it
        var nodes = List.of( //
            mockNodeWithMetadata("/parent"), //
            mockNodeWithMetadata("/parent/cat1"));
        var categoryEnt =
            new NodeCategories(nodes, categoryExtensions).getCategoryEnt(List.of("parent"), m_mapNodesToEnts);
        assertHasNodes(categoryEnt);
        assertThat("The returned category should contain some child categories",
            !categoryEnt.getChildCategories().isEmpty());
    }

    private static void assertHasNodes(final NodeCategoryEnt categoryEnt) {
        assertThat("The returned category should contain some nodes", !categoryEnt.getNodes().isEmpty());
    }

    @Test
    public void testNonExistingCategoryThrows() {
        assertThrows(NoSuchElementException.class, () -> new NodeCategories(List.of(), Map::of)
            .getCategoryEnt(List.of("foo", "bar", "baz"), m_mapNodesToEnts));
    }

    @Test
    public void testCanInsertIntoLockedIfContributedByKNIME() {
        var categoryExtension = CategoryExtension.builder("cat1", "cat1").withLocked(true).build();
        var nodes = List.of(mockNodeWithMetadata("/cat1", knimeVendor()));
        var categoryEnt = new NodeCategories(nodes, () -> Map.of("/", categoryExtension)) //
            .getCategoryEnt(List.of("cat1"), m_mapNodesToEnts);
        assertHasNodes(categoryEnt);
    }

    @Test
    public void testCanInsertIntoLockedIfCompatible() {
        var categoryExtension =
            CategoryExtension.builder("cat1", "cat1").withPluginId(SOME_BUNDLE_VENDOR).withLocked(true).build();
        var nodes = List.of(mockNodeWithMetadata("/cat1", knimeVendor()));
        var categoryEnt = new NodeCategories(nodes, () -> Map.of("/", categoryExtension)) //
            .getCategoryEnt(List.of("cat1"), m_mapNodesToEnts);
        assertHasNodes(categoryEnt);
    }

    /**
     * Note that "can insert" checks are about where a child category can be attached to a parent category.
     */
    @Test
    public void testCanNotInsertIntoLockedElse() {
        var parent =
            CategoryExtension.builder("cat1", "cat1").withPluginId("org.baz.someOtherVendor").withLocked(true).build();
        var nodes = List.of(mockNodeWithMetadata("/parent/cat1", someVendor()));
        assertThrows( //
            Throwable.class, //
            () -> new NodeCategories(nodes, () -> Map.of("/", parent)) //
                .getCategoryEnt(List.of("cat1"), m_mapNodesToEnts) //
        );
    }

    /**
     * See NXT-2840
     */
    @Test
    public void testCanInsertCategoriesWithNoExplicitMetadata() {
        var nodes = List.of(mockNodeWithMetadata("/cat1", knimeVendor()));
        var categoryEnt = new NodeCategories(nodes, Map::of) //
            .getCategoryEnt(List.of("cat1"), m_mapNodesToEnts);
        assertHasNodes(categoryEnt);
    }

    /**
     * See NXT-3229
     */
    @Test
    public void testCanInsertIntoUnlockedWithIncompatibleVendor() {
        var categoryExtension =
            CategoryExtension.builder("cat1", "cat1").withPluginId("org.baz.someOtherVendor").withLocked(false).build();
        var nodes = List.of(mockNodeWithMetadata("/cat1", knimeVendor()));
        var categoryEnt = new NodeCategories(nodes, () -> Map.of("/", categoryExtension)) //
            .getCategoryEnt(List.of("cat1"), m_mapNodesToEnts);
        assertHasNodes(categoryEnt);
    }

}
