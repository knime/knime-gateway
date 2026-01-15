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
package org.knime.gateway.api.webui.util;

import org.knime.core.node.workflow.MetaNodeTemplateInformation;
import org.knime.core.node.workflow.NodeContainerTemplate;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.util.KnimeUrlType;
import org.knime.core.util.hub.ItemVersion;
import org.knime.core.util.urlresolve.URLResolverUtil;
import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.api.util.ItemVersions;
import org.knime.gateway.api.util.KnimeUrls;
import org.knime.gateway.api.webui.entity.ItemVersionEnt;
import org.knime.gateway.api.webui.entity.SpaceProviderEnt;
import org.knime.gateway.api.webui.entity.TemplateLinkEnt;

import java.net.URI;
import java.util.Optional;
import java.util.function.Function;

import static org.knime.gateway.api.entity.EntityBuilderManager.builder;

public class TemplateLinkEntityFactory {
    /**
     * Package-private constructor to restrict instantiation to this package.
     */
    TemplateLinkEntityFactory() {
    }

    static String getTemplateLink(final NodeContainerTemplate nct) {
        if (nct instanceof SubNodeContainer snc && snc.isProject()) {
            return null;
        }
        var sourceURI = nct.getTemplateInformation().getSourceURI();
        return sourceURI == null ? null : sourceURI.toString();
    }

    static TemplateLinkEnt buildTemplateLinkEnt(final NodeContainerTemplate nct,
        final Function<String, Optional<SpaceProviderEnt.TypeEnum>> getSpaceProviderType) {
        final var templateInfo = nct.getTemplateInformation();
        if (templateInfo.getRole() != MetaNodeTemplateInformation.Role.Link) { // Only works for linked components and metanodes
            return null;
        }

        var updateStatus = switch (templateInfo.getUpdateStatus()) {
            case UpToDate -> TemplateLinkEnt.UpdateStatusEnum.UP_TO_DATE;
            case HasUpdate -> TemplateLinkEnt.UpdateStatusEnum.HAS_UPDATE;
            case Error -> TemplateLinkEnt.UpdateStatusEnum.ERROR;
        };

        final var linkUri = templateInfo.getSourceURI();

        return builder(TemplateLinkEnt.TemplateLinkEntBuilder.class) //
            .setUrl(getTemplateLink(nct))//
            .setUpdateStatus(updateStatus) //
            .setIsLinkVariantChangeable( //
                KnimeUrls.isLinkTypeChangeable( //
                    linkUri, //
                    CoreUtil.getProjectWorkflow(nct.getParent()).getContextV2(), //
                    getSpaceProviderType) //
            ) //
            .setIsHubItemVersionChangeable(isHubItemVersionChangeable(linkUri, getSpaceProviderType)) //
            .setCurrentLinkVariant(KnimeUrls.getLinkVariant(linkUri)) //
            .setTargetHubItemVersion(getTargetItemVersion(linkUri)) //
            .build();
    }

    private static ItemVersionEnt getTargetItemVersion(final URI linkUri) {
        var version = URLResolverUtil.parseVersion(linkUri.getQuery()) //
            .orElse(ItemVersion.currentState());
        return ItemVersions.toEntity(version);
    }

    /**
     * The version of a KNIME URL can be changed if it is an absolute URL to a Hub repository item.
     *
     * @param uri KNIME URL to check
     * @param getSpaceProviderType provider lookup used to determine whether the URL points to a Hub
     * @return {@code true} if changing Hub versions is possible, {@code false} otherwise
     */
    private static boolean isHubItemVersionChangeable(final URI uri,
        final Function<String, Optional<SpaceProviderEnt.TypeEnum>> getSpaceProviderType) {
        return KnimeUrlType.getType(uri).orElse(null) == KnimeUrlType.MOUNTPOINT_ABSOLUTE
            && getSpaceProviderType.apply(uri.getAuthority()).orElse(null) == SpaceProviderEnt.TypeEnum.HUB;
    }
}
