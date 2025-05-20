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

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.BiConsumer;

import org.apache.commons.lang3.function.FailableBiConsumer;
import org.apache.commons.lang3.function.FailableBiFunction;
import org.apache.commons.lang3.function.FailableConsumer;
import org.apache.commons.lang3.function.FailableFunction;
import org.knime.gateway.impl.service.util.Listeners;

/**
 * Contains
 * <ul>
 *     <li>A {@link CompletableFuture}</li> that represents the operation(s) to be carried out. This can be a composition of individual futures.
 *     <li>A {@link ProgressReporting.SequenceMonitor}</li> that represents the overall progress. This is a composition of one or several {@link ProgressReporting.ProgressMonitor}.
 * </ul>
 * @param <R>
 */
public class Task<R> {

    private final ProgressReporting.SequenceMonitor monitor;

    private final CompletableFuture<R> future;

    // todo I think these need to be thread-safe
    public Listeners<String> onMessage() {
        return monitor.onMessage;
    }

    // todo I think these need to be thread-safe
    public Listeners<Double> onProgress() {
        return monitor.onProgress;
    }

    private Task(final CompletableFuture<R> future, ProgressReporting.SequenceMonitor sequenceMonitor) {
        this.monitor = sequenceMonitor;
        this.future = future;
    }

    private Task(final CompletableFuture<R> future, ProgressReporting.ProgressMonitor progressMonitor) {
        this.monitor = new ProgressReporting.SequenceMonitor(progressMonitor);
        this.future = future;
    }


    public record ID(String id) {
        public static ID random() {
            return new ID(UUID.randomUUID().toString());
        }
        public static ID fromString(String id) {
            return new ID(id);
        }

        @Override
        public String toString() {
            return id;
        }
    }

    public static <I, O> Task<O> apply(final I initialValue, final Function<I, O> function) {
        var initialMonitor = new ProgressReporting.ProgressMonitor();
        var initialFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return function.apply(initialValue, initialMonitor);
            } catch (Throwable t) {
                throw new CompletionException(t);
            }
        });
        return new Task<>(initialFuture, initialMonitor);
    }

    public static <O> Task<O> supply(final Supplier<O> supplier) {
        var initialMonitor = new ProgressReporting.ProgressMonitor();
        var composedFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return supplier.apply(initialMonitor);
            } catch (Throwable t) {
                throw new CompletionException(t);
            }
        });
        return new Task<>(composedFuture, initialMonitor);
    }

    public static Task<Void> runAsync(final FailableConsumer<ProgressReporting.ProgressMonitor, Throwable> runnable) {
        var initialMonitor = new ProgressReporting.ProgressMonitor();
        var composedFuture = CompletableFuture.runAsync(() -> {
            try {
                runnable.accept(initialMonitor);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });
        return new Task<>(composedFuture, initialMonitor);
    }

    public static <O> Task<O> of(CompletableFuture<O> future) {
        return new Task<>(future, new ProgressReporting.ProgressMonitor());
    }

    public <O> Task<O> thenCompose(final Function<R, O> nextFunction) {
        var nextMonitor = new ProgressReporting.ProgressMonitor();
        var composedFuture = future.thenCompose(previousResult -> {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return nextFunction.apply(previousResult, nextMonitor);
                } catch (Throwable t) {
                    throw new CompletionException(t);
                }
            });
        });
        var composedMonitor = this.monitor.thenCompose(nextMonitor);
        return new Task<>(composedFuture, composedMonitor);
    }

    public <O> Task<O> thenCompose(final CompletableFuture<O> future) {
        return new Task<>(future.thenCompose(ignored -> future), this.monitor);
    }

    public Task<Void> thenAccept(final Consumer<R> consumer) {
        var nextMonitor = new ProgressReporting.ProgressMonitor();
        var composedFuture = future.thenAcceptAsync(previousResult -> {
            try {
                consumer.accept(previousResult, nextMonitor);
            } catch (Throwable t) {
                throw new CompletionException(t);
            }
        });
        var composedMonitor = this.monitor.thenCompose(nextMonitor);
        return new Task<>(composedFuture, composedMonitor);
    }

    public Task<Void> thenRun(final FailableConsumer<ProgressReporting.ProgressMonitor, Throwable> runnable) {
        var nextMonitor = new ProgressReporting.ProgressMonitor();
        var composedFuture = future.thenRunAsync(() -> {
            try {
                runnable.accept(nextMonitor);
            } catch (Throwable t) {
                throw new CompletionException(t);
            }
        });
        var composedMonitor = this.monitor.thenCompose(nextMonitor);
        return new Task<>(composedFuture, composedMonitor);
    }

    public boolean cancel() {
        return future.cancel(true);
    }

    public Task<R> whenComplete(BiConsumer<? super R, ? super Throwable> action) {
        return new Task<>(future.whenComplete(action), this.monitor);
    }

    public R getNow() {
        return this.future.getNow(null);
    }

    public boolean isDone() {
        return this.future.isDone();
    }

    public void join() {
        future.join();
    }

    // todo these names may be confusing
    public interface Function<I, O> extends FailableBiFunction<I, ProgressReporting.ProgressMonitor, O, Throwable> {

    }

    // Consumer --(add parameter)--> BiConsumer
    // Supplier --(add parameter)--> Function
    public interface Supplier<O> extends FailableFunction<ProgressReporting.ProgressMonitor, O, Throwable> {

    }

    public interface Consumer<I> extends FailableBiConsumer<I, ProgressReporting.ProgressMonitor, Throwable> {}
}
