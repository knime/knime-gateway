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
 *   26 May 2025 (leonard.woerteler): created
 */
package org.knime.gateway.api.webui.service.util;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.knime.gateway.api.webui.service.util.ServiceExceptions.ServiceCallException;

/**
 * A mutable exception that can be used to collect details about a failure before converting it to an immutable
 * {@link ServiceCallException} that can then be displayed as a toast in MUI.
 * <p>
 * The main purpose of this class is to allow adding details to an exception before re-throwing it. Example usage:
 * <pre>
 * public void someMethod(final String parentId) throws MutableServiceCallException {
 *     try {
 *         doSpecificThatMightFail(parentId);
 *     } catch (MutableServiceCallException ex) {
 *         ex.addDetails("Couldn't process parent with ID " + parentId);
 *         throw ex;
 *     }
 * }
 * </pre>
 *
 * @author Leonard Woerteler, KNIME GmbH, Konstanz, Germany
 * @since 5.7
 */
@SuppressWarnings("serial")
public final class MutableServiceCallException extends Exception {

    private final int m_status;

    private final ArrayDeque<String> m_details = new ArrayDeque<>();

    private final Map<String, String> m_additional = new HashMap<>();

    private final boolean m_canCopy;

    private static String createMessage(final String title, final Iterable<String> details) {
        final var sb = new StringBuilder(title);
        if (details != null) {
            details.forEach(detail -> sb.append(" * " + detail));
        }
        return sb.toString();
    }

    /**
     * Creates a new mutable exception.
     *
     * @param status HTTP status code of the response that caused this exception, or {@code -1} if not applicable
     * @param details list of details (user-oriented explanation/hint), may be {@code null}
     * @param canCopy whether the details can be copied to the clipboard
     */
    public MutableServiceCallException(final int status, final List<String> details, final boolean canCopy) {
        this(status, details, canCopy, null);
    }

    /**
     * Creates a new mutable exception.
     *
     * @param details list of details (user-oriented explanation/hint), may be {@code null}
     * @param canCopy whether the details can be copied to the clipboard
     */
    public MutableServiceCallException(final List<String> details, final boolean canCopy) {
        this(-1, details, canCopy);
    }

    /**
     * Creates a new mutable exception with a cause.
     *
     * @param details list of details (user-oriented explanation/hint), may be {@code null}
     * @param canCopy whether the details can be copied to the clipboard
     * @param cause the cause, may be {@code null}
     */
    public MutableServiceCallException(final List<String> details, final boolean canCopy, final Throwable cause) {
        this(-1, details, canCopy, cause);
    }

    /**
     * Creates a new mutable exception with a cause.
     *
     * @param status HTTP status code of the response that caused this exception, or {@code -1} if not applicable
     * @param details list of details (user-oriented explanation/hint), may be {@code null}
     * @param canCopy whether the details can be copied to the clipboard
     * @param cause the cause, may be {@code null}
     */
    public MutableServiceCallException(final int status, final List<String> details, final boolean canCopy,
            final Throwable cause) {
        super(createMessage(MutableServiceCallException.class.getSimpleName() + ", details:", details), cause);
        m_status = status;
        if (details != null) {
            m_details.addAll(details);
        }
        m_canCopy = canCopy;
    }

    /**
     * HTTP status code of the response that caused this exception, or {@code -1} if not applicable.
     *
     * @return status code or {@code -1}
     */
    public int getStatus() {
        return m_status;
    }

    /**
     * Adds lines of details to this exception. The lines will be added in the order they are given, i.e. the first
     * line will be the first line in the details list.
     *
     * @param detailsLines lines of details to add, may be {@code null}
     * @return this exception (for method chaining)
     */
    public MutableServiceCallException addDetails(final String... detailsLines) {
        return detailsLines == null ? this : addDetails(List.of(detailsLines));
    }

    /**
     * Adds lines of details to this exception. The lines will be added in the order they are given, i.e. the first
     * line will be the first line in the details list.
     *
     * @param detailsLines lines of details to add, may be {@code null}
     * @return this exception (for method chaining)
     */
    public MutableServiceCallException addDetails(final List<String> detailsLines) {
        if (detailsLines != null) {
            for (int i = detailsLines.size() - 1; i >= 0; i--) {
                m_details.addFirst(detailsLines.get(i));
            }
        }
        return this;
    }

    /**
     * Adds an additional property to this exception that will be copied to the resulting {@link ServiceCallException}.
     *
     * @param key property key
     * @param value property value
     * @return this exception (for method chaining)
     */
    public MutableServiceCallException setAdditionalProperty(final String key, final String value) {
        m_additional.put(key, value);
        return this;
    }

    /**
     * Converts this mutable exception to an immutable {@link ServiceCallException} with the given title.
     *
     * @param title title of the exception
     * @return the immutable exception
     */
    public ServiceCallException toGatewayException(final String title) {
        return ServiceCallException.builder() //
            .withTitle(title) //
            .withDetails(m_details) //
            .canCopy(m_canCopy) //
            .withStatusCode(m_status) //
            .withCause(this) //
            .withAdditionalProps(m_additional) //
            .build();
    }
}
