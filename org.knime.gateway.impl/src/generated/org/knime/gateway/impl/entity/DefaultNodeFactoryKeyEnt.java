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
package org.knime.gateway.impl.entity;

import static org.knime.gateway.api.util.EntityUtil.immutable;

import java.util.Objects;


import org.knime.gateway.api.entity.NodeFactoryKeyEnt;

/**
 * Object to identify a node-specific node implementation.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/org.knime.gateway.impl-config.json"})
public class DefaultNodeFactoryKeyEnt  implements NodeFactoryKeyEnt {

  protected String m_className;
  protected String m_settings;
  protected String m_nodeCreationConfigSettings;
  
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
    m_nodeCreationConfigSettings = immutable(builder.m_nodeCreationConfigSettings);
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
        return Objects.equals(m_className, ent.m_className) && Objects.equals(m_settings, ent.m_settings) && Objects.equals(m_nodeCreationConfigSettings, ent.m_nodeCreationConfigSettings);
    }


  @Override
  public String getClassName() {
        return m_className;
    }
    
  @Override
  public String getSettings() {
        return m_settings;
    }
    
  @Override
  public String getNodeCreationConfigSettings() {
        return m_nodeCreationConfigSettings;
    }
    
  
    public static class DefaultNodeFactoryKeyEntBuilder implements NodeFactoryKeyEntBuilder {
    
        public DefaultNodeFactoryKeyEntBuilder(){
            
        }
    
        private String m_className;
        private String m_settings;
        private String m_nodeCreationConfigSettings;

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
        public DefaultNodeFactoryKeyEntBuilder setNodeCreationConfigSettings(String nodeCreationConfigSettings) {
             m_nodeCreationConfigSettings = nodeCreationConfigSettings;
             return this;
        }

        
        @Override
        public DefaultNodeFactoryKeyEnt build() {
            return new DefaultNodeFactoryKeyEnt(this);
        }
    
    }

}
