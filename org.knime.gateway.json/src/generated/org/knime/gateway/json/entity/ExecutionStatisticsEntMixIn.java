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

import java.math.BigDecimal;
import org.knime.gateway.api.entity.NodeExecutedStatisticsEnt;
import org.knime.gateway.api.entity.NodeExecutingStatisticsEnt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


import org.knime.gateway.api.entity.ExecutionStatisticsEnt;
import org.knime.gateway.impl.entity.DefaultExecutionStatisticsEnt;
import org.knime.gateway.impl.entity.DefaultExecutionStatisticsEnt.DefaultExecutionStatisticsEntBuilder;

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
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/org.knime.gateway.json-config.json"})
public interface ExecutionStatisticsEntMixIn extends ExecutionStatisticsEnt {

    @Override
    @JsonIgnore
    public String getTypeID();

    @Override
    @JsonProperty("totalExecutionDuration")
    public BigDecimal getTotalExecutionDuration();
    
    @Override
    @JsonProperty("totalNodeExecutionsCount")
    public Integer getTotalNodeExecutionsCount();
    
    @Override
    @JsonProperty("nodesExecuted")
    public java.util.List<NodeExecutedStatisticsEnt> getNodesExecuted();
    
    @Override
    @JsonProperty("nodesExecuting")
    public java.util.List<NodeExecutingStatisticsEnt> getNodesExecuting();
    
    @Override
    @JsonProperty("wizardExecutionState")
    public WizardExecutionStateEnum getWizardExecutionState();
    

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
        @JsonProperty("totalNodeExecutionsCount")
        public ExecutionStatisticsEntMixInBuilder setTotalNodeExecutionsCount(final Integer totalNodeExecutionsCount);
        
        @Override
        @JsonProperty("nodesExecuted")
        public ExecutionStatisticsEntMixInBuilder setNodesExecuted(final java.util.List<NodeExecutedStatisticsEnt> nodesExecuted);
        
        @Override
        @JsonProperty("nodesExecuting")
        public ExecutionStatisticsEntMixInBuilder setNodesExecuting(final java.util.List<NodeExecutingStatisticsEnt> nodesExecuting);
        
        @Override
        @JsonProperty("wizardExecutionState")
        public ExecutionStatisticsEntMixInBuilder setWizardExecutionState(final WizardExecutionStateEnum wizardExecutionState);
        
    }


}

