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
package org.knime.gateway.json.webui.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


import org.knime.gateway.api.webui.entity.GatewayExceptionEnt;
import org.knime.gateway.impl.webui.entity.DefaultGatewayExceptionEnt;
import org.knime.gateway.impl.webui.entity.DefaultGatewayExceptionEnt.DefaultGatewayExceptionEntBuilder;

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
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.json-config.json"})
public interface GatewayExceptionEntMixIn extends GatewayExceptionEnt {

    @Override
    @JsonIgnore
    public String getTypeID();

    @Override
    @JsonProperty("exception-name")
    public String getExceptionName();
    
    @Override
    @JsonProperty("exception-message")
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
        @JsonProperty("exception-name")
        public GatewayExceptionEntMixInBuilder setExceptionName(final String exceptionName);
        
        @Override
        @JsonProperty("exception-message")
        public GatewayExceptionEntMixInBuilder setExceptionMessage(final String exceptionMessage);
        
    }


}

