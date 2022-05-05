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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.context.ModifiableNodeCreationConfiguration;
import org.knime.core.node.exec.ThreadNodeExecutionJobManager;
import org.knime.core.node.port.MetaPortInfo;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.streamable.PartitionInfo;
import org.knime.core.node.workflow.ConnectionContainer;
import org.knime.core.node.workflow.FlowLoopContext;
import org.knime.core.node.workflow.LoopStartNode;
import org.knime.core.node.workflow.NativeNodeContainer;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeContainerParent;
import org.knime.core.node.workflow.NodeContainerState;
import org.knime.core.node.workflow.NodeExecutionJobManager;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.api.webui.util.EntityBuilderUtil;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

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

    /**
     * Cancels and closes the passed workflow manager.
     *
     * @param wfm workflow manager to cancel and close
     * @throws InterruptedException
     */
    public static void cancelAndCloseLoadedWorkflow(final WorkflowManager wfm) throws InterruptedException {
        wfm.getNodeContainers().forEach(wfm::cancelExecution);
        if (wfm.getNodeContainerState().isExecutionInProgress()) {
            wfm.waitWhileInExecution(5, TimeUnit.SECONDS);
        }
        if (wfm.isComponentProjectWFM()) {
            SubNodeContainer snc = (SubNodeContainer)wfm.getDirectNCParent();
            snc.getParent().removeProject(snc.getID());
        } else if (wfm.isProject()) {
            wfm.getParent().removeProject(wfm.getID());
        } else {
            throw new IllegalArgumentException("The passed workflow ('" + wfm.getNameWithID()
                + "' it neither a workflow project nor a component project.");
        }
    }

    /**
     * Utility method to resolve files in the current plugin.
     *
     * @param path a path relative to the plugin's root; must start with "/"
     * @param clazz a class of the plugin the file is contained in
     * @return a file object if the file exists
     * @throws IOException if an I/O error occurs or the file does not exist
     */
    public static File resolveToFile(final String path, final Class<?> clazz) throws IOException {
        URL url = FileLocator.toFileURL(resolveToURL(path, clazz));
        return new File(url.getPath()); // NOSONAR vulnerability, because it's for testing purposes only
    }

    /**
     * Utility method to resolve files in the current plugin.
     *
     * @param path a path relative to the plugin's root; must start with "/"
     * @param clazz a class of the plugin the file is contained in
     * @return a URL to the resource
     * @throws IOException if an I/O error occurs or the file does not exist
     */
    public static URL resolveToURL(final String path, final Class<?> clazz) throws IOException {
        Bundle myself = FrameworkUtil.getBundle(clazz);
        IPath p = new Path(path);
        URL url = FileLocator.find(myself, p, null);
        if (url == null) {
            throw new FileNotFoundException("Path " + path + " does not exist in bundle " + myself.getSymbolicName());
        }
        return url;
    }

    /**
     * @param wfm The workflow manager to search in
     * @return The set of nodes in the given {@code wfm} that satisfy at least one of these conditions:
     * <ul>
     *    <li>The node does not have any incoming connections</li>
     *    <li>All incoming connections are from a metanode inport</li>
     * </ul>
     */
    public static Set<NodeContainer> getSourceNodes(WorkflowManager wfm) {
        return wfm.getNodeContainers().stream().filter(nc -> {
            var incoming = wfm.getIncomingConnectionsFor(nc.getID());
            return incoming.isEmpty() || incoming.stream().allMatch(cc -> cc.getSource().equals(wfm.getID()));
        }).collect(Collectors.toSet());
    }


    /**
     * Obtain the loop context of the given node, if any.
     * @param nnc The node container to get the loop context for.
     * @return An Optional containing the loop context if available, else an empty Optional.
     */
    public static Optional<FlowLoopContext> getLoopContext(final NativeNodeContainer nnc) {
        // Node#getLoopContext does not suffice since this field is only set after the first
        //  loop iteration is completed.
        if (nnc.isModelCompatibleTo(LoopStartNode.class)) {
            // The loop head node produces the FlowLoopContext, hence it is not available on the stack yet.
            return Optional.ofNullable(nnc.getOutgoingFlowObjectStack()).map(stack -> stack.peek(FlowLoopContext.class));
        } else {
            return Optional.ofNullable(nnc.getFlowObjectStack()).map(stack -> stack.peek(FlowLoopContext.class));
        }
    }

    /**
     * Determine whether the given node has a *direct* predecessor that is currently waiting to be executed. Does not
     * check predecessors outside the current workflow (e.g. via connections coming into a metanode).
     * @param id The id of the node to consider the predecessors of.
     * @param wfm the workflow manager containing the node
     * @return {@code true} iff the node has a direct predecessor that is currently waiting to be executed. If the node
     *      is not in the given workflow manager, {@code false} is returned.
     */
    public static boolean hasWaitingPredecessor(final NodeID id, final WorkflowManager wfm) {
        return predecessors(id, wfm).stream()
                .map(wfm::getNodeContainer)
                .map(NodeContainer::getNodeContainerState)
                .anyMatch(NodeContainerState::isWaitingToBeExecuted);
    }

    /**
     * Obtain the direct predecessors of the given node.
     * @param id The node to get the direct predecessors of.
     * @param wfm The containing workflow manager
     * @return The set of nodes that are linked to the given node through connections coming into the given node.
     */
    static Set<NodeID> predecessors(final NodeID id, final WorkflowManager wfm) {
        if (wfm.containsNodeContainer(id)) {
            return wfm.getIncomingConnectionsFor(id).stream().map(ConnectionContainer::getSource)
                    .filter(source -> !source.equals(wfm.getID())).collect(Collectors.toSet());
        } else {
            return Collections.emptySet();
        }
    }

    /**
     * Obtain the direct successors of the given node.
     * @param id The node to get the direct successors of
     * @param wfm The containing workflow manager
     * @return The set of nodes that are linked to the given node through connections outgoing from the given node.
     */
    static Set<NodeID> successors(final NodeID id, final WorkflowManager wfm) {
        if (wfm.containsNodeContainer(id)) {
            return wfm.getOutgoingConnectionsFor(id).stream().map(ConnectionContainer::getDest)
                    .filter(dest -> !dest.equals(wfm.getID())).collect(Collectors.toSet());
        } else {
            return Collections.emptySet();
        }
    }

    /**
     * Obtain the ID of the given port type
     *
     * @param ptype The port type to determine the ID of
     * @return The ID of the given port type.
     */
    public static String getPortTypeId(final PortType ptype) {
        // TODO unify with EntityBuilderUtil#getPortTypeId introduced with NXT-645.
        return ptype.getPortObjectClass().getName();
    }

}
