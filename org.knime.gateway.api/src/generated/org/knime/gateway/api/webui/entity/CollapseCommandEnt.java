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

import org.knime.gateway.api.webui.entity.PartBasedCommandEnt;

import java.util.function.BiConsumer;

import org.knime.core.util.Pair;

import org.knime.gateway.api.entity.GatewayEntityBuilder;


import org.knime.gateway.api.entity.GatewayEntity;

/**
 * Resets selected nodes and collapses selected nodes and annotations into a metanode or component.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
public interface CollapseCommandEnt extends GatewayEntity, PartBasedCommandEnt {

  /**
   * Gets or Sets containerType
   */
  public enum ContainerTypeEnum {
    METANODE("metanode"),
    
    COMPONENT("component");

    private String value;

    ContainerTypeEnum(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

  }


  /**
   * Get containerType
   * @return containerType , never <code>null</code>
   **/
  public ContainerTypeEnum getContainerType();


  @Override
  default void forEachPropertyValue(final GatewayEntity other,
      final BiConsumer<String, Pair<Object, Object>> valueConsumer) {
      var e = (CollapseCommandEnt)other;
      valueConsumer.accept("kind", Pair.create(getKind(), e.getKind()));
      valueConsumer.accept("nodeIds", Pair.create(getNodeIds(), e.getNodeIds()));
      valueConsumer.accept("annotationIds", Pair.create(getAnnotationIds(), e.getAnnotationIds()));
      valueConsumer.accept("connectionBendpoints", Pair.create(getConnectionBendpoints(), e.getConnectionBendpoints()));
      valueConsumer.accept("containerType", Pair.create(getContainerType(), e.getContainerType()));
  }

    /**
     * The builder for the entity.
     */
    public interface CollapseCommandEntBuilder extends GatewayEntityBuilder<CollapseCommandEnt> {

        /**
         * The kind of command which directly maps to a specific &#39;implementation&#39;.
         * 
         * @param kind the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        CollapseCommandEntBuilder setKind(KindEnum kind);
        
        /**
         * The ids of the nodes referenced.
         * 
         * @param nodeIds the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        CollapseCommandEntBuilder setNodeIds(java.util.List<org.knime.gateway.api.entity.NodeIDEnt> nodeIds);
        
        /**
         * The ids of the workflow annotations referenced.
         * 
         * @param annotationIds the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        CollapseCommandEntBuilder setAnnotationIds(java.util.List<org.knime.gateway.api.entity.AnnotationIDEnt> annotationIds);
        
        /**
         * Map from connection ID to indices of bendpoints on that connection
         * 
         * @param connectionBendpoints the property value,  
         * @return this entity builder for chaining
         */
        CollapseCommandEntBuilder setConnectionBendpoints(java.util.Map<String, java.util.List<Integer>> connectionBendpoints);
        
        /**
   		 * Set containerType
         * 
         * @param containerType the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        CollapseCommandEntBuilder setContainerType(ContainerTypeEnum containerType);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        CollapseCommandEnt build();
    
    }

}
