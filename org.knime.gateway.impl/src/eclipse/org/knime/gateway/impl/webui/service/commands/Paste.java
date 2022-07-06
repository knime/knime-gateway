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

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import org.knime.core.node.NodeLogger;
import org.knime.core.node.workflow.WorkflowCopyContent;
import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.api.webui.entity.PasteCommandEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.OperationNotAllowedException;
import org.knime.shared.workflow.storage.clipboard.DefClipboardContent;
import org.knime.shared.workflow.storage.text.util.ObjectMapperUtil;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Workflow command to paste workflow parts into the active workflow
 *
 * @author Kai Franze, KNIME GmbH
 */
public class Paste extends AbstractWorkflowCommand {

    private final PasteCommandEnt m_commandEnt;

    private WorkflowCopyContent m_workflowCopyContent;

    private static final NodeLogger LOGGER = NodeLogger.getLogger(Paste.class);

    Paste(final PasteCommandEnt commandEnt) {
        m_commandEnt = commandEnt;
    }

    @Override
    public void undo() throws OperationNotAllowedException {
        var wfm = getWorkflowManager();
        Arrays.stream(m_workflowCopyContent.getNodeIDs()).forEach(wfm::removeNode);
        Arrays.stream(m_workflowCopyContent.getAnnotationIDs()).forEach(wfm::removeAnnotation);
    }

    @Override
    protected boolean executeWithLockedWorkflow() throws OperationNotAllowedException {
        var wfm = getWorkflowManager();
        // paste at original position
        var mapper = ObjectMapperUtil.getInstance().getObjectMapper();
        try {
            // TODO: Add more verification before actually mapping this?
            var defClipboardContent = mapper.readValue(m_commandEnt.getContent(), DefClipboardContent.class);
            m_workflowCopyContent = getWorkflowManager().paste(defClipboardContent);
            LOGGER.info("Position offset of copy content: <" + m_workflowCopyContent.getPositionOffset() + ">");
        } catch (JsonProcessingException e) { // NOSONAR: Don't want to log or re-throw this exception
            throw new OperationNotAllowedException("Could not parse input string to def clipboard content");
        }
        // derive offset
        int[] delta = new int[2]; // NOSONAR: Can't use `var` here
        if (m_commandEnt.getOffset() == null && m_commandEnt.getPosition() == null) {
            delta[0] = 64;
            delta[1] = 128;
        } else if (m_commandEnt.getOffset() != null && m_commandEnt.getPosition() != null) {
            throw new OperationNotAllowedException("Cannot paste workflow parts using an offset and a position");
        } else if (m_commandEnt.getOffset() != null) {
            delta[0] = m_commandEnt.getOffset().getX();
            delta[1] = m_commandEnt.getOffset().getY();
        } else {
            // TODO: Implement this
            throw new OperationNotAllowedException("Position is not supported yet");
        }
        // offset nodes and annotations
        var nodes = Arrays.stream(m_workflowCopyContent.getNodeIDs())//
            .map(id -> CoreUtil.getNodeContainer(id, wfm).orElseThrow())//
            .collect(Collectors.toSet());
        var annotations = Arrays.stream(m_workflowCopyContent.getAnnotationIDs())//
            .map(id -> CoreUtil.getAnnotation(id, wfm).orElse(null))//
            .filter(Objects::nonNull)//
            .collect(Collectors.toSet());
        // TODO: Enable pasting of connections too, since bend points of connections need to be shifted as well
        Translate.performTranslation(wfm, nodes, annotations, delta);
        // * This receives a text string of about 54 million characters for the "Buildings" workflow
        // * Paste command takes about 18 seconds to finish on a local machine
        return true;
    }

}
