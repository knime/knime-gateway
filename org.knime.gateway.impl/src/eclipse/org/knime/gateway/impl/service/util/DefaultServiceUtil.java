/* ------------------------------------------------------------------
 * This source code, its documentation and all appendant files
 * are protected by copyright law. All rights reserved.
 *
 * Copyright by KNIME AG, Zurich, Switzerland
 *
 * You may not modify, publish, transmit, transfer or sell, reproduce,
 * create derivative works from, distribute, perform, display, or in
 * any way exploit any of the content, in whole or in part, except as
 * otherwise expressly permitted in writing by the copyright owner or
 * as specified in the license file distributed with this product.
 *
 * If you have any questions please contact the copyright holder:
 * website: www.knime.com
 * email: contact@knime.com
 * ---------------------------------------------------------------------
 *
 */
package org.knime.gateway.impl.service.util;

import java.util.NoSuchElementException;
import java.util.UUID;

import org.knime.core.node.workflow.ConnectionID;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.node.workflow.WorkflowAnnotationID;
import org.knime.core.node.workflow.WorkflowLock;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.util.Pair;
import org.knime.gateway.api.entity.AnnotationIDEnt;
import org.knime.gateway.api.entity.ConnectionIDEnt;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.service.util.ServiceExceptions;
import org.knime.gateway.api.service.util.ServiceExceptions.NodeNotFoundException;
import org.knime.gateway.api.service.util.ServiceExceptions.NotASubWorkflowException;
import org.knime.gateway.impl.project.WorkflowProject;
import org.knime.gateway.impl.project.WorkflowProjectManager;

/**
 * Helper methods useful for the default service implementations.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class DefaultServiceUtil {

    private DefaultServiceUtil() {
        //utility class
    }

    /**
     * Gets the node container (including (sub-)workflows) for the id-pair of root workflow- and node ID.
     *
     * @param rootWorkflowID id of the root workflow
     * @param nodeID the node id to get the node/workflow for - if {@link NodeIDEnt#getRootID()} the root workflow
     *            itself will be returned
     * @return the {@link NodeContainer} instance
     * @throws NodeNotFoundException if there is no node for the given node id
     * @throws NoSuchElementException if there is no root workflow for the given root workflow id
     */
    public static NodeContainer getNodeContainer(final UUID rootWorkflowID, final NodeIDEnt nodeID)
        throws NodeNotFoundException {
        WorkflowManager wfm = getRootWorkflowManager(rootWorkflowID);
        if (nodeID.equals(NodeIDEnt.getRootID())) {
            return wfm;
        } else {
            try {
                return wfm.findNodeContainer(nodeID.toNodeID(wfm.getID()));
            } catch (IllegalArgumentException e) {
                throw new ServiceExceptions.NodeNotFoundException(e.getMessage());
            }
        }
    }

    /**
     * Gets the workflow manager from the {@link WorkflowProjectManager} for a corresponding root workflow id.
     *
     * @param rootWorkflowID the id to get the wfm for
     * @return the {@link WorkflowManager} instance
     * @throws NoSuchElementException if there is no workflow manager for the id registered
     */
    public static WorkflowManager getRootWorkflowManager(final UUID rootWorkflowID) throws NoSuchElementException {
        return WorkflowProjectManager.openAndCacheWorkflow(rootWorkflowID).orElseThrow(
            () -> new NoSuchElementException("Workflow project for ID \"" + rootWorkflowID + "\" not found."));
    }

    /**
     * Gets the {@link WorkflowProject} from the {@link WorkflowProjectManager} for a corresponding workflow project id.
     *
     * @param workflowProjectID the id to get the project for
     * @return the {@link WorkflowProject} instance
     * @throws NoSuchElementException if there is no workflow project for the id registered
     */
    public static WorkflowProject getWorkflowProject(final UUID workflowProjectID) throws NoSuchElementException {
        return WorkflowProjectManager.getWorkflowProject(workflowProjectID).orElseThrow(
            () -> new NoSuchElementException("Workflow project for ID \"" + workflowProjectID + "\" not found."));
    }

    /**
     * Gets the (sub-)workflow manager for the given root workflow id and node id.
     *
     * @param rootWorkflowID the root workflow id
     * @param nodeID the subnode's or metanode's node id
     * @return the {@link WorkflowManager}-instance
     * @throws NotASubWorkflowException if the node id doesn't reference a workflow (i.e. a sub- or metanode)
     * @throws NodeNotFoundException if there is no node for the given node id
     */
    public static WorkflowManager getWorkflowManager(final UUID rootWorkflowID, final NodeIDEnt nodeID)
        throws NotASubWorkflowException, NodeNotFoundException {
        NodeContainer nodeContainer;
        if (nodeID == null || nodeID.equals(NodeIDEnt.getRootID())) {
            nodeContainer = getRootWorkflowManager(rootWorkflowID);
        } else {
            nodeContainer = getNodeContainer(rootWorkflowID, nodeID);
        }
        if (nodeContainer instanceof SubNodeContainer) {
            return ((SubNodeContainer)nodeContainer).getWorkflowManager();
        } else if (nodeContainer instanceof WorkflowManager) {
            return (WorkflowManager)nodeContainer;
        } else {
            throw new ServiceExceptions.NotASubWorkflowException(
                "The node id '" + nodeID + "' doesn't reference a sub workflow.");
        }
   }

    /**
     * Gets the root workflow manager and the contained node container at the same time (see
     * {@link #getNodeContainer(UUID, NodeIDEnt)} and {@link #getRootWorkflowManager(UUID)}).
     *
     * @param rootWorkflowID the id of the root workflow
     * @param nodeID the id of the node requested
     * @return a pair of {@link WorkflowManager} and {@link NodeContainer} instances
     * @throws NodeNotFoundException if there is no node for the given node id
     * @throws NoSuchElementException if there is no root workflow for the given root workflow id
     */
    public static Pair<WorkflowManager, NodeContainer> getRootWfmAndNc(final UUID rootWorkflowID,
        final NodeIDEnt nodeID) throws NodeNotFoundException {
        return Pair.create(getRootWorkflowManager(rootWorkflowID), getNodeContainer(rootWorkflowID, nodeID));
    }

    /**
     * Converts a node id entity (as provided by gateway entities) to a {@link NodeID} instance.
     *
     * @param rootWorkflowID id of the workflow the node belongs to
     * @param nodeID the node id entity
     *
     * @return the {@link NodeID} instance
     */
    public static NodeID entityToNodeID(final UUID rootWorkflowID, final NodeIDEnt nodeID) {
        return nodeID.toNodeID(getRootWorkflowManager(rootWorkflowID).getID());
    }

    /**
     * Converts annotation id entity (as provided by gateway entities) to a
     * {@link WorkflowAnnotationID}-instance.
     *
     * @param rootWorkflowID id of the root(!) workflow the annotations belongs to
     * @param annotationID the annotation id entity to convert
     * @return the {@link WorkflowAnnotationID} instance
     */
    public static WorkflowAnnotationID entityToAnnotationID(final UUID rootWorkflowID,
        final AnnotationIDEnt annotationID) {
        NodeID nodeID = entityToNodeID(rootWorkflowID, annotationID.getNodeIDEnt());
        return new WorkflowAnnotationID(nodeID, annotationID.getIndex());
    }

    /**
     * Converts a connection id entity (as provided by gateway entities) to a {@link ConnectionID} instance.
     *
     * @param rootWorkflowID id of the workflow the connection belongs to
     * @param connectionID the id entity to convert
     * @return the {@link ConnectionID} instance
     */
    public static ConnectionID entityToConnectionID(final UUID rootWorkflowID, final ConnectionIDEnt connectionID) {
        return new ConnectionID(entityToNodeID(rootWorkflowID, connectionID.getDestNodeIDEnt()),
            connectionID.getDestPortIdx());
    }

    /**
     * Determines the wizard execution state of a workflow to be be used by
     * {@link org.knime.gateway.api.entity.WizardPageEnt.WizardExecutionStateEnum} and
     * {@link org.knime.gateway.api.entity.ExecutionStatisticsEnt.WizardExecutionStateEnum}
     *
     * Needs to be kept in sync with
     * <code>com.knime.enterprise.executor.ExecutorUtil#getWizardExecutionState(WorkflowManager)</code>. <br>
     * However there are some differences:
     * <ul>
     * <li>this method returns an 'executing' state</li>
     * <li>this method always returns a state even if workflow is not in wizard execution mode</li>
     * </ul>
     *
     * @param wfm the workflow to determine the state for
     * @return the wizard execution state string
     */
    public static String getWizardExecutionState(final WorkflowManager wfm) {
        try (WorkflowLock lock = wfm.lock()) {
            if (wfm.getNodeContainerState().isExecuted()) {
                return "EXECUTION_FINISHED";
            } else if (wfm.isInWizardExecution()
                && wfm.getWizardExecutionController().isHaltedAtNonTerminalWizardPage()) {
                return "INTERACTION_REQUIRED";
            } else if (wfm.getNodeContainerState().isExecutionInProgress()) {
                return "EXECUTING";
            } else if (!wfm.isInWizardExecution() || !wfm.getWizardExecutionController().hasExecutionStarted()) {
                return "UNDEFINED";
            } else {
                return "EXECUTION_FAILED";
            }
        }
    }
}
