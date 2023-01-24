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

import org.knime.gateway.api.webui.entity.ExampleProjectEnt;
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

  protected java.util.List<WorkflowProjectEnt> m_openProjects;
  protected java.util.List<ExampleProjectEnt> m_exampleProjects;
  protected java.util.Map<String, PortTypeEnt> m_availablePortTypes;
  protected java.util.List<String> m_suggestedPortTypeIds;
  protected Boolean m_hasNodeRecommendationsEnabled;
  protected java.util.Map<String, Object> m_featureFlags;
  protected Boolean m_nodeRepoFilterEnabled;
  
  protected DefaultAppStateEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "AppState";
  }
  
  private DefaultAppStateEnt(DefaultAppStateEntBuilder builder) {
    
    m_openProjects = immutable(builder.m_openProjects);
    m_exampleProjects = immutable(builder.m_exampleProjects);
    m_availablePortTypes = immutable(builder.m_availablePortTypes);
    m_suggestedPortTypeIds = immutable(builder.m_suggestedPortTypeIds);
    m_hasNodeRecommendationsEnabled = immutable(builder.m_hasNodeRecommendationsEnabled);
    m_featureFlags = immutable(builder.m_featureFlags);
    m_nodeRepoFilterEnabled = immutable(builder.m_nodeRepoFilterEnabled);
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
        return Objects.equals(m_openProjects, ent.m_openProjects) && Objects.equals(m_exampleProjects, ent.m_exampleProjects) && Objects.equals(m_availablePortTypes, ent.m_availablePortTypes) && Objects.equals(m_suggestedPortTypeIds, ent.m_suggestedPortTypeIds) && Objects.equals(m_hasNodeRecommendationsEnabled, ent.m_hasNodeRecommendationsEnabled) && Objects.equals(m_featureFlags, ent.m_featureFlags) && Objects.equals(m_nodeRepoFilterEnabled, ent.m_nodeRepoFilterEnabled);
    }


  
   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
       return new HashCodeBuilder()
               .append(m_openProjects)
               .append(m_exampleProjects)
               .append(m_availablePortTypes)
               .append(m_suggestedPortTypeIds)
               .append(m_hasNodeRecommendationsEnabled)
               .append(m_featureFlags)
               .append(m_nodeRepoFilterEnabled)
               .toHashCode();
   }
  
	
	
  @Override
  public java.util.List<WorkflowProjectEnt> getOpenProjects() {
        return m_openProjects;
  }
    
  @Override
  public java.util.List<ExampleProjectEnt> getExampleProjects() {
        return m_exampleProjects;
  }
    
  @Override
  public java.util.Map<String, PortTypeEnt> getAvailablePortTypes() {
        return m_availablePortTypes;
  }
    
  @Override
  public java.util.List<String> getSuggestedPortTypeIds() {
        return m_suggestedPortTypeIds;
  }
    
  @Override
  public Boolean hasNodeRecommendationsEnabled() {
        return m_hasNodeRecommendationsEnabled;
  }
    
  @Override
  public java.util.Map<String, Object> getFeatureFlags() {
        return m_featureFlags;
  }
    
  @Override
  public Boolean isNodeRepoFilterEnabled() {
        return m_nodeRepoFilterEnabled;
  }
    
  
    public static class DefaultAppStateEntBuilder implements AppStateEntBuilder {
    
        public DefaultAppStateEntBuilder(){
            
        }
    
        private java.util.List<WorkflowProjectEnt> m_openProjects;
        private java.util.List<ExampleProjectEnt> m_exampleProjects;
        private java.util.Map<String, PortTypeEnt> m_availablePortTypes;
        private java.util.List<String> m_suggestedPortTypeIds;
        private Boolean m_hasNodeRecommendationsEnabled;
        private java.util.Map<String, Object> m_featureFlags;
        private Boolean m_nodeRepoFilterEnabled;

        @Override
        public DefaultAppStateEntBuilder setOpenProjects(java.util.List<WorkflowProjectEnt> openProjects) {
             m_openProjects = openProjects;
             return this;
        }

        @Override
        public DefaultAppStateEntBuilder setExampleProjects(java.util.List<ExampleProjectEnt> exampleProjects) {
             m_exampleProjects = exampleProjects;
             return this;
        }

        @Override
        public DefaultAppStateEntBuilder setAvailablePortTypes(java.util.Map<String, PortTypeEnt> availablePortTypes) {
             m_availablePortTypes = availablePortTypes;
             return this;
        }

        @Override
        public DefaultAppStateEntBuilder setSuggestedPortTypeIds(java.util.List<String> suggestedPortTypeIds) {
             m_suggestedPortTypeIds = suggestedPortTypeIds;
             return this;
        }

        @Override
        public DefaultAppStateEntBuilder setHasNodeRecommendationsEnabled(Boolean hasNodeRecommendationsEnabled) {
             m_hasNodeRecommendationsEnabled = hasNodeRecommendationsEnabled;
             return this;
        }

        @Override
        public DefaultAppStateEntBuilder setFeatureFlags(java.util.Map<String, Object> featureFlags) {
             m_featureFlags = featureFlags;
             return this;
        }

        @Override
        public DefaultAppStateEntBuilder setNodeRepoFilterEnabled(Boolean nodeRepoFilterEnabled) {
             m_nodeRepoFilterEnabled = nodeRepoFilterEnabled;
             return this;
        }

        
        @Override
        public DefaultAppStateEnt build() {
            return new DefaultAppStateEnt(this);
        }
    
    }

}
