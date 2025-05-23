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

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
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
import org.knime.core.node.workflow.WorkflowPersistor.NodeContainerTemplateLinkUpdateResult;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.CommandResultEnt;
import org.knime.gateway.api.webui.entity.UpdateLinkedComponentsCommandEnt;
import org.knime.gateway.api.webui.entity.UpdateLinkedComponentsResultEnt;
import org.knime.gateway.api.webui.entity.UpdateLinkedComponentsResultEnt.StatusEnum;
import org.knime.gateway.api.webui.entity.UpdateLinkedComponentsResultEnt.UpdateLinkedComponentsResultEntBuilder;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.ServiceCallException;
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
     * The aggregate status properties to return with the command result entity
     */
    private StatusEnum m_status;

    private List<String> m_details;

    UpdateLinkedComponents(final UpdateLinkedComponentsCommandEnt commandEnt) {
        m_nodeIdEnts = commandEnt.getNodeIds();
    }

    @Override
    protected boolean executeWithWorkflowLockAndContext() throws ServiceCallException {
        if (m_nodeIdEnts.isEmpty()) {
            throw new ServiceCallException("No component IDs passed for <%s>".formatted(getWorkflowKey()));
        }

        final var components = getLinkedComponents(m_nodeIdEnts, getWorkflowKey());

        if (components.size() != m_nodeIdEnts.size()) {
            throw new ServiceCallException(
                "Not all of the nodes <%s> are linked components".formatted(m_nodeIdEnts));
        }

        m_updateLogs = components.stream()//
            .map(UpdateLinkedComponents::updateLinkedComponent)//
            .toList();
        m_status = determineAggregateStatus(m_updateLogs);
        m_details = getDetails(m_status, m_updateLogs);

        if (m_status == StatusEnum.ERROR) { // Undo everything if there was an error
            m_updateLogs.forEach(UpdateLinkedComponents::undoInternal);
        }

        return m_status == StatusEnum.SUCCESS; // Only true if there really was a change
    }

    @Override
    public void undo() throws ServiceCallException {
        if (m_updateLogs != null) {
            m_updateLogs.forEach(UpdateLinkedComponents::undoInternal);
        }
        m_updateLogs = null;
        m_status = null;
        m_details = null;
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
            .setDetails(m_details)//
            .build();
    }

    @Override
    public Set<WorkflowChange> getChangesToWaitFor() {
        return Collections.emptySet(); // Assumption: There is no change to wait for
    }

    private static List<SubNodeContainer> getLinkedComponents(final List<NodeIDEnt> nodeIdEnts,
        final WorkflowKey wfKey) {
        return nodeIdEnts.stream() //
            .map(nodeIdEnt -> DefaultServiceUtil.getNodeContainer(wfKey.getProjectId(), nodeIdEnt)) //
            .filter(SubNodeContainer.class::isInstance) // Only components
            .map(SubNodeContainer.class::cast) //
            .filter(component -> component.getTemplateInformation().getRole() == Role.Link) // Only linked components
            .toList();
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

    private static List<String> getDetails(final StatusEnum status, final List<UpdateLog> updateLogs) {
        if (status == StatusEnum.ERROR) {
            return updateLogs.stream()//
                .map(UpdateLog::message)//
                .filter(Objects::nonNull)//
                .toList();
        }

        return null; // NOSONAR
    }

    private static UpdateLog updateLinkedComponent(final SubNodeContainer component) {
        final var oldComponentId = component.getID();
        final var wfm = component.getParent();
        final var nct = (NodeContainerTemplate)wfm.findNodeContainer(oldComponentId);

        LOGGER.debug("Attempting to update <%s> from <%s>"//
            .formatted(nct.getNameWithID(), nct.getTemplateInformation().getSourceURI()));

        try {
            if (!needsUpdate(oldComponentId, wfm)) {
                return new UpdateLog(oldComponentId, wfm, null, StatusEnum.UNCHANGED, null);
            }
        } catch (IOException e) {
            LOGGER.debug("Node with ID <%s> unexpectedly doesn't need an update.".formatted(oldComponentId), e);
            return logErrorAndReturnUpdateLog(nct, "Error while checking for update availability.", e);
        }

        final NodeContainerTemplateLinkUpdateResult updateResult;
        try {
            final var exec = new ExecutionMonitor();
            final var loadHelper = new WorkflowLoadHelper(true, wfm.getContextV2());
            // This will return an update result even if no update was necessary or possible
            updateResult = nct.getParent().updateMetaNodeLink(oldComponentId, exec, loadHelper);
        } catch (Throwable e) {
            return logErrorAndReturnUpdateLog(nct, null, e); // If f.e. the network is unreachable
        }

        final var persistor = updateResult.getUndoPersistor();
        if (persistor == null) {
            return logErrorAndReturnUpdateLog(nct, null, null); // If f.e. the linked component could not be found
        }

        final var componentId = updateResult.getNCTemplate().getID();
        var status = updateResult.getType() == LoadResultEntryType.Ok ? StatusEnum.SUCCESS : StatusEnum.ERROR;
        return new UpdateLog(componentId, wfm, persistor, status, null);
    }

    private static boolean needsUpdate(final NodeID componentId, final WorkflowManager parent) throws IOException {
        return parent.checkUpdateMetaNodeLink(componentId, new WorkflowLoadHelper(true, parent.getContextV2()));
    }

    private static UpdateLog logErrorAndReturnUpdateLog(final NodeContainerTemplate nct, final String reason,
        final Throwable t) {
        var message = "Could not update <%s> from <%s>%s".formatted(//
            nct.getNameWithID(), //
            nct.getTemplateInformation().getSourceURI(), //
            reason != null ? (": " + reason) : "."//
        );
        LOGGER.error(message, t);
        return new UpdateLog(null, null, null, StatusEnum.ERROR, message);
    }

    private static record UpdateLog(NodeID componentId, WorkflowManager wfm, WorkflowPersistor persistor,
        StatusEnum status, String message) {
        //
    }

}
