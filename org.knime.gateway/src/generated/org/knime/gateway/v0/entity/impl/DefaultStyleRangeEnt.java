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
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
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


import org.knime.gateway.v0.entity.StyleRangeEnt;

/**
 * Defines the style of a range (e.g. within a workflow annotation).
 *
 * @author Martin Horn, University of Konstanz
 */
// AUTO-GENERATED CODE; DO NOT MODIFY
public class DefaultStyleRangeEnt  implements StyleRangeEnt {

  protected Integer m_start;
  protected Integer m_length;
  protected String m_fontName;
  protected FontStyleEnum m_fontStyle;
  protected Integer m_fontSize;
  protected Integer m_foregroundColor;
  
  protected DefaultStyleRangeEnt() {
    //for sub-classes
  }
  
  private DefaultStyleRangeEnt(DefaultStyleRangeEntBuilder builder) {
    
    m_start = builder.m_start;
    m_length = builder.m_length;
    m_fontName = builder.m_fontName;
    m_fontStyle = builder.m_fontStyle;
    m_fontSize = builder.m_fontSize;
    m_foregroundColor = builder.m_foregroundColor;
  }


  /**
   * Style range start.
   * @return start
   **/
  @Override
    public Integer getStart() {
        return m_start;
    }
  /**
   * Style range length.
   * @return length
   **/
  @Override
    public Integer getLength() {
        return m_length;
    }
  /**
   * Style range font name.
   * @return fontName
   **/
  @Override
    public String getFontName() {
        return m_fontName;
    }
  /**
   * The font style, e.g. normal, bold or italic.
   * @return fontStyle
   **/
  @Override
    public FontStyleEnum getFontStyle() {
        return m_fontStyle;
    }
  /**
   * Style range font size.
   * @return fontSize
   **/
  @Override
    public Integer getFontSize() {
        return m_fontSize;
    }
  /**
   * Style range foreground color.
   * @return foregroundColor
   **/
  @Override
    public Integer getForegroundColor() {
        return m_foregroundColor;
    }
  
    public static class DefaultStyleRangeEntBuilder implements StyleRangeEntBuilder {
    
        public DefaultStyleRangeEntBuilder(){
            
        }
    
        private Integer m_start;
        private Integer m_length;
        private String m_fontName;
        private FontStyleEnum m_fontStyle;
        private Integer m_fontSize;
        private Integer m_foregroundColor;

        @Override
        public DefaultStyleRangeEntBuilder setStart(Integer start) {
             m_start = start;
             return this;
        }
        @Override
        public DefaultStyleRangeEntBuilder setLength(Integer length) {
             m_length = length;
             return this;
        }
        @Override
        public DefaultStyleRangeEntBuilder setFontName(String fontName) {
             m_fontName = fontName;
             return this;
        }
        @Override
        public DefaultStyleRangeEntBuilder setFontStyle(FontStyleEnum fontStyle) {
             m_fontStyle = fontStyle;
             return this;
        }
        @Override
        public DefaultStyleRangeEntBuilder setFontSize(Integer fontSize) {
             m_fontSize = fontSize;
             return this;
        }
        @Override
        public DefaultStyleRangeEntBuilder setForegroundColor(Integer foregroundColor) {
             m_foregroundColor = foregroundColor;
             return this;
        }
        
        @Override
        public DefaultStyleRangeEnt build() {
            return new DefaultStyleRangeEnt(this);
        }
    
    }

}
