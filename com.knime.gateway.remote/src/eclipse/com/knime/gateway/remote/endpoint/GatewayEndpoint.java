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
 *   Sep 12, 2017 (hornm): created
 */
package com.knime.gateway.remote.endpoint;

import org.knime.gateway.workflow.service.GatewayService;

/**
 * A gateway endpoint is for instance a http-server, a message-queue client, etc. that receives respective messages that
 * are usually translated in to the respective {@link GatewayService}-calls on the remote side.
 *
 * It is the logical counterpart of a respective service factory implementation (see
 * org.knime.gateway.local.service.ServiceFactory), that essentially sends the messages to be received by the gateway
 * endpoint (possibly mediated by the KNIME server in between).
 *
 * @author Martin Horn, University of Konstanz
 */
public interface GatewayEndpoint {

    static final String EXT_POINT_ID = "com.knime.gateway.remote.endpoint.GatewayEndpoint";

    static final String EXT_POINT_ATTR = "GatewayEndpoint";

    /**
     * Things to be done on initialization of the gateway endpoint. Just called once.
     */
    void start();

    /**
     * Called to shutdown the endpoint.
     */
    void stop();

    /**
     * Called when a new workflow project is available. Use the {@link GatewayEndpointManager}, e.g. in order to access
     * the actual workflow by using the given workflow project id.
     *
     * @param workflowProjectID the id of the newly added project
     */
    void onWorklfowProjectAdded(String workflowProjectID);

    /**
     * @param workflowProjectID the id of the project that has been removed
     */
    void onWorkflowProjectRemoved(String workflowProjectID);

}
