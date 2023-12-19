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
 *
 * History
 *   Dec 14, 2023 (hornm): created
 */
package org.knime.gateway.impl.webui.entity;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import org.knime.core.node.workflow.NativeNodeContainer;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeStateChangeListener;
import org.knime.core.node.workflow.NodeStateEvent;
import org.knime.core.util.Pair;
import org.knime.core.webui.node.NodePortWrapper;
import org.knime.core.webui.node.NodeWrapper;
import org.knime.core.webui.node.port.PortViewManager;
import org.knime.core.webui.node.view.NodeViewManager;
import org.knime.gateway.api.entity.NodeViewEnt;
import org.knime.gateway.api.entity.PortViewEnt;
import org.knime.gateway.impl.webui.service.events.EventSource;
import org.knime.gateway.impl.webui.service.events.NodeViewStateEventSource;
import org.knime.gateway.impl.webui.service.events.SelectionEvent;
import org.knime.gateway.impl.webui.service.events.SelectionEventSource;

/**
 * Helper to create instances of {@link NodeViewEnt NodeViewEnts} and {@link PortViewEnt PortViewEnts}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public final class UIExtensionEntityFactory {

    private UIExtensionEntityFactory() {
        // utility class
    }

    /**
     * Creates a new {@link NodeViewEnt}-instance and initializes associated {@link EventSource EventSources}.
     *
     * Associated event sources are {@link SelectionEventSource} and {@link NodeViewStateEventSource}.
     *
     * @param nnc the node to create the node view entity from
     * @param eventConsumer the event consumer that will receive the events emitted by the initialized event sources
     * @param createNodeViewStateEventSource if {@code true} the {@link NodeViewStateEventSource} will be initialized,
     *            too; otherwise it won't
     * @return the new {@link NodeViewEnt}-instance and the initialized event source(s)
     */
    @SuppressWarnings({"rawtypes", "unused", "java:S2301"})
    public static Pair<NodeViewEnt, EventSource[]> createNodeViewEntAndEventSources(final NativeNodeContainer nnc,
        final BiConsumer<String, Object> eventConsumer, final boolean createNodeViewStateEventSource) {
        var selectionEventSource =
            new SelectionEventSource<>(eventConsumer, NodeViewManager.getInstance().getTableViewManager());
        Supplier<List<String>> initialSelectionSupplier =
            () -> selectionEventSource.addEventListenerAndGetInitialEventFor(NodeWrapper.of(nnc))
                .map(SelectionEvent::getSelection).orElse(Collections.emptyList());

        EventSource[] eventSources;
        if (createNodeViewStateEventSource) {
            var nodeViewStateEventSource = new NodeViewStateEventSource(eventConsumer,
                selectionEventSource::removeAllEventListeners, initialSelectionSupplier);
            nodeViewStateEventSource.addEventListenerAndGetInitialEventFor(nnc);
            eventSources = new EventSource[]{selectionEventSource, nodeViewStateEventSource};
        } else {
            new RemoveAllEventListenersOnNodeStateChange(nnc, selectionEventSource);
            eventSources = new EventSource[]{selectionEventSource};
        }

        return Pair.create(NodeViewEnt.create(nnc, initialSelectionSupplier), eventSources);
    }

    static class RemoveAllEventListenersOnNodeStateChange implements NodeStateChangeListener {

        private final NodeContainer m_nc;

        private SelectionEventSource<? extends NodeWrapper> m_eventSource;

        public RemoveAllEventListenersOnNodeStateChange(final NodeContainer nc,
            final SelectionEventSource<? extends NodeWrapper> eventSource) {
            m_eventSource = eventSource;
            m_nc = nc;
            nc.addNodeStateChangeListener(this); // NOSONAR
        }

        @Override
        public void stateChanged(final NodeStateEvent state) {
            m_eventSource.removeAllEventListeners();
            m_nc.removeNodeStateChangeListener(this);
        }

    }

    /**
     * Helper to create a port with and at the same time initialize the {@link SelectionEventSource} while also
     * determining the initial selection.
     *
     * The listeners for the {@link SelectionEventSource} on the node are removed on node state change.
     *
     * @param npw
     * @param manager
     * @param eventConsumer consumer of the {@link SelectionEventSource}-events
     * @return a new port view ent instance
     */
    public static PortViewEnt createPortViewEntAndInitSelectionEventSource(final NodePortWrapper npw,
        final PortViewManager manager, final BiConsumer<String, Object> eventConsumer) {
        var initialSelectionSupplier = createInitialSelectionSupplierAndInitSelectionEventSource(npw, eventConsumer);
        return new PortViewEnt(npw, manager, initialSelectionSupplier);
    }

    /**
     * Creates a new initial selection supplier and initializes associated {@link SelectionEventSource}.
     *
     * @param npw the port to create the selection event source from
     * @param eventConsumer the event consumer that will receive the events emitted by the initialized event source
     * @return the initial selection supplier
     */
    @SuppressWarnings("unused")
    private static Supplier<List<String>> createInitialSelectionSupplierAndInitSelectionEventSource(
        final NodePortWrapper npw, final BiConsumer<String, Object> eventConsumer) {
        var selectionEventSource =
            new SelectionEventSource<>(eventConsumer, PortViewManager.getInstance().getTableViewManager());
        Supplier<List<String>> initialSelectionSupplier =
            () -> selectionEventSource.addEventListenerAndGetInitialEventFor(npw).map(SelectionEvent::getSelection)
                .orElse(Collections.emptyList());

        new RemoveAllEventListenersOnNodeStateChange(npw.get(), selectionEventSource);

        return initialSelectionSupplier;
    }

}
