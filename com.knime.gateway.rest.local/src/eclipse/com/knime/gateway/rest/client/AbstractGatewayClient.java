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
package com.knime.gateway.rest.client;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.net.URI;
import java.util.List;
import java.util.function.Function;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.common.util.StringUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.knime.core.node.NodeLogger;
import org.knime.core.util.ThreadLocalHTTPAuthenticator;
import org.knime.core.util.ThreadLocalHTTPAuthenticator.AuthenticationCloseable;

import com.knime.enterprise.server.rest.api.Util;
import com.knime.enterprise.server.rest.client.AbstractClient;
import com.knime.enterprise.server.rest.providers.exception.ResponseToExceptionMapper;
import com.knime.gateway.rest.client.providers.json.EntityJSONDeserializer;
import com.knime.gateway.rest.client.service.WorkflowClient;

/**
 * Abstract gateway client to provide functions to the auto-generated clients (such as {@link WorkflowClient} for
 * specific rest interface endpoints.
 *
 * It unifies the creation of the jax-rs proxy instances, the request via these proxies etc.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public abstract class AbstractGatewayClient<C> extends AbstractClient {
    private C m_client;

    /**
     * Creates a new client.
     *
     * @param restAddress the server's REST address
     * @param jwt a json web token for authentication, can be <code>null</code> if not available
     * @throws InstantiationException if providers for the the JAX-RS client cannot be instantiated
     * @throws IllegalAccessException if providers for the the JAX-RS client cannot be instantiated
     * @throws IOException if an I/O error occurs while instantiating the JAX-RS client
     * @throws NotAuthorizedException if the current user cannot be authenticated
     */
    public AbstractGatewayClient(final URI restAddress, final String jwt)
        throws InstantiationException, IllegalAccessException, IOException {
        super(restAddress);
        @SuppressWarnings("unchecked")
        Class<C> resourceClass =
            (Class<C>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        List<Object> jaxRSProviders = Util.getJaxRSProviders();
        //is there a better way than adding the required provider manually?
        jaxRSProviders.add(new EntityJSONDeserializer());
        m_client = createProxy(resourceClass, m_restAddress, null, null, jaxRSProviders, "Explorer01",
            AbstractClient.DEFAULT_TIMEOUT);
        if (jwt != null) {
            WebClient.client(m_client).header("Authorization", "Bearer " + jwt);
        }
    }

    /**
     * Performs the client's request.
     *
     * Assumptions here: response content type is application/json!
     *
     * @param call function to call the right method on the proxy-client
     * @param resultClass the class of the expected response entity or <code>null</code> if none
     * @return status code and the response entity (or <code>null</code> if none)
     * @throws WebApplicationException in case of an exception as response, e.g. not found, etc.
     */
    protected <R> R doRequest(final Function<C, Response> call, final Class<R> resultClass) {
        try (AuthenticationCloseable c = ThreadLocalHTTPAuthenticator.suppressAuthenticationPopups()) {
            Response res = call.apply(m_client);
            if (!MediaType.APPLICATION_JSON_TYPE.isCompatible(res.getMediaType())) {
                throw new IllegalArgumentException(
                    "REST address '" + m_restAddress + "' does not point to a KNIME server's REST interface.");
            }
            if (resultClass != null) {
                return res.readEntity(resultClass);
            } else {
                return null;
            }
        }
    }

    /**
     * Extracts the exception message from a {@link WebApplicationException} directly or from its referencing http
     * response if there isn't any.
     *
     * @param webAppEx the exception to extract the exception message from
     * @return the exception message
     */
    protected static String readExceptionMessage(final WebApplicationException webAppEx) {
        if (!StringUtils.isEmpty(webAppEx.getMessage())) {
            return webAppEx.getMessage();
        } else {
            try {
                return ResponseToExceptionMapper.readMessage(webAppEx.getResponse());
            } catch (IOException ex) {
                NodeLogger.getLogger(AbstractGatewayClient.class)
                    .error("Could not read exception message from server: " + ex.getMessage(), ex);
                return "Unknown message";
            }
        }
    }

}
