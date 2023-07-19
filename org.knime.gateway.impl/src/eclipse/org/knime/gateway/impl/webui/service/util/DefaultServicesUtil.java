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
 */
package org.knime.gateway.impl.webui.service.util;

import org.knime.gateway.api.webui.entity.AppStateEnt;
import org.knime.gateway.impl.project.WorkflowProjectManager;
import org.knime.gateway.impl.service.util.EventConsumer;
import org.knime.gateway.impl.webui.AppStateUpdater;
import org.knime.gateway.impl.webui.ExampleProjects;
import org.knime.gateway.impl.webui.NodeFactoryProvider;
import org.knime.gateway.impl.webui.PreferencesProvider;
import org.knime.gateway.impl.webui.UpdateStateProvider;
import org.knime.gateway.impl.webui.WorkflowMiddleware;
import org.knime.gateway.impl.webui.service.DefaultApplicationService;
import org.knime.gateway.impl.webui.service.ServiceDependencies;
import org.knime.gateway.impl.webui.service.ServiceInstances;
import org.knime.gateway.impl.webui.spaces.SpaceProviders;

/**
 * Utility methods to manage Gateway services
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @author Benjamin Moser, KNIME GmbH, Konstanz, Germany
 * @author Kai Franze, KNIME GmbH
 */
public final class DefaultServicesUtil {

    private DefaultServicesUtil() {
        // Utility class
    }

    /**
     * Set all dependencies required by the default service implementations
     *
     * @param workflowProjectManager
     * @param workflowMiddleware
     * @param appStateUpdater The application state updater
     * @param eventConsumer The event consumer
     * @param spaceProviders The space providers
     * @param updateStateProvider The update state provider
     * @param preferencesProvider
     * @param exampleProjects
     * @param nodeFactoryProvider
     */
    public static void setDefaultServiceDependencies( // NOSONAR
        final WorkflowProjectManager workflowProjectManager, //
        final WorkflowMiddleware workflowMiddleware, //
        final AppStateUpdater appStateUpdater, //
        final EventConsumer eventConsumer, //
        final SpaceProviders spaceProviders, //
        final UpdateStateProvider updateStateProvider, //
        final PreferencesProvider preferencesProvider, //
        final ExampleProjects exampleProjects, //
        final NodeFactoryProvider nodeFactoryProvider) {
        if (!ServiceInstances.areServicesInitialized()) {
            ServiceDependencies.setServiceDependency(AppStateUpdater.class, appStateUpdater);
            ServiceDependencies.setServiceDependency(EventConsumer.class, eventConsumer);
            ServiceDependencies.setServiceDependency(WorkflowMiddleware.class, workflowMiddleware);
            ServiceDependencies.setServiceDependency(WorkflowProjectManager.class, workflowProjectManager);
            ServiceDependencies.setServiceDependency(SpaceProviders.class, spaceProviders);
            ServiceDependencies.setServiceDependency(UpdateStateProvider.class, updateStateProvider);
            ServiceDependencies.setServiceDependency(PreferencesProvider.class, preferencesProvider);
            ServiceDependencies.setServiceDependency(ExampleProjects.class, exampleProjects);
            ServiceDependencies.setServiceDependency(NodeFactoryProvider.class, nodeFactoryProvider);
        } else {
            throw new IllegalStateException(
                "Some services are already initialized. Service dependencies can't be set anymore. "
                    + "Maybe you already started a Web UI within the AP and have now tried to launch another instance in a browser, or vice versa?");
        }
    }

    /**
     * Remove the application service from the provided service dependencies, remove listeners and clear references to
     * workflow projects.
     */
    public static void disposeDefaultServices() {
        removeWorkflowProjects();
        ServiceInstances.disposeAllServiceInstancesAndDependencies();
    }

    private static void removeWorkflowProjects() {
        if (ServiceInstances.areServicesInitialized()) {
            AppStateEnt previousState = DefaultApplicationService.getInstance().getState();
            if (previousState != null) {
                previousState.getOpenProjects().forEach(
                    wfProjEnt -> WorkflowProjectManager.getInstance().removeWorkflowProject(wfProjEnt.getProjectId()));
            }
        }
    }

}
