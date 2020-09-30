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

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.util.Pair;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.WorkflowEnt;
import org.knime.gateway.api.webui.entity.WorkflowSnapshotEnt;
import org.knime.gateway.api.webui.entity.WorkflowSnapshotEnt.WorkflowSnapshotEntBuilder;
import org.knime.gateway.api.webui.service.WorkflowService;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.NodeNotFoundException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.NotASubWorkflowException;
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
        WorkflowManager wfm;
        try {
            wfm = DefaultServiceUtil.getWorkflowManager(projectId, workflowId);
        } catch (IllegalArgumentException ex) {
            throw new NodeNotFoundException(ex.getMessage(), ex);
        } catch (IllegalStateException ex) {
            throw new NotASubWorkflowException(ex.getMessage(), ex);
        }
        return buildWorkflowSnapshotEnt(buildWorkflowEnt(wfm, Boolean.TRUE.equals(includeInfoOnAllowedActions)),
            projectId, workflowId);
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

}
