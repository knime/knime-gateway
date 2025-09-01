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
 *   Nov 30, 2022 (kai): created
 */
package org.knime.gateway.impl.webui.service.commands;

import org.knime.core.node.workflow.AnnotationData.StyleRange;
import org.knime.core.node.workflow.NodeAnnotation;
import org.knime.core.node.workflow.NodeAnnotationData;
import org.knime.gateway.api.webui.entity.UpdateNodeLabelCommandEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.ServiceCallException;
import org.knime.gateway.impl.service.util.DefaultServiceUtil;

/**
 * Workflow command to update the label of a native node, component or metanode.
 *
 * @author Kai Franze, KNIME GmbH
 */
class UpdateNodeLabel extends AbstractWorkflowCommand {

    /* The default background color for node annotations, equal to #FFFFFF */
    private static final int DEFAULT_BG_COLOR = 16777215;

    /* The default border color for node annotations, equal to #000000 */
    private static final int DEFAULT_BORDER_COLOR = 0;

    private final UpdateNodeLabelCommandEnt m_commandEnt;

    private NodeAnnotation m_nodeAnnotation;

    private NodeAnnotationData m_oldNodeAnnotationData;

    UpdateNodeLabel(final UpdateNodeLabelCommandEnt commandEnt) {
        m_commandEnt = commandEnt;
    }

    @Override
    public void undo() throws ServiceCallException {
        m_nodeAnnotation.copyFrom(m_oldNodeAnnotationData, false);
        m_nodeAnnotation = null;
        m_oldNodeAnnotationData = null;
    }

    @Override
    protected boolean executeWithWorkflowLockAndContext() throws ServiceCallException {
        var nodeId = m_commandEnt.getNodeId();
        var nc = DefaultServiceUtil.getNodeContainer(getWorkflowKey().getProjectId(), nodeId);
        var newLabel = m_commandEnt.getLabel();

        // Keep node annotation and node annotation data to undo
        m_nodeAnnotation = nc.getNodeAnnotation();
        m_oldNodeAnnotationData = m_nodeAnnotation.getData().clone();
        var annotationData = m_nodeAnnotation.getData();
        var oldLabel = annotationData.getText();

        // Only update the node annotation if the labels are different
        if (!newLabel.equals(oldLabel)) {
            // Reset node annotation styling
            annotationData.setStyleRanges(new StyleRange[0]);
            annotationData.setBgColor(DEFAULT_BG_COLOR);
            annotationData.setBorderColor(DEFAULT_BORDER_COLOR);
            annotationData.setBorderSize(0);
            // Set node annotation text to new label
            annotationData.setText(newLabel);
            m_nodeAnnotation.copyFrom(annotationData, false);
            return true;
        }
        return false;
    }

}
