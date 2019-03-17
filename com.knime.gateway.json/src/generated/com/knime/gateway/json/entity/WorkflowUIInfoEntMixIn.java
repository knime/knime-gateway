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


import com.knime.gateway.v0.entity.WorkflowUIInfoEnt;
import com.knime.gateway.v0.entity.impl.DefaultWorkflowUIInfoEnt;
import com.knime.gateway.v0.entity.impl.DefaultWorkflowUIInfoEnt.DefaultWorkflowUIInfoEntBuilder;

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
    defaultImpl = DefaultWorkflowUIInfoEnt.class)
@JsonSubTypes({
    @Type(value = DefaultWorkflowUIInfoEnt.class, name="WorkflowUIInfo")
})
@JsonDeserialize(builder=DefaultWorkflowUIInfoEntBuilder.class)
@javax.annotation.Generated(value = {"org.knime.gateway.codegen.GatewayCodegen", "src-gen/com.knime.gateway.json-config.json"})
public interface WorkflowUIInfoEntMixIn extends WorkflowUIInfoEnt {

    @Override
    @JsonIgnore
    public String getTypeID();

    @Override
    @JsonProperty("gridX")
    public Integer getGridX();
    
    @Override
    @JsonProperty("gridY")
    public Integer getGridY();
    
    @Override
    @JsonProperty("snapToGrid")
    public Boolean isSnapToGrid();
    
    @Override
    @JsonProperty("showGrid")
    public Boolean isShowGrid();
    
    @Override
    @JsonProperty("zoomLevel")
    public BigDecimal getZoomLevel();
    
    @Override
    @JsonProperty("hasCurvedConnection")
    public Boolean hasCurvedConnection();
    
    @Override
    @JsonProperty("connectionLineWidth")
    public Integer getConnectionLineWidth();
    

    /**
     * MixIn class for entity builder implementations that adds jackson annotations for the de-/serialization.
     *
     * @author Martin Horn, University of Konstanz
     */
    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "",
        defaultImpl = DefaultWorkflowUIInfoEntBuilder.class)
    @JsonSubTypes({
        @Type(value = DefaultWorkflowUIInfoEnt.DefaultWorkflowUIInfoEntBuilder.class, name="WorkflowUIInfo")
    })
    // AUTO-GENERATED CODE; DO NOT MODIFY
    public static interface WorkflowUIInfoEntMixInBuilder extends WorkflowUIInfoEntBuilder {
    
        @Override
        public WorkflowUIInfoEntMixIn build();
    
        @Override
        @JsonProperty("gridX")
        public WorkflowUIInfoEntMixInBuilder setGridX(final Integer gridX);
        
        @Override
        @JsonProperty("gridY")
        public WorkflowUIInfoEntMixInBuilder setGridY(final Integer gridY);
        
        @Override
        @JsonProperty("snapToGrid")
        public WorkflowUIInfoEntMixInBuilder setSnapToGrid(final Boolean snapToGrid);
        
        @Override
        @JsonProperty("showGrid")
        public WorkflowUIInfoEntMixInBuilder setShowGrid(final Boolean showGrid);
        
        @Override
        @JsonProperty("zoomLevel")
        public WorkflowUIInfoEntMixInBuilder setZoomLevel(final BigDecimal zoomLevel);
        
        @Override
        @JsonProperty("hasCurvedConnection")
        public WorkflowUIInfoEntMixInBuilder setHasCurvedConnection(final Boolean hasCurvedConnection);
        
        @Override
        @JsonProperty("connectionLineWidth")
        public WorkflowUIInfoEntMixInBuilder setConnectionLineWidth(final Integer connectionLineWidth);
        
    }


}

