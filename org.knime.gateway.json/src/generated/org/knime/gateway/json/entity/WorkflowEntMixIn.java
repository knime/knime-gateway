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
package org.knime.gateway.json.entity;

import org.knime.gateway.api.entity.ConnectionEnt;
import org.knime.gateway.api.entity.MetaPortInfoEnt;
import org.knime.gateway.api.entity.NodeEnt;
import org.knime.gateway.api.entity.WorkflowAnnotationEnt;
import org.knime.gateway.api.entity.WorkflowUIInfoEnt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


import org.knime.gateway.api.entity.WorkflowEnt;
import org.knime.gateway.impl.entity.DefaultWorkflowEnt;
import org.knime.gateway.impl.entity.DefaultWorkflowEnt.DefaultWorkflowEntBuilder;

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
    defaultImpl = DefaultWorkflowEnt.class)
@JsonSubTypes({
    @Type(value = DefaultWorkflowEnt.class, name="Workflow")
})
@JsonDeserialize(builder=DefaultWorkflowEntBuilder.class)
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/org.knime.gateway.json-config.json"})
public interface WorkflowEntMixIn extends WorkflowEnt {

    @Override
    @JsonIgnore
    public String getTypeID();

    @Override
    @JsonProperty("nodes")
    public java.util.Map<String, NodeEnt> getNodes();
    
    @Override
    @JsonProperty("connections")
    public java.util.Map<String, ConnectionEnt> getConnections();
    
    @Override
    @JsonProperty("metaInPortInfos")
    public java.util.List<MetaPortInfoEnt> getMetaInPortInfos();
    
    @Override
    @JsonProperty("metaOutPortInfos")
    public java.util.List<MetaPortInfoEnt> getMetaOutPortInfos();
    
    @Override
    @JsonProperty("workflowAnnotations")
    public java.util.Map<String, WorkflowAnnotationEnt> getWorkflowAnnotations();
    
    @Override
    @JsonProperty("workflowUIInfo")
    public WorkflowUIInfoEnt getWorkflowUIInfo();
    
    @Override
    @JsonProperty("hasCredentials")
    public Boolean hasCredentials();
    
    @Override
    @JsonProperty("inWizardExecution")
    public Boolean isInWizardExecution();
    

    /**
     * MixIn class for entity builder implementations that adds jackson annotations for the de-/serialization.
     *
     * @author Martin Horn, University of Konstanz
     */
    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "",
        defaultImpl = DefaultWorkflowEntBuilder.class)
    @JsonSubTypes({
        @Type(value = DefaultWorkflowEnt.DefaultWorkflowEntBuilder.class, name="Workflow")
    })
    // AUTO-GENERATED CODE; DO NOT MODIFY
    public static interface WorkflowEntMixInBuilder extends WorkflowEntBuilder {
    
        @Override
        public WorkflowEntMixIn build();
    
        @Override
        @JsonProperty("nodes")
        public WorkflowEntMixInBuilder setNodes(final java.util.Map<String, NodeEnt> nodes);
        
        @Override
        @JsonProperty("connections")
        public WorkflowEntMixInBuilder setConnections(final java.util.Map<String, ConnectionEnt> connections);
        
        @Override
        @JsonProperty("metaInPortInfos")
        public WorkflowEntMixInBuilder setMetaInPortInfos(final java.util.List<MetaPortInfoEnt> metaInPortInfos);
        
        @Override
        @JsonProperty("metaOutPortInfos")
        public WorkflowEntMixInBuilder setMetaOutPortInfos(final java.util.List<MetaPortInfoEnt> metaOutPortInfos);
        
        @Override
        @JsonProperty("workflowAnnotations")
        public WorkflowEntMixInBuilder setWorkflowAnnotations(final java.util.Map<String, WorkflowAnnotationEnt> workflowAnnotations);
        
        @Override
        @JsonProperty("workflowUIInfo")
        public WorkflowEntMixInBuilder setWorkflowUIInfo(final WorkflowUIInfoEnt workflowUIInfo);
        
        @Override
        @JsonProperty("hasCredentials")
        public WorkflowEntMixInBuilder setHasCredentials(final Boolean hasCredentials);
        
        @Override
        @JsonProperty("inWizardExecution")
        public WorkflowEntMixInBuilder setInWizardExecution(final Boolean inWizardExecution);
        
    }


}
