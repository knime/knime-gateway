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

import com.knime.gateway.v0.entity.MetaNodeDialogCompEnt;


import com.knime.gateway.json.JsonUtil;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


import com.knime.gateway.v0.entity.MetaNodeDialogEnt;
import com.knime.gateway.v0.entity.impl.DefaultMetaNodeDialogEnt;
import com.knime.gateway.v0.entity.impl.DefaultMetaNodeDialogEnt.DefaultMetaNodeDialogEntBuilder;

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
    defaultImpl = DefaultMetaNodeDialogEnt.class)
@JsonSubTypes({
    @Type(value = DefaultMetaNodeDialogEnt.class, name="MetaNodeDialog")
})
@JsonDeserialize(builder=DefaultMetaNodeDialogEntBuilder.class)
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public interface MetaNodeDialogEntMixIn extends MetaNodeDialogEnt {

    @Override
    @JsonIgnore
    public String getTypeID();

    @Override
    @JsonProperty("components")
    public java.util.List<MetaNodeDialogCompEnt> getComponents();
    

    /**
     * MixIn class for entity builder implementations that adds jackson annotations for the de-/serialization.
     *
     * @author Martin Horn, University of Konstanz
     */
    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "",
        defaultImpl = DefaultMetaNodeDialogEntBuilder.class)
    @JsonSubTypes({
        @Type(value = DefaultMetaNodeDialogEnt.DefaultMetaNodeDialogEntBuilder.class, name="MetaNodeDialog")
    })
    // AUTO-GENERATED CODE; DO NOT MODIFY
    public static interface MetaNodeDialogEntMixInBuilder extends MetaNodeDialogEntBuilder {
    
        @Override
        public MetaNodeDialogEntMixIn build();
    
        @Override
        @JsonProperty("components")
        public MetaNodeDialogEntMixInBuilder setComponents(final java.util.List<MetaNodeDialogCompEnt> components);
        
    }


}

