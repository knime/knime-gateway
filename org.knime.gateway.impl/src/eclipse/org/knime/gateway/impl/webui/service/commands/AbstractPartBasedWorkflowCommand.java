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
import org.knime.gateway.api.webui.entity.PartBasedCommandEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions;
import org.knime.gateway.impl.service.util.DefaultServiceUtil;
import org.knime.gateway.impl.webui.WorkflowKey;

/**
 * Workflow command based on workflow parts (i.e. nodes and annotations).
 *
 * @author Benjamin Moser, KNIME GmbH, Konstanz, Germany
 */
public abstract class AbstractPartBasedWorkflowCommand extends AbstractWorkflowCommand {

    private final Set<NodeID> m_nodesQueried;
    private final Set<WorkflowAnnotationID> m_annotationsQueried;

    protected AbstractPartBasedWorkflowCommand(final WorkflowKey wfKey, final PartBasedCommandEnt commandEntity)
            throws ServiceExceptions.NodeNotFoundException, ServiceExceptions.NotASubWorkflowException,
            ServiceExceptions.OperationNotAllowedException {
        super(wfKey);
        var projectId = getWorkflowKey().getProjectId();

        var nodesQueried = commandEntity.getNodeIds().stream().map(id -> DefaultServiceUtil.entityToNodeID(projectId, id))
                .collect(Collectors.toSet());
        var annotationsQueried = commandEntity.getAnnotationIds().stream()
                .map(id -> DefaultServiceUtil.entityToAnnotationID(projectId, id)).collect(Collectors.toSet());

        m_nodesQueried = nodesQueried;
        m_annotationsQueried = annotationsQueried;

        var wfm = getWorkflowManager();
        var nodes = nodesQueried.stream()
                .map(id -> Pair.of(
                        id,
                        WorkflowCommandUtils.getNodeContainer(id, wfm)
                ))
                .collect(Collectors.toSet());

        var annotations = annotationsQueried.stream()
                .map(id -> Pair.of(
                        id,
                        WorkflowCommandUtils.getAnnotation(id, wfm)
                ))
                .collect(Collectors.toSet());

        WorkflowCommandUtils.checkPartsPresentElseThrow(nodes, annotations);

    }

    Set<NodeContainer> getNodeContainers()  {
        return m_nodesQueried.stream()
                .map(id -> WorkflowCommandUtils.getNodeContainer(id, getWorkflowManager()).orElseThrow())
                .collect(Collectors.toSet());
    }

    Set<WorkflowAnnotation> getAnnotations() {
        return m_annotationsQueried.stream()
                .map(id -> WorkflowCommandUtils.getAnnotation(id, getWorkflowManager()).orElseThrow())
                .collect(Collectors.toSet());
    }

    Set<NodeID> getNodeIDs() {
        return getNodeContainers().stream().map(NodeContainer::getID).collect(Collectors.toSet());
    }

    NodeID[] getNodeIDsArray() {
        return getNodeIDs().toArray(NodeID[]::new);
    }

}
