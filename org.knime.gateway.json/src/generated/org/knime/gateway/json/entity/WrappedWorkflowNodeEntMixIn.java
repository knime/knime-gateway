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

import org.knime.gateway.json.entity.NodeEntMixIn;
import org.knime.gateway.v0.entity.JobManagerEnt;
import org.knime.gateway.v0.entity.NodeAnnotationEnt;
import org.knime.gateway.v0.entity.NodeInPortEnt;
import org.knime.gateway.v0.entity.NodeMessageEnt;
import org.knime.gateway.v0.entity.NodeOutPortEnt;
import org.knime.gateway.v0.entity.NodeUIInfoEnt;


import org.knime.gateway.json.JsonUtil;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


import org.knime.gateway.v0.entity.WrappedWorkflowNodeEnt;
import org.knime.gateway.v0.entity.impl.DefaultWrappedWorkflowNodeEnt;
import org.knime.gateway.v0.entity.impl.DefaultWrappedWorkflowNodeEnt.DefaultWrappedWorkflowNodeEntBuilder;

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
    @JsonProperty("virtualInNodeID")
    public String getVirtualInNodeID();
    
    @Override
    @JsonProperty("virtualOutNodeID")
    public String getVirtualOutNodeID();
    

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
        public WrappedWorkflowNodeEntMixInBuilder setNodeState(final NodeStateEnum nodeState);
        
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
        @JsonProperty("hasDialog")
        public WrappedWorkflowNodeEntMixInBuilder setHasDialog(final Boolean hasDialog);
        
        @Override
        @JsonProperty("nodeAnnotation")
        public WrappedWorkflowNodeEntMixInBuilder setNodeAnnotation(final NodeAnnotationEnt nodeAnnotation);
        
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
        @JsonProperty("virtualInNodeID")
        public WrappedWorkflowNodeEntMixInBuilder setVirtualInNodeID(final String virtualInNodeID);
        
        @Override
        @JsonProperty("virtualOutNodeID")
        public WrappedWorkflowNodeEntMixInBuilder setVirtualOutNodeID(final String virtualOutNodeID);
        
    }


}
