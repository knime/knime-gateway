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
package com.knime.gateway.entity.impl;

import static com.knime.gateway.util.EntityUtil.immutable;

import java.util.Objects;


import com.knime.gateway.entity.WizardPageInputEnt;

/**
 * Input data required to execute one wizard page of a workflow.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"org.knime.gateway.codegen.GatewayCodegen", "src-gen/com.knime.gateway-implementations-config.json"})
public class DefaultWizardPageInputEnt  implements WizardPageInputEnt {

  protected java.util.Map<String, String> m_viewValues;
  
  protected DefaultWizardPageInputEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "WizardPageInput";
  }
  
  private DefaultWizardPageInputEnt(DefaultWizardPageInputEntBuilder builder) {
    
    m_viewValues = immutable(builder.m_viewValues);
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
        DefaultWizardPageInputEnt ent = (DefaultWizardPageInputEnt)o;
        return Objects.equals(m_viewValues, ent.m_viewValues);
    }


  @Override
  public java.util.Map<String, String> getViewValues() {
        return m_viewValues;
    }
    
  
    public static class DefaultWizardPageInputEntBuilder implements WizardPageInputEntBuilder {
    
        public DefaultWizardPageInputEntBuilder(){
            
        }
    
        private java.util.Map<String, String> m_viewValues = new java.util.HashMap<>();

        @Override
        public DefaultWizardPageInputEntBuilder setViewValues(java.util.Map<String, String> viewValues) {
             m_viewValues = viewValues;
             return this;
        }

        
        @Override
        public DefaultWizardPageInputEnt build() {
            return new DefaultWizardPageInputEnt(this);
        }
    
    }

}
