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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.knime.core.node.workflow.contextv2.WorkflowContextV2;
import org.knime.core.util.exception.ResourceAccessException;
import org.knime.core.util.pathresolve.ResolverUtil;
import org.knime.core.util.urlresolve.KnimeUrlResolver;
import org.knime.core.util.urlresolve.URLResolverUtil;
import org.knime.gateway.api.util.KnimeUrls;
import org.knime.gateway.api.webui.entity.LinkTypeEnt.TypeEnum;
import org.knime.gateway.api.webui.entity.LinkVariantEnt;

public interface LinkVariants {

    // map enum to richer entities with descriptions
    BidiMap<LinkVariantEnt, TypeEnum> LINKVARIANTENTS_TO_TYPEENUM = new DualHashBidiMap<>(Map.of(

        builder(LinkVariantEnt.LinkVariantEntBuilder.class).setType(KnimeUrls.buildLinkTypeEnt(TypeEnum.NODE_RELATIVE))
            .setTitle("Node-relative").setDescription("Link to a node-relative resource").setLinkValidity("...")
            .build(),
        TypeEnum.NODE_RELATIVE,

        builder(LinkVariantEnt.LinkVariantEntBuilder.class).setType(KnimeUrls.buildLinkTypeEnt(TypeEnum.WORKFLOW_RELATIVE))
            .setTitle("Create workflow-relative link")
            .setDescription("Creates a link to the shared component relative to the location of the workflow.")
            .setLinkValidity(
                "The link remains valid if the relative folder structure between the workflow and the shared component does not change.")
            .build(),
        TypeEnum.WORKFLOW_RELATIVE,

        builder(LinkVariantEnt.LinkVariantEntBuilder.class).setType(KnimeUrls.buildLinkTypeEnt(TypeEnum.SPACE_RELATIVE))
            .setTitle("Create space-relative link")
            .setDescription(
                "Creates a link to the shared component relative to the space where the shared component is stored.")
            .setLinkValidity(
                "The link remains valid as long as the workflow and the shared component stay in the same space.")
            .build(),
        TypeEnum.SPACE_RELATIVE,

        builder(LinkVariantEnt.LinkVariantEntBuilder.class)
            .setType(KnimeUrls.buildLinkTypeEnt(TypeEnum.MOUNTPOINT_RELATIVE)).setTitle("Mountpoint-relative")
            .setDescription("Link to a mountpoint-relative resource").setLinkValidity("...").build(),
        TypeEnum.MOUNTPOINT_RELATIVE,

        builder(LinkVariantEnt.LinkVariantEntBuilder.class).setType(KnimeUrls.buildLinkTypeEnt(TypeEnum.MOUNTPOINT_ABSOLUTE))
            .setTitle("Create absolute link")
            .setDescription(
                "Creates a link to the shared component using the full, fixed path of the shared component.")
            .setLinkValidity("The link may break if the shared component is moved to a different location.").build(),
        TypeEnum.MOUNTPOINT_ABSOLUTE,

        builder(LinkVariantEnt.LinkVariantEntBuilder.class)
            .setType(KnimeUrls.buildLinkTypeEnt(TypeEnum.MOUNTPOINT_ABSOLUTE_ID_BASED)).setTitle("Create ID-based absolute link")
            .setDescription("Creates a link to the shared component using the shared component unique Hub identifier.")
            .setLinkValidity("The link remains valid even if the component is moved or renamed on the Hub.").build(),
        TypeEnum.MOUNTPOINT_ABSOLUTE_ID_BASED,

        builder(LinkVariantEnt.LinkVariantEntBuilder.class).setType(KnimeUrls.buildLinkTypeEnt(TypeEnum.NONE))
            .setTitle("Do not create link")
            .setDescription("Saves the shared component but keeps a stand-alone copy in the workflow.")
            .setLinkValidity(
                "The component is not linked and will not receive updates if the shared component changes.")
            .build(),
        TypeEnum.NONE

    ));

    /**
     * Assumes that item at {@code originalUri} actually exists
     *
     * May cause a network request
     *
     * @param originalUri
     * @param currentContext
     * @return
     */
    // TODO handle lookup misses in users
    BidiMap<TypeEnum, URI> getVariants(final URI originalUri, WorkflowContextV2 currentContext)
        throws ResourceAccessException;

    /**
     * @see LinkVariants#getVariants(URI, WorkflowContextV2)
     * @param originalUri
     * @param currentContext
     * @return
     * @throws ResourceAccessException
     */
    default List<LinkVariantEnt> getVariantEnts(final URI originalUri, final WorkflowContextV2 currentContext)
        throws ResourceAccessException {
        return getVariants(originalUri, currentContext).keySet().stream() //
            .map(typeEnum -> LINKVARIANTENTS_TO_TYPEENUM.inverseBidiMap().get(typeEnum)) //
            .toList();
    }

    class KnimeUrlResolverVariants implements LinkVariants {


        @Override
        public BidiMap<TypeEnum, URI> getVariants(final URI originalUri, final WorkflowContextV2 currentContext)
            throws ResourceAccessException {
            // todo cache?

            // TODO do we ever need the other direction?
            var inverse = KnimeUrls.KNIMEURLVARIANT_TO_TYPEENUM.inverseBidiMap();

            Map<TypeEnum, URI> result = new HashMap<>();

            try {
                var variants = KnimeUrlResolver.getResolver(currentContext).changeLinkType( //
                    URLResolverUtil.toURL(originalUri), //
                    ResolverUtil::translateHubUrl //
                ).entrySet();

                for (var entry : variants) {
                    var type = inverse.get(Optional.of(entry.getKey()));
                    result.put(type, entry.getValue().toURI());
                }

            } catch (URISyntaxException e) {
                throw new RuntimeException(e); // TODO
            }

            return new DualHashBidiMap<>(result);
        }

    }

}
