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
 *   Jul 19, 2022 (hornm): created
 */
package org.knime.gateway.impl.node.port;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.port.flowvariable.FlowVariablePortObject;
import org.knime.core.node.workflow.FlowObjectStack;
import org.knime.core.node.workflow.NodeOutPort;
import org.knime.core.ui.CoreUIPlugin;
import org.knime.core.webui.data.InitialDataService;
import org.knime.core.webui.data.RpcDataService;
import org.knime.core.webui.node.port.PortContext;
import org.knime.core.webui.node.port.PortView;
import org.knime.core.webui.node.port.PortViewFactory;
import org.knime.core.webui.node.view.flowvariable.FlowVariableViewUtil;
import org.knime.core.webui.node.view.table.TableViewUtil;
import org.knime.core.webui.node.view.table.TableViewViewSettings;
import org.knime.core.webui.node.view.table.data.TableViewInitialData;
import org.knime.core.webui.page.Page;

/**
 * Factory for a port view of a {@link FlowVariablePortObject}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public final class FlowVariablePortViewFactory implements PortViewFactory<FlowVariablePortObject> {

    /**
     * {@inheritDoc}
     */
    @Override
    public PortView createPortView(final FlowVariablePortObject portObject) {
        final var port = (NodeOutPort)PortContext.getContext().getNodePort();
        final var tableId = "flowvariableview" + port.getConnectedNodeContainer().getID();
        final var variables = Optional.ofNullable(port.getFlowObjectStack())//
            .map(FlowObjectStack::getAllAvailableFlowVariables) //
            .map(Map::values) //
            .orElse(Collections.emptyList());

        final Supplier<BufferedDataTable> bufferedTableSupplier = //
            () -> {
                return FlowVariableViewUtil.getBufferedTable(variables);
            };
        final Supplier<TableViewViewSettings> settingsSupplier = FlowVariableViewUtil::getSettings;

        return new PortView() {

            @Override
            public Optional<InitialDataService<TableViewInitialData>> createInitialDataService() {
                return Optional.of(//
                    TableViewUtil.createInitialDataService(settingsSupplier, bufferedTableSupplier, null, tableId));
            }

            @Override
            public Optional<RpcDataService> createRpcDataService() {
                return Optional.of(TableViewUtil.createRpcDataService(
                    TableViewUtil.createTableViewDataService(bufferedTableSupplier, null, tableId), tableId));
            }

            @Override
            public Page getPage() {
                return Page.builder(CoreUIPlugin.class, "js-src/dist", "TableView.js") //
                    .markAsReusable("flowvariableview") //
                    .build();
            }

        };
    }

}
