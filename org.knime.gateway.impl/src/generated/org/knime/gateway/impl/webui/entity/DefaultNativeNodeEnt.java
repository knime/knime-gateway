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

import org.knime.gateway.api.webui.entity.NodeAnnotationEnt;
import org.knime.gateway.api.webui.entity.NodePortEnt;
import org.knime.gateway.api.webui.entity.XYEnt;
import org.knime.gateway.impl.webui.entity.DefaultNodeEnt;

import org.knime.gateway.api.webui.entity.NativeNodeEnt;

/**
 * Native node extension of a node.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public class DefaultNativeNodeEnt extends DefaultNodeEnt implements NativeNodeEnt {

  protected TypeEnum m_type;
  
  protected DefaultNativeNodeEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "NativeNode";
  }
  
  private DefaultNativeNodeEnt(DefaultNativeNodeEntBuilder builder) {
    super();
    if(builder.m_name == null) {
        throw new IllegalArgumentException("name must not be null.");
    }
    m_name = immutable(builder.m_name);
    if(builder.m_id == null) {
        throw new IllegalArgumentException("id must not be null.");
    }
    m_id = immutable(builder.m_id);
    m_inPorts = immutable(builder.m_inPorts);
    m_outPorts = immutable(builder.m_outPorts);
    m_annotation = immutable(builder.m_annotation);
    if(builder.m_position == null) {
        throw new IllegalArgumentException("position must not be null.");
    }
    m_position = immutable(builder.m_position);
    if(builder.m_propertyClass == null) {
        throw new IllegalArgumentException("propertyClass must not be null.");
    }
    m_propertyClass = immutable(builder.m_propertyClass);
    if(builder.m_type == null) {
        throw new IllegalArgumentException("type must not be null.");
    }
    m_type = immutable(builder.m_type);
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
        DefaultNativeNodeEnt ent = (DefaultNativeNodeEnt)o;
        return Objects.equals(m_name, ent.m_name) && Objects.equals(m_id, ent.m_id) && Objects.equals(m_inPorts, ent.m_inPorts) && Objects.equals(m_outPorts, ent.m_outPorts) && Objects.equals(m_annotation, ent.m_annotation) && Objects.equals(m_position, ent.m_position) && Objects.equals(m_propertyClass, ent.m_propertyClass) && Objects.equals(m_type, ent.m_type);
    }


  @Override
  public TypeEnum getType() {
        return m_type;
    }
    
  
    public static class DefaultNativeNodeEntBuilder implements NativeNodeEntBuilder {
    
        public DefaultNativeNodeEntBuilder(){
            super();
        }
    
        private String m_name;
        private org.knime.gateway.api.entity.NodeIDEnt m_id;
        private java.util.List<NodePortEnt> m_inPorts = new java.util.ArrayList<>();
        private java.util.List<NodePortEnt> m_outPorts = new java.util.ArrayList<>();
        private NodeAnnotationEnt m_annotation;
        private XYEnt m_position;
        private PropertyClassEnum m_propertyClass;
        private TypeEnum m_type;

        @Override
        public DefaultNativeNodeEntBuilder setName(String name) {
             if(name == null) {
                 throw new IllegalArgumentException("name must not be null.");
             }
             m_name = name;
             return this;
        }

        @Override
        public DefaultNativeNodeEntBuilder setId(org.knime.gateway.api.entity.NodeIDEnt id) {
             if(id == null) {
                 throw new IllegalArgumentException("id must not be null.");
             }
             m_id = id;
             return this;
        }

        @Override
        public DefaultNativeNodeEntBuilder setInPorts(java.util.List<NodePortEnt> inPorts) {
             m_inPorts = inPorts;
             return this;
        }

        @Override
        public DefaultNativeNodeEntBuilder setOutPorts(java.util.List<NodePortEnt> outPorts) {
             m_outPorts = outPorts;
             return this;
        }

        @Override
        public DefaultNativeNodeEntBuilder setAnnotation(NodeAnnotationEnt annotation) {
             m_annotation = annotation;
             return this;
        }

        @Override
        public DefaultNativeNodeEntBuilder setPosition(XYEnt position) {
             if(position == null) {
                 throw new IllegalArgumentException("position must not be null.");
             }
             m_position = position;
             return this;
        }

        @Override
        public DefaultNativeNodeEntBuilder setPropertyClass(PropertyClassEnum propertyClass) {
             if(propertyClass == null) {
                 throw new IllegalArgumentException("propertyClass must not be null.");
             }
             m_propertyClass = propertyClass;
             return this;
        }

        @Override
        public DefaultNativeNodeEntBuilder setType(TypeEnum type) {
             if(type == null) {
                 throw new IllegalArgumentException("type must not be null.");
             }
             m_type = type;
             return this;
        }

        
        @Override
        public DefaultNativeNodeEnt build() {
            return new DefaultNativeNodeEnt(this);
        }
    
    }

}
