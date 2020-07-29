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
package com.knime.gateway.impl.entity;

import static org.knime.gateway.api.util.EntityUtil.immutable;

import java.util.Objects;

import java.math.BigDecimal;

import org.knime.gateway.api.entity.NodeExecutingStatisticsEnt;

/**
 * Details and statistics on a node still in execution.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/java-ui/configs/com.knime.gateway.impl-config.json"})
public class DefaultNodeExecutingStatisticsEnt  implements NodeExecutingStatisticsEnt {

  protected String m_name;
  protected String m_annotation;
  protected String m_nodeID;
  protected BigDecimal m_executionDuration;
  protected BigDecimal m_progress;
  
  protected DefaultNodeExecutingStatisticsEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "NodeExecutingStatistics";
  }
  
  private DefaultNodeExecutingStatisticsEnt(DefaultNodeExecutingStatisticsEntBuilder builder) {
    
    m_name = immutable(builder.m_name);
    m_annotation = immutable(builder.m_annotation);
    m_nodeID = immutable(builder.m_nodeID);
    m_executionDuration = immutable(builder.m_executionDuration);
    m_progress = immutable(builder.m_progress);
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
        DefaultNodeExecutingStatisticsEnt ent = (DefaultNodeExecutingStatisticsEnt)o;
        return Objects.equals(m_name, ent.m_name) && Objects.equals(m_annotation, ent.m_annotation) && Objects.equals(m_nodeID, ent.m_nodeID) && Objects.equals(m_executionDuration, ent.m_executionDuration) && Objects.equals(m_progress, ent.m_progress);
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
  public BigDecimal getProgress() {
        return m_progress;
    }
    
  
    public static class DefaultNodeExecutingStatisticsEntBuilder implements NodeExecutingStatisticsEntBuilder {
    
        public DefaultNodeExecutingStatisticsEntBuilder(){
            
        }
    
        private String m_name;
        private String m_annotation;
        private String m_nodeID;
        private BigDecimal m_executionDuration;
        private BigDecimal m_progress;

        @Override
        public DefaultNodeExecutingStatisticsEntBuilder setName(String name) {
             m_name = name;
             return this;
        }

        @Override
        public DefaultNodeExecutingStatisticsEntBuilder setAnnotation(String annotation) {
             m_annotation = annotation;
             return this;
        }

        @Override
        public DefaultNodeExecutingStatisticsEntBuilder setNodeID(String nodeID) {
             m_nodeID = nodeID;
             return this;
        }

        @Override
        public DefaultNodeExecutingStatisticsEntBuilder setExecutionDuration(BigDecimal executionDuration) {
             m_executionDuration = executionDuration;
             return this;
        }

        @Override
        public DefaultNodeExecutingStatisticsEntBuilder setProgress(BigDecimal progress) {
             m_progress = progress;
             return this;
        }

        
        @Override
        public DefaultNodeExecutingStatisticsEnt build() {
            return new DefaultNodeExecutingStatisticsEnt(this);
        }
    
    }

}
