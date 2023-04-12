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
 *   Apr 5, 2023 (kai): created
 */
package org.knime.gateway.impl.webui.service.commands;

import java.util.Collections;
import java.util.Objects;

import org.knime.core.node.workflow.AnnotationData;
import org.knime.core.node.workflow.AnnotationData.ContentType;
import org.knime.core.node.workflow.AnnotationData.TextAlignment;
import org.knime.core.node.workflow.WorkflowAnnotationID;
import org.knime.gateway.api.webui.entity.UpdateWorkflowAnnotationTextCommandEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.OperationNotAllowedException;
import org.knime.gateway.impl.service.util.DefaultServiceUtil;

/**
 * Updates a workflow annotation's text with formatted text.
 *
 * @author Kai Franze, KNIME GmbH
 */
final class UpdateWorkflowAnnotationText extends AbstractWorkflowCommand {

    private final UpdateWorkflowAnnotationTextCommandEnt m_commandEnt;

    private WorkflowAnnotationID m_annotationId;

    private AnnotationData m_previousAnnotationData;

    UpdateWorkflowAnnotationText(final UpdateWorkflowAnnotationTextCommandEnt commandEnt) {
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
        m_previousAnnotationData = annotation.getData().clone();

        var newAnnotationData = new AnnotationData();
        newAnnotationData.copyFrom(m_previousAnnotationData, true);
        var text = m_commandEnt.getText();

        if (Objects.equals(m_previousAnnotationData.getText(), text)) {
            return false;
        }

        newAnnotationData.setText(text);
        newAnnotationData.setContentType(ContentType.TEXT_HTML);
        newAnnotationData.setStyleRanges(Collections.emptyList()); // No style ranges are back-ported to Classic UI
        newAnnotationData.setAlignment(TextAlignment.LEFT); // Set the default alignment for Classic UI
        annotation.copyFrom(newAnnotationData, true);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void undo() throws OperationNotAllowedException {
        var projectId = getWorkflowKey().getProjectId();
        var annotation = DefaultServiceUtil.getWorkflowAnnotationOrThrowException(projectId, m_annotationId);
        annotation.copyFrom(m_previousAnnotationData, true);
        m_previousAnnotationData = null;
        m_annotationId = null;
    }
}
