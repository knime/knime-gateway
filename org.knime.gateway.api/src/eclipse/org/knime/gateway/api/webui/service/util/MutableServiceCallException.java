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
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.knime.gateway.api.webui.service.util.ServiceExceptions.ServiceCallException;

/**
 *
 * @author leonard.woerteler
 * @since 5.7
 */
@SuppressWarnings("serial")
public final class MutableServiceCallException extends Exception {

    private final int m_status;

    private final Deque<String> m_details = new ArrayDeque<>();

    private final Map<String, String> m_additional = new HashMap<>();

    private final boolean m_canCopy;

    private static String createMessage(final String title, final Iterable<String> details) {
        final var sb = new StringBuilder(title);
        details.forEach(detail -> sb.append(" * " + detail));
        return sb.toString();
    }

    public MutableServiceCallException(final int status, final List<String> details, final boolean canCopy) {
        this(status, details, canCopy, null);
    }

    public MutableServiceCallException(final List<String> details, final boolean canCopy) {
        this(-1, details, canCopy);
    }

    public MutableServiceCallException(final List<String> details, final boolean canCopy, final Throwable cause) {
        this(-1, details, canCopy, cause);
    }

    public MutableServiceCallException(final int status, final List<String> details, final boolean canCopy,
            final Throwable cause) {
        super(createMessage(MutableServiceCallException.class.getSimpleName() + ", details:", details), cause);
        m_status = status;
        if (details != null) {
            m_details.addAll(details);
        }
        m_canCopy = canCopy;
    }

    public MutableServiceCallException addDetails(final String... detailsLines) {
        return addDetails(List.of(detailsLines));
    }

    public MutableServiceCallException addDetails(final List<String> detailsLines) {
        if (detailsLines != null) {
            for (int i = detailsLines.size() - 1; i >= 0; i--) {
                m_details.addFirst(detailsLines.get(i));
            }
        }
        return this;
    }

    public MutableServiceCallException setAdditionalProperty(final String key, final String value) {
        m_additional.put(key, value);
        return this;
    }

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

    public void copyContextTo(final ServiceCallException ex) {
        final var title = getMessage();
        ex.addProperty("title", title);
        if (m_details.isEmpty()) {
            ex.addProperty("details", "");
        } else {
            ex.addProperty("details", m_details.stream().collect(Collectors.joining("\n")));
            ex.addProperty("message", createMessage(title, m_details));
        }
        m_additional.forEach(ex::addProperty);
    }
}
