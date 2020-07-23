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

/**
 * Details and statistics on a node already executed.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/org.knime.gateway.impl-config.json"})
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
