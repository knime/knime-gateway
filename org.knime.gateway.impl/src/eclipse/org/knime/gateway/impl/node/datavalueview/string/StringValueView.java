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
 *   Oct 31, 2024 (marcbux): created
 */
package org.knime.gateway.impl.node.datavalueview.string;

import java.util.Locale;
import java.util.Optional;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.StringValue;
import org.knime.core.data.renderer.StringValueRenderer;
import org.knime.core.webui.data.InitialDataService;
import org.knime.core.webui.data.RpcDataService;
import org.knime.core.webui.node.view.table.TableView;
import org.knime.core.webui.node.view.table.datavalue.DataValueView;
import org.knime.core.webui.page.Page;

/**
 * @author Marc Bux, KNIME GmbH, Berlin, Germany
 */
@SuppressWarnings("restriction")
public class StringValueView implements DataValueView {

    private static final int TRUNCATED_LENGTH = 10_000;

    private StringValue m_value;

    private DataColumnSpec m_colSpec;

    @SuppressWarnings("javadoc")
    public StringValueView(final StringValue value, final DataColumnSpec colSpec) {
        m_value = value;
        m_colSpec = colSpec;
    }

    @Override
    public Page getPage() {
        return Page.builder(TableView.class, "js-src/dist", "StringValueView.html").addResourceDirectory("assets")
            .build();
    }

    enum StringValueViewFormats {
            STRING, HTML;

        @Override
        public String toString() {
            return name().toLowerCase(Locale.getDefault());
        }
    }

    record StringValueViewInitialData(String value, StringValueViewFormats format) {
    }

    @Override
    public Optional<InitialDataService<StringValueViewInitialData>> createInitialDataService() {
        return Optional.of(InitialDataService.builder(this::getInitialData).build());
    }

    StringValueViewInitialData getInitialData() {
        var handler = m_colSpec.getValueFormatHandler();
        return handler == null
            ? new StringValueViewInitialData(
                StringValueRenderer.truncateOverlyLongStrings(m_value.getStringValue(), TRUNCATED_LENGTH),
                StringValueViewFormats.STRING)
            : new StringValueViewInitialData(handler.getFormatModel().getHTML(m_value), StringValueViewFormats.HTML);
    }

    @Override
    public Optional<RpcDataService> createRpcDataService() {
        return Optional.empty();
    }
}
