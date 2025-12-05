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
package org.knime.gateway.impl.webui.spaces;

import static org.knime.gateway.api.entity.EntityBuilderManager.builder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.knime.core.node.workflow.contextv2.LocationInfo;
import org.knime.core.node.workflow.contextv2.WorkflowContextV2;
import org.knime.core.util.Pair;
import org.knime.core.util.exception.ResourceAccessException;
import org.knime.core.util.pathresolve.ResolverUtil;
import org.knime.core.util.urlresolve.KnimeUrlResolver;
import org.knime.core.util.urlresolve.URLResolverUtil;
import org.knime.gateway.api.util.KnimeUrls;
import org.knime.gateway.api.webui.entity.LinkVariantEnt;
import org.knime.gateway.api.webui.entity.LinkVariantInfoEnt;

/**
 * Provides available variants of a component link (the destination link of a "shared component").
 * <p>
 * This abstraction makes the providing backend pluggable/exchangeable and enables testing without dependency on other
 * modules.
 *
 */
public interface LinkVariants {

    /**
     * Map the enum to a richer entity with human-readable descriptions
     *
     * @param type -
     * @return -
     */
    LinkVariantInfoEnt toLinkVariantInfoEnt(LinkVariantEnt.VariantEnum type);

    /**
     * Assumes that item at {@code originalUri} actually exists
     * <p>
     * May cause a network request
     *
     * @param originalUri URI of an existing space item
     * @param currentContext -
     * @return -
     * @throws ResourceAccessException -
     */
    Map<LinkVariantEnt.VariantEnum, URI> getVariants(final URI originalUri, WorkflowContextV2 currentContext)
        throws ResourceAccessException;

    /**
     * Map {@link this#toLinkVariantInfoEnt(LinkVariantEnt.VariantEnum)} over {@link this#getVariants(URI,
     * WorkflowContextV2)}
     *
     * @see LinkVariants#getVariants(URI, WorkflowContextV2)
     * @param originalUri -
     * @param currentContext -
     * @return -
     * @throws ResourceAccessException -
     */
    default List<LinkVariantInfoEnt> getVariantInfoEnts(final URI originalUri, final WorkflowContextV2 currentContext)
        throws ResourceAccessException {
        return getVariants(originalUri, currentContext).keySet().stream() //
            .map(this::toLinkVariantInfoEnt) //
            .toList();
    }

    /**
     * Implementation backed by {@link KnimeUrlResolver}.
     */
    class KnimeUrlResolverVariants implements LinkVariants {

        /**
         * The set of available variants depends on both the destination (WorkflowContextV2) and the source (URI) of the
         * link. However, we can assume that this never changes dynamically.
         */
        private final Map< //
                // WorkflowContextV2 does not have a proper equals implementation, LocationInfo does
                Pair<URI, LocationInfo>, //  input
                Map<LinkVariantEnt.VariantEnum, URI>> //  output
        m_cache = new ConcurrentHashMap<>();

        private static Map<LinkVariantEnt.VariantEnum, URI> fetchVariants(final URI originalUri,
            final WorkflowContextV2 currentContext) throws ResourceAccessException {
            Map<LinkVariantEnt.VariantEnum, URI> result = new EnumMap<>(LinkVariantEnt.VariantEnum.class);
            try {
                var variants = KnimeUrlResolver.getResolver(currentContext).changeLinkType( //
                    URLResolverUtil.toURL(originalUri), //
                    ResolverUtil::translateHubUrl //
                ).entrySet();
                for (var entry : variants) {
                    var type = KnimeUrls.mapEnum(entry.getKey());
                    result.put(type, entry.getValue().toURI());
                }
                result.put(LinkVariantEnt.VariantEnum.NONE, null);
            } catch (URISyntaxException e) {
                throw new ResourceAccessException(e);
            }
            return result;
        }

        @Override
        public Map<LinkVariantEnt.VariantEnum, URI> getVariants(final URI originalUri,
            final WorkflowContextV2 currentContext) throws ResourceAccessException {
            var key = new Pair<>(originalUri, currentContext.getLocationInfo());
            if (!m_cache.containsKey(key)) {
                var value = fetchVariants(originalUri, currentContext);
                m_cache.put(key, value);
                return value;
            } else {
                return m_cache.get(key);
            }
        }

        /**
         * Map enum values to richer entities with descriptions
         *
         * @param type The value to map
         * @return The mapped value
         */
        @Override
        @SuppressWarnings("java:S1151")
        public LinkVariantInfoEnt toLinkVariantInfoEnt(final LinkVariantEnt.VariantEnum type) {
            if (type == null) {
                throw new IllegalArgumentException("Type must not be null");
            }
            return switch (type) {
                case NODE_RELATIVE -> builder(LinkVariantInfoEnt.LinkVariantInfoEntBuilder.class)
                    .setVariant(KnimeUrls.buildLinkVariantEnt(LinkVariantEnt.VariantEnum.NODE_RELATIVE))
                    .setTitle("Create node-relative link").setDescription("Create node-relative link")
                    .setLinkValidity("").build();
                case WORKFLOW_RELATIVE -> builder(LinkVariantInfoEnt.LinkVariantInfoEntBuilder.class)
                    .setVariant(KnimeUrls.buildLinkVariantEnt(LinkVariantEnt.VariantEnum.WORKFLOW_RELATIVE))
                    .setTitle("Create workflow-relative link")
                    .setDescription("Creates a link to the shared component relative to the location of the workflow.")
                    .setLinkValidity(
                        "The link remains valid if the relative folder structure between the workflow and the shared "
                            + "component does not change.")
                    .build();
                case SPACE_RELATIVE -> builder(LinkVariantInfoEnt.LinkVariantInfoEntBuilder.class)
                    .setVariant(KnimeUrls.buildLinkVariantEnt(LinkVariantEnt.VariantEnum.SPACE_RELATIVE))
                    .setTitle("Create space-relative link")
                    .setDescription(
                        "Creates a link to the shared component relative to the space where the shared component"
                            + " is stored.")
                    .setLinkValidity(
                        "The link remains valid as long as the workflow and the shared component stay in the same "
                            + "space.")
                    .build();
                case MOUNTPOINT_RELATIVE -> builder(LinkVariantInfoEnt.LinkVariantInfoEntBuilder.class)
                    .setVariant(KnimeUrls.buildLinkVariantEnt(LinkVariantEnt.VariantEnum.MOUNTPOINT_RELATIVE))
                    .setTitle("Create mountpoint-relative link")
                    .setDescription(
                        "Creates a link to the shared component relative to the mountpoint where the shared component"
                            + " is stored.")
                    .setLinkValidity(
                        "The link remains valid as long as the workflow and the shared component stay in the same"
                            + " mountpoint.")
                    .build();
                case MOUNTPOINT_ABSOLUTE_PATH -> builder(LinkVariantInfoEnt.LinkVariantInfoEntBuilder.class)
                    .setVariant(KnimeUrls.buildLinkVariantEnt(LinkVariantEnt.VariantEnum.MOUNTPOINT_ABSOLUTE_PATH))
                    .setTitle("Create absolute link")
                    .setDescription(
                        "Creates a link to the shared component using the full, fixed path of the shared component.")
                    .setLinkValidity("The link may break if the shared component is moved to a different location.")
                    .build();
                case MOUNTPOINT_ABSOLUTE_ID -> builder(LinkVariantInfoEnt.LinkVariantInfoEntBuilder.class)
                    .setVariant(KnimeUrls.buildLinkVariantEnt(LinkVariantEnt.VariantEnum.MOUNTPOINT_ABSOLUTE_ID))
                    .setTitle("Create ID-based absolute link")
                    .setDescription(
                        "Creates a link to the shared component using the shared component unique Hub identifier.")
                    .setLinkValidity("The link remains valid even if the component is moved or renamed on the Hub.")
                    .build();
                case NONE -> builder(LinkVariantInfoEnt.LinkVariantInfoEntBuilder.class)
                    .setVariant(KnimeUrls.buildLinkVariantEnt(LinkVariantEnt.VariantEnum.NONE))
                    .setTitle("Do not create link")
                    .setDescription("Saves the shared component but keeps a stand-alone copy in the workflow.")
                    .setLinkValidity(
                        "The component is not linked and will not receive updates if the shared component changes.")
                    .build();
            };
        }
    }

}
