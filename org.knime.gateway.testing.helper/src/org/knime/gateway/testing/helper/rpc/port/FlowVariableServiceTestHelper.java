/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.com; Email: contact@knime.com
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
 */
package org.knime.gateway.testing.helper.rpc.port;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.knime.core.node.workflow.FlowObjectStack;
import org.knime.core.node.workflow.FlowVariable;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.NodeOutPort;
import org.knime.core.node.workflow.SingleNodeContainer;
import org.knime.gateway.impl.rpc.flowvars.DefaultFlowVariableService;
import org.knime.gateway.impl.rpc.flowvars.FlowVariableService;
import org.knime.gateway.impl.rpc.table.TableService;

/**
 * Tests expected behavior of {@link FlowVariableService}-methods.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class FlowVariableServiceTestHelper {

    private Function<NodeOutPort, FlowVariableService> m_serviceCreator;

    /**
     * Creates a new table service test helper.
     *
     * @param serviceCreator provider for the {@link TableService} implementation
     */
    public FlowVariableServiceTestHelper(final Function<NodeOutPort, FlowVariableService> serviceCreator) {
        m_serviceCreator = serviceCreator;
    }

    /**
     * Basic service test.
     */
    public void testFlowVariableService() {
        NodeOutPort nodeOutPortMock = mockNodeOutPortWithFlowVariables();
        List<org.knime.gateway.impl.rpc.flowvars.FlowVariable> res =
            m_serviceCreator.apply(nodeOutPortMock).getFlowVariables();
        assertThat(res.size(), is(3)); // 3 because it also includes the global 'knime.workspace' variable
        assertThat(res.get(0).getName(), is("test2"));
        assertThat(res.get(1).getName(), is("test1"));
        assertThat(res.get(0).getOwnerNodeId(), is("4"));
        assertThat(res.get(0).getType(), is("StringType"));
        assertThat(res.get(1).getType(), is("DoubleType"));
        assertThat(res.get(0).getValue(), is("foobar"));
        assertThat(res.get(1).getValue(), is("NaN"));
    }

    /**
     * Creates a mock for a {@link NodeOutPort} as required for the {@link DefaultFlowVariableService} implementation.
     *
     * @return the new mock instance
     */
    public static NodeOutPort mockNodeOutPortWithFlowVariables() {
        NodeOutPort port = mock(NodeOutPort.class);
        SingleNodeContainer snc = mock(SingleNodeContainer.class);
        when(snc.getOutPort(0)).thenReturn(port);
        when(port.getFlowObjectStack()).thenReturn(createTestFlowObjectStack());
        when(port.getConnectedNodeContainer()).thenReturn(snc);
        return port;
    }

    private static FlowObjectStack createTestFlowObjectStack() {
        List<FlowVariable> flowVars =
            Arrays.asList(new FlowVariable("test1", Double.NaN), new FlowVariable("test2", "foobar"));
        return FlowObjectStack.createFromFlowVariableList(flowVars, new NodeID(4));
    }

}
