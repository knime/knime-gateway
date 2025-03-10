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
 *   May 10, 2023 (kai): created
 */
package org.knime.gateway.impl.webui.service.commands;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import org.knime.core.node.workflow.AnnotationData;
import org.knime.core.node.workflow.AnnotationData.TextAlignment;
import org.knime.core.node.workflow.WorkflowAnnotation;
import org.knime.core.node.workflow.WorkflowAnnotationID;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.api.entity.AnnotationIDEnt;
import org.knime.gateway.api.webui.entity.WorkflowAnnotationCommandEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.ServiceCallException;
import org.knime.gateway.impl.service.util.DefaultServiceUtil;
import org.knime.shared.workflow.def.AnnotationDataDef;

/**
 * Abstract workflow annotation command
 *
 * @author Kai Franze, KNIME GmbH
 */
abstract class AbstractWorkflowAnnotationCommand extends AbstractWorkflowCommand {

    /** To stay in sync with the Classic UI */
    private static final AnnotationData.TextAlignment DEFAULT_ALIGNMENT = TextAlignment.LEFT;
    private static final int DEFAULT_BG_COLOR = 0xFFFFFF;
    private static final int DEFAULT_BORDER_SIZE = 10;

    private final AnnotationIDEnt m_annotationIdEnt;

    private WorkflowAnnotationID m_annotationId;

    private AnnotationData m_previousAnnotationData;

    protected AbstractWorkflowAnnotationCommand(final WorkflowAnnotationCommandEnt commandEnt) {
        m_annotationIdEnt = commandEnt.getAnnotationId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean executeWithLockedWorkflow() throws ServiceCallException {
        m_annotationId = DefaultServiceUtil.entityToAnnotationID(getWorkflowKey().getProjectId(), m_annotationIdEnt);
        final var annotation = getWorkflowAnnotation(getWorkflowManager(), m_annotationId);
        m_previousAnnotationData = annotation.getData().clone();
        return executeInternal(annotation, m_previousAnnotationData);
    }

    /**
     * Executes the command, must be implemented by child classes.
     *
     * @param annotation The workflow annotation to manipulate
     * @param annotationDataCopy a copy(!) of the annotation's data
     *
     * @return Whether the command changed the workflow or not
     * @throws ServiceCallException
     */
    protected abstract boolean executeInternal(final WorkflowAnnotation annotation, AnnotationData annotationDataCopy)
        throws ServiceCallException;

    /**
     * {@inheritDoc}
     */
    @Override
    public void undo() throws ServiceCallException {
        final var annotation = getWorkflowAnnotation(getWorkflowManager(), m_annotationId);
        annotation.copyFrom(m_previousAnnotationData, true);
        m_previousAnnotationData = null;
        m_annotationId = null;
    }

    /**
     * Get workflow annotation for workflow annotation ID
     *
     * @param wfm The workflow manager
     * @param annotationId The workflow annotation ID
     *
     * @return The workflow annotation
     * @throws ServiceCallException
     */
    static WorkflowAnnotation getWorkflowAnnotation(final WorkflowManager wfm, final WorkflowAnnotationID annotationId)
        throws ServiceCallException {
        final var workflowAnnotation = wfm.getWorkflowAnnotations(annotationId)[0];
        if (workflowAnnotation == null) {
            throw new ServiceCallException(
                "No workflow annotation found for id " + (new AnnotationIDEnt(annotationId)));
        }
        return workflowAnnotation;
    }

    /**
     * Useful if updating the workflow annotation requires creating a new {@link AnnotationData} instance.
     *
     * @param annotationData The previous annotation data, can be {@code null}.
     * @param updatesToApply The list of updates to apply
     * @return The updated annotation data
     */
    static AnnotationData getUpdatedAnnotationData(final AnnotationData annotationData,
        final List<Consumer<AnnotationData>> updatesToApply) {
        final var newAnnotationData = new AnnotationData();

        if (annotationData != null) {
            newAnnotationData.copyFrom(annotationData, true);
        }

        // Set the default values
        newAnnotationData.setVersion(AnnotationData.VERSION_20230412);
        newAnnotationData.setContentType(AnnotationDataDef.ContentTypeEnum.HTML);
        newAnnotationData.setStyleRanges(Collections.emptyList()); // No style ranges are back-ported to Classic UI
        newAnnotationData.setAlignment(DEFAULT_ALIGNMENT);
        newAnnotationData.setBgColor(DEFAULT_BG_COLOR);
        newAnnotationData.setBorderSize(DEFAULT_BORDER_SIZE);

        // Apply updates last, might overwrite default values
        updatesToApply.forEach(update -> update.accept(newAnnotationData));
        return newAnnotationData;
    }

    /**
     * Converts a hex string to its corresponding integer value.
     *
     * @param hexString
     * @return The decoded integer, {@code null} if the input was {@code null}.
     * @throws ServiceCallException
     */
    static Integer hexStringToInteger(final String hexString) throws ServiceCallException {
        if (hexString == null) {
            return null;
        }
        try {
            return Integer.decode(hexString);
        } catch (NumberFormatException e) {
            throw new ServiceCallException("Invalid hex string <" + hexString + ">", e);
        }
    }

}
