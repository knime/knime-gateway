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
package com.knime.gateway.json.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


import com.knime.gateway.entity.ViewTemplateEnt;
import com.knime.gateway.entity.impl.DefaultViewTemplateEnt;
import com.knime.gateway.entity.impl.DefaultViewTemplateEnt.DefaultViewTemplateEntBuilder;

/**
 * MixIn class for entity implementations that adds jackson annotations for de-/serialization.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "",
    visible = true,
    defaultImpl = DefaultViewTemplateEnt.class)
@JsonSubTypes({
    @Type(value = DefaultViewTemplateEnt.class, name="ViewTemplate")
})
@JsonDeserialize(builder=DefaultViewTemplateEntBuilder.class)
@javax.annotation.Generated(value = {"org.knime.gateway.codegen.GatewayCodegen", "src-gen/com.knime.gateway.json-config.json"})
public interface ViewTemplateEntMixIn extends ViewTemplateEnt {

    @Override
    @JsonIgnore
    public String getTypeID();

    @Override
    @JsonProperty("javascriptLibraries")
    public java.util.List<String> getJavascriptLibraries();
    
    @Override
    @JsonProperty("stylesheets")
    public java.util.List<String> getStylesheets();
    
    @Override
    @JsonProperty("namespace")
    public String getNamespace();
    
    @Override
    @JsonProperty("initMethodName")
    public String getInitMethodName();
    
    @Override
    @JsonProperty("validateMethodName")
    public String getValidateMethodName();
    
    @Override
    @JsonProperty("setValidationErrorMethodName")
    public String getSetValidationErrorMethodName();
    
    @Override
    @JsonProperty("getViewValueMethodName")
    public String getGetViewValueMethodName();
    

    /**
     * MixIn class for entity builder implementations that adds jackson annotations for the de-/serialization.
     *
     * @author Martin Horn, University of Konstanz
     */
    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "",
        defaultImpl = DefaultViewTemplateEntBuilder.class)
    @JsonSubTypes({
        @Type(value = DefaultViewTemplateEnt.DefaultViewTemplateEntBuilder.class, name="ViewTemplate")
    })
    // AUTO-GENERATED CODE; DO NOT MODIFY
    public static interface ViewTemplateEntMixInBuilder extends ViewTemplateEntBuilder {
    
        @Override
        public ViewTemplateEntMixIn build();
    
        @Override
        @JsonProperty("javascriptLibraries")
        public ViewTemplateEntMixInBuilder setJavascriptLibraries(final java.util.List<String> javascriptLibraries);
        
        @Override
        @JsonProperty("stylesheets")
        public ViewTemplateEntMixInBuilder setStylesheets(final java.util.List<String> stylesheets);
        
        @Override
        @JsonProperty("namespace")
        public ViewTemplateEntMixInBuilder setNamespace(final String namespace);
        
        @Override
        @JsonProperty("initMethodName")
        public ViewTemplateEntMixInBuilder setInitMethodName(final String initMethodName);
        
        @Override
        @JsonProperty("validateMethodName")
        public ViewTemplateEntMixInBuilder setValidateMethodName(final String validateMethodName);
        
        @Override
        @JsonProperty("setValidationErrorMethodName")
        public ViewTemplateEntMixInBuilder setSetValidationErrorMethodName(final String setValidationErrorMethodName);
        
        @Override
        @JsonProperty("getViewValueMethodName")
        public ViewTemplateEntMixInBuilder setGetViewValueMethodName(final String getViewValueMethodName);
        
    }


}

