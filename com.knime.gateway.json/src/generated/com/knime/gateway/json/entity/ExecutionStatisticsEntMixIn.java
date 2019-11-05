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

import com.knime.gateway.entity.NodeExecutedStatisticsEnt;
import com.knime.gateway.entity.NodeExecutingStatisticsEnt;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


import com.knime.gateway.entity.ExecutionStatisticsEnt;
import com.knime.gateway.entity.impl.DefaultExecutionStatisticsEnt;
import com.knime.gateway.entity.impl.DefaultExecutionStatisticsEnt.DefaultExecutionStatisticsEntBuilder;

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
    defaultImpl = DefaultExecutionStatisticsEnt.class)
@JsonSubTypes({
    @Type(value = DefaultExecutionStatisticsEnt.class, name="ExecutionStatistics")
})
@JsonDeserialize(builder=DefaultExecutionStatisticsEntBuilder.class)
@javax.annotation.Generated(value = {"org.knime.gateway.codegen.GatewayCodegen", "src-gen/com.knime.gateway.json-config.json"})
public interface ExecutionStatisticsEntMixIn extends ExecutionStatisticsEnt {

    @Override
    @JsonIgnore
    public String getTypeID();

    @Override
    @JsonProperty("totalExecutionDuration")
    public BigDecimal getTotalExecutionDuration();
    
    @Override
    @JsonProperty("nodesExecuted")
    public java.util.List<NodeExecutedStatisticsEnt> getNodesExecuted();
    
    @Override
    @JsonProperty("nodesExecuting")
    public java.util.List<NodeExecutingStatisticsEnt> getNodesExecuting();
    

    /**
     * MixIn class for entity builder implementations that adds jackson annotations for the de-/serialization.
     *
     * @author Martin Horn, University of Konstanz
     */
    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "",
        defaultImpl = DefaultExecutionStatisticsEntBuilder.class)
    @JsonSubTypes({
        @Type(value = DefaultExecutionStatisticsEnt.DefaultExecutionStatisticsEntBuilder.class, name="ExecutionStatistics")
    })
    // AUTO-GENERATED CODE; DO NOT MODIFY
    public static interface ExecutionStatisticsEntMixInBuilder extends ExecutionStatisticsEntBuilder {
    
        @Override
        public ExecutionStatisticsEntMixIn build();
    
        @Override
        @JsonProperty("totalExecutionDuration")
        public ExecutionStatisticsEntMixInBuilder setTotalExecutionDuration(final BigDecimal totalExecutionDuration);
        
        @Override
        @JsonProperty("nodesExecuted")
        public ExecutionStatisticsEntMixInBuilder setNodesExecuted(final java.util.List<NodeExecutedStatisticsEnt> nodesExecuted);
        
        @Override
        @JsonProperty("nodesExecuting")
        public ExecutionStatisticsEntMixInBuilder setNodesExecuting(final java.util.List<NodeExecutingStatisticsEnt> nodesExecuting);
        
    }


}

