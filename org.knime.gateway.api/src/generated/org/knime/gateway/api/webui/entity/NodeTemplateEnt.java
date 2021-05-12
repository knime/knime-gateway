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

import org.knime.gateway.api.webui.entity.NativeNodeInvariantsEnt;
import org.knime.gateway.api.webui.entity.NodeFactoryKeyEnt;
import org.knime.gateway.api.webui.entity.NodePortInvariantsEnt;

import org.knime.gateway.api.entity.GatewayEntityBuilder;


import org.knime.gateway.api.entity.GatewayEntity;

/**
 * Contains all the &#39;static&#39; properties of a node or component required to draw the node/component figure.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
public interface NodeTemplateEnt extends GatewayEntity, NativeNodeInvariantsEnt {


  /**
   * A unique identifier for this template.
   * @return id , never <code>null</code>
   **/
  public String getId();

  /**
   * Whether this node templates represents a component.
   * @return component 
   **/
  public Boolean isComponent();

  /**
   * The node&#39;s input ports.
   * @return inPorts 
   **/
  public java.util.List<NodePortInvariantsEnt> getInPorts();

  /**
   * The node&#39;s output ports.
   * @return outPorts 
   **/
  public java.util.List<NodePortInvariantsEnt> getOutPorts();

  /**
   * Get nodeFactory
   * @return nodeFactory 
   **/
  public NodeFactoryKeyEnt getNodeFactory();


    /**
     * The builder for the entity.
     */
    public interface NodeTemplateEntBuilder extends GatewayEntityBuilder<NodeTemplateEnt> {

        /**
         * The node&#39;s name.
         * 
         * @param name the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        NodeTemplateEntBuilder setName(String name);
        
        /**
         * The type of the node.
         * 
         * @param type the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        NodeTemplateEntBuilder setType(TypeEnum type);
        
        /**
         * The icon encoded in a data-url.
         * 
         * @param icon the property value,  
         * @return this entity builder for chaining
         */
        NodeTemplateEntBuilder setIcon(String icon);
        
        /**
         * A unique identifier for this template.
         * 
         * @param id the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        NodeTemplateEntBuilder setId(String id);
        
        /**
         * Whether this node templates represents a component.
         * 
         * @param component the property value,  
         * @return this entity builder for chaining
         */
        NodeTemplateEntBuilder setComponent(Boolean component);
        
        /**
         * The node&#39;s input ports.
         * 
         * @param inPorts the property value,  
         * @return this entity builder for chaining
         */
        NodeTemplateEntBuilder setInPorts(java.util.List<NodePortInvariantsEnt> inPorts);
        
        /**
         * The node&#39;s output ports.
         * 
         * @param outPorts the property value,  
         * @return this entity builder for chaining
         */
        NodeTemplateEntBuilder setOutPorts(java.util.List<NodePortInvariantsEnt> outPorts);
        
        /**
   		 * Set nodeFactory
         * 
         * @param nodeFactory the property value,  
         * @return this entity builder for chaining
         */
        NodeTemplateEntBuilder setNodeFactory(NodeFactoryKeyEnt nodeFactory);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        NodeTemplateEnt build();
    
    }

}
