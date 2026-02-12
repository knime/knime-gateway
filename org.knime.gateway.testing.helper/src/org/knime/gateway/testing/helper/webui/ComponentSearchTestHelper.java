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
 *  ECLIPSE and the GNU General Public License for KNIME, provided the
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
 *   Jan 19, 2026 (assistant): extracted from SpaceServiceTestHelper
 */
package org.knime.gateway.testing.helper.webui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.knime.gateway.api.entity.EntityBuilderManager.builder;
import static org.knime.gateway.testing.helper.webui.SpaceProviderUtilities.createSpaceProvidersManager;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;

import org.knime.gateway.api.util.Side;
import org.knime.gateway.api.webui.entity.ComponentSearchItemEnt;
import org.knime.gateway.api.webui.entity.NativeNodeInvariantsEnt;
import org.knime.gateway.api.webui.entity.SpaceProviderEnt;
import org.knime.gateway.impl.webui.featureflags.FeatureFlags;
import org.knime.gateway.impl.webui.service.ServiceDependencies;
import org.knime.gateway.impl.webui.spaces.Space;
import org.knime.gateway.impl.webui.spaces.SpaceProvidersManager;
import org.knime.gateway.testing.helper.ResultChecker;
import org.knime.gateway.testing.helper.ServiceProvider;
import org.knime.gateway.testing.helper.WorkflowExecutor;
import org.knime.gateway.testing.helper.WorkflowLoader;

/**
 * Tests component search API behavior via the SpaceService implementation.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @author Kai Franze, KNIME GmbH
 * @author Benjamin Moser, KNIME GmbH
 */
@SuppressWarnings({"javadoc", "java:S112", "java:S1192", "java:S1188", "java:S1602", "java:S2259"})
public class ComponentSearchTestHelper extends WebUIGatewayServiceTestHelper {

    public ComponentSearchTestHelper(final ResultChecker entityResultChecker, final ServiceProvider serviceProvider,
        final WorkflowLoader workflowLoader, final WorkflowExecutor workflowExecutor) {
        super(ComponentSearchTestHelper.class, entityResultChecker, serviceProvider, workflowLoader, workflowExecutor);
    }

    private static Space mockSpace() {
        var mockedSpace = mock(Space.class);
        when(mockedSpace.getId()).thenReturn("some_space_id");
        return mockedSpace;
    }

    public void testSearchComponents() throws Exception {
        var space = mockSpace();
        var spaceProvider = spy(SpaceProviderUtilities.createSpaceProvider(SpaceProviderEnt.TypeEnum.HUB, space));
        ServiceDependencies.setServiceDependency(SpaceProvidersManager.class,
            createSpaceProvidersManager(spaceProvider));

        var queriedComponentType = NativeNodeInvariantsEnt.TypeEnum.LEARNER;

        var expectedEntities = List.of(builder(ComponentSearchItemEnt.ComponentSearchItemEntBuilder.class) //
            .setId("some_id") //
            .setName("some name") //
            .setType(ComponentSearchItemEnt.TypeEnum.LEARNER) //
            .setDescription("some description") //
            .setInPorts(List.of()) //
            .setOutPorts(List.of()) //
            .build());
        doReturn(expectedEntities) //
            .when(spaceProvider).searchComponents(anyString(), any(), any(), any(), any());

        var returnedEntities = ss().searchComponents(queriedComponentType.toString(), 0, 0, "input", "foo");

        verify(spaceProvider).searchComponents(eq(queriedComponentType.toString()), eq(Side.INPUT), eq("foo"), eq(0),
            eq(0));

        assertFalse(returnedEntities.isEmpty());
        assertEquals(returnedEntities.size(), expectedEntities.size());
        forEachZipped(returnedEntities, expectedEntities, (returnedEnt, expectedEnt) -> {
            expectedEnt.forEachPropertyValue(returnedEnt,
                (propName, values) -> assertEquals(values.getFirst(), values.getSecond()));
        });
    }

    private static <S, T> void forEachZipped(final List<S> left, final List<T> right, final BiConsumer<S, T> fn) {
        IntStream.range(0, Math.min(left.size(), right.size())).forEach(i -> fn.accept(left.get(i), right.get(i)));
    }

}
