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
 *   Mar 7, 2025 (kai): created
 */
package org.knime.gateway.impl.project;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.api.util.VersionId;

/**
 * Test class for {@link Project}.
 *
 * @author Kai Franze, KNIME GmbH, Germany
 */
public class ProjectTest {

    private final WorkflowManager m_wfm = mock(WorkflowManager.class);

    private final WorkflowManager m_wfmv1 = mock(WorkflowManager.class);

    private final WorkflowManager m_wfmv2 = mock(WorkflowManager.class);

    private final WorkflowManagerLoader m_wfmLoader = version -> {
        var wfms = Map.of( //
            VersionId.currentState(), m_wfm, //
            VersionId.parse("1"), m_wfmv1, //
            VersionId.parse("2"), m_wfmv2 //
        );
        return wfms.get(version);
    };

    @SuppressWarnings("javadoc")
    @Before
    public void setUp() {
        when(m_wfm.getName()).thenReturn("Test project");
        when(m_wfmv1.getName()).thenReturn("Test project version 1");
        when(m_wfmv2.getName()).thenReturn("Test project version 2");
    }

    @SuppressWarnings("javadoc")
    @After
    public void tearDown() {
        reset(m_wfm);
        reset(m_wfmv1);
        reset(m_wfmv2);
    }

    /**
     * Test the builder with required properties.
     *
     * @throws Exception
     */
    @Test
    public void testBuilderWithRequiredProperties() throws Exception {
        var project1 = Project.builder() //
            .setWfm(m_wfm) //
            .build();
        assertBaseMethodsWork(project1);

        var project2 = Project.builder() //
            .setWfmLoaderProvidingOnlyCurrentState(() -> m_wfm) //
            .setName("Test project") //
            .setId("Custom project ID") //
            .build();
        assertBaseMethodsWork(project2);
    }

    /***
     * Test the builder with all properties.
     *
     * @throws Exception
     */
    @Test
    public void testBuilderWithAllProperties() throws Exception {
        var origin = new Origin("provider id", "space id", "item id");
        var project = Project.builder() //
            .setWfm(m_wfm) //
            .setName("Test project") //
            .setId("Custom project ID") //
            .setOrigin(origin) //
            .clearReport(() -> {
            }) //
            .generateReport(input -> null) //
            .build();

        assertBaseMethodsWork(project);
        assertThat(project.getOrigin().get(), is(origin));
        project.clearReport(); // No exception thrown
        project.generateReport("Hello world"); // No exception thrown
    }

    /***
     * Test the builder with optional properties set to null.
     *
     * @throws Exception
     */
    @Test
    public void testBuilderWithOptionalPropertiesNull() throws Exception {
        var project = Project.builder() //
            .setWfm(m_wfm) //
            .setName("Test project") //
            .setId("Custom project ID") //
            .setOrigin(null) //
            .clearReport(null) //
            .generateReport(null) //
            .build();

        assertBaseMethodsWork(project);
    }

    /**
     * Test the builder throws exception on missing required properties.
     */
    @Test
    public void testBuilderThrows() {
        assertThrows(NullPointerException.class, () -> Project.builder().setWfm(null).build());
        assertThrows(NullPointerException.class, () -> Project.builder()
            .setWfmLoaderProvidingOnlyCurrentState(() -> m_wfm).setName(null).setId("Custom project id").build());
        assertThrows(NullPointerException.class, () -> Project.builder()
            .setWfmLoaderProvidingOnlyCurrentState(() -> m_wfm).setName("Test project").setId(null).build());
    }

    /**
     *
     * @throws Exception
     */
    @Test
    public void testGetAndLoadWorkflowManagerWithVersions() throws Exception {
        var project = Project.builder() //
            .setWfmLoader(m_wfmLoader) //
            .setName("Test project") //
            .setId("Custom project ID") //
            .build();

        // Project
        assertThat(project.getWorkflowManagerIfLoaded(), is(Optional.empty()));
        assertThat(project.getFromCacheOrLoadWorkflowManager(), is(Optional.of(m_wfm)));
        assertThat(project.getWorkflowManagerIfLoaded(), is(Optional.of(m_wfm)));

        // Project version 1
        var version1 = VersionId.parse("1");
        assertThat(project.getWorkflowManagerIfLoaded(version1), is(Optional.empty()));
        assertThat(project.getFromCacheOrLoadWorkflowManager(version1), is(Optional.of(m_wfmv1)));
        assertThat(project.getWorkflowManagerIfLoaded(version1), is(Optional.of(m_wfmv1)));

        // Project version 2 is immutable
        var version2 = VersionId.parse("2");
        assertThat(project.getWorkflowManagerIfLoaded(version2), is(Optional.empty()));
        assertThat(project.getFromCacheOrLoadWorkflowManager(version2), is(Optional.of(m_wfmv2)));
        assertThat(project.getWorkflowManagerIfLoaded(version2), is(Optional.of(m_wfmv2)));
    }

    private void assertBaseMethodsWork(final Project project) throws Exception {
        assertThat(project.getName(), is("Test project"));
        assertThat(project.getID(), not(isEmptyOrNullString()));
        assertThat(project.getFromCacheOrLoadWorkflowManager(), is(Optional.of(m_wfm)));
        assertThat(project.getWorkflowManagerIfLoaded(), is(Optional.of(m_wfm)));
        assertThat(project.getOrigin(), isA(Optional.class));
        project.dispose(); // No exception thrown
        assertThat(project.hashCode(), isA(Integer.class));
        //noinspection EqualsWithItself
        assertThat(project.equals(project), is(true));
        assertThat(project.equals(null), is(false));
    }

}
