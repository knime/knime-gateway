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

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.api.util.VersionId;

/**
 * A workflow or component project.
 *
 * @see ProjectManager
 * @author Martin Horn, University of Konstanz
 * @noreference This interface is not intended to be referenced by clients.
 */
public final class Project {

    private final String m_id;

    private final String m_name;

    private final Origin m_origin;

    private final Runnable m_clearReport;

    private final Function<String, byte[]> m_generateReport;

    private ProjectWfmCache m_projectWfmCache;

    private final Deque<Consumer<WorkflowManager>> m_onWfmLoad = new ArrayDeque<>();

    private final Deque<Consumer<WorkflowManager>> m_onWfmDispose = new ArrayDeque<>();

    private Project(final Builder builder) {
        this( //
            builder.m_id, //
            builder.m_name, //
            builder.m_origin, //
            builder.m_wfmCache, //
            Optional.ofNullable(builder.m_clearReport).orElse(() -> {
            }), //
            builder.m_generateReport //
        );
    }

    private Project(final String projectId, final String name, final Origin origin, final ProjectWfmCache cache,
        final Runnable clearReport, final Function<String, byte[]> generateReport) {
        this.m_id = projectId;
        this.m_name = name;
        this.m_origin = origin;
        this.m_projectWfmCache = cache;
        this.m_clearReport = clearReport;
        this.m_generateReport = generateReport;
    }

    /**
     * -
     *
     * @param originalProject -
     * @param newOrigin -
     * @return A new instance with same property values and updated origin property
     */
    static Project of(final Project originalProject, final Origin newOrigin) {
        return new Project( //
            originalProject.m_id, //
            originalProject.m_name, //
            newOrigin, //
            originalProject.m_projectWfmCache, //
            Optional.ofNullable(originalProject.m_clearReport).orElse(() -> {
            }), //
            originalProject.m_generateReport //
        );
    }

    /**
     * -
     *
     * @param originalProject -
     * @param newName -
     * @param newOrigin -
     * @return A new instance with same property values and updated name and origin
     */
    public static Project of(final Project originalProject, final String newName, final Origin newOrigin) {
        return new Project( //
            originalProject.m_id, //
            newName, //
            newOrigin, //
            originalProject.m_projectWfmCache, //
            Optional.ofNullable(originalProject.m_clearReport).orElse(() -> {
            }), //
            originalProject.m_generateReport //
        );
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
     * Get the {@link WorkflowManager} from cache or load it
     *
     * @return The workflow manager of the project of the current state
     */
    public Optional<WorkflowManager> getFromCacheOrLoadWorkflowManager() {
        return getFromCacheOrLoadWorkflowManager(VersionId.currentState());
    }

    /**
     * Get the {@link WorkflowManager} for the version specified from cache or load it
     *
     * @param version The version id
     * @return The workflow manager of the project of a given {@link VersionId}.
     */
    public Optional<WorkflowManager> getFromCacheOrLoadWorkflowManager(final VersionId version) {
        return Optional.ofNullable(m_projectWfmCache.getWorkflowManager(version)) //
            .map(wfm -> {
                if (version.isCurrentState() && !m_onWfmLoad.isEmpty()) {
                    m_onWfmLoad.pop().accept(wfm); // Call only once
                }
                return wfm;
            });
    }

    /**
     * Get the {@link WorkflowManager} from cache if it is already loaded
     *
     * @return The root workflow manager of the project of the current state, or empty if that workflow manager is not
     *         yet loaded.
     */
    public Optional<WorkflowManager> getWorkflowManagerIfLoaded() {
        return this.m_projectWfmCache.getWorkflowManagerIfLoaded(VersionId.currentState());
    }

    /**
     * Get the {@link WorkflowManager} for the version specified from cache if it is already loaded
     *
     * @param version The version id
     * @return The root workflow manager of the project of a given {@link VersionId}, or empty if that workflow manager
     *         is not yet loaded.
     */
    public Optional<WorkflowManager> getWorkflowManagerIfLoaded(final VersionId version) {
        return this.m_projectWfmCache.getWorkflowManagerIfLoaded(version);
    }

    /**
     * @return Describes from where this workflow/component project originates, i.e. from where it has been created; an
     *         empty optional if the origin is unknown
     */
    public Optional<Origin> getOrigin() {
        return Optional.ofNullable(m_origin);
    }

    /**
     * Dispose the loaded {@link WorkflowManager} instance for the given version.
     */
    public void dispose() {
        m_projectWfmCache.dispose();
    }

    /**
     * Call the {@code onWfmDispose} consumer for the current version's loaded {@link WorkflowManager}, if any.
     */
    public void callOnWfmDispose() {
        m_projectWfmCache.getWorkflowManagerIfLoaded(VersionId.currentState()).ifPresent(wfm -> {
            if (!m_onWfmDispose.isEmpty()) {
                m_onWfmDispose.pop().accept(wfm); // Call only once
            }
        });
    }

    /**
     * Dispose the loaded {@link WorkflowManager} instance for the given version.
     *
     * @param version -
     */
    public void disposeCachedWfm(final VersionId version) {
        this.m_projectWfmCache.dispose(version);
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

    /**
     * TODO NXT-3607 Projects can be immutable (NOSONAR)
     *
     * @param loader -
     * @return -
     */
    public Project setWfmLoader(final WorkflowManagerLoader loader) {
        var previousCache = m_projectWfmCache;
        if (previousCache.contains(VersionId.currentState())) {
            // for full generality one would have to carry over other cached instances too.
            // However, this case (only current-version available) is the only circumstance in which this method is called.
            // This is acceptable since this method will be removed with NXT-3607.
            m_projectWfmCache = new ProjectWfmCache(//
                previousCache.getWorkflowManager(VersionId.currentState()), //
                loader //
            );
        } else {
            m_projectWfmCache = new ProjectWfmCache(loader);
        }
        return this;
    }

    /**
     * -
     *
     * @param onWfmLoad -
     * @return -
     */
    public Project setOnWfmLoad(final Consumer<WorkflowManager> onWfmLoad) {
        m_onWfmLoad.add(onWfmLoad);
        return this;
    }

    /**
     * -
     *
     * @param onWfmLoad -
     * @return -
     */
    public Project setOnWfmDispose(final Consumer<WorkflowManager> onWfmLoad) {
        m_onWfmDispose.add(onWfmLoad);
        return this;
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
     * Builder for {@link Project}-instances.
     */
    public interface BuilderStage {

        /**
         * Builder stage requiring an id.
         */
        @SuppressWarnings({"MissingJavadoc", "java:S1176"})
        interface RequiresId {
            Optionals setId(final String id);
        }

        /**
         * Builder stage requiring a {@link WorkflowManager}.
         */
        @SuppressWarnings({"MissingJavadoc", "java:S1176"})
        interface RequiresWorkflow {
            /**
             * Associate a single {@code WorkflowManager} instance with this project as current state. This means this
             * project is not able to dynamically load any other kind of workflows, e.g. those of previous versions.
             *
             * @param wfm -
             * @return -
             */
            Optionals setWfm(final WorkflowManager wfm);

            /**
             * Define how the project can dynamically load workflows.
             *
             * @param wfmLoader -
             * @return -
             */
            RequiresName setWfmLoader(final WorkflowManagerLoader wfmLoader);

        }

        /**
         * Builder stage requiring a name.
         */
        @SuppressWarnings({"MissingJavadoc", "java:S1176"})
        interface RequiresName {
            RequiresId setName(final String name);
        }

        /**
         * Builder stage offering optional properties.
         */
        @SuppressWarnings({"MissingJavadoc", "java:S1176"})
        interface Optionals extends RequiresId, RequiresName {
            Optionals setOrigin(Origin origin);

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

        private Runnable m_clearReport;

        private Function<String, byte[]> m_generateReport;

        private ProjectWfmCache m_wfmCache;

        private Builder() {
            //
        }

        @Override
        public BuilderStage.Optionals setWfm(final WorkflowManager wfm) {
            Objects.requireNonNull(wfm);
            m_name = wfm.getName();
            m_id = getUniqueProjectId(m_name);
            m_wfmCache = new ProjectWfmCache(wfm, WorkflowManagerLoader.providingOnlyCurrentState(() -> wfm));
            return this;
        }

        @Override
        public BuilderStage.RequiresName setWfmLoader(final WorkflowManagerLoader wfmLoader) {
            Objects.requireNonNull(wfmLoader);
            m_wfmCache = new ProjectWfmCache(wfmLoader);
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
