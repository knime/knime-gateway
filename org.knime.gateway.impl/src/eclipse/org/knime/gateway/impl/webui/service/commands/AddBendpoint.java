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

import static org.knime.gateway.api.entity.EntityBuilderManager.builder;

import java.util.Collections;
import java.util.Set;

import org.knime.core.node.workflow.ConnectionContainer;
import org.knime.core.node.workflow.ConnectionUIInformation;
import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.api.webui.entity.AddBendpointCommandEnt;
import org.knime.gateway.api.webui.entity.CommandResultEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.ServiceCallException;
import org.knime.gateway.impl.service.util.DefaultServiceUtil;
import org.knime.gateway.impl.service.util.WorkflowChangesTracker;

/**
 * Workflow command to add bendpoints to an existing connection.
 *
 * @author Benjamin Moser, KNIME GmbH, Konstanz, Germany
 */
class AddBendpoint extends AbstractWorkflowCommand implements WithResult {

    private final AddBendpointCommandEnt m_commandEnt;

    private ConnectionUIInformation m_originalConnectionUiInfo;

    private ConnectionContainer m_connection;

    protected AddBendpoint(final AddBendpointCommandEnt commandEnt) {
        m_commandEnt = commandEnt;
    }

    @Override
    protected boolean executeWithWorkflowLockAndContext() throws ServiceCallException {

        var wfm = getWorkflowManager();
        var connectionId =
            DefaultServiceUtil.entityToConnectionID(getWorkflowKey().getProjectId(), m_commandEnt.getConnectionId());
        m_connection = CoreUtil.getConnection(connectionId, wfm) //
            .orElseThrow(() -> ServiceCallException.builder() //
                .withTitle("Failed to add bendpoint") //
                .withDetails("Connection not found: " + connectionId) //
                .canCopy(false) //
                .build());
        var connectionUIInfo = m_connection.getUIInfo();
        var nBendpoints = connectionUIInfo == null ? 0 : connectionUIInfo.getAllBendpoints().length;

        if (connectionUIInfo == null) {
            m_originalConnectionUiInfo = null;
        } else {
            m_originalConnectionUiInfo = ConnectionUIInformation.builder().copyFrom(connectionUIInfo).build();
        }

        var index = clip(m_commandEnt.getIndex().intValue(), 0, nBendpoints);
        var position = m_commandEnt.getPosition();
        CoreUtil.insertBendpoint(m_connection, index, position.getX(), position.getY());

        wfm.setDirty();
        return true;
    }

    @Override
    public CommandResultEnt buildEntity(final String snapshotId) {
        return builder(CommandResultEnt.CommandResultEntBuilder.class).setSnapshotId(snapshotId).build();
    }

    @Override
    public Set<WorkflowChangesTracker.WorkflowChange> getChangesToWaitFor() {
        return Collections.singleton(WorkflowChangesTracker.WorkflowChange.BENDPOINTS_MODIFIED);
    }

    @Override
    public void undo() throws ServiceExceptions.ServiceCallException {
        m_connection.setUIInfo(m_originalConnectionUiInfo);
    }

    private static int clip(final int value, final int min, final int max) {
        return Math.max(min, Math.min(value, max));
    }
}
