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

import static org.knime.gateway.api.entity.EntityBuilderManager.builder;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletionException;

import org.knime.core.node.workflow.NodeID;
import org.knime.gateway.api.webui.entity.AddComponentCommandEnt;
import org.knime.gateway.api.webui.entity.AddComponentPlaceholderResultEnt.AddComponentPlaceholderResultEntBuilder;
import org.knime.gateway.api.webui.entity.CommandResultEnt;
import org.knime.gateway.api.webui.entity.CommandResultEnt.KindEnum;
import org.knime.gateway.api.webui.service.util.ServiceExceptions;
import org.knime.gateway.impl.service.util.WorkflowChangesTracker.WorkflowChange;
import org.knime.gateway.impl.webui.ComponentLoadJobManager.LoadJob;
import org.knime.gateway.impl.webui.WorkflowMiddleware;

/**
 * Command to (down-)load and add a component to a workflow from a given item-id.
 *
 * Parts of the loading logic 'inspired' by/copied from
 * org.knime.workbench.editor2.commands.CreateMetaNodeTemplateCommand.createMetaNodeTemplate and
 * org.knime.workbench.editor2.LoadMetaNodeTemplateRunnable.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
final class AddComponent extends AbstractWorkflowCommand implements WithResult {

    private final AddComponentCommandEnt m_commandEnt;

    private final WorkflowMiddleware m_workflowMiddleware;

    private LoadJob m_loadJob;

    AddComponent(final AddComponentCommandEnt commandEnt, final WorkflowMiddleware workflowMiddleware) {
        super(false);
        m_commandEnt = commandEnt;
        m_workflowMiddleware = workflowMiddleware;
    }

    @Override
    public boolean executeWithWorkflowContext() {
        m_loadJob = m_workflowMiddleware.getComponentLoadJobManager(getWorkflowKey()) //
                .startLoadJob(m_commandEnt);
        return true;
    }

    @Override
    public boolean canUndo() {
        var componentId = getComponentId();
        return componentId == null || getWorkflowManager().canRemoveNode(componentId);
    }

    @Override
    public void undo() throws ServiceExceptions.ServiceCallException {
        if (!m_loadJob.future().isDone()) {
            var workflowElementLoader = m_workflowMiddleware.getComponentLoadJobManager(getWorkflowKey());
            workflowElementLoader.cancelAndRemoveLoadJob(m_loadJob.id());
        } else {
            getWorkflowManager().removeNode(getComponentId());
        }
        m_loadJob = null;
    }

    private NodeID getComponentId() {
        var future = m_loadJob.future();
        try {
            var loadResult = future.getNow(null);
            if (loadResult != null) {
                return loadResult.componentId();
            }
        } catch (CompletionException | CancellationException e) { // NOSONAR
            //
        }
        return null;
    }

    @Override
    public CommandResultEnt buildEntity(final String snapshotId) {
        return builder(AddComponentPlaceholderResultEntBuilder.class) //
            .setKind(KindEnum.ADD_COMPONENT_PLACEHOLDER_RESULT) //
            .setNewPlaceholderId(m_loadJob.id()) //
            .setSnapshotId(snapshotId).build();
    }

    @Override
    public Set<WorkflowChange> getChangesToWaitFor() {
        return Collections.singleton(WorkflowChange.COMPONENT_PLACEHOLDER_ADDED);
    }

}