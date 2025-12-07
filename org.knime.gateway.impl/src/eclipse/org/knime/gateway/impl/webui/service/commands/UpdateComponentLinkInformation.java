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
import java.util.function.Function;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.util.CheckUtils;
import org.knime.core.node.workflow.MetaNodeTemplateInformation;
import org.knime.core.node.workflow.MetaNodeTemplateInformation.Role;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.util.exception.ResourceAccessException;
import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.api.webui.entity.LinkVariantEnt;
import org.knime.gateway.api.webui.entity.UpdateComponentLinkInformationCommandEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.ServiceCallException;
import org.knime.gateway.impl.webui.spaces.LinkVariants;

/**
 * Workflow command to update link types of link components. The command is accessed from outside the package.
 *
 * @author Leonard Wörteler, KNIME GmbH, Konstanz, Germany
 * @author Kai Franze, KNIME GmbH, Germany
 */
final class UpdateComponentLinkInformation extends AbstractWorkflowCommand {

    private final Function<WorkflowManager, NodeID> m_componentId;

    private final LinkVariantEnt m_requestedVariant;

    private LinkVariants m_linkVariants;

    private MetaNodeTemplateInformation m_oldTemplateInfo;

    /*
     * For testing the command.
     */
    UpdateComponentLinkInformation(final UpdateComponentLinkInformationCommandEnt ce, final LinkVariants linkVariants) {
        m_componentId = wfm -> ce.getNodeId().toNodeID(wfm);
        m_requestedVariant = ce.getLinkVariant();
        m_linkVariants = CheckUtils.checkNotNull(linkVariants);
    }

    /**
     * Creates an instance of the {@link UpdateComponentLinkInformation} workflow command. This is called from outside
     * the package.
     *
     * @param componentId
     * @param linkVariant
     */
    public UpdateComponentLinkInformation(final NodeID componentId, final LinkVariantEnt linkVariant) {
        m_componentId = wfmIdIgnored -> componentId;
        m_requestedVariant = CheckUtils.checkNotNull(linkVariant);
    }

    @Override
    protected boolean executeWithWorkflowLockAndContext() throws ServiceCallException {
        final var wfm = getWorkflowManager();
        if (wfm.isWriteProtected()) {
            throw ServiceCallException.builder() //
                .withTitle("Failed to update component link") //
                .withDetails("Container is read-only.") //
                .canCopy(false) //
                .build();
        }

        final var componentId = m_componentId.apply(wfm);
        final var component = wfm.getNodeContainer(componentId, SubNodeContainer.class, false);
        if (component == null) {
            throw ServiceCallException.builder() //
                .withTitle("Failed to update component link") //
                .withDetails("Not a component: " + m_componentId + ".") //
                .canCopy(false) //
                .build();
        }

        final var templateInformation = component.getTemplateInformation();
        if (templateInformation.getRole() == Role.Template) {
            throw ServiceCallException.builder() //
                .withTitle("Failed to update component link") //
                .withDetails("Cannot set link source on component template directly: " + m_componentId + ".") //
                .canCopy(false) //
                .build();
        }
        if (templateInformation.getRole() != Role.Link) {
            throw ServiceCallException.builder() //
                .withTitle("Failed to update component link") //
                .withDetails("Component not linked: " + m_componentId + ".") //
                .canCopy(false) //
                .build();
        }

        try {
            final MetaNodeTemplateInformation newTemplateInfo;

            if (m_requestedVariant.getVariant() == LinkVariantEnt.VariantEnum.NONE) {
                newTemplateInfo = MetaNodeTemplateInformation.NONE;
            } else {
                final var newUri = m_linkVariants.getVariants(
                        templateInformation.getSourceURI(),
                        CoreUtil.getProjectWorkflow(component).getContextV2()
                ).get(m_requestedVariant.getVariant());
                if (newUri == null) {
                    throw new ResourceAccessException("Requested link variant not available");
                }
                newTemplateInfo = updateTemplateInformation(templateInformation, newUri);
            }

            m_oldTemplateInfo = wfm.setTemplateInformation(componentId, newTemplateInfo);
            return !m_oldTemplateInfo.equals(newTemplateInfo);
        } catch (ResourceAccessException e) {
            throw ServiceCallException.builder().withTitle("Failed to update component link")
                .withDetails("Unable to resolve link variant.").canCopy(false).withCause(e).build();
        }
    }

    @Override
    public void undo() throws ServiceCallException {
        final var wfm = getWorkflowManager();
        final var componentId = m_componentId.apply(wfm.getProjectWFM());
        wfm.setTemplateInformation(componentId, CheckUtils.checkNotNull(m_oldTemplateInfo));

    }

    /**
     *
     * @param templateInformation
     * @param newURI the new target URI.
     * @return
     */
    private static MetaNodeTemplateInformation
        updateTemplateInformation(final MetaNodeTemplateInformation templateInformation, final URI newURI) {
        if (newURI.equals(templateInformation.getSourceURI())) {
            return templateInformation; // Nothing changed
        }
        try {
            return templateInformation.createLinkWithUpdatedSource(newURI);
        } catch (final InvalidSettingsException e) {
            // Cannot happen since we already checked for non-null and Role.Link
            NodeLogger.getLogger(UpdateComponentLinkInformation.class)
                .coding("New component template source null or template info not Link", e);
            return templateInformation; // Nothing changed
        }
    }
}
