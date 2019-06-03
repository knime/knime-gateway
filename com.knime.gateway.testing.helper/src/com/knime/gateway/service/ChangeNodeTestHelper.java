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
package com.knime.gateway.service;

import static com.knime.gateway.entity.EntityBuilderManager.builder;
import static com.knime.gateway.entity.NodeIDEnt.getRootID;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.StringReader;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
import org.knime.core.node.NodeSettings;
import org.knime.core.node.config.base.JSONConfig;
import org.knime.core.node.config.base.JSONConfig.WriterConfig;

import com.knime.gateway.entity.BoundsEnt;
import com.knime.gateway.entity.BoundsEnt.BoundsEntBuilder;
import com.knime.gateway.entity.GatewayEntity;
import com.knime.gateway.entity.NodeIDEnt;
import com.knime.gateway.entity.NodeSettingsEnt.NodeSettingsEntBuilder;
import com.knime.gateway.service.util.ServiceExceptions.ActionNotAllowedException;
import com.knime.gateway.service.util.ServiceExceptions.IllegalStateException;
import com.knime.gateway.service.util.ServiceExceptions.InvalidSettingsException;
import com.knime.gateway.service.util.ServiceExceptions.NodeNotFoundException;
import com.knime.gateway.testing.helper.ResultChecker;
import com.knime.gateway.testing.helper.ServiceProvider;
import com.knime.gateway.testing.helper.WorkflowExecutor;
import com.knime.gateway.testing.helper.WorkflowLoader;

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
    	UUID wfId = loadWorkflow(TestWorkflow.WORKFLOW);
    	UUID longrunningWfId = loadWorkflow(TestWorkflow.WORKFLOW_LONGRUNNING);

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
        UUID wfId = loadWorkflow(TestWorkflow.WORKFLOW);

        NodeSettings settings = new NodeSettings("configuration");
        JSONConfig.readJSON(settings,
            new StringReader(ns().getNodeSettings(wfId, new NodeIDEnt(1)).getJsonContent()));

        //manipulate settings and save back to server
        settings.getConfig("model").addInt("patcount", 20);
        String settingsString = JSONConfig.toJSONString(settings, WriterConfig.PRETTY);
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
        UUID longrunningWfId = loadWorkflow(TestWorkflow.WORKFLOW_LONGRUNNING);
        executeWorkflowAsync(longrunningWfId);
        try {
            ns().setNodeSettings(longrunningWfId, new NodeIDEnt(13), settingsBuilder.build());
            fail("Expected ServiceException to be thrown");
        } catch (IllegalStateException e) {
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
        UUID wfId = loadWorkflow(TestWorkflow.WORKFLOW);

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
