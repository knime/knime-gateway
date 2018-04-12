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


import com.knime.gateway.v0.entity.StyleRangeEnt;

/**
 * Defines the style of a range (e.g. within a workflow annotation).
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public class DefaultStyleRangeEnt  implements StyleRangeEnt {

  protected Integer m_start;
  protected Integer m_length;
  protected String m_fontName;
  protected FontStyleEnum m_fontStyle;
  protected Integer m_fontSize;
  protected Integer m_foregroundColor;
  
  protected DefaultStyleRangeEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "StyleRange";
  }
  
  private DefaultStyleRangeEnt(DefaultStyleRangeEntBuilder builder) {
    
    m_start = builder.m_start;
    m_length = builder.m_length;
    m_fontName = builder.m_fontName;
    m_fontStyle = builder.m_fontStyle;
    m_fontSize = builder.m_fontSize;
    m_foregroundColor = builder.m_foregroundColor;
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
  public String getFontName() {
        return m_fontName;
    }
    
  @Override
  public FontStyleEnum getFontStyle() {
        return m_fontStyle;
    }
    
  @Override
  public Integer getFontSize() {
        return m_fontSize;
    }
    
  @Override
  public Integer getForegroundColor() {
        return m_foregroundColor;
    }
    
  
    public static class DefaultStyleRangeEntBuilder implements StyleRangeEntBuilder {
    
        public DefaultStyleRangeEntBuilder(){
            
        }
    
        private Integer m_start = null;
        private Integer m_length = null;
        private String m_fontName = null;
        private FontStyleEnum m_fontStyle = null;
        private Integer m_fontSize = null;
        private Integer m_foregroundColor = null;

        @Override
        public DefaultStyleRangeEntBuilder setStart(Integer start) {
             m_start = start;
             return this;
        }

        @Override
        public DefaultStyleRangeEntBuilder setLength(Integer length) {
             m_length = length;
             return this;
        }

        @Override
        public DefaultStyleRangeEntBuilder setFontName(String fontName) {
             m_fontName = fontName;
             return this;
        }

        @Override
        public DefaultStyleRangeEntBuilder setFontStyle(FontStyleEnum fontStyle) {
             m_fontStyle = fontStyle;
             return this;
        }

        @Override
        public DefaultStyleRangeEntBuilder setFontSize(Integer fontSize) {
             m_fontSize = fontSize;
             return this;
        }

        @Override
        public DefaultStyleRangeEntBuilder setForegroundColor(Integer foregroundColor) {
             m_foregroundColor = foregroundColor;
             return this;
        }

        
        @Override
        public DefaultStyleRangeEnt build() {
            return new DefaultStyleRangeEnt(this);
        }
    
    }

}
