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
 *   Dec 21, 2022 (kai): created
 */
package org.knime.gateway.impl.webui.service;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.awaitility.Awaitility;
import org.junit.Test;
import org.knime.core.eclipseUtil.UpdateChecker.UpdateInfo;
import org.knime.gateway.api.webui.entity.UpdateAvailableEventEnt;
import org.knime.gateway.api.webui.util.EntityFactory;
import org.knime.gateway.impl.service.events.EventConsumer;
import org.knime.gateway.impl.webui.UpdateStateProvider;
import org.knime.gateway.impl.webui.UpdateStateProvider.UpdateState;
import org.knime.gateway.impl.webui.service.events.UpdateAvailableEventSource;

/**
 * Tests that the expected update state change events are issued by the event service.
 *
 * @author Kai Franze, KNIME GmbH
 */
public class UpdateAvailableEventsTest extends GatewayServiceTest {

    private final EventConsumer m_testConsumer = mock(EventConsumer.class);

    /**
     * {@inheritDoc}
     */
    @Override
    protected EventConsumer createEventConsumer() {
        return m_testConsumer;
    }

    /**
     * Tests event emitted by {@link UpdateAvailableEventSource}.
     *
     * @throws Exception
     */
    @Test
    public void testUpdateAvailableEventEmittedWithBothTypesOfUpdates() throws Exception {
        List<UpdateInfo> newReleases = List.of(new UpdateInfo(null, "KNIME Analytics Platform 5.0", "5.0", true));
        List<String> bugfixes = List.of("First bugfix", "Yet another bugfix");
        var supplier = createUpdateStateSupplier(newReleases, bugfixes);

        // Expect one event since there is at least one update
        assertUpdateAvailableEvents(supplier, 1);
    }

    /**
     * Tests event emitted by {@link UpdateAvailableEventSource}.
     *
     * @throws Exception
     */
    @Test
    public void testUpdateAvailableEventEmittedWithOnlyOneTypeOfUpdate() throws Exception {
        List<UpdateInfo> newReleases = List.of(new UpdateInfo(null, "KNIME Analytics Platform 5.0", "5.0", true));
        List<String> bugfixes = Collections.emptyList();
        var supplier = createUpdateStateSupplier(newReleases, bugfixes);

        // Expect one event since there is at least one update
        assertUpdateAvailableEvents(supplier, 1);
    }

    /**
     * Tests event not emitted by {@link UpdateAvailableEventSource}.
     *
     * @throws Exception
     */
    @Test
    public void testUpdateAvailableEventNotEmittedWithoutUpdates() throws Exception {
        List<UpdateInfo> newReleases = Collections.emptyList();
        List<String> bugfixes = Collections.emptyList();
        var supplier = createUpdateStateSupplier(newReleases, bugfixes);

        // Expect no event since there is no update
        assertUpdateAvailableEvents(supplier, 0);
    }

    /**
     * Tests event content emitted by {@link UpdateAvailableEventSource}.
     *
     * @throws Exception
     */
    @Test
    public void testUpdateAvailableEventContent() throws Exception {
        List<UpdateInfo> newReleases = List.of(new UpdateInfo(null, "KNIME Analytics Platform 5.0", "5.0", true));
        List<String> bugfixes = List.of("First bugfix", "Yet another bugfix");
        var supplier = createUpdateStateSupplier(newReleases, bugfixes);

        addEventListenerAndcheckForUpdates(supplier);

        Awaitility.await().atMost(500, TimeUnit.MILLISECONDS).untilAsserted(() -> {
            verify(m_testConsumer, times(1)).accept(eq("UpdateAvailableEvent"), argThat(e -> {
                // Compare names of first update info must do
                var newReleasesMatch = ((UpdateAvailableEventEnt)e).getNewReleases().get(0).getName()//
                    .equals(newReleases.get(0).getName());
                // Compare list of strings
                var bugFixesMatch = ((UpdateAvailableEventEnt)e).getBugfixes().equals(bugfixes);
                return newReleasesMatch && bugFixesMatch;
            }));
        });
    }

    private static Supplier<UpdateState> createUpdateStateSupplier(final List<UpdateInfo> newReleases,
        final List<String> bugfixes) {
        return () -> new UpdateState() {
            @Override
            public List<UpdateInfo> getNewReleases() {
                return newReleases;
            }

            @Override
            public List<String> getBugfixes() {
                return bugfixes;
            }
        };
    }

    private static void addEventListenerAndcheckForUpdates(final Supplier<UpdateState> supplier) throws Exception {
        var updateStateProvider = new UpdateStateProvider(supplier);
        ServiceDependencies.setServiceDependency(UpdateStateProvider.class, updateStateProvider);
        DefaultEventService.getInstance().addEventListener(EntityFactory.UpdateState.buildEventTypeEnt());
        updateStateProvider.checkForUpdates();
    }

    private void assertUpdateAvailableEvents(final Supplier<UpdateState> supplier, final int numberOfEvents)
        throws Exception {
        addEventListenerAndcheckForUpdates(supplier);
        Awaitility.await().atMost(500, TimeUnit.MILLISECONDS).untilAsserted(() -> {
            verify(m_testConsumer, times(numberOfEvents)).accept(eq("UpdateAvailableEvent"),
                isA(UpdateAvailableEventEnt.class));
        });
    }

}
