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
 * ------------------------------------------------------------------------
 */
package org.knime.gateway.api.service;

import org.knime.gateway.api.service.GatewayService;
import org.knime.gateway.api.service.util.ServiceExceptions;

import org.knime.gateway.api.entity.ConnectionEnt;
import org.knime.gateway.api.entity.PatchEnt;
import org.knime.gateway.api.entity.WorkflowPartsEnt;
import org.knime.gateway.api.entity.WorkflowSnapshotEnt;

/**
 * Operations on workflows.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/org.knime.gateway.api-config.json"})
public interface WorkflowService extends GatewayService {

    /**
     * Creates a new connection between two nodes. Note: replaces/removes existing connections if destination port is already in use.
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param connectionEnt The connection.
     *
     * @return the result
     * @throws ServiceExceptions.ActionNotAllowedException If the an action is not allowed because it&#39;s not applicable or it doesn&#39;t exist.
     */
    org.knime.gateway.api.entity.ConnectionIDEnt createConnection(java.util.UUID jobId, ConnectionEnt connectionEnt)  throws ServiceExceptions.ActionNotAllowedException;
        
    /**
     * Selects and essentially copies the specified part of the workflow. It will still be available even if (sub)parts are deleted. The parts are referenced by a part-id.  Note: connections will be ignored and _not_ copied!
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param workflowId The ID of a worklow which has the same format as a node-id.
     * @param workflowPartsEnt The actual part selection.
     *
     * @return the result
     * @throws ServiceExceptions.NotASubWorkflowException The requested node is not a sub-workflow (i.e. a meta- or sub-node), but is required to be.
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     * @throws ServiceExceptions.InvalidRequestException If the request is invalid for a reason.
     */
    java.util.UUID createWorkflowCopy(java.util.UUID jobId, org.knime.gateway.api.entity.NodeIDEnt workflowId, WorkflowPartsEnt workflowPartsEnt)  throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NodeNotFoundException, ServiceExceptions.InvalidRequestException;
        
    /**
     * Deletes the given workflow parts. Cannot be undone unless a copy has been made before.
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param workflowId The ID of a worklow which has the same format as a node-id.
     * @param workflowPartsEnt The parts to be deleted.
     * @param copy If a copy should be created before removal. False by default. Please note that the copy will _only_ include the connections that are entirely enclosed by the parts to be removed (i.e. connections that are connecting two removed nodes - all others won&#39;t be kept)
     *
     * @return the result
     * @throws ServiceExceptions.NotASubWorkflowException The requested node is not a sub-workflow (i.e. a meta- or sub-node), but is required to be.
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     * @throws ServiceExceptions.ActionNotAllowedException If the an action is not allowed because it&#39;s not applicable or it doesn&#39;t exist.
     */
    java.util.UUID deleteWorkflowParts(java.util.UUID jobId, org.knime.gateway.api.entity.NodeIDEnt workflowId, WorkflowPartsEnt workflowPartsEnt, Boolean copy)  throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NodeNotFoundException, ServiceExceptions.ActionNotAllowedException;
        
    /**
     * Retrieves the complete structure (nodes, connections, annotations) of (sub-)workflows.
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param workflowId The ID of a worklow which has the same format as a node-id.
     *
     * @return the result
     * @throws ServiceExceptions.NotASubWorkflowException The requested node is not a sub-workflow (i.e. a meta- or sub-node), but is required to be.
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     */
    WorkflowSnapshotEnt getWorkflow(java.util.UUID jobId, org.knime.gateway.api.entity.NodeIDEnt workflowId)  throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NodeNotFoundException;
        
    /**
     * Gives the changes of the sub-workflow as a patch.
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param workflowId The ID of a worklow which has the same format as a node-id.
     * @param snapshotId The id of the workflow snapshot already retrieved.
     *
     * @return the result
     * @throws ServiceExceptions.NotASubWorkflowException The requested node is not a sub-workflow (i.e. a meta- or sub-node), but is required to be.
     * @throws ServiceExceptions.NotFoundException A resource couldn&#39;t be found.
     */
    PatchEnt getWorkflowDiff(java.util.UUID jobId, org.knime.gateway.api.entity.NodeIDEnt workflowId, java.util.UUID snapshotId)  throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NotFoundException;
        
    /**
     * Pastes the referenced parts into the referenced (sub-)workflow.
     *
     * @param jobId ID of the job the workflow is requested for.
     * @param workflowId The ID of a worklow which has the same format as a node-id.
     * @param partsId The id referencing the parts to paste.
     * @param x The x position to paste the parts.
     * @param y The y position to paste the parts.
     *
     * @return the result
     * @throws ServiceExceptions.NotASubWorkflowException The requested node is not a sub-workflow (i.e. a meta- or sub-node), but is required to be.
     * @throws ServiceExceptions.NotFoundException A resource couldn&#39;t be found.
     */
    WorkflowPartsEnt pasteWorkflowParts(java.util.UUID jobId, org.knime.gateway.api.entity.NodeIDEnt workflowId, java.util.UUID partsId, Integer x, Integer y)  throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NotFoundException;
        
}
