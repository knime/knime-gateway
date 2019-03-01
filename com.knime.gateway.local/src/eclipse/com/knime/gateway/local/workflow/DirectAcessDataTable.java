/*
 * ------------------------------------------------------------------------
 *
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
