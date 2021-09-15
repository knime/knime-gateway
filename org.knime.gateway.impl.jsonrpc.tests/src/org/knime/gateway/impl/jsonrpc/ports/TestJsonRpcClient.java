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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.knime.core.webui.data.rpc.RpcServer;
import org.knime.core.webui.data.rpc.RpcSingleClient;
import org.knime.core.webui.data.rpc.RpcTransport;
import org.knime.core.webui.data.rpc.json.impl.JsonRpcSingleServer;
import org.knime.core.webui.data.rpc.json.impl.JsonRpcTestUtil;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.module.mrbean.AbstractTypeMaterializer;
import com.fasterxml.jackson.module.mrbean.MrBeanModule;
import com.googlecode.jsonrpc4j.JsonRpcClient;
import com.googlecode.jsonrpc4j.JsonRpcServer;

/**
 * Creates a {@link JsonRpcClient} for testing purposes. It instantiates a {@link JsonRpcServer}, creates an
 * {@link ObjectMapper} that allows one to create instances from pure interfaces and enables one to get access to the
 * last response received from the 'server'.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
class TestJsonRpcClient<S> {

    private final TestRpcTransport m_transport;

    private final S m_clientService;

    TestJsonRpcClient(final Class<S> serviceClass, final S serviceImpl) {
        ObjectMapper mapper = createObjectMapper(serviceClass);
        RpcServer server = createServer(serviceImpl, mapper);
        m_transport = new TestRpcTransport(server);
        m_clientService = createClientService(serviceClass, mapper, m_transport);
    }

    /**
     * Returns the client service instance. Calls will be serialized into jsonrpc, 'send' to the server and the
     * responses deserialized.
     *
     * @return the client service instance
     */
    S getService() {
        return m_clientService;
    }

    /**
     * @return the last response (as json-rpc string) received from the server or <code>null</code> if no response
     *         received, yet
     */
    String getLastServerResponse() {
        return m_transport.getLastServerResponse();
    }

    private static <S> ObjectMapper createObjectMapper(final Class<S> serviceClass) {
        ObjectMapper mapper = AbstractPortJsonRpcServerFactory.createObjectMapper();
        // allows one to deserialize, e.g., json into objects just represented by an interface
        mapper.registerModule(new MrBeanModule(new AbstractTypeMaterializer(serviceClass.getClassLoader())));
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
        return mapper;
    }

    private static <S> RpcServer createServer(final S serviceImpl, final ObjectMapper mapper) {
        return new JsonRpcSingleServer<S>(serviceImpl, mapper);
    }

    private static <S> S createClientService(final Class<S> serviceClass, final ObjectMapper mapper,
        final RpcTransport transport) {
        RpcSingleClient<S> rpcClient =
            JsonRpcTestUtil.createRpcSingleClientInstanceForTesting(serviceClass, mapper, transport);
        return rpcClient.getService();
    }

    /**
     * A {@link RpcTransport} implementation that directly forwards the request to the rpc server and provides access to
     * the very last received response for testing.
     */
    private static final class TestRpcTransport implements RpcTransport {

        private final RpcServer m_server;

        private String m_lastResponse;

        private TestRpcTransport(final RpcServer server) {
            m_server = server;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String sendAndReceive(final String rpc) {
            try (ByteArrayInputStream request = new ByteArrayInputStream(rpc.getBytes(StandardCharsets.UTF_8));
                    ByteArrayOutputStream response = new ByteArrayOutputStream()) {
                m_server.handleRequest(request, response);
                m_lastResponse = new String(response.toByteArray(), StandardCharsets.UTF_8.name());
                return m_lastResponse;
            } catch (IOException ex) {
                throw new IllegalStateException("I/O exception during node data service rpc request handling", ex);
            }
        }

        private String getLastServerResponse() {
            return m_lastResponse;
        }

    }
}
