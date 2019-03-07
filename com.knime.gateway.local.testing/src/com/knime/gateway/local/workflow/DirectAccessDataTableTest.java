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
