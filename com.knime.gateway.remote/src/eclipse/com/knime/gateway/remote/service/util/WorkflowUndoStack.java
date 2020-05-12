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
 *   May 12, 2020 (hornm): created
 */
package com.knime.gateway.remote.service.util;

import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import org.knime.core.util.Pair;

/**
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class WorkflowUndoStack {

    private static final Map<Integer, WorkflowUndoStack> WORKFLOW_UNDO_STACKS = new HashMap<>();

    private final Deque<Pair<Runnable, Runnable>> m_undoStack = new LinkedList<>();

    private final Deque<Pair<Runnable, Runnable>> m_redoStack = new LinkedList<>();

    private WorkflowUndoStack() {
        //
    }

    public static WorkflowUndoStack getUndoStack(final Object... key) {
        return WORKFLOW_UNDO_STACKS.computeIfAbsent(Arrays.hashCode(key), k -> {
            return new WorkflowUndoStack();
        });
    }

    public <T> void addAndRunOperation(final Consumer<T> operation, final Consumer<T> undoOperation, final T obj) {
        operation.accept(obj);
        if (!m_redoStack.isEmpty()) {
            m_redoStack.clear();
        }
        m_undoStack.addFirst(Pair.create(() -> operation.accept(obj), () -> undoOperation.accept(obj)));
    }

    public <T, R> R addAndRunOperation(final Function<T, R> operation, final BiConsumer<T, R> undoOperation,
        final T obj) {
        R res = operation.apply(obj);
        if (!m_redoStack.isEmpty()) {
            m_redoStack.clear();
        }
        m_undoStack.addFirst(Pair.create(() -> operation.apply(obj), () -> undoOperation.accept(obj, res)));
        return res;
    }

    public boolean canUndo() {
        return !m_undoStack.isEmpty();
    }

    public boolean undo() {
        if (!canUndo()) {
            return false;
        }
        Pair<Runnable, Runnable> pop = m_undoStack.pop();
        pop.getSecond().run();
        m_redoStack.addFirst(pop);
        return true;
    }

    public boolean canRedo() {
        return !m_redoStack.isEmpty();
    }

    public boolean redo() {
        if (!canRedo()) {
            return false;
        }
        Pair<Runnable, Runnable> pop = m_redoStack.pop();
        pop.getFirst().run();
        m_undoStack.addFirst(pop);
        return true;
    }

}
