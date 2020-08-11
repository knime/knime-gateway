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

import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.math.BigDecimal;

import org.knime.gateway.api.webui.entity.NodeExecutionStateEnt;

/**
 * Encapsulates properties around a node&#39;s execution state.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public class DefaultNodeExecutionStateEnt  implements NodeExecutionStateEnt {

  protected StateEnum m_state;
  protected BigDecimal m_progress;
  protected String m_progressMessage;
  protected String m_error;
  protected String m_warning;
  
  protected DefaultNodeExecutionStateEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "NodeExecutionState";
  }
  
  private DefaultNodeExecutionStateEnt(DefaultNodeExecutionStateEntBuilder builder) {
    
    m_state = immutable(builder.m_state);
    m_progress = immutable(builder.m_progress);
    m_progressMessage = immutable(builder.m_progressMessage);
    m_error = immutable(builder.m_error);
    m_warning = immutable(builder.m_warning);
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
        DefaultNodeExecutionStateEnt ent = (DefaultNodeExecutionStateEnt)o;
        return Objects.equals(m_state, ent.m_state) && Objects.equals(m_progress, ent.m_progress) && Objects.equals(m_progressMessage, ent.m_progressMessage) && Objects.equals(m_error, ent.m_error) && Objects.equals(m_warning, ent.m_warning);
    }


  
   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
       return new HashCodeBuilder()
               .append(m_state)
               .append(m_progress)
               .append(m_progressMessage)
               .append(m_error)
               .append(m_warning)
               .toHashCode();
   }
  
	
	
  @Override
  public StateEnum getState() {
        return m_state;
  }
    
  @Override
  public BigDecimal getProgress() {
        return m_progress;
  }
    
  @Override
  public String getProgressMessage() {
        return m_progressMessage;
  }
    
  @Override
  public String getError() {
        return m_error;
  }
    
  @Override
  public String getWarning() {
        return m_warning;
  }
    
  
    public static class DefaultNodeExecutionStateEntBuilder implements NodeExecutionStateEntBuilder {
    
        public DefaultNodeExecutionStateEntBuilder(){
            
        }
    
        private StateEnum m_state;
        private BigDecimal m_progress;
        private String m_progressMessage;
        private String m_error;
        private String m_warning;

        @Override
        public DefaultNodeExecutionStateEntBuilder setState(StateEnum state) {
             m_state = state;
             return this;
        }

        @Override
        public DefaultNodeExecutionStateEntBuilder setProgress(BigDecimal progress) {
             m_progress = progress;
             return this;
        }

        @Override
        public DefaultNodeExecutionStateEntBuilder setProgressMessage(String progressMessage) {
             m_progressMessage = progressMessage;
             return this;
        }

        @Override
        public DefaultNodeExecutionStateEntBuilder setError(String error) {
             m_error = error;
             return this;
        }

        @Override
        public DefaultNodeExecutionStateEntBuilder setWarning(String warning) {
             m_warning = warning;
             return this;
        }

        
        @Override
        public DefaultNodeExecutionStateEnt build() {
            return new DefaultNodeExecutionStateEnt(this);
        }
    
    }

}
