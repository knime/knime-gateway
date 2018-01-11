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

/**
 * Node dimensions - position and size.
 *
 * @author Martin Horn, University of Konstanz
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen", date = "2018-01-10T17:43:16.092+01:00")
public class DefaultBoundsEnt  implements BoundsEnt {

  protected Integer m_x;
  protected Integer m_y;
  protected Integer m_width;
  protected Integer m_height;
  
  protected DefaultBoundsEnt() {
    //for sub-classes
  }
  
  private DefaultBoundsEnt(DefaultBoundsEntBuilder builder) {
    
    m_x = builder.m_x;
    m_y = builder.m_y;
    m_width = builder.m_width;
    m_height = builder.m_height;
  }


  @Override
  public Integer getX() {
        return m_x;
    }
    
  @Override
  public Integer getY() {
        return m_y;
    }
    
  @Override
  public Integer getWidth() {
        return m_width;
    }
    
  @Override
  public Integer getHeight() {
        return m_height;
    }
    
  
    public static class DefaultBoundsEntBuilder implements BoundsEntBuilder {
    
        public DefaultBoundsEntBuilder(){
            
        }
    
        private Integer m_x = null;
        private Integer m_y = null;
        private Integer m_width = null;
        private Integer m_height = null;

        @Override
        public DefaultBoundsEntBuilder setX(Integer x) {
             m_x = x;
             return this;
        }

        @Override
        public DefaultBoundsEntBuilder setY(Integer y) {
             m_y = y;
             return this;
        }

        @Override
        public DefaultBoundsEntBuilder setWidth(Integer width) {
             m_width = width;
             return this;
        }

        @Override
        public DefaultBoundsEntBuilder setHeight(Integer height) {
             m_height = height;
             return this;
        }

        
        @Override
        public DefaultBoundsEnt build() {
            return new DefaultBoundsEnt(this);
        }
    
    }

}
