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

import static com.knime.gateway.util.DefaultEntUtil.immutable;

import java.util.Objects;

import com.knime.gateway.v0.entity.MetaNodeDialogComp_configEnt;
import com.knime.gateway.v0.entity.MetaNodeDialogComp_representationEnt;
import com.knime.gateway.v0.entity.MetaNodeDialogComp_valueEnt;

import com.knime.gateway.v0.entity.MetaNodeDialogCompEnt;

/**
 * A component that is part of a metanode dialog.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public class DefaultMetaNodeDialogCompEnt  implements MetaNodeDialogCompEnt {

  protected String m_nodeID;
  protected Boolean m_isHideInDialog;
  protected MetaNodeDialogComp_representationEnt m_representation;
  protected MetaNodeDialogComp_valueEnt m_value;
  protected MetaNodeDialogComp_configEnt m_config;
  
  protected DefaultMetaNodeDialogCompEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "MetaNodeDialogComp";
  }
  
  private DefaultMetaNodeDialogCompEnt(DefaultMetaNodeDialogCompEntBuilder builder) {
    
    m_nodeID = immutable(builder.m_nodeID);
    m_isHideInDialog = immutable(builder.m_isHideInDialog);
    m_representation = immutable(builder.m_representation);
    m_value = immutable(builder.m_value);
    m_config = immutable(builder.m_config);
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
        return Objects.equals(m_nodeID, ent.m_nodeID) && Objects.equals(m_isHideInDialog, ent.m_isHideInDialog) && Objects.equals(m_representation, ent.m_representation) && Objects.equals(m_value, ent.m_value) && Objects.equals(m_config, ent.m_config);
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
  public MetaNodeDialogComp_representationEnt getRepresentation() {
        return m_representation;
    }
    
  @Override
  public MetaNodeDialogComp_valueEnt getValue() {
        return m_value;
    }
    
  @Override
  public MetaNodeDialogComp_configEnt getConfig() {
        return m_config;
    }
    
  
    public static class DefaultMetaNodeDialogCompEntBuilder implements MetaNodeDialogCompEntBuilder {
    
        public DefaultMetaNodeDialogCompEntBuilder(){
            
        }
    
        private String m_nodeID = null;
        private Boolean m_isHideInDialog = null;
        private MetaNodeDialogComp_representationEnt m_representation;
        private MetaNodeDialogComp_valueEnt m_value;
        private MetaNodeDialogComp_configEnt m_config;

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
        public DefaultMetaNodeDialogCompEntBuilder setRepresentation(MetaNodeDialogComp_representationEnt representation) {
             m_representation = representation;
             return this;
        }

        @Override
        public DefaultMetaNodeDialogCompEntBuilder setValue(MetaNodeDialogComp_valueEnt value) {
             m_value = value;
             return this;
        }

        @Override
        public DefaultMetaNodeDialogCompEntBuilder setConfig(MetaNodeDialogComp_configEnt config) {
             m_config = config;
             return this;
        }

        
        @Override
        public DefaultMetaNodeDialogCompEnt build() {
            return new DefaultMetaNodeDialogCompEnt(this);
        }
    
    }

}
