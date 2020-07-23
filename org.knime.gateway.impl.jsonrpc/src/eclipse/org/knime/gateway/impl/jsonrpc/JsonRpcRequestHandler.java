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
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.knime.gateway.api.service.GatewayService;
import org.knime.gateway.api.service.util.ListServices;
import org.knime.gateway.impl.jsonrpc.service.util.WrapWithJsonRpcService;
import org.knime.gateway.impl.service.DefaultServices;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.jsonrpc4j.JsonRpcMultiServer;

/**
 * Executes json-rpc 2.0 requests and delegates the respective calls to the default service implementations.
 *
 * @author Martin Horn, University of Konstanz
 * @since 4.11
 */
public class JsonRpcRequestHandler {

    private JsonRpcMultiServer m_jsonRpcMultiServer;

    /**
     * Creates a new request handler.
     *
     * @param mapper the object mapper to use for json de-/serialization
     * @param additionalServices additional services to be used by the handler (the simple class name is used as service
     *            name!)
     */
    public JsonRpcRequestHandler(final ObjectMapper mapper, final Object... additionalServices) {
        //setup json-rpc server
        m_jsonRpcMultiServer = new JsonRpcMultiServer(mapper);
        m_jsonRpcMultiServer.setErrorResolver(new JsonRpcErrorResolver());

        for (Entry<String, GatewayService> entry : createWrappedServices().entrySet()) {
            m_jsonRpcMultiServer.addService(entry.getKey(), entry.getValue());
        }

        for (Object service : additionalServices) {
            m_jsonRpcMultiServer.addService(service.getClass().getSimpleName(), service);
        }
    }

    /**
     * Handles a json rpc 2.0 request.
     *
     * @param jsonRpcRequest the request
     * @return a jsonrpc response
     */
    public byte[] handle(final byte[] jsonRpcRequest) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            m_jsonRpcMultiServer.handleRequest(new ByteArrayInputStream(jsonRpcRequest), out);
            return out.toByteArray();
        } catch (IOException ex) {
            //TODO better exception handling
            throw new RuntimeException(ex);
        }
    }

    private static Map<String, GatewayService> createWrappedServices() {
        //create all default services and wrap them with the rest wrapper services
        List<Class<?>> serviceInterfaces = ListServices.listServiceInterfaces();
        Map<String, GatewayService> wrappedServices = new HashMap<String, GatewayService>();
        for (Class<?> serviceInterface : serviceInterfaces) {
            GatewayService wrappedService = WrapWithJsonRpcService.wrap(
                DefaultServices.getDefaultService((Class<? extends GatewayService>)serviceInterface), serviceInterface);
            wrappedServices.put(serviceInterface.getSimpleName(), wrappedService);
        }
        return wrappedServices;
    }
}
