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
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
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
 *   Nov 8, 2016 (hornm): created
 */
package org.knime.gateway.local.workflow;

import static org.knime.gateway.local.service.ServiceManager.service;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArraySet;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeFactory.NodeType;
import org.knime.core.node.NodeSettings;
import org.knime.core.node.config.base.ConfigBaseRO;
import org.knime.core.node.config.base.JSONConfig;
import org.knime.core.node.util.NodeExecutionJobManagerPool;
import org.knime.core.node.workflow.AnnotationData;
import org.knime.core.node.workflow.AnnotationData.StyleRange;
import org.knime.core.node.workflow.AnnotationData.TextAlignment;
import org.knime.core.node.workflow.NodeAnnotation;
import org.knime.core.node.workflow.NodeAnnotationData;
import org.knime.core.node.workflow.NodeContainer.NodeLock;
import org.knime.core.node.workflow.NodeContainer.NodeLocks;
import org.knime.core.node.workflow.NodeContainerState;
import org.knime.core.node.workflow.NodeExecutionJobManager;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.NodeMessage;
import org.knime.core.node.workflow.NodeMessageListener;
import org.knime.core.node.workflow.NodeProgressEvent;
import org.knime.core.node.workflow.NodeProgressListener;
import org.knime.core.node.workflow.NodePropertyChangedListener;
import org.knime.core.node.workflow.NodeStateChangeListener;
import org.knime.core.node.workflow.NodeUIInformation;
import org.knime.core.node.workflow.NodeUIInformationListener;
import org.knime.core.ui.node.workflow.NodeContainerUI;
import org.knime.core.ui.node.workflow.NodeInPortUI;
import org.knime.core.ui.node.workflow.NodeOutPortUI;
import org.knime.core.ui.node.workflow.WorkflowManagerUI;
import org.knime.gateway.local.service.ServerServiceConfig;
import org.knime.gateway.local.util.ClientProxyUtil;
import org.knime.gateway.local.util.ObjectCache;
import org.knime.gateway.v0.workflow.entity.AnnotationEnt;
import org.knime.gateway.v0.workflow.entity.BoundsEnt;
import org.knime.gateway.v0.workflow.entity.NodeAnnotationEnt;
import org.knime.gateway.v0.workflow.entity.NodeEnt;
import org.knime.gateway.v0.workflow.entity.NodeMessageEnt;
import org.knime.gateway.v0.workflow.service.NodeService;

/**
 *
 * @author Martin Horn, University of Konstanz
 */
public abstract class ClientProxyNodeContainer implements NodeContainerUI {

    /**
     * Map that keeps track of all root workflow ids and maps them to a unique node ids.
     * It's the id the will be prepended to the node's id (see {@link #getID()}).
     *
     * TODO: remove worklfow id's from the list that aren't in memory anymore
     */
    private static final Map<String, String> ROOT_ID_MAP =
        new HashMap<String, String>();

    private final NodeEnt m_node;

    private NodeAnnotation m_nodeAnnotation;

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

    protected ServerServiceConfig m_serviceConfig;

    protected ObjectCache m_objCache;

    /**
     * If the underlying entity is a node.
     *
     * @param node
     * @param objCache
     * @param serviceConfig
     */
    public ClientProxyNodeContainer(final NodeEnt node, final ObjectCache objCache,
        final ServerServiceConfig serviceConfig) {
        m_node = node;
        m_objCache = objCache;
        m_serviceConfig = serviceConfig;
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
        return m_node.getParentNodeID().map(s -> {
            //get parent wf
            String parentNodeID;
            if (NodeID.fromString(s).getPrefix() == NodeID.ROOTID) {
                //parent is the highest level workflow
                //the node id has then no meaning here and need to be empty
                parentNodeID = null;
            } else {
                parentNodeID = s;
            }
            return ClientProxyUtil.getWorkflowManager(m_node.getRootWorkflowID(), Optional.ofNullable(parentNodeID),
                m_objCache, m_serviceConfig);
        }).orElse(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeExecutionJobManager getJobManager() {
        if (m_node.getJobManager().isPresent()) {
            return m_node.getJobManager()
                .map(jm -> NodeExecutionJobManagerPool.getJobManagerFactory(jm.getJobManagerID()).getInstance()).get();
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
        return m_node.getJobManager()
            .map(jm -> NodeExecutionJobManagerPool.getJobManagerFactory(jm.getJobManagerID()).getInstance())
            .orElseGet(() -> {
                if (getParent() == null) {
                    //if it's the root workflow, there must be a job manager set
                    return getJobManager();
                } else {
                    return getParent().findJobManager();
                }

            });
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
        NodeMessageEnt nme = m_node.getNodeMessage();
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
     * {@inheritDoc}
     */
    @Override
    public NodeUIInformation getUIInformation() {
        BoundsEnt bounds = m_node.getBounds();
        return NodeUIInformation.builder()
            .setNodeLocation(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight()).build();
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
     * {@inheritDoc}
     */
    @Override
    public NodeContainerState getNodeContainerState() {
        return getNodeContainerState(m_node);
    }

    static NodeContainerState getNodeContainerState(final NodeEnt node) {
        String state = node.getNodeState();
        return ClientProxyNodeContainerState.valueOf(state);
    }

    /** {@inheritDoc} */
    @Override
    public ConfigBaseRO getNodeSettings() {
        String json = service(NodeService.class, m_serviceConfig).getNodeSettingsJSON(m_node.getRootWorkflowID(),
            m_node.getNodeID());
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
        return m_node.getHasDialog();
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
        return m_node.getInPorts().size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeInPortUI getInPort(final int index) {
        return ClientProxyUtil.getNodeInPort(m_node.getInPorts().get(index), m_objCache);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeOutPortUI getOutPort(final int index) {
        return ClientProxyUtil.getNodeOutPort(m_node.getOutPorts().get(index), m_node, m_objCache);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNrOutPorts() {
        return m_node.getOutPorts().size();
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
        return Enum.valueOf(NodeType.class, m_node.getNodeType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeID getID() {
        return ClientProxyUtil.stringToNodeID(ROOT_ID_MAP.get(m_node.getRootWorkflowID()), m_node.getNodeID());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return m_node.getName();
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
            NodeAnnotationEnt anno = m_node.getNodeAnnotation();
            NodeAnnotationData data = NodeAnnotationData.createFromObsoleteCustomName(null);
            if (!anno.getIsDefault()) {
                data.copyFrom(getAnnotationData(anno), false);
            }
            m_nodeAnnotation = new NodeAnnotation(data);
            m_nodeAnnotation.registerOnNodeContainer(getID(), () -> setDirty());
            addUIInformationListener(m_nodeAnnotation);
        }
        return m_nodeAnnotation;
    }

    static AnnotationData getAnnotationData(final AnnotationEnt annoEnt) {
        StyleRange[] styleRanges = annoEnt.getStyleRanges().stream().map(sr -> {
            StyleRange newSR = new StyleRange();
            newSR.setStart(sr.getStart());
            newSR.setLength(sr.getLength());
            newSR.setFontName(sr.getFontName());
            newSR.setFontSize(sr.getFontSize());
            newSR.setFgColor(sr.getForegroundColor());
            newSR.setFontStyle(getFontStyleIdx(sr.getFontStyle()));
            return newSR;
        }).toArray(StyleRange[]::new);
        AnnotationData ad = new AnnotationData();
        ad.setText(annoEnt.getText());
        ad.setX(annoEnt.getBounds().getX());
        ad.setY(annoEnt.getBounds().getY());
        ad.setBgColor(annoEnt.getBackgroundColor());
        ad.setBorderColor(annoEnt.getBorderColor());
        ad.setBorderSize(annoEnt.getBorderSize());
        ad.setDefaultFontSize(annoEnt.getDefaultFontSize());
        ad.setHeight(annoEnt.getBounds().getHeight());
        ad.setWidth(annoEnt.getBounds().getWidth());
        ad.setAlignment(TextAlignment.valueOf(annoEnt.getTextAlignment()));
        ad.setStyleRanges(styleRanges);
        return ad;
    }


    static int getFontStyleIdx(final String fontStyle) {
        //indices from SWT-class
        if (fontStyle.equals("normal")) {
            return 0;
        } else if (fontStyle.equals("bold")) {
            return 1;
        } else if (fontStyle.equals("italic")) {
            return 2;
        } else {
            //return normal style by default
            return 0;
        }
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
        return m_node.getIsDeletable();
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

}