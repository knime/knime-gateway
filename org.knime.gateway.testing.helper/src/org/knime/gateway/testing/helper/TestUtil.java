/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.com; Email: contact@knime.com
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
package org.knime.gateway.testing.helper;

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
     * Loads a workflow into memory. Mainly copied from {@link org.knime.testing.core.ng.WorkflowLoadTest}.
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
