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

import com.knime.gateway.v0.entity.ConnectionEnt;
import com.knime.gateway.v0.entity.PatchEnt;
import com.knime.gateway.v0.entity.WorkflowPartsEnt;
import com.knime.gateway.v0.entity.WorkflowSnapshotEnt;

import com.knime.gateway.v0.service.util.ServiceExceptions;

import com.googlecode.jsonrpc4j.JsonRpcMethod;

import com.knime.gateway.v0.service.WorkflowService;

/**
 * Interface that adds json rpc annotations. 
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public interface JsonRpcWorkflowService extends WorkflowService {

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "WorkflowService.createConnection")
    String createConnection(java.util.UUID jobId, ConnectionEnt connectionEnt)  throws ServiceExceptions.ActionNotAllowedException;

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "WorkflowService.createWorkflowCopy")
    java.util.UUID createWorkflowCopy(java.util.UUID jobId, WorkflowPartsEnt workflowPartsEnt)  throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NodeNotFoundException, ServiceExceptions.InvalidRequestException;

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "WorkflowService.deleteWorkflowParts")
    java.util.UUID deleteWorkflowParts(java.util.UUID jobId, WorkflowPartsEnt workflowPartsEnt, Boolean copy)  throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NodeNotFoundException, ServiceExceptions.ActionNotAllowedException;

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "WorkflowService.getSubWorkflow")
    WorkflowSnapshotEnt getSubWorkflow(java.util.UUID jobId, String nodeId)  throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NodeNotFoundException;

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "WorkflowService.getSubWorkflowDiff")
    PatchEnt getSubWorkflowDiff(java.util.UUID jobId, String nodeId, java.util.UUID snapshotId)  throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NotFoundException;

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "WorkflowService.getWorkflow")
    WorkflowSnapshotEnt getWorkflow(java.util.UUID jobId) ;

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "WorkflowService.getWorkflowDiff")
    PatchEnt getWorkflowDiff(java.util.UUID jobId, java.util.UUID snapshotId)  throws ServiceExceptions.NotFoundException;

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "WorkflowService.pasteWorkflowParts")
    WorkflowPartsEnt pasteWorkflowParts(java.util.UUID jobId, java.util.UUID partsId, Integer x, Integer y, String nodeId)  throws ServiceExceptions.NotASubWorkflowException, ServiceExceptions.NotFoundException;

}
