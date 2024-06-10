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


import org.knime.gateway.api.webui.entity.KaiFeedbackEnt;

/**
 * Encapsulates user feedback to K-AI.
 *
 * @param isPositive
 * @param comment
 * @param projectId
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public record DefaultKaiFeedbackEnt(
    Boolean isPositive,
    String comment,
    String projectId) implements KaiFeedbackEnt {

    /**
     * Validation for required parameters not being {@code null}.
     */
    public DefaultKaiFeedbackEnt {
        if(isPositive == null) {
            throw new IllegalArgumentException("<isPositive> must not be null.");
        }
        if(comment == null) {
            throw new IllegalArgumentException("<comment> must not be null.");
        }
        if(projectId == null) {
            throw new IllegalArgumentException("<projectId> must not be null.");
        }
    }

    @Override
    public String getTypeID() {
        return "KaiFeedback";
    }
  
    @Override
    public Boolean isPositive() {
        return isPositive;
    }
    
    @Override
    public String getComment() {
        return comment;
    }
    
    @Override
    public String getProjectId() {
        return projectId;
    }
    
    /**
     * A builder for {@link DefaultKaiFeedbackEnt}.
     */
    public static class DefaultKaiFeedbackEntBuilder implements KaiFeedbackEntBuilder {

        private Boolean m_isPositive;

        private String m_comment;

        private String m_projectId;

        @Override
        public DefaultKaiFeedbackEntBuilder setIsPositive(Boolean isPositive) {
             if(isPositive == null) {
                 throw new IllegalArgumentException("<isPositive> must not be null.");
             }
             m_isPositive = isPositive;
             return this;
        }

        @Override
        public DefaultKaiFeedbackEntBuilder setComment(String comment) {
             if(comment == null) {
                 throw new IllegalArgumentException("<comment> must not be null.");
             }
             m_comment = comment;
             return this;
        }

        @Override
        public DefaultKaiFeedbackEntBuilder setProjectId(String projectId) {
             if(projectId == null) {
                 throw new IllegalArgumentException("<projectId> must not be null.");
             }
             m_projectId = projectId;
             return this;
        }

        @Override
        public DefaultKaiFeedbackEnt build() {
            return new DefaultKaiFeedbackEnt(
                immutable(m_isPositive),
                immutable(m_comment),
                immutable(m_projectId));
        }
    
    }

}
