/*
 * ------------------------------------------------------------------------
 *
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
 * History
 *   27.04.2020 (hornm): created
 */
package org.knime.next.server;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.UriBuilder;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.HandlerWrapper;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.PathResource;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.knime.core.node.NodeLogger;

/**
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class KnimeNextServer {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(KnimeNextServer.class);

    private int m_port;

    private Server m_server;

    /**
     *
     */
    public KnimeNextServer(final int port) {
        m_port = port;
    }

    public void start() {
        URI baseUri = UriBuilder.fromUri("http://localhost/").port(m_port).build();
        ResourceConfig config = new KnimeServerApplication();
        m_server = JettyHttpContainerFactory.createServer(baseUri, config, false);
        ResourceHandler rh = new ResourceHandler();
        //Bundle myBundle = FrameworkUtil.getBundle(getClass());
        //URL webApp = myBundle.getEntry("ui");
        //TODO
        rh.setBaseResource(
            new PathResource(new File("/home/hornm/dev-knime/workspace/knime-gateway/knime-next-ui/dist")));
        HandlerList handlers = new HandlerList(rh, new AddHeaders(m_server.getHandler()));
        m_server.setHandler(handlers);
        try {
            m_server.start();
        } catch (Exception ex) {
            LOGGER.error("Error occurred while starting Jetty", ex);
            m_server.destroy();
        }
    }

    public void stop() {
        if (m_server != null) {
            m_server.destroy();
        }
    }

    private class AddHeaders extends HandlerWrapper {

        AddHeaders(final Handler handler) {
            setHandler(handler);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void handle(final String target, final Request baseRequest, final HttpServletRequest request,
            final HttpServletResponse response) throws IOException, ServletException {
            response.setHeader("Access-Control-Allow-Origin", "*");
            super.handle(target, baseRequest, request, response);
        }

    }

}
