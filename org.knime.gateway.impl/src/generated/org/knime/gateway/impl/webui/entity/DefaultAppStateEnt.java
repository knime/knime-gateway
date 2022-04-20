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

import org.knime.gateway.api.webui.entity.PortTypeEnt;
import org.knime.gateway.api.webui.entity.WorkflowProjectEnt;

import org.knime.gateway.api.webui.entity.AppStateEnt;

/**
 * Represents the global application state.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public class DefaultAppStateEnt implements AppStateEnt {

  protected java.util.List<WorkflowProjectEnt> m_openedWorkflows;
  protected java.util.Map<String, PortTypeEnt> m_availableOtherPortTypes;
  protected java.util.List<String> m_recommendedPortTypeIds;
  
  protected DefaultAppStateEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "AppState";
  }
  
  private DefaultAppStateEnt(DefaultAppStateEntBuilder builder) {
    
    if(builder.m_openedWorkflows == null) {
        throw new IllegalArgumentException("openedWorkflows must not be null.");
    }
    m_openedWorkflows = immutable(builder.m_openedWorkflows);
    if(builder.m_availableOtherPortTypes == null) {
        throw new IllegalArgumentException("availableOtherPortTypes must not be null.");
    }
    m_availableOtherPortTypes = immutable(builder.m_availableOtherPortTypes);
    if(builder.m_recommendedPortTypeIds == null) {
        throw new IllegalArgumentException("recommendedPortTypeIds must not be null.");
    }
    m_recommendedPortTypeIds = immutable(builder.m_recommendedPortTypeIds);
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
        DefaultAppStateEnt ent = (DefaultAppStateEnt)o;
        return Objects.equals(m_openedWorkflows, ent.m_openedWorkflows) && Objects.equals(m_availableOtherPortTypes, ent.m_availableOtherPortTypes) && Objects.equals(m_recommendedPortTypeIds, ent.m_recommendedPortTypeIds);
    }


  
   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
       return new HashCodeBuilder()
               .append(m_openedWorkflows)
               .append(m_availableOtherPortTypes)
               .append(m_recommendedPortTypeIds)
               .toHashCode();
   }
  
	
	
  @Override
  public java.util.List<WorkflowProjectEnt> getOpenedWorkflows() {
        return m_openedWorkflows;
  }
    
  @Override
  public java.util.Map<String, PortTypeEnt> getAvailableOtherPortTypes() {
        return m_availableOtherPortTypes;
  }
    
  @Override
  public java.util.List<String> getRecommendedPortTypeIds() {
        return m_recommendedPortTypeIds;
  }
    
  
    public static class DefaultAppStateEntBuilder implements AppStateEntBuilder {
    
        public DefaultAppStateEntBuilder(){
            
        }
    
        private java.util.List<WorkflowProjectEnt> m_openedWorkflows = new java.util.ArrayList<>();
        private java.util.Map<String, PortTypeEnt> m_availableOtherPortTypes = new java.util.HashMap<>();
        private java.util.List<String> m_recommendedPortTypeIds = new java.util.ArrayList<>();

        @Override
        public DefaultAppStateEntBuilder setOpenedWorkflows(java.util.List<WorkflowProjectEnt> openedWorkflows) {
             if(openedWorkflows == null) {
                 throw new IllegalArgumentException("openedWorkflows must not be null.");
             }
             m_openedWorkflows = openedWorkflows;
             return this;
        }

        @Override
        public DefaultAppStateEntBuilder setAvailableOtherPortTypes(java.util.Map<String, PortTypeEnt> availableOtherPortTypes) {
             if(availableOtherPortTypes == null) {
                 throw new IllegalArgumentException("availableOtherPortTypes must not be null.");
             }
             m_availableOtherPortTypes = availableOtherPortTypes;
             return this;
        }

        @Override
        public DefaultAppStateEntBuilder setRecommendedPortTypeIds(java.util.List<String> recommendedPortTypeIds) {
             if(recommendedPortTypeIds == null) {
                 throw new IllegalArgumentException("recommendedPortTypeIds must not be null.");
             }
             m_recommendedPortTypeIds = recommendedPortTypeIds;
             return this;
        }

        
        @Override
        public DefaultAppStateEnt build() {
            return new DefaultAppStateEnt(this);
        }
    
    }

}
