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
 *   Mar 16, 2023 (hornm): created
 */
package org.knime.gateway.impl.webui.service.commands;

import org.knime.core.node.workflow.WorkflowAnnotationID;
import org.knime.gateway.api.webui.entity.TransformWorkflowAnnotationCommandEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.OperationNotAllowedException;
import org.knime.gateway.impl.service.util.DefaultServiceUtil;

/**
 * Changes the size and position of a workflow annotation.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
class TransformWorkflowAnnotation extends AbstractWorkflowCommand {

    private final TransformWorkflowAnnotationCommandEnt m_commandEnt;

    private WorkflowAnnotationID m_annotationId;

    private int[] m_previousBounds;

    TransformWorkflowAnnotation(final TransformWorkflowAnnotationCommandEnt commandEnt) {
        m_commandEnt = commandEnt;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean executeWithLockedWorkflow() throws OperationNotAllowedException {
        var projectId = getWorkflowKey().getProjectId();
        m_annotationId = DefaultServiceUtil.entityToAnnotationID(projectId, m_commandEnt.getAnnotationId());
        var annotation = DefaultServiceUtil.getWorkflowAnnotationOrThrowException(projectId, m_annotationId);
        m_previousBounds =
            new int[]{annotation.getX(), annotation.getY(), annotation.getWidth(), annotation.getHeight()};
        var bounds = m_commandEnt.getBounds();
        annotation.setDimension(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void undo() throws OperationNotAllowedException {
        var projectId = getWorkflowKey().getProjectId();
        var annotation = DefaultServiceUtil.getWorkflowAnnotationOrThrowException(projectId, m_annotationId);
        annotation.setDimension(m_previousBounds[0], m_previousBounds[1], m_previousBounds[2], m_previousBounds[3]);
        m_previousBounds = null;
        m_annotationId = null;
    }
}
