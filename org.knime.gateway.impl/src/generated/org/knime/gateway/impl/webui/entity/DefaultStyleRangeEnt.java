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

/**
 * Defines the style of a range (e.g. within a workflow annotation).
 *
 * @param start
 * @param length
 * @param bold
 * @param italic
 * @param fontSize
 * @param color
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @author Kai Franze, KNIME GmbH
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public record DefaultStyleRangeEnt(
    Integer start,
    Integer length,
    Boolean bold,
    Boolean italic,
    Integer fontSize,
    String color) implements StyleRangeEnt {

    /**
     * Canonical constructor for {@link DefaultStyleRangeEnt} including null checks for non-nullable parameters.
     *
     * @param start
     * @param length
     * @param bold
     * @param italic
     * @param fontSize
     * @param color
     */
    public DefaultStyleRangeEnt {
        if(start == null) {
            throw new IllegalArgumentException("<start> must not be null.");
        }
        if(length == null) {
            throw new IllegalArgumentException("<length> must not be null.");
        }
        if(fontSize == null) {
            throw new IllegalArgumentException("<fontSize> must not be null.");
        }
    }

    @Override
    public String getTypeID() {
        return "StyleRange";
    }
  
    @Override
    public Integer getStart() {
        return start;
    }
    
    @Override
    public Integer getLength() {
        return length;
    }
    
    @Override
    public Boolean isBold() {
        return bold;
    }
    
    @Override
    public Boolean isItalic() {
        return italic;
    }
    
    @Override
    public Integer getFontSize() {
        return fontSize;
    }
    
    @Override
    public String getColor() {
        return color;
    }
    
    /**
     * A builder for {@link DefaultStyleRangeEnt}.
     */
    public static class DefaultStyleRangeEntBuilder implements StyleRangeEntBuilder {

        private Integer m_start;

        private Integer m_length;

        private Boolean m_bold;

        private Boolean m_italic;

        private Integer m_fontSize;

        private String m_color;

        @Override
        public DefaultStyleRangeEntBuilder setStart(Integer start) {
             if(start == null) {
                 throw new IllegalArgumentException("<start> must not be null.");
             }
             m_start = start;
             return this;
        }

        @Override
        public DefaultStyleRangeEntBuilder setLength(Integer length) {
             if(length == null) {
                 throw new IllegalArgumentException("<length> must not be null.");
             }
             m_length = length;
             return this;
        }

        @Override
        public DefaultStyleRangeEntBuilder setBold(Boolean bold) {
             m_bold = bold;
             return this;
        }

        @Override
        public DefaultStyleRangeEntBuilder setItalic(Boolean italic) {
             m_italic = italic;
             return this;
        }

        @Override
        public DefaultStyleRangeEntBuilder setFontSize(Integer fontSize) {
             if(fontSize == null) {
                 throw new IllegalArgumentException("<fontSize> must not be null.");
             }
             m_fontSize = fontSize;
             return this;
        }

        @Override
        public DefaultStyleRangeEntBuilder setColor(String color) {
             m_color = color;
             return this;
        }

        @Override
        public DefaultStyleRangeEnt build() {
            return new DefaultStyleRangeEnt(
                immutable(m_start),
                immutable(m_length),
                immutable(m_bold),
                immutable(m_italic),
                immutable(m_fontSize),
                immutable(m_color));
        }
    
    }

}
