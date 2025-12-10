package org.knime.gateway.testing.helper.webui;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.knime.core.util.Version;
import org.knime.gateway.api.webui.entity.SpaceEnt;
import org.knime.gateway.api.webui.entity.SpaceGroupEnt;
import org.knime.gateway.api.webui.entity.SpaceProviderEnt;
import org.knime.gateway.api.webui.service.util.MutableServiceCallException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions;
import org.knime.gateway.api.webui.util.EntityFactory;
import org.knime.gateway.impl.webui.service.ServiceDependencies;
import org.knime.gateway.impl.webui.spaces.Space;
import org.knime.gateway.impl.webui.spaces.SpaceGroup;
import org.knime.gateway.impl.webui.spaces.SpaceProvider;
import org.knime.gateway.impl.webui.spaces.SpaceProviderFactory;
import org.knime.gateway.impl.webui.spaces.SpaceProviders;
import org.knime.gateway.impl.webui.spaces.SpaceProvidersManager;
import org.knime.gateway.impl.webui.spaces.local.LocalSpace;

public final class SpaceProviderUtilities {
    private SpaceProviderUtilities() {
    }

    static SpaceProvider createSpaceProvider(final SpaceProviderEnt.TypeEnum type, final Space... spaces) {
        return createSpaceProvider("provider-id", "provider-name", type, spaces);
    }

    static SpaceProvider createSpaceProvider( //
                                              final String id, //
                                              final String spaceProviderName, //
                                              final SpaceProviderEnt.TypeEnum type, //
                                              final Space... spaces //
    ) {
        return new SpaceProvider() {

            @Override
            public void init(final Consumer<String> loginErrorHandler) {
                // do nothing
            }

            @Override
            public String getId() {
                return id;
            }

            @Override
            public String getName() {
                return spaceProviderName;
            }

            @Override
            public List<SpaceGroupEnt> toEntity()
                throws ServiceExceptions.NetworkException, ServiceExceptions.LoggedOutException, MutableServiceCallException {
                return List.of(getLocalSpaceGroupForTesting(spaces).toEntity());
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
            public SpaceProviderEnt.TypeEnum getType() {
                return type;
            }

            @Override
            public SpaceGroup<?> getSpaceGroup(final String spaceGroupName) {
                return getLocalSpaceGroupForTesting(spaces);
            }
        };
    }

    static Space mockSpace(final String id, final String name, final String owner, final String description,
                           final boolean isPrivate) throws Exception {
        var space = mock(Space.class);
        when(space.getId()).thenReturn(id);
        when(space.getName()).thenReturn(name);
        when(space.toEntity()).thenReturn(EntityFactory.Space.buildSpaceEnt(id, name, owner, description, isPrivate));
        // not mocked methods will return `null` or an appropriate empty/primitive value
        return space;
    }

    static SpaceGroup<Space> getLocalSpaceGroupForTesting(final Space... spaces) {
        return new SpaceGroup<>() {

            static final String ID = "Local-Testing-space-id";

            static final String NAME = "Local Testing Group";

            @Override
            public String getName() {
                return NAME;
            }

            @Override
            public SpaceGroupEnt toEntity() throws ServiceExceptions.NetworkException, ServiceExceptions.LoggedOutException, MutableServiceCallException {
                final List<SpaceEnt> spaceEnts = new ArrayList<>();
                for (final var space : spaces) {
                    spaceEnts.add(space.toEntity());
                }
                return EntityFactory.Space.buildSpaceGroupEnt(ID, NAME, SpaceGroupEnt.TypeEnum.USER, spaceEnts);
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
                try {
                    return mockSpace("*newId", "New space", "testUser", "", true);
                } catch (Exception ex) {
                    throw ExceptionUtils.asRuntimeException(ex);
                }
            }

        };
    }

    /**
     * Helper to create a {@link SpaceProviders}-instance for testing.
     *
     * @param spaceProviders
     * @return
     */
    public static SpaceProvidersManager createSpaceProvidersManager(final SpaceProvider... spaceProviders) {
        var spaceProvidersFactory = mock(SpaceProviderFactory.class);
        var providers = List.of(spaceProviders);
        when(spaceProvidersFactory.createSpaceProviders()).thenReturn(providers);
        var res = new SpaceProvidersManager(id -> {
        }, null, List.of(spaceProvidersFactory));
        res.update();
        return res;
    }

    static SpaceProvider createSpaceProvider(final Space... spaces) {
        return createSpaceProvider("provider-id", "provider-name", spaces);
    }

    /**
     * Create a trivial space provider implementation that can be used to set up tests.
     *
     * @param id
     * @param spaceProviderName
     * @param spaces
     * @param isReachable
     * @return
     */
    static SpaceProvider createSpaceProvider(final String id, final String spaceProviderName, final Space... spaces) {
        return createSpaceProvider(id, spaceProviderName, SpaceProviderEnt.TypeEnum.LOCAL, spaces);
    }

    static SpaceProvider createLocalSpaceProviderForTesting(final Path testWorkspacePath) {
        var localWorkspace = new LocalSpace(testWorkspacePath);
        return new SpaceProvider() {

            @Override
            public void init(final Consumer<String> loginErrorHandler) {
                // do nothing
            }

            @Override
            public String getId() {
                return "local-testing";
            }

            @Override
            public List<SpaceGroupEnt> toEntity()
                throws ServiceExceptions.NetworkException, ServiceExceptions.LoggedOutException, MutableServiceCallException {
                return List.of(getLocalSpaceGroupForTesting(localWorkspace).toEntity());
            }

            @Override
            public String getName() {
                return "local-testing-name";
            }

            @Override
            public Space getSpace(final String spaceId) {
                return Optional.of(localWorkspace).filter(space -> space.getId().equals(spaceId)).orElseThrow();
            }

            @Override
            public Version getServerVersion() {
                return new Version(1, 2, 3);
            }

            @Override
            public SpaceGroup<?> getSpaceGroup(final String spaceGroupName) {
                return getLocalSpaceGroupForTesting(localWorkspace);
            }
        };
    }

    static String registerLocalSpaceProviderForTesting(final Path testWorkspacePath) {
        var spaceProvider = createLocalSpaceProviderForTesting(testWorkspacePath);
        ServiceDependencies.setServiceDependency(SpaceProvidersManager.class,
            createSpaceProvidersManager(spaceProvider));
        return spaceProvider.getId();
    }
}
