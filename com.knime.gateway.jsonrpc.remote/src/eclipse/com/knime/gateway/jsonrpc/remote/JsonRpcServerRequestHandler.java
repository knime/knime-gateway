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
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.tuple.Pair;
import org.knime.gateway.ServiceDefUtil;
import org.knime.gateway.jsonrpc.JsonRpcUtil;
import org.knime.gateway.workflow.service.GatewayService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.googlecode.jsonrpc4j.JsonRpcMultiServer;
import com.knime.enterprise.executor.genericmsg.GenericServerRequestHandler;

/**
 * Implementation of the {@link GenericServerRequestHandler} extension point that executes json-rpc 2.0 requests and
 * delegates the respective calls to the default service implementations.
 *
 * The workflows the default service implementations work on are added via the {@link JobPoolListener}.
 *
 * @author Martin Horn, University of Konstanz
 */
public class JsonRpcServerRequestHandler implements GenericServerRequestHandler {

    private static final String DEFAULT_SERVICE_PACKAGE = "com.knime.gateway.remote";

    private static final String DEFAULT_SERVICE_PREFIX = "Default";

    private JsonRpcMultiServer m_jsonRpcMultiServer;

    /**
     *
     */
    public JsonRpcServerRequestHandler() {
        //setup json-rpc server
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new Jdk8Module());

        JsonRpcUtil.addMixIns(mapper);
        m_jsonRpcMultiServer = new JsonRpcMultiServer(mapper);

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
        Collection<Pair<String, String>> serviceDefs = ServiceDefUtil.getServices();
        Map<String, GatewayService> wrappedServices = new HashMap<String, GatewayService>();
        for (Pair<String, String> p : serviceDefs) {
            Class<?> defaultServiceClass;
            String defaultServiceFullClassName =
                DEFAULT_SERVICE_PACKAGE + "." + p.getRight() + "." + DEFAULT_SERVICE_PREFIX + p.getLeft();
            try {
                defaultServiceClass = Class.forName(defaultServiceFullClassName);
            } catch (ClassNotFoundException ex1) {
                throw new RuntimeException(
                    "No default service implementation found (" + defaultServiceFullClassName + ")", ex1);
            }
            try {
                Class<GatewayService> wrapperServiceClass =
                    (Class<GatewayService>)org.knime.gateway.jsonrpc.remote.ObjectSpecUtil
                        .getClassForFullyQualifiedName(p.getRight(), p.getLeft(), "jsonrpc-wrapper");
                Class<?> serviceInterface =
                    org.knime.gateway.ObjectSpecUtil.getClassForFullyQualifiedName(p.getRight(), p.getLeft(), "api");
                //dots (.) in namespace need to be replaced by '_' and concatenated with the name by '_', too!
                //Because the json-rpc lib uses the dot (.) to separate the service method to be called from the service (or service identifier)
                wrappedServices.put(p.getRight().replace(".", "_") + "_" + p.getLeft(), wrapperServiceClass
                    .getConstructor(serviceInterface).newInstance(defaultServiceClass.newInstance()));
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException | NoSuchMethodException | SecurityException
                    | ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        }
        return wrappedServices;
    }
}
