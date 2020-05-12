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
package com.knime.gateway.jsonrpc.local.service;

import com.knime.gateway.entity.ConnectionEnt;
import com.knime.gateway.entity.PatchEnt;
import com.knime.gateway.entity.WorkflowPartsEnt;
import com.knime.gateway.entity.WorkflowSnapshotEnt;

import com.knime.gateway.service.util.ServiceExceptions;

import com.googlecode.jsonrpc4j.JsonRpcMethod;

import com.knime.gateway.service.WorkflowService;

/**
 * Interface that adds json rpc annotations. 
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"org.knime.gateway.codegen.GatewayCodegen", "src-gen/com.knime.gateway.jsonrpc.local-config.json"})
public interface JsonRpcWorkflowService extends WorkflowService {

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "WorkflowService.createConnection")
    com.knime.gateway.entity.ConnectionIDEnt createConnection(java.util.UUID jobId, ConnectionEnt connectionEnt)  throws ServiceExceptions.ActionNotAllowedException;

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "WorkflowService.createWorkflowCopy")
    java.util.UUID createWorkflowCopy(java.util.UUID jobId, com.knime.gateway.entity.NodeIDEnt workflowId, WorkflowPartsEnt workflowPartsEnt)  throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NodeNotFoundException, ServiceExceptions.InvalidRequestException;

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "WorkflowService.deleteWorkflowParts")
    java.util.UUID deleteWorkflowParts(java.util.UUID jobId, com.knime.gateway.entity.NodeIDEnt workflowId, WorkflowPartsEnt workflowPartsEnt, Boolean copy)  throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NodeNotFoundException, ServiceExceptions.ActionNotAllowedException;

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "WorkflowService.getWorkflow")
    WorkflowSnapshotEnt getWorkflow(java.util.UUID jobId, com.knime.gateway.entity.NodeIDEnt workflowId)  throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NodeNotFoundException;

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "WorkflowService.getWorkflowDiff")
    PatchEnt getWorkflowDiff(java.util.UUID jobId, com.knime.gateway.entity.NodeIDEnt workflowId, java.util.UUID snapshotId)  throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NotFoundException;

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "WorkflowService.pasteWorkflowParts")
    WorkflowPartsEnt pasteWorkflowParts(java.util.UUID jobId, com.knime.gateway.entity.NodeIDEnt workflowId, java.util.UUID partsId, Integer x, Integer y)  throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NotFoundException;

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "WorkflowService.redo")
    Boolean redo(java.util.UUID jobId, com.knime.gateway.entity.NodeIDEnt workflowId)  throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NotFoundException;

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "WorkflowService.undo")
    Boolean undo(java.util.UUID jobId, com.knime.gateway.entity.NodeIDEnt workflowId)  throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NotFoundException;

}
