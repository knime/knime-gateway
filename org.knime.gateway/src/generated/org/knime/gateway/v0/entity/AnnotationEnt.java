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
package org.knime.gateway.v0.entity;

import org.knime.gateway.v0.entity.BoundsEnt;
import org.knime.gateway.v0.entity.StyleRangeEnt;

import org.knime.gateway.entity.GatewayEntityBuilder;


import org.knime.gateway.entity.GatewayEntity;

/**
 * A text annotation.
 * 
 * @author Martin Horn, University of Konstanz
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen", date = "2018-01-10T17:43:16.679+01:00")
public interface AnnotationEnt extends GatewayEntity {


  /**
   * Discriminator for inheritance. Must be the base name of this type/schema.
   * @return type , never <code>null</code>
   **/
  public String getType();

  /**
   * Get text
   * @return text 
   **/
  public String getText();

  /**
   * Get backgroundColor
   * @return backgroundColor 
   **/
  public Integer getBackgroundColor();

  /**
   * Get bounds
   * @return bounds 
   **/
  public BoundsEnt getBounds();

  /**
   * Get textAlignment
   * @return textAlignment 
   **/
  public String getTextAlignment();

  /**
   * Get borderSize
   * @return borderSize 
   **/
  public Integer getBorderSize();

  /**
   * Get borderColor
   * @return borderColor 
   **/
  public Integer getBorderColor();

  /**
   * Get defaultFontSize
   * @return defaultFontSize 
   **/
  public Integer getDefaultFontSize();

  /**
   * Get version
   * @return version 
   **/
  public Integer getVersion();

  /**
   * Defines ranges of different styles within the annotation.
   * @return styleRanges 
   **/
  public java.util.List<StyleRangeEnt> getStyleRanges();


    /**
     * The builder for the entity.
     */
    public interface AnnotationEntBuilder extends GatewayEntityBuilder<AnnotationEnt> {

        /**
         * Discriminator for inheritance. Must be the base name of this type/schema.
         * 
         * @param type the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        AnnotationEntBuilder setType(String type);
        
        /**
         * 
         * @param text the property value,  
         * @return this entity builder for chaining
         */
        AnnotationEntBuilder setText(String text);
        
        /**
         * 
         * @param backgroundColor the property value,  
         * @return this entity builder for chaining
         */
        AnnotationEntBuilder setBackgroundColor(Integer backgroundColor);
        
        /**
         * 
         * @param bounds the property value,  
         * @return this entity builder for chaining
         */
        AnnotationEntBuilder setBounds(BoundsEnt bounds);
        
        /**
         * 
         * @param textAlignment the property value,  
         * @return this entity builder for chaining
         */
        AnnotationEntBuilder setTextAlignment(String textAlignment);
        
        /**
         * 
         * @param borderSize the property value,  
         * @return this entity builder for chaining
         */
        AnnotationEntBuilder setBorderSize(Integer borderSize);
        
        /**
         * 
         * @param borderColor the property value,  
         * @return this entity builder for chaining
         */
        AnnotationEntBuilder setBorderColor(Integer borderColor);
        
        /**
         * 
         * @param defaultFontSize the property value,  
         * @return this entity builder for chaining
         */
        AnnotationEntBuilder setDefaultFontSize(Integer defaultFontSize);
        
        /**
         * 
         * @param version the property value,  
         * @return this entity builder for chaining
         */
        AnnotationEntBuilder setVersion(Integer version);
        
        /**
         * Defines ranges of different styles within the annotation.
         * 
         * @param styleRanges the property value,  
         * @return this entity builder for chaining
         */
        AnnotationEntBuilder setStyleRanges(java.util.List<StyleRangeEnt> styleRanges);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        AnnotationEnt build();
    
    }

}
