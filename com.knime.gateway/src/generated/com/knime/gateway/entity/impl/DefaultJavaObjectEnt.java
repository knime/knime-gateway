/* ------------------------------------------------------------------
 * This source code, its documentation and all appendant files
 * are protected by copyright law. All rights reserved.
 *
 * Copyright by KNIME AG, Zurich, Switzerland
 *
 * You may not modify, publish, transmit, transfer or sell, reproduce,
 * create derivative works from, distribute, perform, display, or in
 * any way exploit any of the content, in whole or in part, except as
 * otherwise expressly permitted in writing by the copyright owner or
 * as specified in the license file distributed with this product.
 *
 * If you have any questions please contact the copyright holder:
 * website: www.knime.com
 * email: contact@knime.com
 * ---------------------------------------------------------------------
 *
 */
package com.knime.gateway.entity.impl;

import static com.knime.gateway.util.EntityUtil.immutable;

import java.util.Objects;


import com.knime.gateway.entity.JavaObjectEnt;

/**
 * A java object/class that can be deserialized from a string.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"org.knime.gateway.codegen.GatewayCodegen", "src-gen/com.knime.gateway-implementations-config.json"})
public class DefaultJavaObjectEnt  implements JavaObjectEnt {

  protected String m_classname;
  protected String m_jsonContent;
  
  protected DefaultJavaObjectEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "JavaObject";
  }
  
  private DefaultJavaObjectEnt(DefaultJavaObjectEntBuilder builder) {
    
    m_classname = immutable(builder.m_classname);
    m_jsonContent = immutable(builder.m_jsonContent);
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
        DefaultJavaObjectEnt ent = (DefaultJavaObjectEnt)o;
        return Objects.equals(m_classname, ent.m_classname) && Objects.equals(m_jsonContent, ent.m_jsonContent);
    }


  @Override
  public String getClassname() {
        return m_classname;
    }
    
  @Override
  public String getJsonContent() {
        return m_jsonContent;
    }
    
  
    public static class DefaultJavaObjectEntBuilder implements JavaObjectEntBuilder {
    
        public DefaultJavaObjectEntBuilder(){
            
        }
    
        private String m_classname;
        private String m_jsonContent;

        @Override
        public DefaultJavaObjectEntBuilder setClassname(String classname) {
             m_classname = classname;
             return this;
        }

        @Override
        public DefaultJavaObjectEntBuilder setJsonContent(String jsonContent) {
             m_jsonContent = jsonContent;
             return this;
        }

        
        @Override
        public DefaultJavaObjectEnt build() {
            return new DefaultJavaObjectEnt(this);
        }
    
    }

}
