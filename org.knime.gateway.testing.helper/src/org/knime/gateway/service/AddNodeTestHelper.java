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
package org.knime.gateway.service;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.knime.gateway.api.entity.EntityBuilderManager.builder;
import static org.knime.gateway.api.entity.NodeIDEnt.getRootID;

import java.util.UUID;

import org.hamcrest.core.Is;
import org.junit.Assert;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ConfigurableNodeFactory;
import org.knime.core.node.NodeModel;
import org.knime.core.node.context.ModifiableNodeCreationConfiguration;
import org.knime.core.node.extension.NodeFactoryExtensionManager;
import org.knime.gateway.api.entity.NativeNodeEnt;
import org.knime.gateway.api.entity.NodeFactoryKeyEnt;
import org.knime.gateway.api.entity.NodeFactoryKeyEnt.NodeFactoryKeyEntBuilder;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.entity.NodeSettingsEnt;
import org.knime.gateway.api.service.util.ServiceExceptions.InvalidRequestException;
import org.knime.gateway.api.service.util.ServiceExceptions.NodeNotFoundException;
import org.knime.gateway.api.util.EntityBuilderUtil;
import org.knime.gateway.api.util.EntityTranslateUtil;
import org.knime.gateway.testing.helper.ResultChecker;
import org.knime.gateway.testing.helper.ServiceProvider;
import org.knime.gateway.testing.helper.WorkflowLoader;

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
    	UUID wfId = loadWorkflow(TestWorkflow.GENERAL);

        //create and add a new node
        NodeFactoryKeyEntBuilder nodeFactoryKeyBuilder = builder(NodeFactoryKeyEntBuilder.class)
            .setClassName("org.knime.base.node.io.filereader.FileReaderNodeFactory");
        NodeIDEnt newNodeID = ns().createNode(wfId, getRootID(), 100, 100, nodeFactoryKeyBuilder.build());
        Assert.assertThat(newNodeID, Is.is(new NodeIDEnt(28)));
        cr(ws().getWorkflow(wfId, getRootID()).getWorkflow(), "workflowent_root_new_node");

        //create and add a new node in sub workflow
        newNodeID = ns().createNode(wfId, new NodeIDEnt(6), 100, 100, nodeFactoryKeyBuilder.build());
        Assert.assertThat(newNodeID, Is.is(new NodeIDEnt(6, 4)));
        cr(ws().getWorkflow(wfId, getRootID()).getWorkflow(), "workflowent_6_new_node");

        //create and add a node created from a dynamic node factory (in particular the Box Plot (JavaScript)-node)
        nodeFactoryKeyBuilder.setClassName("org.knime.dynamic.js.v30.DynamicJSNodeFactory").setSettings(
            "{\"name\":\"settings\",\"value\":{\"nodeDir\":{\"type\":\"string\",\"value\":\"org.knime.dynamic.js.base:nodes/:boxplot_v2\"}}}");
        newNodeID = ns().createNode(wfId, getRootID(), 100, 100, nodeFactoryKeyBuilder.build());
        cr(ws().getWorkflow(wfId, getRootID()).getWorkflow(), "workflowent_root_new_dynamic_node");

        //try adding a dynamic node with corrupted settings
        nodeFactoryKeyBuilder.setSettings("nonsense-settings");
        try {
            ns().createNode(wfId, getRootID(), 100, 100, nodeFactoryKeyBuilder.build());
            fail("Expected ServiceException to be thrown");
        } catch (InvalidRequestException e) {
            assertThat("Unexpected exception message", e.getMessage(),
                containsString("Problem reading factory settings while trying to create node from"));
        }

        //try adding a node with factory key that doesn't exist
        nodeFactoryKeyBuilder.setClassName("nonsense-node-factory-class");
        try {
            ns().createNode(wfId, getRootID(), 100, 100, nodeFactoryKeyBuilder.build());
            fail("Expected ServiceException to be thrown");
        } catch (NodeNotFoundException e) {
            assertThat("Unexpected exception message", e.getMessage(),
                containsString("No node found for factory key"));
        }
    }

    /**
     * Tests to replace a nodes (e.g. to 'dynamically' change the ports).
     *
     * @throws Exception
     */
    public void testReplaceNode() throws Exception {
        UUID wfId = loadWorkflow(TestWorkflow.GENERAL);

        // get node to be replaced
        NodeIDEnt nodeId = new NodeIDEnt(27);
        NativeNodeEnt oldNode = (NativeNodeEnt)ns().getNode(wfId, nodeId);
        @SuppressWarnings("unchecked")
        ConfigurableNodeFactory<NodeModel> nodeFactory = (ConfigurableNodeFactory<NodeModel>)NodeFactoryExtensionManager
            .getInstance().createNodeFactory("org.knime.base.node.preproc.append.row.AppendedRowsNodeFactory").get();
        String oldNodeCreationConfigString = oldNode.getNodeFactoryKey().getNodeCreationConfigSettings();
        ModifiableNodeCreationConfiguration nodeCreationConfig =
            EntityTranslateUtil.translateNodeCreationConfiguration(oldNodeCreationConfigString, nodeFactory).get();
        NodeSettingsEnt oldNodeSettings = ns().getNodeSettings(wfId, nodeId);

        // change port configuration and replace node
        nodeCreationConfig.getPortConfig().get().getExtendablePorts().get("input").addPort(BufferedDataTable.TYPE);
        nodeCreationConfig.getPortConfig().get().getExtendablePorts().get("input").addPort(BufferedDataTable.TYPE);
        NodeFactoryKeyEnt nodeFactoryEnt = EntityBuilderUtil.buildNodeFactoryKeyEnt(null, nodeCreationConfig);
        NativeNodeEnt newNodeReturned = (NativeNodeEnt)ns().replaceNode(wfId, nodeId, nodeFactoryEnt);

        // check replaced Node
        checkReplacedNode(oldNode, newNodeReturned, nodeFactoryEnt.getNodeCreationConfigSettings(), 6, 2);
        NativeNodeEnt newNode = (NativeNodeEnt)ns().getNode(wfId, nodeId);
        newNode.getNodeFactoryKey().getNodeCreationConfigSettings();
        NodeSettingsEnt newNodeSettings = ns().getNodeSettings(wfId, nodeId);
        checkReplacedNode(oldNode, newNode, nodeFactoryEnt.getNodeCreationConfigSettings(), 6, 2);
        assertThat("node settings have changed", oldNodeSettings.getJsonContent(),
            is(newNodeSettings.getJsonContent()));
    }

    private static void checkReplacedNode(final NativeNodeEnt oldNode, final NativeNodeEnt newNode,
        final String expectedNodeCreationConfigString, final int nrInports, final int nrOutports) {
        assertThat("unexpected node name", newNode.getName(), is(oldNode.getName()));
        assertThat("unexpected node annotation", newNode.getNodeAnnotation(), is(oldNode.getNodeAnnotation()));
        assertThat("unexpected node creation config", newNode.getNodeFactoryKey().getNodeCreationConfigSettings(),
            is(expectedNodeCreationConfigString));
        assertThat("unexpected nr of in ports", newNode.getInPorts().size(), is(nrInports));
        assertThat("unexpected nr of out ports", newNode.getOutPorts().size(), is(nrOutports));
    }
}
