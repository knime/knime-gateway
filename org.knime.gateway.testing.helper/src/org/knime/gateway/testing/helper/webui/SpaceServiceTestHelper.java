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
 *   Dec 9, 2022 (hornm): created
 */
package org.knime.gateway.testing.helper.webui;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.hamcrest.MatcherAssert;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.node.workflow.contextv2.LocationInfo;
import org.knime.core.util.FileUtil;
import org.knime.core.util.Pair;
import org.knime.core.util.PathUtils;
import org.knime.core.util.Version;
import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.api.webui.entity.SpaceEnt;
import org.knime.gateway.api.webui.entity.SpaceItemEnt;
import org.knime.gateway.api.webui.entity.SpaceItemReferenceEnt.ProjectTypeEnum;
import org.knime.gateway.api.webui.entity.SpaceProviderEnt;
import org.knime.gateway.api.webui.entity.SpaceProviderEnt.TypeEnum;
import org.knime.gateway.api.webui.entity.WorkflowGroupContentEnt;
import org.knime.gateway.api.webui.service.SpaceService;
import org.knime.gateway.api.webui.service.util.ServiceExceptions;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.InvalidRequestException;
import org.knime.gateway.api.webui.util.EntityFactory;
import org.knime.gateway.impl.project.Project;
import org.knime.gateway.impl.project.Project.Origin;
import org.knime.gateway.impl.project.ProjectManager;
import org.knime.gateway.impl.webui.service.ServiceDependencies;
import org.knime.gateway.impl.webui.spaces.Space;
import org.knime.gateway.impl.webui.spaces.SpaceProvider;
import org.knime.gateway.impl.webui.spaces.SpaceProviders;
import org.knime.gateway.impl.webui.spaces.local.LocalWorkspace;
import org.knime.gateway.testing.helper.ResultChecker;
import org.knime.gateway.testing.helper.ServiceProvider;
import org.knime.gateway.testing.helper.WorkflowExecutor;
import org.knime.gateway.testing.helper.WorkflowLoader;
import org.knime.testing.util.WorkflowManagerUtil;

/**
 * Tests {@link SpaceService}-implementations.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @author Kai Franze, KNIME GmbH
 * @author Benjamin Moser, KNIME GmbH
 */
@SuppressWarnings("javadoc")
public class SpaceServiceTestHelper extends WebUIGatewayServiceTestHelper {

    public SpaceServiceTestHelper(final ResultChecker entityResultChecker, final ServiceProvider serviceProvider,
        final WorkflowLoader workflowLoader, final WorkflowExecutor workflowExecutor) {
        super(SpaceServiceTestHelper.class, entityResultChecker, serviceProvider, workflowLoader, workflowExecutor);
    }

    /**
     * Test the interfacing between space service, space providers and spaces w.r.t renaming.
     */
    public void testRenameSpaceItem() throws Exception {
        var spaceId = "some_space_id";
        var providerId = "some_provider_id";
        var itemId = "some_item_id";
        var newName = "some_new_name";

        var mockedSpace = mock(Space.class);
        when(mockedSpace.getId()).thenReturn(spaceId);
        var newSpaceItemEnt =
            EntityFactory.Space.buildSpaceItemEnt(newName, itemId, SpaceItemEnt.TypeEnum.WORKFLOW);
        when(mockedSpace.renameItem(itemId, newName)).thenReturn(newSpaceItemEnt);

        var spaceProvider = createSpaceProvider(providerId, "mocked_provider_name", mockedSpace);
        var spaceProviders = Map.of(providerId, spaceProvider);
        ServiceDependencies.setServiceDependency(SpaceProviders.class, () -> spaceProviders);

        // trigger operation under test
        var renamedItemEnt = ss().renameItem(providerId, spaceId, itemId, newName);
        MatcherAssert.assertThat("Rename return value describes renamed item",
            renamedItemEnt.getId().equals(newSpaceItemEnt.getId())
                && renamedItemEnt.getType() == newSpaceItemEnt.getType() && renamedItemEnt.getName().equals(newName));

        // verify that rename method of individual space was called
        verify(mockedSpace).renameItem(itemId, newName);
    }

    private static Pair<SpaceProvider, Space> createTempLocalSpaceProvider(final String directoryNamePrefix,
        final String workspaceName)
        throws IOException {
        var tempPath = PathUtils.createTempDir(directoryNamePrefix);
        var spaceProvider = createLocalSpaceProviderForTesting(tempPath);
        var space = spaceProvider.getSpace(LocalWorkspace.LOCAL_WORKSPACE_ID);
        PathUtils.copyDirectory(getTestWorkspacePath(workspaceName), tempPath);
        return new Pair<>(spaceProvider, space);
    }

    public void testRenameSpaceItemLocal() throws Exception {
        var p = createTempLocalSpaceProvider("testRenameSpaceItemLocal", "test_workspace_to_list");
        ServiceDependencies.setServiceDependency(SpaceProviders.class,
            () -> Map.of(p.getFirst().getId(), p.getFirst()));
        var providerId = p.getFirst().getId();
        var space = p.getSecond();

        // find some arbitrary item to rename
        var group = space.listWorkflowGroup(Space.ROOT_ITEM_ID);
        var originalItemEnt = group.getItems().stream() //
            .filter(itemEnt -> itemEnt.getType() == SpaceItemEnt.TypeEnum.WORKFLOW) //
            .findAny().orElseThrow();
        var itemPathBeforeRename = space.toLocalAbsolutePath(null, originalItemEnt.getId()).orElse(null);

        // perform rename
        var newName = "newItemName";
        var renamedItemEnt = ss().renameItem(providerId, space.getId(), originalItemEnt.getId(), newName);

        // ID remains the same, name changes
        MatcherAssert.assertThat("Rename return value describes renamed item",
            renamedItemEnt.getId().equals(originalItemEnt.getId()) //
                && renamedItemEnt.getType() == originalItemEnt.getType() //
                && renamedItemEnt.getName().equals(newName));

        // verify that id map was updated: ID should remain the same, associated path should have changed
        var itemPathAfterRename = space.toLocalAbsolutePath(null, originalItemEnt.getId());
        assertNotEquals("Item path has been updated", itemPathBeforeRename, itemPathAfterRename);

        // verify that file exists at new path
        MatcherAssert.assertThat("File exists at new name",
            itemPathBeforeRename.resolveSibling(newName).toFile().exists());
    }

    public void testRenameToExistingLocal() throws Exception {
        var p = createTempLocalSpaceProvider("testRenameSpaceItemLocal", "test_workspace_to_list");
        ServiceDependencies.setServiceDependency(SpaceProviders.class,
            () -> Map.of(p.getFirst().getId(), p.getFirst()));
        var providerId = p.getFirst().getId();
        var space = p.getSecond();

        var group1 = getItemByName("Group1", space);
        var group2 = getItemByName("Group2", space);
        var newName = group2.getName();

        // note that we explicitly do not allow overwriting items of same type
        // see legacy GlobalRenameAction
        assertThrows(ServiceExceptions.OperationNotAllowedException.class, () -> {
            ss().renameItem(providerId, space.getId(), group1.getId(), newName);
        });
    }

    public void testRenameRootLocal() throws Exception {
        var p = createTempLocalSpaceProvider("testRenameSpaceItemLocal", "test_workspace_to_list");
        ServiceDependencies.setServiceDependency(SpaceProviders.class,
                () -> Map.of(p.getFirst().getId(), p.getFirst()));
        var providerId = p.getFirst().getId();
        var space = p.getSecond();
        assertThrows(ServiceExceptions.OperationNotAllowedException.class, () -> {
            ss().renameItem(providerId, space.getId(), Space.ROOT_ITEM_ID, "newName");
        });
    }

    /**
     * Obtain with given name in root level of space
     *
     * @param name The filename of the item to locate
     * @return an entity describing the item, or an empty optional if it could not be found
     */
    private static SpaceItemEnt getItemByName(final String name, final Space space) throws IOException {
        var group = space.listWorkflowGroup(Space.ROOT_ITEM_ID);
        return group.getItems().stream().filter(item -> item.getName().equals(name)).findAny()
                .orElseThrow(() -> new IllegalArgumentException("Item expected to be present in workspace"));
    }

    private static String findItemId(final WorkflowGroupContentEnt groupContent, final String name) {
        return groupContent.getItems().stream() //
            .filter(e -> name.equals(e.getName())) //
            .map(SpaceItemEnt::getId) //
            .findFirst() //
            .orElseThrow(() -> new IllegalArgumentException("Item expected to be present in workspace"));
    }
    /**
     * Tests {@link SpaceService#listWorkflowGroup(String, String, String)} for the local workspace.
     *
     * @throws Exception
     */
    public void testListWorkflowGroupForLocalWorkspace() throws Exception {
        var testWorkspacePath = getTestWorkspacePath("test_workspace_to_list");
        var providerId = registerLocalSpaceProviderForTesting(testWorkspacePath);
        var spaceId = LocalWorkspace.LOCAL_WORKSPACE_ID;
        var root = ss().listWorkflowGroup(spaceId, providerId, Space.ROOT_ITEM_ID);
        cr(root, "workspace_to_list_root");

        var group1Id = findItemId(root, "Group1");
        var group1 = ss().listWorkflowGroup(spaceId, providerId, group1Id);
        cr(group1, "workspace_to_list_group1");

        var group11Id = findItemId(group1, "Group11");
        var group11 = ss().listWorkflowGroup(spaceId, providerId, group11Id);
        cr(group11, "workspace_to_list_group11");

        var emptyGroupId = findItemId(root, "EmptyGroup");
        var emptyGroup = ss().listWorkflowGroup(spaceId, providerId, emptyGroupId);
        cr(emptyGroup, "workspace_to_list_empty_group");

        var dataTxtId = findItemId(root, "data.txt");
        assertThrows(InvalidRequestException.class, () -> ss().listWorkflowGroup(spaceId, providerId, dataTxtId));

        assertThrows(InvalidRequestException.class,
            () -> ss().listWorkflowGroup(null, "non-existing-provider-id", "blub"));

        assertThrows(InvalidRequestException.class,
            () -> ss().listWorkflowGroup("non-existing-space-id", providerId, "blub"));
    }

    /**
     * Tests {@link SpaceService#getSpaceProvider(String)}.
     *
     * @throws InvalidRequestException
     */
    public void testGetSpaceProvider() throws InvalidRequestException {
        var spaces = new Space[5];
        for (var i = 0; i < 4; i++) {
            spaces[i] = createSpace("id" + i, "name" + i, "owner" + i, "description" + i, i % 2 == 0);
        }
        var provider1 = createSpaceProvider("id1", "name1", spaces[0], spaces[1]);
        var provider2 = createSpaceProvider("id2", "name2", spaces[2], spaces[3]);

        ServiceDependencies.setServiceDependency(SpaceProviders.class,
            () -> Map.of(provider1.getId(), provider1, provider2.getId(), provider2));

        var spaceProvider = ss().getSpaceProvider("id1");
        cr(spaceProvider, "space_provider1");

        spaceProvider = ss().getSpaceProvider("id2");
        cr(spaceProvider, "space_provider2");

        assertThrows(InvalidRequestException.class, () -> ss().getSpaceProvider("non_existing_id"));
    }

    private static SpaceProvider createSpaceProvider(final String id, final String spaceProviderName,
            final Space... spaces) {
        return createSpaceProvider(TypeEnum.LOCAL, id, spaceProviderName, spaces);
    }

    private static SpaceProvider createSpaceProvider(final SpaceProviderEnt.TypeEnum type,
            final String id, final String spaceProviderName, final Space... spaces) {
        return new SpaceProvider() {

            @Override
            public String getId() {
                return id;
            }

            @Override
            public String getName() {
                return spaceProviderName;
            }

            @Override
            public SpaceProviderEnt toEntity() {
                return EntityFactory.Space.buildSpaceProviderEnt(type,
                    Arrays.stream(spaces).map(Space::toEntity).collect(Collectors.toList()));
            }

            @Override
            public Space getSpace(final String spaceId) {
                return Arrays.stream(spaces).filter(s -> s.getId().equals(spaceId)).findFirst() //
                        .orElseThrow(() -> new NoSuchElementException("No space with ID " + spaceId + " found."));
            }

            @Override
            public Version getServerVersion() {
                return new Version(1,2,3);
            }
        };
    }

    private static Space createSpace(final String id, final String name, final String owner, final String description,
        final boolean isPrivate) {
        final var spaceEntity = EntityFactory.Space.buildSpaceEnt(id, name, owner, description, isPrivate);
        return new Space() { // NOSONAR

            @Override
            public String getId() {
                return id;
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public SpaceEnt toEntity() {
                return spaceEntity;
            }

            @Override
            public WorkflowGroupContentEnt listWorkflowGroup(final String workflowGroupItemId) throws IOException {
                return null;
            }

            @Override
            public SpaceItemEnt renameItem(final String itemId, final String newName) throws IOException {
                return null;
            }

            @Override
            public Optional<Path> toLocalAbsolutePath(final ExecutionMonitor monitor, final String itemId) {
                return Optional.empty();
            }

            @Override
            public Path getLocalRootPath() {
                return null;
            }

            @Override
            public LocationInfo getLocationInfo(final String itemId) {
                return null;
            }

            @Override
            public SpaceItemEnt createWorkflow(final String workflowGroupItemId, final String name) throws IOException {
                return null;
            }

            @Override
            public SpaceItemEnt createWorkflowGroup(final String workflowGroupItemId) throws IOException {
                return null;
            }

            @Override
            public URI toKnimeUrl(final String itemId) {
                return null;
            }

            @Override
            public void deleteItems(final List<String> itemIds) throws IOException {
                // do nothing
            }

            @Override
            public void moveOrCopyItems(final List<String> itemIds, final String destWorkflowGroupItemId,
                    final Space.NameCollisionHandling collisionHandling, final boolean copy) throws IOException {
                // do nothing
            }

            @Override
            public SpaceItemEnt importFile(final Path srcPath, final String workflowGroupItemId,
                    final Space.NameCollisionHandling collisionHandling, final IProgressMonitor progress)
                    throws IOException {
                return null;
            }

            @Override
            public SpaceItemEnt importWorkflowOrWorkflowGroup(final Path srcPath, final String workflowGroupItemId,
                    final Consumer<Path> createMetaInfoFileFor, final Space.NameCollisionHandling collisionHandling,
                    final IProgressMonitor progress) throws IOException {
                return null;
            }

            @Override
            public List<String> getAncestorItemIds(final String itemId) {
                return List.of();
            }

            @Override
            public String getItemName(final String itemId) {
                return null;
            }

            @Override
            public boolean containsItemWithName(final String workflowGroupItemId, final String itemName) {
                return false;
            }

            @Override
            public Optional<String> getItemIdByURI(final URI uri) {
                return Optional.empty();
            }

            @Override
            public Optional<ProjectTypeEnum> getProjectType(final String itemId) {
                return Optional.empty();
            }
        };
    }

    private static SpaceProvider createLocalSpaceProviderForTesting(final Path testWorkspacePath) {
        var localWorkspace = new LocalWorkspace(testWorkspacePath);
        return new SpaceProvider() {
            @Override
            public String getId() {
                return "local-testing";
            }

            @Override
            public SpaceProviderEnt toEntity() {
                return EntityFactory.Space.buildSpaceProviderEnt(null, List.of(localWorkspace.toEntity()));
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
                return new Version(1,2,3);
            }
        };
    }

    private static String registerLocalSpaceProviderForTesting(final Path testWorkspacePath) {
        var spaceProvider = createLocalSpaceProviderForTesting(testWorkspacePath);
        var providerId = spaceProvider.getId();
        var spaceProviders = Map.of(providerId, spaceProvider);
        ServiceDependencies.setServiceDependency(SpaceProviders.class, () -> spaceProviders);
        return providerId;
    }

    private static Path getTestWorkspacePath(final String name) throws IOException {
        return CoreUtil.resolveToFile("/files/" + name, SpaceServiceTestHelper.class).toPath();
    }

    /**
     * Tests {@link SpaceService#createWorkflow(String, String, String)} for the local workspace.
     *
     * @throws Exception
     */
    public void testCreateWorkflowForLocalWorkspace() throws Exception {
        var testWorkspacePath = getTestWorkspacePath("test_workspace_to_create");
        var providerId = registerLocalSpaceProviderForTesting(testWorkspacePath);

        try {
            // Create workflows and check
            var spaceId = LocalWorkspace.LOCAL_WORKSPACE_ID;
            var wf0 = ss().createWorkflow(spaceId, providerId, Space.ROOT_ITEM_ID, Space.DEFAULT_WORKFLOW_NAME);
            cr(wf0, "created_workflow_0");
            var level0 = ss().listWorkflowGroup(spaceId, providerId, Space.ROOT_ITEM_ID);
            cr(level0, "workspace_to_create_level0");

            var level1Id = findItemId(level0, "level1");
            var wf1 = ss().createWorkflow(spaceId, providerId, level1Id, Space.DEFAULT_WORKFLOW_NAME);
            cr(wf1, "created_workflow_1");
            var level1 = ss().listWorkflowGroup(spaceId, providerId, level1Id);
            cr(level1, "workspace_to_create_level1");

            var level2Id = findItemId(level1, "level2");
            var wf2 = ss().createWorkflow(spaceId, providerId, level2Id, Space.DEFAULT_WORKFLOW_NAME);
            cr(wf2, "created_workflow_2");
            var level2 = ss().listWorkflowGroup(spaceId, providerId, level2Id);
            cr(level2, "workspace_to_create_level2");
        } finally {
            // Make sure cleanup is always performed
            var pathWf0 = testWorkspacePath//
                .resolve(Space.DEFAULT_WORKFLOW_NAME);
            var pathWf1 = testWorkspacePath//
                .resolve("level1")//
                .resolve(Space.DEFAULT_WORKFLOW_NAME);
            var pathWf2 = testWorkspacePath//
                .resolve("level1")//
                .resolve("level2")//
                .resolve(Space.DEFAULT_WORKFLOW_NAME);
            for (var path : List.of(pathWf0, pathWf1, pathWf2)) {
                FileUtils.deleteQuietly(path.toFile()); // To delete directory recursively
            }
        }
    }

    /**
     * Tests {@link SpaceService#deleteItems(String, String, List)} for the local workspace.
     *
     * @throws Exception
     */
    public void testDeleteItemForLocalWorkspace() throws Exception {
        var testWorkspacePath = FileUtil.createTempDir("delete").toPath();
        var providerId = registerLocalSpaceProviderForTesting(testWorkspacePath);
        var spaceId = LocalWorkspace.LOCAL_WORKSPACE_ID;
        var workflowGroupName = "workflow_group";
        var fileName = "testfile.txt";

        // Create a workflow and delete it
        var wf = ss().createWorkflow(spaceId, providerId, Space.ROOT_ITEM_ID, Space.DEFAULT_WORKFLOW_NAME);
        ss().deleteItems(spaceId, providerId, List.of(wf.getId()));
        assertThat("Workflow must not exist anymore",
            !Files.exists(testWorkspacePath.resolve(Space.DEFAULT_WORKFLOW_NAME)));

        // Create a workflow group with some content and delete it
        var workflowGroupPath = testWorkspacePath.resolve(workflowGroupName);
        Files.createDirectory(workflowGroupPath);
        var workflowGroupId =
            findItemId(ss().listWorkflowGroup(spaceId, providerId, Space.ROOT_ITEM_ID), workflowGroupName);
        ss().createWorkflow(spaceId, providerId, workflowGroupId, Space.DEFAULT_WORKFLOW_NAME);
        ss().createWorkflow(spaceId, providerId, workflowGroupId, Space.DEFAULT_WORKFLOW_NAME);
        Files.createFile(workflowGroupPath.resolve(fileName));
        ss().deleteItems(spaceId, providerId, List.of(workflowGroupId));
        assertThat("Workflow group must not exist anymore", !Files.exists(workflowGroupPath));

        // Delete a single data file
        var filePath = testWorkspacePath.resolve(fileName);
        Files.write(filePath, new byte[]{127, 0, 0, -1, 10});
        var fileId = findItemId(ss().listWorkflowGroup(spaceId, providerId, Space.ROOT_ITEM_ID), fileName);
        ss().deleteItems(spaceId, providerId, List.of(fileId));
        assertThat("File must not exist anymore", !Files.exists(filePath));

        // Call delete on the root
        assertThrows("Deleting root must fail", InvalidRequestException.class,
            () -> ss().deleteItems(spaceId, providerId, List.of("root")));

        // Call delete with an item id that does not exist
        assertThrows("Deleting unknown item must fail", InvalidRequestException.class,
            () -> ss().deleteItems(spaceId, providerId, List.of("0")));

        // Call delete on a workflow that was deleted by another application
        wf = ss().createWorkflow(spaceId, providerId, Space.ROOT_ITEM_ID, Space.DEFAULT_WORKFLOW_NAME);
        PathUtils.deleteDirectoryIfExists(testWorkspacePath.resolve(Space.DEFAULT_WORKFLOW_NAME));
        ss().deleteItems(spaceId, providerId, List.of(wf.getId()));

        // Delete multiple items at once
        ss().createWorkflow(spaceId, providerId, Space.ROOT_ITEM_ID, Space.DEFAULT_WORKFLOW_NAME);
        filePath = testWorkspacePath.resolve(fileName);
        Files.write(filePath, new byte[]{127, 0, 0, -1, 10});
        workflowGroupPath = testWorkspacePath.resolve(workflowGroupName);
        Files.createDirectory(workflowGroupPath);
        workflowGroupId =
            findItemId(ss().listWorkflowGroup(spaceId, providerId, Space.ROOT_ITEM_ID), workflowGroupName);
        ss().createWorkflow(spaceId, providerId, workflowGroupId, Space.DEFAULT_WORKFLOW_NAME);
        ss().createWorkflow(spaceId, providerId, workflowGroupId, Space.DEFAULT_WORKFLOW_NAME);
        Files.createFile(workflowGroupPath.resolve(fileName));
        ss().listWorkflowGroup(spaceId, providerId, workflowGroupId);
        var rootFiles = ss().listWorkflowGroup(spaceId, providerId, Space.ROOT_ITEM_ID);
        ss().deleteItems(spaceId, providerId,
            rootFiles.getItems().stream().map(SpaceItemEnt::getId).collect(Collectors.toList()));
    }

    /**
     * Tests {@link SpaceService#createWorkflowGroup(String, String, String)} for the local workspace.
     *
     * @throws Exception
     */
    public void testCreateWorkflowGroupForLocalWorkspace() throws Exception {
        var testWorkspacePath = getTestWorkspacePath("test_workspace_to_create_group");
        var providerId = registerLocalSpaceProviderForTesting(testWorkspacePath);

        try {
            // Create workflow groups and check
            var spaceId = LocalWorkspace.LOCAL_WORKSPACE_ID;
            var wfg0 = ss().createWorkflowGroup(spaceId, providerId, Space.ROOT_ITEM_ID);
            cr(wfg0, "created_workflow_group_0");
            var level0 = ss().listWorkflowGroup(spaceId, providerId, Space.ROOT_ITEM_ID);
            cr(level0, "workspace_to_create_group_level0");
            var wfg0Path = testWorkspacePath.resolve("Folder");
            assertThat("Folder must be created", Files.isDirectory(wfg0Path));

            var level1Id = wfg0.getId();
            var wfg1 = ss().createWorkflowGroup(spaceId, providerId, level1Id);
            cr(wfg1, "created_workflow_group_1");
            var level1 = ss().listWorkflowGroup(spaceId, providerId, level1Id);
            cr(level1, "workspace_to_create_group_level1_0");
            assertThat("Folder/Folder must be created", Files.isDirectory(wfg0Path.resolve("Folder")));

            var wfg2 = ss().createWorkflowGroup(spaceId, providerId, level1Id);
            cr(wfg2, "created_workflow_group_2");
            var level2 = ss().listWorkflowGroup(spaceId, providerId, level1Id);
            cr(level2, "workspace_to_create_group_level1_1");
            assertThat("Folder/Folder(1) must be created with", Files.isDirectory(wfg0Path.resolve("Folder(1)")));
        } finally {
            // Make sure cleanup is always performed
            var pathWfg0 = testWorkspacePath//
                .resolve(Space.DEFAULT_WORKFLOW_GROUP_NAME);
            PathUtils.deleteDirectoryIfExists(pathWfg0);
        }
    }

    /**
     * Tests {@link SpaceService#moveItems(String, String, List, String)} for the local workspace.
     *
     * @throws Exception
     */
    public void testMoveItemsLocal() throws Exception {
        var testWorkspacePath = FileUtil.createTempDir("move").toPath();
        var providerId = registerLocalSpaceProviderForTesting(testWorkspacePath);
        var spaceId = LocalWorkspace.LOCAL_WORKSPACE_ID;
        var fileName = "testfile.txt";
        var level1 = "level1";
        var level1Path = testWorkspacePath.resolve(level1);
        var level2 = "level2";
        var level2Path = level1Path.resolve(level2);

        try {
            // Create a workflow, a workflow group and a file
            var wfName = Space.DEFAULT_WORKFLOW_NAME;
            var wf = ss().createWorkflow(spaceId, providerId, Space.ROOT_ITEM_ID, Space.DEFAULT_WORKFLOW_NAME);
            Files.createDirectory(level1Path);
            var level1Id = findItemId(ss().listWorkflowGroup(spaceId, providerId, Space.ROOT_ITEM_ID), level1);
            var filePathLevel0 = testWorkspacePath.resolve(fileName);
            Files.createFile(filePathLevel0);
            var fileId = findItemId(ss().listWorkflowGroup(spaceId, providerId, Space.ROOT_ITEM_ID), fileName);
            var wfPathLevel0 = testWorkspacePath.resolve(wfName);

            // Move workflow into level1
            ss().moveOrCopyItems(spaceId, providerId, List.of(wf.getId()), level1Id,
                Space.NameCollisionHandling.NOOP.toString(), false);
            assertThat("The newly created workflow didn't move out of <root>", Files.notExists(wfPathLevel0));
            var wfPathLevel1 = level1Path.resolve(wfName);
            assertThat("The newly created workflow didn't move to <level1>", Files.exists(wfPathLevel1));

            // Move file into level1
            ss().moveOrCopyItems(spaceId, providerId, List.of(fileId), level1Id,
                Space.NameCollisionHandling.NOOP.toString(), false);
            assertThat("The newly created file didn't move out of <root>", Files.notExists(filePathLevel0));
            var filePathLevel1 = level1Path.resolve(fileName);
            assertThat("The newly created file didn't move to <level1>", Files.exists(filePathLevel1));

            // Move workflow and file into level2
            Files.createDirectory(level2Path);
            var level2Id = findItemId(ss().listWorkflowGroup(spaceId, providerId, level1Id), level2);
            var itemsToMove = List.of(//
                findItemId(ss().listWorkflowGroup(spaceId, providerId, level1Id), Space.DEFAULT_WORKFLOW_NAME), //
                findItemId(ss().listWorkflowGroup(spaceId, providerId, level1Id), fileName)//
            );
            ss().moveOrCopyItems(spaceId, providerId, itemsToMove, level2Id,
                Space.NameCollisionHandling.NOOP.toString(), false);
            var wfPathLevel2 = level2Path.resolve(wfName);
            assertThat("The workflow didn't move to <level2>", Files.exists(wfPathLevel2));
            var filePathLevel2 = level2Path.resolve(fileName);
            assertThat("The file didn't move to <level2>", Files.exists(filePathLevel2));

            // duplicate file in <level2>
            final var itemToDuplicate = findItemId(ss().listWorkflowGroup(spaceId, providerId, level2Id), wfName);
            ss().moveOrCopyItems(spaceId, providerId, List.of(itemToDuplicate), level2Id,
                Space.NameCollisionHandling.AUTORENAME.toString(), true);
            // by default a 1 gets appended: e.g. "KNIME_project" -> "KNIME_project1"
            final var duplicatedName = level2Path.resolve(wfPathLevel2.getFileName().toString() + "1");
            assertThat("The workflow was not duplicated at <level2>", Files.exists(duplicatedName));

            // Moving items that do not exist
            assertThrows("Invalid IDs cannot be moved", InvalidRequestException.class,
                () -> ss().moveOrCopyItems(spaceId, providerId, List.of("a", "b", "c"), Space.ROOT_ITEM_ID,
                    Space.NameCollisionHandling.NOOP.toString(), false));

            // Moving the root
            assertThrows("The workspace root cannot be moved", InvalidRequestException.class,
                () -> ss().moveOrCopyItems(spaceId, providerId, List.of(Space.ROOT_ITEM_ID), level1Id,
                    Space.NameCollisionHandling.NOOP.toString(), false));

            // Move item to itself
            assertThrows("Cannot move an item to itself", InvalidRequestException.class,
                () -> ss().moveOrCopyItems(spaceId, providerId, List.of(level1Id), level1Id,
                    Space.NameCollisionHandling.NOOP.toString(), false));
        } finally {
            FileUtils.deleteQuietly(testWorkspacePath.resolve(fileName).toFile());
            FileUtils.deleteQuietly(testWorkspacePath.resolve(Space.DEFAULT_WORKFLOW_NAME).toFile());
            FileUtils.deleteQuietly(level1Path.toFile());
        }
    }

    public void testMoveItemsWithOpenWorkflowLocal() throws Exception {
        var testWorkspacePath = getTestWorkspacePath("test_workspace_to_list");
        var providerId = registerLocalSpaceProviderForTesting(testWorkspacePath);
        var spaceId = LocalWorkspace.LOCAL_WORKSPACE_ID;
        var wfName = "workflow";
        var wfGroupName = "EmptyGroup";
        var fileName = "data.txt";

        // Find space IDs
        var wfId = findItemId(ss().listWorkflowGroup(spaceId, providerId, Space.ROOT_ITEM_ID), wfName);
        var wfGroupId = findItemId(ss().listWorkflowGroup(spaceId, providerId, Space.ROOT_ITEM_ID), wfGroupName);
        var fileId = findItemId(ss().listWorkflowGroup(spaceId, providerId, Space.ROOT_ITEM_ID), fileName);

        // Add open project to workflow project manager
        var workflowProject = createWorkflowProject(providerId, spaceId, wfId);
        ProjectManager.getInstance().addProject(workflowProject);

        try {
            // Try to move data file and open workflow
            assertThrows("Moving an open workflow should not work", ServiceExceptions.InvalidRequestException.class,
                () -> ss().moveOrCopyItems(spaceId, providerId, List.of(wfId, fileId), wfGroupId,
                    Space.NameCollisionHandling.NOOP.toString(), false));
        } finally {
            ProjectManager.getInstance().removeProject("some_id", WorkflowManagerUtil::disposeWorkflow);
        }
    }

    private static Project createWorkflowProject(final String providerId, final String spaceId, final String itemId) {
        var origin = new Origin() {

            @Override
            public String getProviderId() {
                return providerId;
            }

            @Override
            public String getSpaceId() {
                return spaceId;
            }

            @Override
            public String getItemId() {
                return itemId;
            }

            @Override
            public ProjectTypeEnum getProjectType() {
                return ProjectTypeEnum.WORKFLOW;
            }
        };
        return new Project() {

            @Override
            public WorkflowManager openProject() {
                return null;
            }

            @Override
            public String getName() {
                return "some_name";
            }

            @Override
            public String getID() {
                return "some_id";
            }

            @Override
            public Optional<Origin> getOrigin() {
                return Optional.of(origin);
            }
        };
    }

    public void testMoveItemsWithNameCollisionsLocal() throws Exception {
        var testWorkspacePath = FileUtil.createTempDir("move-with-collisions").toPath();
        var providerId = registerLocalSpaceProviderForTesting(testWorkspacePath);
        var spaceId = LocalWorkspace.LOCAL_WORKSPACE_ID;
        var level1 = "level1";
        var level1Path = testWorkspacePath.resolve(level1);
        var fileName = "testfile.txt";

        try {
            // Create folders, files and workflows
            Files.createFile(testWorkspacePath.resolve(fileName));
            Files.createDirectory(level1Path);
            Files.createFile(level1Path.resolve(fileName));
            var level1Id = findItemId(ss().listWorkflowGroup(spaceId, providerId, Space.ROOT_ITEM_ID), level1);
            var wfLevel0 = ss().createWorkflow(spaceId, providerId, Space.ROOT_ITEM_ID, Space.DEFAULT_WORKFLOW_NAME);
            ss().createWorkflow(spaceId, providerId, level1Id, Space.DEFAULT_WORKFLOW_NAME); // This space item is not needed

            // Try to move without name collision handling
            var fileIdLevel0 = findItemId(ss().listWorkflowGroup(spaceId, providerId, Space.ROOT_ITEM_ID), fileName);
            assertThrows("Cannot move a file that already exists at the destination",
                ServiceExceptions.IOException.class, () -> ss().moveOrCopyItems(spaceId, providerId,
                    List.of(fileIdLevel0), level1Id, Space.NameCollisionHandling.NOOP.toString(), false));
            assertThrows("Cannot move a workflow that already exists at the destination",
                ServiceExceptions.IOException.class, () -> ss().moveOrCopyItems(spaceId, providerId,
                    List.of(wfLevel0.getId()), level1Id, Space.NameCollisionHandling.NOOP.toString(), false));

            // Move with overwrite collision handling
            ss().moveOrCopyItems(spaceId, providerId, List.of(fileIdLevel0, wfLevel0.getId()), level1Id,
                Space.NameCollisionHandling.OVERWRITE.toString(), false);
            assertThat("The newly created file didn't move out of <root>",
                Files.notExists(testWorkspacePath.resolve(fileName)));
            assertThat("The newly created file didn't move to <level1>", Files.exists(level1Path.resolve(fileName)));
            assertThat("The newly created workflow didn't move out of <root>",
                Files.notExists(testWorkspacePath.resolve(Space.DEFAULT_WORKFLOW_NAME)));
            assertThat("The newly created workflow didn't move to <level1>",
                Files.exists(level1Path.resolve(Space.DEFAULT_WORKFLOW_NAME)));

            // Move with auto-rename collision handling
            Files.createFile(testWorkspacePath.resolve(fileName));
            var anotherWfLevel0 = ss().createWorkflow(spaceId, providerId, Space.ROOT_ITEM_ID, Space.DEFAULT_WORKFLOW_NAME);
            var anotherFileIdLevel0 =
                findItemId(ss().listWorkflowGroup(spaceId, providerId, Space.ROOT_ITEM_ID), fileName);
            ss().moveOrCopyItems(spaceId, providerId, List.of(anotherFileIdLevel0, anotherWfLevel0.getId()), level1Id,
                Space.NameCollisionHandling.AUTORENAME.toString(), false);
            assertThat("The newly created file didn't move out of <root>",
                Files.notExists(testWorkspacePath.resolve(fileName)));
            assertThat("The newly created file didn't move to <level1>",
                Files.exists(level1Path.resolve("testfile(1).txt")));
            assertThat("The newly created workflow didn't move out of <root>",
                Files.notExists(testWorkspacePath.resolve(Space.DEFAULT_WORKFLOW_NAME)));
            assertThat("The newly created workflow didn't move to <level1>",
                Files.exists(level1Path.resolve(Space.DEFAULT_WORKFLOW_NAME + "1")));
        } finally {
            FileUtils.deleteQuietly(testWorkspacePath.resolve(fileName).toFile());
            FileUtils.deleteQuietly(testWorkspacePath.resolve(Space.DEFAULT_WORKFLOW_NAME).toFile());
            FileUtils.deleteQuietly(level1Path.toFile());
        }
    }

}
