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
package org.knime.gateway.api.webui.entity;


import org.knime.gateway.api.entity.GatewayEntityBuilder;


import org.knime.gateway.api.entity.GatewayEntity;

/**
 * Properties that remain the same no matter to what node a port belongs.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", ""})
public interface NodePortInvariantsEnt extends GatewayEntity {

  /**
   * The port type.
   */
  public enum TypeEnum {
    TABLE("table"),
    
    FLOWVARIABLE("flowVariable"),
    
    GENERIC("generic"),
    
    OTHER("other");

    private String value;

    TypeEnum(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

  }


  /**
   * The port type.
   * @return type , never <code>null</code>
   **/
  public TypeEnum getType();

  /**
   * A unique port type id if it&#39;s a port of type &#39;other&#39;. Only present if interaction info is supposed to be included.
   * @return otherTypeId 
   **/
  public Integer getOtherTypeId();

  /**
   * The color of the port in case of type &#39;other&#39;.
   * @return color 
   **/
  public String getColor();


    /**
     * The builder for the entity.
     */
    public interface NodePortInvariantsEntBuilder extends GatewayEntityBuilder<NodePortInvariantsEnt> {

        /**
         * The port type.
         * 
         * @param type the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        NodePortInvariantsEntBuilder setType(TypeEnum type);
        
        /**
         * A unique port type id if it&#39;s a port of type &#39;other&#39;. Only present if interaction info is supposed to be included.
         * 
         * @param otherTypeId the property value,  
         * @return this entity builder for chaining
         */
        NodePortInvariantsEntBuilder setOtherTypeId(Integer otherTypeId);
        
        /**
         * The color of the port in case of type &#39;other&#39;.
         * 
         * @param color the property value,  
         * @return this entity builder for chaining
         */
        NodePortInvariantsEntBuilder setColor(String color);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        NodePortInvariantsEnt build();
    
    }

}