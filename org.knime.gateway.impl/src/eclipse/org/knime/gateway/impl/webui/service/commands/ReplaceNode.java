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
 *   Mar 11, 2023 (hornm): created
 */
package org.knime.gateway.impl.webui.service.commands;

import static org.knime.gateway.impl.service.util.DefaultServiceUtil.entityToNodeID;

import java.io.IOException;
import java.util.NoSuchElementException;

import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.action.ReplaceNodeResult;
import org.knime.gateway.api.webui.entity.ReplaceNodeCommandEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.OperationNotAllowedException;
import org.knime.gateway.impl.service.util.DefaultServiceUtil;

/**
 * Workflow command to replace a node.
 *
 * @author Juan Baquero
 */
final class ReplaceNode extends AbstractWorkflowCommand {

    private NodeID m_deletedNode;

    private ReplaceNodeResult m_result;

    ReplaceNodeCommandEnt m_commandEnt;

    ReplaceNode(final ReplaceNodeCommandEnt commandEnt) {
        m_commandEnt = commandEnt;
    }

    @Override
    protected boolean executeWithLockedWorkflow() throws OperationNotAllowedException {
        var wfm = getWorkflowManager();
        m_deletedNode = entityToNodeID(getWorkflowKey().getProjectId(), m_commandEnt.getNodeId());

        var nodeFactoryEnt = m_commandEnt.getNodeFactory();
        try {
            var nodeFactory = DefaultServiceUtil.getNodeFactory(nodeFactoryEnt.getClassName(), nodeFactoryEnt.getSettings());
            m_result = wfm.replaceNode(m_deletedNode, null, nodeFactory);
        } catch (NoSuchElementException ex) {
            throw new OperationNotAllowedException(ex.getMessage(), ex);
        } catch (IOException ex) {
            throw new OperationNotAllowedException(ex.getMessage(), ex);
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canUndo() {
        return m_result.canUndo();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void undo() throws OperationNotAllowedException {
        m_result.undo();
    }

}