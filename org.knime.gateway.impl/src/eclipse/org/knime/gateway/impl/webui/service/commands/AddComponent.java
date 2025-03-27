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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
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
import org.knime.core.node.workflow.Credentials;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.NodeUIInformation;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.node.workflow.UnsupportedWorkflowVersionException;
import org.knime.core.node.workflow.WorkflowLoadHelper;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.node.workflow.WorkflowPersistor.LoadResultEntry;
import org.knime.core.node.workflow.WorkflowPersistor.MetaNodeLinkUpdateResult;
import org.knime.core.util.LoadVersion;
import org.knime.core.util.Pair;
import org.knime.core.util.Version;
import org.knime.gateway.api.webui.entity.AddComponentCommandEnt;
import org.knime.gateway.api.webui.entity.AddComponentPlaceholderResultEnt.AddComponentPlaceholderResultEntBuilder;
import org.knime.gateway.api.webui.entity.CommandResultEnt;
import org.knime.gateway.api.webui.entity.CommandResultEnt.KindEnum;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.ServiceCallException;
import org.knime.gateway.impl.service.util.WorkflowChangesTracker.WorkflowChange;
import org.knime.gateway.impl.webui.ComponentLoader.LoadJob;
import org.knime.gateway.impl.webui.ComponentLoader.LoadResult;
import org.knime.gateway.impl.webui.WorkflowMiddleware;
import org.knime.gateway.impl.webui.spaces.SpaceProviders;

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

    private static final NodeLogger LOGGER = NodeLogger.getLogger(AddComponent.class);

    private final AddComponentCommandEnt m_commandEnt;

    private final SpaceProviders m_spaceProviders;

    private final WorkflowMiddleware m_workflowMiddleware;

    private LoadJob m_loadJob;

    AddComponent(final AddComponentCommandEnt commandEnt, final SpaceProviders spaceProviders,
        final WorkflowMiddleware workflowMiddleware) {
        super(false);
        m_commandEnt = commandEnt;
        m_spaceProviders = spaceProviders;
        m_workflowMiddleware = workflowMiddleware;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean executeWithWorkflowContext() throws ServiceCallException {
        var componentLoader = m_workflowMiddleware.getComponentLoader(getWorkflowKey());
        m_loadJob = componentLoader.createComponentLoadJob(m_commandEnt.getPosition().getX(),
            m_commandEnt.getPosition().getY(), this::loadComponent);
        return true;
    }

    private LoadResult loadComponent(final ExecutionMonitor exec) throws CanceledExecutionException {
        var wfm = getWorkflowManager();
        var space = m_spaceProviders.getSpace(m_commandEnt.getProviderId(), m_commandEnt.getSpaceId());
        var uri = space.toKnimeUrl(m_commandEnt.getItemId());
        exec.setMessage("Downloading ...");
        var localPath = space.toLocalAbsolutePath(exec, m_commandEnt.getItemId()).orElseThrow();
        try (var lock = wfm.lock()) {
            exec.setMessage("Loading component ...");
            var loadResult = loadComponent(wfm, localPath.toFile(), uri, m_commandEnt.getPosition().getX(),
                m_commandEnt.getPosition().getY(), false, exec);
            var isOk = loadResult.getStatus().isOK();
            return new LoadResult(loadResult.getComponentId(),
                isOk ? null : loadResult.getTitleAndAggregatedMessage().getFirst(),
                isOk ? null : loadResult.getTitleAndAggregatedMessage().getSecond());
        } catch (CanceledExecutionException e) {
            throw e;
        } catch (Throwable t) { // NOSONAR
            var rootCause = ExceptionUtils.getRootCause(t);
            var loadingFailedErrorMessage = compileLoadingFailedErrorMessage(rootCause);
            throw new CompletionException(loadingFailedErrorMessage, rootCause);
        }
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

    @Override
    public boolean canUndo() {
        var componentId = getComponentId();
        return componentId == null || getWorkflowManager().canRemoveNode(componentId);
    }

    @Override
    public void undo() throws ServiceCallException {
        var componentId = getComponentId();
        if (componentId == null) {
            var workflowElementLoader = m_workflowMiddleware.getComponentLoader(getWorkflowKey());
            workflowElementLoader.cancelAndRemoveLoadJob(m_loadJob.id());
        } else {
            getWorkflowManager().removeNode(componentId);
        }
        m_loadJob = null;
    }

    private NodeID getComponentId() {
        var future = m_loadJob.future();
        try {
            var res = future.getNow(null);
            if (res != null) {
                return res.componentId();
            }
        } catch (CompletionException | CancellationException e) { // NOSONAR
            //
        }
        return null;
    }

    /*
     * @return The internal result of the component loading, that also could have loaded with problems.
     * @throws IOException
     * @throws UnsupportedWorkflowVersionException
     * @throws InvalidSettingsException
     * @throws CanceledExecutionException
     * @throws IllegalStateException
     */
    private static LoadResultInternalRoot loadComponent(final WorkflowManager parentWFM, final File parentFile,
        final URI templateURI, final int x, final int y, final boolean snapToGrid,
        final ExecutionMonitor executionMonitor)
        throws IOException, UnsupportedWorkflowVersionException, InvalidSettingsException, CanceledExecutionException {
        executionMonitor.checkCanceled();
        var loadHelper = createWorkflowLoadHelper();
        var loadPersistor = loadHelper.createTemplateLoadPersistor(parentFile, templateURI);
        var loadResult = new MetaNodeLinkUpdateResult("Shared instance from \"" + templateURI + "\"");
        parentWFM.load(loadPersistor, loadResult, executionMonitor, false);

        var snc = (SubNodeContainer)loadResult.getLoadedInstance();
        if (snc == null) {
            throw new IllegalStateException("No component returned by load routine, see log for details");
        }
        // create extra info and set it
        var info = NodeUIInformation.builder().setNodeLocation(x, y, -1, -1).setHasAbsoluteCoordinates(false)
            .setSnapToGrid(snapToGrid).setIsDropLocation(true).build();
        snc.setUIInformation(info);
        return new LoadResultInternalRoot(loadResult);
    }

    private static WorkflowLoadHelper createWorkflowLoadHelper() {
        // TODO implement; see, e.g., GUIWorkflowLoadHelper - NXT-3388
        return new WorkflowLoadHelper(true, false, null) {

            @Override
            public UnknownKNIMEVersionLoadPolicy getUnknownKNIMEVersionLoadPolicy(
                final LoadVersion workflowKNIMEVersion, final Version createdByKNIMEVersion,
                final boolean isNightlyBuild) {
                return UnknownKNIMEVersionLoadPolicy.Abort;
            }

            @Override
            protected List<Credentials> loadCredentials(final List<Credentials> credentials) {
                return List.of();
            }
        };
    }

    @Override
    public CommandResultEnt buildEntity(final String snapshotId) {
        return builder(AddComponentPlaceholderResultEntBuilder.class).setKind(KindEnum.ADD_COMPONENT_PLACEHOLDER_RESULT)
            .setNewPlaceholderId(m_loadJob.id()).setSnapshotId(snapshotId).build();
    }

    @Override
    public Set<WorkflowChange> getChangesToWaitFor() {
        return Collections.singleton(WorkflowChange.COMPONENT_PLACEHOLDER_ADDED);
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
            StringBuilder b = new StringBuilder();
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

    private static class LoadResultInternal {

        private static final String MESSAGE_NESTING_INDENT = " ".repeat(2);

        private List<LoadResultInternal> m_childLoadResults;

        private final Status m_status;

        private final String m_message;

        private final int m_nestingLevel;

        private LoadResultInternal(final LoadResultEntry loadResult, final int nestingLevel,
            final boolean treatDataLoadErrorsAsOK) {
            m_nestingLevel = nestingLevel;
            m_childLoadResults = Arrays.stream(loadResult.getChildren())
                .map(child -> new LoadResultInternal(child, nestingLevel + 1, treatDataLoadErrorsAsOK)).toList();
            m_status = mapLoadResultEntryTypeToStatus(loadResult, treatDataLoadErrorsAsOK, false);
            m_message = MESSAGE_NESTING_INDENT.repeat(m_nestingLevel) + loadResult.getMessage();
        }

        private static Status mapLoadResultEntryTypeToStatus(final LoadResultEntry loadResult,
            final boolean treatDataLoadErrorsAsOK, final boolean treatStateChangeWarningsAsOK) {
            return switch (loadResult.getType()) {
                case DataLoadError -> treatDataLoadErrorsAsOK ? Status.OK : Status.ERROR;
                case Error -> Status.ERROR;
                case Warning -> {
                    if (treatStateChangeWarningsAsOK && loadResult.getCause().isPresent()
                        && loadResult.getCause().get() == LoadResultEntry.LoadResultEntryCause.NodeStateChanged) {
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

}