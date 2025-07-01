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
 *   Feb 28, (kampmann) created
 */
package org.knime.gateway.impl.webui.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import org.junit.BeforeClass;
import org.junit.Test;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.service.ComponentEditorService;
import org.knime.gateway.testing.helper.TestWorkflowCollection;
import org.knime.gateway.testing.helper.webui.ComponentServiceTestHelper;
import org.knime.js.core.JSCorePlugin;

/**
 * Tests methods in {@link DefaultComponentEditorService} which can't be covered by {@link ComponentServiceTestHelper}.
 *
 * @author Tobias Kampmann, TNG Technology Consulting GmbH
 */
public class DefaultComponentEditorServiceTest extends GatewayServiceTest {

    /**
     * Makes sure the org.knime.js.core plugin is activated which in provides a
     * {@link GatewayServiceFactory}-implementation via the respective extension point.
     */
    @BeforeClass
    public static void activateJsCore() {
        JSCorePlugin.class.getName();
    }

    /**
     * Makes sure that getting and setting of (configuration) layouts of {@link ComponentEditorService} works
     *
     * @throws Exception
     */
    @Test
    public void getAndSetLayout() throws Exception {
        var projectId = "wf_id";
        var wfm = loadWorkflow(TestWorkflowCollection.COMPONENT_REEXECUTION, projectId);
        var componentEditorService = new DefaultComponentEditorService();

        wfm.executeAllAndWaitUntilDone();

        var viewLayout =
            componentEditorService.getViewLayout(projectId, NodeIDEnt.getRootID(), new NodeIDEnt("root:3"));

        assertThat("View layout should not be null", viewLayout, not(is(null)));

        var mockLayout = "mocked layout";

        componentEditorService.setViewLayout(projectId, NodeIDEnt.getRootID(), new NodeIDEnt("root:3"), mockLayout);

        var viewLayoutAfterPush =
            componentEditorService.getViewLayout(projectId, NodeIDEnt.getRootID(), new NodeIDEnt("root:3"));

        assertThat("View layout should have been set", viewLayoutAfterPush, is(mockLayout));

        var configurationLayout =
            componentEditorService.getConfigurationLayout(projectId, NodeIDEnt.getRootID(), new NodeIDEnt("root:3"));

        assertThat("Configuration layout should not be null", configurationLayout, not(is(null)));

        var mockConfigurationLayout = "mocked configuration layout";

        componentEditorService.setConfigurationLayout(projectId, NodeIDEnt.getRootID(), new NodeIDEnt("root:3"),
            mockConfigurationLayout);

        var configurationLayoutAfterPush =
                componentEditorService.getConfigurationLayout(projectId, NodeIDEnt.getRootID(), new NodeIDEnt("root:3"));

        assertThat("Configuration layout should have been set", configurationLayoutAfterPush, is(mockConfigurationLayout));
    }

    /**
     * Makes sure that {@link DefaultComponentEditorService} returns the correct nodes contained in a component
     *
     * @throws Exception
     */
    @Test
    public void getNodes() throws Exception {
        var projectId = "wf_id";
        var wfm = loadWorkflow(TestWorkflowCollection.COMPONENT_REEXECUTION, projectId);
        var componentEditorService = new DefaultComponentEditorService();

        wfm.executeAllAndWaitUntilDone();

        var viewNodes =
            componentEditorService.getViewNodes(projectId, NodeIDEnt.getRootID(), new NodeIDEnt("root:3"));

        System.out.println(viewNodes);

        assertThat("View nodes should not be null", viewNodes, not(is(null)));

        var configurationNodes =
            componentEditorService.getConfigurationNodes(projectId, NodeIDEnt.getRootID(), new NodeIDEnt("root:3"));

        System.out.println(configurationNodes);

        assertThat("Configuration nodes should not be null", configurationNodes, not(is(null)));
    }

}
