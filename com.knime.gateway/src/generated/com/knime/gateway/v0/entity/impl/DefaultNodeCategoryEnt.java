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

import com.knime.gateway.v0.entity.NodeCategoryEnt;
import com.knime.gateway.v0.entity.NodeFactoryKeyEnt;

import com.knime.gateway.v0.entity.NodeCategoryEnt;

/**
 * a category in the node repository
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public class DefaultNodeCategoryEnt  implements NodeCategoryEnt {

  protected String m_name;
  protected java.util.List<NodeCategoryEnt> m_categoryChildren;
  protected java.util.List<NodeFactoryKeyEnt> m_nodeFactoryChildren;
  
  protected DefaultNodeCategoryEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "NodeCategory";
  }
  
  private DefaultNodeCategoryEnt(DefaultNodeCategoryEntBuilder builder) {
    
    m_name = immutable(builder.m_name);
    m_categoryChildren = immutable(builder.m_categoryChildren);
    m_nodeFactoryChildren = immutable(builder.m_nodeFactoryChildren);
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
        DefaultNodeCategoryEnt ent = (DefaultNodeCategoryEnt)o;
        return Objects.equals(m_name, ent.m_name) && Objects.equals(m_categoryChildren, ent.m_categoryChildren) && Objects.equals(m_nodeFactoryChildren, ent.m_nodeFactoryChildren);
    }


  @Override
  public String getName() {
        return m_name;
    }
    
  @Override
  public java.util.List<NodeCategoryEnt> getCategoryChildren() {
        return m_categoryChildren;
    }
    
  @Override
  public java.util.List<NodeFactoryKeyEnt> getNodeFactoryChildren() {
        return m_nodeFactoryChildren;
    }
    
  
    public static class DefaultNodeCategoryEntBuilder implements NodeCategoryEntBuilder {
    
        public DefaultNodeCategoryEntBuilder(){
            
        }
    
        private String m_name = null;
        private java.util.List<NodeCategoryEnt> m_categoryChildren = new java.util.ArrayList<>();
        private java.util.List<NodeFactoryKeyEnt> m_nodeFactoryChildren = new java.util.ArrayList<>();

        @Override
        public DefaultNodeCategoryEntBuilder setName(String name) {
             m_name = name;
             return this;
        }

        @Override
        public DefaultNodeCategoryEntBuilder setCategoryChildren(java.util.List<NodeCategoryEnt> categoryChildren) {
             m_categoryChildren = categoryChildren;
             return this;
        }

        @Override
        public DefaultNodeCategoryEntBuilder setNodeFactoryChildren(java.util.List<NodeFactoryKeyEnt> nodeFactoryChildren) {
             m_nodeFactoryChildren = nodeFactoryChildren;
             return this;
        }

        
        @Override
        public DefaultNodeCategoryEnt build() {
            return new DefaultNodeCategoryEnt(this);
        }
    
    }

}
