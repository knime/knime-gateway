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

import static com.knime.enterprise.server.rest.AutocloseableResponse.acr;
import static java.lang.String.format;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.function.Function;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.cxf.common.util.StringUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.eclipse.jface.preference.IPreferenceStore;
import org.knime.core.node.NodeLogger;
import org.knime.core.util.ThreadLocalHTTPAuthenticator;
import org.knime.core.util.ThreadLocalHTTPAuthenticator.AuthenticationCloseable;
import org.knime.core.util.Version;
import org.knime.workbench.ui.KNIMEUIPlugin;
import org.knime.workbench.ui.preferences.PreferenceConstants;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.knime.enterprise.server.rest.AutocloseableResponse;
import com.knime.enterprise.server.rest.api.Util;
import com.knime.enterprise.server.rest.client.AbstractClient;
import com.knime.enterprise.server.rest.providers.exception.ResponseToExceptionMapper;
import com.knime.gateway.entity.EntityBuilderManager;
import com.knime.gateway.entity.GatewayEntity;
import com.knime.gateway.entity.GatewayExceptionEnt;
import com.knime.gateway.entity.GatewayExceptionEnt.GatewayExceptionEntBuilder;
import com.knime.gateway.json.util.ObjectMapperUtil;
import com.knime.gateway.rest.client.providers.json.CollectionJSONDeserializer;
import com.knime.gateway.rest.client.providers.json.EntityJSONDeserializer;
import com.knime.gateway.rest.client.providers.json.StringJSONDeserializer;
import com.knime.gateway.rest.client.service.WorkflowClient;
import com.knime.gateway.service.ServiceException;

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
    private Version m_serverVersion;

    /**
     * Creates a new client.
     *
     * @param restAddress the server's REST address
     * @param jwt a json web token for authentication, can be <code>null</code> if not available
     * @param serverVersion the server's version or <code>null</code> if not known
     * @throws InstantiationException if providers for the the JAX-RS client cannot be instantiated
     * @throws IllegalAccessException if providers for the the JAX-RS client cannot be instantiated
     * @throws IOException if an I/O error occurs while instantiating the JAX-RS client
     * @throws NotAuthorizedException if the current user cannot be authenticated
     */
    public AbstractGatewayClient(final URI restAddress, final String jwt, final Version serverVersion)
        throws InstantiationException, IllegalAccessException, IOException {
        super(restAddress);
        m_serverVersion = serverVersion;
        @SuppressWarnings("unchecked")
        Class<C> resourceClass =
            (Class<C>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        List<Object> jaxRSProviders = Util.getJaxRSProviders();
        //is there a better way than adding the required provider manually?
        jaxRSProviders.add(new EntityJSONDeserializer());
        jaxRSProviders.add(new StringJSONDeserializer());
        jaxRSProviders.add(0, new CollectionJSONDeserializer());

        final IPreferenceStore prefStore = KNIMEUIPlugin.getDefault().getPreferenceStore();
        final int clientTimeout = prefStore.getInt(PreferenceConstants.P_REMOTE_WORKFLOW_EDITOR_CLIENT_TIMEOUT);
        m_client = createProxy(resourceClass, m_restAddress, null, null, jaxRSProviders, "Explorer01",
            Duration.ofMillis(clientTimeout));
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
        return doRequest(call, resultClass, null, null);
    }

    /**
     * Performs the client's request.
     *
     * Assumptions here: response content type is application/json!
     *
     * @param call function to call the right method on the proxy-client
     * @param resultClass the class of the expected response entity or <code>null</code> if none
     * @param sinceVersion the server version since the called endpoint has been introduced will throw an exception if
     *            the current server version is smaller than the since verison. Can be <code>null</code>.
     * @param missingMessage a error message if the endpoint is missing
     * @return status code and the response entity (or <code>null</code> if none)
     * @throws WebApplicationException in case of an exception as response, e.g. not found, etc.
     * @throws MissingEndpointException if the server is too old and doesn't know about the endpoint to be called
     * @since 4.11
     */
    protected <R> R doRequest(final Function<C, Response> call, final Class<R> resultClass,
        final String sinceVersion, final String missingMessage) {
        checkWithServerSersion(sinceVersion, missingMessage);
        if (resultClass != null) {
            return doRequestInternal(call, (res) -> res.readEntity(resultClass));
        } else {
            return null;
        }
    }

    private void checkWithServerSersion(final String versionToCheck, final String missingMessage) {
        if (m_serverVersion != null && versionToCheck != null) {
            Version v = new Version(versionToCheck);
            if (v.getMajor() > m_serverVersion.getMajor()
                || (v.getMajor() == m_serverVersion.getMajor() && v.getMinor() > m_serverVersion.getMinor())) {
                throw new MissingEndpointException(format(
                    "The connected server is too old (v%1$s) and lacks features:\n%2$s"
                        + "\nPlease consider to update the server.",
                    new Version(m_serverVersion.getMajor(), m_serverVersion.getMinor(), m_serverVersion.getRevision()),
                    missingMessage));
            }
        }
   }

    /**
     * Same as {@link #doRequest(Function, Class)} but for generic types.
     *
     * @param call see above
     * @param resultClass see above
     * @return see above
     * @throws WebApplicationException see above
     */
    protected <R> R doRequest(final Function<C, Response> call, final GenericType<R> resultClass) {
        if (resultClass != null) {
            return doRequestInternal(call, (res) -> res.readEntity(resultClass));
        } else {
            return null;
        }
    }

    /**
     * Same as {@link #doRequest(Function, Class)} but without any return type.
     *
     * @param call see above
     * @throws WebApplicationException see above
     */
    protected void doRequest(final Function<C, Response> call) {
        doRequestInternal(call, null);
    }

    private <R> R doRequestInternal(final Function<C, Response> call, final Function<Response, R> readEntity) {
         try (AuthenticationCloseable c = ThreadLocalHTTPAuthenticator.suppressAuthenticationPopups();
                AutocloseableResponse res = acr(call.apply(m_client))) {
            if (MediaType.TEXT_HTML.equals(res.getHeaderString("Content-Type"))) {
                throw new IllegalArgumentException(
                    "REST address '" + m_restAddress + "' does not point to a KNIME server's REST interface.");
            }
            if (readEntity != null) {
                return readEntity.apply(res);
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

    /**
     * Reads and parses the response body in to a {@link GatewayExceptionEnt}.
     *
     * @param e
     * @param service
     * @param method
     * @return the entity
     * @throws ServiceException if the parsing failed or the provided {@link WebApplicationException} doesn't wrap the
     *             expected response (i.e. status code)
     */
    protected static GatewayExceptionEnt readAndParseGatewayExceptionResponse(final WebApplicationException e,
        final String service, final String method) throws ServiceException {
        if (e.getResponse().getStatus() == Status.BAD_REQUEST.getStatusCode()) {
            try {
                return ObjectMapperUtil.getInstance().getObjectMapper().readValue(e.getMessage(),
                    GatewayExceptionEnt.class);
            } catch (IOException ex) {
                throw new ServiceException("Error response with status code '" + e.getResponse().getStatus()
                    + "' and message '" + readExceptionMessage(e) + "' couldn't be parsed to a gateway exception.", ex);
            }
        } else {
            int status = e.getResponse().getStatus();
            String exceptionName = ResponseToExceptionMap.getExceptionName(service, method, status).orElse(null);
            if (exceptionName != null) {
                return EntityBuilderManager.builder(GatewayExceptionEntBuilder.class)
                    .setExceptionMessage(e.getMessage()).setExceptionName(exceptionName).build();
            }
        }
        throw new ServiceException("Error response with status code '" + e.getResponse().getStatus() + "' and message: "
            + readExceptionMessage(e));
    }

    /**
     * Serializes an object (usually a {@link GatewayEntity}) into into a byte array.
     *
     * @param obj the object to serialize
     * @return byte array result
     */
    protected static byte[] toByteArray(final Object obj) {
        try {
            return ObjectMapperUtil.getInstance().getObjectMapper().writeValueAsString(obj).getBytes();
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
    }
}
