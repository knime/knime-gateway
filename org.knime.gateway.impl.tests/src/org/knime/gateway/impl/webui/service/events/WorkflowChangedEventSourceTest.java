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
 *   Apr 3, 2024 (hornm): created
 */
package org.knime.gateway.impl.webui.service.events;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.knime.gateway.api.entity.EntityBuilderManager.builder;
import static org.mockito.Mockito.mock;

import java.io.IOException;

import org.junit.Test;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.WorkflowChangedEventTypeEnt.WorkflowChangedEventTypeEntBuilder;
import org.knime.gateway.api.webui.util.WorkflowBuildContext;
import org.knime.gateway.impl.project.DefaultProject;
import org.knime.gateway.impl.project.ProjectManager;
import org.knime.gateway.impl.webui.WorkflowKey;
import org.knime.gateway.impl.webui.WorkflowMiddleware;
import org.knime.testing.util.WorkflowManagerUtil;

/**
 * Tests aspects of the {@link WorkflowChangedEventSource}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class WorkflowChangedEventSourceTest {

    /**
     * Tests that workflow changed listeners are removed when the respective project is removed from the
     * {@link ProjectManager}.
     *
     * @throws IOException
     */
    @Test
    public void testRemoveListenerWhenProjectIsRemoved() throws IOException {
        var projectManager = ProjectManager.getInstance();
        var wfm = WorkflowManagerUtil.createEmptyWorkflow();
        projectManager.addProject(DefaultProject.builder(wfm).setId("id1").build());
        projectManager.addProject(DefaultProject.builder(wfm).setId("id2").build());

        // create event source
        var workflowMiddleware = new WorkflowMiddleware(projectManager, null);
        var eventSource = new WorkflowChangedEventSource(mock(EventConsumer.class), workflowMiddleware, projectManager);

        // add event listeners
        var snapshotId1 = workflowMiddleware.buildWorkflowSnapshotEnt(new WorkflowKey("id1", NodeIDEnt.getRootID()),
            () -> WorkflowBuildContext.builder()).getSnapshotId();
        eventSource.addEventListenerAndGetInitialEventFor(builder(WorkflowChangedEventTypeEntBuilder.class)
            .setProjectId("id1").setWorkflowId(NodeIDEnt.getRootID()).setSnapshotId(snapshotId1).build(), null);
        var snapshotId2 = workflowMiddleware.buildWorkflowSnapshotEnt(new WorkflowKey("id2", NodeIDEnt.getRootID()),
            () -> WorkflowBuildContext.builder()).getSnapshotId();
        eventSource.addEventListenerAndGetInitialEventFor(builder(WorkflowChangedEventTypeEntBuilder.class)
            .setProjectId("id2").setWorkflowId(NodeIDEnt.getRootID()).setSnapshotId(snapshotId2).build(), null);

        // check
        assertThat(eventSource.getNumRegisteredListeners(), is(2));
        projectManager.removeProject("id1", w -> {
        });
        assertThat(eventSource.getNumRegisteredListeners(), is(1));
        projectManager.removeProject("id2", w -> {
        });
        assertThat(eventSource.getNumRegisteredListeners(), is(0));

        // clean-up
        WorkflowManagerUtil.disposeWorkflow(wfm);
    }

}
