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


import org.knime.gateway.api.webui.entity.AnnotationEnt;

/**
 * A text annotation.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public class DefaultAnnotationEnt  implements AnnotationEnt {

  protected String m_text;
  
  protected DefaultAnnotationEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "Annotation";
  }
  
  private DefaultAnnotationEnt(DefaultAnnotationEntBuilder builder) {
    
    if(builder.m_text == null) {
        throw new IllegalArgumentException("text must not be null.");
    }
    m_text = immutable(builder.m_text);
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
        return Objects.equals(m_text, ent.m_text);
    }


  @Override
  public String getText() {
        return m_text;
    }
    
  
    public static class DefaultAnnotationEntBuilder implements AnnotationEntBuilder {
    
        public DefaultAnnotationEntBuilder(){
            
        }
    
        private String m_text;

        @Override
        public DefaultAnnotationEntBuilder setText(String text) {
             if(text == null) {
                 throw new IllegalArgumentException("text must not be null.");
             }
             m_text = text;
             return this;
        }

        
        @Override
        public DefaultAnnotationEnt build() {
            return new DefaultAnnotationEnt(this);
        }
    
    }

}
