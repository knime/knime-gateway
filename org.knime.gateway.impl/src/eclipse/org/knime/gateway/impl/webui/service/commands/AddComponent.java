package org.knime.gateway.impl.webui.service.commands;

import static org.knime.gateway.api.entity.EntityBuilderManager.builder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.knime.core.data.container.storage.TableStoreFormatInformation;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.KNIMEComponentInformation;
import org.knime.core.node.NodeAndBundleInformationPersistor;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.workflow.Credentials;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.NodeUIInformation;
import org.knime.core.node.workflow.UnsupportedWorkflowVersionException;
import org.knime.core.node.workflow.WorkflowLoadHelper;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.node.workflow.WorkflowPersistor;
import org.knime.core.node.workflow.WorkflowPersistor.LoadResultEntry;
import org.knime.core.node.workflow.WorkflowPersistor.LoadResultEntry.LoadResultEntryCause;
import org.knime.core.node.workflow.WorkflowPersistor.MetaNodeLinkUpdateResult;
import org.knime.core.node.workflow.contextv2.WorkflowContextV2;
import org.knime.core.util.FileUtil;
import org.knime.core.util.LoadVersion;
import org.knime.core.util.Version;
import org.knime.core.util.exception.ResourceAccessException;
import org.knime.core.util.pathresolve.ResolverUtil;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.AddComponentCommandEnt;
import org.knime.gateway.api.webui.entity.AddComponentResultEnt.AddComponentResultEntBuilder;
import org.knime.gateway.api.webui.entity.CommandResultEnt;
import org.knime.gateway.api.webui.entity.CommandResultEnt.KindEnum;
import org.knime.gateway.api.webui.entity.ProblemMessageEnt.ProblemMessageEntBuilder;
import org.knime.gateway.api.webui.entity.ProblemMessageEnt.TypeEnum;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.OperationNotAllowedException;
import org.knime.gateway.impl.service.util.WorkflowChangesTracker.WorkflowChange;
import org.knime.gateway.impl.webui.spaces.SpaceProvider;
import org.knime.gateway.impl.webui.spaces.SpaceProviders;

/**
 * TODO
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
final class AddComponent extends AbstractWorkflowCommand implements WithResult {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(AddComponent.class);

    private final AddComponentCommandEnt m_commandEnt;

    private final SpaceProviders m_spaceProviders;

    private ComponentLoadResult m_loadResult;

    AddComponent(final AddComponentCommandEnt commandEnt, final SpaceProviders spaceProviders) {
        m_commandEnt = commandEnt;
        m_spaceProviders = spaceProviders;
    }

    @Override
    protected boolean executeWithLockedWorkflow() throws OperationNotAllowedException {
        var space = m_spaceProviders.getSpace(m_commandEnt.getProviderId(), m_commandEnt.getSpaceId());
        var uri = space.toKnimeUrl(m_commandEnt.getItemId());
        var isRemoteLocation = !SpaceProvider.LOCAL_SPACE_PROVIDER_ID.equals(m_commandEnt.getProviderId());
        var wfm = getWorkflowManager();
        m_loadResult = loadComponentAndCatchErrors(wfm, stripWorkflowDotKnime(uri), m_commandEnt.getPosition().getX(),
            m_commandEnt.getPosition().getY(), isRemoteLocation, false);
        return m_loadResult.status.isOK();
    }

    private static URI stripWorkflowDotKnime(final URI templateURI) {
        //strip "workflow.knime" from the URI which is append if
        //local component project is opened in workflow editor
        if (templateURI.toString().endsWith(WorkflowPersistor.WORKFLOW_FILE)) {
            String s = templateURI.toString();
            try {
                return new URI(s.substring(0, s.length() - WorkflowPersistor.WORKFLOW_FILE.length() - 1));
            } catch (URISyntaxException e) {
                //should never happen
                throw new RuntimeException(e);
            }
        } else {
            return templateURI;
        }
    }

    @Override
    public boolean canUndo() {
        return getWorkflowManager().canRemoveNode(m_loadResult.componentId);
    }

    @Override
    public void undo() throws OperationNotAllowedException {
        getWorkflowManager().removeNode(m_loadResult.componentId);
        m_loadResult = null;
    }

    private static ComponentLoadResult loadComponentAndCatchErrors(final WorkflowManager wfm, final URI templateURI,
        final int x, // NOSONAR
        final int y, final boolean isRemoteLocation, final boolean snapToGrid) {
        try {
            return loadComponent(wfm, templateURI, x, y, isRemoteLocation, snapToGrid, true);
        } catch (Throwable t) {
            final var cause = ExceptionUtils.getRootCause(t);
            if ((cause instanceof CanceledExecutionException) || (cause instanceof InterruptedException)) {
                LOGGER.info("Metanode loading was canceled by the user", cause);
                return new ComponentLoadResult(Status.WARNING, null, null, null);
            } else {
                return openErrorOnFailedComponentCreation(cause);
            }
        }
    }

    private static ComponentLoadResult openErrorOnFailedComponentCreation(final Throwable cause) {
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
        return new ComponentLoadResult(Status.ERROR, "Component could not be created",
            String.format("%s %s", error, causeMessage), null);
    }

    private static ComponentLoadResult loadComponent(final WorkflowManager parentWFM, final URI templateURI,
        final int x, final int y, final boolean isRemoteLocation /* TODO */, final boolean snapToGrid,
        final boolean deleteFileAfterLoad /* TODO */) {
        try {
            var pm = createProgressMonitor();
            var parentFile = resolveURIToLocalFile(templateURI, deleteFileAfterLoad, pm);
            if (pm.isCanceled()) {
                throw new InterruptedException();
            }

            var isComponentProject = false; // TODO NXT-3390
            final var loadHelper = createWorkflowLoadHelper(parentFile.getName(), null /*context*/, isComponentProject);
            final var loadPersistor = loadHelper.createTemplateLoadPersistor(parentFile, templateURI);
            final var loadResult = new MetaNodeLinkUpdateResult("Shared instance from \"" + templateURI + "\"");
            parentWFM.load(loadPersistor, loadResult, new ExecutionMonitor(/* TODO progress monitor?*/), false);

            if (pm.isCanceled()) {
                throw new InterruptedException();
            }
            pm.subTask("Finished.");
            pm.done();

            var container = (NodeContainer)loadResult.getLoadedInstance();
            if (container == null) {
                // TODO
                throw new RuntimeException("No template returned by load routine, see log for details");
            }
            // create extra info and set it
            NodeUIInformation info = NodeUIInformation.builder().setNodeLocation(x, y, -1, -1)
                .setHasAbsoluteCoordinates(false).setSnapToGrid(snapToGrid).setIsDropLocation(true).build();
            container.setUIInformation(info);

            // TODO
//            if (container instanceof SubNodeContainer snc) {
//                SubNodeContainer projectComponent = parentWFM.getProjectComponent().orElse(null);
//                if (projectComponent != null) {
//                    // unlink component if it's added to itself
//                    MetaNodeTemplateInformation projectTemplateInformation = projectComponent.getTemplateInformation();
//                    MetaNodeTemplateInformation templateInformation = snc.getTemplateInformation();
//                    if (Objects.equals(templateInformation.getSourceURI(), projectTemplateInformation.getSourceURI())) {
//                        MessageDialog.openWarning(SWTUtilities.getActiveShell(), "Disconnect Link",
//                            "Components can only be added to themselves without linking. Will be disconnected.");
//                        container.getParent().setTemplateInformation(container.getID(),
//                            MetaNodeTemplateInformation.NONE);
//                    }
//                }
//            }

            return mapMetaNodeLinkUpdateResultToComponentLoadResult(loadResult, isComponentProject);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            // IMPORTANT: Remove the reference to the file and the editor!!! Otherwise the memory cannot be freed later
            // TODO necessary?
            // m_parentWFM = null;
        }
    }

    private static File resolveURIToLocalFile(final URI templateURI, final boolean deleteFileAfterLoad,
        final IProgressMonitor pm) throws ResourceAccessException, IOException {
        var parentFile = ResolverUtil.resolveURItoLocalOrTempFile(templateURI, pm);
        if (parentFile.isFile()) {
            //unzip
            final var tempDir = FileUtil.createTempDir("template-workflow");
            FileUtil.unzip(parentFile, tempDir);
            if (deleteFileAfterLoad) {
                Files.delete(parentFile.toPath());
            }
            final var extractedFiles = tempDir.listFiles();
            if (extractedFiles.length == 0) {
                throw new IOException("Unzipping of file '" + parentFile + "' failed");
            }
            parentFile = extractedFiles[0];
        }
        return parentFile;
    }

    private static ComponentLoadResult mapMetaNodeLinkUpdateResultToComponentLoadResult(
        final MetaNodeLinkUpdateResult loadResult, final boolean isComponentProject) {
        // components are always stored without data
        // -> don't report data load errors neither node state changes if component is loaded as project
        final Status status = mapMetaNodeLinkUpdateResultToStatus(loadResult,
            !loadResult.getGUIMustReportDataLoadErrors() || isComponentProject, isComponentProject);
        var nc = (NodeContainer) loadResult.getLoadedInstance();
        var nodeId = nc.getID();

        if (status.isOK()) {
            return new ComponentLoadResult(status, null, null, nodeId);
        }

        String title = "Component load";
        String message = switch (status) {
            case WARNING -> "Warnings during load";
            default -> "Errors during load";
        };

        List<NodeAndBundleInformationPersistor> missingNodes = loadResult.getMissingNodes();
        List<TableStoreFormatInformation> missingTableFormats = loadResult.getMissingTableFormats();
        if (!missingNodes.isEmpty() || !missingTableFormats.isEmpty()) {
            String missingExtensions = Stream.concat(missingNodes.stream(), missingTableFormats.stream()) //
                .map(KNIMEComponentInformation::getComponentName) //
                .distinct() //
                .collect(Collectors.joining(", "));

            String missingPrefix = determineMissingPrefix(missingNodes, missingTableFormats);

            title = "Component requires " + missingPrefix;
            message = message + " due to " + missingPrefix + " (" + missingExtensions
                + "). Do you want to search and install the required extensions?";
            // TODO
            //            if ((dialog.open() == 0) && AbstractP2Action.checkSDKAndReadOnly()) {
            //                Job j = new InstallMissingNodesJob(missingNodes, missingTableFormats);
            //                j.setUser(true);
            //                j.schedule();
            //            }
        }

        return new ComponentLoadResult(status, title, message, nodeId);
    }

    /**
     * (copied from LoadWorkflowRunnable)
     *
     * Depending on what's missing it returns "a missing node extension", "a missing table format extension" and also
     * respects singular/plural.
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

    private static WorkflowLoadHelper createWorkflowLoadHelper(final String workflowName /* TODO */,
        final WorkflowContextV2 context, final boolean isComponentProject) {
        // TODO NXT-3388; see GUIWorkflowLoadHelper
        return new WorkflowLoadHelper(true, isComponentProject, context) {

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

    private static IProgressMonitor createProgressMonitor() {
        // TODO
        return new IProgressMonitor() {

            @Override
            public void worked(final int work) {
                // TODO Auto-generated method stub

            }

            @Override
            public void subTask(final String name) {
                // TODO Auto-generated method stub

            }

            @Override
            public void setTaskName(final String name) {
                // TODO Auto-generated method stub

            }

            @Override
            public void setCanceled(final boolean value) {
                // TODO Auto-generated method stub

            }

            @Override
            public boolean isCanceled() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public void internalWorked(final double work) {
                // TODO Auto-generated method stub

            }

            @Override
            public void done() {
                // TODO Auto-generated method stub

            }

            @Override
            public void beginTask(final String name, final int totalWork) {
                // TODO Auto-generated method stub

            }
        };
    }

    private static Status mapMetaNodeLinkUpdateResultToStatus(final LoadResultEntry loadResult, final boolean treatDataLoadErrorsAsOK,
        final boolean treatStateChangeWarningsAsOK) {
        LoadResultEntry[] children = loadResult.getChildren();
        if (children.length == 0) {
            Status status;
            switch (loadResult.getType()) {
                case DataLoadError:
                    status = treatDataLoadErrorsAsOK ? Status.OK : Status.ERROR;
                    break;
                case Error:
                    status = Status.ERROR;
                    break;
                case Warning:
                    if (treatStateChangeWarningsAsOK && loadResult.getCause().isPresent()
                        && loadResult.getCause().get().equals(LoadResultEntryCause.NodeStateChanged)) {
                        status = Status.OK;
                    } else {
                        status = Status.WARNING;
                    }
                    break;
                default:
                    status = Status.OK;
            }
            return status;
        }
        var subStatus = new Status[children.length];
        for (int i = 0; i < children.length; i++) {
            subStatus[i] = mapMetaNodeLinkUpdateResultToStatus(children[i], treatDataLoadErrorsAsOK, treatStateChangeWarningsAsOK);
        }
        // TODO double-check
        return Arrays.stream(subStatus).max(Comparator.naturalOrder()).orElseThrow();
    }

    private enum Status {
            OK, WARNING, ERROR;

        boolean isOK() {
            return this == OK;
        }

    }

    @Override
    public CommandResultEnt buildEntity(final String snapshotId) {
        var builder = builder(AddComponentResultEntBuilder.class)//
            .setKind(KindEnum.ADD_NODE_RESULT)//
            .setNewNodeId(new NodeIDEnt(m_loadResult.componentId))//
            .setSnapshotId(snapshotId);
        if (!m_loadResult.status.isOK()) {
            builder.setProblem(builder(ProblemMessageEntBuilder.class)
                .setType(m_loadResult.status == Status.WARNING ? TypeEnum.WARNING : TypeEnum.ERROR) //
                .setTitle(m_loadResult.problemTitle) //
                .setMessage(m_loadResult.problemMessage) //
                .build());
        }
        return builder.build();
    }

    @Override
    public Set<WorkflowChange> getChangesToWaitFor() {
        return Collections.singleton(WorkflowChange.NODE_ADDED);
    }

    private record ComponentLoadResult(Status status, String problemTitle, String problemMessage, NodeID componentId) {

    }

}