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
 * The link of a metanode or component.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
public interface TemplateLinkEnt extends GatewayEntity {

  /**
   * The status of the link of this metanode/component (UpToDate, HasUpdate, Error)
   */
  public enum UpdateStatusEnum {
    UP_TO_DATE("UP_TO_DATE"),
    
    HAS_UPDATE("HAS_UPDATE"),
    
    ERROR("ERROR");

    private String value;

    UpdateStatusEnum(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

  }


  /**
   * A URL, if the metanode/component is linked to.
   * @return url 
   **/
  public String getUrl();

  /**
   * The status of the link of this metanode/component (UpToDate, HasUpdate, Error)
   * @return updateStatus 
   **/
  public UpdateStatusEnum getUpdateStatus();

  /**
   * Whether this link type can be changed or not.
   * @return isLinkTypeChangeable 
   **/
  public Boolean isLinkTypeChangeable();

  /**
   * Whether this Hub item version can be changed. This can only be true for shared templates on a Hub.
   * @return isHubItemVersionChangeable 
   **/
  public Object getIsHubItemVersionChangeable();


  @Override
  default void forEachPropertyValue(final GatewayEntity other,
      final BiConsumer<String, Pair<Object, Object>> valueConsumer) {
      var e = (TemplateLinkEnt)other;
      valueConsumer.accept("url", Pair.create(getUrl(), e.getUrl()));
      valueConsumer.accept("updateStatus", Pair.create(getUpdateStatus(), e.getUpdateStatus()));
      valueConsumer.accept("isLinkTypeChangeable", Pair.create(isLinkTypeChangeable(), e.isLinkTypeChangeable()));
      valueConsumer.accept("isHubItemVersionChangeable", Pair.create(getIsHubItemVersionChangeable(), e.getIsHubItemVersionChangeable()));
  }

    /**
     * The builder for the entity.
     */
    public interface TemplateLinkEntBuilder extends GatewayEntityBuilder<TemplateLinkEnt> {

        /**
         * A URL, if the metanode/component is linked to.
         * 
         * @param url the property value,  
         * @return this entity builder for chaining
         */
        TemplateLinkEntBuilder setUrl(String url);
        
        /**
         * The status of the link of this metanode/component (UpToDate, HasUpdate, Error)
         * 
         * @param updateStatus the property value,  
         * @return this entity builder for chaining
         */
        TemplateLinkEntBuilder setUpdateStatus(UpdateStatusEnum updateStatus);
        
        /**
         * Whether this link type can be changed or not.
         * 
         * @param isLinkTypeChangeable the property value,  
         * @return this entity builder for chaining
         */
        TemplateLinkEntBuilder setIsLinkTypeChangeable(Boolean isLinkTypeChangeable);
        
        /**
         * Whether this Hub item version can be changed. This can only be true for shared templates on a Hub.
         * 
         * @param isHubItemVersionChangeable the property value,  
         * @return this entity builder for chaining
         */
        TemplateLinkEntBuilder setIsHubItemVersionChangeable(Object isHubItemVersionChangeable);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        TemplateLinkEnt build();
    
    }

}
