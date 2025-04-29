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

import org.knime.gateway.api.webui.entity.XYEnt;
import org.knime.gateway.impl.webui.entity.DefaultWorkflowCommandEnt;

import org.knime.gateway.api.webui.entity.AddComponentCommandEnt;

/**
 * Adds a new component to the workflow.
 *
 * @param kind
 * @param providerId
 * @param spaceId
 * @param itemId
 * @param position
 * @param name
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public record DefaultAddComponentCommandEnt(
    KindEnum kind,
    String providerId,
    String spaceId,
    String itemId,
    XYEnt position,
    String name) implements AddComponentCommandEnt {

    /**
     * Validation for required parameters not being {@code null}.
     */
    public DefaultAddComponentCommandEnt {
        if(kind == null) {
            throw new IllegalArgumentException("<kind> must not be null.");
        }
        if(providerId == null) {
            throw new IllegalArgumentException("<providerId> must not be null.");
        }
        if(spaceId == null) {
            throw new IllegalArgumentException("<spaceId> must not be null.");
        }
        if(itemId == null) {
            throw new IllegalArgumentException("<itemId> must not be null.");
        }
        if(position == null) {
            throw new IllegalArgumentException("<position> must not be null.");
        }
        if(name == null) {
            throw new IllegalArgumentException("<name> must not be null.");
        }
    }

    @Override
    public String getTypeID() {
        return "AddComponentCommand";
    }
  
    @Override
    public KindEnum getKind() {
        return kind;
    }
    
    @Override
    public String getProviderId() {
        return providerId;
    }
    
    @Override
    public String getSpaceId() {
        return spaceId;
    }
    
    @Override
    public String getItemId() {
        return itemId;
    }
    
    @Override
    public XYEnt getPosition() {
        return position;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    /**
     * A builder for {@link DefaultAddComponentCommandEnt}.
     */
    public static class DefaultAddComponentCommandEntBuilder implements AddComponentCommandEntBuilder {

        private KindEnum m_kind;

        private String m_providerId;

        private String m_spaceId;

        private String m_itemId;

        private XYEnt m_position;

        private String m_name;

        @Override
        public DefaultAddComponentCommandEntBuilder setKind(KindEnum kind) {
             if(kind == null) {
                 throw new IllegalArgumentException("<kind> must not be null.");
             }
             m_kind = kind;
             return this;
        }

        @Override
        public DefaultAddComponentCommandEntBuilder setProviderId(String providerId) {
             if(providerId == null) {
                 throw new IllegalArgumentException("<providerId> must not be null.");
             }
             m_providerId = providerId;
             return this;
        }

        @Override
        public DefaultAddComponentCommandEntBuilder setSpaceId(String spaceId) {
             if(spaceId == null) {
                 throw new IllegalArgumentException("<spaceId> must not be null.");
             }
             m_spaceId = spaceId;
             return this;
        }

        @Override
        public DefaultAddComponentCommandEntBuilder setItemId(String itemId) {
             if(itemId == null) {
                 throw new IllegalArgumentException("<itemId> must not be null.");
             }
             m_itemId = itemId;
             return this;
        }

        @Override
        public DefaultAddComponentCommandEntBuilder setPosition(XYEnt position) {
             if(position == null) {
                 throw new IllegalArgumentException("<position> must not be null.");
             }
             m_position = position;
             return this;
        }

        @Override
        public DefaultAddComponentCommandEntBuilder setName(String name) {
             if(name == null) {
                 throw new IllegalArgumentException("<name> must not be null.");
             }
             m_name = name;
             return this;
        }

        @Override
        public DefaultAddComponentCommandEnt build() {
            return new DefaultAddComponentCommandEnt(
                immutable(m_kind),
                immutable(m_providerId),
                immutable(m_spaceId),
                immutable(m_itemId),
                immutable(m_position),
                immutable(m_name));
        }
    
    }

}
