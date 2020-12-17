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

import org.knime.gateway.api.webui.entity.AllowedActionsEnt;
import org.knime.gateway.api.webui.entity.NodeAnnotationEnt;
import org.knime.gateway.api.webui.entity.NodeExecutionInfoEnt;
import org.knime.gateway.api.webui.entity.NodePortEnt;
import org.knime.gateway.api.webui.entity.NodeStateEnt;
import org.knime.gateway.api.webui.entity.NodeViewEnt;
import org.knime.gateway.api.webui.entity.XYEnt;
import org.knime.gateway.json.webui.entity.ComponentNodeAndTemplateEntMixIn;
import org.knime.gateway.json.webui.entity.NodeEntMixIn;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.knime.gateway.api.webui.entity.ComponentNodeEnt;
import org.knime.gateway.impl.webui.entity.DefaultComponentNodeEnt.DefaultComponentNodeEntBuilder;

/**
 * MixIn class for entity implementations that adds jackson annotations for de-/serialization.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */

@JsonDeserialize(builder=DefaultComponentNodeEntBuilder.class)
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.json-config.json"})
public interface ComponentNodeEntMixIn extends ComponentNodeEnt {

    @Override
    @JsonIgnore
    public String getTypeID();

    @Override
    @JsonProperty("id")
    public org.knime.gateway.api.entity.NodeIDEnt getId();
    
    @Override
    @JsonProperty("dialog")
    public Boolean isDialog();
    
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
    public AllowedActionsEnt getAllowedActions();
    
    @Override
    @JsonProperty("executionInfo")
    public NodeExecutionInfoEnt getExecutionInfo();
    
    @Override
    @JsonProperty("name")
    public String getName();
    
    @Override
    @JsonProperty("type")
    public TypeEnum getType();
    
    @Override
    @JsonProperty("icon")
    public String getIcon();
    
    @Override
    @JsonProperty("state")
    public NodeStateEnt getState();
    
    @Override
    @JsonProperty("link")
    public String getLink();
    
    @Override
    @JsonProperty("view")
    public NodeViewEnt getView();
    

    /**
     * MixIn class for entity builder implementations that adds jackson annotations for the de-/serialization.
     *
     * @author Martin Horn, University of Konstanz
     */

    // AUTO-GENERATED CODE; DO NOT MODIFY
    public static interface ComponentNodeEntMixInBuilder extends ComponentNodeEntBuilder {
    
        @Override
        public ComponentNodeEntMixIn build();
    
        @Override
        @JsonProperty("id")
        public ComponentNodeEntMixInBuilder setId(final org.knime.gateway.api.entity.NodeIDEnt id);
        
        @Override
        @JsonProperty("dialog")
        public ComponentNodeEntMixInBuilder setDialog(final Boolean dialog);
        
        @Override
        @JsonProperty("inPorts")
        public ComponentNodeEntMixInBuilder setInPorts(final java.util.List<? extends NodePortEnt> inPorts);
        
        @Override
        @JsonProperty("outPorts")
        public ComponentNodeEntMixInBuilder setOutPorts(final java.util.List<? extends NodePortEnt> outPorts);
        
        @Override
        @JsonProperty("annotation")
        public ComponentNodeEntMixInBuilder setAnnotation(final NodeAnnotationEnt annotation);
        
        @Override
        @JsonProperty("position")
        public ComponentNodeEntMixInBuilder setPosition(final XYEnt position);
        
        @Override
        @JsonProperty("kind")
        public ComponentNodeEntMixInBuilder setKind(final KindEnum kind);
        
        @Override
        @JsonProperty("allowedActions")
        public ComponentNodeEntMixInBuilder setAllowedActions(final AllowedActionsEnt allowedActions);
        
        @Override
        @JsonProperty("executionInfo")
        public ComponentNodeEntMixInBuilder setExecutionInfo(final NodeExecutionInfoEnt executionInfo);
        
        @Override
        @JsonProperty("name")
        public ComponentNodeEntMixInBuilder setName(final String name);
        
        @Override
        @JsonProperty("type")
        public ComponentNodeEntMixInBuilder setType(final TypeEnum type);
        
        @Override
        @JsonProperty("icon")
        public ComponentNodeEntMixInBuilder setIcon(final String icon);
        
        @Override
        @JsonProperty("state")
        public ComponentNodeEntMixInBuilder setState(final NodeStateEnt state);
        
        @Override
        @JsonProperty("link")
        public ComponentNodeEntMixInBuilder setLink(final String link);
        
        @Override
        @JsonProperty("view")
        public ComponentNodeEntMixInBuilder setView(final NodeViewEnt view);
        
    }


}

