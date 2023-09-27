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

import java.math.BigDecimal;
import org.knime.gateway.api.webui.entity.AllowedNodeActionsEnt;
import org.knime.gateway.api.webui.entity.ComponentNodeAndDescriptionEnt;
import org.knime.gateway.api.webui.entity.NodeAnnotationEnt;
import org.knime.gateway.api.webui.entity.NodeEnt;
import org.knime.gateway.api.webui.entity.NodeExecutionInfoEnt;
import org.knime.gateway.api.webui.entity.NodePortEnt;
import org.knime.gateway.api.webui.entity.NodeStateEnt;
import org.knime.gateway.api.webui.entity.TemplateLinkEnt;
import org.knime.gateway.api.webui.entity.XYEnt;

import java.util.function.BiConsumer;

import org.knime.core.util.Pair;

import org.knime.gateway.api.entity.GatewayEntityBuilder;


import org.knime.gateway.api.entity.GatewayEntity;

/**
 * A node wrapping (referencing) a workflow (also referred to it as component or subnode) that almost behaves as a ordinary node.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
public interface ComponentNodeEnt extends GatewayEntity, NodeEnt, ComponentNodeAndDescriptionEnt {


  /**
   * Get state
   * @return state 
   **/
  public NodeStateEnt getState();

  /**
   * Get link
   * @return link 
   **/
  public TemplateLinkEnt getLink();

  /**
   * The lock-status of this node. It has three states: absent if there is no lock at all, true if it&#39;s locked, false if it&#39;s unlocked.
   * @return isLocked 
   **/
  public Boolean isLocked();


  @Override
  default void forEachPropertyValue(final GatewayEntity other,
      final BiConsumer<String, Pair<Object, Object>> valueConsumer) {
      var e = (ComponentNodeEnt)other;
      valueConsumer.accept("id", Pair.create(getId(), e.getId()));
      valueConsumer.accept("inPorts", Pair.create(getInPorts(), e.getInPorts()));
      valueConsumer.accept("outPorts", Pair.create(getOutPorts(), e.getOutPorts()));
      valueConsumer.accept("annotation", Pair.create(getAnnotation(), e.getAnnotation()));
      valueConsumer.accept("position", Pair.create(getPosition(), e.getPosition()));
      valueConsumer.accept("kind", Pair.create(getKind(), e.getKind()));
      valueConsumer.accept("hasDialog", Pair.create(hasDialog(), e.hasDialog()));
      valueConsumer.accept("allowedActions", Pair.create(getAllowedActions(), e.getAllowedActions()));
      valueConsumer.accept("executionInfo", Pair.create(getExecutionInfo(), e.getExecutionInfo()));
      valueConsumer.accept("weight", Pair.create(getWeight(), e.getWeight()));
      valueConsumer.accept("name", Pair.create(getName(), e.getName()));
      valueConsumer.accept("type", Pair.create(getType(), e.getType()));
      valueConsumer.accept("icon", Pair.create(getIcon(), e.getIcon()));
      valueConsumer.accept("state", Pair.create(getState(), e.getState()));
      valueConsumer.accept("link", Pair.create(getLink(), e.getLink()));
      valueConsumer.accept("isLocked", Pair.create(isLocked(), e.isLocked()));
  }

    /**
     * The builder for the entity.
     */
    public interface ComponentNodeEntBuilder extends GatewayEntityBuilder<ComponentNodeEnt> {

        /**
         * The id of the node.
         * 
         * @param id the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        ComponentNodeEntBuilder setId(org.knime.gateway.api.entity.NodeIDEnt id);
        
        /**
         * The list of inputs.
         * 
         * @param inPorts the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        ComponentNodeEntBuilder setInPorts(java.util.List<? extends NodePortEnt> inPorts);
        
        /**
         * The list of outputs.
         * 
         * @param outPorts the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        ComponentNodeEntBuilder setOutPorts(java.util.List<? extends NodePortEnt> outPorts);
        
        /**
   		 * Set annotation
         * 
         * @param annotation the property value,  
         * @return this entity builder for chaining
         */
        ComponentNodeEntBuilder setAnnotation(NodeAnnotationEnt annotation);
        
        /**
   		 * Set position
         * 
         * @param position the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        ComponentNodeEntBuilder setPosition(XYEnt position);
        
        /**
         * Whether it&#39;s a native node, component or a metanode.
         * 
         * @param kind the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        ComponentNodeEntBuilder setKind(KindEnum kind);
        
        /**
         * Indicates whether this node has a dialog. Not present, if the node has no dialog.
         * 
         * @param hasDialog the property value,  
         * @return this entity builder for chaining
         */
        ComponentNodeEntBuilder setHasDialog(Boolean hasDialog);
        
        /**
   		 * Set allowedActions
         * 
         * @param allowedActions the property value,  
         * @return this entity builder for chaining
         */
        ComponentNodeEntBuilder setAllowedActions(AllowedNodeActionsEnt allowedActions);
        
        /**
   		 * Set executionInfo
         * 
         * @param executionInfo the property value,  
         * @return this entity builder for chaining
         */
        ComponentNodeEntBuilder setExecutionInfo(NodeExecutionInfoEnt executionInfo);
        
        /**
   		 * Set weight
         * 
         * @param weight the property value,  
         * @return this entity builder for chaining
         */
        ComponentNodeEntBuilder setWeight(BigDecimal weight);
        
        /**
         * The component name.
         * 
         * @param name the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        ComponentNodeEntBuilder setName(String name);
        
        /**
         * Can be missing if nothing was selected by the user
         * 
         * @param type the property value,  
         * @return this entity builder for chaining
         */
        ComponentNodeEntBuilder setType(TypeEnum type);
        
        /**
         * The icon encoded in a data-url. Not available if no icon is set.
         * 
         * @param icon the property value,  
         * @return this entity builder for chaining
         */
        ComponentNodeEntBuilder setIcon(String icon);
        
        /**
   		 * Set state
         * 
         * @param state the property value,  
         * @return this entity builder for chaining
         */
        ComponentNodeEntBuilder setState(NodeStateEnt state);
        
        /**
   		 * Set link
         * 
         * @param link the property value,  
         * @return this entity builder for chaining
         */
        ComponentNodeEntBuilder setLink(TemplateLinkEnt link);
        
        /**
         * The lock-status of this node. It has three states: absent if there is no lock at all, true if it&#39;s locked, false if it&#39;s unlocked.
         * 
         * @param isLocked the property value,  
         * @return this entity builder for chaining
         */
        ComponentNodeEntBuilder setIsLocked(Boolean isLocked);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        ComponentNodeEnt build();
    
    }

}
