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
package com.knime.gateway.local.workflow;

import static com.knime.gateway.local.util.EntityProxyUtil.nodeIDToString;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.dialog.ExternalNodeData;
import org.knime.core.node.port.MetaPortInfo;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.PortTypeRegistry;
import org.knime.core.node.workflow.ConnectionID;
import org.knime.core.node.workflow.EditorUIInformation;
import org.knime.core.node.workflow.NodeAnnotation;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.NodeMessage;
import org.knime.core.node.workflow.NodeMessage.Type;
import org.knime.core.node.workflow.NodeStateEvent;
import org.knime.core.node.workflow.NodeUIInformation;
import org.knime.core.node.workflow.NodeUIInformationEvent;
import org.knime.core.node.workflow.WorkflowAnnotation;
import org.knime.core.node.workflow.WorkflowContext;
import org.knime.core.node.workflow.WorkflowListener;
import org.knime.core.ui.node.workflow.ConnectionContainerUI;
import org.knime.core.ui.node.workflow.NodeContainerUI;
import org.knime.core.ui.node.workflow.NodeInPortUI;
import org.knime.core.ui.node.workflow.NodeOutPortUI;
import org.knime.core.ui.node.workflow.SubNodeContainerUI;
import org.knime.core.ui.node.workflow.WorkflowInPortUI;
import org.knime.core.ui.node.workflow.WorkflowManagerUI;
import org.knime.core.ui.node.workflow.WorkflowOutPortUI;
import org.knime.core.util.Pair;

import com.knime.gateway.util.DefaultEntUtil;
import com.knime.gateway.v0.entity.ConnectionEnt;
import com.knime.gateway.v0.entity.MetaPortInfoEnt;
import com.knime.gateway.v0.entity.NodeEnt;
import com.knime.gateway.v0.entity.PortTypeEnt;
import com.knime.gateway.v0.entity.WorkflowEnt;
import com.knime.gateway.v0.entity.WorkflowNodeEnt;
import com.knime.gateway.v0.entity.WorkflowSnapshotEnt;
import com.knime.gateway.v0.entity.WorkflowUIInfoEnt;
import com.knime.gateway.v0.entity.WrappedWorkflowNodeEnt;
import com.knime.gateway.v0.service.util.ServiceExceptions.ActionNotAllowedException;
import com.knime.gateway.v0.service.util.ServiceExceptions.NodeNotFoundException;

/**
 * Abstract {@link WorkflowManagerUI} implementation that wraps (and therewith retrieves its information) from a
 * {@link WorkflowNodeEnt} that represents a workflow most likely received remotely.
 *
 * @author Martin Horn, University of Konstanz
 * @param <E> the type of the workflow node entity (e.g. wrapped)
 */
abstract class AbstractEntityProxyWorkflowManager<E extends WorkflowNodeEnt> extends EntityProxyNodeContainer<E>
    implements WorkflowManagerUI {

    private WorkflowEnt m_workflowEnt;

    private UUID m_snapshotID;

    /**
     * @param workflowNodeEnt
     */
    AbstractEntityProxyWorkflowManager(final E workflowNodeEnt, final EntityProxyAccess access) {
        super(workflowNodeEnt, access);
    }

    private WorkflowEnt getWorkflow() {
        if (m_workflowEnt == null) {
            WorkflowSnapshotEnt wfs = getAccess().getWorkflowSnapshotEnt(getEntity());
            m_workflowEnt = wfs.getWorkflow();
            m_snapshotID = wfs.getSnapshotID();
        }
        return m_workflowEnt;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URL getIcon() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReentrantLock getReentrantLockInstance() {
        // TODO
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isLockedByCurrentThread() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorkflowManagerUI getProjectWFM() {
        //TODO if this is a meta node
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeProject(final NodeID id) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canRemoveNode(final NodeID nodeID) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isProject() {
        //if the workflow has no parent it is very likely a workflow project and not a metanode
        return getParent() == null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConnectionContainerUI addConnection(final NodeID source, final int sourcePort, final NodeID dest,
        final int destPort) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canAddConnection(final NodeID source, final int sourcePort, final NodeID dest, final int destPort) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canAddNewConnection(final NodeID source, final int sourcePort, final NodeID dest,
        final int destPort) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canRemoveConnection(final ConnectionContainerUI cc) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeConnection(final ConnectionContainerUI cc) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<ConnectionContainerUI> getOutgoingConnectionsFor(final NodeID id, final int portIdx) {
        //TODO introduce a more efficient data structure to access the right connection
        Set<ConnectionContainerUI> res = new HashSet<ConnectionContainerUI>();
        String nodeID = nodeIDToString(id);
        for (ConnectionEnt c : getWorkflow().getConnections()) {
            if (c.getSource().equals(nodeID) && c.getSourcePort() == portIdx) {
                res.add(getAccess().getConnectionContainer(c));
            }
        }
        return res;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<ConnectionContainerUI> getOutgoingConnectionsFor(final NodeID id) {
        //TODO introduce a more efficient data structure to access the right connection
        Set<ConnectionContainerUI> res = new HashSet<ConnectionContainerUI>();
        String nodeID = nodeIDToString(id);
        for (ConnectionEnt c : getWorkflow().getConnections()) {
            if (c.getSource().equals(nodeID)) {
                res.add(getAccess().getConnectionContainer(c));
            }
        }
        return res;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConnectionContainerUI getIncomingConnectionFor(final NodeID id, final int portIdx) {
        //TODO introduce a more efficient data structure to access the right connection
        String nodeID = nodeIDToString(id);
        for (ConnectionEnt c : getWorkflow().getConnections()) {
            if (c.getDest().equals(nodeID) && c.getDestPort() == portIdx) {
                return getAccess().getConnectionContainer(c);
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<ConnectionContainerUI> getIncomingConnectionsFor(final NodeID id) {
        //TODO introduce a more efficient data structure to access the right connection
        Set<ConnectionContainerUI> res = new HashSet<ConnectionContainerUI>();
        String nodeID = nodeIDToString(id);
        for (ConnectionEnt c : getWorkflow().getConnections()) {
            if (c.getDest().equals(nodeID)) {
                res.add(getAccess().getConnectionContainer(c));
            }
        }
        return res;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConnectionContainerUI getConnection(final ConnectionID id) {
        //TODO introduce a more efficient data structure to access the right connection
        for (ConnectionEnt c : getWorkflow().getConnections()) {
            if (getWorkflow().getNodes().get(c.getDest()).getNodeID().equals(nodeIDToString(id.getDestinationNode()))
                && id.getDestinationPort() == c.getDestPort()) {
                return getAccess().getConnectionContainer(c);
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaPortInfo[] getMetanodeInputPortInfo(final NodeID metaNodeID) {
        //TODO metaNodeID necessary??
        List<? extends MetaPortInfoEnt> metaInPorts = getWorkflow().getMetaInPortInfos();
        MetaPortInfo[] res = new MetaPortInfo[metaInPorts.size()];
        for (int i = 0; i < res.length; i++) {
            MetaPortInfoEnt in = metaInPorts.get(i);
            PortTypeEnt pte = in.getPortType();
            PortTypeRegistry ptr = PortTypeRegistry.getInstance();
            PortType portType =
                ptr.getPortType(ptr.getObjectClass(pte.getPortObjectClassName()).get(), pte.isOptional());
            res[i] = MetaPortInfo.builder().setIsConnected(in.isConnected()).setMessage(in.getMessage())
                .setNewIndex(in.getNewIndex()).setOldIndex(in.getOldIndex()).setPortType(portType).build();
        }
        return res;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaPortInfo[] getMetanodeOutputPortInfo(final NodeID metaNodeID) {
        //TODO metaNodeID necessary
        List<? extends MetaPortInfoEnt> metaOutPorts = getWorkflow().getMetaOutPortInfos();
        MetaPortInfo[] res = new MetaPortInfo[metaOutPorts.size()];
        for (int i = 0; i < res.length; i++) {
            MetaPortInfoEnt out = metaOutPorts.get(i);
            PortTypeEnt pte = out.getPortType();
            PortTypeRegistry ptr = PortTypeRegistry.getInstance();
            PortType portType =
                ptr.getPortType(ptr.getObjectClass(pte.getPortObjectClassName()).get(), pte.isOptional());
            res[i] = MetaPortInfo.builder().setIsConnected(out.isConnected()).setMessage(out.getMessage())
                .setNewIndex(out.getNewIndex()).setOldIndex(out.getOldIndex()).setPortType(portType).build();
        }
        return res;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaPortInfo[] getSubnodeInputPortInfo(final NodeID subNodeID) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaPortInfo[] getSubnodeOutputPortInfo(final NodeID subNodeID) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void changeMetaNodeInputPorts(final NodeID subFlowID, final MetaPortInfo[] newPorts) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void changeMetaNodeOutputPorts(final NodeID subFlowID, final MetaPortInfo[] newPorts) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void changeSubNodeInputPorts(final NodeID subFlowID, final MetaPortInfo[] newPorts) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void changeSubNodeOutputPorts(final NodeID subFlowID, final MetaPortInfo[] newPorts) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     * @deprecated
     */
    @Deprecated
    @Override
    public void resetAll() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resetAndConfigureAll() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("rawtypes")
    @Override
    public void executeUpToHere(final NodeID... ids) {
        for(NodeID id : ids) {
            NodeContainerUI nc = getNodeContainer(id);
            assert nc instanceof EntityProxyNodeContainer;
            ((EntityProxyNodeContainer)nc).execute();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canReExecuteNode(final NodeID id) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveNodeSettingsToDefault(final NodeID id) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void executePredecessorsAndWait(final NodeID id) throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String canExpandSubNode(final NodeID subNodeID) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String canExpandMetaNode(final NodeID wfmID) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String canCollapseNodesIntoMetaNode(final NodeID[] orgIDs) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("rawtypes")
    @Override
    public boolean canResetNode(final NodeID nodeID) {
        //TODO ask server whether the node can be reset (i.e. whether there are executing successors etc.)
        //very simple (but not complete!) logic to check whether a node can be reset
        NodeContainerUI nc = getNodeContainer(nodeID);
        assert nc instanceof EntityProxyNodeContainer;
        return ((EntityProxyNodeContainer) nc).canReset();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canResetContainedNodes() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("rawtypes")
    @Override
    public void resetAndConfigureNode(final NodeID id) {
        NodeContainerUI nc = getNodeContainer(id);
        assert nc instanceof EntityProxyNodeContainer;
        ((EntityProxyNodeContainer) nc).reset();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canConfigureNodes() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canExecuteNodeDirectly(final NodeID nodeID) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("rawtypes")
    @Override
    public boolean canExecuteNode(final NodeID nodeID) {
        NodeContainerUI nc = getNodeContainer(nodeID);
        assert nc instanceof EntityProxyNodeContainer;
        return ((EntityProxyNodeContainer)nc).canExecute();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canCancelNode(final NodeID nodeID) {
        return getNodeContainer(nodeID).getNodeContainerState().isExecutionInProgress();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canCancelAll() {
        return getNodeContainerState().isExecutionInProgress();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canSetJobManager(final NodeID nodeID) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("rawtypes")
    @Override
    public void cancelExecution(final NodeContainerUI nc) {
        if (nc instanceof EntityProxyNodeContainer) {
            ((EntityProxyNodeContainer)nc).cancelExecution();
        } else {
            throw new IllegalArgumentException("NodeContainerUI implementation not supported.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void shutdown() {
        //TODO
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean executeAllAndWaitUntilDone() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean executeAllAndWaitUntilDoneInterruptibly() throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean waitWhileInExecution(final long time, final TimeUnit unit) throws InterruptedException {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canExecuteAll() {
        //simple (and possibly not complete strategy) to determine whether the entire workflow can be executed
        return getNodeContainers().stream().anyMatch(nc -> {
            assert nc instanceof EntityProxyNodeContainer;
            @SuppressWarnings("rawtypes")
            EntityProxyNodeContainer epnc = (EntityProxyNodeContainer)nc;
            return epnc.canExecute();
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void executeAll() {
        if (!canExecuteAll()) {
            throw new IllegalStateException("Node is not the right state to be executed.");
        }
        try {
            getAccess().nodeService().changeAndGetNodeState(getEntity().getRootWorkflowID(), getEntity().getNodeID(),
                "execute");
        } catch (NodeNotFoundException ex) {
            // should not happen
            throw new RuntimeException(ex);
        } catch (ActionNotAllowedException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String printNodeSummary(final NodeID prefix, final int indent) {
        return "TODO node summary";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<NodeContainerUI> getNodeContainers() {
        Collection<NodeEnt> nodes = getWorkflow().getNodes().values();
        //return exactly the same node container instance for the same node entity
        return nodes.stream().map(n -> getAccess().getNodeContainer(n)).collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<ConnectionContainerUI> getConnectionContainers() {
        List<? extends ConnectionEnt> connections = getWorkflow().getConnections();
        //return exactly the same connection container instance for the same connection entity
        return connections.stream().map(c -> getAccess().getConnectionContainer(c)).collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeContainerUI getNodeContainer(final NodeID id) {
        final NodeEnt nodeEnt = getWorkflow().getNodes().get(nodeIDToString(id));
        //return exactly the same node container instance for the same node entity
        return getAccess().getNodeContainer(nodeEnt);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T getNodeContainer(final NodeID id, final Class<T> subclass, final boolean failOnError) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsNodeContainer(final NodeID id) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsExecutedNode() {
        return false;
    }

    /**
     * {@inheritDoc}
     * @deprecated
     */
    @Deprecated
    @Override
    public List<NodeMessage> getNodeErrorMessages() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Pair<String, NodeMessage>> getNodeMessages(final Type... types) {
        // TODO
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isWriteProtected() {
        //TODO
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<NodeID> getLinkedMetaNodes(final boolean recurse) {
        //TODO
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canUpdateMetaNodeLink(final NodeID id) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasUpdateableMetaNodeLink(final NodeID id) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setWorkflowPassword(final String password, final String hint) throws NoSuchAlgorithmException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isUnlocked() {
        //unlocking not supported yet
        return !isEncrypted();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPasswordHint() {
        return "TODO password hint";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OutputStream cipherOutput(final OutputStream out) throws IOException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCipherFileName(final String fileName) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addListener(final WorkflowListener listener) {
        //TODO
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeListener(final WorkflowListener listener) {
        //TODO
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAutoSaveDirectoryDirtyRecursivly() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorkflowInPortUI getInPort(final int index) {
        //TODO pass the underlying port, too
        return getAccess().getWorkflowInPort(getEntity().getInPorts().get(index));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorkflowOutPortUI getOutPort(final int index) {
        return getAccess().getWorkflowOutPort(getEntity().getOutPorts().get(index), getEntity());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setName(final String name) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean renameWorkflowDirectory(final String newName) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getNameField() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEditorUIInformation(final EditorUIInformation editorInfo) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EditorUIInformation getEditorUIInformation() {
        WorkflowUIInfoEnt uiEnt = getWorkflow().getWorkflowUIInfo();
        return EditorUIInformation.builder().setGridX(uiEnt.getGridX()).setGridY(uiEnt.getGridY())
            .setShowGrid(uiEnt.isShowGrid()).setSnapToGrid(uiEnt.isSnapToGrid())
            .setZoomLevel(uiEnt.getZoomLevel().doubleValue())
            .setHasCurvedConnections(uiEnt.isHasCurvedConnection())
            .setConnectionLineWidth(uiEnt.getConnectionLineWidth()).build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setInPortsBarUIInfo(final NodeUIInformation inPortsBarUIInfo) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setOutPortsBarUIInfo(final NodeUIInformation outPortsBarUIInfo) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeUIInformation getInPortsBarUIInfo() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeUIInformation getOutPortsBarUIInfo() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<WorkflowAnnotation> getWorkflowAnnotations() {
        return getWorkflow().getWorkflowAnnotations().stream().map(wa -> getAccess().getWorkflowAnnotation(wa))
            .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addWorkflowAnnotation(final WorkflowAnnotation annotation) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void bringAnnotationToFront(final WorkflowAnnotation annotation) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendAnnotationToBack(final WorkflowAnnotation annotation) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void nodeUIInformationChanged(final NodeUIInformationEvent evt) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<NodeAnnotation> getNodeAnnotations() {
        //TODO
        //        try (WorkflowLock lock = lock()) {
        Collection<NodeContainerUI> nodeContainers = getNodeContainers();
        List<NodeAnnotation> result = new LinkedList<NodeAnnotation>();
        for (NodeContainerUI node : nodeContainers) {
            result.add(node.getNodeAnnotation());
        }
        return result;
        //        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T castNodeModel(final NodeID id, final Class<T> cl) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> Map<NodeID, T> findNodes(final Class<T> nodeModelClass, final boolean recurse) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeContainerUI findNodeContainer(final NodeID id) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, ExternalNodeData> getInputNodes() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setInputNodes(final Map<String, ExternalNodeData> input) throws InvalidSettingsException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, ExternalNodeData> getExternalOutputs() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeWorkflowVariable(final String name) {
        throw new UnsupportedOperationException();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorkflowContext getContext() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notifyTemplateConnectionChangedListener() {
        throw new UnsupportedOperationException();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRefreshable() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void refresh(final boolean deepRefresh) {
        //only refresh if the workflow was retrieved already
        if (m_workflowEnt != null) {
            WorkflowEnt oldWorkflow = m_workflowEnt;
            // update workflow
            Pair<WorkflowEnt, UUID> res = getAccess().updateWorkflowEnt(getEntity(), m_workflowEnt, m_snapshotID);
            m_workflowEnt = res.getFirst();

            if (oldWorkflow != m_workflowEnt) {
                // refresh the workflow manager only if there is a new (updated) workflow entity

                m_snapshotID = res.getSecond();
                assert (m_snapshotID != null);
                for (Entry<String, NodeEnt> entry : m_workflowEnt.getNodes().entrySet()) {
                    getAccess().updateNodeContainer(oldWorkflow.getNodes().get(entry.getKey()), entry.getValue());
                }

                //refresh the workflow node entity, too, if it is the root workflow (e.g. that contains the state of this metanode)
                if (getEntity().getNodeID().equals(DefaultEntUtil.ROOT_NODE_ID)) {
                    try {
                        super.update((E)getAccess().nodeService().getNode(getEntity().getRootWorkflowID(),
                            getEntity().getNodeID()));
                    } catch (NodeNotFoundException ex) {
                        throw new RuntimeException(ex);
                    }
                }
           }

            if (deepRefresh) {
                //refresh all contained workflows (i.e. metanodes)
                for (NodeEnt node : m_workflowEnt.getNodes().values()) {
                    WorkflowManagerUI wfm = null;
                    //order of checking very important here since WrappedWorkflowNodeEnt is a subclass of WorkflowNodeEnt
                    if (node instanceof WrappedWorkflowNodeEnt) {
                        wfm = ((SubNodeContainerUI)getAccess().getNodeContainer(node)).getWorkflowManager();
                    } else if (node instanceof WorkflowNodeEnt) {
                        wfm = (WorkflowManagerUI)getAccess().getNodeContainer(node);
                    }
                    if (wfm != null && wfm.isRefreshable()) {
                        wfm.refresh(true);
                    }
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNrWorkflowIncomingPorts() {
        return getEntity().getWorkflowIncomingPorts().size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNrWorkflowOutgoingPorts() {
        return getEntity().getWorkflowOutgoingPorts().size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeOutPortUI getWorkflowIncomingPort(final int i) {
        return getAccess().getNodeOutPort(getEntity().getWorkflowIncomingPorts().get(i), getEntity());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeInPortUI getWorkflowOutgoingPort(final int i) {
        return getAccess().getNodeInPort(getEntity().getWorkflowOutgoingPorts().get(i));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEncrypted() {
        return getEntity().isEncrypted();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(final E entity) {
        //update port entities, too -> makes sure that still the very same entity proxy classes are used
        getAccess().updateWorkflowNodeOutPorts(getEntity(), entity);
        getAccess().updateWorkflowNodeInPorts(getEntity(), entity);
        super.update(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void postUpdate() {
        super.postUpdate();
        // notify listeners for nodes state changes at the nodes connected to the out ports
        for (int i = 0; i < getNrOutPorts(); i++) {
            getOutPort(i).notifyNodeStateChangeListener(new NodeStateEvent(NodeID.ROOTID) {
                @Override
                public NodeID getSource() {
                    // since there is currently no node id available of the inner node connected to the output port,
                    // don't allow this operation
                    throw new UnsupportedOperationException("No node ID available.");
                }
            });
        }
    }
}
