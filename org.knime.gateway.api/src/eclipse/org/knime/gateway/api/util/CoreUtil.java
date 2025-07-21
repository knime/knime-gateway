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
import java.io.StringReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ConfigurableNodeFactory;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.KNIMEConstants;
import org.knime.core.node.Node;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettings;
import org.knime.core.node.config.base.JSONConfig;
import org.knime.core.node.context.ModifiableNodeCreationConfiguration;
import org.knime.core.node.exec.ThreadNodeExecutionJobManager;
import org.knime.core.node.extension.InvalidNodeFactoryExtensionException;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.PortTypeRegistry;
import org.knime.core.node.streamable.PartitionInfo;
import org.knime.core.node.workflow.ConnectionContainer;
import org.knime.core.node.workflow.ConnectionID;
import org.knime.core.node.workflow.ConnectionUIInformation;
import org.knime.core.node.workflow.FileNativeNodeContainerPersistor;
import org.knime.core.node.workflow.FlowLoopContext;
import org.knime.core.node.workflow.LoopStartNode;
import org.knime.core.node.workflow.MetaNodeTemplateInformation;
import org.knime.core.node.workflow.NativeNodeContainer;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeContainerMetadata;
import org.knime.core.node.workflow.NodeContainerParent;
import org.knime.core.node.workflow.NodeContainerState;
import org.knime.core.node.workflow.NodeExecutionJobManager;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.NodeStateChangeListener;
import org.knime.core.node.workflow.NodeStateEvent;
import org.knime.core.node.workflow.NodeUIInformation;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.node.workflow.WorkflowAnnotation;
import org.knime.core.node.workflow.WorkflowAnnotationID;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.util.FileUtil;
import org.knime.core.util.Pair;
import org.knime.gateway.api.webui.entity.TypedTextEnt;
import org.knime.gateway.api.webui.util.WorkflowEntityFactory;
import org.knime.shared.workflow.def.AnnotationDataDef;
import org.osgi.framework.FrameworkUtil;

/**
 * Utility methods for 'core-functionality' (i.e. functionality around {@link WorkflowManager} etc.) required by the
 * gateway logic. Can/should eventually be moved into knime-core.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @author Kai Franze, KNIME GmbH
 */
public final class CoreUtil {

    private static final String STREAMING_JOB_MANAGER_ID =
        "org.knime.core.streaming.SimpleStreamerNodeExecutionJobManagerFactory";

    private CoreUtil() {
        // utility
    }

    /**
     * Creates and adds a node to a workflow.
     *
     * @param factoryClassName the node's fully qualified factory class name
     * @param factorySettings optional factory settings in case of dynamic nodes, otherwise <code>null</code>
     * @param x the x-coordinate to add the node at
     * @param y the y-coordinate to add the node at
     * @param wfm the workflow to add the node to
     * @param centerNode if {@code true} the node is centered at the given coordinates, otherwise the coordinates refer
     *            to the upper left corner of the node 'body'
     * @return the id of the new node
     * @throws NoSuchElementException if no node couldn't be found for the given factory class name
     * @throws IOException if a problem occurred while reading in the factory settings
     */
    public static NodeID createAndAddNode(final String factoryClassName, final String factorySettings, final Integer x,
        final Integer y, final WorkflowManager wfm, final boolean centerNode) throws IOException {
        return createAndAddNode(factoryClassName, factorySettings, null, x, y, wfm, centerNode);
    }

    /**
     * Creates and adds a node to a workflow.
     *
     * @param factoryClassName the node's fully qualified factory class name
     * @param factorySettings optional factory settings in case of dynamic nodes, otherwise <code>null</code>
     * @param url optional parameter for the case when a node is to be added which can be configured with an initial URL
     *            (e.g. representing a pre-configured file)
     * @param x the x-coordinate to add the node at
     * @param y the y-coordinate to add the node at
     * @param wfm the workflow to add the node to
     * @param centerNode if {@code true} the node is centered at the given coordinates, otherwise the coordinates refer
     *            to the upper left corner of the node 'body'
     * @return the id of the new node
     * @throws NoSuchElementException if no node couldn't be found for the given factory class name
     * @throws IOException if a problem occurred while reading in the factory settings
     */
    public static NodeID createAndAddNode(final String factoryClassName, final String factorySettings, final URL url,
        final Integer x, final Integer y, final WorkflowManager wfm, final boolean centerNode) throws IOException {
        var nodeFactory = getNodeFactory(factoryClassName, factorySettings);
        NodeID nodeID;
        if (nodeFactory instanceof ConfigurableNodeFactory configurableNodeFactory && url != null) {
            final ModifiableNodeCreationConfiguration config =
                configurableNodeFactory.createNodeCreationConfig();
            config.setURLConfiguration(url);
            nodeID = wfm.addNodeAndApplyContext(configurableNodeFactory, config, -1);
        } else {
            nodeID = wfm.createAndAddNode(nodeFactory);
        }
        NodeUIInformation info =
            NodeUIInformation.builder().setNodeLocation(x, y, -1, -1).setIsDropLocation(centerNode).build();
        wfm.getNodeContainer(nodeID).setUIInformation(info);
        return nodeID;
    }

    /**
     * Obtain a {@link NodeFactory} instance for a node identified by its class name and settings string.
     *
     * @param factoryClassName The class name of the node factory (e.g. `org.knime.[...].MyNodeFactory`)
     * @param factorySettings Additional string to identify node, e.g. for dynamic JS nodes. May be null.
     * @return A {@link NodeFactory} instance.
     * @throws IOException If node factory settings could not be read.
     * @throws NoSuchElementException If no node is found for this factory class name.
     */
    public static NodeFactory<NodeModel> getNodeFactory(final String factoryClassName, final String factorySettings)
        throws IOException, NoSuchElementException {
        NodeFactory<NodeModel> nodeFactory;
        try {
            // TODO use NodeFactoryProvider instead?
            nodeFactory = FileNativeNodeContainerPersistor.loadNodeFactory(factoryClassName);
        } catch (InstantiationException | IllegalAccessException | InvalidNodeFactoryExtensionException
                | InvalidSettingsException ex) {
            var message = "No node found for factory key " + factoryClassName;
            NodeLogger.getLogger(CoreUtil.class).warn(message, ex);
            throw new NoSuchElementException(message);
        }
        if (factorySettings != null && !factorySettings.isEmpty()) {
            try {
                NodeSettings settings =
                    JSONConfig.readJSON(new NodeSettings("settings"), new StringReader(factorySettings));
                nodeFactory.loadAdditionalFactorySettings(settings);
            } catch (IOException | InvalidSettingsException ex) {
                throw new IOException(
                    "Problem reading factory settings while trying to create node from '" + factoryClassName + "'", ex);
            }
        } else if (nodeFactory.isLazilyInitialized()) {
            //no settings stored with a dynamic node factory (which is the, e.g., with the spark nodes)
            //at least init the node factory in order to have the node description available
            nodeFactory.init();
        } else {
            //
        }
        return nodeFactory;
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
            var nc = (NodeContainer)directNCParent;
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
     * @param wfm
     * @return Whether the given workflow corresponds to a metanode.
     */
    public static boolean isMetanodeWFM(final WorkflowManager wfm) {
        return CoreUtil.getContainerType(wfm) //
            .map(type -> type == CoreUtil.ContainerType.METANODE) //
            .orElse(false);
    }

    /**
     * @param wfm A {@link WorkflowManager} instance
     * @return The parenting {@link SubNodeContainer} of a component {@link WorkflowManager} or an empty optional if the
     *         workflow manager does not correspond to a component
     */
    public static Optional<SubNodeContainer> getComponentSNC(final WorkflowManager wfm) {
        return Optional.ofNullable(wfm.getDirectNCParent())//
            .filter(SubNodeContainer.class::isInstance)//
            .map(SubNodeContainer.class::cast);
    }

    /**
     * Determines the project workflow that the given {@code NodeContainer} is part of. This can be either a workflow
     * project or a component project.
     *
     * @param nc A {@code NodeContainer} corresponding a node, metanode, or component
     * @return the project workflow
     */
    public static WorkflowManager getProjectWorkflow(final NodeContainer nc) {
        var wfm = nc instanceof WorkflowManager w ? w : nc.getParent();
        return wfm.getProjectComponent().map(SubNodeContainer::getWorkflowManager).orElse(wfm.getProjectWFM());
    }

    /**
     * Determines the ID of the project workflow that the given {@code NodeContainer}. This can be either a workflow
     * project or a component project.
     *
     * @param nc A {@code NodeContainer} corresponding a node, metanode, or component
     * @return the project workflow id
     */
    public static NodeID getProjectWorkflowNodeID(final NodeContainer nc) {
        return getProjectWorkflow(nc).getID();
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
            NodeLogger.getLogger(WorkflowEntityFactory.class)
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
        if (wfm.getNodeContainerState() != null && wfm.getNodeContainerState().isExecutionInProgress()) {
            CoreUtil.cancel(wfm);
            wfm.waitWhileInExecution(5, TimeUnit.SECONDS);
        }
        if (wfm.isComponentProjectWFM()) {
            var snc = (SubNodeContainer)wfm.getDirectNCParent();
            snc.getParent().removeProject(snc.getID());
        } else if (wfm.isProject()) {
            var parent = wfm.getParent();
            if (parent != null && parent.containsNodeContainer(wfm.getID())) {
                parent.removeProject(wfm.getID());
            }
        }
    }

    /**
     * Cancel execution of the given workflow manager.
     *
     * @param wfm The workflow manager to cancel
     * @implNote This implementation affects the given workflow manager via its parent, which is required for safely
     *           handling components.
     */
    public static void cancel(final WorkflowManager wfm) {
        if (isComponentProjectWorkflow(wfm)) {
            // special handling since parent.cancelExecution doesn't work as expected for component projects
            wfm.cancelExecution();
        } else {
            performActionOnWorkflow(wfm, WorkflowManager::cancelExecution);
        }
    }

    /**
     * Execute the given workflow manager.
     *
     * @param wfm The workflow manager to execute
     * @implNote This implementation affects the given workflow manager via its parent, which is required for safely
     *           handling components.
     */
    public static void execute(final WorkflowManager wfm) {
        if (isComponentProjectWorkflow(wfm)) {
            // special handling since parent.executeUpToHere doesn't work as expected for component projects
            wfm.executeAll();
        } else {
            performActionOnWorkflow(wfm, (parent, nc) -> parent.executeUpToHere(nc.getID()));
        }
    }

    private static boolean isComponentProjectWorkflow(final WorkflowManager wfm) {
        return wfm.getDirectNCParent() instanceof SubNodeContainer snc && snc.isProject();
    }

    /**
     * Apply an action on the parent of {@code wfm} via the grandparent workflow manager, potentially having side
     * effects on the child workflow manager such as e.g. executing or cancelling all of its nodes.
     *
     * @param wfm The child workflow manager
     * @param action The action to apply on the parent of the child
     */
    private static void performActionOnWorkflow(final WorkflowManager wfm,
        final BiConsumer<WorkflowManager, NodeContainer> action) {
        var directNCParent = wfm.getDirectNCParent();
        if (directNCParent instanceof SubNodeContainer snc) {
            action.accept(snc.getParent(), snc);
        } else {
            action.accept(wfm.getParent(), wfm);
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
        return FileUtil.getFileFromURL(FileLocator.toFileURL(resolveToURL(path, clazz)));
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
        var myself = FrameworkUtil.getBundle(clazz);
        IPath p = new Path(path);
        var url = FileLocator.find(myself, p, null);
        if (url == null) {
            throw new FileNotFoundException("Path " + path + " does not exist in bundle " + myself.getSymbolicName());
        }
        return url;
    }

    /**
     * @param wfm The workflow manager to search in
     * @return The set of nodes in the given {@code wfm} that satisfy at least one of these conditions:
     *         <ul>
     *         <li>The node does not have any incoming connections</li>
     *         <li>All incoming connections are from a metanode inport</li>
     *         </ul>
     */
    public static Set<NodeContainer> getSourceNodes(final WorkflowManager wfm) {
        return wfm.getNodeContainers().stream().filter(nc -> {
            var incoming = wfm.getIncomingConnectionsFor(nc.getID());
            return incoming.isEmpty() || incoming.stream().allMatch(cc -> cc.getSource().equals(wfm.getID()));
        }).collect(Collectors.toSet());
    }

    /**
     * Obtain the loop context of the given node, if any.
     *
     * @param nnc The node container to get the loop context for.
     * @return An Optional containing the loop context if available, else an empty Optional.
     */
    public static Optional<FlowLoopContext> getLoopContext(final NativeNodeContainer nnc) {
        // Node#getLoopContext does not suffice since this field is only set after the first
        //  loop iteration is completed.
        if (nnc.isModelCompatibleTo(LoopStartNode.class)) {
            // The loop head node produces the FlowLoopContext, hence it is not available on the stack yet.
            return Optional.ofNullable(nnc.getOutgoingFlowObjectStack())
                .map(stack -> stack.peek(FlowLoopContext.class));
        } else {
            return Optional.ofNullable(nnc.getFlowObjectStack()).map(stack -> stack.peek(FlowLoopContext.class));
        }
    }

    /**
     * Determine whether the given node has a *direct* predecessor that is currently waiting to be executed. Does not
     * check predecessors outside the current workflow (e.g. via connections coming into a metanode).
     *
     * @param id The id of the node to consider the predecessors of.
     * @param wfm the workflow manager containing the node
     * @return {@code true} iff the node has a direct predecessor that is currently waiting to be executed. If the node
     *         is not in the given workflow manager, {@code false} is returned.
     */
    public static boolean hasWaitingPredecessor(final NodeID id, final WorkflowManager wfm) {
        return predecessors(id, wfm).stream().map(wfm::getNodeContainer).map(NodeContainer::getNodeContainerState)
            .anyMatch(NodeContainerState::isWaitingToBeExecuted);
    }

    /**
     * Obtain the direct predecessors of the given node.
     *
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
     *
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
     * Get the port type based on a port type ID
     *
     * @param ptypeId The ID of the port type to obtain
     * @return An Optional containing the Port Type, or an empty optional if the port type could not be determined.
     */
    public static Optional<PortType> getPortType(final String ptypeId) {
        var portTypeRegistry = PortTypeRegistry.getInstance();
        return portTypeRegistry.getObjectClass(ptypeId).map(portTypeRegistry::getPortType);
    }

    /**
     * Get the port type ID of a given port type object
     *
     * @param ptype The port type
     * @return The ID of the given port type
     */
    public static String getPortTypeId(final PortType ptype) {
        return ptype.getPortObjectClass().getName();
    }

    /**
     * Checks whether a source port type and a destination port type are compatible. If true, those two ports could be
     * connected on the workflow.
     *
     * @param sourceType The port type of the source port
     * @param destType The port type of the destination port
     * @return True if compatible, false otherwise
     */
    public static boolean arePortTypesCompatible(final PortType sourceType, final PortType destType) {
        // copied from workflow manager canAddConnection
        Class<? extends PortObject> sourceCl = sourceType.getPortObjectClass();
        Class<? extends PortObject> destCl = destType.getPortObjectClass();
        if (BufferedDataTable.class.equals(sourceCl) ^ BufferedDataTable.class.equals(destCl)) {
            // BufferedDataTable only connects to BufferedDataTable
            return false;
        } else {
            return destCl.isAssignableFrom(sourceCl) || sourceCl.isAssignableFrom(destCl);
        }
    }

    /**
     * Find a workflow annotation with given id in the given workflow manager.
     *
     * @param id The workflow annotation to look for.
     * @param wfm The workflow manager to search in.
     * @return The workflow annotation object corresponding to the given ID, or an empty optional if not available.
     */
    public static Optional<WorkflowAnnotation> getAnnotation(final WorkflowAnnotationID id, final WorkflowManager wfm) {
        var annos = wfm.getWorkflowAnnotations(id);
        if (annos.length == 0 || annos[0] == null) {
            return Optional.empty();
        } else {
            return Optional.of(annos[0]);
        }
    }

    /**
     * @param id The identifier of the queried connection
     * @param wfm The workflow manager to look in
     * @return The connection identified by the given ID, or an empty optional.
     */
    public static Optional<ConnectionContainer> getConnection(final ConnectionID id, final WorkflowManager wfm) {
        try {
            return Optional.of(wfm.getConnection(id));
        } catch (IllegalArgumentException e) { // NOSONAR
            return Optional.empty();
        }
    }

    /**
     * Find a node with given id in the given workflow manager.
     *
     * @param id The node to query
     * @param wfm The workflow manager to search in
     * @return The node container corresponding to the given ID, or an empty optional if not available.
     */
    public static Optional<NodeContainer> getNodeContainer(final NodeID id, final WorkflowManager wfm) {
        try {
            var nc = wfm.getNodeContainer(id);
            return Optional.of(nc);
        } catch (IllegalArgumentException e) { // NOSONAR
            return Optional.empty();
        }
    }

    /**
     * @param connection The connection to examine
     * @return {@code true} iff the given connection has any bendpoints.
     */
    public static boolean hasBendpoints(final ConnectionContainer connection) {
        return connection.getUIInfo() != null && connection.getUIInfo().getAllBendpoints().length > 0;
    }

    /**
     * Translate specified bendpoints on the workflow canvas by {@code delta}.
     *
     * @param connection The connection on which the bendpoints are located.
     * @param bendpointIndices Indices identifying the bendpoints to move
     * @param delta The translation shift. First component is X-coordinate, second is Y-coordinate.
     */
    public static void translateSomeBendpoints(final ConnectionContainer connection,
        final List<Integer> bendpointIndices, final int[] delta) {
        var indices = bendpointIndices.stream().mapToInt(i -> i).toArray();
        editConnectionUIInformation(connection, b -> b.translate(delta, indices));
    }

    /**
     * Translate all bendpoints by {@code delta}
     *
     * @param connection The connection on which the bendpoints are located.
     * @param delta The translation shift. First component is X-coordinate, second is Y-coordinate.
     */
    public static void translateAllBendpoints(final ConnectionContainer connection, final int[] delta) {
        editConnectionUIInformation(connection, b -> b.translate(delta));
    }

    /**
     * Insert a bendpoint.
     *
     * @param connection The connection to insert the bendpoint on.
     * @param index The index at which to insert the bendpoint.
     * @param xPos The X-Position of the bendpoint on the canvas.
     * @param yPos The Y-Position of the workflow on the canvas.
     */
    public static void insertBendpoint(final ConnectionContainer connection, final int index, final int xPos,
        final int yPos) {
        editConnectionUIInformation(connection, b -> b.addBendpoint(xPos, yPos, index));
    }

    /**
     * Remove a bendpoint.
     *
     * @param connection The connection from which to remove a bendpoint.
     * @param indices The indices of the bendpoints to remove
     */
    public static void removeBendpoints(final ConnectionContainer connection, final int... indices) {
        editConnectionUIInformation(connection, b -> {
            var sortedIndices = indices.clone();
            Arrays.sort(sortedIndices);
            for (var i = 0; i < sortedIndices.length; i++) {
                b.removeBendpoint(sortedIndices[i] - i);
            }
        });
    }

    private static void editConnectionUIInformation(final ConnectionContainer connection,
        final Consumer<ConnectionUIInformation.Builder> transformation) {
        var builder = connection.getUIInfo() == null ? ConnectionUIInformation.builder()
            : ConnectionUIInformation.builder().copyFrom(connection.getUIInfo());
        transformation.accept(builder);
        // Dispatch the workflow change, and consequently the notification of any event listeners
        // to a separate thread. Event listeners might in turn hold or require locks, and they should not be able to
        // block the calling thread, potentially causing a deadlock.
        KNIMEConstants.GLOBAL_THREAD_POOL.enqueue(() -> //
        connection.setUIInfo(builder.build()) // will notify event listeners
        );
    }

    /**
     *
     * @param nodes
     * @param wfm
     * @return The set of all connections between nodes in the given set
     */
    public static Set<ConnectionContainer> inducedConnections(final Set<NodeContainer> nodes,
        final WorkflowManager wfm) {
        var nodeIds = nodes.stream().map(NodeContainer::getID).collect(Collectors.toSet());
        return nodes.stream() //
            .flatMap(pastedNode -> wfm.getOutgoingConnectionsFor(pastedNode.getID()).stream()) //
            .filter(connectionStartingInSet -> nodeIds.contains(connectionStartingInSet.getDest()))
            .collect(Collectors.toSet());
    }

    /**
     * @param connections
     * @param wfm
     * @return whether all the passed connections can be added
     */
    public static boolean canAddConnections(final Collection<ConnectionContainer> connections,
        final WorkflowManager wfm) {
        return connections.stream()
            .allMatch(cc -> wfm.canAddConnection(cc.getSource(), cc.getSourcePort(), cc.getDest(), cc.getDestPort()));
    }

    /**
     * @param wfm
     * @param connections
     * @return whether all of the passed connections can be removed
     */
    public static boolean canRemoveConnections(final Collection<ConnectionContainer> connections,
        final WorkflowManager wfm) {
        return connections.stream().allMatch(wfm::canRemoveConnection);
    }

    /**
     * @param nc The node to determine the locked status of
     * @return Whether the given node is locked, or an empty Optional if not applicable
     */
    public static Optional<Boolean> isLocked(final NodeContainer nc) {
        WorkflowManager wfm = null;
        if (nc instanceof WorkflowManager wm) {
            wfm = wm;
        } else if (nc instanceof SubNodeContainer snc) {
            wfm = snc.getWorkflowManager();
        }
        if (wfm == null || !wfm.isEncrypted()) {
            return Optional.empty();
        }
        return Optional.of(!wfm.isUnlocked());
    }

    /**
     * @param nc The node to inspect.
     * @return Whether this node is linked. {@code false} if not applicable.
     */
    public static boolean isLinked(final NodeContainer nc) {
        MetaNodeTemplateInformation templateInfo = null;
        if (nc instanceof WorkflowManager metanodeWfm) {
            templateInfo = metanodeWfm.getTemplateInformation();
        }
        if (nc instanceof SubNodeContainer snc) {
            templateInfo = snc.getTemplateInformation();
        }
        return templateInfo != null && templateInfo.getRole() == MetaNodeTemplateInformation.Role.Link;
    }

    /**
     * @param wfm
     * @return the parent of this workflow manager, handling metanode and component child workflow managers
     */
    public static WorkflowManager getWorkflowParent(final WorkflowManager wfm) {
        var parent = wfm.getDirectNCParent();
        return (parent instanceof SubNodeContainer snc) ? snc.getParent() : (WorkflowManager)parent;
    }

    /**
     * If {@code wfm} is not a metanode, return it. Otherwise, recurse parents until a non-metanode parent is
     * encountered.
     *
     * @param wfm
     * @return the workflow manager instance or an empty optiional if wfm is the project root
     * @throws IllegalArgumentException if the given workflow manager is not part a (or part of a) project workflow
     *             manager
     */
    public static WorkflowManager getNonMetanodeSelfOrParent(final WorkflowManager wfm) {
        if (wfm.getID().isRoot()) {
            throw new IllegalArgumentException("Not a (or part of a) project workflow manager");
        }
        if (isMetanodeWFM(wfm)) {
            return getNonMetanodeSelfOrParent(getWorkflowParent(wfm));
        }
        return wfm;
    }

    /**
     *
     * If the node is already executed, run the given task. If the node is not already executed, execute the workflow up
     * to the node and attach a listener to run the given task once executed. Do nothing if the node is not active.
     *
     * @param nc -
     * @param task -
     * @since 5.6
     */
    public static void executeThenRun(final NodeContainer nc, final Runnable task) {
        if (nc.isInactive()) {
            return;
        }
        if (nc.getNodeContainerState().isExecuted()) {
            task.run();
            return;
        }
        nc.addNodeStateChangeListener(new NodeStateChangeListener() {
            @Override
            public void stateChanged(final NodeStateEvent event) {
                var state = nc.getNodeContainerState();
                if (event.getSource().equals(nc.getID()) && state.isExecuted()) {
                    task.run();
                }
                if (!state.isExecutionInProgress()) {
                    nc.removeNodeStateChangeListener(this);
                }
            }
        });
        nc.getParent().executeUpToHere(nc.getID());
    }

    /**
     * The concrete kind of a container node. We assume a 1-to-1 mapping from a {@link ContainerType} to a subclass of
     * {@link NodeContainer} that implements the node's container behaviour.
     *
     * @see CoreUtil#getContainerType(NodeID, WorkflowManager)
     */
    @SuppressWarnings("javadoc")
    public enum ContainerType {

            METANODE("Metanode"),

            COMPONENT("Component");

        private final String m_label;

        ContainerType(final String label) {
            m_label = label;
        }

        public String getLabel() {
            return m_label;
        }

    }

    /**
     * Obtain the {@link ContainerType} of a container node.
     *
     * @param nodeId the node id to determine the container type for
     * @param wfm The parent workflow manager
     * @return an Optional containing the container type of the node, or an empty Optional if the node could not be
     *         found in the workflow or is not a container node.
     */
    public static Optional<ContainerType> getContainerType(final NodeID nodeId, final WorkflowManager wfm) {
        return getNodeContainer(nodeId, wfm).flatMap(CoreUtil::getContainerType);
    }

    /**
     * @param wfm
     * @return the container type of the workflow manager. Empty if the workflow manager is the root wfm.
     */
    public static Optional<ContainerType> getContainerType(final WorkflowManager wfm) {
        if (wfm.isProject()) {
            return Optional.empty();
        }
        if (isComponentWFM(wfm)) {
            return Optional.of(ContainerType.COMPONENT);
        }
        if (wfm.getParent().getID().isRoot()) {
            // this is a top-level workflow and not a component project
            return Optional.empty();
        }
        return getContainerType(wfm.getID(), wfm.getParent());
    }

    /**
     * Obtain the {@link ContainerType} of a container node.
     *
     * @param nc The node container
     * @return an Optional containing the container type of the node, or an empty Optional if the node is not a
     *         container node.
     */
    public static Optional<ContainerType> getContainerType(final NodeContainer nc) {
        if (nc instanceof WorkflowManager) {
            return Optional.of(ContainerType.METANODE);
        } else if (nc instanceof SubNodeContainer) {
            return Optional.of(ContainerType.COMPONENT);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Obtain the workflow manager that is encapsulated by a container node.
     *
     * @param nodeId The ID of the node to obtain the workflow manager for.
     * @param wfm The parent workflow manager
     * @return The workflow manager instance that is encapsulated by given container node
     */
    public static Optional<WorkflowManager> getContainedWfm(final NodeID nodeId, final WorkflowManager wfm) {
        return getContainerType(nodeId, wfm).map(type -> switch (type) {
            case METANODE -> wfm.getNodeContainer(nodeId, WorkflowManager.class, false);
            case COMPONENT -> wfm.getNodeContainer(nodeId, SubNodeContainer.class, false).getWorkflowManager();
        });
    }

    /**
     * Gets the container type and the workflow manager for components and metanodes.
     *
     * @param nc The node container
     * @return The optional container type and workflow manager, empty if not a component or metanode
     */
    public static Optional<Pair<ContainerType, WorkflowManager>> getTypeAndContainedWfm(final NodeContainer nc) {
        return getContainerType(nc).map(type -> switch (type) {
            case METANODE -> Pair.create(type, (WorkflowManager)nc);
            case COMPONENT -> Pair.create(type, ((SubNodeContainer)nc).getWorkflowManager());
        });
    }

    /**
     * Creates a {@link Node}-instance from the given factory
     *
     * @param factory
     * @return the instance or an empty optional if the node instance couldn't be created (e.g. because
     *         {@link NodeFactory#createNodeModel()} failed unexpectedly)
     */
    public static Optional<Node> createNode(final NodeFactory<? extends NodeModel> factory) {
        try {
            return Optional.of(new Node((NodeFactory<NodeModel>)factory));
        } catch (Throwable e) { // NOSONAR
            NodeLogger.getLogger(CoreUtil.class)
                .error("Could not create instance of node " + factory.getClass().getName() + ": " + e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * If the node has dynamic ports, retrieve its creation configuration
     *
     * @param wfm The workflow manager
     * @param nodeId The node ID
     * @return The optional creation configuration
     */
    public static Optional<ModifiableNodeCreationConfiguration> getCopyOfCreationConfig(final WorkflowManager wfm,
        final NodeID nodeId) {
        var nnc = wfm.getNodeContainer(nodeId, NativeNodeContainer.class, true);
        return nnc.getNode().getCopyOfCreationConfig();
    }

    /**
     * If the factory belongs to a node with dynamic ports, retrieve its creation configuration
     *
     * @param factory The node factory
     * @return The optional creation configuration
     */
    public static Optional<ModifiableNodeCreationConfiguration>
        getCopyOfCreationConfig(final NodeFactory<? extends NodeModel> factory) {
        if (factory instanceof ConfigurableNodeFactory) {
            var creationConfig = ((ConfigurableNodeFactory<? extends NodeModel>)factory).createNodeCreationConfig();
            return Optional.of(creationConfig);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Get all linked components (recursively) and their node container state for a given workflow
     *
     * @param wfm
     * @return A map of all the linked component IDs and their node container state, never {@code null}
     */
    public static Map<NodeID, NodeContainerState> getLinkedComponentToStateMap(final WorkflowManager wfm) {
        final var links = wfm.getLinkedMetaNodes(true);
        return links.stream() //
            .map(wfm::findNodeContainer) //
            .filter(SubNodeContainer.class::isInstance) // Only count linked sub node containers
            .collect(Collectors.toMap(NodeContainer::getID, NodeContainer::getNodeContainerState));
    }

    /**
     * Check if the given workflow is in a dirty state or if it has a parent that is dirty
     *
     * @param wfm
     * @return True if its dirty or it has a parent that is dirty, false otherwise
     */
    public static boolean isWorkflowDirtyOrHasDirtyParent(final WorkflowManager wfm) {
        var isDirty = wfm.isDirty();
        if (!isDirty) {
            if (wfm.isProject()) {
                return false;
            }
            var ncParent = wfm.getDirectNCParent();
            WorkflowManager parentWfm;
            if (ncParent instanceof SubNodeContainer snc) {
                if (snc.isProject()) {
                    return false;
                } else {
                    parentWfm = snc.getParent();
                }
            } else {
                parentWfm = wfm.getParent();
            }
            return isWorkflowDirtyOrHasDirtyParent(parentWfm);
        }
        return true;
    }

    /**
     * Traverse nodes in a given workflow manager, potentially recursing into child workflows.
     *
     * @param wfm The workflow manager to start traversing in.
     * @param nodeVisitor Predicate return value determines whether to recurse into the node (if possible). Side effects
     *            may consume the currently visited node otherwise.
     */
    public static void iterateNodes(final WorkflowManager wfm, final Predicate<NodeContainer> nodeVisitor) {
        for (var nc : wfm.getNodeContainers()) {
            var doRecurse = nodeVisitor.test(nc);
            if (doRecurse) {
                getChildWfm(nc).ifPresent(childWfm -> iterateNodes(childWfm, nodeVisitor));
            }
        }
    }

    private static Optional<WorkflowManager> getChildWfm(final NodeContainer nc) {
        if (nc instanceof NativeNodeContainer) {
            return Optional.empty();
        }
        if (nc instanceof WorkflowManager metanodeWmf) {
            return Optional.of(metanodeWmf);
        }
        if (nc instanceof SubNodeContainer snc) {
            return Optional.of(snc.getWorkflowManager());
        }
        return Optional.empty();
    }

    /**
     * Helper to run logic on a node or component/metanode-workflow respectively.
     *
     * @param nc the node to check
     * @param ncConsumer logic to be run on a node
     * @param wfmConsumer logic to be run on a metanode/component workflow
     */
    public static void runOnNodeOrWfm(final NodeContainer nc, final Consumer<NodeContainer> ncConsumer,
        final Consumer<WorkflowManager> wfmConsumer) {
        if ((ncConsumer != null)) {
            ncConsumer.accept(nc);
        }
        if (wfmConsumer != null) {
            if (nc instanceof WorkflowManager wfm) {
                wfmConsumer.accept(wfm);
            } else if (nc instanceof SubNodeContainer snc) {
                wfmConsumer.accept(snc.getWorkflowManager());
            }
        }
    }

    /**
     * Helper class to convert {@link TypedTextEnt.ContentTypeEnum}s to the corresponding `knime-core` classes.
     */
    public static final class ContentTypeConverter {

        private ContentTypeConverter() {
            // Utility class
        }

        public static NodeContainerMetadata.ContentType toNodeContainerMetadata(//
            final TypedTextEnt.ContentTypeEnum contentType) {
            return switch (contentType) {
                case PLAIN -> NodeContainerMetadata.ContentType.PLAIN;
                case HTML -> NodeContainerMetadata.ContentType.HTML;
            };
        }

        static TypedTextEnt.ContentTypeEnum fromNodeContainerMetadata(//
            final NodeContainerMetadata.ContentType contentType) {
            return switch (contentType) {
                case PLAIN -> TypedTextEnt.ContentTypeEnum.PLAIN;
                case HTML -> TypedTextEnt.ContentTypeEnum.HTML;
            };
        }

        static AnnotationDataDef.ContentTypeEnum toAnnotationDataDef(//
            final TypedTextEnt.ContentTypeEnum contentType) {
            return switch (contentType) {
                case PLAIN -> AnnotationDataDef.ContentTypeEnum.PLAIN;
                case HTML -> AnnotationDataDef.ContentTypeEnum.HTML;
            };

        }

        static TypedTextEnt.ContentTypeEnum fromAnnotationDataDef(//
            final AnnotationDataDef.ContentTypeEnum contentType) {
            return switch (contentType) {
                case PLAIN -> TypedTextEnt.ContentTypeEnum.PLAIN;
                case HTML -> TypedTextEnt.ContentTypeEnum.HTML;
            };
        }

    }

    /**
     * Map a total port index (i.e. counting over all ports of the node) to the index within its port group.
     * <p>
     * For example, consider port groups [[p0, p1, p2], [p3, p4, p5]]. Then getPortIndexWithinGroup(4) = 1
     *
     * @param portIndexToPortGroupMap -
     * @param totalPortIndex index over all ports on this side, including implicit flow variable port
     * @return The index of that port within its port group
     */
    public static int getPortIndexWithinGroup(final String[] portIndexToPortGroupMap, final int totalPortIndex) {
        var portGroupName = portIndexToPortGroupMap[totalPortIndex - 1];
        var portIndexWithinGroup = 0;
        var previousPortGroupName = portGroupName;
        while (totalPortIndex - 1 - portIndexWithinGroup > 0 && portGroupName.equals(previousPortGroupName)) {
            previousPortGroupName = portIndexToPortGroupMap[totalPortIndex - 2 - portIndexWithinGroup];
            if (previousPortGroupName.equals(portGroupName)) {
                portIndexWithinGroup++;
            }
        }
        return portIndexWithinGroup;
    }

}
