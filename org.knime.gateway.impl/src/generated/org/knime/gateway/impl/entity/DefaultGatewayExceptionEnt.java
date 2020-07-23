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


import org.knime.gateway.api.entity.GatewayExceptionEnt;

/**
 * Details of an exception thrown by gateway implementations such as services.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/org.knime.gateway.impl-config.json"})
public class DefaultGatewayExceptionEnt  implements GatewayExceptionEnt {

  protected String m_exceptionName;
  protected String m_exceptionMessage;
  
  protected DefaultGatewayExceptionEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "GatewayException";
  }
  
  private DefaultGatewayExceptionEnt(DefaultGatewayExceptionEntBuilder builder) {
    
    m_exceptionName = immutable(builder.m_exceptionName);
    m_exceptionMessage = immutable(builder.m_exceptionMessage);
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
        DefaultGatewayExceptionEnt ent = (DefaultGatewayExceptionEnt)o;
        return Objects.equals(m_exceptionName, ent.m_exceptionName) && Objects.equals(m_exceptionMessage, ent.m_exceptionMessage);
    }


  @Override
  public String getExceptionName() {
        return m_exceptionName;
    }
    
  @Override
  public String getExceptionMessage() {
        return m_exceptionMessage;
    }
    
  
    public static class DefaultGatewayExceptionEntBuilder implements GatewayExceptionEntBuilder {
    
        public DefaultGatewayExceptionEntBuilder(){
            
        }
    
        private String m_exceptionName;
        private String m_exceptionMessage;

        @Override
        public DefaultGatewayExceptionEntBuilder setExceptionName(String exceptionName) {
             m_exceptionName = exceptionName;
             return this;
        }

        @Override
        public DefaultGatewayExceptionEntBuilder setExceptionMessage(String exceptionMessage) {
             m_exceptionMessage = exceptionMessage;
             return this;
        }

        
        @Override
        public DefaultGatewayExceptionEnt build() {
            return new DefaultGatewayExceptionEnt(this);
        }
    
    }

}
