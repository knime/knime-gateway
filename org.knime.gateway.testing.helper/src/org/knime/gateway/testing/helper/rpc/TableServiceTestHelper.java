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
 * History
 *   Oct 26, 2020 (hornm): created
 */
package org.knime.gateway.testing.helper.rpc;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.MissingCell;
import org.knime.core.data.RowKey;
import org.knime.core.data.def.BooleanCell;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.LongCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.data.filestore.internal.NotInWorkflowDataRepository;
import org.knime.core.data.time.localdatetime.LocalDateTimeCellFactory;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.DefaultNodeProgressMonitor;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.Node;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.PortTypeRegistry;
import org.knime.core.node.workflow.NodeOutPort;
import org.knime.core.node.workflow.SingleNodeContainer;
import org.knime.core.node.workflow.virtual.parchunk.VirtualParallelizedChunkPortObjectInNodeFactory;
import org.knime.gateway.impl.rpc.table.Table;
import org.knime.gateway.impl.rpc.table.TableService;
import org.knime.gateway.impl.rpc.table.TableSpec;
import org.mockito.Mockito;

/**
 * Tests expected behavior of {@link TableService}-methods.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class TableServiceTestHelper {

    private Function<NodeOutPort, TableService> m_tableServiceProvider;

    /**
     * Creates a new table service test helper.
     *
     * @param tableServiceProvider provider for the {@link TableService} implementation
     */
    public TableServiceTestHelper(final Function<NodeOutPort, TableService> tableServiceProvider) {
        m_tableServiceProvider = tableServiceProvider;
    }

    private static final DataTableSpec SPEC =
        new DataTableSpec(new DataColumnSpecCreator("int", IntCell.TYPE).createSpec(),
            new DataColumnSpecCreator("string", StringCell.TYPE).createSpec(),
            new DataColumnSpecCreator("long", LongCell.TYPE).createSpec(),
            new DataColumnSpecCreator("double", DoubleCell.TYPE).createSpec(),
            new DataColumnSpecCreator("boolean", BooleanCell.TYPE).createSpec(),
            new DataColumnSpecCreator("datetime", LocalDateTimeCellFactory.TYPE).createSpec(),
            new DataColumnSpecCreator("mixed-type", DataType.getCommonSuperType(StringCell.TYPE, DoubleCell.TYPE))
                .createSpec());

    private static final VirtualParallelizedChunkPortObjectInNodeFactory FACTORY =
        new VirtualParallelizedChunkPortObjectInNodeFactory(new PortType[0]);

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static final ExecutionContext EXEC =
        new ExecutionContext(new DefaultNodeProgressMonitor(), new Node((NodeFactory)FACTORY),
            SingleNodeContainer.MemoryPolicy.CacheSmallInMemory, NotInWorkflowDataRepository.newInstance());

    /**
     * TODO
     *
     * @param rowCount
     * @return
     */
    public static BufferedDataTable createTable(final int rowCount) {
        final DataRow[] rows = IntStream.range(0, rowCount).mapToObj(i -> createRow(i)).toArray(DataRow[]::new);
        return createTable(SPEC, rows);
    }

    /**
     * TODO
     *
     * @param spec
     * @param rows
     * @return
     */
    public static BufferedDataTable createTable(final DataTableSpec spec, final DataRow... rows) {
        final BufferedDataContainer cont = EXEC.createDataContainer(spec, true, Integer.MAX_VALUE);
        for (final DataRow r : rows) {
            cont.addRowToTable(r);
        }
        cont.close();
        return cont.getTable();
    }

    private static DataRow createRow(final int i) {
        if (i == 1) {
            // add a row with missing cells
            return new DefaultRow(new RowKey(Integer.toString(i)),
                IntStream.range(0, SPEC.getNumColumns())
                    .mapToObj(colIdx -> new MissingCell(colIdx % 2 == 1 ? "error " + colIdx : null))
                    .collect(Collectors.toList()));
        } else {
            return new DefaultRow(new RowKey(Integer.toString(i)), new IntCell(i), new StringCell(Integer.toString(i)),
                new LongCell(i), new DoubleCell(i), i % 2 == 1 ? BooleanCell.TRUE : BooleanCell.FALSE,
                LocalDateTimeCellFactory.create(LocalDateTime.MIN),
                i % 2 == 1 ? new StringCell(Integer.toBinaryString(i)) : new DoubleCell(Double.MAX_VALUE));
        }
    }

    /**
     * Tests the method-calls on {@link TableService} under different circumstances. This test is not meant to test the
     * returned values in detail (this is covered another test).
     */
    public void testTableService() {
        BufferedDataTable bdt = createTable(10);

        // test with BufferedDataTable
        NodeOutPort port = mockNodeOutPort(bdt);
        basicTableServiceChecks(m_tableServiceProvider.apply(port), bdt.size());

        // test with no given table (only spec)
        port = mockNodeOutPort(bdt);
        when(port.getPortObject()).thenReturn(null);
        TableService tableService = m_tableServiceProvider.apply(port);
        Table table = tableService.getTable(2, 5);
        assertNull(table.getRows());
        assertNotNull(table.getSpec());

        // test with no given spec nor table
        when(port.getPortObjectSpec()).thenReturn(null);
        tableService = m_tableServiceProvider.apply(port);
        table = tableService.getTable(2, 5);
        assertNull(table.getRows());
        assertNull(table.getSpec());

        // test with DirectAccessTable
        when(port.getPortType())
            .thenReturn(PortTypeRegistry.getInstance().getPortType(DirectAccessTablePortObject.class));
        when(port.getPortObject()).thenReturn(new DirectAccessTablePortObject(bdt));
        when(port.getPortObjectSpec()).thenReturn(new DirectAccessTablePortObjectSpec(bdt.getDataTableSpec()));
        basicTableServiceChecks(m_tableServiceProvider.apply(port), bdt.size());
    }

    /**
     * Makes sure that excess column are ommitted.
     */
    public void testTruncatedColumns() {
        String[] names = new String[TableSpec.MAX_NUM_COLUMNS + 2];
        DataType[] types = new DataType[names.length];
        for (int i = 0; i < names.length; i++) {
            names[i] = "col " + i;
            types[i] = IntCell.TYPE;
        }
        BufferedDataTable bdt = createTable(new DataTableSpec(names, types));
        NodeOutPort port = mockNodeOutPort(bdt);

        Table table = m_tableServiceProvider.apply(port).getTable(0, 1);
        assertThat(table.getTotalNumRows(), is(0l));
        assertThat(table.getSpec().getTotalNumColumns(), is(names.length));
        assertThat(table.getSpec().getColumns().size(), is(names.length - 2));

    }

    private static void basicTableServiceChecks(final TableService tableService, final long numRows) {
        Table table = tableService.getTable(2, 5);
        assertThat(table.getTotalNumRows(), is(numRows));
        assertThat(table.getRows().size(), is(5));
        assertThat(tableService.getRows(2, 5).size(), is(5));
        assertThat(tableService.getRows(1, (int)numRows + 5).size(), is((int)numRows - 1));
        assertThat(tableService.getRows(-5, 2).size(), is(2));
        assertTrue(tableService.getRows(11, 100).isEmpty());
    }

    /**
     * TODO
     *
     * @param table
     * @return
     */
    public static NodeOutPort mockNodeOutPort(final BufferedDataTable table) {
        NodeOutPort port = Mockito.mock(NodeOutPort.class);
        when(port.getPortObjectSpec()).thenReturn(table.getDataTableSpec());
        when(port.getPortObject()).thenReturn(table);
        when(port.getPortType()).thenReturn(BufferedDataTable.TYPE);
        return port;
    }

}
