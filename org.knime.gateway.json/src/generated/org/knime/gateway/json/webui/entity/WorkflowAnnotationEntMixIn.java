/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.com; Email: contact@knime.com
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME AG herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ------------------------------------------------------------------------
 */
package org.knime.gateway.json.webui.entity;

import org.knime.gateway.api.webui.entity.BoundsEnt;
import org.knime.gateway.api.webui.entity.StyleRangeEnt;
import org.knime.gateway.api.webui.entity.TypedTextEnt;
import org.knime.gateway.json.webui.entity.AnnotationEntMixIn;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.knime.gateway.api.webui.entity.WorkflowAnnotationEnt;
import org.knime.gateway.impl.webui.entity.DefaultWorkflowAnnotationEnt.DefaultWorkflowAnnotationEntBuilder;

/**
 * MixIn class for entity implementations that adds jackson annotations for de-/serialization.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */

@JsonDeserialize(builder=DefaultWorkflowAnnotationEntBuilder.class)
@JsonSerialize(as=WorkflowAnnotationEnt.class)
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.json-config.json"})
public interface WorkflowAnnotationEntMixIn extends WorkflowAnnotationEnt {

    @Override
    @JsonIgnore
    public String getTypeID();

    @Override
    @JsonProperty("text")
    public TypedTextEnt getText();
    
    @Override
    @JsonProperty("backgroundColor")
    public String getBackgroundColor();
    
    @Override
    @JsonProperty("textAlign")
    public TextAlignEnum getTextAlign();
    
    @Override
    @JsonProperty("defaultFontSize")
    public Integer getDefaultFontSize();
    
    @Override
    @JsonProperty("styleRanges")
    public java.util.List<StyleRangeEnt> getStyleRanges();
    
    @Override
    @JsonProperty("bounds")
    public BoundsEnt getBounds();
    
    @Override
    @JsonProperty("id")
    public org.knime.gateway.api.entity.AnnotationIDEnt getId();
    
    @Override
    @JsonProperty("borderWidth")
    public Integer getBorderWidth();
    
    @Override
    @JsonProperty("borderColor")
    public String getBorderColor();
    

    /**
     * MixIn class for entity builder implementations that adds jackson annotations for the de-/serialization.
     *
     * @author Martin Horn, University of Konstanz
     */

    // AUTO-GENERATED CODE; DO NOT MODIFY
    public static interface WorkflowAnnotationEntMixInBuilder extends WorkflowAnnotationEntBuilder {
    
        @Override
        public WorkflowAnnotationEntMixIn build();
    
        @Override
        @JsonProperty("text")
        public WorkflowAnnotationEntMixInBuilder setText(final TypedTextEnt text);
        
        @Override
        @JsonProperty("backgroundColor")
        public WorkflowAnnotationEntMixInBuilder setBackgroundColor(final String backgroundColor);
        
        @Override
        @JsonProperty("textAlign")
        public WorkflowAnnotationEntMixInBuilder setTextAlign(final TextAlignEnum textAlign);
        
        @Override
        @JsonProperty("defaultFontSize")
        public WorkflowAnnotationEntMixInBuilder setDefaultFontSize(final Integer defaultFontSize);
        
        @Override
        @JsonProperty("styleRanges")
        public WorkflowAnnotationEntMixInBuilder setStyleRanges(final java.util.List<StyleRangeEnt> styleRanges);
        
        @Override
        @JsonProperty("bounds")
        public WorkflowAnnotationEntMixInBuilder setBounds(final BoundsEnt bounds);
        
        @Override
        @JsonProperty("id")
        public WorkflowAnnotationEntMixInBuilder setId(final org.knime.gateway.api.entity.AnnotationIDEnt id);
        
        @Override
        @JsonProperty("borderWidth")
        public WorkflowAnnotationEntMixInBuilder setBorderWidth(final Integer borderWidth);
        
        @Override
        @JsonProperty("borderColor")
        public WorkflowAnnotationEntMixInBuilder setBorderColor(final String borderColor);
        
    }


}

