// TODO license
package org.knime.gateway.api.util;

import static org.knime.gateway.api.entity.EntityBuilderManager.builder;

import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
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

    /**
     * Map the serializable gateway entity to the core type
     */
    public static final BidiMap<LinkTypeEnt.TypeEnum, Optional<KnimeUrlResolver.KnimeUrlVariant>> KNIMEURLVARIANT_TO_TYPEENUM = new DualHashBidiMap<>(
        Map.of(LinkTypeEnt.TypeEnum.WORKFLOW_RELATIVE, Optional.of(KnimeUrlResolver.KnimeUrlVariant.WORKFLOW_RELATIVE),
            LinkTypeEnt.TypeEnum.SPACE_RELATIVE, Optional.of(KnimeUrlResolver.KnimeUrlVariant.SPACE_RELATIVE),
            LinkTypeEnt.TypeEnum.MOUNTPOINT_ABSOLUTE, Optional.of(KnimeUrlResolver.KnimeUrlVariant.MOUNTPOINT_ABSOLUTE_PATH),
            LinkTypeEnt.TypeEnum.MOUNTPOINT_ABSOLUTE_ID_BASED, Optional.of(KnimeUrlResolver.KnimeUrlVariant.MOUNTPOINT_ABSOLUTE_ID),
            LinkTypeEnt.TypeEnum.NONE, Optional.empty()));

    /**
     * No network request
     * @param uri
     * @return
     */
    public static LinkTypeEnt getLinkType(final URI uri) {
        return KnimeUrlResolver.KnimeUrlVariant.getVariant(uri)
                .map(var -> KNIMEURLVARIANT_TO_TYPEENUM.inverseBidiMap().get(Optional.of(var)))
                .map(KnimeUrls::buildLinkTypeEnt)
                .orElseThrow(); // TODO
    }

    /**
     * No network request
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
