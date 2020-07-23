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
