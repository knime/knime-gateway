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
package com.knime.gateway.remote;

import static com.knime.gateway.remote.RandomEntityBuilder.buildRandomEntityBuilder;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import com.knime.gateway.remote.util.JaversRepository;
import com.knime.gateway.remote.util.WorkflowEntRepository;
import com.knime.gateway.v0.entity.NodeEnt;
import com.knime.gateway.v0.entity.NodeEnt.NodeEntBuilder;
import com.knime.gateway.v0.entity.NodeEnt.NodeStateEnum;
import com.knime.gateway.v0.entity.NodeMessageEnt.NodeMessageEntBuilder;
import com.knime.gateway.v0.entity.PatchEnt;
import com.knime.gateway.v0.entity.WorkflowEnt;
import com.knime.gateway.v0.entity.WorkflowEnt.WorkflowEntBuilder;
import com.knime.gateway.v0.entity.WorkflowSnapshotEnt;
import com.knime.gateway.v0.entity.WorkflowUIInfoEnt.WorkflowUIInfoEntBuilder;
import com.knime.gateway.v0.entity.impl.DefaultNodeMessageEnt.DefaultNodeMessageEntBuilder;

/**
 * Tests for {@link WorkflowEntRepository} and it's {@link JaversRepository}-implementation.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class WorkflowEntRepoTest {

    WorkflowEntRepository m_repo;

    /**
     * Init javers repository.
     *
     * @throws Exception
     */
    @Before
    public void setup() throws Exception {
        m_repo = new JaversRepository();
    }

    /**
     * Tests the {@link JaversRepository#commit(UUID, String, WorkflowEnt)} method.
     *
     * @throws Exception
     */
    @Test
    public void testCommit() throws Exception {
        WorkflowEntBuilder wfBuilder = buildRandomEntityBuilder(WorkflowEntBuilder.class);
        UUID wfID = UUID.randomUUID();
        WorkflowEnt newEntity = wfBuilder.build();
        WorkflowSnapshotEnt res = m_repo.commit(wfID, null, newEntity);

        WorkflowEnt newUnchangedEntity = wfBuilder.build();
        WorkflowSnapshotEnt res2 = m_repo.commit(wfID, null, newUnchangedEntity);

        //since nothing has changed, the snapshot id should remain unchanged
        assertEquals(res.getSnapshotID(), res2.getSnapshotID());

        WorkflowEnt newChangedEntity =
            wfBuilder.setWorkflowUIInfo(buildRandomEntityBuilder(WorkflowUIInfoEntBuilder.class).build()).build();
        WorkflowSnapshotEnt res3 = m_repo.commit(wfID, null, newChangedEntity);

        //since there is a change, the snapshot id should be new
        assertNotEquals(res.getSnapshotID(), res3.getSnapshotID());
    }

    /**
     * Tests the {@link JaversRepository#getChangesAndCommit(UUID, WorkflowEnt)} method.
     *
     * @throws Exception
     */
    @Test
    public void testGetChangesAndCommit() throws Exception {
        WorkflowEntBuilder wfBuilder = buildRandomEntityBuilder(WorkflowEntBuilder.class);
        UUID wfID = UUID.randomUUID();
        NodeEntBuilder nodeBuilder = buildRandomEntityBuilder(NodeEntBuilder.class);
        nodeBuilder.setNodeMessage(buildRandomEntityBuilder(NodeMessageEntBuilder.class).build())
            .setNodeState(NodeStateEnum.CONFIGURED);
        Map<String, NodeEnt> nodes = new HashMap<String, NodeEnt>(wfBuilder.build().getNodes());
        nodes.put("node_to_be_changed", nodeBuilder.build());
        wfBuilder.setNodes(nodes);

        WorkflowEnt wfEntity = wfBuilder.build();
        WorkflowSnapshotEnt commitRes = m_repo.commit(wfID, "3", wfEntity);

        //modify workflow entity
        nodeBuilder
            .setNodeMessage(new DefaultNodeMessageEntBuilder().setMessage("a new node message").setType("type").build())
            .setNodeState(NodeStateEnum.EXECUTED);
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
     * Tests the {@link JaversRepository#disposeHistory(UUID)} method.
     *
     * @throws Exception
     */
    @Test
    public void testDisposeHistory() throws Exception {
        UUID wfID = UUID.randomUUID();
        WorkflowEntBuilder wfBuilder = buildRandomEntityBuilder(WorkflowEntBuilder.class);
        WorkflowEnt newEntity = wfBuilder.build();
        WorkflowSnapshotEnt commitRes = m_repo.commit(wfID, "2", newEntity);

        m_repo.disposeHistory(wfID);

        try {
            m_repo.getChangesAndCommit(commitRes.getSnapshotID(), wfBuilder.build());
            fail("Expected a IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), containsString("No workflow found for snapshot with ID"));
        }

        WorkflowSnapshotEnt commitRes2 = m_repo.commit(wfID, "2", newEntity);
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
                nodeBuilder.setNodeState(NodeStateEnum.CONFIGURED);
            } else {
                nodeBuilder.setNodeState(NodeStateEnum.EXECUTED);
            }
            nodes.put("node", nodeBuilder.build());
            m_repo.commit(wfID, null, wfBuilder.setNodes(nodes).build());
        }
    }

}
