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
package org.knime.gateway.impl.webui.entity;

import static org.knime.gateway.api.util.EntityUtil.immutable;

import java.util.Objects;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import org.knime.gateway.api.webui.entity.StyleRangeEnt;

import org.knime.gateway.api.webui.entity.AnnotationEnt;

/**
 * A text annotation.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public class DefaultAnnotationEnt  implements AnnotationEnt {

  protected String m_text;
  protected String m_backgroundColor;
  protected TextAlignEnum m_textAlign;
  protected Integer m_borderWidth;
  protected String m_borderColor;
  protected java.util.List<StyleRangeEnt> m_styleRanges;
  
  protected DefaultAnnotationEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "Annotation";
  }
  
  private DefaultAnnotationEnt(DefaultAnnotationEntBuilder builder) {
    
    m_text = immutable(builder.m_text);
    m_backgroundColor = immutable(builder.m_backgroundColor);
    m_textAlign = immutable(builder.m_textAlign);
    m_borderWidth = immutable(builder.m_borderWidth);
    m_borderColor = immutable(builder.m_borderColor);
    if(builder.m_styleRanges == null) {
        throw new IllegalArgumentException("styleRanges must not be null.");
    }
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
        return Objects.equals(m_text, ent.m_text) && Objects.equals(m_backgroundColor, ent.m_backgroundColor) && Objects.equals(m_textAlign, ent.m_textAlign) && Objects.equals(m_borderWidth, ent.m_borderWidth) && Objects.equals(m_borderColor, ent.m_borderColor) && Objects.equals(m_styleRanges, ent.m_styleRanges);
    }


  
   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
       return new HashCodeBuilder()
               .append(m_text)
               .append(m_backgroundColor)
               .append(m_textAlign)
               .append(m_borderWidth)
               .append(m_borderColor)
               .append(m_styleRanges)
               .toHashCode();
   }
  
	
	
  @Override
  public String getText() {
        return m_text;
  }
    
  @Override
  public String getBackgroundColor() {
        return m_backgroundColor;
  }
    
  @Override
  public TextAlignEnum getTextAlign() {
        return m_textAlign;
  }
    
  @Override
  public Integer getBorderWidth() {
        return m_borderWidth;
  }
    
  @Override
  public String getBorderColor() {
        return m_borderColor;
  }
    
  @Override
  public java.util.List<StyleRangeEnt> getStyleRanges() {
        return m_styleRanges;
  }
    
  
    public static class DefaultAnnotationEntBuilder implements AnnotationEntBuilder {
    
        public DefaultAnnotationEntBuilder(){
            
        }
    
        private String m_text;
        private String m_backgroundColor;
        private TextAlignEnum m_textAlign;
        private Integer m_borderWidth;
        private String m_borderColor;
        private java.util.List<StyleRangeEnt> m_styleRanges = new java.util.ArrayList<>();

        @Override
        public DefaultAnnotationEntBuilder setText(String text) {
             m_text = text;
             return this;
        }

        @Override
        public DefaultAnnotationEntBuilder setBackgroundColor(String backgroundColor) {
             m_backgroundColor = backgroundColor;
             return this;
        }

        @Override
        public DefaultAnnotationEntBuilder setTextAlign(TextAlignEnum textAlign) {
             m_textAlign = textAlign;
             return this;
        }

        @Override
        public DefaultAnnotationEntBuilder setBorderWidth(Integer borderWidth) {
             m_borderWidth = borderWidth;
             return this;
        }

        @Override
        public DefaultAnnotationEntBuilder setBorderColor(String borderColor) {
             m_borderColor = borderColor;
             return this;
        }

        @Override
        public DefaultAnnotationEntBuilder setStyleRanges(java.util.List<StyleRangeEnt> styleRanges) {
             if(styleRanges == null) {
                 throw new IllegalArgumentException("styleRanges must not be null.");
             }
             m_styleRanges = styleRanges;
             return this;
        }

        
        @Override
        public DefaultAnnotationEnt build() {
            return new DefaultAnnotationEnt(this);
        }
    
    }

}
