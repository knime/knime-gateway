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
 *   Oct 16, 2024 (kai): created
 */
package org.knime.gateway.impl.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.function.FailableCallable;
import org.junit.Test;
import org.knime.core.util.exception.ResourceAccessException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.NetworkException;

/**
 * Test the {@link NetworkExceptions} utility class methods
 *
 * @author Kai Franze, KNIME GmbH, Germany
 */
@SuppressWarnings({"java:S112", "java:S5960", "java:S5612"})
public class NetworkExceptionsTest {

    /**
     * Tests the happy path
     *
     * @throws Exception
     */
    @Test
    public void testHappyPath() throws Exception {
        final var result = NetworkExceptions.callWithCatch(() -> "success", "failure", Duration.ofSeconds(1));
        assertThat(result).as("Check for the correct result").isEqualTo("success");
    }

    /**
     * Asserts that a NetworkException with a TimeoutException as its cause is thrown if the callable takes too long to
     * complete.
     */
    @Test
    public void testTimeout() {
        var callableDuration = Duration.ofMillis(500);
        final FailableCallable<String, InterruptedException> callable = () -> {
            Thread.sleep(callableDuration.toMillis());
            return "success";
        };
        var insufficientTimeout = callableDuration.minusMillis(200);
        final var exception = assertThrows(NetworkException.class, //
            () -> NetworkExceptions.callWithCatch(callable, "failure", insufficientTimeout) //
        );
        assertThat(exception.getCause()).isInstanceOf(TimeoutException.class);
    }

    /**
     * Assert that some RuntimeExceptions of the callable result in NetworkExceptions, with the cause of the
     * RuntimeException being supplied as the cause of the NetworkException.
     */
    @Test
    public void testSomeRuntimeExceptionCausesAreRethrown() {
        var timeout = Duration.ofSeconds(1);
        List.of(new SocketException(), new UnknownHostException(), new ResourceAccessException("")) //
            .forEach(expectedException -> {
                FailableCallable<Void, Throwable> callable = () -> {
                    throw new RuntimeException(expectedException);
                };
                final var exception = assertThrows( //
                    NetworkException.class, //
                    () -> NetworkExceptions.callWithCatch(callable, "failure", timeout) //
                );
                assertThat(exception.getCause()) //
                    .isInstanceOf(expectedException.getClass()) //
                    .as("Specific causes of runtime exceptions are re-thrown");
            });
    }

    /**
     * Assert that any checked exceptions thrown by the callable which are not explicitly caught to produce a
     * NetworkException are re-thrown by `callWithCatch`.
     */
    @Test
    public void testOtherCheckedExceptionsAreStillThrown() {
        var checkedException = new Exception() {
            // anonymous subclass
        };
        assertThrows( //
            checkedException.getClass(), //
            () -> NetworkExceptions.callWithCatch(() -> {
                throw checkedException;
            }, "failure", Duration.ofSeconds(1)));
    }
}
