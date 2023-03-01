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

import org.knime.gateway.api.webui.entity.NodeDialogOptionGroupEnt;
import org.knime.gateway.api.webui.entity.NodePortDescriptionEnt;
import org.knime.gateway.api.webui.entity.NodeViewDescriptionEnt;

import org.knime.gateway.api.webui.entity.NodeDescriptionEnt;

/**
 * Node description properties that are common to all kinds of nodes. This is static information in the sense that it does not depend on a concrete node instance in a workflow.
 *
 * @param description
 * @param options
 * @param views
 * @param inPorts
 * @param outPorts
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public record DefaultNodeDescriptionEnt(
    String description,
    java.util.List<NodeDialogOptionGroupEnt> options,
    java.util.List<NodeViewDescriptionEnt> views,
    java.util.List<NodePortDescriptionEnt> inPorts,
    java.util.List<NodePortDescriptionEnt> outPorts) implements NodeDescriptionEnt {

    /**
     * Validation for required parameters not being {@code null}.
     */
    public DefaultNodeDescriptionEnt {
    }

    @Override
    public String getTypeID() {
        return "NodeDescription";
    }
  
    @Override
    public String getDescription() {
        return description;
    }
    
    @Override
    public java.util.List<NodeDialogOptionGroupEnt> getOptions() {
        return options;
    }
    
    @Override
    public java.util.List<NodeViewDescriptionEnt> getViews() {
        return views;
    }
    
    @Override
    public java.util.List<NodePortDescriptionEnt> getInPorts() {
        return inPorts;
    }
    
    @Override
    public java.util.List<NodePortDescriptionEnt> getOutPorts() {
        return outPorts;
    }
    
    /**
     * A builder for {@link DefaultNodeDescriptionEnt}.
     */
    public static class DefaultNodeDescriptionEntBuilder implements NodeDescriptionEntBuilder {

        private String m_description;

        private java.util.List<NodeDialogOptionGroupEnt> m_options;

        private java.util.List<NodeViewDescriptionEnt> m_views;

        private java.util.List<NodePortDescriptionEnt> m_inPorts;

        private java.util.List<NodePortDescriptionEnt> m_outPorts;

        @Override
        public DefaultNodeDescriptionEntBuilder setDescription(String description) {
             m_description = description;
             return this;
        }

        @Override
        public DefaultNodeDescriptionEntBuilder setOptions(java.util.List<NodeDialogOptionGroupEnt> options) {
             m_options = options;
             return this;
        }

        @Override
        public DefaultNodeDescriptionEntBuilder setViews(java.util.List<NodeViewDescriptionEnt> views) {
             m_views = views;
             return this;
        }

        @Override
        public DefaultNodeDescriptionEntBuilder setInPorts(java.util.List<NodePortDescriptionEnt> inPorts) {
             m_inPorts = inPorts;
             return this;
        }

        @Override
        public DefaultNodeDescriptionEntBuilder setOutPorts(java.util.List<NodePortDescriptionEnt> outPorts) {
             m_outPorts = outPorts;
             return this;
        }

        @Override
        public DefaultNodeDescriptionEnt build() {
            return new DefaultNodeDescriptionEnt(
                immutable(m_description),
                immutable(m_options),
                immutable(m_views),
                immutable(m_inPorts),
                immutable(m_outPorts));
        }
    
    }

}
