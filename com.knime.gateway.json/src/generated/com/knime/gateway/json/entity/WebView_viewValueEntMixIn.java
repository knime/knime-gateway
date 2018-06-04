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



import com.knime.gateway.json.JsonUtil;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


import com.knime.gateway.v0.entity.WebView_viewValueEnt;
import com.knime.gateway.v0.entity.impl.DefaultWebView_viewValueEnt;
import com.knime.gateway.v0.entity.impl.DefaultWebView_viewValueEnt.DefaultWebView_viewValueEntBuilder;

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
    defaultImpl = DefaultWebView_viewValueEnt.class)
@JsonSubTypes({
    @Type(value = DefaultWebView_viewValueEnt.class, name="WebView_viewValue")
})
@JsonDeserialize(builder=DefaultWebView_viewValueEntBuilder.class)
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public interface WebView_viewValueEntMixIn extends WebView_viewValueEnt {

    @Override
    @JsonIgnore
    public String getTypeID();

    @Override
    @JsonProperty("classname")
    public String getClassname();
    
    @Override
    @JsonProperty("content")
    public String getContent();
    

    /**
     * MixIn class for entity builder implementations that adds jackson annotations for the de-/serialization.
     *
     * @author Martin Horn, University of Konstanz
     */
    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "",
        defaultImpl = DefaultWebView_viewValueEntBuilder.class)
    @JsonSubTypes({
        @Type(value = DefaultWebView_viewValueEnt.DefaultWebView_viewValueEntBuilder.class, name="WebView_viewValue")
    })
    // AUTO-GENERATED CODE; DO NOT MODIFY
    public static interface WebView_viewValueEntMixInBuilder extends WebView_viewValueEntBuilder {
    
        @Override
        public WebView_viewValueEntMixIn build();
    
        @Override
        @JsonProperty("classname")
        public WebView_viewValueEntMixInBuilder setClassname(final String classname);
        
        @Override
        @JsonProperty("content")
        public WebView_viewValueEntMixInBuilder setContent(final String content);
        
    }


}

