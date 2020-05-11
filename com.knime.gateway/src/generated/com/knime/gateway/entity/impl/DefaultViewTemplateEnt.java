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


import com.knime.gateway.entity.ViewTemplateEnt;

/**
 * DefaultViewTemplateEnt
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"org.knime.gateway.codegen.GatewayCodegen", "src-gen/com.knime.gateway-implementations-config.json"})
public class DefaultViewTemplateEnt  implements ViewTemplateEnt {

  protected java.util.List<String> m_javascriptLibraries;
  protected java.util.List<String> m_stylesheets;
  protected String m_namespace;
  protected String m_initMethodName;
  protected String m_validateMethodName;
  protected String m_setValidationErrorMethodName;
  protected String m_getViewValueMethodName;
  
  protected DefaultViewTemplateEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "ViewTemplate";
  }
  
  private DefaultViewTemplateEnt(DefaultViewTemplateEntBuilder builder) {
    
    m_javascriptLibraries = immutable(builder.m_javascriptLibraries);
    m_stylesheets = immutable(builder.m_stylesheets);
    m_namespace = immutable(builder.m_namespace);
    m_initMethodName = immutable(builder.m_initMethodName);
    m_validateMethodName = immutable(builder.m_validateMethodName);
    m_setValidationErrorMethodName = immutable(builder.m_setValidationErrorMethodName);
    m_getViewValueMethodName = immutable(builder.m_getViewValueMethodName);
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
        DefaultViewTemplateEnt ent = (DefaultViewTemplateEnt)o;
        return Objects.equals(m_javascriptLibraries, ent.m_javascriptLibraries) && Objects.equals(m_stylesheets, ent.m_stylesheets) && Objects.equals(m_namespace, ent.m_namespace) && Objects.equals(m_initMethodName, ent.m_initMethodName) && Objects.equals(m_validateMethodName, ent.m_validateMethodName) && Objects.equals(m_setValidationErrorMethodName, ent.m_setValidationErrorMethodName) && Objects.equals(m_getViewValueMethodName, ent.m_getViewValueMethodName);
    }


  @Override
  public java.util.List<String> getJavascriptLibraries() {
        return m_javascriptLibraries;
    }
    
  @Override
  public java.util.List<String> getStylesheets() {
        return m_stylesheets;
    }
    
  @Override
  public String getNamespace() {
        return m_namespace;
    }
    
  @Override
  public String getInitMethodName() {
        return m_initMethodName;
    }
    
  @Override
  public String getValidateMethodName() {
        return m_validateMethodName;
    }
    
  @Override
  public String getSetValidationErrorMethodName() {
        return m_setValidationErrorMethodName;
    }
    
  @Override
  public String getGetViewValueMethodName() {
        return m_getViewValueMethodName;
    }
    
  
    public static class DefaultViewTemplateEntBuilder implements ViewTemplateEntBuilder {
    
        public DefaultViewTemplateEntBuilder(){
            
        }
    
        private java.util.List<String> m_javascriptLibraries = new java.util.ArrayList<>();
        private java.util.List<String> m_stylesheets = new java.util.ArrayList<>();
        private String m_namespace;
        private String m_initMethodName;
        private String m_validateMethodName;
        private String m_setValidationErrorMethodName;
        private String m_getViewValueMethodName;

        @Override
        public DefaultViewTemplateEntBuilder setJavascriptLibraries(java.util.List<String> javascriptLibraries) {
             m_javascriptLibraries = javascriptLibraries;
             return this;
        }

        @Override
        public DefaultViewTemplateEntBuilder setStylesheets(java.util.List<String> stylesheets) {
             m_stylesheets = stylesheets;
             return this;
        }

        @Override
        public DefaultViewTemplateEntBuilder setNamespace(String namespace) {
             m_namespace = namespace;
             return this;
        }

        @Override
        public DefaultViewTemplateEntBuilder setInitMethodName(String initMethodName) {
             m_initMethodName = initMethodName;
             return this;
        }

        @Override
        public DefaultViewTemplateEntBuilder setValidateMethodName(String validateMethodName) {
             m_validateMethodName = validateMethodName;
             return this;
        }

        @Override
        public DefaultViewTemplateEntBuilder setSetValidationErrorMethodName(String setValidationErrorMethodName) {
             m_setValidationErrorMethodName = setValidationErrorMethodName;
             return this;
        }

        @Override
        public DefaultViewTemplateEntBuilder setGetViewValueMethodName(String getViewValueMethodName) {
             m_getViewValueMethodName = getViewValueMethodName;
             return this;
        }

        
        @Override
        public DefaultViewTemplateEnt build() {
            return new DefaultViewTemplateEnt(this);
        }
    
    }

}
