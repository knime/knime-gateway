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

import java.time.OffsetDateTime;

import org.knime.gateway.api.webui.entity.NamedItemVersionEnt;

/**
 * Corresponds to org.knime.hub.client.sdk.ent.catalog.NamedItemVersion
 *
 * @param version
 * @param title
 * @param description
 * @param author
 * @param createdOn
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public record DefaultNamedItemVersionEnt(
    Integer version,
    String title,
    String description,
    String author,
    OffsetDateTime createdOn) implements NamedItemVersionEnt {

    /**
     * Validation for required parameters not being {@code null}.
     */
    public DefaultNamedItemVersionEnt {
        if(version == null) {
            throw new IllegalArgumentException("<version> must not be null.");
        }
    }

    @Override
    public String getTypeID() {
        return "NamedItemVersion";
    }
  
    @Override
    public Integer getVersion() {
        return version;
    }
    
    @Override
    public String getTitle() {
        return title;
    }
    
    @Override
    public String getDescription() {
        return description;
    }
    
    @Override
    public String getAuthor() {
        return author;
    }
    
    @Override
    public OffsetDateTime getCreatedOn() {
        return createdOn;
    }
    
    /**
     * A builder for {@link DefaultNamedItemVersionEnt}.
     */
    public static class DefaultNamedItemVersionEntBuilder implements NamedItemVersionEntBuilder {

        private Integer m_version;

        private String m_title;

        private String m_description;

        private String m_author;

        private OffsetDateTime m_createdOn;

        @Override
        public DefaultNamedItemVersionEntBuilder setVersion(Integer version) {
             if(version == null) {
                 throw new IllegalArgumentException("<version> must not be null.");
             }
             m_version = version;
             return this;
        }

        @Override
        public DefaultNamedItemVersionEntBuilder setTitle(String title) {
             m_title = title;
             return this;
        }

        @Override
        public DefaultNamedItemVersionEntBuilder setDescription(String description) {
             m_description = description;
             return this;
        }

        @Override
        public DefaultNamedItemVersionEntBuilder setAuthor(String author) {
             m_author = author;
             return this;
        }

        @Override
        public DefaultNamedItemVersionEntBuilder setCreatedOn(OffsetDateTime createdOn) {
             m_createdOn = createdOn;
             return this;
        }

        @Override
        public DefaultNamedItemVersionEnt build() {
            return new DefaultNamedItemVersionEnt(
                immutable(m_version),
                immutable(m_title),
                immutable(m_description),
                immutable(m_author),
                immutable(m_createdOn));
        }
    
    }

}
