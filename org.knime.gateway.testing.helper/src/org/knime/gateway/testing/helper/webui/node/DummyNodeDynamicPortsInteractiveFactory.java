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
 *   Nov 28, 2022 (kai): created
 */
package org.knime.gateway.testing.helper.webui.node;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ConfigurableNodeFactory;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NodeView;
import org.knime.core.node.context.NodeCreationConfiguration;
import org.knime.core.node.port.PortObjectSpec;

/**
 * Configurable dummy node with interactive and non-interactive dynamic ports
 *
 * @author Kai Franze, KNIME GmbH
 */
public class DummyNodeDynamicPortsInteractiveFactory extends ConfigurableNodeFactory<NodeModel> {

    static final String INPUT_PORT_GROUP = "Inputs";

    static final String OUTPUT_PORT_GROUP = "Outputs";

    @Override
    protected Optional<PortsConfigurationBuilder> createPortsConfigBuilder() {
        var builder = new PortsConfigurationBuilder();
        builder.addExtendableInputPortGroup(INPUT_PORT_GROUP, BufferedDataTable.TYPE);
        builder.addNonInteractiveExtendableOutputPortGroup(OUTPUT_PORT_GROUP,
            portType -> portType.equals(BufferedDataTable.TYPE));
        return Optional.of(builder);
    }

    @Override
    protected NodeModel createNodeModel(final NodeCreationConfiguration creationConfig) {
        final var config = creationConfig.getPortConfig().orElseThrow();
        return new NodeModel(config.getInputPorts(), config.getOutputPorts()) {
            @Override
            protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs) throws InvalidSettingsException {
                return null;
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

    @Override
    protected NodeDialogPane createNodeDialogPane(final NodeCreationConfiguration creationConfig) {
        return null;
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

}
