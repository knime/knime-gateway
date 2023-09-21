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
 */
package org.knime.gateway.impl.webui.service.commands;

import static org.knime.gateway.api.entity.EntityBuilderManager.builder;
import static org.knime.gateway.api.util.EntityUtil.toLinkEnts;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.knime.core.node.workflow.ComponentMetadata;
import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.api.util.EntityUtil;
import org.knime.gateway.api.webui.entity.ComponentPortDescriptionEnt;
import org.knime.gateway.api.webui.entity.UpdateComponentMetadataCommandEnt;
import org.knime.gateway.api.webui.entity.WorkflowCommandEnt;
import org.knime.gateway.api.webui.util.WorkflowEntityFactory;

/**
 * Update metadata of a component workflow
 */
public final class UpdateComponentMetadata
    extends AbstractUpdateWorkflowMetadata<ComponentMetadata, UpdateComponentMetadataCommandEnt> {

    public UpdateComponentMetadata(final UpdateComponentMetadataCommandEnt commandEnt) {
        super(commandEnt);
    }

    @Override
    ComponentMetadata toMetadata(final UpdateComponentMetadataCommandEnt commandEnt) {
        var type =
            Optional.ofNullable(commandEnt.getType()).map(Enum::name).map(ComponentMetadata.ComponentNodeType::valueOf);
        var icon = Optional.ofNullable(commandEnt.getIcon());
        StringUtils.removeStart(null, null);
        var componentOptionalsBuilder = ComponentMetadata.fluentBuilder() //
            .withComponentType(type.orElse(null)) //
            .withIcon(icon.map(WorkflowEntityFactory::decodeIconDataURL).orElse(null)); //
        commandEnt.getInPorts().forEach(p -> componentOptionalsBuilder.withInPort(p.getName(), p.getDescription()));
        commandEnt.getOutPorts().forEach(p -> componentOptionalsBuilder.withOutPort(p.getName(), p.getDescription()));
        return setProjectMetadata(componentOptionalsBuilder, commandEnt) //
            .build();
    }

    @Override
    UpdateComponentMetadataCommandEnt toEntity(final ComponentMetadata metadata) {
        var inPorts = metadata.getInPorts().stream().map(UpdateComponentMetadata::toPortDescriptionEnt).toList();
        var outPorts = metadata.getOutPorts().stream().map(UpdateComponentMetadata::toPortDescriptionEnt).toList();
        var links = !metadata.getLinks().isEmpty() ? toLinkEnts(metadata.getLinks()) : null;
        var tags = !metadata.getTags().isEmpty() ? metadata.getTags() : null;
        var type =
            metadata.getNodeType().map(t -> UpdateComponentMetadataCommandEnt.TypeEnum.valueOf(t.name())).orElse(null);
        var icon = metadata.getIcon().map(WorkflowEntityFactory::createIconDataURL);
        return builder(UpdateComponentMetadataCommandEnt.UpdateComponentMetadataCommandEntBuilder.class)
            .setDescription(metadata.getDescription().isEmpty() ? null
                : EntityUtil.toTypedTextEnt(metadata.getDescription().orElse(null), metadata.getContentType()))
            .setLinks(links) //
            .setTags(tags) //
            .setIcon(icon.orElse(null)) //
            .setInPorts(inPorts) //
            .setOutPorts(outPorts) //
            .setType(type) //
            .setKind(WorkflowCommandEnt.KindEnum.UPDATE_COMPONENT_METADATA) //
            .build();
    }

    @Override
    void apply(final ComponentMetadata metadata) {
        CoreUtil.getComponentSNC(getWorkflowManager()).orElseThrow().setMetadata(metadata);
    }

    @Override
    ComponentMetadata getOriginal() {
        return CoreUtil.getComponentSNC(getWorkflowManager()).orElseThrow().getMetadata();
    }

    private static ComponentPortDescriptionEnt toPortDescriptionEnt(final ComponentMetadata.Port p) {
        return builder(ComponentPortDescriptionEnt.ComponentPortDescriptionEntBuilder.class)
            .setDescription(p.description()).setName(p.name()).build();
    }

}
