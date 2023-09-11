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

import org.knime.gateway.impl.webui.entity.DefaultWorkflowAnnotationCommandEnt;

import org.knime.gateway.api.webui.entity.UpdateWorkflowAnnotationCommandEnt;

/**
 * Updates the text and/or the border color of a workflow annotation. Either one can be &#39;null&#39;, but never both of them.
 *
 * @param kind
 * @param annotationId
 * @param text
 * @param borderColor
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public record DefaultUpdateWorkflowAnnotationCommandEnt(
    KindEnum kind,
    org.knime.gateway.api.entity.AnnotationIDEnt annotationId,
    String text,
    String borderColor) implements UpdateWorkflowAnnotationCommandEnt {

    /**
     * Validation for required parameters not being {@code null}.
     */
    public DefaultUpdateWorkflowAnnotationCommandEnt {
        if(kind == null) {
            throw new IllegalArgumentException("<kind> must not be null.");
        }
        if(annotationId == null) {
            throw new IllegalArgumentException("<annotationId> must not be null.");
        }
    }

    @Override
    public String getTypeID() {
        return "UpdateWorkflowAnnotationCommand";
    }
  
    @Override
    public KindEnum getKind() {
        return kind;
    }
    
    @Override
    public org.knime.gateway.api.entity.AnnotationIDEnt getAnnotationId() {
        return annotationId;
    }
    
    @Override
    public String getText() {
        return text;
    }
    
    @Override
    public String getBorderColor() {
        return borderColor;
    }
    
    /**
     * A builder for {@link DefaultUpdateWorkflowAnnotationCommandEnt}.
     */
    public static class DefaultUpdateWorkflowAnnotationCommandEntBuilder implements UpdateWorkflowAnnotationCommandEntBuilder {

        private KindEnum m_kind;

        private org.knime.gateway.api.entity.AnnotationIDEnt m_annotationId;

        private String m_text;

        private String m_borderColor;

        @Override
        public DefaultUpdateWorkflowAnnotationCommandEntBuilder setKind(KindEnum kind) {
             if(kind == null) {
                 throw new IllegalArgumentException("<kind> must not be null.");
             }
             m_kind = kind;
             return this;
        }

        @Override
        public DefaultUpdateWorkflowAnnotationCommandEntBuilder setAnnotationId(org.knime.gateway.api.entity.AnnotationIDEnt annotationId) {
             if(annotationId == null) {
                 throw new IllegalArgumentException("<annotationId> must not be null.");
             }
             m_annotationId = annotationId;
             return this;
        }

        @Override
        public DefaultUpdateWorkflowAnnotationCommandEntBuilder setText(String text) {
             m_text = text;
             return this;
        }

        @Override
        public DefaultUpdateWorkflowAnnotationCommandEntBuilder setBorderColor(String borderColor) {
             m_borderColor = borderColor;
             return this;
        }

        @Override
        public DefaultUpdateWorkflowAnnotationCommandEnt build() {
            return new DefaultUpdateWorkflowAnnotationCommandEnt(
                immutable(m_kind),
                immutable(m_annotationId),
                immutable(m_text),
                immutable(m_borderColor));
        }
    
    }

}
