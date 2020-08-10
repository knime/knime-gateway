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
import org.knime.gateway.api.webui.entity.NodePortEnt;
import org.knime.gateway.api.webui.entity.XYEnt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


import org.knime.gateway.api.webui.entity.NodeEnt;
import org.knime.gateway.impl.webui.entity.DefaultNodeEnt;
import org.knime.gateway.impl.webui.entity.DefaultNodeEnt.DefaultNodeEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultNativeNodeEnt;
import org.knime.gateway.impl.webui.entity.DefaultWorkflowNodeEnt;
import org.knime.gateway.impl.webui.entity.DefaultComponentNodeEnt;

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
    defaultImpl = DefaultNodeEnt.class)
@JsonSubTypes({
    @Type(value = DefaultNodeEnt.class, name="Node")
,
  @Type(value = DefaultNativeNodeEnt.class, name = "NativeNode")
,
  @Type(value = DefaultWorkflowNodeEnt.class, name = "WorkflowNode")
,
  @Type(value = DefaultComponentNodeEnt.class, name = "ComponentNode")
})
@JsonDeserialize(builder=DefaultNodeEntBuilder.class)
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.json-config.json"})
public interface NodeEntMixIn extends NodeEnt {

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
    @JsonProperty("class")
    public PropertyClassEnum getPropertyClass();
    

    /**
     * MixIn class for entity builder implementations that adds jackson annotations for the de-/serialization.
     *
     * @author Martin Horn, University of Konstanz
     */
    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "",
        defaultImpl = DefaultNodeEntBuilder.class)
    @JsonSubTypes({
        @Type(value = DefaultNodeEnt.DefaultNodeEntBuilder.class, name="Node")
        ,
      @Type(value = DefaultNativeNodeEnt.DefaultNativeNodeEntBuilder.class, name = "NativeNode")
        ,
      @Type(value = DefaultWorkflowNodeEnt.DefaultWorkflowNodeEntBuilder.class, name = "WorkflowNode")
        ,
      @Type(value = DefaultComponentNodeEnt.DefaultComponentNodeEntBuilder.class, name = "ComponentNode")
    })
    // AUTO-GENERATED CODE; DO NOT MODIFY
    public static interface NodeEntMixInBuilder extends NodeEntBuilder {
    
        @Override
        public NodeEntMixIn build();
    
        @Override
        @JsonProperty("id")
        public NodeEntMixInBuilder setId(final org.knime.gateway.api.entity.NodeIDEnt id);
        
        @Override
        @JsonProperty("inPorts")
        public NodeEntMixInBuilder setInPorts(final java.util.List<NodePortEnt> inPorts);
        
        @Override
        @JsonProperty("outPorts")
        public NodeEntMixInBuilder setOutPorts(final java.util.List<NodePortEnt> outPorts);
        
        @Override
        @JsonProperty("annotation")
        public NodeEntMixInBuilder setAnnotation(final NodeAnnotationEnt annotation);
        
        @Override
        @JsonProperty("position")
        public NodeEntMixInBuilder setPosition(final XYEnt position);
        
        @Override
        @JsonProperty("class")
        public NodeEntMixInBuilder setPropertyClass(final PropertyClassEnum propertyClass);
        
    }


}

