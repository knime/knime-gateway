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


import java.util.function.BiConsumer;

import org.knime.core.util.Pair;

import org.knime.gateway.api.entity.GatewayEntityBuilder;


import org.knime.gateway.api.entity.GatewayEntity;

/**
 * PermissionsEnt
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
public interface PermissionsEnt extends GatewayEntity {


  /**
   * Whether the current user is allowed to configure nodes
   * @return canConfigureNodes , never <code>null</code>
   **/
  public Boolean isCanConfigureNodes();

  /**
   * Whether the current user is allowed to edit workflows,  this includes most operations including execution and editing meta data
   * @return canEditWorkflow , never <code>null</code>
   **/
  public Boolean isCanEditWorkflow();

  /**
   * Whether the current user is allowed access to the node repository
   * @return canAccessNodeRepository , never <code>null</code>
   **/
  public Boolean isCanAccessNodeRepository();

  /**
   * Whether the current user is allowed access to KAI
   * @return canAccessKAIPanel , never <code>null</code>
   **/
  public Boolean isCanAccessKAIPanel();

  /**
   * Whether the current user is allowed to navigate in the space explorer
   * @return canAccessSpaceExplorer , never <code>null</code>
   **/
  public Boolean isCanAccessSpaceExplorer();

  /**
   * Whether an indicator for certain kinds of remote workflows (\&quot;yellow bar\&quot;) should be shown in some contexts
   * @return showRemoteWorkflowInfo , never <code>null</code>
   **/
  public Boolean isShowRemoteWorkflowInfo();

  /**
   * Whether to show a floating download button above the entire app.
   * @return showFloatingDownloadButton , never <code>null</code>
   **/
  public Boolean isShowFloatingDownloadButton();


  @Override
  default void forEachPropertyValue(final GatewayEntity other,
      final BiConsumer<String, Pair<Object, Object>> valueConsumer) {
      var e = (PermissionsEnt)other;
      valueConsumer.accept("canConfigureNodes", Pair.create(isCanConfigureNodes(), e.isCanConfigureNodes()));
      valueConsumer.accept("canEditWorkflow", Pair.create(isCanEditWorkflow(), e.isCanEditWorkflow()));
      valueConsumer.accept("canAccessNodeRepository", Pair.create(isCanAccessNodeRepository(), e.isCanAccessNodeRepository()));
      valueConsumer.accept("canAccessKAIPanel", Pair.create(isCanAccessKAIPanel(), e.isCanAccessKAIPanel()));
      valueConsumer.accept("canAccessSpaceExplorer", Pair.create(isCanAccessSpaceExplorer(), e.isCanAccessSpaceExplorer()));
      valueConsumer.accept("showRemoteWorkflowInfo", Pair.create(isShowRemoteWorkflowInfo(), e.isShowRemoteWorkflowInfo()));
      valueConsumer.accept("showFloatingDownloadButton", Pair.create(isShowFloatingDownloadButton(), e.isShowFloatingDownloadButton()));
  }

    /**
     * The builder for the entity.
     */
    public interface PermissionsEntBuilder extends GatewayEntityBuilder<PermissionsEnt> {

        /**
         * Whether the current user is allowed to configure nodes
         * 
         * @param canConfigureNodes the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        PermissionsEntBuilder setCanConfigureNodes(Boolean canConfigureNodes);
        
        /**
         * Whether the current user is allowed to edit workflows,  this includes most operations including execution and editing meta data
         * 
         * @param canEditWorkflow the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        PermissionsEntBuilder setCanEditWorkflow(Boolean canEditWorkflow);
        
        /**
         * Whether the current user is allowed access to the node repository
         * 
         * @param canAccessNodeRepository the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        PermissionsEntBuilder setCanAccessNodeRepository(Boolean canAccessNodeRepository);
        
        /**
         * Whether the current user is allowed access to KAI
         * 
         * @param canAccessKAIPanel the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        PermissionsEntBuilder setCanAccessKAIPanel(Boolean canAccessKAIPanel);
        
        /**
         * Whether the current user is allowed to navigate in the space explorer
         * 
         * @param canAccessSpaceExplorer the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        PermissionsEntBuilder setCanAccessSpaceExplorer(Boolean canAccessSpaceExplorer);
        
        /**
         * Whether an indicator for certain kinds of remote workflows (\&quot;yellow bar\&quot;) should be shown in some contexts
         * 
         * @param showRemoteWorkflowInfo the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        PermissionsEntBuilder setShowRemoteWorkflowInfo(Boolean showRemoteWorkflowInfo);
        
        /**
         * Whether to show a floating download button above the entire app.
         * 
         * @param showFloatingDownloadButton the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        PermissionsEntBuilder setShowFloatingDownloadButton(Boolean showFloatingDownloadButton);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        PermissionsEnt build();
    
    }

}
