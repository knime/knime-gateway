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
 */
package org.knime.gateway.testing.helper.rpc.node;

import java.io.File;
import java.io.IOException;

import org.apache.xmlbeans.XmlException;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NoDescriptionProxy;
import org.knime.core.node.NodeDescription;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NodeView;
import org.knime.core.webui.data.rpc.NodeRpcServerFactory;
import org.knime.core.webui.data.rpc.RpcSingleServer;
import org.knime.core.webui.data.rpc.json.impl.JsonRpcSingleServer;
import org.xml.sax.SAXException;

/**
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class SingleRpcNodeFactory extends NodeFactory<NodeModel> implements NodeRpcServerFactory<NodeModel> {

    @Override
    public RpcSingleServer<Service> createRpcServer(final NodeModel nodeModel) {
        return new JsonRpcSingleServer<>(new ServiceImpl());
    }

    @Override
    public NodeModel createNodeModel() {
        return new NodeModel(0, 0) { //NOSONAR

            @Override
            protected void loadInternals(final File nodeInternDir, final ExecutionMonitor exec)
                throws IOException, CanceledExecutionException {
                //
            }

            @Override
            protected void saveInternals(final File nodeInternDir, final ExecutionMonitor exec)
                throws IOException, CanceledExecutionException {
                //
            }

            @Override
            protected void saveSettingsTo(final NodeSettingsWO settings) {
                //
            }

            @Override
            protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
                //
            }

            @Override
            protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
                //
            }

            @Override
            protected void reset() {
                //
            }

        };
    }

    @Override
    protected int getNrNodeViews() {
        return 0;
    }

    @Override
    public NodeView<NodeModel> createNodeView(final int viewIndex, final NodeModel nodeModel) {
        return null;
    }

    @Override
    protected boolean hasDialog() {
        return false;
    }

    @Override
    protected NodeDialogPane createNodeDialogPane() {
        return null;
    }

    @Override
    protected NodeDescription createNodeDescription() throws SAXException, IOException, XmlException {
        return new NoDescriptionProxy(getClass());
    }

}
