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
 *   Jan 15, 2021 (hornm): created
 */
package org.knime.gateway.impl.jsonrpc.ports;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.junit.Test;
import org.knime.gateway.impl.rpc.flowvars.DefaultFlowVariableService;
import org.knime.gateway.impl.rpc.flowvars.FlowVariableService;
import org.knime.gateway.testing.helper.rpc.port.FlowVariableServiceTestHelper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Tests the correct serialization of the flow variables into json.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class JsonRpcFlowVariableSerializationTest extends AbstractJsonRpcSerializationTest<FlowVariableService> {

    private final ObjectMapper m_objectMapper;

    /**
     *
     */
    public JsonRpcFlowVariableSerializationTest() {
        super(FlowVariableService.class);
        m_objectMapper = new ObjectMapper();
    }

    /**
     * Tests the serialization of json-rpc requests and responses to and from
     * {@link FlowVariableService#getFlowVariables()}.
     *
     * @throws JsonProcessingException
     * @throws JsonMappingException
     */
    @Test
    public void testFlowVariablesToJsonSerialization() throws JsonMappingException, JsonProcessingException {
        FlowVariableService service = createServiceClient(
            new DefaultFlowVariableService(FlowVariableServiceTestHelper.mockNodeOutPortWithFlowVariables()));
        service.getFlowVariables();
        checkLastServerResponse();
    }

    private void checkLastServerResponse() throws JsonMappingException, JsonProcessingException {
        String res = getLastServerResponse();
        JsonNode json = m_objectMapper.readValue(res, JsonNode.class);
        assertThat(json.get("id").asInt(), is(0));
        assertThat(json.get("jsonrpc").asText(), is("2.0"));
        JsonNode result = json.get("result");
        JsonNode var0 = result.get(0);
        assertThat(var0.get("name").asText(), is("test2"));
        assertThat(var0.get("value").asText(), is("foobar"));
        assertThat(var0.get("type").asText(), is("StringType"));
        assertThat(var0.get("ownerNodeId").asText(), is("4"));
        JsonNode var1 = result.get(1);
        assertThat(var1.get("name").asText(), is("test1"));
        assertThat(var1.get("value").asText(), is("NaN"));
        assertThat(var1.get("type").asText(), is("DoubleType"));
        assertThat(var1.get("ownerNodeId").asText(), is("4"));
        JsonNode var2 = result.get(2);
        assertThat(var2.get("name").asText(), is("knime.workspace"));
        assertThat(var2.get("type").asText(), is("StringType"));
        assertThat(var2.get("ownerNodeId"), nullValue());
    }

}
