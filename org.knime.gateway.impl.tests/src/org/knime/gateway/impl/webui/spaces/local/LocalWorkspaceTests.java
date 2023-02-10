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
 *   Jan 24, 2023 (benjamin): created
 */
package org.knime.gateway.impl.webui.spaces.local;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.knime.core.util.PathUtils;
import org.knime.gateway.impl.webui.spaces.Space;
import org.knime.gateway.testing.helper.webui.SpaceServiceTestHelper;

/**
 * Tests for the {@link LocalWorkspace}. Note that {@link SpaceServiceTestHelper} also tests the local workspace but
 * cannot access the internal data structures.
 *
 * @author Benjamin Wilhelm, KNIME GmbH, Berlin, Germany
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @author Kai Franze, KNIME GmbH
 */
public final class LocalWorkspaceTests {

    /**
     * Tests {@link LocalWorkspace#getAncestorItemIds(String)}.
     *
     * @throws IOException
     */
    @Test
    public void testGetAncestorItemIds() throws IOException {
        var workspaceFolder = PathUtils.createTempDir("workspace");
        var workspace = new LocalWorkspace(workspaceFolder);

        var dir3 = Files.createDirectories(workspaceFolder.resolve("dir1/dir2/dir3"));
        createFile(workspaceFolder.resolve("dir1"), "test.txt");
        createFile(workspaceFolder.resolve("dir1/dir2"), "test2.txt");
        createWorkflow(workspace, workspaceFolder.resolve("dir1/dir2/dir3"), workspace.getItemId(dir3));

        var spaceItemsRoot = workspace.listWorkflowGroup(Space.ROOT_ITEM_ID).getItems();
        assertThat(workspace.getAncestorItemIds(spaceItemsRoot.get(0).getId()),
            Matchers.emptyCollectionOf(String.class));

        var spaceItemsDir1 = workspace.listWorkflowGroup(spaceItemsRoot.get(0).getId()).getItems();
        assertThat(workspace.getAncestorItemIds(spaceItemsDir1.get(1).getId()),
            is(List.of(spaceItemsRoot.get(0).getId())));

        var spaceItemsDir2 = workspace.listWorkflowGroup(spaceItemsDir1.get(0).getId()).getItems();
        assertThat(workspace.getAncestorItemIds(spaceItemsDir2.get(1).getId()),
            is(List.of(spaceItemsDir1.get(0).getId(), spaceItemsRoot.get(0).getId())));

        var spaceItemsDir3 = workspace.listWorkflowGroup(spaceItemsDir2.get(0).getId()).getItems();
        assertThat(workspace.getAncestorItemIds(spaceItemsDir3.get(0).getId()),
            is(List.of(spaceItemsDir2.get(0).getId(), spaceItemsDir1.get(0).getId(), spaceItemsRoot.get(0).getId())));
    }

    /**
     * Tests that the maps containing the id to path mapping and the path to type mapping get updated correctly whenever
     * items are deleted from the workspace.
     *
     * @throws IOException
     */
    @Test
    public void testMapCleanupOnDelete() throws IOException {
        var workspaceFolder = PathUtils.createTempDir("workspace");
        var workspace = new LocalWorkspace(workspaceFolder);

        // Add some items to the root
        var fileNotToBeDeleted = createFile(workspaceFolder, "file_not_to_be_deleted.txt");
        var fileToBeDeleted = createFile(workspaceFolder, "file_to_be_deleted.txt");
        var workflowInRootToBeDeleted = createWorkflow(workspace, workspaceFolder, Space.ROOT_ITEM_ID);
        var workflowInRoot = createWorkflow(workspace, workspaceFolder, Space.ROOT_ITEM_ID);

        // Create a workflow group
        var groupToBeDeleted = workspaceFolder.resolve("groupToBeDeleted");
        Files.createDirectories(groupToBeDeleted);
        var groupToBeDeletedId = findItemId(workspace, Space.ROOT_ITEM_ID, groupToBeDeleted.getFileName().toString());

        // Add some items to the group
        var fileInGroup = createFile(groupToBeDeleted, "file_in_group.txt");
        var workflowInGroup = createWorkflow(workspace, groupToBeDeleted, groupToBeDeletedId);

        // Add another group with one file that gets deleted
        var groupNotToBeDeleted = workspaceFolder.resolve("groupNotToBeDeleted");
        Files.createDirectories(groupNotToBeDeleted);
        var groupNotToBeDeletedId =
            findItemId(workspace, Space.ROOT_ITEM_ID, groupNotToBeDeleted.getFileName().toString());
        var fileInGroupToBeDeleted = createFile(groupNotToBeDeleted, "another_file_in_group.txt");

        // List the items to give them ids
        workspace.listWorkflowGroup(Space.ROOT_ITEM_ID);
        workspace.listWorkflowGroup(groupToBeDeletedId);
        workspace.listWorkflowGroup(groupNotToBeDeletedId);

        // Assert that the maps contain all files and no more
        assertIdAndTypeMapContain(workspace, fileNotToBeDeleted, fileToBeDeleted, workflowInRootToBeDeleted,
            workflowInRoot, groupNotToBeDeleted, groupNotToBeDeleted, fileInGroup, workflowInGroup,
            fileInGroupToBeDeleted);

        // Delete some items and check that the maps get updated correctly
        deleteAndCheckMaps(workspace,
            List.of(fileToBeDeleted, workflowInRootToBeDeleted, groupToBeDeleted, fileInGroupToBeDeleted),
            List.of(fileNotToBeDeleted, workflowInRoot, groupNotToBeDeleted));
    }

    /**
     * Tests that {@link LocalWorkspace#moveItems(List, String, String)} updates the {@link SpaceItemPathAndTypeCache}
     * accordingly.
     *
     * @throws IOException
     */
    @Test
    public void testMoveItemsUpdatesCache() throws IOException {
        var workspaceFolder = PathUtils.createTempDir("workspace");
        var workspace = new LocalWorkspace(workspaceFolder);

        // Add some items to the root
        var fileNotToBeMoved = createFile(workspaceFolder, "file_not_to_be_moved.txt");
        var fileToBeMoved = createFile(workspaceFolder, "file_to_be_moved.txt");
        var fileToBeMovedId = findItemId(workspace, Space.ROOT_ITEM_ID, fileNotToBeMoved.getFileName().toString());
        var workflowInRoot = createWorkflow(workspace, workspaceFolder, Space.ROOT_ITEM_ID);
        var workflowInRootToBeMoved = createWorkflow(workspace, workspaceFolder, Space.ROOT_ITEM_ID);
        var workflowInRootToBeMovedId =
            findItemId(workspace, Space.ROOT_ITEM_ID, workflowInRootToBeMoved.getFileName().toString());

        // Create a workflow group
        var workflowGroup = workspaceFolder.resolve("workflowGroup");
        Files.createDirectories(workflowGroup);
        var workflowGroupId = findItemId(workspace, Space.ROOT_ITEM_ID, workflowGroup.getFileName().toString());

        assertIdAndTypeMapContain(workspace, fileNotToBeMoved, fileToBeMoved, workflowInRootToBeMoved, workflowInRoot,
            workflowGroup);

        // Move file and workflow
        workspace.moveItems(List.of(fileToBeMovedId, workflowInRootToBeMovedId), workflowGroupId,
            Space.NameCollisionHandling.NOOP);

        // Assert paths are updated
        var newfilePath = workspace.m_spaceItemPathAndTypeCache.getPath(fileToBeMovedId);
        assertThat("Workflow path should be the new parent of the moved file path", newfilePath.getParent(),
            equalTo(workflowGroup));
        var newWorkflowPath = workspace.m_spaceItemPathAndTypeCache.getPath(workflowInRootToBeMovedId);
        assertThat("Workflow path should be the new parent of the moved workflow path", newWorkflowPath.getParent(),
            equalTo(workflowGroup));
    }

    /**
     * Some tests for the {@link LocalWorkspace#toKnimeUrl(String)}-method.
     *
     * @throws IOException
     */
    @Test
    public void testToKnimeURl() throws IOException {
        var workspaceFolder = PathUtils.createTempDir("workspace");
        var workspace = new LocalWorkspace(workspaceFolder);

        createFile(workspaceFolder, "test.txt");
        Files.createDirectories(workspaceFolder.resolve("dir"));
        createWorkflow(workspace, workspaceFolder, workspace.getItemId(workspaceFolder));

        var items = workspace.listWorkflowGroup(Space.ROOT_ITEM_ID).getItems();
        assertThat(workspace.toKnimeUrl(items.get(0).getId()).toString(), is("knime://LOCAL/dir/"));
        assertThat(workspace.toKnimeUrl(items.get(1).getId()).toString(), is("knime://LOCAL/KNIME_project/"));
        assertThat(workspace.toKnimeUrl(items.get(2).getId()).toString(), is("knime://LOCAL/test.txt"));
    }

    // TODO: (NXT-1503) Add test for {@link LocalWorkspace#importFile(...)}

    // TODO: (NXT-1503) Add test for {@link LocalWorkspace#importWorkflowOrWorkflowGroup(...)}

    // TODO: (NXT-1503) Add test for {@link LocalWorkspace#moveItemFromTmp(...)}

    private static void deleteAndCheckMaps(final LocalWorkspace workspace, final List<Path> itemsToDelete,
        final List<Path> itemsToKeep) throws IOException {
        var idsToDelete =
            workspace.m_spaceItemPathAndTypeCache.entrySet().stream().filter(e -> itemsToDelete.contains(e.getValue()))
                .map(e -> e.getKey()).map(i -> "" + i).collect(Collectors.toList());
        workspace.deleteItems(idsToDelete);
        assertIdAndTypeMapContain(workspace, itemsToKeep.toArray(Path[]::new));
    }

    private static String findItemId(final LocalWorkspace workspace, final String groupId, final String name)
        throws IOException {
        return workspace.listWorkflowGroup(groupId).getItems().stream().filter(i -> name.equals(i.getName()))
            .findFirst().get().getId();
    }

    private static Path createWorkflow(final LocalWorkspace workspace, final Path groupFolder, final String groupId)
        throws IOException {
        return groupFolder.resolve(workspace.createWorkflow(groupId).getName());
    }

    private static Path createFile(final Path parent, final String name) throws IOException {
        Path path = parent.resolve(name);
        Files.write(path, new byte[]{100, -100});
        return path;
    }

    private static void assertIdAndTypeMapContain(final LocalWorkspace workspace, final Path... expectedPaths) {
        // NB: The root is also in the map
        assertThat("must be expected amount of ids", workspace.m_spaceItemPathAndTypeCache.sizeOfItemIdToPathMap(),
            equalTo(expectedPaths.length + 1));
        // NB: The root is also in the map
        assertThat("must be expected anount of cached types",
            workspace.m_spaceItemPathAndTypeCache.sizeOfPathToTypeMap(), equalTo(expectedPaths.length + 1));
        for (var path : expectedPaths) {
            assertThat("ids map must contain the expected paths",
                workspace.m_spaceItemPathAndTypeCache.containsValue(path));
            assertThat("types map must contain the expected paths",
                workspace.m_spaceItemPathAndTypeCache.containsKey(path));
        }
    }
}
