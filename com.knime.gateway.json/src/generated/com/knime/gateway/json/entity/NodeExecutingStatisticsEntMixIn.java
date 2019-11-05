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

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


import com.knime.gateway.entity.NodeExecutingStatisticsEnt;
import com.knime.gateway.entity.impl.DefaultNodeExecutingStatisticsEnt;
import com.knime.gateway.entity.impl.DefaultNodeExecutingStatisticsEnt.DefaultNodeExecutingStatisticsEntBuilder;

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
    defaultImpl = DefaultNodeExecutingStatisticsEnt.class)
@JsonSubTypes({
    @Type(value = DefaultNodeExecutingStatisticsEnt.class, name="NodeExecutingStatistics")
})
@JsonDeserialize(builder=DefaultNodeExecutingStatisticsEntBuilder.class)
@javax.annotation.Generated(value = {"org.knime.gateway.codegen.GatewayCodegen", "src-gen/com.knime.gateway.json-config.json"})
public interface NodeExecutingStatisticsEntMixIn extends NodeExecutingStatisticsEnt {

    @Override
    @JsonIgnore
    public String getTypeID();

    @Override
    @JsonProperty("name")
    public String getName();
    
    @Override
    @JsonProperty("annotation")
    public String getAnnotation();
    
    @Override
    @JsonProperty("nodeID")
    public String getNodeID();
    
    @Override
    @JsonProperty("executionDuration")
    public BigDecimal getExecutionDuration();
    
    @Override
    @JsonProperty("progress")
    public BigDecimal getProgress();
    

    /**
     * MixIn class for entity builder implementations that adds jackson annotations for the de-/serialization.
     *
     * @author Martin Horn, University of Konstanz
     */
    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "",
        defaultImpl = DefaultNodeExecutingStatisticsEntBuilder.class)
    @JsonSubTypes({
        @Type(value = DefaultNodeExecutingStatisticsEnt.DefaultNodeExecutingStatisticsEntBuilder.class, name="NodeExecutingStatistics")
    })
    // AUTO-GENERATED CODE; DO NOT MODIFY
    public static interface NodeExecutingStatisticsEntMixInBuilder extends NodeExecutingStatisticsEntBuilder {
    
        @Override
        public NodeExecutingStatisticsEntMixIn build();
    
        @Override
        @JsonProperty("name")
        public NodeExecutingStatisticsEntMixInBuilder setName(final String name);
        
        @Override
        @JsonProperty("annotation")
        public NodeExecutingStatisticsEntMixInBuilder setAnnotation(final String annotation);
        
        @Override
        @JsonProperty("nodeID")
        public NodeExecutingStatisticsEntMixInBuilder setNodeID(final String nodeID);
        
        @Override
        @JsonProperty("executionDuration")
        public NodeExecutingStatisticsEntMixInBuilder setExecutionDuration(final BigDecimal executionDuration);
        
        @Override
        @JsonProperty("progress")
        public NodeExecutingStatisticsEntMixInBuilder setProgress(final BigDecimal progress);
        
    }


}

