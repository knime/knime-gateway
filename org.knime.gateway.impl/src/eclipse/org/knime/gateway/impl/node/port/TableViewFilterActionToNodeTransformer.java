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
 *   Nov 8, 2024 (Paul Bärnreuther): created
 */
package org.knime.gateway.impl.node.port;

import java.util.Arrays;
import java.util.stream.Stream;

import org.knime.base.node.preproc.filter.row3.RowFilterNodeSettingsUtil;
import org.knime.base.node.preproc.filter.row3.RowFilterNodeSettingsUtil.StringColumnFilter;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.workflow.SingleNodeContainer;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.webui.node.view.table.actions.FilterActionParameters;
import org.knime.gateway.api.entity.NodeIDEnt;

class TableViewFilterActionToNodeTransformer extends TableViewActionToNodeTranformer {

    final FilterActionParameters m_parameters;

    TableViewFilterActionToNodeTransformer(final FilterActionParameters parameters, final String projectId,
        final NodeIDEnt workflowId, final WorkflowManager wfm) {
        super("org.knime.base.node.preproc.filter.row3.RowFilterNodeFactory", projectId, workflowId, wfm);
        m_parameters = parameters;
    }

    @Override
    protected void toConfigure(final SingleNodeContainer nc, final WorkflowManager wfm) {

        try {
            final var nodeSettings = nc.getNodeSettings();

            final var adjustedSettings = RowFilterNodeSettingsUtil.writeFiltersToRowFilterNodeSettings(toFilters(m_parameters), nodeSettings);

            wfm.loadNodeSettings(nc.getID(), adjustedSettings);
        } catch (InvalidSettingsException ex) {
            throw new RuntimeException(ex);
        }

    }

    private StringColumnFilter[] toFilters(final FilterActionParameters parameters) {
        return Stream.concat(//
            parameters.getRowKeySearchTerm().stream().map(StringColumnFilter::searchingRowKeys), //
            Arrays.stream(parameters.getColumnFilters())
                .map(TableViewFilterActionToNodeTransformer::toStringColumnFilter)//
        ).toArray(StringColumnFilter[]::new);
    }

    private static StringColumnFilter
        toStringColumnFilter(final FilterActionParameters.ColumnFilterParameters columnFilterParameters) {
        return StringColumnFilter.filteringColumn(columnFilterParameters.getColumnName(),
            columnFilterParameters.getFilterValues());
    }


}
