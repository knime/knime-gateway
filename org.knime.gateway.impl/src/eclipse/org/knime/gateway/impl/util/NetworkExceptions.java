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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.function.FailableCallable;
import org.knime.core.node.NodeLogger;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.NetworkException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.ServiceCallException;

/**
 * Utility methods for network calls
 *
 * @author Kai Franze, KNIME GmbH, Germany
 */
public final class NetworkExceptions {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(NetworkExceptions.class);

    private static final int DEFAULT_TIMEOUT = 5;

    private NetworkExceptions() {
        // Utility class
    }

    /**
     * Invokes given callable while catching network related runtime exceptions using the default timeout.
     *
     * @param <R> The return type
     * @param <E> The exception type thrown by the callable
     * @param callable The function to call that might throw a network related runtime exception
     * @param errorMessage The error message to show
     *
     * @return The result of the call
     * @throws NetworkException If a network related exception was caught, a network exception will be thrown
     * @throws ServiceCallException If the call was interrupted
     * @throws Exception Re-thrown exception in case it's not network related
     */
    public static <R, E extends Exception> R callWithCatch(final FailableCallable<R, E> callable,
        final String errorMessage) throws E, NetworkException, ServiceCallException {
        return callWithCatch(callable, errorMessage, DEFAULT_TIMEOUT);
    }

    /**
     * Invokes given callable while catching network related runtime exceptions.
     *
     * @param <R> The return type
     * @param <E> The exception type thrown by the callable
     * @param callable The function to call that might throw a network related runtime exception
     * @param errorMessage The error message to show
     * @param timeout The timeout in seconds, if the timeout is reached, a {@link NetworkException} is thrown
     *
     * @return The result of the call
     * @throws NetworkException If a network related exception was caught, a network exception will be thrown
     * @throws ServiceCallException If the call was interrupted
     * @throws Exception Re-thrown exception in case it's not network related
     */
    public static <R, E extends Exception> R callWithCatch(final FailableCallable<R, E> callable,
        final String errorMessage, final int timeout) throws E, NetworkException, ServiceCallException { // NOSONAR: 'E' can indeed be thrown
        try {
            final var future = CompletableFuture.supplyAsync(() -> {
                try {
                    return callable.call();
                } catch (Exception e) { // NOSONAR: We must catch all exceptions here in order to wrap them
                    throw ExceptionUtils.asRuntimeException(e);
                }
            });
            return future.get(timeout, TimeUnit.SECONDS);
        } catch (ExecutionException e) { // NOSONAR: Exception is logged and re-thrown
            final var unwrappedException = e.getCause();
            LOGGER.error("The request threw an exception. " + errorMessage, unwrappedException);
            return rethrowAsExposableException(unwrappedException, errorMessage); // Might throw 'E'
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ServiceCallException("The request was interrupted. ", e);
        } catch (TimeoutException e) {
            LOGGER.error("The request timed out. " + errorMessage, e);
            throw new NetworkException("The request timed out. " + errorMessage, e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <R, E extends Exception> R rethrowAsExposableException(final Throwable throwable,
        final String errorMessage) throws E, NetworkException {
        if (throwable instanceof RuntimeException runtimeException) {
            if (runtimeException.getCause() instanceof SocketException) {
                throw new NetworkException("No connection. " + errorMessage, runtimeException.getCause());
            }

            if (runtimeException.getCause() instanceof UnknownHostException) {
                throw new NetworkException("Host could not be found. " + errorMessage, runtimeException.getCause());
            }

            // If we can identify more causes indicating a network error, we should add them here

            throw runtimeException;
        }

        throw (E)throwable; // Unchecked type cast acceptable
    }

}
