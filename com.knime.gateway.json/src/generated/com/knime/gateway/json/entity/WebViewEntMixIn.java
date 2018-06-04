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

import com.knime.gateway.v0.entity.WebView_viewRepresentationEnt;
import com.knime.gateway.v0.entity.WebView_viewValueEnt;


import com.knime.gateway.json.JsonUtil;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


import com.knime.gateway.v0.entity.WebViewEnt;
import com.knime.gateway.v0.entity.impl.DefaultWebViewEnt;
import com.knime.gateway.v0.entity.impl.DefaultWebViewEnt.DefaultWebViewEntBuilder;

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
    defaultImpl = DefaultWebViewEnt.class)
@JsonSubTypes({
    @Type(value = DefaultWebViewEnt.class, name="WebView")
})
@JsonDeserialize(builder=DefaultWebViewEntBuilder.class)
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public interface WebViewEntMixIn extends WebViewEnt {

    @Override
    @JsonIgnore
    public String getTypeID();

    @Override
    @JsonProperty("javascriptObjectID")
    public String getJavascriptObjectID();
    
    @Override
    @JsonProperty("viewRepresentation")
    public WebView_viewRepresentationEnt getViewRepresentation();
    
    @Override
    @JsonProperty("viewValue")
    public WebView_viewValueEnt getViewValue();
    
    @Override
    @JsonProperty("viewHTMLPath")
    public String getViewHTMLPath();
    
    @Override
    @JsonProperty("hideInWizard")
    public Boolean isHideInWizard();
    

    /**
     * MixIn class for entity builder implementations that adds jackson annotations for the de-/serialization.
     *
     * @author Martin Horn, University of Konstanz
     */
    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "",
        defaultImpl = DefaultWebViewEntBuilder.class)
    @JsonSubTypes({
        @Type(value = DefaultWebViewEnt.DefaultWebViewEntBuilder.class, name="WebView")
    })
    // AUTO-GENERATED CODE; DO NOT MODIFY
    public static interface WebViewEntMixInBuilder extends WebViewEntBuilder {
    
        @Override
        public WebViewEntMixIn build();
    
        @Override
        @JsonProperty("javascriptObjectID")
        public WebViewEntMixInBuilder setJavascriptObjectID(final String javascriptObjectID);
        
        @Override
        @JsonProperty("viewRepresentation")
        public WebViewEntMixInBuilder setViewRepresentation(final WebView_viewRepresentationEnt viewRepresentation);
        
        @Override
        @JsonProperty("viewValue")
        public WebViewEntMixInBuilder setViewValue(final WebView_viewValueEnt viewValue);
        
        @Override
        @JsonProperty("viewHTMLPath")
        public WebViewEntMixInBuilder setViewHTMLPath(final String viewHTMLPath);
        
        @Override
        @JsonProperty("hideInWizard")
        public WebViewEntMixInBuilder setHideInWizard(final Boolean hideInWizard);
        
    }


}

