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
import java.util.function.Function;

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
import org.knime.core.node.workflow.WorkflowPersistor.NodeContainerTemplateLinkUpdateResult;
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

    /**
     * The list of node ID entities to update
     */
    private final List<NodeIDEnt> m_nodeIdEnts;

    /**
     * Combined information about how the component updates went and how to revert them
     */
    private List<UpdateLog> m_updateLogs;

    /**
     * The aggregate status to return with the command result entity
     */
    private StatusEnum m_status;

    UpdateLinkedComponents(final UpdateLinkedComponentsCommandEnt commandEnt) {
        m_nodeIdEnts = commandEnt.getNodeIds();
    }

    @Override
    protected boolean executeWithLockedWorkflow() throws OperationNotAllowedException {
        if (m_nodeIdEnts.isEmpty()) {
            throw new OperationNotAllowedException(
                "No component IDs passed for <%s>".formatted(getWorkflowKey()));
        }

        final var components = getComponents(m_nodeIdEnts, getWorkflowKey());

        if (!allComponentsAreLinks(components)) {
            throw new OperationNotAllowedException("Not all components are linked components");
        }

        m_updateLogs = components.stream()//
            .map(UpdateLinkedComponents::updateLinkedComponent)//
            .toList();
        m_status = determineAggregateStatus(m_updateLogs);

        if (m_status == StatusEnum.ERROR) { // Undo everything if there was an error
            m_updateLogs.forEach(UpdateLinkedComponents::undoInternal);
        }

        return m_status == StatusEnum.SUCCESS; // Only true if there really was a change
    }

    @Override
    public void undo() throws OperationNotAllowedException {
        if (m_updateLogs != null) {
            m_updateLogs.forEach(UpdateLinkedComponents::undoInternal);
        }
        m_updateLogs = null;
        m_status = null;
    }

    private static void undoInternal(final UpdateLog log) {
        final var componentId = log.componentId;
        final var persistor = log.persistor;
        if (componentId != null && persistor != null) {
            try {
                final var wfm = log.wfm;
                final var nodeToBeDeleted = (NodeContainerTemplate)wfm.findNodeContainer(componentId);
                final var parent = nodeToBeDeleted.getParent();
                parent.removeNode(nodeToBeDeleted.getID());
                parent.paste(persistor);
            } catch (IllegalArgumentException e) {
                LOGGER.error("Could not undo linked component update for <%s>".formatted(componentId), e);
            }
        }
    }

    @Override
    public UpdateLinkedComponentsResultEnt buildEntity(final String snapshotId) {
        return builder(UpdateLinkedComponentsResultEntBuilder.class)//
            .setKind(CommandResultEnt.KindEnum.UPDATE_LINKED_COMPONENTS_RESULT)//
            .setSnapshotId(snapshotId)//
            .setStatus(m_status)//
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

    private static StatusEnum determineAggregateStatus(final List<UpdateLog> updateLogs) {
        return updateLogs.stream()//
            .map(UpdateLog::status)//
            .reduce(StatusEnum.UNCHANGED, (left, right) -> {
                if (right == StatusEnum.ERROR) {
                    return StatusEnum.ERROR; // Because there was at least one error
                }
                if (left != StatusEnum.ERROR && right == StatusEnum.SUCCESS) {
                    return StatusEnum.SUCCESS; // Because there was no error and at least one success
                }
                return left; // Otherwise nothing changed
            });
    }

    private static UpdateLog updateLinkedComponent(final SubNodeContainer component) {
        final var oldComponentId = component.getID();
        final var wfm = component.getParent();
        final var nct = (NodeContainerTemplate)wfm.findNodeContainer(oldComponentId);

        LOGGER.debug("Attempting to update <%s> from <%s>"//
            .formatted(nct.getNameWithID(), nct.getTemplateInformation().getSourceURI()));

        final var statusComputingFunction = getStatusComputingFunction(oldComponentId, wfm); // NOSONAR: Must be here
        final var exec = new ExecutionMonitor();
        final var loadHelper = new WorkflowLoadHelper(true, wfm.getContextV2());

        final NodeContainerTemplateLinkUpdateResult updateResult;
        try {
            // This will return an update result even if no update was necessary or possible
            updateResult = nct.getParent().updateMetaNodeLink(oldComponentId, exec, loadHelper);
        } catch (Throwable e) {
            return logErrorAndReturnEmptyUpdateLog(nct, e); // If f.e. the network is unreachable
        }

        final var persistor = updateResult.getUndoPersistor();
        if (persistor == null) {
            return logErrorAndReturnEmptyUpdateLog(nct, null); // If f.e. the linked component could not be found
        }

        final var componentId = updateResult.getNCTemplate().getID();
        final var status = statusComputingFunction.apply(updateResult.getType());
        return new UpdateLog(componentId, wfm, persistor, status);
    }

    /**
     * To delay execution once we actually got the {@link LoadResultEntryType}
     */
    private static Function<LoadResultEntryType, StatusEnum> getStatusComputingFunction(final NodeID componentId,
        final WorkflowManager parent) {
        final var needsUpdate = isNodeContainerTemplate(componentId, parent);
        return type -> {
            if (!needsUpdate) {
                return StatusEnum.UNCHANGED;
            }
            return type == LoadResultEntryType.Ok ? StatusEnum.SUCCESS : StatusEnum.ERROR;
        };
    }

    /**
     * Copied from {@code UpdateMetaNodeTemplateRunnable}
     */
    private static boolean isNodeContainerTemplate(final NodeID componentId, final WorkflowManager parent) {
        try {
            // this line will fail with in IllegalArgumentException if the node already has been updated
            // basically a computationally cheaper option to WorkflowManager#checkUpdateMetaNodeLink
            return parent.findNodeContainer(componentId) instanceof NodeContainerTemplate;
        } catch (IllegalArgumentException e) {
            LOGGER.debug("Node with ID <%s> unexpectedly doesn't need an update.".formatted(componentId), e);
            return false;
        }
    }

    private static UpdateLog logErrorAndReturnEmptyUpdateLog(final NodeContainerTemplate nct, final Throwable t) {
        LOGGER.error("Could not update <%s> from <%s>"//
            .formatted(nct.getNameWithID(), nct.getTemplateInformation().getSourceURI()), t);
        return new UpdateLog(null, null, null, StatusEnum.ERROR);
    }

    private static record UpdateLog(NodeID componentId, WorkflowManager wfm, WorkflowPersistor persistor,
        StatusEnum status) {
        //
    }

}
