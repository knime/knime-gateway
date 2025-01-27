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
 *   Dec 15, 2022 (kai): created
 */
package org.knime.gateway.impl.webui.service.events;

import java.util.Optional;
import java.util.function.Consumer;

import org.knime.gateway.api.webui.entity.UpdateAvailableEventEnt;
import org.knime.gateway.api.webui.entity.UpdateAvailableEventTypeEnt;
import org.knime.gateway.api.webui.util.EntityFactory;
import org.knime.gateway.impl.webui.UpdateStateProvider;
import org.knime.gateway.impl.webui.UpdateStateProvider.UpdateState;

/**
 * Event source that emits event at the beginning of the application life cycle if there where available updates
 * discovered
 *
 * @author Kai Franze, KNIME GmbH
 */
public class UpdateAvailableEventSource
    extends EventSource<UpdateAvailableEventTypeEnt, UpdateAvailableEventEnt> {

    private final Consumer<UpdateState> m_callback;

    private final UpdateStateProvider m_updateStateProvider;

    /**
     * @param eventConsumer Consumes the emitted events
     * @param updateStateProvider Provides the update state
     */
    public UpdateAvailableEventSource(final EventConsumer eventConsumer,
        final UpdateStateProvider updateStateProvider) {
        super(eventConsumer);
        m_updateStateProvider = updateStateProvider;
        m_callback = updateState -> sendEvent(
            EntityFactory.UpdateState.buildEventEnt(updateState.getNewReleases(), updateState.getBugfixes()), null);
    }

    @Override
    public Optional<UpdateAvailableEventEnt>
        addEventListenerAndGetInitialEventFor(final UpdateAvailableEventTypeEnt eventTypeEnt, final String projectId) {
        m_updateStateProvider.addUpdateStateChangedListener(m_callback);
        return Optional.empty(); // Will be set before update check is triggered, so there will never be an event to emit
    }

    @Override
    public void removeEventListener(final UpdateAvailableEventTypeEnt eventTypeEnt, final String projectId) {
        m_updateStateProvider.removeUpdateStateChangedListener(m_callback);
    }

    @Override
    public void removeAllEventListeners() {
        removeEventListener(null, null);
    }

    @Override
    protected String getName() {
        return "UpdateAvailableEvent";
    }

}
