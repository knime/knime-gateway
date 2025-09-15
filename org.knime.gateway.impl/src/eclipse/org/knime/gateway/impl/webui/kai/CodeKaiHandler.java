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
 *   15 Sep 2023 (chaubold): created
 */
package org.knime.gateway.impl.webui.kai;

import java.io.IOException;

import org.knime.gateway.api.webui.entity.KaiUsageEnt;
import org.knime.gateway.impl.webui.kai.KaiHandlerFactory.AuthTokenProvider;

/**
 * Interface for a service that provides code suggestions via K-AI.
 *
 * @author Carsten Haubold, KNIME GmbH, Konstanz, Germany
 * @author Benjamin Wilhelm, KNIME GmbH, Berlin, Germany
 * @since 5.5
 */
public interface CodeKaiHandler {

    /**
     * @return whether all K-AI-related features are enabled
     */
    boolean isKaiEnabled();

    /**
     * @return the ID of the Hub hosting the backend
     */
    String getHubId();

    /**
     * @param projectId the ID of the workflow-project required for authentication.
     * @return the AI usage for the currently authenticated user.
     * @since 5.8
     */
    KaiUsageEnt getUsage(String projectId);

    /**
     * Log in to the currently selected Hub end point
     *
     * @return true if already logged in or the login was successful
     */
    boolean loginToHub();

    /**
     * @param projectId the projectId of the current project
     * @return true if the user is logged in to the currently selected Hub end point
     */
    boolean isLoggedIn(String projectId);

    /**
     * @return the disclaimer users have to accept before they can use the code generation AI
     */
    String getDisclaimer();

    /**
     * Send a POST request to the provided end point path at the selected KNIME Hub
     *
     * @param projectId the projectId of the current project
     * @param endpointPath The end point at the selected KNIME Hub to call
     * @param request An object that will be turned into JSON using Jackson and sent as data to the endpoint
     * @return The response of the request as String (probably contains JSON content)
     * @throws IOException In case of connection errors or malformed request data.
     */
    String sendRequest(String projectId, final String endpointPath, final Object request) throws IOException;

    /**
     * A data service dependency that provides the project id of the current project such that it can be passed to
     * {@link CodeKaiHandler#isLoggedIn} and {@link CodeKaiHandler#sendRequest} which needs this information to get the
     * authentication token from the {@link AuthTokenProvider}.
     *
     * @param projectId the projectId of the current project
     */
    public record ProjectId(String projectId) {
    }
}
