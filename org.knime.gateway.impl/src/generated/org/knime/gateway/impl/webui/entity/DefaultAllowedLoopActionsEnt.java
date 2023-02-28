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


import org.knime.gateway.api.webui.entity.AllowedLoopActionsEnt;

/**
 * Determines what loop actions are allowed on a loop end node.
 *
 * @param canResume
 * @param canPause
 * @param canStep
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @author Kai Franze, KNIME GmbH
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public record DefaultAllowedLoopActionsEnt(
    Boolean canResume,
    Boolean canPause,
    Boolean canStep) implements AllowedLoopActionsEnt {

    /**
     * Canonical constructor for {@link DefaultAllowedLoopActionsEnt} including null checks for non-nullable parameters.
     *
     * @param canResume
     * @param canPause
     * @param canStep
     */
    public DefaultAllowedLoopActionsEnt {
        if(canResume == null) {
            throw new IllegalArgumentException("<canResume> must not be null.");
        }
        if(canPause == null) {
            throw new IllegalArgumentException("<canPause> must not be null.");
        }
        if(canStep == null) {
            throw new IllegalArgumentException("<canStep> must not be null.");
        }
    }

    @Override
    public String getTypeID() {
        return "AllowedLoopActions";
    }
  
    @Override
    public Boolean isCanResume() {
        return canResume;
    }
    
    @Override
    public Boolean isCanPause() {
        return canPause;
    }
    
    @Override
    public Boolean isCanStep() {
        return canStep;
    }
    
    /**
     * A builder for {@link DefaultAllowedLoopActionsEnt}.
     */
    public static class DefaultAllowedLoopActionsEntBuilder implements AllowedLoopActionsEntBuilder {

        private Boolean m_canResume;

        private Boolean m_canPause;

        private Boolean m_canStep;

        @Override
        public DefaultAllowedLoopActionsEntBuilder setCanResume(Boolean canResume) {
             if(canResume == null) {
                 throw new IllegalArgumentException("<canResume> must not be null.");
             }
             m_canResume = canResume;
             return this;
        }

        @Override
        public DefaultAllowedLoopActionsEntBuilder setCanPause(Boolean canPause) {
             if(canPause == null) {
                 throw new IllegalArgumentException("<canPause> must not be null.");
             }
             m_canPause = canPause;
             return this;
        }

        @Override
        public DefaultAllowedLoopActionsEntBuilder setCanStep(Boolean canStep) {
             if(canStep == null) {
                 throw new IllegalArgumentException("<canStep> must not be null.");
             }
             m_canStep = canStep;
             return this;
        }

        @Override
        public DefaultAllowedLoopActionsEnt build() {
            return new DefaultAllowedLoopActionsEnt(
                immutable(m_canResume),
                immutable(m_canPause),
                immutable(m_canStep));
        }
    
    }

}
