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

import com.knime.gateway.v0.entity.ViewData_viewRepresentationEnt;
import com.knime.gateway.v0.entity.ViewData_viewValueEnt;


import com.knime.gateway.json.JsonUtil;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


import com.knime.gateway.v0.entity.ViewDataEnt;
import com.knime.gateway.v0.entity.impl.DefaultViewDataEnt;
import com.knime.gateway.v0.entity.impl.DefaultViewDataEnt.DefaultViewDataEntBuilder;

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
    defaultImpl = DefaultViewDataEnt.class)
@JsonSubTypes({
    @Type(value = DefaultViewDataEnt.class, name="ViewData")
})
@JsonDeserialize(builder=DefaultViewDataEntBuilder.class)
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public interface ViewDataEntMixIn extends ViewDataEnt {

    @Override
    @JsonIgnore
    public String getTypeID();

    @Override
    @JsonProperty("javascriptObjectID")
    public String getJavascriptObjectID();
    
    @Override
    @JsonProperty("viewRepresentation")
    public ViewData_viewRepresentationEnt getViewRepresentation();
    
    @Override
    @JsonProperty("viewValue")
    public ViewData_viewValueEnt getViewValue();
    
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
        defaultImpl = DefaultViewDataEntBuilder.class)
    @JsonSubTypes({
        @Type(value = DefaultViewDataEnt.DefaultViewDataEntBuilder.class, name="ViewData")
    })
    // AUTO-GENERATED CODE; DO NOT MODIFY
    public static interface ViewDataEntMixInBuilder extends ViewDataEntBuilder {
    
        @Override
        public ViewDataEntMixIn build();
    
        @Override
        @JsonProperty("javascriptObjectID")
        public ViewDataEntMixInBuilder setJavascriptObjectID(final String javascriptObjectID);
        
        @Override
        @JsonProperty("viewRepresentation")
        public ViewDataEntMixInBuilder setViewRepresentation(final ViewData_viewRepresentationEnt viewRepresentation);
        
        @Override
        @JsonProperty("viewValue")
        public ViewDataEntMixInBuilder setViewValue(final ViewData_viewValueEnt viewValue);
        
        @Override
        @JsonProperty("viewHTMLPath")
        public ViewDataEntMixInBuilder setViewHTMLPath(final String viewHTMLPath);
        
        @Override
        @JsonProperty("hideInWizard")
        public ViewDataEntMixInBuilder setHideInWizard(final Boolean hideInWizard);
        
    }


}

