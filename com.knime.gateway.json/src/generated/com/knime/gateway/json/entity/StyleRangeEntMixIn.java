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


import com.knime.gateway.v0.entity.StyleRangeEnt;
import com.knime.gateway.v0.entity.impl.DefaultStyleRangeEnt;
import com.knime.gateway.v0.entity.impl.DefaultStyleRangeEnt.DefaultStyleRangeEntBuilder;

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
    defaultImpl = DefaultStyleRangeEnt.class)
@JsonSubTypes({
    @Type(value = DefaultStyleRangeEnt.class, name="StyleRange")
})
@JsonDeserialize(builder=DefaultStyleRangeEntBuilder.class)
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public interface StyleRangeEntMixIn extends StyleRangeEnt {

    @Override
    @JsonIgnore
    public String getTypeID();

    @Override
    @JsonProperty("start")
    public Integer getStart();
    
    @Override
    @JsonProperty("length")
    public Integer getLength();
    
    @Override
    @JsonProperty("fontName")
    public String getFontName();
    
    @Override
    @JsonProperty("fontStyle")
    public FontStyleEnum getFontStyle();
    
    @Override
    @JsonProperty("fontSize")
    public Integer getFontSize();
    
    @Override
    @JsonProperty("foregroundColor")
    public Integer getForegroundColor();
    

    /**
     * MixIn class for entity builder implementations that adds jackson annotations for the de-/serialization.
     *
     * @author Martin Horn, University of Konstanz
     */
    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "",
        defaultImpl = DefaultStyleRangeEntBuilder.class)
    @JsonSubTypes({
        @Type(value = DefaultStyleRangeEnt.DefaultStyleRangeEntBuilder.class, name="StyleRange")
    })
    // AUTO-GENERATED CODE; DO NOT MODIFY
    public static interface StyleRangeEntMixInBuilder extends StyleRangeEntBuilder {
    
        @Override
        public StyleRangeEntMixIn build();
    
        @Override
        @JsonProperty("start")
        public StyleRangeEntMixInBuilder setStart(final Integer start);
        
        @Override
        @JsonProperty("length")
        public StyleRangeEntMixInBuilder setLength(final Integer length);
        
        @Override
        @JsonProperty("fontName")
        public StyleRangeEntMixInBuilder setFontName(final String fontName);
        
        @Override
        @JsonProperty("fontStyle")
        public StyleRangeEntMixInBuilder setFontStyle(final FontStyleEnum fontStyle);
        
        @Override
        @JsonProperty("fontSize")
        public StyleRangeEntMixInBuilder setFontSize(final Integer fontSize);
        
        @Override
        @JsonProperty("foregroundColor")
        public StyleRangeEntMixInBuilder setForegroundColor(final Integer foregroundColor);
        
    }


}

