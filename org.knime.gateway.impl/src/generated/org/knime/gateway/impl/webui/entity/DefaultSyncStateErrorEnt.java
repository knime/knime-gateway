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


import org.knime.gateway.api.webui.entity.SyncStateErrorEnt;

/**
 * Additional error details in case of a sync state error.
 *
 * @param code
 * @param title
 * @param details
 * @param canCopy
 * @param status
 * @param stackTrace
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public record DefaultSyncStateErrorEnt(
    String code,
    String title,
    java.util.List<String> details,
    Boolean canCopy,
    Integer status,
    String stackTrace) implements SyncStateErrorEnt {

    /**
     * Validation for required parameters not being {@code null}.
     */
    public DefaultSyncStateErrorEnt {
        if(code == null) {
            throw new IllegalArgumentException("<code> must not be null.");
        }
        if(title == null) {
            throw new IllegalArgumentException("<title> must not be null.");
        }
        if(details == null) {
            throw new IllegalArgumentException("<details> must not be null.");
        }
        if(canCopy == null) {
            throw new IllegalArgumentException("<canCopy> must not be null.");
        }
    }

    @Override
    public String getTypeID() {
        return "SyncStateError";
    }
  
    @Override
    public String getCode() {
        return code;
    }
    
    @Override
    public String getTitle() {
        return title;
    }
    
    @Override
    public java.util.List<String> getDetails() {
        return details;
    }
    
    @Override
    public Boolean isCanCopy() {
        return canCopy;
    }
    
    @Override
    public Integer getStatus() {
        return status;
    }
    
    @Override
    public String getStackTrace() {
        return stackTrace;
    }
    
    /**
     * A builder for {@link DefaultSyncStateErrorEnt}.
     */
    public static class DefaultSyncStateErrorEntBuilder implements SyncStateErrorEntBuilder {

        private String m_code;

        private String m_title;

        private java.util.List<String> m_details = new java.util.ArrayList<>();

        private Boolean m_canCopy;

        private Integer m_status;

        private String m_stackTrace;

        @Override
        public DefaultSyncStateErrorEntBuilder setCode(String code) {
             if(code == null) {
                 throw new IllegalArgumentException("<code> must not be null.");
             }
             m_code = code;
             return this;
        }

        @Override
        public DefaultSyncStateErrorEntBuilder setTitle(String title) {
             if(title == null) {
                 throw new IllegalArgumentException("<title> must not be null.");
             }
             m_title = title;
             return this;
        }

        @Override
        public DefaultSyncStateErrorEntBuilder setDetails(java.util.List<String> details) {
             if(details == null) {
                 throw new IllegalArgumentException("<details> must not be null.");
             }
             m_details = details;
             return this;
        }

        @Override
        public DefaultSyncStateErrorEntBuilder setCanCopy(Boolean canCopy) {
             if(canCopy == null) {
                 throw new IllegalArgumentException("<canCopy> must not be null.");
             }
             m_canCopy = canCopy;
             return this;
        }

        @Override
        public DefaultSyncStateErrorEntBuilder setStatus(Integer status) {
             m_status = status;
             return this;
        }

        @Override
        public DefaultSyncStateErrorEntBuilder setStackTrace(String stackTrace) {
             m_stackTrace = stackTrace;
             return this;
        }

        @Override
        public DefaultSyncStateErrorEnt build() {
            return new DefaultSyncStateErrorEnt(
                immutable(m_code),
                immutable(m_title),
                immutable(m_details),
                immutable(m_canCopy),
                immutable(m_status),
                immutable(m_stackTrace));
        }
    
    }

}
