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


import org.knime.gateway.api.webui.entity.UpdateInfoEnt;

/**
 * Information about an available update, derived from &#x60;UpdateInfo&#x60; core class.
 *
 * @param name
 * @param shortName
 * @param isUpdatePossible
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public record DefaultUpdateInfoEnt(
    String name,
    String shortName,
    Boolean isUpdatePossible) implements UpdateInfoEnt {

    /**
     * Validation for required parameters not being {@code null}.
     */
    public DefaultUpdateInfoEnt {
        if(name == null) {
            throw new IllegalArgumentException("<name> must not be null.");
        }
        if(shortName == null) {
            throw new IllegalArgumentException("<shortName> must not be null.");
        }
        if(isUpdatePossible == null) {
            throw new IllegalArgumentException("<isUpdatePossible> must not be null.");
        }
    }

    @Override
    public String getTypeID() {
        return "UpdateInfo";
    }
  
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public String getShortName() {
        return shortName;
    }
    
    @Override
    public Boolean isUpdatePossible() {
        return isUpdatePossible;
    }
    
    /**
     * A builder for {@link DefaultUpdateInfoEnt}.
     */
    public static class DefaultUpdateInfoEntBuilder implements UpdateInfoEntBuilder {

        private String m_name;

        private String m_shortName;

        private Boolean m_isUpdatePossible;

        @Override
        public DefaultUpdateInfoEntBuilder setName(String name) {
             if(name == null) {
                 throw new IllegalArgumentException("<name> must not be null.");
             }
             m_name = name;
             return this;
        }

        @Override
        public DefaultUpdateInfoEntBuilder setShortName(String shortName) {
             if(shortName == null) {
                 throw new IllegalArgumentException("<shortName> must not be null.");
             }
             m_shortName = shortName;
             return this;
        }

        @Override
        public DefaultUpdateInfoEntBuilder setIsUpdatePossible(Boolean isUpdatePossible) {
             if(isUpdatePossible == null) {
                 throw new IllegalArgumentException("<isUpdatePossible> must not be null.");
             }
             m_isUpdatePossible = isUpdatePossible;
             return this;
        }

        @Override
        public DefaultUpdateInfoEnt build() {
            return new DefaultUpdateInfoEnt(
                immutable(m_name),
                immutable(m_shortName),
                immutable(m_isUpdatePossible));
        }
    
    }

}
