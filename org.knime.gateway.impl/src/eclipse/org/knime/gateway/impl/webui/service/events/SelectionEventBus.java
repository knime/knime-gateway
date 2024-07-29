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

import static org.knime.gateway.api.entity.EntityBuilderManager.builder;
import static org.knime.gateway.api.webui.entity.SelectionEventEnt.ModeEnum.ADD;
import static org.knime.gateway.api.webui.entity.SelectionEventEnt.ModeEnum.REMOVE;
import static org.knime.gateway.api.webui.entity.SelectionEventEnt.ModeEnum.REPLACE;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import org.knime.core.data.RowKey;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.property.hilite.HiLiteHandler;
import org.knime.core.node.property.hilite.HiLiteListener;
import org.knime.core.node.property.hilite.KeyEvent;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.SingleNodeContainer;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.webui.node.NodePortWrapper;
import org.knime.core.webui.node.NodeWrapper;
import org.knime.core.webui.node.view.table.TableViewManager;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.SelectionEventEnt;
import org.knime.gateway.api.webui.entity.SelectionEventEnt.SelectionEventEntBuilder;

/**
 * Brings together selection event emitters (node via {@link HiLiteHandler}) and selection event listeners (UI, detached
 * views, data app).
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @author Marc Bux, KNIME GmbH, Berlin, Germany
 *
 * @since 5.3
 */
public final class SelectionEventBus {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(SelectionEventBus.class);


    private Set<Consumer<SelectionEventEnt>> m_eventListeners = Collections.synchronizedSet(new HashSet<>());

    private final Map<NodeID, PerNodeWrapperEventEmitter> m_eventEmitters = new HashMap<>();

    /**
     * Adds a new selection event emitter (a node or port).
     *
     * @param nw the node or port
     * @param tableViewManager provides the hilite-handler and selection translation logic
     * @param <N>
     *
     * @return the initial selection event if there is any
     */
    public <N extends NodeWrapper> Optional<SelectionEventEnt> addSelectionEventEmitterAndGetInitialEvent(final N nw,
        final TableViewManager<N> tableViewManager) {
        return addSelectionEventEmitterAndGetInitialEvent(nw, null, tableViewManager);
    }

    /**
     * Adds a new selection event emitter (a node or port).
     *
     * @param nw the node or port
     * @param tableViewManager provides the hilite-handler and selection translation logic
     * @param projectId
     * @param <N>
     *
     * @return the initial selection event if there is any
     */
    public <N extends NodeWrapper> Optional<SelectionEventEnt> addSelectionEventEmitterAndGetInitialEvent(final N nw,
        final String projectId, final TableViewManager<N> tableViewManager) {
        var handler = tableViewManager.getHiLiteHandler(nw).orElse(null);
        if (handler == null) {
            return Optional.empty();
        }
        // guaranteed to be a single node container because
        // * node views (and, hence, selection) only available for native nodes
        // * in case of port views on metanode output ports, the actual original node the port belongs to is provided
        //   (see CEFNodeView(NodeContainer), ...)
        var snc = (SingleNodeContainer)nw.get();
        synchronized (handler) {
            var hiLitKeys = handler.getHiLitKeys();
            var emitter = new PerNodeWrapperEventEmitter(nw, projectId,
                s -> translateSelections(nw, s, tableViewManager), handler);
            var selectionEvent = hiLitKeys.isEmpty() ? null : emitter.createSelectionEvent(ADD, hiLitKeys);
            var nodeID = snc.getID();
            if (!m_eventEmitters.containsKey(nodeID)) {
                handler.addHiLiteListener(emitter);
                m_eventEmitters.put(snc.getID(), emitter);
                LOGGER.debug(
                    "Selection event emitter added for node " + nodeID + ". Num emitters: " + m_eventEmitters.size());
            }
            return Optional.ofNullable(selectionEvent);
        }

    }

    private <N extends NodeWrapper> SelectionTranslationResult translateSelections(final N nw,
        final Set<RowKey> rowKeys, final TableViewManager<N> tableViewManager) {
        try {
            return new SelectionTranslationResult(tableViewManager.callSelectionTranslationService(nw, rowKeys), null);
        } catch (IOException ex) {
            NodeLogger.getLogger(this.getClass()).error("Selection event couldn't be created", ex);
            return new SelectionTranslationResult(null, ex.getMessage());
        }
    }

    /**
     * Removes event emitter(s) again.
     *
     * @param testNodeID a predicate that returns {@code true} if the emitter for the respective node-id is to be
     *            removed
     */
    public void removeSelectionEventEmitterIf(final Predicate<NodeID> testNodeID) {
        m_eventEmitters.keySet().removeIf(nodeID -> {
            if (testNodeID.test(nodeID)) {
                m_eventEmitters.get(nodeID).removeHiLiteListenerFromHandler();
                LOGGER.debug(
                    "Selection event emitter removed for node " + nodeID + ". Num emitters: " + m_eventEmitters.size());
                return true;
            } else {
                return false;
            }
        });
    }

    /**
     * Removes the selection event emitter for the given node/port.
     *
     * @param nw
     */
    public void removeSelectionEventEmitter(final NodeWrapper nw) {
        var nodeId = nw.get().getID();
        var listener = m_eventEmitters.remove(nodeId);
        if (listener != null) {
            listener.removeHiLiteListenerFromHandler();
        }
        LOGGER
            .debug("Selection event emitter removed for node " + nodeId + ". Num emitters: " + m_eventEmitters.size());
    }

    /**
     * The total number of event emitters registered for the event source. Mainly for testing purposes.
     *
     * @return the total number of event emitters registered
     */
    public int getNumEventEmitters() {
        return m_eventEmitters.size();
    }

    /**
     * Adds a selection event listener.
     *
     * @param listener
     */
    public void addSelectionEventListener(final Consumer<SelectionEventEnt> listener) {
        m_eventListeners.add(listener);
        LOGGER.debug("Selection event listener added. Num listeners: " + m_eventListeners.size());
    }

    /**
     * Removes a selection event listener.
     *
     * @param listener
     */
    public void removeSelectionEventListener(final Consumer<SelectionEventEnt> listener) {
        m_eventListeners.remove(listener);
        LOGGER.debug("Selection event listener removed. Num listeners: " + m_eventListeners.size());
    }

    /**
     * Removes all selection event listeners and emitters and everything else that is required in the process.
     */
    public void clear() {
        m_eventListeners.clear();
        m_eventEmitters.values().forEach(PerNodeWrapperEventEmitter::removeHiLiteListenerFromHandler);
        m_eventEmitters.clear();
    }

    /**
     * Forwards selection events to the given hilite-handler.
     *
     * @param hiLiteHandler hilite-handler to use
     * @param nodeId the id of the node the hilite-handler is associated with
     * @param selectionEventMode the selection event mode
     * @param async if {@code true}, it will return immediately; if {@code false} it will return once the selection has
     *            been processed completely (i.e. once all associated nodes have received the selection change, too).
     * @param rowKeys the keys to be (un-)selected
     */
    public static void processSelectionEvent(final HiLiteHandler hiLiteHandler, final NodeID nodeId,
        final SelectionEventEnt.ModeEnum selectionEventMode, final boolean async, final Set<RowKey> rowKeys) {
        final var keyEvent = new KeyEvent(nodeId, rowKeys);
        switch (selectionEventMode) {
            case ADD:
                hiLiteHandler.fireHiLiteEvent(keyEvent, async);
                break;
            case REMOVE:
                hiLiteHandler.fireUnHiLiteEvent(keyEvent, async);
                break;
            case REPLACE:
                hiLiteHandler.fireReplaceHiLiteEvent(keyEvent, async);
                break;
            default:
        }
    }

    private class PerNodeWrapperEventEmitter implements HiLiteListener {

        private final String m_projectId;

        private final NodeIDEnt m_workflowId;

        private final NodeIDEnt m_nodeIdEnt;

        private final Function<Set<RowKey>, SelectionTranslationResult> m_translateSelection;

        private final HiLiteHandler m_hiLiteHandler;

        private final Integer m_portIndex;

        PerNodeWrapperEventEmitter(final NodeWrapper nw, final String projectId,
            final Function<Set<RowKey>, SelectionTranslationResult> translateSelection,
            final HiLiteHandler hiLiteHandler) {
            m_translateSelection = translateSelection;
            m_hiLiteHandler = hiLiteHandler;
            var snc = nw.get();
            var parent = snc.getParent();
            var projectWfm = parent.getProjectWFM();
            m_projectId = projectId == null ? projectWfm.getNameWithID() : projectId;
            var directNCParent = parent.getDirectNCParent();
            NodeID ncParentId =
                directNCParent instanceof SubNodeContainer directSNCParent ? directSNCParent.getID() : parent.getID();
            m_workflowId = new NodeIDEnt(ncParentId);
            m_nodeIdEnt = new NodeIDEnt(snc.getID());
            m_portIndex = nw instanceof NodePortWrapper npw ? npw.getPortIdx() : null;
            m_hiLiteHandler.addHiLiteListener(this); // NOSONAR
        }

        @Override
        public void hiLite(final KeyEvent event) {
            consumeSelectionEvent(event, ADD);
        }

        @Override
        public void unHiLite(final KeyEvent event) {
            consumeSelectionEvent(event, REMOVE);
        }

        @Override
        public void unHiLiteAll(final KeyEvent event) {
            consumeSelectionEvent(new KeyEvent(event.getSource()), REPLACE);
        }

        @Override
        public void replaceHiLite(final KeyEvent event) {
            consumeSelectionEvent(event, REPLACE);
        }

        private void consumeSelectionEvent(final KeyEvent event, final SelectionEventEnt.ModeEnum mode) {
            notifyListeners(createSelectionEvent(mode, event.keys()));
        }

        private void notifyListeners(final SelectionEventEnt event) {
            m_eventListeners.forEach(l -> l.accept(event));
        }

        private SelectionEventEnt createSelectionEvent(final SelectionEventEnt.ModeEnum mode, final Set<RowKey> keys) {
            SelectionTranslationResult result = m_translateSelection.apply(keys);
            return createSelectionEvent(mode, result.selection, result.error);
        }

        private SelectionEventEnt createSelectionEvent(final SelectionEventEnt.ModeEnum mode,
            final List<String> selection, final String error) {
            return builder(SelectionEventEntBuilder.class).setMode(mode).setSelection(selection)
                .setProjectId(m_projectId).setWorkflowId(m_workflowId).setNodeId(m_nodeIdEnt).setPortIndex(m_portIndex)
                .setError(error).build();
        }

        private void removeHiLiteListenerFromHandler() {
            m_hiLiteHandler.removeHiLiteListener(this);
        }
    }

    /**
     * Represents the selection or an error if the selection couldn't be determined.
     */
    private static record SelectionTranslationResult(List<String> selection, String error) {
        //
    }

}
