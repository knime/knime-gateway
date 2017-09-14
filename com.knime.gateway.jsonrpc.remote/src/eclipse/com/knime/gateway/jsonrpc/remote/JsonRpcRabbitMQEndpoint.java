/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME GmbH, Konstanz, Germany
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
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
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
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeoutException;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.tuple.Pair;
import org.knime.core.node.NodeLogger;
import org.knime.gateway.ServiceDefUtil;
import org.knime.gateway.jsonrpc.JsonRpcUtil;
import org.knime.gateway.workflow.service.GatewayService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.googlecode.jsonrpc4j.JsonRpcMultiServer;
import com.knime.gateway.remote.endpoint.GatewayEndpoint;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

/**
 *
 * @author Martin Horn, University of Konstanz
 */
public class JsonRpcRabbitMQEndpoint implements GatewayEndpoint {

    private static final String DEFAULT_SERVICE_PACKAGE = "com.knime.gateway.remote";

    private static final String DEFAULT_SERVICE_PREFIX = "Default";

    private static NodeLogger LOGGER = NodeLogger.getLogger(JsonRpcRabbitMQEndpoint.class);

    private Connection m_connection;

    private Channel m_channel;

    private JsonRpcMultiServer m_jsonRpcMultiServer;

    @Override
    public void start() {
        String uri = System.getenv("com.knime.enterprise.executor.msgq");
        if (uri == null) {
            uri = System.getProperty("com.knime.enterprise.executor.msgq");
        }
        if (uri == null) {
            //obviously no message queue configured, no gateway service can be started
            LOGGER.warn("No message queue server configured. JSONRPC-gateway service will not be available.");
            return;
        } else {
            //otherwise connect to the message queue server and listen for messages

            //setup json-rpc server
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new Jdk8Module());

            JsonRpcUtil.addMixIns(mapper);
            m_jsonRpcMultiServer = new JsonRpcMultiServer(mapper);

            for (Entry<String, GatewayService> entry : createWrappedServices().entrySet()) {
                m_jsonRpcMultiServer.addService(entry.getKey(), entry.getValue());
            }

            //setup message queue
            ConnectionFactory factory = new ConnectionFactory();
            try {
                factory.setUri(uri);
                m_connection = factory.newConnection();
                m_channel = m_connection.createChannel();
            } catch (KeyManagementException | NoSuchAlgorithmException | URISyntaxException | TimeoutException
                    | IOException ex) {
                LOGGER.error("An error occurred while connecting to the Rabbit MQ server.", ex);
                throw new RuntimeException(ex);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        try {
            m_connection.close();
            m_channel.close();
        } catch (IOException | TimeoutException ex) {
            // TODO better exception handling
            throw new RuntimeException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onWorklfowProjectAdded(final String workflowProjectID) {
        //declare a new queue for the workflow project id
        try {
            m_channel.queueDeclare("gateway-" + workflowProjectID, false, false, false, null);

            m_channel.basicConsume("gateway-" + workflowProjectID, false,
                new GatewayJsonRpcConsumer(m_channel, m_jsonRpcMultiServer));
        } catch (IOException ex) {
            // TODO
            throw new RuntimeException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onWorkflowProjectRemoved(final String workflowProjectID) {
        // discard the queue created for this workflow project
        try {
            m_channel.queueDelete("gateway-" + workflowProjectID);
        } catch (IOException ex) {
            // TODO better exception handling
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

    private class GatewayJsonRpcConsumer extends DefaultConsumer {

        private JsonRpcMultiServer m_jsonRpcServer;

        /**
         * @param channel
         * @param jsonRpcServer
         */
        public GatewayJsonRpcConsumer(final Channel channel, final JsonRpcMultiServer jsonRpcServer) {
            super(channel);
            m_jsonRpcServer = jsonRpcServer;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void handleDelivery(final String consumerTag, final Envelope envelope, final BasicProperties properties,
            final byte[] body) throws IOException {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            m_jsonRpcServer.handleRequest(new ByteArrayInputStream(body), out);

            System.out.println("executor corr id: " + properties.getCorrelationId());

            //return response via message queue
            AMQP.BasicProperties replyProps =
                new AMQP.BasicProperties.Builder().correlationId(properties.getCorrelationId()).build();

            m_channel.basicPublish("", properties.getReplyTo(), replyProps, out.toByteArray());
            m_channel.basicAck(envelope.getDeliveryTag(), false);
        }

    }
}
