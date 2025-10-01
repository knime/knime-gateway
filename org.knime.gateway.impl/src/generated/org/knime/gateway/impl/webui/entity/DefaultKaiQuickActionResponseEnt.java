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

import org.knime.gateway.api.webui.entity.KaiQuickActionResultEnt;
import org.knime.gateway.api.webui.entity.KaiUsageEnt;

import org.knime.gateway.api.webui.entity.KaiQuickActionResponseEnt;

/**
 * Response from executing an AI quick action.
 *
 * @param actionId
 * @param result
 * @param usage
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public record DefaultKaiQuickActionResponseEnt(
    ActionIdEnum actionId,
    KaiQuickActionResultEnt result,
    KaiUsageEnt usage) implements KaiQuickActionResponseEnt {

    /**
     * Validation for required parameters not being {@code null}.
     */
    public DefaultKaiQuickActionResponseEnt {
        if(actionId == null) {
            throw new IllegalArgumentException("<actionId> must not be null.");
        }
        if(result == null) {
            throw new IllegalArgumentException("<result> must not be null.");
        }
        if(usage == null) {
            throw new IllegalArgumentException("<usage> must not be null.");
        }
    }

    @Override
    public String getTypeID() {
        return "KaiQuickActionResponse";
    }
  
    @Override
    public ActionIdEnum getActionId() {
        return actionId;
    }
    
    @Override
    public KaiQuickActionResultEnt getResult() {
        return result;
    }
    
    @Override
    public KaiUsageEnt getUsage() {
        return usage;
    }
    
    /**
     * A builder for {@link DefaultKaiQuickActionResponseEnt}.
     */
    public static class DefaultKaiQuickActionResponseEntBuilder implements KaiQuickActionResponseEntBuilder {

        private ActionIdEnum m_actionId;

        private KaiQuickActionResultEnt m_result;

        private KaiUsageEnt m_usage;

        @Override
        public DefaultKaiQuickActionResponseEntBuilder setActionId(ActionIdEnum actionId) {
             if(actionId == null) {
                 throw new IllegalArgumentException("<actionId> must not be null.");
             }
             m_actionId = actionId;
             return this;
        }

        @Override
        public DefaultKaiQuickActionResponseEntBuilder setResult(KaiQuickActionResultEnt result) {
             if(result == null) {
                 throw new IllegalArgumentException("<result> must not be null.");
             }
             m_result = result;
             return this;
        }

        @Override
        public DefaultKaiQuickActionResponseEntBuilder setUsage(KaiUsageEnt usage) {
             if(usage == null) {
                 throw new IllegalArgumentException("<usage> must not be null.");
             }
             m_usage = usage;
             return this;
        }

        @Override
        public DefaultKaiQuickActionResponseEnt build() {
            return new DefaultKaiQuickActionResponseEnt(
                immutable(m_actionId),
                immutable(m_result),
                immutable(m_usage));
        }
    
    }

}
