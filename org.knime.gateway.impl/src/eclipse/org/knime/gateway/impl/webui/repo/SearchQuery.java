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
 */
package org.knime.gateway.impl.webui.repo;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.knime.core.node.port.PortType;
import org.knime.core.util.Pair;
import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.api.webui.service.util.ServiceExceptions;

/**
 * A search query for a {@link NodeRepository}.
 */
final class SearchQuery {
    private final String m_searchTerm;

    private final List<String> m_tags;

    private final boolean m_allTagsMustMatch;

    private final NodeFilter m_nodeFilter;

    private final PortType m_portType;

    private final boolean m_searchForSuccesors;

    /**
     * Construct a search query from the given parameters.
     *
     * @param query The query string as entered by the user
     * @param tags Selected tags
     * @param allTagsMustMatch Whether search hits must match all selected tags
     * @param portTypeId The port type search hits must be compatible to.
     * @param searchForSuccesors if the search look for successors compatible with the given portTypeId
     */
    SearchQuery(final String query, final List<String> tags, final Boolean allTagsMustMatch, final String portTypeId,
        final boolean searchForSuccesors) throws ServiceExceptions.InvalidRequestException {
        var parsed = parseQuery(query);
        m_searchTerm = parsed.getFirst();
        m_nodeFilter = parsed.getSecond();
        m_tags = tags == null ? Collections.emptyList() : tags;
        m_allTagsMustMatch = Boolean.TRUE.equals(allTagsMustMatch);
        m_portType = verifyPortTypeId(portTypeId);
        m_searchForSuccesors = searchForSuccesors;
    }

    private static PortType verifyPortTypeId(final String portTypeId) throws ServiceExceptions.InvalidRequestException {
        if (StringUtils.isBlank(portTypeId)) {
            return null;
        }
        return CoreUtil.getPortType(portTypeId).orElseThrow(() -> new ServiceExceptions.InvalidRequestException(
            String.format("Not port type found for <%s>", portTypeId)));
    }

    /**
     * Parse the query string for a special suffix indicating the requested deprecation status.
     *
     * @param queryString The query string supplied by the frontend.
     * @return The query string without the suffix (or the original query string if none found), and the requested
     *         deprecation status as indicated by the suffix.
     */
    private static Pair<String, NodeFilter> parseQuery(final String queryString) {
        String effectiveQuery;
        NodeFilter nodeFilter;
        if (queryString == null) {
            effectiveQuery = null;
            nodeFilter = NodeFilter.NONE;
        } else {
            if (queryString.endsWith("//hidden")) {
                effectiveQuery = queryString.replace("//hidden", "");
                nodeFilter = NodeFilter.HIDDEN;
            } else if (queryString.endsWith("//deprecated")) {
                effectiveQuery = queryString.replace("//deprecated", "");
                nodeFilter = NodeFilter.DEPRECATED;
            } else {
                effectiveQuery = queryString;
                nodeFilter = NodeFilter.NONE;
            }
            effectiveQuery = effectiveQuery.strip().toUpperCase(Locale.ROOT);
        }
        return new Pair<>(effectiveQuery, nodeFilter);
    }

    String searchTerm() {
        return m_searchTerm;
    }

    List<String> tags() {
        return m_tags;
    }

    boolean allTagsMustMatch() {
        return m_allTagsMustMatch;
    }

    PortType portType() {
        return m_portType;
    }

    boolean isSearchForSuccesors() {
        return m_searchForSuccesors;
    }

    NodeFilter nodeFilter() {
        return m_nodeFilter;
    }

    enum NodeFilter {
            /**
             * Normal node
             */
            NONE,

            /**
             * Node is marked as hidden in its definition
             */
            HIDDEN,

            /**
             * Node is marked as deprecated in its definition
             */
            DEPRECATED
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (SearchQuery)obj;
        return Objects.equals(this.m_searchTerm, that.m_searchTerm) && Objects.equals(this.m_tags, that.m_tags)
            && this.m_allTagsMustMatch == that.m_allTagsMustMatch
            && Objects.equals(this.m_nodeFilter, that.m_nodeFilter) && Objects.equals(this.m_portType, that.m_portType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(m_searchTerm, m_tags, m_allTagsMustMatch, m_nodeFilter, m_portType);
    }
}
