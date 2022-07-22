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
package org.knime.gateway.impl.node.port.table;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataType;
import org.knime.core.data.MissingValue;

/**
 * Represents a table cell (only for viewing/rendering purposes, not for processing).
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public interface TableCell { // NOSONAR

    /**
     * The maximum number of characters allowed in a string returned by {@link #getValueAsString()}. If the string is
     * longer it will be truncated.
     */
    static final int MAX_STRING_LENGTH = 10000;

    /**
     * The type of this specific cell. Only given if not in accordance with the column spec.
     *
     * It also doesn't contain the cell type's icon.
     *
     * @return the type of this table cell if not in accordance with the cell type given by the column
     */
    TableCellType getType();

    /**
     *
     * A representation of the cell's value as a string (might not be a complete representation, e.g., could be
     * truncated).
     *
     * @return the string representation of the value or <code>null</code> if cell is missing
     */
    String getValueAsString();

    /**
     * @return <code>true</code> if the value has been truncated because it's too long, otherwise <code>null</code>
     */
    Boolean isTruncated();

    /**
     * Provides an optional error if the cell is missing (i.e. if {@link #getValueAsString()} returns
     * <code>null</code>).
     *
     * @return an optional error description for a missing cell
     */
    String getError();

    /**
     * Helper to create a table cell instance.
     *
     * @param cell the original cell to create the instance from
     * @param colType the original data type of the column (not the cell!)
     * @return the new instance
     */
    static TableCell create(final DataCell cell, final DataType colType) { // NOSONAR
        final String value = cell.isMissing() ? null : cell.toString();
        return new TableCell() { // NOSONAR

            @Override
            public String getValueAsString() {
                if (value != null) {
                    if (value.length() >= MAX_STRING_LENGTH) {
                        return value.substring(0, MAX_STRING_LENGTH);
                    } else {
                        return value;
                    }
                } else {
                    return null;
                }
            }

            @Override
            public Boolean isTruncated() {
                return (value != null && value.length() >= MAX_STRING_LENGTH) ? Boolean.TRUE : null;
            }

            @Override
            public TableCellType getType() {
                if (cell.getType().equals(colType) || cell.isMissing()) {
                    return null;
                } else {
                    return TableCellType.create(cell.getType());
                }
            }

            @Override
            public String getError() {
                if (cell.isMissing()) {
                    return ((MissingValue)cell).getError();
                } else {
                    return null;
                }
            }

        };
    }

}
