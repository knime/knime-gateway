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

import java.util.Objects;

import org.knime.core.node.workflow.AnnotationData;
import org.knime.core.node.workflow.WorkflowAnnotation;
import org.knime.gateway.api.webui.entity.UpdateWorkflowAnnotationCommandEnt;
import org.knime.gateway.api.webui.entity.WorkflowAnnotationCommandEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.OperationNotAllowedException;

/**
 * Updates the text and/or the border color of a workflow annotation.
 *
 * @author Kai Franze, KNIME GmbH
 */
final class UpdateWorkflowAnnotation extends AbstractWorkflowAnnotationCommand {

    UpdateWorkflowAnnotation(final UpdateWorkflowAnnotationCommandEnt commandEnt) {
        super(commandEnt);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean executeInternal(final WorkflowAnnotationCommandEnt workflowAnnotationCommandEnt,
        final AnnotationData previousAnnotationData, final WorkflowAnnotation annotation)
        throws OperationNotAllowedException {
        final var commandEnt = (UpdateWorkflowAnnotationCommandEnt)workflowAnnotationCommandEnt;
        final var text = commandEnt.getText();
        final var borderColor = hexStringToInteger(commandEnt.getBorderColor());

        if (text == null && borderColor == null) {
            throw new OperationNotAllowedException(
                "Cannot update a workflow annotation with neither a border color nor a text provided.");
        }

        var textUpdated = false;
        if (text != null) {
            textUpdated = updateText(text, previousAnnotationData, annotation);
        }

        var borderColorUpdated = false;
        if (borderColor != null) {
            borderColorUpdated = updateBorderColor(borderColor, previousAnnotationData, annotation);
        }

        return textUpdated || borderColorUpdated;
    }

    private static boolean updateText(final String text, final AnnotationData previousAnnotationData,
        final WorkflowAnnotation annotation) {
        if (Objects.equals(previousAnnotationData.getText(), text)) {
            return false;
        }
        final var newAnnotationData = getUpdatedAnnotationData(previousAnnotationData, ad -> ad.setText(text));
        annotation.copyFrom(newAnnotationData, true);
        return true;
    }

    private static boolean updateBorderColor(final Integer borderColor, final AnnotationData previousAnnotationData,
        final WorkflowAnnotation annotation) {
        if (Objects.equals(previousAnnotationData.getBorderColor(), borderColor)) {
            return false;
        }
        final var newAnnotationData =
            getUpdatedAnnotationData(previousAnnotationData, ad -> ad.setBorderColor(borderColor));
        annotation.copyFrom(newAnnotationData, true);
        return true;
    }

}
