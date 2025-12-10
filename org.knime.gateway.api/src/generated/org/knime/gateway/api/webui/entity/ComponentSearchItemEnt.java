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

import org.knime.gateway.api.webui.entity.ComponentSearchItemPortEnt;

import java.util.function.BiConsumer;

import org.knime.core.util.Pair;

import org.knime.gateway.api.entity.GatewayEntityBuilder;


import org.knime.gateway.api.entity.GatewayEntity;

/**
 * A result item of a component search on some Hub instance.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
public interface ComponentSearchItemEnt extends GatewayEntity {

  /**
   * The type (a.k.a. \&quot;kind\&quot;) of the component
   */
  public enum TypeEnum {
    SOURCE("Source"),
    
    MANIPULATOR("Manipulator"),
    
    VISUALIZER("Visualizer"),
    
    LEARNER("Learner"),
    
    SINK("Sink"),
    
    OTHER("Other");

    private String value;

    TypeEnum(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

  }


  /**
   * Space item ID of this component
   * @return id , never <code>null</code>
   **/
  public String getId();

  /**
   * The name of the component as given by the component creator
   * @return name , never <code>null</code>
   **/
  public String getName();

  /**
   * The description of the component as given by the component creator
   * @return description 
   **/
  public String getDescription();

  /**
   * serialised data of component icon
   * @return icon 
   **/
  public String getIcon();

  /**
   * The type (a.k.a. \&quot;kind\&quot;) of the component
   * @return type , never <code>null</code>
   **/
  public TypeEnum getType();

  /**
   * The component&#39;s input ports.
   * @return inPorts 
   **/
  public java.util.List<ComponentSearchItemPortEnt> getInPorts();

  /**
   * The component&#39;s output ports.
   * @return outPorts 
   **/
  public java.util.List<ComponentSearchItemPortEnt> getOutPorts();


  @Override
  default void forEachPropertyValue(final GatewayEntity other,
      final BiConsumer<String, Pair<Object, Object>> valueConsumer) {
      var e = (ComponentSearchItemEnt)other;
      valueConsumer.accept("id", Pair.create(getId(), e.getId()));
      valueConsumer.accept("name", Pair.create(getName(), e.getName()));
      valueConsumer.accept("description", Pair.create(getDescription(), e.getDescription()));
      valueConsumer.accept("icon", Pair.create(getIcon(), e.getIcon()));
      valueConsumer.accept("type", Pair.create(getType(), e.getType()));
      valueConsumer.accept("inPorts", Pair.create(getInPorts(), e.getInPorts()));
      valueConsumer.accept("outPorts", Pair.create(getOutPorts(), e.getOutPorts()));
  }

    /**
     * The builder for the entity.
     */
    public interface ComponentSearchItemEntBuilder extends GatewayEntityBuilder<ComponentSearchItemEnt> {

        /**
         * Space item ID of this component
         * 
         * @param id the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        ComponentSearchItemEntBuilder setId(String id);
        
        /**
         * The name of the component as given by the component creator
         * 
         * @param name the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        ComponentSearchItemEntBuilder setName(String name);
        
        /**
         * The description of the component as given by the component creator
         * 
         * @param description the property value,  
         * @return this entity builder for chaining
         */
        ComponentSearchItemEntBuilder setDescription(String description);
        
        /**
         * serialised data of component icon
         * 
         * @param icon the property value,  
         * @return this entity builder for chaining
         */
        ComponentSearchItemEntBuilder setIcon(String icon);
        
        /**
         * The type (a.k.a. \&quot;kind\&quot;) of the component
         * 
         * @param type the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        ComponentSearchItemEntBuilder setType(TypeEnum type);
        
        /**
         * The component&#39;s input ports.
         * 
         * @param inPorts the property value,  
         * @return this entity builder for chaining
         */
        ComponentSearchItemEntBuilder setInPorts(java.util.List<ComponentSearchItemPortEnt> inPorts);
        
        /**
         * The component&#39;s output ports.
         * 
         * @param outPorts the property value,  
         * @return this entity builder for chaining
         */
        ComponentSearchItemEntBuilder setOutPorts(java.util.List<ComponentSearchItemPortEnt> outPorts);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        ComponentSearchItemEnt build();
    
    }

}
