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

import org.knime.gateway.api.webui.entity.DynamicPortGroupDescriptionEnt;
import org.knime.gateway.api.webui.entity.NodeDialogOptionDescriptionEnt;
import org.knime.gateway.api.webui.entity.NodeDialogOptionGroupEnt;
import org.knime.gateway.api.webui.entity.NodePortDescriptionEnt;
import org.knime.gateway.api.webui.entity.NodeViewDescriptionEnt;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.knime.gateway.api.webui.entity.DescribableNodeDescriptionEnt;
import org.knime.gateway.impl.webui.entity.DefaultDescribableNodeDescriptionEnt.DefaultDescribableNodeDescriptionEntBuilder;

/**
 * MixIn class for entity implementations that adds jackson annotations for de-/serialization.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */

@JsonDeserialize(builder=DefaultDescribableNodeDescriptionEntBuilder.class)
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", ""})
public interface DescribableNodeDescriptionEntMixIn extends DescribableNodeDescriptionEnt {

    @Override
    @JsonIgnore
    public String getTypeID();

    @Override
    @JsonProperty("description")
    public String getDescription();
    
    @Override
    @JsonProperty("ungroupedOptions")
    public java.util.List<NodeDialogOptionDescriptionEnt> getUngroupedOptions();
    
    @Override
    @JsonProperty("optionGroups")
    public java.util.List<NodeDialogOptionGroupEnt> getOptionGroups();
    
    @Override
    @JsonProperty("views")
    public java.util.List<NodeViewDescriptionEnt> getViews();
    
    @Override
    @JsonProperty("interactiveView")
    public NodeViewDescriptionEnt getInteractiveView();
    
    @Override
    @JsonProperty("inPorts")
    public java.util.List<NodePortDescriptionEnt> getInPorts();
    
    @Override
    @JsonProperty("dynamicInPortGroupDescriptions")
    public java.util.List<DynamicPortGroupDescriptionEnt> getDynamicInPortGroupDescriptions();
    
    @Override
    @JsonProperty("outPorts")
    public java.util.List<NodePortDescriptionEnt> getOutPorts();
    
    @Override
    @JsonProperty("dynamicOutPortGroupDescriptions")
    public java.util.List<DynamicPortGroupDescriptionEnt> getDynamicOutPortGroupDescriptions();
    

    /**
     * MixIn class for entity builder implementations that adds jackson annotations for the de-/serialization.
     *
     * @author Martin Horn, University of Konstanz
     */

    // AUTO-GENERATED CODE; DO NOT MODIFY
    public static interface DescribableNodeDescriptionEntMixInBuilder extends DescribableNodeDescriptionEntBuilder {
    
        @Override
        public DescribableNodeDescriptionEntMixIn build();
    
        @Override
        @JsonProperty("description")
        public DescribableNodeDescriptionEntMixInBuilder setDescription(final String description);
        
        @Override
        @JsonProperty("ungroupedOptions")
        public DescribableNodeDescriptionEntMixInBuilder setUngroupedOptions(final java.util.List<NodeDialogOptionDescriptionEnt> ungroupedOptions);
        
        @Override
        @JsonProperty("optionGroups")
        public DescribableNodeDescriptionEntMixInBuilder setOptionGroups(final java.util.List<NodeDialogOptionGroupEnt> optionGroups);
        
        @Override
        @JsonProperty("views")
        public DescribableNodeDescriptionEntMixInBuilder setViews(final java.util.List<NodeViewDescriptionEnt> views);
        
        @Override
        @JsonProperty("interactiveView")
        public DescribableNodeDescriptionEntMixInBuilder setInteractiveView(final NodeViewDescriptionEnt interactiveView);
        
        @Override
        @JsonProperty("inPorts")
        public DescribableNodeDescriptionEntMixInBuilder setInPorts(final java.util.List<NodePortDescriptionEnt> inPorts);
        
        @Override
        @JsonProperty("dynamicInPortGroupDescriptions")
        public DescribableNodeDescriptionEntMixInBuilder setDynamicInPortGroupDescriptions(final java.util.List<DynamicPortGroupDescriptionEnt> dynamicInPortGroupDescriptions);
        
        @Override
        @JsonProperty("outPorts")
        public DescribableNodeDescriptionEntMixInBuilder setOutPorts(final java.util.List<NodePortDescriptionEnt> outPorts);
        
        @Override
        @JsonProperty("dynamicOutPortGroupDescriptions")
        public DescribableNodeDescriptionEntMixInBuilder setDynamicOutPortGroupDescriptions(final java.util.List<DynamicPortGroupDescriptionEnt> dynamicOutPortGroupDescriptions);
        
    }


}
