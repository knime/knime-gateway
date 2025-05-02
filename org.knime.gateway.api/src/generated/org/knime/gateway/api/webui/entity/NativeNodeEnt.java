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
import org.knime.gateway.api.webui.entity.LoopInfoEnt;
import org.knime.gateway.api.webui.entity.NodeAnnotationEnt;
import org.knime.gateway.api.webui.entity.NodeEnt;
import org.knime.gateway.api.webui.entity.NodeExecutionInfoEnt;
import org.knime.gateway.api.webui.entity.NodePortEnt;
import org.knime.gateway.api.webui.entity.NodeStateEnt;
import org.knime.gateway.api.webui.entity.PortGroupEnt;
import org.knime.gateway.api.webui.entity.XYEnt;

import java.util.function.BiConsumer;

import org.knime.core.util.Pair;

import org.knime.gateway.api.entity.GatewayEntityBuilder;


import org.knime.gateway.api.entity.GatewayEntity;

/**
 * Native node extension of a node.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
public interface NativeNodeEnt extends GatewayEntity, NodeEnt {


  /**
   * The id of the node template this node is an instance of.
   * @return templateId , never <code>null</code>
   **/
  public String getTemplateId();

  /**
   * Get state
   * @return state 
   **/
  public NodeStateEnt getState();

  /**
   * Get loopInfo
   * @return loopInfo 
   **/
  public LoopInfoEnt getLoopInfo();

  /**
   * A map of string keys and port group values
   * @return portGroups 
   **/
  public java.util.Map<String, PortGroupEnt> getPortGroups();

  /**
   * Indicates whether the node can re-execute itself (e.g. within a page of a data app). It&#39;s absent if the node isn&#39;t re-executable at all (i.e. it can&#39;t even be configured to be re-executable).
   * @return isReexecutable 
   **/
  public Boolean isReexecutable();


  @Override
  default void forEachPropertyValue(final GatewayEntity other,
      final BiConsumer<String, Pair<Object, Object>> valueConsumer) {
      var e = (NativeNodeEnt)other;
      valueConsumer.accept("id", Pair.create(getId(), e.getId()));
      valueConsumer.accept("inPorts", Pair.create(getInPorts(), e.getInPorts()));
      valueConsumer.accept("outPorts", Pair.create(getOutPorts(), e.getOutPorts()));
      valueConsumer.accept("hasView", Pair.create(hasView(), e.hasView()));
      valueConsumer.accept("annotation", Pair.create(getAnnotation(), e.getAnnotation()));
      valueConsumer.accept("position", Pair.create(getPosition(), e.getPosition()));
      valueConsumer.accept("kind", Pair.create(getKind(), e.getKind()));
      valueConsumer.accept("dialogType", Pair.create(getDialogType(), e.getDialogType()));
      valueConsumer.accept("inputContentVersion", Pair.create(getInputContentVersion(), e.getInputContentVersion()));
      valueConsumer.accept("allowedActions", Pair.create(getAllowedActions(), e.getAllowedActions()));
      valueConsumer.accept("executionInfo", Pair.create(getExecutionInfo(), e.getExecutionInfo()));
      valueConsumer.accept("templateId", Pair.create(getTemplateId(), e.getTemplateId()));
      valueConsumer.accept("state", Pair.create(getState(), e.getState()));
      valueConsumer.accept("loopInfo", Pair.create(getLoopInfo(), e.getLoopInfo()));
      valueConsumer.accept("portGroups", Pair.create(getPortGroups(), e.getPortGroups()));
      valueConsumer.accept("isReexecutable", Pair.create(isReexecutable(), e.isReexecutable()));
  }

    /**
     * The builder for the entity.
     */
    public interface NativeNodeEntBuilder extends GatewayEntityBuilder<NativeNodeEnt> {

        /**
         * The id of the node.
         * 
         * @param id the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        NativeNodeEntBuilder setId(org.knime.gateway.api.entity.NodeIDEnt id);
        
        /**
         * The list of inputs.
         * 
         * @param inPorts the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        NativeNodeEntBuilder setInPorts(java.util.List<? extends NodePortEnt> inPorts);
        
        /**
         * The list of outputs.
         * 
         * @param outPorts the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        NativeNodeEntBuilder setOutPorts(java.util.List<? extends NodePortEnt> outPorts);
        
        /**
         * Indicates whether the node has a view.
         * 
         * @param hasView the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        NativeNodeEntBuilder setHasView(Boolean hasView);
        
        /**
   		 * Set annotation
         * 
         * @param annotation the property value,  
         * @return this entity builder for chaining
         */
        NativeNodeEntBuilder setAnnotation(NodeAnnotationEnt annotation);
        
        /**
   		 * Set position
         * 
         * @param position the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        NativeNodeEntBuilder setPosition(XYEnt position);
        
        /**
         * Whether it&#39;s a native node, component or a metanode.
         * 
         * @param kind the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        NativeNodeEntBuilder setKind(KindEnum kind);
        
        /**
         * Indicates whether and type of dialog a node has. Not present if the node has no dialog.
         * 
         * @param dialogType the property value,  
         * @return this entity builder for chaining
         */
        NativeNodeEntBuilder setDialogType(DialogTypeEnum dialogType);
        
        /**
         * A change in this value signals that the input of the node has changed (this currently only considers   port specs). Includes the flow variable port. Not present if &#x60;hasDialog&#x60; is false. Not present if &#x60;interaction info&#x60; is not included. Not present if no input ports present. Not present for metanodes.
         * 
         * @param inputContentVersion the property value,  
         * @return this entity builder for chaining
         */
        NativeNodeEntBuilder setInputContentVersion(Integer inputContentVersion);
        
        /**
   		 * Set allowedActions
         * 
         * @param allowedActions the property value,  
         * @return this entity builder for chaining
         */
        NativeNodeEntBuilder setAllowedActions(AllowedNodeActionsEnt allowedActions);
        
        /**
   		 * Set executionInfo
         * 
         * @param executionInfo the property value,  
         * @return this entity builder for chaining
         */
        NativeNodeEntBuilder setExecutionInfo(NodeExecutionInfoEnt executionInfo);
        
        /**
         * The id of the node template this node is an instance of.
         * 
         * @param templateId the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        NativeNodeEntBuilder setTemplateId(String templateId);
        
        /**
   		 * Set state
         * 
         * @param state the property value,  
         * @return this entity builder for chaining
         */
        NativeNodeEntBuilder setState(NodeStateEnt state);
        
        /**
   		 * Set loopInfo
         * 
         * @param loopInfo the property value,  
         * @return this entity builder for chaining
         */
        NativeNodeEntBuilder setLoopInfo(LoopInfoEnt loopInfo);
        
        /**
         * A map of string keys and port group values
         * 
         * @param portGroups the property value,  
         * @return this entity builder for chaining
         */
        NativeNodeEntBuilder setPortGroups(java.util.Map<String, PortGroupEnt> portGroups);
        
        /**
         * Indicates whether the node can re-execute itself (e.g. within a page of a data app). It&#39;s absent if the node isn&#39;t re-executable at all (i.e. it can&#39;t even be configured to be re-executable).
         * 
         * @param isReexecutable the property value,  
         * @return this entity builder for chaining
         */
        NativeNodeEntBuilder setIsReexecutable(Boolean isReexecutable);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        NativeNodeEnt build();
    
    }

}
