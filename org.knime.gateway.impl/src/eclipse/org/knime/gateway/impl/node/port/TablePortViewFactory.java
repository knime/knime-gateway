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
 *   Jul 18, 2022 (hornm): created
 */
package org.knime.gateway.impl.node.port;

import java.util.Optional;
import java.util.function.Supplier;

import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.workflow.NodeContext;
import org.knime.core.webui.data.DataService;
import org.knime.core.webui.data.InitialDataService;
import org.knime.core.webui.data.rpc.json.impl.JsonRpcDataServiceImpl;
import org.knime.core.webui.data.rpc.json.impl.JsonRpcSingleServer;
import org.knime.core.webui.node.dialog.impl.DefaultInitialDataServiceImpl;
import org.knime.core.webui.node.port.PortView;
import org.knime.core.webui.node.port.PortViewFactory;
import org.knime.core.webui.node.view.table.TableViewUtil;
import org.knime.core.webui.node.view.table.TableViewViewSettings;
import org.knime.core.webui.node.view.table.data.TableViewDataService;
import org.knime.core.webui.node.view.table.data.TableViewInitialData;
import org.knime.core.webui.page.Page;

/**
 * Factory for the {@link PortView} of a {@link BufferedDataTable}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public final class TablePortViewFactory implements PortViewFactory<BufferedDataTable> {

    @Override
    public PortView createPortView(final BufferedDataTable table) {
        var pageId = TableViewUtil.getPageId();
        var tableId = TableViewUtil.toTableId(NodeContext.getContext().getNodeContainer().getID()) + "_"
            + table.getBufferedTableId();
        TableViewUtil.registerRendererRegistryCleanup(tableId);

        return new PortView() { // NOSONAR

            @Override
            public Optional<InitialDataService> createInitialDataService() {
                Supplier<TableViewInitialData> initialTableDataSupplier = () -> TableViewUtil
                    .createInitialData(new TableViewViewSettings(table.getDataTableSpec()), table, tableId);
                return Optional.of(new DefaultInitialDataServiceImpl<TableViewInitialData>(initialTableDataSupplier));
            }

            @Override
            public Optional<DataService> createDataService() {
                var tableService = TableViewUtil.createDataService(table, tableId);
                var dataService =
                    new JsonRpcDataServiceImpl(new JsonRpcSingleServer<TableViewDataService>(tableService));
                return Optional.of(dataService);
            }

            @Override
            public Page getPage() {
                return TableViewUtil.createPage();
            }

            @Override
            public String getPageId() {
                return pageId;
            }

        };
    }

}
