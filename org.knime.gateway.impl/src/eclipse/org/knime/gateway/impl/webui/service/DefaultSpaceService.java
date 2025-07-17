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
 *   Dec 8, 2022 (hornm): created
 */
package org.knime.gateway.impl.webui.service;

import static org.knime.gateway.impl.webui.service.ServiceUtilities.getSpaceProvidersKey;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import org.knime.core.node.workflow.NodeTimer;
import org.knime.core.node.workflow.NodeTimer.GlobalNodeStats;
import org.knime.core.node.workflow.NodeTimer.GlobalNodeStats.WorkflowType;
import org.knime.gateway.api.webui.entity.SpaceEnt;
import org.knime.gateway.api.webui.entity.SpaceGroupEnt;
import org.knime.gateway.api.webui.entity.SpaceItemEnt;
import org.knime.gateway.api.webui.entity.SpaceProviderEnt.TypeEnum;
import org.knime.gateway.api.webui.entity.WorkflowGroupContentEnt;
import org.knime.gateway.api.webui.service.SpaceService;
import org.knime.gateway.api.webui.service.util.MutableServiceCallException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.CollisionException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.LoggedOutException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.NetworkException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.OperationNotAllowedException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.ServiceCallException;
import org.knime.gateway.impl.project.Origin;
import org.knime.gateway.impl.project.Project;
import org.knime.gateway.impl.project.ProjectManager;
import org.knime.gateway.impl.webui.spaces.Space;
import org.knime.gateway.impl.webui.spaces.Space.NameCollisionHandling;
import org.knime.gateway.impl.webui.spaces.SpaceProvider;
import org.knime.gateway.impl.webui.spaces.SpaceProvidersManager;
import org.knime.gateway.impl.webui.spaces.local.LocalSpace;

/**
 * The default workflow service implementation for the web-ui.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @author Kai Franze, KNIME GmbH
 */
public class DefaultSpaceService implements SpaceService {

    /**
     * Returns the singleton instance for this service.
     *
     * @return the singleton instance
     */
    public static DefaultSpaceService getInstance() {
        return ServiceInstances.getDefaultServiceInstance(DefaultSpaceService.class);
    }

    private final SpaceProvidersManager m_spaceProvidersManager =
        ServiceDependencies.getServiceDependency(SpaceProvidersManager.class, true);

    private final ProjectManager m_projectManager =
        ServiceDependencies.getServiceDependency(ProjectManager.class, true);

    DefaultSpaceService() {
        //
    }

    private SpaceProvider getSpaceProvider(final String spaceProviderId) throws MutableServiceCallException {
        if (spaceProviderId == null || spaceProviderId.isBlank()) {
            throw new MutableServiceCallException(List.of("The space provider ID must not be null or empty."), false);
        }

        try {
            return m_spaceProvidersManager.getSpaceProviders(getSpaceProvidersKey()) //
                .getSpaceProvider(spaceProviderId);
        } catch (final NoSuchElementException e) {
            throw new MutableServiceCallException(
                List.of("The space provider with ID '%s' does not exist.".formatted(spaceProviderId)), true, e);
        }
    }

    @Override
    public List<SpaceGroupEnt> getSpaceGroups(final String spaceProviderId)
        throws ServiceCallException, NetworkException, LoggedOutException {
        try {
            return getSpaceProvider(spaceProviderId).toEntity();
        } catch (final MutableServiceCallException e) { // NOSONAR
            throw e.toGatewayException("Failed to fetch space groups");
        }
    }

    @Override
    public WorkflowGroupContentEnt listWorkflowGroup(final String spaceId, final String spaceProviderId,
        final String workflowGroupId) throws ServiceCallException, NetworkException, LoggedOutException {
        try {
            return getSpaceProvider(spaceProviderId).getSpace(spaceId).listWorkflowGroup(workflowGroupId);
        } catch (final MutableServiceCallException e) { // NOSONAR
            throw e.toGatewayException("Failed to fetch folder contents");
        } catch (final NoSuchElementException e) {
            throw ServiceCallException.builder() //
                .withTitle("Folder not found") //
                .withDetails("The folder with ID '%s' does not exist.".formatted(workflowGroupId)) //
                .canCopy(true) //
                .withCause(e) //
                .build();
        }
    }

    @Override
    public List<Object> listJobsForWorkflow(final String spaceId, final String spaceProviderId, final String workflowId)
        throws ServiceCallException, LoggedOutException, NetworkException {
        try {
            return getSpaceProvider(spaceProviderId).getSpace(spaceId).listJobsForWorkflow(workflowId);
        } catch (final MutableServiceCallException e) { // NOSONAR
            throw e.toGatewayException("Failed to fetch jobs");
        }
    }

    @Override
    public void deleteJobsForWorkflow(final String spaceId, final String spaceProviderId, final String itemId,
        final String jobId) throws ServiceCallException, LoggedOutException, NetworkException {
        try {
            getSpaceProvider(spaceProviderId).getSpace(spaceId).deleteJobsForWorkflow(itemId, List.of(jobId));
        } catch (final MutableServiceCallException e) { // NOSONAR
            throw e.toGatewayException("An error occurred while deleting a job");
        }
    }

    @Override
    public List<Object> listSchedulesForWorkflow(final String spaceId, final String spaceProviderId,
        final String workflowId) throws ServiceCallException, LoggedOutException, NetworkException {
        try {
            return getSpaceProvider(spaceProviderId).getSpace(spaceId).listSchedulesForWorkflow(workflowId);
        } catch (final MutableServiceCallException e) { // NOSONAR
            throw e.toGatewayException("Failed to list schedules");
        }
    }

    @Override
    public void deleteSchedulesForWorkflow(final String spaceId, final String spaceProviderId, final String itemId,
        final String scheduleId) throws ServiceCallException, LoggedOutException, NetworkException {
        try {
            getSpaceProvider(spaceProviderId).getSpace(spaceId).deleteSchedulesForWorkflow(itemId, List.of(scheduleId));
        } catch (final MutableServiceCallException e) { // NOSONAR
            throw e.toGatewayException("An error occurred while deleting a schedule");
        }
    }

    @Override
    public SpaceEnt createSpace(final String spaceProviderId, final String spaceGroupName)
        throws ServiceCallException, LoggedOutException, NetworkException, OperationNotAllowedException {
        try {
            return getSpaceProvider(spaceProviderId).getSpaceGroup(spaceGroupName).createSpace().toEntity();
        } catch (final NoSuchElementException | UnsupportedOperationException e) {
            throw ServiceCallException.builder() //
                .withTitle("Space creation failed") //
                .withDetails(e.getMessage()) //
                .canCopy(true) //
                .withCause(e) //
                .build();
        } catch (final MutableServiceCallException e) { // NOSONAR
            throw e.toGatewayException("An error occurred while creating the space");
        }
    }

    @Override
    public SpaceItemEnt createWorkflow(final String spaceId, final String spaceProviderId, final String workflowGroupId,
        final String name)
        throws ServiceCallException, LoggedOutException, NetworkException, OperationNotAllowedException {
        try {
            final var item = getSpaceProvider(spaceProviderId).getSpace(spaceId).createWorkflow(workflowGroupId, name);
            if (GlobalNodeStats.isEnabled()) {
                NodeTimer.GLOBAL_TIMER.incWorkflowCreate(
                    m_spaceProvidersManager.getSpaceProviders(getSpaceProvidersKey()).getSpaceProvider(spaceProviderId)
                        .getType() == TypeEnum.LOCAL ? WorkflowType.LOCAL : WorkflowType.REMOTE);
            }
            return item;
        } catch (final NoSuchElementException e) {
            throw ServiceCallException.builder() //
                .withTitle("An error occurred while creating the workflow") //
                .withDetails(e.getMessage()) //
                .canCopy(true) //
                .withCause(e) //
                .build();
        } catch (final MutableServiceCallException e) { // NOSONAR
            throw e.toGatewayException("An error occurred while creating the workflow");
        }
    }

    @Override
    public void deleteItems(final String spaceId, final String spaceProviderId, final List<String> spaceItemIds,
        final Boolean softDelete)
        throws ServiceCallException, LoggedOutException, NetworkException, OperationNotAllowedException {
        try {
            getSpaceProvider(spaceProviderId).getSpace(spaceId).deleteItems(spaceItemIds, softDelete);
        } catch (final NoSuchElementException | UnsupportedOperationException e) {
            throw ServiceCallException.builder() //
                .withTitle("An error occurred while deleting item(s)") //
                .withDetails(e.getMessage()) //
                .canCopy(true) //
                .withCause(e) //
                .build();
        } catch (final MutableServiceCallException e) { // NOSONAR
            throw e.toGatewayException("An error occurred while deleting item(s)");
        }
    }

    @Override
    public SpaceItemEnt createWorkflowGroup(final String spaceId, final String spaceProviderId, final String itemId)
        throws ServiceCallException, LoggedOutException, NetworkException, OperationNotAllowedException {
        try {
            return getSpaceProvider(spaceProviderId).getSpace(spaceId).createWorkflowGroup(itemId);
        } catch (final NoSuchElementException | UnsupportedOperationException e) {
            throw ServiceCallException.builder() //
                .withTitle("An error occurred while creating the folder") //
                .withDetails(e.getMessage()) //
                .canCopy(true) //
                .withCause(e) //
                .build();
        } catch (final MutableServiceCallException e) { // NOSONAR
            throw e.toGatewayException("An error occurred while creating the folder");
        }
    }

    @Override
    public void moveOrCopyItems(final String spaceId, final String spaceProviderId, final List<String> itemIds,
        final String destSpaceId, final String destWorkflowGroupItemId, final Boolean copy,
        final String collisionHandling) throws ServiceCallException, CollisionException, LoggedOutException,
        NetworkException, OperationNotAllowedException {

        if (itemIds.isEmpty()) {
            return;
        }

        final var title = "An error occurred while %sing item(s)".formatted(Boolean.TRUE.equals(copy) ? "copy" : "mov");
        try {
            var spaceProvider = getSpaceProvider(spaceProviderId);
            var destinationSpace = spaceProvider.getSpace(destSpaceId);
            var sourceSpace = spaceProvider.getSpace(spaceId);

            if (collisionHandling == null) {
                checkForCollisionsInSpace(itemIds, sourceSpace, destinationSpace, destWorkflowGroupItemId);
            }

            if (sourceSpace instanceof LocalSpace localSpace) {
                var workflowsToClose = checkForWorkflowsToClose(getOpenWorkflowIds(localSpace), itemIds, localSpace);
                if (!workflowsToClose.isEmpty()) {
                    throw ServiceCallException.builder() //
                        .withTitle("Open workflows found") //
                        .withDetails("Not all items can be moved. The following workflows need to be closed first: "
                            + workflowsToClose) //
                        .canCopy(false) //
                        .build();
                }
            }

            var actualCollisionHandling =
                NameCollisionHandling.of(collisionHandling).orElse(NameCollisionHandling.NOOP);

            destinationSpace.moveOrCopyItems(itemIds, destWorkflowGroupItemId, actualCollisionHandling, copy);
        } catch (final NoSuchElementException | IllegalArgumentException e) {
            // should never happen
            throw ServiceCallException.builder() //
                .withTitle(title) //
                .withDetails(e.getMessage()) //
                .canCopy(true) //
                .withCause(e) //
                .build();
        } catch (final MutableServiceCallException e) { // NOSONAR
            throw e.toGatewayException(title);
        }
    }

    @Override
    public SpaceItemEnt renameItem(final String spaceProviderId, final String spaceId, final String itemId,
        final String newName) throws ServiceCallException, LoggedOutException, NetworkException {
        try {
            return getSpaceProvider(spaceProviderId).getSpace(spaceId).renameItem(itemId, newName);
        } catch (final OperationNotAllowedException e) {
            throw ServiceCallException.builder() //
                .withTitle(e.getTitle()) //
                .withDetails(e.getDetails()) //
                .canCopy(e.isCanCopy()) //
                .withAdditionalProps(e.getAdditionalProperties()) //
                .withCause(e) //
                .build();
        } catch (final MutableServiceCallException e) { // NOSONAR
            throw e.toGatewayException("An error occurred while renaming item");
        }
    }

    @Override
    public SpaceEnt renameSpace(final String spaceProviderId, final String spaceId, final String spaceName)
        throws ServiceCallException, LoggedOutException, NetworkException {
        try {
            return getSpaceProvider(spaceProviderId).getSpace(spaceId).renameSpace(spaceName);
        } catch (final OperationNotAllowedException e) {
            throw ServiceCallException.builder() //
                .withTitle(e.getTitle()) //
                .withDetails(e.getDetails()) //
                .canCopy(e.isCanCopy()) //
                .withAdditionalProps(e.getAdditionalProperties()) //
                .withCause(e) //
                .build();
        } catch (final MutableServiceCallException e) { // NOSONAR
            throw e.toGatewayException("An error occurred while renaming space");
        }
    }

    private static void checkForCollisionsInSpace(final List<String> itemIds, final Space sourceSpace,
        final Space destinationSpace, final String destWorkflowGroupItemId)
        throws CollisionException, NoSuchElementException, NetworkException, LoggedOutException, ServiceCallException {
        for (var itemId : itemIds) {
            try {
                var itemName = sourceSpace.getItemName(itemId);
                var destinationItemId = destinationSpace.getItemIdForName(destWorkflowGroupItemId, itemName);
                if (destinationItemId.isPresent()) {
                    checkForDestinationContainingSource(itemId, sourceSpace, destinationItemId.get(), destinationSpace,
                        itemName);
                    throw CollisionException.builder() //
                        .withTitle("Name collision") //
                        .withDetails("An item with name '%s' already exists at the target location" //
                            .formatted(itemName)) //
                        .canCopy(false) //
                        .build();
                }
            } catch (final MutableServiceCallException e) { // NOSONAR
                throw e.toGatewayException("An error occurred while checking for name collisions");
            }
        }
    }

    private static void checkForDestinationContainingSource(final String sourceItemId, final Space sourceSpace,
        final String destinationItemId, final Space destinationSpace, final String itemName)
        throws ServiceCallException, NetworkException, LoggedOutException {
        if (!sourceSpace.getId().equals(destinationSpace.getId())) {
            return; // Different spaces, no collision
        }

        final var title = "An error occurred while checking target folder";
        try {
            var ancestorItemIds = sourceSpace.getAncestorItemIds(sourceItemId);
            if (ancestorItemIds.contains(destinationItemId)) {
                throw ServiceCallException.builder() //
                    .withTitle(title) //
                    .withDetails(("The item with name '%s' can't overwrite itself"
                        + " (the destination item contains the source item).").formatted(itemName)) //
                    .canCopy(false) //
                    .build();
            }
        } catch (final MutableServiceCallException e) { // NOSONAR
            throw e.toGatewayException(title);
        }
    }

    /**
     * Get the IDs of all open workflows, optionally filtered to those belonging to a certain space.
     *
     * @param space Filter for workflows belonging to this space. Set `null` to disable.
     * @return Stream of open workflow IDs
     */
    private Stream<String> getOpenWorkflowIds(final Space space) {
        return m_projectManager.getProjectIds().stream()//
            .flatMap(id -> m_projectManager.getProject(id)//
                .flatMap(Project::getOrigin)//
                .filter(origin -> space == null || origin.spaceId().equals(space.getId())).map(Origin::itemId)//
                .stream());
    }

    private static List<String> checkForWorkflowsToClose(final Stream<String> openWorkflowIds,
        final List<String> itemIds, final LocalSpace localSpace) throws ServiceCallException {
        final List<String> toClose = new ArrayList<>();
        for (final String workflowId : (Iterable<String>)openWorkflowIds::iterator) {
            try {
                if (itemIds.contains(workflowId)
                    || localSpace.getAncestorItemIds(workflowId).stream().anyMatch(itemIds::contains)) {
                    toClose.add(workflowId);
                }
            } catch (final MutableServiceCallException e) { // NOSONAR
                throw e.toGatewayException("An error occurred while scanning open workflows");
            }
        }
        return toClose;
    }
}
