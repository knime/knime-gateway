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
package org.knime.gateway.impl.webui.service.commands.util;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ConfigurableNodeFactory;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NodeView;
import org.knime.core.node.context.NodeCreationConfiguration;
import org.knime.core.node.context.ports.ExchangeablePortGroup;
import org.knime.core.node.context.ports.ExtendablePortGroup;
import org.knime.core.node.context.ports.PortsConfiguration;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.flowvariable.FlowVariablePortObject;
import org.knime.core.node.port.image.ImagePortObject;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.testing.util.WorkflowManagerUtil;

@SuppressWarnings("javadoc")
public class MatchingPortsUtilTest {

    private static final String OPTIONAL_GROUP = "optional";

    private WorkflowManager m_wfm;

    @Before
    public void initWorkflow() throws IOException {
        m_wfm = WorkflowManagerUtil.createEmptyWorkflow();
    }

    @Test
    public void testExistingCompatiblePortIsUsed() throws Exception {
        var source = createSourceNode(PortObject.TYPE);
        var destination = createOptionalInputNode(PortObject.TYPE, PortObject.TYPE, BufferedDataTable.TYPE);

        var matching = MatchingPortsUtil.getMatchingPorts(source, destination, 1, null, m_wfm);
        destination = (NodeContainer)m_wfm.getNodeContainer(destination.getID());

        assertEquals(1, matching.get(1).intValue());
        assertThat(destination.getInPort(1).getPortType(), is(PortObject.TYPE));
    }

    @Test
    public void testNoCompatiblePortReturnsMinusOne() throws Exception {
        var source = createSourceNode(BufferedDataTable.TYPE);
        var destination = createOptionalInputNode(null, FlowVariablePortObject.TYPE);

        var matching = MatchingPortsUtil.getMatchingPorts(source, destination, 1, null, m_wfm);

        assertEquals(-1, matching.get(1).intValue());
    }

    @Test
    public void testExtendableInputPrefersFirstSupportedType() throws Exception {
        var source = createSourceNode(ImagePortObject.TYPE);
        var destination = createExtendableInputNode(PortObject.TYPE, ImagePortObject.TYPE);

        var matching = MatchingPortsUtil.getMatchingPorts(source, destination, 1, null, m_wfm);
        var destPortIdx = matching.get(1);
        destination = (NodeContainer)m_wfm.getNodeContainer(destination.getID());

        assertEquals(1, destPortIdx.intValue());
        assertThat(destination.getInPort(destPortIdx).getPortType(), is(PortObject.TYPE));
    }

    @Test
    public void testExchangeableInputPrefersSelectedType() throws Exception {
        var exchangeableGroup = mock(ExchangeablePortGroup.class);
        when(exchangeableGroup.getSelectedPortType()).thenReturn(BufferedDataTable.TYPE);

        var preferred = MatchingPortsUtil.getPreferredCompatiblePortType(exchangeableGroup, BufferedDataTable.TYPE);

        assertThat(preferred, is(Optional.of(BufferedDataTable.TYPE)));
    }

    @Test
    public void testExtendableInputPrefersConfiguredType() throws Exception {
        var extendableGroup = mock(ExtendablePortGroup.class);
        when(extendableGroup.getConfiguredPorts()).thenReturn(new PortType[]{ImagePortObject.TYPE});
        when(extendableGroup.getSupportedPortTypes()).thenReturn(new PortType[]{PortObject.TYPE, ImagePortObject.TYPE});

        var preferred = MatchingPortsUtil.getPreferredCompatiblePortType(extendableGroup, ImagePortObject.TYPE);

        assertThat(preferred, is(Optional.of(ImagePortObject.TYPE)));
    }

    private NodeContainer createSourceNode(final PortType outputType) throws Exception {
        var factory = new FixedPortsNodeFactory(new PortType[0], new PortType[]{outputType});
        return WorkflowManagerUtil.createAndAddNode(m_wfm, factory);
    }

    private NodeContainer createOptionalInputNode(final PortType defaultType, final PortType... supportedTypes)
        throws Exception {
        var factory = new OptionalInputNodeFactory(defaultType, supportedTypes);
        return WorkflowManagerUtil.createAndAddNode(m_wfm, factory);
    }

    private NodeContainer createExtendableInputNode(final PortType... supportedTypes) throws Exception {
        var factory = new ExtendableInputNodeFactory(supportedTypes);
        return WorkflowManagerUtil.createAndAddNode(m_wfm, factory);
    }

    private static final class FixedPortsNodeFactory extends NodeFactory<SimpleNodeModel> {

        private final PortType[] m_inPorts;

        private final PortType[] m_outPorts;

        private FixedPortsNodeFactory(final PortType[] inPorts, final PortType[] outPorts) {
            m_inPorts = inPorts;
            m_outPorts = outPorts;
        }

        @Override
        public SimpleNodeModel createNodeModel() {
            return new SimpleNodeModel(m_inPorts, m_outPorts);
        }

        @Override
        protected int getNrNodeViews() {
            return 0;
        }

        @Override
        public NodeView<SimpleNodeModel> createNodeView(final int viewIndex, final SimpleNodeModel nodeModel) {
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

    private static final class OptionalInputNodeFactory extends ConfigurableNodeFactory<SimpleNodeModel> {

        private final PortType[] m_supportedTypes;

        private final PortType m_defaultType;

        private OptionalInputNodeFactory(final PortType defaultType, final PortType... supportedTypes) {
            super(false);
            m_supportedTypes = supportedTypes;
            m_defaultType = defaultType;
        }

        @Override
        protected Optional<PortsConfigurationBuilder> createPortsConfigBuilder() {
            var builder = new PortsConfigurationBuilder();
            if (m_defaultType == null) {
                builder.addOptionalInputPortGroup(OPTIONAL_GROUP, m_supportedTypes);
            } else {
                builder.addOptionalInputPortGroupWithDefault(OPTIONAL_GROUP, m_defaultType, m_supportedTypes);
            }
            return Optional.of(builder);
        }

        @Override
        protected SimpleNodeModel createNodeModel(final NodeCreationConfiguration creationConfig) {
            var inPorts = creationConfig.getPortConfig().map(PortsConfiguration::getInputPorts).orElse(new PortType[0]);
            var outPorts = new PortType[0];
            return new SimpleNodeModel(inPorts, outPorts);
        }

        @Override
        protected NodeDialogPane createNodeDialogPane(final NodeCreationConfiguration creationConfig) {
            return null;
        }

        @Override
        protected int getNrNodeViews() {
            return 0;
        }

        @Override
        protected boolean hasDialog() {
            return false;
        }

        @Override
        public NodeView<SimpleNodeModel> createNodeView(final int viewIndex, final SimpleNodeModel nodeModel) {
            return null;
        }
    }

    private static final class ExtendableInputNodeFactory extends ConfigurableNodeFactory<SimpleNodeModel> {

        private final PortType[] m_supportedTypes;

        private ExtendableInputNodeFactory(final PortType... supportedTypes) {
            super(false);
            m_supportedTypes = supportedTypes;
        }

        @Override
        protected Optional<PortsConfigurationBuilder> createPortsConfigBuilder() {
            var builder = new PortsConfigurationBuilder();
            builder.addExtendableInputPortGroup(OPTIONAL_GROUP, m_supportedTypes);
            return Optional.of(builder);
        }

        @Override
        protected SimpleNodeModel createNodeModel(final NodeCreationConfiguration creationConfig) {
            var inPorts = creationConfig.getPortConfig().map(PortsConfiguration::getInputPorts).orElse(new PortType[0]);
            var outPorts = new PortType[0];
            return new SimpleNodeModel(inPorts, outPorts);
        }

        @Override
        protected NodeDialogPane createNodeDialogPane(final NodeCreationConfiguration creationConfig) {
            return null;
        }

        @Override
        protected int getNrNodeViews() {
            return 0;
        }

        @Override
        protected boolean hasDialog() {
            return false;
        }

        @Override
        public NodeView<SimpleNodeModel> createNodeView(final int viewIndex, final SimpleNodeModel nodeModel) {
            return null;
        }
    }

    private static final class SimpleNodeModel extends NodeModel {

        private SimpleNodeModel(final PortType[] inPortTypes, final PortType[] outPortTypes) {
            super(inPortTypes, outPortTypes);
        }

        @Override
        protected PortObject[] execute(final PortObject[] inData, final ExecutionContext exec) {
            return new PortObject[0];
        }

        @Override
        protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs) {
            return new PortObjectSpec[0];
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
    }
}
