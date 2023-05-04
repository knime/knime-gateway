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

import org.knime.gateway.api.webui.entity.PortViewsEnt;

import java.util.function.BiConsumer;

import org.knime.core.util.Pair;

import org.knime.gateway.api.entity.GatewayEntityBuilder;


import org.knime.gateway.api.entity.GatewayEntity;

/**
 * Describes the type of a port.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
public interface PortTypeEnt extends GatewayEntity {

  /**
   * Gets or Sets kind
   */
  public enum KindEnum {
    TABLE("table"),
    
    FLOWVARIABLE("flowVariable"),
    
    GENERIC("generic"),
    
    OTHER("other");

    private String value;

    KindEnum(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

  }


  /**
   * A human-readable name for the port type.
   * @return name , never <code>null</code>
   **/
  public String getName();

  /**
   * Get kind
   * @return kind , never <code>null</code>
   **/
  public KindEnum getKind();

  /**
   * The color of the port. Only given if &#39;kind&#39; is &#39;other&#39;.
   * @return color 
   **/
  public String getColor();

  /**
   * List of port type ids this port type is compatible with (i.e. can be connected with). Not present if it&#39;s only compatible with itself. Only present if interaction info is supposed to be included. Only given if &#39;kind&#39; is &#39;other&#39;. Will never contain the &#39;generic&#39; port type since it&#39;s compatible with every port.
   * @return compatibleTypes 
   **/
  public java.util.List<String> getCompatibleTypes();

  /**
   * Whether this port type is hidden, e.g., from being actively selected by the user (e.g. for a component input/output). Will need to be shipped nevertheless to be able to just render hidden ports. This property is only present if true.
   * @return hidden 
   **/
  public Boolean isHidden();

  /**
   * Get views
   * @return views 
   **/
  public PortViewsEnt getViews();


  @Override
  default void forEachPropertyValue(final GatewayEntity other,
      final BiConsumer<String, Pair<Object, Object>> valueConsumer) {
      var e = (PortTypeEnt)other;
      valueConsumer.accept("name", Pair.create(getName(), e.getName()));
      valueConsumer.accept("kind", Pair.create(getKind(), e.getKind()));
      valueConsumer.accept("color", Pair.create(getColor(), e.getColor()));
      valueConsumer.accept("compatibleTypes", Pair.create(getCompatibleTypes(), e.getCompatibleTypes()));
      valueConsumer.accept("hidden", Pair.create(isHidden(), e.isHidden()));
      valueConsumer.accept("views", Pair.create(getViews(), e.getViews()));
  }

    /**
     * The builder for the entity.
     */
    public interface PortTypeEntBuilder extends GatewayEntityBuilder<PortTypeEnt> {

        /**
         * A human-readable name for the port type.
         * 
         * @param name the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        PortTypeEntBuilder setName(String name);
        
        /**
   		 * Set kind
         * 
         * @param kind the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        PortTypeEntBuilder setKind(KindEnum kind);
        
        /**
         * The color of the port. Only given if &#39;kind&#39; is &#39;other&#39;.
         * 
         * @param color the property value,  
         * @return this entity builder for chaining
         */
        PortTypeEntBuilder setColor(String color);
        
        /**
         * List of port type ids this port type is compatible with (i.e. can be connected with). Not present if it&#39;s only compatible with itself. Only present if interaction info is supposed to be included. Only given if &#39;kind&#39; is &#39;other&#39;. Will never contain the &#39;generic&#39; port type since it&#39;s compatible with every port.
         * 
         * @param compatibleTypes the property value,  
         * @return this entity builder for chaining
         */
        PortTypeEntBuilder setCompatibleTypes(java.util.List<String> compatibleTypes);
        
        /**
         * Whether this port type is hidden, e.g., from being actively selected by the user (e.g. for a component input/output). Will need to be shipped nevertheless to be able to just render hidden ports. This property is only present if true.
         * 
         * @param hidden the property value,  
         * @return this entity builder for chaining
         */
        PortTypeEntBuilder setHidden(Boolean hidden);
        
        /**
   		 * Set views
         * 
         * @param views the property value,  
         * @return this entity builder for chaining
         */
        PortTypeEntBuilder setViews(PortViewsEnt views);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        PortTypeEnt build();
    
    }

}
