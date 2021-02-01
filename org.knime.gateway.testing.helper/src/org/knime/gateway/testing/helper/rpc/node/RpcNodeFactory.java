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

import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NodeView;
import org.knime.core.rpc.NodeRpcServerFactory;
import org.knime.core.rpc.RpcServer;
import org.knime.core.rpc.json.JsonRpcServer;

/**
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class RpcNodeFactory extends NodeFactory<NodeModel> implements NodeRpcServerFactory {

    @Override
    public RpcServer createRpcServer(final NodeModel nodeModel) {
        JsonRpcServer jsonRpcServer = new JsonRpcServer();
        jsonRpcServer.addService(Service.class, new ServiceImpl());
        return jsonRpcServer;
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

}
