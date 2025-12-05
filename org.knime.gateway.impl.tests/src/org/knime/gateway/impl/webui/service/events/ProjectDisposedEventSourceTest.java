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
 *   Feb 23, 2024 (hornm): created
 */
package org.knime.gateway.impl.webui.service.events;

import static org.knime.gateway.api.entity.EntityBuilderManager.builder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.knime.gateway.api.webui.entity.ProjectDisposedEventEnt.ProjectDisposedEventEntBuilder;
import org.knime.gateway.api.webui.entity.ProjectDisposedEventTypeEnt.ProjectDisposedEventTypeEntBuilder;
import org.knime.gateway.impl.project.Project;
import org.knime.gateway.impl.project.ProjectManager;
import org.knime.gateway.impl.project.WorkflowManagerLoader;
import org.knime.gateway.impl.webui.PreferencesProvider;
import org.knime.gateway.impl.webui.WorkflowMiddleware;
import org.knime.gateway.impl.webui.repo.NodeCollections;
import org.knime.gateway.impl.webui.service.DefaultEventService;
import org.knime.gateway.impl.webui.service.ServiceDependencies;
import org.knime.gateway.impl.webui.service.ServiceInstances;
import org.knime.gateway.impl.webui.spaces.SpaceProvidersManager;
import org.knime.gateway.impl.webui.syncing.WorkflowSyncerProvider;

/**
 * Tests the {@link ProjectDisposedEventSource} (via the {@link DefaultEventService}).
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@SuppressWarnings("java:S1192") // repeated string literals
public class ProjectDisposedEventSourceTest {

    @SuppressWarnings("javadoc")
    @Test
    public void testProjectDisposedEventSource() throws Exception {
        var projectManager = ProjectManager.getInstance();
        var project = Project.builder() //
            .setWfmLoader(WorkflowManagerLoader.providingOnlyCurrentState(() -> null)) //
            .setName("test name") //
            .setId("test id") //
            .build();
        projectManager.addProject(project);

        // set service dependencies
        var eventConsumer = mock(EventConsumer.class);
        var spaceProvidersManager = new SpaceProvidersManager(s -> {
        }, null);
        var preferenceProvider = mock(PreferencesProvider.class);
        var nodeCollections = mock(NodeCollections.class);
        ServiceInstances.disposeAllServiceInstancesAndDependencies();
        ServiceDependencies.setDefaultServiceDependencies( //
            projectManager, //
            new WorkflowMiddleware(projectManager, null), //
            null, //
            eventConsumer, //
            spaceProvidersManager, //
            null, //
            null, //
            preferenceProvider, //
            null, //
            null, //
            null, //
            nodeCollections, //
            null, //
            null, //
            null,
            WorkflowSyncerProvider.disabled() //
        );

        // register event listener
        DefaultEventService.getInstance()
            .addEventListener(builder(ProjectDisposedEventTypeEntBuilder.class).setProjectId("test id").build());
        DefaultEventService.getInstance()
            .addEventListener(builder(ProjectDisposedEventTypeEntBuilder.class).setProjectId("test id 2").build());
        projectManager.removeProject("test id");

        // the actual check
        verify(eventConsumer).accept("ProjectDisposedEvent",
            builder(ProjectDisposedEventEntBuilder.class).setProjectId("test id").build(), "test id");
        verify(eventConsumer, never()).accept("ProjectDisposedEvent",
            builder(ProjectDisposedEventEntBuilder.class).setProjectId("test id 2").build(), "test id 2");

        // clean-up
        ServiceInstances.disposeAllServiceInstancesAndDependencies();
    }

}
