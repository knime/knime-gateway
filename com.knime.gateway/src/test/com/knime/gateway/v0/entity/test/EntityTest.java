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
 *   Dec 29, 2017 (hornm): created
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
