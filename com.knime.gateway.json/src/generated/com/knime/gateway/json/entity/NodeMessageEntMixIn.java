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


import com.knime.gateway.v0.entity.NodeMessageEnt;
import com.knime.gateway.v0.entity.impl.DefaultNodeMessageEnt;
import com.knime.gateway.v0.entity.impl.DefaultNodeMessageEnt.DefaultNodeMessageEntBuilder;

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
    defaultImpl = DefaultNodeMessageEnt.class)
@JsonSubTypes({
    @Type(value = DefaultNodeMessageEnt.class, name="NodeMessage")
})
@JsonDeserialize(builder=DefaultNodeMessageEntBuilder.class)
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public interface NodeMessageEntMixIn extends NodeMessageEnt {

    @Override
    @JsonIgnore
    public String getTypeID();

    @Override
    @JsonProperty("type")
    public String getType();
    
    @Override
    @JsonProperty("message")
    public String getMessage();
    

    /**
     * MixIn class for entity builder implementations that adds jackson annotations for the de-/serialization.
     *
     * @author Martin Horn, University of Konstanz
     */
    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "",
        defaultImpl = DefaultNodeMessageEntBuilder.class)
    @JsonSubTypes({
        @Type(value = DefaultNodeMessageEnt.DefaultNodeMessageEntBuilder.class, name="NodeMessage")
    })
    // AUTO-GENERATED CODE; DO NOT MODIFY
    public static interface NodeMessageEntMixInBuilder extends NodeMessageEntBuilder {
    
        @Override
        public NodeMessageEntMixIn build();
    
        @Override
        @JsonProperty("type")
        public NodeMessageEntMixInBuilder setType(final String type);
        
        @Override
        @JsonProperty("message")
        public NodeMessageEntMixInBuilder setMessage(final String message);
        
    }


}

