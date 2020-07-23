/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.com; Email: contact@knime.com
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
