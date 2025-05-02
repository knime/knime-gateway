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
 *   Jan 28, 2025 (hornm): created
 */
package org.knime.gateway.impl.webui.spaces;

import static org.assertj.core.api.Assertions.assertThatList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;

import org.junit.Test;
import org.knime.core.node.workflow.contextv2.WorkflowContextV2;
import org.knime.gateway.api.webui.entity.SpaceProviderEnt.TypeEnum;
import org.knime.gateway.impl.webui.spaces.SpaceProvidersManager.Key;

/**
 * Tests for {@link SpaceProvidersManager}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class SpaceProvidersManagerTest {

    /**
     * Tests the {@link SpaceProviders#update}-methods and the {@link SpaceProviders#getSpaceProvider}-method.
     */
    @Test
    public void testUpdateAndGet() {
        var spaceProvidersFactory = mock(SpaceProviderFactory.class);
        var spaceProvidersManager = new SpaceProvidersManager(id -> {
        }, null, List.of(spaceProvidersFactory));
        var spacerProvider1 = mock(SpaceProvider.class);
        when(spacerProvider1.getId()).thenReturn("1");
        when(spacerProvider1.getName()).thenReturn("name 1");
        when(spacerProvider1.getType()).thenReturn(TypeEnum.HUB);

        var spacerProvider2 = mock(SpaceProvider.class);
        when(spacerProvider2.getId()).thenReturn("2");
        when(spacerProvider2.getName()).thenReturn("name 2");
        when(spacerProvider2.getType()).thenReturn(TypeEnum.HUB);

        var wfContext = WorkflowContextV2.forTemporaryWorkflow(Path.of("/"), Path.of("/"));
        var projectId = "project-id";

        // only available factory returns empty optional -> obtained SpaceProviders instance does not contain a SpaceProvider instance
        when(spaceProvidersFactory.createSpaceProvider(wfContext)).thenReturn(Optional.empty());
        var spaceProviders = spaceProvidersManager.update(Key.of(projectId), wfContext);
        assertThat(spaceProviders.getAllSpaceProviders().isEmpty(), is(true));

        // only available factory returns SpaceProvider instance -> instance is stored and can be retrieved via correct ID
        when(spaceProvidersFactory.createSpaceProvider(wfContext)).thenReturn(Optional.of(spacerProvider1));
        spaceProvidersManager.update(Key.of(projectId), wfContext);
        assertThat(spaceProvidersManager.getSpaceProviders(Key.of(projectId)).getSpaceProvider("1").getId(), is("1"));
        assertThrows(NoSuchElementException.class,
            () -> spaceProvidersManager.getSpaceProviders(Key.of(projectId)).getSpaceProvider("2"));

        // `update` replaces the SpaceProviders instance
        when(spaceProvidersFactory.createSpaceProvider(wfContext)).thenReturn(Optional.of(spacerProvider2));
        spaceProvidersManager.update(Key.of(projectId), wfContext);
        assertThat(spaceProvidersManager.getSpaceProviders(Key.of(projectId)).getSpaceProvider("2").getId(), is("2"));

        spaceProvidersManager.remove(Key.of(projectId));
        assertThrows(NoSuchElementException.class, () -> spaceProvidersManager.getSpaceProviders(Key.of(projectId)));

        // several SpaceProvider instances are handled property
        when(spaceProvidersFactory.createSpaceProviders()).thenReturn(List.of(spacerProvider1, spacerProvider2));
        spaceProvidersManager.update();
        assertThat(spaceProvidersManager.getSpaceProviders(Key.of(projectId)).getSpaceProvider("1").getId(), is("1"));
        assertThat(spaceProvidersManager.getSpaceProviders(Key.of(projectId)).getSpaceProvider("2").getId(), is("2"));
        // misses fall back to default key
        assertThat(spaceProvidersManager.getSpaceProviders(Key.of("unknown-project-id")).getSpaceProvider("1").getId(),
            is("1"));

        when(spaceProvidersFactory.createSpaceProviders()).thenReturn(List.of(spacerProvider1));
        var spaceProvidersBeforeUpdate = spaceProvidersManager.getSpaceProviders(Key.defaultKey());
        spaceProvidersManager.update();
        assertThat(spaceProvidersManager.getSpaceProviders(Key.of(projectId)).getSpaceProvider("1").getId(), is("1"));
        // makes sure that the 'default' space providers instance is not replaced
        // - otherwise the classes referencing it would need to be updated, too
        assertThat(spaceProvidersManager.getSpaceProviders(Key.defaultKey()), sameInstance(spaceProvidersBeforeUpdate));
        assertThrows(NoSuchElementException.class,
            () -> spaceProvidersManager.getSpaceProviders(Key.of(projectId)).getSpaceProvider("2"));
    }

    /**
     * Assert that the callbacks are called as expected when updating the space providers.
     */
    @Test
    public void testCallbacksWhenUpdating() {
        var spacerProvider1 = mock(SpaceProvider.class);
        when(spacerProvider1.getId()).thenReturn("1");
        when(spacerProvider1.getType()).thenReturn(TypeEnum.HUB);

        var spacerProvider2 = mock(SpaceProvider.class);
        when(spacerProvider2.getId()).thenReturn("2");
        when(spacerProvider2.getType()).thenReturn(TypeEnum.SERVER);

        var spaceProvidersFactory = mock(SpaceProviderFactory.class);
        when(spaceProvidersFactory.createSpaceProviders()).thenReturn(List.of(spacerProvider1, spacerProvider2));

        var onCreateCounter = new ArrayList<TypeEnum>();
        Consumer<SpaceProvider> onProviderCreated = provider -> onCreateCounter.add(provider.getType());
        var onRemoveCounter = new ArrayList<TypeEnum>();
        Consumer<SpaceProvider> onProviderRemoved = provider -> onRemoveCounter.add(provider.getType());
        var spaceProvidersManager = new SpaceProvidersManager(id -> {}, onProviderCreated, onProviderRemoved, null,
            List.of(spaceProvidersFactory));

        spaceProvidersManager.update();
        assertThatList(onRemoveCounter).isEmpty(); // Nothing to remove on first 'update()' call.
        assertThatList(onCreateCounter).containsExactly(TypeEnum.HUB, TypeEnum.SERVER).hasSize(2);

        onCreateCounter.clear();
        onRemoveCounter.clear();

        spaceProvidersManager.update();
        assertThatList(onRemoveCounter).containsExactly(TypeEnum.HUB, TypeEnum.SERVER).hasSize(2);
        assertThatList(onCreateCounter).containsExactly(TypeEnum.HUB, TypeEnum.SERVER).hasSize(2);
    }

    /**
     * Assert that the callbacks are called as expected when updating the space providers.
     */
    @Test
    public void testCallbacksWhenUpdatingWithinContext() {
        var spacerProvider1 = mock(SpaceProvider.class);
        when(spacerProvider1.getId()).thenReturn("1");
        when(spacerProvider1.getType()).thenReturn(TypeEnum.HUB);

        var wfContext = WorkflowContextV2.forTemporaryWorkflow(Path.of("/"), Path.of("/"));
        var projectId = "project-id";

        var spaceProvidersFactory = mock(SpaceProviderFactory.class);
        when(spaceProvidersFactory.createSpaceProvider(wfContext)).thenReturn(Optional.of(spacerProvider1));

        var onCreateCounter = new ArrayList<TypeEnum>();
        Consumer<SpaceProvider> onProviderCreated = provider -> onCreateCounter.add(provider.getType());
        var onRemoveCounter = new ArrayList<TypeEnum>();
        Consumer<SpaceProvider> onProviderRemoved = provider -> onRemoveCounter.add(provider.getType());
        var spaceProvidersManager = new SpaceProvidersManager(id -> {}, onProviderCreated, onProviderRemoved, null,
            List.of(spaceProvidersFactory));

        spaceProvidersManager.update(Key.of(projectId), wfContext);
        assertThatList(onRemoveCounter).isEmpty(); // Nothing to remove on first 'update()' call.
        assertThatList(onCreateCounter).containsExactly(TypeEnum.HUB).hasSize(1);

        onCreateCounter.clear();
        onRemoveCounter.clear();

        spaceProvidersManager.remove(Key.of(projectId));
        assertThatList(onRemoveCounter).containsExactly(TypeEnum.HUB).hasSize(1);

        spaceProvidersManager.update(Key.of(projectId), wfContext);
        assertThatList(onCreateCounter).containsExactly(TypeEnum.HUB).hasSize(1);
    }

}
