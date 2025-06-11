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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
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
import org.knime.gateway.api.webui.entity.AddComponentCommandEnt;
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
     * Loads a component from a {@link AddComponentCommandEnt}.
     *
     * @param commandEnt contain the all the infos required to load the component
     * @param wfm the workflow to load the component in
     * @param spaceProviders to be able to access the space to (down-)load the component from
     * @param exec progress and cancellation
     * @return infos about the loaded component
     * @throws CanceledExecutionException in case the component loading was canceled
     */
    public static LoadResult loadComponent(final AddComponentCommandEnt commandEnt, final WorkflowManager wfm,
        final SpaceProviders spaceProviders, final ExecutionMonitor exec) throws CanceledExecutionException {
        var space = spaceProviders.getSpace(commandEnt.getProviderId(), commandEnt.getSpaceId());
        var uri = space.toKnimeUrl(commandEnt.getItemId());
        exec.setMessage("Downloading...");
        var localPath = space.toLocalAbsolutePath(exec, commandEnt.getItemId()).orElseThrow();
        var componentName = commandEnt.getName();
        try (var lock = wfm.lock()) {
            exec.setMessage("Loading component...");
            var position = Geometry.Point.of(commandEnt.getPosition());
            var loadResult = loadComponent(wfm, localPath.toFile(), uri, componentName, position, exec);
            var isOk = loadResult.getStatus().isOK();
            return new LoadResult( //
                loadResult.getComponentId(), //
                isOk ? null : loadResult.getTitleAndAggregatedMessage().getFirst(), //
                isOk ? null : loadResult.getTitleAndAggregatedMessage().getSecond() //
            );
        } catch (CanceledExecutionException e) {
            throw e;
        } catch (Throwable t) { // NOSONAR
            var rootCause = ExceptionUtils.getRootCause(t);
            var loadingFailedErrorMessage = compileLoadingFailedErrorMessage(rootCause);
            throw new CompletionException(loadingFailedErrorMessage, rootCause);
        }
    }

    /**
     * Represents the load result once a component loaded successfully.
     *
     * @param componentId the id of the successfully added component
     * @param message a message on warnings/problems during load; can be {@code null}
     * @param details some more details regarding the loading problems; can be {@code null}
     */
    public record LoadResult(NodeID componentId, String message, String details) {

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

    private static LoadResultInternalRoot loadComponent(final WorkflowManager parentWFM, final File parentFile,
        final URI templateURI, final String componentName, final Geometry.Point position,
        final ExecutionMonitor executionMonitor)
        throws IOException, UnsupportedWorkflowVersionException, InvalidSettingsException, CanceledExecutionException {
        executionMonitor.checkCanceled();
        var loadHelper = createWorkflowLoadHelper();
        var loadPersistor = loadHelper.createTemplateLoadPersistor(parentFile, templateURI);
        loadPersistor.setNameOverwrite(componentName);
        var loadResult = new MetaNodeLinkUpdateResult("Shared instance from \"" + templateURI + "\"");
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
                case Warning -> {
                    var nodeStateChanged = loadResult.getCause()
                        .map(cause -> cause == LoadResultEntry.LoadResultEntryCause.NodeStateChanged).orElse(false);
                    if (nodeStateChanged && treatStateChangeWarningsAsOK) {
                        yield Status.OK;
                    } else {
                        yield Status.WARNING;
                    }
                }
                default -> Status.OK;
            };
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
         *
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

}
