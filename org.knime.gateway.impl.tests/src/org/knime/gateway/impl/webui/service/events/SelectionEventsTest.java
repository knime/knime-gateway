/*
 * ------------------------------------------------------------------------
 *
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
 * History
 *   Nov 25, 2021 (hornm): created
 */
package org.knime.gateway.impl.webui.service.events;

import static org.awaitility.Awaitility.await;
import static org.awaitility.Duration.FIVE_SECONDS;
import static org.awaitility.Duration.ONE_HUNDRED_MILLISECONDS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.knime.gateway.api.webui.entity.SelectionEventEnt.ModeEnum.ADD;
import static org.knime.gateway.api.webui.entity.SelectionEventEnt.ModeEnum.REMOVE;
import static org.knime.gateway.api.webui.entity.SelectionEventEnt.ModeEnum.REPLACE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.awaitility.Awaitility;
import org.awaitility.core.ThrowingRunnable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.knime.core.data.RowKey;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.port.PortType;
import org.knime.core.node.property.hilite.HiLiteHandler;
import org.knime.core.node.property.hilite.HiLiteListener;
import org.knime.core.node.property.hilite.KeyEvent;
import org.knime.core.node.workflow.NativeNodeContainer;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.node.workflow.virtual.subnode.VirtualSubNodeInputNodeFactory;
import org.knime.core.webui.data.ApplyDataService;
import org.knime.core.webui.data.InitialDataService;
import org.knime.core.webui.data.RpcDataService;
import org.knime.core.webui.node.NodeWrapper;
import org.knime.core.webui.node.view.NodeTableView;
import org.knime.core.webui.node.view.NodeView;
import org.knime.core.webui.node.view.NodeViewManager;
import org.knime.core.webui.node.view.table.selection.SelectionTranslationService;
import org.knime.core.webui.page.Page;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.entity.NodeViewEnt;
import org.knime.gateway.api.webui.entity.SelectionEventEnt;
import org.knime.gateway.impl.webui.entity.UIExtensionEntityFactory;
import org.knime.testing.node.SourceNodeTestFactory;
import org.knime.testing.node.view.NodeViewNodeFactory;
import org.knime.testing.node.view.NodeViewNodeModel;
import org.knime.testing.util.WorkflowManagerUtil;
import org.mockito.Mockito;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * TODO
 *
 * Tests {@link SelectionEventBus}.
 *
 * @author Marc Bux, KNIME GmbH, Berlin, Germany
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@SuppressWarnings("javadoc")
public class SelectionEventsTest {

    private static final String WORKFLOW_NAME = "workflow";

    private static final List<String> ROWKEYS_1 = List.of("Row01");

    private static final List<String> ROWKEYS_2 = List.of("Row02");

    private static final List<String> ROWKEYS_1_2 = List.of("Row01", "Row02");

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private WorkflowManager m_wfm;

    private NativeNodeContainer m_nnc;

    private HiLiteHandler m_hlh;

    Function<NodeViewNodeModel, NodeView> viewCreator = m -> { // NOSONAR
        return new NodeTableView() { // NOSONAR

            @Override
            public Page getPage() {
                return Page.builder(() -> "foo", "bar.html").build();
            }

            @Override
            public void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
                //
            }

            @Override
            public void loadValidatedSettingsFrom(final NodeSettingsRO settings) {
                //
            }

            @Override
            public <D> Optional<InitialDataService<D>> createInitialDataService() {
                return Optional.empty();
            }

            @Override
            public Optional<RpcDataService> createRpcDataService() {
                return Optional.empty();
            }

            @Override
            public <D> Optional<ApplyDataService<D>> createApplyDataService() {
                return Optional.empty();
            }

            @Override
            public int getPortIndex() {
                return 0;
            }

        };
    };

    @Before
    public void setup() throws IOException {
        m_wfm = WorkflowManagerUtil.createEmptyWorkflow();
        m_nnc = WorkflowManagerUtil.createAndAddNode(m_wfm, new NodeViewNodeFactory(1, 0, viewCreator));
        SourceNodeTestFactory.connectSourceNodeToInputPort(m_wfm, m_nnc, 0);
        m_hlh = m_nnc.getNodeModel().getInHiLiteHandler(0);
        m_wfm.executeAllAndWaitUntilDone();
    }

    @After
    public void tearDown() {
        m_wfm.getParent().removeProject(m_wfm.getID());
    }

    @Test
    public void testConsumeHiLiteEvent() throws Exception {

        @SuppressWarnings("unchecked")
        final Consumer<SelectionEventEnt> consumerMock = mock(Consumer.class);

        try (var close = setupSelectionEvents(consumerMock, m_nnc)) {
            m_nnc.getNodeModel().getInHiLiteHandler(0).fireHiLiteEvent(stringListToRowKeySet(ROWKEYS_1_2));

            await().pollDelay(ONE_HUNDRED_MILLISECONDS).timeout(FIVE_SECONDS).untilAsserted(
                () -> verify(consumerMock, times(1)).accept(argThat(se -> verifySelectionEvent(se, "root", "root:1"))));

            assertThat(m_hlh.getHiLitKeys(), is(stringListToRowKeySet(ROWKEYS_1_2)));
            m_hlh.fireClearHiLiteEvent();
        }
    }

    @Test
    public void testConsumeUnHiLiteEvent() throws Exception {

        m_hlh.fireHiLiteEvent(stringListToRowKeySet(ROWKEYS_1_2));

        @SuppressWarnings("unchecked")
        final Consumer<SelectionEventEnt> consumerMock = mock(Consumer.class);

        try (var close = setupSelectionEvents(consumerMock, m_nnc)) {
            m_nnc.getNodeModel().getInHiLiteHandler(0).fireUnHiLiteEvent(stringListToRowKeySet(ROWKEYS_1));

            await().pollDelay(ONE_HUNDRED_MILLISECONDS).timeout(FIVE_SECONDS)
                .untilAsserted(() -> verify(consumerMock, times(1))
                    .accept(argThat(se -> se.getSelection().equals(ROWKEYS_1) && se.getMode() == REMOVE)));

            assertThat(m_hlh.getHiLitKeys(), is(stringListToRowKeySet(ROWKEYS_2)));
            m_hlh.fireClearHiLiteEvent();
        }
    }

    @Test
    public void testConsumeReplaceHiLiteEvent() throws Exception {

        m_hlh.fireHiLiteEvent(stringListToRowKeySet(ROWKEYS_1));

        @SuppressWarnings("unchecked")
        final Consumer<SelectionEventEnt> consumerMock = mock(Consumer.class);

        try (var close = setupSelectionEvents(consumerMock, m_nnc)) {
            m_nnc.getNodeModel().getInHiLiteHandler(0).fireReplaceHiLiteEvent(stringListToRowKeySet(ROWKEYS_2));

            await().pollDelay(ONE_HUNDRED_MILLISECONDS).timeout(FIVE_SECONDS)
                .untilAsserted(() -> verify(consumerMock, times(1))
                    .accept(argThat(se -> se.getSelection().equals(ROWKEYS_2) && se.getMode() == REPLACE)));

            assertThat(m_hlh.getHiLitKeys(), is(stringListToRowKeySet(ROWKEYS_2)));
            m_hlh.fireClearHiLiteEvent();
        }
    }

    /**
     * Tests the {@code async}-parameter of the
     * {@link SelectionEventBus#processSelectionEvent(NativeNodeContainer, SelectionEventMode, boolean, List)}-method.
     */
    @Test
    public void testProcessSelectionEventAsync() {
        var hiLiteHandler = m_nnc.getNodeModel().getInHiLiteHandler(0);
        var nodeId = m_nnc.getID();
        var hiLiteListener = new TestHiLiteListener();
        hiLiteHandler.addHiLiteListener(hiLiteListener);

        // async call
        var rowKeys = stringListToRowKeySet(List.of("1"));
        SelectionEventBus.processSelectionEvent(hiLiteHandler, nodeId, ADD, true, rowKeys);
        SelectionEventBus.processSelectionEvent(hiLiteHandler, nodeId, REMOVE, true, rowKeys);
        SelectionEventBus.processSelectionEvent(hiLiteHandler, nodeId, REPLACE, true, rowKeys);
        Awaitility.await().atMost(5, TimeUnit.SECONDS).pollInterval(200, TimeUnit.MILLISECONDS)
            .untilAsserted(() -> assertThat(hiLiteListener.m_callerThreadName, is(not(nullValue()))));
        assertThat(hiLiteListener.m_callerThreadName, is(not("INVALID")));
        assertThat(hiLiteListener.m_callerThreadName, is(not(Thread.currentThread().getName())));
        hiLiteListener.m_callerThreadName = null;

        // sync call
        rowKeys = stringListToRowKeySet(List.of("2"));
        SelectionEventBus.processSelectionEvent(hiLiteHandler, nodeId, ADD, false, rowKeys);
        SelectionEventBus.processSelectionEvent(hiLiteHandler, nodeId, REMOVE, false, rowKeys);
        SelectionEventBus.processSelectionEvent(hiLiteHandler, nodeId, REPLACE, false, rowKeys);
        assertThat(hiLiteListener.m_callerThreadName, is(Thread.currentThread().getName()));
    }

    /**
     * Makes sure that an event source registered on a node without a view doesn't emit selection events.
     *
     * @throws Exception
     */
    @Test
    public void testNoSelectionEventForNodeWithoutAView() throws Exception {
        NativeNodeContainer nnc = WorkflowManagerUtil.createAndAddNode(m_wfm,
            new VirtualSubNodeInputNodeFactory(null, new PortType[]{BufferedDataTable.TYPE}));

        @SuppressWarnings("unchecked")
        final Consumer<SelectionEventEnt> consumerMock = mock(Consumer.class);
        try (var close = setupSelectionEvents(consumerMock, m_nnc)) {
            nnc.getNodeModel().getInHiLiteHandler(0).fireUnHiLiteEvent(new KeyEvent(stringListToRowKeySet(ROWKEYS_1)),
                false);
            verify(consumerMock, times(0)).accept(any());
        }
    }

    @Test
    public void testSelectionEventWithError() throws Exception {
        Function<NodeViewNodeModel, NodeView> viewCreator = m -> { // NOSONAR
            return new NodeTableView() { // NOSONAR

                @Override
                public Optional<SelectionTranslationService> createSelectionTranslationService() {
                    return Optional.of(new SelectionTranslationService() {

                        @Override
                        public Set<RowKey> toRowKeys(final List<String> selection) throws IOException {
                            return Collections.emptySet();
                        }

                        @Override
                        public List<String> fromRowKeys(final Set<RowKey> rowKeys) throws IOException {
                            throw new IOException("foo");
                        }
                    });
                }

                @Override
                public Optional<InitialDataService<?>> createInitialDataService() {
                    return Optional.empty();
                }

                @Override
                public Optional<RpcDataService> createRpcDataService() {
                    return Optional.empty();
                }

                @Override
                public Optional<ApplyDataService<?>> createApplyDataService() {
                    return Optional.empty();
                }

                @Override
                public void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
                    //
                }

                @Override
                public void loadValidatedSettingsFrom(final NodeSettingsRO settings) {
                    //
                }

                @Override
                public Page getPage() {
                    return Page.builder(() -> "foo", "bar").build();
                }

                @Override
                public int getPortIndex() {
                    return 0;
                }

            };
        };
        var nnc = WorkflowManagerUtil.createAndAddNode(m_wfm, new NodeViewNodeFactory(1, 0, viewCreator));

        @SuppressWarnings("unchecked")
        final Consumer<SelectionEventEnt> consumerMock = mock(Consumer.class);
        try (var close = setupSelectionEvents(consumerMock, nnc)) {
            var handler = nnc.getNodeModel().getInHiLiteHandler(0);
            handler.fireHiLiteEvent(new KeyEvent(handler, stringListToRowKeySet(ROWKEYS_1)), false);

            verify(consumerMock, times(1))
                .accept(argThat(se -> se.getSelection() == null && se.getMode() == ADD && se.getError().equals("foo")));
        }
    }

    private static class TestHiLiteListener implements HiLiteListener {

        String m_callerThreadName = null;

        @Override
        public void hiLite(final KeyEvent event) {
            updateThreadName();
        }

        @Override
        public void unHiLite(final KeyEvent event) {
            updateThreadName();
        }

        @Override
        public void unHiLiteAll(final KeyEvent event) {
            updateThreadName();
        }

        private void updateThreadName() {
            var threadName = Thread.currentThread().getName();
            if (m_callerThreadName == null) {
                m_callerThreadName = threadName;
            } else if (!m_callerThreadName.equals(threadName)) {
                m_callerThreadName = "INVALID";
            }
        }

    }

    private static boolean verifySelectionEvent(final SelectionEventEnt se, final String workflowId,
        final String nodeId) {
        return se.getSelection().equals(ROWKEYS_1_2) && se.getMode() == SelectionEventEnt.ModeEnum.ADD
            && se.getNodeId().equals(new NodeIDEnt(nodeId)) && se.getWorkflowId().equals(new NodeIDEnt(workflowId))
            && se.getProjectId().startsWith(WORKFLOW_NAME);
    }

    private static Set<RowKey> stringListToRowKeySet(final List<String> keys) {
        return keys.stream().map(RowKey::new).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private static AutoCloseable setupSelectionEvents(final Consumer<SelectionEventEnt> selectionEventConsumer,
        final NativeNodeContainer nnc) {
        var selectionEventBus = new SelectionEventBus();
        selectionEventBus.addSelectionEventListener(selectionEventConsumer);
        selectionEventBus.addSelectionEventEmitterAndGetInitialEvent(NodeWrapper.of(nnc),
            NodeViewManager.getInstance().getTableViewManager());
        return () -> {
            selectionEventBus.removeSelectionEventListener(selectionEventConsumer);
            selectionEventBus.removeSelectionEventEmitter(NodeWrapper.of(nnc));
        };
    }

    /**
     * Tests the {@link SelectionEventBus} in conjunction with {@link NodeViewEnt}.
     */
    @Test
    public void testNodeViewEntWithSelectionEvents() {
        var hiLiteHandler = m_nnc.getNodeModel().getInHiLiteHandler(0);
        hiLiteHandler.fireHiLiteEvent(new RowKey("k1"), new RowKey("k2"));

        @SuppressWarnings("unchecked")
        final Consumer<SelectionEventEnt> consumerMock = mock(Consumer.class);
        var selectionEventBus = new SelectionEventBus();
        selectionEventBus.addSelectionEventListener(consumerMock);
        var initialSelection = selectionEventBus
            .addSelectionEventEmitterAndGetInitialEvent(NodeWrapper.of(m_nnc),
                NodeViewManager.getInstance().getTableViewManager())
            .map(SelectionEventEnt::getSelection).orElse(Collections.emptyList());
        var nodeViewEnt = NodeViewEnt.create(m_nnc, () -> initialSelection);

        assertThat(nodeViewEnt.getInitialSelection(), is(List.of("k1", "k2")));

        hiLiteHandler.fireHiLiteEvent(new RowKey("k3"));
        await().pollDelay(ONE_HUNDRED_MILLISECONDS).timeout(FIVE_SECONDS)
            .untilAsserted(() -> verify(consumerMock, times(1))
                .accept(argThat(se -> se.getSelection().equals(List.of("k3")) && se.getMode() == ADD)));

        selectionEventBus.removeSelectionEventEmitter(NodeWrapper.of(m_nnc));
        selectionEventBus.removeSelectionEventListener(consumerMock);
    }

    /**
     * Tests
     * {@link NodeViewEntUtil#createNodeViewEntAndEventSources(org.knime.core.node.workflow.NativeNodeContainer, BiConsumer, boolean)}.
     *
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testCreateNodeViewEntAndSetUpSelectionEvents() throws Exception {

        Function<NodeViewNodeModel, NodeView> viewCreator = m -> { // NOSONAR
            return new NodeTableView() { // NOSONAR

                @Override
                public Page getPage() {
                    return Page.builder(() -> "foo", "index.html").build();
                }

                @Override
                public void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
                    //
                }

                @Override
                public void loadValidatedSettingsFrom(final NodeSettingsRO settings) {
                    //
                }

                @Override
                public Optional<InitialDataService<String>> createInitialDataService() {
                    return Optional.of(InitialDataService.builder(() -> "the initial data").build());
                }

                @Override
                public Optional<RpcDataService> createRpcDataService() {
                    return Optional.empty();
                }

                @Override
                public Optional<ApplyDataService<String>> createApplyDataService() {
                    return Optional.empty();
                }

                @Override
                public int getPortIndex() {
                    return 0;
                }

            };
        };

        var wfm = WorkflowManagerUtil.createEmptyWorkflow();
        var nnc = WorkflowManagerUtil.createAndAddNode(wfm, new NodeViewNodeFactory(1, 0, viewCreator));
        SourceNodeTestFactory.connectSourceNodeToInputPort(wfm, nnc, 0);

        var hlh = nnc.getNodeModel().getInHiLiteHandler(0);
        wfm.executeAllAndWaitUntilDone();

        BiConsumer<String, Object> eventConsumer = Mockito.mock(BiConsumer.class);

        var selectionEventBus = new SelectionEventBus();

        /* assert that the selection events are properly set up */
        try (var dispose =
            UIExtensionEntityFactory.createNodeViewEntAndSetupEvents(nnc, eventConsumer, false, selectionEventBus)
                .getSecond()) {
            fireHiLiteEvent(hlh, "test");
            verify(eventConsumer).accept(eq("SelectionEvent"),
                argThat(se -> verifySelectionEvent((SelectionEventEnt)se, "test")));

            /* assert that all the listeners are removed from the selection event registry on node state change */
            wfm.resetAndConfigureAll();
            fireHiLiteEvent(hlh, "test2");
            verify(eventConsumer, never()).accept(eq("SelectionEvent"),
                argThat(se -> verifySelectionEvent((SelectionEventEnt)se, "test2")));
        }

        /* test the selection events in combination with the node view state events */
        // test selection events
        wfm.executeAllAndWaitUntilDone();
        try (var dipose =
            UIExtensionEntityFactory.createNodeViewEntAndSetupEvents(nnc, eventConsumer, true, selectionEventBus)
                .getSecond()) {
            fireHiLiteEvent(hlh, "test3");
            verify(eventConsumer).accept(eq("SelectionEvent"),
                argThat(se -> verifySelectionEvent((SelectionEventEnt)se, "test3")));
            // test node view state event: configured
            wfm.resetAndConfigureAll();
            awaitUntilAsserted(() -> verify(eventConsumer).accept(eq("NodeViewStateEvent"),
                argThat(se -> verifyNodeViewStateEvent((NodeViewStateEvent)se, "configured", null))));
            // make sure no selection events are fired if node is not executed
            fireHiLiteEvent(hlh, "test4");
            verify(eventConsumer, never()).accept(eq("SelectionEvent"),
                argThat(se -> verifySelectionEvent((SelectionEventEnt)se, "test4")));
            // test node view state event: executed
            wfm.executeAllAndWaitUntilDone();
            awaitUntilAsserted(() -> verify(eventConsumer).accept(eq("NodeViewStateEvent"),
                argThat(se -> verifyNodeViewStateEvent((NodeViewStateEvent)se, "executed", "the initial data"))));
        }
        WorkflowManagerUtil.disposeWorkflow(wfm);
    }

    private static boolean verifySelectionEvent(final SelectionEventEnt se, final String rowKey) {
        return se.getSelection().equals(List.of(rowKey)) && se.getMode() == ADD;
    }

    private static boolean verifyNodeViewStateEvent(final NodeViewStateEvent e, final String state,
        final String initialData) {
        return e.getNodeView().getNodeInfo().getNodeState().equals(state)
            && Objects.equals(getInitialDataResult(e.getNodeView().getInitialData()), initialData);
    }

    private static String getInitialDataResult(final String initialData) {
        if (initialData == null) {
            return null;
        }
        try {
            return MAPPER.readTree(initialData).get("result").asText();
        } catch (JsonProcessingException ex) {
            throw new AssertionError(ex);
        }
    }

    private static void fireHiLiteEvent(final HiLiteHandler hlh, final String rowKey) {
        hlh.fireHiLiteEvent(new KeyEvent(hlh, new RowKey(rowKey)), false);
    }

    private static void awaitUntilAsserted(final ThrowingRunnable runnable) {
        await().pollDelay(ONE_HUNDRED_MILLISECONDS).timeout(FIVE_SECONDS).untilAsserted(runnable);
    }

}
