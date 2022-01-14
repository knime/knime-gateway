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

import static org.knime.gateway.api.entity.EntityBuilderManager.builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeUIInformation;
import org.knime.core.node.workflow.WorkflowAnnotation;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.api.entity.AnnotationIDEnt;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.TranslateCommandEnt;
import org.knime.gateway.api.webui.entity.TranslateCommandEnt.TranslateCommandEntBuilder;
import org.knime.gateway.api.webui.entity.WorkflowCommandEnt.KindEnum;
import org.knime.gateway.api.webui.entity.XYEnt;
import org.knime.gateway.api.webui.entity.XYEnt.XYEntBuilder;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.OperationNotAllowedException;
import org.knime.gateway.api.webui.util.EntityBuilderUtil;
import org.knime.gateway.impl.service.util.DefaultServiceUtil;

/**
 * Workflow command to translate (i.e. change the position) of nodes and workflow annotations.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
final class Translate extends AbstractWorkflowCommand<TranslateCommandEnt> {

    private int[] m_inverseTranslation;

    @Override
    public boolean execute() throws OperationNotAllowedException {
        m_inverseTranslation = execute(getWorkflowManager(), getWorkflowKey().getProjectId(), getCommandEntity());
        return m_inverseTranslation[0] != 0 && m_inverseTranslation[1] != 0;
    }

    @Override
    public void undo() throws OperationNotAllowedException {
        TranslateCommandEnt commandEntity = getCommandEntity();
        TranslateCommandEnt inverseCommandEntity = builder(TranslateCommandEntBuilder.class)
            .setKind(KindEnum.TRANSLATE).setNodeIds(commandEntity.getNodeIds())//
            .setAnnotationIds(commandEntity.getAnnotationIds())//
            .setTranslation(builder(XYEntBuilder.class)//
                .setX(m_inverseTranslation[0]).setY(m_inverseTranslation[1]).build())//
            .build();
        execute(getWorkflowManager(), getWorkflowKey().getProjectId(), inverseCommandEntity);
        m_inverseTranslation = null;
    }

    private static int[] execute(final WorkflowManager wfm, final String projectId,
        final TranslateCommandEnt commandEntity) throws OperationNotAllowedException {
        List<NodeContainer> nodes;
        List<String> nodesNotFound = null;
        if (!commandEntity.getNodeIds().isEmpty()) {
            nodes = new ArrayList<>();
            for (NodeIDEnt id : commandEntity.getNodeIds()) {
                try {
                    NodeContainer nc = wfm.getNodeContainer(DefaultServiceUtil.entityToNodeID(projectId, id));
                    nodes.add(nc);
                } catch (IllegalArgumentException e) { // NOSONAR will be thrown further down
                    nodesNotFound = initAndAdd(nodesNotFound, id.toString());
                }
            }
        } else {
            nodes = Collections.emptyList();
        }

        List<WorkflowAnnotation> annotations;
        List<String> annosNotFound = null;
        if (!commandEntity.getAnnotationIds().isEmpty()) {
            annotations = new ArrayList<>();
            for (AnnotationIDEnt id : commandEntity.getAnnotationIds()) {
                WorkflowAnnotation[] annos =
                    wfm.getWorkflowAnnotations(DefaultServiceUtil.entityToAnnotationID(projectId, id));
                if (annos.length == 0 || annos[0] == null) {
                    annosNotFound = initAndAdd(annosNotFound, id.toString());
                    continue;
                }
                annotations.add(annos[0]);
            }
        } else {
            annotations = Collections.emptyList();
        }

        checkAndThrowException(nodesNotFound, annosNotFound);

        XYEnt translation = commandEntity.getTranslation();
        executeTranslateCommand(translation, nodes, annotations);

        for (NodeContainer nc : nodes) {
             nc.setDirty();  // will propagate upwards
        }
        if (!annotations.isEmpty()) {
            wfm.setDirty();
        }
        return new int[]{-translation.getX(), -translation.getY()};
    }

    private static void executeTranslateCommand(final XYEnt translation, final List<NodeContainer> nodes,
        final List<WorkflowAnnotation> annotations) {
        int[] delta = new int[]{translation.getX(), translation.getY()};
        for (NodeContainer nc : nodes) {
            NodeUIInformation.moveNodeBy(nc, delta);
        }
        for (WorkflowAnnotation wa : annotations) {
            wa.shiftPosition(delta[0], delta[1] + EntityBuilderUtil.NODE_Y_POS_CORRECTION);
        }
    }

    private static void checkAndThrowException(final List<String> nodesNotFound, final List<String> annosNotFound)
        throws OperationNotAllowedException {
        if (nodesNotFound != null || annosNotFound != null) {
            StringBuilder message = new StringBuilder("Failed to execute command. Workflow parts not found: ");
            if (nodesNotFound != null) {
                message.append("nodes (").append(nodesNotFound.stream().collect(Collectors.joining(","))).append(")");
            }
            if (nodesNotFound != null && annosNotFound != null) {
                message.append(", ");
            }
            if (annosNotFound != null) {
                message.append("workflow-annotations (").append(annosNotFound.stream().collect(Collectors.joining(",")))
                    .append(")");
            }
            throw new OperationNotAllowedException(message.toString());
        }
    }

    private static List<String> initAndAdd(final List<String> l, final String s) {
        List<String> res = l;
        if (res == null) {
            res = new ArrayList<>();
        }
        res.add(s);
        return res;
    }

}
