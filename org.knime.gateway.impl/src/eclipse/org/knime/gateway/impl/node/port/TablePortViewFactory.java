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

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import org.knime.core.data.RowKey;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.workflow.NodeOutPort;
import org.knime.core.node.workflow.NodeOutPortWrapper;
import org.knime.core.webui.data.InitialDataService;
import org.knime.core.webui.data.RpcDataService;
import org.knime.core.webui.node.port.PortContext;
import org.knime.core.webui.node.port.PortView;
import org.knime.core.webui.node.port.PortViewFactory;
import org.knime.core.webui.node.view.table.TableView;
import org.knime.core.webui.node.view.table.TableViewManager;
import org.knime.core.webui.node.view.table.TableViewUtil;
import org.knime.core.webui.node.view.table.TableViewViewSettings;
import org.knime.core.webui.page.Page;

/**
 * Factory for the {@link PortView} of a {@link BufferedDataTable}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@SuppressWarnings("restriction")
public final class TablePortViewFactory implements PortViewFactory<BufferedDataTable> {

    @Override
    public PortView createPortView(final BufferedDataTable table) {
        var nodePort = (NodeOutPort)PortContext.getContext().getNodePort();
        var snc = nodePort.getConnectedNodeContainer();
        var tableId = "table_" + TableViewUtil.toTableId(snc.getID()) + "_" + table.getBufferedTableId();
        TableViewUtil.registerRendererRegistryCleanup(tableId, snc);
        var portIndex = getPortIndex(nodePort);
        var hiLiteHandler = TableViewManager.getOutHiLiteHandler(snc, portIndex - 1).orElse(null);
        Supplier<Set<RowKey>> selectionSupplier;
        if (hiLiteHandler == null) {
            selectionSupplier = Collections::emptySet;
        } else {
            selectionSupplier = hiLiteHandler::getHiLitKeys;
        }
        return new TablePortView(table, tableId, selectionSupplier, portIndex);
    }

    private static int getPortIndex(final NodeOutPort port) {
        if (port instanceof NodeOutPortWrapper wrapper) {
            return wrapper.getConnectedOutport().orElse(-1);
        }
        return port.getPortIndex();
    }

    private static class TablePortView implements PortView, TableView {

        private final BufferedDataTable m_table;

        private final String m_tableId;

        private final Supplier<Set<RowKey>> m_selectionSupplier;

        private final int m_portIndex;

        TablePortView(final BufferedDataTable table, final String tableId,
            final Supplier<Set<RowKey>> selectionSupplier, final int portIndex) {
            m_table = table;
            m_tableId = tableId;
            m_selectionSupplier = selectionSupplier;
            m_portIndex = portIndex;

        }

        @Override
        @SuppressWarnings({"rawtypes", "unchecked"})
        public Optional<InitialDataService> createInitialDataService() {
            var settings = new TableViewViewSettings(m_table.getDataTableSpec());
            settings.m_publishSelection = true;
            settings.m_subscribeToSelection = true;
            settings.m_title = "";
            settings.m_enablePagination = false;
            settings.m_compactMode = true;
            settings.m_showRowIndices = true;
            settings.m_skipRemainingColumns = true;
            return Optional.of(
                TableViewUtil.createInitialDataService(() -> settings, () -> m_table, m_selectionSupplier, m_tableId));
        }

        @Override
        public Optional<RpcDataService> createRpcDataService() {
            return Optional.of(TableViewUtil.createRpcDataService(
                TableViewUtil.createTableViewDataService(() -> m_table, m_selectionSupplier, m_tableId), m_tableId));
        }

        @Override
        public Page getPage() {
            return TableViewUtil.PAGE;
        }

        @Override
        public int getPortIndex() {
            // -1 because m_portIndex accounts for the implicit flow variable port while getPortIndex doesn't
            return m_portIndex - 1;
        }

    }

}
