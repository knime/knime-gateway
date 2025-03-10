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
 *   Sep 28, 2022 (Kai Franze, KNIME GmbH): created
 */
package org.knime.gateway.impl.webui.service.commands;

import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.api.webui.entity.PortCommandEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.ServiceCallException;

/**
 * Abstract port command
 *
 * @author Kai Franze, KNIME GmbH
 * @param <T> The port command entity the command implementation is expecting
 */
abstract class AbstractPortCommand<T extends PortCommandEnt> extends AbstractWorkflowCommand {

    private final T m_portCommandEnt;

    private EditPorts m_editor;

    AbstractPortCommand(final T portCommandEnt) {
        m_portCommandEnt = portCommandEnt;
    }

    @Override
    public boolean canUndo() {
        return m_editor.canUndo();
    }

    @Override
    public void undo() throws ServiceCallException {
        m_editor.undo();
    }

    @Override
    protected abstract boolean executeWithLockedWorkflow() throws ServiceCallException;

    /**
     * Determines whether to edit the ports of a native or a container node and instantiates the editor accordingly.
     *
     * @return The port editor
     */
    EditPorts instantiatePortEditor() {
        var wfm = getWorkflowManager();
        var nodeId = m_portCommandEnt.getNodeId().toNodeID(wfm);
        var nodeIsContainer = CoreUtil.getContainerType(nodeId, wfm).isPresent();
        m_editor = nodeIsContainer ? new EditContainerNodePorts(wfm, m_portCommandEnt)
            : new EditNativeNodePorts(wfm, m_portCommandEnt);
        return m_editor;
    }

    /**
     * @return Port command entity
     */
    T getPortCommandEnt() {
        return m_portCommandEnt;
    }

}
