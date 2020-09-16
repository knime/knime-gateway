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

import java.math.BigDecimal;

import org.knime.gateway.api.entity.GatewayEntityBuilder;


import org.knime.gateway.api.entity.GatewayEntity;

/**
 * Encapsulates properties around a node&#39;s execution state.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
public interface NodeStateEnt extends GatewayEntity {

  /**
   * Different execution states of a node. It is not given, if the node is inactive.
   */
  public enum ExecutionStateEnum {
    IDLE("IDLE"),
    
    CONFIGURED("CONFIGURED"),
    
    EXECUTED("EXECUTED"),
    
    EXECUTING("EXECUTING"),
    
    QUEUED("QUEUED"),
    
    HALTED("HALTED");

    private String value;

    ExecutionStateEnum(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

  }


  /**
   * Different execution states of a node. It is not given, if the node is inactive.
   * @return executionState 
   **/
  public ExecutionStateEnum getExecutionState();

  /**
   * Get progress
   * @return progress 
   **/
  public BigDecimal getProgress();

  /**
   * Get progressMessage
   * @return progressMessage 
   **/
  public String getProgressMessage();

  /**
   * Get error
   * @return error 
   **/
  public String getError();

  /**
   * Get warning
   * @return warning 
   **/
  public String getWarning();


    /**
     * The builder for the entity.
     */
    public interface NodeStateEntBuilder extends GatewayEntityBuilder<NodeStateEnt> {

        /**
         * Different execution states of a node. It is not given, if the node is inactive.
         * 
         * @param executionState the property value,  
         * @return this entity builder for chaining
         */
        NodeStateEntBuilder setExecutionState(ExecutionStateEnum executionState);
        
        /**
   		 * Set progress
         * 
         * @param progress the property value,  
         * @return this entity builder for chaining
         */
        NodeStateEntBuilder setProgress(BigDecimal progress);
        
        /**
   		 * Set progressMessage
         * 
         * @param progressMessage the property value,  
         * @return this entity builder for chaining
         */
        NodeStateEntBuilder setProgressMessage(String progressMessage);
        
        /**
   		 * Set error
         * 
         * @param error the property value,  
         * @return this entity builder for chaining
         */
        NodeStateEntBuilder setError(String error);
        
        /**
   		 * Set warning
         * 
         * @param warning the property value,  
         * @return this entity builder for chaining
         */
        NodeStateEntBuilder setWarning(String warning);
        
        
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
