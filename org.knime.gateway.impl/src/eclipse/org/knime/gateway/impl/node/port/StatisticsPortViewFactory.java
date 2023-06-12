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
 */
package org.knime.gateway.impl.node.port;

import java.util.Optional;
import java.util.function.Supplier;

import org.knime.core.data.statistics.UnivariateStatistics;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.workflow.NodeOutPort;
import org.knime.core.webui.data.DataServiceContext;
import org.knime.core.webui.data.InitialDataService;
import org.knime.core.webui.data.RpcDataService;
import org.knime.core.webui.node.port.PortContext;
import org.knime.core.webui.node.port.PortView;
import org.knime.core.webui.node.port.PortViewFactory;
import org.knime.core.webui.node.view.table.TableViewUtil;
import org.knime.core.webui.node.view.table.TableViewViewSettings;
import org.knime.core.webui.page.Page;

/**
 * A port view displaying statistics over the columns of the output table at that port.
 *
 * @author Benjamin Moser, KNIME GmbH, Konstanz, Germany
 */
@SuppressWarnings("restriction")
public class StatisticsPortViewFactory implements PortViewFactory<BufferedDataTable> {

    @Override
    public PortView createPortView(final BufferedDataTable table) {
        var nc = ((NodeOutPort)PortContext.getContext().getNodePort()).getConnectedNodeContainer();
        var nodeId = nc.getID();
        var tableId = "statistics_" + TableViewUtil.toTableId(nodeId);

        var selectedStatistics = UnivariateStatistics.getDefaultStatistics();

        Supplier<BufferedDataTable> tableSupplier = () -> {
            var context = DataServiceContext.get().getExecutionContext();
            try {
                return UnivariateStatistics.computeStatisticsTable(table, context, selectedStatistics);
            } catch (CanceledExecutionException e) {
                NodeLogger.getLogger(this.getClass()).error("Statistics computation cancelled", e);
                return null;
            }
        };

        return new PortView() { // NOSONAR
            @Override
            public Page getPage() {
                return TableViewUtil.PAGE;
            }

            @Override
            @SuppressWarnings({"rawtypes", "unchecked", "restriction"})
            public Optional<InitialDataService> createInitialDataService() {
                var settings = TableViewViewSettings
                    .getSpecViewSettings(UnivariateStatistics.getStatisticsTableSpec(selectedStatistics));
                return Optional.of(TableViewUtil.createInitialDataService(() -> settings, tableSupplier, tableId));
            }

            @SuppressWarnings("restriction")
            @Override
            public Optional<RpcDataService> createRpcDataService() {
                var tableViewDataService = TableViewUtil.createTableViewDataService(tableSupplier, tableId);
                return Optional.of(TableViewUtil.createRpcDataService(tableViewDataService, tableId));
            }
        };
    }
}
