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


import com.knime.gateway.v0.entity.MetaNodeDialogComp_representationEnt;
import com.knime.gateway.v0.entity.impl.DefaultMetaNodeDialogComp_representationEnt;
import com.knime.gateway.v0.entity.impl.DefaultMetaNodeDialogComp_representationEnt.DefaultMetaNodeDialogComp_representationEntBuilder;

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
    defaultImpl = DefaultMetaNodeDialogComp_representationEnt.class)
@JsonSubTypes({
    @Type(value = DefaultMetaNodeDialogComp_representationEnt.class, name="MetaNodeDialogComp_representation")
})
@JsonDeserialize(builder=DefaultMetaNodeDialogComp_representationEntBuilder.class)
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public interface MetaNodeDialogComp_representationEntMixIn extends MetaNodeDialogComp_representationEnt {

    @Override
    @JsonIgnore
    public String getTypeID();

    @Override
    @JsonProperty("classname")
    public String getClassname();
    
    @Override
    @JsonProperty("content")
    public String getContent();
    

    /**
     * MixIn class for entity builder implementations that adds jackson annotations for the de-/serialization.
     *
     * @author Martin Horn, University of Konstanz
     */
    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "",
        defaultImpl = DefaultMetaNodeDialogComp_representationEntBuilder.class)
    @JsonSubTypes({
        @Type(value = DefaultMetaNodeDialogComp_representationEnt.DefaultMetaNodeDialogComp_representationEntBuilder.class, name="MetaNodeDialogComp_representation")
    })
    // AUTO-GENERATED CODE; DO NOT MODIFY
    public static interface MetaNodeDialogComp_representationEntMixInBuilder extends MetaNodeDialogComp_representationEntBuilder {
    
        @Override
        public MetaNodeDialogComp_representationEntMixIn build();
    
        @Override
        @JsonProperty("classname")
        public MetaNodeDialogComp_representationEntMixInBuilder setClassname(final String classname);
        
        @Override
        @JsonProperty("content")
        public MetaNodeDialogComp_representationEntMixInBuilder setContent(final String content);
        
    }


}

