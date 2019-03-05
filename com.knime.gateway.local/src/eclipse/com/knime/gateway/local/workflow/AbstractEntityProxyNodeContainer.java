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

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CopyOnWriteArraySet;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory.NodeType;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.config.base.ConfigBaseRO;
import org.knime.core.node.util.NodeExecutionJobManagerPool;
import org.knime.core.node.workflow.NodeAnnotation;
import org.knime.core.node.workflow.NodeAnnotationData;
import org.knime.core.node.workflow.NodeContainer.NodeLock;
import org.knime.core.node.workflow.NodeContainer.NodeLocks;
import org.knime.core.node.workflow.NodeContainerState;
import org.knime.core.node.workflow.NodeContext;
import org.knime.core.node.workflow.NodeExecutionJobManager;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.NodeMessage;
import org.knime.core.node.workflow.NodeMessageEvent;
import org.knime.core.node.workflow.NodeMessageListener;
import org.knime.core.node.workflow.NodeProgress;
import org.knime.core.node.workflow.NodeProgressEvent;
import org.knime.core.node.workflow.NodeProgressListener;
import org.knime.core.node.workflow.NodePropertyChangedListener;
import org.knime.core.node.workflow.NodeStateChangeListener;
import org.knime.core.node.workflow.NodeStateEvent;
import org.knime.core.node.workflow.NodeUIInformation;
import org.knime.core.node.workflow.NodeUIInformationEvent;
import org.knime.core.node.workflow.NodeUIInformationListener;
import org.knime.core.ui.node.workflow.InteractiveWebViewsResultUI;
import org.knime.core.ui.node.workflow.NodeContainerUI;
import org.knime.core.ui.node.workflow.NodeInPortUI;
import org.knime.core.ui.node.workflow.NodeOutPortUI;
import org.knime.core.ui.node.workflow.RemoteWorkflowContext;
import org.knime.core.ui.node.workflow.WorkflowManagerUI;
import org.knime.core.ui.node.workflow.async.AsyncNodeContainerUI;
import org.knime.core.ui.node.workflow.async.AsyncWorkflowManagerUI;
import org.knime.core.ui.node.workflow.async.CompletableFutureEx;

import com.knime.gateway.util.EntityTranslateUtil;
import com.knime.gateway.util.EntityUtil;
import com.knime.gateway.v0.entity.BoundsEnt;
import com.knime.gateway.v0.entity.BoundsEnt.BoundsEntBuilder;
import com.knime.gateway.v0.entity.NodeAnnotationEnt;
import com.knime.gateway.v0.entity.NodeEnt;
import com.knime.gateway.v0.entity.NodeMessageEnt;
import com.knime.gateway.v0.entity.NodeStateEnt;
import com.knime.gateway.v0.entity.NodeStateEnt.StateEnum;
import com.knime.gateway.v0.service.util.ServiceExceptions.ActionNotAllowedException;
import com.knime.gateway.v0.service.util.ServiceExceptions.NodeNotFoundException;

/**
 * Entity-proxy class that proxies {@link NodeEnt} and implements {@link NodeContainerUI}.
 *
 * @author Martin Horn, University of Konstanz
 * @param <E>
 */
public abstract class AbstractEntityProxyNodeContainer<E extends NodeEnt> extends AbstractEntityProxy<E>
    implements AsyncNodeContainerUI {

    /**
     * The old entity used prior the update.
     */
    private E m_oldEntity;

    private NodeAnnotation m_nodeAnnotation;

    private AsyncWorkflowManagerUI m_parent;

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

    private NodeUIInformation m_uiInfo;

    /**
     * See {@link AbstractEntityProxy#AbstractEntityProxy(com.knime.gateway.entity.GatewayEntity, EntityProxyAccess)}.
     *
     * @param node
     * @param access
     *
     */
    AbstractEntityProxyNodeContainer(final E node, final EntityProxyAccess access) {
        super(node, access);
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
    public AsyncWorkflowManagerUI getParent() {
        if (m_parent != null) {
            return m_parent;
        }
        if (getEntity().getParentNodeID() != null) {
            //get parent wf
            String parentNodeID;
            if (EntityUtil.ROOT_NODE_ID.equals(getEntity().getParentNodeID())) {
                //parent is the highest level workflow
                //the node id has then no meaning here and need to be empty
                parentNodeID = null;
            } else {
                parentNodeID = getEntity().getParentNodeID();
            }
            m_parent = getAccess().getAbstractWorkflowManager(getEntity().getRootWorkflowID(), parentNodeID);
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
        if (m_uiInfo == null) {
            m_uiInfo = EntityTranslateUtil.translateNodeUIInfoEnt(getEntity().getUIInfo());
        }
        return m_uiInfo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<Void> setUIInformationAsync(final NodeUIInformation uiInformation) {
        //propagate to server
        return AsyncNodeContainerUI.future(() -> {
            int[] b = uiInformation.getBounds();
            BoundsEnt bounds = builder(BoundsEntBuilder.class)
                    .setX(b[0])
                    .setY(b[1])
                    .setWidth(b[2])
                    .setHeight(b[3]).build();
            try {
                getAccess().nodeService().setNodeBounds(getEntity().getRootWorkflowID(), getEntity().getNodeID(),
                    bounds);
            } catch (NodeNotFoundException ex) {
                //should never happen
                throw new CompletionException(ex);
            }
            return null;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUIInformationForCorrection(final NodeUIInformation uiInfo) {
        m_uiInfo = uiInfo;
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
     * Notifies all registered {@link NodeProgressListener}s about the new progress.
     *
     * @param e the new progress event
     */
    protected void notifyNodeProgressListeners(final NodeProgressEvent e) {
        for (NodeProgressListener l : m_progressListeners) {
            l.progressChanged(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeContainerState getNodeContainerState() {
        return getNodeContainerState(getEntity());
    }

    static NodeContainerState getNodeContainerState(final NodeEnt node) {
        return EntityProxyNodeContainerState.valueOf(node.getNodeState().getState().toString());
    }

    /** {@inheritDoc} */
    @Override
    public ConfigBaseRO getNodeSettings() {
        try {
            return getAccess().getNodeSettings(getEntity());
        } catch (NodeNotFoundException ex) {
            throw new RuntimeException(ex);
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
        //by default there is no dialog
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasDialog() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean areDialogAndNodeSettingsEqual() {
        //by default there is no dialog
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFutureEx<NodeDialogPane, NotConfigurableException> getDialogPaneWithSettingsAsync() {
        //by default there is no dialog
        throw new UnsupportedOperationException();
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
        EntityProxyNodeOutPort nodeOutPort =
            getAccess().getNodeOutPort(getEntity().getOutPorts().get(index), getEntity());
        addNodeStateChangeListener(nodeOutPort);
        return nodeOutPort;
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
    public InteractiveWebViewsResultUI getInteractiveWebViews() {
        //by default there are not web views available
        return new InteractiveWebViewsResultUI() {

            @Override
            public int size() {
                return 0;
            }

            @Override
            public SingleInteractiveWebViewResultUI get(final int index) {
                return null;
            }
        };
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
        return getAccess().getNodeID(getEntity());
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
        //do nothing here
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
    @Override
    public void update(final E entity) {
        m_oldEntity = getEntity();
        super.update(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void postUpdate() {
        if (!Objects.equals(m_oldEntity.getNodeState(), getEntity().getNodeState())) {
            notifyNodeStateChangeListener(new NodeStateEvent(getID()));
        }
        if (!Objects.equals(m_oldEntity.getNodeMessage(), getEntity().getNodeMessage())) {
            notifyNodeMessageListener(new NodeMessageEvent(getID(), getNodeMessage()));
        }
        if (getEntity().getNodeState().getState().equals(StateEnum.EXECUTING)) {
            if (!Objects.equals(m_oldEntity.getProgress(), getEntity().getProgress())) {
                BigDecimal progress = getEntity().getProgress().getProgress();
                notifyNodeProgressListeners(new NodeProgressEvent(getID(), new NodeProgress(
                    progress != null ? progress.doubleValue() : null, getEntity().getProgress().getMessage())));
            }
        }
        if(!Objects.equals(m_oldEntity.getUIInfo().getBounds(), getEntity().getUIInfo().getBounds())) {
            m_uiInfo = null;
            notifyUIListeners(new NodeUIInformationEvent(getID(), getUIInformation(), getCustomDescription()));
        }
        //no post update for nested entities necessary, yet
    }

    /**
     * Requests to cancel the nodes execution via the respective service.
     *
     * @throws IllegalStateException when the node cannot be canceled
     */
    void cancelExecution() {
        if (!getNodeContainerState().isExecutionInProgress()) {
            throw new IllegalStateException("Node is not in the right state to be canceled.");
        }
        try {
            getAccess().nodeService().changeAndGetNodeState(getEntity().getRootWorkflowID(), getEntity().getNodeID(),
                "cancel");
        } catch (NodeNotFoundException ex) {
            // should not happen
            throw new RuntimeException(ex);
        } catch (ActionNotAllowedException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Determines whether the node can be reset without considering its context (e.g. the node's successors)
     *
     * @return <code>true</code> if the node is resetable
     */
    boolean canReset() {
        //need to check both conditions here since, e.g. SingleNodeContainer.isResetable() returns true, even if
        //the node is, e.g., configured
        return getEntity().getNodeState().getState().equals(NodeStateEnt.StateEnum.EXECUTED)
            && getEntity().isResetable();
    }

    /**
     * Requests to reset the node via the respective service.
     *
     * @throws IllegalStateException when the node cannot be reset
     */
    void reset() {
        if(!canReset()) {
            throw new IllegalStateException("Node is not in the right state to be reset.");
        }
        try {
            getAccess().nodeService().changeAndGetNodeState(getEntity().getRootWorkflowID(), getEntity().getNodeID(),
                "reset");
        } catch (NodeNotFoundException ex) {
            //should actually not happen
            throw new RuntimeException(ex);
        } catch (ActionNotAllowedException ex) {
            throw new IllegalStateException("", ex);
        }
    }

    /**
     * Very simple logic to determine whether the node can be executed. Doesn't take predecessors, connections etc. into
     * account!
     *
     * @return whether the node can be executed
     */
    boolean canExecute() {
        return getEntity().getNodeState().getState().equals(NodeStateEnt.StateEnum.CONFIGURED);
    }

    /**
     * Requests to execute the node via the respective service.
     *
     * @throws IllegalStateException if the node is not in the proper state for execution
     */
    void execute() {
        if (!canExecute()) {
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
     * @return the {@link RemoteWorkflowContext} associated with this remote node
     */
    protected static Optional<RemoteWorkflowContext> getWorkflowContext() {
        return Optional.ofNullable(NodeContext.getContext())
            .map(nodeCtx -> nodeCtx.getContextObjectForClass(WorkflowManagerUI.class).orElse(null))
            .map(wfm -> wfm.getContext())
            .map(ctx -> ((ctx instanceof RemoteWorkflowContext) ? (RemoteWorkflowContext)ctx : null));
    }
}