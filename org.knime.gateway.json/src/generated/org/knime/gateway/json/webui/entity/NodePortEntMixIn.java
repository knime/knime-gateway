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


import org.knime.gateway.api.webui.entity.NodePortEnt;
import org.knime.gateway.impl.webui.entity.DefaultNodePortEnt;
import org.knime.gateway.impl.webui.entity.DefaultNodePortEnt.DefaultNodePortEntBuilder;

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
    defaultImpl = DefaultNodePortEnt.class)
@JsonSubTypes({
    @Type(value = DefaultNodePortEnt.class, name="NodePort")
})
@JsonDeserialize(builder=DefaultNodePortEntBuilder.class)
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.json-config.json"})
public interface NodePortEntMixIn extends NodePortEnt {

    @Override
    @JsonIgnore
    public String getTypeID();

    @Override
    @JsonProperty("name")
    public String getName();
    
    @Override
    @JsonProperty("info")
    public String getInfo();
    
    @Override
    @JsonProperty("index")
    public Integer getIndex();
    
    @Override
    @JsonProperty("type")
    public TypeEnum getType();
    
    @Override
    @JsonProperty("color")
    public String getColor();
    
    @Override
    @JsonProperty("connectedVia")
    public java.util.List<org.knime.gateway.api.entity.ConnectionIDEnt> getConnectedVia();
    
    @Override
    @JsonProperty("optional")
    public Boolean isOptional();
    
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
        defaultImpl = DefaultNodePortEntBuilder.class)
    @JsonSubTypes({
        @Type(value = DefaultNodePortEnt.DefaultNodePortEntBuilder.class, name="NodePort")
    })
    // AUTO-GENERATED CODE; DO NOT MODIFY
    public static interface NodePortEntMixInBuilder extends NodePortEntBuilder {
    
        @Override
        public NodePortEntMixIn build();
    
        @Override
        @JsonProperty("name")
        public NodePortEntMixInBuilder setName(final String name);
        
        @Override
        @JsonProperty("info")
        public NodePortEntMixInBuilder setInfo(final String info);
        
        @Override
        @JsonProperty("index")
        public NodePortEntMixInBuilder setIndex(final Integer index);
        
        @Override
        @JsonProperty("type")
        public NodePortEntMixInBuilder setType(final TypeEnum type);
        
        @Override
        @JsonProperty("color")
        public NodePortEntMixInBuilder setColor(final String color);
        
        @Override
        @JsonProperty("connectedVia")
        public NodePortEntMixInBuilder setConnectedVia(final java.util.List<org.knime.gateway.api.entity.ConnectionIDEnt> connectedVia);
        
        @Override
        @JsonProperty("optional")
        public NodePortEntMixInBuilder setOptional(final Boolean optional);
        
        @Override
        @JsonProperty("inactive")
        public NodePortEntMixInBuilder setInactive(final Boolean inactive);
        
    }


}

