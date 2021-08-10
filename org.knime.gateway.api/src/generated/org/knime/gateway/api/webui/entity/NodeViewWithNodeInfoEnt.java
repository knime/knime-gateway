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

import org.knime.gateway.api.webui.entity.ComponentViewInfoEnt;
import org.knime.gateway.api.webui.entity.NodeStateEnt;
import org.knime.gateway.api.webui.entity.NodeViewEnt;

import org.knime.gateway.api.entity.GatewayEntityBuilder;


import org.knime.gateway.api.entity.GatewayEntity;

/**
 * TODO
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
public interface NodeViewWithNodeInfoEnt extends GatewayEntity, NodeViewEnt {


  /**
   * Get nodeName
   * @return nodeName 
   **/
  public String getNodeName();

  /**
   * Get nodeAnnotation
   * @return nodeAnnotation 
   **/
  public String getNodeAnnotation();

  /**
   * Get nodeState
   * @return nodeState 
   **/
  public NodeStateEnt getNodeState();

  /**
   * initial data - optional for optimisation
   * @return data 
   **/
  public String getData();

  /**
   * Get componentViewInfo
   * @return componentViewInfo 
   **/
  public ComponentViewInfoEnt getComponentViewInfo();


    /**
     * The builder for the entity.
     */
    public interface NodeViewWithNodeInfoEntBuilder extends GatewayEntityBuilder<NodeViewWithNodeInfoEnt> {

        /**
         * The type of node view.
         * 
         * @param type the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        NodeViewWithNodeInfoEntBuilder setType(TypeEnum type);
        
        /**
         * Relative or absolute path to the html document representing the node view. TODO e.g. for debugging an absolute path is provided Note that this property is only present if type is &#39;iframe&#39;.
         * 
         * @param iframeSrc the property value,  
         * @return this entity builder for chaining
         */
        NodeViewWithNodeInfoEntBuilder setIframeSrc(String iframeSrc);
        
        /**
         * Source of the (vue) component to be used for the node view. Note that this property is only present if type is &#39;ui-component&#39;.
         * 
         * @param uiComponentSrc the property value,  
         * @return this entity builder for chaining
         */
        NodeViewWithNodeInfoEntBuilder setUiComponentSrc(String uiComponentSrc);
        
        /**
         * whether this is a widget and, e.g., only be shown in the webportal but not in the node view panel
         * 
         * @param widget the property value,  
         * @return this entity builder for chaining
         */
        NodeViewWithNodeInfoEntBuilder setWidget(Boolean widget);
        
        /**
         * TODO
         * 
         * @param reexecutable the property value,  
         * @return this entity builder for chaining
         */
        NodeViewWithNodeInfoEntBuilder setReexecutable(Boolean reexecutable);
        
        /**
   		 * Set nodeName
         * 
         * @param nodeName the property value,  
         * @return this entity builder for chaining
         */
        NodeViewWithNodeInfoEntBuilder setNodeName(String nodeName);
        
        /**
   		 * Set nodeAnnotation
         * 
         * @param nodeAnnotation the property value,  
         * @return this entity builder for chaining
         */
        NodeViewWithNodeInfoEntBuilder setNodeAnnotation(String nodeAnnotation);
        
        /**
   		 * Set nodeState
         * 
         * @param nodeState the property value,  
         * @return this entity builder for chaining
         */
        NodeViewWithNodeInfoEntBuilder setNodeState(NodeStateEnt nodeState);
        
        /**
         * initial data - optional for optimisation
         * 
         * @param data the property value,  
         * @return this entity builder for chaining
         */
        NodeViewWithNodeInfoEntBuilder setData(String data);
        
        /**
   		 * Set componentViewInfo
         * 
         * @param componentViewInfo the property value,  
         * @return this entity builder for chaining
         */
        NodeViewWithNodeInfoEntBuilder setComponentViewInfo(ComponentViewInfoEnt componentViewInfo);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        NodeViewWithNodeInfoEnt build();
    
    }

}
