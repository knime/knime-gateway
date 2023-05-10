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
 *   Jan 20, 2021 (hornm): created
 */
package org.knime.gateway.impl.webui.service.commands;

import java.util.Set;

import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeUIInformation;
import org.knime.core.node.workflow.WorkflowAnnotation;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.api.webui.entity.TranslateCommandEnt;
import org.knime.gateway.api.webui.entity.XYEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.OperationNotAllowedException;

/**
 * Workflow command to translate (i.e. change the position) of nodes and workflow annotations.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
final class Translate extends AbstractPartBasedWorkflowCommand {

    private int[] m_delta;

    Translate(final TranslateCommandEnt commandEnt) {
        super(commandEnt);
        XYEnt translationEnt = commandEnt.getTranslation();
        m_delta = new int[]{translationEnt.getX(), translationEnt.getY()};
    }

    @Override
    public boolean executeWithLockedWorkflow() throws OperationNotAllowedException {
        if (m_delta[0] == 0 && m_delta[1] == 0) {
            return false;
        }
        performTranslation(getWorkflowManager(), getNodeContainers(), getAnnotations(), m_delta);
        return true;
    }

    @Override
    public void undo() throws OperationNotAllowedException {
        performTranslation(getWorkflowManager(), getNodeContainers(), getAnnotations(), invert(m_delta));
    }

    // TODO: NXT-1169 Enable translation of connection bend points too
    static void performTranslation(final WorkflowManager wfm, final Set<NodeContainer> nodes,
        final Set<WorkflowAnnotation> annotations, final int[] delta) {

        for (NodeContainer nc : nodes) {
            NodeUIInformation.moveNodeBy(nc, delta);
        }
        for (WorkflowAnnotation wa : annotations) {
            wa.shiftPosition(delta[0], delta[1]);
        }

        for (NodeContainer nc : nodes) {
            nc.setDirty(); // will propagate upwards
        }
        if (!annotations.isEmpty()) {
            wfm.setDirty();
        }
    }

    private static int[] invert(final int[] source) {
        return new int[]{-1 * source[0], -1 * source[1]};
    }

}
