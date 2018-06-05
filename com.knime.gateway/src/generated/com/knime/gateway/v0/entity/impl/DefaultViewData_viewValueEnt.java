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

import static com.knime.gateway.util.DefaultEntUtil.immutable;

import java.util.Objects;


import com.knime.gateway.v0.entity.ViewData_viewValueEnt;

/**
 * The views value.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public class DefaultViewData_viewValueEnt  implements ViewData_viewValueEnt {

  protected String m_classname;
  protected String m_content;
  
  protected DefaultViewData_viewValueEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "ViewData_viewValue";
  }
  
  private DefaultViewData_viewValueEnt(DefaultViewData_viewValueEntBuilder builder) {
    
    m_classname = immutable(builder.m_classname);
    m_content = immutable(builder.m_content);
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
        DefaultViewData_viewValueEnt ent = (DefaultViewData_viewValueEnt)o;
        return Objects.equals(m_classname, ent.m_classname) && Objects.equals(m_content, ent.m_content);
    }


  @Override
  public String getClassname() {
        return m_classname;
    }
    
  @Override
  public String getContent() {
        return m_content;
    }
    
  
    public static class DefaultViewData_viewValueEntBuilder implements ViewData_viewValueEntBuilder {
    
        public DefaultViewData_viewValueEntBuilder(){
            
        }
    
        private String m_classname = null;
        private String m_content = null;

        @Override
        public DefaultViewData_viewValueEntBuilder setClassname(String classname) {
             m_classname = classname;
             return this;
        }

        @Override
        public DefaultViewData_viewValueEntBuilder setContent(String content) {
             m_content = content;
             return this;
        }

        
        @Override
        public DefaultViewData_viewValueEnt build() {
            return new DefaultViewData_viewValueEnt(this);
        }
    
    }

}
