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

import java.time.OffsetDateTime;

import java.util.function.BiConsumer;

import org.knime.core.util.Pair;

import org.knime.gateway.api.entity.GatewayEntityBuilder;


import org.knime.gateway.api.entity.GatewayEntity;

/**
 * Corresponds to org.knime.hub.client.sdk.ent.catalog.NamedItemVersion
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
public interface NamedItemVersionEnt extends GatewayEntity {


  /**
   * Version number of the item.
   * @return version , never <code>null</code>
   **/
  public Integer getVersion();

  /**
   * Get title
   * @return title 
   **/
  public String getTitle();

  /**
   * Get description
   * @return description 
   **/
  public String getDescription();

  /**
   * Get author
   * @return author 
   **/
  public String getAuthor();

  /**
   * Get createdOn
   * @return createdOn 
   **/
  public OffsetDateTime getCreatedOn();


  @Override
  default void forEachPropertyValue(final GatewayEntity other,
      final BiConsumer<String, Pair<Object, Object>> valueConsumer) {
      var e = (NamedItemVersionEnt)other;
      valueConsumer.accept("version", Pair.create(getVersion(), e.getVersion()));
      valueConsumer.accept("title", Pair.create(getTitle(), e.getTitle()));
      valueConsumer.accept("description", Pair.create(getDescription(), e.getDescription()));
      valueConsumer.accept("author", Pair.create(getAuthor(), e.getAuthor()));
      valueConsumer.accept("createdOn", Pair.create(getCreatedOn(), e.getCreatedOn()));
  }

    /**
     * The builder for the entity.
     */
    public interface NamedItemVersionEntBuilder extends GatewayEntityBuilder<NamedItemVersionEnt> {

        /**
         * Version number of the item.
         * 
         * @param version the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        NamedItemVersionEntBuilder setVersion(Integer version);
        
        /**
   		 * Set title
         * 
         * @param title the property value,  
         * @return this entity builder for chaining
         */
        NamedItemVersionEntBuilder setTitle(String title);
        
        /**
   		 * Set description
         * 
         * @param description the property value,  
         * @return this entity builder for chaining
         */
        NamedItemVersionEntBuilder setDescription(String description);
        
        /**
   		 * Set author
         * 
         * @param author the property value,  
         * @return this entity builder for chaining
         */
        NamedItemVersionEntBuilder setAuthor(String author);
        
        /**
   		 * Set createdOn
         * 
         * @param createdOn the property value,  
         * @return this entity builder for chaining
         */
        NamedItemVersionEntBuilder setCreatedOn(OffsetDateTime createdOn);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        NamedItemVersionEnt build();
    
    }

}
