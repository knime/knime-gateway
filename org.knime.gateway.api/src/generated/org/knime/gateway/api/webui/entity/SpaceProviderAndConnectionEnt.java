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

import org.knime.gateway.api.webui.entity.SpaceGroupEnt;
import org.knime.gateway.api.webui.entity.SpaceProviderEnt;
import org.knime.gateway.api.webui.entity.UserEnt;

import java.util.function.BiConsumer;

import org.knime.core.util.Pair;

import org.knime.gateway.api.entity.GatewayEntityBuilder;


import org.knime.gateway.api.entity.GatewayEntity;

/**
 * SpaceProviderAndConnectionEnt
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
public interface SpaceProviderAndConnectionEnt extends GatewayEntity, SpaceProviderEnt {

  /**
   * Gets or Sets connectionMode
   */
  public enum ConnectionModeEnum {
    AUTHENTICATED("AUTHENTICATED"),
    
    ANONYMOUS("ANONYMOUS"),
    
    AUTOMATIC("AUTOMATIC");

    private String value;

    ConnectionModeEnum(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

  }


  /**
   * Get id
   * @return id , never <code>null</code>
   **/
  public String getId();

  /**
   * Get name
   * @return name , never <code>null</code>
   **/
  public String getName();

  /**
   * Get connected
   * @return connected , never <code>null</code>
   **/
  public Boolean isConnected();

  /**
   * Get connectionMode
   * @return connectionMode , never <code>null</code>
   **/
  public ConnectionModeEnum getConnectionMode();

  /**
   * Get user
   * @return user 
   **/
  public UserEnt getUser();


  @Override
  default void forEachPropertyValue(final GatewayEntity other,
      final BiConsumer<String, Pair<Object, Object>> valueConsumer) {
      var e = (SpaceProviderAndConnectionEnt)other;
      valueConsumer.accept("spaceGroups", Pair.create(getSpaceGroups(), e.getSpaceGroups()));
      valueConsumer.accept("type", Pair.create(getType(), e.getType()));
      valueConsumer.accept("hostname", Pair.create(getHostname(), e.getHostname()));
      valueConsumer.accept("isCommunityHub", Pair.create(isCommunityHub(), e.isCommunityHub()));
      valueConsumer.accept("id", Pair.create(getId(), e.getId()));
      valueConsumer.accept("name", Pair.create(getName(), e.getName()));
      valueConsumer.accept("connected", Pair.create(isConnected(), e.isConnected()));
      valueConsumer.accept("connectionMode", Pair.create(getConnectionMode(), e.getConnectionMode()));
      valueConsumer.accept("user", Pair.create(getUser(), e.getUser()));
  }

    /**
     * The builder for the entity.
     */
    public interface SpaceProviderAndConnectionEntBuilder extends GatewayEntityBuilder<SpaceProviderAndConnectionEnt> {

        /**
         * The groups contained in this provider to which the user have access to.
         * 
         * @param spaceGroups the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        SpaceProviderAndConnectionEntBuilder setSpaceGroups(java.util.List<SpaceGroupEnt> spaceGroups);
        
        /**
         * Type of the space provider.
         * 
         * @param type the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        SpaceProviderAndConnectionEntBuilder setType(TypeEnum type);
        
        /**
         * host of the SpaceProvider, absent if not applicable
         * 
         * @param hostname the property value,  
         * @return this entity builder for chaining
         */
        SpaceProviderAndConnectionEntBuilder setHostname(String hostname);
        
        /**
         * Whether this provider is the Community Hub
         * 
         * @param isCommunityHub the property value,  
         * @return this entity builder for chaining
         */
        SpaceProviderAndConnectionEntBuilder setIsCommunityHub(Boolean isCommunityHub);
        
        /**
   		 * Set id
         * 
         * @param id the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        SpaceProviderAndConnectionEntBuilder setId(String id);
        
        /**
   		 * Set name
         * 
         * @param name the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        SpaceProviderAndConnectionEntBuilder setName(String name);
        
        /**
   		 * Set connected
         * 
         * @param connected the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        SpaceProviderAndConnectionEntBuilder setConnected(Boolean connected);
        
        /**
   		 * Set connectionMode
         * 
         * @param connectionMode the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        SpaceProviderAndConnectionEntBuilder setConnectionMode(ConnectionModeEnum connectionMode);
        
        /**
   		 * Set user
         * 
         * @param user the property value,  
         * @return this entity builder for chaining
         */
        SpaceProviderAndConnectionEntBuilder setUser(UserEnt user);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        SpaceProviderAndConnectionEnt build();
    
    }

}
