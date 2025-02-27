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
package org.knime.gateway.impl.project;

import java.util.Optional;

import org.knime.core.node.NodeLogger;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.api.util.VersionId;

/**
 * A workflow or component project.
 *
 * @author Martin Horn, University of Konstanz
 * @noreference This interface is not intended to be referenced by clients.
 */
@SuppressWarnings("javadoc")
public interface Project {

    /**
     * @return the name of the project
     */
    String getName();

    /**
     * @return an id of the project
     */
    String getID();

    /**
     * The root workflow manager of the {@link VersionId.CurrentState} of this project. This might mean loading it, or
     * obtaining it via reference. If this call succeeds, the workflow manager can be understood to be loaded.
     */
    WorkflowManager getWorkflowManager();

    /**
     * @return The root workflow manager of the {@link VersionId.CurrentState} of this project, or empty if that
     *         workflow manager is not yet loaded.
     */
    default Optional<WorkflowManager> getWorkflowManagerIfLoaded() {
        return Optional.empty();
    }

    /**
     * @return describes from where this workflow/component project originates, i.e. from where it has been created; an
     *         empty optional if the origin is unknown
     */
    default Optional<Origin> getOrigin() {
        return Optional.empty();
    }

    /**
     * Obtain the workflow manager in the project of a given {@link VersionId}.
     */
    default Optional<WorkflowManager> getWorkflowManager(final VersionId version) {
        if (version instanceof VersionId.Fixed fixedVersion) {
            return this.getVersion(fixedVersion);
        }
        return Optional.ofNullable(this.getWorkflowManager());
    }

    /**
     * Obtain the workflow manager in the project of a given {@link VersionId.Fixed} version.
     */
    default Optional<WorkflowManager> getVersion(final VersionId.Fixed version) {
        return Optional.empty();
    }

    /**
     * Dispose the project.
     */
    default void dispose() {
        getWorkflowManagerIfLoaded().ifPresent(wfm -> {
            try {
                CoreUtil.cancelAndCloseLoadedWorkflow(wfm);
            } catch (InterruptedException e) { // NOSONAR
                NodeLogger.getLogger(CachedProject.class).error(e);
            }
        });
    }

    /**
     * Clears the report directory of the workflow project (if there is any).
     */
    default void clearReport() {
    }

    /**
     * Generates a report. See {@code org.knime.enterprise.executor.JobPool#generateReport}.
     *
     * @param format the report format
     * @return the report directory or an empty optional
     * @throws IllegalArgumentException if the format is not supported or invalid
     * @throws IllegalStateException if report generation failed for some reason
     */
    default byte[] generateReport(final String format) {
        throw new UnsupportedOperationException("Report generation not supported");
    }

}
