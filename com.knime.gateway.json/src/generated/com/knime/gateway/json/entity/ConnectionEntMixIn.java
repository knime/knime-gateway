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

import com.knime.gateway.v0.entity.XYEnt;


import com.knime.gateway.json.JsonUtil;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


import com.knime.gateway.v0.entity.ConnectionEnt;
import com.knime.gateway.v0.entity.impl.DefaultConnectionEnt;
import com.knime.gateway.v0.entity.impl.DefaultConnectionEnt.DefaultConnectionEntBuilder;

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
    defaultImpl = DefaultConnectionEnt.class)
@JsonSubTypes({
    @Type(value = DefaultConnectionEnt.class, name="Connection")
})
@JsonDeserialize(builder=DefaultConnectionEntBuilder.class)
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public interface ConnectionEntMixIn extends ConnectionEnt {

    @Override
    @JsonIgnore
    public String getTypeID();

    @Override
    @JsonProperty("dest")
    public String getDest();
    
    @Override
    @JsonProperty("destPort")
    public Integer getDestPort();
    
    @Override
    @JsonProperty("source")
    public String getSource();
    
    @Override
    @JsonProperty("sourcePort")
    public Integer getSourcePort();
    
    @Override
    @JsonProperty("deletable")
    public Boolean isDeletable();
    
    @Override
    @JsonProperty("flowVariablePortConnection")
    public Boolean isFlowVariablePortConnection();
    
    @Override
    @JsonProperty("bendPoints")
    public java.util.List<XYEnt> getBendPoints();
    
    @Override
    @JsonProperty("type")
    public TypeEnum getType();
    

    /**
     * MixIn class for entity builder implementations that adds jackson annotations for the de-/serialization.
     *
     * @author Martin Horn, University of Konstanz
     */
    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "",
        defaultImpl = DefaultConnectionEntBuilder.class)
    @JsonSubTypes({
        @Type(value = DefaultConnectionEnt.DefaultConnectionEntBuilder.class, name="Connection")
    })
    // AUTO-GENERATED CODE; DO NOT MODIFY
    public static interface ConnectionEntMixInBuilder extends ConnectionEntBuilder {
    
        @Override
        public ConnectionEntMixIn build();
    
        @Override
        @JsonProperty("dest")
        public ConnectionEntMixInBuilder setDest(final String dest);
        
        @Override
        @JsonProperty("destPort")
        public ConnectionEntMixInBuilder setDestPort(final Integer destPort);
        
        @Override
        @JsonProperty("source")
        public ConnectionEntMixInBuilder setSource(final String source);
        
        @Override
        @JsonProperty("sourcePort")
        public ConnectionEntMixInBuilder setSourcePort(final Integer sourcePort);
        
        @Override
        @JsonProperty("deletable")
        public ConnectionEntMixInBuilder setDeletable(final Boolean deletable);
        
        @Override
        @JsonProperty("flowVariablePortConnection")
        public ConnectionEntMixInBuilder setFlowVariablePortConnection(final Boolean flowVariablePortConnection);
        
        @Override
        @JsonProperty("bendPoints")
        public ConnectionEntMixInBuilder setBendPoints(final java.util.List<XYEnt> bendPoints);
        
        @Override
        @JsonProperty("type")
        public ConnectionEntMixInBuilder setType(final TypeEnum type);
        
    }


}

