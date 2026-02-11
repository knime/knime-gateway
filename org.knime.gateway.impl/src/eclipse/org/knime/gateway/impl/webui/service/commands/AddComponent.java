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

import static org.knime.gateway.api.entity.EntityBuilderManager.builder;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CompletionException;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.api.webui.entity.AddComponentCommandEnt;
import org.knime.gateway.api.webui.entity.AddComponentPlaceholderResultEnt.AddComponentPlaceholderResultEntBuilder;
import org.knime.gateway.api.webui.entity.AutoConnectOptionsEnt;
import org.knime.gateway.api.webui.entity.CommandResultEnt;
import org.knime.gateway.api.webui.entity.CommandResultEnt.KindEnum;
import org.knime.gateway.api.webui.service.util.ServiceExceptions;
import org.knime.gateway.impl.service.util.WorkflowChangesTracker.WorkflowChange;
import org.knime.gateway.impl.webui.ComponentLoadJobManager.LoadJob;
import org.knime.gateway.impl.webui.ComponentLoadJobManager.PostLoadAction;
import org.knime.gateway.impl.webui.WorkflowMiddleware;
import org.knime.gateway.impl.webui.service.commands.util.NodeConnector;

/**
 * Command to (down-)load and add a component to a workflow from a given item-id.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
final class AddComponent extends AbstractWorkflowCommand implements WithResult {

    private final AddComponentCommandEnt m_commandEnt;

    private final WorkflowMiddleware m_workflowMiddleware;

    private LoadJob m_loadJob;

    /**
     * Attached via {@link #setAfterLoad(PostLoadAction)}.
     */
    private PostLoadAction m_postLoadAction;

    AddComponent(final AddComponentCommandEnt commandEnt, final WorkflowMiddleware workflowMiddleware) {
        super(false);
        m_commandEnt = commandEnt;
        m_workflowMiddleware = workflowMiddleware;
    }

    /**
     * Builds a command, optionally attaching post-add callbacks.
     *
     * @param commandEnt command entity with add-component options
     * @param workflowMiddleware workflow middleware for command execution
     * @return the created workflow command
     */
    static WorkflowCommand buildAddComponentCommand(final AddComponentCommandEnt commandEnt,
        final WorkflowMiddleware workflowMiddleware) {
        var moreThanOneSetOfOptions = Stream.of( //
            commandEnt.getInsertionOptions() != null, //
            commandEnt.getReplacementOptions() != null, //
            commandEnt.getAutoConnectOptions() != null //
        ).filter(Boolean::booleanValue).count() > 1;
        if (moreThanOneSetOfOptions) {
            throw new IllegalStateException(
                "Expected exactly one set of insertion, replacement, connect options but received multiple.");
        }

        var add = new AddComponent(commandEnt, workflowMiddleware);
        if (commandEnt.getInsertionOptions() != null) {
            return add.afterLoad(id -> new InsertNode(id, commandEnt.getInsertionOptions(), commandEnt.getPosition()));
        } else if (commandEnt.getReplacementOptions() != null) {
            return add.afterLoad(id -> new ReplaceNode(id, commandEnt.getReplacementOptions()));
        } else if (commandEnt.getAutoConnectOptions() != null) {
            var options = commandEnt.getAutoConnectOptions();
            if (options.getNodeRelation() == AutoConnectOptionsEnt.NodeRelationEnum.SUCCESSORS) {
                // We do not need a workflow command for connection because no state needs to be restored on undo
                //  (connections are removed automatically when the node is removed).
                return add.afterLoadCallback((wfm, id) -> new NodeConnector(wfm, id) //
                    .connectFrom(options.getTargetNodeId(), options.getTargetNodePortIdx()) //
                    .connect() //
                );
            } else {
                return add.afterLoadCallback((wfm, id) -> new NodeConnector(wfm, id) //
                    .connectTo(options.getTargetNodeId().toNodeID(wfm), options.getTargetNodePortIdx()) //
                    .connect() //
                );
            }
        }
        return add;
    }

    @Override
    public boolean executeWithWorkflowContext() {
        m_loadJob = m_workflowMiddleware //
            .getComponentLoadJobManager(getWorkflowKey()) //
            .startLoadJob(m_commandEnt, m_postLoadAction);
        return true;
    }

    @Override
    public boolean canUndo() {
        var canUndoOther = m_loadJob.runner().postLoadGetNow().map(WorkflowCommand::canUndo).orElse(true);
        var canUndoAdd = m_loadJob.runner().loadGetNow().map(id -> getWorkflowManager().canRemoveNode(id)).orElse(true);
        return canUndoOther && canUndoAdd;
    }

    @Override
    public void undo() throws ServiceExceptions.ServiceCallException {
        var other = m_loadJob.runner().postLoadGetNow().orElse(null);
        if (other != null) {
            other.undo();
        }
        if (!m_loadJob.runner().isLoadDone()) {
            m_workflowMiddleware.getComponentLoadJobManager(getWorkflowKey()) //
                .cancelAndRemoveLoadJob(m_loadJob.id());
        } else {
            m_loadJob.runner().loadGetNow().ifPresent(componentId -> getWorkflowManager().removeNode(componentId));
        }
        m_loadJob = null;
    }

    private AddComponent setAfterLoad(final PostLoadAction action) {
        if (m_postLoadAction != null) {
            throw new IllegalStateException("post load action already configured");
        }
        m_postLoadAction = action;
        return this;
    }

    /**
     * Attaches the given command to run after this command has completed. This does not block completion of this
     * command.
     *
     * @implNote This does not configure whether the given command runs under a workflow lock. This is still specified
     *           by the command implementation.
     * @param createCommand factory for the follow-up workflow command
     * @return this command, for fluent chains
     */
    AddComponent afterLoad(final Function<NodeID, WorkflowCommand> createCommand) {
        return setAfterLoad((wfm, componentId) -> {
            var cmd = createCommand.apply(componentId);
            if (cmd == null) {
                return null;
            }
            try {
                cmd.execute(getWorkflowKey());
            } catch (ServiceExceptions.ServiceCallException e) {
                throw new CompletionException(e);
            }
            return cmd;
        });
    }

    /**
     * Attaches the given callback to run after this command has completed. This does not block completion of this
     * command.
     *
     * @param consumer callback invoked with the workflow manager and component id
     * @return this command, for fluent chains
     */
    AddComponent afterLoadCallback(final BiConsumer<WorkflowManager, NodeID> consumer) {
        return setAfterLoad((wfm, componentId) -> {
            consumer.accept(wfm, componentId);
            return null;
        });
    }

    @Override
    public CommandResultEnt buildEntity(final String snapshotId) {
        return builder(AddComponentPlaceholderResultEntBuilder.class) //
            .setKind(KindEnum.ADD_COMPONENT_PLACEHOLDER_RESULT) //
            .setNewPlaceholderId(m_loadJob.id()) //
            .setSnapshotId(snapshotId).build();
    }

    @Override
    public Set<WorkflowChange> getChangesToWaitFor() {
        return Collections.singleton(WorkflowChange.COMPONENT_PLACEHOLDER_ADDED);
    }

}
