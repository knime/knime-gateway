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

        var mockLayout = "mocked layout";
        componentEditorService.setViewLayout(projectId, NodeIDEnt.getRootID(), new NodeIDEnt("root:3"), mockLayout);
        var viewLayoutAfterPush =
            componentEditorService.getViewLayout(projectId, NodeIDEnt.getRootID(), new NodeIDEnt("root:3"));
        assertThat("View layout should have been set", viewLayoutAfterPush, is(mockLayout));

        var mockConfigurationLayout = "mocked configuration layout";
        componentEditorService.setConfigurationLayout(projectId, NodeIDEnt.getRootID(), new NodeIDEnt("root:3"),
            mockConfigurationLayout);
        var configurationLayoutAfterPush =
            componentEditorService.getConfigurationLayout(projectId, NodeIDEnt.getRootID(), new NodeIDEnt("root:3"));
        assertThat("Configuration layout should have been set", configurationLayoutAfterPush,
            is(mockConfigurationLayout));
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

        var viewNodes = componentEditorService.getViewNodes(projectId, NodeIDEnt.getRootID(), new NodeIDEnt("root:3"));
        // TODO: Better assertion, use our object mapper instance, see other tests
        //assertThat("View nodes should not be null", viewNodes, is("[{\"layout\":{\"type\":\"view\",\"nodeID\":\"3\",\"resizeMethod\":\"aspectRatio4by3\",\"autoResize\":false,\"useLegacyMode\":false,\"sizeWidth\":false,\"resizeTolerance\":5,\"scrolling\":false,\"sizeHeight\":true},\"icon\":\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAACXBIWXMAAAsTAAALEwEAmpwYAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAABSSURBVHgBxZNLCgAgCAVfHSS6X0H3XxUtamGYgUqzVXTwg5xS1xBBaLXgxhE3M1iVOQMa33nfDPxmIE3fz4DrKOJ2idJW7A1op+ef0BqEWQAKBuVdBT9PJfc5AAAAAElFTkSuQmCC\",\"type\":\"view\",\"name\":\"Scatter Plot\",\"nodeID\":\"3\",\"containerLegacyModeEnabled\":false,\"availableInView\":true},{\"layout\":{\"type\":\"view\",\"nodeID\":\"4\",\"resizeMethod\":\"viewLowestElement\",\"autoResize\":true,\"useLegacyMode\":false,\"sizeWidth\":false,\"resizeTolerance\":5,\"scrolling\":false,\"sizeHeight\":true},\"icon\":\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAFo9M/3AAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAyhpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuNi1jMTExIDc5LjE1ODMyNSwgMjAxNS8wOS8xMC0wMToxMDoyMCAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIgeG1sbnM6c3RSZWY9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZVJlZiMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENDIDIwMTUgKE1hY2ludG9zaCkiIHhtcE1NOkluc3RhbmNlSUQ9InhtcC5paWQ6NjAwRUJFNjcwRTFDMTFFNkE3NkZCQjA2REE3QjhBQ0IiIHhtcE1NOkRvY3VtZW50SUQ9InhtcC5kaWQ6NjAwRUJFNjgwRTFDMTFFNkE3NkZCQjA2REE3QjhBQ0IiPiA8eG1wTU06RGVyaXZlZEZyb20gc3RSZWY6aW5zdGFuY2VJRD0ieG1wLmlpZDoxMzY0M0M1MDBFMTAxMUU2QTc2RkJCMDZEQTdCOEFDQiIgc3RSZWY6ZG9jdW1lbnRJRD0ieG1wLmRpZDo2MDBFQkU2NjBFMUMxMUU2QTc2RkJCMDZEQTdCOEFDQiIvPiA8L3JkZjpEZXNjcmlwdGlvbj4gPC9yZGY6UkRGPiA8L3g6eG1wbWV0YT4gPD94cGFja2V0IGVuZD0iciI/PpSP8JMAAAKoSURBVHjaYpCWljJWV1d9zMQABHbTLGQAAogBKPI/bKbPQwb/Prf/QMAAEEBgkZsPbv4/cHL/f78O5xwGkCgMP/YP/A8QQGAVIOzf48oC1nL/2X2Q3v+Zh+LAZjDZmFozZByIY3h68jknyCpGsDIguKJs8FBAV0kOIIDAZiCDR48e/g+d4f0maXsU2AgWkOqW1fUMT8UfMMQJpDPMfzyFgZGZiX2ux1JGFCNBIHiy56l/f//vrKy80s4lJXL9z7fvl1AUwMAFSfXNIubaPn+//WQACCBGDQ31p58/f5ZCkjcxLdFtZGRkeL0uf0ci065du6SePHnKAMPcchxnJIzEvRn+/ecFqWZiYmJmOHThAEJ7jj7D11ff/q0r3BkCVvDv31+GZe/mMSQsC2ZYf2gdA78sL8P72+8NYRoY1dRUHjFJMsjaVVsAvcfI8OLy67sb8rerwI2EhbxXs6Ns8HTvRyD2eQm1zZeV9B+e5JDmwurNJwFBYMEPl+89YmLAAkD+BwFQQAEEGCgcnkRFRUknJCQwgHyEDEAeWLBgAcPChQsf6GQo9/Mr8ndxiXKxv7n69tXqzC3iYE+AIvPkyVPASJXGZhnD06dPGZJmRDHI28oyMLExMby7+fbp90+/TDaX7n4BkmeBKSxZks/wXuQNw593vxjyDCsYjDWNGX78/M7QubeFQdFFnuH/r/8MLy+82ru+YIcLsgWMKirKD+Pi4uR8Ar0ZFl6Yy/BN4jMDCysjw7eXP4CeZGLglGFn+Pbm2/8Pdz9Ubyzd3Y7uQoxQ9G5xUuES5NwhpCagzMTOwvDuzvt3315+ddtcufcsNi9ijQZsAJQCBXVVfJi52OEx8f7ynS1MDEQCFi5OPZhmEACxQWJEu+AUpwwXmwD3SpAmWBr49eFrOABu3kNaCuHSIAAAAABJRU5ErkJggg==\",\"type\":\"view\",\"name\":\"Boolean Widget\",\"nodeID\":\"4\",\"containerLegacyModeEnabled\":false,\"availableInView\":true}]"));

        var configurationNodes =
            componentEditorService.getConfigurationNodes(projectId, NodeIDEnt.getRootID(), new NodeIDEnt("root:3"));
        assertThat("Configuration nodes should not be null", configurationNodes, is("[]"));
    }

}
