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
package org.knime.gateway.impl.webui.service.commands;

import org.knime.core.node.NodeLogger;
import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.api.webui.entity.AutoConnectCommandEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.ServiceCallException;
import org.knime.gateway.impl.webui.service.commands.util.AutoDisConnectUtil;

/**
 * Connect selected workflow elements according to an automatically determined plan.
 *
 * @author Benjamin Moser, KNIME GmbH, Germany
 * @author Kai Franze, KNIME GmbH, Germany
 */
public final class AutoConnect extends AbstractWorkflowCommand {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(AutoConnect.class);

    private final AutoConnectCommandEnt m_commandEnt;

    private AutoDisConnectUtil.AutoConnectChanges m_autoConnectChanges;

    AutoConnect(final AutoConnectCommandEnt commandEnt) {
        m_commandEnt = commandEnt;
    }

    @Override
    protected boolean executeWithLockedWorkflow() throws ServiceCallException {
        var changes = AutoDisConnectUtil.autoConnect( //
            getWorkflowManager(), //
            m_commandEnt //
        );
        LOGGER.info("%s connections were added and %s connections were removed"
            .formatted(changes.addedConnections().size(), changes.removedConnections().size()));
        m_autoConnectChanges = changes;
        return !changes.addedConnections().isEmpty();
    }

    @Override
    public boolean canUndo() {
        if (m_autoConnectChanges == null) {
            return false;
        }
        return CoreUtil.canAddConnections(m_autoConnectChanges.removedConnections(), getWorkflowManager())
            && CoreUtil.canRemoveConnections(m_autoConnectChanges.addedConnections(), getWorkflowManager());
    }

    @Override
    public boolean canRedo() {
        if (m_autoConnectChanges == null) {
            return false;
        }
        return CoreUtil.canAddConnections(m_autoConnectChanges.addedConnections(), getWorkflowManager())
            && CoreUtil.canRemoveConnections(m_autoConnectChanges.removedConnections(), getWorkflowManager());
    }

    @Override
    public void undo() throws ServiceCallException {
        final var wfm = getWorkflowManager();
        m_autoConnectChanges.addedConnections().forEach(wfm::removeConnection);
        m_autoConnectChanges.removedConnections().forEach(cc -> {
            try {
                wfm.addConnection(cc.getSource(), cc.getSourcePort(), cc.getDest(), cc.getDestPort());
            } catch (IllegalArgumentException e) {
                LOGGER.info("Could not re-add connection on undo: " + e.getMessage());
            }
        });
        m_autoConnectChanges = null;
    }

}
