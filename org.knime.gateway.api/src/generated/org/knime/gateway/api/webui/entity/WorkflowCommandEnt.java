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
 * A command that is executed to change a workflow.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
public interface WorkflowCommandEnt extends GatewayEntity {

  /**
   * The kind of command which directly maps to a specific &#39;implementation&#39;.
   */
  public enum KindEnum {
    TRANSLATE("translate"),
    
    DELETE("delete"),
    
    CONNECT("connect"),
    
    AUTO_CONNECT("auto_connect"),
    
    AUTO_DISCONNECT("auto_disconnect"),
    
    ADD_NODE("add_node"),
    
    ADD_COMPONENT("add_component"),
    
    DELETE_COMPONENT_PLACEHOLDER("delete_component_placeholder"),
    
    REPLACE_NODE("replace_node"),
    
    INSERT_NODE("insert_node"),
    
    UPDATE_COMPONENT_OR_METANODE_NAME("update_component_or_metanode_name"),
    
    UPDATE_NODE_LABEL("update_node_label"),
    
    COLLAPSE("collapse"),
    
    EXPAND("expand"),
    
    ADD_PORT("add_port"),
    
    REMOVE_PORT("remove_port"),
    
    COPY("copy"),
    
    CUT("cut"),
    
    PASTE("paste"),
    
    TRANSFORM_WORKFLOW_ANNOTATION("transform_workflow_annotation"),
    
    UPDATE_WORKFLOW_ANNOTATION("update_workflow_annotation"),
    
    REORDER_WORKFLOW_ANNOTATIONS("reorder_workflow_annotations"),
    
    ADD_WORKFLOW_ANNOTATION("add_workflow_annotation"),
    
    UPDATE_PROJECT_METADATA("update_project_metadata"),
    
    UPDATE_COMPONENT_METADATA("update_component_metadata"),
    
    ADD_BENDPOINT("add_bendpoint"),
    
    UPDATE_COMPONENT_LINK_INFORMATION("update_component_link_information"),
    
    TRANSFORM_METANODE_PORTS_BAR("transform_metanode_ports_bar"),
    
    UPDATE_LINKED_COMPONENTS("update_linked_components"),
    
    ALIGN_NODES("align_nodes");

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
   * The kind of command which directly maps to a specific &#39;implementation&#39;.
   * @return kind , never <code>null</code>
   **/
  public KindEnum getKind();


  @Override
  default void forEachPropertyValue(final GatewayEntity other,
      final BiConsumer<String, Pair<Object, Object>> valueConsumer) {
      var e = (WorkflowCommandEnt)other;
      valueConsumer.accept("kind", Pair.create(getKind(), e.getKind()));
  }

    /**
     * The builder for the entity.
     */
    public interface WorkflowCommandEntBuilder extends GatewayEntityBuilder<WorkflowCommandEnt> {

        /**
         * The kind of command which directly maps to a specific &#39;implementation&#39;.
         * 
         * @param kind the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        WorkflowCommandEntBuilder setKind(KindEnum kind);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        WorkflowCommandEnt build();
    
    }

}
