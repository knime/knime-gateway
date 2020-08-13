/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.com; Email: contact@knime.com
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME AG herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ------------------------------------------------------------------------
 */
package org.knime.gateway.impl.webui.entity;

import static org.knime.gateway.api.util.EntityUtil.immutable;

import java.util.Objects;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import org.knime.gateway.api.webui.entity.NodeAnnotationEnt;
import org.knime.gateway.api.webui.entity.NodePortEnt;
import org.knime.gateway.api.webui.entity.XYEnt;
import org.knime.gateway.impl.webui.entity.DefaultNodeEnt;

import org.knime.gateway.api.webui.entity.WorkflowNodeEnt;

/**
 * A node containing (referencing) a workflow (also referred to it as metanode)
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public class DefaultWorkflowNodeEnt extends DefaultNodeEnt implements WorkflowNodeEnt {

  protected String m_name;
  
  protected DefaultWorkflowNodeEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "WorkflowNode";
  }
  
  private DefaultWorkflowNodeEnt(DefaultWorkflowNodeEntBuilder builder) {
    super();
    if(builder.m_id == null) {
        throw new IllegalArgumentException("id must not be null.");
    }
    m_id = immutable(builder.m_id);
    if(builder.m_inPorts == null) {
        throw new IllegalArgumentException("inPorts must not be null.");
    }
    m_inPorts = immutable(builder.m_inPorts);
    if(builder.m_outPorts == null) {
        throw new IllegalArgumentException("outPorts must not be null.");
    }
    m_outPorts = immutable(builder.m_outPorts);
    m_annotation = immutable(builder.m_annotation);
    if(builder.m_position == null) {
        throw new IllegalArgumentException("position must not be null.");
    }
    m_position = immutable(builder.m_position);
    if(builder.m_kind == null) {
        throw new IllegalArgumentException("kind must not be null.");
    }
    m_kind = immutable(builder.m_kind);
    if(builder.m_name == null) {
        throw new IllegalArgumentException("name must not be null.");
    }
    m_name = immutable(builder.m_name);
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
        DefaultWorkflowNodeEnt ent = (DefaultWorkflowNodeEnt)o;
        return Objects.equals(m_id, ent.m_id) && Objects.equals(m_inPorts, ent.m_inPorts) && Objects.equals(m_outPorts, ent.m_outPorts) && Objects.equals(m_annotation, ent.m_annotation) && Objects.equals(m_position, ent.m_position) && Objects.equals(m_kind, ent.m_kind) && Objects.equals(m_name, ent.m_name);
    }


  
   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
       return new HashCodeBuilder()
               .append(m_id)
               .append(m_inPorts)
               .append(m_outPorts)
               .append(m_annotation)
               .append(m_position)
               .append(m_kind)
               .append(m_name)
               .toHashCode();
   }
  
	
	
  @Override
  public String getName() {
        return m_name;
  }
    
  
    public static class DefaultWorkflowNodeEntBuilder implements WorkflowNodeEntBuilder {
    
        public DefaultWorkflowNodeEntBuilder(){
            super();
        }
    
        private org.knime.gateway.api.entity.NodeIDEnt m_id;
        private java.util.List<NodePortEnt> m_inPorts = new java.util.ArrayList<>();
        private java.util.List<NodePortEnt> m_outPorts = new java.util.ArrayList<>();
        private NodeAnnotationEnt m_annotation;
        private XYEnt m_position;
        private KindEnum m_kind;
        private String m_name;

        @Override
        public DefaultWorkflowNodeEntBuilder setId(org.knime.gateway.api.entity.NodeIDEnt id) {
             if(id == null) {
                 throw new IllegalArgumentException("id must not be null.");
             }
             m_id = id;
             return this;
        }

        @Override
        public DefaultWorkflowNodeEntBuilder setInPorts(java.util.List<NodePortEnt> inPorts) {
             if(inPorts == null) {
                 throw new IllegalArgumentException("inPorts must not be null.");
             }
             m_inPorts = inPorts;
             return this;
        }

        @Override
        public DefaultWorkflowNodeEntBuilder setOutPorts(java.util.List<NodePortEnt> outPorts) {
             if(outPorts == null) {
                 throw new IllegalArgumentException("outPorts must not be null.");
             }
             m_outPorts = outPorts;
             return this;
        }

        @Override
        public DefaultWorkflowNodeEntBuilder setAnnotation(NodeAnnotationEnt annotation) {
             m_annotation = annotation;
             return this;
        }

        @Override
        public DefaultWorkflowNodeEntBuilder setPosition(XYEnt position) {
             if(position == null) {
                 throw new IllegalArgumentException("position must not be null.");
             }
             m_position = position;
             return this;
        }

        @Override
        public DefaultWorkflowNodeEntBuilder setKind(KindEnum kind) {
             if(kind == null) {
                 throw new IllegalArgumentException("kind must not be null.");
             }
             m_kind = kind;
             return this;
        }

        @Override
        public DefaultWorkflowNodeEntBuilder setName(String name) {
             if(name == null) {
                 throw new IllegalArgumentException("name must not be null.");
             }
             m_name = name;
             return this;
        }

        
        @Override
        public DefaultWorkflowNodeEnt build() {
            return new DefaultWorkflowNodeEnt(this);
        }
    
    }

}
