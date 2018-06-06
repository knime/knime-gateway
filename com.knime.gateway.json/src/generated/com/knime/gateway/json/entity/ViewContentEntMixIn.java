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


import com.knime.gateway.v0.entity.ViewContentEnt;
import com.knime.gateway.v0.entity.impl.DefaultViewContentEnt;
import com.knime.gateway.v0.entity.impl.DefaultViewContentEnt.DefaultViewContentEntBuilder;

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
    defaultImpl = DefaultViewContentEnt.class)
@JsonSubTypes({
    @Type(value = DefaultViewContentEnt.class, name="ViewContent")
})
@JsonDeserialize(builder=DefaultViewContentEntBuilder.class)
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public interface ViewContentEntMixIn extends ViewContentEnt {

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
        defaultImpl = DefaultViewContentEntBuilder.class)
    @JsonSubTypes({
        @Type(value = DefaultViewContentEnt.DefaultViewContentEntBuilder.class, name="ViewContent")
    })
    // AUTO-GENERATED CODE; DO NOT MODIFY
    public static interface ViewContentEntMixInBuilder extends ViewContentEntBuilder {
    
        @Override
        public ViewContentEntMixIn build();
    
        @Override
        @JsonProperty("classname")
        public ViewContentEntMixInBuilder setClassname(final String classname);
        
        @Override
        @JsonProperty("content")
        public ViewContentEntMixInBuilder setContent(final String content);
        
    }


}

