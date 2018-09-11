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

import com.knime.gateway.v0.entity.NodeFactoryKeyEnt;

import com.knime.gateway.v0.entity.NodeTemplateEnt;

/**
 * DefaultNodeTemplateEnt
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public class DefaultNodeTemplateEnt  implements NodeTemplateEnt {

  protected String m_name;
  protected String m_execEnvNodeType;
  protected NodeFactoryKeyEnt m_nodeFactory;
  
  protected DefaultNodeTemplateEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "NodeTemplate";
  }
  
  private DefaultNodeTemplateEnt(DefaultNodeTemplateEntBuilder builder) {
    
    m_name = immutable(builder.m_name);
    m_execEnvNodeType = immutable(builder.m_execEnvNodeType);
    m_nodeFactory = immutable(builder.m_nodeFactory);
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
        DefaultNodeTemplateEnt ent = (DefaultNodeTemplateEnt)o;
        return Objects.equals(m_name, ent.m_name) && Objects.equals(m_execEnvNodeType, ent.m_execEnvNodeType) && Objects.equals(m_nodeFactory, ent.m_nodeFactory);
    }


  @Override
  public String getName() {
        return m_name;
    }
    
  @Override
  public String getExecEnvNodeType() {
        return m_execEnvNodeType;
    }
    
  @Override
  public NodeFactoryKeyEnt getNodeFactory() {
        return m_nodeFactory;
    }
    
  
    public static class DefaultNodeTemplateEntBuilder implements NodeTemplateEntBuilder {
    
        public DefaultNodeTemplateEntBuilder(){
            
        }
    
        private String m_name = null;
        private String m_execEnvNodeType = null;
        private NodeFactoryKeyEnt m_nodeFactory;

        @Override
        public DefaultNodeTemplateEntBuilder setName(String name) {
             m_name = name;
             return this;
        }

        @Override
        public DefaultNodeTemplateEntBuilder setExecEnvNodeType(String execEnvNodeType) {
             m_execEnvNodeType = execEnvNodeType;
             return this;
        }

        @Override
        public DefaultNodeTemplateEntBuilder setNodeFactory(NodeFactoryKeyEnt nodeFactory) {
             m_nodeFactory = nodeFactory;
             return this;
        }

        
        @Override
        public DefaultNodeTemplateEnt build() {
            return new DefaultNodeTemplateEnt(this);
        }
    
    }

}
