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

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.apache.commons.lang3.function.FailableCallable;
import org.junit.Test;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.NetworkException;

/**
 * Test the {@link NetworkExceptions} utility class methods
 *
 * @author Kai Franze, KNIME GmbH, Germany
 */
public class NetworkExceptionsTest {

    /**
     * Tests the happy path
     *
     * @throws Exception
     */
    @Test
    public void testHappyPath() throws Exception {
        final var result = NetworkExceptions.callWithCatch(() -> "success", "failure", 1);
        assertThat(result).as("Check for the correct result").isEqualTo("success");
    }

    /**
     * Tests the timeout path
     *
     * @throws Exception
     */
    @Test
    public void testTimeout() throws Exception {
        final FailableCallable<String, InterruptedException> callable = () -> {
            Thread.sleep(1500);
            return "success";
        };

        // Does not timeout
        final var result = NetworkExceptions.callWithCatch(callable, "failure", 2);
        assertThat(result).as("Check for the correct result").isEqualTo("success");

        // Should timeout
        final var exception = assertThrows(NetworkException.class, () -> NetworkExceptions.callWithCatch(callable, "failure", 1));
        assertThat(exception.getMessage()).as("Check the exception message").contains("failure");
        assertThat(exception.getMessage()).as("Check the exception message").contains("The request timed out.");
    }

    /**
     * Tests the runtime exception path
     */
    @Test
    public void testRuntimeExceptions() {
        final var exception = assertThrows(NetworkException.class, () -> NetworkExceptions.callWithCatch(() -> {
            throw new RuntimeException(new SocketException("network"));
        }, "failure", 1));
        assertThat(exception.getMessage()).as("Check the exception message").contains("failure");
        assertThat(exception.getMessage()).as("Check the exception message").contains("No connection.");

        final var exception2 = assertThrows(NetworkException.class, () -> NetworkExceptions.callWithCatch(() -> {
            throw new RuntimeException(new UnknownHostException("network"));
        }, "failure", 1));
        assertThat(exception2.getMessage()).as("Check the exception message").contains("failure");
        assertThat(exception2.getMessage()).as("Check the exception message").contains("Host could not be found.");

        final var exception3 = assertThrows(RuntimeException.class, () -> NetworkExceptions.callWithCatch(() -> {
            throw new RuntimeException(new Exception("unknown"));
        }, "failure", 1));
        assertThat(exception3.getMessage()).as("Check the exception message").doesNotContain("failure");
        assertThat(exception3.getMessage()).as("Check the exception message").contains("unknown");
    }

    /**
     * Tests the exception forwarding
     */
    @Test
    public void testExceptionForwarding() {
        final var exception = assertThrows(IOException.class, () -> NetworkExceptions.callWithCatch(() -> {
            throw new IOException("forwarded");
        }, "failure", 1));
        assertThat(exception.getMessage()).as("Check the exception message").doesNotContain("failure");
        assertThat(exception.getMessage()).as("Check the exception message").contains("forwarded");
    }

}
