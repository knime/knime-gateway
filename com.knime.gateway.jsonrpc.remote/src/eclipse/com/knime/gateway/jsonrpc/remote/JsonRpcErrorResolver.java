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
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
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
 *   Jan 10, 2018 (hornm): created
 */
package com.knime.gateway.jsonrpc.remote;

import java.lang.reflect.Method;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.googlecode.jsonrpc4j.AnnotationsErrorResolver;
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
        return new JsonError(resolver.code(), message, resolver.data());
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
