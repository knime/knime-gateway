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
 *   Nov 17, 2023 (hornm): created
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
import org.knime.gateway.api.webui.entity.SpaceItemReferenceEnt;
import org.knime.gateway.impl.util.Lazy;
import org.knime.gateway.api.util.VersionId;
import org.knime.gateway.impl.webui.spaces.SpaceProvider;
import org.knime.gateway.impl.webui.spaces.local.LocalSpace;

/**
 * Implementation that caches associated {@link WorkflowManager}s.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public final class CachedProject implements Project {

    private static final int VERSION_WFM_CACHE_MAX_SIZE = 5;

    private final Consumer<WorkflowManager> m_onDispose;

    private final String m_id;

    private final String m_name;

    private final Origin m_origin;

    private final Function<VersionId.Fixed, WorkflowManager> m_getVersion;

    private final Map<VersionId.Fixed, WorkflowManager> m_cachedVersions = new LRUCache<>(VERSION_WFM_CACHE_MAX_SIZE);

    private final Lazy.Init<WorkflowManager> m_cachedWfm;

    /**
     * @param builder
     */
    private CachedProject(final Builder builder) {
        m_cachedWfm = new Lazy.Init<>(builder.m_getWfm);
        m_id = builder.m_id;
        m_name = builder.m_name;
        m_origin = builder.m_origin;
        m_getVersion = builder.m_getVersion;
        m_onDispose = builder.m_onDispose;
    }

    /**
     * Creates a project based on a given {@link WorkflowContextV2}.
     */
    public static CachedProject of(final WorkflowManager wfm, final WorkflowContextV2 context,
        final SpaceItemReferenceEnt.ProjectTypeEnum projectType, final String customProjectId,
        final LocalSpace localSpace) {
        final var path = context.getExecutorInfo().getLocalWorkflowPath();
        final var itemId = localSpace.getItemId(path);
        final var origin =
            Origin.of(SpaceProvider.LOCAL_SPACE_PROVIDER_ID, LocalSpace.LOCAL_SPACE_ID, itemId, projectType);
        final var projectName = path.toFile().getName();
        return CachedProject.builder() //
            .setWfm(wfm) //
            .setOrigin(origin) //
            .setName(projectName) //
            .setId(customProjectId) //
            .build();
    }

    /**
     * @param wfm
     * @return a builder for {@link CachedProject}-instances
     */
    public static BuilderStage.RequiresWorkflow builder() {
        return new Builder();
    }

    @Override
    public String getName() {
        return m_name;
    }

    @Override
    public String getID() {
        return m_id;
    }

    @Override
    public WorkflowManager getWorkflowManager() {
        return m_cachedWfm.get();
    }

    @Override
    public Optional<WorkflowManager> getWorkflowManagerIfLoaded() {
        if (m_cachedWfm.isInitialized()) {
            return Optional.of(m_cachedWfm.get());
        } else {
            return Optional.empty();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Origin> getOrigin() {
        return Optional.ofNullable(m_origin);
    }

    @Override
    public Optional<WorkflowManager> getVersion(final VersionId.Fixed version) {
        if (m_getVersion == null) {
            return Optional.empty();
        }
        return Optional.of(m_cachedVersions.computeIfAbsent(version, m_getVersion));
    }

    @Override
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
            NodeLogger.getLogger(CachedProject.class).error(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(m_id).append(m_name).append(m_origin).build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof CachedProject otherProject)) {
            return false;
        }
        return new EqualsBuilder().append(m_id, otherProject.getID()).append(m_name, otherProject.getName())
            .append(m_origin, otherProject.getOrigin().orElse(null)).build();
    }

    public interface BuilderStage {

        interface RequiresId {
            Optionals setId(final String id);
        }

        interface RequiresWorkflow {
            Optionals setWfm(final WorkflowManager wfm);

            RequiresName setWfmLoader(final Supplier<WorkflowManager> getWfm);
        }

        interface RequiresName {
            RequiresId setName(final String name);
        }

        interface Optionals extends RequiresId, RequiresName {
            Optionals setOrigin(Origin origin);

            Optionals getVersion(Function<VersionId.Fixed, WorkflowManager> getVersion);

            Optionals onDispose(Consumer<WorkflowManager> onDispose);

            CachedProject build();
        }
    }

    /**
     * Builder for {@link CachedProject}-instances.
     */
    @SuppressWarnings({"java:S1939"}) // clearer if all implementations are listed, even though technically redundant
    public static final class Builder implements BuilderStage.RequiresId, BuilderStage.RequiresWorkflow,
        BuilderStage.RequiresName, BuilderStage.Optionals {

        private Supplier<WorkflowManager> m_getWfm;

        private String m_id;

        private String m_name;

        private Origin m_origin;

        private Function<VersionId.Fixed, WorkflowManager> m_getVersion;

        private Consumer<WorkflowManager> m_onDispose;

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
            m_getWfm = () -> wfm;
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
        public BuilderStage.Optionals setId(final String id) {
            Objects.requireNonNull(id);
            m_id = id;
            return this;
        }

        /**
         * @param name the name to set
         * @return this
         */
        public BuilderStage.RequiresId setName(final String name) {
            Objects.requireNonNull(name);
            m_name = name;
            return this;
        }

        /**
         * @param origin the origin to set
         * @return this
         */
        public Builder setOrigin(final Origin origin) {
            Objects.requireNonNull(origin);
            m_origin = origin;
            return this;
        }

        /**
         * @param getVersion
         * @return
         */
        public Builder getVersion(final Function<VersionId.Fixed, WorkflowManager> getVersion) {
            Objects.requireNonNull(getVersion);
            m_getVersion = getVersion;
            return this;
        }

        public Builder onDispose(final Consumer<WorkflowManager> onDispose) {
            Objects.requireNonNull(onDispose);
            m_onDispose = onDispose;
            return this;
        }

        /**
         * @return a new {@link CachedProject}-instance
         */
        public CachedProject build() {
            return new CachedProject(this);
        }
    }

}
