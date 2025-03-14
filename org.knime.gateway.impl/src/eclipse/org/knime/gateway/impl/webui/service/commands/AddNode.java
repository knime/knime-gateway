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
 *   May 11, 2021 (hornm): created
 */
package org.knime.gateway.impl.webui.service.commands;

import static org.knime.gateway.api.entity.EntityBuilderManager.builder;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.Set;

import org.knime.core.node.NodeLogger;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.util.exception.ResourceAccessException;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.AddNodeCommandEnt;
import org.knime.gateway.api.webui.entity.AddNodeCommandEnt.NodeRelationEnum;
import org.knime.gateway.api.webui.entity.AddNodeResultEnt;
import org.knime.gateway.api.webui.entity.AddNodeResultEnt.AddNodeResultEntBuilder;
import org.knime.gateway.api.webui.entity.CommandResultEnt.KindEnum;
import org.knime.gateway.api.webui.entity.NodeFactoryKeyEnt;
import org.knime.gateway.api.webui.entity.NodeFactoryKeyEnt.NodeFactoryKeyEntBuilder;
import org.knime.gateway.api.webui.entity.SpaceItemReferenceEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.ServiceCallException;
import org.knime.gateway.api.webui.util.WorkflowEntityFactory;
import org.knime.gateway.impl.service.util.WorkflowChangesTracker.WorkflowChange;
import org.knime.gateway.impl.webui.NodeFactoryProvider;
import org.knime.gateway.impl.webui.service.commands.util.NodeConnector;
import org.knime.gateway.impl.webui.service.commands.util.NodeCreator;
import org.knime.gateway.impl.webui.spaces.SpaceProviders;

/**
 * Workflow command to add a native node.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @author Kai Franze, KNIME GmbH
 */
final class AddNode extends AbstractWorkflowCommand implements WithResult {

    private NodeID m_addedNode;

    private final AddNodeCommandEnt m_commandEnt;

    private final NodeFactoryProvider m_nodeFactoryProvider;

    private final SpaceProviders m_spaceProviders;

    AddNode(final AddNodeCommandEnt commandEnt) {
        this(commandEnt, null, null);
    }

    AddNode(final AddNodeCommandEnt commandEnt, final NodeFactoryProvider nodeFactoryProvider,
        final SpaceProviders spaceProviders) {
        m_commandEnt = commandEnt;
        m_nodeFactoryProvider = nodeFactoryProvider;
        m_spaceProviders = spaceProviders;
    }

    @Override
    protected boolean executeWithWorkflowLockAndContext() throws ServiceCallException {
        var wfm = getWorkflowManager();
        // Add node
        var positionEnt = m_commandEnt.getPosition();
        var factoryKeyEnt = m_commandEnt.getNodeFactory();
        var url = parseURL(m_commandEnt.getUrl());
        if (url == null && m_commandEnt.getSpaceItemReference() != null && m_spaceProviders != null) {
            url = getUrlFromSpaceItemReference(m_commandEnt.getSpaceItemReference());
        }

        if (factoryKeyEnt == null && url != null && m_nodeFactoryProvider != null) {
            factoryKeyEnt = getNodeFactoryKeyFromUrl(url.getPath());
        }

        if (factoryKeyEnt == null) {
            throw new ServiceCallException("No node factory class given");
        }

        m_addedNode = new NodeCreator(wfm, factoryKeyEnt, positionEnt) //
            .withUrl(url) //
            .trackCreation() //
            .isNodeAddedViaQuickNodeInsertion(isNodeAddedViaQuickNodeInsertion(m_commandEnt)) //
            .withNodeYPosCorrection(-WorkflowEntityFactory.NODE_Y_POS_CORRECTION) //
            .connect(connector -> connectNode(m_commandEnt, connector)) //
            .failOnConnectionAttempt() //
            .create();
        return true; // Workflow changed if no exceptions were thrown
    }

    private static boolean isNodeAddedViaQuickNodeInsertion(final AddNodeCommandEnt commandEnt) {
        // at the moment nodes are added _and_ connected to a node only via the quick node insertion feature
        return commandEnt.getSourceNodeId() != null && commandEnt.getSourcePortIdx() != null
            && commandEnt.getNodeRelation() != null;
    }

    private static void connectNode(final AddNodeCommandEnt commandEnt, final NodeConnector connector) {
        if (commandEnt.getNodeRelation() == null) {
            return;
        }
        if(commandEnt.getNodeRelation() == NodeRelationEnum.SUCCESSORS) {
            connector.connectFrom(commandEnt.getSourceNodeId(), commandEnt.getSourcePortIdx()).trackCreation();
        } else {
            connector.connectTo(commandEnt.getSourceNodeId(), commandEnt.getSourcePortIdx()).trackCreation();
        }
    }

    private NodeFactoryKeyEnt getNodeFactoryKeyFromUrl(final String url) {
        var factory = m_nodeFactoryProvider.fromFileExtension(url);
        if (factory == null) {
            return null;
        }
        return builder(NodeFactoryKeyEntBuilder.class).setClassName(factory.getName()).build();
    }

    private static URL parseURL(final String urlString) {
        if (urlString == null) {
            return null;
        }
        try {
            return new URI(urlString).toURL();
        } catch (MalformedURLException | URISyntaxException ex) {
            NodeLogger.getLogger(AddNode.class).warn("Failed to parse url: " + urlString, ex);
            return null;
        }
    }

    private URL getUrlFromSpaceItemReference(final SpaceItemReferenceEnt spaceItemId) {
        final var spaceProviderId = spaceItemId.getProviderId();
        final var spaceId = spaceItemId.getSpaceId();
        final var itemId = spaceItemId.getItemId();
        try {
            var space = m_spaceProviders.getSpace(spaceProviderId, spaceId);
            return space.toPathBasedKnimeUrl(itemId).toURL();
        } catch (MalformedURLException | ResourceAccessException ex) {
            NodeLogger.getLogger(AddNode.class).warn("Failed to resolve item ID " + itemId
                + " to URL in " + spaceProviderId, ex);
            return null;
        }
    }

    @Override
    public boolean canUndo() {
        return getWorkflowManager().canRemoveNode(m_addedNode);
    }

    @Override
    public void undo() throws ServiceCallException {
        getWorkflowManager().removeNode(m_addedNode);
        m_addedNode = null;
    }

    @Override
    public AddNodeResultEnt buildEntity(final String snapshotId) {
        return builder(AddNodeResultEntBuilder.class)//
            .setKind(KindEnum.ADD_NODE_RESULT)//
            .setNewNodeId(new NodeIDEnt(m_addedNode))//
            .setSnapshotId(snapshotId)//
            .build();
    }

    @Override
    public Set<WorkflowChange> getChangesToWaitFor() {
        return Collections.singleton(WorkflowChange.NODE_ADDED);
    }

}
