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
 */
package org.knime.gateway.impl.webui.syncing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.knime.core.node.workflow.WorkflowListener;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.api.util.DataSize;
import org.knime.gateway.api.webui.entity.SyncStateEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.ServiceCallException;
import org.knime.gateway.impl.webui.syncing.HubUploader.SyncThresholdException;
import org.knime.gateway.impl.webui.syncing.WorkflowSyncer.DefaultWorkflowSyncer;

/**
 * Unit tests for {@link DefaultWorkflowSyncer}.
 */
@SuppressWarnings({"resource", "java:S5960"}) // dispose is invoked manually; assertions in production code are ok
public class WorkflowSyncerTest {

    private WorkflowManager m_wfm;

    private AtomicInteger m_stateChangesCount;

    private DefaultWorkflowSyncer m_syncer;

    private LocalSaver m_localSaver;

    private HubUploader m_hubUploader;

    @SuppressWarnings("java:S1450")
    private SyncStateStore m_syncStateStore;

    private WorkflowListener m_workflowListener;

    private static final DataSize SOME_SIZE = new DataSize(0);

    private String m_javaAwtHeadlessSysProp;

    /**
     * Prepare a fresh syncer with mocked dependencies for each test.
     *
     * @throws Exception if test setup fails
     */
    @Before
    public void setUp() throws Exception {
        m_wfm = mock(WorkflowManager.class);
        when(m_wfm.getName()).thenReturn("test-wf");

        // count state changes invocations
        m_stateChangesCount = new AtomicInteger();
        Runnable listener = () -> m_stateChangesCount.incrementAndGet();

        var config = new WorkflowSyncer.SyncerConfig(Duration.ZERO, SOME_SIZE);

        m_syncStateStore = new SyncStateStore();
        m_syncStateStore.addOnStateChangeListener(listener);
        m_localSaver = mock(LocalSaver.class);
        m_hubUploader = mock(HubUploader.class);
        m_workflowListener = mock(WorkflowListener.class);
        var debouncer = mock(org.knime.gateway.impl.util.Debouncer.class);

        m_javaAwtHeadlessSysProp = System.getProperty("java.awt.headless");
        System.setProperty("java.awt.headless", "true");
        m_syncer = new DefaultWorkflowSyncer( //
            m_wfm, //
            config, //
            m_syncStateStore, //
            cb -> m_workflowListener, //
            m_localSaver, //
            m_hubUploader, //
            task -> debouncer //
        );

        // ensure listener registration happened
        verify(m_wfm).addListener(any(WorkflowListener.class));
    }

    /** Dispose syncer after each test run. */
    @After
    public void tearDown() {
        if (m_javaAwtHeadlessSysProp == null) {
            System.clearProperty("java.awt.headless");
        } else {
            System.setProperty("java.awt.headless", m_javaAwtHeadlessSysProp);
        }
        m_syncer.dispose();
    }

    /**
     * Sync now should save, upload, and reach SYNCED state.
     *
     * @throws Exception when sync fails unexpectedly
     */
    @Test
    public void testSyncProjectNowUpdatesStateAndUploads() throws Exception {
        m_syncer.syncProjectNow();

        verify(m_localSaver).saveProject(m_wfm);
        verify(m_hubUploader).uploadProject(m_wfm);

        assertThat(m_syncer.getSyncState().getState()).isEqualTo(SyncStateEnt.StateEnum.SYNCED);
        // WRITING -> UPLOAD -> SYNCED
        assertThat(m_stateChangesCount.get()).isEqualTo(3);
    }

    /**
     * Sync now should surface errors from save failures.
     *
     * @throws Exception when sync invocation fails unexpectedly
     */
    @Test
    public void testSyncProjectNowSetsErrorOnSaveFailure() throws Exception {
        doThrow(new IOException("exception message")).when(m_localSaver).saveProject(m_wfm);

        assertThatThrownBy(() -> m_syncer.syncProjectNow()).isInstanceOf(ServiceCallException.class);

        assertThat(m_syncer.getSyncState().getState()).isEqualTo(SyncStateEnt.StateEnum.ERROR);
        // WRITING -> ERROR
        assertThat(m_stateChangesCount.get()).isEqualTo(2);
    }

    /**
     * Auto-sync should apply deferred DIRTY state after upload completes.
     *
     * @throws Exception when sync invocation fails unexpectedly
     */
    @Test
    public void testAutoSyncAppliesDeferredDirtyUpdateAfterUpload() throws Exception {
        // Simulate workflow change happening during upload -> deferred DIRTY should win
        doAnswer(inv -> {
            m_syncStateStore.changeStateDeferrable(SyncStateEnt.StateEnum.DIRTY);
            return null;
        }).when(m_hubUploader).uploadProjectWithThreshold(eq(m_wfm), any(DataSize.class));

        m_syncer.syncProjectAutomatically(SOME_SIZE);

        verify(m_localSaver).saveProject(m_wfm);
        verify(m_hubUploader).uploadProjectWithThreshold(eq(m_wfm), any(DataSize.class));

        assertThat(m_syncer.getSyncState().getState()).isEqualTo(SyncStateEnt.StateEnum.DIRTY);
        // WRITING -> UPLOAD -> SYNCED -> DIRTY (deferred)
        assertThat(m_stateChangesCount.get()).isEqualTo(4);
    }

    /**
     * Tests that the WorkflowSyncer disposes itself when the size limit is exceeded.
     *
     * @throws SyncThresholdException
     * @throws IOException
     */
    @Test
    public void testDisposeSyncerWhenExceedingSizeLimit() throws IOException, SyncThresholdException {
        doThrow(new SyncThresholdException("blub")).when(m_hubUploader).uploadProjectWithThreshold(any(), any());

        m_syncer.syncProjectAutomatically(SOME_SIZE);

        verify(m_wfm, never()).removeListener(m_workflowListener);
        var state = m_syncer.getSyncState();
        assertThat(state.getState()).isEqualTo(SyncStateEnt.StateEnum.DIRTY);
        assertThat(state.getError().getCode()).isEqualTo(SyncThresholdException.class.getSimpleName());
    }

}
