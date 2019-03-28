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
package com.knime.gateway.remote.service;

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
import org.knime.core.node.workflow.NodeStateChangeListener;
import org.knime.core.node.workflow.WizardExecutionController;
import org.knime.core.node.workflow.WorkflowLock;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.util.FileUtil;
import org.knime.core.wizard.WizardPageManager;
import org.osgi.framework.Bundle;

import com.knime.gateway.entity.WizardPageInputEnt;
import com.knime.gateway.remote.service.util.DefaultServiceUtil;
import com.knime.gateway.service.WizardExecutionService;
import com.knime.gateway.service.util.ServiceExceptions;
import com.knime.gateway.service.util.ServiceExceptions.InvalidSettingsException;
import com.knime.gateway.service.util.ServiceExceptions.NoWizardPageException;
import com.knime.gateway.service.util.ServiceExceptions.NotFoundException;
import com.knime.gateway.service.util.ServiceExceptions.TimeoutException;

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

    private static final NodeLogger LOGGER = NodeLogger.getLogger(DefaultWizardExecutionService.class);

    private static Map<String, URL> webResourcesUrls;

    private DefaultWizardExecutionService() {
        //private constructor since it's a singleton
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
    public String getCurrentPage(final UUID jobId) throws NoWizardPageException {
        WorkflowManager wfm = DefaultServiceUtil.getRootWorkflowManager(jobId);
        WizardPageManager pageManager = WizardPageManager.of(wfm);

        //otherwise jackson core isn't able to find classes outside its bundle
        ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

        if (!pageManager.getWizardExecutionController().hasCurrentWizardPage()) {
            throw new ServiceExceptions.NoWizardPageException("No wizard page available");
        }

        try {
            return pageManager.createCurrentWizardPageString();
        } catch (IOException ex) {
            String s = "Could not send current wizard page from job '" + jobId + "': " + ex.getMessage();
            LOGGER.error(s, ex);
            throw new IllegalStateException(s, ex);
        } finally {
            Thread.currentThread().setContextClassLoader(contextLoader);
       }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String executeToNextPage(final UUID jobId, final Boolean async, final Long timeout,
        final WizardPageInputEnt wizardPageInput)
        throws NoWizardPageException, InvalidSettingsException, TimeoutException {
        LOGGER.info("Stepping to next page of workflow with id '" + jobId + "'");

        WorkflowManager wfm = DefaultServiceUtil.getRootWorkflowManager(jobId);
        WizardPageManager pageManager = WizardPageManager.of(wfm);
        WizardExecutionController wec = pageManager.getWizardExecutionController();

        String validationResult = null;
        try (WorkflowLock lock = wfm.lock()) {
            if (!wec.hasCurrentWizardPage()) {
                wec.stepFirst();
            } else {
                validationResult = pageManager.applyViewValuesToCurrentPage(wizardPageInput.getViewValues());
                if (StringUtils.isEmpty(validationResult)) {
                    wec.stepNext();
                } else {
                    throw new InvalidSettingsException("Validation of view parameters failed: " + validationResult);
                }
            }
        } catch (IOException ex) {
            String msg = "Could not execute to " + (!wec.hasCurrentWizardPage() ? "first" : "next") + " page: "
                + ex.getMessage();
            LOGGER.error(msg, ex);
            throw new NoWizardPageException(msg);
        }

        if (async) {
            return "";
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

    private static boolean isWfmDone(final WorkflowManager wfm) {
        return wfm.getNodeContainerState().isConfigured() || wfm.getNodeContainerState().isWaitingToBeExecuted()
            || wfm.getNodeContainerState().isExecuted();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String resetToPreviousPage(final UUID jobId) throws NoWizardPageException {
        WorkflowManager wfm = DefaultServiceUtil.getRootWorkflowManager(jobId);
        WizardPageManager pageManager = WizardPageManager.of(wfm);
        WizardExecutionController wec = pageManager.getWizardExecutionController();
        if (!wec.hasPreviousWizardPage()) {
            throw new NoWizardPageException("No previous wizard page");
        }
        wec.stepBack();
        DefaultServiceUtil.getWorkflowProject(jobId).clearReport();
        return getCurrentPage(jobId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> listWebResources(final UUID jobId) {
        ensureThatWebResourceUrlsAreAvailable();
        return new ArrayList<String>(webResourcesUrls.keySet());
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
                    contents.forEach(e -> queue.add(e));
                }
            } else {
                urls.put(relTarget + dir.relativize(p), p.toUri().toURL());
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
}
