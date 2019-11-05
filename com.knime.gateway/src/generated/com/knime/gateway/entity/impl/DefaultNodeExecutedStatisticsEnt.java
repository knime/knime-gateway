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

import java.math.BigDecimal;

import com.knime.gateway.entity.NodeExecutedStatisticsEnt;

/**
 * Details and statistics on a node already executed.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"org.knime.gateway.codegen.GatewayCodegen", "src-gen/com.knime.gateway-implementations-config.json"})
public class DefaultNodeExecutedStatisticsEnt  implements NodeExecutedStatisticsEnt {

  protected String m_name;
  protected String m_annotation;
  protected String m_nodeID;
  protected BigDecimal m_executionDuration;
  protected BigDecimal m_runs;
  
  protected DefaultNodeExecutedStatisticsEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "NodeExecutedStatistics";
  }
  
  private DefaultNodeExecutedStatisticsEnt(DefaultNodeExecutedStatisticsEntBuilder builder) {
    
    m_name = immutable(builder.m_name);
    m_annotation = immutable(builder.m_annotation);
    m_nodeID = immutable(builder.m_nodeID);
    m_executionDuration = immutable(builder.m_executionDuration);
    m_runs = immutable(builder.m_runs);
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
        DefaultNodeExecutedStatisticsEnt ent = (DefaultNodeExecutedStatisticsEnt)o;
        return Objects.equals(m_name, ent.m_name) && Objects.equals(m_annotation, ent.m_annotation) && Objects.equals(m_nodeID, ent.m_nodeID) && Objects.equals(m_executionDuration, ent.m_executionDuration) && Objects.equals(m_runs, ent.m_runs);
    }


  @Override
  public String getName() {
        return m_name;
    }
    
  @Override
  public String getAnnotation() {
        return m_annotation;
    }
    
  @Override
  public String getNodeID() {
        return m_nodeID;
    }
    
  @Override
  public BigDecimal getExecutionDuration() {
        return m_executionDuration;
    }
    
  @Override
  public BigDecimal getRuns() {
        return m_runs;
    }
    
  
    public static class DefaultNodeExecutedStatisticsEntBuilder implements NodeExecutedStatisticsEntBuilder {
    
        public DefaultNodeExecutedStatisticsEntBuilder(){
            
        }
    
        private String m_name;
        private String m_annotation;
        private String m_nodeID;
        private BigDecimal m_executionDuration;
        private BigDecimal m_runs;

        @Override
        public DefaultNodeExecutedStatisticsEntBuilder setName(String name) {
             m_name = name;
             return this;
        }

        @Override
        public DefaultNodeExecutedStatisticsEntBuilder setAnnotation(String annotation) {
             m_annotation = annotation;
             return this;
        }

        @Override
        public DefaultNodeExecutedStatisticsEntBuilder setNodeID(String nodeID) {
             m_nodeID = nodeID;
             return this;
        }

        @Override
        public DefaultNodeExecutedStatisticsEntBuilder setExecutionDuration(BigDecimal executionDuration) {
             m_executionDuration = executionDuration;
             return this;
        }

        @Override
        public DefaultNodeExecutedStatisticsEntBuilder setRuns(BigDecimal runs) {
             m_runs = runs;
             return this;
        }

        
        @Override
        public DefaultNodeExecutedStatisticsEnt build() {
            return new DefaultNodeExecutedStatisticsEnt(this);
        }
    
    }

}
