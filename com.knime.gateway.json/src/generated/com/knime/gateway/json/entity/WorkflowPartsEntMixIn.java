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



import com.knime.gateway.json.JsonUtil;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


import com.knime.gateway.v0.entity.WorkflowPartsEnt;
import com.knime.gateway.v0.entity.impl.DefaultWorkflowPartsEnt;
import com.knime.gateway.v0.entity.impl.DefaultWorkflowPartsEnt.DefaultWorkflowPartsEntBuilder;

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
    defaultImpl = DefaultWorkflowPartsEnt.class)
@JsonSubTypes({
    @Type(value = DefaultWorkflowPartsEnt.class, name="WorkflowParts")
})
@JsonDeserialize(builder=DefaultWorkflowPartsEntBuilder.class)
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public interface WorkflowPartsEntMixIn extends WorkflowPartsEnt {

    @Override
    @JsonIgnore
    public String getTypeID();

    @Override
    @JsonProperty("parentNodeID")
    public String getParentNodeID();
    
    @Override
    @JsonProperty("nodeIDs")
    public java.util.List<String> getNodeIDs();
    
    @Override
    @JsonProperty("connectionIDs")
    public java.util.List<String> getConnectionIDs();
    
    @Override
    @JsonProperty("annotationIDs")
    public java.util.List<String> getAnnotationIDs();
    

    /**
     * MixIn class for entity builder implementations that adds jackson annotations for the de-/serialization.
     *
     * @author Martin Horn, University of Konstanz
     */
    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "",
        defaultImpl = DefaultWorkflowPartsEntBuilder.class)
    @JsonSubTypes({
        @Type(value = DefaultWorkflowPartsEnt.DefaultWorkflowPartsEntBuilder.class, name="WorkflowParts")
    })
    // AUTO-GENERATED CODE; DO NOT MODIFY
    public static interface WorkflowPartsEntMixInBuilder extends WorkflowPartsEntBuilder {
    
        @Override
        public WorkflowPartsEntMixIn build();
    
        @Override
        @JsonProperty("parentNodeID")
        public WorkflowPartsEntMixInBuilder setParentNodeID(final String parentNodeID);
        
        @Override
        @JsonProperty("nodeIDs")
        public WorkflowPartsEntMixInBuilder setNodeIDs(final java.util.List<String> nodeIDs);
        
        @Override
        @JsonProperty("connectionIDs")
        public WorkflowPartsEntMixInBuilder setConnectionIDs(final java.util.List<String> connectionIDs);
        
        @Override
        @JsonProperty("annotationIDs")
        public WorkflowPartsEntMixInBuilder setAnnotationIDs(final java.util.List<String> annotationIDs);
        
    }


}
