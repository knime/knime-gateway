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
 *   Oct 15, 2024 (kai): created
 */
package org.knime.gateway.impl.util;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

import org.apache.commons.lang3.function.FailableCallable;
import org.knime.core.util.exception.ResourceAccessException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.NetworkException;

/**
 * Handle failure cases around network operations.
 *
 * @author Kai Franze, KNIME GmbH, Germany
 */
public final class NetworkExceptions {

    private static final Duration DEFAULT_TIMEOUT = Duration.ofMinutes(2);

    private NetworkExceptions() {
        // Utility class
    }

    /**
     * Invokes given callable while catching network related exceptions using the a timeout.
     *
     * @param <R> The return type
     * @param <E> The exception type thrown by the callable
     * @param callable The task to execute
     * @param errorMessage The error message to show
     *
     * @return The result of the call
     * @throws NetworkException If a network related exception was caught, a network exception will be thrown
     * @throws E Re-thrown exception in case it's not network-related
     */
    public static <R, E extends Throwable> R callWithCatch(final FailableCallable<R, E> callable,
        final String errorMessage) throws E, NetworkException {
        return callWithCatch(callable, errorMessage, DEFAULT_TIMEOUT);
    }

    /**
     * Invokes given callable while catching network related exceptions.
     *
     * @param <R> The return type
     * @param <E> The exception type thrown by the callable
     * @param callable The task to execute
     * @param errorMessage The error message to show
     * @param timeout The timeout. If the timeout is reached, a {@link NetworkException} is thrown
     *
     * @return The result of the call
     * @throws NetworkException If a network related exception was caught, a network exception will be thrown
     * @throws E Re-thrown exception in case it's not network-related
     */
    public static <R, E extends Throwable> R callWithCatch(final FailableCallable<R, E> callable,
        final String errorMessage, final Duration timeout) throws E, NetworkException {
        try {
            return CompletableFuture.supplyAsync(unchecked(callable)) //
                .get(timeout.toSeconds(), TimeUnit.SECONDS);
        } catch (ExecutionException e) { // NOSONAR: Exception is logged and re-thrown
            @SuppressWarnings("unchecked") // Cause can only be of type E, as thrown by the future's callable.
            var cause = (E)e.getCause();
            return mapToNetworkException(cause, errorMessage);
        } catch (InterruptedException | CancellationException e) {
            Thread.currentThread().interrupt();
            throw new NetworkException("The request was interrupted. ", e);
        } catch (TimeoutException e) {
            throw new NetworkException("The request timed out. " + errorMessage, e);
        }
    }

    private static <R, E extends Throwable> R mapToNetworkException(final E throwable, final String message)
        throws E, NetworkException {
        Throwable throwableToInspect = throwable;
        if (throwable instanceof RuntimeException) {
            throwableToInspect = throwable.getCause();
        }
        if (throwableToInspect instanceof ResourceAccessException) {
            throw new NetworkException(message, throwableToInspect);
        }
        // Network-related exceptions such as the below may appear wrapped in runtime exceptions
        if (throwableToInspect instanceof SocketException) {
            throw new NetworkException(message + "No connection.", throwableToInspect);
        }
        if (throwableToInspect instanceof UnknownHostException) {
            throw new NetworkException(message + "Host could not be found.", throwableToInspect);
        }
        throw throwable;
    }

    private static <R, E extends Throwable> Supplier<R> unchecked(final FailableCallable<R, E> callable) {
        return () -> {
            try {
                return callable.call();
            } catch (Throwable e) {  // NOSONAR
                throw new CompletionException(e);
            }
        };
    }

}
