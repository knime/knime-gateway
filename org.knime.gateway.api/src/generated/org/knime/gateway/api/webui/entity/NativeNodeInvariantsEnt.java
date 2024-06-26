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

import org.knime.gateway.api.webui.entity.ExtensionEnt;
import org.knime.gateway.api.webui.entity.NodeFactoryKeyEnt;

import java.util.function.BiConsumer;

import org.knime.core.util.Pair;

import org.knime.gateway.api.entity.GatewayEntityBuilder;


import org.knime.gateway.api.entity.GatewayEntity;

/**
 * Static properties of a native node which remain the same even if the node is not part of a workflow.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
public interface NativeNodeInvariantsEnt extends GatewayEntity {

  /**
   * The type of the node.
   */
  public enum TypeEnum {
    SOURCE("Source"),
    
    SINK("Sink"),
    
    LEARNER("Learner"),
    
    PREDICTOR("Predictor"),
    
    MANIPULATOR("Manipulator"),
    
    VISUALIZER("Visualizer"),
    
    WIDGET("Widget"),
    
    LOOPSTART("LoopStart"),
    
    LOOPEND("LoopEnd"),
    
    SCOPESTART("ScopeStart"),
    
    SCOPEEND("ScopeEnd"),
    
    QUICKFORM("QuickForm"),
    
    CONFIGURATION("Configuration"),
    
    OTHER("Other"),
    
    MISSING("Missing"),
    
    FORBIDDEN("Forbidden"),
    
    UNKNOWN("Unknown"),
    
    SUBNODE("Subnode"),
    
    VIRTUALIN("VirtualIn"),
    
    VIRTUALOUT("VirtualOut"),
    
    CONTAINER("Container");

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
   * The node&#39;s name.
   * @return name , never <code>null</code>
   **/
  public String getName();

  /**
   * The type of the node.
   * @return type , never <code>null</code>
   **/
  public TypeEnum getType();

  /**
   * The icon encoded in a data-url.
   * @return icon 
   **/
  public String getIcon();

  /**
   * Get nodeFactory
   * @return nodeFactory 
   **/
  public NodeFactoryKeyEnt getNodeFactory();

  /**
   * Get extension
   * @return extension 
   **/
  public ExtensionEnt getExtension();


  @Override
  default void forEachPropertyValue(final GatewayEntity other,
      final BiConsumer<String, Pair<Object, Object>> valueConsumer) {
      var e = (NativeNodeInvariantsEnt)other;
      valueConsumer.accept("name", Pair.create(getName(), e.getName()));
      valueConsumer.accept("type", Pair.create(getType(), e.getType()));
      valueConsumer.accept("icon", Pair.create(getIcon(), e.getIcon()));
      valueConsumer.accept("nodeFactory", Pair.create(getNodeFactory(), e.getNodeFactory()));
      valueConsumer.accept("extension", Pair.create(getExtension(), e.getExtension()));
  }

    /**
     * The builder for the entity.
     */
    public interface NativeNodeInvariantsEntBuilder extends GatewayEntityBuilder<NativeNodeInvariantsEnt> {

        /**
         * The node&#39;s name.
         * 
         * @param name the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        NativeNodeInvariantsEntBuilder setName(String name);
        
        /**
         * The type of the node.
         * 
         * @param type the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        NativeNodeInvariantsEntBuilder setType(TypeEnum type);
        
        /**
         * The icon encoded in a data-url.
         * 
         * @param icon the property value,  
         * @return this entity builder for chaining
         */
        NativeNodeInvariantsEntBuilder setIcon(String icon);
        
        /**
   		 * Set nodeFactory
         * 
         * @param nodeFactory the property value,  
         * @return this entity builder for chaining
         */
        NativeNodeInvariantsEntBuilder setNodeFactory(NodeFactoryKeyEnt nodeFactory);
        
        /**
   		 * Set extension
         * 
         * @param extension the property value,  
         * @return this entity builder for chaining
         */
        NativeNodeInvariantsEntBuilder setExtension(ExtensionEnt extension);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        NativeNodeInvariantsEnt build();
    
    }

}
