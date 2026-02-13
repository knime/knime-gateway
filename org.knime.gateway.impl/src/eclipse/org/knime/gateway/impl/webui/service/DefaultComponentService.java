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
 *   Feb 4, 2025 (hornm): created
 */
package org.knime.gateway.impl.webui.service;

import static org.knime.gateway.api.entity.EntityBuilderManager.builder;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;

import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.util.exception.ResourceAccessException;
import org.knime.core.util.hub.NamedItemVersion;
import org.knime.core.util.pathresolve.ResolverUtil;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.api.util.VersionId;
import org.knime.gateway.api.webui.entity.ComponentNodeDescriptionEnt;
import org.knime.gateway.api.webui.entity.LinkVariantEnt;
import org.knime.gateway.api.webui.entity.LinkVariantInfoEnt;
import org.knime.gateway.api.webui.entity.NamedItemVersionEnt;
import org.knime.gateway.api.webui.service.ComponentService;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.NodeNotFoundException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.ServiceCallException;
import org.knime.gateway.api.webui.util.EntityFactory;
import org.knime.gateway.impl.webui.WorkflowKey;
import org.knime.gateway.impl.webui.WorkflowMiddleware;
import org.knime.gateway.impl.webui.spaces.LinkVariants;
import org.knime.gateway.impl.webui.spaces.SpaceProvidersManager;

/**
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @author Tobias Kampmann, TNG Technology Consulting GmbH
 * @since 5.5
 */
public class DefaultComponentService implements ComponentService {

    private final WorkflowMiddleware m_workflowMiddleware =
        ServiceDependencies.getServiceDependency(WorkflowMiddleware.class, true);

    /**
     * Returns the singleton instance for this service.
     *
     * @return the singleton instance
     */
    public static DefaultComponentService getInstance() {
        return ServiceInstances.getDefaultServiceInstance(DefaultComponentService.class);
    }

    DefaultComponentService() {
        // singleton
    }

    @Override
    public void cancelOrRetryComponentLoadJob(final String projectId, final NodeIDEnt workflowId,
        final String placeholderId, final String action) throws ServiceCallException {

        DefaultServiceContext.assertWorkflowProjectId(projectId);
        var componentLoader = m_workflowMiddleware.getComponentLoadJobManager(new WorkflowKey(projectId, workflowId));
        if ("cancel".equals(action)) {
            componentLoader.cancelLoadJob(placeholderId);
        } else if ("retry".equals(action)) {
            componentLoader.rerunLoadJob(placeholderId);
        } else {
            throw ServiceCallException.builder() //
                .withTitle("Unknown action") //
                .withDetails("Unknown component loading action: " + action + ".") //
                .canCopy(false) //
                .build();
        }
    }

    @Override
    public ComponentNodeDescriptionEnt getComponentDescription(final String projectId, final NodeIDEnt workflowId,
        final String versionId, final NodeIDEnt nodeId) throws ServiceCallException {

        try {
            var version = VersionId.parse(versionId);

            var nc = ServiceUtilities.assertProjectIdAndGetNodeContainer(projectId, workflowId, version, nodeId);
            if (nc instanceof SubNodeContainer snc) {
                return EntityFactory.Workflow.buildComponentNodeDescriptionEnt(snc);
            }

            throw ServiceCallException.builder() //
                .withTitle("Component not found") //
                .withDetails("No Component for " + projectId + ", " + workflowId + ", " + nodeId + " found.") //
                .canCopy(false) //
                .build();
        } catch (NodeNotFoundException | IllegalArgumentException ex) {
            throw ServiceCallException.builder() //
                .withTitle("Component description not found") //
                .withDetails("Failed to get component description. " + ex.getMessage() + ".") //
                .canCopy(true) //
                .withCause(ex) //
                .build();
        }
    }

    @Override
    public List<LinkVariantInfoEnt> getLinkVariants(final String projectId, final NodeIDEnt workflowId,
        final NodeIDEnt nodeId) throws ServiceCallException, NodeNotFoundException {
        var nodeContainer = ServiceUtilities.assertProjectIdAndGetNodeContainer(projectId, workflowId,
            VersionId.currentState(), nodeId);
        if (!(nodeContainer instanceof SubNodeContainer snc)) {
            throw new IllegalStateException(
                "Node " + nodeId + " is not a component (type: " + nodeContainer.getClass().getSimpleName() + ").");
        }

        try {
            return ServiceDependencies.getServiceDependency(LinkVariants.class, true) //
                .getVariantInfoEnts( //
                    snc.getTemplateInformation().getSourceURI(), //
                    CoreUtil.getProjectWorkflow(snc).getContextV2() //
                );
        } catch (ResourceAccessException e) {
            throw ServiceCallException.builder() //
                .withTitle("Failed to resolve component URL") //
                .withDetails("Failed to resolve component URL: " + e.getMessage()) //
                .canCopy(true) //
                .withCause(e) //
                .build();
        }
    }

    @Override
    public List<NamedItemVersionEnt> getItemVersions(final String projectId, final NodeIDEnt workflowId,
        final NodeIDEnt nodeId) throws ServiceCallException, NodeNotFoundException {
        var nodeContainer = ServiceUtilities.assertProjectIdAndGetNodeContainer(projectId, workflowId,
            VersionId.currentState(), nodeId);
        if (!(nodeContainer instanceof SubNodeContainer snc)) {
            throw new IllegalStateException(
                "Node " + nodeId + " is not a component (type: " + nodeContainer.getClass().getSimpleName() + ").");
        }
        var uri = snc.getTemplateInformation().getSourceURI();
        try {
            var isIdBased = ServiceDependencies.getServiceDependency(LinkVariants.class, true) //
                .getLinkVariant(uri) //
                == LinkVariantEnt.VariantEnum.MOUNTPOINT_ABSOLUTE_ID;
            if (isIdBased) {
                return ServiceDependencies.getServiceDependency(SpaceProvidersManager.class, true) //
                    .getSpaceProviders(SpaceProvidersManager.Key.of(projectId)) //
                    .getSpaceProvider(uri.getAuthority()) //
                    .getItemVersions(extractHubItemId(uri)) //
                    .stream().map(v -> namedItemVersionToEntity(v)) //
                    .toList(); //
            }
            // else assume path-based, below call only supports path-based urls
            return ResolverUtil.getHubItemVersionList(uri).stream() //
                .map(DefaultComponentService::namedItemVersionToEntity).toList();
        } catch (ResourceAccessException e) {
            throw ServiceCallException.builder() //
                .withTitle("Failed to resolve component URL") //
                .withDetails("Failed to resolve component URL: " + e.getMessage()) //
                .canCopy(true) //
                .withCause(e) //
                .build();
        }
    }

    private static String extractHubItemId(final URI uri) {
        var path = uri.getPath();
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("Cannot extract hub item id: URI has no path: " + uri);
        }
        var trimmed = path.startsWith("/") ? path.substring(1) : path;
        var lastSegment = trimmed.contains("/") ? trimmed.substring(trimmed.lastIndexOf('/') + 1) : trimmed;
        if (lastSegment.startsWith("*") && lastSegment.length() > 1) {
            return lastSegment.substring(1);
        }
        throw new IllegalArgumentException("Cannot extract hub item id from URI: " + uri);
    }

    private static NamedItemVersionEnt namedItemVersionToEntity(final NamedItemVersion namedItemVersion) {
        return builder(NamedItemVersionEnt.NamedItemVersionEntBuilder.class) //
            .setVersion(Integer.valueOf(namedItemVersion.version())) //
            .setTitle(namedItemVersion.title()) //
            .setAuthor(namedItemVersion.author()) //
            .setDescription(namedItemVersion.description()) //
            .setCreatedOn(OffsetDateTime.parse(namedItemVersion.createdOn())) //
            .build();
    }

}
