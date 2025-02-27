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

import org.knime.core.node.workflow.WorkflowMetadata;
import org.knime.gateway.api.util.EntityUtil;
import org.knime.gateway.api.webui.entity.EditableMetadataEnt.MetadataTypeEnum;
import org.knime.gateway.api.webui.entity.UpdateProjectMetadataCommandEnt;
import org.knime.gateway.api.webui.entity.WorkflowCommandEnt;
import org.knime.gateway.impl.webui.WorkflowMiddleware;

/**
 * Update metadata of a workflow project
 */
final class UpdateProjectMetadata
    extends AbstractUpdateWorkflowMetadata<WorkflowMetadata, UpdateProjectMetadataCommandEnt> {

    UpdateProjectMetadata(final UpdateProjectMetadataCommandEnt commandEnt,
        final WorkflowMiddleware workflowMiddleware) {
        super(commandEnt, workflowMiddleware);
    }

    @Override
    WorkflowMetadata toMetadata(final UpdateProjectMetadataCommandEnt commandEnt) {
        return setProjectMetadata(WorkflowMetadata.fluentBuilder(), commandEnt).build();
    }

    @Override
    UpdateProjectMetadataCommandEnt toEntity(final WorkflowMetadata metadata) {
        var links = !metadata.getLinks().isEmpty() ? toLinkEnts(metadata.getLinks()) : null;
        var tags = !metadata.getTags().isEmpty() ? metadata.getTags() : null;
        return builder(UpdateProjectMetadataCommandEnt.UpdateProjectMetadataCommandEntBuilder.class)
            .setDescription(
                EntityUtil.toTypedTextEnt(metadata.getDescription().orElse(null), metadata.getContentType())) //
            .setLinks(links) //
            .setTags(tags) //
            .setKind(WorkflowCommandEnt.KindEnum.UPDATE_PROJECT_METADATA) //
            .setMetadataType(MetadataTypeEnum.PROJECT) //
            .build();
    }

    @Override
    WorkflowMetadata getOriginal() {
        return getWorkflowManagerToModify().getMetadata();
    }

    @Override
    void apply(final WorkflowMetadata metadata) {
        getWorkflowManagerToModify().setContainerMetadata(metadata);
        if (!getWorkflowManager().equals(getWorkflowManagerToModify())) {
            setDirtyAndNotifyWorkflowListener(metadata);
        }
    }

}
