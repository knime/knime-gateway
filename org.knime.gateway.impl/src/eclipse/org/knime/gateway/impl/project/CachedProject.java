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
 *
 * History
 *   Feb 27, 2025 (kai): created
 */
package org.knime.gateway.impl.project;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.util.LRUCache;
import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.api.util.VersionId;
import org.knime.gateway.impl.util.Lazy;

/**
 * Implementation that caches associated {@link WorkflowManager}s.
 * <p>
 * A {@link CachedProject} can be created either through an already-available {@link WorkflowManager} instance, or a
 * {@code Supplier<WorkflowManager>}, which may load the workflow manager. See {@link Builder}.
 * <p>
 * See also
 * <ul>
 * <li>NXT-3356</li>
 * </ul>
 * <p>
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @author Benjamin Moser, KNIME GmbH, Konstanz, Germany
 * @author Kai Franze, KNIME Gmbh, Germany
 */
record CachedProject ( //
    Consumer<WorkflowManager> m_onDispose, //
    String m_id, //
    String m_name, //
    Origin m_origin, //
    Function<VersionId.Fixed, WorkflowManager> m_getVersion, //
    Map<VersionId.Fixed, WorkflowManager> m_cachedVersions, //
    Lazy.Init<WorkflowManager> m_cachedWfm, //
    Runnable m_clearReport, //
    Function<String, byte[]> m_generateReport) implements Project {

    private static final int VERSION_WFM_CACHE_MAX_SIZE = 5;

    private CachedProject(final Builder builder) {
        this( //
            builder.m_onDispose, //
            builder.m_id, //
            builder.m_name, //
            builder.m_origin, //
            builder.m_getVersion, //
            initializeCachedVersions(), //
            initializeCachedWfm(builder), //
            builder.m_clearReport, //
            builder.m_generateReport);
    }

    private static Map<VersionId.Fixed, WorkflowManager> initializeCachedVersions() {
        return new LRUCache<>(VERSION_WFM_CACHE_MAX_SIZE);
    }

    private static Lazy.Init<WorkflowManager> initializeCachedWfm(final Builder builder) {
        return builder.m_loadedWfm != null ? //
            new Lazy.Init<>(builder.m_loadedWfm) : //
            new Lazy.Init<>(builder.m_getWfm);
    }

    static BuilderStage.RequiresWorkflow builder() {
        return new Builder();
    }

    @Override
    public WorkflowManager getWorkflowManager() {
        return this.m_cachedWfm.get();
    }

    @Override
    public Optional<WorkflowManager> getWorkflowManagerIfLoaded() {
        return this.m_cachedWfm.isInitialized() ? //
            Optional.of(this.m_cachedWfm.get()) : //
            Optional.empty();
    }

    @Override
    public Optional<Origin> getOrigin() {
        return Optional.ofNullable(this.m_origin);
    }

    @Override
    public String getName() {
        return this.m_name;
    }

    @Override
    public String getID() {
        return this.m_id;
    }

    @Override
    public Optional<WorkflowManager> getVersion(final VersionId.Fixed version) {
        if (this.m_getVersion == null) {
            return Optional.empty();
        }

        return Optional.of(this.m_cachedVersions.computeIfAbsent(version, this.m_getVersion));
    }

    @Override
    public void dispose() {
        this.m_cachedWfm.ifInitialized(this::disposeWorkflow);
        this.m_cachedVersions.values().forEach(this::disposeWorkflow);
        this.m_cachedVersions.clear();
    }

    private void disposeWorkflow(final WorkflowManager wfm) {
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

    @Override
    public void clearReport() {
        if (this.m_clearReport == null) {
            throw new UnsupportedOperationException("No clear report runnable set for project " + this.m_name + "'");
        }

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
    @Override
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

            RequiresName setWfmLoader(final Supplier<WorkflowManager> getWfm);
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
        interface Optionals extends RequiresId, RequiresName {
            Optionals setOrigin(Origin origin);

            Optionals setVersionWfmLoader(Function<VersionId.Fixed, WorkflowManager> getVersion);

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

        private Supplier<WorkflowManager> m_getWfm;

        private WorkflowManager m_loadedWfm;

        private String m_id;

        private String m_name;

        private Origin m_origin;

        private Function<VersionId.Fixed, WorkflowManager> m_getVersion;

        private Consumer<WorkflowManager> m_onDispose;

        private Runnable m_clearReport;

        private Function<String, byte[]> m_generateReport;

        private Builder() {
            //
        }

        /**
         * @param projectName
         * @return a globally unique project id combined with the given project name
         */
        private static String getUniqueProjectId(final String projectName) {
            return projectName + "_" + UUID.randomUUID();
        }

        @Override
        public BuilderStage.Optionals setWfm(final WorkflowManager wfm) {
            m_loadedWfm = wfm;
            m_name = wfm.getName();
            m_id = getUniqueProjectId(m_name);
            return this;
        }

        @Override
        public BuilderStage.RequiresName setWfmLoader(final Supplier<WorkflowManager> supplier) {
            m_getWfm = supplier;
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
            Objects.requireNonNull(origin);
            m_origin = origin;
            return this;
        }

        /**
         * @param getVersionFunction
         * @return this
         */
        @Override
        public Builder setVersionWfmLoader(final Function<VersionId.Fixed, WorkflowManager> getVersion) {
            Objects.requireNonNull(getVersion);
            m_getVersion = getVersion;
            return this;
        }

        /**
         * @param onDispose
         * @return this
         */
        @Override
        public Builder onDispose(final Consumer<WorkflowManager> onDispose) {
            Objects.requireNonNull(onDispose);
            m_onDispose = onDispose;
            return this;
        }

        /**
         * @param clearReport
         * @return this
         */
        @Override
        public Builder clearReport(final Runnable clearReport) {
            Objects.requireNonNull(clearReport);
            m_clearReport = clearReport;
            return this;
        }

        /**
         * @param generateReport
         * @return this
         */
        @Override
        public Builder generateReport(final Function<String, byte[]> generateReport) {
            Objects.requireNonNull(generateReport);
            m_generateReport = generateReport;
            return this;
        }

        /**
         * @return a new {@link Project}-instance
         */
        @Override
        public Project build() {
            return new CachedProject(this);
        }
    }

}
