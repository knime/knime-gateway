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
 */
package org.knime.gateway.impl.service.util;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.knime.core.util.Pair.create;
import static org.knime.gateway.api.entity.EntityBuilderManager.builder;
import static org.knime.gateway.api.entity.NodeIDEnt.getRootID;
import static org.knime.gateway.impl.service.util.RandomEntityBuilder.buildRandomEntityBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.knime.core.util.Pair;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.NativeNodeEnt.NativeNodeEntBuilder;
import org.knime.gateway.api.webui.entity.NodeEnt;
import org.knime.gateway.api.webui.entity.NodeStateEnt.ExecutionStateEnum;
import org.knime.gateway.api.webui.entity.NodeStateEnt.NodeStateEntBuilder;
import org.knime.gateway.api.webui.entity.PatchEnt;
import org.knime.gateway.api.webui.entity.WorkflowEnt;
import org.knime.gateway.api.webui.entity.WorkflowEnt.WorkflowEntBuilder;
import org.knime.gateway.api.webui.entity.WorkflowInfoEnt.ContainerTypeEnum;
import org.knime.gateway.api.webui.entity.WorkflowInfoEnt.WorkflowInfoEntBuilder;
import org.knime.gateway.api.webui.entity.XYEnt.XYEntBuilder;
import org.knime.gateway.impl.webui.service.DefaultEventService.PatchEntCreator;

/**
 * Tests for {@link EntityRepository}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public abstract class AbstractEntityRepositoryTest {
    private EntityRepository<Pair<UUID, NodeIDEnt>, WorkflowEnt> m_repo;

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
     * @return an instance of the {@link EntityRepository}
     */
    protected abstract EntityRepository<Pair<UUID, NodeIDEnt>, WorkflowEnt> createRepo();

    /**
     * Tests the {@link EntityRepository#commit(Object, org.knime.gateway.api.entity.GatewayEntity)} method.
     *
     * @throws Exception
     */
    @Test
    public void testCommit() throws Exception {
        WorkflowEntBuilder wfBuilder = buildRandomEntityBuilder(WorkflowEntBuilder.class);
        UUID wfID = UUID.randomUUID();
        WorkflowEnt newEntity = wfBuilder.build();
        String res = m_repo.commit(create(wfID, getRootID()), newEntity);

        WorkflowEnt newUnchangedEntity = wfBuilder.build();
        String res2 = m_repo.commit(create(wfID, getRootID()), newUnchangedEntity);

        //since nothing has changed, the snapshot id should remain unchanged
        assertEquals(res, res2);

        WorkflowEnt newChangedEntity =
            wfBuilder.setInfo(builder(WorkflowInfoEntBuilder.class).setContainerId(new NodeIDEnt(2, 3))
                .setName("a new workflow name").setContainerType(ContainerTypeEnum.COMPONENT).build()).build();
        String res3 = m_repo.commit(create(wfID, getRootID()), newChangedEntity);

        //since there is a change, the snapshot id should be new
        assertNotEquals(res, res3);
    }

    /**
     * Tests the
     * {@link EntityRepository#getChangesAndCommit(UUID, org.knime.gateway.api.entity.GatewayEntity, java.util.function.Function)}
     * method.
     *
     * @throws Exception
     */
    @Test
    public void testGetChangesAndCommit() throws Exception {
        WorkflowEntBuilder wfBuilder = buildRandomEntityBuilder(WorkflowEntBuilder.class);
        UUID wfID = UUID.randomUUID();
        NativeNodeEntBuilder nodeBuilder = buildRandomEntityBuilder(NativeNodeEntBuilder.class);

        nodeBuilder.setPosition(builder(XYEntBuilder.class).setX(10).setY(5).build())
            .setState(builder(NodeStateEntBuilder.class).setExecutionState(ExecutionStateEnum.CONFIGURED).build());
        Map<String, NodeEnt> nodes = new HashMap<>(wfBuilder.build().getNodes());
        nodes.put("node_to_be_changed", nodeBuilder.build());
        wfBuilder.setNodes(nodes);

        WorkflowEnt wfEntity = wfBuilder.build();
        String commitRes = m_repo.commit(create(wfID, new NodeIDEnt(3)), wfEntity);

        //modify workflow entity
        nodeBuilder.setPosition(builder(XYEntBuilder.class).setX(11).setY(5).build())
            .setState(builder(NodeStateEntBuilder.class).setWarning("a warning message")
                .setExecutionState(ExecutionStateEnum.EXECUTED).build());
        nodes.put("node_to_be_changed", nodeBuilder.build());
        wfBuilder.setNodes(nodes);

        WorkflowEnt newWfEntity = wfBuilder.build();
        AtomicReference<String> patch1SnapshotId = new AtomicReference<>();
        PatchEnt patch1 =
            m_repo.getChangesAndCommit(commitRes, newWfEntity, id -> {
                patch1SnapshotId.set(id);
                return new PatchEntCreator(id);
            }).orElse(null);
        assertThat(patch1.getOps().size(), is(3));
        assertNotEquals(commitRes, patch1SnapshotId.get());

        //get patch for a non-modified entity
        PatchEnt patch2 =
            m_repo.getChangesAndCommit(patch1SnapshotId.get(), wfBuilder.build(), PatchEntCreator::new).orElse(null);
        Assert.assertNull(patch2);

        //make sure that the very first snapshot is still there
        PatchEnt patch3 = m_repo.getChangesAndCommit(commitRes, wfBuilder.build(), PatchEntCreator::new).orElse(null);
        assertThat(patch3.getOps().size(), is(3));
    }

    /**
     * Tests the {@link EntityRepository#disposeHistory(java.util.function.Predicate)} method.
     *
     * @throws Exception
     */
    @Test
    public void testDisposeHistory() throws Exception {
        UUID wfID = UUID.randomUUID();
        WorkflowEntBuilder wfBuilder = buildRandomEntityBuilder(WorkflowEntBuilder.class);
        WorkflowEnt newEntity = wfBuilder.build();
        String commitRes = m_repo.commit(create(wfID, new NodeIDEnt(2)), newEntity);

        m_repo.disposeHistory(k -> k.getFirst().equals(wfID));

        try {
            m_repo.getChangesAndCommit(commitRes, wfBuilder.build(), PatchEntCreator::new);
            fail("Expected a IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), containsString("No workflow found for snapshot with ID"));
        }

        String commitRes2 = m_repo.commit(create(wfID, new NodeIDEnt(2)), newEntity);
        assertNotEquals(commitRes, commitRes2);
    }

    //@Test
    public void testPerformanceInMemoryRepo() throws Exception {
        UUID wfID = UUID.randomUUID();
        WorkflowEntBuilder wfBuilder = buildRandomEntityBuilder(WorkflowEntBuilder.class);
        Map<String, NodeEnt> nodes = new HashMap<>(wfBuilder.build().getNodes());
        NativeNodeEntBuilder nodeBuilder = buildRandomEntityBuilder(NativeNodeEntBuilder.class);

        for (int i = 0; i < 10000; i++) {
            if (i % 2 == 0) {
                nodeBuilder.setState(
                    builder(NodeStateEntBuilder.class).setExecutionState(ExecutionStateEnum.CONFIGURED).build());
            } else {
                nodeBuilder.setState(
                    builder(NodeStateEntBuilder.class).setExecutionState(ExecutionStateEnum.EXECUTED).build());
            }
            nodes.put("node", nodeBuilder.build());
            m_repo.commit(create(wfID, null), wfBuilder.setNodes(nodes).build());
        }
    }

}
