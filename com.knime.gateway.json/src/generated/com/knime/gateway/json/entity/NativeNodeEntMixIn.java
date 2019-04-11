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

import com.knime.gateway.entity.JobManagerEnt;
import com.knime.gateway.entity.NodeAnnotationEnt;
import com.knime.gateway.entity.NodeFactoryKeyEnt;
import com.knime.gateway.entity.NodeInPortEnt;
import com.knime.gateway.entity.NodeMessageEnt;
import com.knime.gateway.entity.NodeOutPortEnt;
import com.knime.gateway.entity.NodeProgressEnt;
import com.knime.gateway.entity.NodeStateEnt;
import com.knime.gateway.entity.NodeUIInfoEnt;
import com.knime.gateway.json.entity.NodeEntMixIn;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


import com.knime.gateway.entity.NativeNodeEnt;
import com.knime.gateway.entity.impl.DefaultNativeNodeEnt;
import com.knime.gateway.entity.impl.DefaultNativeNodeEnt.DefaultNativeNodeEntBuilder;

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
    defaultImpl = DefaultNativeNodeEnt.class)
@JsonSubTypes({
    @Type(value = DefaultNativeNodeEnt.class, name="NativeNode")
})
@JsonDeserialize(builder=DefaultNativeNodeEntBuilder.class)
@javax.annotation.Generated(value = {"org.knime.gateway.codegen.GatewayCodegen", "src-gen/com.knime.gateway.json-config.json"})
public interface NativeNodeEntMixIn extends NativeNodeEnt {

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
    public com.knime.gateway.entity.NodeIDEnt getNodeID();
    
    @Override
    @JsonProperty("nodeType")
    public NodeTypeEnum getNodeType();
    
    @Override
    @JsonProperty("parentNodeID")
    public com.knime.gateway.entity.NodeIDEnt getParentNodeID();
    
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
    @JsonProperty("nodeFactoryKey")
    public NodeFactoryKeyEnt getNodeFactoryKey();
    
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
        defaultImpl = DefaultNativeNodeEntBuilder.class)
    @JsonSubTypes({
        @Type(value = DefaultNativeNodeEnt.DefaultNativeNodeEntBuilder.class, name="NativeNode")
    })
    // AUTO-GENERATED CODE; DO NOT MODIFY
    public static interface NativeNodeEntMixInBuilder extends NativeNodeEntBuilder {
    
        @Override
        public NativeNodeEntMixIn build();
    
        @Override
        @JsonProperty("type")
        public NativeNodeEntMixInBuilder setType(final String type);
        
        @Override
        @JsonProperty("name")
        public NativeNodeEntMixInBuilder setName(final String name);
        
        @Override
        @JsonProperty("nodeID")
        public NativeNodeEntMixInBuilder setNodeID(final com.knime.gateway.entity.NodeIDEnt nodeID);
        
        @Override
        @JsonProperty("nodeType")
        public NativeNodeEntMixInBuilder setNodeType(final NodeTypeEnum nodeType);
        
        @Override
        @JsonProperty("parentNodeID")
        public NativeNodeEntMixInBuilder setParentNodeID(final com.knime.gateway.entity.NodeIDEnt parentNodeID);
        
        @Override
        @JsonProperty("rootWorkflowID")
        public NativeNodeEntMixInBuilder setRootWorkflowID(final java.util.UUID rootWorkflowID);
        
        @Override
        @JsonProperty("nodeMessage")
        public NativeNodeEntMixInBuilder setNodeMessage(final NodeMessageEnt nodeMessage);
        
        @Override
        @JsonProperty("nodeState")
        public NativeNodeEntMixInBuilder setNodeState(final NodeStateEnt nodeState);
        
        @Override
        @JsonProperty("progress")
        public NativeNodeEntMixInBuilder setProgress(final NodeProgressEnt progress);
        
        @Override
        @JsonProperty("inPorts")
        public NativeNodeEntMixInBuilder setInPorts(final java.util.List<NodeInPortEnt> inPorts);
        
        @Override
        @JsonProperty("outPorts")
        public NativeNodeEntMixInBuilder setOutPorts(final java.util.List<NodeOutPortEnt> outPorts);
        
        @Override
        @JsonProperty("deletable")
        public NativeNodeEntMixInBuilder setDeletable(final Boolean deletable);
        
        @Override
        @JsonProperty("resetable")
        public NativeNodeEntMixInBuilder setResetable(final Boolean resetable);
        
        @Override
        @JsonProperty("hasDialog")
        public NativeNodeEntMixInBuilder setHasDialog(final Boolean hasDialog);
        
        @Override
        @JsonProperty("nodeAnnotation")
        public NativeNodeEntMixInBuilder setNodeAnnotation(final NodeAnnotationEnt nodeAnnotation);
        
        @Override
        @JsonProperty("webViewNames")
        public NativeNodeEntMixInBuilder setWebViewNames(final java.util.List<String> webViewNames);
        
        @Override
        @JsonProperty("jobManager")
        public NativeNodeEntMixInBuilder setJobManager(final JobManagerEnt jobManager);
        
        @Override
        @JsonProperty("uIInfo")
        public NativeNodeEntMixInBuilder setUIInfo(final NodeUIInfoEnt uIInfo);
        
        @Override
        @JsonProperty("nodeFactoryKey")
        public NativeNodeEntMixInBuilder setNodeFactoryKey(final NodeFactoryKeyEnt nodeFactoryKey);
        
        @Override
        @JsonProperty("inactive")
        public NativeNodeEntMixInBuilder setInactive(final Boolean inactive);
        
    }


}

