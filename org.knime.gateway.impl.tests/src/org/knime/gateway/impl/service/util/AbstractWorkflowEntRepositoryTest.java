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
package org.knime.gateway.impl.service.util;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.knime.gateway.api.entity.NodeIDEnt.getRootID;
import static org.knime.gateway.impl.service.util.RandomEntityBuilder.buildRandomEntityBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.knime.gateway.api.entity.EntityBuilderManager;
import org.knime.gateway.api.entity.NodeEnt;
import org.knime.gateway.api.entity.NodeEnt.NodeEntBuilder;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.entity.NodeMessageEnt.NodeMessageEntBuilder;
import org.knime.gateway.api.entity.NodeStateEnt.NodeStateEntBuilder;
import org.knime.gateway.api.entity.NodeStateEnt.StateEnum;
import org.knime.gateway.api.entity.PatchEnt;
import org.knime.gateway.api.entity.WorkflowEnt;
import org.knime.gateway.api.entity.WorkflowEnt.WorkflowEntBuilder;
import org.knime.gateway.api.entity.WorkflowSnapshotEnt;
import org.knime.gateway.api.entity.WorkflowUIInfoEnt.WorkflowUIInfoEntBuilder;
import org.knime.gateway.impl.entity.DefaultNodeMessageEnt.DefaultNodeMessageEntBuilder;

/**
 * Tests for {@link WorkflowEntRepository}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public abstract class AbstractWorkflowEntRepositoryTest {
    private WorkflowEntRepository m_repo;

    /**
     * Init repository.
     *
     * @throws Exception
     */
    @Before
    public void setup() throws Exception {
        m_repo = createRepo();
    }

    /**
     * @return an instance of the {@link WorkflowEntRepository}
     */
    protected abstract WorkflowEntRepository createRepo();

    /**
     * Tests the {@link WorkflowEntRepository#commit(UUID, NodeIDEnt, WorkflowEnt)} method.
     *
     * @throws Exception
     */
    @Test
    public void testCommit() throws Exception {
        WorkflowEntBuilder wfBuilder = buildRandomEntityBuilder(WorkflowEntBuilder.class);
        UUID wfID = UUID.randomUUID();
        WorkflowEnt newEntity = wfBuilder.build();
        WorkflowSnapshotEnt res = m_repo.commit(wfID, getRootID(), newEntity);

        WorkflowEnt newUnchangedEntity = wfBuilder.build();
        WorkflowSnapshotEnt res2 = m_repo.commit(wfID, getRootID(), newUnchangedEntity);

        //since nothing has changed, the snapshot id should remain unchanged
        assertEquals(res.getSnapshotID(), res2.getSnapshotID());

        WorkflowEnt newChangedEntity =
            wfBuilder.setWorkflowUIInfo(buildRandomEntityBuilder(WorkflowUIInfoEntBuilder.class).build()).build();
        WorkflowSnapshotEnt res3 = m_repo.commit(wfID, getRootID(), newChangedEntity);

        //since there is a change, the snapshot id should be new
        assertNotEquals(res.getSnapshotID(), res3.getSnapshotID());
    }

    /**
     * Tests the {@link WorkflowEntRepository#getChangesAndCommit(UUID, WorkflowEnt)} method.
     *
     * @throws Exception
     */
    @Test
    public void testGetChangesAndCommit() throws Exception {
        WorkflowEntBuilder wfBuilder = buildRandomEntityBuilder(WorkflowEntBuilder.class);
        UUID wfID = UUID.randomUUID();
        NodeEntBuilder nodeBuilder = buildRandomEntityBuilder(NodeEntBuilder.class);
        nodeBuilder.setNodeMessage(buildRandomEntityBuilder(NodeMessageEntBuilder.class).build()).setNodeState(
            EntityBuilderManager.builder(NodeStateEntBuilder.class).setState(StateEnum.CONFIGURED).build());
        Map<String, NodeEnt> nodes = new HashMap<String, NodeEnt>(wfBuilder.build().getNodes());
        nodes.put("node_to_be_changed", nodeBuilder.build());
        wfBuilder.setNodes(nodes);

        WorkflowEnt wfEntity = wfBuilder.build();
        WorkflowSnapshotEnt commitRes = m_repo.commit(wfID, new NodeIDEnt(3), wfEntity);

        //modify workflow entity
        nodeBuilder
            .setNodeMessage(new DefaultNodeMessageEntBuilder().setMessage("a new node message").setType("type").build())
            .setNodeState(EntityBuilderManager.builder(NodeStateEntBuilder.class).setState(StateEnum.EXECUTED).build());
        nodes.put("node_to_be_changed", nodeBuilder.build());
        wfBuilder.setNodes(nodes);

        WorkflowEnt newWfEntity = wfBuilder.build();
        PatchEnt patch1 = m_repo.getChangesAndCommit(commitRes.getSnapshotID(), newWfEntity);
        assertThat(patch1.getOps().size(), is(3));
        assertNotEquals(commitRes.getSnapshotID(), patch1.getSnapshotID());

        //get patch for a non-modified entity
        PatchEnt patch2 = m_repo.getChangesAndCommit(patch1.getSnapshotID(), wfBuilder.build());
        assertThat(patch2.getOps().size(), is(0));
        assertTrue(patch2.getSnapshotID() == null);
        assertTrue(patch2.getTargetTypeID() == null);

        //make sure that the very first snapshot is still there
        PatchEnt patch3 = m_repo.getChangesAndCommit(commitRes.getSnapshotID(), wfBuilder.build());
        assertThat(patch3.getOps().size(), is(3));
    }

    /**
     * Tests the {@link WorkflowEntRepository#disposeHistory(UUID)} method.
     *
     * @throws Exception
     */
    @Test
    public void testDisposeHistory() throws Exception {
        UUID wfID = UUID.randomUUID();
        WorkflowEntBuilder wfBuilder = buildRandomEntityBuilder(WorkflowEntBuilder.class);
        WorkflowEnt newEntity = wfBuilder.build();
        WorkflowSnapshotEnt commitRes = m_repo.commit(wfID, new NodeIDEnt(2), newEntity);

        m_repo.disposeHistory(wfID);

        try {
            m_repo.getChangesAndCommit(commitRes.getSnapshotID(), wfBuilder.build());
            fail("Expected a IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), containsString("No workflow found for snapshot with ID"));
        }

        WorkflowSnapshotEnt commitRes2 = m_repo.commit(wfID, new NodeIDEnt(2), newEntity);
        assertNotEquals(commitRes.getSnapshotID(), commitRes2.getSnapshotID());
    }

//    @Test
    public void testPerformanceInMemoryRepo() throws Exception {
        UUID wfID = UUID.randomUUID();
        WorkflowEntBuilder wfBuilder = buildRandomEntityBuilder(WorkflowEntBuilder.class);
        Map<String, NodeEnt> nodes = new HashMap<>(wfBuilder.build().getNodes());
        NodeEntBuilder nodeBuilder = buildRandomEntityBuilder(NodeEntBuilder.class);

        for (int i = 0; i < 10000; i++) {
            if (i % 2 == 0) {
                nodeBuilder.setNodeState(
                    EntityBuilderManager.builder(NodeStateEntBuilder.class).setState(StateEnum.CONFIGURED).build());
            } else {
                nodeBuilder.setNodeState(
                    EntityBuilderManager.builder(NodeStateEntBuilder.class).setState(StateEnum.EXECUTED).build());
            }
            nodes.put("node", nodeBuilder.build());
            m_repo.commit(wfID, null, wfBuilder.setNodes(nodes).build());
        }
    }

}
