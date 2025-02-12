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

import static org.knime.gateway.api.entity.EntityBuilderManager.builder;

import java.util.Optional;

import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.node.workflow.contextv2.AnalyticsPlatformExecutorInfo;
import org.knime.core.node.workflow.contextv2.HubSpaceLocationInfo;
import org.knime.core.util.hub.NamedItemVersion;
import org.knime.gateway.api.webui.entity.SpaceItemReferenceEnt;
import org.knime.gateway.api.webui.entity.SpaceItemVersionEnt;
import org.knime.gateway.impl.webui.spaces.SpaceProvider;

/**
 * Identifies space and item from which this workflow/component project has been opened.
 */
public interface Origin {
    /**
     * @param providerId
     * @param spaceId
     * @param itemId
     * @param projectType the type of the project or {@code null}
     * @return a new instance
     */
    static Origin of(final String providerId, final String spaceId, final String itemId,
        final SpaceItemReferenceEnt.ProjectTypeEnum projectType) {
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
            public Optional<SpaceItemReferenceEnt.ProjectTypeEnum> getProjectType() {
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
     * @return The newly created Origin, or empty if hubLocation or workflow manager are missing
     */
    @SuppressWarnings({"java:S1188"})
    static Optional<Origin> of(final HubSpaceLocationInfo hubLocation, final WorkflowManager wfm,
        final NamedItemVersion selectedVersion) {
        if (hubLocation == null || wfm == null) {
            return Optional.empty();
        }
        final var context = wfm.getContextV2();
        final var apExecInfo = (AnalyticsPlatformExecutorInfo)context.getExecutorInfo();
        final var versionInfo = selectedVersion == null ? null : Origin.buildVersionInfo(selectedVersion);
        return Optional.of(new Origin() {

            @Override
            public String getProviderId() {
                final var mountpoint = apExecInfo.getMountpoint()
                    .orElseThrow(() -> new IllegalStateException("Missing Mount ID for Hub workflow '" + wfm + "'"));
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
            public Optional<SpaceItemReferenceEnt.ProjectTypeEnum> getProjectType() {
                return Optional.of(wfm.isComponentProjectWFM() ? SpaceItemReferenceEnt.ProjectTypeEnum.COMPONENT
                    : SpaceItemReferenceEnt.ProjectTypeEnum.WORKFLOW);
            }

            @Override
            public Optional<SpaceItemVersionEnt> getItemVersion() {
                return Optional.ofNullable(versionInfo);
            }
        });
    }

    private static SpaceItemVersionEnt buildVersionInfo(final NamedItemVersion selectedVersion) {
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
    Optional<SpaceItemReferenceEnt.ProjectTypeEnum> getProjectType();

    /**
     * @return The item version of the workflow/component project, or absent for latest version
     * @since 5.4
     */
    default Optional<SpaceItemVersionEnt> getItemVersion() {
        return Optional.empty();
    }

    default boolean isLocal() {
        return this.getProviderId().equals(SpaceProvider.LOCAL_SPACE_PROVIDER_ID);
    }
}
