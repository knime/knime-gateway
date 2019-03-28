/* ------------------------------------------------------------------
 * This source code, its documentation and all appendant files
 * are protected by copyright law. All rights reserved.
 *
 * Copyright by KNIME AG, Zurich, Switzerland
 *
 * You may not modify, publish, transmit, transfer or sell, reproduce,
 * create derivative works from, distribute, perform, display, or in
 * any way exploit any of the content, in whole or in part, except as
 * otherwise expressly permitted in writing by the copyright owner or
 * as specified in the license file distributed with this product.
 *
 * If you have any questions please contact the copyright holder:
 * website: www.knime.com
 * email: contact@knime.com
 * ---------------------------------------------------------------------
 *
 */
package com.knime.gateway.entity.impl;

import static com.knime.gateway.util.EntityUtil.immutable;

import java.util.Objects;

import com.knime.gateway.entity.BoundsEnt;
import com.knime.gateway.entity.StyleRangeEnt;
import com.knime.gateway.entity.impl.DefaultAnnotationEnt;

import com.knime.gateway.entity.NodeAnnotationEnt;

/**
 * The annotation to a node.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"org.knime.gateway.codegen.GatewayCodegen", "src-gen/com.knime.gateway-implementations-config.json"})
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
