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
 *   15 Jul 2025 (jschroeter): created
 */
package org.knime.gateway.impl.webui.preview.util;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.nodes.Entities.EscapeMode;

/**
 * Utility functions for rendering annotations on workflow previews
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 * @author Jakob Schröter, KNIME GmbH, Konstanz, Germany
 * @since 5.6
 */
public final class AnnotationUtils {

    /**
     * Convert an arbitrary HTML string to valid XHTML, to be SVG compatible
     * @param html a string containing the html content to be treated, potentially containing void tags, e.g. <hr>
     * @return cleaned html valid xhtml, which can be used inside of a foreign object in an SVG
     * @throws IllegalArgumentException if the string provided is malformed or can not be parsed
     */
    public static String cleanHtml(final String html) throws IllegalArgumentException {

        if (StringUtils.isEmpty(html)) {
            return "";
        }

        try {
            Document document = Jsoup.parse(html);
            OutputSettings settings = new OutputSettings();
            settings.syntax(OutputSettings.Syntax.xml);
            settings.escapeMode(EscapeMode.xhtml);
            settings.prettyPrint(false);
            document.outputSettings(settings);
            return document.body().html();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid html for annotation", e);
        }

    }
}
