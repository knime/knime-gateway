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
 *   May 8, 2025 (hornm): created
 */
package org.knime.gateway.impl.webui.service.commands.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.knime.core.data.container.storage.TableStoreFormatInformation;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.KNIMEComponentInformation;
import org.knime.core.node.NodeAndBundleInformationPersistor;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.util.CheckUtils;
import org.knime.core.node.workflow.Credentials;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.NodeUIInformation;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.node.workflow.UnsupportedWorkflowVersionException;
import org.knime.core.node.workflow.WorkflowLoadHelper;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.node.workflow.WorkflowPersistor.LoadResultEntry;
import org.knime.core.node.workflow.WorkflowPersistor.MetaNodeLinkUpdateResult;
import org.knime.core.util.Pair;
import org.knime.gateway.api.service.GatewayException;
import org.knime.gateway.api.util.VersionId;
import org.knime.gateway.api.webui.service.util.MutableServiceCallException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions;
import org.knime.gateway.impl.webui.spaces.SpaceProvider;
import org.knime.gateway.impl.webui.spaces.SpaceProviders;

/**
 * Utility class that encapsulates the logic to load a component.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public final class ComponentLoader {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(ComponentLoader.class);

    private ComponentLoader() {
        // utility class
    }

    /**
     * Loads a component from a command entity.
     *
     * @param commandEnt contains all information required to load the component
     * @param wfm the workflow to load the component into
     * @param spaceProviders provides access to the space for downloading the component
     * @param exec progress and cancellation
     * @return the {@link NodeID} of the loaded component
     * @throws ComponentLoadedWithWarningsException if the component is created but the load result has warnings or
     *             errors
     * @throws CancellationException if loading is canceled
     * @throws CompletionException if loading fails due to download or workflow errors
     */
    public static NodeID loadComponent(final ComponentLoadParameters params, final WorkflowManager wfm,
        final SpaceProviders spaceProviders, final ExecutionMonitor exec) {
        final DownloadedItem downloadedItem;
        try {
            exec.setMessage("Downloading...");
            var provider = spaceProviders.getSpaceProvider(params.providerId());
            downloadedItem = params.spaceId() != null //
                ? downloadComponent(provider, params.spaceId(), params.itemId(), exec) //
                : downloadComponent(provider, params.itemId(), exec);
        } catch (GatewayException ex) {
            LOGGER.debug("Component download failed.", ex);
            throw new CompletionException(compileLoadingFailedErrorMessage(ex), ex);
        } catch (final MutableServiceCallException ex) {
            LOGGER.debug("Failed to fetch component template.", ex);
            throw new CompletionException(ex.toGatewayException("Failed to fetch component template"));
        } catch (final CanceledExecutionException e) {
            LOGGER.debug("Component download cancelled.", e);
            throw new CancellationException(e.getMessage());
        }

        var componentName = params.name();
        LoadResultInternalRoot loadResult;
        try (var lock = wfm.lock()) {
            exec.setMessage("Loading component...");
            loadResult = loadComponent( // throws CanceledExecutionException
                wfm, //
                downloadedItem, //
                componentName, //
                params.insertPosition(), //
                exec //
            );
        } catch (CanceledExecutionException e) {
            LOGGER.debug("Component load cancelled.", e);
            throw new CancellationException(e.getMessage());
        } catch (Throwable t) { // NOSONAR
            var rootCause = ExceptionUtils.getRootCause(t);
            var loadingFailedErrorMessage = compileLoadingFailedErrorMessage(rootCause);
            throw new CompletionException(loadingFailedErrorMessage, rootCause);
        }

        if (!loadResult.getStatus().isOK()) {
            throw new ComponentLoadedWithWarningsException( //
                loadResult.getComponentId(), //
                loadResult.getTitleAndAggregatedMessage().getFirst(), //
                loadResult.getTitleAndAggregatedMessage().getSecond() //
            );
        } else {
            return loadResult.getComponentId();
        }
    }

    /**
     * Signals that a component was created but the load result contains warnings or errors.
     */
    public static class ComponentLoadedWithWarningsException extends RuntimeException {

        private final String m_title;

        private final NodeID m_componentId;

        /**
         * Creates an exception describing a component load with warnings or errors.
         *
         * @param componentId the loaded component id
         * @param title the warning or error title
         * @param message the warning or error details
         */
        public ComponentLoadedWithWarningsException(final NodeID componentId, final String title,
            final String message) {
            super(message);
            m_title = title;
            m_componentId = componentId;
        }

        public String getTitle() {
            return m_title;
        }

        public NodeID getComponentId() {
            return m_componentId;
        }
    }

    private record DownloadedItem(URI sourceUri, Path localPath) {
    }

    private static DownloadedItem downloadComponent(final SpaceProvider spaceProvider, final String itemId,
        final ExecutionMonitor exec) throws CanceledExecutionException, MutableServiceCallException,
        ServiceExceptions.NetworkException, ServiceExceptions.LoggedOutException {
        return new DownloadedItem( //
            spaceProvider.toKnimeUrl(itemId), //
            spaceProvider.toLocalAbsolutePath(exec, itemId, VersionId.currentState()).orElseThrow() //
        );
    }

    private static DownloadedItem downloadComponent(final SpaceProvider spaceProvider, final String spaceId,
        final String itemId, final ExecutionMonitor exec) throws MutableServiceCallException,
        ServiceExceptions.NetworkException, ServiceExceptions.LoggedOutException, CanceledExecutionException {
        var space = spaceProvider.getSpace(spaceId);
        return new DownloadedItem( //
            space.toKnimeUrl(itemId), //
            space.toLocalAbsolutePath(exec, itemId).orElseThrow() //
        );
    }

    private static String compileLoadingFailedErrorMessage(final Throwable cause) {
        var error = "The component could not be created";
        if (cause instanceof FileNotFoundException) {
            error += " because a file could not be found.";
        } else if (cause instanceof IOException) {
            error += " because of an I/O error.";
        } else if (cause instanceof InvalidSettingsException) {
            error += " because the metanode contains invalid settings.";
        } else if (cause instanceof UnsupportedWorkflowVersionException) {
            error += " because the component version is incompatible.";
        } else {
            error += ".";
            LOGGER.error(String.format("Component loading failed with %s: %s", cause.getClass().getSimpleName(),
                cause.getMessage()), cause);
        }
        var causeMessage = StringUtils.defaultIfBlank(cause.getMessage(), "");
        if (!"".equals(causeMessage) && !StringUtils.endsWith(causeMessage, ".")) {
            causeMessage += ".";
        }
        return String.format("%s %s", error, causeMessage);
    }

    private static LoadResultInternalRoot loadComponent(final WorkflowManager parentWFM,
        final DownloadedItem downloadedItem, final String componentName, final Geometry.Point position,
        final ExecutionMonitor executionMonitor)
        throws IOException, UnsupportedWorkflowVersionException, InvalidSettingsException, CanceledExecutionException {
        executionMonitor.checkCanceled();
        var loadHelper = createWorkflowLoadHelper();
        var loadPersistor =
            loadHelper.createTemplateLoadPersistor(downloadedItem.localPath().toFile(), downloadedItem.sourceUri());
        loadPersistor.setNameOverwrite(componentName);
        var loadResult = new MetaNodeLinkUpdateResult("Shared instance from \"" + downloadedItem.sourceUri() + "\"");
        // NXT-3549 (workaround) - cancelation of component load has side effects/bug
        parentWFM.load(loadPersistor, loadResult, executionMonitor.createNonCancelableSubProgress(), false);

        var snc = (SubNodeContainer)loadResult.getLoadedInstance();
        if (snc == null) {
            throw new IllegalStateException("No component returned by load routine, see log for details");
        }
        var info = NodeUIInformation.builder().setNodeLocation(position.x(), position.y(), -1, -1)
            .setHasAbsoluteCoordinates(false).setSnapToGrid(false).setIsDropLocation(true).build();
        snc.setUIInformation(info);
        return new LoadResultInternalRoot(loadResult);
    }

    private static WorkflowLoadHelper createWorkflowLoadHelper() {
        // TODO implement; see, e.g., GUIWorkflowLoadHelper - NXT-3388 (NOSONAR)
        return new WorkflowLoadHelper(true, false, null) {

            @Override
            protected List<Credentials> loadCredentials(final List<Credentials> credentials) {
                return List.of();
            }
        };
    }

    private static class LoadResultInternal { // NOSONAR

        private static final String MESSAGE_NESTING_INDENT = " ".repeat(2);

        private final List<LoadResultInternal> m_childLoadResults;

        private final Status m_status;

        private final String m_message;

        private LoadResultInternal(final LoadResultEntry loadResult, final int nestingLevel,
            final boolean treatDataLoadErrorsAsOK) {
            m_childLoadResults = Arrays.stream(loadResult.getChildren())
                .map(child -> new LoadResultInternal(child, nestingLevel + 1, treatDataLoadErrorsAsOK)).toList();
            m_status = mapLoadResultEntryTypeToStatus(loadResult, treatDataLoadErrorsAsOK, false);
            m_message = MESSAGE_NESTING_INDENT.repeat(nestingLevel) + loadResult.getMessage();
        }

        private static Status mapLoadResultEntryTypeToStatus(final LoadResultEntry loadResult,
            final boolean treatDataLoadErrorsAsOK, final boolean treatStateChangeWarningsAsOK) {
            return switch (loadResult.getType()) {
                case DataLoadError -> treatDataLoadErrorsAsOK ? Status.OK : Status.ERROR;
                case Error -> Status.ERROR;
                case Warning -> mapWarningStatus(loadResult, treatStateChangeWarningsAsOK);
                default -> Status.OK;
            };
        }

        private static Status mapWarningStatus(final LoadResultEntry loadResult,
            final boolean treatStateChangeWarningsAsOK) {
            var nodeStateChanged = loadResult.getCause()
                .map(cause -> cause == LoadResultEntry.LoadResultEntryCause.NodeStateChanged).orElse(Boolean.FALSE);
            return nodeStateChanged.booleanValue() && treatStateChangeWarningsAsOK ? Status.OK : Status.WARNING;
        }

        protected final Status aggregateStatus() {
            return Stream
                .concat(Stream.of(m_status), m_childLoadResults.stream().map(LoadResultInternal::aggregateStatus))
                .max(Comparator.naturalOrder()).orElseThrow();
        }

        protected final String aggregateMessage() {
            return Stream.concat(Stream.of(m_message),
                m_childLoadResults.stream().filter(a -> !a.m_status.isOK()).map(LoadResultInternal::aggregateMessage))
                .collect(Collectors.joining("\n"));
        }

        enum Status {
                OK, WARNING, ERROR;

            /**
             * @return Whether something actually was loaded or not.
             */
            boolean isOK() {
                return this == OK;
            }

        }

    }

    private static class LoadResultInternalRoot extends LoadResultInternal {

        private final MetaNodeLinkUpdateResult m_loadResult;

        private final NodeID m_componentId;

        private final Status m_aggregatedStatus;

        LoadResultInternalRoot(final MetaNodeLinkUpdateResult loadResult) {
            super(loadResult, 0, !loadResult.getGUIMustReportDataLoadErrors());
            m_loadResult = loadResult;
            m_componentId = loadResult.getLoadedInstance().getID();
            m_aggregatedStatus = aggregateStatus();
        }

        NodeID getComponentId() {
            return m_componentId;
        }

        Status getStatus() {
            return m_aggregatedStatus;
        }

        private Pair<String, String> getTitleAndAggregatedMessage() {
            var missingNodes = m_loadResult.getMissingNodes();
            var missingTableFormats = m_loadResult.getMissingTableFormats();
            if (missingNodes.isEmpty() && missingTableFormats.isEmpty()) {
                return Pair.create("Component loaded with problems", aggregateMessage());
            } else {
                var missingExtensions = Stream.concat(missingNodes.stream(), missingTableFormats.stream()) //
                    .map(KNIMEComponentInformation::getComponentName) //
                    .distinct() //
                    .collect(Collectors.joining(", "));
                var missingPrefix = determineMissingPrefix(missingNodes, missingTableFormats);
                var message = switch (m_aggregatedStatus) {
                    case WARNING -> "Warnings during load";
                    default -> "Errors during load";
                };
                var title = "Component requires " + missingPrefix;
                return Pair.create(title, message + " due to " + missingPrefix + " (" + missingExtensions + ").");
            }
        }

        /**
         * (copied from {@link LoadWorkflowRunnable})
         * <p>
         * Depending on what's missing it returns "a missing node extension", "a missing table format extension" and
         * also respects singular/plural.
         */
        private static String determineMissingPrefix(final List<NodeAndBundleInformationPersistor> missingNodes,
            final List<TableStoreFormatInformation> missingTableFormats) {
            var b = new StringBuilder();
            if (missingNodes.size() + missingTableFormats.size() == 1) {
                b.append("a ");
            }
            b.append("missing ");
            if (missingTableFormats.isEmpty()) {
                b.append("node extension");
                if (missingNodes.size() > 1) {
                    b.append("s");
                }
            } else if (missingNodes.isEmpty()) {
                b.append("table format extension");
                if (missingTableFormats.size() > 1) {
                    b.append("s");
                }
            } else {
                b.append("extensions");
            }
            return b.toString();
        }

    }

    /**
     * Parameters for
     * {@link #loadComponent(ComponentLoadParameters, WorkflowManager, SpaceProviders, ExecutionMonitor)}.
     *
     * @param providerId the space provider identifier
     * @param spaceId the space identifier, nullable
     * @param itemId the component item identifier
     * @param name the component name used for the placeholder node
     * @param insertPosition the workflow canvas position for the placeholder
     *
     * @since 5.11
     */
    public static final record ComponentLoadParameters(String providerId, String spaceId, String itemId, String name,
            Geometry.Point insertPosition) {
        public ComponentLoadParameters {
            CheckUtils.checkArgumentNotNull(providerId);
            // space id is nullable
            CheckUtils.checkArgumentNotNull(itemId);
            CheckUtils.checkArgumentNotNull(name);
            CheckUtils.checkArgumentNotNull(insertPosition);

        }
    }
}
