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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.knime.gateway.api.webui.entity.NodeSelectionsEnt;
import org.knime.gateway.api.webui.entity.NodeTemplateEnt;

/**
 * Tests {@link NodeSelection}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@SuppressWarnings("javadoc")
public class NodeSelectionTest {

    private static NodeRepository repo;

    private NodeSelection m_select;

    @BeforeClass
    public static void initRepo() {
        repo = new NodeRepository();
    }

    @Before
    public void initNodeSelection() {
        m_select = new NodeSelection(repo);
    }

    @Test
    public void testSelectNodesMinimalTemplateInfo() {
        NodeSelectionsEnt res = m_select.selectNodes(6, 1, 2, Boolean.FALSE);
        assertThat("unexpected number of selections", res.getSelections().size(), is(2));
        assertThat("unexpected 2nd selection", res.getSelections().get(0).getTag(), is("Manipulation"));
        assertThat("unexpected number of nodes per tag", res.getSelections().get(0).getNodes().size(), is(6));
        NodeTemplateEnt node = res.getSelections().get(0).getNodes().get(0);
        assertThat("name expected", node.getName(), is(notNullValue()));
        assertThat("no icon expected", node.getIcon(), is(nullValue()));
        assertThat("no in-ports expected", node.getInPorts(), is(nullValue()));
        assertThat("no out-ports expected", node.getOutPorts(), is(nullValue()));
    }

    @Test
    public void testSelectNodesFullTemplateInfo() {
        NodeSelectionsEnt res = m_select.selectNodes(6, 2, 3, Boolean.TRUE);
        assertThat("unexpected number of selections", res.getSelections().size(), is(3));
        assertThat("unexpected 3rd selection", res.getSelections().get(0).getTag(), is("Views"));
        assertThat("unexpected number of nodes per tag", res.getSelections().get(0).getNodes().size(), is(6));
        NodeTemplateEnt node = res.getSelections().get(0).getNodes().get(0);
        assertThat("icon expected", node.getIcon(), is(notNullValue()));
        assertThat("in-ports expected", node.getInPorts(), is(notNullValue()));
        assertThat("out-ports expected", node.getOutPorts(), is(notNullValue()));
    }

    @Test
    public void testSelectNodesNoEmptySelections() {
        NodeSelectionsEnt res = m_select.selectNodes(1, 0, null, Boolean.FALSE);
        int numTotalNodes = (int)res.getSelections().stream().mapToInt(s -> s.getNodes().size()).count();
        assertThat("unexpected number of total nodes", res.getTotalNumSelections(), is(numTotalNodes));
    }

}
