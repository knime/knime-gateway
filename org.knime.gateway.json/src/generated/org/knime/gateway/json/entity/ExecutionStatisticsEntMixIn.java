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

import java.math.BigDecimal;
import org.knime.gateway.api.entity.NodeExecutedStatisticsEnt;
import org.knime.gateway.api.entity.NodeExecutingStatisticsEnt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


import org.knime.gateway.api.entity.ExecutionStatisticsEnt;
import org.knime.gateway.impl.entity.DefaultExecutionStatisticsEnt;
import org.knime.gateway.impl.entity.DefaultExecutionStatisticsEnt.DefaultExecutionStatisticsEntBuilder;

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
    defaultImpl = DefaultExecutionStatisticsEnt.class)
@JsonSubTypes({
    @Type(value = DefaultExecutionStatisticsEnt.class, name="ExecutionStatistics")
})
@JsonDeserialize(builder=DefaultExecutionStatisticsEntBuilder.class)
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/org.knime.gateway.json-config.json"})
public interface ExecutionStatisticsEntMixIn extends ExecutionStatisticsEnt {

    @Override
    @JsonIgnore
    public String getTypeID();

    @Override
    @JsonProperty("totalExecutionDuration")
    public BigDecimal getTotalExecutionDuration();
    
    @Override
    @JsonProperty("totalNodeExecutionsCount")
    public Integer getTotalNodeExecutionsCount();
    
    @Override
    @JsonProperty("nodesExecuted")
    public java.util.List<NodeExecutedStatisticsEnt> getNodesExecuted();
    
    @Override
    @JsonProperty("nodesExecuting")
    public java.util.List<NodeExecutingStatisticsEnt> getNodesExecuting();
    
    @Override
    @JsonProperty("wizardExecutionState")
    public WizardExecutionStateEnum getWizardExecutionState();
    

    /**
     * MixIn class for entity builder implementations that adds jackson annotations for the de-/serialization.
     *
     * @author Martin Horn, University of Konstanz
     */
    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "",
        defaultImpl = DefaultExecutionStatisticsEntBuilder.class)
    @JsonSubTypes({
        @Type(value = DefaultExecutionStatisticsEnt.DefaultExecutionStatisticsEntBuilder.class, name="ExecutionStatistics")
    })
    // AUTO-GENERATED CODE; DO NOT MODIFY
    public static interface ExecutionStatisticsEntMixInBuilder extends ExecutionStatisticsEntBuilder {
    
        @Override
        public ExecutionStatisticsEntMixIn build();
    
        @Override
        @JsonProperty("totalExecutionDuration")
        public ExecutionStatisticsEntMixInBuilder setTotalExecutionDuration(final BigDecimal totalExecutionDuration);
        
        @Override
        @JsonProperty("totalNodeExecutionsCount")
        public ExecutionStatisticsEntMixInBuilder setTotalNodeExecutionsCount(final Integer totalNodeExecutionsCount);
        
        @Override
        @JsonProperty("nodesExecuted")
        public ExecutionStatisticsEntMixInBuilder setNodesExecuted(final java.util.List<NodeExecutedStatisticsEnt> nodesExecuted);
        
        @Override
        @JsonProperty("nodesExecuting")
        public ExecutionStatisticsEntMixInBuilder setNodesExecuting(final java.util.List<NodeExecutingStatisticsEnt> nodesExecuting);
        
        @Override
        @JsonProperty("wizardExecutionState")
        public ExecutionStatisticsEntMixInBuilder setWizardExecutionState(final WizardExecutionStateEnum wizardExecutionState);
        
    }


}

