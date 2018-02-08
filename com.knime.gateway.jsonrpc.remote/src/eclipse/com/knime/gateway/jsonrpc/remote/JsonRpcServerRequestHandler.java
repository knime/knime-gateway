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
 *   Sep 8, 2017 (hornm): created
 */
package com.knime.gateway.jsonrpc.remote;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.commons.io.output.ByteArrayOutputStream;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.googlecode.jsonrpc4j.JsonRpcMultiServer;
import com.knime.enterprise.executor.genericmsg.GenericServerRequestHandler;
import com.knime.gateway.json.JsonUtil;
import com.knime.gateway.jsonrpc.remote.service.util.WrapWithJsonRpcService;
import com.knime.gateway.service.GatewayService;
import com.knime.gateway.v0.service.util.ListServices;

/**
 * Implementation of the {@link GenericServerRequestHandler} extension point that executes json-rpc 2.0 requests and
 * delegates the respective calls to the default service implementations.
 *
 * The workflows the default service implementations work on are added via the {@link JobPoolListener}.
 *
 * @author Martin Horn, University of Konstanz
 */
public class JsonRpcServerRequestHandler implements GenericServerRequestHandler {

    private static final String DEFAULT_SERVICE_PACKAGE = "com.knime.gateway.remote.service";

    private static final String DEFAULT_SERVICE_PREFIX = "Default";

    private JsonRpcMultiServer m_jsonRpcMultiServer;

    /**
     *
     */
    public JsonRpcServerRequestHandler() {
        //setup json-rpc server
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new Jdk8Module());

        mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);

        JsonUtil.addMixIns(mapper);
        m_jsonRpcMultiServer = new JsonRpcMultiServer(mapper);
        m_jsonRpcMultiServer.setErrorResolver(new JsonRpcErrorResolver());

        for (Entry<String, GatewayService> entry : createWrappedServices().entrySet()) {
            m_jsonRpcMultiServer.addService(entry.getKey(), entry.getValue());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessageId() {
        return "jsonrpc2.0";
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] handle(final UUID jobId, final byte[] messageBody) {
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
            Class<?> defaultServiceClass;
            String defaultServiceFullClassName =
                DEFAULT_SERVICE_PACKAGE + "." + DEFAULT_SERVICE_PREFIX + serviceInterface.getSimpleName();
            try {
                defaultServiceClass = Class.forName(defaultServiceFullClassName);
                GatewayService wrappedService =
                    WrapWithJsonRpcService.wrap((GatewayService)defaultServiceClass.newInstance(), serviceInterface);
                wrappedServices.put(serviceInterface.getSimpleName(), wrappedService);
            } catch (ClassNotFoundException ex1) {
                throw new RuntimeException(
                    "No default service implementation found (" + defaultServiceFullClassName + ")", ex1);
            } catch (InstantiationException | IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        }
        return wrappedServices;
    }
}
