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


import com.knime.gateway.v0.entity.NodeMessageEnt;

/**
 * node message
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public class DefaultNodeMessageEnt  implements NodeMessageEnt {

  protected String m_type;
  protected String m_message;
  
  protected DefaultNodeMessageEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "NodeMessage";
  }
  
  private DefaultNodeMessageEnt(DefaultNodeMessageEntBuilder builder) {
    
    m_type = immutable(builder.m_type);
    if(builder.m_message == null) {
        throw new IllegalArgumentException("message must not be null.");
    }
    m_message = immutable(builder.m_message);
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
        DefaultNodeMessageEnt ent = (DefaultNodeMessageEnt)o;
        return Objects.equals(m_type, ent.m_type) && Objects.equals(m_message, ent.m_message);
    }


  @Override
  public String getType() {
        return m_type;
    }
    
  @Override
  public String getMessage() {
        return m_message;
    }
    
  
    public static class DefaultNodeMessageEntBuilder implements NodeMessageEntBuilder {
    
        public DefaultNodeMessageEntBuilder(){
            
        }
    
        private String m_type = null;
        private String m_message = null;

        @Override
        public DefaultNodeMessageEntBuilder setType(String type) {
             m_type = type;
             return this;
        }

        @Override
        public DefaultNodeMessageEntBuilder setMessage(String message) {
             if(message == null) {
                 throw new IllegalArgumentException("message must not be null.");
             }
             m_message = message;
             return this;
        }

        
        @Override
        public DefaultNodeMessageEnt build() {
            return new DefaultNodeMessageEnt(this);
        }
    
    }

}
