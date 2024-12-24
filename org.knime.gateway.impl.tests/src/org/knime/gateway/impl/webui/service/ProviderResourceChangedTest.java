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
package org.knime.gateway.impl.webui.service;

import static org.knime.gateway.api.entity.EntityBuilderManager.builder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.knime.core.util.Pair;
import org.knime.gateway.api.webui.entity.ProviderResourceChangedEventEnt;
import org.knime.gateway.api.webui.entity.ProviderResourceChangedEventTypeEnt;
import org.knime.gateway.impl.webui.service.events.EventConsumer;
import org.knime.gateway.impl.webui.spaces.SpaceProvider;
import org.knime.gateway.impl.webui.spaces.SpaceProviders;
import org.knime.gateway.impl.webui.spaces.local.LocalSpaceProvider;
import org.mockito.Mockito;

@SuppressWarnings({"javadoc", "java:S1186", "java:S112"})
public class ProviderResourceChangedTest extends GatewayServiceTest {

    private static final String PROVIDER = "provider";

    private final EventConsumer m_consumer = mock(EventConsumer.class);

    private final DummyNotifier m_notifier = new DummyNotifier();

    static final class DummyNotifier implements SpaceProvider.ProviderResourceChangedNotifier {

        private final Map<Pair<String, String>, Runnable> m_listeners = new HashMap<>();

        @Override
        public void subscribeToItem(String space, String item, Runnable callback) {
            m_listeners.put(new Pair<>(space, item), callback);
        }

        @Override
        public void unsubscribe(String spaceId, String itemId) {
            m_listeners.remove(new Pair<>(spaceId, itemId));
        }

        @Override
        public void unsubscribeAll() {
            m_listeners.clear();
        }

        /**
         * Called from tests to simulate a change happening.
         */
        public void notifyEventListeners() {
            m_listeners.values().forEach(Runnable::run);
        }
    }

    @Before
    public void setUp() {
        var providers = ServiceDependencies.getServiceDependency(SpaceProviders.class, true);
        var provider = mock(LocalSpaceProvider.class);
        when(provider.getChangeNotifier()).thenReturn(Optional.of(m_notifier));
        when(providers.getSpaceProvider(PROVIDER)).thenReturn(provider);
    }

    @Test
    public void testHubResourceChangedEventListener() throws Exception {
        // No listener, no event
        m_notifier.notifyEventListeners();
        verify(m_consumer, times(0)).accept(any(), any());

        var eventService = DefaultEventService.getInstance();
        // Note: The 'HubResourceChangedEventTypeEnt' has 'getTypeID()' and 'getTypeId()'
        var eventType1 = buildEventTypeEnt(PROVIDER, "spaceId1", "itemId1");
        eventService.addEventListener(eventType1);

        // One listener, one event
        m_notifier.notifyEventListeners();
        var expectedEvent1 = buildEventEnt(eventType1);
        verify(m_consumer, times(1)).accept("ProviderResourceChangedEvent", expectedEvent1);

        var eventType2 = buildEventTypeEnt(PROVIDER, "spaceId2", "itemId2");
        eventService.addEventListener(eventType2);
        Mockito.clearInvocations(m_consumer);

        // Two listeners, two events
        m_notifier.notifyEventListeners();
        verify(m_consumer, times(2)).accept(eq("ProviderResourceChangedEvent"), any());

        eventService.removeEventListener(eventType1);
        eventService.removeEventListener(eventType2);
        Mockito.clearInvocations(m_consumer);

        // No listener, no event
        m_notifier.notifyEventListeners();
        verify(m_consumer, times(0)).accept(any(), any());
    }

    private static ProviderResourceChangedEventTypeEnt buildEventTypeEnt(final String providerId, final String spaceId,
        final String itemId) {
        return builder(ProviderResourceChangedEventTypeEnt.ProviderResourceChangedEventTypeEntBuilder.class) //
            .setProviderId(providerId) //
            .setSpaceId(spaceId) //
            .setItemId(itemId) //
            .build();
    }

    private static ProviderResourceChangedEventEnt
        buildEventEnt(final ProviderResourceChangedEventTypeEnt eventTypeEnt) {
        return builder(ProviderResourceChangedEventEnt.ProviderResourceChangedEventEntBuilder.class) //
            .setProviderId(eventTypeEnt.getProviderId()) //
            .setSpaceId(eventTypeEnt.getSpaceId()) //
            .setItemId(eventTypeEnt.getItemId()) //
            .build();
    }

}
