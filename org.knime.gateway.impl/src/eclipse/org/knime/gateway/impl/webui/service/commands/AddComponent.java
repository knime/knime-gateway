package org.knime.gateway.impl.webui.service.commands;

import static org.knime.gateway.api.entity.EntityBuilderManager.builder;

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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.workflow.Credentials;
import org.knime.core.node.workflow.MetaNodeTemplateInformation;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.NodeUIInformation;
import org.knime.core.node.workflow.SubNodeContainer;
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
import org.knime.core.util.pathresolve.ResolverUtil;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.AddComponentCommandEnt;
import org.knime.gateway.api.webui.entity.AddNodeResultEnt.AddNodeResultEntBuilder;
import org.knime.gateway.api.webui.entity.CommandResultEnt;
import org.knime.gateway.api.webui.entity.CommandResultEnt.KindEnum;
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

    private NodeID m_componentId;

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
        var snc = loadComponent(wfm, uri, m_commandEnt.getPosition().getX(), m_commandEnt.getPosition().getY(),
            isRemoteLocation, false);
        if (snc == null) {
            return false;
        }
        m_componentId = snc.getID();
        return true; // TODO
    }

    @Override
    public boolean canUndo() {
        return getWorkflowManager().canRemoveNode(m_componentId);
    }

    @Override
    public void undo() throws OperationNotAllowedException {
        getWorkflowManager().removeNode(m_componentId);
        m_componentId = null;
    }

    /**
     * Creates a meta-node template (i.e. adds a component or metanode from a url to the workflow).
     *
     * @param wfm
     * @param templateURIParam
     * @param x
     * @param y
     * @param isRemoteLocation
     * @param snapToGrid
     * @return the added node or {@code null} if it failed
     */
    private static NodeContainer loadComponent(final WorkflowManager wfm, final URI templateURIParam, final int x, // NOSONAR
        final int y, final boolean isRemoteLocation, final boolean snapToGrid) {

        //strip "workflow.knime" from the URI which is append if
        //local component project is opened in workflow editor
        URI templateURI;
        if (templateURIParam.toString().endsWith(WorkflowPersistor.WORKFLOW_FILE)) {
            String s = templateURIParam.toString();
            try {
                templateURI = new URI(s.substring(0, s.length() - WorkflowPersistor.WORKFLOW_FILE.length() - 1));
            } catch (URISyntaxException e) {
                //should never happen
                throw new RuntimeException(e);
            }
        } else {
            templateURI = templateURIParam;
        }

        NodeContainer container = null;
        try {
            MetaNodeLinkUpdateResult result = loadComponent(wfm, templateURI, true);
            container = (NodeContainer)result.getLoadedInstance();
            if (container == null) {
                throw new RuntimeException("No template returned by load routine, see log for details");
            }
            // create extra info and set it
            NodeUIInformation info = NodeUIInformation.builder().setNodeLocation(x, y, -1, -1)
                .setHasAbsoluteCoordinates(false).setSnapToGrid(snapToGrid).setIsDropLocation(true).build();
            container.setUIInformation(info);

            if (container instanceof SubNodeContainer snc) {
                SubNodeContainer projectComponent = wfm.getProjectComponent().orElse(null);
                if (projectComponent == null) {
                    return container;
                }

                // unlink component if it's added to itself
                MetaNodeTemplateInformation projectTemplateInformation = projectComponent.getTemplateInformation();
                MetaNodeTemplateInformation templateInformation = snc.getTemplateInformation();

                // TODO
                //                if (Objects.equals(templateInformation.getSourceURI(), projectTemplateInformation.getSourceURI())) {
                //                    MessageDialog.openWarning(SWTUtilities.getActiveShell(), "Disconnect Link",
                //                        "Components can only be added to themselves without linking. Will be disconnected.");
                //                    container.getParent().setTemplateInformation(container.getID(), MetaNodeTemplateInformation.NONE);
                //                }
            }
        } catch (Throwable t) {
            final var cause = ExceptionUtils.getRootCause(t);
            if ((cause instanceof CanceledExecutionException) || (cause instanceof InterruptedException)) {
                LOGGER.info("Metanode loading was canceled by the user", cause);
            } else {
                openErrorOnFailedNodeCreation(cause);
            }
        }

        return container;
    }

    private static void openErrorOnFailedNodeCreation(final Throwable cause) {
        var error = "The selected node could not be created";
        if (cause instanceof FileNotFoundException) {
            error += " because a file could not be found.";
        } else if (cause instanceof IOException) {
            error += " because of an I/O error.";
        } else if (cause instanceof InvalidSettingsException) {
            error += " because the metanode contains invalid settings.";
        } else if (cause instanceof UnsupportedWorkflowVersionException) {
            error += " because the metanode version is incompatible.";
        } else {
            error += ".";
            LOGGER.error(String.format("Metanode loading failed with %s: %s", cause.getClass().getSimpleName(),
                cause.getMessage()), cause);
        }
        var causeMessage = StringUtils.defaultIfBlank(cause.getMessage(), "");
        if (!"".equals(causeMessage) && !StringUtils.endsWith(causeMessage, ".")) {
            causeMessage += ".";
        }
        // TODO
        //        MessageDialog.openError(SWTUtilities.getActiveShell(), "Node could not be created.",
        //            String.format("%s %s", error, causeMessage));
    }

    private static MetaNodeLinkUpdateResult loadComponent(final WorkflowManager parentWFM, final URI templateURI,
        final boolean deleteFileAfterLoad /* TODO */) {
        try {
            var pm = createProgressMonitor();
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
            if (pm.isCanceled()) {
                throw new InterruptedException();
            }

            var isComponentProject = false; // TODO

            //            final var loadHelper =
            //                GUIWorkflowLoadHelper.forTemplate(d, parentFile.getName(), m_context, m_isComponentProject);
            final var loadHelper = createWorkflowLoadHelper(parentFile.getName(), null /*context*/, isComponentProject);
            final var loadPersistor = loadHelper.createTemplateLoadPersistor(parentFile, templateURI);
            final var loadResult = new MetaNodeLinkUpdateResult("Shared instance from \"" + templateURI + "\"");
            parentWFM.load(loadPersistor, loadResult, new ExecutionMonitor(/* TODO progress monitor?*/), false);

            if (pm.isCanceled()) {
                throw new InterruptedException();
            }
            pm.subTask("Finished.");
            pm.done();

            // components are always stored without data
            // -> don't report data load errors neither node state changes if component is loaded as project
            final Status status = createStatus(loadResult,
                !loadResult.getGUIMustReportDataLoadErrors() || isComponentProject, isComponentProject);
            final String message;
            switch (status) {
                case OK:
                    message = "No problems during load.";
                    break;
                case WARNING:
                    message = "Warnings during load";
                    break;
                default:
                    message = "Errors during load";
            }
            if (isComponentProject && loadResult.getLoadedInstance() instanceof SubNodeContainer) {
                SubNodeContainer snc = (SubNodeContainer)loadResult.getLoadedInstance();
                final var wfm = snc.getWorkflowManager();
                // TODO
                // m_workflowLoadedCallback.accept(wfm);
                if (!status.isOK()) {
                    // TODO
                    // LoadWorkflowRunnable.showLoadErrorDialog(loadResult, status, message, false);
                }
            } else {
                if (!status.isOK()) {
                    // TODO
                    // LoadWorkflowRunnable.showLoadErrorDialog(loadResult, status, message, false);
                }
            }
            return loadResult;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            // IMPORTANT: Remove the reference to the file and the editor!!! Otherwise the memory cannot be freed later
            // TODO necessary?
            // m_parentWFM = null;
        }
    }

    private static WorkflowLoadHelper createWorkflowLoadHelper(final String workflowName /* TODO */,
        final WorkflowContextV2 context, final boolean isComponentProject) {
        // TODO see GUIWorkflowLoadHelper
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

    /**
     * TODO
     *
     * Create IStatus from load result.
     *
     * @param loadResult Load result.
     * @param treatDataLoadErrorsAsOK data loading is OK (exported with no data)
     * @param treatStateChangeWarningsAsOK warning about node state changes on load are OK
     * @return The IStatus object to be shown.
     */
    private static Status createStatus(final LoadResultEntry loadResult, final boolean treatDataLoadErrorsAsOK,
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
            subStatus[i] = createStatus(children[i], treatDataLoadErrorsAsOK, treatStateChangeWarningsAsOK);
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
        return builder(AddNodeResultEntBuilder.class)//
            .setKind(KindEnum.ADD_NODE_RESULT)//
            .setNewNodeId(new NodeIDEnt(m_componentId))//
            .setSnapshotId(snapshotId)//
            .build();
    }

    @Override
    public Set<WorkflowChange> getChangesToWaitFor() {
        return Collections.singleton(WorkflowChange.NODE_ADDED);
    }

}