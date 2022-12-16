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
 *   Dec 8, 2022 (hornm): created
 */
package org.knime.gateway.impl.webui;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.workflow.MetaNodeTemplateInformation;
import org.knime.core.node.workflow.WorkflowPersistor;
import org.knime.core.util.workflowalizer.MetadataConfig;
import org.knime.gateway.api.webui.entity.SpaceItemEnt;
import org.knime.gateway.api.webui.entity.SpaceItemEnt.TypeEnum;
import org.knime.gateway.api.webui.entity.WorkflowGroupContentEnt;
import org.knime.gateway.api.webui.util.EntityFactory;
import org.knime.gateway.api.webui.util.WorkflowEntityFactory;

/**
 * {@link Space}-implementation that represents the local workspace.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public final class LocalWorkspace implements Space {

    /**
     * ID of the local workspace.
     */
    public static final String LOCAL_WORKSPACE_SPACE_ID = "local";

    // assumption is that there is exactly one user of the local workspace at a time
    private final Map<Integer, Path> m_itemIdToPathMap = new HashMap<>();

    // just for optimization purposes: avoids the repeated determination of the item type
    // which sometimes involves reading and parsing files (in order to determine the component type)
    private final Map<Path, SpaceItemEnt.TypeEnum> m_pathToTypeMap = new HashMap<>();

    private final Path m_localWorkspaceRootPath;

    /**
     * @param localWorkspaceRootPath the path to the root of the local workspace
     */
    public LocalWorkspace(final Path localWorkspaceRootPath) {
        m_localWorkspaceRootPath = localWorkspaceRootPath;
    }

    @Override
    public String getId() {
        return LOCAL_WORKSPACE_SPACE_ID;
    }

    @Override
    public WorkflowGroupContentEnt listWorkflowGroup(final String workflowGroupItemId) throws IOException {
        Path relativePath = null;
        var isRoot = Space.ROOT_ITEM_ID.equals(workflowGroupItemId);
        if (!isRoot) {
            relativePath = m_itemIdToPathMap.get(Integer.valueOf(workflowGroupItemId));
            if (relativePath == null) {
                throw new NoSuchElementException("Unknown item id '" + workflowGroupItemId + "'");
            }
        }
        var absolutePath = isRoot ? m_localWorkspaceRootPath : m_localWorkspaceRootPath.resolve(relativePath);
        if (cacheOrGetSpaceItemTypeFromCache(absolutePath) != TypeEnum.WORKFLOWGROUP) {
            throw new NoSuchElementException("The item with id '" + workflowGroupItemId + "' is not a workflow group");
        }
        return EntityFactory.Space.buildWorkflowGroupContentEnt(absolutePath, m_localWorkspaceRootPath,
            getItemIdFunction(), this::cacheOrGetSpaceItemTypeFromCache, LocalWorkspace::isValidWorkspaceItem,
            getItemComparator());
    }

    @Override
    public Path toLocalAbsolutePath(final String itemId) {
        return m_itemIdToPathMap.get(Integer.valueOf(itemId));
    }

    /**
     * @return the root path of the local workspace
     */
    public Path getLocalWorkspaceRoot() {
        return m_localWorkspaceRootPath;
    }

    private static boolean isValidWorkspaceItem(final Path p) {
        try {
            return !Files.isHidden(p)
                && !WorkflowPersistor.METAINFO_FILE.equals(p.getName(p.getNameCount() - 1).toString());
        } catch (IOException ex) {
            NodeLogger.getLogger(WorkflowEntityFactory.class)
                .warnWithFormat("Failed to evaluate 'isHidden' on path %s. Path ignored.", ex);
            return false;
        }
    }

    private static Comparator<SpaceItemEnt> getItemComparator() {
        return Comparator.comparing(SpaceItemEnt::getType).thenComparing(SpaceItemEnt::getName);
    }

    private Function<Path, String> getItemIdFunction() {
        return path -> {
            var id = path.hashCode();
            Path existingPath;
            while ((existingPath = m_itemIdToPathMap.get(id)) != null && !path.equals(existingPath)) {
                // handle hash collision
                id = 31 * id;
            }
            m_itemIdToPathMap.put(id, path);
            return Integer.toString(id);
        };
    }

    private SpaceItemEnt.TypeEnum cacheOrGetSpaceItemTypeFromCache(final Path item) {
        return m_pathToTypeMap.computeIfAbsent(item, LocalWorkspace::getSpaceItemType);
    }

    private static SpaceItemEnt.TypeEnum getSpaceItemType(final Path item) {
        if (Files.isDirectory(item)) {
            // the order of checking is important because, e.g., a component also contains a workflow.knime file
            if (containsFile(item, WorkflowPersistor.TEMPLATE_FILE)) {
                try (final var s = Files.newInputStream(item.resolve(WorkflowPersistor.TEMPLATE_FILE))) {
                    final var c = new MetadataConfig("ignored");
                    c.load(s);
                    var isComponent = c.getConfigBase("workflow_template_information").getString("templateType")
                        .equals(MetaNodeTemplateInformation.TemplateType.SubNode.toString());
                    return isComponent ? SpaceItemEnt.TypeEnum.COMPONENT : SpaceItemEnt.TypeEnum.WORKFLOWTEMPLATE;
                } catch (InvalidSettingsException | IOException ex) {
                    NodeLogger.getLogger(LocalWorkspace.class)
                        .warnWithFormat("Space item type couldn't be determined for %s", item, ex);
                    return SpaceItemEnt.TypeEnum.DATA;
                }
            } else if (containsFile(item, WorkflowPersistor.WORKFLOW_FILE)) {
                return SpaceItemEnt.TypeEnum.WORKFLOW;
            } else {
                return SpaceItemEnt.TypeEnum.WORKFLOWGROUP;
            }
        } else {
            return SpaceItemEnt.TypeEnum.DATA;
        }
    }

    private static boolean containsFile(final Path directory, final String filename) {
        return Files.exists(directory.resolve(filename));
    }

}
