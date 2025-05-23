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
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

/**
 * Instances remember specific changes until reset.
 *
 * @author Benjamin Moser, KNIME GmbH, Konstanz, Germany
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class WorkflowChangesTracker {

    private final WorkflowChangesTrackerAccess m_trackerAccess;

    /**
     * Types of changes to occur to a workflow manager.
     */
    public enum WorkflowChange {
        /** Tracked on any node state change */
        NODE_STATE_UPDATED,
        /** Tracked whenever a node has been added */
        NODE_ADDED,
        /** Tracked whenever a node has been removed */
        NODE_REMOVED,
        /** Tracked whenever parts of a workflow are collapsed into a metanode/component. */
        NODES_COLLAPSED,
        /** Tracked whenever a metanode or a component have been expanded */
        NODE_EXPANDED,
        /** Tracked whenever a node's port configuration has changed */
        NODE_PORTS_CHANGED,
        /** Tracked when a connection is added */
        CONNECTION_ADDED,
        /** Tracked when a connection is removed */
        CONNECTION_REMOVED,
        /** Tracked whenever a workflow annotation has been added */
        ANNOTATION_ADDED,
        /** Tracked whenever a workflow annotation has been removed */
        ANNOTATION_REMOVED,
        /** Whenever a connection bendpoint has been modified or added or removed **/
        BENDPOINTS_MODIFIED,
        /** Whenever a metanode ports has been moved **/
        PORTS_BAR_MOVED,
        /** Whenever a component placeholder has been added */
        COMPONENT_PLACEHOLDER_ADDED;
    }

    /**
     * @param setAllOccurred If true, set all possible changes to "have occurred".
     */
    WorkflowChangesTracker(final boolean setAllOccurred) {
        m_trackerAccess = new WorkflowChangesTrackerAccess(setAllOccurred);
    }

    /**
     * @param workflowChange Change to remember to have occurred
     */
    synchronized void track(final WorkflowChange workflowChange) {
        m_trackerAccess.m_trackedChanges.add(workflowChange);
    }

    /**
     * Lets one invoke methods on the tracker. Also makes sure that no more changes are being tracked while the methods
     * are being invoked.
     *
     * @param <T>
     * @param trackerAccess provides the instance that gives access to tracker methods
     * @return arbitrary objects returned by the 'trackerAccess'
     */
    public synchronized <T> T invoke(final Function<WorkflowChangesTrackerAccess, T> trackerAccess) {
        return trackerAccess.apply(m_trackerAccess);
    }

    /**
     * Gives access to the actual {@link WorkflowChangesTracker}-methods.
     */
    public static final class WorkflowChangesTrackerAccess {

        private final Set<WorkflowChange> m_trackedChanges = new HashSet<>();

        private WorkflowChangesTrackerAccess(final boolean setAllOccurred) {
            if (setAllOccurred) {
                m_trackedChanges.addAll(Arrays.asList(WorkflowChange.values()));
            }
        }

        /**
         * @param workflowChanges The changes to check for
         * @return Whether at least one of the given changes have occurred
         */
        public Boolean hasOccurredAtLeastOne(final WorkflowChange... workflowChanges) {
            for (var workflowChange : workflowChanges) {
                if (m_trackedChanges.contains(workflowChange)) {
                    return Boolean.TRUE;
                }
            }
            return Boolean.FALSE;
        }

        /**
         * Forget that any event has occurred
         */
        public void reset() {
            m_trackedChanges.clear();
        }

    }

}
