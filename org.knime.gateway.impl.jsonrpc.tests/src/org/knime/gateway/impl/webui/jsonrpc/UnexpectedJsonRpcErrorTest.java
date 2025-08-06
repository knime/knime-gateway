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
 *   Oct 13, 2020 (hornm): created
 */
package org.knime.gateway.impl.webui.jsonrpc;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.knime.gateway.impl.webui.jsonrpc.service.GatewayJsonRpcWrapperServiceTests.createClientProxy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.knime.gateway.api.service.GatewayService;
import org.knime.gateway.api.util.EntityUtil;
import org.knime.gateway.api.webui.service.WorkflowService;
import org.knime.gateway.json.util.ObjectMapperUtil;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.googlecode.jsonrpc4j.ExceptionResolver;
import com.googlecode.jsonrpc4j.JsonRpcClient;

/**
 * Test for correct json-rpc error responses if a gateway service throws an unexpected exception.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class UnexpectedJsonRpcErrorTest {

    /**
     * Tests for the correct json-rpc error response if a gateway service throws an unexpected exception.
     *
     * @throws Exception -
     */
    @Test
    public void testUnexpectedJsonRpcError() throws Exception {
        Map<Class<? extends GatewayService>, Supplier<? extends GatewayService>> serviceMocks = new HashMap<>();
        var workflowServiceMock = mock(WorkflowService.class);
        when(workflowServiceMock.getWorkflow(any(), any(), any(), any()))
            .thenThrow(new UnsupportedOperationException("an unexpected exception"));
        serviceMocks.put(WorkflowService.class, () -> workflowServiceMock);
        var handler = new DefaultJsonRpcRequestHandler(serviceMocks);
        var mapper = ObjectMapperUtil.getInstance().getObjectMapper();
        var jsonRpcClient =
            new JsonRpcClient(mapper, new TestExceptionResolver(Matchers.is("an unexpected exception")));
        var workflowServiceProxy = createClientProxy(WorkflowService.class, handler, jsonRpcClient);
        assertThrows(UnsupportedOperationException.class,
            () -> workflowServiceProxy.getWorkflow(null, null, null, Boolean.FALSE));
    }

    private static class TestExceptionResolver implements ExceptionResolver {

        private Matcher<String> m_messageMatcher;

        /**
         * @param messageMatcher
         * @param codeMatcher
         */
        public TestExceptionResolver(final Matcher<String> messageMatcher) {
            m_messageMatcher = messageMatcher;
        }

        @Override
        public Throwable resolveException(final ObjectNode response) {
            assertThat(response.get("jsonrpc").asText(), is("2.0"));
            var error = response.get("error");
            assertThat("unexpected error code", error.get("code").asInt(), Matchers.is(-32601));
            var data = error.get("data");
            assertNotNull("no stacktrace given", data.get("stackTrace"));
            var message = error.get("message").asText();
            assertThat("unexpected exception message", message, m_messageMatcher);
            assertThat("unexpected title", data.get("title").asText(), Matchers.is(EntityUtil.UNEXPECTED_TITLE));
            assertThat("unexpected error code in data", data.get("code").asText(),
                    Matchers.is("UnsupportedOperationException"));
            assertThat("unexpected canCopy", data.get("canCopy").asBoolean(), Matchers.is(true));
            return new UnsupportedOperationException(message);
        }
    }

}
