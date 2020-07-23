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

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.knime.core.node.workflow.NodeID;
import org.knime.gateway.api.entity.NodeIDEnt;

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
