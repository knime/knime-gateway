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
 */

package org.knime.gateway.impl.service.util.progress;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.knime.core.util.Pair;

/**
 * A collection of {@link Task}s.
 */
public class Tasks {

    /**
     * Reference to the {@link Task} that was most recently completed, either normally or exceptionally. This is to
     * cover the case where the task completes "immediately" before the frontend subscribes to its progress.
     *
     * We assume that no other task is started and immediately completed before a subscription for this task comes in.
     * In other words, a task invocation is always followed by a subscription.
     * We assume the following will never happen:
     * <ol>
     *     <li>FE: Start Task A</li>
     *     <li>Task A "immediately" completes</li>
     *     <li>FE: Start Task B</li>
     *     <li>FE: Subscribe to progress of Task B</li>
     * </ol>
     */
    private final TaskReference mostRecentlyCompletedTask = new TaskReference();

    // todo needs to be thread-safe?
    private final Map<Task.ID, Task<?>> tasksInProgress = new HashMap<>();

    public Task.ID put(final Task<?> task) {
        var id = Task.ID.random();
        // whenComplete is also invoked if the task is already done
        task.whenComplete((ignored, throwable) -> {
            // should also trigger on exceptional completion
            mostRecentlyCompletedTask.set(id, task);
            tasksInProgress.remove(id);
        });
        tasksInProgress.put(id, task);
        return id;
    }

    public Optional<Task> get(final Task.ID id) {
        // todo should this rather be a responsibility of the EventSource?
        if (mostRecentlyCompletedTask.get(id).isPresent()) {
            return mostRecentlyCompletedTask.get(id);
        }
        return Optional.ofNullable(tasksInProgress.get(id));
    }

    public void dispose() {
        tasksInProgress.values().forEach(Task::cancel);
        tasksInProgress.clear();
    }

    private static class TaskReference {
        private final AtomicReference<Pair<Task.ID, Task<?>>> ref = new AtomicReference<>();

        private void set(final Task.ID id, final Task<?> task) {
            ref.set(new Pair<>(id, task));
        }

        private Optional<Task> get(final Task.ID id) {
            var task = ref.get();
            if (task == null || !task.getFirst().equals(id)) {
                return Optional.empty();
            }
            return Optional.of(task.getSecond());
        }
    }


}
