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

import com.knime.gateway.v0.entity.ViewContentEnt;

import com.knime.gateway.v0.entity.ViewDataEnt;

/**
 * The data for a node&#39;s view encompasing the view representation and value.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public class DefaultViewDataEnt  implements ViewDataEnt {

  protected String m_javascriptObjectID;
  protected ViewContentEnt m_viewRepresentation;
  protected ViewContentEnt m_viewValue;
  protected Boolean m_hideInWizard;
  
  protected DefaultViewDataEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "ViewData";
  }
  
  private DefaultViewDataEnt(DefaultViewDataEntBuilder builder) {
    
    m_javascriptObjectID = immutable(builder.m_javascriptObjectID);
    m_viewRepresentation = immutable(builder.m_viewRepresentation);
    m_viewValue = immutable(builder.m_viewValue);
    m_hideInWizard = immutable(builder.m_hideInWizard);
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
        DefaultViewDataEnt ent = (DefaultViewDataEnt)o;
        return Objects.equals(m_javascriptObjectID, ent.m_javascriptObjectID) && Objects.equals(m_viewRepresentation, ent.m_viewRepresentation) && Objects.equals(m_viewValue, ent.m_viewValue) && Objects.equals(m_hideInWizard, ent.m_hideInWizard);
    }


  @Override
  public String getJavascriptObjectID() {
        return m_javascriptObjectID;
    }
    
  @Override
  public ViewContentEnt getViewRepresentation() {
        return m_viewRepresentation;
    }
    
  @Override
  public ViewContentEnt getViewValue() {
        return m_viewValue;
    }
    
  @Override
  public Boolean isHideInWizard() {
        return m_hideInWizard;
    }
    
  
    public static class DefaultViewDataEntBuilder implements ViewDataEntBuilder {
    
        public DefaultViewDataEntBuilder(){
            
        }
    
        private String m_javascriptObjectID = null;
        private ViewContentEnt m_viewRepresentation;
        private ViewContentEnt m_viewValue;
        private Boolean m_hideInWizard = null;

        @Override
        public DefaultViewDataEntBuilder setJavascriptObjectID(String javascriptObjectID) {
             m_javascriptObjectID = javascriptObjectID;
             return this;
        }

        @Override
        public DefaultViewDataEntBuilder setViewRepresentation(ViewContentEnt viewRepresentation) {
             m_viewRepresentation = viewRepresentation;
             return this;
        }

        @Override
        public DefaultViewDataEntBuilder setViewValue(ViewContentEnt viewValue) {
             m_viewValue = viewValue;
             return this;
        }

        @Override
        public DefaultViewDataEntBuilder setHideInWizard(Boolean hideInWizard) {
             m_hideInWizard = hideInWizard;
             return this;
        }

        
        @Override
        public DefaultViewDataEnt build() {
            return new DefaultViewDataEnt(this);
        }
    
    }

}
