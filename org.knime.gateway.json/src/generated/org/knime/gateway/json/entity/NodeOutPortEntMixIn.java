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
package org.knime.gateway.json.entity;

import org.knime.gateway.api.entity.PortTypeEnt;
import org.knime.gateway.json.entity.NodePortEntMixIn;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


import org.knime.gateway.api.entity.NodeOutPortEnt;
import org.knime.gateway.impl.entity.DefaultNodeOutPortEnt;
import org.knime.gateway.impl.entity.DefaultNodeOutPortEnt.DefaultNodeOutPortEntBuilder;

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
    defaultImpl = DefaultNodeOutPortEnt.class)
@JsonSubTypes({
    @Type(value = DefaultNodeOutPortEnt.class, name="NodeOutPort")
})
@JsonDeserialize(builder=DefaultNodeOutPortEntBuilder.class)
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/org.knime.gateway.json-config.json"})
public interface NodeOutPortEntMixIn extends NodeOutPortEnt {

    @Override
    @JsonIgnore
    public String getTypeID();

    @Override
    @JsonProperty("type")
    public String getType();
    
    @Override
    @JsonProperty("portIndex")
    public Integer getPortIndex();
    
    @Override
    @JsonProperty("portType")
    public PortTypeEnt getPortType();
    
    @Override
    @JsonProperty("portName")
    public String getPortName();
    
    @Override
    @JsonProperty("summary")
    public String getSummary();
    
    @Override
    @JsonProperty("inactive")
    public Boolean isInactive();
    

    /**
     * MixIn class for entity builder implementations that adds jackson annotations for the de-/serialization.
     *
     * @author Martin Horn, University of Konstanz
     */
    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "",
        defaultImpl = DefaultNodeOutPortEntBuilder.class)
    @JsonSubTypes({
        @Type(value = DefaultNodeOutPortEnt.DefaultNodeOutPortEntBuilder.class, name="NodeOutPort")
    })
    // AUTO-GENERATED CODE; DO NOT MODIFY
    public static interface NodeOutPortEntMixInBuilder extends NodeOutPortEntBuilder {
    
        @Override
        public NodeOutPortEntMixIn build();
    
        @Override
        @JsonProperty("type")
        public NodeOutPortEntMixInBuilder setType(final String type);
        
        @Override
        @JsonProperty("portIndex")
        public NodeOutPortEntMixInBuilder setPortIndex(final Integer portIndex);
        
        @Override
        @JsonProperty("portType")
        public NodeOutPortEntMixInBuilder setPortType(final PortTypeEnt portType);
        
        @Override
        @JsonProperty("portName")
        public NodeOutPortEntMixInBuilder setPortName(final String portName);
        
        @Override
        @JsonProperty("summary")
        public NodeOutPortEntMixInBuilder setSummary(final String summary);
        
        @Override
        @JsonProperty("inactive")
        public NodeOutPortEntMixInBuilder setInactive(final Boolean inactive);
        
    }


}
