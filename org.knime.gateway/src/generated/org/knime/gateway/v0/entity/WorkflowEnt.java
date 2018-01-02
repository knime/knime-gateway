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
package org.knime.gateway.v0.entity;

import org.knime.gateway.v0.entity.ConnectionEnt;
import org.knime.gateway.v0.entity.MetaPortInfoEnt;
import org.knime.gateway.v0.entity.NodeEnt;
import org.knime.gateway.v0.entity.WorkflowAnnotationEnt;
import org.knime.gateway.v0.entity.WorkflowUIInfoEnt;

import org.knime.gateway.entity.GatewayEntityBuilder;


import org.knime.gateway.entity.GatewayEntity;

/**
 * The structure of a workflow.
 * 
 * @author Martin Horn, University of Konstanz
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen", date = "2018-01-02T16:29:35.284+01:00")
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
  public java.util.List<ConnectionEnt> getConnections();

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
   * List of all workflow annotations. TODO could be moved to an extra UI service in order to not polute the WorkflowEnt too much and separate UI logics.
   * @return workflowAnnotations 
   **/
  public java.util.List<WorkflowAnnotationEnt> getWorkflowAnnotations();

  /**
   * Additional workflow UI information such as grid settings, connection appearance etc. TODO could be moved to an extra UI service in order to not polute the WorkflowEnt too much and separate UI logics.
   * @return workflowUIInfo 
   **/
  public WorkflowUIInfoEnt getWorkflowUIInfo();


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
        WorkflowEntBuilder setConnections(java.util.List<ConnectionEnt> connections);
        
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
         * List of all workflow annotations. TODO could be moved to an extra UI service in order to not polute the WorkflowEnt too much and separate UI logics.
         * 
         * @param workflowAnnotations the property value,  
         * @return this entity builder for chaining
         */
        WorkflowEntBuilder setWorkflowAnnotations(java.util.List<WorkflowAnnotationEnt> workflowAnnotations);
        
        /**
         * Additional workflow UI information such as grid settings, connection appearance etc. TODO could be moved to an extra UI service in order to not polute the WorkflowEnt too much and separate UI logics.
         * 
         * @param workflowUIInfo the property value,  
         * @return this entity builder for chaining
         */
        WorkflowEntBuilder setWorkflowUIInfo(WorkflowUIInfoEnt workflowUIInfo);
        
        
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
