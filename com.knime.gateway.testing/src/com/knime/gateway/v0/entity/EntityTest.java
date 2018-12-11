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
package com.knime.gateway.v0.entity;

import static com.knime.gateway.entity.EntityBuilderManager.builder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.Test;

import com.knime.gateway.entity.EntityBuilderManager;
import com.knime.gateway.v0.entity.ConnectionEnt;
import com.knime.gateway.v0.entity.ConnectionEnt.ConnectionEntBuilder;
import com.knime.gateway.v0.entity.ConnectionEnt.TypeEnum;
import com.knime.gateway.v0.entity.NativeNodeEnt;
import com.knime.gateway.v0.entity.NativeNodeEnt.NativeNodeEntBuilder;
import com.knime.gateway.v0.entity.NodeEnt;
import com.knime.gateway.v0.entity.NodeEnt.NodeTypeEnum;
import com.knime.gateway.v0.entity.NodeFactoryKeyEnt;
import com.knime.gateway.v0.entity.NodeFactoryKeyEnt.NodeFactoryKeyEntBuilder;
import com.knime.gateway.v0.entity.NodeStateEnt;
import com.knime.gateway.v0.entity.NodeStateEnt.NodeStateEntBuilder;
import com.knime.gateway.v0.entity.WorkflowEnt;
import com.knime.gateway.v0.entity.WorkflowEnt.WorkflowEntBuilder;

/**
 * Basic test of the entities and the entity builders. Not testing all of them since they are auto-generated, anyway.
 * Aim is to cover at least the different return types, default values and required properties.
 *
 * Test coverage of all entity code is achieved by the integration test.
 *
 * Needs to be run as plugin test ({@link EntityBuilderManager} uses an extension point).
 *
 * @author Martin Horn, University of Konstanz
 */
public class EntityTest {

    /**
     * Test different property datatypes, optional properties and default values using the {@link NativeNodeEnt}.
     */
    @Test
    public void testNativeNodeEnt() {
        UUID wfId = UUID.randomUUID();
        NativeNodeEnt ent = createNativeNodeEnt(wfId);

		assertEquals(ent.getName(), "name");
		assertEquals(ent.hasDialog(), true);
		assertEquals(ent.getNodeID(), "node_id");
		assertEquals(ent.getNodeState().getState(), NodeStateEnt.StateEnum.CONFIGURED);
		assertEquals(ent.getNodeType(), NodeTypeEnum.LEARNER);
		assertEquals(ent.getType(), "NativeNode");
		assertEquals(ent.getRootWorkflowID(), wfId);
		assertEquals(ent.getNodeFactoryKey().getClassName(), "node_factory_class_name");
		assertEquals(ent.getNodeFactoryKey().getSettings(), "settings");
		assertEquals(ent.getOutPorts(), null);

        //assumes isDeletable to have a default value
        assertEquals(ent.isDeletable(), false);

        //default value is an empty list
        assertEquals(ent.getInPorts().size(), 0);

        //optional complex property
        assertEquals(ent.getNodeMessage(), null);

        //optional primitive property
        assertEquals(ent.getParentNodeID(), null);

    }

    private NativeNodeEnt createNativeNodeEnt(final UUID wfId) {
        return builder(NativeNodeEntBuilder.class)
                .setName("name")
                .setHasDialog(true)
                .setNodeID("node_id")
                .setNodeState(builder(NodeStateEntBuilder.class).setState(NodeStateEnt.StateEnum.CONFIGURED).build())
                .setNodeType(NodeTypeEnum.LEARNER)
                .setType("NativeNode")
                .setRootWorkflowID(wfId)
                .setNodeFactoryKey(createNodeFactoryKeyEnt())
                .setOutPorts(null).build();
    }

    private NodeFactoryKeyEnt createNodeFactoryKeyEnt() {
        return builder(NodeFactoryKeyEntBuilder.class)
        .setClassName("node_factory_class_name")
        .setSettings("settings").build();
    }

    /**
     * Tests the immutability of entity properties (i.e. of lists and maps).
     */
    @Test
    public void testImmutability() {

        /* test immutability of maps */
        UUID node1id = UUID.randomUUID();
        UUID node2id = UUID.randomUUID();
        NativeNodeEnt node1 = createNativeNodeEnt(node1id);
        NativeNodeEnt node2 = createNativeNodeEnt(node2id);

        Map<String, NodeEnt> nodes = new HashMap<>();
        nodes.put("node1", node1);

        WorkflowEnt wf = builder(WorkflowEntBuilder.class).setNodes(nodes).build();

        //replace node
        nodes.replace("node1", node2);

        //make sure the workflow end didn't change
        assertEquals(wf.getNodes().get("node1").getRootWorkflowID(), node1id);

        //add another node
        nodes.put("node2", node2);

        //make sure there is still just one node
        assertEquals(wf.getNodes().size(), 1);

        try {
            wf.getNodes().put("new_node", node2);
            fail("Exception expected to be thrown.");
        } catch (UnsupportedOperationException e) {
            //exception has been thrown
        }

        /* test immutability of lists */
        ConnectionEnt con = builder(ConnectionEntBuilder.class).setDest("dest").setDestPort(1).setSource("source")
            .setSourcePort(3).setType(TypeEnum.STD).build();
        ConnectionEnt con2 = builder(ConnectionEntBuilder.class).setDest("dest").setDestPort(1).setSource("source")
            .setSourcePort(3).setType(TypeEnum.STD).build();

        Map<String, ConnectionEnt> connections = new HashMap<String, ConnectionEnt>();
        connections.put("con1", con);
        wf = builder(WorkflowEntBuilder.class).setConnections(connections).build();

        connections.put("con2", con2);

        assertEquals(wf.getConnections().size(), 1);

        try {
            wf.getConnections().put("con2", con2);
            fail("Exception expected to be thrown");
        } catch (UnsupportedOperationException e) {
            //exception has been thrown
        }
    }

    /**
     * Tests whether the right exception is thrown if a required property is not set.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testErrorAbsentRequiredProperty() {
        //assumes at least one property to be required
        builder(NodeFactoryKeyEntBuilder.class).build();
    }

    /**
     * Makes sure that <code>null</code> can not be set for a required property.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testErrorAbsentRequiredPropertySetNull() {
        //assumes class name to be a required property
        builder(NodeFactoryKeyEntBuilder.class).setClassName(null);
    }


}
