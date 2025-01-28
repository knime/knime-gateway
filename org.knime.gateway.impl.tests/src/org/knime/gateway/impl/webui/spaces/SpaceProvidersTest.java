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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.Test;
import org.knime.core.node.workflow.contextv2.WorkflowContextV2;
import org.knime.gateway.api.webui.entity.SpaceProviderEnt.TypeEnum;

/**
 * Tests for {@link SpaceProviders}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class SpaceProvidersTest {

    /**
     * Tests the {@link SpaceProviders#update}-methods and the {@link SpaceProviders#getSpaceProvider}-method.
     */
    @Test
    public void testUpdateAndGet() {
        var spaceProvidersFactory = mock(SpaceProvidersFactory.class);
        var spaceProviders = new SpaceProviders(id -> {
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

        when(spaceProvidersFactory.createSpaceProvider(wfContext)).thenReturn(Optional.empty());
        spaceProviders.update(projectId, wfContext);
        assertThrows(NoSuchElementException.class, () -> spaceProviders.getSpaceProvider(projectId, "1"));

        when(spaceProvidersFactory.createSpaceProvider(wfContext)).thenReturn(Optional.of(spacerProvider1));
        spaceProviders.update(projectId, wfContext);
        assertThat(spaceProviders.getSpaceProvider(projectId, "1").getId(), is("1"));

        when(spaceProvidersFactory.createSpaceProvider(wfContext)).thenReturn(Optional.of(spacerProvider2));
        spaceProviders.update(projectId, wfContext);
        assertThat(spaceProviders.getSpaceProvider(projectId, "2").getId(), is("2"));

        spaceProviders.update(projectId, null);
        assertThrows(NoSuchElementException.class, () -> spaceProviders.getSpaceProvider(projectId, "1"));
        assertThrows(NoSuchElementException.class, () -> spaceProviders.getSpaceProvider(projectId, "2"));

        when(spaceProvidersFactory.createSpaceProviders()).thenReturn(List.of(spacerProvider1, spacerProvider2));
        spaceProviders.update();
        assertThat(spaceProviders.getSpaceProvider(projectId, "1").getId(), is("1"));
        assertThat(spaceProviders.getSpaceProvider(projectId, "2").getId(), is("2"));
        assertThat(spaceProviders.getSpaceProvider("unknown-project-id", "1").getId(), is("1"));

        when(spaceProvidersFactory.createSpaceProviders()).thenReturn(List.of(spacerProvider1));
        spaceProviders.update();
        assertThat(spaceProviders.getSpaceProvider(projectId, "1").getId(), is("1"));
        assertThrows(NoSuchElementException.class, () -> spaceProviders.getSpaceProvider(projectId, "2"));

    }

}
