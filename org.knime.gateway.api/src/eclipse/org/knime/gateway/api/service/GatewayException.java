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
 *   Mar 26, 2025 (franziskaobergfell): created
 */
package org.knime.gateway.api.service;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * common superclass of gateway api exceptions
 *
 * @author Franziska Obergfell, KNIME GmbH, Konstanz, Germany
 * @since 5.5
 */
public abstract class GatewayException extends Exception {

    private static final long serialVersionUID = 1L;

    private final Map<String, String> m_properties = new HashMap<>();

    private final boolean m_canCopy;

    /**
     * New instance
     *
     * @param canCopy Boolean indicating whether exception properties can be copied.
     */
    protected GatewayException(final boolean canCopy) {
        m_canCopy = canCopy;
    }

    /**
     * Gets the title of the exception as per "Problem Details” / RFC9457 standard.
     *
     * @return Exception title property if present or {@code null} if not present
     */
    public String getTitle() {
        return m_properties.get("title");
    }

    /**
     * Gets the details of the exception as per "Problem Details” / RFC9457 standard.
     *
     * @return Exception details property if present or {@code null} if not present
     */
    public String getDetails() {
        return m_properties.get("details");
    }

    /**
     * Checks whether exception properties can be copied
     *
     * @return {@code true} if copying properties is possible, {@code false} otherwise.
     */
    public boolean isCanCopy() {
        return m_canCopy;
    }

    @Override
    public String getMessage() {
        return m_properties.get("message");
    }

    /**
     * Add a new property to the exception.
     *
     * @param key the name of the property to be set
     * @param value the value the property should be set to
     */
    public void addProperty(final String key, final String value) {
        m_properties.put(key, value);
    }

    /**
     * Retrieves additional exception properties, excluding "title" and "details". Map of property names to property
     * value.
     *
     * @return Key-values pairs of additional properties, excluding "title" and "details".
     */
    public Map<String, String> getAdditionalProperties() {
        return m_properties.entrySet().stream()
            .filter(entry -> !entry.getKey().equals("title") && !entry.getKey().equals("details"))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

}
