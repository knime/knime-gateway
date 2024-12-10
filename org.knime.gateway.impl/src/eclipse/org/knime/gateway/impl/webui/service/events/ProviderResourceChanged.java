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
 *   Dec 10, 2024 (kai): created
 */
package org.knime.gateway.impl.webui.service.events;

import static org.knime.gateway.api.entity.EntityBuilderManager.builder;

import java.util.Optional;

import org.knime.gateway.api.webui.entity.ProviderResourceChangedEventEnt;
import org.knime.gateway.api.webui.entity.ProviderResourceChangedEventTypeEnt;
import org.knime.gateway.impl.service.util.CallThrottle;
import org.knime.gateway.impl.webui.spaces.SpaceProvider.ProviderResourceChangedNotifier;
import org.knime.gateway.impl.webui.spaces.SpaceProviders;

/**
 * ...
 *
 * @author Kai Franze, KNIME GmbH, Germany
 */
public class ProviderResourceChanged
    extends EventSource<ProviderResourceChangedEventTypeEnt, ProviderResourceChangedEventEnt> {

    private final SpaceProviders m_spaceProviders;

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Optional<ProviderResourceChangedNotifier> m_changeNotifier;

    /**
     * ...
     *
     * @param eventConsumer ...
     */
    public ProviderResourceChanged(final EventConsumer eventConsumer, final SpaceProviders spaceProviders) {
        super(eventConsumer);
        m_spaceProviders = spaceProviders;
    }

    @Override
    @SuppressWarnings("java:S1602")
    public Optional<ProviderResourceChangedEventEnt>
        addEventListenerAndGetInitialEventFor(final ProviderResourceChangedEventTypeEnt eventTypeEnt) {
        m_changeNotifier = m_spaceProviders.getProvidersMap().get(eventTypeEnt.getProviderId()).getChangeNotifier();
        if (m_changeNotifier.isEmpty()) {
            return Optional.empty();
        }

        var throttle = new CallThrottle(onSubscriptionNotification(eventTypeEnt), this.getName() + " call throttle");

        m_changeNotifier.ifPresent(notifier -> notifier.subscribeToItem(eventTypeEnt.getSpaceId(),
            eventTypeEnt.getItemId(), throttle::invoke));
        return Optional.empty();
    }

    private Runnable onSubscriptionNotification(ProviderResourceChangedEventTypeEnt eventTypeEnt) {
        return () -> this.sendEvent(
            // provide information on what has changed in the event s.t. the frontend can decide whether it
            // is still interested in it
            buildEvent(eventTypeEnt) //
        );
    }

    @Override
    public void removeEventListener(final ProviderResourceChangedEventTypeEnt eventTypeEnt) {
        m_changeNotifier.ifPresent(notifier -> notifier.unsubscribe(eventTypeEnt.getSpaceId(), eventTypeEnt.getItemId()));
    }

    @Override
    public void removeAllEventListeners() {
        m_changeNotifier.ifPresent(ProviderResourceChangedNotifier::unsubscribeAll);
    }

    @Override
    protected String getName() {
        return "ProviderResourceChangedEvent";
    }

    private static ProviderResourceChangedEventEnt buildEvent(final ProviderResourceChangedEventTypeEnt eventTypeEnt) {
        return builder(ProviderResourceChangedEventEnt.ProviderResourceChangedEventEntBuilder.class) //
            .setProviderId(eventTypeEnt.getProviderId()) //
            .setSpaceId(eventTypeEnt.getSpaceId()) //
            .setItemId(eventTypeEnt.getItemId()) //
            .build();
    }

}
