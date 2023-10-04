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
 *   Jun 23, 2023 (hornm): created
 */
package org.knime.gateway.impl.node.port;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DirectAccessTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.webui.data.RpcDataService;
import org.knime.core.webui.node.dialog.defaultdialog.DefaultNodeSettingsSerializer;
import org.knime.core.webui.node.dialog.defaultdialog.setting.selection.SelectionMode;
import org.knime.core.webui.node.view.table.TableViewViewSettings;
import org.knime.core.webui.node.view.table.TableViewViewSettings.RowHeightMode;
import org.knime.core.webui.node.view.table.data.DataType;
import org.knime.core.webui.node.view.table.data.Table;
import org.knime.core.webui.node.view.table.data.TableViewInitialData;
import org.knime.core.webui.node.view.table.data.render.DataCellContentType;
import org.knime.core.webui.node.view.table.data.render.DataValueImageRenderer.ImageDimension;

/**
 * The {@link RpcDataService} for the {@linke PortView} created by {@link DirectAccessTablePortViewFactory}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class DirectAccessTableDataService {

    private final DirectAccessTable m_table;

    private final DefaultNodeSettingsSerializer m_serializer =
        new DefaultNodeSettingsSerializer<TableViewInitialData>();

    DirectAccessTableDataService(final DirectAccessTable table) {
        m_table = table;
    }

    public String getTableViewInitialData(final int numRows) {
        try {
            return m_serializer.serialize(createInitialData(m_table, numRows));
        } catch (IOException ex) {
            throw new IllegalStateException("Problem creating table view initial data object", ex);
        }
    }

    private static TableViewInitialData createInitialData(final DirectAccessTable table, final int numRows) {
        var spec = table.getDataTableSpec();
        var rows = renderRows(table, numRows);
        return new TableViewInitialData() { // NOSONAR

            @Override
            public Table getTable() {
                return new Table() { // NOSONAR

                    @Override
                    public String[] getDisplayedColumns() {
                        return spec.getColumnNames();
                    }

                    @Override
                    public String[] getColumnContentTypes() {
                        var types = new String[spec.getNumColumns()];
                        Arrays.fill(types, DataCellContentType.TXT.toString());
                        return types;
                    }

                    @Override
                    public String[] getColumnDataTypeIds() {
                        return Arrays.stream(spec.getColumnNames()) //
                            .map(spec::getColumnSpec) //
                            .map(DataColumnSpec::getType) //
                            .map(type -> String.valueOf(type.hashCode())) //
                            .toArray(String[]::new);
                    }

                    @Override
                    public String[] getColumnFormatterDescriptions() {
                        return new String[spec.getNumColumns()];
                    }

                    @Override
                    public List<List<Object>> getRows() {
                        return rows;
                    }

                    @Override
                    public long getRowCount() {
                        return rows.size();
                    }

                    @Override
                    public long getColumnCount() {
                        return spec.getNumColumns();
                    }

                    @Override
                    public Long getTotalSelected() {
                        return 0l;
                    }

                    @Override
                    public Map<String, ImageDimension> getFirstRowImageDimensions() {
                        return Map.of();
                    }

                };
            }

            @Override
            public TableViewViewSettings getSettings() {
                return getSettingsForDataTable(table.getDataTableSpec(), numRows);
            }

            @Override
            public Map<String, DataType> getDataTypes() {
                return IntStream.range(0, spec.getNumColumns()) //
                    .mapToObj(spec::getColumnSpec) //
                    .map(DataColumnSpec::getType) //
                    .distinct() //
                    .map(type -> Map.entry(String.valueOf(type.hashCode()), DataType.create(type))) //
                    .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
            }

            @Override
            public Map<String, String[]> getColumnDomainValues() {
                return Map.of();
            }
        };
    }

    private static TableViewViewSettings getSettingsForDataTable(final DataTableSpec tableSpec, final int numRows) {
        var settings = new TableViewViewSettings(tableSpec);
        settings.m_enableGlobalSearch = false;
        settings.m_enableSortingByHeader = false;
        settings.m_enableColumnSearch = false;
        settings.m_rowHeightMode = RowHeightMode.COMPACT;
        settings.m_selectionMode = SelectionMode.OFF;
        settings.m_showColumnDataType = true;
        settings.m_title = "";
        // enable pagination in order to not lazily fetch data (there isn't any) after initially loading the table in the FE
        // BUT: set the page-size to the 'maximum' such that the 'paging-buttons' actually don't show up
        settings.m_enablePagination = true;
        settings.m_pageSize = numRows;
        settings.m_enableRendererSelection = false;
        settings.m_showRowKeys = true;
        settings.m_showRowIndices = true;
        settings.m_showOnlySelectedRowsConfigurable = false;
        // Hide table size/dimension because this is 're-added' by the component (DeferredTableView) that wraps the orginal table view.
        // The 're-added' table dimensions allow one to select the number of rows to display.
        settings.m_showTableSize = false;
        return settings;
    }

    private static List<List<Object>> renderRows(final DirectAccessTable table, final int numRows) {
        List<DataRow> rows;
        try {
            rows = table.getRows(0, numRows, null);
        } catch (IndexOutOfBoundsException | CanceledExecutionException ex) {
            throw new IllegalStateException("Problem rendering rows", ex);
        }
        var numCols = table.getDataTableSpec().getNumColumns();
        final var stringRows = new ArrayList<List<Object>>(numRows);
        for (int i = 0; i < rows.size(); i++) {
            final var stringRow = new ArrayList<Object>(numCols + 2);
            stringRow.add(String.valueOf(i + 1));
            final var row = rows.get(i);
            stringRow.add(row.getKey().toString());
            for (int j = 0; j < numCols; j++) {
                stringRow.add(row.getCell(j).toString());
            }
            stringRows.add(stringRow);
        }
        return stringRows;
    }

}
