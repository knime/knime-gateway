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

import org.knime.gateway.v0.entity.JobManagerEnt;
import org.knime.gateway.v0.entity.NodeAnnotationEnt;
import org.knime.gateway.v0.entity.NodeInPortEnt;
import org.knime.gateway.v0.entity.NodeMessageEnt;
import org.knime.gateway.v0.entity.NodeOutPortEnt;
import org.knime.gateway.v0.entity.NodeUIInfoEnt;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.knime.gateway.json.JsonUtil;

import org.knime.gateway.v0.entity.NodeEnt;
import org.knime.gateway.v0.entity.impl.DefaultNodeEnt;
import org.knime.gateway.v0.entity.impl.DefaultNodeEnt.DefaultNodeEntBuilder;
import org.knime.gateway.v0.entity.impl.DefaultNativeNodeEnt;
import org.knime.gateway.v0.entity.impl.DefaultWorkflowNodeEnt;
import org.knime.gateway.v0.entity.impl.DefaultWrappedWorkflowNodeEnt;

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

