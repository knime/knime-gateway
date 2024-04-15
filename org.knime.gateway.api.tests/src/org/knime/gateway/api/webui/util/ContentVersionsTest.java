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
package org.knime.gateway.api.webui.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.IntCell;
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
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.NodeOutPort;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.testing.util.WorkflowManagerUtil;

/**
 * @author Benjamin Moser, KNIME GmbH, Konstanz, Germany
 */

@SuppressWarnings({"unchecked", "java:S1186", "java:S3010", "java:S112"})
class ContentVersionsTest {

    private WorkflowManager m_wfm;

    @BeforeEach
    void createEmptyWorkflow() throws IOException {
        m_wfm = WorkflowManagerUtil.createEmptyWorkflow();
    }

    @AfterEach
    void disposeWorkflow() {
        WorkflowManagerUtil.disposeWorkflow(m_wfm);
    }

    @Test
    void portContentVersionDoesNotChangeIfNothingHappens() throws Exception {
        var factory = getFactory(new IncrementingPortOutput());
        var node = WorkflowManagerUtil.createAndAddNode(m_wfm, factory);
        getDataOutPorts(node).forEach(nodeOutPort -> {
            var firstContentVersion = ContentVersions.getPortContentVersion(nodeOutPort);
            var secondContentVersion = ContentVersions.getPortContentVersion(nodeOutPort);
            assertThat(firstContentVersion).isEqualTo(secondContentVersion);
        });
    }

    @Test
    void portContentVersionChangedIfSpecChanged() throws Exception {
        var node = WorkflowManagerUtil.createAndAddNode(m_wfm, getFactory(new IncrementingPortOutput()));
        var contentVersionsAtLoad = getDataOutPorts(node).stream().map(ContentVersions::getPortContentVersion).toList();
        m_wfm.resetAndConfigureAll();
        var contentVersionsAtConfigure =
            getDataOutPorts(node).stream().map(ContentVersions::getPortContentVersion).toList();
        assertContentVersionsChanged(contentVersionsAtLoad, contentVersionsAtConfigure);
    }

    @Test
    void portContentVersionChangesIfDataChanges() throws Exception {
        // execute, assert content version has changed
        var node = WorkflowManagerUtil.createAndAddNode(m_wfm, getFactory(new IncrementingPortOutput()));
        m_wfm.resetAndConfigureAll();
        m_wfm.executeAll();
        waitUntilExecuted(m_wfm);
        var contentVersionsAtFirstExecute =
            getDataOutPorts(node).stream().map(ContentVersions::getPortContentVersion).toList();
        m_wfm.resetAndConfigureAll();
        m_wfm.executeAll();
        waitUntilExecuted(m_wfm);
        var contentVersionsAtSecondExecute =
            getDataOutPorts(node).stream().map(ContentVersions::getPortContentVersion).toList();
        assertContentVersionsChanged(contentVersionsAtFirstExecute, contentVersionsAtSecondExecute);
    }

    @Test
    void inputContentVersionDoesNotChangeIfNothingHappens() throws Exception {
        var sourceNode = WorkflowManagerUtil.createAndAddNode(m_wfm, getFactory(new IncrementingPortOutput()));
        var sinkNode = WorkflowManagerUtil.createAndAddNode(m_wfm, getFactory(1, new IncrementingPortOutput()));
        connect(m_wfm, sourceNode.getID(), 1, sinkNode.getID(), 1);
        var firstContentVersion = ContentVersions.getInputContentVersion(sinkNode);
        var secondContentVersion = ContentVersions.getInputContentVersion(sinkNode);
        assertThat(firstContentVersion).isEqualTo(secondContentVersion);
    }

    private static void connect(final WorkflowManager wfm, final NodeID source, final int sourcePort,
        final NodeID destination, final int destinationPort) {
        assert wfm.canAddConnection(source, sourcePort, destination, destinationPort);
        wfm.addConnection(source, sourcePort, destination, destinationPort);
    }

    @Test
    void inputContentVersionChangesOnPredecessorExecute() throws Exception {
        var sourceNode = WorkflowManagerUtil.createAndAddNode(m_wfm, getFactory(new IncrementingPortOutput()));
        var sinkNode = WorkflowManagerUtil.createAndAddNode(m_wfm, getFactory(1, new IncrementingPortOutput()));
        connect(m_wfm, sourceNode.getID(), 1, sinkNode.getID(), 1);
        var inputVersionBeforeExecute = ContentVersions.getInputContentVersion(sinkNode);
        m_wfm.executeAll();
        waitUntilExecuted(m_wfm);
        var inputVersionAfterExecute = ContentVersions.getInputContentVersion(sinkNode);
        assertThat(inputVersionBeforeExecute).isNotEqualTo(inputVersionAfterExecute);
    }

    private static void waitUntilExecuted(final NodeContainer nc) {
        Awaitility.await().atMost(5, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS)
            .untilAsserted(() -> nc.getNodeContainerState().isExecuted());
    }

    private static void assertContentVersionsChanged(final List<Integer> previousContentVersionsOfPorts,
        final List<Integer> nextContentVersionsOfPorts) {
        zip(previousContentVersionsOfPorts, nextContentVersionsOfPorts)
            .forEach(contentVersionsOfPort -> assertThat(new HashSet<>(contentVersionsOfPort).size())
                .isEqualTo(contentVersionsOfPort.size()));
    }

    private static <T> List<List<T>> zip(final List<T>... lists) {
        var length = Arrays.stream(lists).map(List::size).mapToInt(i -> i).min().orElse(0);
        return IntStream.range(0, length).mapToObj(index -> Arrays.stream(lists).map(l -> l.get(index)).toList())
            .toList();
    }

    private static List<NodeOutPort> getDataOutPorts(final NodeContainer nc) {
        return IntStream.range(1, nc.getNrOutPorts()).mapToObj(nc::getOutPort).toList();
    }

    private interface IntPortOutput {

        int outputValue();

        default BufferedDataTable getTable(final BufferedDataTable[] nodeInputs, final ExecutionContext exec) {
            var spec = getSpec();
            var container = exec.createDataContainer((DataTableSpec)spec);
            container.addRowToTable(new DefaultRow("rowid", new IntCell(outputValue())));
            container.close();
            return container.getTable();
        }

        default PortObjectSpec getSpec() {
            var integerColumn = new DataColumnSpecCreator("name", IntCell.TYPE).createSpec();
            return new DataTableSpec(integerColumn);
        }
    }

    private static class IncrementingPortOutput implements IntPortOutput {

        private static int value;

        IncrementingPortOutput() {
            value = 0;
        }

        @Override
        public int outputValue() {
            value = value + 1; // NOSONAR
            return value;
        }
    }

    private static NodeFactory<NodeModel> getFactory(final IntPortOutput... portOutputs) {
        return getFactory(0, portOutputs);
    }

    private static NodeFactory<NodeModel> getFactory(final int nrDataInPorts, final IntPortOutput... portOutputs) {
        return new NodeFactory<>() {
            @Override
            public NodeModel createNodeModel() {
                return getNodeModel(nrDataInPorts, portOutputs);
            }

            @Override
            protected int getNrNodeViews() {
                return 0;
            }

            @Override
            public NodeView<NodeModel> createNodeView(final int viewIndex, final NodeModel nodeModel) {
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
        };
    }

    private static NodeModel getNodeModel(final int nrDataInPorts, final IntPortOutput... portOutputs) {
        return new NodeModel(nrDataInPorts, portOutputs.length) {
            @Override
            protected BufferedDataTable[] execute(final BufferedDataTable[] inData, final ExecutionContext exec)
                throws Exception {
                return Arrays.stream(portOutputs) //
                    .map(portOutput -> portOutput.getTable(inData, exec)) //
                    .toArray(BufferedDataTable[]::new);
            }

            @Override
            protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs) throws InvalidSettingsException {
                return Arrays.stream(portOutputs) //
                    .map(IntPortOutput::getSpec) //
                    .toArray(PortObjectSpec[]::new);
            }

            @Override
            protected void loadInternals(final File nodeInternDir, final ExecutionMonitor exec)
                throws IOException, CanceledExecutionException {

            }

            @Override
            protected void saveInternals(final File nodeInternDir, final ExecutionMonitor exec)
                throws IOException, CanceledExecutionException {

            }

            @Override
            protected void saveSettingsTo(final NodeSettingsWO settings) {

            }

            @Override
            protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {

            }

            @Override
            protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {

            }

            @Override
            protected void reset() {

            }
        };
    }
}
