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


import com.knime.gateway.v0.entity.NodeStateEnt;

/**
 * DefaultNodeStateEnt
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public class DefaultNodeStateEnt  implements NodeStateEnt {

  protected StateEnum m_state;
  
  protected DefaultNodeStateEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "NodeState";
  }
  
  private DefaultNodeStateEnt(DefaultNodeStateEntBuilder builder) {
    
    if(builder.m_state == null) {
        throw new IllegalArgumentException("state must not be null.");
    }
    m_state = immutable(builder.m_state);
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
        DefaultNodeStateEnt ent = (DefaultNodeStateEnt)o;
        return Objects.equals(m_state, ent.m_state);
    }


  @Override
  public StateEnum getState() {
        return m_state;
    }
    
  
    public static class DefaultNodeStateEntBuilder implements NodeStateEntBuilder {
    
        public DefaultNodeStateEntBuilder(){
            
        }
    
        private StateEnum m_state = null;

        @Override
        public DefaultNodeStateEntBuilder setState(StateEnum state) {
             if(state == null) {
                 throw new IllegalArgumentException("state must not be null.");
             }
             m_state = state;
             return this;
        }

        
        @Override
        public DefaultNodeStateEnt build() {
            return new DefaultNodeStateEnt(this);
        }
    
    }

}
