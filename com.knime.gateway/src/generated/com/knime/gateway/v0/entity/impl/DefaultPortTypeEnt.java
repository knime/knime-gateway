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


import com.knime.gateway.v0.entity.PortTypeEnt;

/**
 * The type of a port.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public class DefaultPortTypeEnt  implements PortTypeEnt {

  protected String m_portObjectClassName;
  protected Boolean m_optional;
  
  protected DefaultPortTypeEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "PortType";
  }
  
  private DefaultPortTypeEnt(DefaultPortTypeEntBuilder builder) {
    
    if(builder.m_portObjectClassName == null) {
        throw new IllegalArgumentException("portObjectClassName must not be null.");
    }
    m_portObjectClassName = builder.m_portObjectClassName;
    if(builder.m_optional == null) {
        throw new IllegalArgumentException("optional must not be null.");
    }
    m_optional = builder.m_optional;
  }


  @Override
  public String getPortObjectClassName() {
        return m_portObjectClassName;
    }
    
  @Override
  public Boolean isOptional() {
        return m_optional;
    }
    
  
    public static class DefaultPortTypeEntBuilder implements PortTypeEntBuilder {
    
        public DefaultPortTypeEntBuilder(){
            
        }
    
        private String m_portObjectClassName = null;
        private Boolean m_optional = null;

        @Override
        public DefaultPortTypeEntBuilder setPortObjectClassName(String portObjectClassName) {
             if(portObjectClassName == null) {
                 throw new IllegalArgumentException("portObjectClassName must not be null.");
             }
             m_portObjectClassName = portObjectClassName;
             return this;
        }

        @Override
        public DefaultPortTypeEntBuilder setOptional(Boolean optional) {
             if(optional == null) {
                 throw new IllegalArgumentException("optional must not be null.");
             }
             m_optional = optional;
             return this;
        }

        
        @Override
        public DefaultPortTypeEnt build() {
            return new DefaultPortTypeEnt(this);
        }
    
    }

}
