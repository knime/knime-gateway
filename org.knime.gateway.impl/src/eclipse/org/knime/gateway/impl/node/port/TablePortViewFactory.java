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

import static org.knime.core.webui.node.view.table.RowHeightPersistorUtil.LEGACY_CUSTOM_ROW_HEIGHT_COMPACT;
import static org.knime.gateway.api.entity.EntityBuilderManager.builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import org.knime.core.data.RowKey;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.workflow.NodeContainerParent;
import org.knime.core.node.workflow.NodeOutPort;
import org.knime.core.node.workflow.NodeOutPortWrapper;
import org.knime.core.node.workflow.SingleNodeContainer;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.webui.data.InitialDataService;
import org.knime.core.webui.data.RpcDataService;
import org.knime.core.webui.node.dialog.defaultdialog.setting.selection.SelectionMode;
import org.knime.core.webui.node.port.PortContext;
import org.knime.core.webui.node.port.PortView;
import org.knime.core.webui.node.port.PortViewFactory;
import org.knime.core.webui.node.view.table.TableView;
import org.knime.core.webui.node.view.table.TableViewManager;
import org.knime.core.webui.node.view.table.TableViewUtil;
import org.knime.core.webui.node.view.table.TableViewViewSettings;
import org.knime.core.webui.node.view.table.TableViewViewSettings.RowHeightMode;
import org.knime.core.webui.node.view.table.TableViewViewSettings.VerticalPaddingMode;
import org.knime.core.webui.node.view.table.actions.ActionParameters;
import org.knime.core.webui.node.view.table.actions.ActionType;
import org.knime.core.webui.node.view.table.actions.FilterActionParameters;
import org.knime.core.webui.node.view.table.actions.SortActionParameters;
import org.knime.core.webui.page.Page;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.AddNodeCommandEnt;
import org.knime.gateway.api.webui.entity.AddNodeCommandEnt.AddNodeCommandEntBuilder;
import org.knime.gateway.api.webui.entity.AddNodeCommandEnt.NodeRelationEnum;
import org.knime.gateway.api.webui.entity.NodeFactoryKeyEnt.NodeFactoryKeyEntBuilder;
import org.knime.gateway.api.webui.entity.WorkflowCommandEnt.KindEnum;
import org.knime.gateway.api.webui.entity.XYEnt.XYEntBuilder;

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

        var portIndex = getPortIndex(nodePort);
        var hiLiteHandler = TableViewManager.getOutHiLiteHandler(snc, portIndex - 1).orElse(null);
        Supplier<Set<RowKey>> selectionSupplier;
        if (hiLiteHandler == null) {
            selectionSupplier = Collections::emptySet;
        } else {
            selectionSupplier = hiLiteHandler::getHiLitKeys;
        }
        return new TablePortView(table, tableId, selectionSupplier, portIndex, createDummyAddNodeCommand(snc));
    }

    private BiConsumer<String, Map<ActionType, ActionParameters>> createDummyAddNodeCommand(final SingleNodeContainer snc) {

        WorkflowManager wfm = snc.getParent();
        WorkflowManager projectWfm = wfm.getProjectWFM();

        NodeContainerParent ncParent = wfm.getDirectNCParent();
        boolean isComponentProject = projectWfm.isComponentProjectWFM();
        final NodeIDEnt workflowId;
        if (ncParent instanceof SubNodeContainer subnc) {
            // it's a component's workflow
            workflowId = new NodeIDEnt(subnc.getID(), isComponentProject);
        } else {
            workflowId = new NodeIDEnt(wfm.getID(), isComponentProject);
        }

        return (projectId, actionMap) -> {
            var transformers = new ArrayList<TableViewActionToNodeTranformer>();
            actionMap.forEach((actionType, actionParameters) -> {
                transformers.add(createAddNodeCommand(snc, actionType, actionParameters, projectId, workflowId, wfm));
            });
            // ToDo sort commands to what order makes sense


            var lastCreatedNodeId = snc;
            for (var transformer : transformers) {
                var newNodeId = transformer.toNode(lastCreatedNodeId);
                lastCreatedNodeId = newNodeId;
            }
        };
    }

    private static TableViewActionToNodeTranformer createAddNodeCommand(final SingleNodeContainer sourceNode, final ActionType actionType,
        final ActionParameters actionParameters, final String projectId, final NodeIDEnt workflowId,
        final WorkflowManager wfm) {

        switch (actionType) {
            case SORT:
                return new TableViewSortActionToNodeTransformer((SortActionParameters) actionParameters, projectId, workflowId, wfm);
            case FILTER:
                return new TableViewFilterActionToNodeTransformer((FilterActionParameters) actionParameters, projectId, workflowId, wfm);
//                return new AddNodeCommandAndConfiguration(createFilterNodeCommand(actionParameters),
//                    configureSorter((SortActionParameters) actionParameters));
            case SELECTED_ROWS:
                return new TableViewSelectionSplitterActionToNodeTransformer(projectId, workflowId, wfm);
            default:
                return null;
//                throw new IllegalArgumentException("Unknown action type: " + actionType);
        }
    }

    /**
     * @param snc
     */
    private static AddNodeCommandEnt createAddSorterNodeCommand(final SingleNodeContainer snc) {
        final var addCommand = builder(AddNodeCommandEntBuilder.class).setNodeFactory(//
            builder(NodeFactoryKeyEntBuilder.class)//
                // .setClassName("org.knime.base.node.preproc.filter.row3.RowFilterNodeFactory")//
                .setClassName("org.knime.base.node.preproc.sorter.SorterNodeFactory")//
                //.setClassName("org.knime.base.node.preproc.filter.hilite.HiliteFilterNodeFactory")//
                .build()//
        )//
             .setPosition(builder(XYEntBuilder.class)//
                    .setX(100)//
                    .setY(100)//
                    .build())
            .setSourceNodeId(new NodeIDEnt(snc.getID()))
            .setNodeRelation(NodeRelationEnum.SUCCESSORS)
            .setKind(KindEnum.ADD_NODE)//
            .build();
        return addCommand;
    }

    private static BiConsumer<SingleNodeContainer, WorkflowManager> configureSorter(final SortActionParameters parameters) {
        return (nc, wfm) -> {
            try {
                final var nodeSettings = nc.getNodeSettings();
                final var modelSettings = nodeSettings.getNodeSettings("model");
                final var sortingCriteria = modelSettings.getNodeSettings("sortingCriteria");
                final var firstCriterion = sortingCriteria.getNodeSettings("0");
                firstCriterion.getNodeSettings("column").addString("selected",
                    parameters.isRowId() ? "<row-keys>" : parameters.columnName());
                firstCriterion.addString("sortingOrder", parameters.isAscending() ? "ASCENDING" : "DESCENDING");
                wfm.loadNodeSettings(nc.getID(), nodeSettings);
            } catch (InvalidSettingsException ex) {
                // TODO Auto-generated catch
            }
        };
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

        private final BiConsumer<String, Map<ActionType, ActionParameters>> m_dummyAddNodeCommand;

        TablePortView(final BufferedDataTable table, final String tableId,
            final Supplier<Set<RowKey>> selectionSupplier, final int portIndex,
            final BiConsumer<String, Map<ActionType, ActionParameters>> dummyAddNodeCommand) {
            m_table = table;
            m_tableId = tableId;
            m_selectionSupplier = selectionSupplier;
            m_portIndex = portIndex;
            m_dummyAddNodeCommand = dummyAddNodeCommand;

        }

        @Override
        @SuppressWarnings({"rawtypes", "unchecked"})
        public Optional<InitialDataService> createInitialDataService() {
            final var spec = m_table.getDataTableSpec();
            var settings = new TableViewViewSettings(spec);
            settings.m_selectionMode = SelectionMode.EDIT;
            settings.m_title = "";
            settings.m_enablePagination = false;
            settings.m_rowHeightMode = RowHeightMode.CUSTOM;
            settings.m_customRowHeight = LEGACY_CUSTOM_ROW_HEIGHT_COMPACT;
            settings.m_verticalPaddingMode = VerticalPaddingMode.COMPACT;
            settings.m_showRowIndices = true;
            settings.m_showOnlySelectedRowsConfigurable = true;
            settings.m_skipRemainingColumns = true;
            //settings.m_enableDataValueViews = true;
            return Optional.of(
                TableViewUtil.createInitialDataService(() -> settings, () -> m_table, m_selectionSupplier, m_tableId));
        }

        @Override
        public Optional<RpcDataService> createRpcDataService() {
            return Optional
                .of(TableViewUtil.createRpcDataService(TableViewUtil.createTableViewDataService(() -> m_table,
                    m_selectionSupplier, m_tableId, m_dummyAddNodeCommand), m_tableId));
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
