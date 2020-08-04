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

import org.knime.gateway.api.webui.entity.BoundsEnt;
import org.knime.gateway.json.webui.entity.AnnotationEntMixIn;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


import org.knime.gateway.api.webui.entity.WorkflowAnnotationEnt;
import org.knime.gateway.impl.webui.entity.DefaultWorkflowAnnotationEnt;
import org.knime.gateway.impl.webui.entity.DefaultWorkflowAnnotationEnt.DefaultWorkflowAnnotationEntBuilder;

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
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.json-config.json"})
public interface WorkflowAnnotationEntMixIn extends WorkflowAnnotationEnt {

    @Override
    @JsonIgnore
    public String getTypeID();

    @Override
    @JsonProperty("text")
    public String getText();
    
    @Override
    @JsonProperty("textAlign")
    public TextAlignEnum getTextAlign();
    
    @Override
    @JsonProperty("defaultFontSize")
    public Integer getDefaultFontSize();
    
    @Override
    @JsonProperty("borderWidth")
    public Integer getBorderWidth();
    
    @Override
    @JsonProperty("borderColor")
    public String getBorderColor();
    
    @Override
    @JsonProperty("backgroundColor")
    public String getBackgroundColor();
    
    @Override
    @JsonProperty("bounds")
    public BoundsEnt getBounds();
    

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
        @JsonProperty("text")
        public WorkflowAnnotationEntMixInBuilder setText(final String text);
        
        @Override
        @JsonProperty("textAlign")
        public WorkflowAnnotationEntMixInBuilder setTextAlign(final TextAlignEnum textAlign);
        
        @Override
        @JsonProperty("defaultFontSize")
        public WorkflowAnnotationEntMixInBuilder setDefaultFontSize(final Integer defaultFontSize);
        
        @Override
        @JsonProperty("borderWidth")
        public WorkflowAnnotationEntMixInBuilder setBorderWidth(final Integer borderWidth);
        
        @Override
        @JsonProperty("borderColor")
        public WorkflowAnnotationEntMixInBuilder setBorderColor(final String borderColor);
        
        @Override
        @JsonProperty("backgroundColor")
        public WorkflowAnnotationEntMixInBuilder setBackgroundColor(final String backgroundColor);
        
        @Override
        @JsonProperty("bounds")
        public WorkflowAnnotationEntMixInBuilder setBounds(final BoundsEnt bounds);
        
    }


}

