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
 *   May 11, 2021 (hornm): created
 */
package org.knime.gateway.impl.webui.service.commands;

import java.io.IOException;
import java.util.NoSuchElementException;

import org.knime.core.node.workflow.NodeID;
import org.knime.gateway.api.webui.entity.AddNodeCommandEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.OperationNotAllowedException;
import org.knime.gateway.api.webui.util.EntityBuilderUtil;
import org.knime.gateway.impl.service.util.DefaultServiceUtil;

/**
 * Workflow command to add a native node.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
final class AddNode extends AbstractWorkflowCommand {

    private NodeID m_addedNode;

    private AddNodeCommandEnt m_commandEnt;

    AddNode(final AddNodeCommandEnt commandEnt) {
        m_commandEnt = commandEnt;
    }

    @Override
    protected boolean executeWithLockedWorkflow() throws OperationNotAllowedException {
        var wfm = getWorkflowManager();
        var positionEnt = m_commandEnt.getPosition();
        var factoryKeyEnt = m_commandEnt.getNodeFactory();
        var targetPosition = new int[]{positionEnt.getX(), positionEnt.getY()};
        try {
            m_addedNode = DefaultServiceUtil.createAndAddNode(factoryKeyEnt.getClassName(), factoryKeyEnt.getSettings(),
                targetPosition[0], targetPosition[1] - EntityBuilderUtil.NODE_Y_POS_CORRECTION, wfm, false);
        } catch (IOException | NoSuchElementException e) {
            throw new OperationNotAllowedException(e.getMessage(), e);
        }
        return true;
    }

    @Override
    public boolean canUndo() {
        return getWorkflowManager().canRemoveNode(m_addedNode);
    }

    @Override
    public void undo() throws OperationNotAllowedException {
        getWorkflowManager().removeNode(m_addedNode);
        m_addedNode = null;
    }

}
