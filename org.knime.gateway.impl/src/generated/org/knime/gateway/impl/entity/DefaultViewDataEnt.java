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

import org.knime.gateway.api.entity.JavaObjectEnt;

import org.knime.gateway.api.entity.ViewDataEnt;

/**
 * The data for a node&#39;s view encompasing the view representation and value.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/org.knime.gateway.impl-config.json"})
public class DefaultViewDataEnt  implements ViewDataEnt {

  protected String m_javascriptObjectID;
  protected JavaObjectEnt m_viewRepresentation;
  protected JavaObjectEnt m_viewValue;
  protected Boolean m_hideInWizard;
  
  protected DefaultViewDataEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "ViewData";
  }
  
  private DefaultViewDataEnt(DefaultViewDataEntBuilder builder) {
    
    m_javascriptObjectID = immutable(builder.m_javascriptObjectID);
    m_viewRepresentation = immutable(builder.m_viewRepresentation);
    m_viewValue = immutable(builder.m_viewValue);
    m_hideInWizard = immutable(builder.m_hideInWizard);
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
        DefaultViewDataEnt ent = (DefaultViewDataEnt)o;
        return Objects.equals(m_javascriptObjectID, ent.m_javascriptObjectID) && Objects.equals(m_viewRepresentation, ent.m_viewRepresentation) && Objects.equals(m_viewValue, ent.m_viewValue) && Objects.equals(m_hideInWizard, ent.m_hideInWizard);
    }


  @Override
  public String getJavascriptObjectID() {
        return m_javascriptObjectID;
    }
    
  @Override
  public JavaObjectEnt getViewRepresentation() {
        return m_viewRepresentation;
    }
    
  @Override
  public JavaObjectEnt getViewValue() {
        return m_viewValue;
    }
    
  @Override
  public Boolean isHideInWizard() {
        return m_hideInWizard;
    }
    
  
    public static class DefaultViewDataEntBuilder implements ViewDataEntBuilder {
    
        public DefaultViewDataEntBuilder(){
            
        }
    
        private String m_javascriptObjectID;
        private JavaObjectEnt m_viewRepresentation;
        private JavaObjectEnt m_viewValue;
        private Boolean m_hideInWizard;

        @Override
        public DefaultViewDataEntBuilder setJavascriptObjectID(String javascriptObjectID) {
             m_javascriptObjectID = javascriptObjectID;
             return this;
        }

        @Override
        public DefaultViewDataEntBuilder setViewRepresentation(JavaObjectEnt viewRepresentation) {
             m_viewRepresentation = viewRepresentation;
             return this;
        }

        @Override
        public DefaultViewDataEntBuilder setViewValue(JavaObjectEnt viewValue) {
             m_viewValue = viewValue;
             return this;
        }

        @Override
        public DefaultViewDataEntBuilder setHideInWizard(Boolean hideInWizard) {
             m_hideInWizard = hideInWizard;
             return this;
        }

        
        @Override
        public DefaultViewDataEnt build() {
            return new DefaultViewDataEnt(this);
        }
    
    }

}
