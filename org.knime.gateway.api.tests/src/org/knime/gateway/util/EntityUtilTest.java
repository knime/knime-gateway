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
package org.knime.gateway.util;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.knime.gateway.api.entity.AnnotationIDEnt;
import org.knime.gateway.api.entity.ConnectionIDEnt;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.util.EntityUtil;

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
