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

import static org.knime.gateway.api.entity.EntityBuilderManager.builder;

import java.util.Optional;

import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.node.workflow.contextv2.AnalyticsPlatformExecutorInfo;
import org.knime.core.node.workflow.contextv2.HubSpaceLocationInfo;
import org.knime.core.node.workflow.contextv2.WorkflowContextV2;
import org.knime.core.util.hub.NamedItemVersion;
import org.knime.gateway.api.webui.entity.SpaceItemReferenceEnt.ProjectTypeEnum;
import org.knime.gateway.api.webui.entity.SpaceItemVersionEnt;
import org.knime.gateway.impl.webui.spaces.SpaceProvider;
import org.knime.gateway.impl.webui.spaces.local.LocalSpace;

/**
 * Represents a workflow or component project.
 *
 * @author Martin Horn, University of Konstanz
 * @noreference This interface is not intended to be referenced by clients.
 */
@SuppressWarnings("javadoc")
public interface Project {

    /**
     * Creates a project based on space-item-infos.
     */
    static Project of(final WorkflowManager wfm, final String spaceProviderId, final String spaceId,
        final String itemId, final String relativePath, final ProjectTypeEnum projectType) {
        return of(wfm, spaceProviderId, spaceId, itemId, relativePath, projectType, null);
    }

    /**
     * Creates a project based on space-item-infos using a custom project ID.
     */
    static Project of(final WorkflowManager wfm, final String providerId, final String spaceId, final String itemId,
        final String relativePath, final ProjectTypeEnum projectType, final String customProjectId) {
        final var origin = Origin.of(providerId, spaceId, itemId, relativePath, projectType);
        final var projectName = wfm.getName();
        return of(wfm, origin, projectName, customProjectId);
    }

    /**
     * Creates a project considering most notably the {@link WorkflowManager} and the {@link WorkflowContextV2}.
     */
    static Project of(final WorkflowManager wfm, final WorkflowContextV2 context, final ProjectTypeEnum projectType,
        final String customProjectId, final LocalSpace localSpace) {
        final var path = context.getExecutorInfo().getLocalWorkflowPath();
        final var itemId = localSpace.getItemId(path);
        final var relativePath = localSpace.getRootPath().relativize(path).toString();
        final var origin = Origin.of(SpaceProvider.LOCAL_SPACE_PROVIDER_ID, LocalSpace.LOCAL_SPACE_ID, itemId,
            relativePath, projectType);
        final var projectName = path.toFile().getName();
        return of(wfm, origin, projectName, customProjectId);
    }

    /**
     * Creates a project from a {@link WorkflowManager} and an {@link Origin}
     */
    static Project of(final WorkflowManager wfm, final Origin origin, final String projectName,
        final String customProjectId) {
        final var projectId =
            customProjectId == null ? DefaultProject.getUniqueProjectId(wfm.getName()) : customProjectId;
        return DefaultProject.builder(wfm) //
            .setId(projectId) //
            .setName(projectName)//
            .setOrigin(origin) //
            .build();
    }

    /**
     * @return the name of the project
     */
    String getName();

    /**
     * @return an id of the project
     */
    String getID();

    /**
     * Opens/loads the actual workflow represented by this workflow/component project. If the workflow has already been
     * opened before it will be opened/loaded again.
     *
     * If the workflow manager is already available with the creation of this project instance,
     * {@link #getWorkflowManager()} should be implemented, too.
     *
     * @return the newly loaded workflow
     */
    WorkflowManager loadWorkflowManager();

    /**
     * This method only returns a workflow manager if it doesn't require to be loaded and is already available with the
     * creation of this project instance. Otherwise {@link #loadWorkflowManager()} will load it.
     *
     * @return the workflow manager if already available, otherwise an empty optional
     */
    default Optional<WorkflowManager> getWorkflowManager() {
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
     * Clears the report directory of the workflow project (if there is any).
     */
    default void clearReport() {
    }

    /**
     * Generates a report. See
     * <code>com.knime.enterprise.executor.JobPool#generateReport(com.knime.enterprise.executor.WorkflowJob, org.knime.core.util.report.ReportingConstants.RptOutputFormat, org.knime.core.util.report.ReportingConstants.RptOutputOptions)</code>
     *
     * @param format the report format
     * @return the report directory or an empty optional
     * @throws IllegalArgumentException if the format is not supported or invalid
     * @throws IllegalStateException if report generation failed for some reason
     */
    default byte[] generateReport(final String format) {
        throw new UnsupportedOperationException("Report generation not supported");
    }

    /**
     * Identifies space and item from which this workflow/component project has been opened.
     */
    interface Origin {
        @SuppressWarnings("javadoc")
        static Origin of(final String providerId, final String spaceId, final String itemId, final String relativePath,
            final ProjectTypeEnum projectType) {
            return new Origin() { // NOSONAR
                @Override
                public String getProviderId() {
                    return providerId;
                }

                @Override
                public String getSpaceId() {
                    return spaceId;
                }

                @Override
                public String getItemId() {
                    return itemId;
                }

                @Override
                public Optional<ProjectTypeEnum> getProjectType() {
                    return Optional.ofNullable(projectType);
                }
            };
        }

        /**
         * Creates an {@link Origin} from a Hub Space and a WorkflowManager
         *
         * @param hubLocation location information of the item
         * @param wfm the WorkflowManager that contains the project
         * @param selectedVersion the version information of the item, can be empty
         * @return The newly created Origin, or an empty {@link Optional} if hubLocation or workflow manager are missing
         */
        static Optional<Origin> of(final HubSpaceLocationInfo hubLocation, final WorkflowManager wfm,
            final Optional<NamedItemVersion> selectedVersion) { // NOSONAR: The version is optional
            if (hubLocation == null || wfm == null) {
                return Optional.empty();
            }
            final var context = wfm.getContextV2();
            final var apExecInfo = (AnalyticsPlatformExecutorInfo)context.getExecutorInfo();
            final var versionInfo = selectedVersion.map(Origin::buildVersionInfo);
            return Optional.of(new Origin() {

                @Override
                public String getProviderId() {
                    final var mountpoint = apExecInfo.getMountpoint().orElseThrow(
                        () -> new IllegalStateException("Missing Mount ID for Hub workflow '" + wfm + "'"));
                    return mountpoint.getFirst().getAuthority();
                }

                @Override
                public String getSpaceId() {
                    return hubLocation.getSpaceItemId();
                }

                @Override
                public String getItemId() {
                    return hubLocation.getWorkflowItemId();
                }

                @Override
                public Optional<ProjectTypeEnum> getProjectType() {
                    return Optional
                        .of(wfm.isComponentProjectWFM() ? ProjectTypeEnum.COMPONENT : ProjectTypeEnum.WORKFLOW);
                }

                @Override
                public Optional<SpaceItemVersionEnt> getItemVersion() {
                    return versionInfo;
                }
            });
        }

        static SpaceItemVersionEnt buildVersionInfo(final NamedItemVersion selectedVersion) {
            return builder(SpaceItemVersionEnt.SpaceItemVersionEntBuilder.class) //
                .setVersion(selectedVersion.version()) //
                .setTitle(selectedVersion.title()) //
                .setDescription(selectedVersion.description()) //
                .setAuthor(selectedVersion.author()) //
                .setAuthorAccountId(selectedVersion.authorAccountId()) //
                .setCreatedOn(selectedVersion.createdOn()) //
                .build();
        }

        /**
         * @return The ID of the space provider containing the workflow/component project
         */
        String getProviderId();

        /**
         * @return The space ID of the workflow/component project
         */
        String getSpaceId();

        /**
         * @return The item ID of the workflow/component project
         */
        String getItemId();

        /**
         * @return The project type of the space item
         */
        Optional<ProjectTypeEnum> getProjectType();

        /**
         * @return the relative path of the original space item - usually only given for the local space
         */
        default Optional<String> getRelativePath() {
            return Optional.empty();
        }

        /**
         * @return The item version of the workflow/component project, or absent for latest version
         * @since 5.4
         */
        default Optional<SpaceItemVersionEnt> getItemVersion() {
            return Optional.empty();
        }
    }
}