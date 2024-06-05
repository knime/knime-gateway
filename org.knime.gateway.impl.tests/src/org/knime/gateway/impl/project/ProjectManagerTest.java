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
 *   Nov 21, 2023 (hornm): created
 */
package org.knime.gateway.impl.project;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.api.webui.entity.SpaceItemReferenceEnt.ProjectTypeEnum;
import org.knime.gateway.impl.project.Project.Origin;
import org.knime.gateway.impl.project.ProjectManager.ProjectConsumerType;

/**
 * Test {@link ProjectManager}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class ProjectManagerTest {

    @SuppressWarnings("javadoc")
    @After
    @Before
    public void clearProjectManager() {
        ProjectManager.getInstance().clearState();
    }

    /**
     * Tests the create, read, update and delete operations on the project manager.
     */
    @Test
    public void testCRUD() {
        var wfm = WorkflowManager.ROOT;
        var pm = ProjectManager.getInstance();

        var proj1 = DefaultProject.builder(wfm).build();
        var proj1a = DefaultProject.builder(wfm).setId(proj1.getID()).build();
        var proj1b = DefaultProject.builder(wfm).setId(proj1.getID()).setName("proj1b").build();
        var proj2 = DefaultProject.builder(wfm).build();

        // add projects
        pm.addProject(proj1);
        assertThat(pm.getProject(proj1.getID()).get().getName(), is(proj1.getName()));
        assertThat(pm.getCachedProject(proj1.getID()).get(), sameInstance(wfm));
        pm.addProject(proj1a, ProjectConsumerType.WORKFLOW_SERVICE, false);
        pm.addProject(proj2);

        // get project-ids
        assertThat(pm.getProjectIds(), is(pm.getProjectIds(ProjectConsumerType.UI)));
        assertThat(pm.getProjectIds(), is(List.of(proj1.getID(), proj2.getID())));
        assertThat(pm.getProjectIds(ProjectConsumerType.WORKFLOW_SERVICE), is(List.of(proj1a.getID())));

        // replace project
        pm.addProject(proj1b);
        assertThat(pm.getProject(proj1.getID()).get().getName(), is("proj1b"));
        pm.addProject(proj1, ProjectConsumerType.UI, false);
        assertThat(pm.getProject(proj1.getID()).get().getName(), is("proj1b"));

        // remove project
        pm.removeProject(proj1.getID(), w -> {}); // removes the ui-consumer
        assertThat(pm.getProjectIds(), is(List.of(proj2.getID())));
        assertThat(pm.getProjectIds(ProjectConsumerType.WORKFLOW_SERVICE), is(List.of(proj1a.getID())));
        pm.removeProject(proj1a.getID(), ProjectConsumerType.WORKFLOW_SERVICE, w -> {
        });
        assertThat(pm.getProjectIds(), is(List.of(proj2.getID())));
        assertThat(pm.getProjectIds(ProjectConsumerType.WORKFLOW_SERVICE), is(List.of()));
    }


    /**
     * Tests {@link ProjectManager#getLocalProject(java.nio.file.Path)}.
     */
    @Test
    public void testGetLocalProject() {
        var wfm = WorkflowManager.ROOT;
        var pm = ProjectManager.getInstance();
        var proj1 = DefaultProject.builder(wfm).setId("1").build();
        var proj2 =
            DefaultProject.builder(wfm).setId("2").setOrigin(createOrigin("relative/path/to/file2", "local")).build();
        var proj3 = DefaultProject.builder(wfm).setId("3")
            .setOrigin(createOrigin("relative/path/to/file3", "non-local")).build();
        pm.addProject(proj1);
        pm.addProject(proj2);
        pm.addProject(proj3);

        var projId = pm.getLocalProject(Path.of("relative/path/to/file2")).orElse(null);
        assertThat(projId, is("2"));
        projId = pm.getLocalProject(Path.of("relative/path/to/file3")).orElse(null);
        assertThat(projId, is(nullValue()));
    }

    private static Origin createOrigin(final String relPath, final String providerId) {
        return new Origin() {

            @Override
            public Optional<String> getRelativePath() {
                return Optional.ofNullable(relPath);
            }

            @Override
            public String getSpaceId() {
                return null;
            }

            @Override
            public String getProviderId() {
                return providerId;
            }

            @Override
            public Optional<ProjectTypeEnum> getProjectType() {
                return Optional.empty();
            }

            @Override
            public String getItemId() {
                return null;
            }
        };
    }

}
