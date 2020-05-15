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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.knime.gateway.entity.AnnotationIDEnt;
import com.knime.gateway.entity.ConnectionIDEnt;
import com.knime.gateway.entity.NodeIDEnt;

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
    public void testCreateNodeIDEntList() {
        List<NodeIDEnt> list = EntityUtil.createNodeIDEntList(new int[][]{{4, 3, 1}, {0}, {}});
        List<NodeIDEnt> expectedList = Arrays.asList(new NodeIDEnt(4, 3, 1), new NodeIDEnt(0), NodeIDEnt.getRootID());
        assertThat("unexpected result list", list, is(expectedList));
    }

    @Test
    public void testCreateConnectionIDEntList() {
        List<ConnectionIDEnt> list = EntityUtil.createConnectionIDEntList(new int[][]{{5, 6}, {}}, 4, 5);
        List<ConnectionIDEnt> expectedList =
            Arrays.asList(new ConnectionIDEnt(new NodeIDEnt(5, 6), 4), new ConnectionIDEnt(NodeIDEnt.getRootID(), 5));
        assertThat("unexpected result list", list, is(expectedList));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateConnectionIDEntListException() {
        EntityUtil.createConnectionIDEntList(new int[][]{{5, 6}, {}}, 4);
    }

    @Test
    public void testCreateAnnotationIDEntList() {
        List<AnnotationIDEnt> list = EntityUtil.createAnnotationIDEntList(new int[][]{{5, 6}, {}}, 4, 5);
        List<AnnotationIDEnt> expectedList =
            Arrays.asList(new AnnotationIDEnt(new NodeIDEnt(5, 6), 4), new AnnotationIDEnt(NodeIDEnt.getRootID(), 5));
        assertThat("unexpected result list", list, is(expectedList));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateAnnotationIDEntListException() {
        EntityUtil.createAnnotationIDEntList(new int[][]{{5, 6}, {}}, 4);
    }
}
