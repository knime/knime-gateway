/* ------------------------------------------------------------------
 * This source code, its documentation and all appendant files
 * are protected by copyright law. All rights reserved.
 *
 * Copyright by KNIME AG, Zurich, Switzerland
 *
 * You may not modify, publish, transmit, transfer or sell, reproduce,
 * create derivative works from, distribute, perform, display, or in
 * any way exploit any of the content, in whole or in part, except as
 * otherwise expressly permitted in writing by the copyright owner or
 * as specified in the license file distributed with this product.
 *
 * If you have any questions please contact the copyright holder:
 * website: www.knime.com
 * email: contact@knime.com
 * ---------------------------------------------------------------------
 *
 */
package org.knime.gateway.impl.service;

import static org.knime.gateway.api.entity.EntityBuilderManager.builder;
import static org.knime.gateway.impl.service.util.DefaultServiceUtil.getWizardExecutionState;
import static org.knime.gateway.impl.service.util.WizardExecutionStatistics.isWfmDone;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.NodeStateChangeListener;
import org.knime.core.node.workflow.WizardExecutionController;
import org.knime.core.node.workflow.WorkflowLock;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.util.FileUtil;
import org.knime.core.wizard.WizardPageManager;
import org.knime.gateway.api.entity.ExecutionStatisticsEnt;
import org.knime.gateway.api.entity.ExecutionStatisticsEnt.ExecutionStatisticsEntBuilder;
import org.knime.gateway.api.entity.WizardPageEnt;
import org.knime.gateway.api.entity.WizardPageEnt.WizardExecutionStateEnum;
import org.knime.gateway.api.entity.WizardPageEnt.WizardPageEntBuilder;
import org.knime.gateway.api.entity.WizardPageInputEnt;
import org.knime.gateway.api.service.ServiceException;
import org.knime.gateway.api.service.WizardExecutionService;
import org.knime.gateway.api.service.util.ServiceExceptions.InvalidRequestException;
import org.knime.gateway.api.service.util.ServiceExceptions.InvalidSettingsException;
import org.knime.gateway.api.service.util.ServiceExceptions.NoWizardPageException;
import org.knime.gateway.api.service.util.ServiceExceptions.NotFoundException;
import org.knime.gateway.api.service.util.ServiceExceptions.TimeoutException;
import org.knime.gateway.api.util.EntityBuilderUtil;
import org.knime.gateway.impl.project.WorkflowProject;
import org.knime.gateway.impl.project.WorkflowProjectManager;
import org.knime.gateway.impl.service.util.DefaultServiceUtil;
import org.knime.gateway.impl.service.util.WizardExecutionStatistics;
import org.knime.js.core.JSONWebNodePage;
import org.knime.js.core.layout.bs.JSONLayoutPage;
import org.knime.reporting.nodes.dataset.ReportingDataSetNodeModel;
import org.osgi.framework.Bundle;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Default implementation of {@link WizardExecutionService} that delegates the operations to knime.core (e.g.
 * {@link WorkflowManager} etc.).
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class DefaultWizardExecutionService implements WizardExecutionService {

    private static final DefaultWizardExecutionService INSTANCE = new DefaultWizardExecutionService();

    private static final String ID_WEB_RES = "org.knime.js.core.webResources";
    private static final String ELEM_BUNDLE = "webResourceBundle";
    private static final String ELEM_RES = "webResource";
    private static final String ATTR_SOURCE = "relativePathSource";
    private static final String ATTR_TARGET = "relativePathTarget";

    /**
     * A flag indicating that we are on the report page. Necessary for the special case if the last node in the workflow
     * is a wizard page and there is a report. The flag helps to distinguish those two states (i.e. last 'real' wizard
     * page or report page).
     */
    private static final String REPORT_PAGE_FLAG = "reportPage";

    private static final NodeLogger LOGGER = NodeLogger.getLogger(DefaultWizardExecutionService.class);

    private static Map<String, URL> webResourcesUrls;

    private final Map<UUID, WizardExecutionStatistics> m_executionStatistics = new HashMap<>();

    private DefaultWizardExecutionService() {
        //private constructor since it's a singleton

        WorkflowProjectManager.addWorkflowProjectRemovedListener(id -> {
            m_executionStatistics.remove(id);
        });
    }

    /**
     * Returns the singleton instance for this service.
     *
     * @return the singleton instance
     */
    public static DefaultWizardExecutionService getInstance() {
        return INSTANCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WizardPageEnt getCurrentPage(final UUID jobId) {
        WorkflowManager wfm = DefaultServiceUtil.getRootWorkflowManager(jobId);
        WizardPageEntBuilder wizardPageBuilder = builder(WizardPageEntBuilder.class);

        // set wizard execution state
        WizardExecutionStateEnum wes = WizardExecutionStateEnum.valueOf(getWizardExecutionState(wfm));
        wizardPageBuilder.setWizardExecutionState(wes);

        // set page content
        if ((wfm.isInWizardExecution() && wfm.getWizardExecutionController().hasCurrentWizardPage())
            && !isOnReportPage(wfm)) {
            //otherwise jackson core isn't able to find classes outside its bundle
            ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
            WizardPageManager pageManager = WizardPageManager.of(wfm);
            try {
                JSONWebNodePage jsonPage = pageManager.createCurrentWizardPage();
                ObjectMapper mapper = JSONLayoutPage.getConfiguredVerboseObjectMapper();
                JsonNode jsonNode = mapper.convertValue(jsonPage, JsonNode.class);
                wizardPageBuilder.setWizardPageContent(jsonNode);
            } catch (IOException ex) {
                String s = "Could not send current wizard page from job '" + jobId + "': " + ex.getMessage();
                LOGGER.error(s, ex);
                throw new IllegalStateException(s, ex);
            } finally {
                Thread.currentThread().setContextClassLoader(contextLoader);
            }
        }

        // set node messages
        switch (wes) {
            case INTERACTION_REQUIRED:
            case EXECUTING:
                wizardPageBuilder.setNodeMessages(null);
                break;
            case UNDEFINED:
                return wizardPageBuilder.setNodeMessages(null).build();
            case EXECUTION_FAILED:
            case EXECUTION_FINISHED:
                wizardPageBuilder.setNodeMessages(EntityBuilderUtil.buildNodeMessageEntMap(wfm));
        }

        // set has-previous- and has-next-page properties (if in wizard execution)
        WizardExecutionController wec = wfm.isInWizardExecution() ? wfm.getWizardExecutionController() : null;
        boolean hasNextPage = false;
        if (wec != null) {
            wizardPageBuilder.setHasPreviousPage(wfm.getWizardExecutionController().hasPreviousWizardPage());
            switch (wes) {
                case INTERACTION_REQUIRED:
                case EXECUTING:
                    hasNextPage = true;
                    break;
                case EXECUTION_FINISHED:
                    if (wec.getProperty(REPORT_PAGE_FLAG).isPresent()) {
                        hasNextPage = false;
                    } else if (hasReport(wfm) && wec.isHaltedAtTerminalWizardPage()) {
                        // special case: execution is finished at a wizard page (i.e. the very last node is a component)
                        // and there is a report -> there is a next page which is for the report only
                        hasNextPage = true;
                    } else {
                        hasNextPage = false;
                    }
                    break;
                case UNDEFINED:
                case EXECUTION_FAILED:
                default:
                    hasNextPage = false;
                    break;
            }
            wizardPageBuilder.setHasNextPage(hasNextPage);
        }

        //set has-report flag (only if we are on the very last page or not in wizard execution)
        if (!hasNextPage) {
            wizardPageBuilder.setHasReport(hasReport(wfm));
        }

        return wizardPageBuilder.build();
    }

    private static boolean hasReport(final WorkflowManager wfm) {
        return !wfm.findNodes(ReportingDataSetNodeModel.class, false).isEmpty();
    }

    private static boolean isOnReportPage(final WorkflowManager wfm) {
        return wfm.isInWizardExecution()
            && wfm.getWizardExecutionController().getProperty(REPORT_PAGE_FLAG).isPresent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WizardPageEnt executeToNextPage(final UUID jobId, final Boolean async, final Long timeout,
        final WizardPageInputEnt wizardPageInput)
        throws NoWizardPageException, InvalidSettingsException, TimeoutException {
        LOGGER.info("Stepping to next page of workflow with id '" + jobId + "'");

        WorkflowManager wfm = DefaultServiceUtil.getRootWorkflowManager(jobId);
        WizardPageManager pageManager = WizardPageManager.of(wfm);
        WizardExecutionController wec = pageManager.getWizardExecutionController();

        // special case: if the workflow is executed, we are on a page
        // (i.e. the very last node is a wizard page component) _and_ there is a report
        // we need to memorize that we stepped from the last wizard page to the report-page
        if (wec.isHaltedAtTerminalWizardPage() && hasReport(wfm)) {
            if (wec.getProperty(REPORT_PAGE_FLAG).isPresent()) {
                throw new NoWizardPageException("Can't execute to next page. Already on the last page (report).");
            }
            wec.setProperty(REPORT_PAGE_FLAG, "true");
            return getCurrentPage(jobId);
        }

        String validationResult = null;
        try (WorkflowLock lock = wfm.lock()) {
            m_executionStatistics.computeIfAbsent(jobId, id -> new WizardExecutionStatistics());

            if (!wec.hasCurrentWizardPage()) {
                m_executionStatistics.get(jobId).resetStatisticsToWizardPage(null, wfm);
                wec.stepFirst();
            } else {
                m_executionStatistics.get(jobId).resetStatisticsToWizardPage(wec.getCurrentWizardPageNodeID(), wfm);
                validationResult = pageManager.applyViewValuesToCurrentPage(wizardPageInput.getViewValues());
                if (StringUtils.isEmpty(validationResult)) {
                    wec.stepNext();
                } else {
                    throw new InvalidSettingsException(validationResult);
                }
            }
        } catch (IOException ex) {
            String msg = "Could not execute to " + (!wec.hasCurrentWizardPage() ? "first" : "next") + " page: "
                + ex.getMessage();
            LOGGER.error(msg, ex);
            throw new NoWizardPageException(msg);
        }

        if (async) {
            return builder(WizardPageEntBuilder.class).setNodeMessages(null).build();
        } else {
            try {
                if (waitWhileInExecution(wfm, timeout, TimeUnit.MILLISECONDS)) {
                    return getCurrentPage(jobId);
                } else {
                    throw new TimeoutException("Workflow didn't finish before timeout");
                }
            } catch (InterruptedException ex) {
                //should never happen
                throw new IllegalStateException(ex);
            }
        }
    }

    /**
     * Causes the current thread to wait
     *
     * Copied and adopted from {@link WorkflowManager#waitWhileInExecution(long, TimeUnit)}.
     *
     * @param wfm the workflow manager to wait for
     * @param time the maximum time to wait (0 or negative for waiting infinitely)
     * @param unit the time unit of the {@code time} argument
     * @return {@code false} if the waiting time detectably elapsed before return from the method, else {@code true}. It
     *         returns {@code true} if the time argument is 0 or negative.
     * @throws InterruptedException if the current thread is interrupted
     */
    private static boolean waitWhileInExecution(final WorkflowManager wfm, final long time, final TimeUnit unit)
        throws InterruptedException {
        ReentrantLock lock = wfm.getReentrantLockInstance();
        Condition whileInExecCondition = lock.newCondition();
        NodeStateChangeListener listener = e -> {
            lock.lock();
            try {
                if (isWfmDone(wfm)) {
                    whileInExecCondition.signalAll();
                }
            } finally {
                lock.unlock();
            }
        };
        wfm.addNodeStateChangeListener(listener);
        lock.lockInterruptibly();
        try {
            if (isWfmDone(wfm)) {
                return true;
            }
            if (time > 0) {
                return whileInExecCondition.await(time, unit);
            } else {
                whileInExecCondition.await();
                return true;
            }
        } finally {
            lock.unlock();
            wfm.removeNodeStateChangeListener(listener);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WizardPageEnt resetToPreviousPage(final UUID jobId, final Long timeout)
        throws NoWizardPageException, TimeoutException {
        WorkflowManager wfm = DefaultServiceUtil.getRootWorkflowManager(jobId);
        WizardPageManager pageManager = WizardPageManager.of(wfm);
        WizardExecutionController wec = pageManager.getWizardExecutionController();
        if (!wec.hasPreviousWizardPage()) {
            throw new NoWizardPageException("No previous wizard page");
        }

        // special case: very last node in the workflow is a wizard page and there is a report
        // stepping back from the 'report-page' doesn't reset part of the workflow
        // but just removes the 'reportPage'-flag
        if (wec.getProperty(REPORT_PAGE_FLAG).isPresent()) {
            wec.removeProperty(REPORT_PAGE_FLAG);
            return getCurrentPage(jobId);
        }
        if (wfm.getNodeContainerState().isExecutionInProgress()) {
            cancelAllExceptLastPage(wfm, wec.getLastWizardPageNodeID().orElse(null));
            boolean timedOut;
            try {
                timedOut = !wfm.waitWhileInExecution(timeout, TimeUnit.MILLISECONDS);
            } catch (InterruptedException ex) {
                //if the waiting gets interrupted we assume it's a timeout
                timedOut = true;
            }
            if (timedOut) {
                throw new TimeoutException("Workflow couldn't be cancelled before timeout");
            }
        }
        wec.stepBack();
        m_executionStatistics.remove(jobId);
        DefaultServiceUtil.getWorkflowProject(jobId).clearReport();
        return getCurrentPage(jobId);
    }

    /*
     * We don't want to cancel the last page because it's in its re-execution (if executing) and we need it
     * executed in order to go back to it.
     */
    private static void cancelAllExceptLastPage(final WorkflowManager wfm, final NodeID lastPage) {
        if (lastPage == null || !wfm.getNodeContainer(lastPage).getNodeContainerState().isExecutionInProgress()) {
            // there is either no last page or it is not executing -> cancel all executing nodes
            wfm.getParent().cancelExecution(wfm);
        } else {
            // cancel all except the last page
            for (NodeContainer nc : wfm.getNodeContainers(Collections.singleton(lastPage), null, false, true)) {
                if (!nc.getID().equals(lastPage)) {
                    wfm.cancelExecution(nc);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExecutionStatisticsEnt getExecutionStatistics(final UUID rootWorkflowID) throws NotFoundException {
        if (!m_executionStatistics.containsKey(rootWorkflowID)) {
            return builder(ExecutionStatisticsEntBuilder.class).build();
        }
        WorkflowManager wfm = DefaultServiceUtil.getRootWorkflowManager(rootWorkflowID);
        return m_executionStatistics.get(rootWorkflowID).getUpdatedStatistics(wfm);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> listWebResources(final UUID jobId) {
        ensureThatWebResourceUrlsAreAvailable();
        return new ArrayList<>(webResourcesUrls.keySet());
    }

    private static void ensureThatWebResourceUrlsAreAvailable() {
        if (webResourcesUrls == null) {
            try {
                webResourcesUrls = collectWebResourceUrls();
            } catch (IOException | URISyntaxException ex) {
                //should never happen
                LOGGER.error("Problem collecting the web resource paths", ex);
                throw new IllegalStateException(ex);
            }
        }
    }

    private static Map<String, URL> collectWebResourceUrls() throws IOException, URISyntaxException {
        IExtensionRegistry registry = Platform.getExtensionRegistry();
        IExtensionPoint point = registry.getExtensionPoint(ID_WEB_RES);
        if (point == null) {
            throw new IllegalStateException("Invalid extension point : " + ID_WEB_RES);

        }

        Map<String, URL> urls = new HashMap<>();

        for (IExtension ext : point.getExtensions()) {
            // get plugin path
            String pluginName = ext.getContributor().getName();
            Bundle bundle = Platform.getBundle(pluginName);

            // get relative paths and collect in map
            IConfigurationElement[] bundleElements = ext.getConfigurationElements();
            for (IConfigurationElement bundleElem : bundleElements) {
                assert bundleElem.getName().equals(ELEM_BUNDLE);

                for (IConfigurationElement resElement : bundleElem.getChildren(ELEM_RES)) {
                    String relSource = resElement.getAttribute(ATTR_SOURCE);
                    URL resourceUrl = bundle.getEntry(relSource);
                    assert (resourceUrl != null) : "Resource '" + relSource + "' does not exist in plug-in '"
                        + pluginName + "'";

                    String relTarget = resElement.getAttribute(ATTR_TARGET);
                    if (StringUtils.isEmpty(relTarget)) {
                        relTarget = relSource;
                    }
                    URL fileUrl = FileLocator.toFileURL(resourceUrl);
                    Path resourceFile = FileUtil.resolveToPath(fileUrl);
                    if (Files.isDirectory(resourceFile)) {
                        collectWebResourceUrlsFromDirectory(fileUrl, relTarget, urls);
                    } else {
                        urls.put(relTarget, resourceFile.toUri().toURL());
                    }
                }
            }
        }
        return urls;
    }

    private static void collectWebResourceUrlsFromDirectory(final URL url, String relTarget,
        final Map<String, URL> urls) throws IOException, URISyntaxException {
        Deque<Path> queue = new ArrayDeque<>(32);
        Path dir = FileUtil.resolveToPath(url);
        queue.push(dir);

        if (!relTarget.isEmpty() && !relTarget.endsWith("/")) {
            relTarget += "/";
        }
        if (relTarget.startsWith("/")) {
            relTarget = relTarget.substring(1);
        }

        while (!queue.isEmpty()) {
            Path p = queue.poll();
            if (Files.isDirectory(p)) {
                if (!relTarget.isEmpty() || (p != dir)) {
                    // don't add an (empty) entry for the root directory itself
                    String s = relTarget + dir.relativize(p);
                    if (!s.endsWith("/")) {
                        s += "/";
                    }
                    urls.put(s, p.toUri().toURL());
                }
                try (DirectoryStream<Path> contents = Files.newDirectoryStream(p)) {
                    contents.forEach(queue::add);
                }
            } else {
                urls.put(relTarget + dir.relativize(p).toString().replace("\\", "/"), p.toUri().toURL());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getWebResource(final UUID jobId, final String resourceId) throws NotFoundException {
        ensureThatWebResourceUrlsAreAvailable();
        URL url = webResourcesUrls.get(resourceId);
        if (url == null) {
            throw new NotFoundException("No resource for given id available");
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (InputStream is = url.openStream()) {
            IOUtils.copyLarge(is, baos);
        } catch (IOException ex) {
            //should never happen
            LOGGER.error("Problem to provide web resource", ex);
            throw new IllegalStateException(ex);
        }
        return baos.toByteArray();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] renderReport(final UUID rootWorkflowID, final String format)
        throws TimeoutException, InvalidRequestException {
        WorkflowProject wfProj = WorkflowProjectManager.getWorkflowProject(rootWorkflowID).get();
        try {
            return wfProj.generateReport(format);
        } catch (IllegalArgumentException e) {
            throw new InvalidRequestException("Unsupported report format: " + format, e);
        } catch(IllegalStateException e) {
            throw new ServiceException("Report generation failed", e);
        }
    }
}
