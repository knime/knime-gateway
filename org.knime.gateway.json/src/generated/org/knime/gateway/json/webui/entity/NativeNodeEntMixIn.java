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

import org.knime.gateway.api.webui.entity.AllowedNodeActionsEnt;
import org.knime.gateway.api.webui.entity.LoopInfoEnt;
import org.knime.gateway.api.webui.entity.NodeAnnotationEnt;
import org.knime.gateway.api.webui.entity.NodeExecutionInfoEnt;
import org.knime.gateway.api.webui.entity.NodePortEnt;
import org.knime.gateway.api.webui.entity.NodeStateEnt;
import org.knime.gateway.api.webui.entity.XYEnt;
import org.knime.gateway.json.webui.entity.NodeEntMixIn;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.knime.gateway.api.webui.entity.NativeNodeEnt;
import org.knime.gateway.impl.webui.entity.DefaultNativeNodeEnt.DefaultNativeNodeEntBuilder;

/**
 * MixIn class for entity implementations that adds jackson annotations for de-/serialization.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */

@JsonDeserialize(builder=DefaultNativeNodeEntBuilder.class)
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.json-config.json"})
public interface NativeNodeEntMixIn extends NativeNodeEnt {

    @Override
    @JsonIgnore
    public String getTypeID();

    @Override
    @JsonProperty("id")
    public org.knime.gateway.api.entity.NodeIDEnt getId();
    
    @Override
    @JsonProperty("inPorts")
    public java.util.List<? extends NodePortEnt> getInPorts();
    
    @Override
    @JsonProperty("outPorts")
    public java.util.List<? extends NodePortEnt> getOutPorts();
    
    @Override
    @JsonProperty("annotation")
    public NodeAnnotationEnt getAnnotation();
    
    @Override
    @JsonProperty("position")
    public XYEnt getPosition();
    
    @Override
    @JsonProperty("kind")
    public KindEnum getKind();
    
    @Override
    @JsonProperty("allowedActions")
    public AllowedNodeActionsEnt getAllowedActions();
    
    @Override
    @JsonProperty("executionInfo")
    public NodeExecutionInfoEnt getExecutionInfo();
    
    @Override
    @JsonProperty("templateId")
    public String getTemplateId();
    
    @Override
    @JsonProperty("state")
    public NodeStateEnt getState();
    
    @Override
    @JsonProperty("loopInfo")
    public LoopInfoEnt getLoopInfo();
    

    /**
     * MixIn class for entity builder implementations that adds jackson annotations for the de-/serialization.
     *
     * @author Martin Horn, University of Konstanz
     */

    // AUTO-GENERATED CODE; DO NOT MODIFY
    public static interface NativeNodeEntMixInBuilder extends NativeNodeEntBuilder {
    
        @Override
        public NativeNodeEntMixIn build();
    
        @Override
        @JsonProperty("id")
        public NativeNodeEntMixInBuilder setId(final org.knime.gateway.api.entity.NodeIDEnt id);
        
        @Override
        @JsonProperty("inPorts")
        public NativeNodeEntMixInBuilder setInPorts(final java.util.List<? extends NodePortEnt> inPorts);
        
        @Override
        @JsonProperty("outPorts")
        public NativeNodeEntMixInBuilder setOutPorts(final java.util.List<? extends NodePortEnt> outPorts);
        
        @Override
        @JsonProperty("annotation")
        public NativeNodeEntMixInBuilder setAnnotation(final NodeAnnotationEnt annotation);
        
        @Override
        @JsonProperty("position")
        public NativeNodeEntMixInBuilder setPosition(final XYEnt position);
        
        @Override
        @JsonProperty("kind")
        public NativeNodeEntMixInBuilder setKind(final KindEnum kind);
        
        @Override
        @JsonProperty("allowedActions")
        public NativeNodeEntMixInBuilder setAllowedActions(final AllowedNodeActionsEnt allowedActions);
        
        @Override
        @JsonProperty("executionInfo")
        public NativeNodeEntMixInBuilder setExecutionInfo(final NodeExecutionInfoEnt executionInfo);
        
        @Override
        @JsonProperty("templateId")
        public NativeNodeEntMixInBuilder setTemplateId(final String templateId);
        
        @Override
        @JsonProperty("state")
        public NativeNodeEntMixInBuilder setState(final NodeStateEnt state);
        
        @Override
        @JsonProperty("loopInfo")
        public NativeNodeEntMixInBuilder setLoopInfo(final LoopInfoEnt loopInfo);
        
    }


}
