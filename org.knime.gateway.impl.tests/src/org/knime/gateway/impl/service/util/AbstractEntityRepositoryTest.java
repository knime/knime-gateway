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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;
import static org.knime.core.util.Pair.create;
import static org.knime.gateway.api.entity.EntityBuilderManager.builder;
import static org.knime.gateway.api.entity.NodeIDEnt.getRootID;
import static org.knime.gateway.impl.service.util.RandomEntityBuilder.buildRandomEntityBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.knime.core.util.Pair;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.NativeNodeEnt.NativeNodeEntBuilder;
import org.knime.gateway.api.webui.entity.NodeEnt;
import org.knime.gateway.api.webui.entity.NodeStateEnt.ExecutionStateEnum;
import org.knime.gateway.api.webui.entity.NodeStateEnt.NodeStateEntBuilder;
import org.knime.gateway.api.webui.entity.WorkflowChangedEventEnt;
import org.knime.gateway.api.webui.entity.WorkflowEnt;
import org.knime.gateway.api.webui.entity.WorkflowEnt.WorkflowEntBuilder;
import org.knime.gateway.api.webui.entity.WorkflowInfoEnt.ContainerTypeEnum;
import org.knime.gateway.api.webui.entity.WorkflowInfoEnt.WorkflowInfoEntBuilder;
import org.knime.gateway.api.webui.entity.XYEnt.XYEntBuilder;
import org.knime.gateway.impl.webui.service.events.WorkflowChangedEventSource.PatchEntCreator;

/**
 * Tests for {@link EntityRepository}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public abstract class AbstractEntityRepositoryTest {

    /**
     * @param numSnapshotsPerEntity -1 if the default shall be used
     * @return an instance of the {@link EntityRepository}
     */
    protected abstract EntityRepository<Pair<UUID, NodeIDEnt>, WorkflowEnt> createRepo(int numSnapshotsPerEntity);

    /**
     * Tests the {@link EntityRepository#commit(Object, org.knime.gateway.api.entity.GatewayEntity)} method.
     *
     * @throws Exception
     */
    @Test
    public void testCommit() throws Exception {
        EntityRepository<Pair<UUID, NodeIDEnt>, WorkflowEnt> repo = createRepo(-1);
        WorkflowEntBuilder wfBuilder = buildRandomEntityBuilder(WorkflowEntBuilder.class);
        UUID wfID = UUID.randomUUID();
        WorkflowEnt newEntity = wfBuilder.build();
        String res = repo.commit(create(wfID, getRootID()), newEntity);

        WorkflowEnt newUnchangedEntity = wfBuilder.build();
        String res2 = repo.commit(create(wfID, getRootID()), newUnchangedEntity);

        //since nothing has changed, the snapshot id should remain unchanged
        assertEquals(res, res2);

        WorkflowEnt newChangedEntity =
            wfBuilder.setInfo(builder(WorkflowInfoEntBuilder.class).setContainerId(new NodeIDEnt(2, 3))
                .setName("a new workflow name").setContainerType(ContainerTypeEnum.COMPONENT).build()).build();
        String res3 = repo.commit(create(wfID, getRootID()), newChangedEntity);

        //since there is a change, the snapshot id should be new
        assertNotEquals(res, res3);
    }

    /**
     * Tests the
     * {@link EntityRepository#getChangesAndCommit(String, org.knime.gateway.api.entity.GatewayEntity, PatchCreator)}
     * method.
     *
     * @throws Exception
     */
    @Test
    public void testGetChangesAndCommit() throws Exception {
        EntityRepository<Pair<UUID, NodeIDEnt>, WorkflowEnt> repo = createRepo(-1);
        WorkflowEntBuilder wfBuilder = buildRandomEntityBuilder(WorkflowEntBuilder.class);
        UUID wfID = UUID.randomUUID();
        NativeNodeEntBuilder nodeBuilder = buildRandomEntityBuilder(NativeNodeEntBuilder.class);

        nodeBuilder.setPosition(builder(XYEntBuilder.class).setX(10).setY(5).build())
            .setState(builder(NodeStateEntBuilder.class).setExecutionState(ExecutionStateEnum.CONFIGURED).build());
        Map<String, NodeEnt> nodes = new HashMap<>(wfBuilder.build().getNodes());
        nodes.put("node_to_be_changed", nodeBuilder.build());
        wfBuilder.setNodes(nodes);

        WorkflowEnt wfEntity = wfBuilder.build(); // This is set once
        String commitRes = repo.commit(create(wfID, new NodeIDEnt(3)), wfEntity);

        //modify workflow entity
        nodeBuilder.setPosition(builder(XYEntBuilder.class).setX(11).setY(5).build())
            .setState(builder(NodeStateEntBuilder.class).setWarning("a warning message")
                .setExecutionState(ExecutionStateEnum.EXECUTED).build());
        nodes.put("node_to_be_changed", nodeBuilder.build());
        wfBuilder.setNodes(nodes);

        WorkflowEnt newWfEntity = wfBuilder.build(); // This also changes `wfEntity`, why?
        WorkflowChangedEventEnt event =
            repo.getChangesAndCommit(commitRes, newWfEntity, new PatchEntCreator(null)).orElse(null);
        assertThat(event.getPatch().getOps().size(), is(3));
        assertNotEquals(commitRes, event.getSnapshotId());

        //get patch for a non-modified entity
        event = repo
            .getChangesAndCommit(event.getSnapshotId(), wfBuilder.build(), new PatchEntCreator(event.getSnapshotId()))
            .orElse(null);
        Assert.assertNull(event);

        //make sure that the very first snapshot is still there
        event =
            repo.getChangesAndCommit(commitRes, wfBuilder.build(), new PatchEntCreator(null)).orElse(null);
        assertThat(event.getPatch().getOps().size(), is(3));
    }

    /**
     * Tests the {@link EntityRepository#disposeHistory(java.util.function.Predicate)} method.
     *
     * @throws Exception
     */
    @Test
    public void testDisposeHistory() throws Exception {
        EntityRepository<Pair<UUID, NodeIDEnt>, WorkflowEnt> repo = createRepo(-1);
        UUID wfID = UUID.randomUUID();
        WorkflowEntBuilder wfBuilder = buildRandomEntityBuilder(WorkflowEntBuilder.class);
        WorkflowEnt newEntity = wfBuilder.build();
        String commitRes = repo.commit(create(wfID, new NodeIDEnt(2)), newEntity);

        repo.disposeHistory(k -> k.getFirst().equals(wfID));

        try {
            repo.getChangesAndCommit(commitRes, wfBuilder.build(), new PatchEntCreator(null));
            fail("Expected a IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), containsString("No workflow found for snapshot with ID"));
        }

        String commitRes2 = repo.commit(create(wfID, new NodeIDEnt(2)), newEntity);
        assertNotEquals(commitRes, commitRes2);
    }

    /**
     * Tests that there is the specified number of snapshots available for each entity.
     *
     * @throws Exception
     */
    @Test
    public void testNumSnapshotsPerEntity() throws Exception {
        EntityRepository<Pair<UUID, NodeIDEnt>, WorkflowEnt> repo = createRepo(2);


        // create snapshot history for first workflow

        WorkflowEntBuilder wfBuilder = buildRandomEntityBuilder(WorkflowEntBuilder.class);
        UUID wfID = UUID.randomUUID();
        WorkflowEnt wf1 = wfBuilder.build();
        String id1 = repo.commit(create(wfID, getRootID()), wf1);

        WorkflowEnt wf12 = wfBuilder.setInfo(builder(WorkflowInfoEntBuilder.class).setContainerId(new NodeIDEnt(2, 3))
            .setName("wf_1_2").setContainerType(ContainerTypeEnum.COMPONENT).build()).build();
        String id12 = repo.commit(create(wfID, getRootID()), wf12);

        WorkflowEnt wf13 = wfBuilder.setInfo(builder(WorkflowInfoEntBuilder.class).setContainerId(new NodeIDEnt(2, 3))
            .setName("wf_1_3").setContainerType(ContainerTypeEnum.COMPONENT).build()).build();
        String id13 = repo.commit(create(wfID, getRootID()), wf13);


        // create snapshot history for another workflow

        wfBuilder = buildRandomEntityBuilder(WorkflowEntBuilder.class);
        wfID = UUID.randomUUID();
        WorkflowEnt wf2 = wfBuilder.build();
        String id2 = repo.commit(create(wfID, new NodeIDEnt(2)), wf2);

        WorkflowEnt wf22 = wfBuilder.setInfo(builder(WorkflowInfoEntBuilder.class).setContainerId(new NodeIDEnt(2, 3))
            .setName("wf_2_2").setContainerType(ContainerTypeEnum.COMPONENT).build()).build();
        String id22 = repo.commit(create(wfID, new NodeIDEnt(2)), wf22);

        WorkflowEnt wf23 = wfBuilder.setInfo(builder(WorkflowInfoEntBuilder.class).setContainerId(new NodeIDEnt(2, 3))
            .setName("wf_2_3").setContainerType(ContainerTypeEnum.COMPONENT).build()).build();
        String id23 = repo.commit(create(wfID, new NodeIDEnt(2)), wf23);


        // actual tests

        // snapshot history size per entity is 2! The first snapshot should be gone now
        assertThrows("workflow is still part of the snapshot history", IllegalArgumentException.class,
            () -> repo.getChangesAndCommit(id1, wf1, new PatchEntCreator(null)));
        assertThat("not an empty patch", repo.getChangesAndCommit(id12, wf12, new PatchEntCreator(null)),
            is(Optional.empty()));
        assertThat("not an empty patch", repo.getChangesAndCommit(id13, wf13, new PatchEntCreator(null)),
            is(Optional.empty()));

        assertThrows("workflow is still part of the snapshot history", IllegalArgumentException.class,
            () -> repo.getChangesAndCommit(id2, wf2, new PatchEntCreator(null)));
        assertThat("not an empty patch", repo.getChangesAndCommit(id22, wf22, new PatchEntCreator(null)),
            is(Optional.empty()));
        assertThat("not an empty patch", repo.getChangesAndCommit(id23, wf23, new PatchEntCreator(null)),
            is(Optional.empty()));
    }

    //@Test
    public void testPerformanceInMemoryRepo() throws Exception {
        EntityRepository<Pair<UUID, NodeIDEnt>, WorkflowEnt> repo = createRepo(-1);
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
            repo.commit(create(wfID, null), wfBuilder.setNodes(nodes).build());
        }
    }

}
