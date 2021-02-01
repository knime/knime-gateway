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
package org.knime.gateway.testing.helper.rpc.port;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DirectAccessTable;
import org.knime.core.data.container.CloseableRowIterator;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.ModelContentRO;
import org.knime.core.node.ModelContentWO;
import org.knime.core.node.port.AbstractSimplePortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.PortTypeRegistry;

/**
 * To test {@link DirectAccessTable} being used in the {@link TableService}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class DirectAccessTablePortObject extends AbstractSimplePortObject implements DirectAccessTable {

    /**
     * The port type.
     */
    @SuppressWarnings("hiding")
    public static final PortType TYPE = PortTypeRegistry.getInstance().getPortType(DirectAccessTablePortObject.class);

    @SuppressWarnings("javadoc")
    public static class DirectAccessTablePortObjectSerializer
        extends AbstractSimplePortObjectSerializer<DirectAccessTablePortObject> {
    }

    private BufferedDataTable m_table;

    /**
     * @param table everything is forwarded to this table
     */
    public DirectAccessTablePortObject(final BufferedDataTable table) {
        m_table = table;
    }

    /**
     * No-arg for de-/serialization
     */
    public DirectAccessTablePortObject() {
        //
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSummary() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JComponent[] getViews() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataTableSpec getDataTableSpec() {
        return getSpec().getDataTableSpec();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<DataRow> getRows(final long start, final int length, final ExecutionMonitor exec)
        throws IndexOutOfBoundsException, CanceledExecutionException {
        long rowCount = m_table.size();
        if (rowCount >= 0 && start >= rowCount) {
            throw new IndexOutOfBoundsException();
        }

        List<DataRow> rows = new ArrayList<>();
        try (CloseableRowIterator it = m_table.iteratorFailProve()) {
            for (int i = 0; i < start && it.hasNext(); i++) {
                it.next();
            }
            for (int i = 0; i < length && it.hasNext(); i++) {
                rows.add(it.next());
            }
        }
        return rows;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getRowCount() throws UnknownRowCountException {
        return m_table.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DirectAccessTablePortObjectSpec getSpec() {
        return new DirectAccessTablePortObjectSpec();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void save(final ModelContentWO model, final ExecutionMonitor exec) throws CanceledExecutionException {
        //
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void load(final ModelContentRO model, final PortObjectSpec spec, final ExecutionMonitor exec)
        throws InvalidSettingsException, CanceledExecutionException {
        //
    }

}
