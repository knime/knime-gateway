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
 *   Jun 29, 2022 (Kai Franze, KNIME GmbH): created
 */
package org.knime.gateway.impl.webui.service.commands;

import static org.knime.gateway.api.entity.EntityBuilderManager.builder;

import java.text.NumberFormat;

import org.knime.core.node.NodeLogger;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.WorkflowAnnotationID;
import org.knime.core.node.workflow.WorkflowCopyContent;
import org.knime.gateway.api.webui.entity.CommandResultEnt;
import org.knime.gateway.api.webui.entity.CopyCommandEnt;
import org.knime.gateway.api.webui.entity.CopyResultEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.OperationNotAllowedException;
import org.knime.gateway.impl.service.util.DefaultServiceUtil;
import org.knime.gateway.impl.service.util.WorkflowChangesTracker.WorkflowChange;
import org.knime.shared.workflow.storage.text.util.ObjectMapperUtil;
import org.knime.shared.workflow.storage.util.PasswordRedactor;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Workflow command to copy selected workflow parts and return them in workflow definition format
 *
 * @author Kai Franze, KNIME GmbH
 */
public class Copy extends AbstractPartBasedWorkflowCommand implements WithResult {

    private final CopyCommandEnt m_commandEnt;

    private String m_content;

    private static final NodeLogger LOGGER = NodeLogger.getLogger(Copy.class);

    Copy(final CopyCommandEnt commandEnt) {
        super(commandEnt);
        m_commandEnt = commandEnt;
    }

    @Override
    public boolean canUndo() {
        return false;
    }

    @Override
    public boolean canRedo() {
        return false;
    }

    @Override
    public void redo() throws OperationNotAllowedException {
        // do nothing
    }

    @Override
    public void undo() throws OperationNotAllowedException {
        // do nothing

    }

    @Override
    public CopyResultEnt buildEntity(final String snapshotId) {
        return builder(CopyResultEnt.CopyResultEntBuilder.class)//
            .setKind(CommandResultEnt.KindEnum.COPYRESULT)//
            .setSnapshotId(snapshotId)//
            .setContent(m_content)//
            .build();

    }

    @Override
    public WorkflowChange getChangeToWaitFor() {
        return WorkflowChange.NONE;
    }

    @Override
    protected boolean executeWithLockedWorkflow() throws OperationNotAllowedException {
        var projectId = getWorkflowKey().getProjectId();
        var nodeIds = m_commandEnt.getNodeIds().stream()//
                .map(nodeId -> DefaultServiceUtil.entityToNodeID(projectId, nodeId))//
                .toArray(NodeID[]::new);
        var annotationIDs = m_commandEnt.getAnnotationIds().stream()//
                .map(annotationId -> DefaultServiceUtil.entityToAnnotationID(projectId, annotationId))//
                .toArray(WorkflowAnnotationID[]::new);
        // TODO: Enable copying of connections too
        var workflowCopyContent = WorkflowCopyContent.builder()//
                .setNodeIDs(nodeIds)//
                .setAnnotationIDs(annotationIDs)//
                .build();
        // TODO: Set an upper bound on the clipboard content size that can be sent to the front end?
        // * This creates a text string of about 54 million characters for the "Buildings" workflow
        // * Copy command takes about 18 seconds to finish on a local machine
        var defClipboardContent = getWorkflowManager().copyToDef(workflowCopyContent, PasswordRedactor.asNull());
        var mapper = ObjectMapperUtil.getInstance().getObjectMapper();
        try {
            var content = mapper.writeValueAsString(defClipboardContent);
            var formatter = NumberFormat.getInstance();
            formatter.setGroupingUsed(true);
            LOGGER.info("Number of characters in copy content object: " + formatter.format(content.length()));
            m_content = content;
        } catch (JsonProcessingException e) {
            LOGGER.error("Cannot copy to system clipboard: ", e);
        }
        return false;
    }

}