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
import java.net.URL;

import org.knime.core.node.DynamicNodeFactory;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.Node;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettings;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.config.base.JSONConfig;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.workflow.CredentialsProvider;
import org.knime.core.node.workflow.FlowObjectStack;
import org.knime.core.node.workflow.NativeNodeContainer;
import org.knime.workbench.repository.RepositoryManager;
import org.w3c.dom.Element;

import com.knime.gateway.local.util.missing.MissingNodeFactory;
import com.knime.gateway.v0.entity.NativeNodeEnt;
import com.knime.gateway.v0.entity.NodeFactoryKeyEnt;

/**
 * Entity-proxy class that proxies {@link NativeNodeEnt} and mimics a {@link NativeNodeContainer}.
 *
 * @author Martin Horn, University of Konstanz
 */
class EntityProxyNativeNodeContainer extends EntityProxySingleNodeContainer<NativeNodeEnt> {

    private NodeFactory<? extends NodeModel> m_nodeFactory = null;

    private NodeModel m_nodeModel = null;

    /**
     * See {@link AbstractEntityProxy#AbstractEntityProxy(com.knime.gateway.entity.GatewayEntity, EntityProxyAccess)}.
     *
     * @param node
     * @param access
     */
    EntityProxyNativeNodeContainer(final NativeNodeEnt node, final EntityProxyAccess access) {
        super(node, access);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        String prefix = (m_nodeFactory instanceof MissingNodeFactory) ? prefix = "MISSING " : "";
        return prefix + super.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Element getXMLDescription() {
        //get xml description from underlying node factory
        return getNodeFactoryInstance().getXMLDescription();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URL getIcon() {
        //get the icon url via the node factory
        return getNodeFactoryInstance().getIcon();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isInactive() {
        return getEntity().isInactive();
    }

    @Override
    public boolean hasDialog() {
        return getEntity().hasDialog();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected NodeDialogPane getDialogPaneWithSettings(final NodeSettings nodeSettings,
        final PortObjectSpec[] portObjectSpecs, final FlowObjectStack flowObjectStack, final NodeDialogPane dialogPane)
        throws NotConfigurableException {
        NodeDialogPane resDialogPane;
        if (dialogPane == null) {
            resDialogPane = Node.createDialogPane((NodeFactory<NodeModel>)getNodeFactoryInstance(),
                getEntity().getInPorts().size(), false);
        } else {
            resDialogPane = dialogPane;
        }

        PortType[] portTypes = new PortType[portObjectSpecs.length];
        for (int i = 0; i < portTypes.length; i++) {
            portTypes[i] = getInPort(i).getPortType();
        }

        return Node.initDialogPaneWithSettings(resDialogPane, portObjectSpecs, portTypes,
            new PortObject[portTypes.length], nodeSettings, getParent().isWriteProtected(), null, flowObjectStack,
            CredentialsProvider.EMPTY_CREDENTIALS_PROVIDER);
    }

    NodeModel getNodeModelInstance() {
        if (m_nodeModel == null) {
            m_nodeModel = getNodeFactoryInstance().createNodeModel();
        }
        return m_nodeModel;
    }

    private NodeFactory<? extends NodeModel> getNodeFactoryInstance() {
        if (m_nodeFactory == null) {
            m_nodeFactory = createNodeFactoryInstance(getEntity());
        }
        return m_nodeFactory;
    }

    /**
     * Creates a new node factory instance from a {@link NativeNodeEnt}.
     *
     * @param node contains the info to create the node factory
     * @return a newly created node factory or a {@link MissingNodeFactory} if creation failed
     */
    static NodeFactory<? extends NodeModel> createNodeFactoryInstance(final NativeNodeEnt node) {
        NodeFactoryKeyEnt nodeFactoryKey = node.getNodeFactoryKey();
        NodeFactory<? extends NodeModel> nodeFactory;
        try {
            nodeFactory = RepositoryManager.INSTANCE.loadNodeFactory(nodeFactoryKey.getClassName());
            if (nodeFactory instanceof DynamicNodeFactory) {
                if (nodeFactoryKey.getSettings() != null) {
                    //in case of a dynamic node factory additional settings need to be loaded
                    NodeSettings config = JSONConfig.readJSON(new NodeSettings("settings"),
                        new StringReader(nodeFactoryKey.getSettings()));
                    nodeFactory.loadAdditionalFactorySettings(config);
                } else {
                    nodeFactory.init();
                }
            }
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | IOException
                | InvalidSettingsException ex) {
            nodeFactory = new MissingNodeFactory(node.getName(), ex.getMessage());
            nodeFactory.init();
        }
        return nodeFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EntityProxyInteractiveWebViewsResult getInteractiveWebViews() {
        return getAccess().getInteractiveWebViewsResult(getEntity());
    }
}
