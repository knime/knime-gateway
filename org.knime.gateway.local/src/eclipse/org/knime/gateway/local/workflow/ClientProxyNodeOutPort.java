/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME GmbH, Konstanz, Germany
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
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
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
 *   Nov 9, 2016 (hornm): created
 */
package org.knime.gateway.local.workflow;

import org.knime.core.def.node.port.PortTypeUID;
import org.knime.core.def.node.workflow.INodeOutPort;
import org.knime.core.def.node.workflow.NodeContainerState;
import org.knime.core.def.node.workflow.NodeStateChangeListener;
import org.knime.core.def.node.workflow.NodeStateEvent;
import org.knime.gateway.v0.workflow.entity.NodeOutPortEnt;
import org.knime.gateway.v0.workflow.entity.PortTypeEnt;

/**
 *
 * @author Martin Horn, University of Konstanz
 */
public class ClientProxyNodeOutPort implements INodeOutPort {

    private NodeOutPortEnt m_outPort;

    /**
     *
     */
    public ClientProxyNodeOutPort(final NodeOutPortEnt outPort) {
        m_outPort = outPort;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPortIndex() {
        return m_outPort.getPortIndex();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PortTypeUID getPortTypeUID() {
        PortTypeEnt pte = m_outPort.getPortType();
        return PortTypeUID.builder(pte.getPortObjectClassName()).setName(pte.getName()).setColor(pte.getColor())
            .setIsHidden(pte.getIsHidden()).setIsOptional(pte.getIsOptional()).build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPortName() {
        return m_outPort.getPortName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPortName(final String portName) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stateChanged(final NodeStateEvent state) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeContainerState getNodeContainerState() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addNodeStateChangeListener(final NodeStateChangeListener listener) {
        // TODO
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeNodeStateChangeListener(final NodeStateChangeListener listener) {
        //TODO
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPortSummary() {
        return "TODO port summary";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isInactive() {
        //TODO
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeContainerState getNodeState() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notifyNodeStateChangeListener(final NodeStateEvent e) {
        throw new UnsupportedOperationException();
    }

}
