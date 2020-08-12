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

import org.knime.gateway.api.webui.entity.NodeAnnotationEnt;
import org.knime.gateway.api.webui.entity.NodeExecutionStateEnt;
import org.knime.gateway.api.webui.entity.NodePortEnt;
import org.knime.gateway.api.webui.entity.XYEnt;
import org.knime.gateway.json.webui.entity.WorkflowNodeEntMixIn;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


import org.knime.gateway.api.webui.entity.ComponentNodeEnt;
import org.knime.gateway.impl.webui.entity.DefaultComponentNodeEnt;
import org.knime.gateway.impl.webui.entity.DefaultComponentNodeEnt.DefaultComponentNodeEntBuilder;

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
    defaultImpl = DefaultComponentNodeEnt.class)
@JsonSubTypes({
    @Type(value = DefaultComponentNodeEnt.class, name="ComponentNode")
})
@JsonDeserialize(builder=DefaultComponentNodeEntBuilder.class)
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.json-config.json"})
public interface ComponentNodeEntMixIn extends ComponentNodeEnt {

    @Override
    @JsonIgnore
    public String getTypeID();

    @Override
    @JsonProperty("id")
    public org.knime.gateway.api.entity.NodeIDEnt getId();
    
    @Override
    @JsonProperty("inPorts")
    public java.util.List<NodePortEnt> getInPorts();
    
    @Override
    @JsonProperty("outPorts")
    public java.util.List<NodePortEnt> getOutPorts();
    
    @Override
    @JsonProperty("annotation")
    public NodeAnnotationEnt getAnnotation();
    
    @Override
    @JsonProperty("position")
    public XYEnt getPosition();
    
    @Override
    @JsonProperty("kind")
    public KindEnum getKind();
    
    @Override
    @JsonProperty("name")
    public String getName();
    
    @Override
    @JsonProperty("type")
    public TypeEnum getType();
    
    @Override
    @JsonProperty("state")
    public NodeExecutionStateEnt getState();
    
    @Override
    @JsonProperty("icon")
    public String getIcon();
    

    /**
     * MixIn class for entity builder implementations that adds jackson annotations for the de-/serialization.
     *
     * @author Martin Horn, University of Konstanz
     */
    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "",
        defaultImpl = DefaultComponentNodeEntBuilder.class)
    @JsonSubTypes({
        @Type(value = DefaultComponentNodeEnt.DefaultComponentNodeEntBuilder.class, name="ComponentNode")
    })
    // AUTO-GENERATED CODE; DO NOT MODIFY
    public static interface ComponentNodeEntMixInBuilder extends ComponentNodeEntBuilder {
    
        @Override
        public ComponentNodeEntMixIn build();
    
        @Override
        @JsonProperty("id")
        public ComponentNodeEntMixInBuilder setId(final org.knime.gateway.api.entity.NodeIDEnt id);
        
        @Override
        @JsonProperty("inPorts")
        public ComponentNodeEntMixInBuilder setInPorts(final java.util.List<NodePortEnt> inPorts);
        
        @Override
        @JsonProperty("outPorts")
        public ComponentNodeEntMixInBuilder setOutPorts(final java.util.List<NodePortEnt> outPorts);
        
        @Override
        @JsonProperty("annotation")
        public ComponentNodeEntMixInBuilder setAnnotation(final NodeAnnotationEnt annotation);
        
        @Override
        @JsonProperty("position")
        public ComponentNodeEntMixInBuilder setPosition(final XYEnt position);
        
        @Override
        @JsonProperty("kind")
        public ComponentNodeEntMixInBuilder setKind(final KindEnum kind);
        
        @Override
        @JsonProperty("name")
        public ComponentNodeEntMixInBuilder setName(final String name);
        
        @Override
        @JsonProperty("type")
        public ComponentNodeEntMixInBuilder setType(final TypeEnum type);
        
        @Override
        @JsonProperty("state")
        public ComponentNodeEntMixInBuilder setState(final NodeExecutionStateEnt state);
        
        @Override
        @JsonProperty("icon")
        public ComponentNodeEntMixInBuilder setIcon(final String icon);
        
    }


}

