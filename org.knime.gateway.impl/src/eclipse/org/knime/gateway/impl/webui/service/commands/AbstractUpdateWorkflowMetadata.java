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

import org.knime.core.node.workflow.NodeContainerMetadata;
import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.api.webui.entity.EditableMetadataEnt;
import org.knime.gateway.api.webui.entity.WorkflowCommandEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions;

/**
 * Abstract logic to update metadata of a workflow (e.g. project or component)
 *
 * @param <M> The type of the metadata
 * @param <E> The type of the command
 */
public abstract class AbstractUpdateWorkflowMetadata<M extends NodeContainerMetadata, E extends WorkflowCommandEnt>
    extends AbstractWorkflowCommand {

    AbstractUpdateWorkflowMetadata(final E commandEnt) {
        m_commandEnt = commandEnt;
    }

    private final E m_commandEnt;

    /**
     * The original state before execution of the command, used as a checkpoint for undo-ing.
     */
    private M m_originalMetadata;

    /**
     * Build a new metadata object from the given values
     *
     * @param commandEnt The command parameters
     * @return A metadata object of the proper specific type
     * @apiNote This is <i>not</i> an inverse to {@link this#toEntity(NodeContainerMetadata)}
     */
    abstract M toMetadata(E commandEnt);

    /**
     * Build a command entity from the given metadata. Such an entity can be thought of as a "view" on the editable
     * properties of the metadata.
     *
     * @param metadata The source metadata
     * @return An entity containing the corresponding values of the given metadata
     * @apiNote This is <i>not</i> an inverse to {@link this#toMetadata(WorkflowCommandEnt)}
     */
    abstract E toEntity(M metadata);

    /**
     * Apply the given metadata to the node container
     *
     * @param metadata The metadata to apply
     */
    abstract void apply(M metadata);

    /**
     * @see this#m_originalMetadata
     */
    abstract M getOriginal();

    @Override
    protected boolean executeWithLockedWorkflow() throws ServiceExceptions.OperationNotAllowedException {
        m_originalMetadata = getOriginal();
        if (m_commandEnt.equals(toEntity(m_originalMetadata))) {
            return false;
        }
        apply(toMetadata(m_commandEnt));
        return true;
    }

    @Override
    public void undo() throws ServiceExceptions.OperationNotAllowedException {
        apply(m_originalMetadata);
        m_originalMetadata = null;
    }

    /**
     * Set some metadata fields of a given builder instance based on the values in a given metadata instance.
     *
     * @param metadataOptionals The builder instance to set fields on
     * @param editableMetadata The source metadata
     * @return The modified builder
     * @param <O> The value type of the builder
     */
    public <O> NodeContainerMetadata.MetadataOptionals<O> setProjectMetadata(
        final NodeContainerMetadata.NeedsContentType<O> metadataOptionals, final EditableMetadataEnt editableMetadata) {
        var metadataBuilder = metadataOptionals
            // explicitly or implicitly modifiable properties
            .withContentType(CoreUtil.ContentTypeConverter
                .toNodeContainerMetadata(editableMetadata.getDescription().getContentType()))
            .withLastModifiedNow() // update last-modified property
            .withDescription(editableMetadata.getDescription().getValue())
            // non-editable properties that are always preserved
            .withAuthor(getOriginal().getAuthor().orElse(null)).withCreated(getOriginal().getCreated().orElse(null));
        if (editableMetadata.getTags() != null) {
            editableMetadata.getTags().forEach(metadataBuilder::addTag);
        }
        if (editableMetadata.getLinks() != null) {
            editableMetadata.getLinks().forEach(link -> metadataBuilder.addLink(link.getUrl(), link.getText()));
        }
        return metadataBuilder;
    }

}
