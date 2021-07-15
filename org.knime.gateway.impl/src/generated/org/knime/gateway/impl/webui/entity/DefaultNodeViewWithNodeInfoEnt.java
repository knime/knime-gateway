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

import org.knime.gateway.api.webui.entity.ComponentViewInfoEnt;
import org.knime.gateway.api.webui.entity.NodeStateEnt;
import org.knime.gateway.impl.webui.entity.DefaultNodeViewEnt;

import org.knime.gateway.api.webui.entity.NodeViewWithNodeInfoEnt;

/**
 * TODO
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public class DefaultNodeViewWithNodeInfoEnt implements NodeViewWithNodeInfoEnt {

  protected TypeEnum m_type;
  protected String m_iframeSrc;
  protected String m_uiComponentId;
  protected String m_nodeName;
  protected String m_nodeAnnotation;
  protected NodeStateEnt m_nodeState;
  protected ComponentViewInfoEnt m_componentViewInfo;
  
  protected DefaultNodeViewWithNodeInfoEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "NodeViewWithNodeInfo";
  }
  
  private DefaultNodeViewWithNodeInfoEnt(DefaultNodeViewWithNodeInfoEntBuilder builder) {
    super();
    if(builder.m_type == null) {
        throw new IllegalArgumentException("type must not be null.");
    }
    m_type = immutable(builder.m_type);
    m_iframeSrc = immutable(builder.m_iframeSrc);
    m_uiComponentId = immutable(builder.m_uiComponentId);
    m_nodeName = immutable(builder.m_nodeName);
    m_nodeAnnotation = immutable(builder.m_nodeAnnotation);
    m_nodeState = immutable(builder.m_nodeState);
    m_componentViewInfo = immutable(builder.m_componentViewInfo);
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
        DefaultNodeViewWithNodeInfoEnt ent = (DefaultNodeViewWithNodeInfoEnt)o;
        return Objects.equals(m_type, ent.m_type) && Objects.equals(m_iframeSrc, ent.m_iframeSrc) && Objects.equals(m_uiComponentId, ent.m_uiComponentId) && Objects.equals(m_nodeName, ent.m_nodeName) && Objects.equals(m_nodeAnnotation, ent.m_nodeAnnotation) && Objects.equals(m_nodeState, ent.m_nodeState) && Objects.equals(m_componentViewInfo, ent.m_componentViewInfo);
    }


  
   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
       return new HashCodeBuilder()
               .append(m_type)
               .append(m_iframeSrc)
               .append(m_uiComponentId)
               .append(m_nodeName)
               .append(m_nodeAnnotation)
               .append(m_nodeState)
               .append(m_componentViewInfo)
               .toHashCode();
   }
  
	
	
  @Override
  public TypeEnum getType() {
        return m_type;
  }
    
  @Override
  public String getIframeSrc() {
        return m_iframeSrc;
  }
    
  @Override
  public String getUiComponentId() {
        return m_uiComponentId;
  }
    
  @Override
  public String getNodeName() {
        return m_nodeName;
  }
    
  @Override
  public String getNodeAnnotation() {
        return m_nodeAnnotation;
  }
    
  @Override
  public NodeStateEnt getNodeState() {
        return m_nodeState;
  }
    
  @Override
  public ComponentViewInfoEnt getComponentViewInfo() {
        return m_componentViewInfo;
  }
    
  
    public static class DefaultNodeViewWithNodeInfoEntBuilder implements NodeViewWithNodeInfoEntBuilder {
    
        public DefaultNodeViewWithNodeInfoEntBuilder(){
            super();
        }
    
        private TypeEnum m_type;
        private String m_iframeSrc;
        private String m_uiComponentId;
        private String m_nodeName;
        private String m_nodeAnnotation;
        private NodeStateEnt m_nodeState;
        private ComponentViewInfoEnt m_componentViewInfo;

        @Override
        public DefaultNodeViewWithNodeInfoEntBuilder setType(TypeEnum type) {
             if(type == null) {
                 throw new IllegalArgumentException("type must not be null.");
             }
             m_type = type;
             return this;
        }

        @Override
        public DefaultNodeViewWithNodeInfoEntBuilder setIframeSrc(String iframeSrc) {
             m_iframeSrc = iframeSrc;
             return this;
        }

        @Override
        public DefaultNodeViewWithNodeInfoEntBuilder setUiComponentId(String uiComponentId) {
             m_uiComponentId = uiComponentId;
             return this;
        }

        @Override
        public DefaultNodeViewWithNodeInfoEntBuilder setNodeName(String nodeName) {
             m_nodeName = nodeName;
             return this;
        }

        @Override
        public DefaultNodeViewWithNodeInfoEntBuilder setNodeAnnotation(String nodeAnnotation) {
             m_nodeAnnotation = nodeAnnotation;
             return this;
        }

        @Override
        public DefaultNodeViewWithNodeInfoEntBuilder setNodeState(NodeStateEnt nodeState) {
             m_nodeState = nodeState;
             return this;
        }

        @Override
        public DefaultNodeViewWithNodeInfoEntBuilder setComponentViewInfo(ComponentViewInfoEnt componentViewInfo) {
             m_componentViewInfo = componentViewInfo;
             return this;
        }

        
        @Override
        public DefaultNodeViewWithNodeInfoEnt build() {
            return new DefaultNodeViewWithNodeInfoEnt(this);
        }
    
    }

}
