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


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


import com.knime.gateway.entity.DataCellEnt;
import com.knime.gateway.entity.impl.DefaultDataCellEnt;
import com.knime.gateway.entity.impl.DefaultDataCellEnt.DefaultDataCellEntBuilder;

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
    defaultImpl = DefaultDataCellEnt.class)
@JsonSubTypes({
    @Type(value = DefaultDataCellEnt.class, name="DataCell")
})
@JsonDeserialize(builder=DefaultDataCellEntBuilder.class)
@javax.annotation.Generated(value = {"org.knime.gateway.codegen.GatewayCodegen", "src-gen/com.knime.gateway.json-config.json"})
public interface DataCellEntMixIn extends DataCellEnt {

    @Override
    @JsonIgnore
    public String getTypeID();

    @Override
    @JsonProperty("type")
    public String getType();
    
    @Override
    @JsonProperty("valueAsString")
    public String getValueAsString();
    
    @Override
    @JsonProperty("missing")
    public Boolean isMissing();
    
    @Override
    @JsonProperty("binary")
    public Boolean isBinary();
    
    @Override
    @JsonProperty("problem")
    public Boolean isProblem();
    

    /**
     * MixIn class for entity builder implementations that adds jackson annotations for the de-/serialization.
     *
     * @author Martin Horn, University of Konstanz
     */
    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "",
        defaultImpl = DefaultDataCellEntBuilder.class)
    @JsonSubTypes({
        @Type(value = DefaultDataCellEnt.DefaultDataCellEntBuilder.class, name="DataCell")
    })
    // AUTO-GENERATED CODE; DO NOT MODIFY
    public static interface DataCellEntMixInBuilder extends DataCellEntBuilder {
    
        @Override
        public DataCellEntMixIn build();
    
        @Override
        @JsonProperty("type")
        public DataCellEntMixInBuilder setType(final String type);
        
        @Override
        @JsonProperty("valueAsString")
        public DataCellEntMixInBuilder setValueAsString(final String valueAsString);
        
        @Override
        @JsonProperty("missing")
        public DataCellEntMixInBuilder setMissing(final Boolean missing);
        
        @Override
        @JsonProperty("binary")
        public DataCellEntMixInBuilder setBinary(final Boolean binary);
        
        @Override
        @JsonProperty("problem")
        public DataCellEntMixInBuilder setProblem(final Boolean problem);
        
    }


}
