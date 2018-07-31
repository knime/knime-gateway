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

import static com.knime.gateway.remote.service.util.DefaultServiceUtil.getRootWorkflowManager;
import static com.knime.gateway.remote.service.util.DefaultServiceUtil.getSubWorkflowManager;
import static com.knime.gateway.util.DefaultEntUtil.connectionIDToString;
import static com.knime.gateway.util.EntityBuilderUtil.buildWorkflowEnt;
import static com.knime.gateway.util.EntityBuilderUtil.buildWorkflowPartsEnt;
import static com.knime.gateway.util.EntityTranslateUtil.translateWorkflowPartsEnt;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

import org.knime.core.node.workflow.ConnectionContainer;
import org.knime.core.node.workflow.ConnectionID;
import org.knime.core.node.workflow.ConnectionUIInformation;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.NodeID.NodeIDSuffix;
import org.knime.core.node.workflow.NodeUIInformation;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.node.workflow.WorkflowAnnotation;
import org.knime.core.node.workflow.WorkflowAnnotationID;
import org.knime.core.node.workflow.WorkflowCopyContent;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.node.workflow.WorkflowPersistor;

import com.knime.gateway.remote.endpoint.WorkflowProjectManager;
import com.knime.gateway.remote.service.util.SimpleRepository;
import com.knime.gateway.remote.service.util.WorkflowCopyRepository;
import com.knime.gateway.remote.service.util.WorkflowEntRepository;
import com.knime.gateway.util.DefaultEntUtil;
import com.knime.gateway.v0.entity.ConnectionEnt;
import com.knime.gateway.v0.entity.PatchEnt;
import com.knime.gateway.v0.entity.WorkflowEnt;
import com.knime.gateway.v0.entity.WorkflowPartsEnt;
import com.knime.gateway.v0.entity.WorkflowSnapshotEnt;
import com.knime.gateway.v0.service.WorkflowService;
import com.knime.gateway.v0.service.util.ServiceExceptions;
import com.knime.gateway.v0.service.util.ServiceExceptions.ActionNotAllowedException;
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

    private final WorkflowCopyRepository m_copyRepo = new WorkflowCopyRepository();

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

    /**
     * {@inheritDoc}
     */
    @Override
    public UUID createWorkflowCopy(final UUID rootWorkflowID, final WorkflowPartsEnt parts) {
        WorkflowManager wfm;
        try {
            wfm = getSubWorkflowManager(rootWorkflowID, parts.getParentNodeID());
        } catch (NotASubWorkflowException | NodeNotFoundException ex) {
            // TODO Auto-generated catch block
            throw new RuntimeException(ex);
        }
        UUID partID = UUID.randomUUID();
        WorkflowPersistor copy;
        copy = wfm.copy(translateWorkflowPartsEnt(parts, s -> stringToNodeID(rootWorkflowID, s),
            s -> stringToAnnotationID(rootWorkflowID, s)));
        m_copyRepo.put(partID, copy);
        return partID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UUID deleteWorkflowParts(final UUID rootWorkflowID, final WorkflowPartsEnt parts, final Boolean copy)
        throws NotASubWorkflowException, NodeNotFoundException {
        WorkflowManager wfm = getSubWorkflowManager(rootWorkflowID, parts.getParentNodeID());
        if (parts.getAnnotationIDs().isEmpty() && parts.getNodeIDs().isEmpty() && parts.getConnectionIDs().isEmpty()) {
            return null;
        }

        UUID partsID = null;
        if (copy != null && copy) {
            //create a copy before removal
            WorkflowPersistor wp = wfm.copy(true, translateWorkflowPartsEnt(parts,
                s -> stringToNodeID(rootWorkflowID, s), s -> stringToAnnotationID(rootWorkflowID, s)));
            partsID = UUID.randomUUID();
            m_copyRepo.put(partsID, wp);
        }

        for (String nodeID : parts.getNodeIDs()) {
            NodeID id = stringToNodeID(rootWorkflowID, nodeID);
            if (wfm.canRemoveNode(id)) {
                wfm.removeNode(id);
            }
        }
        for (String connectionID : parts.getConnectionIDs()) {
            ConnectionID id = stringToConnectionID(rootWorkflowID, connectionID);
            try {
                ConnectionContainer cc;
                if ((cc = wfm.getConnection(id)) != null) {
                    wfm.removeConnection(cc);
                }
            } catch (IllegalArgumentException e) {
                //fail silently
                //TODO better add a respective method to workflow manager to be able to check for existence
            }
        }
        for (String annotationID : parts.getAnnotationIDs()) {
            WorkflowAnnotationID id = stringToAnnotationID(rootWorkflowID, annotationID);
            WorkflowAnnotation[] workflowAnnotations = wfm.getWorkflowAnnotations(id);
            if (workflowAnnotations[0] != null) {
                wfm.removeAnnotation(wfm.getWorkflowAnnotations(id)[0]);
            }
        }
        return partsID;
        //TODO return info what has been removed and what couldn't?
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorkflowPartsEnt pasteWorkflowParts(final UUID rootWorkflowID, final UUID partsID, final Integer x,
        final Integer y, final String nodeID) throws NotASubWorkflowException, NotFoundException, NotFoundException {
        WorkflowManager wfm;
        try {
            wfm = getSubWorkflowManager(rootWorkflowID, nodeID);
        } catch (NodeNotFoundException ex) {
            throw new NotFoundException("No node found for the given parent-id", ex);
        }
        WorkflowPersistor persistor = m_copyRepo.get(partsID);
        if (persistor == null) {
            throw new NotFoundException("No workflow-part copy available for the given id");
        }
        WorkflowCopyContent copyContent = wfm.paste(persistor);
        int[] offset = calcOffset(copyContent, wfm);
        int[] shift = new int[]{x - offset[0], y - offset[1]};
        NodeID[] pastedNodes = copyContent.getNodeIDs();
        Set<NodeID> newIDs = new HashSet<NodeID>(); // fast lookup below
        for (NodeID id : pastedNodes) {
            newIDs.add(id);
            NodeContainer nc = wfm.getNodeContainer(id);
            NodeUIInformation oldUI = nc.getUIInformation();
            NodeUIInformation newUI = NodeUIInformation.builder(oldUI).translate(shift).build();
            nc.setUIInformation(newUI);
        }
        for (ConnectionContainer conn : wfm.getConnectionContainers()) {
            if (newIDs.contains(conn.getDest())
                    && newIDs.contains(conn.getSource())) {
                // get bend points and move them
                ConnectionUIInformation oldUI =
                    conn.getUIInfo();
                if (oldUI != null) {
                    ConnectionUIInformation newUI =
                        ConnectionUIInformation.builder(oldUI).translate(shift).build();
                    conn.setUIInfo(newUI);
                }
            }
        }
        WorkflowAnnotation[] pastedAnnos = wfm.getWorkflowAnnotations(copyContent.getAnnotationIDs());
        for (WorkflowAnnotation a : pastedAnnos) {
            a.shiftPosition(shift[0], shift[1]);
        }
        return buildWorkflowPartsEnt(wfm.getID(), copyContent);
    }

    private static int[] calcOffset(final WorkflowCopyContent wcc, final WorkflowManager wfm) {
        NodeID[] nodes = wcc.getNodeIDs();
        List<int[]> insertedElementBounds = new ArrayList<int[]>();
        for (NodeID i : nodes) {
            NodeContainer nc = wfm.getNodeContainer(i);
            NodeUIInformation ui = nc.getUIInformation();
            int[] bounds = ui.getBounds();
            insertedElementBounds.add(bounds);
        }

        WorkflowAnnotation[] annos = wfm.getWorkflowAnnotations(wcc.getAnnotationIDs());
        for (WorkflowAnnotation a : annos) {
            int[] bounds = new int[]{a.getX(), a.getY(), a.getWidth(), a.getHeight()};
            insertedElementBounds.add(bounds);
        }
        int smallestX = Integer.MAX_VALUE;
        int smallestY = Integer.MAX_VALUE;
        for (int[] bounds : insertedElementBounds) {
            int currentX = bounds[0];
            int currentY = bounds[1];
            if (currentX < smallestX) {
                smallestX = currentX;
            }
            if (currentY < smallestY) {
                smallestY = currentY;
            }
        }
        return new int[]{smallestX, smallestY};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String createConnection(final UUID rootWorkflowID, final ConnectionEnt connection)
        throws ActionNotAllowedException {
        NodeID source = stringToNodeID(rootWorkflowID, connection.getSource());
        NodeID dest = stringToNodeID(rootWorkflowID, connection.getDest());

        //get prefix that references the subworkflow
        NodeID prefix;
        switch (connection.getType()) {
            case WFMIN:
                prefix = dest.getPrefix();
                break;
            case WFMTHROUGH:
                prefix = source;
            default:
                prefix = source.getPrefix();
                break;
        }

        WorkflowManager wfm;
        try {
            wfm = getSubWorkflowManager(rootWorkflowID, DefaultEntUtil.nodeIDToString(prefix));
        } catch (NotASubWorkflowException | NodeNotFoundException ex) {
            throw new ServiceExceptions.ActionNotAllowedException(
                "Parent id of dest/source node-id doesn't reference a (sub-)workflow.");
        }
        ConnectionContainer cc;
        try {
            cc = wfm.addConnection(source, connection.getSourcePort(), dest, connection.getDestPort());
        } catch (IllegalArgumentException e) {
            throw new ActionNotAllowedException("Failed to create connection", e);
        }
        int[][] bendpoints = connection.getBendPoints().stream().map(xy -> new int[]{xy.getX(), xy.getY()})
            .toArray(size -> new int[size][]);
        cc.setUIInfo(ConnectionUIInformation.builder().setBendpoints(bendpoints).build());
        return connectionIDToString(cc.getID());
    }

    private static WorkflowEnt createWorkflowEnt(final UUID rootWorkflowID) {
        WorkflowManager wfm = WorkflowProjectManager.openAndCacheWorkflow(rootWorkflowID).orElseThrow(
            () -> new NoSuchElementException("Workflow project for ID \"" + rootWorkflowID + "\" not found."));
        if (wfm.isEncrypted()) {
            throw new IllegalStateException("Workflow is encrypted and cannot be accessed.");
        }
        //TODO build the new workflow only if the corresponding workflow manager has been modified
        return buildWorkflowEnt(wfm, rootWorkflowID);
    }

    private static WorkflowEnt createSubWorkflowEnt(final UUID rootWorkflowID, final String nodeID)
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

    /**
     * Converts a string representation of a node id (as provided by gateway entities) to a {@link NodeID} instance.
     *
     * @param rootWorkflowID id of the workflow the node belongs to
     * @param nodeID the node id (without the root workflow node id)
     *
     * @return the {@link NodeID} instance
     */
    private static NodeID stringToNodeID(final UUID rootWorkflowID, final String nodeID) {
        return DefaultEntUtil.stringToNodeID(getRootWorkflowManager(rootWorkflowID).getID().toString(), nodeID);
    }

    /**
     * Converts/parses a string representation of a annotation id (as provided by gateway entities) to a
     * {@link WorkflowAnnotationID}-instance.
     *
     * @param rootWorkflowID id of the root(!) workflow the annotations belongs to
     * @param annotationID the annotation id to parse (without the root workflow node id)
     * @return the {@link WorkflowAnnotationID} instance
     */
    private static WorkflowAnnotationID stringToAnnotationID(final UUID rootWorkflowID, final String annotationID) {
        String[] split = annotationID.split("_");
        NodeID nodeID = stringToNodeID(rootWorkflowID, split[0]);
        return new WorkflowAnnotationID(nodeID, Integer.valueOf(split[1]));
    }

    /**
     * Converts a string representation of a connection id (as provided by gateway entities) to a {@link ConnectionID}
     * instance.
     *
     * @param rootWorkflowID id of the workflow the connection belongs to
     *
     * @param s the string representation to convert
     * @return the {@link ConnectionID} instance
     */
    private static ConnectionID stringToConnectionID(final UUID rootWorkflowID, final String s) {
        if (!s.contains("_")) {
            throw new IllegalArgumentException("Unable to parse connection id from string.");
        }
        String[] split = s.split("_");
        return new ConnectionID(stringToNodeID(rootWorkflowID, split[0]), Integer.valueOf(split[1]));
    }
}
