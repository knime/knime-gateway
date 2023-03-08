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
 *   Mar 24, 2021 (hornm): created
 */
package org.knime.gateway.impl.webui;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThrows;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.port.image.ImagePortObject;
import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.api.webui.entity.NodeSearchResultEnt;
import org.knime.gateway.api.webui.entity.NodeTemplateEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.InvalidRequestException;
import org.knime.gateway.impl.webui.NodeSearch.NodePartition;

/**
 * Tests {@link NodeSearch}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@SuppressWarnings("javadoc")
public class NodeSearchTest {

    private static NodeRepository repo;

    private static NodeRepository repoWithCollection;

    private static final String TABLE_PORT_TYPE_ID = CoreUtil.getPortTypeId(BufferedDataTable.TYPE);

    private static final String IMAGE_PORT_TYPE_ID = CoreUtil.getPortTypeId(ImagePortObject.TYPE);

    private NodeSearch m_search;

    private NodeSearch m_searchWithCollection;

    @BeforeClass
    public static void initRepo() {
        repo = NodeRepositoryTestingUtil.createNodeRepository();
        repoWithCollection = NodeRepositoryTestingUtil.createNodeRepositoryWithCollection();
    }

    @Before
    public void initNodeSearch() {
        m_search = new NodeSearch(repo);
        m_searchWithCollection = new NodeSearch(repoWithCollection);
    }

    @Test
    public void testSearchNodesAllNullParameters() throws Exception {
        m_search.searchNodes(null, null, null, null, null, null, null, null);
        m_search.searchNodes(null, null, null, null, null, null, null, ""); // Identical to first one
        m_search.searchNodes(null, Collections.emptyList(), null, null, null, null, null, null); // Identical to first one
        m_search.searchNodes(null, null, false, null, null, null, null, null); // Identical to first one
        m_search.searchNodes(null, Arrays.asList("IO"), null, null, null, null, null, null);
        assertThat("unexpected cache size", m_search.cacheSize(), is(2));
    }

    @Test
    public void testSimpleSearchNodesWithOffsetAndLimitAndMinimumInfo() throws Exception {
        NodeSearchResultEnt res = m_search.searchNodes("Column Filter", null, null, 10, 10, null, null, null);
        assertThat("unexpected number of nodes", res.getNodes().size(), is(10));
        assertThat("column filter not expected to be the first node (offset)", res.getNodes().get(0).getName(),
            is(not("Column Filter")));
        assertThat("total num nodes must be larger then num of returned nodes", res.getTotalNumNodes(),
            greaterThan(res.getNodes().size()));
        assertThat("tags are given", res.getTags(), is(not(empty())));
        assertThat("no icon property expected", res.getNodes().get(0).getIcon(), is(nullValue()));
        assertThat("no in-port property expected", res.getNodes().get(0).getInPorts(), is(nullValue()));
        assertThat("no out-port property expected", res.getNodes().get(0).getInPorts(), is(nullValue()));

        NodeSearchResultEnt res2 = m_search.searchNodes("Column Filter", null, null, 11, 9, null, null, null);
        assertThat("unexpected number of nodes", res2.getNodes().size(), is(9));
        assertThat("unexpected node", res.getNodes().get(1), is(res2.getNodes().get(0)));

        assertThat("unexpected cache size", m_search.cacheSize(), is(1));
    }

    @Test
    public void testSearchNodesAndGetFullTemplateInfo() throws Exception {
        NodeSearchResultEnt res = m_search.searchNodes("col", null, null, 0, 2, Boolean.TRUE, null, null);
        assertThat("icon property expected", res.getNodes().get(0).getIcon(), is(notNullValue()));
        assertThat("in-port property expected", res.getNodes().get(0).getInPorts(), is(not((empty()))));
        assertThat("out-port property expected", res.getNodes().get(0).getOutPorts(), is(not((empty()))));
    }

    @Test
    public void testSearchNodesAnyOrAllTagsMatch() throws Exception {
        NodeSearchResultEnt resAll =
            m_search.searchNodes("er", asList("IO", "Read"), Boolean.TRUE, 0, 1, Boolean.FALSE, null, null);
        NodeSearchResultEnt resAny =
            m_search.searchNodes("er", asList("IO", "Read"), Boolean.FALSE, 0, 1, Boolean.FALSE, null, null);
        assertThat("any match expected to hold more found nodes than all match", resAny.getTotalNumNodes(),
            is(greaterThan(resAll.getTotalNumNodes())));

        assertThat("unexpected cache size", m_search.cacheSize(), is(2));
    }

    @Test
    public void testSearchNodesEasterEggs() throws Exception {
        NodeSearchResultEnt res =
            m_search.searchNodes("test//hidden", Collections.emptyList(), null, 0, 10, false, null, null);
        assertThat("some hidden nodes are expected to be found", res.getTotalNumNodes(), is(greaterThan(0)));

        NodeSearchResultEnt res2 =
            m_search.searchNodes("filter//deprecated", Collections.emptyList(), null, 0, 10, false, null, null);
        assertThat("some deprecated nodes are expected to be found", res2.getTotalNumNodes(), is(greaterThan(0)));
        assertThat("deprecated string expected in node name", res2.getNodes().get(0).getName(),
            containsString("deprecated"));
    }

    @Test
    public void testSearchCollectionNodes() throws Exception {
        var res = m_searchWithCollection.searchNodes("table", Collections.emptyList(), null, 0, 10, true,
            NodePartition.IN_COLLECTION.toString(), null);
        var resFactories = getNodeFactoryNames(res.getNodes());
        assertThat("should return some nodes in the active collection", resFactories.size(), is(greaterThan(0)));
        assertThat("should only contain nodes from the collection",
            resFactories.stream().allMatch(NodeRepositoryTestingUtil.COLLECTION_NODES::contains), is(true));

        var resAdditional = m_searchWithCollection.searchNodes("table", Collections.emptyList(), null, 0, 10, true,
            NodePartition.NOT_IN_COLLECTION.toString(), null);
        var additionalFactories = getNodeFactoryNames(resAdditional.getNodes());
        assertThat("should return some additional nodes", additionalFactories.size(), is(greaterThan(0)));
        assertThat("addtitional nodes should not be in the collection",
            additionalFactories.stream().noneMatch(NodeRepositoryTestingUtil.COLLECTION_NODES::contains), is(true));
    }

    @Test
    public void testSearchCollectionNodesVerifyResultSets() throws Exception {
        var resInCollection = m_searchWithCollection.searchNodes("table", Collections.emptyList(), null, null, null,
            false, NodePartition.IN_COLLECTION.toString(), TABLE_PORT_TYPE_ID);
        assertThat("IN_COLLECTION result should not be empty", resInCollection.getTotalNumNodes(), is(greaterThan(0)));

        var resNotInCollection = m_searchWithCollection.searchNodes("table", Collections.emptyList(), null, null, null,
            false, NodePartition.NOT_IN_COLLECTION.toString(), TABLE_PORT_TYPE_ID);
        assertThat("NOT_IN_COLLECTION result should not be empty", resNotInCollection.getTotalNumNodes(),
            is(greaterThan(0)));
        assertThat("IN_COLLECTION and NOT_IN_COLLECTION are disjunct",
            CollectionUtils.intersection(resInCollection.getNodes(), resNotInCollection.getNodes()), is(empty()));

        var resAll = m_searchWithCollection.searchNodes("table", Collections.emptyList(), null, null, null, false,
            NodePartition.ALL.toString(), TABLE_PORT_TYPE_ID);
        assertThat("ALL result cardinality should be the sum of the other two", resAll.getTotalNumNodes(),
            is(resInCollection.getTotalNumNodes() + resNotInCollection.getTotalNumNodes()));
        assertThat("ALL result should be the union of the other two",
            CollectionUtils.isEqualCollection(
                CollectionUtils.union(resInCollection.getNodes(), resNotInCollection.getNodes()), resAll.getNodes()),
            is(true));
    }

    @Test
    public void testSearchNodesVerifyResultSets() throws Exception {
        var resInCollection = m_search.searchNodes("table", Collections.emptyList(), null, null, null, false,
            NodePartition.IN_COLLECTION.toString(), TABLE_PORT_TYPE_ID);
        assertThat("IN_COLLECTION result should not be empty", resInCollection.getTotalNumNodes(), is(greaterThan(0)));

        var resNotInCollection = m_search.searchNodes("table", Collections.emptyList(), null, null, null, false,
            NodePartition.NOT_IN_COLLECTION.toString(), TABLE_PORT_TYPE_ID);
        assertThat("NOT_IN_COLLECTION result should be empty", resNotInCollection.getTotalNumNodes(), is(0));
        assertThat("IN_COLLECTION and NOT_IN_COLLECTION are disjunct",
            CollectionUtils.intersection(resInCollection.getNodes(), resNotInCollection.getNodes()), is(empty()));

        var resAll = m_searchWithCollection.searchNodes("table", Collections.emptyList(), null, null, null, false,
            NodePartition.ALL.toString(), TABLE_PORT_TYPE_ID);
        assertThat("ALL result cardinality should be the same as the first one", resAll.getTotalNumNodes(),
            is(resInCollection.getTotalNumNodes()));
        assertThat("ALL result should be same as the first one",
            CollectionUtils.isEqualCollection(resInCollection.getNodes(), resAll.getNodes()), is(true));
    }

    @Test
    public void testSearchTagsCollectionNodes() throws Exception {
        var res =
            m_searchWithCollection.searchNodes("", asList("Manipulation"), null, 0, 10, true, "IN_COLLECTION", null);
        var resFactories = getNodeFactoryNames(res.getNodes());
        assertThat("should return some nodes in the active collection", resFactories.size(), is(greaterThan(0)));
        assertThat("should only contain nodes from the collection",
            resFactories.stream().allMatch(NodeRepositoryTestingUtil.COLLECTION_NODES::contains), is(true));

        var resAdditional = m_searchWithCollection.searchNodes("", asList("Manipulation"), null, 0, 10, true,
            "NOT_IN_COLLECTION", null);
        var additionalFactories = getNodeFactoryNames(resAdditional.getNodes());
        assertThat("should return some additional nodes", additionalFactories.size(), is(greaterThan(0)));
        assertThat("addtitional nodes should not be in the collection",
            additionalFactories.stream().noneMatch(NodeRepositoryTestingUtil.COLLECTION_NODES::contains), is(true));
    }

    @Test
    public void testSearchNodesException() {
        assertThrows("Not a valid port type set", InvalidRequestException.class,
            () -> m_search.searchNodes(null, null, null, null, null, null, null, "porttype.id.does.not.exist"));
    }

    @Test
    public void testSearchNodesDifferentPortTypes() throws Exception {
        var res1 = m_search.searchNodes(null, null, null, null, null, null, null, null);
        var res2 = m_search.searchNodes(TABLE_PORT_TYPE_ID, null, null, null, null, null, null, null);
        assertThat("There should be less table port compatible nodes than all nodes", res1.getTotalNumNodes(),
            is(greaterThan(res2.getTotalNumNodes())));
        var res3 = m_search.searchNodes(IMAGE_PORT_TYPE_ID, null, null, null, null, null, null, null);
        assertThat("There should be less image port compatible nodes than all nodes", res1.getTotalNumNodes(),
            is(greaterThan(res3.getTotalNumNodes())));
        assertThat("The set of image port compatible nodes must differ from the set of table port compatible nodes",
            CollectionUtils.isEqualCollection(res2.getNodes(), res3.getNodes()), is(false));
    }

    private static List<String> getNodeFactoryNames(final List<NodeTemplateEnt> nodes) {
        return nodes.stream() //
            .map(n -> n.getNodeFactory().getClassName()) //
            .collect(Collectors.toList());
    }

}
