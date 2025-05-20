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

package org.knime.gateway.impl.webui.service.events;

import static org.knime.gateway.api.entity.EntityBuilderManager.builder;

import java.math.BigDecimal;
import java.util.Optional;

import org.apache.commons.lang3.function.FailableConsumer;
import org.knime.gateway.api.webui.entity.TaskStatusEventEnt;
import org.knime.gateway.api.webui.entity.TaskStatusEventTypeEnt;
import org.knime.gateway.impl.service.util.progress.Task;
import org.knime.gateway.impl.service.util.progress.TaskManager;

/**
 * Subscribe to updates for a previously invoked {@link Task}.
 *
 * A {@link Task} itself is independent on if/how/where it is tracked. A task can be defined and started using the
 * methods of {@link Task}, e.g. {@link Task#runAsync(FailableConsumer)} etc.
 * 
 * If the progress of a Task should be tracked, a reference to it has to be kept somewhere. For instance, Tasks specific
 * to a {@link org.knime.gateway.impl.project.Project} can be kept as a property thereof. In this case, the Task has to
 * be explicitly put into the corresponding {@link org.knime.gateway.impl.service.util.progress.Tasks} instance.
 * 
 * The {@link TaskManager} provides a way to find a Task given only its ID. This means the frontend really only has to
 * keep the Task ID. In the backend, the Task Manager is used e.g. by this Event Source and
 * {@link org.knime.gateway.api.webui.service.TaskService} implementations.
 *
 */
public class TaskEventSource extends EventSource<TaskStatusEventTypeEnt, TaskStatusEventEnt> {

    private final TaskManager taskManager;

    public TaskEventSource(final EventConsumer consumer, final TaskManager taskManager) {
        super(consumer);
        this.taskManager = taskManager;
    }

    @SuppressWarnings("unchecked")
    // todo why is the cast needed?
    public void subscribe(final Task.ID id, final Task task, String projectId) {
        task.onProgress().add(progress -> {
            System.out.println("onProgress: " + progress.toString());
            sendEvent(onProgress(id, (Double)progress), projectId);
        });
        task.onMessage().add(message -> {
            sendEvent(onMessage(id, (String)message), projectId);
        });
        task.whenComplete((ignored, throwable) -> {
            sendEvent(onComplete(id, (Throwable)throwable), projectId);
        });
    }

    @Override
    public Optional<TaskStatusEventEnt> addEventListenerAndGetInitialEventFor(final TaskStatusEventTypeEnt eventTypeEnt,
        final String projectId) {
        // TODO should frontend give project id here?
        // if its a project-specific event: needed for browser setting
        // but in desktop setting, we probably still want to receive updates even if we've switched away from the project tab
        var id = Task.ID.fromString(eventTypeEnt.getId());
        var task = taskManager.get(id);
        if (task == null) {
            return Optional.empty(); // TODO I suppose here we can assume that the task has completed successfully
        }
        if (task.isDone()) {
            // this would in fact already be covered by the branch below because the lambda given to Task#whenComplete
            // is executed immediately if the task/future is already done -- but that update would come as a separate event.
            Throwable encounteredThrowable = null;
            try {
                task.getNow(); // re-throws exception if future completed exceptionally
            } catch (Throwable t) {
                encounteredThrowable = t;
            }
            return Optional.of(onComplete(id, encounteredThrowable));
        } else {
            subscribe(id, task, projectId);
            return Optional.of(onProgress(id, 0.0));
        }
    }

    @Override
    public void removeEventListener(final TaskStatusEventTypeEnt eventTypeEnt, final String projectId) {
        // TODO
    }

    @Override
    public void removeAllEventListeners() {
        // TODO
    }

    @Override
    protected String getName() {
        return "TaskStatusEvent";
    }

    private static TaskStatusEventEnt onProgress(final Task.ID id, final Double progress) {
        return builder(TaskStatusEventEnt.TaskStatusEventEntBuilder.class) //
            .setId(id.toString()) //
            .setProgress(BigDecimal.valueOf(progress)) //
            .build();

    }

    private static TaskStatusEventEnt onMessage(final Task.ID id, final String message) {
        return builder(TaskStatusEventEnt.TaskStatusEventEntBuilder.class) //
            .setId(id.toString()) //
            .setMessage(message).build();
    }

    private static TaskStatusEventEnt onComplete(final Task.ID id, final Throwable throwable) {
        var builder = builder(TaskStatusEventEnt.TaskStatusEventEntBuilder.class) //
            .setId(id.toString());
        if (throwable != null) {
            builder.setError(throwable.getMessage());
        }
        return builder.build();
    }

}
