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


import org.knime.gateway.api.webui.entity.UpdateInfoEnt;

/**
 * Information about an available update, derived from &#x60;UpdateInfo&#x60; core class.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public class DefaultUpdateInfoEnt implements UpdateInfoEnt {

  protected String m_name;
  protected String m_shortName;
  protected Boolean m_isUpdatePossible;
  
  protected DefaultUpdateInfoEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "UpdateInfo";
  }
  
  private DefaultUpdateInfoEnt(DefaultUpdateInfoEntBuilder builder) {
    
    if(builder.m_name == null) {
        throw new IllegalArgumentException("name must not be null.");
    }
    m_name = immutable(builder.m_name);
    if(builder.m_shortName == null) {
        throw new IllegalArgumentException("shortName must not be null.");
    }
    m_shortName = immutable(builder.m_shortName);
    m_isUpdatePossible = immutable(builder.m_isUpdatePossible);
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
        DefaultUpdateInfoEnt ent = (DefaultUpdateInfoEnt)o;
        return Objects.equals(m_name, ent.m_name) && Objects.equals(m_shortName, ent.m_shortName) && Objects.equals(m_isUpdatePossible, ent.m_isUpdatePossible);
    }


  
   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
       return new HashCodeBuilder()
               .append(m_name)
               .append(m_shortName)
               .append(m_isUpdatePossible)
               .toHashCode();
   }
  
	
	
  @Override
  public String getName() {
        return m_name;
  }
    
  @Override
  public String getShortName() {
        return m_shortName;
  }
    
  @Override
  public Boolean isIsUpdatePossible() {
        return m_isUpdatePossible;
  }
    
  
    public static class DefaultUpdateInfoEntBuilder implements UpdateInfoEntBuilder {
    
        public DefaultUpdateInfoEntBuilder(){
            
        }
    
        private String m_name;
        private String m_shortName;
        private Boolean m_isUpdatePossible;

        @Override
        public DefaultUpdateInfoEntBuilder setName(String name) {
             if(name == null) {
                 throw new IllegalArgumentException("name must not be null.");
             }
             m_name = name;
             return this;
        }

        @Override
        public DefaultUpdateInfoEntBuilder setShortName(String shortName) {
             if(shortName == null) {
                 throw new IllegalArgumentException("shortName must not be null.");
             }
             m_shortName = shortName;
             return this;
        }

        @Override
        public DefaultUpdateInfoEntBuilder setIsUpdatePossible(Boolean isUpdatePossible) {
             m_isUpdatePossible = isUpdatePossible;
             return this;
        }

        
        @Override
        public DefaultUpdateInfoEnt build() {
            return new DefaultUpdateInfoEnt(this);
        }
    
    }

}