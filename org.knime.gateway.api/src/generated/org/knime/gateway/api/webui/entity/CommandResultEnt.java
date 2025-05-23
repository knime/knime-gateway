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
 * CommandResultEnt
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
public interface CommandResultEnt extends GatewayEntity {

  /**
   * Gets or Sets kind
   */
  public enum KindEnum {
    COLLAPSE_RESULT("collapse_result"),
    
    EXPAND_RESULT("expand_result"),
    
    CONVERT_CONTAINER_RESULT("convert_container_result"),
    
    COPY_RESULT("copy_result"),
    
    PASTE_RESULT("paste_result"),
    
    ADD_NODE_RESULT("add_node_result"),
    
    ADD_PORT_RESULT("add_port_result"),
    
    ADD_ANNOTATION_RESULT("add_annotation_result"),
    
    UPDATE_LINKED_COMPONENTS_RESULT("update_linked_components_result"),
    
    ADD_COMPONENT_PLACEHOLDER_RESULT("add_component_placeholder_result");

    private String value;

    KindEnum(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

  }


  /**
   * Workflow changes produced by this command are guaranteed to be contained in a workflow snapshot patch as emitted by &#x60;WorkflowChangedEventSource&#x60; with ID less-or-equal to this ID.
   * @return snapshotId 
   **/
  public String getSnapshotId();

  /**
   * Get kind
   * @return kind 
   **/
  public KindEnum getKind();


  @Override
  default void forEachPropertyValue(final GatewayEntity other,
      final BiConsumer<String, Pair<Object, Object>> valueConsumer) {
      var e = (CommandResultEnt)other;
      valueConsumer.accept("snapshotId", Pair.create(getSnapshotId(), e.getSnapshotId()));
      valueConsumer.accept("kind", Pair.create(getKind(), e.getKind()));
  }

    /**
     * The builder for the entity.
     */
    public interface CommandResultEntBuilder extends GatewayEntityBuilder<CommandResultEnt> {

        /**
         * Workflow changes produced by this command are guaranteed to be contained in a workflow snapshot patch as emitted by &#x60;WorkflowChangedEventSource&#x60; with ID less-or-equal to this ID.
         * 
         * @param snapshotId the property value,  
         * @return this entity builder for chaining
         */
        CommandResultEntBuilder setSnapshotId(String snapshotId);
        
        /**
   		 * Set kind
         * 
         * @param kind the property value,  
         * @return this entity builder for chaining
         */
        CommandResultEntBuilder setKind(KindEnum kind);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        CommandResultEnt build();
    
    }

}
