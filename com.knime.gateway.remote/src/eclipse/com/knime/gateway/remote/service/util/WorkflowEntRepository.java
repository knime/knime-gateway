/* ------------------------------------------------------------------
 * This source code, its documentation and all appendant files
 * are protected by copyright law. All rights reserved.
 *
 * Copyright by KNIME AG, Zurich, Switzerland
 *
 * You may not modify, publish, transmit, transfer or sell, reproduce,
 * create derivative works from, distribute, perform, display, or in
 * any way exploit any of the content, in whole or in part, except as
 * otherwise expressly permitted in writing by the copyright owner or
 * as specified in the license file distributed with this product.
 *
 * If you have any questions please contact the copyright holder:
 * website: www.knime.com
 * email: contact@knime.com
 * ---------------------------------------------------------------------
 *
 */
package com.knime.gateway.remote.service.util;

import java.util.UUID;

import com.knime.gateway.entity.PatchEnt;
import com.knime.gateway.entity.WorkflowEnt;
import com.knime.gateway.entity.WorkflowSnapshotEnt;
import com.knime.gateway.remote.service.DefaultWorkflowService;

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
     * @param nodeID the node ID of the node the workflow is contained in in case of a sub-workflow, otherwise an empty
     *            string or <code>null</code>
     * @param entity the entity to commit (and possibly compare with the latest snapshot)
     * @return the workflow entity together with the respective snapshot id encapsulated in a
     *         {@link WorkflowSnapshotEnt}
     */
    WorkflowSnapshotEnt commit(UUID workflowID, String nodeID, WorkflowEnt entity);

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
