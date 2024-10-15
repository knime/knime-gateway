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
 *   30 Oct 2024 (Robin Gerling): created
 */
package org.knime.gateway.impl.node.datavalueview.code;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.knime.core.data.json.JSONCellFactory;
import org.knime.core.data.json.JSONValue;
import org.knime.core.data.xml.XMLCellFactory;
import org.knime.core.data.xml.XMLValue;
import org.knime.gateway.impl.node.datavalueview.code.CodeValueView.CodeValueViewLanguages;

/**
 *
 * @author Robin Gerling
 */
public class CodeValueViewTest {
    /**
     * Test that the initial data of the {@link JSONCodeValueView} is the pretty-printed string representation of the
     * {@link JSONValue}.
     */
    @Test
    public void testJSONCodeValueView() {
        final var jsonCell = new JSONCellFactory().createCell("{\"key\": \"value\", \"bool\": true}");
        final var jsonValue = (JSONValue)jsonCell;

        final var initialData = new JSONCodeValueView(jsonValue).getInitialData();
        assertThat(initialData.language()).isEqualTo(CodeValueViewLanguages.JSON);
        assertThat(initialData.code()).isEqualTo("{\n    \"key\": \"value\",\n    \"bool\": true\n}");
    }

    /**
     * Test that the initial data of the {@link XMLCodeValueView} is the string representation of the {@link XMLValue}.
     */
    @Test
    public void testXMLCodeValueView() {
        final var xmlString = "<parent>\n    <child>Nested content</child>\n</parent>";
        final var xmlCell = new XMLCellFactory().createCell(xmlString);
        final var xmlValue = (XMLValue)xmlCell;

        final var initialData = new XMLCodeValueView(xmlValue).getInitialData();
        assertThat(initialData.language()).isEqualTo(CodeValueViewLanguages.XML);
        assertThat(initialData.code()).isEqualTo("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + xmlString);
    }
}
