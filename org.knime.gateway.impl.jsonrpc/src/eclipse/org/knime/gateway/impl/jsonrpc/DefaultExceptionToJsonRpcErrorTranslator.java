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
 *   Oct 9, 2020 (hornm): created
 */
package org.knime.gateway.impl.jsonrpc;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.knime.gateway.json.util.ObjectMapperUtil;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * A default implementation of the exception to jsonrpc error translator.
 *
 * The json-rpc error message contains the exception message itself. The json-rpc error data is a json object containg
 * the exception name and the entire stack trace.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class DefaultExceptionToJsonRpcErrorTranslator implements ExceptionToJsonRpcErrorTranslator {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage(final Throwable t) {
        return t.getMessage();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonNode getData(final Throwable t) {
        return getExceptionDetails(t);
    }

    @Override
    public int getUnexpectedExceptionErrorCode(final Throwable t) {
        return -32601;
    }

    private static JsonNode getExceptionDetails(final Throwable t) {
        ObjectNode details = ObjectMapperUtil.getInstance().getObjectMapper().createObjectNode();
        details.put("name", t.getClass().getName());

        try (StringWriter stringWriter = new StringWriter(); PrintWriter printWriter = new PrintWriter(stringWriter)) {
            t.printStackTrace(printWriter);
            details.put("stackTrace", stringWriter.toString());
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
        return details;
    }

}
