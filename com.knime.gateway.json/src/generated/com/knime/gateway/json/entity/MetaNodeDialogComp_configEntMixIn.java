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


import com.knime.gateway.v0.entity.MetaNodeDialogComp_configEnt;
import com.knime.gateway.v0.entity.impl.DefaultMetaNodeDialogComp_configEnt;
import com.knime.gateway.v0.entity.impl.DefaultMetaNodeDialogComp_configEnt.DefaultMetaNodeDialogComp_configEntBuilder;

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
    defaultImpl = DefaultMetaNodeDialogComp_configEnt.class)
@JsonSubTypes({
    @Type(value = DefaultMetaNodeDialogComp_configEnt.class, name="MetaNodeDialogComp_config")
})
@JsonDeserialize(builder=DefaultMetaNodeDialogComp_configEntBuilder.class)
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public interface MetaNodeDialogComp_configEntMixIn extends MetaNodeDialogComp_configEnt {

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
        defaultImpl = DefaultMetaNodeDialogComp_configEntBuilder.class)
    @JsonSubTypes({
        @Type(value = DefaultMetaNodeDialogComp_configEnt.DefaultMetaNodeDialogComp_configEntBuilder.class, name="MetaNodeDialogComp_config")
    })
    // AUTO-GENERATED CODE; DO NOT MODIFY
    public static interface MetaNodeDialogComp_configEntMixInBuilder extends MetaNodeDialogComp_configEntBuilder {
    
        @Override
        public MetaNodeDialogComp_configEntMixIn build();
    
        @Override
        @JsonProperty("classname")
        public MetaNodeDialogComp_configEntMixInBuilder setClassname(final String classname);
        
        @Override
        @JsonProperty("content")
        public MetaNodeDialogComp_configEntMixInBuilder setContent(final String content);
        
    }


}

