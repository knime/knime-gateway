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
 *   Jan 20, 2021 (hornm): created
 */
package org.knime.gateway.impl.webui.service.commands;

import java.util.Optional;

import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.api.webui.entity.CommandResultEnt;
import org.knime.gateway.api.webui.entity.WorkflowCommandEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.NodeNotFoundException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.NotASubWorkflowException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.OperationNotAllowedException;
import org.knime.gateway.impl.service.util.WorkflowChangesTracker;
import org.knime.gateway.impl.webui.WorkflowKey;

/**
 * Unifying interface for all workflow commands.
 *
 * Methods are guaranteed to be called in a fixed order:
 * <ol>
 *     <li>
 *         The command is instantiated via its zero-argument constructor.
 *     </li>
 *     <li>
 *         The command is configured via {@link WorkflowCommand#configure(WorkflowKey, WorkflowManager)}. This includes
 *         e.g. providing parameters needed to execute the command.
 *     </li>
 *     <li>
 *         {@link WorkflowCommand#getResultBuilder()} is called.
 *     </li>
 *     <li>
 *         {@link WorkflowCommand#executeWithWorkflowLock()} is called to execute the command.
 *     </li>
 *     <li>
 *         Any number of repetitions of sequence of {@link WorkflowCommand#undo()} and {@link WorkflowCommand#redo()} is called.
 *     </li>
 * </ol>
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @author Benjamin Moser, KNIME GmbH, Konstanz, Germany
 */
public interface WorkflowCommand {

    void configure(final WorkflowKey wfKey, final WorkflowManager wfm);

    /**
     * Executes the workflow Command as represented by the command entity. Always called before {@link #undo()} and
     * {@link #redo()}.
     *
     * @param wfKey references the workflow to execute the command for
     * @param commandEntity representation of the command to be applied
     * @return <code>true</code> if the command changed the workflow, <code>false</code> if the successful execution of
     *         the command didn't do any change to the workflow
     * @throws NodeNotFoundException
     * @throws NotASubWorkflowException
     * @throws OperationNotAllowedException
     */

    boolean executeWithWorkflowLock() throws NodeNotFoundException, NotASubWorkflowException, OperationNotAllowedException;

    /**
     * Whether the command can be undone. Must be a rather light operation because it's potentially called repeatedly
     * (on every/many workflow changes).
     *
     * @return {@code false} if the undo operation can't be carried out
     */
    boolean canUndo();

    /**
     * Undoes this command. Guaranteed to be called only if {@link #execute(WorkflowKey, WorkflowCommandEnt)} has
     * been called before already.
     *
     * @throws OperationNotAllowedException
     */
    void undo() throws OperationNotAllowedException;

    /**
     * Whether the command can be redone. Must be a rather light operation because it's potentially called repeatedly
     * (on every/many workflow changes).
     *
     * @return {@code false} if the redo operation can't be carried out
     */
    boolean canRedo();

    /**
     * Re-does this command. Guaranteed to be called only if {@link #undo()} has been called before already.
     *
     * @throws OperationNotAllowedException
     */
    void redo() throws OperationNotAllowedException;

    /**
     * @return An instance of {@link CommandResultBuilder} specific to the executed command, or an empty optional
     * if the command does not provide a result.
     */
    default Optional<CommandResultBuilder> getResultBuilder() {
        return Optional.empty();
    }

    /**
     * Result of a workflow command.
     */
    interface CommandResultBuilder {

        /**
         *
         * @param snapshotId A workflow snapshot id for which holds that any snapshot with equal-or-greater id
         *                   reflects the changes performed by this command.
         * @return The result entity
         */
        CommandResultEnt buildEntity(String snapshotId);

        WorkflowChangesTracker.WorkflowChange getChangeToWaitFor();

    }

}
