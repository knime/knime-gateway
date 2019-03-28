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

import com.knime.gateway.entity.JavaObjectEnt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


import com.knime.gateway.entity.MetaNodeDialogCompEnt;
import com.knime.gateway.entity.impl.DefaultMetaNodeDialogCompEnt;
import com.knime.gateway.entity.impl.DefaultMetaNodeDialogCompEnt.DefaultMetaNodeDialogCompEntBuilder;

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
@javax.annotation.Generated(value = {"org.knime.gateway.codegen.GatewayCodegen", "src-gen/com.knime.gateway.json-config.json"})
public interface MetaNodeDialogCompEntMixIn extends MetaNodeDialogCompEnt {

    @Override
    @JsonIgnore
    public String getTypeID();

    @Override
    @JsonProperty("paramName")
    public String getParamName();
    
    @Override
    @JsonProperty("nodeID")
    public String getNodeID();
    
    @Override
    @JsonProperty("isHideInDialog")
    public Boolean isIsHideInDialog();
    
    @Override
    @JsonProperty("representation")
    public JavaObjectEnt getRepresentation();
    

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
        @JsonProperty("paramName")
        public MetaNodeDialogCompEntMixInBuilder setParamName(final String paramName);
        
        @Override
        @JsonProperty("nodeID")
        public MetaNodeDialogCompEntMixInBuilder setNodeID(final String nodeID);
        
        @Override
        @JsonProperty("isHideInDialog")
        public MetaNodeDialogCompEntMixInBuilder setIsHideInDialog(final Boolean isHideInDialog);
        
        @Override
        @JsonProperty("representation")
        public MetaNodeDialogCompEntMixInBuilder setRepresentation(final JavaObjectEnt representation);
        
    }


}

