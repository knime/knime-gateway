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
package com.knime.gateway.v0.entity;

import com.knime.gateway.v0.entity.ConnectionEnt;
import com.knime.gateway.v0.entity.MetaPortInfoEnt;
import com.knime.gateway.v0.entity.NodeEnt;
import com.knime.gateway.v0.entity.WorkflowAnnotationEnt;
import com.knime.gateway.v0.entity.WorkflowUIInfoEnt;

import com.knime.gateway.entity.GatewayEntityBuilder;


import com.knime.gateway.entity.GatewayEntity;

/**
 * The structure of a workflow.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"org.knime.gateway.codegen.GatewayCodegen", "src-gen/com.knime.gateway-interfaces-config.json"})
public interface WorkflowEnt extends GatewayEntity {


  /**
   * The node map.
   * @return nodes 
   **/
  public java.util.Map<String, NodeEnt> getNodes();

  /**
   * The list of connections.
   * @return connections 
   **/
  public java.util.Map<String, ConnectionEnt> getConnections();

  /**
   * The inputs of a metanode (if this workflow is one).
   * @return metaInPortInfos 
   **/
  public java.util.List<MetaPortInfoEnt> getMetaInPortInfos();

  /**
   * The outputs of a metanode (if this workflow is one).
   * @return metaOutPortInfos 
   **/
  public java.util.List<MetaPortInfoEnt> getMetaOutPortInfos();

  /**
   * List of all workflow annotations. TODO could be moved to an extra UI service in order to not pollute the WorkflowEnt too much and separate UI logics.
   * @return workflowAnnotations 
   **/
  public java.util.Map<String, WorkflowAnnotationEnt> getWorkflowAnnotations();

  /**
   * Get workflowUIInfo
   * @return workflowUIInfo 
   **/
  public WorkflowUIInfoEnt getWorkflowUIInfo();

  /**
   * Flag indicating whether the workflow has credentials stored.
   * @return hasCredentials 
   **/
  public Boolean hasCredentials();

  /**
   * If the workflow is executed step by step, i.e. in wizard execution mode.
   * @return inWizardExecution 
   **/
  public Boolean isInWizardExecution();


    /**
     * The builder for the entity.
     */
    public interface WorkflowEntBuilder extends GatewayEntityBuilder<WorkflowEnt> {

        /**
         * The node map.
         * 
         * @param nodes the property value,  
         * @return this entity builder for chaining
         */
        WorkflowEntBuilder setNodes(java.util.Map<String, NodeEnt> nodes);
        
        /**
         * The list of connections.
         * 
         * @param connections the property value,  
         * @return this entity builder for chaining
         */
        WorkflowEntBuilder setConnections(java.util.Map<String, ConnectionEnt> connections);
        
        /**
         * The inputs of a metanode (if this workflow is one).
         * 
         * @param metaInPortInfos the property value,  
         * @return this entity builder for chaining
         */
        WorkflowEntBuilder setMetaInPortInfos(java.util.List<MetaPortInfoEnt> metaInPortInfos);
        
        /**
         * The outputs of a metanode (if this workflow is one).
         * 
         * @param metaOutPortInfos the property value,  
         * @return this entity builder for chaining
         */
        WorkflowEntBuilder setMetaOutPortInfos(java.util.List<MetaPortInfoEnt> metaOutPortInfos);
        
        /**
         * List of all workflow annotations. TODO could be moved to an extra UI service in order to not pollute the WorkflowEnt too much and separate UI logics.
         * 
         * @param workflowAnnotations the property value,  
         * @return this entity builder for chaining
         */
        WorkflowEntBuilder setWorkflowAnnotations(java.util.Map<String, WorkflowAnnotationEnt> workflowAnnotations);
        
        /**
   		 * Set workflowUIInfo
         * 
         * @param workflowUIInfo the property value,  
         * @return this entity builder for chaining
         */
        WorkflowEntBuilder setWorkflowUIInfo(WorkflowUIInfoEnt workflowUIInfo);
        
        /**
         * Flag indicating whether the workflow has credentials stored.
         * 
         * @param hasCredentials the property value,  
         * @return this entity builder for chaining
         */
        WorkflowEntBuilder setHasCredentials(Boolean hasCredentials);
        
        /**
         * If the workflow is executed step by step, i.e. in wizard execution mode.
         * 
         * @param inWizardExecution the property value,  
         * @return this entity builder for chaining
         */
        WorkflowEntBuilder setInWizardExecution(Boolean inWizardExecution);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        WorkflowEnt build();
    
    }

}
