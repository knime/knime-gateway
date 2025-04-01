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

package org.knime.gateway.api.util;

import java.util.Objects;

/**
 * Identifies a workflow version, i.e. a version assigned to a specific
 * {@link org.knime.core.node.workflow.WorkflowManager}.
 *
 * @implNote `toString` and {@link this#parse(String)} are compatible with the Catalog Service API spec.
 */
public sealed abstract class VersionId {

    private VersionId() {

    }

    /**
     * TODO
     *
     * @return
     */
    public abstract boolean isCurrentState();

    /**
     * Corresponds to the "draft" (or "working area") concept.
     */
    public static final class CurrentState extends VersionId {

        private CurrentState() {

        }


        private static final CurrentState CURRENT_STATE = new CurrentState();

        /**
         * @return value compatible with Catalog Service
         */
        @Override
        public String toString() {
            return "current-state";
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj == null || obj.getClass() != this.getClass()) {
                return false;
            }
            return Objects.equals(this.toString(), obj.toString());
        }


        @Override
        public int hashCode() {
            return this.toString().hashCode();
        }

        @Override
        public boolean isCurrentState() {
            return true;
        }
    }

    /**
     * @see CurrentState
     */
    public static CurrentState currentState() {
        return CurrentState.CURRENT_STATE;
    }

    /**
     * Represents a read-only snapshot of some previous state.
     */
    public static final class Fixed extends VersionId {
        private final int m_id;

        /**
         * @param id identifier of that version. Can not be assumed to be sequential.
         */
        public Fixed(final int id) {
            this.m_id = id;
        }

        @Override
        public String toString() {
            return String.valueOf(m_id);
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj == null || obj.getClass() != this.getClass()) {
                return false;
            }
            var that = (Fixed)obj;
            return Objects.equals(this.m_id, that.m_id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(m_id);
        }

        @Override
        public boolean isCurrentState() {
            return false;
        }

    }

    /**
     * Parse an instance from the given string.
     */
    public static VersionId parse(final String versionId) throws IllegalArgumentException {
        if (versionId == null || CurrentState.CURRENT_STATE.toString().equals(versionId)) {
            return new CurrentState();
        }
        return new Fixed(Integer.parseInt(versionId));
    }

}
