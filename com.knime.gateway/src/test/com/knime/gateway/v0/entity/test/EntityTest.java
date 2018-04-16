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
package com.knime.gateway.v0.entity.test;

import static com.knime.gateway.entity.EntityBuilderManager.builder;
import static org.junit.Assert.assertEquals;

import java.util.UUID;

import org.junit.Test;

import com.knime.gateway.entity.EntityBuilderManager;
import com.knime.gateway.v0.entity.NativeNodeEnt;
import com.knime.gateway.v0.entity.NativeNodeEnt.NativeNodeEntBuilder;
import com.knime.gateway.v0.entity.NodeEnt.NodeStateEnum;
import com.knime.gateway.v0.entity.NodeEnt.NodeTypeEnum;
import com.knime.gateway.v0.entity.NodeFactoryKeyEnt;
import com.knime.gateway.v0.entity.NodeFactoryKeyEnt.NodeFactoryKeyEntBuilder;

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
        NativeNodeEnt ent = builder(NativeNodeEntBuilder.class)
        .setName("name")
        .setHasDialog(true)
        .setNodeID("node_id")
        .setNodeState(NodeStateEnum.CONFIGURED)
        .setNodeType(NodeTypeEnum.LEARNER)
        .setType("NativeNode")
        .setRootWorkflowID(wfId)
        .setNodeFactoryKey(createNodeFactoryKeyEnt())
        .setOutPorts(null).build();

        assertEquals(ent.getName(), "name");
        assertEquals(ent.isHasDialog(), true);
        assertEquals(ent.getNodeID(), "node_id");
        assertEquals(ent.getNodeState(), NodeStateEnum.CONFIGURED);
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

    private NodeFactoryKeyEnt createNodeFactoryKeyEnt() {
        return builder(NodeFactoryKeyEntBuilder.class)
        .setClassName("node_factory_class_name")
        .setSettings("settings").build();
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
