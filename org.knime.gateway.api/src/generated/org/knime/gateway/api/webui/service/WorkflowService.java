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
package org.knime.gateway.api.webui.service;

import org.knime.gateway.api.service.GatewayService;
import org.knime.gateway.api.webui.service.util.ServiceExceptions;

import org.knime.gateway.api.webui.entity.CommandResultEnt;
import org.knime.gateway.api.webui.entity.NodeIdAndIsExecutedEnt;
import org.knime.gateway.api.webui.entity.WorkflowCommandEnt;
import org.knime.gateway.api.webui.entity.WorkflowMonitorStateSnapshotEnt;
import org.knime.gateway.api.webui.entity.WorkflowSnapshotEnt;

/**
 * Operations on workflows.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
public interface WorkflowService extends GatewayService {

    /**
     * Dispose the workflow (manager) corresponding to the given project and version.
     *
     * @param projectId ID of the workflow-project.
     * @param version The version identifier. &#x60;null&#x60; corresponds to the current-state (working area).
     *
     * 
     * @throws ServiceExceptions.ServiceCallException If a Gateway service call failed for some reason.
     */
    void disposeVersion(String projectId, String version)  throws ServiceExceptions.ServiceCallException;
        
    /**
     * Executed a command on the referenced workflow. Every request with the same operation is idempotent.
     *
     * @param projectId ID of the workflow-project.
     * @param workflowId The ID of a workflow which has the same format as a node-id.
     * @param workflowCommand An object that describes the command to be executed.
     *
     * @return the result
     * @throws ServiceExceptions.ServiceCallException If a Gateway service call failed for some reason.
     */
    CommandResultEnt executeWorkflowCommand(String projectId, org.knime.gateway.api.entity.NodeIDEnt workflowId, WorkflowCommandEnt workflowCommand)  throws ServiceExceptions.ServiceCallException;
        
    /**
     * Returns the node IDs of all updatable linked components present on a workflow, even if they are deeply nested.
     *
     * @param projectId ID of the workflow-project.
     * @param workflowId The ID of a workflow which has the same format as a node-id.
     *
     * @return the result
     * @throws ServiceExceptions.NotASubWorkflowException The requested node is not a sub-workflow (i.e. a meta- or sub-node), but is required to be.
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     * @throws ServiceExceptions.InvalidRequestException If the request is invalid for a reason.
     */
    java.util.List<NodeIdAndIsExecutedEnt> getUpdatableLinkedComponents(String projectId, org.knime.gateway.api.entity.NodeIDEnt workflowId)  throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NodeNotFoundException, ServiceExceptions.InvalidRequestException;
        
    /**
     * Retrieves the complete structure (sub-)workflows.
     *
     * @param projectId ID of the workflow-project.
     * @param workflowId The ID of a workflow which has the same format as a node-id.
     * @param versionId Version ID of the project.
     * @param includeInteractionInfo Whether to enclose information that is required when the user is interacting with the returned workflow. E.g. the allowed actions (reset, execute, cancel) for contained nodes and the entire workflow itself.
     *
     * @return the result
     * @throws ServiceExceptions.NotASubWorkflowException The requested node is not a sub-workflow (i.e. a meta- or sub-node), but is required to be.
     * @throws ServiceExceptions.NodeNotFoundException The requested node was not found.
     */
    WorkflowSnapshotEnt getWorkflow(String projectId, org.knime.gateway.api.entity.NodeIDEnt workflowId, String versionId, Boolean includeInteractionInfo)  throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NodeNotFoundException;
        
    /**
     * Returns the current state of the workflow monitor.
     *
     * @param projectId ID of the workflow-project.
     *
     * @return the result
     */
    WorkflowMonitorStateSnapshotEnt getWorkflowMonitorState(String projectId) ;
        
    /**
     * Re-does the last command from the redo-stack.
     *
     * @param projectId ID of the workflow-project.
     * @param workflowId The ID of a workflow which has the same format as a node-id.
     *
     * 
     * @throws ServiceExceptions.ServiceCallException If a Gateway service call failed for some reason.
     */
    void redoWorkflowCommand(String projectId, org.knime.gateway.api.entity.NodeIDEnt workflowId)  throws ServiceExceptions.ServiceCallException;
        
    /**
     * Save a project. This is a temporary service endpoint to offer saving a project in the browser environment, i.e. without any progress indication. In the desktop environment, this endpoint will not be called and instead the corresponding one from the Desktop API. Projects are usually only saved on session close in the browser environment, so the only other current use-case is saving before creating a version. We leave the call to the Catalog service to create the version to the Frontend for the time being. This means the code paths diverge only on save-and-upload. Otherwise, we would (a) have to parameterise the Gateway endpoint by some &#x60;doSave&#x60;, which is equivalent to &#x60;isBrowser&#x60; and (b) implement capability for the backend to make the Catalog call. As soon as we can provide Browser-compatible (i.e. Web-UI) progress indication (NXT-3634), the two endpoints and their backing duplicated logic can be unified and &#x60;createVersion&#x60; can become a single Gateway endpoint, also performing the hub service call (if desired).
     *
     * @param projectId ID of the workflow-project.
     *
     * 
     * @throws ServiceExceptions.ServiceCallException If a Gateway service call failed for some reason.
     */
    void saveProject(String projectId)  throws ServiceExceptions.ServiceCallException;
        
    /**
     * Un-does the last command from the undo-stack.
     *
     * @param projectId ID of the workflow-project.
     * @param workflowId The ID of a workflow which has the same format as a node-id.
     *
     * 
     * @throws ServiceExceptions.ServiceCallException If a Gateway service call failed for some reason.
     */
    void undoWorkflowCommand(String projectId, org.knime.gateway.api.entity.NodeIDEnt workflowId)  throws ServiceExceptions.ServiceCallException;
        
}
