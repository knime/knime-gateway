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
 *   Jun 29, 2022 (Kai Franze, KNIME GmbH): created
 */
package org.knime.gateway.impl.webui.service.commands;

import static org.knime.gateway.api.entity.EntityBuilderManager.builder;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeUIInformation;
import org.knime.core.node.workflow.WorkflowAnnotation;
import org.knime.core.node.workflow.WorkflowCopyContent;
import org.knime.gateway.api.entity.AnnotationIDEnt;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.api.webui.entity.CommandResultEnt.KindEnum;
import org.knime.gateway.api.webui.entity.PasteCommandEnt;
import org.knime.gateway.api.webui.entity.PasteResultEnt;
import org.knime.gateway.api.webui.entity.PasteResultEnt.PasteResultEntBuilder;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.OperationNotAllowedException;
import org.knime.gateway.impl.service.util.WorkflowChangesTracker.WorkflowChange;
import org.knime.gateway.impl.webui.service.commands.util.EditBendpoints;
import org.knime.gateway.impl.webui.service.commands.util.Geometry;
import org.knime.shared.workflow.storage.clipboard.InvalidDefClipboardContentVersionException;
import org.knime.shared.workflow.storage.clipboard.SystemClipboardFormat;
import org.knime.shared.workflow.storage.clipboard.SystemClipboardFormat.ObfuscatorException;
import org.knime.shared.workflow.storage.text.util.ObjectMapperUtil;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Workflow command to paste workflow parts into the active workflow
 *
 * @author Kai Franze, KNIME GmbH
 */
class Paste extends AbstractWorkflowCommand implements WithResult {

    private static final Geometry.Delta DEFAULT_SHIFT = new Geometry.Delta(120, 120);

    private final PasteCommandEnt m_commandEnt;

    private WorkflowCopyContent m_workflowCopyContent;

    Paste(final PasteCommandEnt commandEnt) {
        m_commandEnt = commandEnt;
    }

    @Override
    public void undo() throws OperationNotAllowedException {
        var wfm = getWorkflowManager();
        Arrays.stream(m_workflowCopyContent.getNodeIDs()).forEach(wfm::removeNode);
        Arrays.stream(m_workflowCopyContent.getAnnotationIDs()).forEach(wfm::removeAnnotation);
    }

    @Override
    protected boolean executeWithLockedWorkflow() throws OperationNotAllowedException {
        var wfm = getWorkflowManager();
        // Paste at original position
        try {
            var mapper = ObjectMapperUtil.getInstance().getObjectMapper();
            // TODO: NXT-1168 Put a limit on the clipboard content size
            var systemClipboardContent = mapper.readValue(m_commandEnt.getContent(), String.class);
            var defClipboardContent = SystemClipboardFormat.deserialize(systemClipboardContent);
            m_workflowCopyContent = getWorkflowManager().paste(defClipboardContent);
        } catch (JsonProcessingException | IllegalArgumentException | InvalidDefClipboardContentVersionException
                | ObfuscatorException e) {
            throw new OperationNotAllowedException("Could not parse input string to def clipboard content: ", e);
        }
        // Get nodes and annotations
        var nodes = Arrays.stream(m_workflowCopyContent.getNodeIDs())//
            .map(id -> CoreUtil.getNodeContainer(id, wfm).orElseThrow())//
            .collect(Collectors.toSet());
        var annotations = Arrays.stream(m_workflowCopyContent.getAnnotationIDs())//
            .map(id -> CoreUtil.getAnnotation(id, wfm).orElse(null))//
            .filter(Objects::nonNull)//
            .collect(Collectors.toSet());
        // Move pasted content to the correct position
        var delta = calculateShift(nodes, annotations);
        Translate.performTranslation(wfm, nodes, annotations, delta);
        return true;
    }

    private Geometry.Delta calculateShift(final Set<NodeContainer> nodes, final Set<WorkflowAnnotation> annotations) {
        if (m_commandEnt.getPosition() == null) {
            return DEFAULT_SHIFT;
        } else {
            var nodePositions = nodes.stream().map(NodeContainer::getUIInformation).map(NodeUIInformation::getBounds)
                .filter(Objects::nonNull).map(bounds -> new Geometry.Point(bounds[0], bounds[1]));
            var annotationPositions = annotations.stream().map(an -> new Geometry.Point(an.getX(), an.getY()));
            var bendpointPositions = EditBendpoints.inducedConnections(nodes, getWorkflowManager()) //
                .stream().flatMap(connection -> Arrays.stream(connection.getUIInfo().getAllBendpoints())) //
                .map(Geometry.Point::of);
            var topLeft = Geometry.Point.min(nodePositions, annotationPositions, bendpointPositions);
            return Geometry.Delta.of(Geometry.Point.of(m_commandEnt.getPosition()), topLeft);
        }
    }


    @Override
    public PasteResultEnt buildEntity(final String snapshotId) {
        return builder(PasteResultEntBuilder.class) //
            .setKind(KindEnum.PASTERESULT) //
            .setNodeIds(
                Arrays.stream(m_workflowCopyContent.getNodeIDs()).map(NodeIDEnt::new).collect(Collectors.toList())) //
            .setAnnotationIds(Arrays.stream(m_workflowCopyContent.getAnnotationIDs()).map(AnnotationIDEnt::new)
                .collect(Collectors.toList())) //
            .setSnapshotId(snapshotId).build();
    }

    @Override
    public Set<WorkflowChange> getChangesToWaitFor() {
        return Set.of(WorkflowChange.NODE_ADDED, WorkflowChange.ANNOTATION_ADDED);
    }


}
