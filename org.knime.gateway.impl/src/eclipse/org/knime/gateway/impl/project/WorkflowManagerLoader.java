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
 */

package org.knime.gateway.impl.project;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.workflow.UnsupportedWorkflowVersionException;
import org.knime.core.node.workflow.WorkflowLoadHelper;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.util.LockFailedException;
import org.knime.core.util.ProgressMonitorAdapter;
import org.knime.gateway.api.util.VersionId;
import org.knime.gateway.impl.webui.spaces.SpaceProviders;

/**
 * Defines how a {@link WorkflowManager} instance is loaded.
 * <p>
 * "Loading" here means loading it from file representation on disk to provide an initialized and usable
 * {@link WorkflowManager} instance. However, this method may also include fetching the files from a remote location.
 *
 * @since 5.5
 */
@FunctionalInterface
@SuppressWarnings("java:S1711") // intentionally not using Function<VersionId, WorkflowManager> instead.
public interface WorkflowManagerLoader {

    @SuppressWarnings({"javadoc", "MissingJavadoc"})
    String LOADING_WORKFLOW_PROGRESS_MSG = "Loading workflow...";

    @SuppressWarnings({"javadoc", "MissingJavadoc"})
    String FETCHING_WORKFLOW_PROGRESS_MSG = "Fetching workflow...";

    /**
     * Load the workflow manager instance
     *
     * @param version the version to load
     * @return the loaded instance, <code>null</code> if loading failed.
     */
    WorkflowManager load(final VersionId version);

    /**
     * Utility to load a {@code WorkflowManager} instance from a given path.
     *
     * @param loadHelper -
     * @param path -
     * @param monitor -
     * @return -
     */
    static WorkflowManager load(final WorkflowLoadHelper loadHelper, final Path path,
        final NullProgressMonitor monitor) {
        try {
            var loadResult = WorkflowManager.loadProject( //
                path.toFile(), //
                new ExecutionMonitor(new ProgressMonitorAdapter(monitor)), //
                loadHelper //
            );
            return loadResult.getWorkflowManager();
        } catch (IOException | InvalidSettingsException | CanceledExecutionException
                | UnsupportedWorkflowVersionException | LockFailedException e) {
            NodeLogger.getLogger(WorkflowManagerLoader.class).error(e);
            return null;
        }
    }

    /**
     * Obtain the path of the on-disk representation of the {@link WorkflowManager} identified by {@link Origin} and
     * {@link VersionId}, using the given {@link SpaceProviders}.
     *
     * @param origin -
     * @param version -
     * @param spaceProviders -
     * @param monitor Will receive updates on the progress of the task
     * @return -
     */
    static Optional<Path> fetch(final Origin origin, final VersionId version, final SpaceProviders spaceProviders,
        final ExecutionMonitor monitor) { // NOSONAR false positive
        var space = spaceProviders.getSpace(origin.providerId(), origin.spaceId());
        try {
            return space.toLocalAbsolutePath(monitor, origin.itemId(), version);
        } catch (CanceledExecutionException e) { // NOSONAR
            return Optional.empty();
        }

    }

}
