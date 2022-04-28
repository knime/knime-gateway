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

import org.knime.gateway.api.webui.service.util.ServiceExceptions.NodeNotFoundException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.NotASubWorkflowException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.OperationNotAllowedException;
import org.knime.gateway.impl.webui.WorkflowKey;

/**
 * Unifying interface for all workflow commands.
 *
 * Methods are guaranteed to be called synchronously (i.e. never at the same time) and in a fixed order:
 * <ol>
 * <li>The command is instantiated.</li>
 * <li>{@link #execute(WorkflowKey)} is called to execute the command.</li>
 * <li>Any number of repetitions of sequence of {@link #undo()} and {@link #redo()} is called (same for
 * {@link #canUndo()} and {@link #canRedo()}).</li>
 * </ol>
 *
 * If a command is supposed to return a result it additionally needs to implement {@link WithResult}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @author Benjamin Moser, KNIME GmbH, Konstanz, Germany
 */
public interface WorkflowCommand {

    /**
     * Executes the workflow command as represented. Always called before {@link #undo()} and {@link #redo()}.
     *
     * @param wfKey references the workflow to execute the command for
     * @return <code>true</code> if the command changed the workflow, <code>false</code> if the successful execution of
     *         the command didn't do any change to the workflow
     * @throws NodeNotFoundException
     * @throws NotASubWorkflowException
     * @throws OperationNotAllowedException
     */
    boolean execute(WorkflowKey wfKey)
        throws NodeNotFoundException, NotASubWorkflowException, OperationNotAllowedException;

    /**
     * Whether the command can be undone. Must be a rather light operation because it's potentially called repeatedly
     * (on every/many workflow changes).
     *
     * @return {@code false} if the undo operation can't be carried out
     */
    boolean canUndo();

    /**
     * Undoes this command. Guaranteed to be called only if {@link #execute(WorkflowKey)} has been called before
     * already.
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

}
