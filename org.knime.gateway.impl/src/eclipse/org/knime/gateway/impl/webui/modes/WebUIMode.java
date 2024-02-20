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
 *   Feb 19, 2024 (kai): created
 */
package org.knime.gateway.impl.webui.modes;

import java.util.Arrays;

/**
 * Modes the Modern UI of the AP can be launched in
 *
 * @author Kai Franze, KNIME GmbH, Germany
 */
public enum WebUIMode {

        /**
         * Special mode to only read but not change or execute a workflow
         */
        JOB_VIEWER("JOB-VIEWER", false),

        /**
         * The default mode
         */
        DEFAULT("DEFAULT", true);

    private final String m_name;

    private final boolean m_needsToLoadNodeRepository;

    private static final String SYSTEM_PROPERTY_KEY = "org.knime.ui.mode";

    WebUIMode(final String name, final boolean needsToLoadNodeRepository) {
        m_name = name;
        m_needsToLoadNodeRepository = needsToLoadNodeRepository;
    }

    /**
     * @return If {@code true}, then the mode requires a node repository; if {@code false} it does not.
     */
    public boolean needsToLoadNodeRepository() {
        return m_needsToLoadNodeRepository;
    }

    /**
     * Reads the {@link WebUIMode} from the system properties
     *
     * @return The {@link WebUIMode} if one was set, {@code Mode.DEFAULT} otherwise
     * @throws IllegalStateException if no legal mode was set
     */
    public static WebUIMode getMode() {
        final var prop = System.getProperty(SYSTEM_PROPERTY_KEY);

        if (prop == null) {
            return DEFAULT;
        }

        return Arrays.stream(values())//
            .filter(mode -> mode.m_name.equals(prop)) //
            .findFirst() //
            .orElseThrow(() -> new IllegalStateException(
                "The given <%s> system property contains a not supported value!".formatted(SYSTEM_PROPERTY_KEY)));
    }

}
