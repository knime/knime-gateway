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
package com.knime.gateway.v0.entity.impl;

import static com.knime.gateway.util.EntityUtil.immutable;

import java.util.Objects;

import com.knime.gateway.v0.entity.JavaObjectEnt;

import com.knime.gateway.v0.entity.MetaNodeDialogCompEnt;

/**
 * A component that is part of a metanode dialog.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public class DefaultMetaNodeDialogCompEnt  implements MetaNodeDialogCompEnt {

  protected String m_paramName;
  protected String m_nodeID;
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
  public String getNodeID() {
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
    
        private String m_paramName = null;
        private String m_nodeID = null;
        private Boolean m_isHideInDialog = null;
        private JavaObjectEnt m_representation;

        @Override
        public DefaultMetaNodeDialogCompEntBuilder setParamName(String paramName) {
             m_paramName = paramName;
             return this;
        }

        @Override
        public DefaultMetaNodeDialogCompEntBuilder setNodeID(String nodeID) {
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