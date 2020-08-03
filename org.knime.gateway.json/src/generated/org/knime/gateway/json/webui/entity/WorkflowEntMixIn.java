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

import org.knime.gateway.api.webui.entity.ConnectionEnt;
import org.knime.gateway.api.webui.entity.NodeEnt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


import org.knime.gateway.api.webui.entity.WorkflowEnt;
import org.knime.gateway.impl.webui.entity.DefaultWorkflowEnt;
import org.knime.gateway.impl.webui.entity.DefaultWorkflowEnt.DefaultWorkflowEntBuilder;

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
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.json-config.json"})
public interface WorkflowEntMixIn extends WorkflowEnt {

    @Override
    @JsonIgnore
    public String getTypeID();

    @Override
    @JsonProperty("name")
    public String getName();
    
    @Override
    @JsonProperty("nodes")
    public java.util.Map<String, NodeEnt> getNodes();
    
    @Override
    @JsonProperty("connections")
    public java.util.Map<String, ConnectionEnt> getConnections();
    

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
        @JsonProperty("name")
        public WorkflowEntMixInBuilder setName(final String name);
        
        @Override
        @JsonProperty("nodes")
        public WorkflowEntMixInBuilder setNodes(final java.util.Map<String, NodeEnt> nodes);
        
        @Override
        @JsonProperty("connections")
        public WorkflowEntMixInBuilder setConnections(final java.util.Map<String, ConnectionEnt> connections);
        
    }


}

