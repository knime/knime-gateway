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


import com.knime.gateway.v0.entity.PatchOpEnt;
import com.knime.gateway.v0.entity.impl.DefaultPatchOpEnt;
import com.knime.gateway.v0.entity.impl.DefaultPatchOpEnt.DefaultPatchOpEntBuilder;

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
    defaultImpl = DefaultPatchOpEnt.class)
@JsonSubTypes({
    @Type(value = DefaultPatchOpEnt.class, name="PatchOp")
})
@JsonDeserialize(builder=DefaultPatchOpEntBuilder.class)
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public interface PatchOpEntMixIn extends PatchOpEnt {

    @Override
    @JsonIgnore
    public String getTypeID();

    @Override
    @JsonProperty("op")
    public OpEnum getOp();
    
    @Override
    @JsonProperty("path")
    public String getPath();
    
    @Override
    @JsonProperty("value")
    public Object getValue();
    
    @Override
    @JsonProperty("from")
    public String getFrom();
    

    /**
     * MixIn class for entity builder implementations that adds jackson annotations for the de-/serialization.
     *
     * @author Martin Horn, University of Konstanz
     */
    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "",
        defaultImpl = DefaultPatchOpEntBuilder.class)
    @JsonSubTypes({
        @Type(value = DefaultPatchOpEnt.DefaultPatchOpEntBuilder.class, name="PatchOp")
    })
    // AUTO-GENERATED CODE; DO NOT MODIFY
    public static interface PatchOpEntMixInBuilder extends PatchOpEntBuilder {
    
        @Override
        public PatchOpEntMixIn build();
    
        @Override
        @JsonProperty("op")
        public PatchOpEntMixInBuilder setOp(final OpEnum op);
        
        @Override
        @JsonProperty("path")
        public PatchOpEntMixInBuilder setPath(final String path);
        
        @Override
        @JsonProperty("value")
        public PatchOpEntMixInBuilder setValue(final Object value);
        
        @Override
        @JsonProperty("from")
        public PatchOpEntMixInBuilder setFrom(final String from);
        
    }


}

