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
package com.knime.gateway.remote.service;

import static com.knime.gateway.remote.util.EntityBuilderUtil.buildWorkflowEnt;

import java.util.NoSuchElementException;
import java.util.UUID;

import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeID.NodeIDSuffix;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.node.workflow.WorkflowManager;

import com.knime.gateway.remote.endpoint.WorkflowProjectManager;
import com.knime.gateway.remote.util.SimpleRepository;
import com.knime.gateway.remote.util.WorkflowEntRepository;
import com.knime.gateway.util.DefaultEntUtil;
import com.knime.gateway.v0.entity.PatchEnt;
import com.knime.gateway.v0.entity.WorkflowEnt;
import com.knime.gateway.v0.entity.WorkflowSnapshotEnt;
import com.knime.gateway.v0.service.WorkflowService;
import com.knime.gateway.v0.service.util.ServiceExceptions;
import com.knime.gateway.v0.service.util.ServiceExceptions.NodeNotFoundException;
import com.knime.gateway.v0.service.util.ServiceExceptions.NotASubWorkflowException;
import com.knime.gateway.v0.service.util.ServiceExceptions.NotFoundException;

/**
 * Default implementation of {@link WorkflowService} that delegates the operations to knime.core (e.g.
 * {@link WorkflowManager} etc.).
 *
 * @author Martin Horn, University of Konstanz
 */
public class DefaultWorkflowService implements WorkflowService {
    private static final DefaultWorkflowService INSTANCE = new DefaultWorkflowService();

    private final WorkflowEntRepository m_entityRepo = new SimpleRepository();

    /**
     * Creates a new workflow service.
     */
    private DefaultWorkflowService() {
        WorkflowProjectManager.addWorkflowProjectRemovedListener(uuid -> m_entityRepo.disposeHistory(uuid));
    }

    /**
     * Returns the singleton instance for this service.
     *
     * @return the singleton instance
     */
    public static DefaultWorkflowService getInstance() {
       return INSTANCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorkflowSnapshotEnt getWorkflow(final UUID rootWorkflowID) {
        WorkflowEnt ent = createWorkflowEnt(rootWorkflowID);
        return m_entityRepo.commit(rootWorkflowID, null, ent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PatchEnt getWorkflowDiff(final UUID rootWorkflowID, final UUID snapshotID) throws NotFoundException {
        return createWorkflowDiff(rootWorkflowID, null, snapshotID, createWorkflowEnt(rootWorkflowID));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorkflowSnapshotEnt getSubWorkflow(final UUID rootWorkflowID, final String nodeID)
        throws NotASubWorkflowException, NodeNotFoundException {
        if (nodeID.equals(DefaultEntUtil.ROOT_NODE_ID)) {
            return getWorkflow(rootWorkflowID);
        }
        WorkflowEnt ent = createSubWorkflowEnt(rootWorkflowID, nodeID);
        return m_entityRepo.commit(rootWorkflowID, nodeID, ent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PatchEnt getSubWorkflowDiff(final UUID rootWorkflowID, final String nodeID, final UUID snapshotID)
        throws NotASubWorkflowException, NotFoundException {
        if (nodeID.equals(DefaultEntUtil.ROOT_NODE_ID)) {
            return getWorkflowDiff(rootWorkflowID, snapshotID);
        }
        try {
            return createWorkflowDiff(rootWorkflowID, nodeID, snapshotID, createSubWorkflowEnt(rootWorkflowID, nodeID));
        } catch (NodeNotFoundException ex) {
            throw new NotFoundException(ex.getMessage(), ex);
        }
    }

    private WorkflowEnt createWorkflowEnt(final UUID rootWorkflowID) {
        WorkflowManager wfm = WorkflowProjectManager.openAndCacheWorkflow(rootWorkflowID).orElseThrow(
            () -> new NoSuchElementException("Workflow project for ID \"" + rootWorkflowID + "\" not found."));
        if (wfm.isEncrypted()) {
            throw new IllegalStateException("Workflow is encrypted and cannot be accessed.");
        }
        //TODO build the new workflow only if the corresponding workflow manager has been modified
        return buildWorkflowEnt(wfm, rootWorkflowID);
    }

    private WorkflowEnt createSubWorkflowEnt(final UUID rootWorkflowID, final String nodeID)
        throws NotASubWorkflowException, NodeNotFoundException {
        // get the right IWorkflowManager for the given id and create a WorkflowEnt from it
        WorkflowManager rootWfm = WorkflowProjectManager.openAndCacheWorkflow(rootWorkflowID).orElseThrow(
            () -> new NoSuchElementException("Workflow project for ID \"" + rootWorkflowID + "\" not found."));
        try {
            NodeContainer metaNode =
                rootWfm.findNodeContainer(NodeIDSuffix.fromString(nodeID).prependParent(rootWfm.getID()));
            if (metaNode instanceof WorkflowManager) {
                WorkflowManager wfm = (WorkflowManager)metaNode;
                if (wfm.isEncrypted()) {
                    throw new IllegalStateException("Workflow is encrypted and cannot be accessed.");
                }
                return buildWorkflowEnt(wfm, rootWorkflowID);
            } else if (metaNode instanceof SubNodeContainer) {
                SubNodeContainer snc = (SubNodeContainer)metaNode;
                return buildWorkflowEnt(snc.getWorkflowManager(), rootWorkflowID);
            } else {
                throw new ServiceExceptions.NotASubWorkflowException("Node for the given node id ('" + nodeID.toString()
                    + "') is neither a metanode nor a wrapped metanode.");
            }
        } catch (IllegalArgumentException e) {
            throw new ServiceExceptions.NodeNotFoundException(e.getMessage(), e);
        }
    }

    private PatchEnt createWorkflowDiff(final UUID rootWorkflowID, final String nodeID, final UUID snapshotID,
        final WorkflowEnt ent) throws NotFoundException {
        if (snapshotID == null) {
            throw new NotFoundException("No snapshot id given!");
        }
        try {
            return m_entityRepo.getChangesAndCommit(snapshotID, ent);
        } catch (IllegalArgumentException e) {
            //thrown when there is no snapshot for the given snapshot id
            throw new NotFoundException(e.getMessage(), e);
        }
    }
}
