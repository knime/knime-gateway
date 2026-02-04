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

import org.knime.gateway.api.webui.entity.AutoConnectOptionsEnt;
import org.knime.gateway.api.webui.entity.InsertionOptionsEnt;
import org.knime.gateway.api.webui.entity.ReplacementOptionsEnt;
import org.knime.gateway.api.webui.entity.WorkflowCommandEnt;
import org.knime.gateway.api.webui.entity.XYEnt;

import java.util.function.BiConsumer;

import org.knime.core.util.Pair;

import org.knime.gateway.api.entity.GatewayEntityBuilder;


import org.knime.gateway.api.entity.GatewayEntity;

/**
 * Adds a new component to the workflow.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
public interface AddComponentCommandEnt extends GatewayEntity, WorkflowCommandEnt {


  /**
   * Get providerId
   * @return providerId , never <code>null</code>
   **/
  public String getProviderId();

  /**
   * Get spaceId
   * @return spaceId 
   **/
  public String getSpaceId();

  /**
   * Get itemId
   * @return itemId , never <code>null</code>
   **/
  public String getItemId();

  /**
   * Get position
   * @return position 
   **/
  public XYEnt getPosition();

  /**
   * Get insertionOptions
   * @return insertionOptions 
   **/
  public InsertionOptionsEnt getInsertionOptions();

  /**
   * Get replacementOptions
   * @return replacementOptions 
   **/
  public ReplacementOptionsEnt getReplacementOptions();

  /**
   * Get autoConnectOptions
   * @return autoConnectOptions 
   **/
  public AutoConnectOptionsEnt getAutoConnectOptions();

  /**
   * The name of the component to be added. Such that it can already be used for the loading placeholder before the component is loaded.
   * @return name , never <code>null</code>
   **/
  public String getName();


  @Override
  default void forEachPropertyValue(final GatewayEntity other,
      final BiConsumer<String, Pair<Object, Object>> valueConsumer) {
      var e = (AddComponentCommandEnt)other;
      valueConsumer.accept("kind", Pair.create(getKind(), e.getKind()));
      valueConsumer.accept("providerId", Pair.create(getProviderId(), e.getProviderId()));
      valueConsumer.accept("spaceId", Pair.create(getSpaceId(), e.getSpaceId()));
      valueConsumer.accept("itemId", Pair.create(getItemId(), e.getItemId()));
      valueConsumer.accept("position", Pair.create(getPosition(), e.getPosition()));
      valueConsumer.accept("insertionOptions", Pair.create(getInsertionOptions(), e.getInsertionOptions()));
      valueConsumer.accept("replacementOptions", Pair.create(getReplacementOptions(), e.getReplacementOptions()));
      valueConsumer.accept("autoConnectOptions", Pair.create(getAutoConnectOptions(), e.getAutoConnectOptions()));
      valueConsumer.accept("name", Pair.create(getName(), e.getName()));
  }

    /**
     * The builder for the entity.
     */
    public interface AddComponentCommandEntBuilder extends GatewayEntityBuilder<AddComponentCommandEnt> {

        /**
         * The kind of command which directly maps to a specific &#39;implementation&#39;.
         * 
         * @param kind the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        AddComponentCommandEntBuilder setKind(KindEnum kind);
        
        /**
   		 * Set providerId
         * 
         * @param providerId the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        AddComponentCommandEntBuilder setProviderId(String providerId);
        
        /**
   		 * Set spaceId
         * 
         * @param spaceId the property value,  
         * @return this entity builder for chaining
         */
        AddComponentCommandEntBuilder setSpaceId(String spaceId);
        
        /**
   		 * Set itemId
         * 
         * @param itemId the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        AddComponentCommandEntBuilder setItemId(String itemId);
        
        /**
   		 * Set position
         * 
         * @param position the property value,  
         * @return this entity builder for chaining
         */
        AddComponentCommandEntBuilder setPosition(XYEnt position);
        
        /**
   		 * Set insertionOptions
         * 
         * @param insertionOptions the property value,  
         * @return this entity builder for chaining
         */
        AddComponentCommandEntBuilder setInsertionOptions(InsertionOptionsEnt insertionOptions);
        
        /**
   		 * Set replacementOptions
         * 
         * @param replacementOptions the property value,  
         * @return this entity builder for chaining
         */
        AddComponentCommandEntBuilder setReplacementOptions(ReplacementOptionsEnt replacementOptions);
        
        /**
   		 * Set autoConnectOptions
         * 
         * @param autoConnectOptions the property value,  
         * @return this entity builder for chaining
         */
        AddComponentCommandEntBuilder setAutoConnectOptions(AutoConnectOptionsEnt autoConnectOptions);
        
        /**
         * The name of the component to be added. Such that it can already be used for the loading placeholder before the component is loaded.
         * 
         * @param name the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        AddComponentCommandEntBuilder setName(String name);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        AddComponentCommandEnt build();
    
    }

}
