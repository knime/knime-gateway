/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.org; Email: contact@knime.org
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
 * ---------------------------------------------------------------------
 *
 * History
 *   Feb 4, 2025 (hornm): created
 */
package org.knime.gateway.impl.webui.service;

import java.util.List;

import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.util.ExtPointUtil;
import org.knime.gateway.api.webui.service.ComponentEditorService;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.ServiceCallException;

/**
 * Service implementation for the composite view service. Practically a wrapper that delegates all calls to a
 * {@link ComponentEditorService} implementation created in js-core
 *
 * @author Tobias Kampmann, TNG Technology Consulting GmbH
 * @since 5.7
 */
public class DefaultComponentEditorService implements ComponentEditorService {

    private ComponentEditorService m_componentEditorServiceDelegate;

    DefaultComponentEditorService() {
        // singleton
    }

    @Override
    public String getViewNodes(final String projectId, final NodeIDEnt workflowId, final NodeIDEnt nodeId)
        throws ServiceCallException {

        DefaultServiceContext.assertWorkflowProjectId(projectId);
        return getComponentEditorServiceDelegate().getViewNodes(projectId, workflowId, nodeId);
    }

    @Override
    public String getViewLayout(final String projectId, final NodeIDEnt workflowId, final NodeIDEnt nodeId)
        throws ServiceCallException {

        DefaultServiceContext.assertWorkflowProjectId(projectId);
        return getComponentEditorServiceDelegate().getViewLayout(projectId, workflowId, nodeId);
    }

    @Override
    public void setViewLayout(final String projectId, final NodeIDEnt workflowId, final NodeIDEnt nodeId,
        final String componentViewLayout) throws ServiceCallException {

        DefaultServiceContext.assertWorkflowProjectId(projectId);
        getComponentEditorServiceDelegate().setViewLayout(projectId, workflowId, nodeId, componentViewLayout);
    }

    @Override
    public String getConfigurationNodes(final String projectId, final NodeIDEnt workflowId, final NodeIDEnt nodeId)
        throws ServiceCallException {

        DefaultServiceContext.assertWorkflowProjectId(projectId);
        return getComponentEditorServiceDelegate().getConfigurationNodes(projectId, workflowId, nodeId);
    }

    @Override
    public String getConfigurationLayout(final String projectId, final NodeIDEnt workflowId, final NodeIDEnt nodeId)
        throws ServiceCallException {

        DefaultServiceContext.assertWorkflowProjectId(projectId);
        return getComponentEditorServiceDelegate().getConfigurationLayout(projectId, workflowId, nodeId);
    }

    @Override
    public void setConfigurationLayout(final String projectId, final NodeIDEnt workflowId, final NodeIDEnt nodeId,
        final String componentConfigurationLayout) throws ServiceCallException {

        DefaultServiceContext.assertWorkflowProjectId(projectId);
        getComponentEditorServiceDelegate().setConfigurationLayout(projectId, workflowId, nodeId,
            componentConfigurationLayout);
    }

    private ComponentEditorService getComponentEditorServiceDelegate() {
        if (m_componentEditorServiceDelegate == null) {
            List<GatewayServiceFactory> serviceFactories =
                ExtPointUtil.collectExecutableExtensions("org.knime.gateway.impl.GatewayServiceFactory", "impl");
            if (serviceFactories.size() != 1) {
                throw new IllegalStateException(
                    "Expected only a single gateway service factory. Got " + serviceFactories.size() + ".");
            }
            m_componentEditorServiceDelegate = serviceFactories.get(0).createComponentEditorService();
        }
        return m_componentEditorServiceDelegate;
    }

}
