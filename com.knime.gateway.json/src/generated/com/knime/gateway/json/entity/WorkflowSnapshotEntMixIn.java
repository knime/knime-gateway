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

import com.knime.gateway.v0.entity.WorkflowEnt;


import com.knime.gateway.json.JsonUtil;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


import com.knime.gateway.v0.entity.WorkflowSnapshotEnt;
import com.knime.gateway.v0.entity.impl.DefaultWorkflowSnapshotEnt;
import com.knime.gateway.v0.entity.impl.DefaultWorkflowSnapshotEnt.DefaultWorkflowSnapshotEntBuilder;

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
    defaultImpl = DefaultWorkflowSnapshotEnt.class)
@JsonSubTypes({
    @Type(value = DefaultWorkflowSnapshotEnt.class, name="WorkflowSnapshot")
})
@JsonDeserialize(builder=DefaultWorkflowSnapshotEntBuilder.class)
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public interface WorkflowSnapshotEntMixIn extends WorkflowSnapshotEnt {

    @Override
    @JsonIgnore
    public String getTypeID();

    @Override
    @JsonProperty("workflow")
    public WorkflowEnt getWorkflow();
    
    @Override
    @JsonProperty("snapshotID")
    public java.util.UUID getSnapshotID();
    

    /**
     * MixIn class for entity builder implementations that adds jackson annotations for the de-/serialization.
     *
     * @author Martin Horn, University of Konstanz
     */
    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "",
        defaultImpl = DefaultWorkflowSnapshotEntBuilder.class)
    @JsonSubTypes({
        @Type(value = DefaultWorkflowSnapshotEnt.DefaultWorkflowSnapshotEntBuilder.class, name="WorkflowSnapshot")
    })
    // AUTO-GENERATED CODE; DO NOT MODIFY
    public static interface WorkflowSnapshotEntMixInBuilder extends WorkflowSnapshotEntBuilder {
    
        @Override
        public WorkflowSnapshotEntMixIn build();
    
        @Override
        @JsonProperty("workflow")
        public WorkflowSnapshotEntMixInBuilder setWorkflow(final WorkflowEnt workflow);
        
        @Override
        @JsonProperty("snapshotID")
        public WorkflowSnapshotEntMixInBuilder setSnapshotID(final java.util.UUID snapshotID);
        
    }


}

