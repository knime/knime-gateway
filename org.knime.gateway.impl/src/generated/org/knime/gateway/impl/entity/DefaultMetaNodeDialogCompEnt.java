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
package org.knime.gateway.impl.entity;

import static org.knime.gateway.api.util.EntityUtil.immutable;

import java.util.Objects;

import org.knime.gateway.api.entity.JavaObjectEnt;

import org.knime.gateway.api.entity.MetaNodeDialogCompEnt;

/**
 * A component that is part of a metanode dialog.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/org.knime.gateway.impl-config.json"})
public class DefaultMetaNodeDialogCompEnt  implements MetaNodeDialogCompEnt {

  protected String m_paramName;
  protected org.knime.gateway.api.entity.NodeIDEnt m_nodeID;
  protected Boolean m_isHideInDialog;
  protected JavaObjectEnt m_representation;
  
  protected DefaultMetaNodeDialogCompEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "MetaNodeDialogComp";
  }
  
  private DefaultMetaNodeDialogCompEnt(DefaultMetaNodeDialogCompEntBuilder builder) {
    
    m_paramName = immutable(builder.m_paramName);
    m_nodeID = immutable(builder.m_nodeID);
    m_isHideInDialog = immutable(builder.m_isHideInDialog);
    m_representation = immutable(builder.m_representation);
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
        DefaultMetaNodeDialogCompEnt ent = (DefaultMetaNodeDialogCompEnt)o;
        return Objects.equals(m_paramName, ent.m_paramName) && Objects.equals(m_nodeID, ent.m_nodeID) && Objects.equals(m_isHideInDialog, ent.m_isHideInDialog) && Objects.equals(m_representation, ent.m_representation);
    }


  @Override
  public String getParamName() {
        return m_paramName;
    }
    
  @Override
  public org.knime.gateway.api.entity.NodeIDEnt getNodeID() {
        return m_nodeID;
    }
    
  @Override
  public Boolean isIsHideInDialog() {
        return m_isHideInDialog;
    }
    
  @Override
  public JavaObjectEnt getRepresentation() {
        return m_representation;
    }
    
  
    public static class DefaultMetaNodeDialogCompEntBuilder implements MetaNodeDialogCompEntBuilder {
    
        public DefaultMetaNodeDialogCompEntBuilder(){
            
        }
    
        private String m_paramName;
        private org.knime.gateway.api.entity.NodeIDEnt m_nodeID;
        private Boolean m_isHideInDialog;
        private JavaObjectEnt m_representation;

        @Override
        public DefaultMetaNodeDialogCompEntBuilder setParamName(String paramName) {
             m_paramName = paramName;
             return this;
        }

        @Override
        public DefaultMetaNodeDialogCompEntBuilder setNodeID(org.knime.gateway.api.entity.NodeIDEnt nodeID) {
             m_nodeID = nodeID;
             return this;
        }

        @Override
        public DefaultMetaNodeDialogCompEntBuilder setIsHideInDialog(Boolean isHideInDialog) {
             m_isHideInDialog = isHideInDialog;
             return this;
        }

        @Override
        public DefaultMetaNodeDialogCompEntBuilder setRepresentation(JavaObjectEnt representation) {
             m_representation = representation;
             return this;
        }

        
        @Override
        public DefaultMetaNodeDialogCompEnt build() {
            return new DefaultMetaNodeDialogCompEnt(this);
        }
    
    }

}
