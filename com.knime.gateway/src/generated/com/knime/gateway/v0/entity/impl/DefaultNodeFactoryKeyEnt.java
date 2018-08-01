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

/**
 * Object to identify a node-specific node implementation.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public class DefaultNodeFactoryKeyEnt  implements NodeFactoryKeyEnt {

  protected String m_className;
  protected String m_settings;
  
  protected DefaultNodeFactoryKeyEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "NodeFactoryKey";
  }
  
  private DefaultNodeFactoryKeyEnt(DefaultNodeFactoryKeyEntBuilder builder) {
    
    if(builder.m_className == null) {
        throw new IllegalArgumentException("className must not be null.");
    }
    m_className = immutable(builder.m_className);
    m_settings = immutable(builder.m_settings);
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
        DefaultNodeFactoryKeyEnt ent = (DefaultNodeFactoryKeyEnt)o;
        return Objects.equals(m_className, ent.m_className) && Objects.equals(m_settings, ent.m_settings);
    }


  @Override
  public String getClassName() {
        return m_className;
    }
    
  @Override
  public String getSettings() {
        return m_settings;
    }
    
  
    public static class DefaultNodeFactoryKeyEntBuilder implements NodeFactoryKeyEntBuilder {
    
        public DefaultNodeFactoryKeyEntBuilder(){
            
        }
    
        private String m_className = null;
        private String m_settings = null;

        @Override
        public DefaultNodeFactoryKeyEntBuilder setClassName(String className) {
             if(className == null) {
                 throw new IllegalArgumentException("className must not be null.");
             }
             m_className = className;
             return this;
        }

        @Override
        public DefaultNodeFactoryKeyEntBuilder setSettings(String settings) {
             m_settings = settings;
             return this;
        }

        
        @Override
        public DefaultNodeFactoryKeyEnt build() {
            return new DefaultNodeFactoryKeyEnt(this);
        }
    
    }

}
