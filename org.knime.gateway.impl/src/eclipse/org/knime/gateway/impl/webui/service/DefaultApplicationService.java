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
 *   Aug 21, 2020 (hornm): created
 */
package org.knime.gateway.impl.webui.service;

import java.util.function.Predicate;

import org.knime.gateway.api.webui.entity.AppStateEnt;
import org.knime.gateway.api.webui.service.ApplicationService;
import org.knime.gateway.impl.project.ProjectManager;
import org.knime.gateway.impl.webui.AppStateUpdater;
import org.knime.gateway.impl.webui.NodeCollections;
import org.knime.gateway.impl.webui.NodeFactoryProvider;
import org.knime.gateway.impl.webui.PreferencesProvider;
import org.knime.gateway.impl.webui.entity.AppStateEntityFactory;
import org.knime.gateway.impl.webui.kai.KaiHandler;
import org.knime.gateway.impl.webui.spaces.SpaceProviders;

/**
 * The default implementation of the {@link ApplicationService}-interface.
 *
 * @noextend This class is not intended to be subclassed by clients.
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @author Kai Franze, KNIME GmbH
 */
public final class DefaultApplicationService implements ApplicationService {

    private final AppStateUpdater m_appStateUpdater =
        ServiceDependencies.getServiceDependency(AppStateUpdater.class, false);

    private final ProjectManager m_projectManager =
        ServiceDependencies.getServiceDependency(ProjectManager.class, true);

    private final PreferencesProvider m_preferencesProvider =
        ServiceDependencies.getServiceDependency(PreferencesProvider.class, true);

    private final SpaceProviders m_spaceProviders =
        ServiceDependencies.getServiceDependency(SpaceProviders.class, true);

    private final NodeFactoryProvider m_nodeFactoryProvider =
        ServiceDependencies.getServiceDependency(NodeFactoryProvider.class, false);

    private final NodeCollections m_nodeCollections =
        ServiceDependencies.getServiceDependency(NodeCollections.class, false);

    private final KaiHandler m_kaiHandler = ServiceDependencies.getServiceDependency(KaiHandler.class, false);

    /**
     * Returns the singleton instance for this service.
     *
     * @return the singleton instance
     */
    public static DefaultApplicationService getInstance() {
        return ServiceInstances.getDefaultServiceInstance(DefaultApplicationService.class);
    }

    DefaultApplicationService() {
        // singleton
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        if (m_appStateUpdater != null) {
            m_appStateUpdater.setLastAppState(null);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AppStateEnt getState() {
        var workflowProjectFilter =
            DefaultServiceContext.getWorkflowProjectId().<Predicate<String>> map(id -> id::equals).orElse(null);
        Predicate<String> isActiveProject = workflowProjectFilter == null ? null : id -> true;
        var dependencies = new AppStateEntityFactory.ServiceDependencies(m_projectManager, m_preferencesProvider,
            m_spaceProviders, m_nodeFactoryProvider, m_nodeCollections, m_kaiHandler);
        var appState =
            AppStateEntityFactory.buildAppStateEnt(workflowProjectFilter, isActiveProject, dependencies);
        if (m_appStateUpdater != null) {
            m_appStateUpdater.setLastAppState(appState);
        }
        return appState;
    }

}
