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


import org.knime.gateway.api.webui.entity.GatewayProblemDescriptionEnt;

import java.util.Map;


/**
 * DefaultGatewayProblemDescriptionEnt
 *
 * @param title
 * @param code
 * @param canCopy
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public record DefaultGatewayProblemDescriptionEnt(
    String title,
    String code,
    Boolean canCopy, 
    Map<String, String> additionalProperties) implements GatewayProblemDescriptionEnt {

    /**
     * Validation for required parameters not being {@code null}.
     */
    public DefaultGatewayProblemDescriptionEnt {
    }

    @Override
    public String getTypeID() {
        return "GatewayProblemDescription";
    }
  
    @Override
    public String getTitle() {
        return title;
    }
    
    @Override
    public String getCode() {
        return code;
    }
    
    @Override
    public Boolean isCanCopy() {
        return canCopy;
    }
    
    @Override
    public Map<String, String> getAdditionalProperties() {
        return additionalProperties;
    }
    
    /**
     * A builder for {@link DefaultGatewayProblemDescriptionEnt}.
     */
    public static class DefaultGatewayProblemDescriptionEntBuilder implements GatewayProblemDescriptionEntBuilder {

        private String m_title;

        private String m_code;

        private Boolean m_canCopy;

        private Map<String, String> m_additionalProperties;

        @Override
        public DefaultGatewayProblemDescriptionEntBuilder setTitle(String title) {
             m_title = title;
             return this;
        }

        @Override
        public DefaultGatewayProblemDescriptionEntBuilder setCode(String code) {
             m_code = code;
             return this;
        }

        @Override
        public DefaultGatewayProblemDescriptionEntBuilder setCanCopy(Boolean canCopy) {
             m_canCopy = canCopy;
             return this;
        }

        @Override
        public DefaultGatewayProblemDescriptionEntBuilder setAdditionalProperties(Map<String, String> additionalProperties) {
            m_additionalProperties = additionalProperties;
            return this;
        }

        @Override
        public DefaultGatewayProblemDescriptionEnt build() {
            return new DefaultGatewayProblemDescriptionEnt(
                immutable(m_title),
                immutable(m_code),
                immutable(m_canCopy), 
                immutable(m_additionalProperties));
        }
    
    }

}
