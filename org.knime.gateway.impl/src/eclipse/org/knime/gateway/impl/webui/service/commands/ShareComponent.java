/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.com; Email: contact@knime.com
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
 */
package org.knime.gateway.impl.webui.service.commands;

import static org.knime.gateway.api.entity.EntityBuilderManager.builder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.EnumMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.util.CheckUtils;
import org.knime.core.node.workflow.MetaNodeTemplateInformation;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.util.FileUtil;
import org.knime.core.util.exception.ResourceAccessException;
import org.knime.core.util.pathresolve.ResolverUtil;
import org.knime.core.util.urlresolve.KnimeUrlResolver;
import org.knime.core.util.urlresolve.URLResolverUtil;
import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.api.webui.entity.CommandResultEnt;
import org.knime.gateway.api.webui.entity.ShareComponentCommandEnt;
import org.knime.gateway.api.webui.entity.ShareComponentResultEnt;
import org.knime.gateway.api.webui.entity.SpaceItemEnt;
import org.knime.gateway.api.webui.service.util.MutableServiceCallException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions;
import org.knime.gateway.impl.service.util.WorkflowChangesTracker;
import org.knime.gateway.impl.webui.service.commands.util.ComponentExporter;
import org.knime.gateway.impl.webui.spaces.Space;
import org.knime.gateway.impl.webui.spaces.SpaceProviders;
import org.knime.gateway.impl.webui.spaces.local.LocalSpace;

/**
 * Export and link a component
 *
 * @since 5.9
 */
@SuppressWarnings("java:S1130")
public class ShareComponent extends AbstractWorkflowCommand implements WithResult {

    private final SpaceProviders m_spaceProviders;

    private final ShareComponentCommandEnt m_command;

    private boolean m_resultIsNameCollision;

    private MetaNodeTemplateInformation m_oldTemplateInfo;

    ShareComponent(final ShareComponentCommandEnt ce, final SpaceProviders spaceProviders) {
        m_command = ce;
        m_spaceProviders = spaceProviders;
    }

    private static SubNodeContainer getSubNodeContainerOrThrow(final WorkflowManager wfm, final NodeID componentId) {
        final var component = wfm.getNodeContainer(componentId, SubNodeContainer.class, false);
        if (component == null) {
            throw new IllegalArgumentException("Not a component:  " + componentId);
        }
        return component;
    }

    private static boolean hasCollision(final String destItemId, final SubNodeContainer component,
        final Space destinationSpace)
        throws MutableServiceCallException, ServiceExceptions.NetworkException, ServiceExceptions.LoggedOutException {
        return destinationSpace.getItemIdForName(destItemId, component.getName()).isPresent();
    }

    private static URI resolveLinkUri(final KnimeUrlResolver.KnimeUrlVariant requestedVariant,
        final Space destinationSpace, final String uploadedComponentItemId, final SubNodeContainer component)
        throws ServiceExceptions.LoggedOutException, ServiceExceptions.NetworkException, MutableServiceCallException {

        final var absoluteUri = destinationSpace.toKnimeUrl(uploadedComponentItemId);
        final var pathBasedUri = destinationSpace.toPathBasedKnimeUrl(uploadedComponentItemId);

        final var projectWorkflow = CoreUtil.getProjectWorkflow(component);
        final var resolver = KnimeUrlResolver.getResolver(projectWorkflow.getContextV2());

        try {
            final var translator = createHubUrlTranslator(pathBasedUri);
            final var variants =
                new EnumMap<KnimeUrlResolver.KnimeUrlVariant, URL>(KnimeUrlResolver.KnimeUrlVariant.class);
            variants.putAll(resolver.changeLinkType(URLResolverUtil.toURL(pathBasedUri), translator));
            variants.putIfAbsent(KnimeUrlResolver.KnimeUrlVariant.MOUNTPOINT_ABSOLUTE_PATH,
                URLResolverUtil.toURL(pathBasedUri));
            variants.putIfAbsent(KnimeUrlResolver.KnimeUrlVariant.MOUNTPOINT_ABSOLUTE_ID,
                URLResolverUtil.toURL(absoluteUri));

            final var selectedUrl = variants.get(requestedVariant);
            if (selectedUrl == null) {
                throw new MutableServiceCallException("No URL variant found for " + requestedVariant, true);
            }
            return URLResolverUtil.toURI(selectedUrl);

        } catch (ResourceAccessException e) {
            throw new MutableServiceCallException("Could not resolve link types", true, e);
        }
    }

    private static Function<URL, Optional<KnimeUrlResolver.IdAndPath>> createHubUrlTranslator(final URI pathBasedUri) {
        return url -> {
            try {
                return ResolverUtil.translateHubUrl(pathBasedUri.toURL());
            } catch (MalformedURLException e) {
                NodeLogger.getLogger(ShareComponent.class).info("Malformed URI", e);
                return Optional.empty();
            }
        };
    }

    private static Optional<KnimeUrlResolver.KnimeUrlVariant>
        parseUrlVariant(final ShareComponentCommandEnt.LinkTypeEnum input) {
        return switch (input) {
            case WORKFLOW_RELATIVE -> Optional.of(KnimeUrlResolver.KnimeUrlVariant.WORKFLOW_RELATIVE);
            case SPACE_RELATIVE -> Optional.of(KnimeUrlResolver.KnimeUrlVariant.SPACE_RELATIVE);
            case MOUNTPOINT_ABSOLUTE -> Optional.of(KnimeUrlResolver.KnimeUrlVariant.MOUNTPOINT_ABSOLUTE_PATH);
            case MOUNTPOINT_ABSOLUTE_ID_BASED -> Optional.of(KnimeUrlResolver.KnimeUrlVariant.MOUNTPOINT_ABSOLUTE_ID);
            case NONE -> Optional.empty();
        };
    }

    private static Space.NameCollisionHandling
        parseCollisionHandling(final ShareComponentCommandEnt.CollisionHandlingEnum p) {
        return switch (p) {
            case NOOP -> Space.NameCollisionHandling.NOOP;
            case AUTORENAME -> Space.NameCollisionHandling.AUTORENAME;
            case OVERWRITE -> Space.NameCollisionHandling.OVERWRITE;
        };
    }

    @Override
    public ShareComponentResultEnt buildEntity(final String snapshotId) {
        return builder(ShareComponentResultEnt.ShareComponentResultEntBuilder.class) //
            .setKind(CommandResultEnt.KindEnum.SHARE_COMPONENT_RESULT) //
            .setSnapshotId(snapshotId) //
            .setIsNameCollision(m_resultIsNameCollision) //
            .build();
    }

    @Override
    public Set<WorkflowChangesTracker.WorkflowChange> getChangesToWaitFor() {
        return Set.of();
    }

    @Override
    @SuppressWarnings("java:S1941")
    protected boolean executeWithWorkflowLockAndContext() throws ServiceExceptions.ServiceCallException {
        final var wfm = getWorkflowManager();
        final var componentId = m_command.getNodeId().toNodeID(wfm);
        final var component = getSubNodeContainerOrThrow(wfm, componentId);

        try {
            final var destinationSpace = m_spaceProviders.getSpace( //
                m_command.getDestinationSpaceProviderId(), //
                m_command.getDestinationSpaceId() //
            );
            // pattern: frontend first tries with NOOP collision handling, backend checks for collision and returns in case,
            //  frontend can prompt user for strategy and try again.
            final var collisionHandling = Optional.ofNullable(m_command.getCollisionHandling()) //
                .map(ShareComponent::parseCollisionHandling) //
                .orElse(Space.NameCollisionHandling.NOOP);
            if ( //
            collisionHandling == Space.NameCollisionHandling.NOOP //
                && hasCollision(m_command.getDestinationItemId(), component, destinationSpace) //
            ) {
                m_resultIsNameCollision = true; // command result
                return false;
            }

            var parameters = new ImportParameters( //
                m_command.getDestinationItemId(), //
                component, //
                collisionHandling, //
                m_command.isIncludeInputData() //
            );
            var importResult = destinationSpace instanceof LocalSpace localSpace ? //
                importToLocal(localSpace, parameters) //
                : importToSpace(destinationSpace, parameters);

            final var requestedLinkVariant = parseUrlVariant(m_command.getLinkType());
            if (requestedLinkVariant.isEmpty()) {
                return false; // share, but do not create local link
            }
            final var newLink = resolveLinkUri( //
                requestedLinkVariant.get(), //
                destinationSpace, //
                importResult.spaceItemEnt().getId(), //
                component //
            );

            var newTemplateInfo = importResult.templateInfo().createLink(newLink);
            m_oldTemplateInfo = wfm.setTemplateInformation(componentId, newTemplateInfo);
            return true;
        } catch (MutableServiceCallException e) {
            throw e.toGatewayException("Failed to link component");
        } catch (CanceledExecutionException e) {
            return false; // user cancelled, do nothing
        } catch (Exception e) {
            throw convertException(e);
        }
    }

    /**
     * Imports a component to a local space by saving it directly to a directory without compression.
     *
     * @param localSpace the target local space
     * @param params the import parameters including destination, collision handling, and input data settings
     * @return import result containing template information and space item entity
     * @throws MutableServiceCallException if the import operation fails
     * @throws CanceledExecutionException if the operation is canceled
     */
    private static ImportResult importToLocal(final LocalSpace localSpace, final ImportParameters params)
            throws Exception {
        final var result = localSpace.importWithResult( //
            destination -> ComponentExporter.exportToDirectory(params.component, destination, params.includeInputData), //
            params.destinationItemId, //
            params.component.getName(), //
            params.collisionHandling //
        );
        return new ImportResult(result.getSecond(), result.getFirst());
    }

    /**
     * Imports a component to a non-local space by first compressing it and then uploading.
     *
     * @param destinationSpace the target space
     * @param params the import parameters including destination, collision handling, and input data settings
     * @return import result containing template information and space item entity
     * @throws MutableServiceCallException if the import operation fails
     * @throws CanceledExecutionException if the operation is canceled
     * @throws ServiceExceptions.NetworkException if a network error occurs
     * @throws ServiceExceptions.LoggedOutException if the user is logged out
     * @throws ServiceExceptions.OperationNotAllowedException if the operation is not allowed
     */
    private static ImportResult importToSpace(final Space destinationSpace, final ImportParameters params)
        throws MutableServiceCallException, CanceledExecutionException,
            ServiceExceptions.NetworkException, ServiceExceptions.LoggedOutException,
        ServiceExceptions.OperationNotAllowedException, ServiceExceptions.ServiceCallException {
        try ( //
                // the directory name determines the name of the item in the Space, so it has to be component.getName()
                // to avoid name collisions, put it in directory with unique name
                var wfArtifactParent = FileUtil.createTempDirResource("MetaTemplateUpload"); //
                var compressionTargetParent = FileUtil.createTempDirResource("knime_uploaded_item") //
        ) {

            var wfArtifactTarget = wfArtifactParent.getPath().resolve(params.component.getName());

            var compressionTarget = compressionTargetParent.getPath().resolve(params.component.getName());
            var uploadLimit = destinationSpace.getUploadLimit();
            var originalTemplateInfo = ComponentExporter.exportComponentWithLimit( //
                params.component, //
                wfArtifactTarget, //
                compressionTarget, //
                params.includeInputData, //
                uploadLimit);

            var uploadedComponentItemEnt = destinationSpace.importWorkflowOrWorkflowGroup( // currently expects archive
                compressionTarget, //
                params.destinationItemId, //
                p -> {
                }, //
                params.collisionHandling, //
                new NullProgressMonitor() //
            );
            return new ImportResult(originalTemplateInfo, uploadedComponentItemEnt);

        } catch (IOException e) {
            throw new MutableServiceCallException("Could not set up temporary directories for export.", true, e);
        }
    }

    private record ImportParameters(String destinationItemId, SubNodeContainer component,
            Space.NameCollisionHandling collisionHandling, boolean includeInputData) {

    }


    private record ImportResult(MetaNodeTemplateInformation templateInfo, SpaceItemEnt spaceItemEnt) {
    }

    @Override
    public void undo() throws ServiceExceptions.ServiceCallException {
        final var wfm = getWorkflowManager();
        final var componentId = m_command.getNodeId().toNodeID(wfm);
        wfm.setTemplateInformation(componentId, CheckUtils.checkNotNull(m_oldTemplateInfo));
    }

    private static ServiceExceptions.ServiceCallException convertException(final Exception exception) {
        // re-wrapping can be avoided once we have (richer) exception handling for workflow commands
        if (exception instanceof MutableServiceCallException mutableServiceCallException) {
            return mutableServiceCallException.toGatewayException("Failed to share component");
        }
        if (exception instanceof ServiceExceptions.NetworkException networkException) {
            return ServiceExceptions.ServiceCallException.builder() //
                .withTitle("Could not connect to the destination.") //
                .withDetails(networkException.getDetails()) //
                .canCopy(true) //
                .withCause(networkException) //
                .build(); //
        }
        if (exception instanceof ServiceExceptions.LoggedOutException loggedOutException) {
            return ServiceExceptions.ServiceCallException.builder() //
                .withTitle("You've been logged out. Please re-connect to the destination") //
                .withDetails(loggedOutException.getDetails()) //
                .canCopy(true) //
                .withCause(loggedOutException) //
                .build(); //
        }
        if (exception instanceof ServiceExceptions.ServiceCallException serviceCallException) {
            return serviceCallException;
        }
        return ServiceExceptions.ServiceCallException.builder() //
            .withTitle("Failed to share component") //
            .withDetails(List.of(exception.getMessage())) //
            .canCopy(true) //
            .withCause(exception) //
            .build(); //
    }

}
