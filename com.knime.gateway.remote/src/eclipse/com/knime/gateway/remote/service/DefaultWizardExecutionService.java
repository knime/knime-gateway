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

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.workflow.WizardExecutionController;
import org.knime.core.node.workflow.WorkflowExecutionMode;
import org.knime.core.node.workflow.WorkflowLock;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.wizard.WizardPageManager;

import com.knime.enterprise.executor.ExecutorUtil;
import com.knime.gateway.remote.service.util.DefaultServiceUtil;
import com.knime.gateway.v0.service.WizardExecutionService;
import com.knime.gateway.v0.service.util.ServiceExceptions;
import com.knime.gateway.v0.service.util.ServiceExceptions.InvalidSettingsException;
import com.knime.gateway.v0.service.util.ServiceExceptions.NoWizardPageException;
import com.knime.gateway.v0.service.util.ServiceExceptions.TimeoutException;

/**
 * Default implementation of {@link WizardExecutionService} that delegates the operations to knime.core (e.g.
 * {@link WorkflowManager} etc.).
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class DefaultWizardExecutionService implements WizardExecutionService {
    private static final DefaultWizardExecutionService INSTANCE = new DefaultWizardExecutionService();

    private static final NodeLogger LOGGER = NodeLogger.getLogger(DefaultWizardExecutionService.class);

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
        final Map<String, String> requestBody)
        throws NoWizardPageException, InvalidSettingsException, TimeoutException {
        LOGGER.info("Stepping to next page of workflow with id '" + jobId + "'");

        WorkflowManager wfm = DefaultServiceUtil.getRootWorkflowManager(jobId);
        WizardPageManager pageManager = WizardPageManager.of(wfm);
        WizardExecutionController wec = pageManager.getWizardExecutionController();

        if(ExecutorUtil.getPossibleExecutionMode(wfm) == WorkflowExecutionMode.REGULAR) {
            throw new NoWizardPageException("Workflow not in wizard execution mode");
        }

        String validationResult = null;
        try (WorkflowLock lock = wfm.lock()) {
            if (!wec.hasCurrentWizardPage()) {
                wec.stepFirst();
            } else {
                validationResult = pageManager.applyViewValuesToCurrentPage(requestBody);
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
            if (wec.hasCurrentWizardPage()) {
                return "";
            } else {
                //no content
                throw new NoWizardPageException("No wizard page available");
            }
        } else {
            try {
                if (wfm.waitWhileInExecution(timeout, TimeUnit.MILLISECONDS)) {
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
}
