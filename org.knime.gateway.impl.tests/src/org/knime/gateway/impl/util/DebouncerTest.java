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
import static org.hamcrest.Matchers.is;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

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
        var debouncer = new Debouncer(Duration.ofSeconds(1), () -> {
            executions.incrementAndGet();
            latch.countDown();
        });

        try {
            debouncer.call();
            Thread.sleep(300);
            debouncer.call();
            Thread.sleep(300);
            debouncer.call();

            assertThat("No execution should happen before the final delay elapses",
                latch.await(700, TimeUnit.MILLISECONDS), is(false));

            assertThat("Only one execution expected after debouncing", latch.await(2, TimeUnit.SECONDS), is(true));
            assertThat(executions.get(), is(1));

            Thread.sleep(600);
            assertThat("Cancelled executions must not leak through", executions.get(), is(1));
        } finally {
            debouncer.shutdown();
        }
    }
}
