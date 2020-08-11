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

/**
 * Defines the style of a range (e.g. within a workflow annotation).
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public class DefaultStyleRangeEnt  implements StyleRangeEnt {

  protected Integer m_start;
  protected Integer m_length;
  protected Boolean m_bold;
  protected Boolean m_italic;
  protected Integer m_fontSize;
  protected String m_color;
  
  protected DefaultStyleRangeEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "StyleRange";
  }
  
  private DefaultStyleRangeEnt(DefaultStyleRangeEntBuilder builder) {
    
    if(builder.m_start == null) {
        throw new IllegalArgumentException("start must not be null.");
    }
    m_start = immutable(builder.m_start);
    if(builder.m_length == null) {
        throw new IllegalArgumentException("length must not be null.");
    }
    m_length = immutable(builder.m_length);
    m_bold = immutable(builder.m_bold);
    m_italic = immutable(builder.m_italic);
    if(builder.m_fontSize == null) {
        throw new IllegalArgumentException("fontSize must not be null.");
    }
    m_fontSize = immutable(builder.m_fontSize);
    m_color = immutable(builder.m_color);
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
        DefaultStyleRangeEnt ent = (DefaultStyleRangeEnt)o;
        return Objects.equals(m_start, ent.m_start) && Objects.equals(m_length, ent.m_length) && Objects.equals(m_bold, ent.m_bold) && Objects.equals(m_italic, ent.m_italic) && Objects.equals(m_fontSize, ent.m_fontSize) && Objects.equals(m_color, ent.m_color);
    }


  
   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
       return new HashCodeBuilder()
               .append(m_start)
               .append(m_length)
               .append(m_bold)
               .append(m_italic)
               .append(m_fontSize)
               .append(m_color)
               .toHashCode();
   }
  
	
	
  @Override
  public Integer getStart() {
        return m_start;
  }
    
  @Override
  public Integer getLength() {
        return m_length;
  }
    
  @Override
  public Boolean isBold() {
        return m_bold;
  }
    
  @Override
  public Boolean isItalic() {
        return m_italic;
  }
    
  @Override
  public Integer getFontSize() {
        return m_fontSize;
  }
    
  @Override
  public String getColor() {
        return m_color;
  }
    
  
    public static class DefaultStyleRangeEntBuilder implements StyleRangeEntBuilder {
    
        public DefaultStyleRangeEntBuilder(){
            
        }
    
        private Integer m_start;
        private Integer m_length;
        private Boolean m_bold;
        private Boolean m_italic;
        private Integer m_fontSize;
        private String m_color;

        @Override
        public DefaultStyleRangeEntBuilder setStart(Integer start) {
             if(start == null) {
                 throw new IllegalArgumentException("start must not be null.");
             }
             m_start = start;
             return this;
        }

        @Override
        public DefaultStyleRangeEntBuilder setLength(Integer length) {
             if(length == null) {
                 throw new IllegalArgumentException("length must not be null.");
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
                 throw new IllegalArgumentException("fontSize must not be null.");
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
            return new DefaultStyleRangeEnt(this);
        }
    
    }

}
