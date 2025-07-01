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

import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.service.CompositeViewService;
import org.knime.gateway.testing.helper.TestWorkflowCollection;
import org.knime.gateway.testing.helper.webui.ComponentServiceTestHelper;
import org.knime.js.core.JSCorePlugin;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Tests methods in {@link DefaultCompositeViewService} which can't be covered by {@link ComponentServiceTestHelper}.
 *
 * @author Tobias Kampmann, TNG Technology Consulting GmbH
 */
public class DefaultCompositeViewServiceTest extends GatewayServiceTest {

    /**
     * Makes sure the org.knime.js.core plugin is activated which in provides a
     * {@link GatewayServiceFactory}-implementation via the respective extension point.
     */
    @BeforeClass
    public static void activateJsCore() {
        JSCorePlugin.class.getName();
    }

    private final ObjectMapper m_mapper = new ObjectMapper();

    /**
     * Makes sure that {@link CompositeViewService#triggerComponentReexecution(String, NodeIDEnt, NodeIDEnt, String, Map)} works
     *
     * @throws Exception
     */
    @Test
    public void reexecuteComponentViaBooleanWidget() throws Exception {
        var projectId = "wf_id";
        var wfm = loadWorkflow(TestWorkflowCollection.COMPONENT_REEXECUTION, projectId);
        var cvs = new DefaultCompositeViewService();

        var widgetBooleanPath = "/webNodes/3:0:4/viewRepresentation/currentValue/boolean";
        var scatterPlotInitialDataPath = "/nodeViews/3:0:3/initialData";
        var scatterPlotYAxisLabelPath = "/result/settings/yAxisLabel";

        wfm.executeAllAndWaitUntilDone();

        var compositeViewPage =
            (String)cvs.getCompositeViewPage(projectId, NodeIDEnt.getRootID(), null, new NodeIDEnt("root:3"));
        var beforeJson = m_mapper.readTree(compositeViewPage);
        var beforeBoolean = beforeJson.at(widgetBooleanPath);
        var scatterPlotBefore = m_mapper.readTree(beforeJson.at(scatterPlotInitialDataPath).asText());

        assertThat("yAxisLabel should _not_ have value 'test'",
            scatterPlotBefore.at(scatterPlotYAxisLabelPath).asText(), not(is("test")));

        cvs.triggerComponentReexecution(projectId, NodeIDEnt.getRootID(), new NodeIDEnt("root:3"), "3:0:4", Map
            .of("3:0:4", "{\"@class\":\"org.knime.js.base.node.base.input.bool.BooleanNodeValue\",\"boolean\":true}"));
        cvs.pollComponentReexecutionStatus(projectId, NodeIDEnt.getRootID(), new NodeIDEnt("root:3"), "3:0:4");

        wfm.executeAllAndWaitUntilDone();

        var compositeViewPageAfterReexecution =
            (String)cvs.getCompositeViewPage(projectId, NodeIDEnt.getRootID(), null, new NodeIDEnt("root:3"));
        var afterJson = m_mapper.readTree(compositeViewPageAfterReexecution);
        var afterBoolean = afterJson.at(widgetBooleanPath);
        var scatterPlotAfter = m_mapper.readTree(afterJson.at(scatterPlotInitialDataPath).asText());

        assertThat("Boolean value should have changed after re-execution", afterBoolean, not(is(beforeBoolean)));
        assertThat("yAxisLabel should have value 'test'", scatterPlotAfter.at(scatterPlotYAxisLabelPath).asText(),
            is("test"));
    }

    /**
     * Makes sure that {@link CompositeViewService#setViewValuesAsNewDefault(String, NodeIDEnt, NodeIDEnt, Map)} works
     *
     * @throws Exception
     */
    @Test
    public void setViewValuesAsNewDefault() throws Exception {
        var projectId = "wf_id";
        var wfm = loadWorkflow(TestWorkflowCollection.COMPONENT_REEXECUTION, projectId);
        var cvs = new DefaultCompositeViewService();

        var widgetBooleanPath = "/webNodes/3:0:4/viewRepresentation/currentValue/boolean";

        wfm.executeAllAndWaitUntilDone();

        var compositeViewPage =
            (String)cvs.getCompositeViewPage(projectId, NodeIDEnt.getRootID(), null, new NodeIDEnt("root:3"));
        var beforeJson = m_mapper.readTree(compositeViewPage);
        var beforeBooleanDefault = beforeJson.at("/webNodes/3:0:4/viewRepresentation/defaultValue");

        cvs.setViewValuesAsNewDefault(projectId, NodeIDEnt.getRootID(), new NodeIDEnt("root:3"), Map.of("3:0:4",
            "{\"@class\":\"org.knime.js.base.node.base.input.bool.BooleanNodeValue\",\"boolean\":true}"));

        wfm.executeAllAndWaitUntilDone();

        var compositeViewPageAfterReexecution =
            (String)cvs.getCompositeViewPage(projectId, NodeIDEnt.getRootID(), null, new NodeIDEnt("root:3"));
        var afterJson = m_mapper.readTree(compositeViewPageAfterReexecution);
        var afterBooleanDefault = afterJson.at(widgetBooleanPath);

        assertThat("Boolean value should have changed after re-execution", beforeBooleanDefault,
            not(is(afterBooleanDefault)));
    }

}
