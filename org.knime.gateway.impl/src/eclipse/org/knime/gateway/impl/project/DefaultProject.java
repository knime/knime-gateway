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
 *   Nov 17, 2023 (hornm): created
 */
package org.knime.gateway.impl.project;

import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.knime.core.node.workflow.WorkflowManager;

/**
 * Default implementation of {@link Project}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public final class DefaultProject implements Project {

    /**
     * @param projectName
     * @return a globally unique project id combined with the given project name
     */
    public static String getUniqueProjectId(final String projectName) {
        return projectName + "_" + UUID.randomUUID();
    }

    private final String m_id;

    private final String m_name;

    private final WorkflowManager m_wfm;

    private final Origin m_origin;

    /**
     * @param builder
     */
    private DefaultProject(final DefaultProjectBuilder builder) {
        m_wfm = builder.m_wfm;
        m_id = builder.m_id;
        m_name = builder.m_name;
        m_origin = builder.m_origin;
    }

    @Override
    public String getName() {
        return m_name;
    }

    @Override
    public String getID() {
        return m_id;
    }

    @Override
    public WorkflowManager openProject() {
        return m_wfm;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Origin> getOrigin() {
        return Optional.ofNullable(m_origin);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(m_id).append(m_name).append(m_origin).build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Project)) {
            return false;
        }
        final Project otherProject = (Project)other;
        return new EqualsBuilder().append(m_id, otherProject.getID()).append(m_name, otherProject.getName())
            .append(m_origin, otherProject.getOrigin().orElse(null)).build();
    }

    /**
     * @param wfm
     * @return a builder for {@link DefaultProject}-instances
     */
    public static DefaultProjectBuilder builder(final WorkflowManager wfm) {
        return new DefaultProjectBuilder(wfm);
    }

    /**
     * Builder for {@link DefaultProject}-instances.
     */
    public static final class DefaultProjectBuilder {

        private final WorkflowManager m_wfm;

        private String m_id;

        private String m_name;

        private Origin m_origin;

        private DefaultProjectBuilder(final WorkflowManager wfm) {
            m_wfm = wfm;
            m_name = wfm.getName();
            m_id = getUniqueProjectId(m_name);
            m_origin = null;
        }

        /**
         * @param id the id to set
         * @return this
         */
        public DefaultProjectBuilder setId(final String id) {
            m_id = id;
            return this;
        }

        /**
         * @param name the name to set
         * @return this
         */
        public DefaultProjectBuilder setName(final String name) {
            m_name = name;
            return this;
        }

        /**
         * @param origin the origin to set
         * @return this
         */
        public DefaultProjectBuilder setOrigin(final Origin origin) {
            m_origin = origin;
            return this;
        }

        /**
         * @return a new {@link DefaultProject}-instance
         */
        public DefaultProject build() {
            return new DefaultProject(this);
        }

    }

}
