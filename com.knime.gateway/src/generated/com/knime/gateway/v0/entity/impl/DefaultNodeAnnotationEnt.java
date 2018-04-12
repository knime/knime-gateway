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

import com.knime.gateway.v0.entity.BoundsEnt;
import com.knime.gateway.v0.entity.StyleRangeEnt;
import com.knime.gateway.v0.entity.impl.DefaultAnnotationEnt;

import com.knime.gateway.v0.entity.NodeAnnotationEnt;

/**
 * The annotation to a node.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
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
    m_type = builder.m_type;
    m_text = builder.m_text;
    m_backgroundColor = builder.m_backgroundColor;
    m_bounds = builder.m_bounds;
    m_textAlignment = builder.m_textAlignment;
    m_borderSize = builder.m_borderSize;
    m_borderColor = builder.m_borderColor;
    m_defaultFontSize = builder.m_defaultFontSize;
    m_version = builder.m_version;
    m_styleRanges = builder.m_styleRanges;
    m__default = builder.m__default;
  }


  @Override
  public Boolean isDefault() {
        return m__default;
    }
    
  
    public static class DefaultNodeAnnotationEntBuilder implements NodeAnnotationEntBuilder {
    
        public DefaultNodeAnnotationEntBuilder(){
            super();
        }
    
        private String m_type = null;
        private String m_text = null;
        private Integer m_backgroundColor = null;
        private BoundsEnt m_bounds;
        private String m_textAlignment = null;
        private Integer m_borderSize = null;
        private Integer m_borderColor = null;
        private Integer m_defaultFontSize = null;
        private Integer m_version = null;
        private java.util.List<StyleRangeEnt> m_styleRanges = new java.util.ArrayList<>();
        private Boolean m__default = null;

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
