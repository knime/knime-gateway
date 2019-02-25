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

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.junit.Test;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DirectAccessTable;
import org.knime.core.data.RowKey;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.node.ExecutionMonitor;

/**
 * Test for the {@link DirectAcessDataTable}. Likely to be removed with AP-11368.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class DirectAccessDataTableTest {

    /**
     * Tests the correct behaviour of the {@link DirectAcessDataTable}'s iterator.
     */
    @Test
    public void testIterateChunkedDataTable() {
        List<DataRow> rows = createDataRows();
        DataTableSpec spec = createSpec();

        DirectAccessTable chunks = new DirectAccessTable() {

            @Override
            public DataTableSpec getDataTableSpec() {
                return spec;
            }

            @Override
            public List<DataRow> getRows(final long from, final int count, final ExecutionMonitor mon) {
                if (from < rows.size()) {
                    return rows.stream().skip(from).limit(count).collect(Collectors.toList());
                } else {
                    return Collections.emptyList();
                }
            }

            @Override
            public long getRowCount() throws UnknownRowCountException {
                return -1;
            }
        };

        DirectAcessDataTable chunkedTable = new DirectAcessDataTable(3, chunks);
        Iterator<DataRow> testIt = chunkedTable.iterator();
        Iterator<DataRow> refIt = rows.iterator();

        while (testIt.hasNext()) {
            assertThat("Rows don't match", refIt.next(), is(testIt.next()));
        }
        assertThat("Not all rows have been iterated", false, is(refIt.hasNext()));
        assertThat("Chunked table should not return true on hasNext", false, is(testIt.hasNext()));
        assertThat("Unexpected data table spec", spec, is(chunkedTable.getDataTableSpec()));
    }

    /**
     * Test that a {@link NoSuchElementException} is thrown when there are no more rows but next is called.
     */
    @Test(expected = NoSuchElementException.class)
    public void testHasNoSuchElementExceptionOnNext() {
        DirectAccessTable chunks = new DirectAccessTable() {

            @Override
            public DataTableSpec getDataTableSpec() {
                return null;
            }

            @Override
            public List<DataRow> getRows(final long from, final int count, final ExecutionMonitor mon) {
                return Collections.emptyList();
            }

            @Override
            public long getRowCount() throws UnknownRowCountException {
                return -1;
            }
        };
        new DirectAcessDataTable(10, chunks).iterator().next();
    }

    private static DataTableSpec createSpec() {
        DataColumnSpec[] colSpecs =
            new DataColumnSpec[]{new DataColumnSpecCreator("col", DoubleCell.TYPE).createSpec()};
        return new DataTableSpec(colSpecs);
    }

    private static List<DataRow> createDataRows() {
        List<DataRow> rows = new ArrayList<>();
        for (int i = 23; i < 50; i++) {
            rows.add(creatRow(i, i * 2));
        }
        return rows;
    }

    private static DataRow creatRow(final long i, final double d) {
        return new DefaultRow(RowKey.createRowKey(i), new DoubleCell(d));
    }
}
