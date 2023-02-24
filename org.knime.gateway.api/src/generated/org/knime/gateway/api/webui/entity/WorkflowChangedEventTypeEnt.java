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

import org.knime.gateway.api.webui.entity.EventTypeEnt;

import java.util.function.BiConsumer;

import org.knime.core.util.Pair;

import org.knime.gateway.api.entity.GatewayEntityBuilder;


import org.knime.gateway.api.entity.GatewayEntity;

/**
 * Event type to register for &#39;WorkflowChangedEvent&#39;s.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
public interface WorkflowChangedEventTypeEnt extends GatewayEntity, EventTypeEnt {


  /**
   * The workflow project id to get the change-events for.
   * @return projectId , never <code>null</code>
   **/
  public String getProjectId();

  /**
   * The top-level (root) or sub-workflow to get the change-events for.
   * @return workflowId , never <code>null</code>
   **/
  public org.knime.gateway.api.entity.NodeIDEnt getWorkflowId();

  /**
   * The initial snapshot id of the workflow version known to the client/ui.
   * @return snapshotId , never <code>null</code>
   **/
  public String getSnapshotId();


  @Override
  default void forEachPropertyValue(final GatewayEntity other,
      final BiConsumer<String, Pair<Object, Object>> valueConsumer) {
      var e = (WorkflowChangedEventTypeEnt)other;
      valueConsumer.accept("typeId", Pair.create(getTypeId(), e.getTypeId()));
      valueConsumer.accept("projectId", Pair.create(getProjectId(), e.getProjectId()));
      valueConsumer.accept("workflowId", Pair.create(getWorkflowId(), e.getWorkflowId()));
      valueConsumer.accept("snapshotId", Pair.create(getSnapshotId(), e.getSnapshotId()));
  }

    /**
     * The builder for the entity.
     */
    public interface WorkflowChangedEventTypeEntBuilder extends GatewayEntityBuilder<WorkflowChangedEventTypeEnt> {

        /**
         * A unique type id. Must be the name of the actual event type object (e.g. &#39;WorkflowChangedEventType&#39;)
         * 
         * @param typeId the property value,  
         * @return this entity builder for chaining
         */
        WorkflowChangedEventTypeEntBuilder setTypeId(String typeId);
        
        /**
         * The workflow project id to get the change-events for.
         * 
         * @param projectId the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        WorkflowChangedEventTypeEntBuilder setProjectId(String projectId);
        
        /**
         * The top-level (root) or sub-workflow to get the change-events for.
         * 
         * @param workflowId the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        WorkflowChangedEventTypeEntBuilder setWorkflowId(org.knime.gateway.api.entity.NodeIDEnt workflowId);
        
        /**
         * The initial snapshot id of the workflow version known to the client/ui.
         * 
         * @param snapshotId the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        WorkflowChangedEventTypeEntBuilder setSnapshotId(String snapshotId);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        WorkflowChangedEventTypeEnt build();
    
    }

}
