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

import org.knime.gateway.api.webui.entity.AnnotationEnt;
import org.knime.gateway.api.webui.entity.BoundsEnt;
import org.knime.gateway.api.webui.entity.StyleRangeEnt;
import org.knime.gateway.api.webui.entity.TypedTextEnt;

import java.util.function.BiConsumer;

import org.knime.core.util.Pair;

import org.knime.gateway.api.entity.GatewayEntityBuilder;


import org.knime.gateway.api.entity.GatewayEntity;

/**
 * A workflow annotation.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
public interface WorkflowAnnotationEnt extends GatewayEntity, AnnotationEnt {


  /**
   * Get bounds
   * @return bounds , never <code>null</code>
   **/
  public BoundsEnt getBounds();

  /**
   * A unique identifier for the workflow annotation.
   * @return id , never <code>null</code>
   **/
  public org.knime.gateway.api.entity.AnnotationIDEnt getId();

  /**
   * Get borderWidth
   * @return borderWidth , never <code>null</code>
   **/
  public Integer getBorderWidth();

  /**
   * A hex color string (rgb).
   * @return borderColor , never <code>null</code>
   **/
  public String getBorderColor();


  @Override
  default void forEachPropertyValue(final GatewayEntity other,
      final BiConsumer<String, Pair<Object, Object>> valueConsumer) {
      var e = (WorkflowAnnotationEnt)other;
      valueConsumer.accept("text", Pair.create(getText(), e.getText()));
      valueConsumer.accept("backgroundColor", Pair.create(getBackgroundColor(), e.getBackgroundColor()));
      valueConsumer.accept("textAlign", Pair.create(getTextAlign(), e.getTextAlign()));
      valueConsumer.accept("defaultFontSize", Pair.create(getDefaultFontSize(), e.getDefaultFontSize()));
      valueConsumer.accept("styleRanges", Pair.create(getStyleRanges(), e.getStyleRanges()));
      valueConsumer.accept("bounds", Pair.create(getBounds(), e.getBounds()));
      valueConsumer.accept("id", Pair.create(getId(), e.getId()));
      valueConsumer.accept("borderWidth", Pair.create(getBorderWidth(), e.getBorderWidth()));
      valueConsumer.accept("borderColor", Pair.create(getBorderColor(), e.getBorderColor()));
  }

    /**
     * The builder for the entity.
     */
    public interface WorkflowAnnotationEntBuilder extends GatewayEntityBuilder<WorkflowAnnotationEnt> {

        /**
   		 * Set text
         * 
         * @param text the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        WorkflowAnnotationEntBuilder setText(TypedTextEnt text);
        
        /**
         * The background color. If not given, the default background color needs to be used (which is usually opaque).
         * 
         * @param backgroundColor the property value,  
         * @return this entity builder for chaining
         */
        WorkflowAnnotationEntBuilder setBackgroundColor(String backgroundColor);
        
        /**
   		 * Set textAlign
         * 
         * @param textAlign the property value,  
         * @return this entity builder for chaining
         */
        WorkflowAnnotationEntBuilder setTextAlign(TextAlignEnum textAlign);
        
        /**
         * The default font size (in pt) for parts of the text where no style range is defined.
         * 
         * @param defaultFontSize the property value,  
         * @return this entity builder for chaining
         */
        WorkflowAnnotationEntBuilder setDefaultFontSize(Integer defaultFontSize);
        
        /**
         * Defines ranges of different styles within the annotation.
         * 
         * @param styleRanges the property value,  
         * @return this entity builder for chaining
         */
        WorkflowAnnotationEntBuilder setStyleRanges(java.util.List<StyleRangeEnt> styleRanges);
        
        /**
   		 * Set bounds
         * 
         * @param bounds the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        WorkflowAnnotationEntBuilder setBounds(BoundsEnt bounds);
        
        /**
         * A unique identifier for the workflow annotation.
         * 
         * @param id the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        WorkflowAnnotationEntBuilder setId(org.knime.gateway.api.entity.AnnotationIDEnt id);
        
        /**
   		 * Set borderWidth
         * 
         * @param borderWidth the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        WorkflowAnnotationEntBuilder setBorderWidth(Integer borderWidth);
        
        /**
         * A hex color string (rgb).
         * 
         * @param borderColor the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        WorkflowAnnotationEntBuilder setBorderColor(String borderColor);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        WorkflowAnnotationEnt build();
    
    }

}
