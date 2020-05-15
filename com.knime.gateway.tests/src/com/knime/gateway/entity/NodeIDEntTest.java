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
package com.knime.gateway.entity;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.knime.core.node.workflow.NodeID;

/**
 * Tests {@link NodeIDEnt}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@SuppressWarnings("javadoc")
public class NodeIDEntTest {

    @Test
    public void testAppendNodeID() {
        assertThat(new NodeIDEnt(5).appendNodeID(4), is(new NodeIDEnt(5, 4)));
        assertThat(NodeIDEnt.getRootID().appendNodeID(5), is(new NodeIDEnt(5)));
    }

    @Test
    public void testToAndFromNodeID() {
        //to
        NodeIDEnt ent = new NodeIDEnt(4, 2, 1);
        assertThat(ent.toNodeID(new NodeID(4)), is(NodeID.fromString("4:4:2:1")));
        assertThat(ent.toNodeID(NodeID.fromString("3:4")), is(NodeID.fromString("3:4:4:2:1")));
        assertThat(NodeIDEnt.getRootID().toNodeID(NodeID.fromString("3:4")), is(NodeID.fromString("3:4")));

        //from
        assertThat(new NodeIDEnt(NodeID.fromString("3:4")), is(new NodeIDEnt(4)));
        assertThat(new NodeIDEnt(new NodeID(2)), is(NodeIDEnt.getRootID()));
    }

    /**
     * Tests 'toString' and create from string via constructor.
     */
    @Test
    public void testToAndFromString() {
        //to
        String s = new NodeIDEnt(3, 4, 1).toString();
        assertThat(s, is("root:3:4:1"));
        assertThat(NodeIDEnt.getRootID().toString(), is("root"));

        //from
        assertThat(new NodeIDEnt(s), is(new NodeIDEnt(3, 4, 1)));
        assertThat(new NodeIDEnt("root"), is(NodeIDEnt.getRootID()));
    }

}
