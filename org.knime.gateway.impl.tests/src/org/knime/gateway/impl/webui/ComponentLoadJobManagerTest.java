/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.org; Email: contact@knime.org
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
 *  ECLIPSE and the GNU General Public License Version 3 applying for KNIME,
 *  provided the license terms of ECLIPSE themselves allow for the respective
 *  use and propagation of ECLIPSE together with KNIME.
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
 * History
 *   Feb 4, 2026 (chatgpt): created
 */
package org.knime.gateway.impl.webui;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.knime.gateway.api.entity.EntityBuilderManager.builder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.NodeUIInformation;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.AddComponentCommandEnt;
import org.knime.gateway.api.webui.entity.AddComponentCommandEnt.AddComponentCommandEntBuilder;
import org.knime.gateway.api.webui.entity.ComponentPlaceholderEnt;
import org.knime.gateway.api.webui.entity.ComponentPlaceholderEnt.StateEnum;
import org.knime.gateway.api.webui.entity.ReplacementOptionsEnt;
import org.knime.gateway.api.webui.entity.WorkflowCommandEnt.KindEnum;
import org.knime.gateway.api.webui.entity.XYEnt.XYEntBuilder;
import org.knime.gateway.impl.service.util.WorkflowChangesListener;
import org.knime.gateway.impl.service.util.WorkflowChangesTracker.WorkflowChange;
import org.knime.gateway.impl.webui.ComponentLoadJobManager.IComponentLoader;
import org.knime.gateway.impl.webui.service.commands.util.ComponentLoader.ComponentLoadedWithWarningsException;
import org.knime.gateway.impl.webui.spaces.SpaceProviders;
import org.knime.testing.node.SourceNodeTestFactory;
import org.knime.testing.util.WorkflowManagerUtil;

/**
 * Tests for {@link ComponentLoadJobManager}.
 */
public class ComponentLoadJobManagerTest {

    @Test
    public void testStartLoadJobCreatesLoadingPlaceholderAndTriggersChange() throws Exception {
        var wfm = WorkflowManagerUtil.createEmptyWorkflow();
        var workflowChangesListener = mock(WorkflowChangesListener.class);
        var startLatch = new CountDownLatch(1);
        var releaseLatch = new CountDownLatch(1);

        IComponentLoader loader = (ent, workflow, spaces, monitor) -> {
            startLatch.countDown();
            awaitLatch(releaseLatch);
            return new NodeID(1);
        };

        var manager = createLoadJobManager(wfm, workflowChangesListener, loader);
        var command = createCommand("component");

        var loadJob = manager.startLoadJob(command);

        await().untilAsserted(() -> assertThat(startLatch.getCount(), is(0L)));

        var placeholders = manager.getComponentPlaceholdersAndCleanUp();
        assertThat(placeholders.size(), is(1));
        var placeholder = placeholders.iterator().next();
        assertThat(placeholder.getId(), is(loadJob.id()));
        assertThat(placeholder.getState(), is(StateEnum.LOADING));
        assertThat(placeholder.getName(), is(command.getName()));
        assertThat(placeholder.getPosition(), is(command.getPosition()));
        verify(workflowChangesListener).trigger(WorkflowChange.COMPONENT_PLACEHOLDER_ADDED);

        releaseLatch.countDown();
        await().untilAsserted(() -> assertThat(loadJob.runner().isLoadDone(), is(true)));
    }

    @Test
    public void testSuccessUpdatesPlaceholderAndCleansUp() throws Exception {
        var wfm = WorkflowManagerUtil.createEmptyWorkflow();
        var workflowChangesListener = mock(WorkflowChangesListener.class);
        var nodeId = new NodeID(2);

        IComponentLoader loader = (ent, workflow, spaces, monitor) -> nodeId;

        var manager = createLoadJobManager(wfm, workflowChangesListener, loader);
        manager.startLoadJob(createCommand("success"));

        await().untilAsserted(() -> {
            var placeholder = getSinglePlaceholder(manager);
            assertThat(placeholder.getState(), is(StateEnum.SUCCESS));
            assertThat(placeholder.getComponentId(), is(new NodeIDEnt(nodeId).toString()));
        });

        assertThat(manager.getComponentPlaceholdersAndCleanUp().isEmpty(), is(true));
    }

    @Test
    public void testSuccessReplaceCommandUsesTargetNodePosition() throws Exception {
        var wfm = WorkflowManagerUtil.createEmptyWorkflow();
        var workflowChangesListener = mock(WorkflowChangesListener.class);
        var nodeId = new NodeID(2);
        var loadGate = new CountDownLatch(1);

        var targetNode = WorkflowManagerUtil.createAndAddNode(wfm, new SourceNodeTestFactory());
        targetNode.setUIInformation(NodeUIInformation.builder().setNodeLocation(5, 7, 0, 0).build());
        var expectedPosition = builder(XYEntBuilder.class).setX(5).setY(7).build();

        IComponentLoader loader = (ent, workflow, spaces, monitor) -> {
            awaitLatch(loadGate);
            return nodeId;
        };

        var manager = createLoadJobManager(wfm, workflowChangesListener, loader);
        var command = builder(AddComponentCommandEntBuilder.class) //
            .setKind(KindEnum.ADD_COMPONENT) //
            .setProviderId("provider") //
            .setSpaceId("space") //
            .setItemId("item") //
            .setReplacementOptions(builder(ReplacementOptionsEnt.ReplacementOptionsEntBuilder.class) //
                .setTargetNodeId(new NodeIDEnt(targetNode.getID())) //
                .build() //
            ) //
            .setName("replace") //
            .build();

        manager.startLoadJob(command);

        await().untilAsserted(() -> {
            var placeholder = getSinglePlaceholder(manager);
            assertThat(placeholder.getState(), is(StateEnum.LOADING));
        });
        loadGate.countDown();

        await().untilAsserted(() -> {
            var placeholder = getSinglePlaceholder(manager);
            assertThat(placeholder.getState(), is(StateEnum.SUCCESS));
            assertThat(placeholder.getComponentId(), is(new NodeIDEnt(nodeId).toString()));
            assertThat(placeholder.getPosition(), is(expectedPosition));
        });

        assertThat(manager.getComponentPlaceholdersAndCleanUp().isEmpty(), is(true));
    }

    @Test
    public void testWarningsUpdatePlaceholder() throws Exception {
        var wfm = WorkflowManagerUtil.createEmptyWorkflow();
        var workflowChangesListener = mock(WorkflowChangesListener.class);
        var nodeId = new NodeID(3);

        IComponentLoader loader = (ent, workflow, spaces, monitor) -> {
            throw new ComponentLoadedWithWarningsException(nodeId, "Warning", "Details");
        };

        var manager = createLoadJobManager(wfm, workflowChangesListener, loader);
        manager.startLoadJob(createCommand("warning"));

        await().untilAsserted(() -> {
            var placeholder = getSinglePlaceholder(manager);
            assertThat(placeholder.getState(), is(StateEnum.SUCCESS_WITH_WARNING));
            assertThat(placeholder.getComponentId(), is(new NodeIDEnt(nodeId).toString()));
            assertThat(placeholder.getMessage(), is("Warning"));
            assertThat(placeholder.getDetails(), is("Details"));
        });

        assertThat(manager.getComponentPlaceholdersAndCleanUp().isEmpty(), is(true));
    }

    @Test
    public void testCancellationUpdatesErrorPlaceholder() throws Exception {
        var wfm = WorkflowManagerUtil.createEmptyWorkflow();
        var workflowChangesListener = mock(WorkflowChangesListener.class);

        IComponentLoader loader = (ent, workflow, spaces, monitor) -> {
            throw new CancellationException("cancelled");
        };

        var manager = createLoadJobManager(wfm, workflowChangesListener, loader);
        manager.startLoadJob(createCommand("cancelled"));

        await().untilAsserted(() -> {
            var placeholder = getSinglePlaceholder(manager);
            assertThat(placeholder.getState(), is(StateEnum.ERROR));
            assertThat(placeholder.getMessage(), is("Component loading cancelled"));
            assertThat(placeholder.getDetails(), is((String)null));
        });
    }

    @Test
    public void testErrorUpdatesErrorPlaceholder() throws Exception {
        var wfm = WorkflowManagerUtil.createEmptyWorkflow();
        var workflowChangesListener = mock(WorkflowChangesListener.class);

        IComponentLoader loader = (ent, workflow, spaces, monitor) -> {
            throw new IllegalStateException("boom");
        };

        var manager = createLoadJobManager(wfm, workflowChangesListener, loader);
        manager.startLoadJob(createCommand("error"));

        await().untilAsserted(() -> {
            var placeholder = getSinglePlaceholder(manager);
            assertThat(placeholder.getState(), is(StateEnum.ERROR));
            assertThat(placeholder.getMessage(), is("Component could not be loaded"));
            assertThat(placeholder.getDetails(), is("boom"));
        });
    }

    private static ComponentLoadJobManager createLoadJobManager(final WorkflowManager wfm,
        final WorkflowChangesListener workflowChangesListener, final IComponentLoader loader) {
        var spaceProviders = mock(SpaceProviders.class);
        return new ComponentLoadJobManager(wfm, workflowChangesListener, spaceProviders, loader);
    }

    private static AddComponentCommandEnt createCommand(final String name) {
        return builder(AddComponentCommandEntBuilder.class) //
            .setKind(KindEnum.ADD_COMPONENT) //
            .setProviderId("provider") //
            .setSpaceId("space") //
            .setItemId("item") //
            .setPosition(builder(XYEntBuilder.class).setX(10).setY(20).build()) //
            .setName(name) //
            .build();
    }

    private static ComponentPlaceholderEnt getSinglePlaceholder(final ComponentLoadJobManager manager) {
        var placeholders = manager.getComponentPlaceholdersAndCleanUp();
        assertThat(placeholders.size(), is(1));
        return placeholders.iterator().next();
    }

    private static void awaitLatch(final CountDownLatch latch) {
        try {
            if (!latch.await(2, TimeUnit.SECONDS)) {
                throw new AssertionError("Timed out waiting for latch");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AssertionError("Interrupted while waiting for latch", e);
        }
    }
}
