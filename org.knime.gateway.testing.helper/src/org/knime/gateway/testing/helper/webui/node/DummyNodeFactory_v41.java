package org.knime.gateway.testing.helper.webui.node;

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

/**
 * Dummy node factory for a node that uses the v4.1 node description schema.
 */
public class DummyNodeFactory_v41 extends NodeFactory<NodeModel> {
    @Override
    public NodeModel createNodeModel() {
        // descriptions for two in- and out-ports, resp.
        return new NodeModel(2, 2) { // NOSONAR
            @Override
            protected void loadInternals(final File nodeInternDir, final ExecutionMonitor exec)
                throws IOException, CanceledExecutionException {
                // no implementation needed
            }

            @Override
            protected void saveInternals(final File nodeInternDir, final ExecutionMonitor exec)
                throws IOException, CanceledExecutionException {
                // no implementation needed
            }

            @Override
            protected void saveSettingsTo(final NodeSettingsWO settings) {
                // no implementation needed
            }

            @Override
            protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
                // no implementation needed
            }

            @Override
            protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
                // no implementation needed
            }

            @Override
            protected void reset() {
                // no implementation needed
            }
        };
    }

    @Override
    protected int getNrNodeViews() {
        return 2; // two descriptions available
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
    public NodeView<NodeModel> createNodeView(final int viewIndex, final NodeModel nodeModel) {
        return null;
    }
}