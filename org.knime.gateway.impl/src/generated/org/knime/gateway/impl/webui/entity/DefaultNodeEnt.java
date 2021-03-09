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

import org.knime.gateway.api.webui.entity.AllowedNodeActionsEnt;
import org.knime.gateway.api.webui.entity.NodeAnnotationEnt;
import org.knime.gateway.api.webui.entity.NodeExecutionInfoEnt;
import org.knime.gateway.api.webui.entity.NodePortEnt;
import org.knime.gateway.api.webui.entity.XYEnt;

import org.knime.gateway.api.webui.entity.NodeEnt;

/**
 * Represents a node of certain kind (native node, component, metanode) in a workflow.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public class DefaultNodeEnt implements NodeEnt {

  protected org.knime.gateway.api.entity.NodeIDEnt m_id;
  protected java.util.List<? extends NodePortEnt> m_inPorts;
  protected java.util.List<? extends NodePortEnt> m_outPorts;
  protected NodeAnnotationEnt m_annotation;
  protected XYEnt m_position;
  protected KindEnum m_kind;
  protected AllowedNodeActionsEnt m_allowedActions;
  protected NodeExecutionInfoEnt m_executionInfo;
  protected java.util.BitSet m_successors;
  
  protected DefaultNodeEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "Node";
  }
  
  private DefaultNodeEnt(DefaultNodeEntBuilder builder) {
    
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
    m_allowedActions = immutable(builder.m_allowedActions);
    m_executionInfo = immutable(builder.m_executionInfo);
    m_successors = immutable(builder.m_successors);
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
        return Objects.equals(m_id, ent.m_id) && Objects.equals(m_inPorts, ent.m_inPorts) && Objects.equals(m_outPorts, ent.m_outPorts) && Objects.equals(m_annotation, ent.m_annotation) && Objects.equals(m_position, ent.m_position) && Objects.equals(m_kind, ent.m_kind) && Objects.equals(m_allowedActions, ent.m_allowedActions) && Objects.equals(m_executionInfo, ent.m_executionInfo) && Objects.equals(m_successors, ent.m_successors);
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
               .append(m_allowedActions)
               .append(m_executionInfo)
               .append(m_successors)
               .toHashCode();
   }
  
	
	
  @Override
  public org.knime.gateway.api.entity.NodeIDEnt getId() {
        return m_id;
  }
    
  @Override
  public java.util.List<? extends NodePortEnt> getInPorts() {
        return m_inPorts;
  }
    
  @Override
  public java.util.List<? extends NodePortEnt> getOutPorts() {
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
    
  @Override
  public KindEnum getKind() {
        return m_kind;
  }
    
  @Override
  public AllowedNodeActionsEnt getAllowedActions() {
        return m_allowedActions;
  }
    
  @Override
  public NodeExecutionInfoEnt getExecutionInfo() {
        return m_executionInfo;
  }
    
  @Override
  public java.util.BitSet getSuccessors() {
        return m_successors;
  }
    
  
    public static class DefaultNodeEntBuilder implements NodeEntBuilder {
    
        public DefaultNodeEntBuilder(){
            
        }
    
        private org.knime.gateway.api.entity.NodeIDEnt m_id;
        private java.util.List<? extends NodePortEnt> m_inPorts = new java.util.ArrayList<>();
        private java.util.List<? extends NodePortEnt> m_outPorts = new java.util.ArrayList<>();
        private NodeAnnotationEnt m_annotation;
        private XYEnt m_position;
        private KindEnum m_kind;
        private AllowedNodeActionsEnt m_allowedActions;
        private NodeExecutionInfoEnt m_executionInfo;
        private java.util.BitSet m_successors;

        @Override
        public DefaultNodeEntBuilder setId(org.knime.gateway.api.entity.NodeIDEnt id) {
             if(id == null) {
                 throw new IllegalArgumentException("id must not be null.");
             }
             m_id = id;
             return this;
        }

        @Override
        public DefaultNodeEntBuilder setInPorts(java.util.List<? extends NodePortEnt> inPorts) {
             if(inPorts == null) {
                 throw new IllegalArgumentException("inPorts must not be null.");
             }
             m_inPorts = inPorts;
             return this;
        }

        @Override
        public DefaultNodeEntBuilder setOutPorts(java.util.List<? extends NodePortEnt> outPorts) {
             if(outPorts == null) {
                 throw new IllegalArgumentException("outPorts must not be null.");
             }
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
        public DefaultNodeEntBuilder setKind(KindEnum kind) {
             if(kind == null) {
                 throw new IllegalArgumentException("kind must not be null.");
             }
             m_kind = kind;
             return this;
        }

        @Override
        public DefaultNodeEntBuilder setAllowedActions(AllowedNodeActionsEnt allowedActions) {
             m_allowedActions = allowedActions;
             return this;
        }

        @Override
        public DefaultNodeEntBuilder setExecutionInfo(NodeExecutionInfoEnt executionInfo) {
             m_executionInfo = executionInfo;
             return this;
        }

        @Override
        public DefaultNodeEntBuilder setSuccessors(java.util.BitSet successors) {
             m_successors = successors;
             return this;
        }

        
        @Override
        public DefaultNodeEnt build() {
            return new DefaultNodeEnt(this);
        }
    
    }

}
