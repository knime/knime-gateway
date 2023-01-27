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
import static org.junit.Assert.assertThrows;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.workflow.contextv2.LocationInfo;
import org.knime.core.util.PathUtils;
import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.api.webui.entity.SpaceItemEnt;
import org.knime.gateway.api.webui.entity.WorkflowGroupContentEnt;
import org.knime.gateway.api.webui.service.SpaceService;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.InvalidRequestException;
import org.knime.gateway.impl.webui.service.ServiceDependencies;
import org.knime.gateway.impl.webui.spaces.LocalWorkspace;
import org.knime.gateway.impl.webui.spaces.Space;
import org.knime.gateway.impl.webui.spaces.SpaceProvider;
import org.knime.gateway.impl.webui.spaces.SpaceProviders;
import org.knime.gateway.testing.helper.ResultChecker;
import org.knime.gateway.testing.helper.ServiceProvider;
import org.knime.gateway.testing.helper.WorkflowExecutor;
import org.knime.gateway.testing.helper.WorkflowLoader;

/**
 * Tests {@link SpaceService}-implementations.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @author Kai Franze, KNIME GmbH
 */
public class SpaceServiceTestHelper extends WebUIGatewayServiceTestHelper {

    /**
     * @param entityResultChecker
     * @param serviceProvider
     * @param workflowLoader
     * @param workflowExecutor
     */
    protected SpaceServiceTestHelper(final ResultChecker entityResultChecker, final ServiceProvider serviceProvider,
        final WorkflowLoader workflowLoader, final WorkflowExecutor workflowExecutor) {
        super(SpaceServiceTestHelper.class, entityResultChecker, serviceProvider, workflowLoader, workflowExecutor);
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

        var group1Id = getItemIdForItemWithName(root.getItems(), "Group1");
        var group1 = ss().listWorkflowGroup(spaceId, providerId, group1Id);
        cr(group1, "workspace_to_list_group1");

        var group11Id = getItemIdForItemWithName(group1.getItems(), "Group11");
        var group11 = ss().listWorkflowGroup(spaceId, providerId, group11Id);
        cr(group11, "workspace_to_list_group11");

        var emptyGroupId = getItemIdForItemWithName(root.getItems(), "EmptyGroup");
        var emptyGroup = ss().listWorkflowGroup(spaceId, providerId, emptyGroupId);
        cr(emptyGroup, "workspace_to_list_empty_group");

        var dataTxtId = getItemIdForItemWithName(root.getItems(), "data.txt");
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
            public Map<String, Space> getSpaceMap() {
                return Arrays.stream(spaces).collect(Collectors.toMap(Space::getId, s -> s));
            }

        };
    }

    private static Space createSpace(final String id, final String name, final String owner, final String description,
        final boolean isPrivate) {
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
            public String getOwner() {
                return owner;
            }

            @Override
            public String getDescription() {
                return description;
            }

            @Override
            public boolean isPrivate() {
                return isPrivate;
            }

            @Override
            public WorkflowGroupContentEnt listWorkflowGroup(final String workflowGroupItemId) throws IOException {
                return null;
            }

            @Override
            public Path toLocalAbsolutePath(final ExecutionMonitor monitor, final String itemId) {
                return null;
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
            public SpaceItemEnt createWorkflow(final String workflowGroupItemId) throws IOException {
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
            public SpaceItemEnt importFile(final Path srcPath, final String workflowGroupItemId) throws IOException {
                return null;
            }

            @Override
            public SpaceItemEnt importWorkflows(final Path srcPath, final String workflowGroupItemId,
                final Consumer<Path> createMetaInfoFileFor) throws IOException {
                return null;
            }

            @Override
            public List<String> getAncestorItemIds(final String itemId) {
                return List.of();
            }
        };
    }

    private static SpaceProvider createLocalSpaceProviderForTesting(final Path testWorkspacePath)  {
        var localWorkspace = new LocalWorkspace(testWorkspacePath);
        return new SpaceProvider() {
            @Override public String getId() {
                return "local-testing";
            }

            @Override
            public Map<String, Space> getSpaceMap() {
                return Collections.singletonMap(localWorkspace.getId(), localWorkspace);
            }

            @Override
            public String getName() {
                return "local-testing-name";
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

    private static String getItemIdForItemWithName(final List<SpaceItemEnt> items, final String name) {
        return items.stream().filter(i -> i.getName().equals(name)).map(SpaceItemEnt::getId).findFirst().orElse(null);
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
            var wf0 = ss().createWorkflow(spaceId, providerId, Space.ROOT_ITEM_ID);
            cr(wf0, "created_workflow_0");
            var level0 = ss().listWorkflowGroup(spaceId, providerId, Space.ROOT_ITEM_ID);
            cr(level0, "workspace_to_create_level0");

            var level1Id = getItemIdForItemWithName(level0.getItems(), "level1");
            var wf1 = ss().createWorkflow(spaceId, providerId, level1Id);
            cr(wf1, "created_workflow_1");
            var level1 = ss().listWorkflowGroup(spaceId, providerId, level1Id);
            cr(level1, "workspace_to_create_level1");

            var level2Id = getItemIdForItemWithName(level1.getItems(), "level2");
            var wf2 = ss().createWorkflow(spaceId, providerId, level2Id);
            cr(wf2, "created_workflow_2");
            var level2 = ss().listWorkflowGroup(spaceId, providerId, level2Id);
            cr(level2, "workspace_to_create_level2");
        } finally {
            // Make sure cleanup is always performed
            var pathWf0 = testWorkspacePath//
                .resolve(LocalWorkspace.DEFAULT_WORKFLOW_NAME);
            var pathWf1 = testWorkspacePath//
                .resolve("level1")//
                .resolve(LocalWorkspace.DEFAULT_WORKFLOW_NAME);
            var pathWf2 = testWorkspacePath//
                .resolve("level1")//
                .resolve("level2")//
                .resolve(LocalWorkspace.DEFAULT_WORKFLOW_NAME);
            for (var path : List.of(pathWf0, pathWf1, pathWf2)) {
                FileUtils.deleteDirectory(path.toFile()); // To delete directory recursively
            }
        }
    }

    private static String findItemId(final WorkflowGroupContentEnt groupContent, final String name) {
        return groupContent.getItems().stream() //
            .filter(e -> name.equals(e.getName())) //
            .map(SpaceItemEnt::getId) //
            .findFirst() //
            .orElse(null);
    }

    /**
     * Tests {@link SpaceService#deleteItems(String, String, List)} for the local workspace.
     *
     * @throws Exception
     */
    public void testDeleteItemForLocalWorkspace() throws Exception {
        var testWorkspacePath = getTestWorkspacePath("test_workspace_to_delete");
        var providerId = registerLocalSpaceProviderForTesting(testWorkspacePath);
        var spaceId = LocalWorkspace.LOCAL_WORKSPACE_ID;
        var workflowGroupName = "workflow_group";
        var fileName = "testfile.txt";

        // Create a workflow and delete it
        var wf = ss().createWorkflow(spaceId, providerId, Space.ROOT_ITEM_ID);
        ss().deleteItems(spaceId, providerId, List.of(wf.getId()));
        assertThat("Workflow must not exist anymore",
            !Files.exists(testWorkspacePath.resolve(LocalWorkspace.DEFAULT_WORKFLOW_NAME)));

        // Create a workflow group with some content and delete it
        var workflowGroupPath = testWorkspacePath.resolve(workflowGroupName);
        Files.createDirectory(workflowGroupPath);
        var workflowGroupId =
            findItemId(ss().listWorkflowGroup(spaceId, providerId, Space.ROOT_ITEM_ID), workflowGroupName);
        ss().createWorkflow(spaceId, providerId, workflowGroupId);
        ss().createWorkflow(spaceId, providerId, workflowGroupId);
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
        wf = ss().createWorkflow(spaceId, providerId, Space.ROOT_ITEM_ID);
        PathUtils.deleteDirectoryIfExists(testWorkspacePath.resolve(LocalWorkspace.DEFAULT_WORKFLOW_NAME));
        ss().deleteItems(spaceId, providerId, List.of(wf.getId()));

        // Delete multiple items at once
        ss().createWorkflow(spaceId, providerId, Space.ROOT_ITEM_ID);
        filePath = testWorkspacePath.resolve(fileName);
        Files.write(filePath, new byte[]{127, 0, 0, -1, 10});
        workflowGroupPath = testWorkspacePath.resolve(workflowGroupName);
        Files.createDirectory(workflowGroupPath);
        workflowGroupId =
            findItemId(ss().listWorkflowGroup(spaceId, providerId, Space.ROOT_ITEM_ID), workflowGroupName);
        ss().createWorkflow(spaceId, providerId, workflowGroupId);
        ss().createWorkflow(spaceId, providerId, workflowGroupId);
        Files.createFile(workflowGroupPath.resolve(fileName));
        ss().listWorkflowGroup(spaceId, providerId, workflowGroupId);
        var rootFiles = ss().listWorkflowGroup(spaceId, providerId, Space.ROOT_ITEM_ID);
        ss().deleteItems(spaceId, providerId,
            rootFiles.getItems().stream().map(SpaceItemEnt::getId).collect(Collectors.toList()));
    }
}
