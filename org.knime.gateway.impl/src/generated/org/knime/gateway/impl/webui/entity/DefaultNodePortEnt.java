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

import org.knime.gateway.api.webui.entity.PortViewEnt;
import org.knime.gateway.impl.webui.entity.DefaultNodePortAndTemplateEnt;

import org.knime.gateway.api.webui.entity.NodePortEnt;

/**
 * A single port of a node.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public class DefaultNodePortEnt implements NodePortEnt {

  protected String m_name;
  protected TypeEnum m_type;
  protected String m_color;
  protected Boolean m_optional;
  protected String m_info;
  protected Integer m_index;
  protected java.util.List<org.knime.gateway.api.entity.ConnectionIDEnt> m_connectedVia;
  protected Boolean m_inactive;
  protected PortViewEnt m_view;
  
  protected DefaultNodePortEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "NodePort";
  }
  
  private DefaultNodePortEnt(DefaultNodePortEntBuilder builder) {
    super();
    m_name = immutable(builder.m_name);
    if(builder.m_type == null) {
        throw new IllegalArgumentException("type must not be null.");
    }
    m_type = immutable(builder.m_type);
    m_color = immutable(builder.m_color);
    m_optional = immutable(builder.m_optional);
    m_info = immutable(builder.m_info);
    if(builder.m_index == null) {
        throw new IllegalArgumentException("index must not be null.");
    }
    m_index = immutable(builder.m_index);
    m_connectedVia = immutable(builder.m_connectedVia);
    m_inactive = immutable(builder.m_inactive);
    m_view = immutable(builder.m_view);
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
        return Objects.equals(m_name, ent.m_name) && Objects.equals(m_type, ent.m_type) && Objects.equals(m_color, ent.m_color) && Objects.equals(m_optional, ent.m_optional) && Objects.equals(m_info, ent.m_info) && Objects.equals(m_index, ent.m_index) && Objects.equals(m_connectedVia, ent.m_connectedVia) && Objects.equals(m_inactive, ent.m_inactive) && Objects.equals(m_view, ent.m_view);
    }


  
   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
       return new HashCodeBuilder()
               .append(m_name)
               .append(m_type)
               .append(m_color)
               .append(m_optional)
               .append(m_info)
               .append(m_index)
               .append(m_connectedVia)
               .append(m_inactive)
               .append(m_view)
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
  public String getColor() {
        return m_color;
  }
    
  @Override
  public Boolean isOptional() {
        return m_optional;
  }
    
  @Override
  public String getInfo() {
        return m_info;
  }
    
  @Override
  public Integer getIndex() {
        return m_index;
  }
    
  @Override
  public java.util.List<org.knime.gateway.api.entity.ConnectionIDEnt> getConnectedVia() {
        return m_connectedVia;
  }
    
  @Override
  public Boolean isInactive() {
        return m_inactive;
  }
    
  @Override
  public PortViewEnt getView() {
        return m_view;
  }
    
  
    public static class DefaultNodePortEntBuilder implements NodePortEntBuilder {
    
        public DefaultNodePortEntBuilder(){
            super();
        }
    
        private String m_name;
        private TypeEnum m_type;
        private String m_color;
        private Boolean m_optional;
        private String m_info;
        private Integer m_index;
        private java.util.List<org.knime.gateway.api.entity.ConnectionIDEnt> m_connectedVia;
        private Boolean m_inactive;
        private PortViewEnt m_view;

        @Override
        public DefaultNodePortEntBuilder setName(String name) {
             m_name = name;
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
        public DefaultNodePortEntBuilder setOptional(Boolean optional) {
             m_optional = optional;
             return this;
        }

        @Override
        public DefaultNodePortEntBuilder setInfo(String info) {
             m_info = info;
             return this;
        }

        @Override
        public DefaultNodePortEntBuilder setIndex(Integer index) {
             if(index == null) {
                 throw new IllegalArgumentException("index must not be null.");
             }
             m_index = index;
             return this;
        }

        @Override
        public DefaultNodePortEntBuilder setConnectedVia(java.util.List<org.knime.gateway.api.entity.ConnectionIDEnt> connectedVia) {
             m_connectedVia = connectedVia;
             return this;
        }

        @Override
        public DefaultNodePortEntBuilder setInactive(Boolean inactive) {
             m_inactive = inactive;
             return this;
        }

        @Override
        public DefaultNodePortEntBuilder setView(PortViewEnt view) {
             m_view = view;
             return this;
        }

        
        @Override
        public DefaultNodePortEnt build() {
            return new DefaultNodePortEnt(this);
        }
    
    }

}