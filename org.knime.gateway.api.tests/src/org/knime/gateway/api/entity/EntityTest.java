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
package org.knime.gateway.api.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.knime.gateway.api.entity.EntityBuilderManager.builder;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.Test;
import org.knime.gateway.api.entity.ConnectionEnt;
import org.knime.gateway.api.entity.ConnectionEnt.ConnectionEntBuilder;
import org.knime.gateway.api.entity.ConnectionEnt.TypeEnum;
import org.knime.gateway.api.entity.EntityBuilderManager;
import org.knime.gateway.api.entity.NativeNodeEnt;
import org.knime.gateway.api.entity.NativeNodeEnt.NativeNodeEntBuilder;
import org.knime.gateway.api.entity.NodeEnt;
import org.knime.gateway.api.entity.NodeEnt.NodeTypeEnum;
import org.knime.gateway.api.entity.NodeFactoryKeyEnt;
import org.knime.gateway.api.entity.NodeFactoryKeyEnt.NodeFactoryKeyEntBuilder;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.entity.NodeStateEnt;
import org.knime.gateway.api.entity.NodeStateEnt.NodeStateEntBuilder;
import org.knime.gateway.api.entity.WorkflowEnt;
import org.knime.gateway.api.entity.WorkflowEnt.WorkflowEntBuilder;

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
		assertEquals(ent.getNodeID(), new NodeIDEnt(14));
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
                .setNodeID(new NodeIDEnt(14))
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
        ConnectionEnt con = builder(ConnectionEntBuilder.class).setDest(new NodeIDEnt(10)).setDestPort(1)
            .setSource(new NodeIDEnt(11)).setSourcePort(3).setType(TypeEnum.STD).build();
        ConnectionEnt con2 = builder(ConnectionEntBuilder.class).setDest(new NodeIDEnt(10)).setDestPort(1)
            .setSource(new NodeIDEnt(11)).setSourcePort(3).setType(TypeEnum.STD).build();

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
