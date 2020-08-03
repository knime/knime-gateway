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
import org.knime.gateway.api.webui.entity.NodeInPortEnt;
import org.knime.gateway.api.webui.entity.NodeOutPortEnt;
import org.knime.gateway.api.webui.entity.NodeStateEnt;
import org.knime.gateway.api.webui.entity.XYEnt;

import org.knime.gateway.api.webui.entity.NodeEnt;

/**
 * A node.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public class DefaultNodeEnt  implements NodeEnt {

  protected String m_objectType;
  protected String m_name;
  protected org.knime.gateway.api.entity.NodeIDEnt m_id;
  protected NodeStateEnt m_state;
  protected java.util.List<NodeInPortEnt> m_inPorts;
  protected java.util.List<NodeOutPortEnt> m_outPorts;
  protected NodeAnnotationEnt m_annotation;
  protected XYEnt m_position;
  
  protected DefaultNodeEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "Node";
  }
  
  private DefaultNodeEnt(DefaultNodeEntBuilder builder) {
    
    if(builder.m_objectType == null) {
        throw new IllegalArgumentException("objectType must not be null.");
    }
    m_objectType = immutable(builder.m_objectType);
    if(builder.m_name == null) {
        throw new IllegalArgumentException("name must not be null.");
    }
    m_name = immutable(builder.m_name);
    if(builder.m_id == null) {
        throw new IllegalArgumentException("id must not be null.");
    }
    m_id = immutable(builder.m_id);
    if(builder.m_state == null) {
        throw new IllegalArgumentException("state must not be null.");
    }
    m_state = immutable(builder.m_state);
    m_inPorts = immutable(builder.m_inPorts);
    m_outPorts = immutable(builder.m_outPorts);
    m_annotation = immutable(builder.m_annotation);
    if(builder.m_position == null) {
        throw new IllegalArgumentException("position must not be null.");
    }
    m_position = immutable(builder.m_position);
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
        DefaultNodeEnt ent = (DefaultNodeEnt)o;
        return Objects.equals(m_objectType, ent.m_objectType) && Objects.equals(m_name, ent.m_name) && Objects.equals(m_id, ent.m_id) && Objects.equals(m_state, ent.m_state) && Objects.equals(m_inPorts, ent.m_inPorts) && Objects.equals(m_outPorts, ent.m_outPorts) && Objects.equals(m_annotation, ent.m_annotation) && Objects.equals(m_position, ent.m_position);
    }


  @Override
  public String getObjectType() {
        return m_objectType;
    }
    
  @Override
  public String getName() {
        return m_name;
    }
    
  @Override
  public org.knime.gateway.api.entity.NodeIDEnt getId() {
        return m_id;
    }
    
  @Override
  public NodeStateEnt getState() {
        return m_state;
    }
    
  @Override
  public java.util.List<NodeInPortEnt> getInPorts() {
        return m_inPorts;
    }
    
  @Override
  public java.util.List<NodeOutPortEnt> getOutPorts() {
        return m_outPorts;
    }
    
  @Override
  public NodeAnnotationEnt getAnnotation() {
        return m_annotation;
    }
    
  @Override
  public XYEnt getPosition() {
        return m_position;
    }
    
  
    public static class DefaultNodeEntBuilder implements NodeEntBuilder {
    
        public DefaultNodeEntBuilder(){
            
        }
    
        private String m_objectType;
        private String m_name;
        private org.knime.gateway.api.entity.NodeIDEnt m_id;
        private NodeStateEnt m_state;
        private java.util.List<NodeInPortEnt> m_inPorts = new java.util.ArrayList<>();
        private java.util.List<NodeOutPortEnt> m_outPorts = new java.util.ArrayList<>();
        private NodeAnnotationEnt m_annotation;
        private XYEnt m_position;

        @Override
        public DefaultNodeEntBuilder setObjectType(String objectType) {
             if(objectType == null) {
                 throw new IllegalArgumentException("objectType must not be null.");
             }
             m_objectType = objectType;
             return this;
        }

        @Override
        public DefaultNodeEntBuilder setName(String name) {
             if(name == null) {
                 throw new IllegalArgumentException("name must not be null.");
             }
             m_name = name;
             return this;
        }

        @Override
        public DefaultNodeEntBuilder setId(org.knime.gateway.api.entity.NodeIDEnt id) {
             if(id == null) {
                 throw new IllegalArgumentException("id must not be null.");
             }
             m_id = id;
             return this;
        }

        @Override
        public DefaultNodeEntBuilder setState(NodeStateEnt state) {
             if(state == null) {
                 throw new IllegalArgumentException("state must not be null.");
             }
             m_state = state;
             return this;
        }

        @Override
        public DefaultNodeEntBuilder setInPorts(java.util.List<NodeInPortEnt> inPorts) {
             m_inPorts = inPorts;
             return this;
        }

        @Override
        public DefaultNodeEntBuilder setOutPorts(java.util.List<NodeOutPortEnt> outPorts) {
             m_outPorts = outPorts;
             return this;
        }

        @Override
        public DefaultNodeEntBuilder setAnnotation(NodeAnnotationEnt annotation) {
             m_annotation = annotation;
             return this;
        }

        @Override
        public DefaultNodeEntBuilder setPosition(XYEnt position) {
             if(position == null) {
                 throw new IllegalArgumentException("position must not be null.");
             }
             m_position = position;
             return this;
        }

        
        @Override
        public DefaultNodeEnt build() {
            return new DefaultNodeEnt(this);
        }
    
    }

}
