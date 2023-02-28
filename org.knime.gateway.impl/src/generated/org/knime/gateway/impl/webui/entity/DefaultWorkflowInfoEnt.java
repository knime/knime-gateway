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

import org.knime.gateway.api.webui.entity.JobManagerEnt;

import org.knime.gateway.api.webui.entity.WorkflowInfoEnt;

/**
 * DefaultWorkflowInfoEnt
 *
 * @param name
 * @param containerId
 * @param containerType
 * @param linked
 * @param onHub
 * @param jobManager
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @author Kai Franze, KNIME GmbH
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public record DefaultWorkflowInfoEnt(
    String name,
    org.knime.gateway.api.entity.NodeIDEnt containerId,
    ContainerTypeEnum containerType,
    Boolean linked,
    Boolean onHub,
    JobManagerEnt jobManager) implements WorkflowInfoEnt {

    /**
     * Canonical constructor for {@link DefaultWorkflowInfoEnt} including null checks for non-nullable parameters.
     *
     * @param name
     * @param containerId
     * @param containerType
     * @param linked
     * @param onHub
     * @param jobManager
     */
    public DefaultWorkflowInfoEnt {
        if(name == null) {
            throw new IllegalArgumentException("<name> must not be null.");
        }
        if(containerId == null) {
            throw new IllegalArgumentException("<containerId> must not be null.");
        }
        if(containerType == null) {
            throw new IllegalArgumentException("<containerType> must not be null.");
        }
    }

    @Override
    public String getTypeID() {
        return "WorkflowInfo";
    }
  
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public org.knime.gateway.api.entity.NodeIDEnt getContainerId() {
        return containerId;
    }
    
    @Override
    public ContainerTypeEnum getContainerType() {
        return containerType;
    }
    
    @Override
    public Boolean isLinked() {
        return linked;
    }
    
    @Override
    public Boolean isOnHub() {
        return onHub;
    }
    
    @Override
    public JobManagerEnt getJobManager() {
        return jobManager;
    }
    
    /**
     * A builder for {@link DefaultWorkflowInfoEnt}.
     */
    public static class DefaultWorkflowInfoEntBuilder implements WorkflowInfoEntBuilder {

        private String m_name;

        private org.knime.gateway.api.entity.NodeIDEnt m_containerId;

        private ContainerTypeEnum m_containerType;

        private Boolean m_linked;

        private Boolean m_onHub;

        private JobManagerEnt m_jobManager;

        @Override
        public DefaultWorkflowInfoEntBuilder setName(String name) {
             if(name == null) {
                 throw new IllegalArgumentException("<name> must not be null.");
             }
             m_name = name;
             return this;
        }

        @Override
        public DefaultWorkflowInfoEntBuilder setContainerId(org.knime.gateway.api.entity.NodeIDEnt containerId) {
             if(containerId == null) {
                 throw new IllegalArgumentException("<containerId> must not be null.");
             }
             m_containerId = containerId;
             return this;
        }

        @Override
        public DefaultWorkflowInfoEntBuilder setContainerType(ContainerTypeEnum containerType) {
             if(containerType == null) {
                 throw new IllegalArgumentException("<containerType> must not be null.");
             }
             m_containerType = containerType;
             return this;
        }

        @Override
        public DefaultWorkflowInfoEntBuilder setLinked(Boolean linked) {
             m_linked = linked;
             return this;
        }

        @Override
        public DefaultWorkflowInfoEntBuilder setOnHub(Boolean onHub) {
             m_onHub = onHub;
             return this;
        }

        @Override
        public DefaultWorkflowInfoEntBuilder setJobManager(JobManagerEnt jobManager) {
             m_jobManager = jobManager;
             return this;
        }

        @Override
        public DefaultWorkflowInfoEnt build() {
            return new DefaultWorkflowInfoEnt(
                immutable(m_name),
                immutable(m_containerId),
                immutable(m_containerType),
                immutable(m_linked),
                immutable(m_onHub),
                immutable(m_jobManager));
        }
    
    }

}
