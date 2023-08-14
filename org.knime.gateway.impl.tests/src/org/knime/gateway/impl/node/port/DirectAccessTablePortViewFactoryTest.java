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
 *   Jun 26, 2023 (hornm): created
 */
package org.knime.gateway.impl.node.port;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.util.List;

import javax.swing.JComponent;

import org.junit.Test;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTable;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DirectAccessTable;
import org.knime.core.data.cache.WindowCacheTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.webui.node.port.PortContext;
import org.knime.core.webui.node.port.PortView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Tests {@link DirectAccessTablePortViewFactory}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class DirectAccessTablePortViewFactoryTest {

    /**
     * Asserts that the correct page is returned by the {@link PortView} created by the
     * {@link DirectAccessTablePortViewFactory}.
     *
     * @throws IOException
     */
    @Test
    public void testPage() throws IOException {
        var bdt = TestingUtilities.createTable(2);
        var directAccessTablePortObject = new DirectAccessTablePortObject(bdt);
        var port = TestingUtilities.createNodeOutPort(bdt);
        PortContext.pushContext(port.get());
        try {
            var portView = new DirectAccessTablePortViewFactory().createPortView(directAccessTablePortObject);
            var page = portView.getPage();
            assertThat(page.getContentType().toString(), is("VUE_COMPONENT_LIB"));
            var pageId = page.getPageIdForReusablePage().orElse(null);
            assertThat(pageId, is("deferredtableview"));
        } finally {
            PortContext.removeLastContext();
            port.dispose();
        }
    }

    /**
     * Tests the rpc data service - in particular the
     * {@link DirectAccessTableDataService#getTableViewInitialData(int)}-call.
     *
     * @throws IOException
     */
    @Test
    public void testRpcDataService() throws IOException {
        var bdt = TestingUtilities.createTable(2);
        var directAccessTablePortObject = new DirectAccessTablePortObject(bdt);
        var port = TestingUtilities.createNodeOutPort(bdt);
        PortContext.pushContext(port.get());
        try {
            var rpcDataService = new DirectAccessTablePortViewFactory().createPortView(directAccessTablePortObject)
                .createRpcDataService().orElseThrow();
            var jsonRpcResponseString = rpcDataService.handleRpcRequest("""
                    {
                        "jsonrpc" : "2.0",
                        "method" : "getTableViewInitialData",
                        "params" : [100],
                        "id" : 1
                    }
                    """);
            var mapper = new ObjectMapper();
            var result = deserializeAndTruncateTableViewInitialData(
                mapper.readTree(jsonRpcResponseString).get("result").textValue(), mapper);

            var expectedResult = deserializeAndTruncateTableViewInitialData(TABLE_VIEW_INITIAL_DATA_SNAPSHOT, mapper);
            assertThat(result, is(expectedResult));
        } finally {
            PortContext.removeLastContext();
            port.dispose();
        }

    }

    private static class DirectAccessTablePortObject implements PortObject, DirectAccessTable {

        private final DirectAccessTable m_table;

        DirectAccessTablePortObject(final DataTable table) {
            m_table = new WindowCacheTable(table);
        }

        @Override
        public DataTableSpec getDataTableSpec() {
            return m_table.getDataTableSpec();
        }

        @Override
        public List<DataRow> getRows(final long start, final int length, final ExecutionMonitor exec)
            throws IndexOutOfBoundsException, CanceledExecutionException {
            return m_table.getRows(start, length, exec);
        }

        @Override
        public long getRowCount() throws UnknownRowCountException {
            return m_table.getRowCount();
        }

        @Override
        public String getSummary() {
            return null;
        }

        @Override
        public PortObjectSpec getSpec() {
            return m_table.getDataTableSpec();
        }

        @Override
        public JComponent[] getViews() {
            return null;
        }

    }

    private static JsonNode deserializeAndTruncateTableViewInitialData(final String tableViewInitialData,
        final ObjectMapper mapper) throws JsonMappingException, JsonProcessingException {
        var tree = (ObjectNode)mapper.readTree(tableViewInitialData);
        var tableTree = (ObjectNode)tree.get("table");
        // the column-data-type-ids are non-deterministic - i.e. not suited for the snapshot test
        tableTree.remove("columnDataTypeIds");
        // a map with column-data-type-ids as keys -> non-deterministic
        tree.remove("dataTypes");
        return tree;
    }

    private static final String TABLE_VIEW_INITIAL_DATA_SNAPSHOT =
        """
                {
                  "table" : {
                    "rows" : [ [ "1", "0", "0", "0", "0", "0.0", "false", "1.7976931348623157E308" ], [ "2", "1", "?", "?", "?", "?", "?", "?" ] ],
                    "rowCount" : 2,
                    "columnCount" : 6,
                    "displayedColumns" : [ "int", "string", "long", "double", "boolean", "mixed-type" ],
                    "columnContentTypes" : [ "txt", "txt", "txt", "txt", "txt", "txt" ],
                    "columnFormatterDescriptions" : [ null, null, null, null, null, null ],
                    "totalSelected" : 0,
                    "firstRowImageDimensions" : {}
                  },
                  "settings" : {
                    "displayedColumns" : {
                      "mode" : "MANUAL",
                      "patternFilter" : {
                        "pattern" : "",
                        "isCaseSensitive" : false,
                        "isInverted" : false
                      },
                      "manualFilter" : {
                        "manuallySelected" : [ "int", "string", "long", "double", "boolean", "mixed-type" ],
                        "manuallyDeselected" : [ ],
                        "includeUnknownColumns" : false
                      },
                      "typeFilter" : {
                        "selectedTypes" : [ ],
                        "typeDisplays" : [ ]
                      }
                    },
                    "showRowIndices" : true,
                    "showRowKeys" : true,
                    "showColumnDataType" : true,
                    "showTableSize" : false,
                    "title" : "Table View",
                    "showTitle" : false,
                    "enablePagination" : true,
                    "pageSize" : 100,
                    "autoSizeColumnsToContent":"FIXED",
                    "compactMode" : true,
                    "enableGlobalSearch" : false,
                    "enableColumnSearch" : false,
                    "enableSortingByHeader" : false,
                    "enableRendererSelection" : false,
                    "enableCellCopying" : true,
                    "publishSelection" : false,
                    "subscribeToSelection" : false,
                    "skipRemainingColumns" : false,
                    "showTableSize" : false
                  },
                  "columnDomainValues" : { }
                }
                            """;

}
