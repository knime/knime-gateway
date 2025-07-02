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
 *   21 Jul 2025 (albrecht): created
 */
package org.knime.gateway.impl.webui.preview.util;

import java.util.HashMap;
import java.util.Map;

import org.knime.gateway.api.webui.entity.ComponentNodeEnt;
import org.knime.gateway.api.webui.entity.MetaNodeEnt;
import org.knime.gateway.api.webui.entity.NativeNodeEnt;
import org.knime.gateway.api.webui.entity.NativeNodeInvariantsEnt;
import org.knime.gateway.api.webui.entity.NodeEnt;

/**
 * Utility functions for rendering nodes on workflow previews
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 * @since 5.6
 */
@SuppressWarnings("javadoc")
public final class NodeUtils {

    private final Map<String, NativeNodeInvariantsEnt> m_nodeTemplates;

    public NodeUtils(final Map<String, NativeNodeInvariantsEnt> nodeTemplates) {
        m_nodeTemplates = nodeTemplates;
    }

    private String getNodeOrTemplateProperty(final NodeEnt node, final String property) {
        if (node.getKind() == NodeEnt.KindEnum.NODE) {
            var template = m_nodeTemplates.get(((NativeNodeEnt)node).getTemplateId());

            if (template == null) {
                return null;
            }

            return switch (property) {
                case "name" -> template.getName();
                case "icon" -> template.getIcon();
                case "type" -> template.getType().toString();
                case "annotation" -> node.getAnnotation() != null ? node.getAnnotation().getText().getValue()
                    : null;
                default -> null;
            };
        } else if (node.getKind() == NodeEnt.KindEnum.COMPONENT) {
            var component = (ComponentNodeEnt)node;
            return switch (property) {
                case "name" -> component.getName();
                case "icon" -> component.getIcon();
                case "type" -> component.getType() != null ? component.getType().toString() : null;
                case "annotation" -> component.getAnnotation() != null ? component.getAnnotation().getText().getValue()
                    : null;
                default -> null;
            };
        } else if (node.getKind() == NodeEnt.KindEnum.METANODE) {
            var metanode = (MetaNodeEnt)node;
            return switch (property) {
                case "name" -> metanode.getName();
                case "annotation" -> metanode.getAnnotation() != null ? metanode.getAnnotation().getText().getValue()
                    : null;
                default -> null;
            };
        } else {
            return null;
        }
    }

    public String getNodeIcon(final NodeEnt node) {
        return getNodeOrTemplateProperty(node, "icon");
    }

    public String getNodeType(final NodeEnt node) {
        return getNodeOrTemplateProperty(node, "type");
    }

    public static Boolean isMetanode(final NodeEnt node) {
        return node.getKind() == NodeEnt.KindEnum.METANODE;
    }

    public String getNodeName(final NodeEnt node) {
        return getNodeOrTemplateProperty(node, "name");
    }

    public String getNodeLabel(final NodeEnt node) {
        return getNodeOrTemplateProperty(node, "annotation");
    }

    public static String getNodeTorsoPath(final String type) {
        String nullSafeType = type == null ? "default" : type;
        return ShapeConstants.NODE_TORSO_PATHS.getOrDefault(nullSafeType,
            ShapeConstants.NODE_TORSO_PATHS.get("default"));
    }

    public static String getNodeBackgroundColor(final String type) {
        String nullSafeType = type == null ? "HibiscusDark" : type;
        return ColorConstants.NODE_COLORS.getOrDefault(nullSafeType, ColorConstants.KNIME_COLORS.get("HibiscusDark"));
    }

    public enum ExecutionStateEnum {
            IDLE, CONFIGURED, EXECUTED, HALTED
    }

    /**
     * @param executionState the execution state of a node
     * @return a boolean array for the values of the traffic light in the form [red, yellow, green]
     */
    public static boolean[] getTrafficLight(final String executionState) {
        boolean[] defaultTrafficLight = new boolean[]{false, false, false};

        Map<ExecutionStateEnum, boolean[]> stateMapper = new HashMap<>(4);
        stateMapper.put(ExecutionStateEnum.IDLE, new boolean[]{true, false, false});
        stateMapper.put(ExecutionStateEnum.CONFIGURED, new boolean[]{false, true, false});
        stateMapper.put(ExecutionStateEnum.EXECUTED, new boolean[]{false, false, true});
        stateMapper.put(ExecutionStateEnum.HALTED, new boolean[]{false, false, true});

        if (executionState != null) {
            try {
                ExecutionStateEnum stateEnum = ExecutionStateEnum.valueOf(executionState);
                if (stateMapper.containsKey(stateEnum)) {
                    return stateMapper.get(stateEnum);
                }
            } catch (IllegalArgumentException e) {
                // thrown by valueOf, return null
            }
            return null;
        }
        return defaultTrafficLight;
    }

    private static final String[] activeColorNames = new String[]{"red", "yellow", "green"};

    private static final String[] activeStrokeNames = new String[]{"redBorder", "yellowBorder", "greenBorder"};

    public static String getTrafficFillColor(final boolean active, final int index) {
        return active && index >= 0 && index < activeColorNames.length
            ? ColorConstants.TRAFFIC_LIGHT.get(activeColorNames[index]) : ColorConstants.TRAFFIC_LIGHT.get("inactive");
    }

    public static String getTrafficStrokeColor(final boolean active, final int index) {
        return active && index >= 0 && index < activeStrokeNames.length
            ? ColorConstants.TRAFFIC_LIGHT.get(activeStrokeNames[index])
            : ColorConstants.TRAFFIC_LIGHT.get("inactiveBorder");
    }
}
