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
package org.knime.gateway.json.entity;

import org.knime.gateway.v0.entity.ConnectionEnt;
import org.knime.gateway.v0.entity.MetaPortInfoEnt;
import org.knime.gateway.v0.entity.NodeEnt;
import org.knime.gateway.v0.entity.WorkflowAnnotationEnt;
import org.knime.gateway.v0.entity.WorkflowUIInfoEnt;


import org.knime.gateway.json.JsonUtil;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


import org.knime.gateway.v0.entity.WorkflowEnt;
import org.knime.gateway.v0.entity.impl.DefaultWorkflowEnt;
import org.knime.gateway.v0.entity.impl.DefaultWorkflowEnt.DefaultWorkflowEntBuilder;

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
    defaultImpl = DefaultWorkflowEnt.class)
@JsonSubTypes({
    @Type(value = DefaultWorkflowEnt.class, name="Workflow")
})
@JsonDeserialize(builder=DefaultWorkflowEntBuilder.class)
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public interface WorkflowEntMixIn extends WorkflowEnt {

    @Override
    @JsonProperty("nodes")
    public java.util.Map<String, NodeEnt> getNodes();
    
    @Override
    @JsonProperty("connections")
    public java.util.List<ConnectionEnt> getConnections();
    
    @Override
    @JsonProperty("metaInPortInfos")
    public java.util.List<MetaPortInfoEnt> getMetaInPortInfos();
    
    @Override
    @JsonProperty("metaOutPortInfos")
    public java.util.List<MetaPortInfoEnt> getMetaOutPortInfos();
    
    @Override
    @JsonProperty("workflowAnnotations")
    public java.util.List<WorkflowAnnotationEnt> getWorkflowAnnotations();
    
    @Override
    @JsonProperty("workflowUIInfo")
    public WorkflowUIInfoEnt getWorkflowUIInfo();
    

    /**
     * MixIn class for entity builder implementations that adds jackson annotations for the de-/serialization.
     *
     * @author Martin Horn, University of Konstanz
     */
    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "",
        defaultImpl = DefaultWorkflowEntBuilder.class)
    @JsonSubTypes({
        @Type(value = DefaultWorkflowEnt.DefaultWorkflowEntBuilder.class, name="Workflow")
    })
    // AUTO-GENERATED CODE; DO NOT MODIFY
    public static interface WorkflowEntMixInBuilder extends WorkflowEntBuilder {
    
        @Override
        public WorkflowEntMixIn build();
    
        @Override
        @JsonProperty("nodes")
        public WorkflowEntMixInBuilder setNodes(final java.util.Map<String, NodeEnt> nodes);
        
        @Override
        @JsonProperty("connections")
        public WorkflowEntMixInBuilder setConnections(final java.util.List<ConnectionEnt> connections);
        
        @Override
        @JsonProperty("metaInPortInfos")
        public WorkflowEntMixInBuilder setMetaInPortInfos(final java.util.List<MetaPortInfoEnt> metaInPortInfos);
        
        @Override
        @JsonProperty("metaOutPortInfos")
        public WorkflowEntMixInBuilder setMetaOutPortInfos(final java.util.List<MetaPortInfoEnt> metaOutPortInfos);
        
        @Override
        @JsonProperty("workflowAnnotations")
        public WorkflowEntMixInBuilder setWorkflowAnnotations(final java.util.List<WorkflowAnnotationEnt> workflowAnnotations);
        
        @Override
        @JsonProperty("workflowUIInfo")
        public WorkflowEntMixInBuilder setWorkflowUIInfo(final WorkflowUIInfoEnt workflowUIInfo);
        
    }


}
