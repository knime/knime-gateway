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
import org.knime.gateway.api.webui.entity.NodeDialogOptionDescriptionEnt;
import org.knime.gateway.api.webui.entity.NodeDialogOptionGroupEnt;
import org.knime.gateway.api.webui.entity.NodePortDescriptionEnt;
import org.knime.gateway.api.webui.entity.NodeViewDescriptionEnt;

import org.knime.gateway.api.entity.GatewayEntityBuilder;


import org.knime.gateway.api.entity.GatewayEntity;

/**
 * Descriptions of aspects of some node.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", ""})
public interface DescribableNodeDescriptionEnt extends GatewayEntity {


  /**
   * The freeform description text of the node. Sometimes also referred to as \&quot;intro text\&quot;. The description text may contain HTML markup tags.
   * @return description 
   **/
  public String getDescription();

  /**
   * List of ungrouped dialog options.
   * @return ungroupedOptions 
   **/
  public java.util.List<NodeDialogOptionDescriptionEnt> getUngroupedOptions();

  /**
   * List of dialog option groups.
   * @return optionGroups 
   **/
  public java.util.List<NodeDialogOptionGroupEnt> getOptionGroups();

  /**
   * Descriptions for the node views.
   * @return views 
   **/
  public java.util.List<NodeViewDescriptionEnt> getViews();

  /**
   * Get interactiveView
   * @return interactiveView 
   **/
  public NodeViewDescriptionEnt getInteractiveView();

  /**
   * Get inPorts
   * @return inPorts 
   **/
  public java.util.List<NodePortDescriptionEnt> getInPorts();

  /**
   * Get dynamicInPortGroupDescriptions
   * @return dynamicInPortGroupDescriptions 
   **/
  public java.util.List<DynamicPortGroupDescriptionEnt> getDynamicInPortGroupDescriptions();

  /**
   * Get outPorts
   * @return outPorts 
   **/
  public java.util.List<NodePortDescriptionEnt> getOutPorts();

  /**
   * Get dynamicOutPortGroupDescriptions
   * @return dynamicOutPortGroupDescriptions 
   **/
  public java.util.List<DynamicPortGroupDescriptionEnt> getDynamicOutPortGroupDescriptions();


    /**
     * The builder for the entity.
     */
    public interface DescribableNodeDescriptionEntBuilder extends GatewayEntityBuilder<DescribableNodeDescriptionEnt> {

        /**
         * The freeform description text of the node. Sometimes also referred to as \&quot;intro text\&quot;. The description text may contain HTML markup tags.
         * 
         * @param description the property value,  
         * @return this entity builder for chaining
         */
        DescribableNodeDescriptionEntBuilder setDescription(String description);
        
        /**
         * List of ungrouped dialog options.
         * 
         * @param ungroupedOptions the property value,  
         * @return this entity builder for chaining
         */
        DescribableNodeDescriptionEntBuilder setUngroupedOptions(java.util.List<NodeDialogOptionDescriptionEnt> ungroupedOptions);
        
        /**
         * List of dialog option groups.
         * 
         * @param optionGroups the property value,  
         * @return this entity builder for chaining
         */
        DescribableNodeDescriptionEntBuilder setOptionGroups(java.util.List<NodeDialogOptionGroupEnt> optionGroups);
        
        /**
         * Descriptions for the node views.
         * 
         * @param views the property value,  
         * @return this entity builder for chaining
         */
        DescribableNodeDescriptionEntBuilder setViews(java.util.List<NodeViewDescriptionEnt> views);
        
        /**
   		 * Set interactiveView
         * 
         * @param interactiveView the property value,  
         * @return this entity builder for chaining
         */
        DescribableNodeDescriptionEntBuilder setInteractiveView(NodeViewDescriptionEnt interactiveView);
        
        /**
   		 * Set inPorts
         * 
         * @param inPorts the property value,  
         * @return this entity builder for chaining
         */
        DescribableNodeDescriptionEntBuilder setInPorts(java.util.List<NodePortDescriptionEnt> inPorts);
        
        /**
   		 * Set dynamicInPortGroupDescriptions
         * 
         * @param dynamicInPortGroupDescriptions the property value,  
         * @return this entity builder for chaining
         */
        DescribableNodeDescriptionEntBuilder setDynamicInPortGroupDescriptions(java.util.List<DynamicPortGroupDescriptionEnt> dynamicInPortGroupDescriptions);
        
        /**
   		 * Set outPorts
         * 
         * @param outPorts the property value,  
         * @return this entity builder for chaining
         */
        DescribableNodeDescriptionEntBuilder setOutPorts(java.util.List<NodePortDescriptionEnt> outPorts);
        
        /**
   		 * Set dynamicOutPortGroupDescriptions
         * 
         * @param dynamicOutPortGroupDescriptions the property value,  
         * @return this entity builder for chaining
         */
        DescribableNodeDescriptionEntBuilder setDynamicOutPortGroupDescriptions(java.util.List<DynamicPortGroupDescriptionEnt> dynamicOutPortGroupDescriptions);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        DescribableNodeDescriptionEnt build();
    
    }

}
