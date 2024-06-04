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
 */
package org.knime.gateway.api.webui.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.knime.core.data.DataTableSpec;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NodeView;
import org.knime.core.node.workflow.ComponentMetadata;
import org.knime.core.node.workflow.MetaNodeTemplateInformation;
import org.knime.core.node.workflow.NodeContainerMetadata;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.node.workflow.WorkflowAnnotationID;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.api.webui.entity.ComponentNodeAndDescriptionEnt;
import org.knime.testing.util.WorkflowManagerUtil;

@SuppressWarnings({"javadoc", "java:S5960"})
public class WorkflowMonitorStateEntityFactoryTest {
    private WorkflowManager m_wfm;

    private static final String FAILURE_MESSAGE_PREFIX = "Execute failed: ";

    @BeforeEach
    void createEmptyWorkflow() throws IOException {
        m_wfm = WorkflowManagerUtil.createEmptyWorkflow();
    }

    @AfterEach
    void disposeWorkflow() {
        WorkflowManagerUtil.disposeWorkflow(m_wfm);
    }

    @Test
    public void messageReportedForFailingNode() {
        m_wfm.createAndAddNode(failingNativeNode());
        m_wfm.executeAllAndWaitUntilDone();
        var result = EntityFactory.WorkflowMonitorState.buildWorkflowMonitorStateEnt(m_wfm);
        assertThat(result.getWarnings()).isEmpty();
        assertThat(result.getErrors()).hasSize(1);
    }

    @Test
    public void noMessageForSucceedingNode() {
        m_wfm.createAndAddNode(succeedingNativeNode());
        m_wfm.executeAllAndWaitUntilDone();
        var result = EntityFactory.WorkflowMonitorState.buildWorkflowMonitorStateEnt(m_wfm);
        assertThat(result.getWarnings()).hasSize(0);
        assertThat(result.getErrors()).hasSize(0);
    }

    @Test
    public void messageFieldsForNativeNode() {
        var expectedMessage = "expected failure message";
        var failingNodeId = m_wfm.createAndAddNode(failingNativeNode(expectedMessage));
        m_wfm.executeAllAndWaitUntilDone();
        var result = EntityFactory.WorkflowMonitorState.buildWorkflowMonitorStateEnt(m_wfm);
        var error = result.getErrors().get(0);
        assertThat(error.getTemplateId()).isNotNull();
        assertThat(error.getComponentInfo()).isNull();
        assertThat(error.getWorkflowId().toNodeID(m_wfm)).isEqualTo(m_wfm.getID());
        assertThat(error.getNodeId().toNodeID(m_wfm)).isEqualTo(failingNodeId);
        assertThat(error.getMessage()).isEqualTo(FAILURE_MESSAGE_PREFIX + expectedMessage);
    }

    /**
     * We expect no message for the component, but a message for the failing node within.
     */
    @Test
    public void messageFieldsForComponent() {
        var expectedMessage = "expected failure message";
        var failingNodeId = m_wfm.createAndAddNode(failingNativeNode(expectedMessage));
        var containerName = "component name";
        var snc = wrapInComponent(containerName, failingNodeId);
        m_wfm.executeAllAndWaitUntilDone();
        var result = EntityFactory.WorkflowMonitorState.buildWorkflowMonitorStateEnt(m_wfm);
        assertThat(result.getErrors()).hasSize(1);
        var error = result.getErrors().get(0);
        assertThat(error.getNodeId().toNodeID(m_wfm)).isNotEqualTo(snc.getID());
        assertThat(error.getTemplateId()).isNotNull();
        assertThat(error.getComponentInfo()).isNull();
        assertThat(error.getWorkflowId().toNodeID(m_wfm)).isEqualTo(snc.getID());
        assertThat(error.getMessage()).isEqualTo(FAILURE_MESSAGE_PREFIX + expectedMessage);
        assertThat(result.getWarnings()).hasSize(0);

    }

    /**
     * For a linked component, we expect a message for the component node, but not for any of the nodes within. Further,
     * warnings of internal nodes are not surfaced.
     */
    @Test
    public void componentMessagesOnlyForLinkedComponent() throws URISyntaxException {
        var failingNodeId = m_wfm.createAndAddNode(failingNativeNode());
        var containerName = "component name";
        var expectedType = ComponentMetadata.ComponentNodeType.LEARNER;
        var snc = wrapInComponent(containerName, expectedType, failingNodeId);
        linkComponent(snc, new URI(""));
        m_wfm.executeAllAndWaitUntilDone();
        var result = EntityFactory.WorkflowMonitorState.buildWorkflowMonitorStateEnt(m_wfm);
        assertThat(result.getErrors()).hasSize(1);
        var error = result.getErrors().get(0);
        assertThat(error.getNodeId().toNodeID(m_wfm)).isEqualTo(snc.getID());
        assertThat(error.getTemplateId()).isNull();
        assertThat(error.getComponentInfo()).isNotNull();
        assertThat(result.getWarnings()).isEmpty();
        var componentInfo = error.getComponentInfo();
        assertThat(componentInfo.getName()).isEqualTo(containerName);
        assertThat(componentInfo.getType())
            .isEqualTo(ComponentNodeAndDescriptionEnt.TypeEnum.valueOf(expectedType.name()));
        assertThat(componentInfo.getIcon()).isNotNull();
    }

    @Test
    public void warningInComponentIsReported() {
        var warningNodeId = m_wfm.createAndAddNode(warningNativeNode());
        var snc = wrapInComponent(warningNodeId);
        m_wfm.executeAllAndWaitUntilDone();
        var result = EntityFactory.WorkflowMonitorState.buildWorkflowMonitorStateEnt(m_wfm);
        assertThat(result.getErrors()).isEmpty();
        assertThat(result.getWarnings()).hasSize(1);
        assertThat(result.getWarnings().get(0).getNodeId().toNodeID(m_wfm)).isNotEqualTo(snc.getID());
    }

    private static void linkComponent(final SubNodeContainer snc, final URI uri) {
        var templateInfo = MetaNodeTemplateInformation.createNewTemplate(SubNodeContainer.class).createLink(uri);
        snc.setTemplateInformation(templateInfo);
    }

    private SubNodeContainer wrapInComponent(final NodeID nodeId) {
        return wrapInComponent("Container", nodeId);
    }

    private SubNodeContainer wrapInComponent(final String containerName, final NodeID nodeId) {
        return wrapInComponent(containerName, ComponentMetadata.ComponentNodeType.OTHER, nodeId);
    }

    private SubNodeContainer wrapInComponent(final String containerName, final ComponentMetadata.ComponentNodeType type,
        final NodeID nodeId) {

        var collapseResult =
            m_wfm.collapseIntoMetaNode(new NodeID[]{nodeId}, new WorkflowAnnotationID[]{}, containerName);

        var conversionResult = m_wfm.convertMetaNodeToSubNode(collapseResult.getCollapsedMetanodeID());
        var snc = m_wfm.getNodeContainer(conversionResult.getConvertedNodeID(), SubNodeContainer.class, true);

        var expectedIcon = new byte[]{0};
        snc.setMetadata(ComponentMetadata.fluentBuilder() //
            .withComponentType(type) //
            .withIcon(expectedIcon) //
            .withContentType(NodeContainerMetadata.ContentType.PLAIN) //
            .withLastModifiedNow() //
            .withDescription("") //
            .build());
        return snc;
    }

    private static NodeFactory<NodeModel> succeedingNativeNode() {
        return new NoOpNodeFactory<>() {
            @Override
            public NodeModel createNodeModel() {
                return new NoOpNodeModel(0, 0) {
                    @Override
                    protected BufferedDataTable[] execute(final BufferedDataTable[] inData, final ExecutionContext exec)
                        throws Exception {
                        return inData;
                    }
                };
            }
        };
    }

    private static NodeFactory<NodeModel> warningNativeNode() {
        return new NoOpNodeFactory<>() {
            @Override
            public NodeModel createNodeModel() {
                return new NoOpNodeModel(0, 0) {
                    @Override
                    protected BufferedDataTable[] execute(final BufferedDataTable[] inData, final ExecutionContext exec)
                        throws Exception {
                        this.setWarningMessage("This warning is set on each execution");
                        return inData;
                    }
                };
            }
        };
    }

    private static NodeFactory<NodeModel> failingNativeNode() {
        return failingNativeNode("This node fails on each execution");
    }

    private static NodeFactory<NodeModel> failingNativeNode(final String message) {
        return new NoOpNodeFactory<>() {
            @Override
            public NodeModel createNodeModel() {
                return new NoOpNodeModel(0, 0) {
                    @Override
                    protected BufferedDataTable[] execute(final BufferedDataTable[] inData, final ExecutionContext exec)
                        throws Exception {
                        throw new Exception(message); // NOSONAR
                    }
                };
            }
        };
    }

    private abstract static class NoOpNodeFactory<T extends NodeModel> extends NodeFactory<T> {

        @Override
        protected int getNrNodeViews() {
            return 0;
        }

        @Override
        public NodeView<T> createNodeView(final int viewIndex, final T nodeModel) {
            return null;
        }

        @Override
        protected boolean hasDialog() {
            return false;
        }

        @Override
        protected NodeDialogPane createNodeDialogPane() {
            return null;
        }
    }

    private static class NoOpNodeModel extends NodeModel {

        protected NoOpNodeModel(final int nrInDataPorts, final int nrOutDataPorts) {
            super(nrInDataPorts, nrOutDataPorts);
        }

        @Override
        protected void loadInternals(final File nodeInternDir, final ExecutionMonitor exec)
            throws IOException, CanceledExecutionException {
            //
        }

        @Override
        protected void saveInternals(final File nodeInternDir, final ExecutionMonitor exec)
            throws IOException, CanceledExecutionException {
            //
        }

        @Override
        protected void saveSettingsTo(final NodeSettingsWO settings) {
            //
        }

        @Override
        protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
            //
        }

        @Override
        protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
            //
        }

        @Override
        protected DataTableSpec[] configure(final DataTableSpec[] inSpecs) throws InvalidSettingsException {
            return inSpecs;
        }

        @Override
        protected void reset() {
            //
        }
    }
}
