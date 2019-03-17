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
package com.knime.gateway.v0.entity.impl;

import static com.knime.gateway.util.EntityUtil.immutable;

import java.util.Objects;

import com.knime.gateway.v0.entity.BoundsEnt;
import com.knime.gateway.v0.entity.StyleRangeEnt;

import com.knime.gateway.v0.entity.AnnotationEnt;

/**
 * A text annotation.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"org.knime.gateway.codegen.GatewayCodegen", "src-gen/com.knime.gateway-implementations-config.json"})
public class DefaultAnnotationEnt  implements AnnotationEnt {

  protected String m_type;
  protected String m_text;
  protected Integer m_backgroundColor;
  protected BoundsEnt m_bounds;
  protected String m_textAlignment;
  protected Integer m_borderSize;
  protected Integer m_borderColor;
  protected Integer m_defaultFontSize;
  protected Integer m_version;
  protected java.util.List<StyleRangeEnt> m_styleRanges;
  
  protected DefaultAnnotationEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "Annotation";
  }
  
  private DefaultAnnotationEnt(DefaultAnnotationEntBuilder builder) {
    
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
        DefaultAnnotationEnt ent = (DefaultAnnotationEnt)o;
        return Objects.equals(m_type, ent.m_type) && Objects.equals(m_text, ent.m_text) && Objects.equals(m_backgroundColor, ent.m_backgroundColor) && Objects.equals(m_bounds, ent.m_bounds) && Objects.equals(m_textAlignment, ent.m_textAlignment) && Objects.equals(m_borderSize, ent.m_borderSize) && Objects.equals(m_borderColor, ent.m_borderColor) && Objects.equals(m_defaultFontSize, ent.m_defaultFontSize) && Objects.equals(m_version, ent.m_version) && Objects.equals(m_styleRanges, ent.m_styleRanges);
    }


  @Override
  public String getType() {
        return m_type;
    }
    
  @Override
  public String getText() {
        return m_text;
    }
    
  @Override
  public Integer getBackgroundColor() {
        return m_backgroundColor;
    }
    
  @Override
  public BoundsEnt getBounds() {
        return m_bounds;
    }
    
  @Override
  public String getTextAlignment() {
        return m_textAlignment;
    }
    
  @Override
  public Integer getBorderSize() {
        return m_borderSize;
    }
    
  @Override
  public Integer getBorderColor() {
        return m_borderColor;
    }
    
  @Override
  public Integer getDefaultFontSize() {
        return m_defaultFontSize;
    }
    
  @Override
  public Integer getVersion() {
        return m_version;
    }
    
  @Override
  public java.util.List<StyleRangeEnt> getStyleRanges() {
        return m_styleRanges;
    }
    
  
    public static class DefaultAnnotationEntBuilder implements AnnotationEntBuilder {
    
        public DefaultAnnotationEntBuilder(){
            
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

        @Override
        public DefaultAnnotationEntBuilder setType(String type) {
             if(type == null) {
                 throw new IllegalArgumentException("type must not be null.");
             }
             m_type = type;
             return this;
        }

        @Override
        public DefaultAnnotationEntBuilder setText(String text) {
             m_text = text;
             return this;
        }

        @Override
        public DefaultAnnotationEntBuilder setBackgroundColor(Integer backgroundColor) {
             m_backgroundColor = backgroundColor;
             return this;
        }

        @Override
        public DefaultAnnotationEntBuilder setBounds(BoundsEnt bounds) {
             m_bounds = bounds;
             return this;
        }

        @Override
        public DefaultAnnotationEntBuilder setTextAlignment(String textAlignment) {
             m_textAlignment = textAlignment;
             return this;
        }

        @Override
        public DefaultAnnotationEntBuilder setBorderSize(Integer borderSize) {
             m_borderSize = borderSize;
             return this;
        }

        @Override
        public DefaultAnnotationEntBuilder setBorderColor(Integer borderColor) {
             m_borderColor = borderColor;
             return this;
        }

        @Override
        public DefaultAnnotationEntBuilder setDefaultFontSize(Integer defaultFontSize) {
             m_defaultFontSize = defaultFontSize;
             return this;
        }

        @Override
        public DefaultAnnotationEntBuilder setVersion(Integer version) {
             m_version = version;
             return this;
        }

        @Override
        public DefaultAnnotationEntBuilder setStyleRanges(java.util.List<StyleRangeEnt> styleRanges) {
             m_styleRanges = styleRanges;
             return this;
        }

        
        @Override
        public DefaultAnnotationEnt build() {
            return new DefaultAnnotationEnt(this);
        }
    
    }

}
