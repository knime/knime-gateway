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


import com.knime.gateway.entity.XYEnt;
import com.knime.gateway.entity.impl.DefaultXYEnt;
import com.knime.gateway.entity.impl.DefaultXYEnt.DefaultXYEntBuilder;

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
    defaultImpl = DefaultXYEnt.class)
@JsonSubTypes({
    @Type(value = DefaultXYEnt.class, name="XY")
})
@JsonDeserialize(builder=DefaultXYEntBuilder.class)
@javax.annotation.Generated(value = {"org.knime.gateway.codegen.GatewayCodegen", "src-gen/com.knime.gateway.json-config.json"})
public interface XYEntMixIn extends XYEnt {

    @Override
    @JsonIgnore
    public String getTypeID();

    @Override
    @JsonProperty("x")
    public Integer getX();
    
    @Override
    @JsonProperty("y")
    public Integer getY();
    

    /**
     * MixIn class for entity builder implementations that adds jackson annotations for the de-/serialization.
     *
     * @author Martin Horn, University of Konstanz
     */
    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "",
        defaultImpl = DefaultXYEntBuilder.class)
    @JsonSubTypes({
        @Type(value = DefaultXYEnt.DefaultXYEntBuilder.class, name="XY")
    })
    // AUTO-GENERATED CODE; DO NOT MODIFY
    public static interface XYEntMixInBuilder extends XYEntBuilder {
    
        @Override
        public XYEntMixIn build();
    
        @Override
        @JsonProperty("x")
        public XYEntMixInBuilder setX(final Integer x);
        
        @Override
        @JsonProperty("y")
        public XYEntMixInBuilder setY(final Integer y);
        
    }


}
