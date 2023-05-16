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
 *   Apr 26, 2023 (kai): created
 */
package org.knime.gateway.impl.webui.service.commands;

import static org.knime.gateway.api.entity.EntityBuilderManager.builder;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import org.knime.core.node.workflow.AnnotationData;
import org.knime.core.node.workflow.WorkflowAnnotationID;
import org.knime.gateway.api.entity.AnnotationIDEnt;
import org.knime.gateway.api.webui.entity.AddAnnotationResultEnt;
import org.knime.gateway.api.webui.entity.AddAnnotationResultEnt.AddAnnotationResultEntBuilder;
import org.knime.gateway.api.webui.entity.AddWorkflowAnnotationCommandEnt;
import org.knime.gateway.api.webui.entity.CommandResultEnt.KindEnum;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.OperationNotAllowedException;
import org.knime.gateway.impl.service.util.WorkflowChangesTracker.WorkflowChange;

/**
 * Workflow command to create a new workflow annotation
 *
 * @author Kai Franze, KNIME GmbH
 */
final class AddWorkflowAnnotation extends AbstractWorkflowCommand implements WithResult {

    private final AddWorkflowAnnotationCommandEnt m_commandEnt;

    private WorkflowAnnotationID m_workflowAnnotationID;

    AddWorkflowAnnotation(final AddWorkflowAnnotationCommandEnt commandEnt) {
        m_commandEnt = commandEnt;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean executeWithLockedWorkflow() throws OperationNotAllowedException {
        final var wfm = getWorkflowManager();
        final var bounds = m_commandEnt.getBounds();
        final var borderColor = AbstractWorkflowAnnotationCommand.hexStringToInteger(m_commandEnt.getBorderColor());
        List<Consumer<AnnotationData>> updatesToApply = List.of(//
            ad -> ad.setBorderColor(borderColor), //
            ad -> ad.setDimension(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight()));
        final var annoData = AbstractWorkflowAnnotationCommand.getUpdatedAnnotationData(null, updatesToApply);
        final var workflowAnnotation = wfm.addWorkflowAnnotation(annoData, -1);
        m_workflowAnnotationID = workflowAnnotation.getID();
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void undo() throws OperationNotAllowedException {
        final var wfm = getWorkflowManager();
        wfm.removeAnnotation(m_workflowAnnotationID);
        m_workflowAnnotationID = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AddAnnotationResultEnt buildEntity(final String snapshotId) {
        final var newAnnotationId = new AnnotationIDEnt(m_workflowAnnotationID);
        return builder(AddAnnotationResultEntBuilder.class)//
            .setKind(KindEnum.ADDANNOTATIONRESULT)//
            .setSnapshotId(snapshotId)//
            .setNewAnnotationId(newAnnotationId)//
            .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<WorkflowChange> getChangesToWaitFor() {
        return Collections.singleton(WorkflowChange.ANNOTATION_ADDED);
    }

}
