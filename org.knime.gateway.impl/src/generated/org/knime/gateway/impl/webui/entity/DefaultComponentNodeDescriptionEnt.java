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

import org.knime.gateway.api.webui.entity.NodeDialogOptionsEnt;
import org.knime.gateway.api.webui.entity.NodePortDescriptionEnt;
import org.knime.gateway.api.webui.entity.NodeViewDescriptionEnt;
import org.knime.gateway.impl.webui.entity.DefaultComponentNodeAndDescriptionEnt;

import org.knime.gateway.api.webui.entity.ComponentNodeDescriptionEnt;

/**
 * Description of certain aspects of a component. This is static information for a component which remain the same even if component is not part of a workflow.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public class DefaultComponentNodeDescriptionEnt implements ComponentNodeDescriptionEnt {

  protected String m_name;
  protected TypeEnum m_type;
  protected String m_icon;
  protected String m_description;
  protected java.util.List<NodeDialogOptionsEnt> m_options;
  protected java.util.List<NodeViewDescriptionEnt> m_views;
  protected java.util.List<NodePortDescriptionEnt> m_inPorts;
  protected java.util.List<NodePortDescriptionEnt> m_outPorts;
  
  protected DefaultComponentNodeDescriptionEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "ComponentNodeDescription";
  }
  
  private DefaultComponentNodeDescriptionEnt(DefaultComponentNodeDescriptionEntBuilder builder) {
    super();
    if(builder.m_name == null) {
        throw new IllegalArgumentException("name must not be null.");
    }
    m_name = immutable(builder.m_name);
    m_type = immutable(builder.m_type);
    m_icon = immutable(builder.m_icon);
    m_description = immutable(builder.m_description);
    m_options = immutable(builder.m_options);
    m_views = immutable(builder.m_views);
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
        DefaultComponentNodeDescriptionEnt ent = (DefaultComponentNodeDescriptionEnt)o;
        return Objects.equals(m_name, ent.m_name) && Objects.equals(m_type, ent.m_type) && Objects.equals(m_icon, ent.m_icon) && Objects.equals(m_description, ent.m_description) && Objects.equals(m_options, ent.m_options) && Objects.equals(m_views, ent.m_views) && Objects.equals(m_inPorts, ent.m_inPorts) && Objects.equals(m_outPorts, ent.m_outPorts);
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
               .append(m_description)
               .append(m_options)
               .append(m_views)
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
  public String getDescription() {
        return m_description;
  }
    
  @Override
  public java.util.List<NodeDialogOptionsEnt> getOptions() {
        return m_options;
  }
    
  @Override
  public java.util.List<NodeViewDescriptionEnt> getViews() {
        return m_views;
  }
    
  @Override
  public java.util.List<NodePortDescriptionEnt> getInPorts() {
        return m_inPorts;
  }
    
  @Override
  public java.util.List<NodePortDescriptionEnt> getOutPorts() {
        return m_outPorts;
  }
    
  
    public static class DefaultComponentNodeDescriptionEntBuilder implements ComponentNodeDescriptionEntBuilder {
    
        public DefaultComponentNodeDescriptionEntBuilder(){
            super();
        }
    
        private String m_name;
        private TypeEnum m_type;
        private String m_icon;
        private String m_description;
        private java.util.List<NodeDialogOptionsEnt> m_options;
        private java.util.List<NodeViewDescriptionEnt> m_views;
        private java.util.List<NodePortDescriptionEnt> m_inPorts;
        private java.util.List<NodePortDescriptionEnt> m_outPorts;

        @Override
        public DefaultComponentNodeDescriptionEntBuilder setName(String name) {
             if(name == null) {
                 throw new IllegalArgumentException("name must not be null.");
             }
             m_name = name;
             return this;
        }

        @Override
        public DefaultComponentNodeDescriptionEntBuilder setType(TypeEnum type) {
             m_type = type;
             return this;
        }

        @Override
        public DefaultComponentNodeDescriptionEntBuilder setIcon(String icon) {
             m_icon = icon;
             return this;
        }

        @Override
        public DefaultComponentNodeDescriptionEntBuilder setDescription(String description) {
             m_description = description;
             return this;
        }

        @Override
        public DefaultComponentNodeDescriptionEntBuilder setOptions(java.util.List<NodeDialogOptionsEnt> options) {
             m_options = options;
             return this;
        }

        @Override
        public DefaultComponentNodeDescriptionEntBuilder setViews(java.util.List<NodeViewDescriptionEnt> views) {
             m_views = views;
             return this;
        }

        @Override
        public DefaultComponentNodeDescriptionEntBuilder setInPorts(java.util.List<NodePortDescriptionEnt> inPorts) {
             m_inPorts = inPorts;
             return this;
        }

        @Override
        public DefaultComponentNodeDescriptionEntBuilder setOutPorts(java.util.List<NodePortDescriptionEnt> outPorts) {
             m_outPorts = outPorts;
             return this;
        }

        
        @Override
        public DefaultComponentNodeDescriptionEnt build() {
            return new DefaultComponentNodeDescriptionEnt(this);
        }
    
    }

}
