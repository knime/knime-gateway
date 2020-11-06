/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.org; Email: contact@knime.org
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
 * History
 *   Oct 23, 2020 (hornm): created
 */
package org.knime.gateway.impl.rpc.table;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DirectAccessTable;
import org.knime.core.data.DirectAccessTable.UnknownRowCountException;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.NodeLogger;

/**
 * Represents a table (e.g. of a port- or node view). Only for the purpose of displaying a table (i.e. not for
 * processing).
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public interface Table {

    /**
     * @return the table specification or <code>null</code> if not available
     */
    TableSpec getSpec();

    /**
     * @return the list of rows; an empty collection if there are no rows in the given range; <code>null</code> if the
     *         underlying port or node view doesn't have any data
     */
    List<Row> getRows();

    /**
     * @return the total number of rows available or -1 if no data is available at all (i.e. not just an empty table)
     */
    long getTotalNumRows();

    /**
     * Helper to create a table instance on the fly.
     *
     * @param spec
     * @param table
     * @param start
     * @param size
     * @return the new table instance with direct access to the underlying table
     */
    static Table create(final DataTableSpec spec, final DirectAccessTable table, final long start, final int size) {
        return new Table() { // NOSONAR

            @Override
            public TableSpec getSpec() {
                return TableSpec.create(spec);
            }

            @Override
            public List<Row> getRows() {
                return Table.getRows(table, start, size, spec);
            }

            @Override
            public long getTotalNumRows() {
                if (table == null) {
                    return -1;
                }
                try {
                    return table.getRowCount();
                } catch (UnknownRowCountException ex) { // NOSONAR
                    return -1;
                }
            }

        };
    }

    /**
     * Helper to get and instantiate the list of {@link Row}s.
     *
     * @param table
     * @param start
     * @param size
     * @param spec
     * @return the rows
     */
    static List<Row> getRows(final DirectAccessTable table, final long start, final int size,
        final DataTableSpec spec) {
        if (table == null) {
            return null; // NOSONAR
        }
        try {
            return table.getRows(Math.max(start, 0), size, null).stream().map(r -> Row.create(r, spec))
                .collect(Collectors.toList());
        } catch (IndexOutOfBoundsException | CanceledExecutionException ex) {
            NodeLogger.getLogger(Table.class).error("Problem reading rows", ex);
            return Collections.emptyList();
        }
    }

}
