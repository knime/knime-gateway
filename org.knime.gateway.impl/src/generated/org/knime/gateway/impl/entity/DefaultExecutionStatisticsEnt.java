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

import java.math.BigDecimal;
import org.knime.gateway.api.entity.NodeExecutedStatisticsEnt;
import org.knime.gateway.api.entity.NodeExecutingStatisticsEnt;

import org.knime.gateway.api.entity.ExecutionStatisticsEnt;

/**
 * Statistics and progress on the workflow execution.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/org.knime.gateway.impl-config.json"})
public class DefaultExecutionStatisticsEnt  implements ExecutionStatisticsEnt {

  protected BigDecimal m_totalExecutionDuration;
  protected Integer m_totalNodeExecutionsCount;
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
    m_totalNodeExecutionsCount = immutable(builder.m_totalNodeExecutionsCount);
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
        return Objects.equals(m_totalExecutionDuration, ent.m_totalExecutionDuration) && Objects.equals(m_totalNodeExecutionsCount, ent.m_totalNodeExecutionsCount) && Objects.equals(m_nodesExecuted, ent.m_nodesExecuted) && Objects.equals(m_nodesExecuting, ent.m_nodesExecuting) && Objects.equals(m_wizardExecutionState, ent.m_wizardExecutionState);
    }


  @Override
  public BigDecimal getTotalExecutionDuration() {
        return m_totalExecutionDuration;
    }
    
  @Override
  public Integer getTotalNodeExecutionsCount() {
        return m_totalNodeExecutionsCount;
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
        private Integer m_totalNodeExecutionsCount;
        private java.util.List<NodeExecutedStatisticsEnt> m_nodesExecuted = new java.util.ArrayList<>();
        private java.util.List<NodeExecutingStatisticsEnt> m_nodesExecuting = new java.util.ArrayList<>();
        private WizardExecutionStateEnum m_wizardExecutionState;

        @Override
        public DefaultExecutionStatisticsEntBuilder setTotalExecutionDuration(BigDecimal totalExecutionDuration) {
             m_totalExecutionDuration = totalExecutionDuration;
             return this;
        }

        @Override
        public DefaultExecutionStatisticsEntBuilder setTotalNodeExecutionsCount(Integer totalNodeExecutionsCount) {
             m_totalNodeExecutionsCount = totalNodeExecutionsCount;
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
