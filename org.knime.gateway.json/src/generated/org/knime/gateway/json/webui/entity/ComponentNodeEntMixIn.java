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
import org.knime.gateway.api.webui.entity.NodeInPortEnt;
import org.knime.gateway.api.webui.entity.NodeMessageEnt;
import org.knime.gateway.api.webui.entity.NodeOutPortEnt;
import org.knime.gateway.api.webui.entity.NodeProgressEnt;
import org.knime.gateway.api.webui.entity.NodeStateEnt;
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
    @JsonProperty("type")
    public String getType();
    
    @Override
    @JsonProperty("name")
    public String getName();
    
    @Override
    @JsonProperty("nodeID")
    public org.knime.gateway.api.entity.NodeIDEnt getNodeID();
    
    @Override
    @JsonProperty("nodeType")
    public NodeTypeEnum getNodeType();
    
    @Override
    @JsonProperty("parentNodeID")
    public org.knime.gateway.api.entity.NodeIDEnt getParentNodeID();
    
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
    @JsonProperty("nodeAnnotation")
    public NodeAnnotationEnt getNodeAnnotation();
    
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
        defaultImpl = DefaultComponentNodeEntBuilder.class)
    @JsonSubTypes({
        @Type(value = DefaultComponentNodeEnt.DefaultComponentNodeEntBuilder.class, name="ComponentNode")
    })
    // AUTO-GENERATED CODE; DO NOT MODIFY
    public static interface ComponentNodeEntMixInBuilder extends ComponentNodeEntBuilder {
    
        @Override
        public ComponentNodeEntMixIn build();
    
        @Override
        @JsonProperty("type")
        public ComponentNodeEntMixInBuilder setType(final String type);
        
        @Override
        @JsonProperty("name")
        public ComponentNodeEntMixInBuilder setName(final String name);
        
        @Override
        @JsonProperty("nodeID")
        public ComponentNodeEntMixInBuilder setNodeID(final org.knime.gateway.api.entity.NodeIDEnt nodeID);
        
        @Override
        @JsonProperty("nodeType")
        public ComponentNodeEntMixInBuilder setNodeType(final NodeTypeEnum nodeType);
        
        @Override
        @JsonProperty("parentNodeID")
        public ComponentNodeEntMixInBuilder setParentNodeID(final org.knime.gateway.api.entity.NodeIDEnt parentNodeID);
        
        @Override
        @JsonProperty("rootWorkflowID")
        public ComponentNodeEntMixInBuilder setRootWorkflowID(final java.util.UUID rootWorkflowID);
        
        @Override
        @JsonProperty("nodeMessage")
        public ComponentNodeEntMixInBuilder setNodeMessage(final NodeMessageEnt nodeMessage);
        
        @Override
        @JsonProperty("nodeState")
        public ComponentNodeEntMixInBuilder setNodeState(final NodeStateEnt nodeState);
        
        @Override
        @JsonProperty("progress")
        public ComponentNodeEntMixInBuilder setProgress(final NodeProgressEnt progress);
        
        @Override
        @JsonProperty("inPorts")
        public ComponentNodeEntMixInBuilder setInPorts(final java.util.List<NodeInPortEnt> inPorts);
        
        @Override
        @JsonProperty("outPorts")
        public ComponentNodeEntMixInBuilder setOutPorts(final java.util.List<NodeOutPortEnt> outPorts);
        
        @Override
        @JsonProperty("nodeAnnotation")
        public ComponentNodeEntMixInBuilder setNodeAnnotation(final NodeAnnotationEnt nodeAnnotation);
        
        @Override
        @JsonProperty("workflowOutgoingPortNodeStates")
        public ComponentNodeEntMixInBuilder setWorkflowOutgoingPortNodeStates(final java.util.List<NodeStateEnt> workflowOutgoingPortNodeStates);
        
    }


}

