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

import org.knime.gateway.api.webui.entity.NodeFactoryKeyEnt;
import org.knime.gateway.api.webui.entity.SpaceItemReferenceEnt;
import org.knime.gateway.api.webui.entity.XYEnt;
import org.knime.gateway.json.webui.entity.WorkflowCommandEntMixIn;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.knime.gateway.api.webui.entity.AddNodeCommandEnt;
import org.knime.gateway.impl.webui.entity.DefaultAddNodeCommandEnt.DefaultAddNodeCommandEntBuilder;

/**
 * MixIn class for entity implementations that adds jackson annotations for de-/serialization.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */

@JsonDeserialize(builder=DefaultAddNodeCommandEntBuilder.class)
@JsonSerialize(as=AddNodeCommandEnt.class)
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.json-config.json"})
public interface AddNodeCommandEntMixIn extends AddNodeCommandEnt {

    @Override
    @JsonIgnore
    public String getTypeID();

    @Override
    @JsonProperty("kind")
    public KindEnum getKind();
    
    @Override
    @JsonProperty("position")
    public XYEnt getPosition();
    
    @Override
    @JsonProperty("nodeFactory")
    public NodeFactoryKeyEnt getNodeFactory();
    
    @Override
    @JsonProperty("url")
    public String getUrl();
    
    @Override
    @JsonProperty("spaceItemReference")
    public SpaceItemReferenceEnt getSpaceItemReference();
    
    @Override
    @JsonProperty("sourceNodeId")
    public org.knime.gateway.api.entity.NodeIDEnt getSourceNodeId();
    
    @Override
    @JsonProperty("sourcePortIdx")
    public Integer getSourcePortIdx();
    
    @Override
    @JsonProperty("nodeRelation")
    public NodeRelationEnum getNodeRelation();
    

    /**
     * MixIn class for entity builder implementations that adds jackson annotations for the de-/serialization.
     *
     * @author Martin Horn, University of Konstanz
     */

    // AUTO-GENERATED CODE; DO NOT MODIFY
    public static interface AddNodeCommandEntMixInBuilder extends AddNodeCommandEntBuilder {
    
        @Override
        public AddNodeCommandEntMixIn build();
    
        @Override
        @JsonProperty("kind")
        public AddNodeCommandEntMixInBuilder setKind(final KindEnum kind);
        
        @Override
        @JsonProperty("position")
        public AddNodeCommandEntMixInBuilder setPosition(final XYEnt position);
        
        @Override
        @JsonProperty("nodeFactory")
        public AddNodeCommandEntMixInBuilder setNodeFactory(final NodeFactoryKeyEnt nodeFactory);
        
        @Override
        @JsonProperty("url")
        public AddNodeCommandEntMixInBuilder setUrl(final String url);
        
        @Override
        @JsonProperty("spaceItemReference")
        public AddNodeCommandEntMixInBuilder setSpaceItemReference(final SpaceItemReferenceEnt spaceItemReference);
        
        @Override
        @JsonProperty("sourceNodeId")
        public AddNodeCommandEntMixInBuilder setSourceNodeId(final org.knime.gateway.api.entity.NodeIDEnt sourceNodeId);
        
        @Override
        @JsonProperty("sourcePortIdx")
        public AddNodeCommandEntMixInBuilder setSourcePortIdx(final Integer sourcePortIdx);
        
        @Override
        @JsonProperty("nodeRelation")
        public AddNodeCommandEntMixInBuilder setNodeRelation(final NodeRelationEnum nodeRelation);
        
    }


}

