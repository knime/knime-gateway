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


import org.knime.gateway.api.webui.entity.KaiMessageEnt;

/**
 * A message send to K-AI. Consists of the role (user or AI) and a message content.
 *
 * @param role
 * @param content
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public record DefaultKaiMessageEnt(
    String role,
    String content) implements KaiMessageEnt {

    /**
     * Validation for required parameters not being {@code null}.
     */
    public DefaultKaiMessageEnt {
        if(role == null) {
            throw new IllegalArgumentException("<role> must not be null.");
        }
        if(content == null) {
            throw new IllegalArgumentException("<content> must not be null.");
        }
    }

    @Override
    public String getTypeID() {
        return "KaiMessage";
    }
  
    @Override
    public String getRole() {
        return role;
    }
    
    @Override
    public String getContent() {
        return content;
    }
    
    /**
     * A builder for {@link DefaultKaiMessageEnt}.
     */
    public static class DefaultKaiMessageEntBuilder implements KaiMessageEntBuilder {

        private String m_role;

        private String m_content;

        @Override
        public DefaultKaiMessageEntBuilder setRole(String role) {
             if(role == null) {
                 throw new IllegalArgumentException("<role> must not be null.");
             }
             m_role = role;
             return this;
        }

        @Override
        public DefaultKaiMessageEntBuilder setContent(String content) {
             if(content == null) {
                 throw new IllegalArgumentException("<content> must not be null.");
             }
             m_content = content;
             return this;
        }

        @Override
        public DefaultKaiMessageEnt build() {
            return new DefaultKaiMessageEnt(
                immutable(m_role),
                immutable(m_content));
        }
    
    }

}