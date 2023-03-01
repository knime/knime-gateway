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

import org.knime.gateway.api.webui.entity.AllowedWorkflowActionsEnt;
import org.knime.gateway.api.webui.entity.ComponentNodeDescriptionEnt;
import org.knime.gateway.api.webui.entity.ConnectionEnt;
import org.knime.gateway.api.webui.entity.MetaPortsEnt;
import org.knime.gateway.api.webui.entity.NativeNodeInvariantsEnt;
import org.knime.gateway.api.webui.entity.ProjectMetadataEnt;
import org.knime.gateway.api.webui.entity.WorkflowAnnotationEnt;
import org.knime.gateway.api.webui.entity.WorkflowInfoEnt;

import java.util.function.BiConsumer;

import org.knime.core.util.Pair;

import org.knime.gateway.api.entity.GatewayEntityBuilder;


import org.knime.gateway.api.entity.GatewayEntity;

/**
 * The structure of a workflow.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
public interface WorkflowEnt extends GatewayEntity {


  /**
   * Get info
   * @return info , never <code>null</code>
   **/
  public WorkflowInfoEnt getInfo();

  /**
   * The node map.
   * @return nodes , never <code>null</code>
   **/
  public java.util.Map<String, NodeEnt> getNodes();

  /**
   * A map from ids to node templates.
   * @return nodeTemplates , never <code>null</code>
   **/
  public java.util.Map<String, NativeNodeInvariantsEnt> getNodeTemplates();

  /**
   * The list of connections.
   * @return connections , never <code>null</code>
   **/
  public java.util.Map<String, ConnectionEnt> getConnections();

  /**
   * List of all workflow annotations. The list order determines the z-order. Annotations at the end of the list are rendered on top.
   * @return workflowAnnotations , never <code>null</code>
   **/
  public java.util.List<WorkflowAnnotationEnt> getWorkflowAnnotations();

  /**
   * The workflow parent hierarchy. The first in the list represents the root workflow, the last the direct parent. Not available if this workflow is the root already.
   * @return parents 
   **/
  public java.util.List<WorkflowInfoEnt> getParents();

  /**
   * Get metaInPorts
   * @return metaInPorts 
   **/
  public MetaPortsEnt getMetaInPorts();

  /**
   * Get metaOutPorts
   * @return metaOutPorts 
   **/
  public MetaPortsEnt getMetaOutPorts();

  /**
   * Get allowedActions
   * @return allowedActions 
   **/
  public AllowedWorkflowActionsEnt getAllowedActions();

  /**
   * Get componentMetadata
   * @return componentMetadata 
   **/
  public ComponentNodeDescriptionEnt getComponentMetadata();

  /**
   * Get projectMetadata
   * @return projectMetadata 
   **/
  public ProjectMetadataEnt getProjectMetadata();

  /**
   * Flag indicating whether the workflow is in a dirty state, i.e. contains unsaved changes.
   * @return dirty , never <code>null</code>
   **/
  public Boolean isDirty();


  @Override
  default void forEachPropertyValue(final GatewayEntity other,
      final BiConsumer<String, Pair<Object, Object>> valueConsumer) {
      var e = (WorkflowEnt)other;
      valueConsumer.accept("info", Pair.create(getInfo(), e.getInfo()));
      valueConsumer.accept("nodes", Pair.create(getNodes(), e.getNodes()));
      valueConsumer.accept("nodeTemplates", Pair.create(getNodeTemplates(), e.getNodeTemplates()));
      valueConsumer.accept("connections", Pair.create(getConnections(), e.getConnections()));
      valueConsumer.accept("workflowAnnotations", Pair.create(getWorkflowAnnotations(), e.getWorkflowAnnotations()));
      valueConsumer.accept("parents", Pair.create(getParents(), e.getParents()));
      valueConsumer.accept("metaInPorts", Pair.create(getMetaInPorts(), e.getMetaInPorts()));
      valueConsumer.accept("metaOutPorts", Pair.create(getMetaOutPorts(), e.getMetaOutPorts()));
      valueConsumer.accept("allowedActions", Pair.create(getAllowedActions(), e.getAllowedActions()));
      valueConsumer.accept("componentMetadata", Pair.create(getComponentMetadata(), e.getComponentMetadata()));
      valueConsumer.accept("projectMetadata", Pair.create(getProjectMetadata(), e.getProjectMetadata()));
      valueConsumer.accept("dirty", Pair.create(isDirty(), e.isDirty()));
  }

    /**
     * The builder for the entity.
     */
    public interface WorkflowEntBuilder extends GatewayEntityBuilder<WorkflowEnt> {

        /**
   		 * Set info
         * 
         * @param info the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        WorkflowEntBuilder setInfo(WorkflowInfoEnt info);
        
        /**
         * The node map.
         * 
         * @param nodes the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        WorkflowEntBuilder setNodes(java.util.Map<String, NodeEnt> nodes);
        
        /**
         * A map from ids to node templates.
         * 
         * @param nodeTemplates the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        WorkflowEntBuilder setNodeTemplates(java.util.Map<String, NativeNodeInvariantsEnt> nodeTemplates);
        
        /**
         * The list of connections.
         * 
         * @param connections the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        WorkflowEntBuilder setConnections(java.util.Map<String, ConnectionEnt> connections);
        
        /**
         * List of all workflow annotations. The list order determines the z-order. Annotations at the end of the list are rendered on top.
         * 
         * @param workflowAnnotations the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        WorkflowEntBuilder setWorkflowAnnotations(java.util.List<WorkflowAnnotationEnt> workflowAnnotations);
        
        /**
         * The workflow parent hierarchy. The first in the list represents the root workflow, the last the direct parent. Not available if this workflow is the root already.
         * 
         * @param parents the property value,  
         * @return this entity builder for chaining
         */
        WorkflowEntBuilder setParents(java.util.List<WorkflowInfoEnt> parents);
        
        /**
   		 * Set metaInPorts
         * 
         * @param metaInPorts the property value,  
         * @return this entity builder for chaining
         */
        WorkflowEntBuilder setMetaInPorts(MetaPortsEnt metaInPorts);
        
        /**
   		 * Set metaOutPorts
         * 
         * @param metaOutPorts the property value,  
         * @return this entity builder for chaining
         */
        WorkflowEntBuilder setMetaOutPorts(MetaPortsEnt metaOutPorts);
        
        /**
   		 * Set allowedActions
         * 
         * @param allowedActions the property value,  
         * @return this entity builder for chaining
         */
        WorkflowEntBuilder setAllowedActions(AllowedWorkflowActionsEnt allowedActions);
        
        /**
   		 * Set componentMetadata
         * 
         * @param componentMetadata the property value,  
         * @return this entity builder for chaining
         */
        WorkflowEntBuilder setComponentMetadata(ComponentNodeDescriptionEnt componentMetadata);
        
        /**
   		 * Set projectMetadata
         * 
         * @param projectMetadata the property value,  
         * @return this entity builder for chaining
         */
        WorkflowEntBuilder setProjectMetadata(ProjectMetadataEnt projectMetadata);
        
        /**
         * Flag indicating whether the workflow is in a dirty state, i.e. contains unsaved changes.
         * 
         * @param dirty the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        WorkflowEntBuilder setDirty(Boolean dirty);
        
        
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
