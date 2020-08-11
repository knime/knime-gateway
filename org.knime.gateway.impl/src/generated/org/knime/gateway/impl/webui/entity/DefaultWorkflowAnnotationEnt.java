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

import org.knime.gateway.api.webui.entity.BoundsEnt;
import org.knime.gateway.impl.webui.entity.DefaultAnnotationEnt;

import org.knime.gateway.api.webui.entity.WorkflowAnnotationEnt;

/**
 * A workflow annotation.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public class DefaultWorkflowAnnotationEnt extends DefaultAnnotationEnt implements WorkflowAnnotationEnt {

  protected TextAlignEnum m_textAlign;
  protected Integer m_defaultFontSize;
  protected Integer m_borderWidth;
  protected String m_borderColor;
  protected String m_backgroundColor;
  protected BoundsEnt m_bounds;
  
  protected DefaultWorkflowAnnotationEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "WorkflowAnnotation";
  }
  
  private DefaultWorkflowAnnotationEnt(DefaultWorkflowAnnotationEntBuilder builder) {
    super();
    if(builder.m_text == null) {
        throw new IllegalArgumentException("text must not be null.");
    }
    m_text = immutable(builder.m_text);
    m_textAlign = immutable(builder.m_textAlign);
    m_defaultFontSize = immutable(builder.m_defaultFontSize);
    m_borderWidth = immutable(builder.m_borderWidth);
    m_borderColor = immutable(builder.m_borderColor);
    m_backgroundColor = immutable(builder.m_backgroundColor);
    m_bounds = immutable(builder.m_bounds);
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
        DefaultWorkflowAnnotationEnt ent = (DefaultWorkflowAnnotationEnt)o;
        return Objects.equals(m_text, ent.m_text) && Objects.equals(m_textAlign, ent.m_textAlign) && Objects.equals(m_defaultFontSize, ent.m_defaultFontSize) && Objects.equals(m_borderWidth, ent.m_borderWidth) && Objects.equals(m_borderColor, ent.m_borderColor) && Objects.equals(m_backgroundColor, ent.m_backgroundColor) && Objects.equals(m_bounds, ent.m_bounds);
    }


  
   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
       return new HashCodeBuilder()
               .append(m_text)
               .append(m_textAlign)
               .append(m_defaultFontSize)
               .append(m_borderWidth)
               .append(m_borderColor)
               .append(m_backgroundColor)
               .append(m_bounds)
               .toHashCode();
   }
  
	
	
  @Override
  public TextAlignEnum getTextAlign() {
        return m_textAlign;
  }
    
  @Override
  public Integer getDefaultFontSize() {
        return m_defaultFontSize;
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
  public String getBackgroundColor() {
        return m_backgroundColor;
  }
    
  @Override
  public BoundsEnt getBounds() {
        return m_bounds;
  }
    
  
    public static class DefaultWorkflowAnnotationEntBuilder implements WorkflowAnnotationEntBuilder {
    
        public DefaultWorkflowAnnotationEntBuilder(){
            super();
        }
    
        private String m_text;
        private TextAlignEnum m_textAlign;
        private Integer m_defaultFontSize;
        private Integer m_borderWidth;
        private String m_borderColor;
        private String m_backgroundColor;
        private BoundsEnt m_bounds;

        @Override
        public DefaultWorkflowAnnotationEntBuilder setText(String text) {
             if(text == null) {
                 throw new IllegalArgumentException("text must not be null.");
             }
             m_text = text;
             return this;
        }

        @Override
        public DefaultWorkflowAnnotationEntBuilder setTextAlign(TextAlignEnum textAlign) {
             m_textAlign = textAlign;
             return this;
        }

        @Override
        public DefaultWorkflowAnnotationEntBuilder setDefaultFontSize(Integer defaultFontSize) {
             m_defaultFontSize = defaultFontSize;
             return this;
        }

        @Override
        public DefaultWorkflowAnnotationEntBuilder setBorderWidth(Integer borderWidth) {
             m_borderWidth = borderWidth;
             return this;
        }

        @Override
        public DefaultWorkflowAnnotationEntBuilder setBorderColor(String borderColor) {
             m_borderColor = borderColor;
             return this;
        }

        @Override
        public DefaultWorkflowAnnotationEntBuilder setBackgroundColor(String backgroundColor) {
             m_backgroundColor = backgroundColor;
             return this;
        }

        @Override
        public DefaultWorkflowAnnotationEntBuilder setBounds(BoundsEnt bounds) {
             m_bounds = bounds;
             return this;
        }

        
        @Override
        public DefaultWorkflowAnnotationEnt build() {
            return new DefaultWorkflowAnnotationEnt(this);
        }
    
    }

}
