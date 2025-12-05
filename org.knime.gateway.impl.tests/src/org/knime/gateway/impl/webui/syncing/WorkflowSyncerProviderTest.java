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
 *   Dec 5, 2025 (motacilla): created
 */
package org.knime.gateway.impl.webui.syncing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Test;
import org.knime.gateway.impl.webui.AppStateUpdater;
import org.knime.gateway.impl.webui.spaces.SpaceProvider;
import org.knime.gateway.impl.webui.spaces.SpaceProviders;
import org.knime.gateway.impl.webui.spaces.SpaceProvidersManager;
import org.knime.gateway.impl.webui.spaces.SpaceProvidersManager.Key;
import org.knime.gateway.impl.webui.syncing.WorkflowSyncer.DefaultWorkflowSyncer;
import org.knime.gateway.impl.webui.syncing.WorkflowSyncer.NoOpWorkflowSyncer;

/**
 * Test for the {@link WorkflowSyncerProvider}.
 *
 * @author Kai Franze, KNIME GmbH, Germany
 */
@SuppressWarnings("javadoc")
public class WorkflowSyncerProviderTest {

    @Test
    public void testDisabledProviderReturnsNoOpWorkflowSyncer() {
        // Given a disabled provider
        var provider = WorkflowSyncerProvider.disabled();
        var key = mock(Key.class);

        // When getting a workflow syncer
        var syncer = provider.getWorkflowSyncerForContext(key);

        // Then it should return a NoOpWorkflowSyncer
        assertThat(syncer).isInstanceOf(NoOpWorkflowSyncer.class);
    }

    @Test
    public void testDisabledProviderWithZeroSyncDelayReturnsNoOpWorkflowSyncer() {
        // Given a provider with zero sync delay (disabled)
        var appStateUpdater = mock(AppStateUpdater.class);
        var spaceProvidersManager = mock(SpaceProvidersManager.class);
        var provider = new WorkflowSyncerProvider(appStateUpdater, spaceProvidersManager, 0, 100);
        var key = mock(Key.class);

        // When getting a workflow syncer
        var syncer = provider.getWorkflowSyncerForContext(key);

        // Then it should return a NoOpWorkflowSyncer
        assertThat(syncer).isInstanceOf(NoOpWorkflowSyncer.class);
    }

    @Test
    public void testGetWorkflowSyncerForContextReturnsSameInstance() {
        // Given a disabled provider (for simplicity)
        var provider = WorkflowSyncerProvider.disabled();
        var key = mock(Key.class);

        // When getting the workflow syncer twice for the same key
        var syncer1 = provider.getWorkflowSyncerForContext(key);
        var syncer2 = provider.getWorkflowSyncerForContext(key);

        // Then it should return the same instance (cached)
        assertThat(syncer1).isSameAs(syncer2);
    }

    @Test
    public void testGetWorkflowSyncerForContextReturnsDifferentInstancesForDifferentKeys() {
        // Given a disabled provider (for simplicity)
        var provider = WorkflowSyncerProvider.disabled();
        var key1 = mock(Key.class);
        var key2 = mock(Key.class);

        // When getting workflow syncers for different keys
        var syncer1 = provider.getWorkflowSyncerForContext(key1);
        var syncer2 = provider.getWorkflowSyncerForContext(key2);

        // Then it should return different instances
        assertThat(syncer1).isNotSameAs(syncer2);
    }

    @Test
    public void testEnabledProviderReturnsDefaultWorkflowSyncer() {
        // Given an enabled provider with all required dependencies
        var appStateUpdater = mock(AppStateUpdater.class);
        var spaceProvidersManager = mock(SpaceProvidersManager.class);
        var spaceProviders = mock(SpaceProviders.class);
        var spaceProvider = mock(SpaceProvider.class);
        var key = mock(Key.class);

        // Mock the space providers manager to return a space provider
        when(spaceProvidersManager.getSpaceProviders(key)).thenReturn(spaceProviders);
        when(spaceProviders.getAllSpaceProviders()).thenReturn(List.of(spaceProvider));

        var provider = new WorkflowSyncerProvider(appStateUpdater, spaceProvidersManager, 5, 100);

        // When getting a workflow syncer
        var syncer = provider.getWorkflowSyncerForContext(key);

        // Then it should return a DefaultWorkflowSyncer
        assertThat(syncer).isInstanceOf(DefaultWorkflowSyncer.class);
    }
}
