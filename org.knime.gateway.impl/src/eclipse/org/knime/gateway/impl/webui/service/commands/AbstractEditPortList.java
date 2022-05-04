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

import org.knime.core.node.workflow.NodeID;
import org.knime.gateway.api.webui.entity.AddPortCommandEnt;
import org.knime.gateway.api.webui.entity.PortCommandEnt;
import org.knime.gateway.api.webui.entity.RemovePortCommandEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions;

/**
 * Basic structure for workflow commands that modify node ports.
 *
 * @author Benjamin Moser, KNIME GmbH, Konstanz, Germany
 */
public abstract class AbstractEditPortList extends AbstractWorkflowCommand {

    private final PortCommandEnt m_portCommandEnt;

    AbstractEditPortList(final PortCommandEnt portCommandEnt) {
        m_portCommandEnt = portCommandEnt;
    }

    /**
     * @return The command entity describing this command.
     */
    protected PortCommandEnt getPortCommandEnt() {
        return m_portCommandEnt;
    }

    /**
     * @return The ID of the node to edit ports of.
     */
    protected NodeID getNodeId() {
        return getPortCommandEnt().getNodeId()
            .toNodeID(NodeID.ROOTID.createChild(getWorkflowManager().getProjectWFM().getID().getIndex()));
    }

    @Override
    protected boolean executeWithLockedWorkflow() throws ServiceExceptions.OperationNotAllowedException {
        var portCommandEnt = getPortCommandEnt();
        if (portCommandEnt instanceof AddPortCommandEnt) {
            addPort((AddPortCommandEnt)portCommandEnt);
            return true;
        } else if (portCommandEnt instanceof RemovePortCommandEnt) {
            removePort((RemovePortCommandEnt)portCommandEnt);
            return true;
        } else {
            throw new ServiceExceptions.OperationNotAllowedException("Unknown port operation");
        }
    }

    /**
     * Add a port to the node
     * @param addPortCommandEnt The parameters of the command.
     */
    protected abstract void addPort(AddPortCommandEnt addPortCommandEnt);

    /**
     * Remove a port from the node
     * @param removePortCommandEnt The parameters of the command
     * @throws ServiceExceptions.OperationNotAllowedException If the operation can not be executed
     */
    protected abstract void removePort(RemovePortCommandEnt removePortCommandEnt)
        throws ServiceExceptions.OperationNotAllowedException;

}
