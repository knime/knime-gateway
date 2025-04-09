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
import java.util.function.Supplier;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.node.workflow.contextv2.WorkflowContextV2;
import org.knime.core.util.LRUCache;
import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.api.util.VersionId;
import org.knime.gateway.api.webui.entity.SpaceItemReferenceEnt;
import org.knime.gateway.impl.util.Lazy;
import org.knime.gateway.impl.webui.spaces.SpaceProvider;
import org.knime.gateway.impl.webui.spaces.local.LocalSpace;

/**
 * A workflow or component project.
 *
 * @author Martin Horn, University of Konstanz
 * @noreference This interface is not intended to be referenced by clients.
 */
public final class Project {

    private static final int VERSION_WFM_CACHE_MAX_SIZE = 5;

    private final Consumer<WorkflowManager> m_onDispose;

    private final String m_id;

    private final String m_name;

    private final Origin m_origin;

    private final Function<VersionId.Fixed, WorkflowManager> m_getVersion;

    private final Map<VersionId.Fixed, WorkflowManager> m_cachedVersions = new LRUCache<>(VERSION_WFM_CACHE_MAX_SIZE);

    private final Lazy.Init<WorkflowManager> m_cachedWfm;

    private final Runnable m_clearReport;

    private final Function<String, byte[]> m_generateReport;

    private Project(final Builder builder) {
        m_onDispose = builder.m_onDispose;
        m_id = builder.m_id;
        m_name = builder.m_name;
        m_origin = builder.m_origin;
        m_getVersion = builder.m_getVersion;
        m_cachedWfm = builder.m_loadedWfm != null ? //
            new Lazy.Init<>(builder.m_loadedWfm) : //
            new Lazy.Init<>(builder.m_getWfm);
        m_clearReport = builder.m_clearReport != null ? //
            builder.m_clearReport : //
            () -> {};
        m_generateReport = builder.m_generateReport;
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
     * @return The workflow manager of the project of the current state
     */
    public Optional<WorkflowManager> getFromCacheOrLoadWorkflowManager() {
        return Optional.ofNullable(m_cachedWfm.get());
    }

    /**
     * @param version
     * @return The workflow manager of the project of a given {@link VersionId}.
     */
    public Optional<WorkflowManager> getFromCacheOrLoadWorkflowManager(final VersionId version) {
        return version instanceof VersionId.Fixed fixedVersion ? //
            getVersion(fixedVersion) : //
            getFromCacheOrLoadWorkflowManager();
    }

    private Optional<WorkflowManager> getVersion(final VersionId.Fixed version) {
        if (this.m_getVersion == null) {
            return Optional.empty();
        }
        return Optional.of(m_cachedVersions.computeIfAbsent(version, this.m_getVersion));
    }

    /**
     * @return The root workflow manager of the project of the current state, or empty if that workflow manager is not
     *         yet loaded.
     */
    public Optional<WorkflowManager> getWorkflowManagerIfLoaded() {
        return m_cachedWfm.isInitialized() ? //
            Optional.of(m_cachedWfm.get()) : //
            Optional.empty();
    }

    /**
     * @param version
     * @return The root workflow manager of the project of a given {@link VersionId}, or empty if that workflow manager
     *         is not yet loaded.
     */
    public Optional<WorkflowManager> getWorkflowManagerIfLoaded(final VersionId version) {
        // TODO: Use this method to double check if the version is loaded
        if (version instanceof VersionId.Fixed fixedVersion) {
            return m_cachedVersions.containsKey(fixedVersion) ? //
                Optional.of(m_cachedVersions.get(fixedVersion)) : //
                Optional.empty();
        }
        return this.getWorkflowManagerIfLoaded();
    }

    /**
     * @return Describes from where this workflow/component project originates, i.e. from where it has been created; an
     *         empty optional if the origin is unknown
     */
    public Optional<Origin> getOrigin() {
        return Optional.ofNullable(m_origin);
    }

    /**
     * Dispose the project.
     */
    public void dispose() {
        m_cachedWfm.ifInitialized(this::disposeWorkflow);
        m_cachedVersions.values().forEach(this::disposeWorkflow);
        m_cachedVersions.clear();
    }

    private void disposeWorkflow(final WorkflowManager wfm) {
        if (wfm == null) {
            return;
        }
        if (m_onDispose != null) {
            m_onDispose.accept(wfm);
        }
        try {
            CoreUtil.cancelAndCloseLoadedWorkflow(wfm);
        } catch (InterruptedException e) { // NOSONAR
            NodeLogger.getLogger(Project.class).error(e);
        }
    }

    /**
     * Clears the report directory of the workflow project.
     */
    public void clearReport() {
        m_clearReport.run();
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
        if (m_generateReport == null) {
            throw new UnsupportedOperationException(
                "No report generation function set for project '" + this.m_name + "'");
        }
        return this.m_generateReport.apply(format);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder() //
            .append(m_id) //
            .append(m_name) //
            .append(m_origin) //
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
            .append(m_id, otherProject.getID()) //
            .append(m_name, otherProject.getName()) //
            .append(m_origin, otherProject.getOrigin().orElse(null)) //
            .build();
    }

    /**
     * @return A builder to create a new {@link Project}-instance.
     */
    public static BuilderStage.RequiresWorkflow builder() {
        return new Builder();
    }

    /**
     * Creates a project based on a given {@link WorkflowContextV2}.
     *
     * @param wfm
     * @param context
     * @param projectType
     * @param customProjectId
     * @param localSpace
     * @return A new {@link Project}-instance
     */
    public static Project of(final WorkflowManager wfm, final WorkflowContextV2 context,
        final SpaceItemReferenceEnt.ProjectTypeEnum projectType, final String customProjectId,
        final LocalSpace localSpace) {
        final var path = context.getExecutorInfo().getLocalWorkflowPath();
        final var itemId = localSpace.getItemId(path);
        final var origin =
            new Origin(SpaceProvider.LOCAL_SPACE_PROVIDER_ID, LocalSpace.LOCAL_SPACE_ID, itemId, projectType);
        final var projectName = path.toFile().getName();
        return Project.builder() //
            .setWfm(wfm) //
            .setOrigin(origin) //
            .setName(projectName) //
            .setId(customProjectId) //
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
            Objects.requireNonNull(wfm);
            m_loadedWfm = wfm;
            m_name = wfm.getName();
            m_id = getUniqueProjectId(m_name);
            return this;
        }

        @Override
        public BuilderStage.RequiresName setWfmLoader(final Supplier<WorkflowManager> supplier) {
            Objects.requireNonNull(supplier);
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
            m_origin = origin;
            return this;
        }

        /**
         * @param getVersion
         * @return this
         */
        @Override
        public Builder setVersionWfmLoader(final Function<VersionId.Fixed, WorkflowManager> getVersion) {
            m_getVersion = getVersion;
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
