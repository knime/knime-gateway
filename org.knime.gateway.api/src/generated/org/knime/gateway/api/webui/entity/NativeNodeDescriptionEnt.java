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

import org.knime.gateway.api.webui.entity.DynamicPortGroupDescriptionEnt;
import org.knime.gateway.api.webui.entity.LinkEnt;
import org.knime.gateway.api.webui.entity.NodeDescriptionEnt;
import org.knime.gateway.api.webui.entity.NodeDialogOptionGroupEnt;
import org.knime.gateway.api.webui.entity.NodePortDescriptionEnt;
import org.knime.gateway.api.webui.entity.NodeViewDescriptionEnt;

import java.util.function.BiConsumer;

import org.knime.core.util.Pair;

import org.knime.gateway.api.entity.GatewayEntityBuilder;


import org.knime.gateway.api.entity.GatewayEntity;

/**
 * Description of certain aspects of a native node.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
public interface NativeNodeDescriptionEnt extends GatewayEntity, NodeDescriptionEnt {


  /**
   * A short description of the node. This is a simple string without markup tags.
   * @return shortDescription 
   **/
  public String getShortDescription();

  /**
   * Get dynamicInPortGroupDescriptions
   * @return dynamicInPortGroupDescriptions 
   **/
  public java.util.List<DynamicPortGroupDescriptionEnt> getDynamicInPortGroupDescriptions();

  /**
   * Get dynamicOutPortGroupDescriptions
   * @return dynamicOutPortGroupDescriptions 
   **/
  public java.util.List<DynamicPortGroupDescriptionEnt> getDynamicOutPortGroupDescriptions();

  /**
   * Get interactiveView
   * @return interactiveView 
   **/
  public NodeViewDescriptionEnt getInteractiveView();

  /**
   * A collection of URLs.
   * @return links 
   **/
  public java.util.List<LinkEnt> getLinks();


  @Override
  default void forEachPropertyValue(final GatewayEntity other,
      final BiConsumer<String, Pair<Object, Object>> valueConsumer) {
      var e = (NativeNodeDescriptionEnt)other;
      valueConsumer.accept("description", Pair.create(getDescription(), e.getDescription()));
      valueConsumer.accept("options", Pair.create(getOptions(), e.getOptions()));
      valueConsumer.accept("views", Pair.create(getViews(), e.getViews()));
      valueConsumer.accept("inPorts", Pair.create(getInPorts(), e.getInPorts()));
      valueConsumer.accept("outPorts", Pair.create(getOutPorts(), e.getOutPorts()));
      valueConsumer.accept("shortDescription", Pair.create(getShortDescription(), e.getShortDescription()));
      valueConsumer.accept("dynamicInPortGroupDescriptions", Pair.create(getDynamicInPortGroupDescriptions(), e.getDynamicInPortGroupDescriptions()));
      valueConsumer.accept("dynamicOutPortGroupDescriptions", Pair.create(getDynamicOutPortGroupDescriptions(), e.getDynamicOutPortGroupDescriptions()));
      valueConsumer.accept("interactiveView", Pair.create(getInteractiveView(), e.getInteractiveView()));
      valueConsumer.accept("links", Pair.create(getLinks(), e.getLinks()));
  }

    /**
     * The builder for the entity.
     */
    public interface NativeNodeDescriptionEntBuilder extends GatewayEntityBuilder<NativeNodeDescriptionEnt> {

        /**
         * The freeform description text of the node. Sometimes also referred to as \&quot;intro text\&quot;. May contain HTML markup tags.
         * 
         * @param description the property value,  
         * @return this entity builder for chaining
         */
        NativeNodeDescriptionEntBuilder setDescription(String description);
        
        /**
         * List of dialog option groups. In case the dialog options are actually ungrouped, this is a singleton list containing a group with no name or description.
         * 
         * @param options the property value,  
         * @return this entity builder for chaining
         */
        NativeNodeDescriptionEntBuilder setOptions(java.util.List<NodeDialogOptionGroupEnt> options);
        
        /**
         * Descriptions for the node views.
         * 
         * @param views the property value,  
         * @return this entity builder for chaining
         */
        NativeNodeDescriptionEntBuilder setViews(java.util.List<NodeViewDescriptionEnt> views);
        
        /**
         * Descriptions of static input ports.
         * 
         * @param inPorts the property value,  
         * @return this entity builder for chaining
         */
        NativeNodeDescriptionEntBuilder setInPorts(java.util.List<NodePortDescriptionEnt> inPorts);
        
        /**
         * Descriptions of static output ports.
         * 
         * @param outPorts the property value,  
         * @return this entity builder for chaining
         */
        NativeNodeDescriptionEntBuilder setOutPorts(java.util.List<NodePortDescriptionEnt> outPorts);
        
        /**
         * A short description of the node. This is a simple string without markup tags.
         * 
         * @param shortDescription the property value,  
         * @return this entity builder for chaining
         */
        NativeNodeDescriptionEntBuilder setShortDescription(String shortDescription);
        
        /**
   		 * Set dynamicInPortGroupDescriptions
         * 
         * @param dynamicInPortGroupDescriptions the property value,  
         * @return this entity builder for chaining
         */
        NativeNodeDescriptionEntBuilder setDynamicInPortGroupDescriptions(java.util.List<DynamicPortGroupDescriptionEnt> dynamicInPortGroupDescriptions);
        
        /**
   		 * Set dynamicOutPortGroupDescriptions
         * 
         * @param dynamicOutPortGroupDescriptions the property value,  
         * @return this entity builder for chaining
         */
        NativeNodeDescriptionEntBuilder setDynamicOutPortGroupDescriptions(java.util.List<DynamicPortGroupDescriptionEnt> dynamicOutPortGroupDescriptions);
        
        /**
   		 * Set interactiveView
         * 
         * @param interactiveView the property value,  
         * @return this entity builder for chaining
         */
        NativeNodeDescriptionEntBuilder setInteractiveView(NodeViewDescriptionEnt interactiveView);
        
        /**
         * A collection of URLs.
         * 
         * @param links the property value,  
         * @return this entity builder for chaining
         */
        NativeNodeDescriptionEntBuilder setLinks(java.util.List<LinkEnt> links);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        NativeNodeDescriptionEnt build();
    
    }

}
