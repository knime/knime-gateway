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

import org.knime.gateway.api.webui.entity.StyleRangeEnt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


import org.knime.gateway.api.webui.entity.AnnotationEnt;
import org.knime.gateway.impl.webui.entity.DefaultAnnotationEnt;
import org.knime.gateway.impl.webui.entity.DefaultAnnotationEnt.DefaultAnnotationEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultNodeAnnotationEnt;
import org.knime.gateway.impl.webui.entity.DefaultWorkflowAnnotationEnt;

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
    defaultImpl = DefaultAnnotationEnt.class)
@JsonSubTypes({
    @Type(value = DefaultAnnotationEnt.class, name="Annotation")
,
  @Type(value = DefaultNodeAnnotationEnt.class, name = "NodeAnnotation")
,
  @Type(value = DefaultWorkflowAnnotationEnt.class, name = "WorkflowAnnotation")
})
@JsonDeserialize(builder=DefaultAnnotationEntBuilder.class)
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.json-config.json"})
public interface AnnotationEntMixIn extends AnnotationEnt {

    @Override
    @JsonIgnore
    public String getTypeID();

    @Override
    @JsonProperty("text")
    public String getText();
    
    @Override
    @JsonProperty("backgroundColor")
    public String getBackgroundColor();
    
    @Override
    @JsonProperty("textAlign")
    public TextAlignEnum getTextAlign();
    
    @Override
    @JsonProperty("borderWidth")
    public Integer getBorderWidth();
    
    @Override
    @JsonProperty("borderColor")
    public String getBorderColor();
    
    @Override
    @JsonProperty("styleRanges")
    public java.util.List<StyleRangeEnt> getStyleRanges();
    

    /**
     * MixIn class for entity builder implementations that adds jackson annotations for the de-/serialization.
     *
     * @author Martin Horn, University of Konstanz
     */
    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "",
        defaultImpl = DefaultAnnotationEntBuilder.class)
    @JsonSubTypes({
        @Type(value = DefaultAnnotationEnt.DefaultAnnotationEntBuilder.class, name="Annotation")
        ,
      @Type(value = DefaultNodeAnnotationEnt.DefaultNodeAnnotationEntBuilder.class, name = "NodeAnnotation")
        ,
      @Type(value = DefaultWorkflowAnnotationEnt.DefaultWorkflowAnnotationEntBuilder.class, name = "WorkflowAnnotation")
    })
    // AUTO-GENERATED CODE; DO NOT MODIFY
    public static interface AnnotationEntMixInBuilder extends AnnotationEntBuilder {
    
        @Override
        public AnnotationEntMixIn build();
    
        @Override
        @JsonProperty("text")
        public AnnotationEntMixInBuilder setText(final String text);
        
        @Override
        @JsonProperty("backgroundColor")
        public AnnotationEntMixInBuilder setBackgroundColor(final String backgroundColor);
        
        @Override
        @JsonProperty("textAlign")
        public AnnotationEntMixInBuilder setTextAlign(final TextAlignEnum textAlign);
        
        @Override
        @JsonProperty("borderWidth")
        public AnnotationEntMixInBuilder setBorderWidth(final Integer borderWidth);
        
        @Override
        @JsonProperty("borderColor")
        public AnnotationEntMixInBuilder setBorderColor(final String borderColor);
        
        @Override
        @JsonProperty("styleRanges")
        public AnnotationEntMixInBuilder setStyleRanges(final java.util.List<StyleRangeEnt> styleRanges);
        
    }


}

