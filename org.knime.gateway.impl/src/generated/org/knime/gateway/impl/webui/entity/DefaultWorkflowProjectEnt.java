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

import org.knime.gateway.api.webui.entity.SpaceItemReferenceEnt;

import org.knime.gateway.api.webui.entity.WorkflowProjectEnt;

/**
 * Represents an entire workflow project.
 *
 * @param projectId
 * @param origin
 * @param name
 * @param activeWorkflowId
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @author Kai Franze, KNIME GmbH
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public record DefaultWorkflowProjectEnt(
    String projectId,
    SpaceItemReferenceEnt origin,
    String name,
    org.knime.gateway.api.entity.NodeIDEnt activeWorkflowId) implements WorkflowProjectEnt {

    /**
     * Canonical constructor for {@link DefaultWorkflowProjectEnt} including null checks for non-nullable parameters.
     *
     * @param projectId
     * @param origin
     * @param name
     * @param activeWorkflowId
     */
    public DefaultWorkflowProjectEnt {
        if(projectId == null) {
            throw new IllegalArgumentException("<projectId> must not be null.");
        }
        if(origin == null) {
            throw new IllegalArgumentException("<origin> must not be null.");
        }
        if(name == null) {
            throw new IllegalArgumentException("<name> must not be null.");
        }
    }

    @Override
    public String getTypeID() {
        return "WorkflowProject";
    }
  
    @Override
    public String getProjectId() {
        return projectId;
    }
    
    @Override
    public SpaceItemReferenceEnt getOrigin() {
        return origin;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public org.knime.gateway.api.entity.NodeIDEnt getActiveWorkflowId() {
        return activeWorkflowId;
    }
    
    /**
     * A builder for {@link DefaultWorkflowProjectEnt}.
     */
    public static class DefaultWorkflowProjectEntBuilder implements WorkflowProjectEntBuilder {

        private String m_projectId;

        private SpaceItemReferenceEnt m_origin;

        private String m_name;

        private org.knime.gateway.api.entity.NodeIDEnt m_activeWorkflowId;

        @Override
        public DefaultWorkflowProjectEntBuilder setProjectId(String projectId) {
             if(projectId == null) {
                 throw new IllegalArgumentException("<projectId> must not be null.");
             }
             m_projectId = projectId;
             return this;
        }

        @Override
        public DefaultWorkflowProjectEntBuilder setOrigin(SpaceItemReferenceEnt origin) {
             if(origin == null) {
                 throw new IllegalArgumentException("<origin> must not be null.");
             }
             m_origin = origin;
             return this;
        }

        @Override
        public DefaultWorkflowProjectEntBuilder setName(String name) {
             if(name == null) {
                 throw new IllegalArgumentException("<name> must not be null.");
             }
             m_name = name;
             return this;
        }

        @Override
        public DefaultWorkflowProjectEntBuilder setActiveWorkflowId(org.knime.gateway.api.entity.NodeIDEnt activeWorkflowId) {
             m_activeWorkflowId = activeWorkflowId;
             return this;
        }

        @Override
        public DefaultWorkflowProjectEnt build() {
            return new DefaultWorkflowProjectEnt(
                immutable(m_projectId),
                immutable(m_origin),
                immutable(m_name),
                immutable(m_activeWorkflowId));
        }
    
    }

}
