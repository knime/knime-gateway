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

import org.knime.gateway.api.webui.entity.KaiMessageEnt;
import org.knime.gateway.api.webui.entity.XYEnt;

import java.util.function.BiConsumer;

import org.knime.core.util.Pair;

import org.knime.gateway.api.entity.GatewayEntityBuilder;


import org.knime.gateway.api.entity.GatewayEntity;

/**
 * Encapsulates a request to K-AI which contains the entire conversation  as well as information on the open workflow, subworkflow and selected nodes. 
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
public interface KaiRequestEnt extends GatewayEntity {


  /**
   * The conversationId is assigned by the service and allows to correlate requests. Null for the first request of a conversation. 
   * @return conversationId 
   **/
  public String getConversationId();

  /**
   * Identifies the top-level workflow.
   * @return projectId , never <code>null</code>
   **/
  public String getProjectId();

  /**
   * ID of the subworkflow the user is in.
   * @return workflowId , never <code>null</code>
   **/
  public String getWorkflowId();

  /**
   * The IDs of the selected nodes.
   * @return selectedNodes , never <code>null</code>
   **/
  public java.util.List<String> getSelectedNodes();

  /**
   * Get startPosition
   * @return startPosition 
   **/
  public XYEnt getStartPosition();

  /**
   * Get messages
   * @return messages , never <code>null</code>
   **/
  public java.util.List<KaiMessageEnt> getMessages();


  @Override
  default void forEachPropertyValue(final GatewayEntity other,
      final BiConsumer<String, Pair<Object, Object>> valueConsumer) {
      var e = (KaiRequestEnt)other;
      valueConsumer.accept("conversationId", Pair.create(getConversationId(), e.getConversationId()));
      valueConsumer.accept("projectId", Pair.create(getProjectId(), e.getProjectId()));
      valueConsumer.accept("workflowId", Pair.create(getWorkflowId(), e.getWorkflowId()));
      valueConsumer.accept("selectedNodes", Pair.create(getSelectedNodes(), e.getSelectedNodes()));
      valueConsumer.accept("startPosition", Pair.create(getStartPosition(), e.getStartPosition()));
      valueConsumer.accept("messages", Pair.create(getMessages(), e.getMessages()));
  }

    /**
     * The builder for the entity.
     */
    public interface KaiRequestEntBuilder extends GatewayEntityBuilder<KaiRequestEnt> {

        /**
         * The conversationId is assigned by the service and allows to correlate requests. Null for the first request of a conversation. 
         * 
         * @param conversationId the property value,  
         * @return this entity builder for chaining
         */
        KaiRequestEntBuilder setConversationId(String conversationId);
        
        /**
         * Identifies the top-level workflow.
         * 
         * @param projectId the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        KaiRequestEntBuilder setProjectId(String projectId);
        
        /**
         * ID of the subworkflow the user is in.
         * 
         * @param workflowId the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        KaiRequestEntBuilder setWorkflowId(String workflowId);
        
        /**
         * The IDs of the selected nodes.
         * 
         * @param selectedNodes the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        KaiRequestEntBuilder setSelectedNodes(java.util.List<String> selectedNodes);
        
        /**
   		 * Set startPosition
         * 
         * @param startPosition the property value,  
         * @return this entity builder for chaining
         */
        KaiRequestEntBuilder setStartPosition(XYEnt startPosition);
        
        /**
   		 * Set messages
         * 
         * @param messages the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        KaiRequestEntBuilder setMessages(java.util.List<KaiMessageEnt> messages);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        KaiRequestEnt build();
    
    }

}
