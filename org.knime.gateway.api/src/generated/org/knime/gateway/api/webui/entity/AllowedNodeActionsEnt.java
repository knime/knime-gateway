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

import org.knime.gateway.api.webui.entity.AllowedActionsEnt;

import org.knime.gateway.api.entity.GatewayEntityBuilder;


import org.knime.gateway.api.entity.GatewayEntity;

/**
 * Set of actions allowed specific to nodes.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
public interface AllowedNodeActionsEnt extends GatewayEntity, AllowedActionsEnt {

  /**
   * Only present for components or metanodes. Describes whether the node can be expanded. In case of \&quot;resetRequired\&quot;, the contained nodes have to be reset first.
   */
  public enum CanExpandEnum {
    TRUE("true"),
    
    RESETREQUIRED("resetRequired"),
    
    FALSE("false");

    private String value;

    CanExpandEnum(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

  }

  /**
   * Describes whether the selection can be collapsed if it contains this node. In case of \&quot;resetRequired\&quot;, the node has to be reset first.
   */
  public enum CanCollapseEnum {
    TRUE("true"),
    
    RESETREQUIRED("resetRequired"),
    
    FALSE("false");

    private String value;

    CanCollapseEnum(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

  }


  /**
   * Indicates whether the dialog can be opened (extra window) or not. If the property is absent, no dialog is available altogether.
   * @return canOpenDialog 
   **/
  public Boolean isCanOpenDialog();

  /**
   * Indicates  whether a legacy flow variable dialog can be opened. If the property is absent, there is no legacy flow variable dialog.
   * @return canOpenLegacyFlowVariableDialog 
   **/
  public Boolean isCanOpenLegacyFlowVariableDialog();

  /**
   * Indicates whether the node view can opened (extra window) or not. If the property is absent, no node view is available altogether.
   * @return canOpenView 
   **/
  public Boolean isCanOpenView();

  /**
   * Indicates whether this node can be deleted. Circumstances that would prevent a node from being deleted are, e.g., executing state, executing successors, a deletion-lock set on the node, ...
   * @return canDelete 
   **/
  public Boolean isCanDelete();

  /**
   * Only present for components or metanodes. Describes whether the node can be expanded. In case of \&quot;resetRequired\&quot;, the contained nodes have to be reset first.
   * @return canExpand 
   **/
  public CanExpandEnum getCanExpand();

  /**
   * Describes whether the selection can be collapsed if it contains this node. In case of \&quot;resetRequired\&quot;, the node has to be reset first.
   * @return canCollapse 
   **/
  public CanCollapseEnum getCanCollapse();


    /**
     * The builder for the entity.
     */
    public interface AllowedNodeActionsEntBuilder extends GatewayEntityBuilder<AllowedNodeActionsEnt> {

        /**
         * Whether the node can be executed which depends on the node state and the states of the node&#39;s predecessors.
         * 
         * @param canExecute the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        AllowedNodeActionsEntBuilder setCanExecute(Boolean canExecute);
        
        /**
         * Whether the node can be cancelled.
         * 
         * @param canCancel the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        AllowedNodeActionsEntBuilder setCanCancel(Boolean canCancel);
        
        /**
         * Whether the node can be reset which depends on the node state and the states of the node&#39;s successors. Not given in case of the project workflow (action to reset all is not supported there).
         * 
         * @param canReset the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        AllowedNodeActionsEntBuilder setCanReset(Boolean canReset);
        
        /**
         * Indicates whether the dialog can be opened (extra window) or not. If the property is absent, no dialog is available altogether.
         * 
         * @param canOpenDialog the property value,  
         * @return this entity builder for chaining
         */
        AllowedNodeActionsEntBuilder setCanOpenDialog(Boolean canOpenDialog);
        
        /**
         * Indicates  whether a legacy flow variable dialog can be opened. If the property is absent, there is no legacy flow variable dialog.
         * 
         * @param canOpenLegacyFlowVariableDialog the property value,  
         * @return this entity builder for chaining
         */
        AllowedNodeActionsEntBuilder setCanOpenLegacyFlowVariableDialog(Boolean canOpenLegacyFlowVariableDialog);
        
        /**
         * Indicates whether the node view can opened (extra window) or not. If the property is absent, no node view is available altogether.
         * 
         * @param canOpenView the property value,  
         * @return this entity builder for chaining
         */
        AllowedNodeActionsEntBuilder setCanOpenView(Boolean canOpenView);
        
        /**
         * Indicates whether this node can be deleted. Circumstances that would prevent a node from being deleted are, e.g., executing state, executing successors, a deletion-lock set on the node, ...
         * 
         * @param canDelete the property value,  
         * @return this entity builder for chaining
         */
        AllowedNodeActionsEntBuilder setCanDelete(Boolean canDelete);
        
        /**
         * Only present for components or metanodes. Describes whether the node can be expanded. In case of \&quot;resetRequired\&quot;, the contained nodes have to be reset first.
         * 
         * @param canExpand the property value,  
         * @return this entity builder for chaining
         */
        AllowedNodeActionsEntBuilder setCanExpand(CanExpandEnum canExpand);
        
        /**
         * Describes whether the selection can be collapsed if it contains this node. In case of \&quot;resetRequired\&quot;, the node has to be reset first.
         * 
         * @param canCollapse the property value,  
         * @return this entity builder for chaining
         */
        AllowedNodeActionsEntBuilder setCanCollapse(CanCollapseEnum canCollapse);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        AllowedNodeActionsEnt build();
    
    }

}
