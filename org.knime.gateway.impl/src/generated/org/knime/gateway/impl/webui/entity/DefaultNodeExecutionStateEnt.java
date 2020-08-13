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
