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

import org.knime.gateway.api.webui.entity.ComponentEditorConfigEnt;

import org.knime.gateway.api.webui.entity.ComponentEditorStateEnt;

/**
 * The state of the component editor.
 *
 * @param config
 * @param viewNodes
 * @param configurationNodes
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public record DefaultComponentEditorStateEnt(
    ComponentEditorConfigEnt config,
    java.util.List<String> viewNodes,
    java.util.List<String> configurationNodes) implements ComponentEditorStateEnt {

    /**
     * Validation for required parameters not being {@code null}.
     */
    public DefaultComponentEditorStateEnt {
        if(config == null) {
            throw new IllegalArgumentException("<config> must not be null.");
        }
        if(viewNodes == null) {
            throw new IllegalArgumentException("<viewNodes> must not be null.");
        }
        if(configurationNodes == null) {
            throw new IllegalArgumentException("<configurationNodes> must not be null.");
        }
    }

    @Override
    public String getTypeID() {
        return "ComponentEditorState";
    }
  
    @Override
    public ComponentEditorConfigEnt getConfig() {
        return config;
    }
    
    @Override
    public java.util.List<String> getViewNodes() {
        return viewNodes;
    }
    
    @Override
    public java.util.List<String> getConfigurationNodes() {
        return configurationNodes;
    }
    
    /**
     * A builder for {@link DefaultComponentEditorStateEnt}.
     */
    public static class DefaultComponentEditorStateEntBuilder implements ComponentEditorStateEntBuilder {

        private ComponentEditorConfigEnt m_config;

        private java.util.List<String> m_viewNodes = new java.util.ArrayList<>();

        private java.util.List<String> m_configurationNodes = new java.util.ArrayList<>();

        @Override
        public DefaultComponentEditorStateEntBuilder setConfig(ComponentEditorConfigEnt config) {
             if(config == null) {
                 throw new IllegalArgumentException("<config> must not be null.");
             }
             m_config = config;
             return this;
        }

        @Override
        public DefaultComponentEditorStateEntBuilder setViewNodes(java.util.List<String> viewNodes) {
             if(viewNodes == null) {
                 throw new IllegalArgumentException("<viewNodes> must not be null.");
             }
             m_viewNodes = viewNodes;
             return this;
        }

        @Override
        public DefaultComponentEditorStateEntBuilder setConfigurationNodes(java.util.List<String> configurationNodes) {
             if(configurationNodes == null) {
                 throw new IllegalArgumentException("<configurationNodes> must not be null.");
             }
             m_configurationNodes = configurationNodes;
             return this;
        }

        @Override
        public DefaultComponentEditorStateEnt build() {
            return new DefaultComponentEditorStateEnt(
                immutable(m_config),
                immutable(m_viewNodes),
                immutable(m_configurationNodes));
        }
    
    }

}
