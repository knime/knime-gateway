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

import com.knime.gateway.v0.entity.PortTypeEnt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


import com.knime.gateway.v0.entity.MetaPortInfoEnt;
import com.knime.gateway.v0.entity.impl.DefaultMetaPortInfoEnt;
import com.knime.gateway.v0.entity.impl.DefaultMetaPortInfoEnt.DefaultMetaPortInfoEntBuilder;

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
    defaultImpl = DefaultMetaPortInfoEnt.class)
@JsonSubTypes({
    @Type(value = DefaultMetaPortInfoEnt.class, name="MetaPortInfo")
})
@JsonDeserialize(builder=DefaultMetaPortInfoEntBuilder.class)
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public interface MetaPortInfoEntMixIn extends MetaPortInfoEnt {

    @Override
    @JsonIgnore
    public String getTypeID();

    @Override
    @JsonProperty("portType")
    public PortTypeEnt getPortType();
    
    @Override
    @JsonProperty("connected")
    public Boolean isConnected();
    
    @Override
    @JsonProperty("message")
    public String getMessage();
    
    @Override
    @JsonProperty("oldIndex")
    public Integer getOldIndex();
    
    @Override
    @JsonProperty("newIndex")
    public Integer getNewIndex();
    

    /**
     * MixIn class for entity builder implementations that adds jackson annotations for the de-/serialization.
     *
     * @author Martin Horn, University of Konstanz
     */
    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "",
        defaultImpl = DefaultMetaPortInfoEntBuilder.class)
    @JsonSubTypes({
        @Type(value = DefaultMetaPortInfoEnt.DefaultMetaPortInfoEntBuilder.class, name="MetaPortInfo")
    })
    // AUTO-GENERATED CODE; DO NOT MODIFY
    public static interface MetaPortInfoEntMixInBuilder extends MetaPortInfoEntBuilder {
    
        @Override
        public MetaPortInfoEntMixIn build();
    
        @Override
        @JsonProperty("portType")
        public MetaPortInfoEntMixInBuilder setPortType(final PortTypeEnt portType);
        
        @Override
        @JsonProperty("connected")
        public MetaPortInfoEntMixInBuilder setConnected(final Boolean connected);
        
        @Override
        @JsonProperty("message")
        public MetaPortInfoEntMixInBuilder setMessage(final String message);
        
        @Override
        @JsonProperty("oldIndex")
        public MetaPortInfoEntMixInBuilder setOldIndex(final Integer oldIndex);
        
        @Override
        @JsonProperty("newIndex")
        public MetaPortInfoEntMixInBuilder setNewIndex(final Integer newIndex);
        
    }


}

