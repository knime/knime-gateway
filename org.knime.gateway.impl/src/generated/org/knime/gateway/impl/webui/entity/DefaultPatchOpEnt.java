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


import org.knime.gateway.api.webui.entity.PatchOpEnt;

/**
 * A JSONPatch document as defined by RFC 6902
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public class DefaultPatchOpEnt  implements PatchOpEnt {

  protected OpEnum m_op;
  protected String m_path;
  protected Object m_value;
  protected String m_from;
  
  protected DefaultPatchOpEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "PatchOp";
  }
  
  private DefaultPatchOpEnt(DefaultPatchOpEntBuilder builder) {
    
    if(builder.m_op == null) {
        throw new IllegalArgumentException("op must not be null.");
    }
    m_op = immutable(builder.m_op);
    if(builder.m_path == null) {
        throw new IllegalArgumentException("path must not be null.");
    }
    m_path = immutable(builder.m_path);
    m_value = immutable(builder.m_value);
    m_from = immutable(builder.m_from);
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
        DefaultPatchOpEnt ent = (DefaultPatchOpEnt)o;
        return Objects.equals(m_op, ent.m_op) && Objects.equals(m_path, ent.m_path) && Objects.equals(m_value, ent.m_value) && Objects.equals(m_from, ent.m_from);
    }


  
   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
       return new HashCodeBuilder()
               .append(m_op)
               .append(m_path)
               .append(m_value)
               .append(m_from)
               .toHashCode();
   }
  
	
	
  @Override
  public OpEnum getOp() {
        return m_op;
  }
    
  @Override
  public String getPath() {
        return m_path;
  }
    
  @Override
  public Object getValue() {
        return m_value;
  }
    
  @Override
  public String getFrom() {
        return m_from;
  }
    
  
    public static class DefaultPatchOpEntBuilder implements PatchOpEntBuilder {
    
        public DefaultPatchOpEntBuilder(){
            
        }
    
        private OpEnum m_op;
        private String m_path;
        private Object m_value = null;
        private String m_from;

        @Override
        public DefaultPatchOpEntBuilder setOp(OpEnum op) {
             if(op == null) {
                 throw new IllegalArgumentException("op must not be null.");
             }
             m_op = op;
             return this;
        }

        @Override
        public DefaultPatchOpEntBuilder setPath(String path) {
             if(path == null) {
                 throw new IllegalArgumentException("path must not be null.");
             }
             m_path = path;
             return this;
        }

        @Override
        public DefaultPatchOpEntBuilder setValue(Object value) {
             m_value = value;
             return this;
        }

        @Override
        public DefaultPatchOpEntBuilder setFrom(String from) {
             m_from = from;
             return this;
        }

        
        @Override
        public DefaultPatchOpEnt build() {
            return new DefaultPatchOpEnt(this);
        }
    
    }

}
