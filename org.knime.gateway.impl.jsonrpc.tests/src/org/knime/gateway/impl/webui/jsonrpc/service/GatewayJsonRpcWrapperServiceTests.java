/*
 * ------------------------------------------------------------------------
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
package org.knime.gateway.impl.webui.jsonrpc.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.Map;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.knime.gateway.api.webui.service.EventService;
import org.knime.gateway.api.webui.service.NodeService;
import org.knime.gateway.api.webui.service.WorkflowService;
import org.knime.gateway.impl.project.WorkflowProjectManager;
import org.knime.gateway.impl.webui.jsonrpc.DefaultJsonRpcRequestHandler;
import org.knime.gateway.impl.webui.service.DefaultEventService;
import org.knime.gateway.json.util.ObjectMapperUtil;
import org.knime.gateway.testing.helper.EventSource;
import org.knime.gateway.testing.helper.LocalWorkflowLoader;
import org.knime.gateway.testing.helper.ResultChecker;
import org.knime.gateway.testing.helper.ServiceProvider;
import org.knime.gateway.testing.helper.WorkflowExecutor;
import org.knime.gateway.testing.helper.webui.GatewayTestCollection;
import org.knime.gateway.testing.helper.webui.GatewayTestRunner;
import org.knime.gateway.testing.helper.webui.WebUIGatewayServiceTestHelper;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.googlecode.jsonrpc4j.ExceptionResolver;
import com.googlecode.jsonrpc4j.JsonRpcClient;
import com.googlecode.jsonrpc4j.ReflectionUtil;

/**
 * Runs all tests provided by {@link GatewayTestCollection} on the json-rpc wrapper service implementations, e.g.
 * {@link JsonRpcWorkflowServiceWrapper}.
 *
 * TODO consider using dynamic tests with JUnit 5 //NOSONAR
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@RunWith(Parameterized.class)
public class GatewayJsonRpcWrapperServiceTests {

    private static final Map<String, GatewayTestRunner> GATEWAY_TESTS = GatewayTestCollection.collectAllGatewayTests();

    private static ResultChecker resultChecker;

    private final String m_gatewayTestName;

    private final LocalWorkflowLoader m_workflowLoader;

    private final WorkflowExecutor m_workflowExecutor;

    private final ServiceProvider m_serviceProvider;

    private final EventSource m_eventSource;

    /**
     * @return all names of the tests of {@link GatewayTestCollection}
     */
    @Parameters(name = "{0}")
    public static Iterable<String> testNames() {
        return GATEWAY_TESTS.keySet();
    }

    /**
     * @param gatewayTestName the test to run, the test names stemming from {@link #testNames()}
     */
    public GatewayJsonRpcWrapperServiceTests(final String gatewayTestName) {
        m_workflowLoader = new LocalWorkflowLoader();
        m_workflowExecutor = new WorkflowExecutor() {

            @Override
            public void executeWorkflowAsync(final String wfId) throws Exception {
                WorkflowProjectManager.openAndCacheWorkflow(wfId)
                    .orElseThrow(() -> new IllegalStateException("No workflow for id " + wfId)).executeAll();
            }

            @Override
            public void executeWorkflow(final String wfId) throws Exception {
                WorkflowProjectManager.openAndCacheWorkflow(wfId)
                    .orElseThrow(() -> new IllegalStateException("No workflow for id " + wfId))
                    .executeAllAndWaitUntilDone();
            }
        };
        DefaultJsonRpcRequestHandler handler = new DefaultJsonRpcRequestHandler();
        ObjectMapper mapper = ObjectMapperUtil.getInstance().getObjectMapper();
        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT);
        JsonRpcClient jsonRpcClient =
            new JsonRpcClient(mapper, new TestExceptionResolver(notNullValue(String.class), is(-32600)));
        m_serviceProvider = new ServiceProvider() {

            @Override
            public WorkflowService getWorkflowService() {
                return createClientProxy(WorkflowService.class, handler, jsonRpcClient);
            }

            @Override
            public NodeService getNodeService() {
                return createClientProxy(NodeService.class, handler, jsonRpcClient);
            }

            @Override
            public EventService getEventService() {
                return createClientProxy(EventService.class, handler, jsonRpcClient);
            }
        };

        m_gatewayTestName = gatewayTestName;
        m_eventSource = c -> DefaultEventService.getInstance().addEventConsumer(c);
    }

    /**
     * Runs the actual (parametrized) test.
     *
     * @throws Exception
     */
    @Test
    public void test() throws Exception {
        GATEWAY_TESTS.get(m_gatewayTestName).runGatewayTest(resultChecker, m_serviceProvider, m_workflowLoader,
            m_workflowExecutor, m_eventSource);
    }

    /**
     * Removes the project where necessary.
     *
     * @throws InterruptedException
     */
    @After
    public void disposeWorkflows() throws InterruptedException {
        m_workflowLoader.disposeWorkflows();
    }

    /**
     * Initializes/instantiates the result checker.
     */
    @BeforeClass
    public static void initResultChecker() {
        resultChecker = WebUIGatewayServiceTestHelper.createResultChecker();
    }

    /**
     * Creates a json-rpc service proxy that uses the provided {@link JsonRpcClient} to create the json-rpc request and
     * parse the json-rpc response.
     *
     * The actual json-rpc request is turned into a json-rpc response by the provided
     * {@link DefaultJsonRpcRequestHandler}.
     *
     * @param <T>
     * @param proxyInterface
     * @param handler
     * @param jsonRpcClient
     *
     * @return the service proxy
     */
    @SuppressWarnings("unchecked")
    public static <T> T createClientProxy(final Class<T> proxyInterface, final DefaultJsonRpcRequestHandler handler,
        final JsonRpcClient jsonRpcClient) {
        return (T)Proxy.newProxyInstance(proxyInterface.getClassLoader(), new Class<?>[]{proxyInterface},
            (proxy, method, args) -> {
                final Object arguments = ReflectionUtil.parseArguments(method, args);
                String methodName = proxyInterface.getSimpleName() + "." + method.getName();

                byte[] response;
                try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                    jsonRpcClient.invoke(methodName, arguments, out);
                    response = handler.handle(out.toByteArray());
                }

                try (ByteArrayInputStream in = new ByteArrayInputStream(response)) {
                    return jsonRpcClient.readResponse(method.getGenericReturnType(), in);
                }
            });
    }

    public static class TestExceptionResolver implements ExceptionResolver {

        private Matcher<String> m_messageMatcher;
        private Matcher<Integer> m_codeMatcher;

        /**
         * @param messageMatcher
         * @param codeMatcher
         */
        public TestExceptionResolver(final Matcher<String> messageMatcher, final Matcher<Integer> codeMatcher) {
            m_messageMatcher = messageMatcher;
            m_codeMatcher = codeMatcher;
        }

        @Override
        public Throwable resolveException(final ObjectNode response) {
            assertThat(response.get("jsonrpc").asText(), is("2.0"));
            JsonNode error = response.get("error");
            assertThat("unexpected error code", error.get("code").asInt(), m_codeMatcher);
            JsonNode data = error.get("data");
            String name = data.get("name").asText();
            assertNotNull("no stacktrace given", data.get("stackTrace"));
            String message = error.get("message").asText();
            assertThat("unexpected exception message", message, m_messageMatcher);
            try {
                @SuppressWarnings("unchecked")
                Class<? extends Exception> cl = (Class<? extends Exception>)Class.forName(name);
                Constructor<? extends Exception> cons = cl.getConstructor(String.class);
                return cons.newInstance(message);
            } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException
                    | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                throw new AssertionError("Exception couldn't be created from the json-rpc error", ex);
            }
        }
    }

}
