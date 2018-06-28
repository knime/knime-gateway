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

import com.knime.gateway.v0.entity.MetaNodeDialogComp_configEnt;
import com.knime.gateway.v0.entity.MetaNodeDialogComp_representationEnt;
import com.knime.gateway.v0.entity.MetaNodeDialogComp_valueEnt;


import com.knime.gateway.json.JsonUtil;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


import com.knime.gateway.v0.entity.MetaNodeDialogCompEnt;
import com.knime.gateway.v0.entity.impl.DefaultMetaNodeDialogCompEnt;
import com.knime.gateway.v0.entity.impl.DefaultMetaNodeDialogCompEnt.DefaultMetaNodeDialogCompEntBuilder;

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
    defaultImpl = DefaultMetaNodeDialogCompEnt.class)
@JsonSubTypes({
    @Type(value = DefaultMetaNodeDialogCompEnt.class, name="MetaNodeDialogComp")
})
@JsonDeserialize(builder=DefaultMetaNodeDialogCompEntBuilder.class)
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public interface MetaNodeDialogCompEntMixIn extends MetaNodeDialogCompEnt {

    @Override
    @JsonIgnore
    public String getTypeID();

    @Override
    @JsonProperty("nodeID")
    public String getNodeID();
    
    @Override
    @JsonProperty("isHideInDialog")
    public Boolean isIsHideInDialog();
    
    @Override
    @JsonProperty("representation")
    public MetaNodeDialogComp_representationEnt getRepresentation();
    
    @Override
    @JsonProperty("value")
    public MetaNodeDialogComp_valueEnt getValue();
    
    @Override
    @JsonProperty("config")
    public MetaNodeDialogComp_configEnt getConfig();
    

    /**
     * MixIn class for entity builder implementations that adds jackson annotations for the de-/serialization.
     *
     * @author Martin Horn, University of Konstanz
     */
    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "",
        defaultImpl = DefaultMetaNodeDialogCompEntBuilder.class)
    @JsonSubTypes({
        @Type(value = DefaultMetaNodeDialogCompEnt.DefaultMetaNodeDialogCompEntBuilder.class, name="MetaNodeDialogComp")
    })
    // AUTO-GENERATED CODE; DO NOT MODIFY
    public static interface MetaNodeDialogCompEntMixInBuilder extends MetaNodeDialogCompEntBuilder {
    
        @Override
        public MetaNodeDialogCompEntMixIn build();
    
        @Override
        @JsonProperty("nodeID")
        public MetaNodeDialogCompEntMixInBuilder setNodeID(final String nodeID);
        
        @Override
        @JsonProperty("isHideInDialog")
        public MetaNodeDialogCompEntMixInBuilder setIsHideInDialog(final Boolean isHideInDialog);
        
        @Override
        @JsonProperty("representation")
        public MetaNodeDialogCompEntMixInBuilder setRepresentation(final MetaNodeDialogComp_representationEnt representation);
        
        @Override
        @JsonProperty("value")
        public MetaNodeDialogCompEntMixInBuilder setValue(final MetaNodeDialogComp_valueEnt value);
        
        @Override
        @JsonProperty("config")
        public MetaNodeDialogCompEntMixInBuilder setConfig(final MetaNodeDialogComp_configEnt config);
        
    }


}

