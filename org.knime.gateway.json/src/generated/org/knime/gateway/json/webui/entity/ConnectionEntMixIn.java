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


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


import org.knime.gateway.api.webui.entity.ConnectionEnt;
import org.knime.gateway.impl.webui.entity.DefaultConnectionEnt;
import org.knime.gateway.impl.webui.entity.DefaultConnectionEnt.DefaultConnectionEntBuilder;

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
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.json-config.json"})
public interface ConnectionEntMixIn extends ConnectionEnt {

    @Override
    @JsonIgnore
    public String getTypeID();

    @Override
    @JsonProperty("destNode")
    public org.knime.gateway.api.entity.NodeIDEnt getDestNode();
    
    @Override
    @JsonProperty("destPort")
    public Integer getDestPort();
    
    @Override
    @JsonProperty("sourceNode")
    public org.knime.gateway.api.entity.NodeIDEnt getSourceNode();
    
    @Override
    @JsonProperty("sourcePort")
    public Integer getSourcePort();
    
    @Override
    @JsonProperty("flowVariableConnection")
    public Boolean isFlowVariableConnection();
    

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
        @JsonProperty("destNode")
        public ConnectionEntMixInBuilder setDestNode(final org.knime.gateway.api.entity.NodeIDEnt destNode);
        
        @Override
        @JsonProperty("destPort")
        public ConnectionEntMixInBuilder setDestPort(final Integer destPort);
        
        @Override
        @JsonProperty("sourceNode")
        public ConnectionEntMixInBuilder setSourceNode(final org.knime.gateway.api.entity.NodeIDEnt sourceNode);
        
        @Override
        @JsonProperty("sourcePort")
        public ConnectionEntMixInBuilder setSourcePort(final Integer sourcePort);
        
        @Override
        @JsonProperty("flowVariableConnection")
        public ConnectionEntMixInBuilder setFlowVariableConnection(final Boolean flowVariableConnection);
        
    }


}

