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

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.knime.core.util.exception.ResourceAccessException;
import org.knime.gateway.api.webui.entity.SpaceItemEnt;
import org.knime.gateway.api.webui.entity.SpaceProviderEnt;
import org.knime.gateway.api.webui.entity.WorkflowGroupContentEnt;
import org.knime.gateway.api.webui.service.SpaceService;
import org.knime.gateway.api.webui.service.util.ServiceExceptions;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.InvalidRequestException;
import org.knime.gateway.impl.project.Project;
import org.knime.gateway.impl.project.Project.Origin;
import org.knime.gateway.impl.project.ProjectManager;
import org.knime.gateway.impl.webui.spaces.Space;
import org.knime.gateway.impl.webui.spaces.Space.NameCollisionHandling;
import org.knime.gateway.impl.webui.spaces.SpaceProviders;
import org.knime.gateway.impl.webui.spaces.local.LocalWorkspace;

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

    private final SpaceProviders m_spaceProviders =
        ServiceDependencies.getServiceDependency(SpaceProviders.class, true);

    private final ProjectManager m_projectManager =
            ServiceDependencies.getServiceDependency(ProjectManager.class, true);

    DefaultSpaceService() {
        //
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SpaceProviderEnt getSpaceProvider(final String spaceProviderId) throws InvalidRequestException {
        if (spaceProviderId != null && !spaceProviderId.isBlank()) {
            var spaceProvider = m_spaceProviders.getProvidersMap().get(spaceProviderId);
            if (spaceProvider == null) {
                throw new InvalidRequestException("No space provider available for id '" + spaceProviderId + "'");
            }
            return spaceProvider.toEntity();
        } else {
            throw new InvalidRequestException("Invalid space-provider-id (empty/null)");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorkflowGroupContentEnt listWorkflowGroup(final String spaceId, final String spaceProviderId,
        final String workflowGroupId) throws InvalidRequestException, ServiceExceptions.IOException {
        try {
            return SpaceProviders.getSpace(m_spaceProviders, spaceProviderId, spaceId)
                .listWorkflowGroup(workflowGroupId);
        } catch (NoSuchElementException e) {
            throw new InvalidRequestException("Problem fetching space items", e);
        } catch (IOException e) {
            throw new ServiceExceptions.IOException(e.getMessage(), e);
        }
    }

    @Override
    public List<Object> listJobsForWorkflow(final String spaceId, final String spaceProviderId, final String workflowId)
        throws InvalidRequestException {
        try {
            return SpaceProviders.getSpace(m_spaceProviders, spaceProviderId, spaceId).listJobsForWorkflow(workflowId);
        } catch (NoSuchElementException e) {
            throw new InvalidRequestException("Problem fetching jobs", e);
        }
    }

    @Override
    public void deleteJobsForWorkflow(final String spaceId, final String spaceProviderId, final String itemId,
            final String jobId) throws ServiceExceptions.IOException, ServiceExceptions.InvalidRequestException {
        final var space = SpaceProviders.getSpace(m_spaceProviders, spaceProviderId, spaceId);
        try {
            space.deleteJobsForWorkflow(itemId, List.of(jobId));
        } catch (final ResourceAccessException e) {
            throw new ServiceExceptions.IOException(e.getMessage(), e);
        }
    }

    @Override
    public List<Object> listSchedulesForWorkflow(final String spaceId, final String spaceProviderId, final String workflowId)
        throws InvalidRequestException {
        try {
            return SpaceProviders.getSpace(m_spaceProviders, spaceProviderId, spaceId).listSchedulesForWorkflow(workflowId);
        } catch (NoSuchElementException e) {
            throw new InvalidRequestException("Problem fetching jobs", e);
        }
    }

    @Override
    public void deleteSchedulesForWorkflow(final String spaceId, final String spaceProviderId, final String itemId,
            final String scheduleId) throws org.knime.gateway.api.webui.service.util.ServiceExceptions.IOException,
            InvalidRequestException {
        final var space = SpaceProviders.getSpace(m_spaceProviders, spaceProviderId, spaceId);
        try {
            space.deleteSchedulesForWorkflow(itemId, List.of(scheduleId));
        } catch (final ResourceAccessException e) {
            throw new ServiceExceptions.IOException(e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SpaceItemEnt createWorkflow(final String spaceId, final String spaceProviderId, final String workflowGroupId,
            final String name) throws InvalidRequestException, ServiceExceptions.IOException {
        try {
            return SpaceProviders.getSpace(m_spaceProviders, spaceProviderId, spaceId) //
                    .createWorkflow(workflowGroupId, name);
        } catch (NoSuchElementException e) {
            throw new InvalidRequestException("Problem fetching space items", e);
        } catch (IOException e) {
            throw new ServiceExceptions.IOException(e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteItems(final String spaceId, final String spaceProviderId, final List<String> spaceItemIds)
        throws ServiceExceptions.IOException, InvalidRequestException {
        try {
            // TODO: Check for open workflows in local workspace (included in NXT-1481)
            SpaceProviders.getSpace(m_spaceProviders, spaceProviderId, spaceId).deleteItems(spaceItemIds);
        } catch (NoSuchElementException | UnsupportedOperationException e) {
            throw new InvalidRequestException(e.getMessage(), e);
        } catch (IOException e) {
            throw new ServiceExceptions.IOException(e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SpaceItemEnt createWorkflowGroup(final String spaceId, final String spaceProviderId, final String itemId)
        throws ServiceExceptions.IOException, InvalidRequestException {
        try {
            return SpaceProviders.getSpace(m_spaceProviders, spaceProviderId, spaceId).createWorkflowGroup(itemId);
        } catch (NoSuchElementException | UnsupportedOperationException e) {
            throw new InvalidRequestException(e.getMessage(), e);
        } catch (IOException e) {
            throw new ServiceExceptions.IOException(e.getMessage(), e);
        }
    }

    @Override
    public void moveOrCopyItems(final String spaceId, final String spaceProviderId, final List<String> itemIds,
        final String destWorkflowGroupItemId, final String collisionHandling, final Boolean copy)
        throws ServiceExceptions.IOException, InvalidRequestException {
        try {
            var space = SpaceProviders.getSpace(m_spaceProviders, spaceProviderId, spaceId);
            if (space instanceof LocalWorkspace) {
                var workflowsToClose = checkForWorkflowsToClose(getOpenWorkflowIds(), itemIds, space);
                if (!workflowsToClose.isEmpty()) {
                    throw new InvalidRequestException(
                        "Not all items can be moved. The following workflows need to be closed first: "
                            + workflowsToClose);
                }
            }
            space.moveOrCopyItems(itemIds, destWorkflowGroupItemId, NameCollisionHandling.valueOf(collisionHandling),
                copy);
        } catch (NoSuchElementException | IllegalArgumentException e) {
            throw new InvalidRequestException(e.getMessage(), e);
        } catch (IOException e) {
            throw new ServiceExceptions.IOException(e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SpaceItemEnt renameItem(final String spaceProviderId, final String spaceId, final String itemId,
        final String newName)
        throws ServiceExceptions.IOException, InvalidRequestException, ServiceExceptions.OperationNotAllowedException {
        try {
            // TODO: Check for open workflows in local workspace (included in NXT-1481)
            return SpaceProviders.getSpace(m_spaceProviders, spaceProviderId, spaceId).renameItem(itemId, newName);
        } catch (NoSuchElementException e) {
            throw new InvalidRequestException("Could not access space", e);
        } catch (IOException e) {
            throw new ServiceExceptions.IOException(e.getMessage(), e);
        }
    }

    private Stream<String> getOpenWorkflowIds() {
        return m_projectManager.getProjectIds().stream()//
            .flatMap(id -> m_projectManager.getProject(id)//
                .flatMap(Project::getOrigin)//
                .map(Origin::getItemId)//
                .stream());
    }

    private static List<String> checkForWorkflowsToClose(final Stream<String> openWorkflowIds,
        final List<String> itemIds, final Space space) {
        return openWorkflowIds//
            .filter(workflowId -> {
                if (itemIds.contains(workflowId)) {
                    return true;
                }
                var ancestorsItemIds = space.getAncestorItemIds(workflowId);
                return ancestorsItemIds.stream().anyMatch(itemIds::contains);
            })//
            .collect(Collectors.toList());
    }

}
