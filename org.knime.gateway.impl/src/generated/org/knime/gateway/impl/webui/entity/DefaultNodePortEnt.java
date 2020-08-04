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
package org.knime.gateway.impl.webui.entity;

import static org.knime.gateway.api.util.EntityUtil.immutable;

import java.util.Objects;


import org.knime.gateway.api.webui.entity.NodePortEnt;

/**
 * A single port of a node.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public class DefaultNodePortEnt  implements NodePortEnt {

  protected Integer m_index;
  protected TypeEnum m_type;
  protected String m_color;
  protected java.util.List<org.knime.gateway.api.entity.ConnectionIDEnt> m_connectedVia;
  protected Boolean m_optional;
  protected Boolean m_inactive;
  
  protected DefaultNodePortEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "NodePort";
  }
  
  private DefaultNodePortEnt(DefaultNodePortEntBuilder builder) {
    
    if(builder.m_index == null) {
        throw new IllegalArgumentException("index must not be null.");
    }
    m_index = immutable(builder.m_index);
    if(builder.m_type == null) {
        throw new IllegalArgumentException("type must not be null.");
    }
    m_type = immutable(builder.m_type);
    m_color = immutable(builder.m_color);
    m_connectedVia = immutable(builder.m_connectedVia);
    m_optional = immutable(builder.m_optional);
    m_inactive = immutable(builder.m_inactive);
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
        DefaultNodePortEnt ent = (DefaultNodePortEnt)o;
        return Objects.equals(m_index, ent.m_index) && Objects.equals(m_type, ent.m_type) && Objects.equals(m_color, ent.m_color) && Objects.equals(m_connectedVia, ent.m_connectedVia) && Objects.equals(m_optional, ent.m_optional) && Objects.equals(m_inactive, ent.m_inactive);
    }


  @Override
  public Integer getIndex() {
        return m_index;
    }
    
  @Override
  public TypeEnum getType() {
        return m_type;
    }
    
  @Override
  public String getColor() {
        return m_color;
    }
    
  @Override
  public java.util.List<org.knime.gateway.api.entity.ConnectionIDEnt> getConnectedVia() {
        return m_connectedVia;
    }
    
  @Override
  public Boolean isOptional() {
        return m_optional;
    }
    
  @Override
  public Boolean isInactive() {
        return m_inactive;
    }
    
  
    public static class DefaultNodePortEntBuilder implements NodePortEntBuilder {
    
        public DefaultNodePortEntBuilder(){
            
        }
    
        private Integer m_index;
        private TypeEnum m_type;
        private String m_color;
        private java.util.List<org.knime.gateway.api.entity.ConnectionIDEnt> m_connectedVia = new java.util.ArrayList<>();
        private Boolean m_optional;
        private Boolean m_inactive;

        @Override
        public DefaultNodePortEntBuilder setIndex(Integer index) {
             if(index == null) {
                 throw new IllegalArgumentException("index must not be null.");
             }
             m_index = index;
             return this;
        }

        @Override
        public DefaultNodePortEntBuilder setType(TypeEnum type) {
             if(type == null) {
                 throw new IllegalArgumentException("type must not be null.");
             }
             m_type = type;
             return this;
        }

        @Override
        public DefaultNodePortEntBuilder setColor(String color) {
             m_color = color;
             return this;
        }

        @Override
        public DefaultNodePortEntBuilder setConnectedVia(java.util.List<org.knime.gateway.api.entity.ConnectionIDEnt> connectedVia) {
             m_connectedVia = connectedVia;
             return this;
        }

        @Override
        public DefaultNodePortEntBuilder setOptional(Boolean optional) {
             m_optional = optional;
             return this;
        }

        @Override
        public DefaultNodePortEntBuilder setInactive(Boolean inactive) {
             m_inactive = inactive;
             return this;
        }

        
        @Override
        public DefaultNodePortEnt build() {
            return new DefaultNodePortEnt(this);
        }
    
    }

}
