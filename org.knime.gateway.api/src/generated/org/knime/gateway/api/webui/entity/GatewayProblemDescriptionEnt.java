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


import java.util.Map;

import org.knime.gateway.api.entity.GatewayEntity;

/**
 * Represents a thrown instance of x-knime-gateway-executor-exceptions i.e. GatewayException.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
public interface GatewayProblemDescriptionEnt extends GatewayEntity {


  /**
   * Title of the problem (non instance-specific).
   * @return title , never <code>null</code>
   **/
  public String getTitle();

  /**
   * Name of the thrown exception.
   * @return code , never <code>null</code>
   **/
  public String getCode();

  /**
   * HTTP response status code (if applicable).
   * @return status 
   **/
  public Integer getStatus();

  /**
   * List of details (\&quot;user-facing stack trace\&quot;) of the problem.
   * @return details 
   **/
  public java.util.List<String> getDetails();

  /**
   * Indicating whether error details can be copied by the user.
   * @return canCopy 
   **/
  public Boolean isCanCopy();

  /**
   * Get any additional properties
   * @return map of additional property names and values 
   **/
  public Map<String, String> getAdditionalProperties();


  @Override
  default void forEachPropertyValue(final GatewayEntity other,
      final BiConsumer<String, Pair<Object, Object>> valueConsumer) {
      var e = (GatewayProblemDescriptionEnt)other;
      valueConsumer.accept("title", Pair.create(getTitle(), e.getTitle()));
      valueConsumer.accept("code", Pair.create(getCode(), e.getCode()));
      valueConsumer.accept("status", Pair.create(getStatus(), e.getStatus()));
      valueConsumer.accept("details", Pair.create(getDetails(), e.getDetails()));
      valueConsumer.accept("canCopy", Pair.create(isCanCopy(), e.isCanCopy()));
  }

    /**
     * The builder for the entity.
     */
    public interface GatewayProblemDescriptionEntBuilder extends GatewayEntityBuilder<GatewayProblemDescriptionEnt> {

        /**
         * Title of the problem (non instance-specific).
         * 
         * @param title the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        GatewayProblemDescriptionEntBuilder setTitle(String title);
        
        /**
         * Name of the thrown exception.
         * 
         * @param code the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        GatewayProblemDescriptionEntBuilder setCode(String code);
        
        /**
         * HTTP response status code (if applicable).
         * 
         * @param status the property value,  
         * @return this entity builder for chaining
         */
        GatewayProblemDescriptionEntBuilder setStatus(Integer status);
        
        /**
         * List of details (\&quot;user-facing stack trace\&quot;) of the problem.
         * 
         * @param details the property value,  
         * @return this entity builder for chaining
         */
        GatewayProblemDescriptionEntBuilder setDetails(java.util.List<String> details);
        
        /**
         * Indicating whether error details can be copied by the user.
         * 
         * @param canCopy the property value,  
         * @return this entity builder for chaining
         */
        GatewayProblemDescriptionEntBuilder setCanCopy(Boolean canCopy);
        
        /**
        * Set additionalProperties
        *
        * @param additionalProperties map of additional property names and values
        * @return this entity builder for chaining
        */
        GatewayProblemDescriptionEntBuilder setAdditionalProperties(Map<String, String> additionalProperties);

        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        GatewayProblemDescriptionEnt build();
    
    }

}
