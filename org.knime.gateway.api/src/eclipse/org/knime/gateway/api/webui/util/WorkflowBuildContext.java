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
 *   Mar 1, 2021 (hornm): created
 */
package org.knime.gateway.api.webui.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.knime.core.node.context.ModifiableNodeCreationConfiguration;
import org.knime.core.node.context.ports.ModifiablePortsConfiguration;
import org.knime.core.node.workflow.NativeNodeContainer;
import org.knime.core.node.workflow.NodeContainerTemplate;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.api.util.DependentNodeProperties;
import org.knime.gateway.api.webui.entity.WorkflowEnt;

/**
 * Additional information and configuration which is necessary to derive a {@link WorkflowEnt} from a
 * {@link WorkflowManager}. It contains all the information which is not directly available. Some information is simply
 * not part of the {@link WorkflowManager} (or related classes) or needs to be determined first. The determination
 * (calculation) of missing information is done in the context's build step, i.e.
 * {@link WorkflowBuildContextBuilder#build(WorkflowManager)}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public final class WorkflowBuildContext {

    private final WorkflowManager m_wfm;

    private final boolean m_includeInteractionInfo;

    private final DependentNodeProperties m_depNodeProps;

    private final boolean m_isInStreamingMode;

    private final boolean m_hasComponentProjectParent;

    private final boolean m_canUndo;

    private final boolean m_canRedo;

    private Map<NodeID, String[]> m_inPortGroupsAndIndices;

    private Map<NodeID, String[]> m_outPortGroupsAndIndices;

    private Map<NodeID, ModifiablePortsConfiguration> m_portsConfigurations;

    private final Predicate<NodeContainerTemplate> m_isLinkTypeChangable;

    private WorkflowBuildContext(final WorkflowManager wfm, final WorkflowBuildContextBuilder builder,
        final boolean isInStreamingMode, final boolean hasComponentProjectParent,
        final DependentNodeProperties depNodeProps) {
        m_wfm = wfm;
        m_includeInteractionInfo = builder.m_includeInteractionInfo;
        m_isInStreamingMode = isInStreamingMode;
        m_hasComponentProjectParent = hasComponentProjectParent;
        m_depNodeProps = depNodeProps;
        m_canUndo = builder.m_canUndo;
        m_canRedo = builder.m_canRedo;
        m_isLinkTypeChangable = builder.m_isLinkTypeChangable;
    }

    NodeIDEnt buildNodeIDEnt(final NodeID nodeID) {
        return new NodeIDEnt(nodeID, m_hasComponentProjectParent);
    }

    boolean isInStreamingMode() {
        return m_isInStreamingMode;
    }

    WorkflowManager wfm() {
        return m_wfm;
    }

    boolean includeInteractionInfo() {
        return m_includeInteractionInfo;
    }

    DependentNodeProperties dependentNodeProperties() {
        return m_depNodeProps;
    }

    boolean canUndo() {
        return m_canUndo;
    }

    boolean canRedo() {
        return m_canRedo;
    }

    /**
     * @param nnc the node to get the port index to port group map for
     * @param inPort whether to the map for the input or output ports
     * @return an optional array containing the port group name per port index; can be empty if the given index is not
     *         part of a port group
     */
    Optional<String[]> getPortIndexToPortGroupMap(final NativeNodeContainer nnc, final boolean inPort) {
        Map<NodeID, String[]> inOrOutPortGroupsAndIndices;
        if (inPort) {
            if (m_inPortGroupsAndIndices == null) {
                m_inPortGroupsAndIndices = new HashMap<>();
            }
            inOrOutPortGroupsAndIndices = m_inPortGroupsAndIndices;
        } else {
            if (m_outPortGroupsAndIndices == null) {
                m_outPortGroupsAndIndices = new HashMap<>();
            }
            inOrOutPortGroupsAndIndices = m_outPortGroupsAndIndices;
        }
        var portIndexToPortGroupMap =
            inOrOutPortGroupsAndIndices.computeIfAbsent(nnc.getID(), id -> getPortGroupsPerIndex(nnc, inPort));
        return Optional.ofNullable(portIndexToPortGroupMap);
    }

    /**
     * @param nnc The node to get the ports configuration for
     * @return The optional of {@link ModifiablePortsConfiguration} for the given node; it's cached such that no extra
     *         copy needs to be created every time it's accessed; can be empty if there is none
     */
    Optional<ModifiablePortsConfiguration> getPortsConfiguration(final NativeNodeContainer nnc) {
        if (m_portsConfigurations == null) {
            m_portsConfigurations = new HashMap<>();
        }
        // Compute the ports configuration if absent, unwrap the optional
        var portsConfig = m_portsConfigurations.computeIfAbsent(nnc.getID(), id -> nnc.getNode()//
            .getCopyOfCreationConfig()//
            .flatMap(ModifiableNodeCreationConfiguration::getPortConfig)//
            .orElse(null));
        // Wrap the result in an optional again
        return Optional.ofNullable(portsConfig);
    }

    private String[] getPortGroupsPerIndex(final NativeNodeContainer nnc, final boolean inPort) {
        return getPortsConfiguration(nnc)//
            // Create map of port group to port group indices
            .map(portsConfig -> inPort ? portsConfig.getInputPortLocation() : portsConfig.getOutputPortLocation())//
            // Create a String array of port groups per index
            .map(portGroupsToIndicesMap -> {
                var portGroupsPerIndex = new String[inPort ? (nnc.getNrInPorts() - 1) : (nnc.getNrOutPorts() - 1)];
                portGroupsToIndicesMap.entrySet().forEach(
                    entry -> Arrays.stream(entry.getValue()).forEach(i -> portGroupsPerIndex[i] = entry.getKey()));
                return portGroupsPerIndex;
            })//
            // Return null if the Optional received by "getPortsConfiguration(...)" was empty
            .orElse(null);
    }

    boolean isLinkTypeChangable(final NodeContainerTemplate nct) {
        return m_isLinkTypeChangable.test(nct);
    }

    /**
     * Creates a new builder instance.
     *
     * @return a new builder instance
     */
    public static WorkflowBuildContextBuilder builder() {
        return new WorkflowBuildContextBuilder();
    }

    /**
     * Builder to create {@link WorkflowBuildContext}-objects.
     */
    public static final class WorkflowBuildContextBuilder {

        private boolean m_includeInteractionInfo = false;

        private boolean m_canUndo = false;

        private boolean m_canRedo = false;

        private Supplier<DependentNodeProperties> m_depNodeProps;

        private Predicate<NodeContainerTemplate> m_isLinkTypeChangable;

        private WorkflowBuildContextBuilder() {
            //
        }

        /**
         * Sets the 'canUndo' property.
         *
         * @param canUndo whether an undo is possible at least once on the workflow
         * @return this builder instance
         */
        public WorkflowBuildContextBuilder canUndo(final boolean canUndo) {
            m_canUndo = canUndo;
            return this;
        }

        /**
         * Sets the 'canRedo' property.
         *
         * @param canRedo whether a redo is possible at least once on the workflow
         * @return this builder instance
         */
        public WorkflowBuildContextBuilder canRedo(final boolean canRedo) {
            m_canRedo = canRedo;
            return this;
        }

        /**
         * Whether interaction info should be included in the workflow entity to build.
         *
         * @param includeInteractionInfo the new value
         * @return this builder instance
         */
        public WorkflowBuildContextBuilder includeInteractionInfo(final boolean includeInteractionInfo) {
            m_includeInteractionInfo = includeInteractionInfo;
            return this;
        }

        /**
         * Provides the already calculated dependent node properties. Only relevant if interaction info is to be
         * included in the resulting workflow entity.
         *
         * If no dependent node properties are provided and interaction info supposed to be included, they will be
         * determined in the {@link #build()}-step.
         *
         * @param depNodeProps the pre-calculated dependent node properties
         * @return this builder instance
         */
        public WorkflowBuildContextBuilder setDependentNodeProperties(final Supplier<DependentNodeProperties> depNodeProps) {
            m_depNodeProps = depNodeProps;
            return this;
        }

        /**
         * Set the predicate for linked components.
         *
         * @param isLinkTypeChangable
         * @return This builder instance
         */
        public WorkflowBuildContextBuilder
            setIsLinkTypePredicate(final Predicate<NodeContainerTemplate> isLinkTypeChangable) {
            m_isLinkTypeChangable = isLinkTypeChangable;
            return this;
        }

        /**
         * Builds the workflow context. This might be an operation which is a bit more involved since certain
         * characteristics of the workflow are being determined.
         *
         * Note: the passed workflow manager must be locked, i.e. {@link WorkflowManager#lock()}.
         *
         * @param wfm the workflow manager to build the workflow entity for
         * @return the new {@link WorkflowBuildContext} instance
         */
        WorkflowBuildContext build(final WorkflowManager wfm) {
            assert wfm.isLockedByCurrentThread();
            DependentNodeProperties dnp = null;
            if (m_includeInteractionInfo) {
                dnp = m_depNodeProps == null ? DependentNodeProperties.determineDependentNodeProperties(wfm)
                    : m_depNodeProps.get();
            }
            return new WorkflowBuildContext(wfm, this, CoreUtil.isInStreamingMode(wfm),
                wfm.getProjectComponent().isPresent(), dnp);
        }

    }
}