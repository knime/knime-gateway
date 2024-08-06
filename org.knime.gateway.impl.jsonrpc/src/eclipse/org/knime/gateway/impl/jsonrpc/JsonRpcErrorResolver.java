/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.com; Email: contact@knime.com
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
 */
package org.knime.gateway.impl.jsonrpc;

import java.lang.reflect.Method;
import java.util.List;

import org.knime.core.node.NodeLogger;

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
final class JsonRpcErrorResolver implements ErrorResolver {

    private final ExceptionToJsonRpcErrorTranslator m_translator;

    JsonRpcErrorResolver(final ExceptionToJsonRpcErrorTranslator t) {
        m_translator = t;
    }

    @Override
    public JsonError resolveError(final Throwable thrownException, final Method method,
        final List<JsonNode> arguments) {
        JsonRpcError jsonRpcError = getResolverForException(thrownException, method);
        if (jsonRpcError == null) {
            NodeLogger.getLogger(getClass()).warn("An unexpected json rpc error occurred", thrownException);
        }
        return new JsonError(
            jsonRpcError != null ? jsonRpcError.code() : m_translator.getUnexpectedExceptionErrorCode(thrownException),
            m_translator.getMessage(thrownException, jsonRpcError),
            m_translator.getData(thrownException, jsonRpcError));
    }

    private static JsonRpcError getResolverForException(final Throwable thrownException, final Method method) {
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

    private static boolean hasAnnotations(final JsonRpcErrors errors) {
        return errors != null;
    }

    private static boolean isExceptionInstanceOfError(final Throwable target, final JsonRpcError em) {
        return em.exception().isInstance(target);
    }
}
