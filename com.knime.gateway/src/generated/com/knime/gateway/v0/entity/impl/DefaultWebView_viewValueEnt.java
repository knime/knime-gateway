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
package com.knime.gateway.v0.entity.impl;

import static com.knime.gateway.util.DefaultEntUtil.immutable;

import java.util.Objects;


import com.knime.gateway.v0.entity.WebView_viewValueEnt;

/**
 * The view&#39;s value.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public class DefaultWebView_viewValueEnt  implements WebView_viewValueEnt {

  protected String m_classname;
  protected String m_content;
  
  protected DefaultWebView_viewValueEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "WebView_viewValue";
  }
  
  private DefaultWebView_viewValueEnt(DefaultWebView_viewValueEntBuilder builder) {
    
    m_classname = immutable(builder.m_classname);
    m_content = immutable(builder.m_content);
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
        DefaultWebView_viewValueEnt ent = (DefaultWebView_viewValueEnt)o;
        return Objects.equals(m_classname, ent.m_classname) && Objects.equals(m_content, ent.m_content);
    }


  @Override
  public String getClassname() {
        return m_classname;
    }
    
  @Override
  public String getContent() {
        return m_content;
    }
    
  
    public static class DefaultWebView_viewValueEntBuilder implements WebView_viewValueEntBuilder {
    
        public DefaultWebView_viewValueEntBuilder(){
            
        }
    
        private String m_classname = null;
        private String m_content = null;

        @Override
        public DefaultWebView_viewValueEntBuilder setClassname(String classname) {
             m_classname = classname;
             return this;
        }

        @Override
        public DefaultWebView_viewValueEntBuilder setContent(String content) {
             m_content = content;
             return this;
        }

        
        @Override
        public DefaultWebView_viewValueEnt build() {
            return new DefaultWebView_viewValueEnt(this);
        }
    
    }

}
