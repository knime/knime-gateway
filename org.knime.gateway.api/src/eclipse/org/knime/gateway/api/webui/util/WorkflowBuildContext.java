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

import java.util.function.Supplier;

import org.knime.core.node.workflow.DependentNodeProperties;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.NodeSuccessors;
import org.knime.core.node.workflow.WorkflowLock;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.api.webui.entity.WorkflowEnt;

/**
 * Additional information and configuration which is necessary to derive a {@link WorkflowEnt} from a
 * {@link WorkflowManager}. It contains all the information which is not directly available. Some information is simply
 * not part of the {@link WorkflowManager} (or related classes) or needs to be determined first. The determination
 * (calculation) of missing information is done in the context's build step, i.e.
 * {@link WorkflowBuildContextBuilder#build()}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public final class WorkflowBuildContext {

    private final WorkflowManager m_wfm;

    private final boolean m_includeInteractionInfo;

    private final DependentNodeProperties m_depNodeProps;

    private final NodeSuccessors m_nodeSuccessors;

    private final boolean m_isInStreamingMode;

    private final boolean m_hasComponentProjectParent;

    private final boolean m_canUndo;

    private final boolean m_canRedo;

    private int m_nodeCount;

    private WorkflowBuildContext(final WorkflowBuildContextBuilder builder, final boolean isInStreamingMode,
        final boolean hasComponentProjectParent, final DependentNodeProperties depNodeProps,
        final NodeSuccessors nodeSuccessors, final int nodeCount) {
        m_wfm = builder.m_wfm;
        m_includeInteractionInfo = builder.m_includeInteractionInfo;
        m_isInStreamingMode = isInStreamingMode;
        m_hasComponentProjectParent = hasComponentProjectParent;
        m_depNodeProps = depNodeProps;
        m_canUndo = builder.m_canUndo;
        m_canRedo = builder.m_canRedo;
        m_nodeSuccessors = nodeSuccessors;
        m_nodeCount = nodeCount;
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

    NodeSuccessors nodeSuccessors() {
        return m_nodeSuccessors;
    }

    int nodeCount() {
        return m_nodeCount;
    }

    /**
     * Creates a new builder instance.
     *
     * @param wfm the wfm to create the builder instance for
     * @return a new builder instance
     */
    public static WorkflowBuildContextBuilder builder(final WorkflowManager wfm) {
        return new WorkflowBuildContextBuilder(wfm);
    }

    /**
     * Builder to create {@link WorkflowBuildContext}-objects.
     */
    public static final class WorkflowBuildContextBuilder {

        private boolean m_includeInteractionInfo = false;

        private final WorkflowManager m_wfm;

        private boolean m_canUndo = false;

        private boolean m_canRedo = false;

        private Supplier<DependentNodeProperties> m_depNodeProps;

        private Supplier<NodeSuccessors> m_nodeSuccessors;

        private WorkflowBuildContextBuilder(final WorkflowManager wfm) {
            m_wfm = wfm;
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
        public WorkflowBuildContextBuilder
            dependentNodeProperties(final Supplier<DependentNodeProperties> depNodeProps) {
            m_depNodeProps = depNodeProps;
            return this;
        }

        /**
         * Provides the already calculated node successors for every node. Only relevant if interaction info is to be
         * included in the resulting workflow entity.
         *
         * If no pre-calculated node successors are provided and interaction info supposed to be included, they will be
         * determined in the {@link #build()}-step.
         *
         * @param nodeSuccessors the pre-calculated node successors
         * @return this builder instance
         */
        public WorkflowBuildContextBuilder nodeSuccessors(final Supplier<NodeSuccessors> nodeSuccessors) {
            m_nodeSuccessors = nodeSuccessors;
            return this;
        }

        /**
         * Builds the workflow context. This might be an operation which is a bit more involved since certain
         * characteristics of the workflow are being determined.
         *
         * {@link #lockWorkflow()} must be called first, before this is called, in order to lock the underlying
         * workflow.
         *
         * @return
         */
        WorkflowBuildContext build() {
            assert m_wfm.isLockedByCurrentThread();
            DependentNodeProperties dnp = null;
            NodeSuccessors ns = null;
            if (m_includeInteractionInfo) {
                dnp = m_depNodeProps == null ? m_wfm.determineDependentNodeProperties() : m_depNodeProps.get();
                ns = m_nodeSuccessors == null ? m_wfm.determineNodeSuccessors() : m_nodeSuccessors.get();
            }
            return new WorkflowBuildContext(this, CoreUtil.isInStreamingMode(m_wfm),
                m_wfm.getProjectComponent().isPresent(), dnp, ns, m_wfm.getNodeContainers().size());
        }

        /**
         * Locks the underlying workflow and returns the lock.
         *
         * @return the workflow lock
         */
        WorkflowLock lockWorkflow() {
            return m_wfm.lock();
        }

    }
}