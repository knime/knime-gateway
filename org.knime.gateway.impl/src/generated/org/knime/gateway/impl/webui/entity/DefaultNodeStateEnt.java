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

import org.knime.gateway.api.webui.entity.NodeStateEnt;

/**
 * Encapsulates properties around a node&#39;s execution state.
 *
 * @param executionState
 * @param progress
 * @param progressMessages
 * @param error
 * @param warning
 * @param issue
 * @param resolutions
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public record DefaultNodeStateEnt(
    ExecutionStateEnum executionState,
    BigDecimal progress,
    java.util.List<String> progressMessages,
    String error,
    String warning,
    String issue,
    java.util.List<String> resolutions) implements NodeStateEnt {

    /**
     * Validation for required parameters not being {@code null}.
     */
    public DefaultNodeStateEnt {
    }

    @Override
    public String getTypeID() {
        return "NodeState";
    }
  
    @Override
    public ExecutionStateEnum getExecutionState() {
        return executionState;
    }
    
    @Override
    public BigDecimal getProgress() {
        return progress;
    }
    
    @Override
    public java.util.List<String> getProgressMessages() {
        return progressMessages;
    }
    
    @Override
    public String getError() {
        return error;
    }
    
    @Override
    public String getWarning() {
        return warning;
    }
    
    @Override
    public String getIssue() {
        return issue;
    }
    
    @Override
    public java.util.List<String> getResolutions() {
        return resolutions;
    }
    
    /**
     * A builder for {@link DefaultNodeStateEnt}.
     */
    public static class DefaultNodeStateEntBuilder implements NodeStateEntBuilder {

        private ExecutionStateEnum m_executionState;

        private BigDecimal m_progress;

        private java.util.List<String> m_progressMessages;

        private String m_error;

        private String m_warning;

        private String m_issue;

        private java.util.List<String> m_resolutions;

        @Override
        public DefaultNodeStateEntBuilder setExecutionState(ExecutionStateEnum executionState) {
             m_executionState = executionState;
             return this;
        }

        @Override
        public DefaultNodeStateEntBuilder setProgress(BigDecimal progress) {
             m_progress = progress;
             return this;
        }

        @Override
        public DefaultNodeStateEntBuilder setProgressMessages(java.util.List<String> progressMessages) {
             m_progressMessages = progressMessages;
             return this;
        }

        @Override
        public DefaultNodeStateEntBuilder setError(String error) {
             m_error = error;
             return this;
        }

        @Override
        public DefaultNodeStateEntBuilder setWarning(String warning) {
             m_warning = warning;
             return this;
        }

        @Override
        public DefaultNodeStateEntBuilder setIssue(String issue) {
             m_issue = issue;
             return this;
        }

        @Override
        public DefaultNodeStateEntBuilder setResolutions(java.util.List<String> resolutions) {
             m_resolutions = resolutions;
             return this;
        }

        @Override
        public DefaultNodeStateEnt build() {
            return new DefaultNodeStateEnt(
                immutable(m_executionState),
                immutable(m_progress),
                immutable(m_progressMessages),
                immutable(m_error),
                immutable(m_warning),
                immutable(m_issue),
                immutable(m_resolutions));
        }
    
    }

}
