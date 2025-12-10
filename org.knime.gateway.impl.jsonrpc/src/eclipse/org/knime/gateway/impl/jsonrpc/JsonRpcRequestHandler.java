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
package org.knime.gateway.impl.jsonrpc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Map.Entry;

import org.knime.core.node.NodeLogger;
import org.knime.gateway.api.service.GatewayService;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.googlecode.jsonrpc4j.JsonRpcMultiServer;

/**
 * Executes json-rpc 2.0 requests and delegates the respective calls to the default service implementations.
 *
 * @author Martin Horn, University of Konstanz
 * @since 4.11
 */
public class JsonRpcRequestHandler {

    private static final ClassLoader defaultContextClassLoader =
        JsonRpcBundleActivator.getDefaultContextClassLoader().orElse(null);

    private final JsonRpcMultiServer m_jsonRpcMultiServer;
    private final ExceptionToJsonRpcErrorTranslator m_exceptionTranslator;
    private final ObjectMapper m_mapper;

    /**
     * Creates a new request handler.
     *
     * @param mapper the object mapper to use for json de-/serialization
     * @param services the services to be used by the handler (map from service name to service handler)
     * @param t to translate exception to json-rpc errors
     */
    public JsonRpcRequestHandler(final ObjectMapper mapper, final Map<String, GatewayService> services,
        final ExceptionToJsonRpcErrorTranslator t) {
        m_mapper = mapper;
        //setup json-rpc server
        m_jsonRpcMultiServer = new JsonRpcMultiServer(mapper);
        m_jsonRpcMultiServer.setErrorResolver(new JsonRpcErrorResolver(t));
        for (Entry<String, GatewayService> entry : services.entrySet()) {
            m_jsonRpcMultiServer.addService(entry.getKey(), entry.getValue());
        }

        m_exceptionTranslator = t;
    }

    /**
     * Handles a json rpc 2.0 request.
     *
     * @param jsonRpcRequest the request
     * @return a jsonrpc response
     */
    public byte[] handle(final byte[] jsonRpcRequest) {
        try (var out = new ByteArrayOutputStream();
                var in = new ByteArrayInputStream(jsonRpcRequest);
                var clCloser = new WithDefaultContextClassLoaderCloseable()) {
            m_jsonRpcMultiServer.handleRequest(in, out);
            return out.toByteArray();
        } catch (IOException e) {
            NodeLogger.getLogger(getClass()).warn("Problem handling json rpc request", e);
            // turn it into a json error object
            return createJsonRpcErrorResponse(m_mapper, m_exceptionTranslator.getUnexpectedExceptionErrorCode(e),
                m_exceptionTranslator.getMessage(e), m_exceptionTranslator.getData(e)).getBytes(StandardCharsets.UTF_8);
        }
    }

    /**
     * Creates a json rpc error response.
     *
     * @param mapper mapper used to create the json object and serialize the data, if given.
     * @param errorCode
     * @param message
     * @param data the data to be set in the data field, or {@code null} if none
     * @return the json rpc error response
     */
    public static String createJsonRpcErrorResponse(final ObjectMapper mapper, final int errorCode,
        final String message, final Object data) {
        ObjectNode jsonRpc = mapper.createObjectNode().put("jsonrpc", "2.0"); // NOSONAR
        var res = jsonRpc.putObject("error").put("code", errorCode).put("message", message);
        if (data != null) {
            res.set("data", mapper.convertValue(data, JsonNode.class));
        }
        return jsonRpc.toPrettyString();
    }

    /**
     * A resource that temporarily sets the context finder class loader as the thread context class loader
     * (if available), restoring the previous class loader when closed.
     *
     * <p>
     * Introduced as part of AP-25450 to fix class loading issues in semi-isolated third-party (node) bundles that
     * rely on {@code ServiceLoader}-type class loading. For example, a partner extension that bundled 50+ jars
     * wired via plain old services.
     */
    private static final class WithDefaultContextClassLoaderCloseable implements AutoCloseable {
        private final ClassLoader m_previousCL;

        /**
         * Saves the current thread context class loader and sets it to the context finder class loader.
         */
        WithDefaultContextClassLoaderCloseable() {
            m_previousCL = Thread.currentThread().getContextClassLoader();
            if (defaultContextClassLoader != null) {
                Thread.currentThread().setContextClassLoader(defaultContextClassLoader);
            }
        }

        /**
         * Restores the previous thread context class loader.
         */
        @Override
        public void close() {
            Thread.currentThread().setContextClassLoader(m_previousCL);
        }
    }
}
