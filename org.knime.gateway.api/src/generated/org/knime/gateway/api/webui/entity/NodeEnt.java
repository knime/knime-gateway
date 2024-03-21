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

import org.knime.gateway.api.webui.entity.AllowedNodeActionsEnt;
import org.knime.gateway.api.webui.entity.NodeAnnotationEnt;
import org.knime.gateway.api.webui.entity.NodeExecutionInfoEnt;
import org.knime.gateway.api.webui.entity.NodePortEnt;
import org.knime.gateway.api.webui.entity.XYEnt;

import java.util.function.BiConsumer;

import org.knime.core.util.Pair;

import org.knime.gateway.api.entity.GatewayEntityBuilder;


import org.knime.gateway.api.entity.GatewayEntity;

/**
 * Represents a node of certain kind (native node, component, metanode) in a workflow.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
public interface NodeEnt extends GatewayEntity {

  /**
   * Whether it&#39;s a native node, component or a metanode.
   */
  public enum KindEnum {
    NODE("node"),
    
    COMPONENT("component"),
    
    METANODE("metanode");

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
   * The id of the node.
   * @return id , never <code>null</code>
   **/
  public org.knime.gateway.api.entity.NodeIDEnt getId();

  /**
   * The list of inputs.
   * @return inPorts , never <code>null</code>
   **/
  public java.util.List<? extends NodePortEnt> getInPorts();

  /**
   * The list of outputs.
   * @return outPorts , never <code>null</code>
   **/
  public java.util.List<? extends NodePortEnt> getOutPorts();

  /**
   * Get annotation
   * @return annotation 
   **/
  public NodeAnnotationEnt getAnnotation();

  /**
   * Get position
   * @return position , never <code>null</code>
   **/
  public XYEnt getPosition();

  /**
   * Whether it&#39;s a native node, component or a metanode.
   * @return kind , never <code>null</code>
   **/
  public KindEnum getKind();

  /**
   * Indicates whether this node has a dialog. Not present if the node has no dialog. Not true if only a legacy dialog is available.
   * @return hasDialog 
   **/
  public Boolean hasDialog();

  /**
   * Get allowedActions
   * @return allowedActions 
   **/
  public AllowedNodeActionsEnt getAllowedActions();

  /**
   * Get executionInfo
   * @return executionInfo 
   **/
  public NodeExecutionInfoEnt getExecutionInfo();


  @Override
  default void forEachPropertyValue(final GatewayEntity other,
      final BiConsumer<String, Pair<Object, Object>> valueConsumer) {
      var e = (NodeEnt)other;
      valueConsumer.accept("id", Pair.create(getId(), e.getId()));
      valueConsumer.accept("inPorts", Pair.create(getInPorts(), e.getInPorts()));
      valueConsumer.accept("outPorts", Pair.create(getOutPorts(), e.getOutPorts()));
      valueConsumer.accept("annotation", Pair.create(getAnnotation(), e.getAnnotation()));
      valueConsumer.accept("position", Pair.create(getPosition(), e.getPosition()));
      valueConsumer.accept("kind", Pair.create(getKind(), e.getKind()));
      valueConsumer.accept("hasDialog", Pair.create(hasDialog(), e.hasDialog()));
      valueConsumer.accept("allowedActions", Pair.create(getAllowedActions(), e.getAllowedActions()));
      valueConsumer.accept("executionInfo", Pair.create(getExecutionInfo(), e.getExecutionInfo()));
  }

    /**
     * The builder for the entity.
     */
    public interface NodeEntBuilder extends GatewayEntityBuilder<NodeEnt> {

        /**
         * The id of the node.
         * 
         * @param id the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        NodeEntBuilder setId(org.knime.gateway.api.entity.NodeIDEnt id);
        
        /**
         * The list of inputs.
         * 
         * @param inPorts the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        NodeEntBuilder setInPorts(java.util.List<? extends NodePortEnt> inPorts);
        
        /**
         * The list of outputs.
         * 
         * @param outPorts the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        NodeEntBuilder setOutPorts(java.util.List<? extends NodePortEnt> outPorts);
        
        /**
   		 * Set annotation
         * 
         * @param annotation the property value,  
         * @return this entity builder for chaining
         */
        NodeEntBuilder setAnnotation(NodeAnnotationEnt annotation);
        
        /**
   		 * Set position
         * 
         * @param position the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        NodeEntBuilder setPosition(XYEnt position);
        
        /**
         * Whether it&#39;s a native node, component or a metanode.
         * 
         * @param kind the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        NodeEntBuilder setKind(KindEnum kind);
        
        /**
         * Indicates whether this node has a dialog. Not present if the node has no dialog. Not true if only a legacy dialog is available.
         * 
         * @param hasDialog the property value,  
         * @return this entity builder for chaining
         */
        NodeEntBuilder setHasDialog(Boolean hasDialog);
        
        /**
   		 * Set allowedActions
         * 
         * @param allowedActions the property value,  
         * @return this entity builder for chaining
         */
        NodeEntBuilder setAllowedActions(AllowedNodeActionsEnt allowedActions);
        
        /**
   		 * Set executionInfo
         * 
         * @param executionInfo the property value,  
         * @return this entity builder for chaining
         */
        NodeEntBuilder setExecutionInfo(NodeExecutionInfoEnt executionInfo);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        NodeEnt build();
    
    }

}
