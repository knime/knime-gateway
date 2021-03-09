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
import org.knime.gateway.api.webui.entity.XYEnt;

import org.knime.gateway.api.entity.GatewayEntityBuilder;


import org.knime.gateway.api.entity.GatewayEntity;

/**
 * Native node extension of a node.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
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
         * All successors of this node represented as a bitset. Every bit stands for a node and is &#39;1&#39; if the respective node is a (indirect!) successor. The bitset includes this node, too.
         * 
         * @param successors the property value,  
         * @return this entity builder for chaining
         */
        NativeNodeEntBuilder setSuccessors(java.util.BitSet successors);
        
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
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        NativeNodeEnt build();
    
    }

}
