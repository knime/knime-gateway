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

import java.util.Optional;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.knime.gateway.api.util.VersionId;
import org.knime.gateway.api.webui.entity.SpaceItemReferenceEnt;
import org.knime.gateway.api.webui.entity.SpaceItemVersionEnt;
import org.knime.gateway.impl.webui.spaces.SpaceProvider;

/**
 * Identifies space and item from which this workflow/component project has been opened.
 *
 * @param providerId The ID of the space provider containing the workflow/component project
 * @param spaceId The space ID of the workflow/component project
 * @param itemId The item ID of the workflow/component project
 * @param projectType The project type of the space item
 * @param versionId The version ID of the workflow/component project, or absent for latest version
 * @param itemVersion The item version of the workflow/component project, or absent for latest version
 */
public record Origin(//
    String providerId, //
    String spaceId, //
    String itemId, //
    Optional<SpaceItemReferenceEnt.ProjectTypeEnum> projectType, //
    Optional<VersionId> versionId, //
    Optional<SpaceItemVersionEnt> itemVersion) { // TODO NXT-3701: Remove itemVersion from this record

    /**
     * @see Origin
     */
    @SuppressWarnings("java:S1176")
    public Origin(final String providerId, final String spaceId, final String itemId) {
        this(providerId, spaceId, itemId, Optional.empty(), Optional.empty(), Optional.empty());
    }

    /**
     * @see Origin
     */
    @SuppressWarnings("java:S1176")
    public Origin(final String providerId, final String spaceId, final String itemId,
        final SpaceItemReferenceEnt.ProjectTypeEnum projectType) {
        this(providerId, spaceId, itemId, Optional.ofNullable(projectType), Optional.empty(), Optional.empty());
    }

    /**
     * -
     *
     * @param originalOrigin -
     * @param versionId -
     *
     * @return A new instance with same property values and updated 'versionId' property.
     */
    public static Origin updateVersionId(final Origin originalOrigin, final VersionId versionId) {
        return new Origin(originalOrigin.providerId, originalOrigin.spaceId, originalOrigin.itemId,
            originalOrigin.projectType, Optional.ofNullable(versionId), originalOrigin.itemVersion);
    }

    /**
     * @return {@code true} if the space provider is local
     */
    public boolean isLocal() {
        return this.providerId.equals(SpaceProvider.LOCAL_SPACE_PROVIDER_ID);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Origin)) {
            return false;
        }

        final Origin other = (Origin)obj;
        return new EqualsBuilder()//
            .append(providerId, other.providerId)//
            .append(spaceId, other.spaceId)//
            .append(itemId, other.itemId)//
            .isEquals();
    }

}
