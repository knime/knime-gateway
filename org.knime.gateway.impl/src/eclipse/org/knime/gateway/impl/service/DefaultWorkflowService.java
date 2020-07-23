/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.com; Email: contact@knime.com
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
 */
package org.knime.gateway.impl.service;

import static org.knime.gateway.api.util.EntityBuilderUtil.buildWorkflowEnt;
import static org.knime.gateway.api.util.EntityBuilderUtil.buildWorkflowPartsEnt;
import static org.knime.gateway.api.util.EntityTranslateUtil.translateWorkflowPartsEnt;
import static org.knime.gateway.impl.service.util.DefaultServiceUtil.entityToAnnotationID;
import static org.knime.gateway.impl.service.util.DefaultServiceUtil.entityToConnectionID;
import static org.knime.gateway.impl.service.util.DefaultServiceUtil.entityToNodeID;
import static org.knime.gateway.impl.service.util.DefaultServiceUtil.getWorkflowManager;

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
import org.knime.core.node.workflow.NodeUIInformation;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.node.workflow.WorkflowAnnotation;
import org.knime.core.node.workflow.WorkflowAnnotationID;
import org.knime.core.node.workflow.WorkflowCopyContent;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.node.workflow.WorkflowPersistor;
import org.knime.gateway.api.entity.AnnotationIDEnt;
import org.knime.gateway.api.entity.ConnectionEnt;
import org.knime.gateway.api.entity.ConnectionIDEnt;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.entity.PatchEnt;
import org.knime.gateway.api.entity.WorkflowEnt;
import org.knime.gateway.api.entity.WorkflowPartsEnt;
import org.knime.gateway.api.entity.WorkflowSnapshotEnt;
import org.knime.gateway.api.service.WorkflowService;
import org.knime.gateway.api.service.util.ServiceExceptions;
import org.knime.gateway.api.service.util.ServiceExceptions.ActionNotAllowedException;
import org.knime.gateway.api.service.util.ServiceExceptions.InvalidRequestException;
import org.knime.gateway.api.service.util.ServiceExceptions.NodeNotFoundException;
import org.knime.gateway.api.service.util.ServiceExceptions.NotASubWorkflowException;
import org.knime.gateway.api.service.util.ServiceExceptions.NotFoundException;
import org.knime.gateway.impl.project.WorkflowProjectManager;
import org.knime.gateway.impl.service.util.SimpleRepository;
import org.knime.gateway.impl.service.util.WorkflowCopyRepository;
import org.knime.gateway.impl.service.util.WorkflowEntRepository;

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
    public WorkflowSnapshotEnt getWorkflow(final UUID rootWorkflowID, final NodeIDEnt nodeID)
        throws NotASubWorkflowException, NodeNotFoundException {
        if (nodeID.equals(NodeIDEnt.getRootID())) {
            WorkflowEnt ent = createWorkflowEnt(rootWorkflowID);
            return m_entityRepo.commit(rootWorkflowID, null, ent);
        }
        WorkflowEnt ent = createSubWorkflowEnt(rootWorkflowID, nodeID);
        return m_entityRepo.commit(rootWorkflowID, nodeID, ent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PatchEnt getWorkflowDiff(final UUID rootWorkflowID, final NodeIDEnt nodeID, final UUID snapshotID)
        throws NotASubWorkflowException, NotFoundException {
        if (nodeID.equals(NodeIDEnt.getRootID())) {
            return createWorkflowDiff(snapshotID, createWorkflowEnt(rootWorkflowID));
        }
        try {
            return createWorkflowDiff(snapshotID, createSubWorkflowEnt(rootWorkflowID, nodeID));
        } catch (NodeNotFoundException ex) {
            throw new NotFoundException(ex.getMessage(), ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UUID createWorkflowCopy(final UUID rootWorkflowID, final NodeIDEnt workflowID, final WorkflowPartsEnt parts)
        throws NotASubWorkflowException, NodeNotFoundException, InvalidRequestException {
        WorkflowManager wfm = getWorkflowManager(rootWorkflowID, workflowID);
        UUID partID = UUID.randomUUID();
        WorkflowPersistor copy;
        WorkflowCopyContent content = translateWorkflowPartsEnt(parts, s -> entityToNodeID(rootWorkflowID, s),
            s -> entityToAnnotationID(rootWorkflowID, s));
        for (WorkflowAnnotationID id : content.getAnnotationIDs()) {
            if (wfm.getWorkflowAnnotations(id)[0] == null) {
                throw new InvalidRequestException("Failed to copy parts: No annotation with ID " + id);
            }
        }
        try {
            copy = wfm.copy(content);
        } catch (IllegalArgumentException e) {
            //thrown when a part to copy doesn't exist
            throw new InvalidRequestException("Failed to copy parts: " + e.getMessage(), e);
        }
        m_copyRepo.put(partID, copy);
        return partID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UUID deleteWorkflowParts(final UUID rootWorkflowID, final NodeIDEnt workflowID, final WorkflowPartsEnt parts,
        final Boolean copy) throws NotASubWorkflowException, NodeNotFoundException, ActionNotAllowedException {
        WorkflowManager wfm = getWorkflowManager(rootWorkflowID, workflowID);
        if (parts.getAnnotationIDs().isEmpty() && parts.getNodeIDs().isEmpty() && parts.getConnectionIDs().isEmpty()) {
            return null;
        }

        UUID partsID = null;
        if (copy != null && copy) {
            //create a copy before removal
            WorkflowPersistor wp = wfm.copy(true, translateWorkflowPartsEnt(parts,
                e -> entityToNodeID(rootWorkflowID, e), e -> entityToAnnotationID(rootWorkflowID, e)));
            partsID = UUID.randomUUID();
            m_copyRepo.put(partsID, wp);
        }

        for (NodeIDEnt nodeID : parts.getNodeIDs()) {
            NodeID id = entityToNodeID(rootWorkflowID, nodeID);
            if (!wfm.containsNodeContainer(id)) {
                continue;
            }
            if (wfm.getIncomingConnectionsFor(id).stream().anyMatch(cc -> !wfm.canRemoveConnection(cc))) {
                throw new ActionNotAllowedException(
                    "There is an incoming connection that cannot be removed for node with id '" + nodeID + "'");
            }
            if (wfm.getOutgoingConnectionsFor(id).stream().anyMatch(cc -> !wfm.canRemoveConnection(cc))) {
                throw new ActionNotAllowedException(
                    "There is an outgoing connection that cannot be removed for node with id '" + nodeID + "'");
            }
            if (wfm.canRemoveNode(id)) {
                wfm.removeNode(id);
            } else {
                throw new ActionNotAllowedException("Node with id '" + nodeID + "' cannot be removed");
            }
        }

        for (ConnectionIDEnt connectionID : parts.getConnectionIDs()) {
            ConnectionID id = entityToConnectionID(rootWorkflowID, connectionID);
            try {
                ConnectionContainer cc;
                if ((cc = wfm.getConnection(id)) != null) {
                    if (wfm.canRemoveConnection(cc)) {
                        wfm.removeConnection(cc);
                    } else {
                        throw new ActionNotAllowedException(
                            "Connection with id '" + connectionID + "' cannot be removed");
                    }
                }
            } catch (IllegalArgumentException e) {
                //fail silently
                //TODO better add a respective method to workflow manager to be able to check for existence
            }
        }

        for (AnnotationIDEnt annotationID : parts.getAnnotationIDs()) {
            WorkflowAnnotationID id = entityToAnnotationID(rootWorkflowID, annotationID);
            WorkflowAnnotation workflowAnnotation = wfm.getWorkflowAnnotations(id)[0];
            if (workflowAnnotation != null) {
                wfm.removeAnnotation(workflowAnnotation);
            }
        }
        return partsID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorkflowPartsEnt pasteWorkflowParts(final UUID rootWorkflowID, final NodeIDEnt workflowID,
        final UUID partsID, final Integer x, final Integer y)
        throws NotASubWorkflowException, NotFoundException, NotFoundException {
        WorkflowManager wfm;
        try {
            wfm = getWorkflowManager(rootWorkflowID, workflowID);
        } catch (NodeNotFoundException ex) {
            throw new NotFoundException("No node found for the given parent-id", ex);
        }
        WorkflowPersistor persistor = m_copyRepo.get(partsID);
        if (persistor == null) {
            throw new NotFoundException("No workflow-part copy available for the given id");
        }
        WorkflowCopyContent copyContent = wfm.paste(persistor);
        int[] offset = calcOffset(copyContent.getNodeIDs(), copyContent.getAnnotationIDs(), wfm);
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
        return buildWorkflowPartsEnt(copyContent);
    }

    private static int[] calcOffset(final NodeID[] nodes, final WorkflowAnnotationID[] annotations,
        final WorkflowManager wfm) {
        List<int[]> insertedElementBounds = new ArrayList<int[]>();
        for (NodeID i : nodes) {
            NodeContainer nc = wfm.getNodeContainer(i);
            NodeUIInformation ui = nc.getUIInformation();
            int[] bounds = ui.getBounds();
            insertedElementBounds.add(bounds);
        }

        WorkflowAnnotation[] annos = wfm.getWorkflowAnnotations(annotations);
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
    public ConnectionIDEnt createConnection(final UUID rootWorkflowID, final ConnectionEnt connection)
        throws ActionNotAllowedException {
        NodeID source = entityToNodeID(rootWorkflowID, connection.getSource());
        NodeID dest = entityToNodeID(rootWorkflowID, connection.getDest());

        //get prefix that references the subworkflow
        NodeID prefix;
        switch (connection.getType()) {
            case WFMIN:
                prefix = dest.getPrefix();
                break;
            case WFMTHROUGH:
                prefix = source;
                break;
            default:
                prefix = source.getPrefix();
                break;
        }

        WorkflowManager wfm;
        try {
            wfm = getWorkflowManager(rootWorkflowID, new NodeIDEnt(prefix));
        } catch (NotASubWorkflowException | NodeNotFoundException ex) {
            throw new ServiceExceptions.ActionNotAllowedException(
                "Parent id of dest/source node-id doesn't reference a (sub-)workflow.");
        }
        if (!wfm.canAddConnection(source, connection.getSourcePort(), dest, connection.getDestPort())) {
            throw new ActionNotAllowedException("Not allowed");
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
        return new ConnectionIDEnt(cc.getID());
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

    private static WorkflowEnt createSubWorkflowEnt(final UUID rootWorkflowID, final NodeIDEnt nodeID)
        throws NotASubWorkflowException, NodeNotFoundException {
        // get the right IWorkflowManager for the given id and create a WorkflowEnt from it
        WorkflowManager rootWfm = WorkflowProjectManager.openAndCacheWorkflow(rootWorkflowID).orElseThrow(
            () -> new NoSuchElementException("Workflow project for ID \"" + rootWorkflowID + "\" not found."));
        try {
            NodeContainer metaNode =
                rootWfm.findNodeContainer(nodeID.toNodeID(rootWfm.getID()));
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
                    + "') is neither a metanode nor a component.");
            }
        } catch (IllegalArgumentException e) {
            throw new ServiceExceptions.NodeNotFoundException(e.getMessage(), e);
        }
    }

    private PatchEnt createWorkflowDiff(final UUID snapshotID, final WorkflowEnt ent) throws NotFoundException {
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
