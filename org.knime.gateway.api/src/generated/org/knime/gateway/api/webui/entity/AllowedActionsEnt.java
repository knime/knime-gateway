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
 * Mainly provides information on what actions are allowed on a node or an entire workflow.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
public interface AllowedActionsEnt extends GatewayEntity {


  /**
   * Whether the node can be executed which depends on the node state and the states of the node&#39;s predecessors.
   * @return canExecute , never <code>null</code>
   **/
  public Boolean isCanExecute();

  /**
   * Whether the node can be cancelled.
   * @return canCancel , never <code>null</code>
   **/
  public Boolean isCanCancel();

  /**
   * Whether the node can be reset which depends on the node state and the states of the node&#39;s successors. Not given in case of the project workflow (action to reset all is not supported there).
   * @return canReset , never <code>null</code>
   **/
  public Boolean isCanReset();


  @Override
  default void forEachPropertyValue(final GatewayEntity other,
      final BiConsumer<String, Pair<Object, Object>> valueConsumer) {
      var e = (AllowedActionsEnt)other;
      valueConsumer.accept("canExecute", Pair.create(isCanExecute(), e.isCanExecute()));
      valueConsumer.accept("canCancel", Pair.create(isCanCancel(), e.isCanCancel()));
      valueConsumer.accept("canReset", Pair.create(isCanReset(), e.isCanReset()));
  }

    /**
     * The builder for the entity.
     */
    public interface AllowedActionsEntBuilder extends GatewayEntityBuilder<AllowedActionsEnt> {

        /**
         * Whether the node can be executed which depends on the node state and the states of the node&#39;s predecessors.
         * 
         * @param canExecute the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        AllowedActionsEntBuilder setCanExecute(Boolean canExecute);
        
        /**
         * Whether the node can be cancelled.
         * 
         * @param canCancel the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        AllowedActionsEntBuilder setCanCancel(Boolean canCancel);
        
        /**
         * Whether the node can be reset which depends on the node state and the states of the node&#39;s successors. Not given in case of the project workflow (action to reset all is not supported there).
         * 
         * @param canReset the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        AllowedActionsEntBuilder setCanReset(Boolean canReset);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        AllowedActionsEnt build();
    
    }

}
