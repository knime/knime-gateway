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

import org.knime.gateway.api.webui.entity.NodeFactoryKeyEnt;
import org.knime.gateway.api.webui.entity.NodePortTemplateEnt;
import org.knime.gateway.impl.webui.entity.DefaultNativeNodeInvariantsEnt;

import org.knime.gateway.api.webui.entity.NodeTemplateEnt;

/**
 * Contains all the &#39;static&#39; properties of a node or component required to draw the node/component figure.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public class DefaultNodeTemplateEnt implements NodeTemplateEnt {

  protected String m_name;
  protected TypeEnum m_type;
  protected String m_icon;
  protected NodeFactoryKeyEnt m_nodeFactory;
  protected String m_id;
  protected Boolean m_component;
  protected java.util.List<NodePortTemplateEnt> m_inPorts;
  protected java.util.List<NodePortTemplateEnt> m_outPorts;
  
  protected DefaultNodeTemplateEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "NodeTemplate";
  }
  
  private DefaultNodeTemplateEnt(DefaultNodeTemplateEntBuilder builder) {
    super();
    if(builder.m_name == null) {
        throw new IllegalArgumentException("name must not be null.");
    }
    m_name = immutable(builder.m_name);
    if(builder.m_type == null) {
        throw new IllegalArgumentException("type must not be null.");
    }
    m_type = immutable(builder.m_type);
    m_icon = immutable(builder.m_icon);
    m_nodeFactory = immutable(builder.m_nodeFactory);
    if(builder.m_id == null) {
        throw new IllegalArgumentException("id must not be null.");
    }
    m_id = immutable(builder.m_id);
    m_component = immutable(builder.m_component);
    m_inPorts = immutable(builder.m_inPorts);
    m_outPorts = immutable(builder.m_outPorts);
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
        DefaultNodeTemplateEnt ent = (DefaultNodeTemplateEnt)o;
        return Objects.equals(m_name, ent.m_name) && Objects.equals(m_type, ent.m_type) && Objects.equals(m_icon, ent.m_icon) && Objects.equals(m_nodeFactory, ent.m_nodeFactory) && Objects.equals(m_id, ent.m_id) && Objects.equals(m_component, ent.m_component) && Objects.equals(m_inPorts, ent.m_inPorts) && Objects.equals(m_outPorts, ent.m_outPorts);
    }


  
   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
       return new HashCodeBuilder()
               .append(m_name)
               .append(m_type)
               .append(m_icon)
               .append(m_nodeFactory)
               .append(m_id)
               .append(m_component)
               .append(m_inPorts)
               .append(m_outPorts)
               .toHashCode();
   }
  
	
	
  @Override
  public String getName() {
        return m_name;
  }
    
  @Override
  public TypeEnum getType() {
        return m_type;
  }
    
  @Override
  public String getIcon() {
        return m_icon;
  }
    
  @Override
  public NodeFactoryKeyEnt getNodeFactory() {
        return m_nodeFactory;
  }
    
  @Override
  public String getId() {
        return m_id;
  }
    
  @Override
  public Boolean isComponent() {
        return m_component;
  }
    
  @Override
  public java.util.List<NodePortTemplateEnt> getInPorts() {
        return m_inPorts;
  }
    
  @Override
  public java.util.List<NodePortTemplateEnt> getOutPorts() {
        return m_outPorts;
  }
    
  
    public static class DefaultNodeTemplateEntBuilder implements NodeTemplateEntBuilder {
    
        public DefaultNodeTemplateEntBuilder(){
            super();
        }
    
        private String m_name;
        private TypeEnum m_type;
        private String m_icon;
        private NodeFactoryKeyEnt m_nodeFactory;
        private String m_id;
        private Boolean m_component;
        private java.util.List<NodePortTemplateEnt> m_inPorts;
        private java.util.List<NodePortTemplateEnt> m_outPorts;

        @Override
        public DefaultNodeTemplateEntBuilder setName(String name) {
             if(name == null) {
                 throw new IllegalArgumentException("name must not be null.");
             }
             m_name = name;
             return this;
        }

        @Override
        public DefaultNodeTemplateEntBuilder setType(TypeEnum type) {
             if(type == null) {
                 throw new IllegalArgumentException("type must not be null.");
             }
             m_type = type;
             return this;
        }

        @Override
        public DefaultNodeTemplateEntBuilder setIcon(String icon) {
             m_icon = icon;
             return this;
        }

        @Override
        public DefaultNodeTemplateEntBuilder setNodeFactory(NodeFactoryKeyEnt nodeFactory) {
             m_nodeFactory = nodeFactory;
             return this;
        }

        @Override
        public DefaultNodeTemplateEntBuilder setId(String id) {
             if(id == null) {
                 throw new IllegalArgumentException("id must not be null.");
             }
             m_id = id;
             return this;
        }

        @Override
        public DefaultNodeTemplateEntBuilder setComponent(Boolean component) {
             m_component = component;
             return this;
        }

        @Override
        public DefaultNodeTemplateEntBuilder setInPorts(java.util.List<NodePortTemplateEnt> inPorts) {
             m_inPorts = inPorts;
             return this;
        }

        @Override
        public DefaultNodeTemplateEntBuilder setOutPorts(java.util.List<NodePortTemplateEnt> outPorts) {
             m_outPorts = outPorts;
             return this;
        }

        
        @Override
        public DefaultNodeTemplateEnt build() {
            return new DefaultNodeTemplateEnt(this);
        }
    
    }

}
