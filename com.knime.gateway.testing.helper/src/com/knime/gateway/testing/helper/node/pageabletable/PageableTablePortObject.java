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
package com.knime.gateway.testing.helper.node.pageabletable;

import javax.swing.JComponent;

import org.knime.core.data.DataRow;
import org.knime.core.data.RowKey;
import org.knime.core.data.container.CloseableRowIterator;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.ModelContentRO;
import org.knime.core.node.ModelContentWO;
import org.knime.core.node.port.AbstractSimplePortObject;
import org.knime.core.node.port.PageableDataTable;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.PortTypeRegistry;

/**
 * To test {@link PageableDataTable} in the remote workflow editor.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class PageableTablePortObject extends AbstractSimplePortObject implements PageableDataTable {

    /**
     * The port type.
     */
    @SuppressWarnings("hiding")
    public static final PortType TYPE = PortTypeRegistry.getInstance().getPortType(PageableTablePortObject.class);

    @SuppressWarnings("javadoc")
    public static class PageableTablePortObjectSerializer
        extends AbstractSimplePortObjectSerializer<PageableTablePortObject> {
    }

    private int m_rowCount;

    /**
     * @param rowCount if < 0 -> unkown row count
     */
    public PageableTablePortObject(final int rowCount) {
        m_rowCount = rowCount;
    }

    /**
     * No-arg for de-/serialization
     */
    public PageableTablePortObject() {
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
    public CloseableRowIterator iterator(final long from, final long count) {
        return new CloseableRowIterator() {

            private long m_rowIdx = 0;

            @Override
            public DataRow next() {
                DataRow row = new DefaultRow(RowKey.createRowKey(from + m_rowIdx), from + m_rowIdx);
                m_rowIdx++;
                return row;
            }

            @Override
            public boolean hasNext() {
                return m_rowIdx < count;
            }

            @Override
            public void close() {
                //
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long calcTotalRowCount() throws UnknownRowCountException {
        if (m_rowCount < 0) {
            throw new UnknownRowCountException("negative row count");
        }
        return m_rowCount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableTablePortObjectSpec getSpec() {
        return new PageableTablePortObjectSpec();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void save(final ModelContentWO model, final ExecutionMonitor exec) throws CanceledExecutionException {
        model.addInt("row_count", m_rowCount);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void load(final ModelContentRO model, final PortObjectSpec spec, final ExecutionMonitor exec)
        throws InvalidSettingsException, CanceledExecutionException {
        m_rowCount = model.getInt("row_count");
    }

}
