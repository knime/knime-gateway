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

import org.knime.gateway.api.webui.entity.NodeDialogEnt;
import org.knime.gateway.api.webui.entity.NodeViewEnt;

import org.knime.gateway.api.webui.entity.SingleNodeEnt;

/**
 * TODO
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public class DefaultSingleNodeEnt implements SingleNodeEnt {

  protected NodeDialogEnt m_dialog;
  protected NodeViewEnt m_view;
  
  protected DefaultSingleNodeEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "SingleNode";
  }
  
  private DefaultSingleNodeEnt(DefaultSingleNodeEntBuilder builder) {
    
    m_dialog = immutable(builder.m_dialog);
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
        DefaultSingleNodeEnt ent = (DefaultSingleNodeEnt)o;
        return Objects.equals(m_dialog, ent.m_dialog) && Objects.equals(m_view, ent.m_view);
    }


  
   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
       return new HashCodeBuilder()
               .append(m_dialog)
               .append(m_view)
               .toHashCode();
   }
  
	
	
  @Override
  public NodeDialogEnt getDialog() {
        return m_dialog;
  }
    
  @Override
  public NodeViewEnt getView() {
        return m_view;
  }
    
  
    public static class DefaultSingleNodeEntBuilder implements SingleNodeEntBuilder {
    
        public DefaultSingleNodeEntBuilder(){
            
        }
    
        private NodeDialogEnt m_dialog;
        private NodeViewEnt m_view;

        @Override
        public DefaultSingleNodeEntBuilder setDialog(NodeDialogEnt dialog) {
             m_dialog = dialog;
             return this;
        }

        @Override
        public DefaultSingleNodeEntBuilder setView(NodeViewEnt view) {
             m_view = view;
             return this;
        }

        
        @Override
        public DefaultSingleNodeEnt build() {
            return new DefaultSingleNodeEnt(this);
        }
    
    }

}
