package org.knime.gateway.impl.node.port;
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
 *   Jul 21, 2022 (hornm): created
 */

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.knime.core.webui.data.rpc.json.JsonRpcDataService.jsonRpcRequest;

import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;
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
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.DefaultNodeProgressMonitor;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.Node;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.port.PortType;
import org.knime.core.node.workflow.NodeContext;
import org.knime.core.node.workflow.SingleNodeContainer;
import org.knime.core.node.workflow.virtual.parchunk.VirtualParallelizedChunkPortObjectInNodeFactory;
import org.knime.core.util.Pair;
import org.knime.core.webui.data.DataService;
import org.knime.core.webui.data.InitialDataService;
import org.knime.core.webui.data.rpc.json.JsonRpcDataService;
import org.knime.core.webui.data.rpc.json.impl.ObjectMapperUtil;
import org.knime.core.webui.data.text.TextInitialDataService;
import org.knime.core.webui.node.port.PortView;
import org.knime.testing.node.view.NodeViewNodeFactory;
import org.knime.testing.util.WorkflowManagerUtil;

/**
 * Tests {@link TablePortViewFactory}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class TablePortViewFactoryTest {

    /**
     * Asserts that the correct page is returned by the {@link PortView} created by the {@link TablePortViewFactory}.
     * @throws IOException
     */
    @Test
    public void testTablePortViewPage() throws IOException {
        var bdt = createTable(2);
        var portViewAndDispose = createPortView(bdt);
        var portView = portViewAndDispose.getFirst();
        var page = portView.getPage();
        assertThat(page.getContentType().toString(), is("VUE_COMPONENT_LIB"));
        var pageId = page.getPageIdForReusablePage().orElse(null);
        assertThat(pageId, is("tableview"));

        portViewAndDispose.getSecond().dispose();
    }

    /**
     * Checks the {@link InitialDataService} of the {@link PortView} created by the {@link TablePortViewFactory}.
     * @throws IOException
     */
    @Test
    public void testTablePortViewInitialData() throws IOException {
        var bdt = createTable(2);
        var portView = createPortView(bdt);
        var initialData = ((TextInitialDataService)portView.getFirst().createInitialDataService().get()).getInitialData();
        assertThat(initialData, containsString("{\"result\":{"));
        assertThat(initialData, containsString("\"table\":{"));
        assertThat(initialData, containsString("\"settings\":{"));
        var initialDataTree = ObjectMapperUtil.getInstance().getObjectMapper().readTree(initialData);
        var settings = initialDataTree.get("result").get("settings");
        assertThat(settings.get("showTitle").asBoolean(), is(false));
        assertThat(settings.get("publishSelection").asBoolean(), is(false));
        assertThat(settings.get("subscribeToSelection").asBoolean(), is(false));
        assertThat(settings.get("enablePagination").asBoolean(), is(false));
        assertThat(settings.get("compactMode").asBoolean(), is(true));
        assertThat(settings.get("showRowKeys").asBoolean(), is(true));
        assertThat(settings.get("showColumnDataType").asBoolean(), is(true));
        assertThat(settings.get("showRowIndices").asBoolean(), is(true));
        assertThat(settings.get("enableGlobalSearch").asBoolean(), is(true));
        assertThat(settings.get("enableColumnSearch").asBoolean(), is(true));
        assertThat(settings.get("enableSortingByHeader").asBoolean(), is(true));
        assertThat(settings.get("enableRendererSelection").asBoolean(), is(true));        
        assertThat(settings.get("skipRemainingColumns").asBoolean(), is(true));

        portView.getSecond().dispose();
    }

    /**
     * Checks the {@link DataService} of the {@link PortView} created by the {@link TablePortViewFactory}.
     * @throws IOException
     */
    @Test
    public void testTablePortViewData() throws IOException {
        var bdt = createTable(10);
        var portView = createPortView(bdt);
        var jsonRpcResponse = ((JsonRpcDataService)portView.getFirst().createDataService().get())
            .handleRequest(jsonRpcRequest("getTable", "string", "0", "2", null, "false", "true"));
        assertThat(jsonRpcResponse, containsString("{\"jsonrpc\":\"2.0\",\"id\":1,\"result\":"));

        portView.getSecond().dispose();
    }

    /*
     * returns the port view and a runnable to dispose port-view related stuff
     */
    private static Pair<PortView, Dispose> createPortView(final BufferedDataTable table) throws IOException {
        var wfm = WorkflowManagerUtil.createEmptyWorkflow();
        var nc = WorkflowManagerUtil.createAndAddNode(wfm, new NodeViewNodeFactory());
        NodeContext.pushContext(nc);
        try {
            return Pair.create(new TablePortViewFactory().createPortView(table),
                () -> WorkflowManagerUtil.disposeWorkflow(wfm));
        } finally {
            NodeContext.removeLastContext();
        }
    }

    @FunctionalInterface
    private static interface Dispose {
        void dispose();
    }

    private static final DataTableSpec SPEC =
            new DataTableSpec(new DataColumnSpecCreator("int", IntCell.TYPE).createSpec(),
                new DataColumnSpecCreator("string", StringCell.TYPE).createSpec(),
                new DataColumnSpecCreator("long", LongCell.TYPE).createSpec(),
                new DataColumnSpecCreator("double", DoubleCell.TYPE).createSpec(),
                new DataColumnSpecCreator("boolean", BooleanCell.TYPE).createSpec(),
                new DataColumnSpecCreator("mixed-type", DataType.getCommonSuperType(StringCell.TYPE, DoubleCell.TYPE))
                    .createSpec());

        private static final VirtualParallelizedChunkPortObjectInNodeFactory FACTORY =
            new VirtualParallelizedChunkPortObjectInNodeFactory(new PortType[0]);

        @SuppressWarnings({"rawtypes", "unchecked"})
        private static final ExecutionContext EXEC =
            new ExecutionContext(new DefaultNodeProgressMonitor(), new Node((NodeFactory)FACTORY),
                SingleNodeContainer.MemoryPolicy.CacheSmallInMemory, NotInWorkflowDataRepository.newInstance());

        /**
         * Creates a new {@link BufferedDataTable} for testing purposes with some dummy values per row.
         *
         * @param rowCount the number of rows in the new table
         * @return the new table instance
         */
        private static BufferedDataTable createTable(final int rowCount) {
            final DataRow[] rows =
                IntStream.range(0, rowCount).mapToObj(TablePortViewFactoryTest::createRow).toArray(DataRow[]::new);
            return createTable(SPEC, rows);
        }

        /**
         * Creates a new {@link BufferedDataTable} for testing purposes.
         *
         * @param spec the spec of the new table
         * @param rows the rows of the new table
         * @return the new table instance
         */
        private static BufferedDataTable createTable(final DataTableSpec spec, final DataRow... rows) {
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
                        .mapToObj(colIdx -> new MissingCell(colIdx % 2 == 1 ? ("error " + colIdx) : null))
                        .collect(Collectors.toList()));
            } else {
                return new DefaultRow(new RowKey(Integer.toString(i)), new IntCell(i), new StringCell(Integer.toString(i)),
                    new LongCell(i), new DoubleCell(i), i % 2 != 0 ? BooleanCell.TRUE : BooleanCell.FALSE,
                    i % 2 != 0 ? new StringCell(Integer.toBinaryString(i)) : new DoubleCell(Double.MAX_VALUE));
            }
        }

}
