/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.com; Email: contact@knime.com
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
 * ------------------------------------------------------------------------
 */
package org.knime.gateway.impl.webui.entity;

import static org.knime.gateway.api.util.EntityUtil.immutable;

import org.knime.gateway.api.webui.entity.StyleRangeEnt;

import org.knime.gateway.api.webui.entity.AnnotationEnt;

/**
 * A text annotation.
 *
 * @param text
 * @param backgroundColor
 * @param contentType
 * @param textAlign
 * @param defaultFontSize
 * @param styleRanges
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public record DefaultAnnotationEnt(
    String text,
    String backgroundColor,
    ContentTypeEnum contentType,
    TextAlignEnum textAlign,
    Integer defaultFontSize,
    java.util.List<StyleRangeEnt> styleRanges) implements AnnotationEnt {

    /**
     * Validation for required parameters not being {@code null}.
     */
    public DefaultAnnotationEnt {
        if(text == null) {
            throw new IllegalArgumentException("<text> must not be null.");
        }
        if(contentType == null) {
            throw new IllegalArgumentException("<contentType> must not be null.");
        }
    }

    @Override
    public String getTypeID() {
        return "Annotation";
    }
  
    @Override
    public String getText() {
        return text;
    }
    
    @Override
    public String getBackgroundColor() {
        return backgroundColor;
    }
    
    @Override
    public ContentTypeEnum getContentType() {
        return contentType;
    }
    
    @Override
    public TextAlignEnum getTextAlign() {
        return textAlign;
    }
    
    @Override
    public Integer getDefaultFontSize() {
        return defaultFontSize;
    }
    
    @Override
    public java.util.List<StyleRangeEnt> getStyleRanges() {
        return styleRanges;
    }
    
    /**
     * A builder for {@link DefaultAnnotationEnt}.
     */
    public static class DefaultAnnotationEntBuilder implements AnnotationEntBuilder {

        private String m_text;

        private String m_backgroundColor;

        private ContentTypeEnum m_contentType;

        private TextAlignEnum m_textAlign;

        private Integer m_defaultFontSize;

        private java.util.List<StyleRangeEnt> m_styleRanges;

        @Override
        public DefaultAnnotationEntBuilder setText(String text) {
             if(text == null) {
                 throw new IllegalArgumentException("<text> must not be null.");
             }
             m_text = text;
             return this;
        }

        @Override
        public DefaultAnnotationEntBuilder setBackgroundColor(String backgroundColor) {
             m_backgroundColor = backgroundColor;
             return this;
        }

        @Override
        public DefaultAnnotationEntBuilder setContentType(ContentTypeEnum contentType) {
             if(contentType == null) {
                 throw new IllegalArgumentException("<contentType> must not be null.");
             }
             m_contentType = contentType;
             return this;
        }

        @Override
        public DefaultAnnotationEntBuilder setTextAlign(TextAlignEnum textAlign) {
             m_textAlign = textAlign;
             return this;
        }

        @Override
        public DefaultAnnotationEntBuilder setDefaultFontSize(Integer defaultFontSize) {
             m_defaultFontSize = defaultFontSize;
             return this;
        }

        @Override
        public DefaultAnnotationEntBuilder setStyleRanges(java.util.List<StyleRangeEnt> styleRanges) {
             m_styleRanges = styleRanges;
             return this;
        }

        @Override
        public DefaultAnnotationEnt build() {
            return new DefaultAnnotationEnt(
                immutable(m_text),
                immutable(m_backgroundColor),
                immutable(m_contentType),
                immutable(m_textAlign),
                immutable(m_defaultFontSize),
                immutable(m_styleRanges));
        }
    
    }

}
