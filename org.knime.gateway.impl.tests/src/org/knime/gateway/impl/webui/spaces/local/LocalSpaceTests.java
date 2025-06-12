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
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.knime.core.util.Pair;
import org.knime.core.util.PathUtils;
import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.api.webui.entity.SpaceItemEnt.TypeEnum;
import org.knime.gateway.api.webui.entity.SpaceItemReferenceEnt.ProjectTypeEnum;
import org.knime.gateway.api.webui.service.util.ServiceExceptions;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.OperationNotAllowedException;
import org.knime.gateway.impl.webui.spaces.Space;
import org.knime.gateway.impl.webui.spaces.Space.NameCollisionHandling;
import org.knime.gateway.impl.webui.spaces.SpaceItemPathAndTypeCache;
import org.knime.gateway.testing.helper.webui.SpaceServiceTestHelper;

/**
 * Tests for the {@link LocalSpace}. Note that {@link SpaceServiceTestHelper} also tests the local workspace but
 * cannot access the internal data structures.
 *
 * @author Benjamin Wilhelm, KNIME GmbH, Berlin, Germany
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @author Kai Franze, KNIME GmbH
 */
@SuppressWarnings({"static-method", "javadoc"})
public final class LocalSpaceTests {

    /**
     * The instance under test
     */
    private LocalSpace m_space;

    @Before
    public void setUpLocalWorkspace() throws IOException {
        m_space = new LocalSpace(PathUtils.createTempDir("workspace"));
    }

    /**
     * Tests {@link LocalSpace#getAncestorItemIds(String)}.
     *
     * @throws IOException
     */
    @Test
    public void testGetAncestorItemIds() throws IOException {
        var dir3 = Files.createDirectories(m_space.getRootPath().resolve("dir1/dir2/dir3"));
        createFile(m_space.getRootPath().resolve("dir1"), "test.txt");
        createFile(m_space.getRootPath().resolve("dir1/dir2"), "test2.txt");
        createWorkflow(m_space, m_space.getRootPath().resolve("dir1/dir2/dir3"), m_space.getItemId(dir3), Space.DEFAULT_WORKFLOW_NAME);

        var spaceItemsRoot = m_space.listWorkflowGroup(Space.ROOT_ITEM_ID).getItems();
        assertThat(m_space.getAncestorItemIds(spaceItemsRoot.get(0).getId()),
            Matchers.emptyCollectionOf(String.class));

        var spaceItemsDir1 = m_space.listWorkflowGroup(spaceItemsRoot.get(0).getId()).getItems();
        assertThat(m_space.getAncestorItemIds(spaceItemsDir1.get(1).getId()),
            equalTo(List.of(spaceItemsRoot.get(0).getId())));

        var spaceItemsDir2 = m_space.listWorkflowGroup(spaceItemsDir1.get(0).getId()).getItems();
        assertThat(m_space.getAncestorItemIds(spaceItemsDir2.get(1).getId()),
            equalTo(List.of(spaceItemsDir1.get(0).getId(), spaceItemsRoot.get(0).getId())));

        var spaceItemsDir3 = m_space.listWorkflowGroup(spaceItemsDir2.get(0).getId()).getItems();
        assertThat(m_space.getAncestorItemIds(spaceItemsDir3.get(0).getId()),
            equalTo(List.of(spaceItemsDir2.get(0).getId(), spaceItemsDir1.get(0).getId(), spaceItemsRoot.get(0).getId())));
    }

    /**
     * Tests that the maps containing the id to path mapping and the path to type mapping get updated correctly whenever
     * items are deleted from the workspace.
     *
     * @throws IOException
     */
    @Test
    public void testMapCleanupOnDelete() throws IOException {
        // Add some items to the root
        var fileNotToBeDeleted = createFile(m_space.getRootPath(), "file_not_to_be_deleted.txt");
        var fileToBeDeleted = createFile(m_space.getRootPath(), "file_to_be_deleted.txt");
        var workflowInRootToBeDeleted = createWorkflow(m_space, m_space.getRootPath(), Space.ROOT_ITEM_ID, Space.DEFAULT_WORKFLOW_NAME);
        var workflowInRoot = createWorkflow(m_space, m_space.getRootPath(), Space.ROOT_ITEM_ID, Space.DEFAULT_WORKFLOW_NAME);

        // Create a workflow group
        var groupToBeDeleted = m_space.getRootPath().resolve("groupToBeDeleted");
        Files.createDirectories(groupToBeDeleted);
        var groupToBeDeletedId = findItemId(m_space, Space.ROOT_ITEM_ID, groupToBeDeleted.getFileName().toString());

        // Add some items to the group
        var fileInGroup = createFile(groupToBeDeleted, "file_in_group.txt");
        var workflowInGroup = createWorkflow(m_space, groupToBeDeleted, groupToBeDeletedId, Space.DEFAULT_WORKFLOW_NAME);

        // Add another group with one file that gets deleted
        var groupNotToBeDeleted = m_space.getRootPath().resolve("groupNotToBeDeleted");
        Files.createDirectories(groupNotToBeDeleted);
        var groupNotToBeDeletedId =
            findItemId(m_space, Space.ROOT_ITEM_ID, groupNotToBeDeleted.getFileName().toString());
        var fileInGroupToBeDeleted = createFile(groupNotToBeDeleted, "another_file_in_group.txt");

        // List the items to give them ids
        m_space.listWorkflowGroup(Space.ROOT_ITEM_ID);
        m_space.listWorkflowGroup(groupToBeDeletedId);
        m_space.listWorkflowGroup(groupNotToBeDeletedId);

        // Assert that the maps contain all files and no more
        assertIdAndTypeMapContain(m_space, fileNotToBeDeleted, fileToBeDeleted, workflowInRootToBeDeleted,
            workflowInRoot, groupNotToBeDeleted, groupNotToBeDeleted, fileInGroup, workflowInGroup,
            fileInGroupToBeDeleted);

        // Delete some items and check that the maps get updated correctly
        deleteAndCheckMaps(m_space,
            List.of(fileToBeDeleted, workflowInRootToBeDeleted, groupToBeDeleted, fileInGroupToBeDeleted),
            List.of(fileNotToBeDeleted, workflowInRoot, groupNotToBeDeleted));
    }

    /**
     * Tests that {@link LocalSpace#moveItems(List, String, NameCollisionHandling)}
     * updates the {@link SpaceItemPathAndTypeCache} accordingly.
     *
     * @throws IOException
     */
    @Test
    public void testMoveItemsUpdatesCache() throws IOException {
        // Add some items to the root
        var fileNotToBeMoved = createFile(m_space.getRootPath(), "file_not_to_be_moved.txt");
        var fileToBeMoved = createFile(m_space.getRootPath(), "file_to_be_moved.txt");
        var fileToBeMovedId = findItemId(m_space, Space.ROOT_ITEM_ID, fileNotToBeMoved.getFileName().toString());
        var workflowInRoot = createWorkflow(m_space, m_space.getRootPath(), Space.ROOT_ITEM_ID, Space.DEFAULT_WORKFLOW_NAME);
        var workflowInRootToBeMoved = createWorkflow(m_space, m_space.getRootPath(), Space.ROOT_ITEM_ID, Space.DEFAULT_WORKFLOW_NAME + "1");
        var workflowInRootToBeMovedId =
            findItemId(m_space, Space.ROOT_ITEM_ID, workflowInRootToBeMoved.getFileName().toString());

        // Create a workflow group
        var workflowGroup = m_space.getRootPath().resolve("workflowGroup");
        Files.createDirectories(workflowGroup);
        var workflowGroupId = findItemId(m_space, Space.ROOT_ITEM_ID, workflowGroup.getFileName().toString());

        assertIdAndTypeMapContain(m_space, fileNotToBeMoved, fileToBeMoved, workflowInRootToBeMoved, workflowInRoot,
            workflowGroup);

        // Move file and workflow
        m_space.moveOrCopyItems(List.of(fileToBeMovedId, workflowInRootToBeMovedId), workflowGroupId,
            NameCollisionHandling.NOOP, false);

        // Assert paths are updated
        var newfilePath = m_space.m_spaceItemPathAndTypeCache.getPath(fileToBeMovedId);
        assertThat("Workflow path should be the new parent of the moved file path", newfilePath.getParent(),
            equalTo(workflowGroup));
        var newWorkflowPath = m_space.m_spaceItemPathAndTypeCache.getPath(workflowInRootToBeMovedId);
        assertThat("Workflow path should be the new parent of the moved workflow path", newWorkflowPath.getParent(),
            equalTo(workflowGroup));
    }

    /**
     * Some tests for the {@link LocalSpace#toKnimeUrl(String)}-method.
     *
     * @throws IOException
     */
    @Test
    public void testToKnimeURl() throws IOException {
        createFile(m_space.getRootPath(), "test.txt");
        Files.createDirectories(m_space.getRootPath().resolve("dir"));
        createWorkflow(m_space, m_space.getRootPath(), m_space.getItemId(m_space.getRootPath()), Space.DEFAULT_WORKFLOW_NAME);

        var items = m_space.listWorkflowGroup(Space.ROOT_ITEM_ID).getItems();
        assertThat(m_space.toKnimeUrl(items.get(0).getId()).toString(), equalTo("knime://LOCAL/dir/"));
        assertThat(m_space.toKnimeUrl(items.get(1).getId()).toString(), equalTo("knime://LOCAL/KNIME_project/"));
        assertThat(m_space.toKnimeUrl(items.get(2).getId()).toString(), equalTo("knime://LOCAL/test.txt"));

        // for historical reasons: consistency with workbench `AbstractContentProvider#getRootStore()`'s
        // KNIME URI representation
        assertThat(m_space.toKnimeUrl(Space.ROOT_ITEM_ID).toString(), equalTo("knime://LOCAL/"));
    }

    /**
     * Tests that {@link LocalSpace#importFile(Path, String, NameCollisionHandling, IProgressMonitor)}
     * updates the {@link SpaceItemPathAndTypeCache} accordingly.
     *
     * @throws IOException
     */
    @Test
    public void testImportFileUpdatesCache() throws IOException {
        var tmpFolder = PathUtils.createTempDir("tmp");

        var fileNameTxt = "test.txt";
        var tmpTxtPath = createFile(tmpFolder, fileNameTxt);
        m_space.importFile(tmpTxtPath, Space.ROOT_ITEM_ID, NameCollisionHandling.NOOP, null);
        assertIdAndTypeMapContain(m_space, m_space.getRootPath().resolve(fileNameTxt));
        assertItemIsOfType(m_space, m_space.getRootPath().resolve(fileNameTxt), TypeEnum.DATA);

        var fileNameCsv = "collisions.csv";
        var tmpCsvPath = createFile(tmpFolder, fileNameCsv);
        m_space.importFile(tmpCsvPath, Space.ROOT_ITEM_ID, NameCollisionHandling.NOOP, null);
        m_space.importFile(tmpCsvPath, Space.ROOT_ITEM_ID, NameCollisionHandling.OVERWRITE, null);
        assertIdAndTypeMapContain(m_space, m_space.getRootPath().resolve(fileNameTxt),
            m_space.getRootPath().resolve(fileNameCsv));
        assertItemIsOfType(m_space, m_space.getRootPath().resolve(fileNameCsv), TypeEnum.DATA);

        m_space.importFile(tmpCsvPath, Space.ROOT_ITEM_ID, NameCollisionHandling.AUTORENAME, null);
        assertIdAndTypeMapContain(m_space, m_space.getRootPath().resolve(fileNameTxt), m_space.getRootPath().resolve(fileNameCsv),
            m_space.getRootPath().resolve("collisions(1).csv"));
        assertItemIsOfType(m_space, m_space.getRootPath().resolve("collisions(1).csv"), TypeEnum.DATA);
    }

    /**
     * Tests that {@link LocalSpace#importWorkflowOrWorkflowGroup(Path, String, Consumer, NameCollisionHandling, IProgressMonitor)}
     * updates the {@link SpaceItemPathAndTypeCache} accordingly for *.knwf imports.
     *
     * @throws IOException
     */
    @Test
    public void testImportWorkflowUpdatesCache() throws IOException {
        runTestImportWorkflowOrWorkflowGroupUpdatesCache("simple.knwf", "simple", TypeEnum.WORKFLOW);
    }

    /**
     * Tests that {@link LocalSpace#importWorkflowOrWorkflowGroup(Path, String, Consumer, NameCollisionHandling, IProgressMonitor)}
     * updates the {@link SpaceItemPathAndTypeCache} accordingly for *.knar imports.
     *
     * @throws IOException
     */
    @Test
    public void testImportWorkflowGroupUpdatesCache() throws IOException {
        runTestImportWorkflowOrWorkflowGroupUpdatesCache("group.knar", "group", TypeEnum.WORKFLOWGROUP);
    }

    /**
     * Tests {@link LocalSpace#getProjectType(String)}.
     *
     * @throws IOException
     */
    @Test
    public void testGetProjectType() throws IOException {
        createFile(m_space.getRootPath(), "data.txt");
        createWorkflow(m_space, m_space.getRootPath(), m_space.getItemId(m_space.getRootPath()), Space.DEFAULT_WORKFLOW_NAME);
        var spaceItems = m_space.listWorkflowGroup(Space.ROOT_ITEM_ID).getItems();

        var workflowItem =
            spaceItems.stream().filter(item -> item.getType() == TypeEnum.WORKFLOW).findFirst().orElseThrow();
        assertThat("Not a workflow", m_space.getProjectType(workflowItem.getId()).orElseThrow(),
            equalTo(ProjectTypeEnum.WORKFLOW));

        var dataItem = spaceItems.stream().filter(item -> item.getType() == TypeEnum.DATA).findFirst().orElseThrow();
        assertThat("Not null", m_space.getProjectType(dataItem.getId()).orElse(null), nullValue());
    }

    /**
     * Tests renaming an item (via {@link LocalSpace#renameItem(String, String)}) by only change the case of the
     * item's name from lower to upper case.
     *
     * @throws IOException
     * @throws OperationNotAllowedException
     */
    @Test
    public void testRenameChangingItemCasing() throws IOException, OperationNotAllowedException {
        final var spaceRootPath = m_space.getRootPath();
        final var spaceRootId = m_space.getItemId(spaceRootPath);

        // test a data file and a workflow
        final var itemIds = List.of(m_space.getItemId(createFile(spaceRootPath, "data.txt")),
            m_space.getItemId(createWorkflow(m_space, spaceRootPath, spaceRootId, "workflow")));

        for (final var itemId : itemIds) {
            final var itemName = m_space.getItemName(itemId);
            final var oldPath = m_space.toLocalAbsolutePath(itemId).orElseThrow().toRealPath();
            assertThat(itemName, equalTo(Files.isDirectory(oldPath) ? "workflow" : "data.txt"));
            assertThat(oldPath.getFileName().toString(), equalTo(itemName));

            // rename the item, which should (1) not fail and (2) actually rename the item on disk and in the space
            final var newItemName = itemName.toUpperCase();
            final var renamed = m_space.renameItem(itemId, newItemName);
            assertThat(renamed.getName(), equalTo(newItemName));

            final var newPath = m_space.toLocalAbsolutePath(itemId).orElseThrow().toRealPath();
            assertThat(newPath.getFileName().toString(), equalTo(newItemName));
            assertThat(m_space.getItemName(itemId), equalTo(newItemName));
        }
    }

    @Test
    public void testNoOpRenameItem() throws OperationNotAllowedException, IOException {
        final var spaceRootPath = m_space.getRootPath();
        final var spaceRootId = m_space.getItemId(spaceRootPath);

        // test a directory, a data file and a workflow
        final var group = spaceRootPath.resolve(m_space.createWorkflowGroup(spaceRootId).getName());
        final var dataItem = createFile(spaceRootPath, "data.txt");
        final var wf = createWorkflow(m_space, spaceRootPath, spaceRootId, "workflow");
        final var itemIds = List.of(group, dataItem, wf).stream() //
                .map(m_space::getItemId).collect(Collectors.toList());

        for (final var itemId : itemIds) {
            // rename item to itself --> no-op and should not fail
            final var existingFile = m_space.toLocalAbsolutePath(itemId).orElseThrow();
            final var itemName = m_space.getItemName(itemId);
            final var renamedNoOp = m_space.renameItem(itemId, itemName);
            assertThat(renamedNoOp.getName(), equalTo(itemName));
            final var renamedNoOpFile = m_space.toLocalAbsolutePath(itemId).orElseThrow();
            // depending on the OS/FileSystem implementation
            // Path#equals might not be true even if they point to the same file
            assertTrue(Files.isSameFile(existingFile, renamedNoOpFile),
                "Expected paths to point to same file after no-op rename");
            assertEquals(existingFile, renamedNoOpFile, "Expected paths to be equal after no-op rename");
        }
    }

    @Test
    public void testRenameItemCollision() throws OperationNotAllowedException, IOException {
        final var spaceRootPath = m_space.getRootPath();
        final var spaceRootId = m_space.getItemId(spaceRootPath);

        // test a directory, a data file and a workflow with a type name to be referenced in the error message
        final var group = Pair.create(spaceRootPath.resolve(m_space.createWorkflowGroup(spaceRootId).getName()),
            "workflow group");
        final var dataItem = Pair.create(createFile(spaceRootPath, "data.txt"), "data file");
        final var wf = Pair.create(createWorkflow(m_space, spaceRootPath, spaceRootId, "workflow"), "workflow");
        final var itemIds = List.of(group, dataItem, wf).stream() //
                .map(p -> Pair.create(m_space.getItemId(p.getFirst()), p.getSecond())).toList();

        for (var i = 0; i < itemIds.size(); i++) {
            final var itemIdAndType = itemIds.get(i);
            final var itemId = itemIdAndType.getFirst();

            final var existingItemIdAndType = itemIds.get((i + 1) % itemIds.size());
            final var existingItemId = existingItemIdAndType.getFirst();
            // what we expect in the error message
            final var existingTypeName = existingItemIdAndType.getSecond();
            final var existingItemName = m_space.getItemName(existingItemId);
            try {
                m_space.renameItem(itemId, existingItemName);
            } catch (final ServiceExceptions.OperationNotAllowedException e) {
                final var msg = e.getMessage();
                assertThat("Expected exception message to contain the existing item type", msg,
                    Matchers.containsString(existingTypeName));
            }

        }
    }

    private void runTestImportWorkflowOrWorkflowGroupUpdatesCache(final String archiveName,
        final String itemName, final TypeEnum itemType) throws IOException {
        Consumer<Path> createMetaInfoFileFor = path -> {
            // Do nothing
        };

        var archivePath = getFilePath(archiveName);
        m_space.importWorkflowOrWorkflowGroup(archivePath, Space.ROOT_ITEM_ID, createMetaInfoFileFor,
            NameCollisionHandling.NOOP, null);
        assertIdAndTypeMapContain(m_space, m_space.getRootPath().resolve(itemName));
        assertItemIsOfType(m_space, m_space.getRootPath().resolve(itemName), itemType);

        m_space.importWorkflowOrWorkflowGroup(archivePath, Space.ROOT_ITEM_ID, createMetaInfoFileFor,
            NameCollisionHandling.OVERWRITE, null);
        assertIdAndTypeMapContain(m_space, m_space.getRootPath().resolve(itemName));
        assertItemIsOfType(m_space, m_space.getRootPath().resolve(itemName), itemType);

        m_space.importWorkflowOrWorkflowGroup(archivePath, Space.ROOT_ITEM_ID, createMetaInfoFileFor,
            NameCollisionHandling.AUTORENAME, null);
        assertIdAndTypeMapContain(m_space, m_space.getRootPath().resolve(itemName),
            m_space.getRootPath().resolve(itemName + "1"));
        assertItemIsOfType(m_space, m_space.getRootPath().resolve(itemName + "1"), itemType);
    }

    private static void deleteAndCheckMaps(final LocalSpace workspace, final List<Path> itemsToDelete,
        final List<Path> itemsToKeep) throws IOException {
        var idsToDelete =
            workspace.m_spaceItemPathAndTypeCache.entrySet().stream().filter(e -> itemsToDelete.contains(e.getValue()))
                .map(e -> e.getKey()).map(i -> i).collect(Collectors.toList());
        workspace.deleteItems(idsToDelete, false);
        assertIdAndTypeMapContain(workspace, itemsToKeep.toArray(Path[]::new));
    }

    private static String findItemId(final LocalSpace workspace, final String groupId, final String name)
        throws IOException {
        return workspace.listWorkflowGroup(groupId).getItems().stream().filter(i -> name.equals(i.getName()))
            .findFirst().get().getId();
    }

    private static Path createWorkflow(final LocalSpace workspace, final Path groupFolder, final String groupId,
        final String workflowName) throws IOException {
        return groupFolder.resolve(workspace.createWorkflow(groupId, workflowName).getName());
    }

    private static Path createFile(final Path parent, final String name) throws IOException {
        var path = parent.resolve(name);
        Files.write(path, new byte[]{100, -100});
        return path;
    }

    private static void assertIdAndTypeMapContain(final LocalSpace workspace, final Path... expectedPaths) {
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

    private static void assertItemIsOfType(final LocalSpace workspace, final Path itemPath, final TypeEnum type) {
        assertThat("item must be of the expected type",
            workspace.m_spaceItemPathAndTypeCache.determineTypeOrGetFromCache(itemPath), equalTo(type));
    }

    private static Path getFilePath(final String fileName) {
        try {
            var baseDir = CoreUtil.resolveToFile("/files/test_exports", LocalSpaceTests.class);
            return Paths.get(baseDir.getAbsolutePath(), getDirFromClass(LocalSpaceTests.class) + fileName);
        } catch (IOException ex) {
            throw new RuntimeException(ex); // Should never happen
        }
    }

    private static String getDirFromClass(final Class<?> testClass) {
        return "/" + testClass.getCanonicalName() + "/";
    }

}
