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
 */
package org.knime.gateway.impl.node.port;

import java.util.Optional;
import java.util.function.Supplier;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.workflow.NodeOutPort;
import org.knime.core.webui.data.DataServiceContext;
import org.knime.core.webui.data.InitialDataService;
import org.knime.core.webui.data.RpcDataService;
import org.knime.core.webui.node.port.PortContext;
import org.knime.core.webui.node.port.PortSpecViewFactory;
import org.knime.core.webui.node.port.PortView;
import org.knime.core.webui.node.view.table.TableViewUtil;
import org.knime.core.webui.node.view.table.TableViewViewSettings;
import org.knime.core.webui.node.view.table.TableViewViewSettings.RowHeightMode;
import org.knime.core.webui.page.Page;

/**
 * Provides information on the table spec.
 *
 * @author Benjamin Moser, KNIME GmbH, Konstanz, Germany
 */
@SuppressWarnings("restriction")
public class TableSpecViewFactory implements PortSpecViewFactory<DataTableSpec> {

    @Override
    public PortView createPortView(final DataTableSpec tableSpec) {
        var nc = ((NodeOutPort)PortContext.getContext().getNodePort()).getConnectedNodeContainer();
        var nodeId = nc.getID();
        var tableId = "spec_" + TableViewUtil.toTableId(nodeId);
        Supplier<BufferedDataTable> emptyTableSupplier = () -> {
            var emptyTable = DataServiceContext.get().getExecutionContext().createDataContainer(tableSpec);
            emptyTable.close();
            return emptyTable.getTable();
        };
        return new PortView() { // NOSONAR

            @Override
            public Page getPage() {
                return TableViewUtil.PAGE;
            }

            @Override
            @SuppressWarnings({"rawtypes", "unchecked"})
            public Optional<InitialDataService> createInitialDataService() {
                var settings = getSettingsForDataTable(tableSpec);
                return Optional
                    .of(TableViewUtil.createInitialDataService(() -> settings, emptyTableSupplier, null, tableId));
            }

            @Override
            public Optional<RpcDataService> createRpcDataService() {
                return Optional.empty();
            }
        };
    }

    /**
     * Package scope for testing
     */
    static TableViewViewSettings getSettingsForDataTable(final DataTableSpec tableSpec) {
        var settings = new TableViewViewSettings(tableSpec);
        settings.m_enableGlobalSearch = false;
        settings.m_title = "";
        settings.m_enableSortingByHeader = false;
        settings.m_enableColumnSearch = false;
        settings.m_rowHeightMode = RowHeightMode.COMPACT;
        settings.m_subscribeToSelection = false;
        settings.m_publishSelection = false;
        // enable pagination in order to not lazily fetch data (there isn't any) after initially loading the table in the FE
        settings.m_enablePagination = true;
        settings.m_enableRendererSelection = false;
        settings.m_showRowKeys = false;
        settings.m_showRowIndices = false;
        settings.m_skipRemainingColumns = true;
        return settings;
    }

}
