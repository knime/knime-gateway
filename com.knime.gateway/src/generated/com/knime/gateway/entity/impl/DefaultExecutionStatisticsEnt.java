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
package com.knime.gateway.entity.impl;

import static com.knime.gateway.util.EntityUtil.immutable;

import java.util.Objects;

import com.knime.gateway.entity.NodeExecutedStatisticsEnt;
import com.knime.gateway.entity.NodeExecutingStatisticsEnt;
import java.math.BigDecimal;

import com.knime.gateway.entity.ExecutionStatisticsEnt;

/**
 * Statistics and progress on the workflow execution.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"org.knime.gateway.codegen.GatewayCodegen", "src-gen/com.knime.gateway-implementations-config.json"})
public class DefaultExecutionStatisticsEnt  implements ExecutionStatisticsEnt {

  protected BigDecimal m_totalExecutionDuration;
  protected java.util.List<NodeExecutedStatisticsEnt> m_nodesExecuted;
  protected java.util.List<NodeExecutingStatisticsEnt> m_nodesExecuting;
  protected WizardExecutionStateEnum m_wizardExecutionState;
  
  protected DefaultExecutionStatisticsEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "ExecutionStatistics";
  }
  
  private DefaultExecutionStatisticsEnt(DefaultExecutionStatisticsEntBuilder builder) {
    
    m_totalExecutionDuration = immutable(builder.m_totalExecutionDuration);
    m_nodesExecuted = immutable(builder.m_nodesExecuted);
    m_nodesExecuting = immutable(builder.m_nodesExecuting);
    m_wizardExecutionState = immutable(builder.m_wizardExecutionState);
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
        DefaultExecutionStatisticsEnt ent = (DefaultExecutionStatisticsEnt)o;
        return Objects.equals(m_totalExecutionDuration, ent.m_totalExecutionDuration) && Objects.equals(m_nodesExecuted, ent.m_nodesExecuted) && Objects.equals(m_nodesExecuting, ent.m_nodesExecuting) && Objects.equals(m_wizardExecutionState, ent.m_wizardExecutionState);
    }


  @Override
  public BigDecimal getTotalExecutionDuration() {
        return m_totalExecutionDuration;
    }
    
  @Override
  public java.util.List<NodeExecutedStatisticsEnt> getNodesExecuted() {
        return m_nodesExecuted;
    }
    
  @Override
  public java.util.List<NodeExecutingStatisticsEnt> getNodesExecuting() {
        return m_nodesExecuting;
    }
    
  @Override
  public WizardExecutionStateEnum getWizardExecutionState() {
        return m_wizardExecutionState;
    }
    
  
    public static class DefaultExecutionStatisticsEntBuilder implements ExecutionStatisticsEntBuilder {
    
        public DefaultExecutionStatisticsEntBuilder(){
            
        }
    
        private BigDecimal m_totalExecutionDuration;
        private java.util.List<NodeExecutedStatisticsEnt> m_nodesExecuted = new java.util.ArrayList<>();
        private java.util.List<NodeExecutingStatisticsEnt> m_nodesExecuting = new java.util.ArrayList<>();
        private WizardExecutionStateEnum m_wizardExecutionState;

        @Override
        public DefaultExecutionStatisticsEntBuilder setTotalExecutionDuration(BigDecimal totalExecutionDuration) {
             m_totalExecutionDuration = totalExecutionDuration;
             return this;
        }

        @Override
        public DefaultExecutionStatisticsEntBuilder setNodesExecuted(java.util.List<NodeExecutedStatisticsEnt> nodesExecuted) {
             m_nodesExecuted = nodesExecuted;
             return this;
        }

        @Override
        public DefaultExecutionStatisticsEntBuilder setNodesExecuting(java.util.List<NodeExecutingStatisticsEnt> nodesExecuting) {
             m_nodesExecuting = nodesExecuting;
             return this;
        }

        @Override
        public DefaultExecutionStatisticsEntBuilder setWizardExecutionState(WizardExecutionStateEnum wizardExecutionState) {
             m_wizardExecutionState = wizardExecutionState;
             return this;
        }

        
        @Override
        public DefaultExecutionStatisticsEnt build() {
            return new DefaultExecutionStatisticsEnt(this);
        }
    
    }

}