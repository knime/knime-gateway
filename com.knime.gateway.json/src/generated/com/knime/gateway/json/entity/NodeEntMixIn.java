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

import com.knime.gateway.v0.entity.JobManagerEnt;
import com.knime.gateway.v0.entity.NodeAnnotationEnt;
import com.knime.gateway.v0.entity.NodeInPortEnt;
import com.knime.gateway.v0.entity.NodeMessageEnt;
import com.knime.gateway.v0.entity.NodeOutPortEnt;
import com.knime.gateway.v0.entity.NodeUIInfoEnt;


import com.knime.gateway.json.JsonUtil;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


import com.knime.gateway.v0.entity.NodeEnt;
import com.knime.gateway.v0.entity.impl.DefaultNodeEnt;
import com.knime.gateway.v0.entity.impl.DefaultNodeEnt.DefaultNodeEntBuilder;
import com.knime.gateway.v0.entity.impl.DefaultNativeNodeEnt;
import com.knime.gateway.v0.entity.impl.DefaultWorkflowNodeEnt;
import com.knime.gateway.v0.entity.impl.DefaultWrappedWorkflowNodeEnt;

/**
 * MixIn class for entity implementations that adds jackson annotations for de-/serialization.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "type",
    visible = true,
    defaultImpl = DefaultNodeEnt.class)
@JsonSubTypes({
    @Type(value = DefaultNodeEnt.class, name="Node")
,
  @Type(value = DefaultNativeNodeEnt.class, name = "NativeNode")
,
  @Type(value = DefaultWorkflowNodeEnt.class, name = "WorkflowNode")
,
  @Type(value = DefaultWrappedWorkflowNodeEnt.class, name = "WrappedWorkflowNode")
})
@JsonDeserialize(builder=DefaultNodeEntBuilder.class)
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public interface NodeEntMixIn extends NodeEnt {

    @Override
    @JsonProperty("type")
    public String getType();
    
    @Override
    @JsonProperty("name")
    public String getName();
    
    @Override
    @JsonProperty("nodeID")
    public String getNodeID();
    
    @Override
    @JsonProperty("nodeType")
    public NodeTypeEnum getNodeType();
    
    @Override
    @JsonProperty("parentNodeID")
    public String getParentNodeID();
    
    @Override
    @JsonProperty("rootWorkflowID")
    public java.util.UUID getRootWorkflowID();
    
    @Override
    @JsonProperty("nodeMessage")
    public NodeMessageEnt getNodeMessage();
    
    @Override
    @JsonProperty("nodeState")
    public NodeStateEnum getNodeState();
    
    @Override
    @JsonProperty("inPorts")
    public java.util.List<NodeInPortEnt> getInPorts();
    
    @Override
    @JsonProperty("outPorts")
    public java.util.List<NodeOutPortEnt> getOutPorts();
    
    @Override
    @JsonProperty("deletable")
    public Boolean isDeletable();
    
    @Override
    @JsonProperty("hasDialog")
    public Boolean isHasDialog();
    
    @Override
    @JsonProperty("nodeAnnotation")
    public NodeAnnotationEnt getNodeAnnotation();
    
    @Override
    @JsonProperty("jobManager")
    public JobManagerEnt getJobManager();
    
    @Override
    @JsonProperty("uIInfo")
    public NodeUIInfoEnt getUIInfo();
    

    /**
     * MixIn class for entity builder implementations that adds jackson annotations for the de-/serialization.
     *
     * @author Martin Horn, University of Konstanz
     */
    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        defaultImpl = DefaultNodeEntBuilder.class)
    @JsonSubTypes({
        @Type(value = DefaultNodeEnt.DefaultNodeEntBuilder.class, name="Node")
        ,
      @Type(value = DefaultNativeNodeEnt.DefaultNativeNodeEntBuilder.class, name = "NativeNode")
        ,
      @Type(value = DefaultWorkflowNodeEnt.DefaultWorkflowNodeEntBuilder.class, name = "WorkflowNode")
        ,
      @Type(value = DefaultWrappedWorkflowNodeEnt.DefaultWrappedWorkflowNodeEntBuilder.class, name = "WrappedWorkflowNode")
    })
    // AUTO-GENERATED CODE; DO NOT MODIFY
    public static interface NodeEntMixInBuilder extends NodeEntBuilder {
    
        @Override
        public NodeEntMixIn build();
    
        @Override
        @JsonProperty("type")
        public NodeEntMixInBuilder setType(final String type);
        
        @Override
        @JsonProperty("name")
        public NodeEntMixInBuilder setName(final String name);
        
        @Override
        @JsonProperty("nodeID")
        public NodeEntMixInBuilder setNodeID(final String nodeID);
        
        @Override
        @JsonProperty("nodeType")
        public NodeEntMixInBuilder setNodeType(final NodeTypeEnum nodeType);
        
        @Override
        @JsonProperty("parentNodeID")
        public NodeEntMixInBuilder setParentNodeID(final String parentNodeID);
        
        @Override
        @JsonProperty("rootWorkflowID")
        public NodeEntMixInBuilder setRootWorkflowID(final java.util.UUID rootWorkflowID);
        
        @Override
        @JsonProperty("nodeMessage")
        public NodeEntMixInBuilder setNodeMessage(final NodeMessageEnt nodeMessage);
        
        @Override
        @JsonProperty("nodeState")
        public NodeEntMixInBuilder setNodeState(final NodeStateEnum nodeState);
        
        @Override
        @JsonProperty("inPorts")
        public NodeEntMixInBuilder setInPorts(final java.util.List<NodeInPortEnt> inPorts);
        
        @Override
        @JsonProperty("outPorts")
        public NodeEntMixInBuilder setOutPorts(final java.util.List<NodeOutPortEnt> outPorts);
        
        @Override
        @JsonProperty("deletable")
        public NodeEntMixInBuilder setDeletable(final Boolean deletable);
        
        @Override
        @JsonProperty("hasDialog")
        public NodeEntMixInBuilder setHasDialog(final Boolean hasDialog);
        
        @Override
        @JsonProperty("nodeAnnotation")
        public NodeEntMixInBuilder setNodeAnnotation(final NodeAnnotationEnt nodeAnnotation);
        
        @Override
        @JsonProperty("jobManager")
        public NodeEntMixInBuilder setJobManager(final JobManagerEnt jobManager);
        
        @Override
        @JsonProperty("uIInfo")
        public NodeEntMixInBuilder setUIInfo(final NodeUIInfoEnt uIInfo);
        
    }


}

