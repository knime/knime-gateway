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

import org.knime.gateway.api.webui.entity.ProblemMessageEnt;
import org.knime.gateway.impl.webui.entity.DefaultCommandResultEnt;

import org.knime.gateway.api.webui.entity.AddComponentResultEnt;

/**
 * DefaultAddComponentResultEnt
 *
 * @param snapshotId
 * @param kind
 * @param newNodeId
 * @param problem
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public record DefaultAddComponentResultEnt(
    String snapshotId,
    KindEnum kind,
    org.knime.gateway.api.entity.NodeIDEnt newNodeId,
    ProblemMessageEnt problem) implements AddComponentResultEnt {

    /**
     * Validation for required parameters not being {@code null}.
     */
    public DefaultAddComponentResultEnt {
        if(newNodeId == null) {
            throw new IllegalArgumentException("<newNodeId> must not be null.");
        }
    }

    @Override
    public String getTypeID() {
        return "AddComponentResult";
    }
  
    @Override
    public String getSnapshotId() {
        return snapshotId;
    }
    
    @Override
    public KindEnum getKind() {
        return kind;
    }
    
    @Override
    public org.knime.gateway.api.entity.NodeIDEnt getNewNodeId() {
        return newNodeId;
    }
    
    @Override
    public ProblemMessageEnt getProblem() {
        return problem;
    }
    
    /**
     * A builder for {@link DefaultAddComponentResultEnt}.
     */
    public static class DefaultAddComponentResultEntBuilder implements AddComponentResultEntBuilder {

        private String m_snapshotId;

        private KindEnum m_kind;

        private org.knime.gateway.api.entity.NodeIDEnt m_newNodeId;

        private ProblemMessageEnt m_problem;

        @Override
        public DefaultAddComponentResultEntBuilder setSnapshotId(String snapshotId) {
             m_snapshotId = snapshotId;
             return this;
        }

        @Override
        public DefaultAddComponentResultEntBuilder setKind(KindEnum kind) {
             m_kind = kind;
             return this;
        }

        @Override
        public DefaultAddComponentResultEntBuilder setNewNodeId(org.knime.gateway.api.entity.NodeIDEnt newNodeId) {
             if(newNodeId == null) {
                 throw new IllegalArgumentException("<newNodeId> must not be null.");
             }
             m_newNodeId = newNodeId;
             return this;
        }

        @Override
        public DefaultAddComponentResultEntBuilder setProblem(ProblemMessageEnt problem) {
             m_problem = problem;
             return this;
        }

        @Override
        public DefaultAddComponentResultEnt build() {
            return new DefaultAddComponentResultEnt(
                immutable(m_snapshotId),
                immutable(m_kind),
                immutable(m_newNodeId),
                immutable(m_problem));
        }
    
    }

}
