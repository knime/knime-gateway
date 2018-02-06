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


import com.knime.gateway.v0.entity.JobManagerEnt;

/**
 * node&#39;s job manager
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public class DefaultJobManagerEnt  implements JobManagerEnt {

  protected String m_id;
  
  protected DefaultJobManagerEnt() {
    //for sub-classes
  }
  
  private DefaultJobManagerEnt(DefaultJobManagerEntBuilder builder) {
    
    if(builder.m_id == null) {
        throw new IllegalArgumentException("id must not be null.");
    }
    m_id = builder.m_id;
  }


  @Override
  public String getId() {
        return m_id;
    }
    
  
    public static class DefaultJobManagerEntBuilder implements JobManagerEntBuilder {
    
        public DefaultJobManagerEntBuilder(){
            
        }
    
        private String m_id = null;

        @Override
        public DefaultJobManagerEntBuilder setId(String id) {
             if(id == null) {
                 throw new IllegalArgumentException("id must not be null.");
             }
             m_id = id;
             return this;
        }

        
        @Override
        public DefaultJobManagerEnt build() {
            return new DefaultJobManagerEnt(this);
        }
    
    }

}
