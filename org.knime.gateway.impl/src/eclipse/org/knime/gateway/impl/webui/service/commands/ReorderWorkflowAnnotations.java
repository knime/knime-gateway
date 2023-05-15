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
 *   Mar 28, 2023 (kai): created
 */
package org.knime.gateway.impl.webui.service.commands;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.IntBinaryOperator;
import java.util.stream.Collectors;

import org.knime.core.node.workflow.WorkflowAnnotation;
import org.knime.core.node.workflow.WorkflowAnnotationID;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.api.webui.entity.ReorderWorkflowAnnotationsCommandEnt;
import org.knime.gateway.api.webui.entity.ReorderWorkflowAnnotationsCommandEnt.ActionEnum;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.OperationNotAllowedException;
import org.knime.gateway.impl.service.util.DefaultServiceUtil;

/**
 * Moves workflow annotations through the workflow's z-plane.
 *
 * @author Kai Franze, KNIME GmbH
 */
class ReorderWorkflowAnnotations extends AbstractWorkflowCommand {

    private final ReorderWorkflowAnnotationsCommandEnt m_commandEnt;

    /**
     * To enable undo, we need to keep track of the z-positions of all the annotations involved.
     */
    private Map<WorkflowAnnotationID, Integer> m_annotationIdToPreviousIndex;

    /**
     * Shifting a list of annotations to the left requires a different iteration order than shifting it to the right. To
     * enable undo, we need to keep the order of iteration for execution, so we can reverse it when undoing it.
     */
    private Comparator<WorkflowAnnotation> m_comparator;

    ReorderWorkflowAnnotations(final ReorderWorkflowAnnotationsCommandEnt commandEnt) {
        m_commandEnt = commandEnt;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean executeWithLockedWorkflow() throws OperationNotAllowedException {
        final var wfm = getWorkflowManager();
        final var workflowKey = getWorkflowKey();
        final var annotationIds = m_commandEnt.getAnnotationIds().stream()//
            .map(id -> DefaultServiceUtil.entityToAnnotationID(workflowKey.getProjectId(), id))//
            .collect(Collectors.toList());
        final var annotations = getAnnotions(wfm, annotationIds);
        final var action = m_commandEnt.getAction();

        m_annotationIdToPreviousIndex = getAnnotationIdToPreviousIndexMap(wfm, annotations);
        m_comparator = computeIterationOrder(action, m_annotationIdToPreviousIndex);

        // Define the function applied to every annotation
        final Function<WorkflowAnnotation, Boolean> function = annotation -> switch (action) {
            case BRING_FORWARD -> wfm.bringAnnotationForward(annotation);
            case BRING_TO_FRONT -> wfm.bringAnnotationToFront(annotation);
            case SEND_BACKWARD -> wfm.sendAnnotationBackward(annotation);
            case SEND_TO_BACK -> wfm.sendAnnotationToBack(annotation);
        };

        return processAnnotations(annotations, m_comparator, function);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void undo() throws OperationNotAllowedException {
        final var wfm = getWorkflowManager();
        final var annotationIds = m_annotationIdToPreviousIndex.keySet().stream().toList();
        final var annotations = getAnnotions(wfm, annotationIds);

        // Define the function applied to every annotation
        final Function<WorkflowAnnotation, Boolean> function = annotation -> {
            final var newIndex = m_annotationIdToPreviousIndex.get(annotation.getID());
            wfm.setAnnotationZOrdering(annotation, newIndex);
            return true; // Final result will always be true, even though it's not evaluated
        };

        processAnnotations(annotations, m_comparator.reversed(), function);
        m_annotationIdToPreviousIndex = null;
        m_comparator = null;
    }

    private static List<WorkflowAnnotation> getAnnotions(final WorkflowManager wfm,
        final List<WorkflowAnnotationID> annotationIds) throws OperationNotAllowedException {
        final List<WorkflowAnnotation> annotations = new ArrayList<>();
        for (final var annotationId : annotationIds) {
            final var annotation = AbstractWorkflowAnnotationCommand.getWorkflowAnnotation(wfm, annotationId);
            annotations.add(annotation);
        }
        return annotations;
    }

    private static Map<WorkflowAnnotationID, Integer> getAnnotationIdToPreviousIndexMap(final WorkflowManager wfm,
        final List<WorkflowAnnotation> annotations) {
        return annotations.stream().collect(Collectors.toMap(WorkflowAnnotation::getID, wfm::getZOrderForAnnotation));
    }

    private static Comparator<WorkflowAnnotation> computeIterationOrder(final ActionEnum action,
        final Map<WorkflowAnnotationID, Integer> annotationIdToPreviousIndex) {
        // Calculate the operator once
        final IntBinaryOperator operator = (left, right) -> switch (action) {
            case BRING_FORWARD, BRING_TO_FRONT -> right - left; // Iterate left to right
            case SEND_BACKWARD, SEND_TO_BACK -> left - right; // Iterate from right to left
        };
        // Apply the calculated operator in the comparator returned
        return (left, right) -> {
            final var leftIndex = annotationIdToPreviousIndex.get(left.getID());
            final var rightIndex = annotationIdToPreviousIndex.get(right.getID());
            return operator.applyAsInt(leftIndex, rightIndex);
        };
    }

    /**
     * Applies a given function to every annotation in the order specified by the comparator.
     *
     * @param annotations The annotations to process
     * @param comparator The order of processing
     * @param function The function to apply to every annotation
     * @return Whether at least one annotation's z-order was changed or not
     */
    private static boolean processAnnotations(final List<WorkflowAnnotation> annotations,
        final Comparator<WorkflowAnnotation> comparator, final Function<WorkflowAnnotation, Boolean> function) {
        return annotations.stream()//
            .sorted(comparator)//
            .map(function)//
            .reduce(Boolean.FALSE, (left, right) -> left || right); // True if at least one annotations z-order changed
    }
}
