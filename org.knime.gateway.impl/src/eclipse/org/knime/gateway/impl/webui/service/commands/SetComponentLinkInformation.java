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
 *   Aug 14, 2023 (leonard.woerteler): created
 */
package org.knime.gateway.impl.webui.service.commands;

import java.net.URI;
import java.util.function.UnaryOperator;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.util.CheckUtils;
import org.knime.core.node.workflow.MetaNodeTemplateInformation;
import org.knime.core.node.workflow.MetaNodeTemplateInformation.Role;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.gateway.api.webui.entity.SetComponentLinkInformationCommandEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.OperationNotAllowedException;

public final class SetComponentLinkInformation extends AbstractWorkflowCommand {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(SetComponentLinkInformation.class);

    private final UnaryOperator<NodeID> m_componentId;
    private final URI m_newURL;

    private MetaNodeTemplateInformation m_oldTemplateInfo;

    public SetComponentLinkInformation(final SetComponentLinkInformationCommandEnt ce) {
        m_componentId = wfmId -> ce.getNodeId().toNodeID(wfmId);
        final var newUrl = ce.getNewUrl();
        m_newURL = newUrl != null ? URI.create(newUrl) : null;
    }

    public SetComponentLinkInformation(final NodeID componentId, final URI targetUri) {
        m_componentId = wfmIdIgnored -> componentId;
        m_newURL = targetUri;
    }

    @Override
    protected boolean executeWithLockedWorkflow() throws OperationNotAllowedException {
        final var wfm = getWorkflowManager();
        if (wfm.isWriteProtected()) {
            throw new OperationNotAllowedException("Container is read-only.");
        }

        final var componentId = m_componentId.apply(wfm.getProjectWFM().getID());
        final var component = wfm.getNodeContainer(componentId, SubNodeContainer.class, false);
        if (component == null) {
            throw new OperationNotAllowedException("Not a component: " + m_componentId);
        }

        MetaNodeTemplateInformation newTemplateInfo = null;
        MetaNodeTemplateInformation templateInformation = component.getTemplateInformation();
        if (m_newURL == null) {
            if (templateInformation.getRole() != Role.Link) {
                return false;
            }
            newTemplateInfo = MetaNodeTemplateInformation.NONE;
        } else {
            final var role = templateInformation.getRole();
            if (role == Role.Link) {
                if (m_newURL.equals(templateInformation.getSourceURI())) {
                    return false;
                }
                try {
                    newTemplateInfo = templateInformation.createLinkWithUpdatedSource(m_newURL);
                } catch (final InvalidSettingsException e) {
                    // cannot happen since we already checked for non-null and Role.Link
                    LOGGER.coding("New component template source null or template info not Link", e);
                    return false;
                }
            } else if (role == Role.Template) {
                throw new OperationNotAllowedException("Cannot set link source on component template directly.");
            } else {
                // creating link is not yet supported by this command
                return false;
            }
        }
        m_oldTemplateInfo = wfm.setTemplateInformation(componentId, newTemplateInfo);
        return !m_oldTemplateInfo.equals(newTemplateInfo);
    }

    @Override
    public void undo() {
        final var wfm = getWorkflowManager();
        final var componentId = m_componentId.apply(wfm.getProjectWFM().getID());
        wfm.setTemplateInformation(componentId, CheckUtils.checkNotNull(m_oldTemplateInfo));

    }
}
