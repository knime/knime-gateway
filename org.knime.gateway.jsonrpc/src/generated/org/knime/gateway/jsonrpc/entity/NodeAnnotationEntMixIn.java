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
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
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
package org.knime.gateway.jsonrpc.entity;

import org.knime.gateway.jsonrpc.entity.AnnotationEntMixIn;
import org.knime.gateway.v0.entity.BoundsEnt;
import org.knime.gateway.v0.entity.StyleRangeEnt;


import org.knime.gateway.jsonrpc.JsonRpcUtil;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


import org.knime.gateway.v0.entity.NodeAnnotationEnt;
import org.knime.gateway.v0.entity.impl.DefaultNodeAnnotationEnt;
import org.knime.gateway.v0.entity.impl.DefaultNodeAnnotationEnt.DefaultNodeAnnotationEntBuilder;

/**
 * MixIn class for entity implementations that adds jackson annotations for de-/serialization.
 *
 * @author Martin Horn, University of Konstanz
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = JsonRpcUtil.ENTITY_TYPE_KEY,
    defaultImpl = DefaultNodeAnnotationEnt.class)
@JsonSubTypes({
    @Type(value = DefaultNodeAnnotationEnt.class, name="NodeAnnotation")
})
@JsonDeserialize(builder=DefaultNodeAnnotationEntBuilder.class)
// AUTO-GENERATED CODE; DO NOT MODIFY
public interface NodeAnnotationEntMixIn extends NodeAnnotationEnt {

    @Override
    @JsonProperty("type")
    public Integer getType();
    
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
    @JsonProperty("_default")
    public Boolean isDefault();
    

    /**
     * MixIn class for entity builder implementations that adds jackson annotations for the de-/serialization.
     *
     * @author Martin Horn, University of Konstanz
     */
    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = JsonRpcUtil.ENTITY_TYPE_KEY,
        defaultImpl = DefaultNodeAnnotationEntBuilder.class)
    @JsonSubTypes({
        @Type(value = DefaultNodeAnnotationEnt.DefaultNodeAnnotationEntBuilder.class, name="NodeAnnotation")
    })
    // AUTO-GENERATED CODE; DO NOT MODIFY
    public static interface NodeAnnotationEntMixInBuilder extends NodeAnnotationEntBuilder {
    
        @Override
        public NodeAnnotationEntMixIn build();
    
        @Override
        @JsonProperty("type")
        public NodeAnnotationEntMixInBuilder setType(final Integer type);
        
        @Override
        @JsonProperty("text")
        public NodeAnnotationEntMixInBuilder setText(final String text);
        
        @Override
        @JsonProperty("backgroundColor")
        public NodeAnnotationEntMixInBuilder setBackgroundColor(final Integer backgroundColor);
        
        @Override
        @JsonProperty("bounds")
        public NodeAnnotationEntMixInBuilder setBounds(final BoundsEnt bounds);
        
        @Override
        @JsonProperty("textAlignment")
        public NodeAnnotationEntMixInBuilder setTextAlignment(final String textAlignment);
        
        @Override
        @JsonProperty("borderSize")
        public NodeAnnotationEntMixInBuilder setBorderSize(final Integer borderSize);
        
        @Override
        @JsonProperty("borderColor")
        public NodeAnnotationEntMixInBuilder setBorderColor(final Integer borderColor);
        
        @Override
        @JsonProperty("defaultFontSize")
        public NodeAnnotationEntMixInBuilder setDefaultFontSize(final Integer defaultFontSize);
        
        @Override
        @JsonProperty("version")
        public NodeAnnotationEntMixInBuilder setVersion(final Integer version);
        
        @Override
        @JsonProperty("styleRanges")
        public NodeAnnotationEntMixInBuilder setStyleRanges(final java.util.List<StyleRangeEnt> styleRanges);
        
        @Override
        @JsonProperty("_default")
        public NodeAnnotationEntMixInBuilder setDefault(final Boolean _default);
        
    }


}

