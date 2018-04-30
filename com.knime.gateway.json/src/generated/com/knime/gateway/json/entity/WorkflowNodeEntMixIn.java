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

import com.knime.gateway.json.entity.NodeEntMixIn;
import com.knime.gateway.v0.entity.JobManagerEnt;
import com.knime.gateway.v0.entity.NodeAnnotationEnt;
import com.knime.gateway.v0.entity.NodeInPortEnt;
import com.knime.gateway.v0.entity.NodeMessageEnt;
import com.knime.gateway.v0.entity.NodeOutPortEnt;
import com.knime.gateway.v0.entity.NodeStateEnt;
import com.knime.gateway.v0.entity.NodeUIInfoEnt;


import com.knime.gateway.json.JsonUtil;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


import com.knime.gateway.v0.entity.WorkflowNodeEnt;
import com.knime.gateway.v0.entity.impl.DefaultWorkflowNodeEnt;
import com.knime.gateway.v0.entity.impl.DefaultWorkflowNodeEnt.DefaultWorkflowNodeEntBuilder;
import com.knime.gateway.v0.entity.impl.DefaultWrappedWorkflowNodeEnt;

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
    defaultImpl = DefaultWorkflowNodeEnt.class)
@JsonSubTypes({
    @Type(value = DefaultWorkflowNodeEnt.class, name="WorkflowNode")
,
  @Type(value = DefaultWrappedWorkflowNodeEnt.class, name = "WrappedWorkflowNode")
})
@JsonDeserialize(builder=DefaultWorkflowNodeEntBuilder.class)
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public interface WorkflowNodeEntMixIn extends WorkflowNodeEnt {

    @Override
    @JsonIgnore
    public String getTypeID();

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
    public NodeStateEnt getNodeState();
    
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
    @JsonProperty("resetable")
    public Boolean isResetable();
    
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
    
    @Override
    @JsonProperty("workflowIncomingPorts")
    public java.util.List<NodeOutPortEnt> getWorkflowIncomingPorts();
    
    @Override
    @JsonProperty("workflowOutgoingPorts")
    public java.util.List<NodeInPortEnt> getWorkflowOutgoingPorts();
    
    @Override
    @JsonProperty("encrypted")
    public Boolean isEncrypted();
    
    @Override
    @JsonProperty("workflowOutgoingPortNodeStates")
    public java.util.List<NodeStateEnt> getWorkflowOutgoingPortNodeStates();
    

    /**
     * MixIn class for entity builder implementations that adds jackson annotations for the de-/serialization.
     *
     * @author Martin Horn, University of Konstanz
     */
    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "",
        defaultImpl = DefaultWorkflowNodeEntBuilder.class)
    @JsonSubTypes({
        @Type(value = DefaultWorkflowNodeEnt.DefaultWorkflowNodeEntBuilder.class, name="WorkflowNode")
        ,
      @Type(value = DefaultWrappedWorkflowNodeEnt.DefaultWrappedWorkflowNodeEntBuilder.class, name = "WrappedWorkflowNode")
    })
    // AUTO-GENERATED CODE; DO NOT MODIFY
    public static interface WorkflowNodeEntMixInBuilder extends WorkflowNodeEntBuilder {
    
        @Override
        public WorkflowNodeEntMixIn build();
    
        @Override
        @JsonProperty("type")
        public WorkflowNodeEntMixInBuilder setType(final String type);
        
        @Override
        @JsonProperty("name")
        public WorkflowNodeEntMixInBuilder setName(final String name);
        
        @Override
        @JsonProperty("nodeID")
        public WorkflowNodeEntMixInBuilder setNodeID(final String nodeID);
        
        @Override
        @JsonProperty("nodeType")
        public WorkflowNodeEntMixInBuilder setNodeType(final NodeTypeEnum nodeType);
        
        @Override
        @JsonProperty("parentNodeID")
        public WorkflowNodeEntMixInBuilder setParentNodeID(final String parentNodeID);
        
        @Override
        @JsonProperty("rootWorkflowID")
        public WorkflowNodeEntMixInBuilder setRootWorkflowID(final java.util.UUID rootWorkflowID);
        
        @Override
        @JsonProperty("nodeMessage")
        public WorkflowNodeEntMixInBuilder setNodeMessage(final NodeMessageEnt nodeMessage);
        
        @Override
        @JsonProperty("nodeState")
        public WorkflowNodeEntMixInBuilder setNodeState(final NodeStateEnt nodeState);
        
        @Override
        @JsonProperty("inPorts")
        public WorkflowNodeEntMixInBuilder setInPorts(final java.util.List<NodeInPortEnt> inPorts);
        
        @Override
        @JsonProperty("outPorts")
        public WorkflowNodeEntMixInBuilder setOutPorts(final java.util.List<NodeOutPortEnt> outPorts);
        
        @Override
        @JsonProperty("deletable")
        public WorkflowNodeEntMixInBuilder setDeletable(final Boolean deletable);
        
        @Override
        @JsonProperty("resetable")
        public WorkflowNodeEntMixInBuilder setResetable(final Boolean resetable);
        
        @Override
        @JsonProperty("hasDialog")
        public WorkflowNodeEntMixInBuilder setHasDialog(final Boolean hasDialog);
        
        @Override
        @JsonProperty("nodeAnnotation")
        public WorkflowNodeEntMixInBuilder setNodeAnnotation(final NodeAnnotationEnt nodeAnnotation);
        
        @Override
        @JsonProperty("jobManager")
        public WorkflowNodeEntMixInBuilder setJobManager(final JobManagerEnt jobManager);
        
        @Override
        @JsonProperty("uIInfo")
        public WorkflowNodeEntMixInBuilder setUIInfo(final NodeUIInfoEnt uIInfo);
        
        @Override
        @JsonProperty("workflowIncomingPorts")
        public WorkflowNodeEntMixInBuilder setWorkflowIncomingPorts(final java.util.List<NodeOutPortEnt> workflowIncomingPorts);
        
        @Override
        @JsonProperty("workflowOutgoingPorts")
        public WorkflowNodeEntMixInBuilder setWorkflowOutgoingPorts(final java.util.List<NodeInPortEnt> workflowOutgoingPorts);
        
        @Override
        @JsonProperty("encrypted")
        public WorkflowNodeEntMixInBuilder setEncrypted(final Boolean encrypted);
        
        @Override
        @JsonProperty("workflowOutgoingPortNodeStates")
        public WorkflowNodeEntMixInBuilder setWorkflowOutgoingPortNodeStates(final java.util.List<NodeStateEnt> workflowOutgoingPortNodeStates);
        
    }


}

