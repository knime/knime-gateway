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

import org.knime.gateway.api.entity.DataRowEnt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


import org.knime.gateway.api.entity.DataTableEnt;
import org.knime.gateway.impl.entity.DefaultDataTableEnt;
import org.knime.gateway.impl.entity.DefaultDataTableEnt.DefaultDataTableEntBuilder;

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
    defaultImpl = DefaultDataTableEnt.class)
@JsonSubTypes({
    @Type(value = DefaultDataTableEnt.class, name="DataTable")
})
@JsonDeserialize(builder=DefaultDataTableEntBuilder.class)
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/org.knime.gateway.json-config.json"})
public interface DataTableEntMixIn extends DataTableEnt {

    @Override
    @JsonIgnore
    public String getTypeID();

    @Override
    @JsonProperty("columnNames")
    public java.util.List<String> getColumnNames();
    
    @Override
    @JsonProperty("numTotalRows")
    public Long getNumTotalRows();
    
    @Override
    @JsonProperty("rows")
    public java.util.List<DataRowEnt> getRows();
    

    /**
     * MixIn class for entity builder implementations that adds jackson annotations for the de-/serialization.
     *
     * @author Martin Horn, University of Konstanz
     */
    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "",
        defaultImpl = DefaultDataTableEntBuilder.class)
    @JsonSubTypes({
        @Type(value = DefaultDataTableEnt.DefaultDataTableEntBuilder.class, name="DataTable")
    })
    // AUTO-GENERATED CODE; DO NOT MODIFY
    public static interface DataTableEntMixInBuilder extends DataTableEntBuilder {
    
        @Override
        public DataTableEntMixIn build();
    
        @Override
        @JsonProperty("columnNames")
        public DataTableEntMixInBuilder setColumnNames(final java.util.List<String> columnNames);
        
        @Override
        @JsonProperty("numTotalRows")
        public DataTableEntMixInBuilder setNumTotalRows(final Long numTotalRows);
        
        @Override
        @JsonProperty("rows")
        public DataTableEntMixInBuilder setRows(final java.util.List<DataRowEnt> rows);
        
    }


}

