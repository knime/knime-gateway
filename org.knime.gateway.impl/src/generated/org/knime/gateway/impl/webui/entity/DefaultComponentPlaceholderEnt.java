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

import java.math.BigDecimal;
import org.knime.gateway.api.webui.entity.XYEnt;

import org.knime.gateway.api.webui.entity.ComponentPlaceholderEnt;

/**
 * Placeholder for a component why it&#39;s being loaded.
 *
 * @param id
 * @param name
 * @param state
 * @param componentId
 * @param progress
 * @param message
 * @param details
 * @param position
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public record DefaultComponentPlaceholderEnt(
    String id,
    String name,
    StateEnum state,
    String componentId,
    BigDecimal progress,
    String message,
    String details,
    XYEnt position) implements ComponentPlaceholderEnt {

    /**
     * Validation for required parameters not being {@code null}.
     */
    public DefaultComponentPlaceholderEnt {
        if(id == null) {
            throw new IllegalArgumentException("<id> must not be null.");
        }
        if(state == null) {
            throw new IllegalArgumentException("<state> must not be null.");
        }
        if(position == null) {
            throw new IllegalArgumentException("<position> must not be null.");
        }
    }

    @Override
    public String getTypeID() {
        return "ComponentPlaceholder";
    }
  
    @Override
    public String getId() {
        return id;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public StateEnum getState() {
        return state;
    }
    
    @Override
    public String getComponentId() {
        return componentId;
    }
    
    @Override
    public BigDecimal getProgress() {
        return progress;
    }
    
    @Override
    public String getMessage() {
        return message;
    }
    
    @Override
    public String getDetails() {
        return details;
    }
    
    @Override
    public XYEnt getPosition() {
        return position;
    }
    
    /**
     * A builder for {@link DefaultComponentPlaceholderEnt}.
     */
    public static class DefaultComponentPlaceholderEntBuilder implements ComponentPlaceholderEntBuilder {

        private String m_id;

        private String m_name;

        private StateEnum m_state;

        private String m_componentId;

        private BigDecimal m_progress;

        private String m_message;

        private String m_details;

        private XYEnt m_position;

        @Override
        public DefaultComponentPlaceholderEntBuilder setId(String id) {
             if(id == null) {
                 throw new IllegalArgumentException("<id> must not be null.");
             }
             m_id = id;
             return this;
        }

        @Override
        public DefaultComponentPlaceholderEntBuilder setName(String name) {
             m_name = name;
             return this;
        }

        @Override
        public DefaultComponentPlaceholderEntBuilder setState(StateEnum state) {
             if(state == null) {
                 throw new IllegalArgumentException("<state> must not be null.");
             }
             m_state = state;
             return this;
        }

        @Override
        public DefaultComponentPlaceholderEntBuilder setComponentId(String componentId) {
             m_componentId = componentId;
             return this;
        }

        @Override
        public DefaultComponentPlaceholderEntBuilder setProgress(BigDecimal progress) {
             m_progress = progress;
             return this;
        }

        @Override
        public DefaultComponentPlaceholderEntBuilder setMessage(String message) {
             m_message = message;
             return this;
        }

        @Override
        public DefaultComponentPlaceholderEntBuilder setDetails(String details) {
             m_details = details;
             return this;
        }

        @Override
        public DefaultComponentPlaceholderEntBuilder setPosition(XYEnt position) {
             if(position == null) {
                 throw new IllegalArgumentException("<position> must not be null.");
             }
             m_position = position;
             return this;
        }

        @Override
        public DefaultComponentPlaceholderEnt build() {
            return new DefaultComponentPlaceholderEnt(
                immutable(m_id),
                immutable(m_name),
                immutable(m_state),
                immutable(m_componentId),
                immutable(m_progress),
                immutable(m_message),
                immutable(m_details),
                immutable(m_position));
        }
    
    }

}
