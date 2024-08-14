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

import org.knime.gateway.api.webui.entity.NodeFactoryKeyEnt;
import org.knime.gateway.api.webui.entity.SpaceItemReferenceEnt;
import org.knime.gateway.api.webui.entity.WorkflowCommandEnt;
import org.knime.gateway.api.webui.entity.XYEnt;

import java.util.function.BiConsumer;

import org.knime.core.util.Pair;

import org.knime.gateway.api.entity.GatewayEntityBuilder;


import org.knime.gateway.api.entity.GatewayEntity;

/**
 * Adds a new node to the workflow.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
public interface AddNodeCommandEnt extends GatewayEntity, WorkflowCommandEnt {

  /**
   * Optional parameter that describe the relation of the new node with the given node,  either a Successor or a predecessor of the given node
   */
  public enum NodeRelationEnum {
    PREDECESSORS("PREDECESSORS"),
    
    SUCCESSORS("SUCCESSORS");

    private String value;

    NodeRelationEnum(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

  }


  /**
   * Get position
   * @return position , never <code>null</code>
   **/
  public XYEnt getPosition();

  /**
   * Get nodeFactory
   * @return nodeFactory 
   **/
  public NodeFactoryKeyEnt getNodeFactory();

  /**
   * a url to a file which then determines what node to add
   * @return url 
   **/
  public String getUrl();

  /**
   * Get spaceItemReference
   * @return spaceItemReference 
   **/
  public SpaceItemReferenceEnt getSpaceItemReference();

  /**
   * Optional parameter identifying the existing node to connect to
   * @return sourceNodeId 
   **/
  public org.knime.gateway.api.entity.NodeIDEnt getSourceNodeId();

  /**
   * Optional parameter identifying the port index of the existing node to connect to. This will be determined automatically if only a source node id is provided.
   * @return sourcePortIdx 
   **/
  public Integer getSourcePortIdx();

  /**
   * Optional parameter that describe the relation of the new node with the given node,  either a Successor or a predecessor of the given node
   * @return nodeRelation 
   **/
  public NodeRelationEnum getNodeRelation();


  @Override
  default void forEachPropertyValue(final GatewayEntity other,
      final BiConsumer<String, Pair<Object, Object>> valueConsumer) {
      var e = (AddNodeCommandEnt)other;
      valueConsumer.accept("kind", Pair.create(getKind(), e.getKind()));
      valueConsumer.accept("position", Pair.create(getPosition(), e.getPosition()));
      valueConsumer.accept("nodeFactory", Pair.create(getNodeFactory(), e.getNodeFactory()));
      valueConsumer.accept("url", Pair.create(getUrl(), e.getUrl()));
      valueConsumer.accept("spaceItemReference", Pair.create(getSpaceItemReference(), e.getSpaceItemReference()));
      valueConsumer.accept("sourceNodeId", Pair.create(getSourceNodeId(), e.getSourceNodeId()));
      valueConsumer.accept("sourcePortIdx", Pair.create(getSourcePortIdx(), e.getSourcePortIdx()));
      valueConsumer.accept("nodeRelation", Pair.create(getNodeRelation(), e.getNodeRelation()));
  }

    /**
     * The builder for the entity.
     */
    public interface AddNodeCommandEntBuilder extends GatewayEntityBuilder<AddNodeCommandEnt> {

        /**
         * The kind of command which directly maps to a specific &#39;implementation&#39;.
         * 
         * @param kind the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        AddNodeCommandEntBuilder setKind(KindEnum kind);
        
        /**
   		 * Set position
         * 
         * @param position the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        AddNodeCommandEntBuilder setPosition(XYEnt position);
        
        /**
   		 * Set nodeFactory
         * 
         * @param nodeFactory the property value,  
         * @return this entity builder for chaining
         */
        AddNodeCommandEntBuilder setNodeFactory(NodeFactoryKeyEnt nodeFactory);
        
        /**
         * a url to a file which then determines what node to add
         * 
         * @param url the property value,  
         * @return this entity builder for chaining
         */
        AddNodeCommandEntBuilder setUrl(String url);
        
        /**
   		 * Set spaceItemReference
         * 
         * @param spaceItemReference the property value,  
         * @return this entity builder for chaining
         */
        AddNodeCommandEntBuilder setSpaceItemReference(SpaceItemReferenceEnt spaceItemReference);
        
        /**
         * Optional parameter identifying the existing node to connect to
         * 
         * @param sourceNodeId the property value,  
         * @return this entity builder for chaining
         */
        AddNodeCommandEntBuilder setSourceNodeId(org.knime.gateway.api.entity.NodeIDEnt sourceNodeId);
        
        /**
         * Optional parameter identifying the port index of the existing node to connect to. This will be determined automatically if only a source node id is provided.
         * 
         * @param sourcePortIdx the property value,  
         * @return this entity builder for chaining
         */
        AddNodeCommandEntBuilder setSourcePortIdx(Integer sourcePortIdx);
        
        /**
         * Optional parameter that describe the relation of the new node with the given node,  either a Successor or a predecessor of the given node
         * 
         * @param nodeRelation the property value,  
         * @return this entity builder for chaining
         */
        AddNodeCommandEntBuilder setNodeRelation(NodeRelationEnum nodeRelation);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        AddNodeCommandEnt build();
    
    }

}
