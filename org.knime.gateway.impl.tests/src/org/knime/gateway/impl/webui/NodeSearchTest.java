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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.knime.gateway.api.webui.entity.NodeSearchResultEnt;
import org.knime.gateway.api.webui.entity.NodeTemplateEnt;

/**
 * Tests {@link NodeSearch}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@SuppressWarnings("javadoc")
public class NodeSearchTest {

    private static NodeRepository repo;

    private static NodeRepository repoWithCollection;

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
    public void testSearchNodesAllNullParameters() {
        m_search.searchNodes(null, null, null, null, null, null, true);
        m_search.searchNodes(null, Collections.emptyList(), null, null, null, null, true);
        m_search.searchNodes(null, Arrays.asList("IO"), null, null, null, null, true);
        assertThat("unexpected cache size", m_search.cacheSize(), is(3));
    }

    @Test
    public void testSimpleSearchNodesWithOffsetAndLimitAndMinimumInfo() {
        NodeSearchResultEnt res = m_search.searchNodes("Column Filter", null, null, 10, 10, null, true);
        assertThat("unexpected number of nodes", res.getNodes().size(), is(10));
        assertThat("column filter not expected to be the first node (offset)", res.getNodes().get(0).getName(),
            is(not("Column Filter")));
        assertThat("total num nodes must be larger then num of returned nodes", res.getTotalNumNodes(),
            greaterThan(res.getNodes().size()));
        assertThat("tags are given", res.getTags(), is(not(empty())));
        assertThat("no icon property expected", res.getNodes().get(0).getIcon(), is(nullValue()));
        assertThat("no in-port property expected", res.getNodes().get(0).getInPorts(), is(nullValue()));
        assertThat("no out-port property expected", res.getNodes().get(0).getInPorts(), is(nullValue()));

        NodeSearchResultEnt res2 = m_search.searchNodes("Column Filter", null, null, 11, 9, null, true);
        assertThat("unexpected number of nodes", res2.getNodes().size(), is(9));
        assertThat("unexpected node", res.getNodes().get(1), is(res2.getNodes().get(0)));

        assertThat("unexpected cache size", m_search.cacheSize(), is(1));
    }

    @Test
    public void testSearchNodesAndGetFullTemplateInfo() {
        NodeSearchResultEnt res = m_search.searchNodes("col", null, null, 0, 2, Boolean.TRUE, true);
        assertThat("icon property expected", res.getNodes().get(0).getIcon(), is(notNullValue()));
        assertThat("in-port property expected", res.getNodes().get(0).getInPorts(), is(not((empty()))));
        assertThat("out-port property expected", res.getNodes().get(0).getOutPorts(), is(not((empty()))));
    }

    @Test
    public void testSearchNodesAnyOrAllTagsMatch() {
        NodeSearchResultEnt resAll =
            m_search.searchNodes("er", asList("IO", "Read"), Boolean.TRUE, 0, 1, Boolean.FALSE, true);
        NodeSearchResultEnt resAny =
            m_search.searchNodes("er", asList("IO", "Read"), Boolean.FALSE, 0, 1, Boolean.FALSE, true);
        assertThat("any match expected to hold more found nodes than all match", resAny.getTotalNumNodes(),
            is(greaterThan(resAll.getTotalNumNodes())));

        assertThat("unexpected cache size", m_search.cacheSize(), is(2));
    }

    @Test
    public void testSearchNodesEasterEggs() {
        NodeSearchResultEnt res =
            m_search.searchNodes("test//hidden", Collections.emptyList(), null, 0, 10, false, true);
        assertThat("some hidden nodes are expected to be found", res.getTotalNumNodes(), is(greaterThan(0)));

        NodeSearchResultEnt res2 =
            m_search.searchNodes("filter//deprecated", Collections.emptyList(), null, 0, 10, false, true);
        assertThat("some deprecated nodes are expected to be found", res2.getTotalNumNodes(), is(greaterThan(0)));
        assertThat("deprecated string expected in node name", res2.getNodes().get(0).getName(),
            containsString("deprecated"));
    }

    @Test
    public void testSearchCollectionNodes() {
        var res = m_searchWithCollection.searchNodes("table", Collections.emptyList(), null, 0, 10, true, true);
        var resFactories = getNodeFactoryNames(res.getNodes());
        assertThat("should return some nodes in the active collection", resFactories.size(), is(greaterThan(0)));
        assertThat("should only contain nodes from the collection",
            resFactories.stream().allMatch(NodeRepositoryTestingUtil.COLLECTION_NODES::contains), is(true));

        var resAdditional = m_searchWithCollection.searchNodes("table", Collections.emptyList(), null, 0, 10, true, false);
        var additionalFactories = getNodeFactoryNames(resAdditional.getNodes());
        assertThat("should return some additional nodes", additionalFactories.size(), is(greaterThan(0)));
        assertThat("addtitional nodes should not be in the collection",
            additionalFactories.stream().noneMatch(NodeRepositoryTestingUtil.COLLECTION_NODES::contains), is(true));
    }

    @Test
    public void testSearchTagsCollectionNodes() {
        var res = m_searchWithCollection.searchNodes("", asList("Manipulation"), null, 0, 10, true, true);
        var resFactories = getNodeFactoryNames(res.getNodes());
        assertThat("should return some nodes in the active collection", resFactories.size(), is(greaterThan(0)));
        assertThat("should only contain nodes from the collection",
            resFactories.stream().allMatch(NodeRepositoryTestingUtil.COLLECTION_NODES::contains), is(true));

        var resAdditional = m_searchWithCollection.searchNodes("", asList("Manipulation"), null, 0, 10, true, false);
        var additionalFactories = getNodeFactoryNames(resAdditional.getNodes());
        assertThat("should return some additional nodes", additionalFactories.size(), is(greaterThan(0)));
        assertThat("addtitional nodes should not be in the collection",
            additionalFactories.stream().noneMatch(NodeRepositoryTestingUtil.COLLECTION_NODES::contains), is(true));
    }

    private static List<String> getNodeFactoryNames(final List<NodeTemplateEnt> nodes) {
        return nodes.stream() //
            .map(n -> n.getNodeFactory().getClassName()) //
            .collect(Collectors.toList());
    }
}
