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
 *   Nov 7, 2016 (hornm): created
 */
package org.knime.gateway.local.workflow;

import static org.knime.gateway.local.util.EntityProxyUtil.nodeIDToString;

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
import java.util.Set;
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
import org.knime.core.node.workflow.NodeUIInformation;
import org.knime.core.node.workflow.NodeUIInformationEvent;
import org.knime.core.node.workflow.WorkflowAnnotation;
import org.knime.core.node.workflow.WorkflowContext;
import org.knime.core.node.workflow.WorkflowListener;
import org.knime.core.ui.node.workflow.ConnectionContainerUI;
import org.knime.core.ui.node.workflow.NodeContainerUI;
import org.knime.core.ui.node.workflow.NodeInPortUI;
import org.knime.core.ui.node.workflow.NodeOutPortUI;
import org.knime.core.ui.node.workflow.WorkflowInPortUI;
import org.knime.core.ui.node.workflow.WorkflowManagerUI;
import org.knime.core.ui.node.workflow.WorkflowOutPortUI;
import org.knime.core.util.Pair;
import org.knime.gateway.v0.entity.ConnectionEnt;
import org.knime.gateway.v0.entity.MetaPortInfoEnt;
import org.knime.gateway.v0.entity.NodeEnt;
import org.knime.gateway.v0.entity.PortTypeEnt;
import org.knime.gateway.v0.entity.WorkflowEnt;
import org.knime.gateway.v0.entity.WorkflowUIInfoEnt;
import org.knime.gateway.v0.entity.WrappedWorkflowNodeEnt;

/**
 * {@link WorkflowManagerUI} implementation that wraps (and therewith retrieves its information) from a
 * {@link WrappedWorkflowNodeEnt} most likely received remotely.
 *
 * @author Martin Horn, University of Konstanz
 */
public final class EntityProxyWrappedWorkflowManager extends EntityProxyNodeContainer<WrappedWorkflowNodeEnt>
    implements WorkflowManagerUI {

    private WorkflowEnt m_workflowEnt;

    /**
     * @param wrappedWorkflowNodeEnt
     * @param access
     */
    public EntityProxyWrappedWorkflowManager(final WrappedWorkflowNodeEnt wrappedWorkflowNodeEnt,
        final EntityProxyAccess access) {
        super(wrappedWorkflowNodeEnt, access);
    }

    private WorkflowEnt getWorkflow() {
        if (m_workflowEnt == null) {
            m_workflowEnt = getAccess().getWorkflowEnt(getEntity());
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
     */
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
    @Override
    public void executeUpToHere(final NodeID... ids) {
        throw new UnsupportedOperationException();
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
    @Override
    public boolean canResetNode(final NodeID nodeID) {
        return false;
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
    @Override
    public void resetAndConfigureNode(final NodeID id) {
        throw new UnsupportedOperationException();
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
    @Override
    public boolean canExecuteNode(final NodeID nodeID) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canCancelNode(final NodeID nodeID) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canCancelAll() {
        //TODO
        return false;
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
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canExecuteAll() {
        //TODO
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void executeAll() {
        throw new UnsupportedOperationException();
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
        return nodes.stream().map(n -> getAccess().getNodeContainer(n))
            .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<ConnectionContainerUI> getConnectionContainers() {
        //TODO e.g. put the entities into a hash map for quicker access
        List<? extends ConnectionEnt> connections = getWorkflow().getConnections();
        //return exactly the same connection container instance for the same connection entity
        return connections.stream().map(c -> getAccess().getConnectionContainer(c)).collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeContainerUI getNodeContainer(final NodeID id) {
        //TODO e.g. put the node entities into a hash map for quicker access
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
     */
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
    public boolean isEncrypted() {
        return getEntity().isEncrypted();
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
        //get underlying port
        return getAccess().getWorkflowInPort(getEntity().getInPorts().get(index), null, getEntity());
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
        return getWorkflow().getWorkflowAnnotations().stream()
            .map(wa -> getAccess().getWorkflowAnnotation(wa)).collect(Collectors.toList());
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

}
