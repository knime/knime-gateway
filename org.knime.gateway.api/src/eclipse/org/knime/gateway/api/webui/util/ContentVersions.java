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
 */
package org.knime.gateway.api.webui.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.knime.core.node.config.base.ConfigBase;
import org.knime.core.node.config.base.JSONConfig;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.flowvariable.FlowVariablePortObject;
import org.knime.core.node.workflow.FlowVariable;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeOutPort;
import org.knime.core.webui.data.util.InputPortUtil;
import org.knime.core.webui.node.dialog.SettingsType;
import org.knime.core.webui.node.dialog.internal.SettingsExtractor;
import org.knime.core.webui.node.view.NodeViewManager;

/**
 * A reference will yield a different <i>content version</i> if its "content" has changed. The definition of "content"
 * depends on the type. This can be, for instance, a hashcode of carried data, but also instance identity.
 * 
 * @author Benjamin Moser, KNIME GmbH
 */
final class ContentVersions {

    private ContentVersions() {

    }

    /**
     * Retrieves an integer representing the version of the configuration content associated with the provided node
     * container. A change in the return value of this method indicates that the configuration content has been
     * modified.
     *
     * @param nc The node container whose configuration content version needs to be retrieved
     * @return An integer representing the version of the configuration content
     */
    static Integer getModelSettingsContentVersion(final NodeContainer nc) {
        if (!nc.getNodeContainerState().isExecuted() //
            || !NodeViewManager.hasNodeView(nc)) {
            return null;
        }
        var settings = new SettingsExtractor( //
            nc, //
            // only interested in updating model settings -- view settings have their own mechanism
            // of live-updating the view
            Set.of(SettingsType.MODEL), //
            s -> {
                // no need to validate extractedSettings here, because we are only interested in the content.
                // even if invalid, this should trigger a "content version" change.
            }) //
            .getNodeSettingsOverwrittenByVariables();

        return serialiseSettings(settings.get(SettingsType.MODEL)).hashCode();
    }

    private static String serialiseSettings(final ConfigBase settings) {
        return JSONConfig.toJSONString(settings, JSONConfig.WriterConfig.DEFAULT);
    }

    /**
     * A change in the return value of this method signals that the content provided by the given port has changed.
     *
     * @param outPort An output port providing some content
     * @return A value identifying the content provided by the port
     */
    public static Integer getPortContentVersion(final NodeOutPort outPort) {
        if (outPort.getPortType().equals(FlowVariablePortObject.TYPE)) {
            return getContentVersion(getAllAvailableFlowVariables(outPort));
        } else {
            var po = outPort.getPortObject();
            if (po == null) {
                var spec = outPort.getPortObjectSpec();
                return spec == null ? null : getContentVersion(spec);
            } else {
                return getContentVersion(po);
            }
        }
    }

    /**
     * A change in the return value of this method signals that the input provided to a given node has changed.
     *
     * @param nc The node whose input to consider
     * @return A value identifying the input provided to the node
     */
    public static Integer getInputContentVersion(final NodeContainer nc) {
        var inPortSpecs = InputPortUtil.getInputSpecsIncludingVariablePort(nc);
        return getContentVersion(inPortSpecs);
    }

    private static Integer getContentVersion(final Collection<FlowVariable> flowVariables) {
        if (flowVariables.isEmpty()) {
            return null;
        }
        var hashes = flowVariables.stream().map(ContentVersions::getContentVersion).toList();
        return combineHashes(hashes);
    }

    private static Integer getContentVersion(final PortObjectSpec[] specs) {
        if (specs == null || specs.length == 0) {
            return null;
        }
        var hashes = Arrays.stream(specs) //
            .map(ContentVersions::getContentVersion) //
            .toList();
        return combineHashes(hashes);
    }

    /**
     * The content version of a single flow variable is based on the data it carries, namely name and value.
     * 
     * @implNote {@link System#identityHashCode(Object)} must not be used here because we are comparing values, not
     *           instances.
     */
    private static Integer getContentVersion(final FlowVariable flowVariable) {
        return combineHashes(List.of( //
            Objects.hashCode(flowVariable.getName()), //
            Objects.hashCode(flowVariable.getValue(flowVariable.getVariableType()))) //
        );
    }

    /**
     * @see this#getContentVersion(PortObject)
     */
    private static Integer getContentVersion(final PortObjectSpec spec) {
        return System.identityHashCode(spec);
    }

    /**
     * The content version of a port object is based on instance identity alone, no matter the data it carries or how it
     * overrides {@link Object#hashCode()}
     */
    private static Integer getContentVersion(final PortObject po) {
        return System.identityHashCode(po);
    }

    private static Collection<FlowVariable> getAllAvailableFlowVariables(final NodeOutPort outPort) {
        return Optional.ofNullable(outPort.getFlowObjectStack()) //
            .map(stack -> stack.getAllAvailableFlowVariables().values()) //
            .orElse(Collections.emptyList());
    }

    private static Integer combineHashes(final List<Integer> hashes) {
        // must not use stream reduce here because aggregation is not associative.
        var combinedHash = 0;
        for (int hash : hashes) {
            combinedHash = 31 * combinedHash + hash;
        }
        return combinedHash;
    }

}
