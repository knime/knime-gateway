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
 *   Nov 7, 2016 (hornm): created
 */
package org.knime.gateway.local.workflow;

import org.knime.core.ui.node.workflow.NodeInPortUI;
import org.knime.core.ui.node.workflow.NodeOutPortUI;
import org.knime.core.ui.node.workflow.WorkflowInPortUI;
import org.knime.core.ui.node.workflow.WorkflowManagerUI;
import org.knime.core.ui.node.workflow.WorkflowOutPortUI;
import org.knime.gateway.v0.entity.WorkflowEnt;
import org.knime.gateway.v0.entity.WrappedWorkflowNodeEnt;

/**
 * {@link WorkflowManagerUI} implementation that wraps (and therewith retrieves its information) from a
 * {@link WrappedWorkflowNodeEnt} most likely received remotely.
 *
 * @author Martin Horn, University of Konstanz
 */
public final class EntityProxyWrappedWorkflowManager
    extends AbstractEntityProxyWorkflowManager<WrappedWorkflowNodeEnt> {

    /**
     * @param wrappedWorkflowNodeEnt
     * @param access
     */
    EntityProxyWrappedWorkflowManager(final WrappedWorkflowNodeEnt wrappedWorkflowNodeEnt,
        final EntityProxyAccess access) {
        super(wrappedWorkflowNodeEnt, access);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected WorkflowEnt getWorkflowEnt() {
        return getAccess().getWorkflowEnt(getEntity());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorkflowInPortUI getInPort(final int index) {
        //get underlying port
        return getAccess().getWorkflowInPort(getEntity().getInPorts().get(index), null, getEntity());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorkflowOutPortUI getOutPort(final int index) {
        return getAccess().getWorkflowOutPort(getEntity().getOutPorts().get(index), getEntity());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNrWorkflowIncomingPorts() {
        return getEntity().getWorkflowIncomingPorts().size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNrWorkflowOutgoingPorts() {
        return getEntity().getWorkflowOutgoingPorts().size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeOutPortUI getWorkflowIncomingPort(final int i) {
        return getAccess().getNodeOutPort(getEntity().getWorkflowIncomingPorts().get(i), getEntity());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeInPortUI getWorkflowOutgoingPort(final int i) {
        return getAccess().getNodeInPort(getEntity().getWorkflowOutgoingPorts().get(i));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEncrypted() {
        return getEntity().isEncrypted();
    }
}
