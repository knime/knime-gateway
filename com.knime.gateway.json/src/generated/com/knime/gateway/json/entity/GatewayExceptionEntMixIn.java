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


import com.knime.gateway.v0.entity.GatewayExceptionEnt;
import com.knime.gateway.v0.entity.impl.DefaultGatewayExceptionEnt;
import com.knime.gateway.v0.entity.impl.DefaultGatewayExceptionEnt.DefaultGatewayExceptionEntBuilder;

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
    defaultImpl = DefaultGatewayExceptionEnt.class)
@JsonSubTypes({
    @Type(value = DefaultGatewayExceptionEnt.class, name="GatewayException")
})
@JsonDeserialize(builder=DefaultGatewayExceptionEntBuilder.class)
@javax.annotation.Generated(value = {"org.knime.gateway.codegen.GatewayCodegen", "src-gen/com.knime.gateway.json-config.json"})
public interface GatewayExceptionEntMixIn extends GatewayExceptionEnt {

    @Override
    @JsonIgnore
    public String getTypeID();

    @Override
    @JsonProperty("exceptionName")
    public String getExceptionName();
    
    @Override
    @JsonProperty("exceptionMessage")
    public String getExceptionMessage();
    

    /**
     * MixIn class for entity builder implementations that adds jackson annotations for the de-/serialization.
     *
     * @author Martin Horn, University of Konstanz
     */
    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "",
        defaultImpl = DefaultGatewayExceptionEntBuilder.class)
    @JsonSubTypes({
        @Type(value = DefaultGatewayExceptionEnt.DefaultGatewayExceptionEntBuilder.class, name="GatewayException")
    })
    // AUTO-GENERATED CODE; DO NOT MODIFY
    public static interface GatewayExceptionEntMixInBuilder extends GatewayExceptionEntBuilder {
    
        @Override
        public GatewayExceptionEntMixIn build();
    
        @Override
        @JsonProperty("exceptionName")
        public GatewayExceptionEntMixInBuilder setExceptionName(final String exceptionName);
        
        @Override
        @JsonProperty("exceptionMessage")
        public GatewayExceptionEntMixInBuilder setExceptionMessage(final String exceptionMessage);
        
    }


}

