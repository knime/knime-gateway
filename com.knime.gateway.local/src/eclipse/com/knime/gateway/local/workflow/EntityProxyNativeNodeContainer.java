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
 *   Aug 18, 2017 (hornm): created
 */
package com.knime.gateway.local.workflow;

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;

import org.knime.core.node.DynamicNodeFactory;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettings;
import org.knime.core.node.config.base.JSONConfig;
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
public class EntityProxyNativeNodeContainer extends EntityProxySingleNodeContainer<NativeNodeEnt> {

    private NodeFactory<? extends NodeModel> m_nodeFactory = null;

    /**
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
