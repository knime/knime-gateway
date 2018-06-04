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

import com.knime.gateway.json.entity.WorkflowNodeEntMixIn;
import com.knime.gateway.v0.entity.JobManagerEnt;
import com.knime.gateway.v0.entity.NodeAnnotationEnt;
import com.knime.gateway.v0.entity.NodeInPortEnt;
import com.knime.gateway.v0.entity.NodeMessageEnt;
import com.knime.gateway.v0.entity.NodeOutPortEnt;
import com.knime.gateway.v0.entity.NodeProgressEnt;
import com.knime.gateway.v0.entity.NodeStateEnt;
import com.knime.gateway.v0.entity.NodeUIInfoEnt;


import com.knime.gateway.json.JsonUtil;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


import com.knime.gateway.v0.entity.WrappedWorkflowNodeEnt;
import com.knime.gateway.v0.entity.impl.DefaultWrappedWorkflowNodeEnt;
import com.knime.gateway.v0.entity.impl.DefaultWrappedWorkflowNodeEnt.DefaultWrappedWorkflowNodeEntBuilder;

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
    defaultImpl = DefaultWrappedWorkflowNodeEnt.class)
@JsonSubTypes({
    @Type(value = DefaultWrappedWorkflowNodeEnt.class, name="WrappedWorkflowNode")
})
@JsonDeserialize(builder=DefaultWrappedWorkflowNodeEntBuilder.class)
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public interface WrappedWorkflowNodeEntMixIn extends WrappedWorkflowNodeEnt {

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
    @JsonProperty("progress")
    public NodeProgressEnt getProgress();
    
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
    public Boolean hasDialog();
    
    @Override
    @JsonProperty("nodeAnnotation")
    public NodeAnnotationEnt getNodeAnnotation();
    
    @Override
    @JsonProperty("webViewNames")
    public java.util.List<String> getWebViewNames();
    
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
    
    @Override
    @JsonProperty("virtualInNodeID")
    public String getVirtualInNodeID();
    
    @Override
    @JsonProperty("virtualOutNodeID")
    public String getVirtualOutNodeID();
    
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
        defaultImpl = DefaultWrappedWorkflowNodeEntBuilder.class)
    @JsonSubTypes({
        @Type(value = DefaultWrappedWorkflowNodeEnt.DefaultWrappedWorkflowNodeEntBuilder.class, name="WrappedWorkflowNode")
    })
    // AUTO-GENERATED CODE; DO NOT MODIFY
    public static interface WrappedWorkflowNodeEntMixInBuilder extends WrappedWorkflowNodeEntBuilder {
    
        @Override
        public WrappedWorkflowNodeEntMixIn build();
    
        @Override
        @JsonProperty("type")
        public WrappedWorkflowNodeEntMixInBuilder setType(final String type);
        
        @Override
        @JsonProperty("name")
        public WrappedWorkflowNodeEntMixInBuilder setName(final String name);
        
        @Override
        @JsonProperty("nodeID")
        public WrappedWorkflowNodeEntMixInBuilder setNodeID(final String nodeID);
        
        @Override
        @JsonProperty("nodeType")
        public WrappedWorkflowNodeEntMixInBuilder setNodeType(final NodeTypeEnum nodeType);
        
        @Override
        @JsonProperty("parentNodeID")
        public WrappedWorkflowNodeEntMixInBuilder setParentNodeID(final String parentNodeID);
        
        @Override
        @JsonProperty("rootWorkflowID")
        public WrappedWorkflowNodeEntMixInBuilder setRootWorkflowID(final java.util.UUID rootWorkflowID);
        
        @Override
        @JsonProperty("nodeMessage")
        public WrappedWorkflowNodeEntMixInBuilder setNodeMessage(final NodeMessageEnt nodeMessage);
        
        @Override
        @JsonProperty("nodeState")
        public WrappedWorkflowNodeEntMixInBuilder setNodeState(final NodeStateEnt nodeState);
        
        @Override
        @JsonProperty("progress")
        public WrappedWorkflowNodeEntMixInBuilder setProgress(final NodeProgressEnt progress);
        
        @Override
        @JsonProperty("inPorts")
        public WrappedWorkflowNodeEntMixInBuilder setInPorts(final java.util.List<NodeInPortEnt> inPorts);
        
        @Override
        @JsonProperty("outPorts")
        public WrappedWorkflowNodeEntMixInBuilder setOutPorts(final java.util.List<NodeOutPortEnt> outPorts);
        
        @Override
        @JsonProperty("deletable")
        public WrappedWorkflowNodeEntMixInBuilder setDeletable(final Boolean deletable);
        
        @Override
        @JsonProperty("resetable")
        public WrappedWorkflowNodeEntMixInBuilder setResetable(final Boolean resetable);
        
        @Override
        @JsonProperty("hasDialog")
        public WrappedWorkflowNodeEntMixInBuilder setHasDialog(final Boolean hasDialog);
        
        @Override
        @JsonProperty("nodeAnnotation")
        public WrappedWorkflowNodeEntMixInBuilder setNodeAnnotation(final NodeAnnotationEnt nodeAnnotation);
        
        @Override
        @JsonProperty("webViewNames")
        public WrappedWorkflowNodeEntMixInBuilder setWebViewNames(final java.util.List<String> webViewNames);
        
        @Override
        @JsonProperty("jobManager")
        public WrappedWorkflowNodeEntMixInBuilder setJobManager(final JobManagerEnt jobManager);
        
        @Override
        @JsonProperty("uIInfo")
        public WrappedWorkflowNodeEntMixInBuilder setUIInfo(final NodeUIInfoEnt uIInfo);
        
        @Override
        @JsonProperty("workflowIncomingPorts")
        public WrappedWorkflowNodeEntMixInBuilder setWorkflowIncomingPorts(final java.util.List<NodeOutPortEnt> workflowIncomingPorts);
        
        @Override
        @JsonProperty("workflowOutgoingPorts")
        public WrappedWorkflowNodeEntMixInBuilder setWorkflowOutgoingPorts(final java.util.List<NodeInPortEnt> workflowOutgoingPorts);
        
        @Override
        @JsonProperty("encrypted")
        public WrappedWorkflowNodeEntMixInBuilder setEncrypted(final Boolean encrypted);
        
        @Override
        @JsonProperty("workflowOutgoingPortNodeStates")
        public WrappedWorkflowNodeEntMixInBuilder setWorkflowOutgoingPortNodeStates(final java.util.List<NodeStateEnt> workflowOutgoingPortNodeStates);
        
        @Override
        @JsonProperty("virtualInNodeID")
        public WrappedWorkflowNodeEntMixInBuilder setVirtualInNodeID(final String virtualInNodeID);
        
        @Override
        @JsonProperty("virtualOutNodeID")
        public WrappedWorkflowNodeEntMixInBuilder setVirtualOutNodeID(final String virtualOutNodeID);
        
        @Override
        @JsonProperty("inactive")
        public WrappedWorkflowNodeEntMixInBuilder setInactive(final Boolean inactive);
        
    }


}

