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
 *   Aug 3, 2023 (leonard.woerteler): created
 */
package org.knime.gateway.impl.webui.spaces.local;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.workflow.MetaNodeTemplateInformation;
import org.knime.core.node.workflow.WorkflowPersistor;
import org.knime.core.util.workflowalizer.MetadataConfig;
import org.knime.gateway.api.webui.entity.SpaceItemEnt;
import org.knime.gateway.api.webui.entity.SpaceItemEnt.TypeEnum;
import org.knime.gateway.impl.webui.spaces.SpaceItemPathAndTypeCache;

/**
 * Space item path and item type cache for the local space.
 *
 * @author Leonard Wörteler, KNIME GmbH, Konstanz, Germany
 */
final class LocalSpaceItemPathAndTypeCache extends SpaceItemPathAndTypeCache<Path> {

    LocalSpaceItemPathAndTypeCache(final String rootItemId, final Path localWorkspaceRootPath) {
        super(rootItemId, localWorkspaceRootPath);
    }

    Set<Map.Entry<String, Path>> entrySet() {
        return m_itemIdAndPathMapping.entrySet();
    }

    int sizeOfItemIdToPathMap() {
        return m_itemIdAndPathMapping.size();
    }

    int sizeOfPathToTypeMap() {
        return m_pathToTypeMap.size();
    }

    @Override
    protected TypeEnum getSpaceItemType(final Path item) {
        if (!Files.exists(item)) {
            return null;
        }
        if (Files.isDirectory(item)) {
            SpaceItemEnt.TypeEnum type;
            // the order of checking is important because, e.g., a component also contains a workflow.knime file
            if (containsFile(item, WorkflowPersistor.TEMPLATE_FILE)) {
                try (final var s = Files.newInputStream(item.resolve(WorkflowPersistor.TEMPLATE_FILE))) {
                    final var c = new MetadataConfig("ignored");
                    c.load(s);
                    var isComponent = c.getConfigBase("workflow_template_information").getString("templateType")
                        .equals(MetaNodeTemplateInformation.TemplateType.SubNode.toString());
                    type = isComponent ? SpaceItemEnt.TypeEnum.COMPONENT : SpaceItemEnt.TypeEnum.WORKFLOWTEMPLATE;
                } catch (InvalidSettingsException | IOException ex) {
                    NodeLogger.getLogger(LocalWorkspace.class)
                        .warnWithFormat("Space item type couldn't be determined for %s", item, ex);
                    type = SpaceItemEnt.TypeEnum.DATA;
                }
            } else if (containsFile(item, WorkflowPersistor.WORKFLOW_FILE)) {
                type = SpaceItemEnt.TypeEnum.WORKFLOW;
            } else {
                type = SpaceItemEnt.TypeEnum.WORKFLOWGROUP;
            }
            return type;
        } else {
            return SpaceItemEnt.TypeEnum.DATA;
        }
    }

    private static boolean containsFile(final Path directory, final String filename) {
        return Files.exists(directory.resolve(filename));
    }

    @Override
    protected boolean isPrefixOf(final Path prefix, final Path pathToCheck) {
        return pathToCheck.startsWith(prefix);
    }

    @Override
    protected boolean isAbsolute(final Path path) {
        return path.isAbsolute();
    }
}
