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

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import javax.swing.JComponent;

import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DirectAccessTable;
import org.knime.core.data.RowKey;
import org.knime.core.data.def.DefaultRow;
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
 * To test {@link DirectAccessTable} in the remote workflow editor.
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
    public static class PageableTablePortObjectSerializer
        extends AbstractSimplePortObjectSerializer<DirectAccessTablePortObject> {
    }

    private int m_rowCount;
    private boolean m_unknownRowCount;

    /**
     * @param rowCount the maximum number of rows available
     * @param unknownRowCount if row count is not known in advance, i.e. {@link #getRowCount()} throws an exception
     */
    public DirectAccessTablePortObject(final int rowCount, final boolean unknownRowCount) {
        m_rowCount = rowCount;
        m_unknownRowCount = unknownRowCount;
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
        if (m_rowCount >= 0 && start >= m_rowCount) {
            throw new IndexOutOfBoundsException();
        }
        int actualLength = (int)Math.min(length, m_rowCount - start);
        return LongStream.range(start, start + actualLength).mapToObj(i -> {
            return new DefaultRow(RowKey.createRowKey(i), i);
        }).collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getRowCount() throws UnknownRowCountException {
        if (m_unknownRowCount) {
            throw new UnknownRowCountException("unknown row count");
        }
        return m_rowCount;
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
        model.addInt("row_count", m_rowCount);
        model.addBoolean("unknown_row_count", m_unknownRowCount);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void load(final ModelContentRO model, final PortObjectSpec spec, final ExecutionMonitor exec)
        throws InvalidSettingsException, CanceledExecutionException {
        m_rowCount = model.getInt("row_count");
        m_unknownRowCount = model.getBoolean("unknown_row_count");
    }

}
