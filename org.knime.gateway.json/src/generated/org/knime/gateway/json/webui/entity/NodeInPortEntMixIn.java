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
package org.knime.gateway.json.webui.entity;

import org.knime.gateway.api.webui.entity.PortTypeEnt;
import org.knime.gateway.json.webui.entity.NodePortEntMixIn;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


import org.knime.gateway.api.webui.entity.NodeInPortEnt;
import org.knime.gateway.impl.webui.entity.DefaultNodeInPortEnt;
import org.knime.gateway.impl.webui.entity.DefaultNodeInPortEnt.DefaultNodeInPortEntBuilder;

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
    defaultImpl = DefaultNodeInPortEnt.class)
@JsonSubTypes({
    @Type(value = DefaultNodeInPortEnt.class, name="NodeInPort")
})
@JsonDeserialize(builder=DefaultNodeInPortEntBuilder.class)
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.json-config.json"})
public interface NodeInPortEntMixIn extends NodeInPortEnt {

    @Override
    @JsonIgnore
    public String getTypeID();

    @Override
    @JsonProperty("objectType")
    public String getObjectType();
    
    @Override
    @JsonProperty("portIndex")
    public Integer getPortIndex();
    
    @Override
    @JsonProperty("portType")
    public PortTypeEnt getPortType();
    
    @Override
    @JsonProperty("portName")
    public String getPortName();
    

    /**
     * MixIn class for entity builder implementations that adds jackson annotations for the de-/serialization.
     *
     * @author Martin Horn, University of Konstanz
     */
    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "",
        defaultImpl = DefaultNodeInPortEntBuilder.class)
    @JsonSubTypes({
        @Type(value = DefaultNodeInPortEnt.DefaultNodeInPortEntBuilder.class, name="NodeInPort")
    })
    // AUTO-GENERATED CODE; DO NOT MODIFY
    public static interface NodeInPortEntMixInBuilder extends NodeInPortEntBuilder {
    
        @Override
        public NodeInPortEntMixIn build();
    
        @Override
        @JsonProperty("objectType")
        public NodeInPortEntMixInBuilder setObjectType(final String objectType);
        
        @Override
        @JsonProperty("portIndex")
        public NodeInPortEntMixInBuilder setPortIndex(final Integer portIndex);
        
        @Override
        @JsonProperty("portType")
        public NodeInPortEntMixInBuilder setPortType(final PortTypeEnt portType);
        
        @Override
        @JsonProperty("portName")
        public NodeInPortEntMixInBuilder setPortName(final String portName);
        
    }


}

