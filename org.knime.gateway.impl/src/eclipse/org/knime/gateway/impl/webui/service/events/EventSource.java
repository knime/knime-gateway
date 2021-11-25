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
 *   Nov 25, 2021 (hornm): created
 */
package org.knime.gateway.impl.webui.service.events;

import java.util.function.BiConsumer;

import org.knime.gateway.api.webui.entity.EventEnt;
import org.knime.gateway.api.webui.entity.EventTypeEnt;
import org.knime.gateway.api.webui.entity.WorkflowChangedEventTypeEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.InvalidRequestException;

/**
 * An event source produces events that are forwarded to an event consumer.
 *
 * The implementations call {@link #sendEvent(String, EventEnt)} to emit events.
 *
 * An event source is directly associated with a certain {@link EventTypeEnt} and is able to register event listener
 * ({@link #addEventListener(EventTypeEnt)} depending on the concrete event type instance. E.g. the
 * {@link WorkflowChangedEventSource} registers event listeners for multiple workflows, specified by the
 * {@link WorkflowChangedEventTypeEnt}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz
 *
 * @param <E> the event type this event source is associated with
 */
public abstract class EventSource<E extends EventTypeEnt> {

    private final BiConsumer<String, EventEnt> m_eventConsumer;

    /*
     * For testing purposess only.
     */
    private Runnable m_preEventCreationCallback = null;

    /**
     * @param eventConsumer the consumer for forward the emitted events to
     */
    protected EventSource(final BiConsumer<String, EventEnt> eventConsumer) {
        m_eventConsumer = eventConsumer;
    }

    /**
     * Registers a new event listener with the event source.
     * @param eventTypeEnt
     * @throws InvalidRequestException
     */
    public abstract void addEventListener(E eventTypeEnt) throws InvalidRequestException;

    /**
     * Removes an event listener for a particular event type instance.
     *
     * @param eventTypeEnt
     */
    public abstract void removeEventListener(E eventTypeEnt);

    /**
     * Removes all event listeners registered with the event source.
     */
    public abstract void removeAllEventListeners();

    /**
     * Called by sub-classes to emit an event.
     *
     * @param name a name unique to the event source
     * @param event the event instance
     */
    protected final void sendEvent(final String name, final EventEnt event) {
        m_eventConsumer.accept(name, event);
    }

    /**
     * For testing purposes only!
     *
     * @param callback
     */
    public final void setPreEventCreationCallback(final Runnable callback) {
        m_preEventCreationCallback = callback;
    }

    /**
     * For testing purposes only!
     */
    protected void preEventCreation() {
        if (m_preEventCreationCallback != null) {
            m_preEventCreationCallback.run();
        }
    }

}
