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
 *   Jan 23, 2017 (hornm): created
 */
package org.knime.gateway.jsonrpc.local;

import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.ws.rs.core.UriBuilder;

import org.apache.http.conn.ssl.BrowserCompatHostnameVerifier;
import org.knime.gateway.jsonrpc.JsonRpcUtil;
import org.knime.gateway.jsonrpc.local.service.util.ServiceInterface2JsonRpcMap;
import org.knime.gateway.local.service.ServerServiceConfig;
import org.knime.gateway.local.service.ServiceConfig;
import org.knime.gateway.local.service.ServiceFactory;
import org.knime.gateway.service.GatewayService;
import org.knime.gateway.service.ServiceException;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.googlecode.jsonrpc4j.ProxyUtil;

/**
 * Service factories whose returned services talk to a http(s) server at "v4/gateway/jsonrpc" by 'posting' json-rpc
 * messages.
 *
 * @author Martin Horn, University of Konstanz
 */
public class JsonRpcClientServiceFactory implements ServiceFactory {

    private static final String GATEWAY_PATH = "/v4/jobs/{uuid}/gateway/jsonrpc";

    /**
     * COPIED from com.knime.enterprise.server.rest.api.Util!
     */
    private static final HostnameVerifier HOSTNAME_VERIFIER = new HostnameVerifier() {
        private final HostnameVerifier m_defaultVerifier = new BrowserCompatHostnameVerifier();

        @Override
        public boolean verify(final String hostname, final SSLSession session) {
            // we accept if the certificate hostname does not match the actual hostname but the certificate was
            // signed by us; this is for default server installations that all use a common certificate
            try {
                return m_defaultVerifier.verify(hostname, session)
                    || session.getPeerCertificateChain()[0].getSubjectDN().toString().equals(
                        "CN=default-server-installation.knime.local, O=KNIME.com AG, L=Atlantis, ST=Utopia, C=AA");
            } catch (SSLPeerUnverifiedException ex) {
                return false;
            }
        }
    };

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

            JsonRpcUtil.addMixIns(mapper);

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
                        throw new ServiceException(jsonObject.get("error").get("message").asText());
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
                    String jobId = (String)((Object[])argument)[0];
                    setServiceUrl(UriBuilder.fromUri(uri).path(GATEWAY_PATH).build(jobId).toURL());
                    return super.invoke(methodName, argument, returnType, extraHeaders);
                }
            };
            httpClient.setHostNameVerifier(HOSTNAME_VERIFIER);
            return ProxyUtil.createClientProxy(proxyInterface.getClassLoader(), proxyInterface, httpClient);
        } catch (MalformedURLException ex) {
            throw new RuntimeException(ex);
        }
    }
}
