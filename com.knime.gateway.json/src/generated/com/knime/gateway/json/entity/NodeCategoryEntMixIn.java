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

import com.knime.gateway.v0.entity.NodeCategoryEnt;
import com.knime.gateway.v0.entity.NodeFactoryKeyEnt;


import com.knime.gateway.json.JsonUtil;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


import com.knime.gateway.v0.entity.NodeCategoryEnt;
import com.knime.gateway.v0.entity.impl.DefaultNodeCategoryEnt;
import com.knime.gateway.v0.entity.impl.DefaultNodeCategoryEnt.DefaultNodeCategoryEntBuilder;

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
    defaultImpl = DefaultNodeCategoryEnt.class)
@JsonSubTypes({
    @Type(value = DefaultNodeCategoryEnt.class, name="NodeCategory")
})
@JsonDeserialize(builder=DefaultNodeCategoryEntBuilder.class)
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public interface NodeCategoryEntMixIn extends NodeCategoryEnt {

    @Override
    @JsonIgnore
    public String getTypeID();

    @Override
    @JsonProperty("name")
    public String getName();
    
    @Override
    @JsonProperty("categoryChildren")
    public java.util.List<NodeCategoryEnt> getCategoryChildren();
    
    @Override
    @JsonProperty("nodeFactoryChildren")
    public java.util.List<NodeFactoryKeyEnt> getNodeFactoryChildren();
    

    /**
     * MixIn class for entity builder implementations that adds jackson annotations for the de-/serialization.
     *
     * @author Martin Horn, University of Konstanz
     */
    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "",
        defaultImpl = DefaultNodeCategoryEntBuilder.class)
    @JsonSubTypes({
        @Type(value = DefaultNodeCategoryEnt.DefaultNodeCategoryEntBuilder.class, name="NodeCategory")
    })
    // AUTO-GENERATED CODE; DO NOT MODIFY
    public static interface NodeCategoryEntMixInBuilder extends NodeCategoryEntBuilder {
    
        @Override
        public NodeCategoryEntMixIn build();
    
        @Override
        @JsonProperty("name")
        public NodeCategoryEntMixInBuilder setName(final String name);
        
        @Override
        @JsonProperty("categoryChildren")
        public NodeCategoryEntMixInBuilder setCategoryChildren(final java.util.List<NodeCategoryEnt> categoryChildren);
        
        @Override
        @JsonProperty("nodeFactoryChildren")
        public NodeCategoryEntMixInBuilder setNodeFactoryChildren(final java.util.List<NodeFactoryKeyEnt> nodeFactoryChildren);
        
    }


}

