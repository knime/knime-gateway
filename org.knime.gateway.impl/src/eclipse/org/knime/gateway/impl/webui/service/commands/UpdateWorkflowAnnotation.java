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
 *   May 15, 2023 (kai): created
 */
package org.knime.gateway.impl.webui.service.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.knime.core.node.workflow.AnnotationData;
import org.knime.core.node.workflow.WorkflowAnnotation;
import org.knime.gateway.api.webui.entity.UpdateWorkflowAnnotationCommandEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.OperationNotAllowedException;

/**
 * Updates the text and/or the border color of a workflow annotation.
 *
 * @author Kai Franze, KNIME GmbH
 */
final class UpdateWorkflowAnnotation extends AbstractWorkflowAnnotationCommand {

    private final UpdateWorkflowAnnotationCommandEnt m_commandEnt;

    UpdateWorkflowAnnotation(final UpdateWorkflowAnnotationCommandEnt commandEnt) {
        super(commandEnt);
        m_commandEnt = commandEnt;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean executeInternal(final WorkflowAnnotation annotation, final AnnotationData annotationDataCopy)
        throws OperationNotAllowedException {
        final var updateWorkflowAnnotationCommandEnt = m_commandEnt;
        final var text = updateWorkflowAnnotationCommandEnt.getText();
        final var borderColor = hexStringToInteger(updateWorkflowAnnotationCommandEnt.getBorderColor());

        if (text == null && borderColor == null) {
            throw new OperationNotAllowedException(
                "Cannot update a workflow annotation with neither a border color nor a text provided.");
        }

        var workflowChanged = false;
        List<Consumer<AnnotationData>> updatesToApply = new ArrayList<>();

        // Update text if possible
        if (text != null && !text.equals(annotation.getText())) {
            updatesToApply.add(ad -> ad.setText(text));
            workflowChanged = true;
        }

        // Update border color if possible
        if (borderColor != null && !borderColor.equals(annotation.getBorderColor())) {
            updatesToApply.add(ad -> ad.setBorderColor(borderColor));
            workflowChanged = true;
        }

        final var newAnnotationData = getUpdatedAnnotationData(annotationDataCopy, updatesToApply);
        annotation.copyFrom(newAnnotationData, true);
        return workflowChanged;
    }

}
