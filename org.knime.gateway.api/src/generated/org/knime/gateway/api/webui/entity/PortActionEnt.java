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
 * An action that can currently be performed on this port.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
public interface PortActionEnt extends GatewayEntity {

  /**
   * the type of allowed port action
   */
  public enum TypeEnum {
    ADD("add"),
    
    REMOVE("remove");

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
   * the type of allowed port action
   * @return type , never <code>null</code>
   **/
  public TypeEnum getType();

  /**
   * The name of the port group the port, this action refers to, is part of. Not present, e.g., if it&#39;s an action on component ports.
   * @return portGroupName 
   **/
  public String getPortGroupName();

  /**
   * The IDs of port types associated with the action. Only present for certain types of port actions, e.g., to add a port.
   * @return supportedPortTypeIds 
   **/
  public java.util.List<String> getSupportedPortTypeIds();


    /**
     * The builder for the entity.
     */
    public interface PortActionEntBuilder extends GatewayEntityBuilder<PortActionEnt> {

        /**
         * the type of allowed port action
         * 
         * @param type the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        PortActionEntBuilder setType(TypeEnum type);
        
        /**
         * The name of the port group the port, this action refers to, is part of. Not present, e.g., if it&#39;s an action on component ports.
         * 
         * @param portGroupName the property value,  
         * @return this entity builder for chaining
         */
        PortActionEntBuilder setPortGroupName(String portGroupName);
        
        /**
         * The IDs of port types associated with the action. Only present for certain types of port actions, e.g., to add a port.
         * 
         * @param supportedPortTypeIds the property value,  
         * @return this entity builder for chaining
         */
        PortActionEntBuilder setSupportedPortTypeIds(java.util.List<String> supportedPortTypeIds);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        PortActionEnt build();
    
    }

}
