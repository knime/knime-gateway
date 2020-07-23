/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.com; Email: contact@knime.com
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME AG herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ------------------------------------------------------------------------
 */
package org.knime.gateway.json.entity;

import org.knime.gateway.api.entity.JobManagerEnt;
import org.knime.gateway.api.entity.NodeAnnotationEnt;
import org.knime.gateway.api.entity.NodeInPortEnt;
import org.knime.gateway.api.entity.NodeMessageEnt;
import org.knime.gateway.api.entity.NodeOutPortEnt;
import org.knime.gateway.api.entity.NodeProgressEnt;
import org.knime.gateway.api.entity.NodeStateEnt;
import org.knime.gateway.api.entity.NodeUIInfoEnt;
import org.knime.gateway.json.entity.NodeEntMixIn;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


import org.knime.gateway.api.entity.WorkflowNodeEnt;
import org.knime.gateway.impl.entity.DefaultWorkflowNodeEnt;
import org.knime.gateway.impl.entity.DefaultWorkflowNodeEnt.DefaultWorkflowNodeEntBuilder;
import org.knime.gateway.impl.entity.DefaultWrappedWorkflowNodeEnt;

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
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/org.knime.gateway.json-config.json"})
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
        public WorkflowNodeEntMixInBuilder setNodeID(final org.knime.gateway.api.entity.NodeIDEnt nodeID);
        
        @Override
        @JsonProperty("nodeType")
        public WorkflowNodeEntMixInBuilder setNodeType(final NodeTypeEnum nodeType);
        
        @Override
        @JsonProperty("parentNodeID")
        public WorkflowNodeEntMixInBuilder setParentNodeID(final org.knime.gateway.api.entity.NodeIDEnt parentNodeID);
        
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
        @JsonProperty("progress")
        public WorkflowNodeEntMixInBuilder setProgress(final NodeProgressEnt progress);
        
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
        @JsonProperty("webViewNames")
        public WorkflowNodeEntMixInBuilder setWebViewNames(final java.util.List<String> webViewNames);
        
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

