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

import com.knime.gateway.v0.entity.PortTypeEnt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


import com.knime.gateway.v0.entity.PortObjectSpecEnt;
import com.knime.gateway.v0.entity.impl.DefaultPortObjectSpecEnt;
import com.knime.gateway.v0.entity.impl.DefaultPortObjectSpecEnt.DefaultPortObjectSpecEntBuilder;

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
    defaultImpl = DefaultPortObjectSpecEnt.class)
@JsonSubTypes({
    @Type(value = DefaultPortObjectSpecEnt.class, name="PortObjectSpec")
})
@JsonDeserialize(builder=DefaultPortObjectSpecEntBuilder.class)
@javax.annotation.Generated(value = {"org.knime.gateway.codegen.GatewayCodegen", "src-gen/com.knime.gateway.json-config.json"})
public interface PortObjectSpecEntMixIn extends PortObjectSpecEnt {

    @Override
    @JsonIgnore
    public String getTypeID();

    @Override
    @JsonProperty("className")
    public String getClassName();
    
    @Override
    @JsonProperty("portType")
    public PortTypeEnt getPortType();
    
    @Override
    @JsonProperty("representation")
    public String getRepresentation();
    
    @Override
    @JsonProperty("inactive")
    public Boolean isInactive();
    
    @Override
    @JsonProperty("problem")
    public Boolean isProblem();
    

    /**
     * MixIn class for entity builder implementations that adds jackson annotations for the de-/serialization.
     *
     * @author Martin Horn, University of Konstanz
     */
    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "",
        defaultImpl = DefaultPortObjectSpecEntBuilder.class)
    @JsonSubTypes({
        @Type(value = DefaultPortObjectSpecEnt.DefaultPortObjectSpecEntBuilder.class, name="PortObjectSpec")
    })
    // AUTO-GENERATED CODE; DO NOT MODIFY
    public static interface PortObjectSpecEntMixInBuilder extends PortObjectSpecEntBuilder {
    
        @Override
        public PortObjectSpecEntMixIn build();
    
        @Override
        @JsonProperty("className")
        public PortObjectSpecEntMixInBuilder setClassName(final String className);
        
        @Override
        @JsonProperty("portType")
        public PortObjectSpecEntMixInBuilder setPortType(final PortTypeEnt portType);
        
        @Override
        @JsonProperty("representation")
        public PortObjectSpecEntMixInBuilder setRepresentation(final String representation);
        
        @Override
        @JsonProperty("inactive")
        public PortObjectSpecEntMixInBuilder setInactive(final Boolean inactive);
        
        @Override
        @JsonProperty("problem")
        public PortObjectSpecEntMixInBuilder setProblem(final Boolean problem);
        
    }


}

