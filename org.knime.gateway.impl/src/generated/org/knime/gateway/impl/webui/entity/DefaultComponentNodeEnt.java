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
import org.knime.gateway.impl.webui.entity.DefaultWorkflowNodeEnt;

import org.knime.gateway.api.webui.entity.ComponentNodeEnt;

/**
 * A node wrapping (referencing) a workflow (also referred to it as component or subnode) that almost behaves as a ordinary node.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public class DefaultComponentNodeEnt extends DefaultWorkflowNodeEnt implements ComponentNodeEnt {

  protected TypeEnum m_type;
  
  protected DefaultComponentNodeEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "ComponentNode";
  }
  
  private DefaultComponentNodeEnt(DefaultComponentNodeEntBuilder builder) {
    super();
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
    m_workflowOutgoingPortNodeStates = immutable(builder.m_workflowOutgoingPortNodeStates);
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
        DefaultComponentNodeEnt ent = (DefaultComponentNodeEnt)o;
        return Objects.equals(m_objectType, ent.m_objectType) && Objects.equals(m_name, ent.m_name) && Objects.equals(m_id, ent.m_id) && Objects.equals(m_state, ent.m_state) && Objects.equals(m_inPorts, ent.m_inPorts) && Objects.equals(m_outPorts, ent.m_outPorts) && Objects.equals(m_annotation, ent.m_annotation) && Objects.equals(m_position, ent.m_position) && Objects.equals(m_workflowOutgoingPortNodeStates, ent.m_workflowOutgoingPortNodeStates) && Objects.equals(m_type, ent.m_type);
    }


  @Override
  public TypeEnum getType() {
        return m_type;
    }
    
  
    public static class DefaultComponentNodeEntBuilder implements ComponentNodeEntBuilder {
    
        public DefaultComponentNodeEntBuilder(){
            super();
        }
    
        private String m_objectType;
        private String m_name;
        private org.knime.gateway.api.entity.NodeIDEnt m_id;
        private NodeStateEnt m_state;
        private java.util.List<NodeInPortEnt> m_inPorts = new java.util.ArrayList<>();
        private java.util.List<NodeOutPortEnt> m_outPorts = new java.util.ArrayList<>();
        private NodeAnnotationEnt m_annotation;
        private XYEnt m_position;
        private java.util.List<NodeStateEnt> m_workflowOutgoingPortNodeStates = new java.util.ArrayList<>();
        private TypeEnum m_type;

        @Override
        public DefaultComponentNodeEntBuilder setObjectType(String objectType) {
             if(objectType == null) {
                 throw new IllegalArgumentException("objectType must not be null.");
             }
             m_objectType = objectType;
             return this;
        }

        @Override
        public DefaultComponentNodeEntBuilder setName(String name) {
             if(name == null) {
                 throw new IllegalArgumentException("name must not be null.");
             }
             m_name = name;
             return this;
        }

        @Override
        public DefaultComponentNodeEntBuilder setId(org.knime.gateway.api.entity.NodeIDEnt id) {
             if(id == null) {
                 throw new IllegalArgumentException("id must not be null.");
             }
             m_id = id;
             return this;
        }

        @Override
        public DefaultComponentNodeEntBuilder setState(NodeStateEnt state) {
             if(state == null) {
                 throw new IllegalArgumentException("state must not be null.");
             }
             m_state = state;
             return this;
        }

        @Override
        public DefaultComponentNodeEntBuilder setInPorts(java.util.List<NodeInPortEnt> inPorts) {
             m_inPorts = inPorts;
             return this;
        }

        @Override
        public DefaultComponentNodeEntBuilder setOutPorts(java.util.List<NodeOutPortEnt> outPorts) {
             m_outPorts = outPorts;
             return this;
        }

        @Override
        public DefaultComponentNodeEntBuilder setAnnotation(NodeAnnotationEnt annotation) {
             m_annotation = annotation;
             return this;
        }

        @Override
        public DefaultComponentNodeEntBuilder setPosition(XYEnt position) {
             if(position == null) {
                 throw new IllegalArgumentException("position must not be null.");
             }
             m_position = position;
             return this;
        }

        @Override
        public DefaultComponentNodeEntBuilder setWorkflowOutgoingPortNodeStates(java.util.List<NodeStateEnt> workflowOutgoingPortNodeStates) {
             m_workflowOutgoingPortNodeStates = workflowOutgoingPortNodeStates;
             return this;
        }

        @Override
        public DefaultComponentNodeEntBuilder setType(TypeEnum type) {
             m_type = type;
             return this;
        }

        
        @Override
        public DefaultComponentNodeEnt build() {
            return new DefaultComponentNodeEnt(this);
        }
    
    }

}
