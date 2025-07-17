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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.knime.core.node.workflow.NodeID;
import org.knime.gateway.api.tests.TestWorkflowCollection;
import org.knime.testing.util.WorkflowManagerUtil;

/**
 * Tests {@link NodeIDEnt}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
class NodeIDEntTest {

    @Test
    void testAppendNodeID() {
        assertThat(new NodeIDEnt(5).appendNodeID(4)).isEqualTo(new NodeIDEnt(5, 4));
        assertThat(NodeIDEnt.getRootID().appendNodeID(5)).isEqualTo(new NodeIDEnt(5));
    }

    @Test
    void testToAndFromNodeID() throws Exception {
        //to
        NodeIDEnt ent = new NodeIDEnt(4, 2, 1);
        var wfm = TestWorkflowCollection.CONTAINER_NODES_WF.loadWorkflow();
        assertThat(NodeIDEnt.getRootID().toNodeID(wfm)).isEqualTo(NodeID.fromString("3"));
        assertThat(ent.toNodeID(wfm)).isEqualTo(NodeID.fromString("3:4:2:1"));

        var componentWfm = TestWorkflowCollection.CONTAINER_NODES_WF.loadWorkflow();
        assertThat(NodeIDEnt.getRootID().toNodeID(componentWfm)).isEqualTo(NodeID.fromString("4"));
        assertThat(ent.toNodeID(componentWfm)).isEqualTo(NodeID.fromString("4:4:2:1"));

        //from
        assertThat(new NodeIDEnt(NodeID.fromString("3:4"))).isEqualTo(new NodeIDEnt(4)); // NOSONAR
        assertThat(new NodeIDEnt(new NodeID(2))).isEqualTo(NodeIDEnt.getRootID());

        WorkflowManagerUtil.disposeWorkflow(wfm);
        WorkflowManagerUtil.disposeWorkflow(componentWfm);
    }

    /**
     * Tests 'toString' and create from string via constructor.
     */
    @Test
    void testToAndFromString() {
        //to
        String s = new NodeIDEnt(3, 4, 1).toString();
        assertThat(s).isEqualTo("root:3:4:1");
        assertThat(NodeIDEnt.getRootID()).hasToString("root");

        //from
        assertThat(new NodeIDEnt(s)).isEqualTo(new NodeIDEnt(3, 4, 1));
        assertThat(new NodeIDEnt("root")).isEqualTo(NodeIDEnt.getRootID());
    }

    @Test
    void testIsEqualOrParentOf() {
        assertThat(new NodeIDEnt("root:1:2:3").isEqualOrParentOf(new NodeIDEnt("root:1:2:3"))).isTrue();
        assertThat(new NodeIDEnt("root:1:2:3").isEqualOrParentOf(new NodeIDEnt("root:1:2:3:4"))).isTrue();
        assertThat(new NodeIDEnt("root:1:2:3").isEqualOrParentOf(new NodeIDEnt("root:1:2"))).isFalse();
        assertThat(new NodeIDEnt("root:1:2:3").isEqualOrParentOf(new NodeIDEnt("root:1:2:4"))).isFalse();
        assertThat(new NodeIDEnt("root").isEqualOrParentOf(new NodeIDEnt("root:1:2:4"))).isTrue();
        assertThat(new NodeIDEnt("root").isEqualOrParentOf(new NodeIDEnt("root"))).isTrue();
    }

}
