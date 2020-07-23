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

import org.knime.gateway.api.entity.PortTypeEnt;

import org.knime.gateway.api.entity.PortObjectSpecEnt;

/**
 * Specification of a port object.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/org.knime.gateway.impl-config.json"})
public class DefaultPortObjectSpecEnt  implements PortObjectSpecEnt {

  protected String m_className;
  protected PortTypeEnt m_portType;
  protected String m_representation;
  protected Boolean m_inactive;
  protected Boolean m_problem;
  
  protected DefaultPortObjectSpecEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "PortObjectSpec";
  }
  
  private DefaultPortObjectSpecEnt(DefaultPortObjectSpecEntBuilder builder) {
    
    if(builder.m_className == null) {
        throw new IllegalArgumentException("className must not be null.");
    }
    m_className = immutable(builder.m_className);
    if(builder.m_portType == null) {
        throw new IllegalArgumentException("portType must not be null.");
    }
    m_portType = immutable(builder.m_portType);
    m_representation = immutable(builder.m_representation);
    if(builder.m_inactive == null) {
        throw new IllegalArgumentException("inactive must not be null.");
    }
    m_inactive = immutable(builder.m_inactive);
    m_problem = immutable(builder.m_problem);
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
        DefaultPortObjectSpecEnt ent = (DefaultPortObjectSpecEnt)o;
        return Objects.equals(m_className, ent.m_className) && Objects.equals(m_portType, ent.m_portType) && Objects.equals(m_representation, ent.m_representation) && Objects.equals(m_inactive, ent.m_inactive) && Objects.equals(m_problem, ent.m_problem);
    }


  @Override
  public String getClassName() {
        return m_className;
    }
    
  @Override
  public PortTypeEnt getPortType() {
        return m_portType;
    }
    
  @Override
  public String getRepresentation() {
        return m_representation;
    }
    
  @Override
  public Boolean isInactive() {
        return m_inactive;
    }
    
  @Override
  public Boolean isProblem() {
        return m_problem;
    }
    
  
    public static class DefaultPortObjectSpecEntBuilder implements PortObjectSpecEntBuilder {
    
        public DefaultPortObjectSpecEntBuilder(){
            
        }
    
        private String m_className;
        private PortTypeEnt m_portType;
        private String m_representation;
        private Boolean m_inactive;
        private Boolean m_problem;

        @Override
        public DefaultPortObjectSpecEntBuilder setClassName(String className) {
             if(className == null) {
                 throw new IllegalArgumentException("className must not be null.");
             }
             m_className = className;
             return this;
        }

        @Override
        public DefaultPortObjectSpecEntBuilder setPortType(PortTypeEnt portType) {
             if(portType == null) {
                 throw new IllegalArgumentException("portType must not be null.");
             }
             m_portType = portType;
             return this;
        }

        @Override
        public DefaultPortObjectSpecEntBuilder setRepresentation(String representation) {
             m_representation = representation;
             return this;
        }

        @Override
        public DefaultPortObjectSpecEntBuilder setInactive(Boolean inactive) {
             if(inactive == null) {
                 throw new IllegalArgumentException("inactive must not be null.");
             }
             m_inactive = inactive;
             return this;
        }

        @Override
        public DefaultPortObjectSpecEntBuilder setProblem(Boolean problem) {
             m_problem = problem;
             return this;
        }

        
        @Override
        public DefaultPortObjectSpecEnt build() {
            return new DefaultPortObjectSpecEnt(this);
        }
    
    }

}
