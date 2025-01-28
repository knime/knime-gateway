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
 *   Apr 12, 2024 (hornm): created
 */
package org.knime.gateway.impl.webui;

import static java.util.Arrays.asList;
import static org.knime.gateway.api.entity.EntityBuilderManager.builder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Test;
import org.knime.gateway.api.webui.entity.AppStateChangedEventEnt;
import org.knime.gateway.api.webui.entity.AppStateChangedEventEnt.AppStateChangedEventEntBuilder;
import org.knime.gateway.api.webui.entity.AppStateChangedEventTypeEnt.AppStateChangedEventTypeEntBuilder;
import org.knime.gateway.api.webui.entity.AppStateEnt.AppStateEntBuilder;
import org.knime.gateway.api.webui.entity.CompositeEventEnt;
import org.knime.gateway.api.webui.entity.CompositeEventEnt.CompositeEventEntBuilder;
import org.knime.gateway.api.webui.entity.ProjectDirtyStateEventEnt;
import org.knime.gateway.api.webui.entity.ProjectDirtyStateEventEnt.ProjectDirtyStateEventEntBuilder;
import org.knime.gateway.api.webui.entity.ProjectEnt.ProjectEntBuilder;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.InvalidRequestException;
import org.knime.gateway.impl.project.DefaultProject;
import org.knime.gateway.impl.project.Project;
import org.knime.gateway.impl.project.ProjectManager;
import org.knime.gateway.impl.webui.service.DefaultApplicationService;
import org.knime.gateway.impl.webui.service.DefaultEventService;
import org.knime.gateway.impl.webui.service.ServiceDependencies;
import org.knime.gateway.impl.webui.service.ServiceInstances;
import org.knime.gateway.impl.webui.service.events.EventConsumer;
import org.knime.gateway.impl.webui.spaces.SpaceProviders;
import org.knime.testing.util.WorkflowManagerUtil;

/**
 * Tests functionality related to the {@link AppStateUpdater}.
 */
public class AppStateUpdaterTest {

    /**
     * Tests the {@link AppStateUpdater} and the events it triggers (through the {@link DefaultEventService}).
     *
     * @throws InvalidRequestException
     * @throws IOException
     */
    @Test
    public void testAppStateUpdaterToTriggerAppStateChangedEvents() throws InvalidRequestException, IOException {
        var eventConsumer = mock(EventConsumer.class);
        var appStateUpdater = new AppStateUpdater();
        var preferencesProvider = mock(PreferencesProvider.class);
        when(preferencesProvider.hasNodeRecommendationsEnabled()).thenReturn(false);
        setupServiceDependencies(appStateUpdater, eventConsumer, preferencesProvider);

        var pm = ProjectManager.getInstance();
        var wfm1 = WorkflowManagerUtil.createEmptyWorkflow();
        var proj1 = DefaultProject.builder(wfm1).build();
        pm.addProject(proj1);

        DefaultApplicationService.getInstance().getState(); // initializes the app-state for the AppStateUpdater
        DefaultEventService.getInstance().addEventListener(builder(AppStateChangedEventTypeEntBuilder.class).build());
        // verify initial event
        var expectedEvent =
            builder(CompositeEventEntBuilder.class).setEvents(asList(null, buildProjectDirtyStateEvent(proj1)))
                .build();
        verify(eventConsumer).accept("AppStateChangedEvent:ProjectDirtyStateEvent", expectedEvent, null);

        // update open projects and verify resulting app state changed event
        var wfm2 = WorkflowManagerUtil.createEmptyWorkflow();
        var proj2 = DefaultProject.builder(wfm2).build();
        pm.addProject(proj2);
        appStateUpdater.updateAppState();
        verify(eventConsumer).accept("AppStateChangedEvent:ProjectDirtyStateEvent",
            buildExpectedCompositeEvent(proj1, proj2), null);

        // update a preference and verify resulting app state changed event
        when(preferencesProvider.hasNodeRecommendationsEnabled()).thenReturn(true);
        appStateUpdater.updateAppState();
        var appStateChangedEvent = builder(AppStateChangedEventEntBuilder.class)
            .setAppState(builder(AppStateEntBuilder.class).setHasNodeRecommendationsEnabled(Boolean.TRUE).build())
            .build();
        expectedEvent = builder(CompositeEventEntBuilder.class)
            .setEvents(List.of(appStateChangedEvent, buildProjectDirtyStateEvent(proj1, proj2))).build();
        verify(eventConsumer).accept("AppStateChangedEvent:ProjectDirtyStateEvent", expectedEvent, null);

        pm.removeProject(proj1.getID(), WorkflowManagerUtil::disposeWorkflow);
        pm.removeProject(proj2.getID(), WorkflowManagerUtil::disposeWorkflow);
    }

    /**
     * Tests the {@link AppStateUpdater} to trigger {@link AppStateChangedEventEnt app-state-changed-events}, but remove
     * any project-specific infos from the events in case the app-state-update is configured to do so.
     *
     * @throws InvalidRequestException
     * @throws IOException
     */
    @Test
    public void testAppStateUpdaterToFilterProjectSpecificInfosFromAppStateChangedEvent()
        throws InvalidRequestException, IOException {
        var eventConsumer = mock(EventConsumer.class);
        var appStateUpdater = new AppStateUpdater(true /* that's what we actually test here */);
        var preferencesProvider = mock(PreferencesProvider.class);
        when(preferencesProvider.hasNodeRecommendationsEnabled()).thenReturn(false);
        setupServiceDependencies(appStateUpdater, eventConsumer, preferencesProvider);

        var pm = ProjectManager.getInstance();
        var wfm1 = WorkflowManagerUtil.createEmptyWorkflow();
        var proj1 = DefaultProject.builder(wfm1).build();
        pm.addProject(proj1);

        DefaultApplicationService.getInstance().getState(); // initializes the app-state for the AppStateUpdater
        DefaultEventService.getInstance().addEventListener(builder(AppStateChangedEventTypeEntBuilder.class).build());
        // verify there is no initial event (because there is no project-specific info to be updated initially)
        verify(eventConsumer, times(0)).accept(any(), any(), any());
        verify(eventConsumer, times(0)).accept(any(), any());

        // update open projects and check resulting app state changed event (there should be none)
        var wfm2 = WorkflowManagerUtil.createEmptyWorkflow();
        var proj2 = DefaultProject.builder(wfm2).build();
        pm.addProject(proj2);
        appStateUpdater.updateAppState();
        verify(eventConsumer, times(0)).accept(any(), any(), any());
        verify(eventConsumer, times(0)).accept(any(), any());

        // update a preference and check resulting app state changed event
        when(preferencesProvider.hasNodeRecommendationsEnabled()).thenReturn(true);
        appStateUpdater.updateAppState();
        var appStateChangedEvent = builder(AppStateChangedEventEntBuilder.class)
            .setAppState(builder(AppStateEntBuilder.class).setHasNodeRecommendationsEnabled(Boolean.TRUE).build())
            .build();
        var expectedEvent =
            builder(CompositeEventEntBuilder.class).setEvents(asList(appStateChangedEvent, null)).build();
        verify(eventConsumer).accept("AppStateChangedEvent:ProjectDirtyStateEvent", expectedEvent, null);

        pm.removeProject(proj1.getID(), WorkflowManagerUtil::disposeWorkflow);
        pm.removeProject(proj2.getID(), WorkflowManagerUtil::disposeWorkflow);

    }

    private static CompositeEventEnt buildExpectedCompositeEvent(final Project... projects) {
        var appStateChangedEvent =
            builder(AppStateChangedEventEntBuilder.class).setAppState(builder(AppStateEntBuilder.class).setOpenProjects( //
                Arrays.stream(projects)
                    .map(p -> builder(ProjectEntBuilder.class).setProjectId(p.getID()).setName(p.getName()).build())
                    .toList() //
            ).build()).build();

        return builder(CompositeEventEntBuilder.class)
            .setEvents(List.of(appStateChangedEvent, buildProjectDirtyStateEvent(projects))).build();
    }

    private static ProjectDirtyStateEventEnt buildProjectDirtyStateEvent(final Project... projects) {
        return builder(ProjectDirtyStateEventEntBuilder.class).setDirtyProjectsMap( //
            Arrays.stream(projects).map(Project::getID).collect(Collectors.toMap(k -> k, k -> Boolean.TRUE)) //
        ).setShouldReplace(Boolean.TRUE).build();
    }

    @SuppressWarnings("javadoc")
    @After
    public void disposeServices() {
        ServiceInstances.disposeAllServiceInstancesAndDependencies();
    }

    private static void setupServiceDependencies(final AppStateUpdater appStateUpdater,
        final EventConsumer eventConsumer, final PreferencesProvider preferencesProvider) {
        ServiceDependencies.setServiceDependency(AppStateUpdater.class, appStateUpdater);
        final var projectManager = ProjectManager.getInstance();
        ServiceDependencies.setServiceDependency(WorkflowMiddleware.class,
            new WorkflowMiddleware(projectManager, null));
        ServiceDependencies.setServiceDependency(EventConsumer.class, eventConsumer);
        ServiceDependencies.setServiceDependency(ProjectManager.class, projectManager);
        ServiceDependencies.setServiceDependency(PreferencesProvider.class, preferencesProvider);
        ServiceDependencies.setServiceDependency(SpaceProviders.class, mock(SpaceProviders.class));
    }

}
