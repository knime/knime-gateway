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
package org.knime.gateway.impl.webui.service.commands;

import org.knime.core.node.context.ModifiableNodeCreationConfiguration;
import org.knime.core.node.context.ports.ExtendablePortGroup;
import org.knime.core.node.workflow.NativeNodeContainer;
import org.knime.core.node.workflow.action.ReplaceNodeResult;
import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.api.webui.entity.AddPortCommandEnt;
import org.knime.gateway.api.webui.entity.PortCommandEnt;
import org.knime.gateway.api.webui.entity.RemovePortCommandEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions;

/**
 *
 * Implementations for modifying ports on a native node.
 *
 * @author Benjamin Moser, KNIME GmbH, Konstanz, Germany
 */
final class EditNativeNodePorts extends AbstractEditPorts {

    private ReplaceNodeResult m_replaceNodeResult;

    EditNativeNodePorts(final PortCommandEnt portCommandEnt) {
        super(portCommandEnt);
    }

    @Override
    protected void addPort(final AddPortCommandEnt addPortCommandEnt) {
        var newPortType = CoreUtil.getPortType(addPortCommandEnt.getPortTypeId())
            .orElseThrow(() -> new UnsupportedOperationException("Unknown port type"));
        var groupName = getPortCommandEnt().getPortGroup();
        var creationConfigCopy = getCopyOfCreationConfig();
        getExtendablePortGroup(creationConfigCopy, groupName).addPort(newPortType);
        executeInternal(creationConfigCopy);
    }

    @Override
    protected void removePort(final RemovePortCommandEnt removePortCommandEnt)
        throws ServiceExceptions.OperationNotAllowedException {
        var creationConfigCopy = getCopyOfCreationConfig();
        var groupName = getPortCommandEnt().getPortGroup();
        getExtendablePortGroup(creationConfigCopy, groupName).removeLastPort();
        executeInternal(creationConfigCopy);
    }

    @Override
    public void undo() throws ServiceExceptions.OperationNotAllowedException {
        m_replaceNodeResult.undo();
    }

    @Override
    public boolean canUndo() {
        return m_replaceNodeResult.canUndo();
    }

    private void executeInternal(final ModifiableNodeCreationConfiguration creationConfigCopy) {
        m_replaceNodeResult = getWorkflowManager().replaceNode(getNodeId(), creationConfigCopy);
    }

    private ModifiableNodeCreationConfiguration getCopyOfCreationConfig() {
        var nnc = getWorkflowManager().getNodeContainer(getNodeId(), NativeNodeContainer.class, true);
        return nnc.getNode().getCopyOfCreationConfig().orElseThrow();
    }

    private static ExtendablePortGroup getExtendablePortGroup(final ModifiableNodeCreationConfiguration creationConfig,
        final String groupName) {
        var portsConfig = creationConfig.getPortConfig().orElseThrow();
        return (ExtendablePortGroup)portsConfig.getGroup(groupName);
    }
}
