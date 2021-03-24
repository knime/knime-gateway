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

import org.knime.gateway.api.webui.entity.NodeSelectionEnt;

import org.knime.gateway.api.webui.entity.NodeSelectionsEnt;

/**
 * A list of node selections.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public class DefaultNodeSelectionsEnt implements NodeSelectionsEnt {

  protected java.util.List<NodeSelectionEnt> m_selections;
  protected Integer m_totalNumSelections;
  
  protected DefaultNodeSelectionsEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "NodeSelections";
  }
  
  private DefaultNodeSelectionsEnt(DefaultNodeSelectionsEntBuilder builder) {
    
    m_selections = immutable(builder.m_selections);
    m_totalNumSelections = immutable(builder.m_totalNumSelections);
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
        DefaultNodeSelectionsEnt ent = (DefaultNodeSelectionsEnt)o;
        return Objects.equals(m_selections, ent.m_selections) && Objects.equals(m_totalNumSelections, ent.m_totalNumSelections);
    }


  
   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
       return new HashCodeBuilder()
               .append(m_selections)
               .append(m_totalNumSelections)
               .toHashCode();
   }
  
	
	
  @Override
  public java.util.List<NodeSelectionEnt> getSelections() {
        return m_selections;
  }
    
  @Override
  public Integer getTotalNumSelections() {
        return m_totalNumSelections;
  }
    
  
    public static class DefaultNodeSelectionsEntBuilder implements NodeSelectionsEntBuilder {
    
        public DefaultNodeSelectionsEntBuilder(){
            
        }
    
        private java.util.List<NodeSelectionEnt> m_selections;
        private Integer m_totalNumSelections;

        @Override
        public DefaultNodeSelectionsEntBuilder setSelections(java.util.List<NodeSelectionEnt> selections) {
             m_selections = selections;
             return this;
        }

        @Override
        public DefaultNodeSelectionsEntBuilder setTotalNumSelections(Integer totalNumSelections) {
             m_totalNumSelections = totalNumSelections;
             return this;
        }

        
        @Override
        public DefaultNodeSelectionsEnt build() {
            return new DefaultNodeSelectionsEnt(this);
        }
    
    }

}
