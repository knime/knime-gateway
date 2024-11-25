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
 *   Jul 21, 2022 (hornm): created
 */
package org.knime.gateway.impl.node.port;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.port.flowvariable.FlowVariablePortObject;
import org.knime.core.node.workflow.FlowObjectStack;
import org.knime.core.node.workflow.FlowVariable;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.NodeOutPort;
import org.knime.core.node.workflow.UnsupportedWorkflowVersionException;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.util.LockFailedException;
import org.knime.core.webui.data.InitialDataService;
import org.knime.core.webui.node.port.PortContext;
import org.knime.core.webui.node.port.PortView;
import org.knime.gateway.api.util.CoreUtil;
import org.knime.testing.util.WorkflowManagerUtil;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Tests {@link FlowVariablePortViewFactory}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@SuppressWarnings("restriction")
public class FlowVariablePortViewFactoryTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private WorkflowManager m_wfm;

    @Before
    public void loadWorkflow() throws IOException, InvalidSettingsException, CanceledExecutionException,
        UnsupportedWorkflowVersionException, LockFailedException {
        var workflowDir =
            CoreUtil.resolveToFile("/files/workflows/flow_variables", FlowVariablePortViewFactoryTest.class).toPath();
        m_wfm = WorkflowManagerUtil.loadWorkflowInWorkspace(workflowDir, workflowDir.getParent());
    }

    @After
    public void disposeWorkflow() {
        WorkflowManagerUtil.disposeWorkflow(m_wfm);
    }

    /**
     * Asserts that the correct page is returned by the {@link PortView} created by the
     * {@link FlowVariablePortViewFactory}
     */
    @Test
    public void testFlowVariablePortViewPage() {
        PortView portView;
        PortContext.pushContext(getNodeOutPort());
        try {
            portView = new FlowVariablePortViewFactory().createPortView(FlowVariablePortObject.INSTANCE);
        } finally {
            PortContext.removeLastContext();
        }
        var page = portView.getPage();
        assertThat(page.getContentType().toString(), is("SHADOW_APP"));
        var pageId = page.getPageIdForReusablePage().orElse(null);
        assertThat(pageId, is("flowvariableview"));
    }

    /**
     * Tests the {@link InitialDataService} of the {@link PortView} created by the {@link FlowVariablePortViewFactory}.
     *
     * @throws JsonProcessingException
     */
    @Test
    public void testFlowVariablePortViewData() throws JsonProcessingException {
        PortView portView;
        var outPort = getNodeOutPort();
        PortContext.pushContext(outPort);
        try {
            portView = new FlowVariablePortViewFactory().createPortView(FlowVariablePortObject.INSTANCE);

            // initial data
            var initialData = ((InitialDataService)portView.createInitialDataService().get()).getInitialData();
            var jsonNode = MAPPER.readTree(initialData);
            var res = jsonNode.get("result");
            var table = res.get("table");
            assertThat(table.get("rows").size(), is(0)); // 0 because the actual row data needs to be requested via the rpc data service
            assertThat(table.get("rowCount").asInt(), is(3));
            assertThat(table.get("displayedColumns").get(0).textValue(), is("Owner ID"));
            assertThat(table.get("displayedColumns").get(1).textValue(), is("Data Type"));
            assertThat(table.get("displayedColumns").get(2).textValue(), is("Variable Name"));
            assertThat(table.get("displayedColumns").get(3).textValue(), is("Value"));

            // table data
            var rows = MAPPER
                .readTree(portView.createRpcDataService().get().handleRpcRequest(
                    """
                            {"jsonrpc":"2.0","method":"getTable","params":[["Owner ID","Data Type","Variable Name","Value"],0,3,[null,null,null,null],false,false,true,false],"id":1}
                            """))
                .get("result").get("rows");
            assertThat(rows.get(0).get(4).textValue(), is("variable_1")); // name
            assertThat(rows.get(1).get(4).textValue(), is("variable_2")); // name
            assertThat(rows.get(0).get(2).textValue(), endsWith(":1")); // ownerNodeId
            assertThat(rows.get(0).get(3).textValue(), is("DoubleType")); // type
            assertThat(rows.get(1).get(3).textValue(), is("StringType")); // type
            assertThat(rows.get(0).get(5).textValue(), is("NaN")); // value
            assertThat(rows.get(1).get(5).textValue(), is("foobar")); // value
        } finally {
            PortContext.removeLastContext();
        }

    }

    private NodeOutPort getNodeOutPort() {
        var nc = m_wfm.getNodeContainers().iterator().next();
        return nc.getOutPort(0);
    }

    private static FlowObjectStack createTestFlowObjectStack() {
        List<FlowVariable> flowVars =
            Arrays.asList(new FlowVariable("test1", Double.NaN), new FlowVariable("test2", "foobar"));
        return FlowObjectStack.createFromFlowVariableList(flowVars, new NodeID(4));
    }

}
