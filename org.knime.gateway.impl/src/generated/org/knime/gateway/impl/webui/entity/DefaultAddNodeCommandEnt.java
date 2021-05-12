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
import org.knime.gateway.api.webui.entity.XYEnt;
import org.knime.gateway.impl.webui.entity.DefaultWorkflowCommandEnt;

import org.knime.gateway.api.webui.entity.AddNodeCommandEnt;

/**
 * Adds a new node to the workflow.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public class DefaultAddNodeCommandEnt implements AddNodeCommandEnt {

  protected KindEnum m_kind;
  protected XYEnt m_position;
  protected NodeFactoryKeyEnt m_nodeFactory;
  
  protected DefaultAddNodeCommandEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "AddNodeCommand";
  }
  
  private DefaultAddNodeCommandEnt(DefaultAddNodeCommandEntBuilder builder) {
    super();
    if(builder.m_kind == null) {
        throw new IllegalArgumentException("kind must not be null.");
    }
    m_kind = immutable(builder.m_kind);
    if(builder.m_position == null) {
        throw new IllegalArgumentException("position must not be null.");
    }
    m_position = immutable(builder.m_position);
    if(builder.m_nodeFactory == null) {
        throw new IllegalArgumentException("nodeFactory must not be null.");
    }
    m_nodeFactory = immutable(builder.m_nodeFactory);
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
        DefaultAddNodeCommandEnt ent = (DefaultAddNodeCommandEnt)o;
        return Objects.equals(m_kind, ent.m_kind) && Objects.equals(m_position, ent.m_position) && Objects.equals(m_nodeFactory, ent.m_nodeFactory);
    }


  
   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
       return new HashCodeBuilder()
               .append(m_kind)
               .append(m_position)
               .append(m_nodeFactory)
               .toHashCode();
   }
  
	
	
  @Override
  public KindEnum getKind() {
        return m_kind;
  }
    
  @Override
  public XYEnt getPosition() {
        return m_position;
  }
    
  @Override
  public NodeFactoryKeyEnt getNodeFactory() {
        return m_nodeFactory;
  }
    
  
    public static class DefaultAddNodeCommandEntBuilder implements AddNodeCommandEntBuilder {
    
        public DefaultAddNodeCommandEntBuilder(){
            super();
        }
    
        private KindEnum m_kind;
        private XYEnt m_position;
        private NodeFactoryKeyEnt m_nodeFactory;

        @Override
        public DefaultAddNodeCommandEntBuilder setKind(KindEnum kind) {
             if(kind == null) {
                 throw new IllegalArgumentException("kind must not be null.");
             }
             m_kind = kind;
             return this;
        }

        @Override
        public DefaultAddNodeCommandEntBuilder setPosition(XYEnt position) {
             if(position == null) {
                 throw new IllegalArgumentException("position must not be null.");
             }
             m_position = position;
             return this;
        }

        @Override
        public DefaultAddNodeCommandEntBuilder setNodeFactory(NodeFactoryKeyEnt nodeFactory) {
             if(nodeFactory == null) {
                 throw new IllegalArgumentException("nodeFactory must not be null.");
             }
             m_nodeFactory = nodeFactory;
             return this;
        }

        
        @Override
        public DefaultAddNodeCommandEnt build() {
            return new DefaultAddNodeCommandEnt(this);
        }
    
    }

}
