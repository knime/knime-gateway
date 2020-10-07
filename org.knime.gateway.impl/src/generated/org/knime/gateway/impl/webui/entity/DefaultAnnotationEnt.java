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
package org.knime.gateway.impl.webui.entity;

import static org.knime.gateway.api.util.EntityUtil.immutable;

import java.util.Objects;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import org.knime.gateway.api.webui.entity.StyleRangeEnt;

import org.knime.gateway.api.webui.entity.AnnotationEnt;

/**
 * A text annotation.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public class DefaultAnnotationEnt  implements AnnotationEnt {

  protected String m_text;
  protected String m_backgroundColor;
  protected TextAlignEnum m_textAlign;
  protected Integer m_defaultFontSize;
  protected java.util.List<StyleRangeEnt> m_styleRanges;
  
  protected DefaultAnnotationEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "Annotation";
  }
  
  private DefaultAnnotationEnt(DefaultAnnotationEntBuilder builder) {
    
    if(builder.m_text == null) {
        throw new IllegalArgumentException("text must not be null.");
    }
    m_text = immutable(builder.m_text);
    m_backgroundColor = immutable(builder.m_backgroundColor);
    if(builder.m_textAlign == null) {
        throw new IllegalArgumentException("textAlign must not be null.");
    }
    m_textAlign = immutable(builder.m_textAlign);
    m_defaultFontSize = immutable(builder.m_defaultFontSize);
    if(builder.m_styleRanges == null) {
        throw new IllegalArgumentException("styleRanges must not be null.");
    }
    m_styleRanges = immutable(builder.m_styleRanges);
  }
  
   /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (getClass() != o.getClass()) {
            return false;
        }
        DefaultAnnotationEnt ent = (DefaultAnnotationEnt)o;
        return Objects.equals(m_text, ent.m_text) && Objects.equals(m_backgroundColor, ent.m_backgroundColor) && Objects.equals(m_textAlign, ent.m_textAlign) && Objects.equals(m_defaultFontSize, ent.m_defaultFontSize) && Objects.equals(m_styleRanges, ent.m_styleRanges);
    }


  
   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
       return new HashCodeBuilder()
               .append(m_text)
               .append(m_backgroundColor)
               .append(m_textAlign)
               .append(m_defaultFontSize)
               .append(m_styleRanges)
               .toHashCode();
   }
  
	
	
  @Override
  public String getText() {
        return m_text;
  }
    
  @Override
  public String getBackgroundColor() {
        return m_backgroundColor;
  }
    
  @Override
  public TextAlignEnum getTextAlign() {
        return m_textAlign;
  }
    
  @Override
  public Integer getDefaultFontSize() {
        return m_defaultFontSize;
  }
    
  @Override
  public java.util.List<StyleRangeEnt> getStyleRanges() {
        return m_styleRanges;
  }
    
  
    public static class DefaultAnnotationEntBuilder implements AnnotationEntBuilder {
    
        public DefaultAnnotationEntBuilder(){
            
        }
    
        private String m_text;
        private String m_backgroundColor;
        private TextAlignEnum m_textAlign;
        private Integer m_defaultFontSize;
        private java.util.List<StyleRangeEnt> m_styleRanges = new java.util.ArrayList<>();

        @Override
        public DefaultAnnotationEntBuilder setText(String text) {
             if(text == null) {
                 throw new IllegalArgumentException("text must not be null.");
             }
             m_text = text;
             return this;
        }

        @Override
        public DefaultAnnotationEntBuilder setBackgroundColor(String backgroundColor) {
             m_backgroundColor = backgroundColor;
             return this;
        }

        @Override
        public DefaultAnnotationEntBuilder setTextAlign(TextAlignEnum textAlign) {
             if(textAlign == null) {
                 throw new IllegalArgumentException("textAlign must not be null.");
             }
             m_textAlign = textAlign;
             return this;
        }

        @Override
        public DefaultAnnotationEntBuilder setDefaultFontSize(Integer defaultFontSize) {
             m_defaultFontSize = defaultFontSize;
             return this;
        }

        @Override
        public DefaultAnnotationEntBuilder setStyleRanges(java.util.List<StyleRangeEnt> styleRanges) {
             if(styleRanges == null) {
                 throw new IllegalArgumentException("styleRanges must not be null.");
             }
             m_styleRanges = styleRanges;
             return this;
        }

        
        @Override
        public DefaultAnnotationEnt build() {
            return new DefaultAnnotationEnt(this);
        }
    
    }

}
