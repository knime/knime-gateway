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
 */
package org.knime.gateway.impl.service.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Instances remember specific changes until reset.
 *
 * @author Benjamin Moser, KNIME GmbH, Konstanz, Germany
 */
public class WorkflowChangesTracker {

    private final Set<WorkflowChange> m_trackedChanges = Collections.synchronizedSet(new HashSet<>());

    /**
     * Types of changes to occur to a workflow manager.
     *
     * @author Benjamin Moser, KNIME GmbH, Konstanz, Germany
     */
    public enum WorkflowChange {
        NODE_STATE_UPDATED,
        NODE_OR_CONNECTION_ADDED_OR_REMOVED,
        NODES_COLLAPSED,
        NODE_EXPANDED,
        ANY,
        ANNOTATION_ADDED_OR_REMOVED
    }

    /**
     * @param setAllOccurred If true, set all possible events to "have occurred".
     */
    public WorkflowChangesTracker(final boolean setAllOccurred) {
        if (setAllOccurred) {
            m_trackedChanges.addAll(Arrays.asList(WorkflowChange.values()));
        }
    }

    /**
     * Initialize a new tracker
     */
    public WorkflowChangesTracker() {
        this(false );
    }

    /**
     * @param workflowChange Change to remember to have occurred
     */
    public void track(final WorkflowChange workflowChange) {
        m_trackedChanges.add(workflowChange);
    }

    /**
     * @param workflowChange The event to check for
     * @return Whether the given event has occurred
     */
    public boolean hasOccurred(final WorkflowChange workflowChange) {
        return m_trackedChanges.contains(workflowChange);
    }

    /**
     * Forget that any event has occurred
     */
    public void reset() {
        m_trackedChanges.clear();
    }

}
