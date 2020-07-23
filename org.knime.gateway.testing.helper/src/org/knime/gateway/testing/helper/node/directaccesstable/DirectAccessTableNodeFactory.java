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
package org.knime.gateway.testing.helper.node.directaccesstable;

import java.io.File;
import java.io.IOException;

import org.knime.core.data.DirectAccessTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NodeView;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentBoolean;
import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelInteger;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;

/**
 * To test {@link DirectAccessTable} in the remote workflow editor.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class DirectAccessTableNodeFactory extends NodeFactory<NodeModel> {

    private static SettingsModelInteger createRowCountModel() {
        return new SettingsModelInteger("row_count", 1000);
    }

    private static SettingsModelBoolean createUnknownRowCountModel() {
        return new SettingsModelBoolean("unknown_row_count", false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeModel createNodeModel() {
        return new NodeModel(new PortType[0], new PortType[]{DirectAccessTablePortObject.TYPE}) {

            private SettingsModelInteger m_rowCount = createRowCountModel();

            private SettingsModelBoolean m_unknownRowCount = createUnknownRowCountModel();

            @Override
            protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs) throws InvalidSettingsException {
                return new PortObjectSpec[]{new DirectAccessTablePortObject(0, false).getSpec().getDataTableSpec()};
            }

            @Override
            protected PortObject[] execute(final PortObject[] inObjects, final ExecutionContext exec) throws Exception {
                return new PortObject[]{
                    new DirectAccessTablePortObject(m_rowCount.getIntValue(), m_unknownRowCount.getBooleanValue())};
            }

            @Override
            protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
                m_rowCount.validateSettings(settings);
            }

            @Override
            protected void saveSettingsTo(final NodeSettingsWO settings) {
                m_rowCount.saveSettingsTo(settings);
                m_unknownRowCount.saveSettingsTo(settings);
            }

            @Override
            protected void saveInternals(final File nodeInternDir, final ExecutionMonitor exec)
                throws IOException, CanceledExecutionException {
                //
            }

            @Override
            protected void reset() {
                //
            }

            @Override
            protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
                m_rowCount.loadSettingsFrom(settings);
                m_unknownRowCount.loadSettingsFrom(settings);
            }

            @Override
            protected void loadInternals(final File nodeInternDir, final ExecutionMonitor exec)
                throws IOException, CanceledExecutionException {
                //
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int getNrNodeViews() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeView<NodeModel> createNodeView(final int viewIndex, final NodeModel nodeModel) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean hasDialog() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected NodeDialogPane createNodeDialogPane() {
        return new DefaultNodeSettingsPane() {
            {
                addDialogComponent(new DialogComponentNumber(createRowCountModel(), "row count", 1));
                addDialogComponent(new DialogComponentBoolean(createUnknownRowCountModel(), "unkown row count"));
            }
        };
    }

}
