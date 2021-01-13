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
 *   Jul 30, 2020 (hornm): created
 */
package org.knime.gateway.impl.webui.service;

import static org.knime.gateway.api.entity.EntityBuilderManager.builder;
import static org.knime.gateway.api.webui.util.EntityBuilderUtil.buildWorkflowEnt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeUIInformation;
import org.knime.core.node.workflow.WorkflowAnnotation;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.util.Pair;
import org.knime.gateway.api.entity.AnnotationIDEnt;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.WorkflowEnt;
import org.knime.gateway.api.webui.entity.WorkflowPartsWithPositionEnt;
import org.knime.gateway.api.webui.entity.WorkflowSnapshotEnt;
import org.knime.gateway.api.webui.entity.WorkflowSnapshotEnt.WorkflowSnapshotEntBuilder;
import org.knime.gateway.api.webui.entity.XYEnt;
import org.knime.gateway.api.webui.service.WorkflowService;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.NodeNotFoundException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.NotASubWorkflowException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.NotFoundException;
import org.knime.gateway.api.webui.util.EntityBuilderUtil;
import org.knime.gateway.impl.project.WorkflowProjectManager;
import org.knime.gateway.impl.service.util.DefaultServiceUtil;
import org.knime.gateway.impl.service.util.EntityRepository;
import org.knime.gateway.impl.service.util.SimpleRepository;

/**
 * The default workflow service implementation for the web-ui.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public final class DefaultWorkflowService implements WorkflowService {
    private static final DefaultWorkflowService INSTANCE = new DefaultWorkflowService();

    private final EntityRepository<Pair<String, NodeIDEnt>, WorkflowEnt> m_entityRepo;

    /**
     * Returns the singleton instance for this service.
     *
     * @return the singleton instance
     */
    public static DefaultWorkflowService getInstance() {
        return INSTANCE;
    }

    private DefaultWorkflowService() {
        m_entityRepo = new SimpleRepository<>(1, new SnapshotIdGenerator());
        WorkflowProjectManager
            .addWorkflowProjectRemovedListener(uuid -> m_entityRepo.disposeHistory(k -> k.getFirst().equals(uuid)));
    }

    EntityRepository<Pair<String, NodeIDEnt>, WorkflowEnt> getEntityRepository() {
        return m_entityRepo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorkflowSnapshotEnt getWorkflow(final String projectId, final NodeIDEnt workflowId,
        final Boolean includeInfoOnAllowedActions) throws NotASubWorkflowException, NodeNotFoundException {
        WorkflowManager wfm = getWorkflowManager(projectId, workflowId);
        return buildWorkflowSnapshotEnt(buildWorkflowEnt(wfm, Boolean.TRUE.equals(includeInfoOnAllowedActions)),
            projectId, workflowId);
    }

    private static WorkflowManager getWorkflowManager(final String projectId, final NodeIDEnt workflowId)
        throws NodeNotFoundException, NotASubWorkflowException {
        WorkflowManager wfm;
        try {
            wfm = DefaultServiceUtil.getWorkflowManager(projectId, workflowId);
        } catch (IllegalArgumentException ex) {
            throw new NodeNotFoundException(ex.getMessage(), ex);
        } catch (IllegalStateException ex) {
            throw new NotASubWorkflowException(ex.getMessage(), ex);
        }
        return wfm;
    }

    WorkflowSnapshotEnt buildWorkflowSnapshotEnt(final WorkflowEnt workflow, final String projectId,
        final NodeIDEnt workflowId) {
        String snapshotId = m_entityRepo.commit(Pair.create(projectId, workflowId), workflow);
        return builder(WorkflowSnapshotEntBuilder.class).setSnapshotId(snapshotId).setWorkflow(workflow).build();
    }

    private static class SnapshotIdGenerator implements Supplier<String> {

        private AtomicLong m_count = new AtomicLong();

        @Override
        public String get() {
            return Long.toString(m_count.getAndIncrement());
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void translateWorkflowParts(final String projectId, final NodeIDEnt workflowId,
        final WorkflowPartsWithPositionEnt workflowPartsWithPositionEnt)
        throws NotASubWorkflowException, NodeNotFoundException, NotFoundException {
        WorkflowManager wfm = getWorkflowManager(projectId, workflowId);
        List<NodeContainer> nodes;
        List<String> nodesNotFound = null;
        int x = Integer.MAX_VALUE;
        int y = Integer.MAX_VALUE;
        if (!workflowPartsWithPositionEnt.getParts().getNodeIDs().isEmpty()) {
            nodes = new ArrayList<>();
            for (NodeIDEnt id : workflowPartsWithPositionEnt.getParts().getNodeIDs()) {
                try {
                    NodeContainer nc = wfm.getNodeContainer(DefaultServiceUtil.entityToNodeID(projectId, id));
                    int[] bounds = nc.getUIInformation().getBounds();
                    x = Math.min(bounds[0], x);
                    y = Math.min(bounds[1], y);
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
        if (!workflowPartsWithPositionEnt.getParts().getAnnotationIDs().isEmpty()) {
            annotations = new ArrayList<>();
            for (AnnotationIDEnt id : workflowPartsWithPositionEnt.getParts().getAnnotationIDs()) {
                WorkflowAnnotation[] annos =
                    wfm.getWorkflowAnnotations(DefaultServiceUtil.entityToAnnotationID(projectId, id));
                if (annos.length == 0 || annos[0] == null) {
                    annosNotFound = initAndAdd(annosNotFound, id.toString());
                    continue;
                }
                x = Math.min(annos[0].getX(), x);
                y = Math.min(annos[0].getY(), y);
                annotations.add(annos[0]);
            }
        } else {
            annotations = Collections.emptyList();
        }

        checkAndThrowNotFoundException(nodesNotFound, annosNotFound);

        translateWorkflowParts(workflowPartsWithPositionEnt.getPosition(), nodes, x, y, annotations);
    }

    private static void translateWorkflowParts(final XYEnt newPos, final List<NodeContainer> nodes, final int x,
        final int y, final List<WorkflowAnnotation> annotations) {
        int[] delta = new int[]{newPos.getX() - x, newPos.getY() - y - EntityBuilderUtil.NODE_Y_POS_CORRECTION};
        for (NodeContainer nc : nodes) {
            NodeUIInformation.moveNodeBy(nc, delta);
        }
        for (WorkflowAnnotation wa : annotations) {
            wa.shiftPosition(delta[0], delta[1] + EntityBuilderUtil.NODE_Y_POS_CORRECTION);
        }
    }

    private static void checkAndThrowNotFoundException(final List<String> nodesNotFound,
        final List<String> annosNotFound) throws NotFoundException {
        if (nodesNotFound != null || annosNotFound != null) {
            StringBuilder message = new StringBuilder("Parts not found: ");
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
            throw new NotFoundException(message.toString());
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
