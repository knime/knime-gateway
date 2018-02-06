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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


import com.knime.gateway.v0.entity.BoundsEnt;
import com.knime.gateway.v0.entity.impl.DefaultBoundsEnt;
import com.knime.gateway.v0.entity.impl.DefaultBoundsEnt.DefaultBoundsEntBuilder;

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
    defaultImpl = DefaultBoundsEnt.class)
@JsonSubTypes({
    @Type(value = DefaultBoundsEnt.class, name="Bounds")
})
@JsonDeserialize(builder=DefaultBoundsEntBuilder.class)
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public interface BoundsEntMixIn extends BoundsEnt {

    @Override
    @JsonProperty("x")
    public Integer getX();
    
    @Override
    @JsonProperty("y")
    public Integer getY();
    
    @Override
    @JsonProperty("width")
    public Integer getWidth();
    
    @Override
    @JsonProperty("height")
    public Integer getHeight();
    

    /**
     * MixIn class for entity builder implementations that adds jackson annotations for the de-/serialization.
     *
     * @author Martin Horn, University of Konstanz
     */
    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "",
        defaultImpl = DefaultBoundsEntBuilder.class)
    @JsonSubTypes({
        @Type(value = DefaultBoundsEnt.DefaultBoundsEntBuilder.class, name="Bounds")
    })
    // AUTO-GENERATED CODE; DO NOT MODIFY
    public static interface BoundsEntMixInBuilder extends BoundsEntBuilder {
    
        @Override
        public BoundsEntMixIn build();
    
        @Override
        @JsonProperty("x")
        public BoundsEntMixInBuilder setX(final Integer x);
        
        @Override
        @JsonProperty("y")
        public BoundsEntMixInBuilder setY(final Integer y);
        
        @Override
        @JsonProperty("width")
        public BoundsEntMixInBuilder setWidth(final Integer width);
        
        @Override
        @JsonProperty("height")
        public BoundsEntMixInBuilder setHeight(final Integer height);
        
    }


}

