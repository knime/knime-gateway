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

import com.knime.gateway.v0.entity.WebView_viewRepresentationEnt;
import com.knime.gateway.v0.entity.WebView_viewValueEnt;

import com.knime.gateway.v0.entity.WebViewEnt;

/**
 * Represents a web view, e.g. Javascript.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public class DefaultWebViewEnt  implements WebViewEnt {

  protected String m_javascriptObjectID;
  protected WebView_viewRepresentationEnt m_viewRepresentation;
  protected WebView_viewValueEnt m_viewValue;
  protected String m_viewHTMLPath;
  protected Boolean m_hideInWizard;
  
  protected DefaultWebViewEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "WebView";
  }
  
  private DefaultWebViewEnt(DefaultWebViewEntBuilder builder) {
    
    m_javascriptObjectID = immutable(builder.m_javascriptObjectID);
    m_viewRepresentation = immutable(builder.m_viewRepresentation);
    m_viewValue = immutable(builder.m_viewValue);
    m_viewHTMLPath = immutable(builder.m_viewHTMLPath);
    m_hideInWizard = immutable(builder.m_hideInWizard);
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
        DefaultWebViewEnt ent = (DefaultWebViewEnt)o;
        return Objects.equals(m_javascriptObjectID, ent.m_javascriptObjectID) && Objects.equals(m_viewRepresentation, ent.m_viewRepresentation) && Objects.equals(m_viewValue, ent.m_viewValue) && Objects.equals(m_viewHTMLPath, ent.m_viewHTMLPath) && Objects.equals(m_hideInWizard, ent.m_hideInWizard);
    }


  @Override
  public String getJavascriptObjectID() {
        return m_javascriptObjectID;
    }
    
  @Override
  public WebView_viewRepresentationEnt getViewRepresentation() {
        return m_viewRepresentation;
    }
    
  @Override
  public WebView_viewValueEnt getViewValue() {
        return m_viewValue;
    }
    
  @Override
  public String getViewHTMLPath() {
        return m_viewHTMLPath;
    }
    
  @Override
  public Boolean isHideInWizard() {
        return m_hideInWizard;
    }
    
  
    public static class DefaultWebViewEntBuilder implements WebViewEntBuilder {
    
        public DefaultWebViewEntBuilder(){
            
        }
    
        private String m_javascriptObjectID = null;
        private WebView_viewRepresentationEnt m_viewRepresentation;
        private WebView_viewValueEnt m_viewValue;
        private String m_viewHTMLPath = null;
        private Boolean m_hideInWizard = null;

        @Override
        public DefaultWebViewEntBuilder setJavascriptObjectID(String javascriptObjectID) {
             m_javascriptObjectID = javascriptObjectID;
             return this;
        }

        @Override
        public DefaultWebViewEntBuilder setViewRepresentation(WebView_viewRepresentationEnt viewRepresentation) {
             m_viewRepresentation = viewRepresentation;
             return this;
        }

        @Override
        public DefaultWebViewEntBuilder setViewValue(WebView_viewValueEnt viewValue) {
             m_viewValue = viewValue;
             return this;
        }

        @Override
        public DefaultWebViewEntBuilder setViewHTMLPath(String viewHTMLPath) {
             m_viewHTMLPath = viewHTMLPath;
             return this;
        }

        @Override
        public DefaultWebViewEntBuilder setHideInWizard(Boolean hideInWizard) {
             m_hideInWizard = hideInWizard;
             return this;
        }

        
        @Override
        public DefaultWebViewEnt build() {
            return new DefaultWebViewEnt(this);
        }
    
    }

}
