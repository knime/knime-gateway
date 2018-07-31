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

import com.knime.gateway.json.entity.AnnotationEntMixIn;
import com.knime.gateway.v0.entity.BoundsEnt;
import com.knime.gateway.v0.entity.StyleRangeEnt;


import com.knime.gateway.json.JsonUtil;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


import com.knime.gateway.v0.entity.WorkflowAnnotationEnt;
import com.knime.gateway.v0.entity.impl.DefaultWorkflowAnnotationEnt;
import com.knime.gateway.v0.entity.impl.DefaultWorkflowAnnotationEnt.DefaultWorkflowAnnotationEntBuilder;

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
    defaultImpl = DefaultWorkflowAnnotationEnt.class)
@JsonSubTypes({
    @Type(value = DefaultWorkflowAnnotationEnt.class, name="WorkflowAnnotation")
})
@JsonDeserialize(builder=DefaultWorkflowAnnotationEntBuilder.class)
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public interface WorkflowAnnotationEntMixIn extends WorkflowAnnotationEnt {

    @Override
    @JsonIgnore
    public String getTypeID();

    @Override
    @JsonProperty("type")
    public String getType();
    
    @Override
    @JsonProperty("text")
    public String getText();
    
    @Override
    @JsonProperty("backgroundColor")
    public Integer getBackgroundColor();
    
    @Override
    @JsonProperty("bounds")
    public BoundsEnt getBounds();
    
    @Override
    @JsonProperty("textAlignment")
    public String getTextAlignment();
    
    @Override
    @JsonProperty("borderSize")
    public Integer getBorderSize();
    
    @Override
    @JsonProperty("borderColor")
    public Integer getBorderColor();
    
    @Override
    @JsonProperty("defaultFontSize")
    public Integer getDefaultFontSize();
    
    @Override
    @JsonProperty("version")
    public Integer getVersion();
    
    @Override
    @JsonProperty("styleRanges")
    public java.util.List<StyleRangeEnt> getStyleRanges();
    
    @Override
    @JsonProperty("annotationID")
    public String getAnnotationID();
    

    /**
     * MixIn class for entity builder implementations that adds jackson annotations for the de-/serialization.
     *
     * @author Martin Horn, University of Konstanz
     */
    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "",
        defaultImpl = DefaultWorkflowAnnotationEntBuilder.class)
    @JsonSubTypes({
        @Type(value = DefaultWorkflowAnnotationEnt.DefaultWorkflowAnnotationEntBuilder.class, name="WorkflowAnnotation")
    })
    // AUTO-GENERATED CODE; DO NOT MODIFY
    public static interface WorkflowAnnotationEntMixInBuilder extends WorkflowAnnotationEntBuilder {
    
        @Override
        public WorkflowAnnotationEntMixIn build();
    
        @Override
        @JsonProperty("type")
        public WorkflowAnnotationEntMixInBuilder setType(final String type);
        
        @Override
        @JsonProperty("text")
        public WorkflowAnnotationEntMixInBuilder setText(final String text);
        
        @Override
        @JsonProperty("backgroundColor")
        public WorkflowAnnotationEntMixInBuilder setBackgroundColor(final Integer backgroundColor);
        
        @Override
        @JsonProperty("bounds")
        public WorkflowAnnotationEntMixInBuilder setBounds(final BoundsEnt bounds);
        
        @Override
        @JsonProperty("textAlignment")
        public WorkflowAnnotationEntMixInBuilder setTextAlignment(final String textAlignment);
        
        @Override
        @JsonProperty("borderSize")
        public WorkflowAnnotationEntMixInBuilder setBorderSize(final Integer borderSize);
        
        @Override
        @JsonProperty("borderColor")
        public WorkflowAnnotationEntMixInBuilder setBorderColor(final Integer borderColor);
        
        @Override
        @JsonProperty("defaultFontSize")
        public WorkflowAnnotationEntMixInBuilder setDefaultFontSize(final Integer defaultFontSize);
        
        @Override
        @JsonProperty("version")
        public WorkflowAnnotationEntMixInBuilder setVersion(final Integer version);
        
        @Override
        @JsonProperty("styleRanges")
        public WorkflowAnnotationEntMixInBuilder setStyleRanges(final java.util.List<StyleRangeEnt> styleRanges);
        
        @Override
        @JsonProperty("annotationID")
        public WorkflowAnnotationEntMixInBuilder setAnnotationID(final String annotationID);
        
    }


}

