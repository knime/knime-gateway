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
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettings;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.config.base.JSONConfig;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.workflow.FlowObjectStack;
import org.knime.core.node.workflow.NativeNodeContainer;
import org.knime.workbench.repository.RepositoryManager;
import org.w3c.dom.Element;

import com.knime.gateway.local.util.missing.MissingNodeFactory;
import com.knime.gateway.v0.entity.NativeNodeEnt;
import com.knime.gateway.v0.entity.NodeFactoryKeyEnt;
import com.knime.gateway.v0.service.util.ServiceExceptions.NotSupportedException;

/**
 * Entity-proxy class that proxies {@link NativeNodeEnt} and mimics a {@link NativeNodeContainer}.
 *
 * @author Martin Horn, University of Konstanz
 */
class EntityProxyNativeNodeContainer extends EntityProxySingleNodeContainer<NativeNodeEnt> {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(EntityProxyNativeNodeContainer.class);

    private NodeFactory<? extends NodeModel> m_nodeFactory = null;

    private NodeDialogPane m_dialogPane;

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
        return getEntity().isHasDialog();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeDialogPane getDialogPaneWithSettings() throws NotConfigurableException {
        if (m_dialogPane == null) {
            m_dialogPane = NodeDialogPane.createDialogPane((NodeFactory<NodeModel>)getNodeFactoryInstance(),
                getEntity().getInPorts().size(), false);
        }
        NodeSettings nodeSettings = getAccess().getNodeSettings(getEntity());
        PortObjectSpec[] portObjectSpecs;
        try {
            portObjectSpecs = getAccess().getInputPortObjectSpecs(getEntity());
        } catch (NotSupportedException ex) {
            throw new NotConfigurableException("Problem retrieving settings info: " + ex.getMessage(), ex);
        }
        PortType[] portTypes = new PortType[portObjectSpecs.length];
        for (int i = 1; i < portTypes.length; i++) {
            portTypes[i] = getOutPort(i - 1).getPortType();
        }
        FlowObjectStack flowObjectStack = getAccess().getFlowVariableStack(getEntity(), getID());
        return NodeDialogPane.initDialogPaneWithSettings(m_dialogPane, portObjectSpecs, portTypes,
            new PortObject[portObjectSpecs.length], nodeSettings, true, null, flowObjectStack, null);
    }

    private NodeFactory<? extends NodeModel> getNodeFactoryInstance() {
        if (m_nodeFactory == null) {
            NodeFactoryKeyEnt nodeFactoryKey = getEntity().getNodeFactoryKey();
            try {
                m_nodeFactory = RepositoryManager.INSTANCE.loadNodeFactory(nodeFactoryKey.getClassName());
                if (m_nodeFactory instanceof DynamicNodeFactory) {
                    if (nodeFactoryKey.getSettings() != null) {
                        //in case of a dynamic node factory additional settings need to be loaded
                        NodeSettings config = JSONConfig.readJSON(new NodeSettings("settings"),
                            new StringReader(nodeFactoryKey.getSettings()));
                        m_nodeFactory.loadAdditionalFactorySettings(config);
                    } else {
                        m_nodeFactory.init();
                    }
                }
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | IOException
                    | InvalidSettingsException ex) {
                m_nodeFactory = new MissingNodeFactory(super.getName(), ex.getMessage());
                m_nodeFactory.init();
            }
        }
        return m_nodeFactory;
    }

}
