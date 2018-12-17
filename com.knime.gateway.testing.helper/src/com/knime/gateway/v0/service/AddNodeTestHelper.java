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
package com.knime.gateway.v0.service;

import static com.knime.gateway.entity.EntityBuilderManager.builder;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.UUID;

import org.hamcrest.core.Is;
import org.junit.Assert;

import com.knime.gateway.testing.helper.ResultChecker;
import com.knime.gateway.testing.helper.ServiceProvider;
import com.knime.gateway.testing.helper.WorkflowLoader;
import com.knime.gateway.v0.entity.NodeFactoryKeyEnt.NodeFactoryKeyEntBuilder;
import com.knime.gateway.v0.service.util.ServiceExceptions.InvalidRequestException;
import com.knime.gateway.v0.service.util.ServiceExceptions.NodeNotFoundException;

/**
 * Tests to make changes to single nodes, such as to the execution status (execute, reset, cancel) or to the node
 * settings.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class AddNodeTestHelper extends AbstractGatewayServiceTestHelper {


    /**
     * See
     * {@link AbstractGatewayServiceTestHelper#AbstractGatewayServiceTestHelper(String, WorkflowLoader, ServiceProvider, ResultChecker)}.
     *
     * @param workflowLoader
     * @param serviceProvider
     * @param entityResultChecker
     */
    public AddNodeTestHelper(final ServiceProvider serviceProvider, final ResultChecker entityResultChecker,
        final WorkflowLoader workflowLoader) {
        super("addnode", serviceProvider, entityResultChecker, workflowLoader);
    }

    /**
     * Tests to add new nodes to a workflow.
     *
     * @throws Exception if an error occurs
     */
    public void testAddNode() throws Exception {
    	UUID wfId = loadWorkflow(TestWorkflow.WORKFLOW);

        //create and add a new node
        NodeFactoryKeyEntBuilder nodeFactoryKeyBuilder = builder(NodeFactoryKeyEntBuilder.class)
            .setClassName("org.knime.base.node.io.filereader.FileReaderNodeFactory");
        String newNodeID = ns().createNode(wfId, 100, 100, nodeFactoryKeyBuilder.build(), null);
        Assert.assertThat(newNodeID, Is.is("23"));
        cr(ws().getWorkflow(wfId).getWorkflow(), "workflowent_root_new_node");

        //create and add a new node in sub workflow
        newNodeID = ns().createNode(wfId, 100, 100, nodeFactoryKeyBuilder.build(), "6");
        Assert.assertThat(newNodeID, Is.is("6:4"));
        cr(ws().getWorkflow(wfId).getWorkflow(), "workflowent_6_new_node");

        //create and add a node created from a dynamic node factory (in particular the Box Plot (JavaScript)-node)
        nodeFactoryKeyBuilder.setClassName("org.knime.dynamic.js.v30.DynamicJSNodeFactory").setSettings(
            "{\"name\":\"settings\",\"value\":{\"nodeDir\":{\"type\":\"string\",\"value\":\"org.knime.dynamic.js.base:nodes/:boxplot_v2\"}}}");
        newNodeID = ns().createNode(wfId, 100, 100, nodeFactoryKeyBuilder.build(), null);
        cr(ws().getWorkflow(wfId).getWorkflow(), "workflowent_root_new_dynamic_node");

        //try adding a dynamic node but without providing the required settings
        nodeFactoryKeyBuilder.setSettings(null);
        try {
            ns().createNode(wfId, 100, 100, nodeFactoryKeyBuilder.build(), null);
            fail("Expected ServiceException to be thrown");
        } catch (InvalidRequestException e) {
            assertThat("Unexpected exception message", e.getMessage(),
                containsString("Settings are expected for dynamic node"));
        }

        //try adding a dynamic node with corrupted settings
        nodeFactoryKeyBuilder.setSettings("nonsense-settings");
        try {
            ns().createNode(wfId, 100, 100, nodeFactoryKeyBuilder.build(), null);
            fail("Expected ServiceException to be thrown");
        } catch (InvalidRequestException e) {
            assertThat("Unexpected exception message", e.getMessage(),
                containsString("Problem reading factory settings while trying to create node from"));
        }

        //try adding a node with factory key that doesn't exist
        nodeFactoryKeyBuilder.setClassName("nonsense-node-factory-class");
        try {
            ns().createNode(wfId, 100, 100, nodeFactoryKeyBuilder.build(), null);
            fail("Expected ServiceException to be thrown");
        } catch (NodeNotFoundException e) {
            assertThat("Unexpected exception message", e.getMessage(),
                containsString("No node found for factory key"));
        }

    }
}
