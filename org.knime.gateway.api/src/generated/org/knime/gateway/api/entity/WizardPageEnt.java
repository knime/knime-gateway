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

import org.knime.gateway.api.entity.NodeMessageEnt;

import org.knime.gateway.api.entity.GatewayEntityBuilder;


import org.knime.gateway.api.entity.GatewayEntity;

/**
 * Wizard page as returned, e.g., by the next-page and current-page endpoints. 
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/org.knime.gateway.api-config.json"})
public interface WizardPageEnt extends GatewayEntity {

  /**
   * The wizard execution state. 
   */
  public enum WizardExecutionStateEnum {
    INTERACTION_REQUIRED("INTERACTION_REQUIRED"),
    
    EXECUTING("EXECUTING"),
    
    EXECUTION_FINISHED("EXECUTION_FINISHED"),
    
    EXECUTION_FAILED("EXECUTION_FAILED"),
    
    UNDEFINED("UNDEFINED");

    private String value;

    WizardExecutionStateEnum(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

  }


  /**
   * The actual page content as provided by a component. Page content is guaranteed to be not available if wizard execution state is &#39;executing&#39;, &#39;undefined&#39; or &#39;execution_failed&#39;. 
   * @return wizardPageContent 
   **/
  public Object getWizardPageContent();

  /**
   * The wizard execution state. 
   * @return wizardExecutionState 
   **/
  public WizardExecutionStateEnum getWizardExecutionState();

  /**
   * The node (name and id) to node message map. Only available if workflow execution finished or failed.
   * @return nodeMessages 
   **/
  public java.util.Map<String, NodeMessageEnt> getNodeMessages();

  /**
   * Whether there is a previous page available or not.
   * @return hasPreviousPage 
   **/
  public Boolean hasPreviousPage();

  /**
   * Whether there is a next page available or not.
   * @return hasNextPage 
   **/
  public Boolean hasNextPage();

  /**
   * Whether the workflow provides a report at the end. The property will only be available if this is the very last page (i.e. wizard execution state is &#39;executed&#39;). 
   * @return hasReport 
   **/
  public Boolean hasReport();


    /**
     * The builder for the entity.
     */
    public interface WizardPageEntBuilder extends GatewayEntityBuilder<WizardPageEnt> {

        /**
         * The actual page content as provided by a component. Page content is guaranteed to be not available if wizard execution state is &#39;executing&#39;, &#39;undefined&#39; or &#39;execution_failed&#39;. 
         * 
         * @param wizardPageContent the property value,  
         * @return this entity builder for chaining
         */
        WizardPageEntBuilder setWizardPageContent(Object wizardPageContent);
        
        /**
         * The wizard execution state. 
         * 
         * @param wizardExecutionState the property value,  
         * @return this entity builder for chaining
         */
        WizardPageEntBuilder setWizardExecutionState(WizardExecutionStateEnum wizardExecutionState);
        
        /**
         * The node (name and id) to node message map. Only available if workflow execution finished or failed.
         * 
         * @param nodeMessages the property value,  
         * @return this entity builder for chaining
         */
        WizardPageEntBuilder setNodeMessages(java.util.Map<String, NodeMessageEnt> nodeMessages);
        
        /**
         * Whether there is a previous page available or not.
         * 
         * @param hasPreviousPage the property value,  
         * @return this entity builder for chaining
         */
        WizardPageEntBuilder setHasPreviousPage(Boolean hasPreviousPage);
        
        /**
         * Whether there is a next page available or not.
         * 
         * @param hasNextPage the property value,  
         * @return this entity builder for chaining
         */
        WizardPageEntBuilder setHasNextPage(Boolean hasNextPage);
        
        /**
         * Whether the workflow provides a report at the end. The property will only be available if this is the very last page (i.e. wizard execution state is &#39;executed&#39;). 
         * 
         * @param hasReport the property value,  
         * @return this entity builder for chaining
         */
        WizardPageEntBuilder setHasReport(Boolean hasReport);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        WizardPageEnt build();
    
    }

}
