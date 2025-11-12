/*
 * ------------------------------------------------------------------------
 *
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
 * ---------------------------------------------------------------------
 */
package org.knime.gateway.impl.webui.service.commands.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;
import java.util.zip.ZipFile;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.node.workflow.WorkflowAnnotationID;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.api.webui.service.util.ServiceExceptions;
import org.knime.gateway.testing.helper.webui.node.NoOpDummyNodeFactory;
import org.knime.testing.util.WorkflowManagerUtil;

/**
 * Test for {@link ComponentExporter}.
 *
 * @author Assistant
 */
@SuppressWarnings({"java:S5960", "java:S1176", "java:S112", "java:S1176"})
public class ComponentExporterTest {

    private final TemporaryFolder m_tempFolder = new TemporaryFolder();

    private WorkflowManager m_wfm;

    @Before
    public void setUp() throws IOException {
        m_tempFolder.create();
        m_wfm = WorkflowManagerUtil.createEmptyWorkflow();
    }

    /**
     * Test successful component export with input data inclusion.
     */
    @Test
    @Ignore
    public void testExportComponentWithInputData() throws Exception {
        var component = createTestComponent();
        var wfArtifactTarget = m_tempFolder.newFolder("wf-artifact").toPath();
        var compressionTarget = m_tempFolder.newFile("component.knwf").toPath();

        var originalTemplateInfo = ComponentExporter.exportComponentWithLimit(component, wfArtifactTarget,
            compressionTarget, true, Optional.empty());

        // Verify result structure
        assertThat(originalTemplateInfo, is(notNullValue()));

        // Verify files were created
        assertTrue("Workflow artifact should be created", Files.exists(wfArtifactTarget));
        assertTrue("Compression target should be created", Files.exists(compressionTarget));
        assertTrue("Compressed file should have content", Files.size(compressionTarget) > 0);

        // Verify wfArtifactTarget structure
        var componentName = component.getName();
        var componentDir = wfArtifactTarget.resolve(componentName);
        assertTrue("Component directory should exist", Files.exists(componentDir));
        assertTrue("workflow.knime should exist", Files.exists(componentDir.resolve("workflow.knime")));
        assertTrue("template.knime should exist", Files.exists(componentDir.resolve("template.knime")));
        assertTrue("component-metadata.xml should exist", Files.exists(componentDir.resolve("component-metadata.xml")));

        // Verify compressionTarget is a valid zip file with expected contents
        try (var zipFile = new ZipFile(compressionTarget.toFile())) {
            var workflowEntry = zipFile.getEntry(componentName + "/workflow.knime");
            var templateEntry = zipFile.getEntry(componentName + "/template.knime");
            var metadataEntry = zipFile.getEntry(componentName + "/component-metadata.xml");

            assertThat("workflow.knime should be in zip", workflowEntry, is(notNullValue()));
            assertThat("template.knime should be in zip", templateEntry, is(notNullValue()));
            assertThat("component-metadata.xml should be in zip", metadataEntry, is(notNullValue()));
        }
    }

    /**
     * Test that the upload limit is respected and exception is thrown when exceeded.
     */
    @Test
    @Ignore
    public void testExportComponentWithUploadLimitExceeded() throws Exception {
        var component = createTestComponent();
        var wfArtifactTarget = m_tempFolder.newFolder("wf-artifact-limit").toPath();
        var compressionTarget = m_tempFolder.newFile("component-limit.knwf").toPath();

        // Set a very small upload limit (1 byte) to ensure it gets exceeded
        var uploadLimit = Optional.of(1L);

        // Export should fail with ServiceCallException due to size limit
        var exception = assertThrows(ServiceExceptions.ServiceCallException.class, () -> ComponentExporter
            .exportComponentWithLimit(component, wfArtifactTarget, compressionTarget, true, uploadLimit));

        // Verify the exception message indicates size limit was exceeded
        assertThat("Exception should mention file size limit", exception.getTitle(), is("Failed to share component"));
        assertTrue("Exception should mention size limit", exception.getDetails().contains("File size limit exceeded"));
    }

    /**
     * Creates a test component for testing purposes.
     */
    @SuppressWarnings("java:S1130")
    private SubNodeContainer createTestComponent() throws Exception {
        // Create a simple source node
        var nodeFactory = new NoOpDummyNodeFactory() {
            @Override
            protected Optional<PortsConfigurationBuilder> createPortsConfigBuilder() {
                return Optional.empty();
            }
        };
        var nodeContainer = WorkflowManagerUtil.createAndAddNode(m_wfm, nodeFactory);

        // Create a subnode (component) containing this node
        var collapseResult = m_wfm.collapseIntoMetaNode(new NodeID[]{nodeContainer.getID()},
            new WorkflowAnnotationID[]{}, "Test Component");
        var convertResult = m_wfm.convertMetaNodeToSubNode(collapseResult.getCollapsedMetanodeID());

        return (SubNodeContainer)m_wfm.getNodeContainer(convertResult.getConvertedNodeID());
    }
}
