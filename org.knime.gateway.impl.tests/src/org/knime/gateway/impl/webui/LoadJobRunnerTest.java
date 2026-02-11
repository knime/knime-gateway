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
 */
package org.knime.gateway.impl.webui;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.NodeProgressListener;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.impl.service.util.WorkflowChangesListener;
import org.knime.gateway.impl.webui.ComponentLoadJobManager.LoadJobRunner;
import org.knime.gateway.impl.webui.ComponentLoadJobManager.PostLoadAction;
import org.knime.gateway.impl.webui.service.commands.WorkflowCommand;
import org.knime.gateway.impl.webui.service.commands.util.ComponentLoader.ComponentLoadedWithWarningsException;
import org.knime.gateway.impl.webui.spaces.SpaceProviders;
import org.knime.testing.util.WorkflowManagerUtil;

/**
 * Tests for {@link ComponentLoadJobManager.LoadJobRunner}.
 */
public class LoadJobRunnerTest {

    @Test
    public void testRunInvokesSuccessCallbackAndPostLoadAction() throws Exception {
        var wfm = WorkflowManagerUtil.createEmptyWorkflow();
        var manager = createManager(wfm);
        var nodeId = new NodeID(1);
        var successId = new AtomicReference<NodeID>();
        var postLoadComponentId = new AtomicReference<NodeID>();
        var postLoadWfm = new AtomicReference<WorkflowManager>();
        var warningCalled = new AtomicBoolean(false);
        var cancelledCalled = new AtomicBoolean(false);
        var errorCalled = new AtomicBoolean(false);
        var successTime = new AtomicLong(0);
        var postLoadTime = new AtomicLong(0);
        var command = mock(WorkflowCommand.class);

        PostLoadAction postLoadAction = (workflow, componentId) -> {
            postLoadWfm.set(workflow);
            postLoadComponentId.set(componentId);
            postLoadTime.set(System.nanoTime());
            return command;
        };

        LoadJobRunner runner = manager.new LoadJobRunner(
            monitor -> nodeId,
            postLoadAction,
            createProgressListener(),
            id -> {
                successTime.set(System.nanoTime());
                successId.set(id);
            },
            ex -> warningCalled.set(true),
            ex -> cancelledCalled.set(true),
            ex -> errorCalled.set(true)
        );

        runner.run();

        await().untilAsserted(() -> assertThat(successId.get(), is(nodeId)));
        await().untilAsserted(() -> assertThat(runner.postLoadGetNow().orElse(null), is(command)));
        assertThat(postLoadComponentId.get(), is(nodeId));
        assertThat(postLoadWfm.get(), is(wfm));
        assertThat(successTime.get() > 0, is(true));
        assertThat(postLoadTime.get() >= successTime.get(), is(true));
        assertThat(warningCalled.get(), is(false));
        assertThat(cancelledCalled.get(), is(false));
        assertThat(errorCalled.get(), is(false));
    }

    @Test
    public void testRunInvokesWarningsCallbackAndCompletesLoadFuture() throws Exception {
        var manager = createManager(WorkflowManagerUtil.createEmptyWorkflow());
        var nodeId = new NodeID(2);
        var successId = new AtomicReference<NodeID>();
        var warningRef = new AtomicReference<ComponentLoadedWithWarningsException>();
        var cancelledCalled = new AtomicBoolean(false);
        var errorCalled = new AtomicBoolean(false);

        LoadJobRunner runner = manager.new LoadJobRunner(
            monitor -> {
                throw new ComponentLoadedWithWarningsException(nodeId, "Warning", "Details");
            },
            null,
            createProgressListener(),
            successId::set,
            warningRef::set,
            ex -> cancelledCalled.set(true),
            ex -> errorCalled.set(true)
        );

        runner.run();

        await().untilAsserted(() -> assertThat(warningRef.get() != null, is(true)));
        assertThat(warningRef.get().getComponentId(), is(nodeId));
        assertThat(warningRef.get().getTitle(), is("Warning"));
        assertThat(warningRef.get().getMessage(), is("Details"));
        await().untilAsserted(() -> assertThat(runner.loadGetNow().orElse(null), is(nodeId)));
        assertThat(successId.get(), is((NodeID)null));
        assertThat(cancelledCalled.get(), is(false));
        assertThat(errorCalled.get(), is(false));
    }

    @Test
    public void testRunInvokesCancelledCallback() throws Exception {
        var manager = createManager(WorkflowManagerUtil.createEmptyWorkflow());
        var cancelledRef = new AtomicReference<CancellationException>();
        var errorCalled = new AtomicBoolean(false);

        LoadJobRunner runner = manager.new LoadJobRunner(
            monitor -> {
                throw new CancellationException("cancelled");
            },
            null,
            createProgressListener(),
            id -> {
            },
            ex -> {
            },
            cancelledRef::set,
            ex -> errorCalled.set(true)
        );

        runner.run();

        await().untilAsserted(() -> assertThat(cancelledRef.get().getMessage(), is("cancelled")));
        await().untilAsserted(() -> assertThat(runner.isLoadDone(), is(true)));
        assertThat(runner.loadGetNow().isEmpty(), is(true));
        assertThat(errorCalled.get(), is(false));
    }

    @Test
    public void testRunInvokesErrorCallback() throws Exception {
        var manager = createManager(WorkflowManagerUtil.createEmptyWorkflow());
        var errorRef = new AtomicReference<Throwable>();
        var cancelledCalled = new AtomicBoolean(false);
        var error = new IllegalStateException("boom");

        LoadJobRunner runner = manager.new LoadJobRunner(
            monitor -> {
                throw error;
            },
            null,
            createProgressListener(),
            id -> {
            },
            ex -> {
            },
            ex -> cancelledCalled.set(true),
            errorRef::set
        );

        runner.run();

        await().untilAsserted(() -> assertThat(errorRef.get(), is(error)));
        await().untilAsserted(() -> assertThat(runner.isLoadDone(), is(true)));
        assertThat(runner.loadGetNow().isEmpty(), is(true));
        assertThat(cancelledCalled.get(), is(false));
    }

    @Test
    public void testProgressEventEmittedImmediatelyAndListenerRemoved() throws Exception {
        var manager = createManager(WorkflowManagerUtil.createEmptyWorkflow());
        var nodeId = new NodeID(3);
        var progressEventLatch = new CountDownLatch(1);
        var progressEventTime = new AtomicLong(0);
        var loadStartTime = new AtomicLong(0);
        var progressCount = new AtomicInteger(0);
        var monitorRef = new AtomicReference<ExecutionMonitor>();

        LoadJobRunner runner = manager.new LoadJobRunner(
            monitor -> {
                monitorRef.set(monitor);
                loadStartTime.set(System.nanoTime());
                try {
                    progressEventLatch.await(2, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new AssertionError("Interrupted while waiting for progress event", e);
                }
                return nodeId;
            },
            null,
            () -> event -> {
                progressCount.incrementAndGet();
                if (progressEventTime.compareAndSet(0, System.nanoTime())) {
                    progressEventLatch.countDown();
                }
            },
            id -> {
            },
            ex -> {
            },
            ex -> {
            },
            ex -> {
            }
        );

        runner.run();

        await().untilAsserted(() -> assertThat(runner.isLoadDone(), is(true)));
        assertThat(progressEventTime.get() > 0, is(true));
        assertThat(loadStartTime.get() > 0, is(true));

        var countAfterLoad = progressCount.get();
        monitorRef.get().setProgress(0.5);
        assertThat(progressCount.get(), is(countAfterLoad));
    }

    @Test
    public void testCancelMarksMonitorCanceled() throws Exception {
        var manager = createManager(WorkflowManagerUtil.createEmptyWorkflow());
        var nodeId = new NodeID(4);
        var monitorReady = new CountDownLatch(1);
        var cancelIssued = new CountDownLatch(1);
        var cancelObserved = new AtomicBoolean(false);

        LoadJobRunner runner = manager.new LoadJobRunner(
            monitor -> {
                monitorReady.countDown();
                try {
                    cancelIssued.await(2, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new AssertionError("Interrupted while waiting for cancel", e);
                }
                try {
                    monitor.checkCanceled();
                } catch (CanceledExecutionException e) {
                    cancelObserved.set(true);
                }
                return nodeId;
            },
            null,
            createProgressListener(),
            id -> {
            },
            ex -> {
            },
            ex -> {
            },
            ex -> {
            }
        );

        runner.run();

        await().untilAsserted(() -> assertThat(monitorReady.getCount(), is(0L)));
        runner.cancel();
        cancelIssued.countDown();

        await().untilAsserted(() -> assertThat(runner.isLoadDone(), is(true)));
        assertThat(cancelObserved.get(), is(true));
    }

    @Test
    public void testGetNowBeforeAndAfterCompletion() throws Exception {
        var manager = createManager(WorkflowManagerUtil.createEmptyWorkflow());
        var nodeId = new NodeID(5);
        var loadStarted = new CountDownLatch(1);
        var releaseLoad = new CountDownLatch(1);
        var command = mock(WorkflowCommand.class);

        LoadJobRunner runner = manager.new LoadJobRunner(
            monitor -> {
                loadStarted.countDown();
                try {
                    releaseLoad.await(2, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new AssertionError("Interrupted while waiting to release load", e);
                }
                return nodeId;
            },
            (workflow, componentId) -> command,
            createProgressListener(),
            id -> {
            },
            ex -> {
            },
            ex -> {
            },
            ex -> {
            }
        );

        runner.run();

        await().untilAsserted(() -> assertThat(loadStarted.getCount(), is(0L)));
        assertThat(runner.isLoadDone(), is(false));
        assertThat(runner.loadGetNow().isEmpty(), is(true));
        assertThat(runner.postLoadGetNow().isEmpty(), is(true));

        releaseLoad.countDown();

        await().untilAsserted(() -> assertThat(runner.loadGetNow().orElse(null), is(nodeId)));
        await().untilAsserted(() -> assertThat(runner.postLoadGetNow().orElse(null), is(command)));
    }

    private static ComponentLoadJobManager createManager(final WorkflowManager wfm) {
        var workflowChangesListener = mock(WorkflowChangesListener.class);
        var spaceProviders = mock(SpaceProviders.class);
        return new ComponentLoadJobManager(wfm, workflowChangesListener, spaceProviders);
    }

    private static java.util.function.Supplier<NodeProgressListener> createProgressListener() {
        return () -> event -> {
        };
    }

}
