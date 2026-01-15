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

import org.knime.gateway.api.webui.entity.KaiInquiryOptionEnt;

import java.util.function.BiConsumer;

import org.knime.core.util.Pair;

import org.knime.gateway.api.entity.GatewayEntityBuilder;


import org.knime.gateway.api.entity.GatewayEntity;

/**
 * A structured inquiry from K-AI requesting user input.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
public interface KaiInquiryEnt extends GatewayEntity {

  /**
   * Type of inquiry.
   */
  public enum InquiryTypeEnum {
    PERMISSION("permission"),
    
    CONFIRMATION("confirmation");

    private String value;

    InquiryTypeEnum(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

  }


  /**
   * Unique identifier for correlating the response.
   * @return inquiryId , never <code>null</code>
   **/
  public String getInquiryId();

  /**
   * Type of inquiry.
   * @return inquiryType , never <code>null</code>
   **/
  public InquiryTypeEnum getInquiryType();

  /**
   * Title briefly describing the inquiry.
   * @return title , never <code>null</code>
   **/
  public String getTitle();

  /**
   * Additional information describing the inquiry and providing context.
   * @return description , never <code>null</code>
   **/
  public String getDescription();

  /**
   * Available choices.
   * @return options , never <code>null</code>
   **/
  public java.util.List<KaiInquiryOptionEnt> getOptions();

  /**
   * Auto-cancel after N seconds.
   * @return timeoutSeconds , never <code>null</code>
   **/
  public Integer getTimeoutSeconds();

  /**
   * Option to select on timeout.
   * @return defaultOptionId , never <code>null</code>
   **/
  public String getDefaultOptionId();

  /**
   * Optional object containing inquiry type-specific information that might be used by clients.
   * @return metadata 
   **/
  public java.util.Map<String, Object> getMetadata();


  @Override
  default void forEachPropertyValue(final GatewayEntity other,
      final BiConsumer<String, Pair<Object, Object>> valueConsumer) {
      var e = (KaiInquiryEnt)other;
      valueConsumer.accept("inquiryId", Pair.create(getInquiryId(), e.getInquiryId()));
      valueConsumer.accept("inquiryType", Pair.create(getInquiryType(), e.getInquiryType()));
      valueConsumer.accept("title", Pair.create(getTitle(), e.getTitle()));
      valueConsumer.accept("description", Pair.create(getDescription(), e.getDescription()));
      valueConsumer.accept("options", Pair.create(getOptions(), e.getOptions()));
      valueConsumer.accept("timeoutSeconds", Pair.create(getTimeoutSeconds(), e.getTimeoutSeconds()));
      valueConsumer.accept("defaultOptionId", Pair.create(getDefaultOptionId(), e.getDefaultOptionId()));
      valueConsumer.accept("metadata", Pair.create(getMetadata(), e.getMetadata()));
  }

    /**
     * The builder for the entity.
     */
    public interface KaiInquiryEntBuilder extends GatewayEntityBuilder<KaiInquiryEnt> {

        /**
         * Unique identifier for correlating the response.
         * 
         * @param inquiryId the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        KaiInquiryEntBuilder setInquiryId(String inquiryId);
        
        /**
         * Type of inquiry.
         * 
         * @param inquiryType the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        KaiInquiryEntBuilder setInquiryType(InquiryTypeEnum inquiryType);
        
        /**
         * Title briefly describing the inquiry.
         * 
         * @param title the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        KaiInquiryEntBuilder setTitle(String title);
        
        /**
         * Additional information describing the inquiry and providing context.
         * 
         * @param description the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        KaiInquiryEntBuilder setDescription(String description);
        
        /**
         * Available choices.
         * 
         * @param options the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        KaiInquiryEntBuilder setOptions(java.util.List<KaiInquiryOptionEnt> options);
        
        /**
         * Auto-cancel after N seconds.
         * 
         * @param timeoutSeconds the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        KaiInquiryEntBuilder setTimeoutSeconds(Integer timeoutSeconds);
        
        /**
         * Option to select on timeout.
         * 
         * @param defaultOptionId the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        KaiInquiryEntBuilder setDefaultOptionId(String defaultOptionId);
        
        /**
         * Optional object containing inquiry type-specific information that might be used by clients.
         * 
         * @param metadata the property value,  
         * @return this entity builder for chaining
         */
        KaiInquiryEntBuilder setMetadata(java.util.Map<String, Object> metadata);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        KaiInquiryEnt build();
    
    }

}
