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

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Supplier;

import org.knime.core.data.DataTableSpec;
import org.knime.core.data.statistics.UnivariateStatistics;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.KNIMEConstants;
import org.knime.core.node.workflow.NodeOutPort;
import org.knime.core.webui.data.DataServiceContext;
import org.knime.core.webui.data.DataServiceException;
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

    /**
     * The most recent statistics computation.
     */
    private static StatisticsComputation mostRecent;

    /**
     * Obtain a {@link CompletableFuture} for the statistics table, additionally managing any potentially current
     * running computations:
     * <ul>
     * <li>If a computation for the given table is already running or was the last one to complete, do not start a new
     * one and reuse that</li>
     * <li>If a computation for a different table is running, cancel that and start one for the given table</li>
     * </ul>
     *
     * @param tableId The ID of the source table
     * @param task The task to compute
     * @return A {@link CompletableFuture} for {@code task}
     */
    private static Future<BufferedDataTable> cancelOtherAndGetFutureFor(final String tableId,
        final Callable<BufferedDataTable> task) {
        if (hasFutureOf(tableId)) {
            return mostRecent.future();
        } else {
            cancelCurrent();
        }
        var newFuture = KNIMEConstants.GLOBAL_THREAD_POOL.enqueue(task);
        mostRecent = new StatisticsComputation(tableId, newFuture);
        return newFuture;
    }

    private static boolean hasFutureOf(final String tableId) {
        if (mostRecent == null) {
            return false;
        }
        return Objects.equals(mostRecent.tableId(), tableId);
    }

    private static void cancelCurrent() {
        if (mostRecent == null) {
            return;
        }
        mostRecent.future().cancel(true);
        mostRecent = null;
    }

    @Override
    public PortView createPortView(final BufferedDataTable table) {
        var nc = ((NodeOutPort)PortContext.getContext().getNodePort()).getConnectedNodeContainer();
        var nodeId = nc.getID();
        var tableId = "statistics_" + TableViewUtil.toTableId(nodeId) + "_" + table.getBufferedTableId();
        var numColumns = table.getSpec().getNumColumns();

        var selectedStatistics = UnivariateStatistics.getDefaultStatistics();

        Supplier<BufferedDataTable> tableSupplier = () -> {
            var context = DataServiceContext.get().getExecutionContext();
            try {
                return cancelOtherAndGetFutureFor(tableId,
                    () -> computeStatisticsTable(table, selectedStatistics, context)).get();
            } catch (CancellationException | ExecutionException | InterruptedException e) { // NOSONAR: expected
                throw new DataServiceException("Statistics table computation aborted", e);
            }
        };

        return new PortView() { // NOSONAR
            @Override
            public Page getPage() {
                return TableViewUtil.PAGE;
            }

            @SuppressWarnings({"rawtypes", "unchecked"})
            @Override
            public Optional<InitialDataService> createInitialDataService() {
                var settings = getSettingsForDataTable(UnivariateStatistics.getStatisticsTableSpec(selectedStatistics),
                    numColumns);
                Runnable onDispose = () -> {
                    if (hasFutureOf(tableId)) {
                        cancelCurrent();
                    }
                };
                return Optional
                    .of(TableViewUtil.createInitialDataService(() -> settings, tableSupplier, null, tableId,
                        onDispose));
            }

            @Override
            public Optional<RpcDataService> createRpcDataService() {
                return Optional.empty(); // Don't compute statistics twice
            }
        };
    }

    private static BufferedDataTable computeStatisticsTable(final BufferedDataTable table,
        final List<UnivariateStatistics.Statistic> selectedStatistics, final ExecutionContext context) {
        try {
            return UnivariateStatistics.computeStatisticsTable(table, context, selectedStatistics);
        } catch (CanceledExecutionException e) {
            throw new DataServiceException("Statistics computation cancelled", e);
        }
    }

    /**
     * Package scope for testing
     */
    static TableViewViewSettings getSettingsForDataTable(final DataTableSpec tableSpec, final int numColumns) {
        var settings = new TableViewViewSettings(tableSpec);
        settings.m_enableGlobalSearch = false;
        settings.m_enableSortingByHeader = false;
        settings.m_title = "";
        settings.m_enableColumnSearch = false;
        settings.m_compactMode = true;
        settings.m_subscribeToSelection = false;
        settings.m_publishSelection = false;
        settings.m_showColumnDataType = false;
        // enable pagination in order to not lazily fetch data (there isn't any) after initially loading the table in the FE
        // BUT: set the page-size to the 'maximum' such that the 'paging-buttons' actually don't show up
        settings.m_enablePagination = true;
        settings.m_pageSize = numColumns;
        settings.m_enableRendererSelection = false;
        settings.m_showRowKeys = false;
        settings.m_showRowIndices = false;
        return settings;
    }

    /**
     * @param tableId Identifies the source table
     * @param future A Future that might still be in progress or be already completed
     */
    private record StatisticsComputation(String tableId, Future<BufferedDataTable> future) { // NOSONAR
    }
}
