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


import com.knime.gateway.v0.entity.FlowVariableEnt;

/**
 * Local variable of basic type which is passed along connections in a workflow.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public class DefaultFlowVariableEnt  implements FlowVariableEnt {

  protected String m_name;
  protected TypeEnum m_type;
  protected String m_value;
  
  protected DefaultFlowVariableEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "FlowVariable";
  }
  
  private DefaultFlowVariableEnt(DefaultFlowVariableEntBuilder builder) {
    
    m_name = immutable(builder.m_name);
    m_type = immutable(builder.m_type);
    m_value = immutable(builder.m_value);
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
        DefaultFlowVariableEnt ent = (DefaultFlowVariableEnt)o;
        return Objects.equals(m_name, ent.m_name) && Objects.equals(m_type, ent.m_type) && Objects.equals(m_value, ent.m_value);
    }


  @Override
  public String getName() {
        return m_name;
    }
    
  @Override
  public TypeEnum getType() {
        return m_type;
    }
    
  @Override
  public String getValue() {
        return m_value;
    }
    
  
    public static class DefaultFlowVariableEntBuilder implements FlowVariableEntBuilder {
    
        public DefaultFlowVariableEntBuilder(){
            
        }
    
        private String m_name = null;
        private TypeEnum m_type = null;
        private String m_value = null;

        @Override
        public DefaultFlowVariableEntBuilder setName(String name) {
             m_name = name;
             return this;
        }

        @Override
        public DefaultFlowVariableEntBuilder setType(TypeEnum type) {
             m_type = type;
             return this;
        }

        @Override
        public DefaultFlowVariableEntBuilder setValue(String value) {
             m_value = value;
             return this;
        }

        
        @Override
        public DefaultFlowVariableEnt build() {
            return new DefaultFlowVariableEnt(this);
        }
    
    }

}
