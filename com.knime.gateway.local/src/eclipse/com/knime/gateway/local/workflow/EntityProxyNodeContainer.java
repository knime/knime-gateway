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

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeFactory.NodeType;
import org.knime.core.node.NodeSettings;
import org.knime.core.node.config.base.ConfigBaseRO;
import org.knime.core.node.config.base.JSONConfig;
import org.knime.core.node.util.NodeExecutionJobManagerPool;
import org.knime.core.node.workflow.NodeAnnotation;
import org.knime.core.node.workflow.NodeAnnotationData;
import org.knime.core.node.workflow.NodeContainer.NodeLock;
import org.knime.core.node.workflow.NodeContainer.NodeLocks;
import org.knime.core.node.workflow.NodeContainerState;
import org.knime.core.node.workflow.NodeExecutionJobManager;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.NodeMessage;
import org.knime.core.node.workflow.NodeMessageEvent;
import org.knime.core.node.workflow.NodeMessageListener;
import org.knime.core.node.workflow.NodeProgressEvent;
import org.knime.core.node.workflow.NodeProgressListener;
import org.knime.core.node.workflow.NodePropertyChangedListener;
import org.knime.core.node.workflow.NodeStateChangeListener;
import org.knime.core.node.workflow.NodeStateEvent;
import org.knime.core.node.workflow.NodeUIInformation;
import org.knime.core.node.workflow.NodeUIInformationEvent;
import org.knime.core.node.workflow.NodeUIInformationListener;
import org.knime.core.ui.node.workflow.NodeContainerUI;
import org.knime.core.ui.node.workflow.NodeInPortUI;
import org.knime.core.ui.node.workflow.NodeOutPortUI;
import org.knime.core.ui.node.workflow.WorkflowManagerUI;

import com.knime.gateway.local.util.EntityProxyUtil;
import com.knime.gateway.v0.entity.NodeAnnotationEnt;
import com.knime.gateway.v0.entity.NodeEnt;
import com.knime.gateway.v0.entity.NodeMessageEnt;
import com.knime.gateway.v0.entity.NodeUIInfoEnt;

/**
 * Entity-proxy class that proxies {@link NodeEnt} and implements {@link NodeContainerUI}.
 *
 * @author Martin Horn, University of Konstanz
 * @param <E>
 */
public abstract class EntityProxyNodeContainer<E extends NodeEnt> extends AbstractEntityProxy<E>
    implements NodeContainerUI {

    /**
     * Map that keeps track of all root workflow ids and maps them to a unique node ids. It's the id the will be
     * prepended to the node's id (see {@link #getID()}).
     *
     * TODO: remove worklfow id's from the list that aren't in memory anymore
     */
    private static final Map<UUID, String> ROOT_ID_MAP = new HashMap<UUID, String>();

    private NodeAnnotation m_nodeAnnotation;

    private WorkflowManagerUI m_parent;

    /*--------- listener administration------------*/

    private final CopyOnWriteArraySet<NodeStateChangeListener> m_stateChangeListeners =
        new CopyOnWriteArraySet<NodeStateChangeListener>();

    private final CopyOnWriteArraySet<NodeMessageListener> m_messageListeners =
        new CopyOnWriteArraySet<NodeMessageListener>();

    private final CopyOnWriteArraySet<NodeProgressListener> m_progressListeners =
        new CopyOnWriteArraySet<NodeProgressListener>();

    private final CopyOnWriteArraySet<NodeUIInformationListener> m_uiListeners =
        new CopyOnWriteArraySet<NodeUIInformationListener>();

    private final CopyOnWriteArraySet<NodePropertyChangedListener> m_nodePropertyChangedListeners =
        new CopyOnWriteArraySet<NodePropertyChangedListener>();

    /**
     * If the underlying entity is a node.
     *
     * @param node
     * @param access
     *
     */
    public EntityProxyNodeContainer(final E node, final EntityProxyAccess access) {
        super(node, access);
        ROOT_ID_MAP.computeIfAbsent(node.getRootWorkflowID(), s -> String.valueOf(ROOT_ID_MAP.size() + 1));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void progressChanged(final NodeProgressEvent pe) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorkflowManagerUI getParent() {
        if (m_parent != null) {
            return m_parent;
        }
        if (getEntity().getParentNodeID() != null) {
            //get parent wf
            String parentNodeID;
            if ("root".equals(getEntity().getParentNodeID())) {
                //parent is the highest level workflow
                //the node id has then no meaning here and need to be empty
                parentNodeID = null;
            } else {
                parentNodeID = getEntity().getParentNodeID();
            }
            m_parent = getAccess().getWorkflowManager(getEntity().getRootWorkflowID(), parentNodeID);
            return m_parent;
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeExecutionJobManager getJobManager() {
        if (getEntity().getJobManager() != null) {
            return NodeExecutionJobManagerPool.getJobManagerFactory(getEntity().getJobManager().getId()).getInstance();
        } else if (getParent() == null) {
            //if it's the root workflow and no job manager set, return the default one
            return NodeExecutionJobManagerPool.getDefaultJobManagerFactory().getInstance();
        } else {
            //if there is no job manager set nor it's the root workflow
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeExecutionJobManager findJobManager() {
        //optionally derive the job manager from the parent
        if (getEntity().getJobManager() != null) {
            return NodeExecutionJobManagerPool.getJobManagerFactory(getEntity().getJobManager().getId()).getInstance();
        } else {
            if (getParent() == null) {
                //if it's the root workflow, there must be a job manager set
                return getJobManager();
            } else {
                return getParent().findJobManager();
            }

        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addNodePropertyChangedListener(final NodePropertyChangedListener l) {
        return m_nodePropertyChangedListeners.add(l);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeNodePropertyChangedListener(final NodePropertyChangedListener l) {
        return m_nodePropertyChangedListeners.remove(l);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearWaitingLoopList() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addProgressListener(final NodeProgressListener listener) {
        if (listener == null) {
            throw new NullPointerException("Node progress listener must not be null");
        }
        return m_progressListeners.add(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeNodeProgressListener(final NodeProgressListener listener) {
        return m_progressListeners.add(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addNodeMessageListener(final NodeMessageListener listener) {
        if (listener == null) {
            throw new NullPointerException("Node message listner must not be null!");
        }
        return m_messageListeners.add(listener);
    }

    /**
     * Notifies the registered node message listeners.
     *
     * @param event essentially the new node message
     */
    protected void notifyNodeMessageListener(final NodeMessageEvent event) {
        m_messageListeners.forEach(l -> l.messageChanged(event));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeNodeMessageListener(final NodeMessageListener listener) {
        return m_messageListeners.remove(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeMessage getNodeMessage() {
        NodeMessageEnt nme = getEntity().getNodeMessage();
        return new NodeMessage(NodeMessage.Type.valueOf(nme.getType()), nme.getMessage());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setNodeMessage(final NodeMessage newMessage) {
        throw new UnsupportedOperationException();
        //        service(NodeService.class).updateNode(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addUIInformationListener(final NodeUIInformationListener l) {
        if (l == null) {
            throw new NullPointerException("NodeUIInformationListener must not be null!");
        }
        m_uiListeners.add(l);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeUIInformationListener(final NodeUIInformationListener l) {
        m_uiListeners.remove(l);

    }

    /**
     * Notifies the registered UI listeners.
     *
     * @param evt some additional information about the actual event
     */
    protected void notifyUIListeners(final NodeUIInformationEvent evt) {
        for (NodeUIInformationListener l : m_uiListeners) {
            l.nodeUIInformationChanged(evt);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeUIInformation getUIInformation() {
        NodeUIInfoEnt uiInfo = getEntity().getUIInfo();
        return NodeUIInformation.builder()
            .setNodeLocation(uiInfo.getBounds().getX(), uiInfo.getBounds().getY(), uiInfo.getBounds().getWidth(),
                uiInfo.getBounds().getHeight())
            .setIsSymbolRelative(uiInfo.isSymbolRelative())
            .setHasAbsoluteCoordinates(uiInfo.isHasAbsoluteCoordinates())
            .setIsDropLocation(uiInfo.isDropLocation())
            .setSnapToGrid(uiInfo.isSnapToGrid()).build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUIInformation(final NodeUIInformation uiInformation) {
        //        service(NodeService.class).updateNode(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addNodeStateChangeListener(final NodeStateChangeListener listener) {
        if (listener == null) {
            throw new NullPointerException("Node state change listener must not be null!");
        }
        return m_stateChangeListeners.add(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeNodeStateChangeListener(final NodeStateChangeListener listener) {
        return m_stateChangeListeners.remove(listener);
    }

    /**
     * Notifies the node state change listeners (e.g. the UI).
     *
     * @param state the new node state
     */
    protected void notifyNodeStateChangeListener(final NodeStateEvent state) {
        m_stateChangeListeners.forEach(l -> l.stateChanged(state));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeContainerState getNodeContainerState() {
        return getNodeContainerState(getEntity());
    }

    static NodeContainerState getNodeContainerState(final NodeEnt node) {
        return EntityProxyNodeContainerState.valueOf(node.getNodeState().toString());
    }

    /** {@inheritDoc} */
    @Override
    public ConfigBaseRO getNodeSettings() {
        String json = getAccess().getSettingsAsJson(getEntity());
        try {
            return JSONConfig.readJSON(new NodeSettings("settings"), new StringReader(json));
        } catch (IOException ex) {
            throw new RuntimeException("Unable to read NodeSettings from XML String", ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasDataAwareDialogPane() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAllInputDataAvailable() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canExecuteUpToHere() {
        throw new UnsupportedOperationException();
        //        return service(ExecutionService.class).getCanExecuteUpToHere(null, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void applySettingsFromDialog() throws InvalidSettingsException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean areDialogSettingsValid() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasDialog() {
        return getEntity().isHasDialog();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean areDialogAndNodeSettingsEqual() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNrInPorts() {
        return getEntity().getInPorts().size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeInPortUI getInPort(final int index) {
        return getAccess().getNodeInPort(getEntity().getInPorts().get(index));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeOutPortUI getOutPort(final int index) {
        return getAccess().getNodeOutPort(getEntity().getOutPorts().get(index), getEntity());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNrOutPorts() {
        return getEntity().getOutPorts().size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNrViews() {
        //TODO
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNrNodeViews() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getViewName(final int i) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getNodeViewName(final int i) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasInteractiveView() {
        //TODO
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getInteractiveViewName() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeType getType() {
        return NodeType.valueOf(getEntity().getNodeType().toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeID getID() {
        return EntityProxyUtil.stringToNodeID(ROOT_ID_MAP.get(getEntity().getRootWorkflowID()),
            getEntity().getNodeID());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return getEntity().getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getNameWithID() {
        return getName() + " " + getID().toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayLabel() {
        //copied from NodeContainer
        String label = getID().toString() + " - " + getName();
        // if this node has an annotation add the first line to the label - TODO
        //        String customLabel = getDisplayCustomLine();
        //        if (!customLabel.isEmpty()) {
        //            label += " (" + customLabel + ")";
        //        }
        return label;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCustomName() {
        //TODO
        return "TODO custom name";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeAnnotation getNodeAnnotation() {
        if (m_nodeAnnotation == null) {
            NodeAnnotationEnt anno = getEntity().getNodeAnnotation();
            NodeAnnotationData data = NodeAnnotationData.createFromObsoleteCustomName(null);
            if (!anno.isDefault()) {
                data.copyFrom(EntityProxyWorkflowAnnotation.getAnnotationData(anno), false);
            }
            m_nodeAnnotation = new NodeAnnotation(data);
            m_nodeAnnotation.registerOnNodeContainer(getID(), () -> setDirty());
            addUIInformationListener(m_nodeAnnotation);
        }
        return m_nodeAnnotation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCustomDescription() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCustomDescription(final String customDescription) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDeletable(final boolean value) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDeletable() {
        return getEntity().isDeletable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDirty() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDirty() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void changeNodeLocks(final boolean setLock, final NodeLock... locks) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeLocks getNodeLocks() {
        //TODO
        return new NodeLocks(false, false, false);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void update(final NodeEnt entity) {
        super.update((E)entity);
        notifyNodeStateChangeListener(new NodeStateEvent(getID()));
        notifyNodeMessageListener(new NodeMessageEvent(getID(), getNodeMessage()));
    }
}
