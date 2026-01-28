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
 *   Dec 23, 2025 (ChatGPT): created
 */
package org.knime.gateway.impl.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

import org.junit.Test;

/**
 * Tests for {@link Debouncer}.
 */
public class DebouncerTest {

    /**
     * Ensures a single call is executed only after the configured delay.
     *
     * @throws Exception - unexpected
     */
    @Test
    public void testCallExecutesAfterDelay() throws Exception {
        var executed = new AtomicBoolean(false);
        var latch = new CountDownLatch(1);
        var debouncer = new Debouncer(Duration.ofSeconds(1), () -> {
            executed.set(true);
            latch.countDown();
        });

        try {
            debouncer.call();

            assertThat("Task must not run before delay expires", latch.await(500, TimeUnit.MILLISECONDS), is(false));
            assertThat("Task should run after delay", latch.await(2, TimeUnit.SECONDS), is(true));
            assertThat(executed.get(), is(true));
        } finally {
            debouncer.shutdown();
        }
    }

    /**
     * Ensures subsequent calls reset the pending execution so only the final invocation runs.
     *
     * @throws Exception - unexpected
     */
    @Test
    public void testCallResetsDelay() throws Exception {
        var executions = new AtomicInteger();
        var latch = new CountDownLatch(1);
        var executionTime = new AtomicLong(); // to be able to modify from lambda
        var debouncer = new Debouncer(Duration.ofSeconds(1), () -> {
            executions.incrementAndGet();
            executionTime.set(System.currentTimeMillis());
            latch.countDown();
        });

        try {
            debouncer.call();
            Thread.sleep(300);
            assertThat("No execution should happen before the final delay elapses",
                latch.await(100, TimeUnit.MILLISECONDS), is(false));
            debouncer.call();
            Thread.sleep(300);
            assertThat("No execution should happen before the final delay elapses",
                latch.await(100, TimeUnit.MILLISECONDS), is(false));
            debouncer.call();
            var lastCallTime = System.currentTimeMillis();

            assertThat("No execution should happen before the final delay elapses",
                latch.await(700, TimeUnit.MILLISECONDS), is(false));

            assertThat("Only one execution expected after debouncing", latch.await(2, TimeUnit.SECONDS), is(true));
            assertThat(executions.get(), is(1));
            assertThat("Execution should happen after the final call delay",
                executionTime.get() - lastCallTime, greaterThanOrEqualTo(1000L));

            Thread.sleep(600);
            assertThat("Cancelled executions must not leak through", executions.get(), is(1));
        } finally {
            debouncer.shutdown();
        }
    }

    /**
     * Ensures no execution happens after shutdown.
     */
    @Test
    public void testCallIgnoredAfterShutdown() throws Exception {
        var latch = new CountDownLatch(1);
        var debouncer = new Debouncer(Duration.ofSeconds(1), latch::countDown);
        debouncer.shutdown();

        debouncer.call();

        assertThat("Task must not run after shutdown", latch.await(1500, TimeUnit.MILLISECONDS), is(false));
    }

    /**
     * Ensures exceptions in the runnable do not stall the debouncer.
     */
    @Test
    public void testExceptionInRunnableDoesNotStall() throws Exception {
        var executions = new AtomicInteger();
        var latch = new CountDownLatch(2);
        var firstStartLatch = new CountDownLatch(1);
        var debouncer = new Debouncer(Duration.ofMillis(200), () -> {
            executions.incrementAndGet();
            firstStartLatch.countDown();
            latch.countDown();
            throw new RuntimeException("boom");
        });

        try {
            debouncer.call();
            assertThat("First execution should start", firstStartLatch.await(2, TimeUnit.SECONDS), is(true));
            debouncer.call();

            assertThat("Both executions should happen despite exceptions", latch.await(2, TimeUnit.SECONDS), is(true));
            assertThat(executions.get(), is(2));
        } finally {
            debouncer.shutdown();
        }
    }

    /**
     * Ensures a burst of calls coalesces to a single execution.
     */
    @Test
    public void testBurstCallsCoalesce() throws Exception {
        var executions = new AtomicInteger();
        var latch = new CountDownLatch(1);
        var debouncer = new Debouncer(Duration.ofMillis(300), () -> {
            executions.incrementAndGet();
            latch.countDown();
        });

        try {
            IntStream.range(0, 20).forEach(i -> debouncer.call());
            assertThat("Execution should happen after delay", latch.await(2, TimeUnit.SECONDS), is(true));
            assertThat(executions.get(), is(1));
        } finally {
            debouncer.shutdown();
        }
    }

    /**
     * Ensures a call during execution schedules a follow-up after completion.
     */
    @Test
    public void testCallDuringExecutionSchedulesFollowUp() throws Exception {
        var executions = new AtomicInteger();
        var latch = new CountDownLatch(2);
        var firstStartLatch = new CountDownLatch(1);
        var startTimes = new long[2];
        var debouncer = new Debouncer(Duration.ofMillis(500), () -> {
            startTimes[executions.getAndIncrement()] = System.currentTimeMillis();
            firstStartLatch.countDown();
            sleep(1);
            latch.countDown();
        });

        try {
            debouncer.call();
            assertThat("First execution should start", firstStartLatch.await(3, TimeUnit.SECONDS), is(true));
            debouncer.call();

            assertThat("Second execution should happen", latch.await(6, TimeUnit.SECONDS), is(true));
            assertThat(startTimes[1] - startTimes[0], greaterThanOrEqualTo(1500L));
        } finally {
            debouncer.shutdown();
        }
    }

    /**
     * Ensures re-scheduling while scheduled resets the delay.
     */
    @Test
    public void testCancelWhileScheduledResetsDelay() throws Exception {
        var latch = new CountDownLatch(1);
        var start = System.currentTimeMillis();
        var debouncer = new Debouncer(Duration.ofMillis(800), latch::countDown);

        try {
            debouncer.call();
            Thread.sleep(300);
            debouncer.call();

            assertThat("Execution should occur after the last call delay", latch.await(2, TimeUnit.SECONDS), is(true));
            assertThat(System.currentTimeMillis() - start, greaterThanOrEqualTo(1100L));
        } finally {
            debouncer.shutdown();
        }
    }

    /**
     * Ensure a fixed interval between execution independent from how long the execution takes (NXT-4433).
     *
     * @throws InterruptedException
     */
    @Test
    public void testCallScheduledOnlyAfterExecutionCompleted() throws InterruptedException {
        var executions = new AtomicInteger();
        var latch = new CountDownLatch(2);
        var firstStartLatch = new CountDownLatch(1);
        var startTimes = new long[2];
        var debouncer = new Debouncer(Duration.ofSeconds(2), () -> {
            startTimes[executions.getAndIncrement()] = System.currentTimeMillis();
            firstStartLatch.countDown();
            sleep(1);
            latch.countDown();
        });

        try {
            debouncer.call();
            assertThat("First execution should start", firstStartLatch.await(4, TimeUnit.SECONDS), is(true));
            debouncer.call();
            assertThat("Second execution should happen after first completes", latch.await(7, TimeUnit.SECONDS),
                is(true));

            // 1s execution + 2s delay between the start of executions
            assertThat("Start times must be at least 3s apart", startTimes[1] - startTimes[0],
                greaterThanOrEqualTo(3000L));

        } finally {
            debouncer.shutdown();
        }
    }

    private static void sleep(final long seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}
