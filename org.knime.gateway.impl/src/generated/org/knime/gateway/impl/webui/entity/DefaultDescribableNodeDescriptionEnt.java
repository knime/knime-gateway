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
package org.knime.gateway.impl.webui.entity;

import static org.knime.gateway.api.util.EntityUtil.immutable;

import java.util.Objects;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import org.knime.gateway.api.webui.entity.DynamicPortGroupDescriptionEnt;
import org.knime.gateway.api.webui.entity.NodeDialogOptionDescriptionEnt;
import org.knime.gateway.api.webui.entity.NodeDialogOptionGroupEnt;
import org.knime.gateway.api.webui.entity.NodePortDescriptionEnt;
import org.knime.gateway.api.webui.entity.NodeViewDescriptionEnt;

import org.knime.gateway.api.webui.entity.DescribableNodeDescriptionEnt;

/**
 * Descriptions of aspects of some node.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", ""})
public class DefaultDescribableNodeDescriptionEnt implements DescribableNodeDescriptionEnt {

  protected String m_description;
  protected java.util.List<NodeDialogOptionDescriptionEnt> m_ungroupedOptions;
  protected java.util.List<NodeDialogOptionGroupEnt> m_optionGroups;
  protected java.util.List<NodeViewDescriptionEnt> m_views;
  protected NodeViewDescriptionEnt m_interactiveView;
  protected java.util.List<NodePortDescriptionEnt> m_inPorts;
  protected java.util.List<DynamicPortGroupDescriptionEnt> m_dynamicInPortGroupDescriptions;
  protected java.util.List<NodePortDescriptionEnt> m_outPorts;
  protected java.util.List<DynamicPortGroupDescriptionEnt> m_dynamicOutPortGroupDescriptions;
  
  protected DefaultDescribableNodeDescriptionEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "DescribableNodeDescription";
  }
  
  private DefaultDescribableNodeDescriptionEnt(DefaultDescribableNodeDescriptionEntBuilder builder) {
    
    m_description = immutable(builder.m_description);
    m_ungroupedOptions = immutable(builder.m_ungroupedOptions);
    m_optionGroups = immutable(builder.m_optionGroups);
    m_views = immutable(builder.m_views);
    m_interactiveView = immutable(builder.m_interactiveView);
    m_inPorts = immutable(builder.m_inPorts);
    m_dynamicInPortGroupDescriptions = immutable(builder.m_dynamicInPortGroupDescriptions);
    m_outPorts = immutable(builder.m_outPorts);
    m_dynamicOutPortGroupDescriptions = immutable(builder.m_dynamicOutPortGroupDescriptions);
  }
  
   /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (getClass() != o.getClass()) {
            return false;
        }
        DefaultDescribableNodeDescriptionEnt ent = (DefaultDescribableNodeDescriptionEnt)o;
        return Objects.equals(m_description, ent.m_description) && Objects.equals(m_ungroupedOptions, ent.m_ungroupedOptions) && Objects.equals(m_optionGroups, ent.m_optionGroups) && Objects.equals(m_views, ent.m_views) && Objects.equals(m_interactiveView, ent.m_interactiveView) && Objects.equals(m_inPorts, ent.m_inPorts) && Objects.equals(m_dynamicInPortGroupDescriptions, ent.m_dynamicInPortGroupDescriptions) && Objects.equals(m_outPorts, ent.m_outPorts) && Objects.equals(m_dynamicOutPortGroupDescriptions, ent.m_dynamicOutPortGroupDescriptions);
    }


  
   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
       return new HashCodeBuilder()
               .append(m_description)
               .append(m_ungroupedOptions)
               .append(m_optionGroups)
               .append(m_views)
               .append(m_interactiveView)
               .append(m_inPorts)
               .append(m_dynamicInPortGroupDescriptions)
               .append(m_outPorts)
               .append(m_dynamicOutPortGroupDescriptions)
               .toHashCode();
   }
  
	
	
  @Override
  public String getDescription() {
        return m_description;
  }
    
  @Override
  public java.util.List<NodeDialogOptionDescriptionEnt> getUngroupedOptions() {
        return m_ungroupedOptions;
  }
    
  @Override
  public java.util.List<NodeDialogOptionGroupEnt> getOptionGroups() {
        return m_optionGroups;
  }
    
  @Override
  public java.util.List<NodeViewDescriptionEnt> getViews() {
        return m_views;
  }
    
  @Override
  public NodeViewDescriptionEnt getInteractiveView() {
        return m_interactiveView;
  }
    
  @Override
  public java.util.List<NodePortDescriptionEnt> getInPorts() {
        return m_inPorts;
  }
    
  @Override
  public java.util.List<DynamicPortGroupDescriptionEnt> getDynamicInPortGroupDescriptions() {
        return m_dynamicInPortGroupDescriptions;
  }
    
  @Override
  public java.util.List<NodePortDescriptionEnt> getOutPorts() {
        return m_outPorts;
  }
    
  @Override
  public java.util.List<DynamicPortGroupDescriptionEnt> getDynamicOutPortGroupDescriptions() {
        return m_dynamicOutPortGroupDescriptions;
  }
    
  
    public static class DefaultDescribableNodeDescriptionEntBuilder implements DescribableNodeDescriptionEntBuilder {
    
        public DefaultDescribableNodeDescriptionEntBuilder(){
            
        }
    
        private String m_description;
        private java.util.List<NodeDialogOptionDescriptionEnt> m_ungroupedOptions;
        private java.util.List<NodeDialogOptionGroupEnt> m_optionGroups;
        private java.util.List<NodeViewDescriptionEnt> m_views;
        private NodeViewDescriptionEnt m_interactiveView;
        private java.util.List<NodePortDescriptionEnt> m_inPorts;
        private java.util.List<DynamicPortGroupDescriptionEnt> m_dynamicInPortGroupDescriptions;
        private java.util.List<NodePortDescriptionEnt> m_outPorts;
        private java.util.List<DynamicPortGroupDescriptionEnt> m_dynamicOutPortGroupDescriptions;

        @Override
        public DefaultDescribableNodeDescriptionEntBuilder setDescription(String description) {
             m_description = description;
             return this;
        }

        @Override
        public DefaultDescribableNodeDescriptionEntBuilder setUngroupedOptions(java.util.List<NodeDialogOptionDescriptionEnt> ungroupedOptions) {
             m_ungroupedOptions = ungroupedOptions;
             return this;
        }

        @Override
        public DefaultDescribableNodeDescriptionEntBuilder setOptionGroups(java.util.List<NodeDialogOptionGroupEnt> optionGroups) {
             m_optionGroups = optionGroups;
             return this;
        }

        @Override
        public DefaultDescribableNodeDescriptionEntBuilder setViews(java.util.List<NodeViewDescriptionEnt> views) {
             m_views = views;
             return this;
        }

        @Override
        public DefaultDescribableNodeDescriptionEntBuilder setInteractiveView(NodeViewDescriptionEnt interactiveView) {
             m_interactiveView = interactiveView;
             return this;
        }

        @Override
        public DefaultDescribableNodeDescriptionEntBuilder setInPorts(java.util.List<NodePortDescriptionEnt> inPorts) {
             m_inPorts = inPorts;
             return this;
        }

        @Override
        public DefaultDescribableNodeDescriptionEntBuilder setDynamicInPortGroupDescriptions(java.util.List<DynamicPortGroupDescriptionEnt> dynamicInPortGroupDescriptions) {
             m_dynamicInPortGroupDescriptions = dynamicInPortGroupDescriptions;
             return this;
        }

        @Override
        public DefaultDescribableNodeDescriptionEntBuilder setOutPorts(java.util.List<NodePortDescriptionEnt> outPorts) {
             m_outPorts = outPorts;
             return this;
        }

        @Override
        public DefaultDescribableNodeDescriptionEntBuilder setDynamicOutPortGroupDescriptions(java.util.List<DynamicPortGroupDescriptionEnt> dynamicOutPortGroupDescriptions) {
             m_dynamicOutPortGroupDescriptions = dynamicOutPortGroupDescriptions;
             return this;
        }

        
        @Override
        public DefaultDescribableNodeDescriptionEnt build() {
            return new DefaultDescribableNodeDescriptionEnt(this);
        }
    
    }

}
