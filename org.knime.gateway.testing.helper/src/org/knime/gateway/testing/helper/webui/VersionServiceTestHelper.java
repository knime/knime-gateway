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
 *   Jan 16, 2025 (kai): created
 */
package org.knime.gateway.testing.helper.webui;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertThrows;
import static org.knime.gateway.api.entity.EntityBuilderManager.builder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

import org.knime.core.util.Version;
import org.knime.core.util.exception.ResourceAccessException;
import org.knime.gateway.api.webui.entity.SpaceGroupEnt;
import org.knime.gateway.api.webui.entity.SpaceItemVersionEnt;
import org.knime.gateway.api.webui.entity.SpaceItemVersionEnt.SpaceItemVersionEntBuilder;
import org.knime.gateway.api.webui.entity.SpaceProviderEnt;
import org.knime.gateway.api.webui.entity.SpaceProviderEnt.TypeEnum;
import org.knime.gateway.api.webui.service.VersionService;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.NetworkException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.ServiceCallException;
import org.knime.gateway.api.webui.util.EntityFactory;
import org.knime.gateway.impl.webui.service.ServiceDependencies;
import org.knime.gateway.impl.webui.spaces.Space;
import org.knime.gateway.impl.webui.spaces.SpaceGroup;
import org.knime.gateway.impl.webui.spaces.SpaceProvider;
import org.knime.gateway.impl.webui.spaces.SpaceProviders;
import org.knime.gateway.testing.helper.ResultChecker;
import org.knime.gateway.testing.helper.ServiceProvider;
import org.knime.gateway.testing.helper.WorkflowExecutor;
import org.knime.gateway.testing.helper.WorkflowLoader;

/**
 * Tests the {@link VersionService} methods.
 *
 * @author Kai Franze, KNIME GmbH, Germany
 */
@SuppressWarnings("javadoc")
public class VersionServiceTestHelper extends WebUIGatewayServiceTestHelper {

    private static final String SPACE_PROVIDER_ID = "some_provider_id";

    private static final String SPACE_ID = "some_space_id";

    private static final String ITEM_ID = "some_item_id";

    public VersionServiceTestHelper(final ResultChecker entityResultChecker, final ServiceProvider serviceProvider,
        final WorkflowLoader workflowLoader, final WorkflowExecutor workflowExecutor) {
        super(VersionServiceTestHelper.class, entityResultChecker, serviceProvider, workflowLoader, workflowExecutor);
    }

    /**
     * Tests the regular happy path
     */
    public void testListVersionsForItem() throws Exception {
        var mockedSpace = mockSpace(TypeEnum.HUB);
        var mockedResult = List.of(//
            createSpaceItemVersionEnt(1, "v1"), //
            createSpaceItemVersionEnt(2, "v2"));
        when(mockedSpace.listVersionsForItem(ITEM_ID, null)).thenReturn(mockedResult);

        var actualResult = vs().listVersionsForItem(SPACE_PROVIDER_ID, SPACE_ID, ITEM_ID, null);
        assertThat("Return value should be as expected", actualResult.equals(mockedResult));
        verify(mockedSpace, times(1)).listVersionsForItem(ITEM_ID, null);
    }

    /**
     * Tests the regular happy path with limit
     */
    public void testListVersionsForItemWithLimit() throws Exception {
        var mockedSpace = mockSpace(TypeEnum.HUB);
        var mockedResult = List.of(createSpaceItemVersionEnt(1, "v1"));
        when(mockedSpace.listVersionsForItem(ITEM_ID, 1)).thenReturn(mockedResult);

        var actualResult = vs().listVersionsForItem(SPACE_PROVIDER_ID, SPACE_ID, ITEM_ID, 1);
        assertThat("Return value should be as expected", actualResult.equals(mockedResult));
        verify(mockedSpace, times(1)).listVersionsForItem(ITEM_ID, 1);
    }

    /**
     * Tests with exception
     */
    public void testListVersionsForItemThrowingHandledException() throws Exception {
        var mockedSpace = mockSpace(TypeEnum.HUB);

        when(mockedSpace.listVersionsForItem(ITEM_ID, null)).thenThrow(new ResourceAccessException("Something went wrong"));

        assertThrows("A network exception should have been thrown", NetworkException.class,
            () -> vs().listVersionsForItem(SPACE_PROVIDER_ID, SPACE_ID, ITEM_ID, null));
        verify(mockedSpace, times(1)).listVersionsForItem(ITEM_ID, null);
    }

    /**
     * Tests with exception
     */
    public void testListVersionsForItemThrowingUnhandledException() throws Exception {
        var mockedSpace = mockSpace(TypeEnum.HUB);

        when(mockedSpace.listVersionsForItem(ITEM_ID, null)).thenThrow(new RuntimeException("Something went wrong"));

        assertThrows("A runtime exception should have been thrown", RuntimeException.class,
            () -> vs().listVersionsForItem(SPACE_PROVIDER_ID, SPACE_ID, ITEM_ID, null));
        verify(mockedSpace, times(1)).listVersionsForItem(ITEM_ID, null);
    }

    /**
     * Tests with exception
     */
    public void testListVersionsForItemWithIncorrectIds() throws Exception {
        var mockedSpace = mockSpace(TypeEnum.HUB);
        when(mockedSpace.listVersionsForItem(any(), any())).thenThrow(new ResourceAccessException("Item not found"));

        assertThrows("A network exception should have been thrown", NetworkException.class,
            () -> vs().listVersionsForItem(SPACE_PROVIDER_ID, SPACE_ID, "does not exist", null));
        verify(mockedSpace, times(1)).listVersionsForItem("does not exist", null);

        clearInvocations(mockedSpace);

        assertThrows("A service call exception should have been thrown", ServiceCallException.class,
            () -> vs().listVersionsForItem(SPACE_PROVIDER_ID, "does not exist", ITEM_ID, null));
        verify(mockedSpace, times(0)).listVersionsForItem(any(), any());
        assertThrows("A service call exception should have been thrown", ServiceCallException.class,
            () -> vs().listVersionsForItem("does not exist", SPACE_ID, ITEM_ID, null));
        verify(mockedSpace, times(0)).listVersionsForItem(any(), any());
    }

    private static SpaceItemVersionEnt createSpaceItemVersionEnt(final Integer version, final String title) {
        return builder(SpaceItemVersionEntBuilder.class)//
            .setVersion(version)//
            .setTitle(title)//
            .build();
    }

    // TODO: Move to helper class
    private static Space mockSpace(final TypeEnum spaceType) {
        var mockedSpace = mock(Space.class);
        when(mockedSpace.getId()).thenReturn(SPACE_ID);
        var spaceProvider = createSpaceProvider(SPACE_PROVIDER_ID, "Mocked Space Provider", spaceType, mockedSpace);
        var spaceProviders = Map.of(SPACE_PROVIDER_ID, spaceProvider);
        ServiceDependencies.setServiceDependency(SpaceProviders.class, () -> spaceProviders);
        return mockedSpace;
    }

    // TODO: Move to helper class
    private static SpaceProvider createSpaceProvider(final String spaceProviderId, final String spaceProviderName,
        final TypeEnum spaceProviderType, final Space... spaces) {
        var spaceGroup = getSpaceGroupForTesting(spaces);
        return new SpaceProvider() {

            @Override
            public void init(final Consumer<String> loginErrorHandler) {
                // do nothing
            }

            @Override
            public String getId() {
                return spaceProviderId;
            }

            @Override
            public String getName() {
                return spaceProviderName;
            }

            @Override
            public SpaceProviderEnt toEntity() {
                return EntityFactory.Space.buildSpaceProviderEnt(spaceProviderType, List.of(spaceGroup.toEntity()));
            }

            @Override
            public Space getSpace(final String spaceId) {
                return Arrays.stream(spaces).filter(s -> s.getId().equals(spaceId)).findFirst() //
                    .orElseThrow(() -> new NoSuchElementException("No space with ID " + spaceId + " found."));
            }

            @Override
            public Version getServerVersion() {
                return new Version(1, 2, 3);
            }

            @Override
            public SpaceGroup<?> getSpaceGroup(final String spaceGroupName) {
                return spaceGroup;
            }

        };
    }

    // TODO: Move to helper class
    private static SpaceGroup<Space> getSpaceGroupForTesting(final Space... spaces) {
        return new SpaceGroup<>() {

            static final String ID = "some_space_group_id";

            static final String NAME = "Testing Group";

            @Override
            public String getName() {
                return NAME;
            }

            @Override
            public SpaceGroupEnt toEntity() {
                return EntityFactory.Space.buildSpaceGroupEnt(ID, NAME, SpaceGroupEnt.TypeEnum.USER,
                    Arrays.stream(spaces).map(Space::toEntity).toList());
            }

            @Override
            public SpaceGroupType getType() {
                return SpaceGroupType.USER;
            }

            @Override
            public List<Space> getSpaces() {
                return List.of(spaces);
            }

            @Override
            public Space createSpace() {
                return null;
            }

        };
    }

}
