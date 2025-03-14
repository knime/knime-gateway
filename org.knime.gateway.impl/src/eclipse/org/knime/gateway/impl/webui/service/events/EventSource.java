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

import java.util.Optional;
import java.util.function.BiConsumer;

import org.knime.core.node.util.CheckUtils;
import org.knime.gateway.impl.webui.service.DefaultServiceContext;

/**
 * An event source produces events that are forwarded to an event consumer.
 *
 * The implementations call {@link #sendEvent(Object)} to emit events.
 *
 * An event source is directly associated with a certain event-type and is able to register event listener
 * ({@link #addEventListenerFor(Object)} depending on the concrete event type instance.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz
 *
 * @param <T> the event type this event source is associated with
 * @param <E> the kind of event being emitted
 */
public abstract class EventSource<T, E> {

    private final EventConsumer m_eventConsumer;

    /**
     * Lock to make sure that the initial events issued by {@link #addEventListenerAndGetInitialEventFor(Object)} are
     * the first being forwarded to the event consumer (via {@link #sendEvent(Object)}) when
     * {@link #addEventListenerFor(Object)} is being called.
     */
    private final Object m_sendEventLock = new Object();

    /*
     * For testing purposes only.
     */
    private Runnable m_preEventCreationCallback;

    /**
     * @param eventConsumer consumes the emitted events
     */
    protected EventSource(final BiConsumer<String, Object> eventConsumer) {
        m_eventConsumer = eventConsumer::accept;
    }

    /**
     * @param eventConsumer consumes the emitted events
     */
    protected EventSource(final EventConsumer eventConsumer) {
        m_eventConsumer = eventConsumer;
    }

    /**
     * Registers a listener with the event source for the specified type of event and optionally returns the very first
     * event to 'catch up' (instead of passing it to the associated event consumer).
     *
     * @param eventTypeEnt
     * @param projectId id of the workflow project this event listener is associated with (i.e. the initial event, if
     *            there is one, will be associated wit that workflow project - see {@link #sendEvent(Object, String)};
     *            can be {@code null} (see also {@link DefaultServiceContext})
     *
     * @return the very first event or an empty optional if there isn't any (method must not wait for events to arrive -
     *         only returns if there is an event at event listener registration time)
     * @throws IllegalArgumentException if object describing the event type isn't valid
     */
    public abstract Optional<E> addEventListenerAndGetInitialEventFor(T eventTypeEnt, String projectId);

    /**
     * Little helper to assert that the project id equals the expected one (see also {@link DefaultServiceContext}).
     *
     * @param expectedProjectId
     * @param actualProjectId
     */
    protected static final void assertValidProjectId(final String expectedProjectId, final String actualProjectId) {
        CheckUtils.checkArgument(expectedProjectId == null || expectedProjectId.equals(actualProjectId),
            "Illegal project id");
    }

    /**
     * Registers a listener with the event source for the specified type of event.
     *
     * @param eventTypeEnt
     * @param projectId id of the workflow project this event listener is associated with (i.e. the initial event, if
     *            there is one, will be associated with that workflow project - see {@link #sendEvent(Object, String)};
     *            can be {@code null}
     *
     * @throws IllegalArgumentException if object describing the event type isn't valid
     */
    public void addEventListenerFor(final T eventTypeEnt, final String projectId) {
        // make sure the returned event is the first being send!
        synchronized (m_sendEventLock) {
            addEventListenerAndGetInitialEventFor(eventTypeEnt, projectId).ifPresent(e -> sendEvent(e, projectId));
        }
    }

    /**
     * Removes an event listener for a particular event type instance.
     *
     * @param eventTypeEnt
     * @param projectId the id of the workflow project this event listener is associated with; can be {@code null} if
     *            not associated with a particular project (see also {@link DefaultServiceContext})
     */
    public abstract void removeEventListener(T eventTypeEnt, String projectId);

    /**
     * Removes all event listeners registered with the event source. After this method, no events will be emitted
     * anymore.
     */
    public abstract void removeAllEventListeners();

    /**
     * Called by sub-classes to emit an event. This event is associated with a dedicated workflow project (specified by
     * the project id).
     *
     * @param event the event instance
     * @param projectId the id of the project the event listener is associated with; can be {@code null} - see
     *            {@link EventConsumer#accept(String, Object, String)}.
     */
    protected final void sendEvent(final E event, final String projectId) {
        synchronized (m_sendEventLock) {
            m_eventConsumer.accept(getName(), event, projectId);
        }
    }

    /**
     * @return a name unique to the event source
     */
    protected abstract String getName();

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
