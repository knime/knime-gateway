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
import java.util.stream.IntStream;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataTableSpec;
import org.knime.core.webui.data.InitialDataService;
import org.knime.core.webui.data.RpcDataService;
import org.knime.core.webui.node.port.PortSpecViewFactory;
import org.knime.core.webui.node.port.PortView;
import org.knime.core.webui.page.Page;

/**
 * Provides information on the table spec.
 * 
 * @author Benjamin Moser, KNIME GmbH, Konstanz, Germany
 */
@SuppressWarnings("restriction")
public class TableSpecViewFactory implements PortSpecViewFactory<DataTableSpec> {
    @Override
    public PortView createPortView(final DataTableSpec portObjectSpec) {

        var nCols = portObjectSpec.getNumColumns();
        var colSpecs = IntStream.range(0, nCols) //
            .mapToObj(portObjectSpec::getColumnSpec) //
            .map(this::extractProperties) //
            .toList();

        return new PortView() {
            @Override
            public Page getPage() {
                return Page.builder(TableSpecViewFactory.class, "not-used", "vue_component_reference") //
                    .markAsReusable("TableSpecView") //
                    .build();
            }

            @SuppressWarnings({"rawtypes", "unchecked"})
            @Override
            public Optional<InitialDataService> createInitialDataService() {
                return Optional.of(InitialDataService.builder(() -> colSpecs).build());
            }

            @Override
            public Optional<RpcDataService> createRpcDataService() {
                return Optional.empty();
            }
        };
    }

    private ColSpec extractProperties(final DataColumnSpec originalColSpec) {
        return new ColSpec(originalColSpec.getName(), originalColSpec.getType().getName());
    }

    /**
     * Record to assemble subset of ColSpec attributes
     * 
     * @param name
     * @param dataType
     */
    record ColSpec(String name, String dataType) { // NOSONAR: record
    }
}
