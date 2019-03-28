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

import static com.knime.gateway.entity.EntityBuilderManager.builder;
import static com.knime.gateway.local.workflow.WorkflowEntChangeProcessor.processChanges;
import static com.knime.gateway.util.EntityBuilderUtil.buildConnectionEnt;
import static com.knime.gateway.util.EntityUtil.connectionIDToString;
import static com.knime.gateway.util.EntityUtil.nodeIDToString;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeSettings;
import org.knime.core.node.config.base.JSONConfig;
import org.knime.core.node.config.base.JSONConfig.WriterConfig;
import org.knime.core.node.dialog.ExternalNodeData;
import org.knime.core.node.port.MetaPortInfo;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.PortTypeRegistry;
import org.knime.core.node.workflow.ConnectionContainer.ConnectionType;
import org.knime.core.node.workflow.ConnectionID;
import org.knime.core.node.workflow.EditorUIInformation;
import org.knime.core.node.workflow.FlowVariable;
import org.knime.core.node.workflow.NodeAnnotation;
import org.knime.core.node.workflow.NodeContainerState;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.NodeMessage;
import org.knime.core.node.workflow.NodeMessage.Type;
import org.knime.core.node.workflow.NodeStateEvent;
import org.knime.core.node.workflow.NodeUIInformation;
import org.knime.core.node.workflow.NodeUIInformationEvent;
import org.knime.core.node.workflow.WorkflowAnnotation;
import org.knime.core.node.workflow.WorkflowAnnotationID;
import org.knime.core.node.workflow.WorkflowCopyContent;
import org.knime.core.node.workflow.WorkflowEvent;
import org.knime.core.node.workflow.WorkflowListener;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.ui.node.workflow.ConnectionContainerUI;
import org.knime.core.ui.node.workflow.NodeContainerUI;
import org.knime.core.ui.node.workflow.NodeInPortUI;
import org.knime.core.ui.node.workflow.NodeOutPortUI;
import org.knime.core.ui.node.workflow.RemoteWorkflowContext;
import org.knime.core.ui.node.workflow.SubNodeContainerUI;
import org.knime.core.ui.node.workflow.WorkflowContextUI;
import org.knime.core.ui.node.workflow.WorkflowCopyWithOffsetUI;
import org.knime.core.ui.node.workflow.WorkflowInPortUI;
import org.knime.core.ui.node.workflow.WorkflowManagerUI;
import org.knime.core.ui.node.workflow.WorkflowOutPortUI;
import org.knime.core.ui.node.workflow.async.AsyncNodeContainerUI;
import org.knime.core.ui.node.workflow.async.AsyncWorkflowManagerUI;
import org.knime.core.ui.node.workflow.async.CompletableFutureEx;
import org.knime.core.ui.node.workflow.async.OperationNotAllowedException;
import org.knime.core.ui.node.workflow.async.SnapshotNotFoundException;
import org.knime.core.ui.node.workflow.lazy.LazyWorkflowManagerUI;
import org.knime.core.ui.node.workflow.lazy.NotLoadedException;
import org.knime.core.util.Pair;

import com.knime.gateway.entity.ConnectionEnt;
import com.knime.gateway.entity.MetaPortInfoEnt;
import com.knime.gateway.entity.NodeEnt;
import com.knime.gateway.entity.NodeFactoryKeyEnt.NodeFactoryKeyEntBuilder;
import com.knime.gateway.entity.PatchEnt;
import com.knime.gateway.entity.PortTypeEnt;
import com.knime.gateway.entity.WorkflowAnnotationEnt;
import com.knime.gateway.entity.WorkflowEnt;
import com.knime.gateway.entity.WorkflowNodeEnt;
import com.knime.gateway.entity.WorkflowPartsEnt;
import com.knime.gateway.entity.WorkflowSnapshotEnt;
import com.knime.gateway.entity.WorkflowUIInfoEnt;
import com.knime.gateway.entity.WrappedWorkflowNodeEnt;
import com.knime.gateway.local.patch.EntityPatchApplierManager;
import com.knime.gateway.local.workflow.WorkflowEntChangeProcessor.WorkflowEntChangeListener;
import com.knime.gateway.service.util.ServiceExceptions.ActionNotAllowedException;
import com.knime.gateway.service.util.ServiceExceptions.InvalidRequestException;
import com.knime.gateway.service.util.ServiceExceptions.NodeNotFoundException;
import com.knime.gateway.service.util.ServiceExceptions.NotASubWorkflowException;
import com.knime.gateway.service.util.ServiceExceptions.NotFoundException;
import com.knime.gateway.util.EntityBuilderUtil;
import com.knime.gateway.util.EntityTranslateUtil;
import com.knime.gateway.util.EntityUtil;

/**
 * Abstract {@link WorkflowManagerUI} implementation that wraps (and therewith retrieves its information) from a
 * {@link WorkflowNodeEnt} that represents a workflow most likely received remotely.
 *
 * @author Martin Horn, University of Konstanz
 * @param <E> the type of the workflow node entity (e.g. wrapped)
 */
abstract class AbstractEntityProxyWorkflowManager<E extends WorkflowNodeEnt> extends AbstractEntityProxyNodeContainer<E>
    implements AsyncWorkflowManagerUI, LazyWorkflowManagerUI {

    /**
     * The name of the property of a node entity holding it's state as defined by the gateway API.
     */
    private static final String NODESTATE_PROPERTY = "nodeState";

    private WorkflowEnt m_workflowEnt;

    private UUID m_snapshotID;

    private boolean m_isDisconnected = false;

    private final List<Runnable> m_writeProtectionChangedListeners = new ArrayList<Runnable>();

    private CopyOnWriteArrayList<WorkflowListener> m_wfmListeners = new CopyOnWriteArrayList<WorkflowListener>();

    /* Lock to ensure that only one refresh happens at a time and refreshes are not getting queued up */
    private final ReentrantLock m_refreshLock = new ReentrantLock();

    /* Listener to apply workflow patches for update/refresh */
    private final WorkflowEntChangeListener m_workflowEntChangeListener = new MyWorkflowEntChangeListener();

    private RemoteWorkflowContext m_workflowContext;

    AbstractEntityProxyWorkflowManager(final E workflowNodeEnt, final EntityProxyAccess access) {
        this(workflowNodeEnt, access, null);
    }

    AbstractEntityProxyWorkflowManager(final E workflowNodeEnt, final EntityProxyAccess access,
        final RemoteWorkflowContext workflowContext) {
        super(workflowNodeEnt, access);
        m_workflowContext = workflowContext;
    }

    private WorkflowEnt getWorkflow() throws NotLoadedException {
        if (m_workflowEnt == null) {
            throw new NotLoadedException();
        }
        return m_workflowEnt;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isLoaded() {
        return m_workflowEnt != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<Void> load() {
        return CompletableFuture.runAsync(() -> {
            downloadWorkflowEnt();
        });
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
    public void removeProject(final NodeID id) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     *
     * Logic mainly copied from {@link WorkflowManager#canRemoveNode(NodeID)}
     */
    @Override
    public boolean canRemoveNode(final NodeID nodeID) {
        NodeContainerUI nc = getNodeContainer(nodeID);
        if (getNodeContainer(nodeID) == null) {
            return false;
        }
        if (nc.getNodeContainerState().isExecutionInProgress()) {
            return false;
        }
        if (!nc.isDeletable()) {
            return false;
        }
        for (ConnectionContainerUI c : getOutgoingConnectionsFor(nodeID)) {
            if (!c.isDeletable()) {
                return false;
            }
        }
        for (ConnectionContainerUI c : getIncomingConnectionsFor(nodeID)) {
            if (!c.isDeletable()) {
                return false;
            }
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFutureEx<Void, OperationNotAllowedException> removeAsync(final NodeID[] nodeIDs, final ConnectionID[] connectionIDs,
        final WorkflowAnnotationID[] annotationIDs) {
        return futureExRefresh(() -> {
            WorkflowPartsEnt parts =
                EntityBuilderUtil.buildWorkflowPartsEnt(getID(), nodeIDs, connectionIDs, annotationIDs);
            try {
                getAccess().workflowService().deleteWorkflowParts(getEntity().getRootWorkflowID(), parts, false);
            } catch (NotASubWorkflowException | NodeNotFoundException ex) {
                //should never happen
                throw new CompletionException(ex);
            } catch (ActionNotAllowedException ex) {
                throw new CompletionException(new OperationNotAllowedException(ex.getMessage(), ex));
            }
            return null;
        }, OperationNotAllowedException.class);
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
    public CompletableFuture<NodeID> createAndAddNodeAsync(final NodeFactory<?> factory, final NodeUIInformation uiInfo) {
        if (!uiInfo.hasAbsoluteCoordinates()) {
            throw new IllegalArgumentException("Relative node coordinates are not supported");
        }
        return futureRefresh(() -> {
            String id;
            try {
                NodeFactoryKeyEntBuilder factoryKeyBuilder =
                    builder(NodeFactoryKeyEntBuilder.class).setClassName(factory.getClass().getCanonicalName());
                NodeSettings settings = new NodeSettings("settings");
                factory.saveAdditionalFactorySettings(settings);
                if (settings.getChildCount() > 0) {
                    factoryKeyBuilder.setSettings(JSONConfig.toJSONString(settings, WriterConfig.DEFAULT));
                }
                id = getAccess().nodeService().createNode(getEntity().getRootWorkflowID(), uiInfo.getBounds()[0],
                    uiInfo.getBounds()[1], factoryKeyBuilder.build(), getEntity().getNodeID());
            } catch (NotASubWorkflowException | NodeNotFoundException | InvalidRequestException ex) {
                //should never happen
                throw new CompletionException(ex);
            }
            return getAccess().getNodeID(getEntity().getRootWorkflowID(), id);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canAddConnection(final NodeID source, final int sourcePort, final NodeID dest, final int destPort) {
        //always allow the connection to be added and fail later on when trying
        //TODO add some basic logic for checking
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canAddNewConnection(final NodeID source, final int sourcePort, final NodeID dest,
        final int destPort) {
        //always allow the connection to be added and fail later on when trying
        //TODO add some basic logic for checking
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * Most logic copied from
     * {@link WorkflowManager#canRemoveConnection(org.knime.core.node.workflow.ConnectionContainer)}
     */
    @Override
    public boolean canRemoveConnection(final ConnectionContainerUI cc) {
        if (cc == null || !cc.isDeletable()) {
            return false;
        }
        NodeID destID = cc.getDest();
        NodeID sourceID = cc.getSource();
        // make sure both nodes (well, their connection lists) exist
        if (getIncomingConnectionsFor(destID).isEmpty()) {
            return false;
        }
        if (getOutgoingConnectionsFor(sourceID).isEmpty()) {
            return false;
        }
        // make sure connection between those two nodes exists
        if (!getIncomingConnectionsFor(destID).contains(cc)) {
            return false;
        }
        if (!getOutgoingConnectionsFor(sourceID).contains(cc)) {
            return false;
        }
        //TODO: following logic cannot be duplicated and a server request would
        //be required here - i.e. removal would be allowed but will fail, when carried out
        //        if (destID.equals(getID())) { // wfm out connection
        //            // note it is ok if the WFM itself is executing...
        //            if (getParent().hasSuccessorInProgress(getID())) {
        //                return false;
        //            }
        //        } else {
        final NodeContainerUI nc = getNodeContainer(destID);
        if (nc != null) {
            NodeContainerState state = nc.getNodeContainerState();
            if (state.isExecutionInProgress() || (state.isExecuted() && !canResetNode(destID))) {
                return false;
            }
        }
        //        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFutureEx<ConnectionContainerUI, OperationNotAllowedException> addConnectionAsync(
        final NodeID source, final int sourcePort, final NodeID dest, final int destPort, final int[][] bendpoints) {
        return futureExRefresh(() -> {
            // determine connection type
            NodeEnt sourceNode = getWorkflow().getNodes().get(nodeIDToString(source));
            NodeEnt destNode = getWorkflow().getNodes().get(nodeIDToString(dest));
            ConnectionType type;
            if ((sourceNode == null) && (destNode == null)) {
                type = ConnectionType.WFMTHROUGH;
            } else if (sourceNode == null) {
                type = ConnectionType.WFMIN;
            } else if (destNode == null) {
                type = ConnectionType.WFMOUT;
            } else {
                type = ConnectionType.STD;
            }
            ConnectionEnt connectionEnt = buildConnectionEnt(source, sourcePort, dest, destPort, type, bendpoints);
            try {
                getAccess().workflowService().createConnection(getEntity().getRootWorkflowID(), connectionEnt);
            } catch (ActionNotAllowedException ex) {
                throw new CompletionException(
                    new OperationNotAllowedException("Adding a connection is not allowed here", ex));
            }
            return getAccess().getConnectionContainer(connectionEnt, getEntity().getRootWorkflowID());
        }, OperationNotAllowedException.class);
    }

    /**
     * {@inheritDoc}
     *
     * @throws NotLoadedException when {@link #isLoaded()} returns <code>false</code>
     */
    @Override
    public Set<ConnectionContainerUI> getOutgoingConnectionsFor(final NodeID id, final int portIdx)
        throws NotLoadedException {
        //TODO introduce a more efficient data structure to access the right connection
        Set<ConnectionContainerUI> res = new HashSet<ConnectionContainerUI>();
        String nodeID = nodeIDToString(id);
        for (ConnectionEnt c : getWorkflow().getConnections().values()) {
            if (c.getSource().equals(nodeID) && c.getSourcePort() == portIdx) {
                res.add(getAccess().getConnectionContainer(c, getEntity().getRootWorkflowID()));
            }
        }
        return res;
    }

    /**
     * {@inheritDoc}
     *
     * @throws NotLoadedException when {@link #isLoaded()} returns <code>false</code>
     */
    @Override
    public Set<ConnectionContainerUI> getOutgoingConnectionsFor(final NodeID id) throws NotLoadedException {
        //TODO introduce a more efficient data structure to access the right connection
        Set<ConnectionContainerUI> res = new HashSet<ConnectionContainerUI>();
        String nodeID = nodeIDToString(id);
        for (ConnectionEnt c : getWorkflow().getConnections().values()) {
            if (c.getSource().equals(nodeID)) {
                res.add(getAccess().getConnectionContainer(c, getEntity().getRootWorkflowID()));
            }
        }
        return res;
    }

    /**
     * {@inheritDoc}
     *
     * @throws NotLoadedException when {@link #isLoaded()} returns <code>false</code>
     */
    @Override
    public ConnectionContainerUI getIncomingConnectionFor(final NodeID id, final int portIdx)
        throws NotLoadedException {
        String connID = connectionIDToString(nodeIDToString(id), portIdx);
        return getAccess().getConnectionContainer(getWorkflow().getConnections().get(connID),
            getEntity().getRootWorkflowID());
    }

    /**
     * {@inheritDoc}
     *
     * @throws NotLoadedException when {@link #isLoaded()} returns <code>false</code>
     */
    @Override
    public Set<ConnectionContainerUI> getIncomingConnectionsFor(final NodeID id) throws NotLoadedException {
        //TODO introduce a more efficient data structure to access the right connection
        Set<ConnectionContainerUI> res = new HashSet<ConnectionContainerUI>();
        String nodeID = nodeIDToString(id);
        for (ConnectionEnt c : getWorkflow().getConnections().values()) {
            if (c.getDest().equals(nodeID)) {
                res.add(getAccess().getConnectionContainer(c, getEntity().getRootWorkflowID()));
            }
        }
        return res;
    }

    /**
     * {@inheritDoc}
     *
     * @throws NotLoadedException when {@link #isLoaded()} returns <code>false</code>
     */
    @Override
    public ConnectionContainerUI getConnection(final ConnectionID id) throws NotLoadedException {
        ConnectionEnt connectionEnt = getWorkflow().getConnections().get(connectionIDToString(id));
        return getAccess().getConnectionContainer(connectionEnt, getEntity().getRootWorkflowID());
    }

    /**
     * {@inheritDoc}
     *
     * @throws NotLoadedException when {@link #isLoaded()} returns <code>false</code>
     */
    @Override
    public MetaPortInfo[] getMetanodeInputPortInfo(final NodeID metaNodeID) throws NotLoadedException {
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
     *
     * @throws NotLoadedException when {@link #isLoaded()} returns <code>false</code>
     */
    @Override
    public MetaPortInfo[] getMetanodeOutputPortInfo(final NodeID metaNodeID) throws NotLoadedException {
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
            assert nc instanceof AbstractEntityProxyNodeContainer;
            ((AbstractEntityProxyNodeContainer)nc).execute();
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
    @SuppressWarnings("rawtypes")
    @Override
    public boolean canResetNode(final NodeID nodeID) {
        if(isWriteProtected()) {
            return false;
        }
        //TODO ask server whether the node can be reset (i.e. whether there are executing successors etc.)
        //very simple (but not complete!) logic to check whether a node can be reset
        NodeContainerUI nc = getNodeContainer(nodeID);
        if(nc == null) {
            return false;
        }
        assert nc instanceof AbstractEntityProxyNodeContainer;
        return ((AbstractEntityProxyNodeContainer) nc).canReset();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("rawtypes")
    @Override
    public void resetAndConfigureNode(final NodeID id) {
        NodeContainerUI nc = getNodeContainer(id);
        assert nc instanceof AbstractEntityProxyNodeContainer;
        ((AbstractEntityProxyNodeContainer) nc).reset();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("rawtypes")
    @Override
    public boolean canExecuteNode(final NodeID nodeID) {
        if (isWriteProtected()) {
            return false;
        }
        NodeContainerUI nc = getNodeContainer(nodeID);
        if (nc == null) {
            return false;
        }
        assert nc instanceof AbstractEntityProxyNodeContainer;
        return ((AbstractEntityProxyNodeContainer)nc).canExecute();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canCancelNode(final NodeID nodeID) {
        if (isWriteProtected()) {
            return false;
        }
        NodeContainerUI nc = getNodeContainer(nodeID);
        if (nc == null) {
            return false;
        }
        return nc.getNodeContainerState().isExecutionInProgress();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canCancelAll() {
        if(isWriteProtected()) {
            return false;
        }
        return getNodeContainerState().isExecutionInProgress();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("rawtypes")
    @Override
    public void cancelExecution(final NodeContainerUI nc) {
        if (nc instanceof AbstractEntityProxyNodeContainer) {
            ((AbstractEntityProxyNodeContainer)nc).cancelExecution();
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
        if (isWriteProtected()) {
            return false;
        }
        //simple (and possibly not complete strategy) to determine whether the entire workflow can be executed
        return getNodeContainers().stream().anyMatch(nc -> {
            assert nc instanceof AbstractEntityProxyNodeContainer;
            @SuppressWarnings("rawtypes")
            AbstractEntityProxyNodeContainer epnc = (AbstractEntityProxyNodeContainer)nc;
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
     *
     * @throws NotLoadedException when {@link #isLoaded()} returns <code>false</code>
     */
    @Override
    public Collection<NodeContainerUI> getNodeContainers() throws NotLoadedException {
        Collection<NodeEnt> nodes = getWorkflow().getNodes().values();
        //return exactly the same node container instance for the same node entity
        return nodes.stream().map(n -> getAccess().getNodeContainer(n)).collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     *
     * @throws NotLoadedException when {@link #isLoaded()} returns <code>false</code>
     */
    @Override
    public Collection<ConnectionContainerUI> getConnectionContainers() throws NotLoadedException {
        Collection<? extends ConnectionEnt> connections = getWorkflow().getConnections().values();
        //return exactly the same connection container instance for the same connection entity
        return connections.stream().map(c -> getAccess().getConnectionContainer(c, getEntity().getRootWorkflowID()))
            .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     *
     * @throws NotLoadedException when {@link #isLoaded()} returns <code>false</code>
     */
    @Override
    public AsyncNodeContainerUI getNodeContainer(final NodeID id) throws NotLoadedException {
        final NodeEnt nodeEnt = getWorkflow().getNodes().get(nodeIDToString(id));
        //return exactly the same node container instance for the same node entity
        return getAccess().getNodeContainer(nodeEnt);
    }

    /**
     * {@inheritDoc}
     *
     * @throws NotLoadedException when {@link #isLoaded()} returns <code>false</code>
     */
    @Override
    public boolean containsNodeContainer(final NodeID id) throws NotLoadedException {
        return getWorkflow().getNodes().get(nodeIDToString(id)) != null;
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
        return m_isDisconnected || isInWizardExecution();
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
    public void addListener(final WorkflowListener listener) {
        if (!m_wfmListeners.contains(listener)) {
            m_wfmListeners.add(listener);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeListener(final WorkflowListener listener) {
        m_wfmListeners.remove(listener);
    }

    private final void notifyWorkflowListeners(final WorkflowEvent evt) {
        if (m_wfmListeners.isEmpty()) {
            return;
        }
        m_wfmListeners.forEach(l -> l.workflowChanged(evt));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<WorkflowCopyWithOffsetUI> copyAsync(final WorkflowCopyContent content) {
        return AsyncNodeContainerUI.future(() -> {
            WorkflowPartsEnt workflowPartsEnt = EntityBuilderUtil.buildWorkflowPartsEnt(getID(), content);
            UUID partsID;
            try {
                partsID = getAccess().workflowService().createWorkflowCopy(getEntity().getRootWorkflowID(),
                    workflowPartsEnt);
            } catch (NotASubWorkflowException | NodeNotFoundException | InvalidRequestException ex) {
                //should never happen
                throw new CompletionException(ex);
            }
            int[] offset = EntityProxyWorkflowCopy.calcOffset(content, this);
            return new EntityProxyWorkflowCopy(partsID, offset[0], offset[1]);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFutureEx<WorkflowCopyWithOffsetUI, OperationNotAllowedException>
        cutAsync(final WorkflowCopyContent content) {
        return futureExRefresh(() -> {
            WorkflowPartsEnt workflowPartsEnt = EntityBuilderUtil.buildWorkflowPartsEnt(getID(), content);
            UUID partsId;
            try {
                partsId = getAccess().workflowService().deleteWorkflowParts(getEntity().getRootWorkflowID(),
                    workflowPartsEnt, true);
            } catch (NotASubWorkflowException | NodeNotFoundException ex) {
                //should never happen
                throw new CompletionException(ex);
            } catch (ActionNotAllowedException ex) {
                throw new CompletionException(new OperationNotAllowedException(ex.getMessage(), ex));
            }
            int[] offset = EntityProxyWorkflowCopy.calcOffset(content, this);
            return new EntityProxyWorkflowCopy(partsId, offset[0], offset[1]);
        }, OperationNotAllowedException.class);

    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException if the workflow copy is not of type {@link EntityProxyWorkflowCopy}
     */
    @Override
    public CompletableFuture<WorkflowCopyContent> pasteAsync(final WorkflowCopyWithOffsetUI workflowCopy) {
        if (workflowCopy instanceof EntityProxyWorkflowCopy) {
            return futureRefresh(() -> {
                EntityProxyWorkflowCopy epwc = (EntityProxyWorkflowCopy)workflowCopy;
                WorkflowPartsEnt workflowPartsEnt;
                try {
                    workflowPartsEnt = getAccess().workflowService()
                        .pasteWorkflowParts(getEntity().getRootWorkflowID(), epwc.getPartsID(),
                            epwc.getX() + epwc.getXShift(), epwc.getY() + epwc.getYShift(), getEntity().getNodeID());
                } catch (NotASubWorkflowException ex) {
                    //should never happen
                    throw new CompletionException(ex);
                } catch (NotFoundException ex) {
                    //should almost never happen
                    throw new CompletionException("Workflow copy not available anymore", ex);
                }
                return EntityTranslateUtil.translateWorkflowPartsEnt(workflowPartsEnt,
                    s -> getAccess().getNodeID(getEntity().getRootWorkflowID(), s),
                    s -> getAccess().getAnnotationID(getEntity().getRootWorkflowID(), s));
            });
        } else {
            throw new IllegalArgumentException(
                "Only workflow copies of type '" + EntityProxyWorkflowCopy.class.getSimpleName() + "' allowed.");
        }
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
     *
     * @throws NotLoadedException when {@link #isLoaded()} returns <code>false</code>
     */
    @Override
    public EditorUIInformation getEditorUIInformation() throws NotLoadedException {
        WorkflowUIInfoEnt uiEnt = getWorkflow().getWorkflowUIInfo();
        if(uiEnt == null) {
            return null;
        }
        return EditorUIInformation.builder().setGridX(uiEnt.getGridX()).setGridY(uiEnt.getGridY())
            .setShowGrid(uiEnt.isShowGrid()).setSnapToGrid(uiEnt.isSnapToGrid())
            .setZoomLevel(uiEnt.getZoomLevel().doubleValue())
            .setHasCurvedConnections(uiEnt.hasCurvedConnection())
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
    public List<FlowVariable> getWorkflowVariables() {
        return getAccess().getOutputFlowVariableList(getEntity());
    }

    /**
     * {@inheritDoc}
     *
     * @throws NotLoadedException when {@link #isLoaded()} returns <code>false</code>
     */
    @Override
    public Collection<WorkflowAnnotation> getWorkflowAnnotations() throws NotLoadedException {
        return getWorkflow().getWorkflowAnnotations().values().stream()
            .map(wa -> getAccess().getWorkflowAnnotation(wa, getEntity().getRootWorkflowID(),
                getEntity().getNodeID()))
            .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     *
     * @throws NotLoadedException when {@link #isLoaded()} returns <code>false</code>
     */
    @Override
    public WorkflowAnnotation[] getWorkflowAnnotations(final WorkflowAnnotationID... ids) throws NotLoadedException {
        if (ids.length == 0) {
            return new WorkflowAnnotation[0];
        }
        return Arrays.stream(ids).map(waID -> {
            WorkflowAnnotationEnt ent =
                getWorkflow().getWorkflowAnnotations().get(EntityUtil.annotationIDToString(waID));
            return getAccess().getWorkflowAnnotation(ent, getEntity().getRootWorkflowID(),
                getEntity().getNodeID());
        }).toArray(size -> new WorkflowAnnotation[size]);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorkflowAnnotationID addWorkflowAnnotation(final WorkflowAnnotation annotation) {
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
    public WorkflowContextUI getContext() {
        if (m_workflowContext == null) {
            return getParent().getContext();
        } else {
            return m_workflowContext;
        }
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
    @SuppressWarnings("unchecked")
    @Override
    public CompletableFuture<Void> refreshAsync(final boolean deepRefresh) {
        return AsyncNodeContainerUI.future(() -> {
            refreshInternalOrReDownload(deepRefresh);
            return null;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void refreshOrFail(final boolean deepRefresh) throws SnapshotNotFoundException {
        try {
            refreshInternal(deepRefresh, false);
        } catch (NotFoundException ex) {
            throw new SnapshotNotFoundException(
                "Workflow cannot be refreshed. The snapshot is not available on the server (anymore).");
        }
    }

    private void refreshInternalOrReDownload(final boolean deepRefresh) {
        try {
            refreshInternal(deepRefresh, true);
        } catch (NotFoundException ex) {
            //doesn't happen since attempt is made to download entire workflow again
        }
    }

    private void refreshInternal(final boolean deepRefresh, final boolean reDownloadIfSnapshotNotFound)
        throws NotFoundException {
        //return immediately if there is currently a refresh already going on
        if (!m_refreshLock.tryLock()) {
            return;
        }

        try {
            //only refresh if the workflow was retrieved already
            if (m_workflowEnt != null) {
                WorkflowEnt oldWorkflow = m_workflowEnt;
                PatchEnt patch;
                try {
                    patch = updateWorkflowEntWithPatch();
                } catch (NotFoundException ex) {
                    if (reDownloadIfSnapshotNotFound) {
                        downloadWorkflowEnt();
                        patch = null;
                    } else {
                        throw ex;
                    }
                }

                if (oldWorkflow != m_workflowEnt) {
                    // refresh the workflow manager only if there is a new (updated) workflow entity

                    assert (m_snapshotID != null);
                    //update contained nodes
                    for (Entry<String, NodeEnt> entry : m_workflowEnt.getNodes().entrySet()) {
                        getAccess().updateNodeContainer(oldWorkflow.getNodes().get(entry.getKey()), entry.getValue());
                    }

                    //TODO: connections only get updated when their bendpoints change (not supported, yet)
                    //connections _don't_ get updated when they are replaced! Otherwise it will cause render problems.
                    //connections are replaced by adding the new and removing the old one

                    //update contained workflow annotations
                    for (Entry<String, WorkflowAnnotationEnt> entry : m_workflowEnt.getWorkflowAnnotations()
                        .entrySet()) {
                        getAccess().updateWorkflowAnnotation(oldWorkflow.getWorkflowAnnotations().get(entry.getKey()),
                            entry.getValue());
                    }
                    //post-update contained workflow annotations
                    for (WorkflowAnnotationEnt anno : m_workflowEnt.getWorkflowAnnotations().values()) {
                        getAccess().postUpdateWorkflowAnnotation(anno);
                    }

                    //refresh the workflow node entity, too, if it is the root workflow
                    //(e.g. that contains the state of this metanode)
                    if (getEntity().getNodeID().equals(EntityUtil.ROOT_NODE_ID)) {
                        //only try updating the root workflow node entity
                        //if there is at least one state change of the contained nodes
                        //(saves http-requests)
                        if (patch != null && patch.getOps().stream().anyMatch(o -> {
                            return o.getPath().contains(NODESTATE_PROPERTY);
                        })) {
                            try {
                                super.update((E)getAccess().nodeService().getNode(getEntity().getRootWorkflowID(),
                                    getEntity().getNodeID()));
                            } catch (NodeNotFoundException ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                    }
                    //apply patch changes on workflow-level
                    processChanges(patch, oldWorkflow, m_workflowEnt, m_workflowEntChangeListener);
                }

                if (deepRefresh) {
                    //refresh all contained workflows (i.e. metanodes)
                    for (NodeEnt node : m_workflowEnt.getNodes().values()) {
                        AsyncWorkflowManagerUI wfm = null;
                        //order of checking very important here since WrappedWorkflowNodeEnt is a subclass of WorkflowNodeEnt
                        if (node instanceof WrappedWorkflowNodeEnt) {
                            wfm = (AsyncWorkflowManagerUI)((SubNodeContainerUI)getAccess().getNodeContainer(node))
                                .getWorkflowManager();
                        } else if (node instanceof WorkflowNodeEnt) {
                            wfm = (AsyncWorkflowManagerUI)getAccess().getNodeContainer(node);
                        }
                        if (wfm != null) {
                            wfm.refreshAsync(true).get();
                        }
                    }
                }
            }
        } catch (InterruptedException | ExecutionException ex) {
            throw new CompletionException(ex);
        } finally {
            m_refreshLock.unlock();
        }
    }

    private PatchEnt updateWorkflowEntWithPatch() throws NotFoundException {
        PatchEnt patch = getAccess().getWorkflowPatch(getEntity(), m_snapshotID);
        if (!patch.getOps().isEmpty()) {
            m_workflowEnt = EntityPatchApplierManager.getPatchApplier().applyPatch(m_workflowEnt, patch);
            m_snapshotID = patch.getSnapshotID();
        }
        return patch;
    }

    private void downloadWorkflowEnt() {
        WorkflowSnapshotEnt wfs = getAccess().getWorkflowSnapshotEnt(getEntity());
        m_workflowEnt = wfs.getWorkflow();
        m_snapshotID = wfs.getSnapshotID();
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
     *
     * @throws NotLoadedException when {@link #isLoaded()} returns <code>false</code>
     */
    @Override
    public boolean isInWizardExecution() throws NotLoadedException {
        return getWorkflow().isInWizardExecution();
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDisconnected(final boolean disconnected) {
        boolean changed = m_isDisconnected != disconnected;
        m_isDisconnected = disconnected;
        if (changed) {
            //notify write protection changed listeners since #isWriteProtected depends on the connected-state
            m_writeProtectionChangedListeners.forEach(l -> l.run());
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws NotLoadedException when {@link #isLoaded()} returns <code>false</code>
     */
    @Override
    public boolean hasCredentials() throws NotLoadedException {
        return getWorkflow().hasCredentials();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addWriteProtectionChangedListener(final Runnable listener) {
        m_writeProtectionChangedListeners.add(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeWriteProtectionChangedListener(final Runnable listener) {
        m_writeProtectionChangedListeners.remove(listener);
    }

    /**
     * Creates a new {@link CompletableFuture} that also triggers a workflow refresh when completed.
     *
     * @param sup the actual stuff to run
     * @return a new future
     */
    private <U> CompletableFuture<U> futureRefresh(final Supplier<U> sup) {
        return CompletableFuture.supplyAsync(sup).thenApply(u -> {
            this.refreshInternalOrReDownload(false);
            return u;
        });
    }

    /**
     * Creates a new {@link CompletableFutureEx} that also triggers a workflow refresh when completed.
     *
     * @param sup the actual stuff to run
     * @param exceptionClass the possibly thrown exception when run with {@link CompletableFutureEx#getOrThrow()}
     * @return a new future
     */
    private <U, E extends Exception> CompletableFutureEx<U, E> futureExRefresh(final Supplier<U> sup,
        final Class<E> exceptionClass) {
        return new CompletableFutureEx<U, E>(CompletableFuture.supplyAsync(sup).thenApply(u -> {
            this.refreshInternalOrReDownload(false);
            return u;
        }), exceptionClass);
    }

    private class MyWorkflowEntChangeListener implements WorkflowEntChangeListener {

        /**
         * {@inheritDoc}
         */
        @Override
        public void nodeEntAdded(final NodeEnt newNode) {
            NodeContainerUI nc = getAccess().getNodeContainer(newNode);
            notifyWorkflowListeners(new WorkflowEvent(WorkflowEvent.Type.NODE_ADDED, nc.getID(), null, nc));

        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void nodeEntRemoved(final NodeEnt removedNode) {
            NodeContainerUI nc = getAccess().getNodeContainer(removedNode);
            notifyWorkflowListeners(new WorkflowEvent(WorkflowEvent.Type.NODE_REMOVED, nc.getID(), nc, null));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void connectionEntAdded(final ConnectionEnt newConnection) {
            EntityProxyConnectionContainer cc =
                getAccess().getConnectionContainer(newConnection, getEntity().getRootWorkflowID());
            notifyWorkflowListeners(new WorkflowEvent(WorkflowEvent.Type.CONNECTION_ADDED, null, null, cc));

        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void connectionEntRemoved(final ConnectionEnt removedConnection) {
            ConnectionContainerUI cc =
                getAccess().getConnectionContainer(removedConnection, getEntity().getRootWorkflowID());
            notifyWorkflowListeners(new WorkflowEvent(WorkflowEvent.Type.CONNECTION_REMOVED, null, cc, null));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void connectionEntReplaced(final ConnectionEnt oldConnection, final ConnectionEnt newConnection) {
            ConnectionContainerUI oldCC =
                getAccess().getConnectionContainer(oldConnection, getEntity().getRootWorkflowID());
            notifyWorkflowListeners(new WorkflowEvent(WorkflowEvent.Type.CONNECTION_REMOVED, null, oldCC, null));
            ConnectionContainerUI newCC =
                getAccess().getConnectionContainer(newConnection, getEntity().getRootWorkflowID());
            notifyWorkflowListeners(new WorkflowEvent(WorkflowEvent.Type.CONNECTION_ADDED, null, null, newCC));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void annotationEntAdded(final WorkflowAnnotationEnt newAnno) {
            notifyWorkflowListeners(new WorkflowEvent(WorkflowEvent.Type.ANNOTATION_ADDED, null, null, null));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void annotationEntRemoved(final WorkflowAnnotationEnt removedAnno) {
            notifyWorkflowListeners(new WorkflowEvent(WorkflowEvent.Type.ANNOTATION_REMOVED, null, null, null));
        }

    }
}
