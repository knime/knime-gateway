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
 */
package org.knime.gateway.impl.webui.service;

import java.util.Collections;

import org.junit.Test;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.impl.webui.AppState;
import org.knime.gateway.impl.webui.AppState.OpenedWorkflow;
import org.knime.gateway.testing.helper.TestWorkflowCollection;

/**
 * Tests for the {@link DefaultApplicationService}-implementation.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class ApplicationServiceTest extends GatewayServiceTest {

    /**
     * Test to get the app state.
     *
     * @throws Exception
     */
    @Test
    public void testGetAppState() throws Exception {
        String workflowProjectId = "the_workflow_project_id";
        loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI, workflowProjectId);
        DefaultApplicationService appService = DefaultApplicationService.getInstance();
        cr(appService.getState(), "empty_appstate");

        appService.setAppStateSupplier(() -> createAppState(workflowProjectId));
        cr(appService.getState(), "appstate");
    }

    private static AppState createAppState(final String workflowProjectId) {
        return () -> Collections.singletonList(createOpenedWorkflow(workflowProjectId));
    }

    private static OpenedWorkflow createOpenedWorkflow(final String workflowProjectId) {
        return new OpenedWorkflow() {

            @Override
            public String getProjectId() {
                return workflowProjectId;
            }

            @Override
            public String getWorkflowId() {
                return NodeIDEnt.getRootID().toString();
            }

            @Override
            public boolean isVisible() {
                return true;
            }
        };
    }
}