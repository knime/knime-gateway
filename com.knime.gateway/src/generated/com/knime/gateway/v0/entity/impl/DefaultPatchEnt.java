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

import com.knime.gateway.v0.entity.PatchOpEnt;

import com.knime.gateway.v0.entity.PatchEnt;

/**
 * A list of patch operations.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public class DefaultPatchEnt  implements PatchEnt {

  protected java.util.List<PatchOpEnt> m_ops;
  protected java.util.UUID m_snapshotID;
  protected String m_targetTypeID;
  
  protected DefaultPatchEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "Patch";
  }
  
  private DefaultPatchEnt(DefaultPatchEntBuilder builder) {
    
    m_ops = immutable(builder.m_ops);
    m_snapshotID = immutable(builder.m_snapshotID);
    m_targetTypeID = immutable(builder.m_targetTypeID);
  }


  @Override
  public java.util.List<PatchOpEnt> getOps() {
        return m_ops;
    }
    
  @Override
  public java.util.UUID getSnapshotID() {
        return m_snapshotID;
    }
    
  @Override
  public String getTargetTypeID() {
        return m_targetTypeID;
    }
    
  
    public static class DefaultPatchEntBuilder implements PatchEntBuilder {
    
        public DefaultPatchEntBuilder(){
            
        }
    
        private java.util.List<PatchOpEnt> m_ops = new java.util.ArrayList<>();
        private java.util.UUID m_snapshotID = null;
        private String m_targetTypeID = null;

        @Override
        public DefaultPatchEntBuilder setOps(java.util.List<PatchOpEnt> ops) {
             m_ops = ops;
             return this;
        }

        @Override
        public DefaultPatchEntBuilder setSnapshotID(java.util.UUID snapshotID) {
             m_snapshotID = snapshotID;
             return this;
        }

        @Override
        public DefaultPatchEntBuilder setTargetTypeID(String targetTypeID) {
             m_targetTypeID = targetTypeID;
             return this;
        }

        
        @Override
        public DefaultPatchEnt build() {
            return new DefaultPatchEnt(this);
        }
    
    }

}