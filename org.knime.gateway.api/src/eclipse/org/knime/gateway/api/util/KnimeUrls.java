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
package org.knime.gateway.api.util;

import static org.knime.gateway.api.entity.EntityBuilderManager.builder;

import java.net.URI;
import java.util.Optional;
import java.util.function.Function;

import org.knime.core.node.NodeLogger;
import org.knime.core.node.workflow.contextv2.WorkflowContextV2;
import org.knime.core.util.exception.ResourceAccessException;
import org.knime.core.util.urlresolve.KnimeUrlResolver;
import org.knime.core.util.urlresolve.URLResolverUtil;
import org.knime.gateway.api.webui.entity.LinkTypeEnt;
import org.knime.gateway.api.webui.entity.SpaceProviderEnt;

/**
 * TODO
 */
public class KnimeUrls {

    public static LinkTypeEnt.TypeEnum urlVariantToType(final KnimeUrlResolver.KnimeUrlVariant variant) {
        return switch (variant) {
            case WORKFLOW_RELATIVE -> LinkTypeEnt.TypeEnum.WORKFLOW_RELATIVE;
            case NODE_RELATIVE -> LinkTypeEnt.TypeEnum.NODE_RELATIVE;
            case SPACE_RELATIVE -> LinkTypeEnt.TypeEnum.SPACE_RELATIVE;
            case MOUNTPOINT_ABSOLUTE_PATH -> LinkTypeEnt.TypeEnum.MOUNTPOINT_ABSOLUTE; // todo rename this to match
            case MOUNTPOINT_ABSOLUTE_ID -> LinkTypeEnt.TypeEnum.MOUNTPOINT_ABSOLUTE_ID_BASED;
            case MOUNTPOINT_RELATIVE -> LinkTypeEnt.TypeEnum.MOUNTPOINT_RELATIVE;
        };
    }

    /**
     * No network request
     * @param uri
     * @return
     */
    public static LinkTypeEnt getLinkType(final URI uri) {
        return KnimeUrlResolver.KnimeUrlVariant.getVariant(uri)
                .map(KnimeUrls::urlVariantToType)
                .map(KnimeUrls::buildLinkTypeEnt)
                .orElseThrow(); // TODO
    }

    /**
     * Heuristic to avoid network requests (null translator)
     *
     * @param uri
     * @param context
     * @param getSpaceProviderType
     * @return
     */
    public static boolean isLinkTypeChangeable(final URI uri, WorkflowContextV2 context, Function<String, Optional<SpaceProviderEnt.TypeEnum>> getSpaceProviderType) {

        final var optLinkVariant = KnimeUrlResolver.KnimeUrlVariant.getVariant(uri);
        if (optLinkVariant.isEmpty()) {
            return false;
        }

        try {
            final var resolver = KnimeUrlResolver.getResolver(context);
            // find the space provider ID by converting the URL to mountpoint-absolute
            final var spaceProviderType = resolver.resolveToAbsolute(uri) //
                    .flatMap(url -> getSpaceProviderType.apply(url.getAuthority())) //
                    .orElse(null);
            if (spaceProviderType == SpaceProviderEnt.TypeEnum.HUB) {
                // can convert between ID-based and path-based URLs (not done here because it needs a REST call)
                return true;
            }

            final var urls = resolver.changeLinkType(URLResolverUtil.toURL(uri), null);
            final var linkVariant = optLinkVariant.get();
            if (urls.size() > (urls.containsKey(linkVariant) ? 1 : 0)) {
                // there are other options available
                return true;
            }
        } catch (ResourceAccessException e) {
            NodeLogger.getLogger(KnimeUrls.class).debug(
                    () -> "Cannot compute alternative KNIME URL types for '" + uri + "': " + e.getMessage(), e);
        }
        return false;
    }

    /**
     * Get a {@link LinkTypeEnt} for a {@link LinkTypeEnt.TypeEnum}.
     *
     * @param type the link type
     * @return the link type entity
     */
    public static LinkTypeEnt buildLinkTypeEnt(final LinkTypeEnt.TypeEnum type) {
        return builder(LinkTypeEnt.LinkTypeEntBuilder.class).setType(type).build();
    }

}
