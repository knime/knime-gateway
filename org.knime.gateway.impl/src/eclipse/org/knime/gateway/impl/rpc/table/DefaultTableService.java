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

import java.lang.ref.WeakReference;
import java.util.List;

import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DirectAccessTable;
import org.knime.core.data.cache.WindowCacheTable;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.port.DataTableSpecProvider;
import org.knime.core.node.port.PortType;
import org.knime.core.node.workflow.NodeOutPort;

/**
 * Default implementation for {@link TableService}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public final class DefaultTableService implements TableService {

    private final DirectAccessTable m_table;

    private final DataTableSpec m_spec;

    /**
     * Creates a new table service instance.
     *
     * @param port the port to create the table service from
     */
    public DefaultTableService(final NodeOutPort port) {
        PortType portType = port.getPortType();
        if (DataTableSpec.class.isAssignableFrom(portType.getPortObjectSpecClass())
            && BufferedDataTable.class.equals(portType.getPortObjectClass())) {
            BufferedDataTable btable = (BufferedDataTable)port.getPortObject();
            m_table = btable != null ? new WindowCacheTable(btable) : null;
            m_spec = (DataTableSpec)port.getPortObjectSpec();
        } else if (DataTableSpecProvider.class.isAssignableFrom(portType.getPortObjectSpecClass())
            && DirectAccessTable.class.isAssignableFrom(portType.getPortObjectClass())) {
            m_table = (DirectAccessTable)port.getPortObject();
            m_spec = ((DataTableSpecProvider)port.getPortObjectSpec()).getDataTableSpec();
        } else {
            throw new IllegalArgumentException("No table can be served from port type " + portType.getName());
        }
    }

    @Override
    public Table getTable(final long start, final int size) {
        return Table.create(m_spec, m_table, start, size);
    }

    @Override
    public List<Row> getRows(final long start, final int size) {
        return Table.getRows(m_table, start, size, m_spec);
    }

}