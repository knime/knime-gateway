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

import static com.knime.gateway.util.EntityUtil.immutable;

import java.util.Objects;

import com.knime.gateway.v0.entity.MetaNodeDialogCompEnt;

import com.knime.gateway.v0.entity.MetaNodeDialogEnt;

/**
 * A representation of a metanode dialog.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public class DefaultMetaNodeDialogEnt  implements MetaNodeDialogEnt {

  protected java.util.List<MetaNodeDialogCompEnt> m_components;
  
  protected DefaultMetaNodeDialogEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "MetaNodeDialog";
  }
  
  private DefaultMetaNodeDialogEnt(DefaultMetaNodeDialogEntBuilder builder) {
    
    m_components = immutable(builder.m_components);
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
        DefaultMetaNodeDialogEnt ent = (DefaultMetaNodeDialogEnt)o;
        return Objects.equals(m_components, ent.m_components);
    }


  @Override
  public java.util.List<MetaNodeDialogCompEnt> getComponents() {
        return m_components;
    }
    
  
    public static class DefaultMetaNodeDialogEntBuilder implements MetaNodeDialogEntBuilder {
    
        public DefaultMetaNodeDialogEntBuilder(){
            
        }
    
        private java.util.List<MetaNodeDialogCompEnt> m_components = new java.util.ArrayList<>();

        @Override
        public DefaultMetaNodeDialogEntBuilder setComponents(java.util.List<MetaNodeDialogCompEnt> components) {
             m_components = components;
             return this;
        }

        
        @Override
        public DefaultMetaNodeDialogEnt build() {
            return new DefaultMetaNodeDialogEnt(this);
        }
    
    }

}
