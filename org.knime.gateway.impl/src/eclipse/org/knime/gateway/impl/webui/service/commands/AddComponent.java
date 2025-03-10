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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.knime.core.data.container.storage.TableStoreFormatInformation;
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
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.AddComponentCommandEnt;
import org.knime.gateway.api.webui.entity.AddComponentResultEnt.AddComponentResultEntBuilder;
import org.knime.gateway.api.webui.entity.CommandResultEnt;
import org.knime.gateway.api.webui.entity.CommandResultEnt.KindEnum;
import org.knime.gateway.api.webui.entity.ProblemMessageEnt.ProblemMessageEntBuilder;
import org.knime.gateway.api.webui.entity.ProblemMessageEnt.TypeEnum;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.ServiceCallException;
import org.knime.gateway.impl.service.util.WorkflowChangesTracker.WorkflowChange;
import org.knime.gateway.impl.webui.spaces.SpaceProviders;

/**
 * Command to (down-)load and add a component to a workflow from a given item-id.
 *
 * Logic 'inspired' by/copied from
 * org.knime.workbench.editor2.commands.CreateMetaNodeTemplateCommand.createMetaNodeTemplate and
 * org.knime.workbench.editor2.LoadMetaNodeTemplateRunnable.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
final class AddComponent extends AbstractWorkflowCommand implements WithResult {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(AddComponent.class);

    private final AddComponentCommandEnt m_commandEnt;

    private final SpaceProviders m_spaceProviders;

    private LoadResultInternalRoot m_loadResult;

    AddComponent(final AddComponentCommandEnt commandEnt, final SpaceProviders spaceProviders) {
        m_commandEnt = commandEnt;
        m_spaceProviders = spaceProviders;
    }

    @Override
    protected boolean executeWithLockedWorkflow() throws ServiceCallException {
        var space = m_spaceProviders.getSpace(m_commandEnt.getProviderId(), m_commandEnt.getSpaceId());
        var uri = space.toKnimeUrl(m_commandEnt.getItemId());
        var localPath = space.toLocalAbsolutePath(new ExecutionMonitor(), m_commandEnt.getItemId()).orElseThrow();
        var wfm = getWorkflowManager();
        try {
            m_loadResult = loadComponent(wfm, localPath.toFile(), uri, m_commandEnt.getPosition().getX(),
                m_commandEnt.getPosition().getY(), false);
            return m_loadResult.getStatus().isOK();
        } catch (Throwable t) {
            var loadingFailedErrorMessage = compileLoadingFailedErrorMessage(ExceptionUtils.getRootCause(t));
            throw new ServiceCallException(loadingFailedErrorMessage);
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
        return getWorkflowManager().canRemoveNode(m_loadResult.getComponentId());
    }

    @Override
    public void undo() throws ServiceCallException {
        getWorkflowManager().removeNode(m_loadResult.getComponentId());
        m_loadResult = null;
    }

    private static LoadResultInternalRoot loadComponent(final WorkflowManager parentWFM, final File parentFile,
        final URI templateURI, final int x, final int y, final boolean snapToGrid) {
        try {
            final var loadHelper = createWorkflowLoadHelper();
            final var loadPersistor = loadHelper.createTemplateLoadPersistor(parentFile, templateURI);
            final var loadResult = new MetaNodeLinkUpdateResult("Shared instance from \"" + templateURI + "\"");
            parentWFM.load(loadPersistor, loadResult, new ExecutionMonitor(), false);

            var snc = (SubNodeContainer)loadResult.getLoadedInstance();
            if (snc == null) {
                throw new RuntimeException("No component returned by load routine, see log for details");
            }
            // create extra info and set it
            var info = NodeUIInformation.builder().setNodeLocation(x, y, -1, -1).setHasAbsoluteCoordinates(false)
                .setSnapToGrid(snapToGrid).setIsDropLocation(true).build();
            snc.setUIInformation(info);
            return new LoadResultInternalRoot(loadResult);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private static WorkflowLoadHelper createWorkflowLoadHelper() {
        // TODO NXT-3388; see GUIWorkflowLoadHelper
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
        return m_loadResult.buildCommandResultEnt(snapshotId);
    }

    @Override
    public Set<WorkflowChange> getChangesToWaitFor() {
        return Collections.singleton(WorkflowChange.NODE_ADDED);
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

        CommandResultEnt buildCommandResultEnt(final String snapshotId) {
            var builder = builder(AddComponentResultEntBuilder.class)//
                .setKind(KindEnum.ADD_NODE_RESULT)//
                .setNewNodeId(new NodeIDEnt(m_componentId))//
                .setSnapshotId(snapshotId);
            if (!m_aggregatedStatus.isOK()) {
                var titleAndMessage = getTitleAndAggregatedMessage();
                builder.setProblem(builder(ProblemMessageEntBuilder.class)
                    .setType(m_aggregatedStatus == Status.WARNING ? TypeEnum.WARNING : TypeEnum.ERROR) //
                    .setTitle(titleAndMessage.getFirst()) //
                    .setMessage(titleAndMessage.getSecond()) //
                    .build());
            }
            return builder.build();
        }

        private Pair<String, String> getTitleAndAggregatedMessage() {
            var missingNodes = m_loadResult.getMissingNodes();
            var missingTableFormats = m_loadResult.getMissingTableFormats();
            if (missingNodes.isEmpty() && missingTableFormats.isEmpty()) {
                return Pair.create("Component load", aggregateMessage());
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
         * (copied from LoadWorkflowRunnable)
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
                        && loadResult.getCause().get().equals(LoadResultEntry.LoadResultEntryCause.NodeStateChanged)) {
                        yield Status.OK;
                    } else {
                        yield Status.WARNING;
                    }
                }
                default -> Status.OK;
            };
        }

        protected Status aggregateStatus() {
            return Stream
                .concat(Stream.of(m_status), m_childLoadResults.stream().map(LoadResultInternal::aggregateStatus))
                // TODO double-check order
                .max(Comparator.naturalOrder()).orElseThrow();
        }

        protected String aggregateMessage() {
            return Stream.concat(Stream.of(m_message),
                m_childLoadResults.stream().filter(a -> !a.m_status.isOK()).map(LoadResultInternal::aggregateMessage))
                .collect(Collectors.joining("\n"));
        }

        enum Status {
                OK, WARNING, ERROR;

            boolean isOK() {
                return this == OK;
            }

        }

    }

}