package org.knime.gateway.api.util;

import static org.knime.gateway.api.entity.EntityBuilderManager.builder;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.util.exception.ResourceAccessException;
import org.knime.core.util.pathresolve.ResolverUtil;
import org.knime.core.util.urlresolve.KnimeUrlResolver;
import org.knime.core.util.urlresolve.URLResolverUtil;
import org.knime.gateway.api.webui.entity.LinkTypeEnt;
import org.knime.gateway.api.webui.service.util.MutableServiceCallException;

/**
 * Utility methods for working with component links.
 */
public final class ComponentLinkUtil {
    private ComponentLinkUtil() {

    }

    /**
     * Map the serializable gateway entity to the core type
     */
    private static final BidiMap<LinkTypeEnt.TypeEnum, Optional<KnimeUrlResolver.KnimeUrlVariant>> LINK_TYPE_VARIANTS = new DualHashBidiMap<>(Map.of(
            LinkTypeEnt.TypeEnum.WORKFLOW_RELATIVE, Optional.of(KnimeUrlResolver.KnimeUrlVariant.WORKFLOW_RELATIVE),
            LinkTypeEnt.TypeEnum.SPACE_RELATIVE, Optional.of(KnimeUrlResolver.KnimeUrlVariant.SPACE_RELATIVE),
            LinkTypeEnt.TypeEnum.MOUNTPOINT_ABSOLUTE, Optional.of(KnimeUrlResolver.KnimeUrlVariant.MOUNTPOINT_ABSOLUTE_PATH),
            LinkTypeEnt.TypeEnum.MOUNTPOINT_ABSOLUTE_ID_BASED, Optional.of(KnimeUrlResolver.KnimeUrlVariant.MOUNTPOINT_ABSOLUTE_ID),
            LinkTypeEnt.TypeEnum.NONE, Optional.empty()
    ));

    /**
     * Parse a link type into a {@link KnimeUrlResolver.KnimeUrlVariant}.
     * @param linkType the link type
     * @return the variant. Throws if the link type is not supported
     */
    public static Optional<KnimeUrlResolver.KnimeUrlVariant>
        parseUrlVariant(final LinkTypeEnt linkType) throws MutableServiceCallException {
        var result =  LINK_TYPE_VARIANTS.get(linkType.getType());
        if (result == null) {
            throw new MutableServiceCallException("Unsupported link type: " + linkType.getType(), true);
        }
        return result;
    }

    /**
     * Get a {@link LinkTypeEnt} for a {@link KnimeUrlResolver.KnimeUrlVariant}.
     * @param variant the variant
     * @return the link type, or empty if the variant is not supported
     */
    public static Optional<LinkTypeEnt> getLinkType(final KnimeUrlResolver.KnimeUrlVariant variant) {
        var type = LINK_TYPE_VARIANTS.inverseBidiMap().get(Optional.of(variant));
        if (type == null) {
            return Optional.empty();
        }
        return Optional.of(getEntity(type));
    }

    /**
     * Get a {@link LinkTypeEnt} for a {@link LinkTypeEnt.TypeEnum}.
     *
     * @param type the link type
     * @return the link type entity
     */
    public static LinkTypeEnt getEntity(final LinkTypeEnt.TypeEnum type) {
        return builder(LinkTypeEnt.LinkTypeEntBuilder.class)
                .setType(type)
                .build();
    }

    /**
     * Get a link variant (workflow-relative, space-relative, ...) based on a link to some item.
     *
     * @param requestedVariant the requested link variant
     * @param itemUri          the URI of the item
     * @param hostWfm          the workflow manager containing the linked component
     * @return the link URL for the requested variant, or null if the variant is not available
     * @throws ResourceAccessException if the link cannot be resolved
     * @throws MalformedURLException if the link is malformed
     */
    public static Optional<URI> getVariant(final KnimeUrlResolver.KnimeUrlVariant requestedVariant, final URI itemUri, final WorkflowManager hostWfm) throws ResourceAccessException, MalformedURLException {
        final var resolver = KnimeUrlResolver.getResolver(hostWfm.getContextV2());
        final var variants = resolver.changeLinkType(URLResolverUtil.toURL(itemUri), ResolverUtil::translateHubUrl);
        var value = variants.get(requestedVariant);
        if (value == null) {
            return Optional.empty();
        }
        return Optional.of(URLResolverUtil.toURI(value));

    }

    /**
     * Get a {@link LinkTypeEnt} for a link URI.
     *
     * @param linkUri the link URI
     * @return the link type, or null if the link URI is not supported
     */
    public static LinkTypeEnt getLinkType(final URI linkUri) {
        if (linkUri == null) {
            return null;
        }
        return KnimeUrlResolver.KnimeUrlVariant.getVariant(linkUri)
            .flatMap(ComponentLinkUtil::getLinkType)
            .orElse(null);
    }
}
