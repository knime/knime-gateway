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
 *   Oct 8, 2020 (hornm): created
 */
package org.knime.gateway.impl.jsonrpc;

import com.googlecode.jsonrpc4j.JsonRpcError;

/**
 * Turns an exception into the 'message' and 'data' properties of an error object as defined by the json-rpc 2.0
 * standard.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public interface ExceptionToJsonRpcErrorTranslator {

    /**
     * @param t the thrown exception
     * @return a short description of the error - corresponds to the 'message' property in the error object of the json
     *         rpc 2.0 standard
     */
    default String getMessage(final Throwable t) {
        return t.getClass().getSimpleName();
    }

    /**
     * @param t the thrown exception
     * @param errorAnnotation the json-rpc-error annotation if there is already a mapping from exception to
     *            json-rpc-error
     * @return a short description of the error - corresponds to the 'message' property in the error object of the json
     *         rpc 2.0 standard
     */
    default String getMessage(final Throwable t, final JsonRpcError errorAnnotation) {
        return getMessage(t);
    }

    /**
     * @param t the thrown exception
     * @return additional information about the error - corresponds to the 'data' property in the error object of the
     *         json-rpc 2.0 standard
     */
    default Object getData(final Throwable t) {
        return t.getMessage();
    }

    /**
     * @param t the thrown exception
     * @param errorAnnotation the json-rpc-error annotation if there is already a mapping from exception to
     *            json-rpc-error
     * @return a short description of the error - corresponds to the 'message' property in the error object of the json
     *         rpc 2.0 standard
     */
    default Object getData(final Throwable t, final JsonRpcError errorAnnotation) {
        return getData(t);
    }

    /**
     * @param t the thrown exception
     * @return the json-rpc 2.0 error code
     */
    int getUnexpectedExceptionErrorCode(Throwable t);

}
