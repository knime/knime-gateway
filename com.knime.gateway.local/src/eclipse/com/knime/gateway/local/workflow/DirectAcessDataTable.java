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

import java.util.List;
import java.util.NoSuchElementException;

import org.knime.core.data.DataRow;
import org.knime.core.data.DataTable;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DirectAccessTable;
import org.knime.core.data.RowIterator;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.tableview.TableContentModel;

/**
 * A {@link DataTable} whose rows are provided by ({@link DirectAccessTable})-instance.
 *
 * TODO can be removed as soon as AP-11368 is resolved and the {@link TableContentModel} directly supports the
 * {@link DirectAccessTable}. However, the caching functionality maybe needs to be extracted, still.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class DirectAcessDataTable implements DataTable {

    private final int m_chunkSize;

    private final DirectAccessTable m_directAccessTable;

    /**
     * Creates a new data table from the passed {@link DirectAccessTable}.
     *
     * @param chunkSize the size of each chunks to be retrieved in the {@link #iterator()}-method
     * @param daTable the table to get the rows from
     */
    public DirectAcessDataTable(final int chunkSize, final DirectAccessTable daTable) {
        m_chunkSize = chunkSize;
        m_directAccessTable = daTable;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataTableSpec getDataTableSpec() {
        return m_directAccessTable.getDataTableSpec();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RowIterator iterator() {
        return new RowIterator() {
            int m_chunkIndex = 0;

            int m_rowIndexInChunk = 0;

            private List<DataRow> m_currentChunk;

            @Override
            public DataRow next() {
                if(!hasNext()) {
                    throw new NoSuchElementException("No more rows");
                }
                DataRow row = getCurrentChunk().get(m_rowIndexInChunk);
                m_rowIndexInChunk++;
                if (m_rowIndexInChunk >= m_currentChunk.size()) {
                    m_rowIndexInChunk = 0;
                    m_chunkIndex++;
                    //load next chunk here rather in 'hasNext'
                    retrieveChunk(m_chunkIndex);
                }
                return row;
            }

            @Override
            public boolean hasNext() {
                return !getCurrentChunk().isEmpty();
            }

            private List<DataRow> getCurrentChunk() {
                if (m_currentChunk == null) {
                    retrieveChunk(m_chunkIndex);
                }
                return m_currentChunk;
            }

            private void retrieveChunk(final int chunkIndex) {
                try {
                    m_currentChunk = m_directAccessTable.getRows(chunkIndex * m_chunkSize, m_chunkSize, null);
                } catch (IndexOutOfBoundsException | CanceledExecutionException ex) {
                    //can never happen, because chunk size and index are always greater than 0
                    //and there is no execution monitor passed
                    throw new RuntimeException(ex);
                }
            }
        };
    }
}
