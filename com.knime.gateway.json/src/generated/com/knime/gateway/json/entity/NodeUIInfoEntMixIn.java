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

import com.knime.gateway.v0.entity.BoundsEnt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


import com.knime.gateway.v0.entity.NodeUIInfoEnt;
import com.knime.gateway.v0.entity.impl.DefaultNodeUIInfoEnt;
import com.knime.gateway.v0.entity.impl.DefaultNodeUIInfoEnt.DefaultNodeUIInfoEntBuilder;

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
    defaultImpl = DefaultNodeUIInfoEnt.class)
@JsonSubTypes({
    @Type(value = DefaultNodeUIInfoEnt.class, name="NodeUIInfo")
})
@JsonDeserialize(builder=DefaultNodeUIInfoEntBuilder.class)
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public interface NodeUIInfoEntMixIn extends NodeUIInfoEnt {

    @Override
    @JsonIgnore
    public String getTypeID();

    @Override
    @JsonProperty("bounds")
    public BoundsEnt getBounds();
    
    @Override
    @JsonProperty("symbolRelative")
    public Boolean isSymbolRelative();
    
    @Override
    @JsonProperty("dropLocation")
    public Boolean isDropLocation();
    
    @Override
    @JsonProperty("snapToGrid")
    public Boolean isSnapToGrid();
    

    /**
     * MixIn class for entity builder implementations that adds jackson annotations for the de-/serialization.
     *
     * @author Martin Horn, University of Konstanz
     */
    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "",
        defaultImpl = DefaultNodeUIInfoEntBuilder.class)
    @JsonSubTypes({
        @Type(value = DefaultNodeUIInfoEnt.DefaultNodeUIInfoEntBuilder.class, name="NodeUIInfo")
    })
    // AUTO-GENERATED CODE; DO NOT MODIFY
    public static interface NodeUIInfoEntMixInBuilder extends NodeUIInfoEntBuilder {
    
        @Override
        public NodeUIInfoEntMixIn build();
    
        @Override
        @JsonProperty("bounds")
        public NodeUIInfoEntMixInBuilder setBounds(final BoundsEnt bounds);
        
        @Override
        @JsonProperty("symbolRelative")
        public NodeUIInfoEntMixInBuilder setSymbolRelative(final Boolean symbolRelative);
        
        @Override
        @JsonProperty("dropLocation")
        public NodeUIInfoEntMixInBuilder setDropLocation(final Boolean dropLocation);
        
        @Override
        @JsonProperty("snapToGrid")
        public NodeUIInfoEntMixInBuilder setSnapToGrid(final Boolean snapToGrid);
        
    }


}

