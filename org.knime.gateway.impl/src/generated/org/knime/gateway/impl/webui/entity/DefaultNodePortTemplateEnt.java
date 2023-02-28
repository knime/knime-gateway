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


import org.knime.gateway.api.webui.entity.NodePortTemplateEnt;

/**
 * Properties that have NodePort and NodePortDescription in common. Or, put differently, the properties required to render a node port in the node repository (i.e. without port information specific to a node-instance).
 *
 * @param name
 * @param typeId
 * @param optional
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @author Kai Franze, KNIME GmbH
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public record DefaultNodePortTemplateEnt(
    String name,
    String typeId,
    Boolean optional) implements NodePortTemplateEnt {

    /**
     * Canonical constructor for {@link DefaultNodePortTemplateEnt} including null checks for non-nullable parameters.
     *
     * @param name
     * @param typeId
     * @param optional
     */
    public DefaultNodePortTemplateEnt {
        if(typeId == null) {
            throw new IllegalArgumentException("<typeId> must not be null.");
        }
    }

    @Override
    public String getTypeID() {
        return "NodePortTemplate";
    }
  
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public String getTypeId() {
        return typeId;
    }
    
    @Override
    public Boolean isOptional() {
        return optional;
    }
    
    /**
     * A builder for {@link DefaultNodePortTemplateEnt}.
     */
    public static class DefaultNodePortTemplateEntBuilder implements NodePortTemplateEntBuilder {

        private String m_name;

        private String m_typeId;

        private Boolean m_optional;

        @Override
        public DefaultNodePortTemplateEntBuilder setName(String name) {
             m_name = name;
             return this;
        }

        @Override
        public DefaultNodePortTemplateEntBuilder setTypeId(String typeId) {
             if(typeId == null) {
                 throw new IllegalArgumentException("<typeId> must not be null.");
             }
             m_typeId = typeId;
             return this;
        }

        @Override
        public DefaultNodePortTemplateEntBuilder setOptional(Boolean optional) {
             m_optional = optional;
             return this;
        }

        @Override
        public DefaultNodePortTemplateEnt build() {
            return new DefaultNodePortTemplateEnt(
                immutable(m_name),
                immutable(m_typeId),
                immutable(m_optional));
        }
    
    }

}
