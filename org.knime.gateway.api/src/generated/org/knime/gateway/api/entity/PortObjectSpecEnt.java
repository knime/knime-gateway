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
package org.knime.gateway.api.entity;

import org.knime.gateway.api.entity.PortTypeEnt;

import org.knime.gateway.api.entity.GatewayEntityBuilder;


import org.knime.gateway.api.entity.GatewayEntity;

/**
 * Specification of a port object.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/org.knime.gateway.api-config.json"})
public interface PortObjectSpecEnt extends GatewayEntity {


  /**
   * The fully-qualified class name of the port object spec. 
   * @return className , never <code>null</code>
   **/
  public String getClassName();

  /**
   * Get portType
   * @return portType , never <code>null</code>
   **/
  public PortTypeEnt getPortType();

  /**
   * The actual port object spec representation or a description of the problem if &#39;problem&#39; is true.
   * @return representation 
   **/
  public String getRepresentation();

  /**
   * Flag indicating whether the port is inactive. If true, there will be no representation available.
   * @return inactive , never <code>null</code>
   **/
  public Boolean isInactive();

  /**
   * Flag that indicates that a problem occured while providing the port objects spec  (e.g. de-/serialization problem). The representation-field will contain more details.
   * @return problem 
   **/
  public Boolean isProblem();


    /**
     * The builder for the entity.
     */
    public interface PortObjectSpecEntBuilder extends GatewayEntityBuilder<PortObjectSpecEnt> {

        /**
         * The fully-qualified class name of the port object spec. 
         * 
         * @param className the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        PortObjectSpecEntBuilder setClassName(String className);
        
        /**
   		 * Set portType
         * 
         * @param portType the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        PortObjectSpecEntBuilder setPortType(PortTypeEnt portType);
        
        /**
         * The actual port object spec representation or a description of the problem if &#39;problem&#39; is true.
         * 
         * @param representation the property value,  
         * @return this entity builder for chaining
         */
        PortObjectSpecEntBuilder setRepresentation(String representation);
        
        /**
         * Flag indicating whether the port is inactive. If true, there will be no representation available.
         * 
         * @param inactive the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        PortObjectSpecEntBuilder setInactive(Boolean inactive);
        
        /**
         * Flag that indicates that a problem occured while providing the port objects spec  (e.g. de-/serialization problem). The representation-field will contain more details.
         * 
         * @param problem the property value,  
         * @return this entity builder for chaining
         */
        PortObjectSpecEntBuilder setProblem(Boolean problem);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        PortObjectSpecEnt build();
    
    }

}
