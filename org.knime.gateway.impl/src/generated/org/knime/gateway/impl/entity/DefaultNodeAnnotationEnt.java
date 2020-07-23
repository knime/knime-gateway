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
package org.knime.gateway.impl.entity;

import static org.knime.gateway.api.util.EntityUtil.immutable;

import java.util.Objects;

import org.knime.gateway.api.entity.BoundsEnt;
import org.knime.gateway.api.entity.StyleRangeEnt;
import org.knime.gateway.impl.entity.DefaultAnnotationEnt;

import org.knime.gateway.api.entity.NodeAnnotationEnt;

/**
 * The annotation to a node.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/org.knime.gateway.impl-config.json"})
public class DefaultNodeAnnotationEnt extends DefaultAnnotationEnt implements NodeAnnotationEnt {

  protected Boolean m__default;
  
  protected DefaultNodeAnnotationEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "NodeAnnotation";
  }
  
  private DefaultNodeAnnotationEnt(DefaultNodeAnnotationEntBuilder builder) {
    super();
    if(builder.m_type == null) {
        throw new IllegalArgumentException("type must not be null.");
    }
    m_type = immutable(builder.m_type);
    m_text = immutable(builder.m_text);
    m_backgroundColor = immutable(builder.m_backgroundColor);
    m_bounds = immutable(builder.m_bounds);
    m_textAlignment = immutable(builder.m_textAlignment);
    m_borderSize = immutable(builder.m_borderSize);
    m_borderColor = immutable(builder.m_borderColor);
    m_defaultFontSize = immutable(builder.m_defaultFontSize);
    m_version = immutable(builder.m_version);
    m_styleRanges = immutable(builder.m_styleRanges);
    m__default = immutable(builder.m__default);
  }
  
   /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (getClass() != o.getClass()) {
            return false;
        }
        DefaultNodeAnnotationEnt ent = (DefaultNodeAnnotationEnt)o;
        return Objects.equals(m_type, ent.m_type) && Objects.equals(m_text, ent.m_text) && Objects.equals(m_backgroundColor, ent.m_backgroundColor) && Objects.equals(m_bounds, ent.m_bounds) && Objects.equals(m_textAlignment, ent.m_textAlignment) && Objects.equals(m_borderSize, ent.m_borderSize) && Objects.equals(m_borderColor, ent.m_borderColor) && Objects.equals(m_defaultFontSize, ent.m_defaultFontSize) && Objects.equals(m_version, ent.m_version) && Objects.equals(m_styleRanges, ent.m_styleRanges) && Objects.equals(m__default, ent.m__default);
    }


  @Override
  public Boolean isDefault() {
        return m__default;
    }
    
  
    public static class DefaultNodeAnnotationEntBuilder implements NodeAnnotationEntBuilder {
    
        public DefaultNodeAnnotationEntBuilder(){
            super();
        }
    
        private String m_type;
        private String m_text;
        private Integer m_backgroundColor;
        private BoundsEnt m_bounds;
        private String m_textAlignment;
        private Integer m_borderSize;
        private Integer m_borderColor;
        private Integer m_defaultFontSize;
        private Integer m_version;
        private java.util.List<StyleRangeEnt> m_styleRanges = new java.util.ArrayList<>();
        private Boolean m__default;

        @Override
        public DefaultNodeAnnotationEntBuilder setType(String type) {
             if(type == null) {
                 throw new IllegalArgumentException("type must not be null.");
             }
             m_type = type;
             return this;
        }

        @Override
        public DefaultNodeAnnotationEntBuilder setText(String text) {
             m_text = text;
             return this;
        }

        @Override
        public DefaultNodeAnnotationEntBuilder setBackgroundColor(Integer backgroundColor) {
             m_backgroundColor = backgroundColor;
             return this;
        }

        @Override
        public DefaultNodeAnnotationEntBuilder setBounds(BoundsEnt bounds) {
             m_bounds = bounds;
             return this;
        }

        @Override
        public DefaultNodeAnnotationEntBuilder setTextAlignment(String textAlignment) {
             m_textAlignment = textAlignment;
             return this;
        }

        @Override
        public DefaultNodeAnnotationEntBuilder setBorderSize(Integer borderSize) {
             m_borderSize = borderSize;
             return this;
        }

        @Override
        public DefaultNodeAnnotationEntBuilder setBorderColor(Integer borderColor) {
             m_borderColor = borderColor;
             return this;
        }

        @Override
        public DefaultNodeAnnotationEntBuilder setDefaultFontSize(Integer defaultFontSize) {
             m_defaultFontSize = defaultFontSize;
             return this;
        }

        @Override
        public DefaultNodeAnnotationEntBuilder setVersion(Integer version) {
             m_version = version;
             return this;
        }

        @Override
        public DefaultNodeAnnotationEntBuilder setStyleRanges(java.util.List<StyleRangeEnt> styleRanges) {
             m_styleRanges = styleRanges;
             return this;
        }

        @Override
        public DefaultNodeAnnotationEntBuilder setDefault(Boolean _default) {
             m__default = _default;
             return this;
        }

        
        @Override
        public DefaultNodeAnnotationEnt build() {
            return new DefaultNodeAnnotationEnt(this);
        }
    
    }

}
