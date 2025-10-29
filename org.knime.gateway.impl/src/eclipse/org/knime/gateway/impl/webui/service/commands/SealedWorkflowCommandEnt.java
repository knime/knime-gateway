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
 *   Oct 29, 2025 (motacilla): created
 */
package org.knime.gateway.impl.webui.service.commands;

import org.knime.gateway.api.webui.entity.AddWorkflowAnnotationCommandEnt;
import org.knime.gateway.api.webui.entity.WorkflowCommandEnt;

/**
 * A sealed interface for all permitted {@link WorkflowCommandEnt}.
 *
 * TODO: Can we automatically generate this class?
 *
 * @author Kai Franzel, KNIME GmbH, Germany
 */
sealed interface SealedWorkflowCommandEnt {

    /**
     * TODO: Is there a way to skip this?
     */
    static SealedWorkflowCommandEnt of(final WorkflowCommandEnt commandEnt) {
        return switch (commandEnt.getKind()) {
//            case TRANSLATE -> new PermittedTranslateCommandEnt(commandEnt);
//            case DELETE -> new PermittedDeleteCommandEnt(commandEnt);
//            case CONNECT -> new PermittedConnectCommandEnt(commandEnt);
//            case AUTO_CONNECT -> new PermittedAutoConnectCommandEnt(commandEnt);
//            case AUTO_DISCONNECT -> new PermittedAutoDisconnectCommandEnt(commandEnt);
//            case ADD_NODE -> new PermittedAddNodeCommandEnt(commandEnt);
//            case ADD_COMPONENT -> new PermittedAddComponentCommandEnt(commandEnt);
//            case DELETE_COMPONENT_PLACEHOLDER -> new PermittedDeleteComponentPlaceholderCommandEnt(commandEnt);
//            case REPLACE_NODE -> new PermittedReplaceNodeCommandEnt(commandEnt);
//            case INSERT_NODE -> new PermittedInsertNodeCommandEnt(commandEnt);
//            case UPDATE_COMPONENT_OR_METANODE_NAME -> new PermittedUpdateComponentOrMetanodeNameCommandEnt(commandEnt);
//            case UPDATE_NODE_LABEL -> new PermittedUpdateNodeLabelCommandEnt(commandEnt);
//            case COLLAPSE -> new PermittedCollapseCommandEnt(commandEnt);
//            case EXPAND -> new PermittedExpandCommandEnt(commandEnt);
//            case ADD_PORT -> new PermittedAddPortCommandEnt(commandEnt);
//            case REMOVE_PORT -> new PermittedRemovePortCommandEnt(commandEnt);
//            case COPY -> new PermittedCopyCommandEnt(commandEnt);
//            case CUT -> new PermittedCutCommandEnt(commandEnt);
//            case PASTE -> new PermittedPasteCommandEnt(commandEnt);
//            case TRANSFORM_WORKFLOW_ANNOTATION -> new PermittedTransformWorkflowAnnotationCommandEnt(commandEnt);
//            case UPDATE_WORKFLOW_ANNOTATION -> new PermittedUpdateWorkflowAnnotationCommandEnt(commandEnt);
//            case REORDER_WORKFLOW_ANNOTATIONS -> new PermittedReorderWorkflowAnnotationsCommandEnt(commandEnt);
            case ADD_WORKFLOW_ANNOTATION -> new PermittedAddWorkflowAnnotationCommandEnt(commandEnt);
//            case UPDATE_PROJECT_METADATA -> new PermittedUpdateProjectMetadataCommandEnt(commandEnt);
//            case UPDATE_COMPONENT_METADATA -> new PermittedUpdateComponentMetadataCommandEnt(commandEnt);
//            case ADD_BENDPOINT -> new PermittedAddBendpointCommandEnt(commandEnt);
//            case UPDATE_COMPONENT_LINK_INFORMATION -> new PermittedUpdateComponentLinkInformationCommandEnt(commandEnt);
//            case TRANSFORM_METANODE_PORTS_BAR -> new PermittedTransformMetanodePortsBarCommandEnt(commandEnt);
//            case UPDATE_LINKED_COMPONENTS -> new PermittedUpdateLinkedComponentsCommandEnt(commandEnt);
//            case ALIGN_NODES -> new PermittedAlignNodesCommandEnt(commandEnt);
//            case null -> null; // TODO: Do we need this?
            default -> null;
        };
    }

    record PermittedAddWorkflowAnnotationCommandEnt(WorkflowCommandEnt delegate) implements SealedWorkflowCommandEnt {
        AddWorkflowAnnotationCommandEnt get() {
            return (AddWorkflowAnnotationCommandEnt)delegate;
        }
    }
}
