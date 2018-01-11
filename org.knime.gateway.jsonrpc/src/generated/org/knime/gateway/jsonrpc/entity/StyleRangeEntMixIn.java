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
package org.knime.gateway.jsonrpc.entity;



import org.knime.gateway.jsonrpc.JsonRpcUtil;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


import org.knime.gateway.v0.entity.StyleRangeEnt;
import org.knime.gateway.v0.entity.impl.DefaultStyleRangeEnt;
import org.knime.gateway.v0.entity.impl.DefaultStyleRangeEnt.DefaultStyleRangeEntBuilder;

/**
 * MixIn class for entity implementations that adds jackson annotations for de-/serialization.
 *
 * @author Martin Horn, University of Konstanz
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
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen", date = "2018-01-10T17:43:16.542+01:00")
public interface StyleRangeEntMixIn extends StyleRangeEnt {

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

