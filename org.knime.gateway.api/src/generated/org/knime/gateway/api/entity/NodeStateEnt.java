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


import org.knime.gateway.api.entity.GatewayEntityBuilder;


import org.knime.gateway.api.entity.GatewayEntity;

/**
 * NodeStateEnt
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/org.knime.gateway.api-config.json"})
public interface NodeStateEnt extends GatewayEntity {

  /**
   * Gets or Sets state
   */
  public enum StateEnum {
    IDLE("IDLE"),
    
    CONFIGURED("CONFIGURED"),
    
    UNCONFIGURED_MARKEDFOREXEC("UNCONFIGURED_MARKEDFOREXEC"),
    
    CONFIGURED_MARKEDFOREXEC("CONFIGURED_MARKEDFOREXEC"),
    
    EXECUTED_MARKEDFOREXEC("EXECUTED_MARKEDFOREXEC"),
    
    CONFIGURED_QUEUED("CONFIGURED_QUEUED"),
    
    EXECUTED_QUEUED("EXECUTED_QUEUED"),
    
    PREEXECUTE("PREEXECUTE"),
    
    EXECUTING("EXECUTING"),
    
    EXECUTINGREMOTELY("EXECUTINGREMOTELY"),
    
    POSTEXECUTE("POSTEXECUTE"),
    
    EXECUTED("EXECUTED");

    private String value;

    StateEnum(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

  }


  /**
   * Get state
   * @return state , never <code>null</code>
   **/
  public StateEnum getState();


    /**
     * The builder for the entity.
     */
    public interface NodeStateEntBuilder extends GatewayEntityBuilder<NodeStateEnt> {

        /**
   		 * Set state
         * 
         * @param state the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        NodeStateEntBuilder setState(StateEnum state);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        NodeStateEnt build();
    
    }

}
