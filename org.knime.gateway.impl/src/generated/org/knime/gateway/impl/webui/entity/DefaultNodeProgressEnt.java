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
package org.knime.gateway.impl.webui.entity;

import static org.knime.gateway.api.util.EntityUtil.immutable;

import java.util.Objects;

import java.math.BigDecimal;

import org.knime.gateway.api.webui.entity.NodeProgressEnt;

/**
 * Represents the node&#39;s progress.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public class DefaultNodeProgressEnt  implements NodeProgressEnt {

  protected BigDecimal m_progress;
  protected String m_message;
  
  protected DefaultNodeProgressEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "NodeProgress";
  }
  
  private DefaultNodeProgressEnt(DefaultNodeProgressEntBuilder builder) {
    
    m_progress = immutable(builder.m_progress);
    m_message = immutable(builder.m_message);
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
        DefaultNodeProgressEnt ent = (DefaultNodeProgressEnt)o;
        return Objects.equals(m_progress, ent.m_progress) && Objects.equals(m_message, ent.m_message);
    }


  @Override
  public BigDecimal getProgress() {
        return m_progress;
    }
    
  @Override
  public String getMessage() {
        return m_message;
    }
    
  
    public static class DefaultNodeProgressEntBuilder implements NodeProgressEntBuilder {
    
        public DefaultNodeProgressEntBuilder(){
            
        }
    
        private BigDecimal m_progress;
        private String m_message;

        @Override
        public DefaultNodeProgressEntBuilder setProgress(BigDecimal progress) {
             m_progress = progress;
             return this;
        }

        @Override
        public DefaultNodeProgressEntBuilder setMessage(String message) {
             m_message = message;
             return this;
        }

        
        @Override
        public DefaultNodeProgressEnt build() {
            return new DefaultNodeProgressEnt(this);
        }
    
    }

}
