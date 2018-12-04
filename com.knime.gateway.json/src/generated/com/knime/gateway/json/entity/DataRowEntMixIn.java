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

import com.knime.gateway.v0.entity.DataCellEnt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


import com.knime.gateway.v0.entity.DataRowEnt;
import com.knime.gateway.v0.entity.impl.DefaultDataRowEnt;
import com.knime.gateway.v0.entity.impl.DefaultDataRowEnt.DefaultDataRowEntBuilder;

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
    defaultImpl = DefaultDataRowEnt.class)
@JsonSubTypes({
    @Type(value = DefaultDataRowEnt.class, name="DataRow")
})
@JsonDeserialize(builder=DefaultDataRowEntBuilder.class)
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public interface DataRowEntMixIn extends DataRowEnt {

    @Override
    @JsonIgnore
    public String getTypeID();

    @Override
    @JsonProperty("rowID")
    public String getRowID();
    
    @Override
    @JsonProperty("columns")
    public java.util.List<DataCellEnt> getColumns();
    

    /**
     * MixIn class for entity builder implementations that adds jackson annotations for the de-/serialization.
     *
     * @author Martin Horn, University of Konstanz
     */
    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "",
        defaultImpl = DefaultDataRowEntBuilder.class)
    @JsonSubTypes({
        @Type(value = DefaultDataRowEnt.DefaultDataRowEntBuilder.class, name="DataRow")
    })
    // AUTO-GENERATED CODE; DO NOT MODIFY
    public static interface DataRowEntMixInBuilder extends DataRowEntBuilder {
    
        @Override
        public DataRowEntMixIn build();
    
        @Override
        @JsonProperty("rowID")
        public DataRowEntMixInBuilder setRowID(final String rowID);
        
        @Override
        @JsonProperty("columns")
        public DataRowEntMixInBuilder setColumns(final java.util.List<DataCellEnt> columns);
        
    }


}

