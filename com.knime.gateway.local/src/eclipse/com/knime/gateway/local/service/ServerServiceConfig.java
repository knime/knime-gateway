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
package com.knime.gateway.local.service;

import java.net.URI;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.knime.core.node.util.CheckUtils;
import org.knime.core.util.Version;

/**
 * {@link ServiceConfig}-implementation to configure services that communicate with a server specified by a host name
 * and a port.
 *
 * @author Martin Horn, University of Konstanz
 */
public class ServerServiceConfig implements ServiceConfig {
    private final String m_jwt;
    private final URI m_uri;
    private Version m_serverVersion;

    /**
     * @param uri the server uri
     * @param jwt a json web token for authentication, can be <code>null</code> if there is none
     * @param serverVersion the server's version
     * @since 4.11
     */
    public ServerServiceConfig(final URI uri, final String jwt, final Version serverVersion) {
        m_serverVersion = serverVersion;
        CheckUtils.checkArgumentNotNull(uri);
        m_uri = uri;
        m_jwt = jwt;
    }

    /**
     * @param uri the server uri
     * @param jwt a json web token for authentication, can be <code>null</code> if there is none
     */
    public ServerServiceConfig(final URI uri, final String jwt) {
        this(uri, jwt, null);
    }

    /**
     * @return the server uri
     */
    public URI getURI() {
        return m_uri;
    }

    /**
     * @return the json web token or an empty optional if none available
     */
    public Optional<String> getJWT() {
        return Optional.ofNullable(m_jwt);
    }

    /**
     * @return the server version or an empty optional if none available
     * @since 4.11
     */
    public Optional<Version> getServerVersion() {
        return Optional.ofNullable(m_serverVersion);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + m_uri.hashCode();
        result = prime * result + m_jwt.hashCode();
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ServerServiceConfig other = (ServerServiceConfig)obj;
        if (!m_uri.equals(other.m_uri)) {
            return false;
        } else if (!StringUtils.equals(m_jwt, other.m_jwt)) {
            return false;
        }
        return true;
    }
}
