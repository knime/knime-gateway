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
package org.knime.gateway.impl.webui;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import org.knime.core.util.PathUtils;
import org.knime.gateway.testing.helper.webui.SpaceServiceTestHelper;

/**
 * Tests for the {@link LocalWorkspace}. Note that {@link SpaceServiceTestHelper} also tests the local workspace but
 * cannot access the internal data structures.
 *
 * @author Benjamin Wilhelm, KNIME GmbH, Berlin, Germany
 */
public final class LocalWorkspaceTests {

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

    private static void deleteAndCheckMaps(final LocalWorkspace workspace, final List<Path> itemsToDelete,
        final List<Path> itemsToKeep) throws IOException {
        var idsToDelete =
            workspace.m_itemIdToPathMap.entrySet().stream().filter(e -> itemsToDelete.contains(e.getValue()))
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
        assertThat("must be expected amount of ids", workspace.m_itemIdToPathMap.size(), equalTo(expectedPaths.length));
        // NB: The root is also in the map
        assertThat("must be expected anount of cached types", workspace.m_pathToTypeMap.size(),
            equalTo(expectedPaths.length + 1));
        for (var path : expectedPaths) {
            assertThat("ids map must contain the expected paths", workspace.m_itemIdToPathMap.values().contains(path));
            assertThat("types map must contain the expected paths", workspace.m_pathToTypeMap.keySet().contains(path));
        }
    }
}
