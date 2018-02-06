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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


import com.knime.gateway.v0.entity.PortTypeEnt;
import com.knime.gateway.v0.entity.impl.DefaultPortTypeEnt;
import com.knime.gateway.v0.entity.impl.DefaultPortTypeEnt.DefaultPortTypeEntBuilder;

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
    defaultImpl = DefaultPortTypeEnt.class)
@JsonSubTypes({
    @Type(value = DefaultPortTypeEnt.class, name="PortType")
})
@JsonDeserialize(builder=DefaultPortTypeEntBuilder.class)
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public interface PortTypeEntMixIn extends PortTypeEnt {

    @Override
    @JsonProperty("portObjectClassName")
    public String getPortObjectClassName();
    
    @Override
    @JsonProperty("optional")
    public Boolean isOptional();
    

    /**
     * MixIn class for entity builder implementations that adds jackson annotations for the de-/serialization.
     *
     * @author Martin Horn, University of Konstanz
     */
    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "",
        defaultImpl = DefaultPortTypeEntBuilder.class)
    @JsonSubTypes({
        @Type(value = DefaultPortTypeEnt.DefaultPortTypeEntBuilder.class, name="PortType")
    })
    // AUTO-GENERATED CODE; DO NOT MODIFY
    public static interface PortTypeEntMixInBuilder extends PortTypeEntBuilder {
    
        @Override
        public PortTypeEntMixIn build();
    
        @Override
        @JsonProperty("portObjectClassName")
        public PortTypeEntMixInBuilder setPortObjectClassName(final String portObjectClassName);
        
        @Override
        @JsonProperty("optional")
        public PortTypeEntMixInBuilder setOptional(final Boolean optional);
        
    }


}

