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
package com.knime.gateway.impl.entity;

import static org.knime.gateway.api.util.EntityUtil.immutable;

import java.util.Objects;


import org.knime.gateway.api.entity.PatchOpEnt;

/**
 * A JSONPatch document as defined by RFC 6902
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/java-ui/configs/com.knime.gateway.impl-config.json"})
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
        DefaultPatchOpEnt ent = (DefaultPatchOpEnt)o;
        return Objects.equals(m_op, ent.m_op) && Objects.equals(m_path, ent.m_path) && Objects.equals(m_value, ent.m_value) && Objects.equals(m_from, ent.m_from);
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
    
        private OpEnum m_op;
        private String m_path;
        private Object m_value = null;
        private String m_from;

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
