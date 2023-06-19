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
 * History
 *   Jun 7, 2023 (kai): created
 */
package org.knime.gateway.impl.webui.service.commands;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.knime.core.node.workflow.NodeContainerMetadata.ContentType;
import org.knime.core.node.workflow.WorkflowMetadata;
import org.knime.gateway.api.util.EntityUtil;
import org.knime.gateway.api.webui.entity.LinkEnt;
import org.knime.gateway.api.webui.entity.UpdateProjectMetadataCommandEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.OperationNotAllowedException;

/**
 * Workflow command to update a workflow projects metadata.
 *
 * @author Kai Franze, KNIME GmbH
 */
final class UpdateProjectMetadata extends AbstractWorkflowCommand {

    private final UpdateProjectMetadataCommandEnt m_commandEnt;

    private WorkflowMetadata m_metadata;

    UpdateProjectMetadata(final UpdateProjectMetadataCommandEnt commandEnt) {
        m_commandEnt = commandEnt;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean executeWithLockedWorkflow() throws OperationNotAllowedException {
        if (m_commandEnt.getDescription() == null && m_commandEnt.getTags() == null
            && m_commandEnt.getLinks() == null) {
            return false; // Nothing changed
        }

        final var description = m_commandEnt.getDescription() == null ? "" : m_commandEnt.getDescription();
        final List<String> tags = m_commandEnt.getTags() == null ? Collections.emptyList() : m_commandEnt.getTags();
        final List<LinkEnt> links = m_commandEnt.getLinks() == null ? Collections.emptyList() : m_commandEnt.getLinks();
        final var wfm = getWorkflowManager();
        m_metadata = wfm.getMetadata();

        final var oldDescription = m_metadata.getDescription().orElse(null);
        final var oldTags = m_metadata.getTags();
        final var oldLinks = EntityUtil.toLinkEnts(m_metadata.getLinks());
        final var oldAndNewMetadata =
            new OldAndNewMetadata(oldDescription, description, oldTags, tags, oldLinks, links);

        if (oldAndNewMetadata.isWithoutChanges()) {
            return false; // Nothing changed
        }

        final var updated = oldAndNewMetadata.getUpdatedMetadata();
        wfm.setContainerMetadata(updated);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void undo() throws OperationNotAllowedException {
        final var wfm = getWorkflowManager();
        wfm.setContainerMetadata(m_metadata);
        m_metadata = null;
    }

    private static record OldAndNewMetadata(// NOSONAR: record
        String oldDescription, String description, // NOSONAR: record
        List<String> oldTags, List<String> tags, //
        List<LinkEnt> oldLinks, List<LinkEnt> links) {

        private boolean isWithoutChanges() {
            return Objects.equals(oldDescription, description) //
                && CollectionUtils.isEqualCollection(oldTags, tags) //
                && CollectionUtils.isEqualCollection(oldLinks, links);
        }

        private WorkflowMetadata getUpdatedMetadata() {
            final var builder = WorkflowMetadata.fluentBuilder()//
                .withLastModifiedNow()// Since we are modifying it now
                .withDescription(description, ContentType.PLAIN);
            tags.forEach(builder::addTag);
            links.forEach(link -> builder.addLink(link.getUrl(), link.getText()));
            return builder.build();
        }
    }

}
