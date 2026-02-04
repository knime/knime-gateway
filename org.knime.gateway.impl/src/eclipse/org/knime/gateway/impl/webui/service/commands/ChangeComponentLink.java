/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.com; Email: contact@knime.com
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
 */
package org.knime.gateway.impl.webui.service.commands;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.workflow.MetaNodeTemplateInformation;
import org.knime.core.node.workflow.MetaNodeTemplateInformation.Role;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.util.urlresolve.URLResolverUtil;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.util.ItemVersions;
import org.knime.gateway.api.webui.entity.ChangeComponentLinkCommandEnt;
import org.knime.gateway.api.webui.entity.ItemVersionEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.ServiceCallException;

/**
 * Workflow command to change the link (source URI and last-modified timestamp) of a component.
 */
public final class ChangeComponentLink extends AbstractWorkflowCommand {

    private final NodeIDEnt m_componentId;

    private final ItemVersionEnt m_requestedItemVersion;

    private MetaNodeTemplateInformation m_previousTemplateInfo;

    /**
     * Constructs the command from an entity coming from the Gateway API.
     *
     * @param ce the command entity
     */
    public ChangeComponentLink(final ChangeComponentLinkCommandEnt ce) {
        m_componentId = ce.getNodeId();
        m_requestedItemVersion = ce.getItemVersion();
    }

    private SubNodeContainer getComponent() {
        return getWorkflowManager().getNodeContainer( //
            m_componentId.toNodeID(getWorkflowManager()), //
            SubNodeContainer.class, //
            true //
        );
    }

    @Override
    protected boolean executeWithWorkflowLockAndContext() throws ServiceCallException {
        final var previousTemplateInfo = getComponent().getTemplateInformation();
        if (previousTemplateInfo.getRole() != Role.Link) {
            throw ServiceCallException.builder() //
                .withTitle("Failed to change component link") //
                .withDetails("Component not linked: " + m_componentId + ".") //
                .canCopy(false) //
                .build();
        }
        m_previousTemplateInfo = previousTemplateInfo;
        var newUri = transformUri(previousTemplateInfo.getSourceURI(), m_requestedItemVersion);
        final var updatedTemplateInfo = createUpdatedTemplateInformation(previousTemplateInfo, newUri);
        if (previousTemplateInfo.equals(updatedTemplateInfo)) {
            return false;
        }
        getWorkflowManager().setTemplateInformation(getComponent().getID(), updatedTemplateInfo);
        return true;
    }

    private static URI transformUri(final URI previousLink, final ItemVersionEnt requestedItemVersion)
        throws ServiceCallException {
        URI newLink;
        try {
            newLink = URLResolverUtil.applyTo( //
                ItemVersions.fromEntity(requestedItemVersion), //
                previousLink //
            );
        } catch (URISyntaxException e) {
            throw ServiceCallException.builder() //
                .withTitle("Cannot construct new version URL") //
                .withDetails(e.getMessage()) //
                .canCopy(false) //
                .build(); //
        }
        return newLink;
    }

    @Override
    public void undo() throws ServiceCallException {
        if (m_previousTemplateInfo == null) {
            return;
        }
        getWorkflowManager().setTemplateInformation(getComponent().getID(), m_previousTemplateInfo);
    }

    private static MetaNodeTemplateInformation createUpdatedTemplateInformation(
        final MetaNodeTemplateInformation templateInformation, final URI newLink) throws ServiceCallException {
        try {
            return templateInformation.createLinkWithUpdatedSource( //
                newLink, //
                // this value is used as an If-Modified-Since hint when fetching the component.
                // Since we change the link target, we never want to use some older state.
                Instant.EPOCH //
            );
        } catch (final InvalidSettingsException e) {
            throw ServiceCallException.builder() //
                .withTitle("Failed to change component link") //
                .withDetails("Unable to update component link.") //
                .canCopy(false) //
                .withCause(e) //
                .build();
        }
    }

}
