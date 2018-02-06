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
package org.knime.gateway.v0.entity.impl;

import org.knime.gateway.v0.entity.BoundsEnt;
import org.knime.gateway.v0.entity.StyleRangeEnt;

import org.knime.gateway.v0.entity.AnnotationEnt;

/**
 * A text annotation.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public class DefaultAnnotationEnt  implements AnnotationEnt {

  protected String m_type;
  protected String m_text;
  protected Integer m_backgroundColor;
  protected BoundsEnt m_bounds;
  protected String m_textAlignment;
  protected Integer m_borderSize;
  protected Integer m_borderColor;
  protected Integer m_defaultFontSize;
  protected Integer m_version;
  protected java.util.List<StyleRangeEnt> m_styleRanges;
  
  protected DefaultAnnotationEnt() {
    //for sub-classes
  }
  
  private DefaultAnnotationEnt(DefaultAnnotationEntBuilder builder) {
    
    if(builder.m_type == null) {
        throw new IllegalArgumentException("type must not be null.");
    }
    m_type = builder.m_type;
    m_text = builder.m_text;
    m_backgroundColor = builder.m_backgroundColor;
    m_bounds = builder.m_bounds;
    m_textAlignment = builder.m_textAlignment;
    m_borderSize = builder.m_borderSize;
    m_borderColor = builder.m_borderColor;
    m_defaultFontSize = builder.m_defaultFontSize;
    m_version = builder.m_version;
    m_styleRanges = builder.m_styleRanges;
  }


  @Override
  public String getType() {
        return m_type;
    }
    
  @Override
  public String getText() {
        return m_text;
    }
    
  @Override
  public Integer getBackgroundColor() {
        return m_backgroundColor;
    }
    
  @Override
  public BoundsEnt getBounds() {
        return m_bounds;
    }
    
  @Override
  public String getTextAlignment() {
        return m_textAlignment;
    }
    
  @Override
  public Integer getBorderSize() {
        return m_borderSize;
    }
    
  @Override
  public Integer getBorderColor() {
        return m_borderColor;
    }
    
  @Override
  public Integer getDefaultFontSize() {
        return m_defaultFontSize;
    }
    
  @Override
  public Integer getVersion() {
        return m_version;
    }
    
  @Override
  public java.util.List<StyleRangeEnt> getStyleRanges() {
        return m_styleRanges;
    }
    
  
    public static class DefaultAnnotationEntBuilder implements AnnotationEntBuilder {
    
        public DefaultAnnotationEntBuilder(){
            
        }
    
        private String m_type = null;
        private String m_text = null;
        private Integer m_backgroundColor = null;
        private BoundsEnt m_bounds;
        private String m_textAlignment = null;
        private Integer m_borderSize = null;
        private Integer m_borderColor = null;
        private Integer m_defaultFontSize = null;
        private Integer m_version = null;
        private java.util.List<StyleRangeEnt> m_styleRanges = new java.util.ArrayList<>();

        @Override
        public DefaultAnnotationEntBuilder setType(String type) {
             if(type == null) {
                 throw new IllegalArgumentException("type must not be null.");
             }
             m_type = type;
             return this;
        }

        @Override
        public DefaultAnnotationEntBuilder setText(String text) {
             m_text = text;
             return this;
        }

        @Override
        public DefaultAnnotationEntBuilder setBackgroundColor(Integer backgroundColor) {
             m_backgroundColor = backgroundColor;
             return this;
        }

        @Override
        public DefaultAnnotationEntBuilder setBounds(BoundsEnt bounds) {
             m_bounds = bounds;
             return this;
        }

        @Override
        public DefaultAnnotationEntBuilder setTextAlignment(String textAlignment) {
             m_textAlignment = textAlignment;
             return this;
        }

        @Override
        public DefaultAnnotationEntBuilder setBorderSize(Integer borderSize) {
             m_borderSize = borderSize;
             return this;
        }

        @Override
        public DefaultAnnotationEntBuilder setBorderColor(Integer borderColor) {
             m_borderColor = borderColor;
             return this;
        }

        @Override
        public DefaultAnnotationEntBuilder setDefaultFontSize(Integer defaultFontSize) {
             m_defaultFontSize = defaultFontSize;
             return this;
        }

        @Override
        public DefaultAnnotationEntBuilder setVersion(Integer version) {
             m_version = version;
             return this;
        }

        @Override
        public DefaultAnnotationEntBuilder setStyleRanges(java.util.List<StyleRangeEnt> styleRanges) {
             m_styleRanges = styleRanges;
             return this;
        }

        
        @Override
        public DefaultAnnotationEnt build() {
            return new DefaultAnnotationEnt(this);
        }
    
    }

}
