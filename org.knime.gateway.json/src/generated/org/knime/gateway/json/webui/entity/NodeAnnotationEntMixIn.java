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
package org.knime.gateway.json.webui.entity;

import org.knime.gateway.json.webui.entity.AnnotationEntMixIn;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


import org.knime.gateway.api.webui.entity.NodeAnnotationEnt;
import org.knime.gateway.impl.webui.entity.DefaultNodeAnnotationEnt;
import org.knime.gateway.impl.webui.entity.DefaultNodeAnnotationEnt.DefaultNodeAnnotationEntBuilder;

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
    defaultImpl = DefaultNodeAnnotationEnt.class)
@JsonSubTypes({
    @Type(value = DefaultNodeAnnotationEnt.class, name="NodeAnnotation")
})
@JsonDeserialize(builder=DefaultNodeAnnotationEntBuilder.class)
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.json-config.json"})
public interface NodeAnnotationEntMixIn extends NodeAnnotationEnt {

    @Override
    @JsonIgnore
    public String getTypeID();

    @Override
    @JsonProperty("text")
    public String getText();
    
    @Override
    @JsonProperty("default")
    public Boolean isDefault();
    

    /**
     * MixIn class for entity builder implementations that adds jackson annotations for the de-/serialization.
     *
     * @author Martin Horn, University of Konstanz
     */
    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "",
        defaultImpl = DefaultNodeAnnotationEntBuilder.class)
    @JsonSubTypes({
        @Type(value = DefaultNodeAnnotationEnt.DefaultNodeAnnotationEntBuilder.class, name="NodeAnnotation")
    })
    // AUTO-GENERATED CODE; DO NOT MODIFY
    public static interface NodeAnnotationEntMixInBuilder extends NodeAnnotationEntBuilder {
    
        @Override
        public NodeAnnotationEntMixIn build();
    
        @Override
        @JsonProperty("text")
        public NodeAnnotationEntMixInBuilder setText(final String text);
        
        @Override
        @JsonProperty("default")
        public NodeAnnotationEntMixInBuilder setDefault(final Boolean _default);
        
    }


}

