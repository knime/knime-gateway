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
package com.knime.gateway.testing.helper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.workflow.UnsupportedWorkflowVersionException;
import org.knime.core.node.workflow.WorkflowContext;
import org.knime.core.node.workflow.WorkflowLoadHelper;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.node.workflow.WorkflowPersistor.LoadResultEntry.LoadResultEntryType;
import org.knime.core.node.workflow.WorkflowPersistor.WorkflowLoadResult;
import org.knime.core.util.LoadVersion;
import org.knime.core.util.LockFailedException;
import org.knime.core.util.Version;
import org.knime.testing.core.ng.WorkflowLoadTest;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * Methods used by the testcases.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @author Thorsten Meinl, KNIME AG, Zurich, Switzerland
 */
public class TestUtil {
    /**
     * Utility method to resolve files in the current plugin.
     *
     * COPIED from com.knime.enterprise.server.ittests.TestUtil
     *
     * @param path a path relative to the plugin's root; must start with "/"
     * @return a file object if the file exists
     * @throws IOException if an I/O error occurs or the file does not exist
     */
    public static File resolveToFile(final String path) throws IOException {
        URL url = FileLocator.toFileURL(resolveToURL(path));
        return new File(url.getPath());
    }

    /**
     * Utility method to resolve files in the current plugin.
     *
     * COPIED from com.knime.enterprise.server.ittests.TestUtil
     *
     * @param path a path relative to the plugin's root; must start with "/"
     * @return a URL to the resource
     * @throws IOException if an I/O error occurs or the file does not exist
     */
    public static URL resolveToURL(final String path) throws IOException {
        Bundle myself = FrameworkUtil.getBundle(TestUtil.class);
        IPath p = new Path(path);
        URL url = FileLocator.find(myself, p, null);
        if (url == null) {
            throw new FileNotFoundException("Path " + path + " does not exist in bundle " + myself.getSymbolicName());
        }
        return url;
    }

    /**
     * Loads a workflow into memory. Mainly copied from {@link WorkflowLoadTest}.
     *
     * @param workflowDir
     * @return the loaded workflow
     * @throws IOException
     * @throws InvalidSettingsException
     * @throws CanceledExecutionException
     * @throws UnsupportedWorkflowVersionException
     * @throws LockFailedException
     */

    public static WorkflowManager loadWorkflow(final File workflowDir) throws IOException, InvalidSettingsException,
        CanceledExecutionException, UnsupportedWorkflowVersionException, LockFailedException {
        WorkflowLoadHelper loadHelper = new WorkflowLoadHelper() {
            /**
             * {@inheritDoc}
             */
            @Override
            public WorkflowContext getWorkflowContext() {
                WorkflowContext.Factory fac = new WorkflowContext.Factory(workflowDir);
                //fac.setMountpointRoot(testcaseRoot);
                return fac.createContext();
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public UnknownKNIMEVersionLoadPolicy getUnknownKNIMEVersionLoadPolicy(
                final LoadVersion workflowKNIMEVersion, final Version createdByKNIMEVersion,
                final boolean isNightlyBuild) {
                return UnknownKNIMEVersionLoadPolicy.Try;
            }
        };

        WorkflowLoadResult loadRes = WorkflowManager.loadProject(workflowDir, new ExecutionMonitor(), loadHelper);
        if ((loadRes.getType() == LoadResultEntryType.Error)
            || ((loadRes.getType() == LoadResultEntryType.DataLoadError) && loadRes.getGUIMustReportDataLoadErrors())) {
            throw new RuntimeException(loadRes.getFilteredError("", LoadResultEntryType.Error));
        }

        WorkflowManager wfm = loadRes.getWorkflowManager();

        return wfm;
    }

    /**
     * Cancels and closes the passed workflow manager.
     *
     * @param wfm workflow manager to cancel and close
     */
    public static void cancelAndCloseLoadedWorkflow(final WorkflowManager wfm) {
        wfm.getParent().cancelExecution(wfm);
        if(wfm.getNodeContainerState().isExecutionInProgress()) {
            try {
                wfm.waitWhileInExecution(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        wfm.getParent().removeProject(wfm.getID());
    }


}
