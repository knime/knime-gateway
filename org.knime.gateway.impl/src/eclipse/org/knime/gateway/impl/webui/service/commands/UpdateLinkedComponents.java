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
 *   Nov 13, 2023 (kai): created
 */
package org.knime.gateway.impl.webui.service.commands;

import static org.knime.gateway.api.entity.EntityBuilderManager.builder;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.knime.core.node.NodeLogger;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.CommandResultEnt;
import org.knime.gateway.api.webui.entity.LinkedComponentUpdateEnt;
import org.knime.gateway.api.webui.entity.LinkedComponentUpdateEnt.UpdateStatusEnum;
import org.knime.gateway.api.webui.entity.UpdateLinkedComponentsCommandEnt;
import org.knime.gateway.api.webui.entity.UpdateLinkedComponentsResultEnt;
import org.knime.gateway.api.webui.entity.UpdateLinkedComponentsResultEnt.UpdateLinkedComponentsResultEntBuilder;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.OperationNotAllowedException;
import org.knime.gateway.api.webui.util.EntityFactory;
import org.knime.gateway.impl.service.util.DefaultServiceUtil;
import org.knime.gateway.impl.service.util.WorkflowChangesTracker.WorkflowChange;
import org.knime.gateway.impl.webui.WorkflowKey;

/**
 * Updates linked components and returns the update success.
 *
 * @author Kai Franze, KNIME GmbH, Germany
 */
class UpdateLinkedComponents extends AbstractWorkflowCommand implements WithResult {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(UpdateLinkedComponents.class);

    private static final Random RANDOM = new Random();

    final List<NodeIDEnt> m_nodeIdEnts;

    List<SubNodeContainer> m_components;

    UpdateLinkedComponents(final UpdateLinkedComponentsCommandEnt commandEnt) {
        m_nodeIdEnts = commandEnt.getNodeIds();
    }

    @Override
    protected boolean executeWithLockedWorkflow() throws OperationNotAllowedException {
        if (m_nodeIdEnts.isEmpty()) {
            throw new OperationNotAllowedException(
                "There are no linked component updates available for <%s>".formatted(getWorkflowKey()));
        }

        LOGGER.info("Updating the following node ID entities: %s".formatted(m_nodeIdEnts));
        m_components = getComponents(m_nodeIdEnts, getWorkflowKey());
        LOGGER.info("Updating the following components: %s".formatted(m_components));

        // TODO: Actual update implementation

        return true;
    }

    @Override
    public void undo() throws OperationNotAllowedException {
        // TODO: Actual undo implementation

    }

    @Override
    public UpdateLinkedComponentsResultEnt buildEntity(final String snapshotId) {
        // Wait for a random while between 0 and 10 seconds
        try {
            final var milis = RANDOM.nextLong(10000);
            Thread.sleep(milis);
        } catch (IllegalArgumentException | InterruptedException e) {
            LOGGER.error("Something went wrong with 'Thread.sleep(...)'", e);
            Thread.currentThread().interrupt();
        }

        return builder(UpdateLinkedComponentsResultEntBuilder.class)//
            .setKind(CommandResultEnt.KindEnum.UPDATELINKEDCOMPONENTSRESULT)//
            .setLinkedComponentUpdates(buildLinkedComponentUpdateEnts(m_components))//
            .setSnapshotId(snapshotId)//
            .build();
    }

    @Override
    public Set<WorkflowChange> getChangesToWaitFor() {
        // TODO: Do we need to add another 'WorkflowChange' to wait for here?
        return Collections.emptySet();
    }

    private static List<SubNodeContainer> getComponents(final List<NodeIDEnt> nodeIdEnts, final WorkflowKey wfKey) {
        return nodeIdEnts.stream()//
            .map(nodeIdEnt -> DefaultServiceUtil.getNodeContainer(wfKey.getProjectId(), nodeIdEnt))//
            .map(SubNodeContainer.class::cast)//
            .toList();
    }

    /**
     * TODO: Implement for real
     */
    private static List<LinkedComponentUpdateEnt>
        buildLinkedComponentUpdateEnts(final List<SubNodeContainer> components) {
        return components.stream()//
            .map(component -> {
                final var nodeId = component.getID();
                final var updateStatus = RANDOM.nextBoolean() ? UpdateStatusEnum.ERROR : UpdateStatusEnum.SUCCESS;
                return EntityFactory.Workflow.buildLinkedComponentUpdateEnt(nodeId, updateStatus);
            })//
            .toList();
    }

}
