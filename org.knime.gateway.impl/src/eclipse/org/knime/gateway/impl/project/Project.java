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

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.util.LRUCache;
import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.api.util.VersionId;
import org.knime.gateway.impl.util.Lazy;

/**
 * A workflow or component project.
 *
 * @author Martin Horn, University of Konstanz
 * @noreference This interface is not intended to be referenced by clients.
 */
public final class Project {

    private final String m_id;

    private final String m_name;

    private final Origin m_origin;

    private final Runnable m_clearReport;

    private final Function<String, byte[]> m_generateReport;

    private final WorkflowManagerCache m_wfmCache;

    private Project(final Builder builder) {
        this.m_id = builder.m_id;
        this.m_name = builder.m_name;
        this.m_origin = builder.m_origin;
        this.m_wfmCache = new WorkflowManagerCache(builder.m_wfmLoader, builder.m_onDispose);
        this.m_clearReport = (builder.m_clearReport != null) ? builder.m_clearReport : () -> {
        };
        this.m_generateReport = builder.m_generateReport;
    }

    /**
     * Generate a unique project ID.
     * 
     * @param projectName The human-readable name of the project
     * @return a globally unique project id combined with the given project name
     */
    public static String getUniqueProjectId(final String projectName) {
        return projectName + "_" + UUID.randomUUID();
    }

    /**
     * Caches loaded {@link WorkflowManager} instances associated with a project.
     * <p>
     * For fixed versions, we want an LRU cache. The current-state a.k.a. working area
     * should be kept indefinitely.
     */
    private static class WorkflowManagerCache {

        private final WorkflowManagerLoader m_wfmLoader;

        private final Lazy.Init<WorkflowManager> m_currentState;

        private static final int VERSION_WFM_CACHE_MAX_SIZE = 5;

        private final Map<VersionId.Fixed, WorkflowManager> m_fixedVersions =
            new LRUCache<>(VERSION_WFM_CACHE_MAX_SIZE);

        private final Consumer<WorkflowManager> m_onDispose;

        public WorkflowManagerCache(final WorkflowManagerLoader wfmLoader, final Consumer<WorkflowManager> onDispose) {
            this.m_wfmLoader = wfmLoader;
            this.m_onDispose = onDispose;
            this.m_currentState = new Lazy.Init<>(() -> wfmLoader.apply(VersionId.currentState()));
        }

        /**
         * Get a value, computing it if not yet cached.
         * @param version
         * @return
         */
        WorkflowManager get(final VersionId version) {
            if (version instanceof VersionId.CurrentState) {
                return m_currentState.get();
            } else {
                return m_fixedVersions.computeIfAbsent((VersionId.Fixed)version, m_wfmLoader);
            }
        }

        void clearCurrentState() {
            m_currentState.ifInitialized(this::disposeWorkflowManager);
            m_currentState.clear();
        }

        void dispose() {
            clearCurrentState();
            m_fixedVersions.values().forEach(this::disposeWorkflowManager);
            m_fixedVersions.clear();
        }

        void dispose(final VersionId version) {
            if (version instanceof VersionId.CurrentState) {
                clearCurrentState();
            } else {
                var cachedWfm = m_fixedVersions.remove(version);
                if (cachedWfm != null) {
                    disposeWorkflowManager(cachedWfm);
                }
            }
        }

        boolean contains(final VersionId version) {
            if (version instanceof VersionId.CurrentState) {
                return m_currentState.isInitialized();
            } else {
                return m_fixedVersions.containsKey(version);
            }
        }

        private void disposeWorkflowManager(final WorkflowManager wfm) {
            if (wfm == null) {
                return;
            }
            if (this.m_onDispose != null) {
                this.m_onDispose.accept(wfm);
            }
            try {
                CoreUtil.cancelAndCloseLoadedWorkflow(wfm);
            } catch (InterruptedException e) { // NOSONAR
                NodeLogger.getLogger(Project.class).error(e);
            }
        }
    }

    /**
     * @return the name of the project
     */
    public String getName() {
        return m_name;
    }

    /**
     * @return an id of the project
     */
    public String getID() {
        return m_id;
    }

    /**
     * @return The root workflow manager of the {@link VersionId.CurrentState} of this project. This might mean loading
     *         it, or obtaining it via reference. If this call succeeds, the workflow manager can be understood to be
     *         loaded.
     */
    public WorkflowManager getWorkflowManager() {
        return m_wfmCache.get(VersionId.currentState());
    }

    /**
     * @return The root workflow manager of the {@link VersionId.CurrentState} of this project, or empty if that
     *         workflow manager is not yet loaded.
     */
    public Optional<WorkflowManager> getWorkflowManagerIfLoaded() {
        return this.m_wfmCache.contains(VersionId.currentState()) ? //
            Optional.of(m_wfmCache.get(VersionId.currentState())) : //
            Optional.empty();
    }

    /**
     * @return Describes from where this workflow/component project originates, i.e. from where it has been created; an
     *         empty optional if the origin is unknown
     */
    public Optional<Origin> getOrigin() {
        return Optional.ofNullable(this.m_origin);
    }

    /**
     * Obtain the workflow manager instance for the given workflow.
     * 
     * @param version The version of the workflow manager
     * @return The workflow manager instance
     */
    public Optional<WorkflowManager> getWorkflowManager(final VersionId version) {
        return Optional.ofNullable(m_wfmCache.get(version));
    }

    /**
     * Dispose the entire project.
     */
    void dispose() {
        this.m_wfmCache.dispose();
    }

    /**
     * Call via {@link ProjectManager#disposeVersion(String, VersionId)} to allow invocation of listeners that can not
     * be attached to specific instances.
     * 
     * @param version Dispose the loaded workflow manager instance (if any) associated with this version.
     */
    void dispose(final VersionId version) {
        m_wfmCache.dispose(version);
    }

    /**
     * Clears the report directory of the workflow project.
     */
    public void clearReport() {
        this.m_clearReport.run();
    }

    /**
     * Generates a report. See {@code org.knime.enterprise.executor.JobPool#generateReport}.
     *
     * @param format the report format
     * @return the report directory or an empty optional
     * @throws IllegalArgumentException if the format is not supported or invalid
     * @throws IllegalStateException if report generation failed for some reason
     */
    public byte[] generateReport(final String format) {
        if (this.m_generateReport == null) {
            throw new UnsupportedOperationException(
                "No report generation function set for project '" + this.m_name + "'");
        }
        return this.m_generateReport.apply(format);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder() //
            .append(this.m_id) //
            .append(this.m_name) //
            .append(this.m_origin) //
            .build();
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Project otherProject)) {
            return false;
        }
        return new EqualsBuilder() //
            .append(this.m_id, otherProject.getID()) //
            .append(this.m_name, otherProject.getName()) //
            .append(this.m_origin, otherProject.getOrigin().orElse(null)) //
            .build();
    }

    /**
     * @return A builder to create a new {@link Project}-instance.
     */
    public static BuilderStage.RequiresWorkflow builder() {
        return new Builder();
    }

    /**
     * Builder for {@link Project}-instances.
     */
    interface BuilderStage {

        /**
         * Builder stage requiring an id.
         */
        interface RequiresId {
            Optionals setId(final String id);
        }

        /**
         * Builder stage requiring a {@link WorkflowManager}.
         */
        interface RequiresWorkflow {
            Optionals setWfm(final WorkflowManager wfm);

            RequiresName setWfmLoader(final WorkflowManagerLoader getWfm);
        }

        /**
         * Builder stage requiring a name.
         */
        interface RequiresName {
            RequiresId setName(final String name);
        }

        /**
         * Builder stage offering optional properties.
         */
        @SuppressWarnings("java:S1176") // javadoc
        interface Optionals extends RequiresId, RequiresName {
            Optionals setOrigin(Origin origin);

            Optionals onDispose(Consumer<WorkflowManager> onDispose);

            Optionals clearReport(Runnable clearReport);

            Optionals generateReport(Function<String, byte[]> generateReport);

            Project build();
        }
    }

    /**
     * Builder for {@link Project}-instances.
     *
     * This builder offers different routes to construct an instance. Namely, one via a supplier which requires setting
     * additional properties, and one via an instance which infers these properties from the instance.
     */
    @SuppressWarnings({"java:S1939", "unused"}) // Clearer if all implementations are listed, even though technically redundant
    private static final class Builder implements BuilderStage.RequiresId, BuilderStage.RequiresWorkflow,
        BuilderStage.RequiresName, BuilderStage.Optionals {

        private WorkflowManagerLoader m_wfmLoader;

        private String m_id;

        private String m_name;

        private Origin m_origin;

        private Consumer<WorkflowManager> m_onDispose;

        private Runnable m_clearReport;

        private Function<String, byte[]> m_generateReport;

        private Builder() {
            //
        }

        @Override
        public BuilderStage.Optionals setWfm(final WorkflowManager wfm) {
            Objects.requireNonNull(wfm);
            m_name = wfm.getName();
            m_id = getUniqueProjectId(m_name);
            m_wfmLoader = WorkflowManagerLoader.providingOnlyCurrentState(() -> wfm);
            return this;
        }

        @Override
        public BuilderStage.RequiresName setWfmLoader(final WorkflowManagerLoader supplier) {
            Objects.requireNonNull(supplier);
            m_wfmLoader = supplier;
            return this;
        }

        /**
         * @param id the id to set
         * @return this
         */
        @Override
        public BuilderStage.Optionals setId(final String id) {
            Objects.requireNonNull(id);
            m_id = id;
            return this;
        }

        /**
         * @param name the name to set
         * @return this
         */
        @Override
        public BuilderStage.RequiresId setName(final String name) {
            Objects.requireNonNull(name);
            m_name = name;
            return this;
        }

        /**
         * @param origin the origin to set
         * @return this
         */
        @Override
        public Builder setOrigin(final Origin origin) {
            m_origin = origin;
            return this;
        }

        /**
         * @param onDispose
         * @return this
         */
        @Override
        public Builder onDispose(final Consumer<WorkflowManager> onDispose) {
            m_onDispose = onDispose;
            return this;
        }

        /**
         * @param clearReport
         * @return this
         */
        @Override
        public Builder clearReport(final Runnable clearReport) {
            m_clearReport = clearReport;
            return this;
        }

        /**
         * @param generateReport
         * @return this
         */
        @Override
        public Builder generateReport(final Function<String, byte[]> generateReport) {
            m_generateReport = generateReport;
            return this;
        }

        /**
         * @return a new {@link Project}-instance
         */
        @Override
        public Project build() {
            return new Project(this);
        }
    }

}
