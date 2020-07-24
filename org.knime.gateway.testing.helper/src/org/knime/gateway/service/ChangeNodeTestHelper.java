/*
 * ------------------------------------------------------------------------
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
 */
package org.knime.gateway.service;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.knime.gateway.api.entity.EntityBuilderManager.builder;
import static org.knime.gateway.api.entity.NodeIDEnt.getRootID;

import java.io.StringReader;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
import org.knime.core.node.NodeSettings;
import org.knime.core.node.config.base.JSONConfig;
import org.knime.core.node.config.base.JSONConfig.WriterConfig;
import org.knime.gateway.api.entity.BoundsEnt;
import org.knime.gateway.api.entity.BoundsEnt.BoundsEntBuilder;
import org.knime.gateway.api.entity.GatewayEntity;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.entity.NodeSettingsEnt.NodeSettingsEntBuilder;
import org.knime.gateway.api.service.util.ServiceExceptions;
import org.knime.gateway.api.service.util.ServiceExceptions.ActionNotAllowedException;
import org.knime.gateway.api.service.util.ServiceExceptions.InvalidSettingsException;
import org.knime.gateway.api.service.util.ServiceExceptions.NodeNotFoundException;
import org.knime.gateway.testing.helper.ResultChecker;
import org.knime.gateway.testing.helper.ServiceProvider;
import org.knime.gateway.testing.helper.WorkflowExecutor;
import org.knime.gateway.testing.helper.WorkflowLoader;

/**
 * Tests to make changes to single nodes, such as to the execution status (execute, reset, cancel) or to the node
 * settings.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class ChangeNodeTestHelper extends AbstractGatewayServiceTestHelper {

    /**
     * See
     * {@link AbstractGatewayServiceTestHelper#AbstractGatewayServiceTestHelper(String, WorkflowLoader, ServiceProvider, ResultChecker)}.
     *
     * @param workflowLoader
     * @param workflowExecutor
     * @param serviceProvider
     * @param entityResultChecker
     */
    public ChangeNodeTestHelper(final ServiceProvider serviceProvider, final ResultChecker entityResultChecker,
        final WorkflowLoader workflowLoader, final WorkflowExecutor workflowExecutor) {
        super("changenode", serviceProvider, entityResultChecker, workflowLoader, workflowExecutor);
    }

    /**
     * Tests to reset, cancel and execute individual nodes and all nodes from the job view.
     *
     * @throws Exception if an error occurs
     */
    public void testChangeNodeState() throws Exception {
    	UUID wfId = loadWorkflow(TestWorkflow.GENERAL);
    	UUID longrunningWfId = loadWorkflow(TestWorkflow.LONGRUNNING);

        //execute individual node
        ns().changeAndGetNodeState(wfId, new NodeIDEnt(2), "execute");
        Awaitility.await().atMost(2, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS).untilAsserted(() -> {
            GatewayEntity wfEntity = ws().getWorkflow(wfId, getRootID()).getWorkflow();
            cr(wfEntity, "worklfowent_root_executed_till_node_2");
        });

        //reset node #1
        ns().changeAndGetNodeState(wfId, new NodeIDEnt(1), "reset");
        Awaitility.await().atMost(2, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS).untilAsserted(() -> {
            GatewayEntity wfEntity = ws().getWorkflow(wfId, getRootID()).getWorkflow();
            cr(wfEntity, "workflowent_root");
        });

        //execute a node within a metanode
        ns().changeAndGetNodeState(wfId, new NodeIDEnt(6, 3), "execute");
        Awaitility.await().atMost(2, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS).untilAsserted(() -> {
            GatewayEntity wfEntity = ws().getWorkflow(wfId, getRootID()).getWorkflow();
            cr(wfEntity, "worklfowent_root_executed_till_node_6_3");
        });

        //reset entire workflow by reseting the first node
        ns().changeAndGetNodeState(wfId, new NodeIDEnt(1), "reset");
        Awaitility.await().atMost(2, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS).untilAsserted(() -> {
            GatewayEntity wfEntity = ws().getWorkflow(wfId, getRootID()).getWorkflow();
            cr(wfEntity, "workflowent_root");
        });

        //execute a metanode
        ns().changeAndGetNodeState(wfId, new NodeIDEnt(6), "execute");
        Awaitility.await().atMost(2, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS).untilAsserted(() -> {
            GatewayEntity wfEntity = ws().getWorkflow(wfId, getRootID()).getWorkflow();
            cr(wfEntity, "worklfowent_root_executed_till_node_6_3");
        });

        //execute & cancel individual node
        //execute all
        ns().changeAndGetNodeState(longrunningWfId, getRootID(), "execute");
        Awaitility.await().atMost(2, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS).untilAsserted(() -> {
            GatewayEntity wfEntity = ws().getWorkflow(longrunningWfId, getRootID()).getWorkflow();
            cr(wfEntity, "workflowent_longrunning_root_executing");
        });

        //cancel node 13
        ns().changeAndGetNodeState(longrunningWfId, new NodeIDEnt(13), "cancel");
        Awaitility.await().atMost(2, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS).untilAsserted(() -> {
            GatewayEntity wfEntity = ws().getWorkflow(longrunningWfId, getRootID()).getWorkflow();
            cr(wfEntity, "workflowent_longrunning_root_canceled_node_13");
        });

        //check exceptions
        //execute all again
        ns().changeAndGetNodeState(longrunningWfId, getRootID(), "execute");
        //try canceling the first node (that has an executing successor)
        try {
            ns().changeAndGetNodeState(longrunningWfId, new NodeIDEnt(1), "reset");
            fail("Expected ServiceException to be thrown");
        } catch (ActionNotAllowedException e) {
            assertThat("Unexpected exception message", e.getMessage(),
                containsString("Cannot reset node (wrong state of node or successors)"));
        }
    }

    /**
     * Tests to change the node's settings.
     *
     * @throws Exception if an error occurs
     */
    public void testChangeNodeSettings() throws Exception {
        UUID wfId = loadWorkflow(TestWorkflow.GENERAL);

        NodeSettings settings = new NodeSettings("configuration");
        JSONConfig.readJSON(settings,
            new StringReader(ns().getNodeSettings(wfId, new NodeIDEnt(1)).getJsonContent()));

        //manipulate settings and save back to server
        settings.getConfig("model").addInt("patcount", 20);
        String settingsString = JSONConfig.toJSONString(settings, WriterConfig.DEFAULT);
        NodeSettingsEntBuilder settingsBuilder = builder(NodeSettingsEntBuilder.class).setJsonContent(settingsString);
        ns().setNodeSettings(wfId, new NodeIDEnt(1), settingsBuilder.build());

        //check the changed settings
        assertThat("Unexpected node settings after modification",
            ns().getNodeSettings(wfId, new NodeIDEnt(1)).getJsonContent(), is(settingsString));

        //check invalid settings
        try {
            ns().setNodeSettings(wfId, new NodeIDEnt(2), settingsBuilder.build());
            fail("Expected ServiceException to be thrown");
        } catch (InvalidSettingsException e) {
            assertThat("Unexpected exception message", e.getMessage(),
                containsString("Config for key \"column-filter\" not found"));
        }

        //check illegal node state
        UUID longrunningWfId = loadWorkflow(TestWorkflow.LONGRUNNING);
        executeWorkflowAsync(longrunningWfId);
        try {
            ns().setNodeSettings(longrunningWfId, new NodeIDEnt(13), settingsBuilder.build());
            fail("Expected ServiceException to be thrown");
        } catch (ServiceExceptions.IllegalStateException e) {
            assertThat("Unexpected exception message", e.getMessage(),
                containsString("Cannot load settings into node; it is executing or has executing successors"));
        }
    }

    /**
     * Tests to change node's bounds (e.g. move a node).
     *
     * @throws Exception if something goes wrong
     */
    public void testChangeNodeBounds() throws Exception {
        UUID wfId = loadWorkflow(TestWorkflow.GENERAL);

        BoundsEnt oldBounds = ns().getNode(wfId, new NodeIDEnt(1)).getUIInfo().getBounds();
        BoundsEnt newBounds = builder(BoundsEntBuilder.class).setWidth(oldBounds.getWidth())
            .setHeight(oldBounds.getHeight()).setX(0).setY(0).build();
        ns().setNodeBounds(wfId, new NodeIDEnt(1), newBounds);
        cr(ws().getWorkflow(wfId, getRootID()).getWorkflow(), "workflowent_root_moved_node_1");

        //move back
        ns().setNodeBounds(wfId, new NodeIDEnt(1), oldBounds);
        cr(ws().getWorkflow(wfId, getRootID()).getWorkflow(), "workflowent_root");

        //settings different width and height
        ns().setNodeBounds(wfId, new NodeIDEnt(1),
            builder(BoundsEntBuilder.class).setX(0).setY(0).setWidth(0).setHeight(0).build());
        cr(ws().getWorkflow(wfId, getRootID()).getWorkflow(), "workflowent_root_moved_node_1_0_bounds");

        //move node within a metanode
        ns().setNodeBounds(wfId, new NodeIDEnt(6, 3), oldBounds);
        cr(ws().getWorkflow(wfId, new NodeIDEnt(6)).getWorkflow(), "workflowent_6_moved_node_3");

        //move node within a component
        ns().setNodeBounds(wfId, new NodeIDEnt(9, 0, 7), oldBounds);
        cr(ws().getWorkflow(wfId, new NodeIDEnt(9)).getWorkflow(), "workflowent_9_moved_node_7");

        //try moving a non-existing node
        try {
            ns().setNodeBounds(wfId, new NodeIDEnt(99), oldBounds);
            fail("Expected ServiceException to be thrown");
        } catch (NodeNotFoundException e) {
            assertThat("Unexpected exception message", e.getMessage(),
                containsString("No such node ID"));
        }
    }
}
