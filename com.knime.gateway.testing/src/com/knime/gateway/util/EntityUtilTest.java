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
package com.knime.gateway.util;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.knime.core.node.workflow.ConnectionID;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.WorkflowAnnotationID;

import com.knime.gateway.util.EntityUtil;

/**
 * Tests {@link EntityUtil}.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class EntityUtilTest {

	@Test
	public void testImmutable() {
		Object o = null;
		assertThat("failed to return null", EntityUtil.immutable(o), is(nullValue()));

		o = new Object();
		assertThat("not the same object returned", EntityUtil.immutable(o), is(o));
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testImmutableMap() {
		Map<String, String> m = new HashMap<>();
		Map<String, String> im = EntityUtil.immutable(m);
		m.put("key", "value");
		assertThat("map not immutable", im.size(), is(0));
		im.put("key", "value");
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testImmutableList() {
		List<String> l = new ArrayList<String>();
		List<String> il = EntityUtil.immutable(l);
		l.add("value");
		assertThat("map not immutable", il.size(), is(0));
		il.add("value");
	}

	@Test
	public void testNodeIDToString() {
		String nodeID = EntityUtil.nodeIDToString(NodeID.fromString("5:10:0:3"));
		assertThat("node id to string conversion failed", nodeID, is("10:0:3"));

		nodeID = EntityUtil.nodeIDToString(NodeID.fromString("5"));
		assertThat("node id to string conversion failed", nodeID, is(EntityUtil.ROOT_NODE_ID));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testStringToNodeID() {
		NodeID nodeID = EntityUtil.stringToNodeID("5", "0:6");
		assertThat("string to node id conversion failed", nodeID, is(NodeID.fromString("5:0:6")));

		nodeID = EntityUtil.stringToNodeID("5", EntityUtil.ROOT_NODE_ID);
		assertThat("string to node id conversion failed", nodeID, is(NodeID.fromString("5")));

		EntityUtil.stringToNodeID("test", "test");
	}

	@Test
	public void testConnectionIDToString() {
		String connID = EntityUtil.connectionIDToString(new ConnectionID(NodeID.fromString("4"), 2));
		assertThat("connection id to string conversion failed", connID, is("root_2"));
	}

	@Test
	public void testAnnotationIDToString() {
		String annoID = EntityUtil.annotationIDToString(new WorkflowAnnotationID(NodeID.fromString("10:4"), 6));
		assertThat("annotation id to string conversion failed", annoID, is("4_6"));
	}

}
