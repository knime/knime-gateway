/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.com; Email: contact@knime.com
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

import java.util.UUID;

import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.entity.PatchEnt;
import org.knime.gateway.api.entity.WorkflowEnt;
import org.knime.gateway.api.entity.WorkflowSnapshotEnt;
import org.knime.gateway.impl.service.DefaultWorkflowService;

/**
 * Repository to keep track of changes to {@link WorkflowEnt} objects. Different versions of the same object can be
 * committed and it's differences to another object at hand determined.
 *
 * It provides very specific methods as required by the {@link DefaultWorkflowService}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @noimplement This interface is not intended to be implemented by clients.
 * @noreference This interface is not intended to be referenced by clients.
 */
public interface WorkflowEntRepository {

    /**
     * Commits the given workflow entity and returns it together with a respective snapshot id (wrapped as
     * {@link WorkflowSnapshotEnt}).
     *
     * The snapshot id is either newly generated (in case there has been some changes) or the snapshot id of the latest
     * commit (if there weren't any changes).
     *
     * @param workflowID the ID of the workflow to be committed
     * @param nodeID the node ID of the node the workflow is contained in or {@link NodeIDEnt#getRootID()}
     * @param entity the entity to commit (and possibly compare with the latest snapshot)
     * @return the workflow entity together with the respective snapshot id encapsulated in a
     *         {@link WorkflowSnapshotEnt}
     */
    WorkflowSnapshotEnt commit(UUID workflowID, NodeIDEnt nodeID, WorkflowEnt entity);

    /**
     * Determines the diff (i.e. changes as a patch) of the provided entity to the entity once committed to the
     * repository with the given snapshotID.
     *
     * If there has been any changes (apart from the workflow entity's snapshot id), the provided workflow entity will
     * be committed to the repository.
     *
     * @param snapshotID id of the snapshot requested from the repository to be compared
     * @param entity the workflow entity to compare the requested snapshot to (and that will possibly be committed)
     * @return the changes as a patch or an empty patch (even without snapshot id etc.)
     * @throws IllegalArgumentException if there is not change history for the given snapshotID combination
     */
    PatchEnt getChangesAndCommit(UUID snapshotID, WorkflowEnt entity) throws IllegalArgumentException;

    /**
     * Disposes the whole history of the workflow (and all it's sub-workflows) with the given workflow ID. A subsequent
     * {@link #getChangesAndCommit(UUID, WorkflowEnt)} with the same workflow ID will throw an exception.
     *
     * @param workflowID the ID of the workflow whose history shall be disposed
     */
    void disposeHistory(UUID workflowID);

}
