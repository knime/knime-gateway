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


import com.knime.gateway.v0.entity.ExecEnvEnt;

/**
 * Execution environment instance
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public class DefaultExecEnvEnt  implements ExecEnvEnt {

  protected String m_execEnvID;
  protected String m_instanceID;
  protected String m_typeName;
  protected java.util.List<String> m_allowedNodeTypes;
  
  protected DefaultExecEnvEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "ExecEnv";
  }
  
  private DefaultExecEnvEnt(DefaultExecEnvEntBuilder builder) {
    
    m_execEnvID = immutable(builder.m_execEnvID);
    m_instanceID = immutable(builder.m_instanceID);
    m_typeName = immutable(builder.m_typeName);
    m_allowedNodeTypes = immutable(builder.m_allowedNodeTypes);
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
        DefaultExecEnvEnt ent = (DefaultExecEnvEnt)o;
        return Objects.equals(m_execEnvID, ent.m_execEnvID) && Objects.equals(m_instanceID, ent.m_instanceID) && Objects.equals(m_typeName, ent.m_typeName) && Objects.equals(m_allowedNodeTypes, ent.m_allowedNodeTypes);
    }


  @Override
  public String getExecEnvID() {
        return m_execEnvID;
    }
    
  @Override
  public String getInstanceID() {
        return m_instanceID;
    }
    
  @Override
  public String getTypeName() {
        return m_typeName;
    }
    
  @Override
  public java.util.List<String> getAllowedNodeTypes() {
        return m_allowedNodeTypes;
    }
    
  
    public static class DefaultExecEnvEntBuilder implements ExecEnvEntBuilder {
    
        public DefaultExecEnvEntBuilder(){
            
        }
    
        private String m_execEnvID = null;
        private String m_instanceID = null;
        private String m_typeName = null;
        private java.util.List<String> m_allowedNodeTypes = new java.util.ArrayList<>();

        @Override
        public DefaultExecEnvEntBuilder setExecEnvID(String execEnvID) {
             m_execEnvID = execEnvID;
             return this;
        }

        @Override
        public DefaultExecEnvEntBuilder setInstanceID(String instanceID) {
             m_instanceID = instanceID;
             return this;
        }

        @Override
        public DefaultExecEnvEntBuilder setTypeName(String typeName) {
             m_typeName = typeName;
             return this;
        }

        @Override
        public DefaultExecEnvEntBuilder setAllowedNodeTypes(java.util.List<String> allowedNodeTypes) {
             m_allowedNodeTypes = allowedNodeTypes;
             return this;
        }

        
        @Override
        public DefaultExecEnvEnt build() {
            return new DefaultExecEnvEnt(this);
        }
    
    }

}
