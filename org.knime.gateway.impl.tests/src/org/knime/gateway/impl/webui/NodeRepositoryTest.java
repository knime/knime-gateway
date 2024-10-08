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
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.knime.core.customization.APCustomization;
import org.knime.core.node.BufferedDataTable;
import org.knime.gateway.api.webui.entity.NativeNodeInvariantsEnt;
import org.knime.gateway.api.webui.entity.NodeGroupsEnt;
import org.knime.gateway.api.webui.entity.NodePortTemplateEnt;
import org.knime.gateway.api.webui.entity.NodeSearchResultEnt;
import org.knime.gateway.api.webui.entity.NodeTemplateEnt;
import org.knime.gateway.impl.APCustomizationInjection;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * Tests {@link NodeRepository}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@SuppressWarnings("javadoc")
public class NodeRepositoryTest {

    private static NodeRepository repo;

    private static NodeRepository repoWithFilter;

    @BeforeClass
    public static void initNodeRepo() {
        repo = NodeRepositoryTestingUtil.createNodeRepository();
        repoWithFilter = NodeRepositoryTestingUtil.createNodeRepositoryWithFilter();
    }

    @Test
    public void testGetNodeTemplates() throws Exception {
        NodeSearch search = new NodeSearch(repo);
        NodeSearchResultEnt res =
            search.searchNodes("Column Filter", asList("Manipulation"), null, 0, 1, Boolean.TRUE, null, null);

        NodeTemplateEnt nodeFromSearch = res.getNodes().get(0);
        List<String> ids = asList(nodeFromSearch.getId());
        Map<String, NodeTemplateEnt> templates = repo.getNodeTemplates(ids, true);
        NodeTemplateEnt nodeFromRepo = templates.get(ids.get(0));
        assertThat("templates not equal", nodeFromRepo, is(nodeFromSearch));
        assertThat("unexpected name", nodeFromRepo.getName(), is("Column Filter"));
        assertThat(nodeFromRepo.getExtension().getName(), is("KNIME Base Nodes"));
        assertThat(nodeFromRepo.getExtension().getVendor().getName(), is("KNIME AG, Zurich, Switzerland"));
        assertThat(nodeFromRepo.getExtension().getVendor().isKNIME(), is(true));
        NodePortTemplateEnt nodeInPort = nodeFromRepo.getInPorts().get(0);
        assertThat("optional inport", nodeInPort.isOptional(), is(false));
        assertThat("no port name expected", nodeInPort.getName(), is(nullValue()));
        assertThat("wrong port type id", nodeInPort.getTypeId(), is((BufferedDataTable.class).getName()));
        assertThat(nodeFromRepo.getId(), is(nodeFromSearch.getId()));
        assertThat(nodeFromRepo.getType(), is(NativeNodeInvariantsEnt.TypeEnum.MANIPULATOR));
        assertThat(nodeFromRepo.isComponent(), is(false));
        assertThat("icon property expected", nodeFromSearch.getIcon(), is(notNullValue()));
        assertThat("in-port property expected", nodeFromSearch.getInPorts(), is(not((empty()))));
        assertThat("out-port property expected", nodeFromSearch.getOutPorts(), is(not((empty()))));
        assertThat("factory class expected", nodeFromSearch.getNodeFactory().getClassName(),
            is("org.knime.base.node.preproc.filter.column.DataColumnSpecFilterNodeFactory"));
        assertThat("no factory settings expected", nodeFromSearch.getNodeFactory().getSettings(), is(nullValue()));

        // dynamic node
        res = search.searchNodes("Bar Chart (Java Script)", asList("Views"), null, 0, 1, Boolean.TRUE, null, null);
        nodeFromSearch = res.getNodes().get(0);
        nodeFromRepo = repo.getNodeTemplate(nodeFromSearch.getId(), true);
        assertThat("templates not equal", nodeFromRepo, is(nodeFromSearch));
        assertThat("unexpected name", nodeFromRepo.getName(), is("Bar Chart (JavaScript)"));
        assertThat(nodeFromRepo.getExtension().getVendor().getName(), is("KNIME AG, Zurich, Switzerland"));
        assertThat(nodeFromRepo.getExtension().getVendor().isKNIME(), is(true));
        assertThat(nodeFromRepo.getId(), is(nodeFromSearch.getId()));
        assertThat(nodeFromRepo.getType(), is(NativeNodeInvariantsEnt.TypeEnum.VISUALIZER));
        assertThat(nodeFromRepo.isComponent(), is(false));
        assertThat("icon property expected", nodeFromSearch.getIcon(), is(notNullValue()));
        assertThat("in-port property expected", nodeFromSearch.getInPorts(), is(not((empty()))));
        assertThat("out-port property expected", nodeFromSearch.getOutPorts(), is(not((empty()))));
        assertThat("factory class expected", nodeFromSearch.getNodeFactory().getClassName(),
            is("org.knime.dynamic.js.v30.DynamicJSNodeFactory"));
        assertThat("factory settings expected", nodeFromSearch.getNodeFactory().getSettings(), is(
            "{\"name\":\"settings\",\"value\":{\"nodeDir\":{\"type\":\"string\",\"value\":\"org.knime.dynamic.js.base:nodes/:barChart\"}}}"));
    }

    @Test
    public void testGetHiddenOrDeprecatedNodeTemplates() throws Exception {
        NodeSearch search = new NodeSearch(repo);

        NodeTemplateEnt nodeFromSearch =
            search.searchNodes("//hidden", null, null, 0, 1, Boolean.TRUE, null, null).getNodes().get(0);
        NodeTemplateEnt nodeFromRepo =
            repo.getNodeTemplates(asList(nodeFromSearch.getId()), true).get(nodeFromSearch.getId());
        assertThat(nodeFromRepo, is(nodeFromSearch));

        nodeFromSearch = search.searchNodes("//deprecated", null, null, 0, 1, Boolean.TRUE, null, null).getNodes().get(0);
        nodeFromRepo = repo.getNodeTemplates(asList(nodeFromSearch.getId()), true).get(nodeFromSearch.getId());
        assertThat(nodeFromRepo, is(nodeFromSearch));
        assertThat(nodeFromRepo.getName(), containsString("deprecated"));
    }

    /**
     * Do a node search and a node selection without any parameter (i.e. all null). In both cases all nodes are expected
     * to be returned.
     */
    @Test
    public void testSelectNodesAndNodeSearchNullParameters() throws Exception {
        NodeGroups select = new NodeGroups(repo);
        NodeSearch search = new NodeSearch(repo);

        NodeGroupsEnt groupsRes = select.getNodesGroupedByTags(null, null, null, null);
        NodeSearchResultEnt searchRes = search.searchNodes(null, null, null, null, null, null, null, null);

        Map<String, NodeTemplateEnt> overplus = new HashMap<>();
        for (NodeTemplateEnt n1 : groupsRes.getGroups().stream().flatMap(s -> s.getNodes().stream())
            .collect(Collectors.toList())) {
            if (overplus.containsKey(n1.getId() + "_search")) {
                overplus.remove(n1.getId() + "_search");
            } else {
                overplus.put(n1.getId() + "_select", n1);
                for (NodeTemplateEnt n2 : searchRes.getNodes()) {
                    if (overplus.containsKey(n2.getId() + "_select")) {
                        overplus.remove(n2.getId() + "_select");
                    } else {
                        overplus.put(n2.getId() + "_search", n2);
                    }
                }
            }
        }
        if (!overplus.isEmpty()) {
            StringBuilder sb = new StringBuilder(
                "Same select- and search-results expected if selection and search are not constrained!\n");
            sb.append("But there are differences:\n");
            for (Entry<String, NodeTemplateEnt> entry : overplus.entrySet()) {
                sb.append("NODE ");
                sb.append(entry.getKey());
                sb.append(";name: ");
                sb.append(entry.getValue().getName());
                sb.append(";path: ");
                sb.append(repo.getNodes().stream()
                    .filter(node -> node.templateId.equals(entry.getValue().getId()))
                    .map(node -> node.nodeSpec.metadata().categoryPath()).findFirst().orElse(null));
                sb.append("\n");
            }
            Assert.fail(sb.toString());
        }
    }

    @Test
    public void testFilteredNodeRepository() {
        assertThat("repo without filter should have no additional nodes", repo.getFilteredNodes(), empty());
        assertThat("repo with filter should have additional nodes", repoWithFilter.getFilteredNodes(),
            not(empty()));

        var nodes =
            repoWithFilter.getNodes().stream().map(n -> n.templateId).collect(Collectors.toList());
        var additionalNodes =
            repoWithFilter.getFilteredNodes().stream().map(n -> n.templateId).collect(Collectors.toList());
        assertThat("nodes and additional nodes should be all nodes", repo.getNodes().stream()
            .allMatch(n -> nodes.contains(n.templateId) || additionalNodes.contains(n.templateId)));
        assertThat("nodes and additional nodes should be disjoint",
            nodes.stream().noneMatch(additionalNodes::contains));
    }

    /**
     * Tests that {@link APCustomization}s for nodes are respected by the node repository.
     *
     * @throws JsonProcessingException
     * @throws JsonMappingException
     */
    @Test
    public void testNodeCustomizations() throws JsonMappingException, JsonProcessingException {
        String customizationYaml = """
                  version: 'customization-v0.1'
                  nodes:
                      filter:
                      - scope: use
                        rule: allow
                        predicate:
                          type: pattern
                          patterns:
                            - org.knime.base.node.preproc.append.row.AppendedRowsNodeFactory
                            - org.knime.base.node.preproc.normalize3.Normalizer3NodeFactory
                          isRegex: false
                """;
        try (var injected = new APCustomizationInjection(customizationYaml)) {
            var nodes = new NodeRepository().getNodes().stream().map(n -> n.templateId).toList();
            assertThat(nodes,
                Matchers.containsInAnyOrder("org.knime.base.node.preproc.append.row.AppendedRowsNodeFactory",
                    "org.knime.base.node.preproc.normalize3.Normalizer3NodeFactory"));

            assertThat(NodeRepository.isNodeUsageForbidden(
                "org.knime.base.node.preproc.autobinner.apply.AutoBinnerApplyNodeFactory"), is(true));
            assertThat(
                NodeRepository.isNodeUsageForbidden("org.knime.base.node.preproc.append.row.AppendedRowsNodeFactory"),
                is(false));
            assertThat(
                NodeRepository.isNodeUsageForbidden("org.knime.base.node.preproc.normalize3.Normalizer3NodeFactory"),
                is(false));
        }
    }
}
