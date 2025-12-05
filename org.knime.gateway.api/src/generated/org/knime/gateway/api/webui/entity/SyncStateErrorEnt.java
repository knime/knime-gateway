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
 * Additional error details in case of a sync state error.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
public interface SyncStateErrorEnt extends GatewayEntity {


  /**
   * Get code
   * @return code , never <code>null</code>
   **/
  public String getCode();

  /**
   * Get title
   * @return title , never <code>null</code>
   **/
  public String getTitle();

  /**
   * Get details
   * @return details , never <code>null</code>
   **/
  public java.util.List<String> getDetails();

  /**
   * Get canCopy
   * @return canCopy , never <code>null</code>
   **/
  public Boolean isCanCopy();

  /**
   * Get status
   * @return status 
   **/
  public Integer getStatus();

  /**
   * Get stackTrace
   * @return stackTrace 
   **/
  public String getStackTrace();


  @Override
  default void forEachPropertyValue(final GatewayEntity other,
      final BiConsumer<String, Pair<Object, Object>> valueConsumer) {
      var e = (SyncStateErrorEnt)other;
      valueConsumer.accept("code", Pair.create(getCode(), e.getCode()));
      valueConsumer.accept("title", Pair.create(getTitle(), e.getTitle()));
      valueConsumer.accept("details", Pair.create(getDetails(), e.getDetails()));
      valueConsumer.accept("canCopy", Pair.create(isCanCopy(), e.isCanCopy()));
      valueConsumer.accept("status", Pair.create(getStatus(), e.getStatus()));
      valueConsumer.accept("stackTrace", Pair.create(getStackTrace(), e.getStackTrace()));
  }

    /**
     * The builder for the entity.
     */
    public interface SyncStateErrorEntBuilder extends GatewayEntityBuilder<SyncStateErrorEnt> {

        /**
   		 * Set code
         * 
         * @param code the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        SyncStateErrorEntBuilder setCode(String code);
        
        /**
   		 * Set title
         * 
         * @param title the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        SyncStateErrorEntBuilder setTitle(String title);
        
        /**
   		 * Set details
         * 
         * @param details the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        SyncStateErrorEntBuilder setDetails(java.util.List<String> details);
        
        /**
   		 * Set canCopy
         * 
         * @param canCopy the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        SyncStateErrorEntBuilder setCanCopy(Boolean canCopy);
        
        /**
   		 * Set status
         * 
         * @param status the property value,  
         * @return this entity builder for chaining
         */
        SyncStateErrorEntBuilder setStatus(Integer status);
        
        /**
   		 * Set stackTrace
         * 
         * @param stackTrace the property value,  
         * @return this entity builder for chaining
         */
        SyncStateErrorEntBuilder setStackTrace(String stackTrace);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        SyncStateErrorEnt build();
    
    }

}
