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
package org.knime.gateway.api.util;

import java.lang.reflect.Method;

import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.exec.ThreadNodeExecutionJobManager;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.streamable.PartitionInfo;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeContainerParent;
import org.knime.core.node.workflow.NodeExecutionJobManager;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.api.webui.util.EntityBuilderUtil;

/**
 * Utility methods for 'core-functionality' (i.e. functionality around {@link WorkflowManager} etc.) required by the
 * gateway logic. Can/should eventually be moved into knime-core.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public final class CoreUtil {

    private static final String STREAMING_JOB_MANAGER_ID =
        "org.knime.core.streaming.SimpleStreamerNodeExecutionJobManagerFactory";

    private CoreUtil() {
        // utility
    }

    /**
     * Determines whether a workflow has the streaming executor set.
     *
     * @param wfm the workflow to check
     * @return <code>true</code> if in streaming mode
     */
    public static boolean isInStreamingMode(final WorkflowManager wfm) {
        NodeContainerParent directNCParent = wfm.getDirectNCParent();
        if (wfm.getDirectNCParent() instanceof SubNodeContainer) {
            NodeContainer nc = (NodeContainer)directNCParent;
            return nc.getJobManager() != null && nc.getJobManager().getID().equals(STREAMING_JOB_MANAGER_ID);
        }
        return false;
    }

    /**
     * Shortcut to determine whether a workflow is the workflow of a component.
     *
     * @param wfm the workflow to test
     * @return <code>true</code> if the argument is a component workflow
     */
    public static boolean isComponentWFM(final WorkflowManager wfm) {
        return wfm.getDirectNCParent() instanceof SubNodeContainer;
    }

    /**
     * Determines for the job manager whether it's the default job manager, i.e. {@link ThreadNodeExecutionJobManager},
     * or <code>null</code> (i.e. not set).
     *
     * @param jobManager the job manager to test
     * @return <code>true</code> if it's the default job manager or null, otherwise <code>false</code>
     */
    public static boolean isDefaultOrNullJobManager(final NodeExecutionJobManager jobManager) {
        return jobManager == null || jobManager instanceof ThreadNodeExecutionJobManager;
    }

    /**
     * Determines whether it's the job manager responsible for streaming execution.
     *
     * @param jobManager the job manager to test
     * @return <code>true</code> if it's the streaming job manager, otherwise <code>false</code>
     */
    public static boolean isStreamingJobManager(final NodeExecutionJobManager jobManager) {
        return jobManager.getID().equals(STREAMING_JOB_MANAGER_ID);
    }

    /**
     * Determines whether a node model implementation is streamable, i.e. implements the streaming API.
     *
     * @param nodeModelClass the node model class to test
     * @return <code>true</code> if the node model class implements the streaming API, otherwise <code>false</code>
     */
    public static boolean isStreamable(final Class<?> nodeModelClass) {
        Method m;
        try {
            m = nodeModelClass.getMethod("createStreamableOperator", PartitionInfo.class, PortObjectSpec[].class);
            return m.getDeclaringClass() != NodeModel.class;
        } catch (NoSuchMethodException | SecurityException ex) {
            NodeLogger.getLogger(EntityBuilderUtil.class)
                .error("Ability to be run in streaming mode couldn't be determined for node " + nodeModelClass, ex);
        }
        return false;
    }

}
