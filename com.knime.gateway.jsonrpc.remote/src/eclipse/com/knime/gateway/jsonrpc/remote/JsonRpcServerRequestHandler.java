/* ------------------------------------------------------------------
 * This source code, its documentation and all appendant files
 * are protected by copyright law. All rights reserved.
 *
 * Copyright by KNIME AG, Zurich, Switzerland
 *
 * You may not modify, publish, transmit, transfer or sell, reproduce,
 * create derivative works from, distribute, perform, display, or in
 * any way exploit any of the content, in whole or in part, except as
 * otherwise expressly permitted in writing by the copyright owner or
 * as specified in the license file distributed with this product.
 *
 * If you have any questions please contact the copyright holder:
 * website: www.knime.com
 * email: contact@knime.com
 * ---------------------------------------------------------------------
 *
 */
package com.knime.gateway.jsonrpc.remote;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.output.ByteArrayOutputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.jsonrpc4j.JsonRpcMultiServer;
import com.knime.gateway.json.util.ObjectMapperUtil;
import com.knime.gateway.jsonrpc.remote.service.util.WrapWithJsonRpcService;
import com.knime.gateway.remote.service.DefaultServices;
import com.knime.gateway.service.GatewayService;
import com.knime.gateway.service.util.ListServices;

/**
 * Implementation of the {@link GenericServerRequestHandler} extension point that executes json-rpc 2.0 requests and
 * delegates the respective calls to the default service implementations.
 *
 * The workflows the default service implementations work on are added via the {@link JobPoolListener}.
 *
 * @author Martin Horn, University of Konstanz
 */
public class JsonRpcServerRequestHandler {

    private JsonRpcMultiServer m_jsonRpcMultiServer;

    /**
     *
     */
    public JsonRpcServerRequestHandler() {
        //setup json-rpc server
        ObjectMapper mapper = getObjectMapper();

        m_jsonRpcMultiServer = new JsonRpcMultiServer(mapper);
        m_jsonRpcMultiServer.setErrorResolver(new JsonRpcErrorResolver());

        for (Entry<String, GatewayService> entry : createWrappedServices().entrySet()) {
            m_jsonRpcMultiServer.addService(entry.getKey(), entry.getValue());
        }
    }

    /**
     * @return the object mapper to use for json-rpc-request deserialization and json-rpc-response serialization
     */
    protected ObjectMapper getObjectMapper() {
        return ObjectMapperUtil.getInstance().getObjectMapper();
    }


    public byte[] handle(final byte[] messageBody) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            m_jsonRpcMultiServer.handleRequest(new ByteArrayInputStream(messageBody), out);
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
