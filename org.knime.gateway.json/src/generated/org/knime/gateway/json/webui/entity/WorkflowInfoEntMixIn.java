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

import org.knime.gateway.api.webui.entity.JobManagerEnt;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.knime.gateway.api.webui.entity.WorkflowInfoEnt;
import org.knime.gateway.impl.webui.entity.DefaultWorkflowInfoEnt.DefaultWorkflowInfoEntBuilder;

/**
 * MixIn class for entity implementations that adds jackson annotations for de-/serialization.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */

@JsonDeserialize(builder=DefaultWorkflowInfoEntBuilder.class)
@JsonSerialize(as=WorkflowInfoEnt.class)
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.json-config.json"})
public interface WorkflowInfoEntMixIn extends WorkflowInfoEnt {

    @Override
    @JsonIgnore
    public String getTypeID();

    @Override
    @JsonProperty("name")
    public String getName();
    
    @Override
    @JsonProperty("containerId")
    public org.knime.gateway.api.entity.NodeIDEnt getContainerId();
    
    @Override
    @JsonProperty("containerType")
    public ContainerTypeEnum getContainerType();
    
    @Override
    @JsonProperty("linked")
    public Boolean isLinked();
    
    @Override
    @JsonProperty("containsLinkedComponents")
    public Boolean isContainsLinkedComponents();
    
    @Override
    @JsonProperty("providerType")
    public ProviderTypeEnum getProviderType();
    
    @Override
    @JsonProperty("version")
    public String getVersion();
    
    @Override
    @JsonProperty("jobManager")
    public JobManagerEnt getJobManager();
    

    /**
     * MixIn class for entity builder implementations that adds jackson annotations for the de-/serialization.
     *
     * @author Martin Horn, University of Konstanz
     */

    // AUTO-GENERATED CODE; DO NOT MODIFY
    public static interface WorkflowInfoEntMixInBuilder extends WorkflowInfoEntBuilder {
    
        @Override
        public WorkflowInfoEntMixIn build();
    
        @Override
        @JsonProperty("name")
        public WorkflowInfoEntMixInBuilder setName(final String name);
        
        @Override
        @JsonProperty("containerId")
        public WorkflowInfoEntMixInBuilder setContainerId(final org.knime.gateway.api.entity.NodeIDEnt containerId);
        
        @Override
        @JsonProperty("containerType")
        public WorkflowInfoEntMixInBuilder setContainerType(final ContainerTypeEnum containerType);
        
        @Override
        @JsonProperty("linked")
        public WorkflowInfoEntMixInBuilder setLinked(final Boolean linked);
        
        @Override
        @JsonProperty("containsLinkedComponents")
        public WorkflowInfoEntMixInBuilder setContainsLinkedComponents(final Boolean containsLinkedComponents);
        
        @Override
        @JsonProperty("providerType")
        public WorkflowInfoEntMixInBuilder setProviderType(final ProviderTypeEnum providerType);
        
        @Override
        @JsonProperty("version")
        public WorkflowInfoEntMixInBuilder setVersion(final String version);
        
        @Override
        @JsonProperty("jobManager")
        public WorkflowInfoEntMixInBuilder setJobManager(final JobManagerEnt jobManager);
        
    }


}

