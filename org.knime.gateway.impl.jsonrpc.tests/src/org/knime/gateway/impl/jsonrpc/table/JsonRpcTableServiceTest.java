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
 * History
 *   Oct 26, 2020 (hornm): created
 */
package org.knime.gateway.impl.jsonrpc.table;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.Before;
import org.junit.Test;
import org.knime.core.rpc.RpcServer;
import org.knime.core.rpc.RpcSingleClient;
import org.knime.core.rpc.RpcTransport;
import org.knime.core.rpc.json.JsonRpcSingleServer;
import org.knime.core.rpc.json.JsonRpcTestUtil;
import org.knime.gateway.impl.rpc.table.DefaultTableService;
import org.knime.gateway.impl.rpc.table.Table;
import org.knime.gateway.impl.rpc.table.TableService;
import org.knime.gateway.testing.helper.rpc.port.TableServiceTestHelper;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.module.mrbean.AbstractTypeMaterializer;
import com.fasterxml.jackson.module.mrbean.MrBeanModule;

/**
 * Tests expected behavior of {@link TableService}-methods.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class JsonRpcTableServiceTest {

    private TableServiceTestHelper m_testHelper;

    /**
     * Initializes the {@link TableServiceTestHelper}.
     */
    @Before
    public void setupTestHelper() {
        TestJsonRpcTableServerFactory factory = new TestJsonRpcTableServerFactory();
        m_testHelper = new TableServiceTestHelper(p -> {
            factory.createRpcServer(p);
            return factory.getRpcClient().getService();
        });
    }

    /**
     * see {@link TableServiceTestHelper#testTableService()}
     */
    @Test
    public void testTableService() {
        m_testHelper.testTableService();
    }

    /**
     * see {@link TableServiceTestHelper#testTruncatedColumns()}
     */
    @Test
    public void testTruncatedColumns() {
        m_testHelper.testTruncatedColumns();
    }

    static class TestJsonRpcTableServerFactory extends JsonRpcTableServerFactory {

        private RpcSingleClient<TableService> m_rpcClient;

        private final TestRpcTransport m_transport;

        TestJsonRpcTableServerFactory() {
            this(null);
        }

        TestJsonRpcTableServerFactory(final TestRpcTransport transport) {
            m_transport = transport;
        }

        @Override
        protected RpcServer createRpcServer(final DefaultTableService tableService, final ObjectMapper mapper) {
            // allows one to deserialize, e.g., json into objects just represented by an interface
            mapper.registerModule(new MrBeanModule(new AbstractTypeMaterializer(Table.class.getClassLoader())));
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            mapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);

            JsonRpcSingleServer<TableService> server = new JsonRpcSingleServer<>(tableService, mapper);
            if (m_transport == null) {
                m_rpcClient =
                    JsonRpcTestUtil.createRpcSingleClientInstanceForTesting(TableService.class, tableService, mapper);
            } else {
                m_transport.setRpcServer(server);
                m_rpcClient =
                    JsonRpcTestUtil.createRpcSingleClientInstanceForTesting(TableService.class, mapper, m_transport);
            }
            return server;
        }

        RpcSingleClient<TableService> getRpcClient() {
            return m_rpcClient;
        }

    }

    static class TestRpcTransport implements RpcTransport {

        private RpcServer m_server;

        private String m_lastResponse;

        void setRpcServer(final RpcServer server) {
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

        String getLastResponse() {
            return m_lastResponse;
        }

    }

}
