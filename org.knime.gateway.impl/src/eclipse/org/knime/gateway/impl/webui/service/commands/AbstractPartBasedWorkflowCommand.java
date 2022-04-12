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
 */
package org.knime.gateway.impl.webui.service.commands;

import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.WorkflowAnnotation;
import org.knime.core.node.workflow.WorkflowAnnotationID;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.api.webui.entity.PartBasedCommandEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions;
import org.knime.gateway.impl.service.util.DefaultServiceUtil;
import org.knime.gateway.impl.webui.WorkflowKey;

/**
 * Workflow command based on workflow parts (i.e. nodes and annotations).
 *
 * @apiNote Subclasses may override getter methods for nodes and annotations. Thus, it is advised to use
 *  these getters instead of accessing the fields {@link AbstractPartBasedWorkflowCommand#m_nodesQueried} or
 *  {@link AbstractPartBasedWorkflowCommand#m_annotationsQueried}
 * @author Benjamin Moser, KNIME GmbH, Konstanz, Germany
 */
abstract class AbstractPartBasedWorkflowCommand extends AbstractWorkflowCommand {

    private Set<NodeID> m_nodesQueried;
    private Set<WorkflowAnnotationID> m_annotationsQueried;

    void configure(final WorkflowKey wfKey, final WorkflowManager wfm, final PartBasedCommandEnt commandEnt) {
        super.configure(wfKey, wfm);
        var projectId = getWorkflowKey().getProjectId();

        var nodesQueried = commandEnt.getNodeIds().stream().map(id -> DefaultServiceUtil.entityToNodeID(projectId, id))
                .collect(Collectors.toSet());
        var annotationsQueried = commandEnt.getAnnotationIds().stream()
                .map(id -> DefaultServiceUtil.entityToAnnotationID(projectId, id)).collect(Collectors.toSet());

        m_nodesQueried = nodesQueried;
        m_annotationsQueried = annotationsQueried;
    }

    /**
     * Check whether the workflow parts affected by this command are available (i.e. part of the workflow).
     * To avoid already performing modifications to the workflow and only then realising some workflow part is not
     * present, you may call this method at the beginning of {@link AbstractPartBasedWorkflowCommand#execute()} and,
     * going forward, assume that all workflow parts are available.
     *
     * @throws ServiceExceptions.OperationNotAllowedException If a workflow part is not available
     */
    void checkPartsPresentElseThrow() throws ServiceExceptions.OperationNotAllowedException {
        var nodeLookupResult = getNodeIDs().stream()
                .map(id -> Pair.of(
                        id,
                        WorkflowCommandUtils.getNodeContainer(id, getWorkflowManager())
                ))
                .collect(Collectors.toSet());

        var annotationLookupResult = getAnnotationIDs().stream()
                .map(id -> Pair.of(
                        id,
                        WorkflowCommandUtils.getAnnotation(id, getWorkflowManager())
                ))
                .collect(Collectors.toSet());

        var nodesNotFound = nodeLookupResult.stream()
                .filter(p -> p.getRight().isEmpty())
                .map(Pair::getLeft)
                .collect(Collectors.toSet());
        var annotsNotFound = annotationLookupResult.stream()
                .filter(p -> p.getRight().isEmpty())
                .map(Pair::getLeft)
                .collect(Collectors.toSet());
        boolean nodesMissing = !nodesNotFound.isEmpty();
        boolean annotsMissing = !annotsNotFound.isEmpty();
        if (nodesMissing || annotsMissing) {
            var message = new StringBuilder("Failed to execute command. Workflow parts not found: ");
            if (nodesMissing) {
                message.append("nodes (").append(nodesNotFound.stream().map(NodeID::toString).collect(Collectors.joining(","))).append(")");
            }
            if (nodesMissing && annotsMissing) {
                message.append(", ");
            }
            if (annotsMissing) {
                message.append("workflow-annotations (").append(annotsNotFound.stream().map(WorkflowAnnotationID::toString).collect(Collectors.joining(","))).append(")");
            }
            throw new ServiceExceptions.OperationNotAllowedException(message.toString());
        }
    }

    /**
     * @apiNote This method is assumed to be called after {@link AbstractPartBasedWorkflowCommand#checkPartsPresentElseThrow()}
     * @throws java.util.NoSuchElementException If a node container is not available.
     * @return The node containers for the node ids affected by this command.
     */
    protected Set<NodeContainer> getNodeContainers() {
        return getNodeIDs().stream()
                .map(id -> WorkflowCommandUtils.getNodeContainer(id, getWorkflowManager()).orElseThrow())
                .collect(Collectors.toSet());
    }

    /**
     * @apiNote This method is assumed to be called after {@link AbstractPartBasedWorkflowCommand#checkPartsPresentElseThrow()}
     * @throws java.util.NoSuchElementException If an annotation is not available.
     * @return The annotation objects for the annotation ids affected by this command.
     */
    protected Set<WorkflowAnnotation> getAnnotations() {
        return getAnnotationIDs().stream()
                .map(id -> WorkflowCommandUtils.getAnnotation(id, getWorkflowManager()).orElseThrow())
                .collect(Collectors.toSet());
    }

    protected Set<NodeID> getNodeIDs() {
        return m_nodesQueried;
    }

    protected Set<WorkflowAnnotationID> getAnnotationIDs() {
        return m_annotationsQueried;
    }

}
