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
import org.knime.gateway.api.webui.entity.LinkEnt;
import org.knime.gateway.api.webui.entity.NodeDialogOptionGroupEnt;
import org.knime.gateway.api.webui.entity.NodePortDescriptionEnt;
import org.knime.gateway.api.webui.entity.NodeViewDescriptionEnt;
import org.knime.gateway.impl.webui.entity.DefaultNodeDescriptionEnt;

import org.knime.gateway.api.webui.entity.NativeNodeDescriptionEnt;

/**
 * Description of certain aspects of a native node.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public class DefaultNativeNodeDescriptionEnt implements NativeNodeDescriptionEnt {

  protected String m_description;
  protected java.util.List<NodeDialogOptionGroupEnt> m_options;
  protected java.util.List<NodeViewDescriptionEnt> m_views;
  protected java.util.List<NodePortDescriptionEnt> m_inPorts;
  protected java.util.List<NodePortDescriptionEnt> m_outPorts;
  protected String m_shortDescription;
  protected java.util.List<DynamicPortGroupDescriptionEnt> m_dynamicInPortGroupDescriptions;
  protected java.util.List<DynamicPortGroupDescriptionEnt> m_dynamicOutPortGroupDescriptions;
  protected NodeViewDescriptionEnt m_interactiveView;
  protected java.util.List<LinkEnt> m_links;
  
  protected DefaultNativeNodeDescriptionEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "NativeNodeDescription";
  }
  
  private DefaultNativeNodeDescriptionEnt(DefaultNativeNodeDescriptionEntBuilder builder) {
    super();
    m_description = immutable(builder.m_description);
    m_options = immutable(builder.m_options);
    m_views = immutable(builder.m_views);
    m_inPorts = immutable(builder.m_inPorts);
    m_outPorts = immutable(builder.m_outPorts);
    m_shortDescription = immutable(builder.m_shortDescription);
    m_dynamicInPortGroupDescriptions = immutable(builder.m_dynamicInPortGroupDescriptions);
    m_dynamicOutPortGroupDescriptions = immutable(builder.m_dynamicOutPortGroupDescriptions);
    m_interactiveView = immutable(builder.m_interactiveView);
    m_links = immutable(builder.m_links);
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
        DefaultNativeNodeDescriptionEnt ent = (DefaultNativeNodeDescriptionEnt)o;
        return Objects.equals(m_description, ent.m_description) && Objects.equals(m_options, ent.m_options) && Objects.equals(m_views, ent.m_views) && Objects.equals(m_inPorts, ent.m_inPorts) && Objects.equals(m_outPorts, ent.m_outPorts) && Objects.equals(m_shortDescription, ent.m_shortDescription) && Objects.equals(m_dynamicInPortGroupDescriptions, ent.m_dynamicInPortGroupDescriptions) && Objects.equals(m_dynamicOutPortGroupDescriptions, ent.m_dynamicOutPortGroupDescriptions) && Objects.equals(m_interactiveView, ent.m_interactiveView) && Objects.equals(m_links, ent.m_links);
    }


  
   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
       return new HashCodeBuilder()
               .append(m_description)
               .append(m_options)
               .append(m_views)
               .append(m_inPorts)
               .append(m_outPorts)
               .append(m_shortDescription)
               .append(m_dynamicInPortGroupDescriptions)
               .append(m_dynamicOutPortGroupDescriptions)
               .append(m_interactiveView)
               .append(m_links)
               .toHashCode();
   }
  
	
	
  @Override
  public String getDescription() {
        return m_description;
  }
    
  @Override
  public java.util.List<NodeDialogOptionGroupEnt> getOptions() {
        return m_options;
  }
    
  @Override
  public java.util.List<NodeViewDescriptionEnt> getViews() {
        return m_views;
  }
    
  @Override
  public java.util.List<NodePortDescriptionEnt> getInPorts() {
        return m_inPorts;
  }
    
  @Override
  public java.util.List<NodePortDescriptionEnt> getOutPorts() {
        return m_outPorts;
  }
    
  @Override
  public String getShortDescription() {
        return m_shortDescription;
  }
    
  @Override
  public java.util.List<DynamicPortGroupDescriptionEnt> getDynamicInPortGroupDescriptions() {
        return m_dynamicInPortGroupDescriptions;
  }
    
  @Override
  public java.util.List<DynamicPortGroupDescriptionEnt> getDynamicOutPortGroupDescriptions() {
        return m_dynamicOutPortGroupDescriptions;
  }
    
  @Override
  public NodeViewDescriptionEnt getInteractiveView() {
        return m_interactiveView;
  }
    
  @Override
  public java.util.List<LinkEnt> getLinks() {
        return m_links;
  }
    
  
    public static class DefaultNativeNodeDescriptionEntBuilder implements NativeNodeDescriptionEntBuilder {
    
        public DefaultNativeNodeDescriptionEntBuilder(){
            super();
        }
    
        private String m_description;
        private java.util.List<NodeDialogOptionGroupEnt> m_options;
        private java.util.List<NodeViewDescriptionEnt> m_views;
        private java.util.List<NodePortDescriptionEnt> m_inPorts;
        private java.util.List<NodePortDescriptionEnt> m_outPorts;
        private String m_shortDescription;
        private java.util.List<DynamicPortGroupDescriptionEnt> m_dynamicInPortGroupDescriptions;
        private java.util.List<DynamicPortGroupDescriptionEnt> m_dynamicOutPortGroupDescriptions;
        private NodeViewDescriptionEnt m_interactiveView;
        private java.util.List<LinkEnt> m_links;

        @Override
        public DefaultNativeNodeDescriptionEntBuilder setDescription(String description) {
             m_description = description;
             return this;
        }

        @Override
        public DefaultNativeNodeDescriptionEntBuilder setOptions(java.util.List<NodeDialogOptionGroupEnt> options) {
             m_options = options;
             return this;
        }

        @Override
        public DefaultNativeNodeDescriptionEntBuilder setViews(java.util.List<NodeViewDescriptionEnt> views) {
             m_views = views;
             return this;
        }

        @Override
        public DefaultNativeNodeDescriptionEntBuilder setInPorts(java.util.List<NodePortDescriptionEnt> inPorts) {
             m_inPorts = inPorts;
             return this;
        }

        @Override
        public DefaultNativeNodeDescriptionEntBuilder setOutPorts(java.util.List<NodePortDescriptionEnt> outPorts) {
             m_outPorts = outPorts;
             return this;
        }

        @Override
        public DefaultNativeNodeDescriptionEntBuilder setShortDescription(String shortDescription) {
             m_shortDescription = shortDescription;
             return this;
        }

        @Override
        public DefaultNativeNodeDescriptionEntBuilder setDynamicInPortGroupDescriptions(java.util.List<DynamicPortGroupDescriptionEnt> dynamicInPortGroupDescriptions) {
             m_dynamicInPortGroupDescriptions = dynamicInPortGroupDescriptions;
             return this;
        }

        @Override
        public DefaultNativeNodeDescriptionEntBuilder setDynamicOutPortGroupDescriptions(java.util.List<DynamicPortGroupDescriptionEnt> dynamicOutPortGroupDescriptions) {
             m_dynamicOutPortGroupDescriptions = dynamicOutPortGroupDescriptions;
             return this;
        }

        @Override
        public DefaultNativeNodeDescriptionEntBuilder setInteractiveView(NodeViewDescriptionEnt interactiveView) {
             m_interactiveView = interactiveView;
             return this;
        }

        @Override
        public DefaultNativeNodeDescriptionEntBuilder setLinks(java.util.List<LinkEnt> links) {
             m_links = links;
             return this;
        }

        
        @Override
        public DefaultNativeNodeDescriptionEnt build() {
            return new DefaultNativeNodeDescriptionEnt(this);
        }
    
    }

}
