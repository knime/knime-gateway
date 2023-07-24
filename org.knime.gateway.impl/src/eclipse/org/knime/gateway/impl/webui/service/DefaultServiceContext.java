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
 *   Jul 24, 2023 (hornm): created
 */
package org.knime.gateway.impl.webui.service;

import java.io.Closeable;
import java.util.Optional;

/**
 * Contextual information available to default service implementations when service methods are being called.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public final class DefaultServiceContext {

    private static final ThreadLocal<DefaultServiceContext> CONTEXT = new ThreadLocal<>();

    private final String m_workflowProjectId;

    private DefaultServiceContext(final String workflowProjectId) {
        m_workflowProjectId = workflowProjectId;
    }

    private DefaultServiceContext() {
        this(null);
    }

    /**
     * Sets the context for the current thread initialized with the given workflow project id.
     *
     * @param workflowProjectId
     * @return a closable to be used to unset the context again (i.e. equivalent to calling {@link #unset()}).
     * @throws IllegalStateException if there is already a context set
     */
    public static Closeable set(final String workflowProjectId) {
        if (CONTEXT.get() != null) {
            throw new IllegalStateException("DefaultServiceContext already set");
        }
        CONTEXT.set(new DefaultServiceContext(workflowProjectId));
        return DefaultServiceContext::unset;
    }

    /**
     * Unsets the currently set context for this thread.
     */
    public static void unset() {
        CONTEXT.remove();
    }

    /**
     * @return the workflow project id associated with the current context (for the current thread) or an empty optional
     *         if there is none
     */
    static Optional<String> getWorkflowProjectId() {
        var c = CONTEXT.get();
        if (c != null) {
            return Optional.ofNullable(c.m_workflowProjectId);
        }
        return Optional.empty();
    }

    /**
     * Asserts that the passed workflow project id is associated with the context (if one is set).
     *
     * @param workflowProjectIdToCheck
     * @throws IllegalStateException if the workflow project id associated with the current context doesn't match the
     *             one passed as parameter
     */
    static void assertWorkflowProjectId(final String workflowProjectIdToCheck) {
        var expectedId = getWorkflowProjectId().orElse(null);
        if (expectedId == null) {
            return;
        }
        if (!expectedId.equals(workflowProjectIdToCheck)) {
            throw new IllegalStateException(
                "No workflow project id available/accessible for the given id (" + workflowProjectIdToCheck + ")");
        }
    }

}
