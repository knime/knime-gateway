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
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.knime.core.node.workflow.NativeNodeContainer;
import org.knime.core.util.Pair;
import org.knime.core.webui.node.NodePortWrapper;
import org.knime.core.webui.node.NodeWrapper;
import org.knime.core.webui.node.port.PortViewManager;
import org.knime.core.webui.node.util.NodeCleanUpCallback;
import org.knime.core.webui.node.view.NodeViewManager;
import org.knime.core.webui.node.view.table.TableViewManager;
import org.knime.gateway.api.entity.NodeViewEnt;
import org.knime.gateway.api.entity.PortViewEnt;
import org.knime.gateway.api.webui.entity.SelectionEventEnt;
import org.knime.gateway.impl.webui.service.events.NodeViewStateEvent;
import org.knime.gateway.impl.webui.service.events.NodeViewStateEventSource;
import org.knime.gateway.impl.webui.service.events.SelectionEventBus;

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
     * Creates a new {@link NodeViewEnt}-instance and registers the passed event consumer to receive respective events
     * and the node to emit respective events.
     *
     * Events that can be received/emitted are {@link SelectionEventEnt} and {@link NodeViewStateEvent}.
     *
     * @param nnc the node to create the node view entity from
     * @param eventConsumer the event consumer that will receive the events
     * @param setupNodeViewStateEvents if {@code true} the {@link NodeViewStateEvent}s will be send via the event
     *            consumer, too; otherwise it won't
     * @param selectionEventBus
     * @return the new {@link NodeViewEnt}-instance and the logic to clean-up stuff
     */
    @SuppressWarnings({"rawtypes", "unused", "java:S2301"})
    public static Pair<NodeViewEnt, AutoCloseable> createNodeViewEntAndSetupEvents(final NativeNodeContainer nnc,
        final BiConsumer<String, Object> eventConsumer, final boolean setupNodeViewStateEvents,
        final SelectionEventBus selectionEventBus) {
        var initialSelectionSupplierAndSelectionEventCleanUp = createInitialSelectionSupplierAndSetupEvents(
            NodeWrapper.of(nnc), NodeViewManager.getInstance().getTableViewManager(), eventConsumer, selectionEventBus);

        Runnable nodeViewStateEventCleanUp = null;
        if (setupNodeViewStateEvents) {
            var nodeViewStateEventSource = new NodeViewStateEventSource(eventConsumer,
                initialSelectionSupplierAndSelectionEventCleanUp.getFirst());
            nodeViewStateEventSource.addEventListenerAndGetInitialEventFor(nnc);
            nodeViewStateEventCleanUp = nodeViewStateEventSource::removeAllEventListeners;
        }

        final var nodeViewStateEventCleanUpFinal = nodeViewStateEventCleanUp;
        return Pair.create(NodeViewEnt.create(nnc, initialSelectionSupplierAndSelectionEventCleanUp.getFirst()), () -> {
            initialSelectionSupplierAndSelectionEventCleanUp.getSecond().run();
            if (nodeViewStateEventCleanUpFinal != null) {
                nodeViewStateEventCleanUpFinal.run();
            }
        });
    }

    /**
     * Helper to create a port with and at the same time initialize the passed event consumer to receive
     * {@link SelectionEventEnt}s while also determining the initial selection.
     *
     * The listeners for the {@link SelectionEventEnt}s on the node are removed on node state change.
     *
     * @param npw
     * @param manager
     * @param eventConsumer consumer of the {@link SelectionEventEnt}s
     * @param selectionEventBus
     * @return a new port view ent instance
     */
    public static Pair<PortViewEnt, AutoCloseable> createPortViewEntAndSetupEvents(final NodePortWrapper npw,
        final PortViewManager manager, final BiConsumer<String, Object> eventConsumer,
        final SelectionEventBus selectionEventBus) {
        var initialSelectionSupplierAndSelectionEventCleanUp = createInitialSelectionSupplierAndSetupEvents(npw,
            PortViewManager.getInstance().getTableViewManager(), eventConsumer, selectionEventBus);
        return Pair.create(new PortViewEnt(npw, manager, initialSelectionSupplierAndSelectionEventCleanUp.getFirst()),
            initialSelectionSupplierAndSelectionEventCleanUp.getSecond()::run);
    }

    /**
     * Creates a new initial selection supplier and initializes associated {@link SelectionEventBus}.
     *
     * @param npw the port to create the selection event source from
     * @param eventConsumer the event consumer that will receive the events emitted by the initialized event source
     * @return the initial selection supplier and the logic to clean-up stuff
     */
    @SuppressWarnings("unused")
    private static <N extends NodeWrapper> Pair<Supplier<List<String>>, Runnable>
        createInitialSelectionSupplierAndSetupEvents(final N nw, final TableViewManager<N> tableViewManager,
            final BiConsumer<String, Object> eventConsumer, final SelectionEventBus selectionEventBus) {

        Consumer<SelectionEventEnt> selectionEventConsumer = e -> eventConsumer.accept("SelectionEvent", e);
        selectionEventBus.addSelectionEventListener(selectionEventConsumer);
        Supplier<List<String>> initialSelectionSupplier =
            () -> selectionEventBus.addSelectionEventEmitterAndGetInitialEvent(nw, tableViewManager)
                .map(SelectionEventEnt::getSelection).orElse(Collections.emptyList());

        Runnable selectionEventCleanUp = () -> {
            selectionEventBus.removeSelectionEventEmitter(nw);
            selectionEventBus.removeSelectionEventListener(selectionEventConsumer);
        };
        NodeCleanUpCallback.builder(nw.get(), selectionEventCleanUp).cleanUpOnNodeStateChange(true).build();

        return Pair.create(initialSelectionSupplier, selectionEventCleanUp);
    }

}
