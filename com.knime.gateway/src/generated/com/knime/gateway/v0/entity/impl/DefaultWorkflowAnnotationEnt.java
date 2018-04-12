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

import com.knime.gateway.v0.entity.WorkflowAnnotationEnt;

/**
 * A workflow annotation.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public class DefaultWorkflowAnnotationEnt extends DefaultAnnotationEnt implements WorkflowAnnotationEnt {

  
  protected DefaultWorkflowAnnotationEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "WorkflowAnnotation";
  }
  
  private DefaultWorkflowAnnotationEnt(DefaultWorkflowAnnotationEntBuilder builder) {
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
  }


  
    public static class DefaultWorkflowAnnotationEntBuilder implements WorkflowAnnotationEntBuilder {
    
        public DefaultWorkflowAnnotationEntBuilder(){
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

        @Override
        public DefaultWorkflowAnnotationEntBuilder setType(String type) {
             if(type == null) {
                 throw new IllegalArgumentException("type must not be null.");
             }
             m_type = type;
             return this;
        }

        @Override
        public DefaultWorkflowAnnotationEntBuilder setText(String text) {
             m_text = text;
             return this;
        }

        @Override
        public DefaultWorkflowAnnotationEntBuilder setBackgroundColor(Integer backgroundColor) {
             m_backgroundColor = backgroundColor;
             return this;
        }

        @Override
        public DefaultWorkflowAnnotationEntBuilder setBounds(BoundsEnt bounds) {
             m_bounds = bounds;
             return this;
        }

        @Override
        public DefaultWorkflowAnnotationEntBuilder setTextAlignment(String textAlignment) {
             m_textAlignment = textAlignment;
             return this;
        }

        @Override
        public DefaultWorkflowAnnotationEntBuilder setBorderSize(Integer borderSize) {
             m_borderSize = borderSize;
             return this;
        }

        @Override
        public DefaultWorkflowAnnotationEntBuilder setBorderColor(Integer borderColor) {
             m_borderColor = borderColor;
             return this;
        }

        @Override
        public DefaultWorkflowAnnotationEntBuilder setDefaultFontSize(Integer defaultFontSize) {
             m_defaultFontSize = defaultFontSize;
             return this;
        }

        @Override
        public DefaultWorkflowAnnotationEntBuilder setVersion(Integer version) {
             m_version = version;
             return this;
        }

        @Override
        public DefaultWorkflowAnnotationEntBuilder setStyleRanges(java.util.List<StyleRangeEnt> styleRanges) {
             m_styleRanges = styleRanges;
             return this;
        }

        
        @Override
        public DefaultWorkflowAnnotationEnt build() {
            return new DefaultWorkflowAnnotationEnt(this);
        }
    
    }

}
