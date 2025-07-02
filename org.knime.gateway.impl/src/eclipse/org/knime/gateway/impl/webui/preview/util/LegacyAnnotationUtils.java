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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.knime.gateway.api.webui.entity.StyleRangeEnt;
import org.knime.gateway.api.webui.entity.WorkflowAnnotationEnt;
import org.knime.gateway.impl.webui.entity.DefaultStyleRangeEnt.DefaultStyleRangeEntBuilder;
import org.knime.gateway.impl.webui.preview.util.ShapeConstants.ShapeKey;

/**
 * Utility functions for legacy annotations to be used in templates
 *
 * @author Jakob Schröter, KNIME GmbH, Konstanz, Germany
 * @since 5.6
 */
@SuppressWarnings({"javadoc", "hiding"})
public final class LegacyAnnotationUtils {
    private static Integer DEFAULT_FONT_SIZE = 12;

    private static class NormalizedResult {
        private final List<StyleRangeEnt> m_normalized;
        private final boolean m_isValid;

        public NormalizedResult(final List<StyleRangeEnt> normalized, final boolean isValid) {
            m_normalized = normalized;
            m_isValid = isValid;
        }
    }

    public static class TextRange {
        private final String text;

        private final StyleRangeEnt styleRange;

        public TextRange(final String text, final StyleRangeEnt styleRange) {
            this.text = text;
            this.styleRange = styleRange;
        }

        public StyleRangeEnt getStyleRange() {
            return styleRange;
        }

        public String getText() {
            return text;
        }
    }

    private static StyleRangeEnt buildSimpleStyleRange(final int start, final int length, final int fontSize) {
        var builder = new DefaultStyleRangeEntBuilder();
        builder.setStart(start);
        builder.setLength(length);
        builder.setFontSize(fontSize);
        return builder.build();
    }

    private static List<StyleRangeEnt> getFallback(final String text, final Integer defaultFontSize) {
        List<StyleRangeEnt> fallback = new ArrayList<>();

        fallback.add(buildSimpleStyleRange(0, text.length(), defaultFontSize));
        return fallback;
    }

    /**
     * Normalize a given styleRange list: 1. handle empty list 2. return fallback if format is not supported
     * (overlapping ranges) 3. fill gaps between ranges
     *
     * @return NormalizedResult Normalized styleRange list, and a flag indicating whether the input range was valid
     */
    private static NormalizedResult normalize(final List<StyleRangeEnt> styleRanges, final String text, final Integer defaultFontSize) {
        List<StyleRangeEnt> normalized = new ArrayList<>(styleRanges);
        normalized.sort(Comparator.comparingInt(a -> a.getStart()));

        // handle empty string
        if (text == null || text.isEmpty()) {
            return new NormalizedResult(new ArrayList<>(), normalized.size() == 0);
        }

        // handle empty range list
        if (normalized.size() == 0) {
            return new NormalizedResult(getFallback(text, defaultFontSize), true);
        }

        // validate overlapping ranges (not supported)
        for (int i = 0; i < normalized.size(); i++) {
            StyleRangeEnt range = normalized.get(i);
            StyleRangeEnt nextRange = (i + 1 < normalized.size()) ? normalized.get(i + 1) : null;
            if (range.getLength() < 0 || range.getStart() < 0 || range.getStart() + range.getLength() > text.length()
                || (nextRange != null && range.getStart() + range.getLength() > nextRange.getStart())) {
                return new NormalizedResult(getFallback(text, defaultFontSize), false);
            }
        }

        // fill gap at start
        if (normalized.get(0).getStart() != 0) {
            normalized.add(0, buildSimpleStyleRange(0, normalized.get(0).getStart(), defaultFontSize));
        }

        // fill gaps in between
        List<StyleRangeEnt> filled = new ArrayList<>();
        for (StyleRangeEnt range : normalized) {
            if (!filled.isEmpty()) {
                StyleRangeEnt lastRange = filled.get(filled.size() - 1);
                int lastEnd = lastRange.getStart() + lastRange.getLength();
                if (lastEnd < range.getStart()) {
                    filled.add(buildSimpleStyleRange(lastEnd, range.getStart() - lastEnd, defaultFontSize));
                }
            }
            filled.add(range);
        }
        normalized = filled;

        // fill gap at end
        StyleRangeEnt lastRange = normalized.get(normalized.size() - 1);
        int lastEnd = lastRange.getStart() + lastRange.getLength();
        if (lastEnd < text.length()) {
            normalized.add(buildSimpleStyleRange(lastEnd, text.length() - lastEnd, defaultFontSize));
        }

        return new NormalizedResult(normalized, true);
    }

    /**
     * Apply styleRanges to a given text
     * @return List<TextRange> An array of text chunks with style info
     */
    public static List<TextRange> applyStyleRanges(final List<StyleRangeEnt> styleRanges, final String text, final Integer defaultFontSize) {
        NormalizedResult norm = normalize(styleRanges, text, defaultFontSize != null ? defaultFontSize : DEFAULT_FONT_SIZE);
        List<TextRange> textRanges = new ArrayList<>();
        for (StyleRangeEnt styleRange : norm.m_normalized) {
            String chunk = text.substring(styleRange.getStart(), styleRange.getStart() + styleRange.getLength());
            textRanges.add(new TextRange(chunk, styleRange));
        }
        return textRanges;
    }

    private static String getFontSizeCSS(final Integer size) {
        return size == null ? "" : "font-size:"
            + size * ShapeConstants.get(ShapeKey.LEGACY_ANNOTATIONS_FONT_SIZE_POINT_TO_PIXEL_FACTOR) + "px;";
    }

    public static String getWrapperStyle(final WorkflowAnnotationEnt annotation) {
        String style = LegacyAnnotationUtils.getFontSizeCSS(
            annotation.getDefaultFontSize() != null ? annotation.getDefaultFontSize() : DEFAULT_FONT_SIZE);
        if (annotation.getTextAlign() != null) {
            style += "text-align:" + annotation.getTextAlign() + ";";
        }

        return style;
    }

    public static String getPartStyle(final StyleRangeEnt styleRange) {
        String style = "";

        if (styleRange.getFontSize() != null) {
            style += LegacyAnnotationUtils.getFontSizeCSS(styleRange.getFontSize());
        }

        if (styleRange.getColor() != null) {
            style += "color:" + styleRange.getColor() + ";";
        }

        if (styleRange.isBold() != null) {
            style += "font-weight:bold;";
        }
        if (styleRange.isItalic() != null) {
            style += "font-style:italic;";
        }

        return style;
    }
}
