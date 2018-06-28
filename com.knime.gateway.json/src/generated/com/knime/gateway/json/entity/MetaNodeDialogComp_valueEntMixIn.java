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


import com.knime.gateway.v0.entity.MetaNodeDialogComp_valueEnt;
import com.knime.gateway.v0.entity.impl.DefaultMetaNodeDialogComp_valueEnt;
import com.knime.gateway.v0.entity.impl.DefaultMetaNodeDialogComp_valueEnt.DefaultMetaNodeDialogComp_valueEntBuilder;

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
    defaultImpl = DefaultMetaNodeDialogComp_valueEnt.class)
@JsonSubTypes({
    @Type(value = DefaultMetaNodeDialogComp_valueEnt.class, name="MetaNodeDialogComp_value")
})
@JsonDeserialize(builder=DefaultMetaNodeDialogComp_valueEntBuilder.class)
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public interface MetaNodeDialogComp_valueEntMixIn extends MetaNodeDialogComp_valueEnt {

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
        defaultImpl = DefaultMetaNodeDialogComp_valueEntBuilder.class)
    @JsonSubTypes({
        @Type(value = DefaultMetaNodeDialogComp_valueEnt.DefaultMetaNodeDialogComp_valueEntBuilder.class, name="MetaNodeDialogComp_value")
    })
    // AUTO-GENERATED CODE; DO NOT MODIFY
    public static interface MetaNodeDialogComp_valueEntMixInBuilder extends MetaNodeDialogComp_valueEntBuilder {
    
        @Override
        public MetaNodeDialogComp_valueEntMixIn build();
    
        @Override
        @JsonProperty("classname")
        public MetaNodeDialogComp_valueEntMixInBuilder setClassname(final String classname);
        
        @Override
        @JsonProperty("content")
        public MetaNodeDialogComp_valueEntMixInBuilder setContent(final String content);
        
    }


}

