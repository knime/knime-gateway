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
package com.knime.gateway.jsonrpc.local;

import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.ws.rs.core.UriBuilder;

import org.knime.core.util.KNIMEServerHostnameVerifier;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.googlecode.jsonrpc4j.ProxyUtil;
import com.knime.gateway.json.JsonUtil;
import com.knime.gateway.jsonrpc.local.service.util.ServiceInterface2JsonRpcMap;
import com.knime.gateway.local.service.ServerServiceConfig;
import com.knime.gateway.local.service.ServiceConfig;
import com.knime.gateway.local.service.ServiceFactory;
import com.knime.gateway.service.GatewayService;

/**
 * Service factories whose returned services talk to a http(s) server at "v4/gateway/jsonrpc" by 'posting' json-rpc
 * messages.
 *
 * @author Martin Horn, University of Konstanz
 */
public class JsonRpcClientServiceFactory implements ServiceFactory {

    private static final String GATEWAY_PATH = "/v4/jobs/{uuid}/gateway/jsonrpc";

    /**
     * {@inheritDoc}
     */
    @Override
    public <S extends GatewayService> S createService(final Class<S> serviceInterface,
        final ServiceConfig serviceConfig) {
        if (serviceConfig instanceof ServerServiceConfig) {
            ServerServiceConfig serverServiceConfig = (ServerServiceConfig)serviceConfig;
            Class<?> proxyInterface = ServiceInterface2JsonRpcMap.get(serviceInterface);
            return (S)createService(proxyInterface, serverServiceConfig.getURI(), serverServiceConfig.getJWT());
        } else {
            throw new IllegalStateException("No server service config given!");
        }
    }

    private <T> T createService(final Class<T> proxyInterface, final URI uri, final Optional<String> jwt) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new Jdk8Module());

            mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
            mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);

            JsonUtil.addMixIns(mapper);

            Map<String, String> headers = new HashMap<String, String>();
            headers.put("Content-Type", "application/json");
            jwt.ifPresent(s -> headers.put("Authorization", "Bearer " + s));
            headers.put("KNIME-API-Version", "4.6.0");
            JsonRpcHttpClient httpClient = new JsonRpcHttpClient(mapper, uri.toURL(), headers) {
                /**
                 * {@inheritDoc}
                 */
                @Override
                protected void handleErrorResponse(final ObjectNode jsonObject) throws Throwable {
                    if (hasError(jsonObject)) {
                        String message = jsonObject.get("error").get("message").asText();
                        throw (Exception)Class
                            .forName(jsonObject.get("error").get("data").get("exceptionTypeName").asText())
                            .getConstructor(String.class).newInstance(message);
                    }
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public Object invoke(final String methodName, final Object argument, final Type returnType,
                    final Map<String, String> extraHeaders) throws Throwable {
                    //set the service URL to /v4/jobs/{uuid}/gateway/jsonrpc
                    //assuming that the very first argument ('argument' is an array) contains the job id
                    UUID jobId = (UUID)((Object[])argument)[0];
                    setServiceUrl(UriBuilder.fromUri(uri).path(GATEWAY_PATH).build(jobId.toString()).toURL());
                    return super.invoke(methodName, argument, returnType, extraHeaders);
                }
            };
            httpClient.setHostNameVerifier(KNIMEServerHostnameVerifier.getInstance());
            return ProxyUtil.createClientProxy(proxyInterface.getClassLoader(), proxyInterface, httpClient);
        } catch (MalformedURLException ex) {
            throw new RuntimeException(ex);
        }
    }
}
