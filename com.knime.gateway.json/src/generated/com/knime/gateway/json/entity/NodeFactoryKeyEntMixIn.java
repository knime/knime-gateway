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


import com.knime.gateway.v0.entity.NodeFactoryKeyEnt;
import com.knime.gateway.v0.entity.impl.DefaultNodeFactoryKeyEnt;
import com.knime.gateway.v0.entity.impl.DefaultNodeFactoryKeyEnt.DefaultNodeFactoryKeyEntBuilder;

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
    defaultImpl = DefaultNodeFactoryKeyEnt.class)
@JsonSubTypes({
    @Type(value = DefaultNodeFactoryKeyEnt.class, name="NodeFactoryKey")
})
@JsonDeserialize(builder=DefaultNodeFactoryKeyEntBuilder.class)
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public interface NodeFactoryKeyEntMixIn extends NodeFactoryKeyEnt {

    @Override
    @JsonIgnore
    public String getTypeID();

    @Override
    @JsonProperty("className")
    public String getClassName();
    
    @Override
    @JsonProperty("settings")
    public String getSettings();
    

    /**
     * MixIn class for entity builder implementations that adds jackson annotations for the de-/serialization.
     *
     * @author Martin Horn, University of Konstanz
     */
    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "",
        defaultImpl = DefaultNodeFactoryKeyEntBuilder.class)
    @JsonSubTypes({
        @Type(value = DefaultNodeFactoryKeyEnt.DefaultNodeFactoryKeyEntBuilder.class, name="NodeFactoryKey")
    })
    // AUTO-GENERATED CODE; DO NOT MODIFY
    public static interface NodeFactoryKeyEntMixInBuilder extends NodeFactoryKeyEntBuilder {
    
        @Override
        public NodeFactoryKeyEntMixIn build();
    
        @Override
        @JsonProperty("className")
        public NodeFactoryKeyEntMixInBuilder setClassName(final String className);
        
        @Override
        @JsonProperty("settings")
        public NodeFactoryKeyEntMixInBuilder setSettings(final String settings);
        
    }


}

