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

import static com.knime.gateway.util.DefaultEntUtil.immutable;


import com.knime.gateway.v0.entity.PatchOpEnt;

/**
 * A JSONPatch document as defined by RFC 6902
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public class DefaultPatchOpEnt  implements PatchOpEnt {

  protected OpEnum m_op;
  protected String m_path;
  protected Object m_value;
  protected String m_from;
  
  protected DefaultPatchOpEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "PatchOp";
  }
  
  private DefaultPatchOpEnt(DefaultPatchOpEntBuilder builder) {
    
    if(builder.m_op == null) {
        throw new IllegalArgumentException("op must not be null.");
    }
    m_op = immutable(builder.m_op);
    if(builder.m_path == null) {
        throw new IllegalArgumentException("path must not be null.");
    }
    m_path = immutable(builder.m_path);
    m_value = immutable(builder.m_value);
    m_from = immutable(builder.m_from);
  }


  @Override
  public OpEnum getOp() {
        return m_op;
    }
    
  @Override
  public String getPath() {
        return m_path;
    }
    
  @Override
  public Object getValue() {
        return m_value;
    }
    
  @Override
  public String getFrom() {
        return m_from;
    }
    
  
    public static class DefaultPatchOpEntBuilder implements PatchOpEntBuilder {
    
        public DefaultPatchOpEntBuilder(){
            
        }
    
        private OpEnum m_op = null;
        private String m_path = null;
        private Object m_value = null;
        private String m_from = null;

        @Override
        public DefaultPatchOpEntBuilder setOp(OpEnum op) {
             if(op == null) {
                 throw new IllegalArgumentException("op must not be null.");
             }
             m_op = op;
             return this;
        }

        @Override
        public DefaultPatchOpEntBuilder setPath(String path) {
             if(path == null) {
                 throw new IllegalArgumentException("path must not be null.");
             }
             m_path = path;
             return this;
        }

        @Override
        public DefaultPatchOpEntBuilder setValue(Object value) {
             m_value = value;
             return this;
        }

        @Override
        public DefaultPatchOpEntBuilder setFrom(String from) {
             m_from = from;
             return this;
        }

        
        @Override
        public DefaultPatchOpEnt build() {
            return new DefaultPatchOpEnt(this);
        }
    
    }

}
