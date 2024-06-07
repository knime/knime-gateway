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

import org.knime.gateway.api.webui.entity.EventEnt;

import java.util.function.BiConsumer;

import org.knime.core.util.Pair;

import org.knime.gateway.api.entity.GatewayEntityBuilder;


import org.knime.gateway.api.entity.GatewayEntity;

/**
 * A selection (aka hiliting) event.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
public interface SelectionEventEnt extends GatewayEntity, EventEnt {

  /**
   * selection mode
   */
  public enum ModeEnum {
    ADD("ADD"),
    
    REMOVE("REMOVE"),
    
    REPLACE("REPLACE");

    private String value;

    ModeEnum(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

  }


  /**
   * The project emitting the event.
   * @return projectId , never <code>null</code>
   **/
  public String getProjectId();

  /**
   * The (sub-)workflow emitting the event.
   * @return workflowId , never <code>null</code>
   **/
  public org.knime.gateway.api.entity.NodeIDEnt getWorkflowId();

  /**
   * The node emitting the event.
   * @return nodeId , never <code>null</code>
   **/
  public org.knime.gateway.api.entity.NodeIDEnt getNodeId();

  /**
   * The port emitting the event (in case of a port view).
   * @return portIndex 
   **/
  public Integer getPortIndex();

  /**
   * selection mode
   * @return mode , never <code>null</code>
   **/
  public ModeEnum getMode();

  /**
   * representation of the actual selection (e.g. a list of row keys)
   * @return selection 
   **/
  public java.util.List<String> getSelection();

  /**
   * an error message if the selection event couldn&#39;t be created
   * @return error 
   **/
  public String getError();


  @Override
  default void forEachPropertyValue(final GatewayEntity other,
      final BiConsumer<String, Pair<Object, Object>> valueConsumer) {
      var e = (SelectionEventEnt)other;
      valueConsumer.accept("projectId", Pair.create(getProjectId(), e.getProjectId()));
      valueConsumer.accept("workflowId", Pair.create(getWorkflowId(), e.getWorkflowId()));
      valueConsumer.accept("nodeId", Pair.create(getNodeId(), e.getNodeId()));
      valueConsumer.accept("portIndex", Pair.create(getPortIndex(), e.getPortIndex()));
      valueConsumer.accept("mode", Pair.create(getMode(), e.getMode()));
      valueConsumer.accept("selection", Pair.create(getSelection(), e.getSelection()));
      valueConsumer.accept("error", Pair.create(getError(), e.getError()));
  }

    /**
     * The builder for the entity.
     */
    public interface SelectionEventEntBuilder extends GatewayEntityBuilder<SelectionEventEnt> {

        /**
         * The project emitting the event.
         * 
         * @param projectId the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        SelectionEventEntBuilder setProjectId(String projectId);
        
        /**
         * The (sub-)workflow emitting the event.
         * 
         * @param workflowId the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        SelectionEventEntBuilder setWorkflowId(org.knime.gateway.api.entity.NodeIDEnt workflowId);
        
        /**
         * The node emitting the event.
         * 
         * @param nodeId the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        SelectionEventEntBuilder setNodeId(org.knime.gateway.api.entity.NodeIDEnt nodeId);
        
        /**
         * The port emitting the event (in case of a port view).
         * 
         * @param portIndex the property value,  
         * @return this entity builder for chaining
         */
        SelectionEventEntBuilder setPortIndex(Integer portIndex);
        
        /**
         * selection mode
         * 
         * @param mode the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        SelectionEventEntBuilder setMode(ModeEnum mode);
        
        /**
         * representation of the actual selection (e.g. a list of row keys)
         * 
         * @param selection the property value,  
         * @return this entity builder for chaining
         */
        SelectionEventEntBuilder setSelection(java.util.List<String> selection);
        
        /**
         * an error message if the selection event couldn&#39;t be created
         * 
         * @param error the property value,  
         * @return this entity builder for chaining
         */
        SelectionEventEntBuilder setError(String error);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        SelectionEventEnt build();
    
    }

}
