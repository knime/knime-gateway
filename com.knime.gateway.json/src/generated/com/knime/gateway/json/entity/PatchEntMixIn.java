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

import com.knime.gateway.entity.PatchOpEnt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


import com.knime.gateway.entity.PatchEnt;
import com.knime.gateway.entity.impl.DefaultPatchEnt;
import com.knime.gateway.entity.impl.DefaultPatchEnt.DefaultPatchEntBuilder;

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
    defaultImpl = DefaultPatchEnt.class)
@JsonSubTypes({
    @Type(value = DefaultPatchEnt.class, name="Patch")
})
@JsonDeserialize(builder=DefaultPatchEntBuilder.class)
@javax.annotation.Generated(value = {"org.knime.gateway.codegen.GatewayCodegen", "src-gen/com.knime.gateway.json-config.json"})
public interface PatchEntMixIn extends PatchEnt {

    @Override
    @JsonIgnore
    public String getTypeID();

    @Override
    @JsonProperty("ops")
    public java.util.List<PatchOpEnt> getOps();
    
    @Override
    @JsonProperty("snapshotID")
    public java.util.UUID getSnapshotID();
    
    @Override
    @JsonProperty("targetTypeID")
    public String getTargetTypeID();
    

    /**
     * MixIn class for entity builder implementations that adds jackson annotations for the de-/serialization.
     *
     * @author Martin Horn, University of Konstanz
     */
    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "",
        defaultImpl = DefaultPatchEntBuilder.class)
    @JsonSubTypes({
        @Type(value = DefaultPatchEnt.DefaultPatchEntBuilder.class, name="Patch")
    })
    // AUTO-GENERATED CODE; DO NOT MODIFY
    public static interface PatchEntMixInBuilder extends PatchEntBuilder {
    
        @Override
        public PatchEntMixIn build();
    
        @Override
        @JsonProperty("ops")
        public PatchEntMixInBuilder setOps(final java.util.List<PatchOpEnt> ops);
        
        @Override
        @JsonProperty("snapshotID")
        public PatchEntMixInBuilder setSnapshotID(final java.util.UUID snapshotID);
        
        @Override
        @JsonProperty("targetTypeID")
        public PatchEntMixInBuilder setTargetTypeID(final String targetTypeID);
        
    }


}

