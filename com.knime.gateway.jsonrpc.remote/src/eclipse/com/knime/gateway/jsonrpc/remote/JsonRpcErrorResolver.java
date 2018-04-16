/* ------------------------------------------------------------------
 * This source code, its documentation and all appendant files
 * are protected by copyright law. All rights reserved.
 *
 * Copyright by KNIME AG, Zurich, Switzerland
 *
 * You may not modify, publish, transmit, transfer or sell, reproduce,
 * create derivative works from, distribute, perform, display, or in
 * any way exploit any of the content, in whole or in part, except as
 * otherwise expressly permitted in writing by the copyright owner or
 * as specified in the license file distributed with this product.
 *
 * If you have any questions please contact the copyright holder:
 * website: www.knime.com
 * email: contact@knime.com
 * ---------------------------------------------------------------------
 *
 */
package com.knime.gateway.jsonrpc.remote;

import java.lang.reflect.Method;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.googlecode.jsonrpc4j.AnnotationsErrorResolver;
import com.googlecode.jsonrpc4j.ErrorData;
import com.googlecode.jsonrpc4j.ErrorResolver;
import com.googlecode.jsonrpc4j.JsonRpcError;
import com.googlecode.jsonrpc4j.JsonRpcErrors;
import com.googlecode.jsonrpc4j.ReflectionUtil;

/**
 * Resolves exceptions by looking at {@link JsonRpcErrors} annotations. Almost the same as
 * {@link AnnotationsErrorResolver} (can not be derived, some code copied from there), except the error data object.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class JsonRpcErrorResolver implements ErrorResolver {

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonError resolveError(final Throwable thrownException, final Method method,
        final List<JsonNode> arguments) {
        JsonRpcError resolver = getResolverForException(thrownException, method);
        if (notFoundResolver(resolver)) {
            return null;
        }

        String message = hasErrorMessage(resolver) ? resolver.message() : thrownException.getMessage();
        return new JsonError(resolver.code(), message,
            new ErrorData(thrownException.getClass().getName(), resolver.data()));
    }

    private JsonRpcError getResolverForException(final Throwable thrownException, final Method method) {
        JsonRpcErrors errors = ReflectionUtil.getAnnotation(method, JsonRpcErrors.class);
        if (hasAnnotations(errors)) {
            for (JsonRpcError errorDefined : errors.value()) {
                if (isExceptionInstanceOfError(thrownException, errorDefined)) {
                    return errorDefined;
                }
            }
        }
        return null;
    }

    private boolean notFoundResolver(final JsonRpcError resolver) {
        return resolver == null;
    }

    private boolean hasErrorMessage(final JsonRpcError em) {
        // noinspection ConstantConditions
        return em.message() != null && em.message().trim().length() > 0;
    }

    private boolean hasAnnotations(final JsonRpcErrors errors) {
        return errors != null;
    }

    private boolean isExceptionInstanceOfError(final Throwable target, final JsonRpcError em) {
        return em.exception().isInstance(target);
    }
}
