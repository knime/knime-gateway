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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


import org.knime.gateway.api.entity.NodeExecutedStatisticsEnt;
import org.knime.gateway.impl.entity.DefaultNodeExecutedStatisticsEnt;
import org.knime.gateway.impl.entity.DefaultNodeExecutedStatisticsEnt.DefaultNodeExecutedStatisticsEntBuilder;

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
    defaultImpl = DefaultNodeExecutedStatisticsEnt.class)
@JsonSubTypes({
    @Type(value = DefaultNodeExecutedStatisticsEnt.class, name="NodeExecutedStatistics")
})
@JsonDeserialize(builder=DefaultNodeExecutedStatisticsEntBuilder.class)
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/org.knime.gateway.json-config.json"})
public interface NodeExecutedStatisticsEntMixIn extends NodeExecutedStatisticsEnt {

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
    @JsonProperty("runs")
    public BigDecimal getRuns();
    

    /**
     * MixIn class for entity builder implementations that adds jackson annotations for the de-/serialization.
     *
     * @author Martin Horn, University of Konstanz
     */
    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "",
        defaultImpl = DefaultNodeExecutedStatisticsEntBuilder.class)
    @JsonSubTypes({
        @Type(value = DefaultNodeExecutedStatisticsEnt.DefaultNodeExecutedStatisticsEntBuilder.class, name="NodeExecutedStatistics")
    })
    // AUTO-GENERATED CODE; DO NOT MODIFY
    public static interface NodeExecutedStatisticsEntMixInBuilder extends NodeExecutedStatisticsEntBuilder {
    
        @Override
        public NodeExecutedStatisticsEntMixIn build();
    
        @Override
        @JsonProperty("name")
        public NodeExecutedStatisticsEntMixInBuilder setName(final String name);
        
        @Override
        @JsonProperty("annotation")
        public NodeExecutedStatisticsEntMixInBuilder setAnnotation(final String annotation);
        
        @Override
        @JsonProperty("nodeID")
        public NodeExecutedStatisticsEntMixInBuilder setNodeID(final String nodeID);
        
        @Override
        @JsonProperty("executionDuration")
        public NodeExecutedStatisticsEntMixInBuilder setExecutionDuration(final BigDecimal executionDuration);
        
        @Override
        @JsonProperty("runs")
        public NodeExecutedStatisticsEntMixInBuilder setRuns(final BigDecimal runs);
        
    }


}

