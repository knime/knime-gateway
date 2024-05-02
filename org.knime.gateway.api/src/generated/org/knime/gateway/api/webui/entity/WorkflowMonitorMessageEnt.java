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


import java.util.function.BiConsumer;

import org.knime.core.util.Pair;

import org.knime.gateway.api.entity.GatewayEntityBuilder;


import org.knime.gateway.api.entity.GatewayEntity;

/**
 * A message in the workflow monitor.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
public interface WorkflowMonitorMessageEnt extends GatewayEntity {


  /**
   * The template id of the node the message is associated with.
   * @return templateId , never <code>null</code>
   **/
  public String getTemplateId();

  /**
   * The id of the worklfow the node is contained in.
   * @return workflowId , never <code>null</code>
   **/
  public org.knime.gateway.api.entity.NodeIDEnt getWorkflowId();

  /**
   * The id of the node the message is associated with.
   * @return nodeId , never <code>null</code>
   **/
  public org.knime.gateway.api.entity.NodeIDEnt getNodeId();

  /**
   * The name of the node the message is associated with.
   * @return name , never <code>null</code>
   **/
  public String getName();

  /**
   * The actual message.
   * @return message , never <code>null</code>
   **/
  public String getMessage();


  @Override
  default void forEachPropertyValue(final GatewayEntity other,
      final BiConsumer<String, Pair<Object, Object>> valueConsumer) {
      var e = (WorkflowMonitorMessageEnt)other;
      valueConsumer.accept("templateId", Pair.create(getTemplateId(), e.getTemplateId()));
      valueConsumer.accept("workflowId", Pair.create(getWorkflowId(), e.getWorkflowId()));
      valueConsumer.accept("nodeId", Pair.create(getNodeId(), e.getNodeId()));
      valueConsumer.accept("name", Pair.create(getName(), e.getName()));
      valueConsumer.accept("message", Pair.create(getMessage(), e.getMessage()));
  }

    /**
     * The builder for the entity.
     */
    public interface WorkflowMonitorMessageEntBuilder extends GatewayEntityBuilder<WorkflowMonitorMessageEnt> {

        /**
         * The template id of the node the message is associated with.
         * 
         * @param templateId the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        WorkflowMonitorMessageEntBuilder setTemplateId(String templateId);
        
        /**
         * The id of the worklfow the node is contained in.
         * 
         * @param workflowId the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        WorkflowMonitorMessageEntBuilder setWorkflowId(org.knime.gateway.api.entity.NodeIDEnt workflowId);
        
        /**
         * The id of the node the message is associated with.
         * 
         * @param nodeId the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        WorkflowMonitorMessageEntBuilder setNodeId(org.knime.gateway.api.entity.NodeIDEnt nodeId);
        
        /**
         * The name of the node the message is associated with.
         * 
         * @param name the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        WorkflowMonitorMessageEntBuilder setName(String name);
        
        /**
         * The actual message.
         * 
         * @param message the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        WorkflowMonitorMessageEntBuilder setMessage(String message);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        WorkflowMonitorMessageEnt build();
    
    }

}
