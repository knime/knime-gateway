/* ------------------------------------------------------------------
 * This source code, its documentation and all appendant files
 * are protected by copyright law. All rights reserved.
 *
 * Copyright by KNIME AG, Zurich, Switzerland
 *
 * You may not modify, publish, transmit, transfer or sell, reproduce,
 * create derivative works from, distribute, perform, display, or in
 * any way exploit any of the content, in whole or in part, except as
 * otherwise expressly permitted in writing by the copyright owner or
 * as specified in the license file distributed with this product.
 *
 * If you have any questions please contact the copyright holder:
 * website: www.knime.com
 * email: contact@knime.com
 * ---------------------------------------------------------------------
 *
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
