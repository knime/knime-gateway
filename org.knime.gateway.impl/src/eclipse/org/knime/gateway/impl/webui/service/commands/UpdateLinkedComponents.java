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
 *   Nov 13, 2023 (kai): created
 */
package org.knime.gateway.impl.webui.service.commands;

import static org.knime.gateway.api.entity.EntityBuilderManager.builder;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.workflow.MetaNodeTemplateInformation.Role;
import org.knime.core.node.workflow.NodeContainerTemplate;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.node.workflow.WorkflowLoadHelper;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.node.workflow.WorkflowPersistor;
import org.knime.core.node.workflow.WorkflowPersistor.LoadResultEntry.LoadResultEntryType;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.CommandResultEnt;
import org.knime.gateway.api.webui.entity.UpdateLinkedComponentsCommandEnt;
import org.knime.gateway.api.webui.entity.UpdateLinkedComponentsResultEnt;
import org.knime.gateway.api.webui.entity.UpdateLinkedComponentsResultEnt.StatusEnum;
import org.knime.gateway.api.webui.entity.UpdateLinkedComponentsResultEnt.UpdateLinkedComponentsResultEntBuilder;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.OperationNotAllowedException;
import org.knime.gateway.impl.service.util.DefaultServiceUtil;
import org.knime.gateway.impl.service.util.WorkflowChangesTracker.WorkflowChange;
import org.knime.gateway.impl.webui.WorkflowKey;

/**
 * Updates linked components and returns the update success.
 *
 * @author Kai Franze, KNIME GmbH, Germany
 */
class UpdateLinkedComponents extends AbstractWorkflowCommand implements WithResult {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(UpdateLinkedComponents.class);

    final List<NodeIDEnt> m_nodeIdEnts;

    List<UpdateLog> m_updateLog;

    Boolean m_success;

    UpdateLinkedComponents(final UpdateLinkedComponentsCommandEnt commandEnt) {
        m_nodeIdEnts = commandEnt.getNodeIds();
    }

    @Override
    protected boolean executeWithLockedWorkflow() throws OperationNotAllowedException {
        if (m_nodeIdEnts.isEmpty()) {
            throw new OperationNotAllowedException(
                "There are no linked component updates available for <%s>".formatted(getWorkflowKey()));
        }

        final var components = getComponents(m_nodeIdEnts, getWorkflowKey());

        if (!allComponentsAreLinks(components)) {
            throw new OperationNotAllowedException("Not all components are linked components");
        }

        m_updateLog = components.stream()//
            .map(UpdateLinkedComponents::updateLinkedComponent)//
            .toList();
        m_success = m_updateLog.stream().anyMatch(log -> !log.success);

        if (Boolean.FALSE.equals(m_success)) { // Undo all the changes if something went wrong
            m_updateLog.forEach(UpdateLinkedComponents::undoInternal);
        }

        return m_success;
    }

    @Override
    public void undo() throws OperationNotAllowedException {
        if (m_updateLog != null) {
            m_updateLog.forEach(UpdateLinkedComponents::undoInternal);
        }
        m_updateLog = null;
        m_success = null;
    }

    @Override
    public UpdateLinkedComponentsResultEnt buildEntity(final String snapshotId) {
        return builder(UpdateLinkedComponentsResultEntBuilder.class)//
            .setKind(CommandResultEnt.KindEnum.UPDATELINKEDCOMPONENTSRESULT)//
            .setKind(CommandResultEnt.KindEnum.UPDATE_LINKED_COMPONENTS_RESULT)//
            .setSnapshotId(snapshotId)//
            .setStatus(Boolean.TRUE.equals(m_success) ? StatusEnum.SUCCESS : StatusEnum.ERROR)//
            .build();
    }

    @Override
    public Set<WorkflowChange> getChangesToWaitFor() {
        return Collections.emptySet(); // Assumption: There is no change to wait for
    }

    private static List<SubNodeContainer> getComponents(final List<NodeIDEnt> nodeIdEnts, final WorkflowKey wfKey) {
        return nodeIdEnts.stream()//
            .map(nodeIdEnt -> DefaultServiceUtil.getNodeContainer(wfKey.getProjectId(), nodeIdEnt))//
            .map(SubNodeContainer.class::cast)//
            .toList();
    }

    private static boolean allComponentsAreLinks(final List<SubNodeContainer> components) {
        return components.stream()//
            .allMatch(component -> component.getTemplateInformation().getRole() == Role.Link);
    }

    private static UpdateLog updateLinkedComponent(final SubNodeContainer component) {
        final var oldId = component.getID();
        final var parent = component.getParent();
        final var nct = (NodeContainerTemplate)parent.findNodeContainer(oldId);

        LOGGER.info("Updating <%s> from <%s>"//
            .formatted(nct.getNameWithID(), nct.getTemplateInformation().getSourceURI()));

        final var exec = new ExecutionMonitor();
        final var loadHelper = new WorkflowLoadHelper(true, parent.getContextV2());

        // TODO: Do we need to do some more checking before actually updating?
        final var updateResult = nct.getParent().updateMetaNodeLink(oldId, exec, loadHelper);

        final var persistor = updateResult.getUndoPersistor();
        final var newId = updateResult.getNCTemplate().getID();
        final var success = updateResult.getType() == LoadResultEntryType.Ok;
        return new UpdateLog(oldId, newId, parent, persistor, success);
    }

    private static void undoInternal(final UpdateLog log) {
        final var newId = log.newId;
        final var wfm = log.parent; // TODO: Could we simply do 'getWorkflowManager()' instead?
        final var persistor = log.persistor;
        try {
            final var nodeToBeDeleted = (NodeContainerTemplate)wfm.findNodeContainer(newId);
            final var parent = nodeToBeDeleted.getParent();
            parent.removeNode(nodeToBeDeleted.getID());
            parent.paste(persistor);
        } catch (IllegalArgumentException e) {
            LOGGER.error("Could not undo linked component update for <%s>".formatted(newId), e);
        }
    }

    /**
     * Internal update log to report success and enable undo
     *
     */
    private static record UpdateLog(NodeID oldId, NodeID newId, WorkflowManager parent, WorkflowPersistor persistor,
        boolean success) {
        // TODO: Do we need the workflow manager in here?
    }

}
