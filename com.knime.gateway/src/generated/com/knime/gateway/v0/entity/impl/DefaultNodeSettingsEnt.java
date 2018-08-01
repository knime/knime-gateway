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


import com.knime.gateway.v0.entity.NodeSettingsEnt;

/**
 * Settings of a node.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public class DefaultNodeSettingsEnt  implements NodeSettingsEnt {

  protected String m_jsonContent;
  
  protected DefaultNodeSettingsEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "NodeSettings";
  }
  
  private DefaultNodeSettingsEnt(DefaultNodeSettingsEntBuilder builder) {
    
    m_jsonContent = immutable(builder.m_jsonContent);
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
        DefaultNodeSettingsEnt ent = (DefaultNodeSettingsEnt)o;
        return Objects.equals(m_jsonContent, ent.m_jsonContent);
    }


  @Override
  public String getJsonContent() {
        return m_jsonContent;
    }
    
  
    public static class DefaultNodeSettingsEntBuilder implements NodeSettingsEntBuilder {
    
        public DefaultNodeSettingsEntBuilder(){
            
        }
    
        private String m_jsonContent = null;

        @Override
        public DefaultNodeSettingsEntBuilder setJsonContent(String jsonContent) {
             m_jsonContent = jsonContent;
             return this;
        }

        
        @Override
        public DefaultNodeSettingsEnt build() {
            return new DefaultNodeSettingsEnt(this);
        }
    
    }

}
