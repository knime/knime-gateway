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
  
  private DefaultNodeMessageEnt(DefaultNodeMessageEntBuilder builder) {
    
    m_type = builder.m_type;
    if(builder.m_message == null) {
        throw new IllegalArgumentException("message must not be null.");
    }
    m_message = builder.m_message;
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
